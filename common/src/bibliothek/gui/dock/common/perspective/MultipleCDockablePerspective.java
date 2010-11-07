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
import bibliothek.gui.dock.perspective.PerspectiveDockable;
import bibliothek.gui.dock.perspective.PerspectiveStation;
import bibliothek.util.Path;

/**
 * Representation of a {@link MultipleCDockable} in a perspective.
 * @author Benjamin Sigg
 * @param <L> the kind of layout data this element needs to describe its content
 */
public class MultipleCDockablePerspective<L extends MultipleCDockableLayout> implements PerspectiveDockable{
	private String factory;
	private L layout;
	private PerspectiveStation parent;
	private PerspectiveStation workingArea;
	
	/**
	 * Creates a new representation. The identifier of the factory must match an identifier of a 
	 * {@link MultipleCDockableFactory} that is registered at a {@link CControl} through the method
	 * {@link CControl#addMultipleDockableFactory(String, MultipleCDockableFactory)}.
	 * @param factoryId the unique identifier of a {@link MultipleCDockableFactory}, not <code>null</code>
	 * @param layout description of the content of this dockable, will be given to the {@link MultipleCDockableFactory}
	 * that is accessed through <code>factoryId</code>
	 */
	public MultipleCDockablePerspective( String factoryId, L layout ){
		if( factoryId == null ){
			throw new IllegalArgumentException( "factoryId must not be null" );
		}
		if( layout == null ){
			throw new IllegalArgumentException( "layout must not be null" );
		}
		
		this.factory = factoryId;
		this.layout = layout;
	}
	
	/**
	 * Sets the working-area of this element. This user will not be able drag
	 * this element away from that working-area.
	 * @param workingArea the working-area, can be <code>null</code>
	 */
	public void setWorkingArea( PerspectiveStation workingArea ){
		this.workingArea = workingArea;
	}
	
	/**
	 * Gets the working-area of this element
	 * @return the area, can be <code>null</code>
	 * @see #setWorkingArea(PerspectiveStation)
	 */
	public PerspectiveStation getWorkingArea(){
		return workingArea;
	}
	
	/**
	 * Sets the layout which describes this dockable.
	 * @param layout the layout, not <code>null</code>
	 */
	public void setLayout( L layout ){
		if( layout == null ){
			throw new IllegalArgumentException( "layout must not be null" );
		}
		this.layout = layout;
	}
	
	/**
	 * Gets the layout which describes the content of this dockable.
	 * @return the layout, not <code>null</code>
	 */
	public L getLayout(){
		return layout;
	}
	
	public PerspectiveStation getParent(){
		return parent;
	}
	
	public Path getPlaceholder(){
		return null;
	}
	
	public void setParent( PerspectiveStation parent ){
		this.parent = parent;	
	}
	
	public PerspectiveDockable asDockable(){
		return this;
	}
	
	public PerspectiveStation asStation(){
		return null;
	}
	
	public String getFactoryID(){
		return factory;
	}
}
