Readme

This project can be found on: dock.javaforge.com

To learn how the framework works:
 - read the guides
 - visit http://forum.byte-welt.net/forumdisplay.php?f=69 if you have any questions

 - for non-technical support you can also contact me directly: benjamin_sigg@gmx.ch

Each directory represents an own project:
	docking-frames-core: The basic project containing the core drag and drop mechanism. All other projects depend on this one.
	docking-frames-common: Project for fast development of applications, a layer to hide the complexity of dockingFrame
	docking-frames-ext-glass: An additional set of tabs for the EclipseTheme

	docking-frames-tutorial: A set of small code snippets demonstrating aspects of the projects.
	
	docking-frames-demo-app-ice: public interfaces of the demonstration framework
	docking-frames-demo-app: demonstration framework
	docking-frames-demo-help: a client of the demonstration-framework, shows JavaDoc.
	docking-frames-demo-notes: a client of the demonstration-framework, shows some notes
	docking-frames-demo-chess: a client of the demonstration-framework, creates a new type of DockStation
	docking-frames-demo-paint: a client using the common project
	docking-frames-demo-size-and-color: a client using the common project
	docking-frames-demo-layouts: a client allowing to play a bit with persistent storage of layouts.

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
