# CSO-mirror

The repository to host a mirror of Communicating Scala Object (SCO) library.

## Licencing

This SCO library in this repository is redistributed under the [Artistic Licence 2.0](https://www.cs.ox.ac.uk/people/bernard.sufrin/personal/CSO/ArtisticLicence.txt), the licence given in the original distribution of SCO, as permitted by the Section (4) of the Licence. The Standard Version of the library can be obtained from [https://www.cs.ox.ac.uk/people/bernard.sufrin/personal/CSO/](https://www.cs.ox.ac.uk/people/bernard.sufrin/personal/CSO/).

The condition (4) of the Artistic License 2.0 applies to the Distribution of Modified Versions. This repository contains a Modified Version of the library, with the following difference:
 - the repository only contains the `src` folder of the Standard Version
 - build script has been largely altered
   - namely, the following Ant-based configuration files:
     - `scalatasks.xml`
     - `scala.properties`
     - `build.xml`
     - `build.properties`
     
     has been replaced with a SBT script [build.sbt](./build.sbt)
   - the build script no longer contains commands to build sources in `testsrc` and `APP` directories
     because these directories are entirely removed from the distribution

Users of the library are encouraged to read the original [licence agreement of SCO Library](https://www.cs.ox.ac.uk/people/bernard.sufrin/personal/CSO/ArtisticLicence.txt) regarding further distribution of the library (e.g. in a fat Jar).
