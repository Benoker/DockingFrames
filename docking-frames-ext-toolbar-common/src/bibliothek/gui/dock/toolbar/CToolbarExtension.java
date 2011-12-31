package bibliothek.gui.dock.toolbar;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import bibliothek.gui.DockController;
import bibliothek.gui.dock.common.CControl;
import bibliothek.gui.dock.common.location.CLocationExpandStrategy;
import bibliothek.gui.dock.common.location.DefaultExpandStrategy;
import bibliothek.gui.dock.toolbar.location.CToolbarMode;
import bibliothek.gui.dock.toolbar.location.ToolbarExpandStrategy;
import bibliothek.gui.dock.util.extension.Extension;
import bibliothek.gui.dock.util.extension.ExtensionName;

/**
 * This extension adds toolbars to the common project.
 * @author Benjamin Sigg
 */
public class CToolbarExtension implements Extension{
	@Override
	public void install( DockController controller ){
		// ignore
	}
	
	@Override
	public void uninstall( DockController controller ){
		CControl control = controller.getProperties().get( CControl.CCONTROL );
		if( control != null ){
			uninstall( control );
		}
	}

	/**
	 * Installs this extension on <code>control</code>.
	 * @param control the control using this extension
	 */
	protected void install( CControl control ){
		control.getLocationManager().putMode( new CToolbarMode( control ) );
	}
	
	/**
	 * Removes this extension from <code>control</code>.
	 * @param control the control which is no longer using this extension
	 */
	protected void uninstall( CControl control ){
		
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public <E> Collection<E> load( DockController controller, ExtensionName<E> extension ){
		if( extension.getName().equals( CControl.CCONTROL_EXTENSION )){
			install( (CControl)extension.get( CControl.EXTENSION_PARAM ));
			return null;
		}
		
		if( extension.getName().equals( DefaultExpandStrategy.STRATEGY_EXTENSION )){
			return (Collection<E>) createExpandStrategy();
		}
		
		// TODO Auto-generated method stub
		return null;
	}

	protected Collection<CLocationExpandStrategy> createExpandStrategy(){
		List<CLocationExpandStrategy> result = new ArrayList<CLocationExpandStrategy>();
		result.add( new ToolbarExpandStrategy() );
		return result;
	}
}
