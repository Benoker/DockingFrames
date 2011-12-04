package bibliothek.gui.dock.station.toolbar.group;

import bibliothek.gui.dock.layout.DockableProperty;
import bibliothek.gui.dock.layout.DockablePropertyFactory;

/**
 * This factory creates new {@link ToolbarGroupProperty}s.
 * @author Benjamin Sigg
 */
public class ToolbarGroupPropertyFactory implements DockablePropertyFactory{
	/** the unique identifier of this factory */
	public static final String ID = "ToolbarGroupProperty";
	
	@Override
	public String getID(){
		return ID;
	}

	@Override
	public DockableProperty createProperty(){
		return new ToolbarGroupProperty();
	}

}
