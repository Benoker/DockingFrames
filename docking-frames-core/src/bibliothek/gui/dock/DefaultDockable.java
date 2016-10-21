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

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Container;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.LayoutManager;
import java.lang.ref.WeakReference;

import javax.swing.Icon;
import javax.swing.LayoutFocusTraversalPolicy;

import bibliothek.gui.DockController;
import bibliothek.gui.DockStation;
import bibliothek.gui.Dockable;
import bibliothek.gui.dock.component.DockComponentRootHandler;
import bibliothek.gui.dock.dockable.AbstractDockable;
import bibliothek.gui.dock.dockable.DefaultDockableFactory;
import bibliothek.gui.dock.dockable.DockableBackgroundComponent;
import bibliothek.gui.dock.dockable.DockableIcon;
import bibliothek.gui.dock.dockable.IconHandling;
import bibliothek.gui.dock.themes.ThemeManager;
import bibliothek.gui.dock.util.BackgroundAlgorithm;
import bibliothek.gui.dock.util.BackgroundPanel;
import bibliothek.gui.dock.util.ConfiguredBackgroundPanel;
import bibliothek.gui.dock.util.PropertyKey;
import bibliothek.gui.dock.util.Transparency;
import bibliothek.gui.dock.util.icon.DockIcon;

/**
 * A {@link Dockable} which consists only of one {@link Component} called
 * "content pane". It's possible to add or remove components from the
 * content pane at any time.
 * @author Benjamin Sigg
 */
public class DefaultDockable extends AbstractDockable {
    /** the content pane */
    private BackgroundPanel pane = new ConfiguredBackgroundPanel( new BorderLayout(), Transparency.SOLID );
    
    /** the id used to identify the factory of this dockable */
    private String factoryId = DefaultDockableFactory.ID;
    
    /** the background of this dockable */
    private Background background = new Background();
    
    /** the component that was set by the client */
    private WeakReference<Component> clientComponent;
    
    /**
     * Constructs a new DefaultDockable
     */
    public DefaultDockable(){
        this(  null, null, null, IconHandling.REPLACE_NULL_ICON );
    }

    /**
     * Constructs a new DefaultDockable and sets the icon.
     * @param icon the icon, to be shown at various places
     */
    public DefaultDockable( Icon icon ){
        this( null, null, icon, IconHandling.KEEP_NULL_ICON );
    }
    
    /**
     * Constructs a new DefaultDockable and sets the title.
     * @param title the title, to be shown at various places
     */
    public DefaultDockable( String title ){
        this( null, title, null, IconHandling.REPLACE_NULL_ICON );
    }
    
    /**
     * Constructs a new DefaultDockable and places one component onto the
     * content pane.
     * @param component the only child of the content pane 
     */
    public DefaultDockable( Component component ){
        this( component, null, null, IconHandling.REPLACE_NULL_ICON );
    }

    /**
     * Constructs a new DefaultDockable, sets an icon and places one
     * component.
     * @param component the only child of the content pane
     * @param icon the icon, to be shown at various places
     */
    public DefaultDockable( Component component, Icon icon ){
        this( component, null, icon, IconHandling.KEEP_NULL_ICON );
    }
    
    /**
     * Constructs a new DefaultDockable, sets the title and places one
     * component.
     * @param component the only child of the content pane
     * @param title the title, to be shown at various places
     */
    public DefaultDockable( Component component, String title ){
        this( component, title, null, IconHandling.REPLACE_NULL_ICON );
    }

    /**
     * Constructs a new DefaultDockable, sets the icon and the title, and
     * places a component.
     * @param component the only child of the content pane
     * @param title the title, to be shown at various places
     * @param icon the icon, to be shown at various places
     */
    public DefaultDockable( Component component, String title, Icon icon ){
    	this( component, title, icon, IconHandling.KEEP_NULL_ICON );
    }
    
    /**
     * Constructs a new DefaultDockable, sets the icon and the title, and
     * places a component.
     * @param component the only child of the content pane
     * @param title the title, to be shown at various places
     * @param icon the icon, to be shown at various places
     * @param handling how to understand the <code>icon</code> parameter
     */
    public DefaultDockable( Component component, String title, Icon icon, IconHandling handling ){
    	super( PropertyKey.DOCKABLE_TITLE, PropertyKey.DOCKABLE_TOOLTIP );
    	
    	pane.setFocusable( false );
    	pane.setFocusTraversalPolicyProvider( true );
    	pane.setFocusTraversalPolicy( new LayoutFocusTraversalPolicy() );
    	pane.setBackground( background );
    	
        if( component != null ){
        	clientComponent = new WeakReference<Component>( component );
            getContentPane().setLayout( new GridLayout( 1, 1 ));
            getContentPane().add( component );
        }
        
        setTitleIconHandling( handling );
        setTitleIcon( icon );
        setTitleText( title );
    }
    
    @Override
    protected DockIcon createTitleIcon(){
	    return new DockableIcon( "dockable.default", this ){
			protected void changed( Icon oldValue, Icon newValue ){
				fireTitleIconChanged( oldValue, newValue );	
			}
		};
    }
    
    protected DockComponentRootHandler createRootHandler() {
	    return new DockComponentRootHandler( this ) {
			protected TraverseResult shouldTraverse( Component component ) {
				if( component == getContentPane() ){
					return TraverseResult.EXCLUDE_CHILDREN;
				}
				else{
					return TraverseResult.INCLUDE_CHILDREN;
				}
			}
		};
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
     * Gets the number of {@link Component}s on this dockable, this is equivalent of calling
     * <code>getContentPane().getComponentCount()</code>.
     * @return the number of components
     * @see #getContentPane()
     * @see Container#getComponentCount()
     */
    public int getComponentCount(){
    	return getContentPane().getComponentCount();
    }

    /**
     * Gets the index'th child of this {@link Dockable}, this is equivalent of calling
     * <code>getContentPane().getComponent( index )</code>.
     * @param index the index of the child
     * @return the component
     * @see #getContentPane()
     * @see Container#getComponent(int)
     */
    public Component getComponent( int index ){
    	return getContentPane().getComponent( index );
    }
    
    /**
     * Gets the {@link Component} which was given to this {@link DefaultDockable} through the constructor. If the client
     * ever removes and the client component from the {@link #getContentPane() content-pane}, and then adds the component
     * again, then the result of this method gets unspecified.<br> 
     * Please note: the implementation of how the client component is stored does not prevent the garbage collector from
     * deleting the client component.
     * @return the component that was given to this dockable through the constructor or <code>null</code> if that component
     * was removed from the {@link #getContentPane() content-pane} 
     */
    public Component getClientComponent(){
    	if( clientComponent == null ){
    		return null;
    	}
    	Component child = clientComponent.get();
    	if( child == null ){
    		clientComponent = null;
    		return null;
    	}
    	if( child.getParent() != getContentPane() ){
    		clientComponent = null;
    		return null;
    	}
    	return child;
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
     * Sets the layout of the content pane. The layout is normally a
     * {@link FlowLayout}, except the constructor has added a component to the
     * layout. In that case, the layout is a {@link GridLayout}.
     * @param layout the new layout of the content pane
     */
    public void setLayout( LayoutManager layout ){
        getContentPane().setLayout( layout );
    }
    
    @Override
    public void setController( DockController controller ){
    	super.setController( controller );
    	background.setController( controller );
    }
    
    /**
     * A representation of the background of this {@link Dockable}.
     * @author Benjamin Sigg
     *
     */
    private class Background extends BackgroundAlgorithm implements DockableBackgroundComponent{
    	public Background(){
    		super( DockableBackgroundComponent.KIND, ThemeManager.BACKGROUND_PAINT + ".dockable" );
    	}
    	
    	public Component getComponent(){
    		return getDockable().getComponent();
    	}
    	
    	public Dockable getDockable(){
    		return DefaultDockable.this;
    	}
    }
}
