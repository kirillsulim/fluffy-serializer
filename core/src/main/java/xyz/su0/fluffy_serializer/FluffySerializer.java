package xyz.su0.fluffy_serializer;

import java.lang.reflect.*;
import java.lang.annotation.*;
import java.lang.StringBuilder;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;
import java.util.regex.Pattern;
import java.util.regex.Matcher;
import org.apache.commons.lang3.reflect.FieldUtils;

import xyz.su0.fluffy_serializer.atomic_serializers.*;
import xyz.su0.fluffy_serializer.annotations.*;
import xyz.su0.fluffy_serializer.exceptions.*;

/**
 * Use instance of this class to serialize or deserialize objects
 */
public class FluffySerializer {
  private static List<String> ignoredNames;
  static {
    ignoredNames = new ArrayList<>();
    ignoredNames.add("this$0");
  }

  /**
   * Pass object to serialize. If there is refernce to another objects, they
   * will be serialized too.
   * @param object Object to serialize
   * @return String represents serialized object (or objects)
   * @throws FluffyNotSerializableException if objects is not marked as serializable by fluffy-serializer
   * @throws FluffySerializationException if error occured
   */
  public String serialize(Object object) throws FluffyNotSerializableException, FluffySerializationException {
    List<Object> objectsArray = new ArrayList<>();
    List<String> serializedObjects = new ArrayList<>();
    AtomicHolder atomics = new AtomicHolder(objectsArray);

    objectsArray.add(object);
    int currentObjectIndex = 0;

    do {
      Object currentObject = objectsArray.get(currentObjectIndex);
      serializedObjects.add(serializeObject(currentObject, atomics));
      ++currentObjectIndex;
    } while(currentObjectIndex < objectsArray.size());

    StringBuilder result = new StringBuilder();
    result.append('[');
    result.append(String.join(",", serializedObjects));
    result.append(']');

    return result.toString();
  }

  /**
   * Pass string with serialized data to deserialize to object (or objects).
   * Function return Object type so you need to cast result to desired type.
   * @param string String with serialized data
   * @return Deserialized object
   * @throws FluffyParseException if string is inconsistent data
   */
  public Object deserialize(String string) throws FluffyParseException {
    if(!Pattern.matches("\\[(\\{.+\\})(,\\{.+\\})*\\]", string)) {
      throw new FluffyParseException("Inconsistent data");
    }

    List<Object> objectsArray = new ArrayList<>();
    AtomicHolder atomics = new AtomicHolder(objectsArray);

    List<ObjectData> objectsData = createObjectDataList(string);

    for (ObjectData data : objectsData) {
      objectsArray.add(deserializeObject(data, atomics));
    }
    for (ObjectData data : objectsData) {
      applyReferences(data, objectsArray, atomics);
    }

    return objectsArray.get(0);
  }

  private String trimQuotes(String string) {
    String result = string.replaceAll("^\"|\"$", "");
    return result;
  }

  private List<ObjectData> createObjectDataList(String string)  throws FluffyParseException {
    List<ObjectData> result = new ArrayList<>();

    String[] objectsStrings = string.replaceAll("^\\[\\{|\\}\\]$", "").split("\\},\\{");

    int currentIndex = 0;

    for(String objectString : objectsStrings) {

      ObjectData data = new ObjectData();

      String[] kvPairs = objectString.split(",");
      for(String pair : kvPairs) {
        String[] t = pair.split(":");
        if(t.length != 2) {
          throw new FluffyParseException();
        }
        if(trimQuotes(t[0]).equals("@class")) {
          data.className = trimQuotes(t[1]);
        }
        else if(trimQuotes(t[1]).startsWith("&")) {
          data.refFields.put(trimQuotes(t[0]), t[1]);
        }
        else {
          data.fields.put(trimQuotes(t[0]), t[1]);
        }
      }
      data.index = currentIndex++;
      result.add(data);
    }
    return result;
  }

  private String serializeObject(Object object, AtomicHolder atomics) throws FluffySerializationException, FluffyNotSerializableException {
    Class clazz = object.getClass();
    if(!clazz.isAnnotationPresent(FluffySerializable.class)) {
      throw new FluffyNotSerializableException();
    }

    String className = clazz.getName();

    List<String> kvPairs = new ArrayList<>();

    kvPairs.add("\"@class\":\"" + className + "\"");

    Field[] fields = FieldUtils.getAllFields(clazz);
    for(Field f : fields) {
      f.setAccessible(true);
      String name = f.getName();
      if(ignoredNames.contains(name)) {
        continue;
      }

      Class fieldClass = f.getType();

      IAtomicSerializer sz = null;
      try {
        sz = atomics.getAtomicSerializerInstance(fieldClass);
      }
      catch (FluffyNotSerializableException e) {
        throw e;
      }
      catch (Exception e) {
        throw new FluffySerializationException(e);
      }

      String value = "";
      try {
        Object objValue = f.get(object);
        value = sz.serialize(objValue);
      }
      catch (IllegalAccessException e) {
        throw new FluffySerializationException(e);
      }

      StringBuilder kv = new StringBuilder();
      kv.append("\"");
      kv.append(name);
      kv.append("\":");
      kv.append(value);

      kvPairs.add(kv.toString());
    }

    StringBuilder result = new StringBuilder();
    result.append('{');
    result.append(String.join(",", kvPairs));
    result.append('}');
    return result.toString();

  }

  private Object deserializeObject(ObjectData data, AtomicHolder atomics) throws FluffyParseException {
    String className = data.className;

    Object object = null;
    Class objectClass = null;
    try {
      objectClass = Class.forName(className);
      object = objectClass.newInstance();
    }
    catch(Exception e) {
      throw new FluffyParseException(e);
    }

    for(Map.Entry<String, String> entry : data.fields.entrySet()) {
      try {
        boolean forceAccess = true;
        Field currentField = FieldUtils.getField(objectClass, entry.getKey(), forceAccess);
        currentField.setAccessible(true);

        IAtomicSerializer sz = atomics.getAtomicSerializerInstance(currentField.getType());
        currentField.set(object, sz.deserialize(entry.getValue()));
      }
      catch(Exception e) {
        throw new FluffyParseException(e);
      }
    }

    return object;
  }

  private void applyReferences(ObjectData data, List<Object> objectsArray, AtomicHolder atomics) throws FluffyParseException {
    Object object = objectsArray.get(data.index);
    Class objectClass = object.getClass();

    for(Map.Entry<String, String> entry : data.refFields.entrySet()) {
      try {
        boolean forceAccess = true;
        Field currentField = FieldUtils.getField(objectClass, entry.getKey(), forceAccess);
        currentField.setAccessible(true);

        IAtomicSerializer sz = atomics.getAtomicSerializerInstance(currentField.getType());
        currentField.set(object, sz.deserialize(entry.getValue()));
      }
      catch(Exception e) {
        throw new FluffyParseException(e);
      }
    }
  }
}

class ObjectData {
  public int index;
  public String className;
  public Map<String, String> fields = new HashMap<>();
  public Map<String, String> refFields = new HashMap<>();
}
