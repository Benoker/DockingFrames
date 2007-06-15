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

package bibliothek.gui.dock;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import javax.swing.Icon;
import javax.swing.event.MouseInputListener;

import bibliothek.gui.DockController;
import bibliothek.gui.DockStation;
import bibliothek.gui.DockTheme;
import bibliothek.gui.Dockable;
import bibliothek.gui.dock.action.DockAction;
import bibliothek.gui.dock.action.DockActionSource;
import bibliothek.gui.dock.event.DockableListener;
import bibliothek.gui.dock.title.DockTitle;
import bibliothek.gui.dock.title.DockTitleVersion;
import bibliothek.gui.dock.util.DockProperties;
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
    
    /** a list of listeners which will be informed when some properties changes */
    private List<DockableListener> listeners = new ArrayList<DockableListener>();
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
    }
    
    public void setDockParent( DockStation station ) {
        parent = station;
    }

    public DockStation getDockParent() {
        return parent;
    }
    
    public Dockable asDockable() {
        return this;
    }

    public void setController( DockController controller ) {
        this.controller = controller;
        if( controller == null ){
        	titleIcon.setProperties( null );
        	titleText.setProperties( null );
        }
        else{
        	titleIcon.setProperties( controller.getProperties() );
        	titleText.setProperties( controller.getProperties() );
        }
    }

    public DockController getController() {
        return controller;
    }

    public void addDockableListener( DockableListener listener ) {
        listeners.add( listener );
    }

    public void removeDockableListener( DockableListener listener ) {
        listeners.remove( listener );
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
     * Sets the title of this dockable. All listeners are informed about
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
     * Sets the icon of this dockable. All listeners are informed about 
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
    		throw new IllegalArgumentException( "Title is already binded" );
    	titles.add( title );
    	fireTitleBinded( title );
    }

    public void unbind( DockTitle title ) {
    	if( !titles.contains( title ))
    		throw new IllegalArgumentException( "Title is unknown" );
    	titles.remove( title );
    	fireTitleUnbinded( title );
    }
    
    public DockTitle[] listBindedTitles(){
    	return titles.toArray( new DockTitle[ titles.size() ] );
    }

    public DockActionSource getActionOffers() {
        return source;
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
        for( DockableListener listener : listeners.toArray( new DockableListener[ listeners.size()] ))
            listener.titleTextChanged( this, oldTitle, newTitle );
    }

    /**
     * Calls the {@link DockableListener#titleIconChanged(Dockable, Icon, Icon) titleIconChanged}
     * method of all registered {@link DockableListener}.
     * @param oldIcon the old icon
     * @param newIcon the new icon
     */
    protected void fireTitleIconChanged( Icon oldIcon, Icon newIcon ){
        for( DockableListener listener : listeners.toArray( new DockableListener[ listeners.size()] ))
            listener.titleIconChanged( this, oldIcon, newIcon );
    }
    
    /**
     * Informs all listeners that <code>title</code> was binded to this dockable.
     * @param title the title which was binded
     */
    protected void fireTitleBinded( DockTitle title ){
        for( DockableListener listener : listeners.toArray( new DockableListener[ listeners.size()] ))
            listener.titleBinded( this, title );
    }
    
    /**
     * Informs all listeners that <code>title</code> was unbinded from this dockable.
     * @param title the title which was unbinded
     */
    protected void fireTitleUnbinded( DockTitle title ){
        for( DockableListener listener : listeners.toArray( new DockableListener[ listeners.size()] ))
            listener.titleUnbinded( this, title );
    }
}
