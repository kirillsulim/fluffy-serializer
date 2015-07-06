/*
 *
 */
package xyz.su0.fluffy_serializer;

import xyz.su0.fluffy_serializer.FluffySerializer;
import xyz.su0.fluffy_serializer.annotations.FluffySerializable;
import xyz.su0.fluffy_serializer.exceptions.*;

@FluffySerializable
class Person {
  private String name;
  private int age;

  public void setName(String name) {
    this.name = name;
  }

  public String getName() {
    return this.name;
  }

  public void setAge(int age) {
    this.age = age;
  }

  public int getAge() {
    return this.age;
  }

  public String toString() {
    return String.format("%s (%d)", name, age);
  }
}

@FluffySerializable
class Customer extends Person {
  /* Empty */
}

@FluffySerializable
class Manager extends Person {
  private Customer customer;

  public void setCustomer(Customer customer) {
    this.customer = customer;
  }

  public Customer getCustomer() {
    return this.customer;
  }

  public String toString() {
    String s = super.toString();
    if(customer != null) {
      return s + " manage " + customer.toString();
    }
    else {
      return s + " no customers";
    }
  }
}

@FluffySerializable
class Message {
  public Manager manager;
  public Customer customer;
  public String actionDescription;

  public String toString() {
    return manager.toString() + " do " + actionDescription + " on " + customer.toString();
  }
}

class RunDemo {
  public static void main(String[] args) throws FluffyNotSerializableException, FluffyParseException, FluffySerializationException {
    System.out.println("Fluffy-serializer demo.");

    FluffySerializer sz = new FluffySerializer();

    Customer customer = new Customer();
    customer.setName("John Dow");
    customer.setAge(42);

    System.out.println("We created Customer object:");
    System.out.println(customer);

    System.out.println("Now serialize Costomer object:");
    String serializedCustomer = sz.serialize(customer);
    System.out.println(serializedCustomer);

    System.out.println("And deserialize it:");
    System.out.println(sz.deserialize(serializedCustomer));

    Manager manager = new Manager();
    manager.setName("Bill Gates");
    manager.setAge(55);
    manager.setCustomer(customer);

    System.out.println("We created Manager object:");
    System.out.println(manager);

    System.out.println("Now serialize Manager object:");
    String serializedManager = sz.serialize(manager);
    System.out.println(serializedManager);

    System.out.println("And deserialize it:");
    System.out.println(sz.deserialize(serializedManager));

    Message message = new Message();
    message.manager = manager;
    message.customer = customer;
    message.actionDescription = "Some action";

    System.out.println("We created Message object:");
    System.out.println(message);

    System.out.println("Now serialize Message object:");
    String serializedMessage = sz.serialize(message);
    System.out.println(serializedMessage);

    System.out.println("And deserialize it:");
    System.out.println(sz.deserialize(serializedMessage));
  }
}
