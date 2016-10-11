This works at the commandline build.

In Eclipse:
- import the parent POM as a project, and `mvn verify -Dmaven.test.skip=true` on that do trigger a maven build of both the Scala project and the Spoofax project.
- right click Spoofax project > `Spoofax (Meta)` > `Load Language` to see changes in Eclipse editors
