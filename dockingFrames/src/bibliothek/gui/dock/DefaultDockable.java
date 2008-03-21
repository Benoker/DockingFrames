/*
 * Bibliothek - DockingFrames
 * Library built on Java/Swing, allows the user to "drag and drop"
 * panels containing any Swing-Component the developer likes to add.
 * 
 * Copyright (C) 2007 Benjamin Sigg
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

package bibliothek.gui.dock;

import java.awt.*;

import javax.swing.Icon;
import javax.swing.JPanel;

import bibliothek.gui.DockStation;
import bibliothek.gui.Dockable;
import bibliothek.gui.dock.dockable.AbstractDockable;
import bibliothek.gui.dock.dockable.DefaultDockableFactory;
import bibliothek.gui.dock.util.PropertyKey;

/**
 * A {@link Dockable} which consists only of one {@link Component} called
 * "content pane". It's possible to add or remove components from the
 * content pane at any time.
 * @author Benjamin Sigg
 */
public class DefaultDockable extends AbstractDockable {
    /** the content pane */
    private JPanel pane = new JPanel( new BorderLayout() );
    
    /** the id used to identify the factory of this dockable */
    private String factoryId = DefaultDockableFactory.ID;
    
    /**
     * Constructs a new DefaultDockable
     */
    public DefaultDockable(){
        this(  null, null, null );
    }

    /**
     * Constructs a new DefaultDockable and sets the icon.
     * @param icon the icon, to be shown at various places
     */
    public DefaultDockable( Icon icon ){
        this( null, null, icon );
    }
    
    /**
     * Constructs a new DefaultDockable and sets the title.
     * @param title the title, to be shown at various places
     */
    public DefaultDockable( String title ){
        this( null, title, null );
    }
    
    /**
     * Constructs a new DefaultDockable and places one component onto the
     * content pane.
     * @param component the only child of the content pane 
     */
    public DefaultDockable( Component component ){
        this( component, null, null );
    }

    /**
     * Constructs a new DefaultDockable, sets an icon and places one
     * component.
     * @param component the only child of the content pane
     * @param icon the icon, to be shown at various places
     */
    public DefaultDockable( Component component, Icon icon ){
        this( component, null, icon );
    }
    
    /**
     * Constructs a new DefaultDockable, sets the title and places one
     * component.
     * @param component the only child of the content pane
     * @param title the title, to be shown at various places
     */
    public DefaultDockable( Component component, String title ){
        this( component, title, null );
    }
    
    /**
     * Constructs a new DefaultDockable, sets the icon and the title, and
     * places a component.
     * @param component the only child of the content pane
     * @param title the title, to be shown at various places
     * @param icon the icon, to be shown at various places
     */
    public DefaultDockable( Component component, String title, Icon icon ){
    	super( PropertyKey.DOCKABLE_ICON, PropertyKey.DOCKABLE_TITLE );
    	pane.setFocusCycleRoot( true );
    	
        if( component != null ){
            getContentPane().setLayout( new GridLayout( 1, 1 ));
            getContentPane().add( component );
        }
        
        setTitleIcon( icon );
        setTitleText( title );
    }
    
    public String getFactoryID() {
        return factoryId;
    }
    
    /**
     * Sets the id for the {@link DockFactory} which will be used to store
     * and load this dockable.
     * @param factoryId the id of the factory
     */
    public void setFactoryID( String factoryId ){
    	if( factoryId == null )
    		throw new IllegalArgumentException( "FactoryID must not be null" );
		this.factoryId = factoryId;
	}
    
    public Component getComponent() {
        return pane;
    }

    public DockStation asDockStation() {
        return null;
    }
    
    /**
     * Gets a panel for children of this Dockable. Clients can do whatever
     * they like, except removing the content pane from its parent.
     * @return the representation of this dockable
     */
    public Container getContentPane(){
        return pane;
    }
    
    /**
     * Adds <code>component</code> to the content pane.
     * @param component the new child
     */
    public void add( Component component ){
        getContentPane().add( component );
    }
    
    /**
     * Adds <code>component</code> to the content pane.
     * @param component the new child
     * @param constraints information for th {@link LayoutManager}
     */
    public void add( Component component, Object constraints ){
        getContentPane().add( component, constraints );
    }
    
    /**
     * Removes <code>component</code> from the content pane.
     * @param component the child to remove
     */
    public void remove( Component component ){
        getContentPane().remove( component );
    }
    
    /**
     * Sets the layout of the content pane. The layout is normaly a
     * {@link FlowLayout}, except the constructor has added a component to the
     * layout. In that case, the layout is a {@link GridLayout}.
     * @param layout the new layout of the content pane
     */
    public void setLayout( LayoutManager layout ){
        getContentPane().setLayout( layout );
    }
}
