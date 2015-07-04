/*
 *
 */
package xyz.su0.fluffy_serializer;

import java.lang.reflect.*;
import java.lang.annotation.*;
import java.lang.StringBuilder;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;
import java.lang.Exception;



import xyz.su0.fluffy_serializer.atomic_serializers.*;
import xyz.su0.fluffy_serializer.annotations.*;

public class FluffySerializer {
  private static List<String> ignoredNames;
  static {
    ignoredNames = new ArrayList<>();
    ignoredNames.add("this$0");
  }

  public String serialize(Object object) {
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

  private String serializeObject(Object object, AtomicHolder atomics) {
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
        // TODO: fix after exception added
      }

      String value = "";
      try {
        Object objValue = f.get(object);
        value = sz.serialize(objValue);
      }
      catch (IllegalAccessException e) {
        // TODO: fix after exception added
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

  public Object deserialize(String string) {
    String[] objectsStrings = string.replaceAll("^\\[\\{|\\}\\]$", "").split("\\},\\{");

    List<Object> objectsArray = new ArrayList<>();
    AtomicHolder atomics = new AtomicHolder(objectsArray);

    for (String oString : objectsStrings) {
      objectsArray.add(deserializeObject(oString, atomics));
    }
    applyReferences(objectsStrings, objectsArray);

    return objectsArray.get(0);
  }

  public Object deserializeObject(String string, AtomicHolder atomics) {
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

  public void applyReferences(String[] objectsStrings, List<Object> objectsArray) {

  }
}
