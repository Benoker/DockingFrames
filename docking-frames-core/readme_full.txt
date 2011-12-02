*********************************
* DockingFrames                 *
* Version: 1.0.8                *
*********************************

********************************
* 1 * Writing new applications *
********************************

The easiest way to write new applications is to use the common-project. For
that you should perform these steps:

- include "bin/dockingFramesCore.jar" into your class-path.
- include "bin/dockingFramesCommon.jar" into your class-path.
	That will give you access to the core and the common library

- include "src/dockingFramesCore" into your source-path.
- include "src/dockingFramesCommon" into your source-path.
	That will give you access to the source of the libraries. This step
	is not really necessary, but the source often helps understanding 
	new methods
	
- read "common.pdf"
	This is a document that gives an overview of the classes in the common 
	project
	
- bookmark "doc/dockingFramesCommon/index.html"
	The API documentation of the common and the core project.

***********************************
* 2 * Using the core library only *
***********************************

If you want to use only the core library:

- include "bin/dockingFramesCore.jar" into your class-path.
- include "src/dockingFramesCore" into your source-path.	
- read "core.pdf"
	This is a document that gives an overview of the classes in the core library
	
- bookmark "doc/dockingFramesCore/index.html"
	The API documentation of the common and the core project.
	
*************************
* 3 *  List of projects *
*************************

There are a number of different projects in this archive, this is a list of 
them.

DockingFrames Core - The core library of DockingFrames.
	Path: "dockingFramesCore.jar"
	Executable: no
	Dependencies: none

DockingFrames Common - Layer above the core, adds new features
	Path: "dockingFramesCommon.jar"
	Executable: no
	Dependencies: 
		dockingFramesCore

Tutorial - A set of very small classes containing some basic code
	Path: "tutorial.jar"
	Executable: yes
	Dependencies: 
		dockingFramesCore
		dockingFramesCommon

Interfaces - Interfaces needed for the demonstration-application
	Path: "interfaces.jar"
	Executable: no
	Dependencies: dockingFramesCommon

Demonstration - a client starting other applications, shows some demonstrations
	Path: "demonstration.jar"
	Executable: yes
	Dependencies:
		dockingFramesCore
		dockingFramesCommon
		interfaces
		notes
		help
		chess
		paint
		sizeAndColor
		commonLayouts

Notes - an application showing some notes
	Path: "notes.jar"
	Executable: yes
	Dependencies:
		dockingFramesCore
		dockingFramesCommon
		interfaces

Help - an application that displays JavaDoc
	Path: "help.jar"
	Executable: yes
	Dependencies: 
		dockingFramesCore
		dockingFramesCommon
		interfaces
		For compilation only:[JDK]/lib/tools.jar

Chess - a chess application for two players
	Path: "chess.jar"
	Executable: yes
	Dependencies:
		dockingFramesCore
		dockingFramesCommon
		interfaces

Paint - application to paint images
	Path: "paint.jar"
	Executable: yes
	Dependencies:
		dockingFramesCore
		dockingFramesCommon
		interfaces
		
SizeAndColor - application to demonstrate new features of version 1.0.3
	Path: "sizeAndColor.jar"
	Executable: yes
	Dependencies:
		dockingFramesCore
		dockingFramesCommon
		interfaces
		
Common Layouts - application to test the layout storage capabilities of common
	Path: "commonLayouts.jar"
	Executable: yes
	Dependencies:
		dockingFramesCore
		dockingFramesCommon
		interfaces 