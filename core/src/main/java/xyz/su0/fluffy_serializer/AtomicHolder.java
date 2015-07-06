package xyz.su0.fluffy_serializer;

import java.lang.reflect.*;
import java.lang.annotation.*;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

import org.apache.commons.lang3.ClassUtils;

import xyz.su0.fluffy_serializer.atomic_serializers.*;
import xyz.su0.fluffy_serializer.annotations.*;
import xyz.su0.fluffy_serializer.exceptions.*;


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
    boolean primitive = ClassUtils.isPrimitiveOrWrapper(clazz);
    boolean annotatedAsFluffy = clazz.isAnnotationPresent(FluffySerializable.class);
    Class atomicSzClass = atomics.get(clazz);

    if(atomicSzClass != null) {
      return atomicSzClass;
    }
    if(!primitive && annotatedAsFluffy) {
      return ReferenceSerializer.class;
    }
    return null;    
  }

  public IAtomicSerializer getAtomicSerializerInstance(Class clazz) throws FluffySerializationException {
    Class implClass = getAtomicSerializerClass(clazz);

    if(implClass != ReferenceSerializer.class) {
      IAtomicSerializer sz = null;
      try {
        sz = (IAtomicSerializer)implClass.getConstructor().newInstance();
      }
      catch (Exception e) {
        throw new FluffySerializationException(e);
      }
      return sz;
    }
    else {
      return new ReferenceSerializer(objectsArray);
    }
  }
}
