
README for version 1.0.7 preview 5

-----------------
Table of contents

1. Introduction
2. Algorithms
2.1 DockFrontend
2.2 MissingDockableStrategy
2.3 CControl
2.3.1 SingleCDockable
2.3.2 MultipleCDockable
3. Usage
4. Important Notes

---------------
1. Introduction

More changes were made to the storage mechanism for layouts. The mechanism
is now also supported by Common.

-------------
2. Algorithms

There are not that many changes compared to preview 4, a few more methods
have been added but the basic concept remains unchanged.

The most important change is, that 'DockFrontend' stores the whole layout
as it was given to "setSetting". Later the layout can be read again in order
to restore the location of unnamed 'Dockable's (in preview 4 only elements 
with a unique identifier could be restored).

----------------
2.1 DockFrontend

DockFrontend has now more than one method "fillMissing". The new "fillMissing"
takes a 'DockFactory' and tries to read the last used 'Setting' to recreate
missing 'Dockable's. The last 'Setting' can be accessed through 
'getLastAppliedFullSetting' or 'getLastAppliedEntrySetting'.

These last layouts can also be used to just reload the layout once all the
'Dockable's and factories are available. That would look somewhat like this:

	frontend.setSetting( frontend.getLastAppliedFullSetting(), false );
	
Note that "getLastAppliedFullSetting" can return 'null'.

---------------------------
2.2 MissingDockableStrategy

This interface has new methods "shouldCreate". These methods tell 'DockFrontend'
whether to create 'Dockable's for some factory or not. These new methods are
used only in the new "fillMissing" method. 

------------
2.3 CControl

'CControl' uses these new methods. 'SingleCDockable's and 'MultipleCDockable's 
have however a different behavior.

2.3.1 SingleCDockable
---------------------

Since every 'SingleCDockable' has a unique identifier they behave just like
named 'Dockable's of a 'DockFrontend'. As a result:
- When loading a layout, the location of a 'SingleCDockable' is read and stored
  in a special location.
- Always the freshest data is used. The data is tracked for each 'SingleCDockable'
  individually, so different dockables can use data from different layouts.
- It is possible to store a layout again without loosing the location information. 

2.3.2 MultipleCDockable
-----------------------

'MultipleCDockable's behave like unnamed 'Dockable's of a 'DockFrontend'. They
receive less attention (as their lifetime is expected to be shorter anyway).
- When adding a 'MultipleCDockableFactory' the last applied layout is used to
  estimate the locations. New elements are created, perhaps replacing old one.
- Just adding a 'MultipleCDockableFactory' which has the same identifier as an
  old one will NOT restore its location.
- Only the freshest layout is used, data from older layouts are dismissed.
- It is not possible to store a layout again, location information will be lost.

3. Usage
--------

Clients need to set the 'MissingCDockableStrategy' of 'CControl', the method
"setMissingStrategy" can be used for that. The default behavior is not to do
anything at all. There are some implementations of 'MissingCDockableStrategy'
stored as constants in 'MissingCDockableStrategy' itself.

4. Important Notes
------------------

The recovery mechanism is in no way perfect. It is a much better idea to first
make sure all factories/dockables are available then to use recovery.

When working with the recovery mechanism, pay special attention to these points:
- set the 'MissingCDockableStrategy' (or 'MissingDockableStrategy' when using
  'DockFrontend'). Otherwise recovery is disabled.
- Do not, under no circumstances (unless you exactly know what you do, and not
  even then) mix unique identifiers. The worst thing that can happen is a
  factory that tries to read stuff of another factory.
- 'SingleCDockable's receive much more support than 'MultipleCDockable's. It
  might even be better to disable recovery for 'MultipleCDockable's.
  
This new mechanism uses a new format to store layouts. Old versions of the library
will not load these layouts correctly (if at all). Layouts stored by any version
1.0.4 or newer can be read by this mechanism.

And finally: this mechanism might receive an upgrade in future versions. It is
likely that some interfaces and methods will be changed again.