# JUtilsXtra
Additional Utility functions for Java that are missing from the java.util and Apache commons
This project contains many functions that I have needed to you for all sorts of things when programming. 
They mostly contain operations on Arrays, such as comparing arrays, finding highest values,unions, distinct, average value etc. 
There are also some hash functions and methods for converting between primitives or int and long arrays to ByteArrays and back.
These methods were written as and when I needed them, as such they have not all been fully tested so you should double check them. 
It's always a good idea to test first, I would be happy to receive bug fixes.

All of the Methods are in a single class named Utils2 with only one external dependacy (trove3) included in the src folder as
a all in one zip. 

I have also created a "S" class that contains shortcuts for printing lines eg, System.out.println("bla") becomes S.pl("bla")
There are also equivalents for printing arrays and error lines as well as a shutdown command.

For the full list of available methods please consult the javadoc folder.
