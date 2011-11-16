package bibliothek.gui.dock.station.toolbar;

import bibliothek.gui.dock.layout.DockableProperty;
import bibliothek.gui.dock.layout.DockablePropertyFactory;

/**
 * Creates new {@link ContainerLineProperty}s.
 * @author Benjamin Sigg
 * @author Herve Guillaume
 */
public class ContainerLinePropertyFactory implements DockablePropertyFactory{
	/** the unique identifier of this factory */
	public static final String ID = "ContainerLineProperty";
	
	public String getID(){
		return ID;
	}
	
	public DockableProperty createProperty(){
		return new ContainerLineProperty();
	}
}