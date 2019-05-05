*********************************
* DockingFrames                 *
* Version: 1.1.2                *
*********************************

********************************
* 1 * Writing new applications *
********************************

The easiest way to write new applications is to use the common-project (if you
want to know why, please read the introduction of "core.pdf"). To use the
common-project you should perform these steps:

- include "bin/docking-frames-core.jar" into your class-path.
- include "bin/docking-frames-common.jar" into your class-path.
	That will give you access to the core and the common library

- include "src/docking-frames-core" into your source-path.
- include "src/docking-frames-common" into your source-path.
	That will give you access to the source of the libraries. This step
	is not really necessary, but the source often helps understanding 
	new methods
	
- read "common.pdf"
	This is a document that gives an overview of the classes in the common 
	project
	
- bookmark "doc/docking-frames-common/index.html"
	The API documentation of the common and the core project.

- start the tutorial "tutorial.sh" or "tutorial.bat"
    A little collection of examples showing how to work with the framework.

***********************************
* 2 * Using the core library only *
***********************************

If you want to use only the core library:

- include "bin/docking-frames-core.jar" into your class-path.
- include "src/docking-frames-core" into your source-path.	
- read "core.pdf"
	This is a document that gives an overview of the classes in the core library
	
- bookmark "doc/docking-frames-core/index.html"
	The API documentation of the common and the core project.
	
*************************
* 3 *  List of projects *
*************************

There are a number of different projects in this archive, this is a list of 
them.

DockingFrames Core - The core library of DockingFrames.
	Path: "docking-frames-core.jar"
	Executable: no
	Dependencies: none

DockingFrames Common - Layer above the core, adds new features
	Path: "docking-frames-common.jar"
	Executable: no
	Dependencies: 
		docking-frames-core

Tutorial - A set of very small classes containing some basic code
	Path: "docking-frames-demo-tutorial.jar"
	Executable: yes
	Dependencies: 
		docking-frames-core
		docking-frames-common
		docking-frames-ext-toolbar-tutorial (optional)

Interfaces - Interfaces needed for the demonstration-application
	Path: "docking-frames-demo-app-ice.jar"
	Executable: no
	Dependencies: docking-frames-common

Demonstration - a client starting other applications, shows some demonstrations
	Path: "docking-frames-demo-app.jar"
	Executable: yes
	Dependencies:
		docking-frames-core
		docking-frames-common
		docking-frames-demo-app-ice
		docking-frames-demo-notes
		docking-frames-demo-help
		docking-frames-demo-chess
		docking-frames-demo-paint
		docking-frames-demo-size-and-color
		docking-frames-demo-layout

Notes - an application showing some notes
	Path: "docking-frames-demo-notes.jar"
	Executable: yes
	Dependencies:
		docking-frames-core
		docking-frames-common
		docking-frames-demo-app-ice

Help - an application that displays JavaDoc
	Path: "docking-frames-demo-help.jar"
	Executable: yes
	Dependencies: 
		docking-frames-core
		docking-frames-common
		docking-frames-demo-app-ice
		For compilation only:[JDK]/lib/tools.jar

Chess - a chess application for two players
	Path: "docking-frames-demo-chess.jar"
	Executable: yes
	Dependencies:
		docking-frames-core
		docking-frames-common
		docking-frames-demo-app-ice

Paint - application to paint images
	Path: "docking-frames-demo-paint.jar"
	Executable: yes
	Dependencies:
		docking-frames-core
		docking-frames-common
		docking-frames-demo-app-ice
		
SizeAndColor - application to demonstrate new features of version 1.0.3
	Path: "docking-frames-demo-size-and-color.jar"
	Executable: yes
	Dependencies:
		docking-frames-core
		docking-frames-common
		docking-frames-demo-app-ice
		
Common Layouts - application to test the layout storage capabilities of common
	Path: "docking-frames-demo-layout.jar"
	Executable: yes
	Dependencies:
		docking-frames-core
		docking-frames-common
		docking-frames-demo-app-ice 