JNI++
=====

This is a 'fork' of jnipp project found at http://sourceforge.net/projects/jnipp/

Since I had some troubles with running the binaries I decided to build it myself.
However I don't like the gmake console stuff, so I decided to port the project into
a NetBeans (7.3) project to make it more developer-friendly :)

I also used built-in NetBeans code formater to unify the coding style and I also
replaced some of the depricated methods and classes and then I removed some warning-causing
segments of code, e.g., unused imports, @Override, etc.

Other than that it is still the same project whose author is Phillip E. Trewhella.
Licencing is same as the original jnipp project, that is LGPL...whatever version that was :)

 - bin: compiled JAR and BAT script for running the `jnipp` GUI
 - docs: official API documentation and User Guide
 - include: include files for C++ part
 - JNI++: the NetBeans project