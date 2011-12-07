package bibliothek.gui.dock.station.toolbar;

import bibliothek.gui.dock.layout.DockableProperty;
import bibliothek.gui.dock.layout.DockablePropertyFactory;

/**
 * Creates new {@link ToolbarContainerProperty}s.
 * @author Benjamin Sigg
 * @author Herve Guillaume
 */
public class ToolbarContainerPropertyFactory implements DockablePropertyFactory{
	/** the unique identifier of this factory */
	public static final String ID = "ToolbarContainerProperty";
	
	@Override
	public String getID(){
		return ID;
	}
	
	@Override
	public DockableProperty createProperty(){
		return new ToolbarContainerProperty();
	}
}