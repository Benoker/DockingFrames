# Readme

The newest versions of this project are published on http://docking-frames.org

To learn how the framework works:

* read the guides
* visit http://forum.byte-welt.net/forumdisplay.php?f=69 if you have any questions
* for non-technical support you can also contact me directly: benjamin_sigg@gmx.ch

Each directory represents an own project:

* **docking-frames-core**: The basic project containing the core drag and drop mechanism. All other projects depend on this one.
* **docking-frames-common**: Project for fast development of applications, a layer to hide the complexity of dockingFrame
* **docking-frames-ext-glass**: An additional set of tabs for the EclipseTheme
* **docking-frames-tutorial**: A set of small code snippets demonstrating aspects of the projects.
* **docking-frames-demo-app-ice**: public interfaces of the demonstration framework
* **docking-frames-demo-app**: demonstration framework
* **docking-frames-demo-help**: a client of the demonstration-framework, shows JavaDoc.
* **docking-frames-demo-notes**: a client of the demonstration-framework, shows some notes
* **docking-frames-demo-chess**: a client of the demonstration-framework, creates a new type of DockStation
* **docking-frames-demo-paint**: a client using the common project
* **docking-frames-demo-size-and-color**: a client using the common project
* **docking-frames-demo-layouts**: a client allowing to play a bit with persistent storage of layouts.

The projects have these dependencies:

    docking-frames-core:
    - no dependencies

    docking-frames-common:
    + docking-frames-core

    docking-frames-ext-glass:
    + docking-frames-core
    + docking-frames-common (optional during runtime)

    docking-frames-tutorial:
    + docking-frames-core
    + docking-frames-common

    docking-frames-demo-app-ice
    + docking-frames-common

    docking-frames-demo-app
    + docking-frames-demo-app-ice
    + docking-frames-core
    + docking-frames-common
    + docking-frames-demo-help
    + docking-frames-demo-notes
    + docking-frames-demo-chess
    + docking-frames-demo-paint
    + docking-frames-demo-size-and-color
    + docking-frames-demo-layouts

    docking-frames-demo-help
    + docking-frames-demo-app-ice
    + docking-frames-core
    + docking-frames-common
    + lib/tools.jar, can be found in the JDK

    docking-frames-demo-notes
    + docking-frames-demo-app-ice
    + docking-frames-core

    docking-frames-demo-chess
    + docking-frames-demo-app-ice
    + docking-frames-core

    docking-frames-demo-paint
    + docking-frames-demo-app-ice
    + docking-frames-core
    + docking-frames-common

    docking-frames-demo-size-and-color
    + docking-frames-demo-app-ice
    + docking-frames-core
    + docking-frames-common

    docking-frames-demo-layouts
    + docking-frames-demo-app-ice
    + docking-frames-core
    + docking-frames-common

## Maven

Please note: the library is no longer updated in maven. Services will not resume in the near future. Please download directly from https://docking-frames.org .

### Maven Snapshot Repository (outdated version)

you can get an outdated project snapshot here:
https://oss.sonatype.org/content/repositories/snapshots/org/dockingframes/

or add to your pom.xml:

    <repositories>

        <repository>
            <id>sonatype-oss-snapshots</id>
            <name>Sonatype OSS Maven Repository for Staging Snapshots</name>
            <url>https://oss.sonatype.org/content/repositories/snapshots</url>
            <releases>
                <enabled>false</enabled>
            </releases>
            <snapshots>
                <enabled>true</enabled>
            </snapshots>
        </repository>

    </repositories>

    <dependencies>
        <dependency>
            <groupId>org.dockingframes</groupId>
            <artifactId>docking-frames-common</artifactId>
            <version>1.1.2-SNAPSHOT</version>
        </dependency>
    </dependencies>

be sure to use latest &lt;version>-SNAPSHOT


### Maven Release Repository (outdated version)

you can an outdated stable release from maven central:
http://search.maven.org/#artifactdetails|org.dockingframes|docking-frames-common|1.1.1|jar

just add to your pom.xml:

    <dependencies>
        <dependency>
            <groupId>org.dockingframes</groupId>
            <artifactId>docking-frames-common</artifactId>
            <version>1.1.1</version>
        </dependency>
    </dependencies>

be sure to use latest &lt;version>

