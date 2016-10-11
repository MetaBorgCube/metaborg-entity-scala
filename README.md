This works at the commandline build.

In Eclipse:
- import the parent POM as a project, and `mvn verify -Dmaven.test.skip=true` on that do trigger a maven build of both the Scala project and the Spoofax project.
- right click Spoofax project > `Spoofax (Meta)` > `Load Language` to see changes in Eclipse editors

TODO in Eclipse (Option A):
- import jars with Ant build step, remove the maven copy artifacts build step http://metaborg.org/en/latest/source/langdev/manual/config.html

TODO in Eclipse (Option B):
- make mvn build of parent project + reloading of language easier
