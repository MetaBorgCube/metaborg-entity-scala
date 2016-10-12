This works at the commandline build.

In Eclipse (once):
1. change `editor/Main.esv` commenting out first line and uncommenting second line:
```
  provider : target/metaborg/Scalaproject-0.1.0-SNAPSHOT.jar // use this for commandline maven build
//  provider : ../Scalaproject/target/Scalaproject-0.1.0-SNAPSHOT.jar // use this in Eclipse (bypasses the copying step by using a relative path, but only works inside eclipse)
```
(though, do not commit this)
2. in the Scalaproject use the maven-build with `package -DskipTests` to generate a jar fast

In Eclipse (every build):
1. build scalaproject with maven (generates jar)
2. build spoofax project with spoofax (cmd+alt+b), reloads language with new jar.

Note: after a spoofax clean the scala-library will not be there anymore, so run the full maven build in the parent pom file.

Suggestion:
- bind `Run Maven Build` to shortkey `Ctrl+Alt+B` (on mac) 
