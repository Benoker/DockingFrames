/**
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

package bibliothek.gui.dock.dockable;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import javax.swing.Icon;
import javax.swing.event.MouseInputListener;

import bibliothek.gui.DockController;
import bibliothek.gui.DockStation;
import bibliothek.gui.Dockable;
import bibliothek.gui.dock.action.DockActionSource;
import bibliothek.gui.dock.action.HierarchyDockActionSource;
import bibliothek.gui.dock.event.DockHierarchyListener;
import bibliothek.gui.dock.event.DockableListener;
import bibliothek.gui.dock.title.DockTitle;
import bibliothek.gui.dock.title.DockTitleVersion;
import bibliothek.gui.dock.util.PropertyKey;
import bibliothek.gui.dock.util.PropertyValue;

/**
 * An implementation of {@link Dockable} which deals with the simple things.<br>
 * Some of the capabilities of an AbstractDockable are:
 * <li>
 *  <ul>add or remove a {@link DockableListener}, and fire an event</ul>
 *  <ul>set the parent and the {@link DockController controller}</ul>
 *  <ul>set the title and the icon</ul> 
 *  <ul>store a list of {@link DockAction DockActions}</ul>
 * </li>
 * @author Benjamin Sigg
 */
public abstract class AbstractDockable implements Dockable {
    /** the parent */
    private DockStation parent;
    /** the controller used to get information like the {@link DockTheme} */
    private DockController controller;
    
    /** a list of dockableListeners which will be informed when some properties changes */
    private List<DockableListener> dockableListeners = new ArrayList<DockableListener>();
    /** a listener to the hierarchy of the parent */
    private DockHierarchyObserver hierarchyObserver;
    
    /** the title of this dockable */
    private PropertyValue<String> titleText;
    /** the icon of this dockable */
    private PropertyValue<Icon> titleIcon;
    
    /** the DockTitles which are bound to this dockable */
    private List<DockTitle> titles = new LinkedList<DockTitle>();
    
    /**
     * A modifiable list of {@link DockAction} which can be triggered and 
     * will affect this dockable.
     */
    private DockActionSource source;
    
    /** an unmodifiable list of actions used for this dockable */
    private HierarchyDockActionSource globalSource;
    
    /**
     * Creates a new dockable.
     * @param titleIcon the key of the icon, used to read in {@link DockProperties}
     * @param titleText the key of the title, used to read in {@link DockProperties}
     */
    protected AbstractDockable( PropertyKey<Icon> titleIcon, PropertyKey<String> titleText ){
    	this.titleIcon = new PropertyValue<Icon>( titleIcon ){
    		@Override
    		protected void valueChanged( Icon oldValue, Icon newValue ){
    			fireTitleIconChanged( oldValue, newValue );
    		}
    	};
    	
    	this.titleText = new PropertyValue<String>( titleText ){
    		@Override
    		protected void valueChanged( String oldValue, String newValue ){
    			if( oldValue == null )
    				oldValue = "";
    			
    			if( newValue == null )
    				newValue = "";
    			
    			fireTitleTextChanged( oldValue, newValue );
    		}
    	};
    	
    	hierarchyObserver = new DockHierarchyObserver( this );
    	globalSource = new HierarchyDockActionSource( this );
    	globalSource.bind();
    }
    
    public void setDockParent( DockStation station ) {
    	if( this.parent != station ){
    		parent = station;
    		hierarchyObserver.update();
    	}
    }

    public DockStation getDockParent() {
        return parent;
    }
    
    public Dockable asDockable() {
        return this;
    }

    public void setController( DockController controller ) {
        this.controller = controller;
        titleIcon.setProperties( controller );
        titleText.setProperties( controller );
        hierarchyObserver.controllerChanged( controller );
    }

    public DockController getController() {
        return controller;
    }

    public void addDockableListener( DockableListener listener ) {
        dockableListeners.add( listener );
    }

    public void removeDockableListener( DockableListener listener ) {
        dockableListeners.remove( listener );
    }
    
    public void addDockHierarchyListener( DockHierarchyListener listener ){
    	hierarchyObserver.addDockHierarchyListener( listener );
    }
    
    public void removeDockHierarchyListener( DockHierarchyListener listener ){
    	hierarchyObserver.removeDockHierarchyListener( listener );
    }
    
    public void addMouseInputListener( MouseInputListener listener ) {
        getComponent().addMouseListener( listener );
        getComponent().addMouseMotionListener( listener );
    }
    
    public void removeMouseInputListener( MouseInputListener listener ) {
        getComponent().removeMouseListener( listener );
        getComponent().removeMouseMotionListener( listener );
    }

    public boolean accept( DockStation station ) {
        return true;
    }
    
    public boolean accept( DockStation base, Dockable neighbour ) {
        return true;
    }

    public String getTitleText() {
        String text = titleText.getValue();
        if( text == null )
        	return "";
        else
        	return text;
    }

    /**
     * Sets the title of this dockable. All dockableListeners are informed about
     * the change.
     * @param titleText the title, <code>null</code> is replaced by the
     * empty string
     */
    public void setTitleText( String titleText ) {
    	this.titleText.setValue( titleText );
    }
    
    public Icon getTitleIcon() {
        return titleIcon.getValue();
    }

    /**
     * Sets the icon of this dockable. All dockableListeners are informed about 
     * the change.
     * @param titleIcon the new icon, may be <code>null</code>
     */
    public void setTitleIcon( Icon titleIcon ) {
        this.titleIcon.setValue( titleIcon );
    }
    
    public DockTitle getDockTitle( DockTitleVersion version ) {
        return version.createDockable( this );
    }

    public void bind( DockTitle title ) {
    	if( titles.contains( title ))
    		throw new IllegalArgumentException( "Title is already bound" );
    	titles.add( title );
    	fireTitleBound( title );
    }

    public void unbind( DockTitle title ) {
    	if( !titles.contains( title ))
    		throw new IllegalArgumentException( "Title is unknown" );
    	titles.remove( title );
    	fireTitleUnbound( title );
    }
    
    public DockTitle[] listBoundTitles(){
    	return titles.toArray( new DockTitle[ titles.size() ] );
    }

    public DockActionSource getLocalActionOffers() {
        return source;
    }
    
    public DockActionSource getGlobalActionOffers(){
    	return globalSource;
    }

    /** 
     * Sets the action-source of this {@link Dockable}. If some other
     * parties have already called the {@link #getActionOffers()}-method,
     * they will not be informed about the change in any way.
     * @param source The new source, may be <code>null</code>
     */
    public void setActionOffers( DockActionSource source ){
        this.source = source;
    }
    
    /**
     * Calls the {@link DockableListener#titleTextChanged(Dockable, String, String) titleTextChanged}
     * method of all registered {@link DockableListener}.
     * @param oldTitle the old title
     * @param newTitle the new title
     */
    protected void fireTitleTextChanged( String oldTitle, String newTitle ){
        for( DockableListener listener : dockableListeners.toArray( new DockableListener[ dockableListeners.size()] ))
            listener.titleTextChanged( this, oldTitle, newTitle );
    }

    /**
     * Calls the {@link DockableListener#titleIconChanged(Dockable, Icon, Icon) titleIconChanged}
     * method of all registered {@link DockableListener}.
     * @param oldIcon the old icon
     * @param newIcon the new icon
     */
    protected void fireTitleIconChanged( Icon oldIcon, Icon newIcon ){
        for( DockableListener listener : dockableListeners.toArray( new DockableListener[ dockableListeners.size()] ))
            listener.titleIconChanged( this, oldIcon, newIcon );
    }
    
    /**
     * Informs all dockableListeners that <code>title</code> was bound to this dockable.
     * @param title the title which was bound
     */
    protected void fireTitleBound( DockTitle title ){
        for( DockableListener listener : dockableListeners.toArray( new DockableListener[ dockableListeners.size()] ))
            listener.titleBound( this, title );
    }
    
    /**
     * Informs all dockableListeners that <code>title</code> was unbound from this dockable.
     * @param title the title which was unbound
     */
    protected void fireTitleUnbound( DockTitle title ){
        for( DockableListener listener : dockableListeners.toArray( new DockableListener[ dockableListeners.size()] ))
            listener.titleUnbound( this, title );
    }
}
