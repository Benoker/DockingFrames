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

package bibliothek.gui.dock.dockable;

import java.awt.Component;
import java.awt.Point;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import javax.swing.Icon;
import javax.swing.event.MouseInputListener;

import bibliothek.gui.DockController;
import bibliothek.gui.DockStation;
import bibliothek.gui.DockTheme;
import bibliothek.gui.Dockable;
import bibliothek.gui.dock.DockElement;
import bibliothek.gui.dock.action.DockAction;
import bibliothek.gui.dock.action.DockActionSource;
import bibliothek.gui.dock.action.HierarchyDockActionSource;
import bibliothek.gui.dock.component.DockComponentConfiguration;
import bibliothek.gui.dock.component.DockComponentRootHandler;
import bibliothek.gui.dock.displayer.DisplayerRequest;
import bibliothek.gui.dock.displayer.DockableDisplayerHints;
import bibliothek.gui.dock.event.DockHierarchyListener;
import bibliothek.gui.dock.event.DockableListener;
import bibliothek.gui.dock.event.KeyboardListener;
import bibliothek.gui.dock.title.DockTitle;
import bibliothek.gui.dock.title.DockTitleRequest;
import bibliothek.gui.dock.util.DockProperties;
import bibliothek.gui.dock.util.PropertyKey;
import bibliothek.gui.dock.util.PropertyValue;
import bibliothek.gui.dock.util.icon.DockIcon;
import bibliothek.util.Todo;
import bibliothek.util.Todo.Compatibility;
import bibliothek.util.Todo.Priority;
import bibliothek.util.Todo.Version;

/**
 * An implementation of {@link Dockable} which deals with the simple things.<br>
 * Some of the capabilities of an AbstractDockable are:
 * <ul>
 *  <li>add or remove a {@link DockableListener}, and fire an event</li>
 *  <li>set the parent and the {@link DockController controller}</li>
 *  <li>set the title and the icon</li> 
 *  <li>store a list of {@link DockAction DockActions}</li>
 * </ul>
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
    /** a listener for monitoring the location of this dockable */
    private DockableStateListenerManager dockableStateListeners;
    
    /** the list of {@link KeyListener}s of this dockable */
    private List<KeyListener> keyListeners = new ArrayList<KeyListener>();
    /** the listener dispatching events to {@link #keyListeners} */
    private KeyboardListener keyboardListener;
    
    /** the title of this dockable */
    private PropertyValue<String> titleText;
    /** the icon of this dockable */
    private DockIcon titleIcon;
    /** the current value of {@link #titleIcon} */
    private Icon currentTitleIcon;
    /** how to react if a <code>null</code> {@link Icon} is set as title icon */
    private IconHandling titleIconHandling = IconHandling.KEEP_NULL_ICON;
    
    /** the tooltip of this dockable */
    private PropertyValue<String> titleToolTip;
    
    /** the DockTitles which are bound to this dockable */
    private List<DockTitle> titles = new LinkedList<DockTitle>();
    
    private DockableDisplayerHints hints;
    
    /** Informs the client about all the {@link Component}s that are present on this {@link Dockable} */
    private DockComponentRootHandler rootHandler;
    
    /**
     * A modifiable list of {@link DockAction} which can be triggered and 
     * will affect this dockable.
     */
    private DockActionSource source;
    
    /** an unmodifiable list of actions used for this dockable */
    private HierarchyDockActionSource globalSource;
    
    /**
     * Creates a new dockable.
     * @param titleText the key of the title, used to read in {@link DockProperties}
     * @param titleTooltip the key of the tooltip, used to read in {@link DockProperties}
     */
    protected AbstractDockable( PropertyKey<String> titleText, PropertyKey<String> titleTooltip ){
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
    	
    	this.titleToolTip = new PropertyValue<String>( titleTooltip ){
    	    @Override
    	    protected void valueChanged( String oldValue, String newValue ) {
    	        fireTitleTooltipChanged( oldValue, newValue );
    	    }
    	};
    	
    	dockableStateListeners = new DockableStateListenerManager( this );
    	hierarchyObserver = new DockHierarchyObserver( this );
    	globalSource = new HierarchyDockActionSource( this );
    	globalSource.bind();
    }
    
    /**
     * Gets the {@link DockComponentRootHandler} which is responsible for keeping track of all the {@link Component}s of this
     * dockable.
     * @return the root handler, not <code>null</code>
     */
    protected DockComponentRootHandler getRootHandler(){
    	if( rootHandler == null ){
    		rootHandler = createRootHandler();
    		rootHandler.addRoot( getComponent() );
    	}
    	return rootHandler;
    }
    
    /**
     * Creates the {@link DockComponentRootHandler} which configures the {@link Component}s of this dockable.
     * @return the new handler, not <code>null</code>
     */
    protected abstract DockComponentRootHandler createRootHandler();
    
    /**
     * Creates the {@link DockIcon} which represents this {@link Dockable} or this {@link DockStation}. The
     * icon must call {@link #fireTitleIconChanged(Icon, Icon)} if the icon changes.
     * @return the default icon for this element
     */
    protected abstract DockIcon createTitleIcon();
    
    private DockIcon titleIcon(){
    	if( titleIcon == null ){
    		titleIcon = createTitleIcon();
    		titleIcon.setController( getController() );
    	}
    	return titleIcon;
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
    	getRootHandler().setController( null );
    	
    	if( this.controller != null ){
    		if( keyboardListener != null ){
    			this.controller.getKeyboardController().removeListener( keyboardListener );
    			keyboardListener = null;
    		}
    	}
    	
        this.controller = controller;
        titleIcon().setController( controller );
        titleText.setProperties( controller );
        hierarchyObserver.controllerChanged( controller );
        
        if( !keyListeners.isEmpty() ){
        	registerKeyboardListener();
        }
        
        getRootHandler().setController( controller );
    }

    public DockController getController() {
        return controller;
    }
    
    public void setComponentConfiguration( DockComponentConfiguration configuration ) {
    	getRootHandler().setConfiguration( configuration );
    }
    
    public DockComponentConfiguration getComponentConfiguration() {
    	return getRootHandler().getConfiguration();
    }
    
    public boolean isDockableShowing(){
	    return isDockableVisible();
    }
    
    @Deprecated
    @Todo( compatibility=Compatibility.BREAK_MAJOR, priority=Priority.ENHANCEMENT, target=Version.VERSION_1_1_3, description="remove this method" )
    public boolean isDockableVisible(){
    	DockController controller = getController();
    	if( controller == null ){
    		return false;
    	}
    	DockStation parent = getDockParent();
    	if( parent != null ){
    		return parent.isVisible( this );
    	}
    	return false;
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
    
    public void addDockableStateListener( DockableStateListener listener ){
    	dockableStateListeners.addListener( listener );
    }
    
    public void removeDockableStateListener( DockableStateListener listener ){
    	dockableStateListeners.removeListener( listener );
    }
    
    /**
     * Gets the manager which is responsible for handling {@link DockableStateListener}s.
     * @return the manager, not <code>null</code>
     */
    protected DockableStateListenerManager getDockableStateListeners(){
		return dockableStateListeners;
	}
    
    /**
     * Access to the {@link DockableStateListenerManager} which can be used to fire {@link DockableStateEvent}s. This method
     * is intended to be used by subclasses that implement {@link DockStation}.
     * @return the listeners
     */
    protected DockableStateListenerManager getDockElementObserver(){
		return dockableStateListeners;
	}
    
    public void addMouseInputListener( MouseInputListener listener ) {
        getComponent().addMouseListener( listener );
        getComponent().addMouseMotionListener( listener );
    }
    
    public void removeMouseInputListener( MouseInputListener listener ) {
        getComponent().removeMouseListener( listener );
        getComponent().removeMouseMotionListener( listener );
    }
    
    /**
     * Adds a {@link KeyListener} to this {@link Dockable}. The listener
     * will be informed about any un-consumed {@link KeyEvent} that is
     * related to this {@link Dockable}, e.g. an event that is dispatched
     * on a {@link DockTitle}. 
     * @param listener the new listener
     */
    public void addKeyListener( KeyListener listener ){
    	keyListeners.add( listener );
    	registerKeyboardListener();
    }
    
    /**
     * Removes <code>listener</code> from this element.
     * @param listener the listener to remove
     */
    public void removeKeyListener( KeyListener listener ){
    	keyListeners.remove( listener );
    	if( keyboardListener != null && controller != null ){
    		controller.getKeyboardController().removeListener( keyboardListener );
    		keyboardListener = null;
    	}
    }
    
    private KeyListener[] getKeyListeners(){
    	return keyListeners.toArray( new KeyListener[ keyListeners.size() ] );
    }
    
    private void registerKeyboardListener(){
    	if( keyboardListener == null && controller != null ){
    		keyboardListener = new KeyboardListener() {
				public DockElement getTreeLocation(){
					return AbstractDockable.this;
				}
				
				public boolean keyTyped( DockElement element, KeyEvent event ){
					if( element == AbstractDockable.this ){
						for( KeyListener listener : getKeyListeners() ){
							listener.keyTyped( event );
						}
						return event.isConsumed();
					}
					else{
						return false;
					}
				}
				
				public boolean keyReleased( DockElement element, KeyEvent event ){
					if( element == AbstractDockable.this ){
						for( KeyListener listener : getKeyListeners() ){
							listener.keyReleased( event );
						}
						return event.isConsumed();
					}
					else{
						return false;
					}
				}
				
				public boolean keyPressed( DockElement element, KeyEvent event ){
					if( element == AbstractDockable.this ){
						for( KeyListener listener : getKeyListeners() ){
							listener.keyPressed( event );
						}
						return event.isConsumed();
					}
					else{
						return false;
					}
				}
			};
			controller.getKeyboardController().addListener( keyboardListener );
    	}
    }
    
    public DockElement getElement() {
        return this;
    }
    
    public boolean isUsedAsTitle() {
        return false;
    }
    
    public boolean shouldFocus(){
    	return true;
    }
    
    public boolean shouldTransfersFocus(){
	    return false;
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
        return currentTitleIcon;
    }
    
    /**
     * Sets the tooltip that will be shown on any title of this dockable.
     * @param titleToolTip the new tooltip, can be <code>null</code>
     */
    public void setTitleToolTip( String titleToolTip ){
        this.titleToolTip.setValue( titleToolTip );
    }
    
    public String getTitleToolTip() {
        return titleToolTip.getValue();
    }
    
    public Point getPopupLocation( Point click, boolean popupTrigger ) {
        if( popupTrigger )
            return click;
        else
            return null;
    }

    /**
     * Sets the behavior of how the title icon is handled, whether it is replaced by the default
     * icon if <code>null</code> or simply not shown.<br>
     * Calling this method does not have any effect, rather the behavior of {@link #setTitleIcon(Icon)}
     * is changed.
     * @param titleIconHandling the new behavior, not <code>null</code>
     */
    public void setTitleIconHandling( IconHandling titleIconHandling ){
    	if( titleIconHandling == null ){
    		throw new IllegalArgumentException( "titleIconHandling must not be null" );
    	}
		this.titleIconHandling = titleIconHandling;
	}
    
    /**
     * Tells how a <code>null</code> title icon is handled.
     * @return the behavior
     * @see #setTitleIconHandling(IconHandling)
     */
    public IconHandling getTitleIconHandling(){
		return titleIconHandling;
	}
    
    /**
     * Sets the icon of this dockable. All dockableListeners are informed about 
     * the change.<br>
     * If <code>titleIcon</code> is <code>null</code>, then the exact behavior of this method
     * depends on the result of {@link #getTitleIconHandling()}. The method may either replace
     * the <code>null</code> {@link Icon} by the default icon, or simply not show any icon.
     * @param titleIcon the new icon, may be <code>null</code>
     */
    public void setTitleIcon( Icon titleIcon ) {
    	switch( getTitleIconHandling() ){
    		case KEEP_NULL_ICON:
    			titleIcon().setValue( titleIcon, true );
    			break;
    		case REPLACE_NULL_ICON:
    			titleIcon().setValue( titleIcon, false );
    			break;
    		default:
    			throw new IllegalStateException( "unknown behavior: " + titleIconHandling );
    	}
        
    }
    
    /**
     * Resets the icon of this {@link Dockable}, the default icon is shown again.
     */
    public void resetTitleIcon(){
    	titleIcon().setValue( null );
    }
    
    /**
     * The default behavior of this method is to do nothing.
     */
    public void requestDockTitle( DockTitleRequest request ){
	    // ignore	
    }
    
    /**
     * The default behavior of this method is to do nothing.
     */
    public void requestDisplayer( DisplayerRequest request ){
	    // ignore	
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
     * Sets the action-source of this {@link Dockable}. Other elements which
     * used {@link #getGlobalActionOffers()} will be informed about this change.
     * @param source The new source, may be <code>null</code>
     */
    public void setActionOffers( DockActionSource source ){
        this.source = source;
        globalSource.update();
    }
    
    /**
     * Calls the {@link DockableListener#titleTextChanged(Dockable, String, String) titleTextChanged}
     * method of all registered {@link DockableListener}s.
     * @param oldTitle the old title
     * @param newTitle the new title
     */
    protected void fireTitleTextChanged( String oldTitle, String newTitle ){
        for( DockableListener listener : dockableListeners.toArray( new DockableListener[ dockableListeners.size()] ))
            listener.titleTextChanged( this, oldTitle, newTitle );
    }
    
    /**
     * Called the {@link DockableListener#titleToolTipChanged(Dockable, String, String) titleTooltipChanged}
     * method of all registered {@link DockableListener}s.
     * @param oldTooltip the old value
     * @param newTooltip the new value
     */
    protected void fireTitleTooltipChanged( String oldTooltip, String newTooltip ){
        for( DockableListener listener : dockableListeners.toArray( new DockableListener[ dockableListeners.size()] ))
            listener.titleToolTipChanged( this, oldTooltip, newTooltip );
    }

    /**
     * Calls the {@link DockableListener#titleIconChanged(Dockable, Icon, Icon) titleIconChanged}
     * method of all registered {@link DockableListener}.
     * @param oldIcon the old icon
     * @param newIcon the new icon
     */
    protected void fireTitleIconChanged( Icon oldIcon, Icon newIcon ){
    	currentTitleIcon = newIcon;
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
    
    /**
     * Informs all {@link DockableListener}s that <code>title</code> is no longer
     * considered to be a good title and should be exchanged.
     * @param title a title, can be <code>null</code>
     */
    protected void fireTitleExchanged( DockTitle title ){
        for( DockableListener listener : dockableListeners.toArray( new DockableListener[ dockableListeners.size()] ))
            listener.titleExchanged( this, title );
    }
    
    /**
     * Informs all {@link DockableListener}s that all bound titles and the
     * <code>null</code> title are no longer considered good titles and
     * should be replaced
     */
    protected void fireTitleExchanged(){
        DockTitle[] bound = listBoundTitles();
        for( DockTitle title : bound )
            fireTitleExchanged( title );
        
        fireTitleExchanged( null );
    }
    
    public void configureDisplayerHints( DockableDisplayerHints hints ) {
        this.hints = hints;
    }
    
    /**
     * Gets the last {@link DockableDisplayerHints} that were given to
     * {@link #configureDisplayerHints(DockableDisplayerHints)}.
     * @return the current configurable hints, can be <code>null</code>
     */
    protected DockableDisplayerHints getConfigurableDisplayerHints() {
        return hints;
    }
}
