package bibliothek.gui.dock.station.toolbar;

import bibliothek.gui.dock.layout.DockableProperty;
import bibliothek.gui.dock.layout.DockablePropertyFactory;

/**
 * A factory creating new {@link DockableProperty}s.
 * 
 * @author Benjamin Sigg
 */
public class ToolbarPropertyFactory implements DockablePropertyFactory{
	/** the unique identifier of this factory */
	public static final String ID = "ToolbarPropertyFactory";

	@Override
	public String getID(){
		return ID;
	}

	@Override
	public DockableProperty createProperty(){
		return new ToolbarProperty();
	}
}
