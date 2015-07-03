# Disclaimer

This is a test task project for java developer position.
Goal of this project is to demonstrate skills not to create some useful
 application or library.

## Main idea

This test task demand that this serializer has tho features:

1. Serializer must serialize full objects graph.
2. Possibly small size of serialized data.

So I choose to:

1. Use JSON as format. (Cons: not so small. Pros: JavaScript support.)
2. Use symbols like '@' or '&' for information.
3. Serialized graph is JSON array where first element is start point for this
 graph.
