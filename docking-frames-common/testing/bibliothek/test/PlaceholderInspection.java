package bibliothek.test;

import bibliothek.gui.dock.FlapDockStation;
import bibliothek.gui.dock.ScreenDockStation;
import bibliothek.gui.dock.SplitDockStation;
import bibliothek.gui.dock.StackDockStation;
import bibliothek.gui.dock.common.CControl;
import bibliothek.gui.dock.common.SingleCDockable;
import bibliothek.gui.dock.station.split.Leaf;
import bibliothek.gui.dock.station.split.Node;
import bibliothek.gui.dock.station.split.Placeholder;
import bibliothek.gui.dock.station.support.PlaceholderMap;
import bibliothek.gui.dock.station.support.PlaceholderMap.Key;
import bibliothek.test.inspect.CControlInspectable;
import bibliothek.test.inspect.FlapDockStationInspectable;
import bibliothek.test.inspect.KeyInspectable;
import bibliothek.test.inspect.PlaceholderMapInspectable;
import bibliothek.test.inspect.ScreenDockStationInspectable;
import bibliothek.test.inspect.SingleCDockableInspectable;
import bibliothek.test.inspect.SplitDockStationInspectable;
import bibliothek.test.inspect.SplitLeafInspectable;
import bibliothek.test.inspect.SplitNodeInspectable;
import bibliothek.test.inspect.SplitPlaceholderInspectable;
import bibliothek.test.inspect.StackDockStationInspectable;

public class PlaceholderInspection extends InspectionGraph{
	public PlaceholderInspection(){
		putInspectableAdapter( CControl.class, CControlInspectable.class );
		putInspectableAdapter( SingleCDockable.class, SingleCDockableInspectable.class );
		putInspectableAdapter( FlapDockStation.class, FlapDockStationInspectable.class );
		putInspectableAdapter( SplitDockStation.class, SplitDockStationInspectable.class );
		putInspectableAdapter( ScreenDockStation.class, ScreenDockStationInspectable.class );
		putInspectableAdapter( Leaf.class, SplitLeafInspectable.class );
		putInspectableAdapter( Node.class, SplitNodeInspectable.class );
		putInspectableAdapter( Placeholder.class, SplitPlaceholderInspectable.class );
		putInspectableAdapter( StackDockStation.class, StackDockStationInspectable.class );
		putInspectableAdapter( PlaceholderMap.class, PlaceholderMapInspectable.class );
		putInspectableAdapter( Key.class, KeyInspectable.class );
	}
}
