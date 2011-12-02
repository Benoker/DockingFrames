
README for version 1.0.7 preview 4

-----------------
Table of contents

1. Introduction
2. Algorithms
2.1 DockSituation
2.1.1 read
2.1.2 write
2.2 PredefinedDockSituation
2.2.1 read
2.2.2 write
3. Usage
4. Important Note

---------------
1. Introduction

This version the mechanism used to store layouts (position of Dockables compared
to their parent DockStations) has been altered. The new mechanism can now also 
process information for Dockables which are missing.
The mechanism is supported by DockFrontend, PredefinedDockSituation and 
DockSituation, but not yet by any class from the common project.

-------------
2. Algorithms
To better understand what is happening, this chapter will give an overview
of the algorithms used to read and write the layout.

-----------------
2.1 DockSituation
DockSituation handles the low level stuff of the layout mechanism. It is the
only class that has direct contact with the files that store the layout.

There are two important new methods:

"fillMissing" takes a layout that is in the intermediate format (so called
DockLayoutComposition) and tries to fill gaps. Gaps are produced when some
DockFactory is missing while the layout-file is read. This new method can use
newly registered DockFactories to re-read the gaps.

"estimateLocations" takes a layout in its intermediate format and tries
to guess where the Dockables will show up. It produces a new DockableProperty
for each element and stores the properties directly within the
DockLayoutComposition.


----------
2.1.1 read
Reading a layout works as follows:

- From file to intermediate format
 > Open file. The file is divided into "entries"
 > For each entry:
 - > Read id of DockFactory
 - > If there is a DockFactory for the id: use the factory to create 
     a DockLayout
 - > If there is no DockFactory for the id: Try using the "MissingFactory"
     to create a DockLayout
 - > If no DockLayout has been produced until now: store the data from the file
     in its raw format (byte[] or xml).
 - > Read additional layout information using "AdjacentFactory"s.
 > For each entry a DockLayoutComposition is produced. This DockLayoutComposition
   contains a DockLayoutInfo. The DockLayoutInfo contains either a 
   DockLayout, or data in raw format.

- From intermediate format to DockElements
 > For each DockLayoutComposition
 - > Search the DockFactory which can be used to read the DockLayout of 
     that composition.
 - > If a factory was found: use it to create a new DockStation or Dockable
 - > If no factory was found: ignore this composition

-----------
2.1.2 write
Writing works just the opposite way then reading.

The intermediate format is converted into raw data using DockFactories. If
some data is already in raw format, then it is just written without further
conversions. However data in raw format can only be written out if it is the
correct raw format, writing xml-raw-format to a stream does not work, neither
writing byte[]-raw-format to xml. An IllegalArgumentException will be thrown
if such a case is found.

---------------------------
2.2 PredefinedDockSituation
The PredefinedDockSituation uses a wrapper-DockFactory for most of its
entries. Any operation first goes into that wrapper-DockFactory which then
forwards the operation to another DockFactory.
The PredefinedDockSituation now also handles the case when an operation cannot
be forwarded because there is no DockFactory present.

There is one new interesting method:
"shouldLayout" which returns a boolean:
- If true: then the contents of a DockElement are stored. This means that a
  DockFactory is used to convert the DockElement into raw data and store it.
  On the other hand raw data gets converted into a DockElement on read.
- If false: then the contents of a DockElement are not stored, the location
  however is stored.

----------
2.2.1 read
The wrapper-DockFactory is used for almost any operation. It forwards all 
read-calls to other DockFactories. If it cannot forward a read-call, then
it stores the data in raw format (xml or byte[]).

The method "fillMissing" can later use the data in raw format to create
the intermediate format (assuming the missing DockFactory is added by then).

-----------
2.2.2 write
Write just works as usual. If data in raw-format is found it is just written
out, assuming that the file-format and the raw-format are the same. Otherwise
an IllegalArgumentException is raised.

----------------
3. Usage
DockFrontend uses PredefinedDockSituation and hence supports the new mechanisms.
Per default the mechanism is not enabled, that helps save memory which might
otherwise be filled over a long period of time.

To enable the mechanism a MissingDockableStrategy has to be set. The method
"setMissingDockableStrategy" can be used for that. The strategy 
"MissingDockableStrategy.STORE_ALL" can be used to just store as much data
as possible, but clients can also implement more sophisticated strategies in 
case that some Dockables are sure to never show up again.

When data for a missing Dockable is found, and approved by the
MissingDockableStrategy, then a new "empty info" is created. An empty info
contains information about a Dockable without knowing the Dockable. Clients
can use "addEmpty", "removeEmpty" and "listEmpty" to query and change the set of
empty infos.

Note that adding a set of "empty infos" has the same effect than setting a
strategy that just approves of those elements whose identifier is within the
set of "empty infos". Setting "empty infos" is an eager strategy, while
using MissingDockableStrategy is a lazy one.

DockFrontend can now store a DockLayoutComposition for any Dockable. This 
composition is used to set the contents of a Dockable. The differences between
this version and the previous are:
- Compositions are stored for invisible Dockables as well. Previously only
  the contents of visible Dockable was stored. This can be considered to be
  a bugfix.
- When adding a Dockable and a composition is present, then this composition
  is immediately used to update the contents of the Dockable

There are two cases when to store or load compositions:
- when writing or reading the layout to a file (so called Setting)
- when writing or reading an "entry-Setting". A entry-Setting is like a 
  Setting, but should just contain lightweight layout information. The 
  entry-Settings can normally be applied by the user at any time, and the
  user can give them names.
  
In the first case, the compositions are stored anyway. Although clients can
register DockFactories which do not store much...
In the second case clients can set whether to store the compositions or not.
The method "setEntryLayout" can be used for that. The default value of this
property can be set by the method "setDefaultEntryLayout".

-----------------
4. Important Note
This mechanism is not finished. Further modifications are likely.

"Common" does not yet support this new mechanism. It will however in the final
release version.