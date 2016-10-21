/*
 * Bibliothek - DockingFrames
 * Library built on Java/Swing, allows the user to "drag and drop"
 * panels containing any Swing-Component the developer likes to add.
 * 
 * Copyright (C) 2010 Benjamin Sigg
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
 * Benjamin Sigg
 * benjamin_sigg@gmx.ch
 * CH - Switzerland
 */
package bibliothek.gui.dock.util.icon;

import java.util.HashMap;
import java.util.Map;

import javax.swing.Icon;

import bibliothek.gui.DockController;
import bibliothek.gui.dock.util.AbstractUIScheme;
import bibliothek.gui.dock.util.DockProperties;
import bibliothek.gui.dock.util.IconManager;
import bibliothek.gui.dock.util.PropertyKey;
import bibliothek.gui.dock.util.PropertyValue;
import bibliothek.gui.dock.util.UIProperties;
import bibliothek.gui.dock.util.UIScheme;

/**
 * This abstract implementation of an {@link UIScheme} offers support for transferring values
 * from the {@link DockProperties} to the {@link IconManager}.
 * @author Benjamin Sigg
 */
public abstract class AbstractIconScheme extends AbstractUIScheme<Icon, DockIcon, DockIconBridge>{
	private DockController controller;
	
	private int bound = 0;
	
	/** all the links that are currently used */
	private Map<String, Link> links;
	
	/**
	 * Creates a new scheme
	 * @param controller the controller in whose realm this scheme will be used
	 */
	public AbstractIconScheme( DockController controller ){
		this.controller = controller;
	}
	
	/**
	 * Creates a link between the value of <code>key</code> and the entry of identifier
	 * <code>id</code>.
	 * @param key the property to observe
	 * @param id the identifier of the value to set
	 */
	public void link( PropertyKey<Icon> key, String id ){
		if( links == null ){
			links = new HashMap<String, Link>();
		}
		
		Link link = links.get( id );
		if( link != null ){
			link.setKey( key );
		}
		else{
			link = new Link( key, id );
			links.put( id, link );
			if( bound > 0 ){
				link.setProperties( controller );
			}
		}
	}
	
	/**
	 * Removes the link between the entry for <code>id</code> and a {@link PropertyKey}.
	 * @param id the link to remove
	 */
	public void unlink( String id ){
		if( links != null ){
			Link link = links.remove( id );
			if( links.isEmpty() ){
				links = null;
			}
			if( link != null ){
				link.setProperties( (DockProperties)null );
			}
		}
	}
	
	/**
	 * Called if property accessed through a {@link PropertyKey} changed its value. 
	 * @param id the identifier of the value that changed
	 * @param icon the new value, can be <code>null</code>
	 */
	protected abstract void changed( String id, Icon icon );
	
	public void install( UIProperties<Icon, DockIcon, DockIconBridge> properties ){
		if( bound == 0 && links != null ){
			for( Link link : links.values() ){
				link.setProperties( controller );
			}
		}
		bound++;
	}
	
	public void uninstall( UIProperties<Icon, DockIcon, DockIconBridge> properties ){
		bound--;
		if( bound == 0 && links != null ){
			for( Link link : links.values() ){
				link.setProperties( (DockProperties)null );
			}
		}
	}
	
	private class Link extends PropertyValue<Icon>{
		private String id;
		
		public Link( PropertyKey<Icon> key, String id ){
			super( key );
			this.id = id;
		}
		
		@Override
		protected void valueChanged( Icon oldValue, Icon newValue ){
			changed( id, newValue );
		}
	}
}
