# fluffy-serializer

[![Build Status](https://travis-ci.org/kirillsulim/fluffy-serializer.svg)](https://travis-ci.org/kirillsulim/fluffy-serializer)

Yet another serializer =)

Please read [disclaimer](DISCLAIMER.md) before use this project.


## Known issues
1. Due to using regular expression instead of lexical analysis, there may be some errors while parsing strings values with
 symbols like ',[]{}'.
2. Parser cannot process strings with '\n'.
3. There is no way to serialize standart dictionaries or arrays yet, but it can be done by adding specific atomic serializers.

## Use lib:
1. Imnport FluffySerializer class and @FluffySerializable annotation
2. Annotate your class
3. Call FluffySerializer.serialize() and get string.
4. Call FluffySerializer.deserialize() and get object (or objects).

## Run demo
I tested demo using `gradle run`. I recomend you to do the same.

## TODO's
1. Change regexp to lexical analysis.
2. Add way to register custom atomic serializers with annotations or resource file (like xml).
3. Add support of other java primitives. For now serializer supports only String (not primitive btw, but strings is so common, I decided to process them like primitives) and Integer.
4. Add serialization of collections.
