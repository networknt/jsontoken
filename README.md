jsontoken
=========

This is a fork of original google jsontoken from the following url.

https://code.google.com/p/jsontoken/

It has not been updated since Sep 11, 2012 and depends on some old packages.

What I have done:

1. Convert from Joda time to Java 8 time. So it requires Java 8.
2. Covert Json parser from Gson to Jackson as I don't want to include two Json parsers to my projects.
3. Remove google collections from dependency list as it is stopped long time ago.

All existing unit tests passed along with some newly added test cases.

