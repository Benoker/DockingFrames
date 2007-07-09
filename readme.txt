Readme

Each directory represents an own project:
	dockingFrame - the DockingFrames
	demo - small demonstrations building a tutorial

	demonstration_interface - public interfaces of the demonstration framework
	demonstration - demonstration framework
	help - a client of the demonstration-framework, shows JavaDoc.
	notes - a client of the demonstration-framework, shows some notes

The projects have these dependencies:
	dockingFrame:
	- no dependencies

	demo:
	+ dockingFrames

	demonstration_interface
	- no dependencies

	demonstration
	+ demonstration_interface
	+ help
	+ notes

	help
	+ demonstration_interface
	+ dockingFrames
	+ lib/tools.jar, can be found in the JDK
	
	notes
	+ demonstration_interface
	+ dockingFrames