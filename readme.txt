Readme

Each directory represents an own project:
	dockingFrame - the DockingFrames
	demo - small demonstrations building a tutorial

	common - project for fast development of applications, a layer to hide the complexity of dockingFrame

	demonstration_interface - public interfaces of the demonstration framework
	demonstration - demonstration framework
	help - a client of the demonstration-framework, shows JavaDoc.
	notes - a client of the demonstration-framework, shows some notes
	chess - a client of the demonstration-framework, creates a new type of DockStation
	paint - a client using the common project
	sizeAndColor - a client using the common project

The projects have these dependencies:
	dockingFrame:
	- no dependencies

	common:
	+ dockingFrames

	demo:
	+ dockingFrames
	+ common

	demonstration_interface
	+ common

	demonstration
	+ demonstration_interface
	+ dockingFrame
	+ common
	+ help
	+ notes
	+ chess
	+ paint
	+ sizeAndColor

	help
	+ demonstration_interface
	+ dockingFrames
	+ common
	+ lib/tools.jar, can be found in the JDK

	notes
	+ demonstration_interface
	+ dockingFrame
	+ common

	chess
	+ demonstration_interface
	+ dockingFrame
	+ common

	paint
	+ demonstration_interface
	+ dockingFrame
	+ common

	sizeAndColor
	+ demonstration_interface
	+ dockingFrame
	+ common


Maven Snapshot Repository

	you can get current project snapshot here:
	https://oss.sonatype.org/content/repositories/snapshots/org/dockingframes/

	or add to your pom.xml

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
    		<version>1.1.1-SNAPSHOT</version>
		</dependency>
	</dependencies>

	be sure to use latest <version>-SNAPSHOT


Maven Release Repository

	you can get latest release from maven central:
	http://search.maven.org/#artifactdetails|org.dockingframes|docking-frames-common|1.1.0|jar

	just add to your pom.xml

	<dependencies>
		<dependency>
    		<groupId>org.dockingframes</groupId>
    		<artifactId>docking-frames-common</artifactId>
    		<version>1.1.0</version>
		</dependency>
	</dependencies>

	be sure to use latest <version>

