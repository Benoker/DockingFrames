/*
 * Bibliothek - DockingFrames
 * Library built on Java/Swing, allows the user to "drag and drop"
 * panels containing any Swing-Component the developer likes to add.
 * 
 * Copyright (C) 2012 Herve Guillaume, Benjamin Sigg
 * 
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301  USA
 * 
 * Herve Guillaume
 * rvguillaume@hotmail.com
 * FR - France
 *
 * Benjamin Sigg
 * benjamin_sigg@gmx.ch
 * CH - Switzerland
 */

package bibliothek.gui.dock.toolbar;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import bibliothek.gui.DockController;
import bibliothek.gui.dock.common.CControl;
import bibliothek.gui.dock.common.location.CLocationExpandStrategy;
import bibliothek.gui.dock.common.location.DefaultExpandStrategy;
import bibliothek.gui.dock.facile.mode.status.DefaultExtendedModeEnablement;
import bibliothek.gui.dock.facile.mode.status.ExtendedModeEnablementFactory;
import bibliothek.gui.dock.toolbar.intern.ToolbarExtendedModeEnablement;
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
		
		if( extension.getName().equals( DefaultExtendedModeEnablement.EXTENSION )){
			return (Collection<E>) createEnablements();
		}
		
		return null;
	}

	protected Collection<CLocationExpandStrategy> createExpandStrategy(){
		List<CLocationExpandStrategy> result = new ArrayList<CLocationExpandStrategy>();
		result.add( new ToolbarExpandStrategy() );
		return result;
	}
	
	protected Collection<ExtendedModeEnablementFactory> createEnablements(){
		List<ExtendedModeEnablementFactory> result = new ArrayList<ExtendedModeEnablementFactory>();
		result.add( ToolbarExtendedModeEnablement.FACTORY );
		return result;
	}
}
