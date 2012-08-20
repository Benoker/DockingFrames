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
package bibliothek.gui.dock.common.perspective;

import bibliothek.gui.dock.common.CControl;
import bibliothek.gui.dock.common.MultipleCDockable;
import bibliothek.gui.dock.common.MultipleCDockableFactory;
import bibliothek.gui.dock.common.MultipleCDockableLayout;
import bibliothek.gui.dock.common.intern.CPlaceholderStrategy;
import bibliothek.gui.dock.perspective.PerspectiveDockable;
import bibliothek.gui.dock.perspective.PerspectiveStation;
import bibliothek.util.Path;

/**
 * Representation of a {@link MultipleCDockable} in a perspective.
 * @author Benjamin Sigg
 */
public class MultipleCDockablePerspective extends AbstractCDockablePerspective{
	private String factory;
	private MultipleCDockableLayout layout;
	private CommonElementPerspective intern;
	private String uniqueId;
	
	/**
	 * Creates a new representation. The identifier of the factory must match an identifier of a 
	 * {@link MultipleCDockableFactory} that is registered at a {@link CControl} through the method
	 * {@link CControl#addMultipleDockableFactory(String, MultipleCDockableFactory)}.
	 * @param factoryId the unique identifier of a {@link MultipleCDockableFactory}, not <code>null</code>
	 * @param layout description of the content of this dockable, will be given to the {@link MultipleCDockableFactory}
	 * that is accessed through <code>factoryId</code>
	 */
	public MultipleCDockablePerspective( String factoryId, MultipleCDockableLayout layout ){
		this( factoryId, null, layout );
	}
	
	/**
	 * Creates a new representation. The identifier of the factory must match an identifier of a 
	 * {@link MultipleCDockableFactory} that is registered at a {@link CControl} through the method
	 * {@link CControl#addMultipleDockableFactory(String, MultipleCDockableFactory)}.
	 * @param factoryId the unique identifier of a {@link MultipleCDockableFactory}, not <code>null</code>
	 * @param uniqueId a unique identifier for this dockable, can be <code>null</code>
	 * @param layout description of the content of this dockable, will be given to the {@link MultipleCDockableFactory}
	 * that is accessed through <code>factoryId</code>
	 */
	public MultipleCDockablePerspective( String factoryId, String uniqueId, MultipleCDockableLayout layout ){
		if( factoryId == null ){
			throw new IllegalArgumentException( "factoryId must not be null" );
		}
		if( layout == null ){
			throw new IllegalArgumentException( "layout must not be null" );
		}
		
		this.factory = factoryId;
		this.uniqueId = uniqueId;
		this.layout = layout;
	}
	
	/**
	 * Sets the unique identifier of this element. The identifier can be used to replace an  existing {@link MultipleCDockable} 
	 * with the dockable that is represented by this. If no identifier is set, then a random identifier will be set when the {@link CPerspective} is applied.
	 * @param uniqueId the unique id or <code>null</code>
	 */
	public void setUniqueId( String uniqueId ){
		this.uniqueId = uniqueId;
	}
	
	/**
	 * Gets the unique identifier of this element.
	 * @return the unique identifier, can be <code>null</code>
	 */
	public String getUniqueId(){
		return uniqueId;
	}
	
	/**
	 * Called the first time {@link #intern()} is called, creates the internal representation of this 
	 * dockable.
	 * @return the internal representation, not <code>null</code>
	 */
	protected CommonElementPerspective create(){
		return new Intern();
	}
	
	public CommonElementPerspective intern(){
		if( intern == null ){
			intern = create();
		}
		return intern;
	}

	
	/**
	 * Sets the layout which describes this dockable.
	 * @param layout the layout, not <code>null</code>
	 */
	public void setLayout( MultipleCDockableLayout layout ){
		if( layout == null ){
			throw new IllegalArgumentException( "layout must not be null" );
		}
		this.layout = layout;
	}
	
	/**
	 * Gets the layout which describes the content of this dockable.
	 * @return the layout, not <code>null</code>
	 */
	public MultipleCDockableLayout getLayout(){
		return layout;
	}

	public CDockablePerspective asDockable(){
		return this;
	}
	
	public CStationPerspective asStation(){
		return null;
	}

	/**
	 * Gets the unique identifier of the factory that handles this kind of dockable.
	 * @return the factory, not <code>null</code>
	 */
	public String getFactoryID(){
		return factory;
	}
	
	/**
	 * The default representation for a {@link MultipleCDockablePerspective}.
	 * @author Benjamin Sigg
	 */
	protected class Intern implements PerspectiveDockable, CommonElementPerspective{
		private PerspectiveStation parent;
		
		public CElementPerspective getElement(){
			return MultipleCDockablePerspective.this;
		}
		
		public String getFactoryID(){
			return factory;
		}
		
		public PerspectiveStation asStation(){
			return null;
		}
		
		public PerspectiveDockable asDockable(){
			return this;
		}
		
		public void setParent( PerspectiveStation parent ){
			this.parent = parent;	
		}
		
		public Path getPlaceholder(){
			if( uniqueId == null ){
				return null;
			}
			return CPlaceholderStrategy.getMultipleDockablePlaceholder( uniqueId );
		}
		
		public PerspectiveStation getParent(){
			return parent;
		}
	}
}
