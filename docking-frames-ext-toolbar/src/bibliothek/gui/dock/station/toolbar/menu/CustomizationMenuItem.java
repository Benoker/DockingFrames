/**
 * 
 */
package bibliothek.gui.dock.station.toolbar.menu;

import java.awt.Component;

import bibliothek.gui.DockController;

/**
 * The <code>CustomizationMenuItem</code> is a simple <code>Component</code>.
 * 
 * @author "Herve Guillaume"
 * 
 */
public class CustomizationMenuItem implements CustomizationMenuContent{

	private final Component component;

	/**
	 * Creates a new item.
	 * 
	 * @param dockable
	 *            the item represented by this button
	 */
	public CustomizationMenuItem( Component component ){
		this.component = component;
	}

	@Override
	public Component getView(){
		return component;
	}

	/**
	 * Do nothing, so any call is useless.
	 */
	@Override
	public void setController( DockController controller ){
		// do nothing
	}

	/**
	 * Do nothing, so any call is useless.
	 */
	@Override
	public void bind( CustomizationMenuCallback callback ){
		// do nothing
	}

	/**
	 * Do nothing, so any call is useless.
	 */
	@Override
	public void unbind(){
		// do nothing
	}

}
