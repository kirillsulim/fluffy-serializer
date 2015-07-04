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

  private AtomicHolder atomics = new AtomicHolder();

  public String serialize(Object object) {


    Class clazz = object.getClass();

    Field[] fields = clazz.getDeclaredFields();

    List<String> kvPairs = new ArrayList<>();

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
    return null;
  }
}
