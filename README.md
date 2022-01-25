# CSO-mirror

The repository to host a mirror of Communicating Scala Object (SCO) library.

## Licencing

This SCO library in this repository is redistributed under the [Artistic Licence 2.0](https://www.cs.ox.ac.uk/people/bernard.sufrin/personal/CSO/ArtisticLicence.txt), the licence given in the original distribution of SCO, as permitted by the Section (4) of the Licence. The Standard Version of the library can be obtained from [https://www.cs.ox.ac.uk/people/bernard.sufrin/personal/CSO/](https://www.cs.ox.ac.uk/people/bernard.sufrin/personal/CSO/).

The condition (4) of the Artistic License 2.0 applies to the Distribution of Modified Versions. This repository contains a Modified Version of the library, with the following difference:
 - the repository only contains the `src` folder of the Standard Version
 - contents of the original `src` folder are divided into two modules
   - `modules/macros` contains all the macro implementations
   - `modules/lib` contains all the library code (that may depend on macros)

   each of which contains source codes under their respective `main/scala` directories.

   The purpose of this change is to bring the project structure closer to an ordinary multi-module sbt project. Along the way it fixes a bundle issue (see [#2](https://github.com/kory33/CSO-mirror/issues/2) for details).
 - build configuration has been largely altered
   - namely, the following Ant-based configuration files:
     - `scalatasks.xml`
     - `scala.properties`
     - `build.xml`
     - `build.properties`
     
     have been replaced with a SBT script [build.sbt](./build.sbt)
   - the build script no longer contains commands to build sources in `testsrc` and `APP` directories
     because these directories are entirely removed from the distribution

Users of the library are encouraged to read the original [licence agreement of SCO Library](https://www.cs.ox.ac.uk/people/bernard.sufrin/personal/CSO/ArtisticLicence.txt) regarding further distribution of the library (e.g. in a fat Jar).
