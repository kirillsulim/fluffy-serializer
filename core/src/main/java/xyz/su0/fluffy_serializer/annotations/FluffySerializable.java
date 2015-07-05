package xyz.su0.fluffy_serializer.annotations;

import java.lang.annotation.*;


/**
 * Use this annotation to mark classes serializablle by fluffy-serializer
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface FluffySerializable {
}
