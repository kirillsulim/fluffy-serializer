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
   * @throws ParseException if string is inconsistent data
   */
  public Object deserialize(String string) throws FluffyParseException {
    if(!Pattern.matches("\\[(\\{.+\\})(,\\{.+\\})*\\]", string)) {
      throw new FluffyParseException("Inconsistent data");
    }

    String[] objectsStrings = string.replaceAll("^\\[\\{|\\}\\]$", "").split("\\},\\{");

    List<Object> objectsArray = new ArrayList<>();
    AtomicHolder atomics = new AtomicHolder(objectsArray);

    for (String objString : objectsStrings) {
      objectsArray.add(deserializeObject(objString, atomics));
    }
    for (int i = 0; i < objectsArray.size(); ++i) {
      applyReferences(i, objectsStrings[i], objectsArray, atomics);
    }

    return objectsArray.get(0);
  }

  private String serializeObject(Object object, AtomicHolder atomics) throws FluffySerializationException, FluffyNotSerializableException {
    Class clazz = object.getClass();
    String className = clazz.getName();

    List<String> kvPairs = new ArrayList<>();

    kvPairs.add("\"@class\":\"" + className + "\"");

    Field[] fields = clazz.getDeclaredFields();
    for(Field f : fields) {
      f.setAccessible(true);
      String name = f.getName();
      if(ignoredNames.contains(name)) {
        continue;
      }

      System.out.println(name);

      Class fieldClass = f.getType();
      System.out.println(fieldClass.getName());

      IAtomicSerializer sz = null;
      try {
        sz = atomics.getAtomicSerializerInstance(fieldClass);
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

  private Object deserializeObject(String string, AtomicHolder atomics) throws FluffyParseException {
    String[] kvPairs = string.split(",");

    Map<String, String> kv = new HashMap<>();
    for(String pair : kvPairs) {
      System.out.println(pair);
      String[] t = pair.split(":");
      kv.put(t[0].replaceAll("\"", ""), t[1].replaceAll("\"", ""));
    }

    String className = kv.get("@class");
    kv.remove("@class");
    System.out.println(className);
    Object object = null;
    Class objectClass = null;
    try {
      objectClass = Class.forName(className);
      object = objectClass.newInstance();
    }
    catch(Exception e) {
      // TODO: add after exceptions
      System.out.println(e.toString());
    }

    for(Map.Entry<String, String> entry : kv.entrySet()) {
      if(entry.getValue().startsWith("&")) {
        continue;
      }

      Field currentField = null;
      try {
        currentField = objectClass.getField(entry.getKey());
      }
      catch(Exception e) {
        // TODO: fix
        System.out.println(e.toString());
      }
      currentField.setAccessible(true);

      IAtomicSerializer sz = atomics.getAtomicSerializerInstance(currentField.getType());
      try {
        currentField.set(object, sz.deserialize(entry.getValue()));
      }
      catch(Exception e) {
        // TODO: fix
        System.out.println(e.toString());
      }
    }

    return object;
  }

  private void applyReferences(int index, String objectString, List<Object> objectsArray, AtomicHolder atomics) throws FluffyParseException {
    String[] kvPairs = objectString.split(",");

    Map<String, String> kv = new HashMap<>();
    for(String pair : kvPairs) {
      System.out.println(pair);
      String[] t = pair.split(":");
      kv.put(t[0].replaceAll("\"", ""), t[1]);
    }
    String className = kv.get("@class");
    kv.remove("@class");

    Object object = objectsArray.get(index);
    Class objectClass = object.getClass();

    for(Map.Entry<String, String> entry : kv.entrySet()) {
      if(!entry.getValue().startsWith("\"&")) {
        continue;
      }

      Field currentField = null;
      try {
        currentField = objectClass.getField(entry.getKey());
      }
      catch(Exception e) {
        // TODO: fix
        System.out.println(e.toString());
      }
      currentField.setAccessible(true);

      IAtomicSerializer sz = atomics.getAtomicSerializerInstance(currentField.getType());
      try {
        currentField.set(object, sz.deserialize(entry.getValue()));
        System.out.println("Stted " + entry.getValue() + " = " + currentField.get(object));
      }
      catch(Exception e) {
        // TODO: fix
        System.out.println(e.toString());
      }
    }
  }
}
