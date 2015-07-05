/*
 *
 */

package xyz.su0.fluffy_serializer;

import java.lang.reflect.*;
import java.lang.annotation.*;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

import org.apache.commons.lang3.ClassUtils;

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

  private List<Object> objectsArray;

  public AtomicHolder(List<Object> objectsArray) {
    this.objectsArray = objectsArray;
  }

  public Class getAtomicSerializerClass(Class clazz) {
    Class atomicSzClass = atomics.get(clazz);
    if(atomicSzClass != null) {
      return atomicSzClass;
    }
    if(!ClassUtils.isPrimitiveOrWrapper(clazz)) {
      return ReferenceSerializer.class;
    }
    else {
      return null;
    }
  }

  public IAtomicSerializer getAtomicSerializerInstance(Class clazz)
    /*throws
    TODO: make deal with this
    NoSuchMethodException,
    InstantiationException,
    IllegalAccessException,
    InvocationTargetException*/
  {
    Class implClass = getAtomicSerializerClass(clazz);

    if(implClass != ReferenceSerializer.class) {
      IAtomicSerializer sz = null;
      try {

        sz = (IAtomicSerializer)implClass.getConstructor().newInstance();
      }
      catch (Exception e) {
      }
      return sz;
    }
    else {
      return new ReferenceSerializer(objectsArray);
    }
  }
}
