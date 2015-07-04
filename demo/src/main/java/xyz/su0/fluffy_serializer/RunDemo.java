/*
 *
 */
package xyz.su0.fluffy_serializer;

import xyz.su0.fluffy_serializer.*;
import xyz.su0.fluffy_serializer.annotations.*;

class RunDemo {
  public static void main(String[] args) {
    FluffySerializer sz = new FluffySerializer();

    class SimpleObject {
      public int data1;
      public String data2;
    }

    SimpleObject o = new SimpleObject();
    o.data1 = 42;
    o.data2 = "Hello World!";

    String result = sz.serialize(o);

    System.out.println(result);
  }
}
