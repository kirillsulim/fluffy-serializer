/*
 *
 */

package xyz.su0.fluffy_serializer;

import java.lang.reflect.*;
import java.lang.annotation.*;
import java.util.Map;
import java.util.HashMap;

import xyz.su0.fluffy_serializer.atomic_serializers.*;
import xyz.su0.fluffy_serializer.annotations.*;


class AtomicHolder {
  private static final Map<Class, Class<? extends IAtomicSerializer>> atomics;
  static {
    atomics = new HashMap<>();
    atomics.put(Integer.class, IntegerSerializer.class);
    atomics.put(int.class, IntegerSerializer.class);
    atomics.put(String.class, StringSerializer.class);
  }

  public Class getAtomicSerializerClass(Class clazz) {
    return atomics.get(clazz);
  }

  public IAtomicSerializer getAtomicSerializerInstance(Class clazz)
    /*throws
    NoSuchMethodException,
    InstantiationException,
    IllegalAccessException,
    InvocationTargetException*/
  {
    IAtomicSerializer sz = null;
    try {
      Class implClass = getAtomicSerializerClass(clazz);
      sz = (IAtomicSerializer)implClass.getConstructor().newInstance();
    }
    catch (Exception e) {
      
    }
    return sz;
  }
}
