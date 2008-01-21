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

package bibliothek.gui;

import java.awt.Component;
import java.awt.KeyboardFocusManager;
import java.util.*;

import javax.swing.FocusManager;
import javax.swing.SwingUtilities;

import bibliothek.gui.dock.DockElement;
import bibliothek.gui.dock.accept.DockAcceptance;
import bibliothek.gui.dock.accept.MultiDockAcceptance;
import bibliothek.gui.dock.action.*;
import bibliothek.gui.dock.action.view.ActionViewConverter;
import bibliothek.gui.dock.control.*;
import bibliothek.gui.dock.event.*;
import bibliothek.gui.dock.title.DockTitle;
import bibliothek.gui.dock.title.DockTitleManager;
import bibliothek.gui.dock.util.DockProperties;
import bibliothek.gui.dock.util.DockUtilities;
import bibliothek.gui.dock.util.IconManager;

/**
 * A controller is needed to drag and drop {@link Dockable dockables} from
 * one {@link DockStation} to another station.<br>
 * In order to use a station, it must be {@link #add(DockStation) added}
 * to a controller. Stations which are children of other stations will be 
 * added automatically. Dockables can only be dragged and dropped from
 * stations with the same controller.<br>
 * Note: if a controller is no longer in use, the method {@link #kill()} should
 * be called to free some resources.
 * @author Benjamin Sigg
 */
public class DockController {
	/** the known dockables and DockStations */
	private DockRegister register;
	/** the known {@link Component}s in the realm of this controller */
	private ComponentHierarchyObserver componentHierarchyObserver;
	
	/** a manager handling drag and drop */
	private DockRelocator relocator;
	
	/** the controller that manages global double clicks */
	private DoubleClickController doubleClickController;
	
	/** the controller that manages global key-events */
	private KeyboardController keyboardController;
	
    /** the Dockable which has currently the focus, can be <code>null</code> */
    private Dockable focusedDockable = null;
    
    /** Listeners observing the focused {@link Dockable} */
    private List<DockableFocusListener> dockableFocusListeners = new ArrayList<DockableFocusListener>();
    /** Listeners observing the bound-state of {@link DockTitle}s */
    private List<DockTitleBindingListener> dockTitleBindingListeners = new ArrayList<DockTitleBindingListener>();
        
    /** <code>true</code> while the controller actively changes the focus */
    private boolean onFocusing = false;
    /** a special controller listening to AWT-events and changing the focused dockable */
    private MouseFocusObserver focusObserver;
    
    /** an observer of the bound {@link DockTitle}s */
    private DockTitleObserver dockTitleObserver = new DockTitleObserver();
    /** mapping tells which titles are currently active */
    private Map<DockTitle, Dockable> activeTitles = new HashMap<DockTitle, Dockable>();
    /** a source for {@link DockTitle} */
    private DockTitleManager dockTitles = new DockTitleManager( this );
    
    /** the set of icons used with this controller */
    private IconManager icons = new IconManager();
    
    /** A list of sources for a {@link DockActionSource} */
    private List<ActionOffer> actionOffers = new ArrayList<ActionOffer>();
    /** A  list of sources for {@link DockActionSource DockActionSources} */
    private List<ActionGuard> guards = new ArrayList<ActionGuard>();
    /** The default source for a {@link DockActionSource} */
    private ActionOffer defaultActionOffer;
    /** A converter used to transform {@link DockAction actions} into views */
    private ActionViewConverter actionViewConverter;
    
    /** behavior which dockable can be dropped over which station */
    private MultiDockAcceptance acceptance = new MultiDockAcceptance();
    /** tells which popups are to be shown */
    private ActionPopupSuppressor popupSuppressor = ActionPopupSuppressor.ALLOW_ALWAYS;
   
    /** remover of stations with none or one child */
    private SingleParentRemover remover;
    
    /** a theme describing the look of the stations */
    private DockTheme theme;
    /** a set of properties */
    private DockProperties properties = new DockProperties();
    
    /** the factory that creates new parts of this controller */
    private DockControllerFactory factory;
    
    /** tells which {@link Component} represents which {@link DockElement} */
    private Map<Component, DockElement> componentToDockElements = 
    	new HashMap<Component, DockElement>();
    
    /**
     * Creates a new controller. 
     */
    public DockController(){
    	this( new DefaultDockControllerFactory() );
    }
    
    /**
     * Creates a new controller but does not initiate the properties of this
     * controller if not wished. Clients should call the method 
     * {@link #initiate(DockControllerFactory,ControllerSetupCollection)} 
     * if they pass <code>null</code> to this constructor.
     * Otherwise the behavior of this controller is unspecified.
     * @param factory the factory creating elements of this controller or
     * <code>null</code> if {@link #initiate(DockControllerFactory,ControllerSetupCollection)} will be
     * called later
     */
    public DockController( DockControllerFactory factory ){
    	if( factory != null ){
    		initiate( factory, null );
    	}
    }
    
    /**
     * Initializes all properties of this controller. This method should be
     * called only once. This method can be called by a subclass if the 
     * subclass used {@link #DockController(DockControllerFactory)} with an argument <code>null</code>.
     * @param factory a factory used to create various sub-controls
     * @param setup the collection of {@link ControllerSetupListener}s that will be invoked
     * when setup is finished. If this parameter is set, then all {@link ControllerSetupListener}s
     * will be added to <code>setup</code>. If this parameter is <code>null</code>, then
     * a new collection will be created, and the event will be fired as soon as
     * this method is finished.
     */
    protected final void initiate( DockControllerFactory factory, ControllerSetupCollection setup ){
        if( this.factory != null )
            throw new IllegalStateException( "DockController already initialized" );
        
        if( factory == null )
            throw new IllegalArgumentException( "Factory must not be null" );
        
        final List<ControllerSetupListener> setupListeners = new LinkedList<ControllerSetupListener>();
        if( setup == null ){
            setup = new ControllerSetupCollection(){
                public void add( ControllerSetupListener listener ) {
                    if( listener == null )
                        throw new NullPointerException( "listener must not be null" );
                    setupListeners.add( listener );
                }
            };
        }
        
        this.factory = factory;
        
    	register = factory.createRegister( this, setup );
    	DockRegisterListener focus = factory.createFocusController( this, setup );
    	if( focus != null )
    		register.addDockRegisterListener( focus );
    	
    	DockRegisterListener popup = factory.createPopupController( this, setup );
    	if( popup != null )
    		register.addDockRegisterListener( popup );
    	
    	DockRegisterListener binder = factory.createActionBinder( this, setup );
    	if( binder != null )
    	    register.addDockRegisterListener( binder );
    	
		register.addDockRegisterListener( dockTitleObserver );
		addDockTitleBindingListener( dockTitleObserver );
		register.addDockRegisterListener( new DockableSelectionObserver() );
		
        relocator = factory.createRelocator( this, setup );
        
        defaultActionOffer = factory.createDefaultActionOffer( this, setup );
        focusObserver = factory.createMouseFocusObserver( this, setup );
        actionViewConverter = factory.createActionViewConverter( this, setup );
        doubleClickController = factory.createDoubleClickController( this, setup );
        keyboardController = factory.createKeyboardController( this, setup );
        
        DockUI.getDefaultDockUI().fillIcons( icons );
        
        setTheme( DockUI.getDefaultDockUI().getDefaultTheme().create() );
        
        relocator.addMode( DockRelocatorMode.SCREEN_ONLY );
        relocator.addMode( DockRelocatorMode.NO_COMBINATION );
        
        setSingleParentRemover( factory.createSingleParentRemover( this, setup ) );
        
        for( ControllerSetupListener listener : setupListeners )
            listener.done( this );
        
        /*
        relocator.install();
        register.install();
        focusObserver.install();
        doubleClickController.install();
        keyboardController.install();
        componentHierarchyObserver.install();
        */
    }
    
    /**
     * Removes listeners and frees resources. This method should be called
     * if this controller is no longer needed.
     */
    public void kill(){
	    focusObserver.kill();
	    register.kill();
	    keyboardController.kill();
    }
    
    /**
     * Gets the current focus-controller
     * @return the controller
     */
    public MouseFocusObserver getFocusObserver() {
        return focusObserver;
    }
    
    /**
     * Gets the set of {@link Dockable Dockables} and {@link DockStation DockStations}
     * known to this controller.
     * @return the set of elements
     */
    public DockRegister getRegister(){
		return register;
	}
    
    /**
     * Gets a list of all {@link Component}s which are used on the {@link Dockable}s
     * known to this controller.
     * @return the list of <code>Component</code>s.
     */
    public ComponentHierarchyObserver getComponentHierarchyObserver() {
        if( componentHierarchyObserver == null ){
            componentHierarchyObserver = new ComponentHierarchyObserver( this );
        }
        return componentHierarchyObserver;
    }
    
    /**
     * Gets the manager for handling drag and drop operations.
     * @return the manager
     */
    public DockRelocator getRelocator(){
		return relocator;
	}
    
    /**
     * Gets the manager for handling global double clicks of the mouse.
     * @return the manager
     */
    public DoubleClickController getDoubleClickController() {
        return doubleClickController;
    }
    
    /**
     * Gets the manager that handles all global KeyEvents.
     * @return the handler
     */
    public KeyboardController getKeyboardController(){
		return keyboardController;
	}
    
    /**
     * Gets the current {@link ActionViewConverter}.
     * @return the converter
     */
    public ActionViewConverter getActionViewConverter(){
    	return actionViewConverter;
    }
    
    /**
     * Gets the handler used to remove stations with only one or none
     * children.
     * @return the handler or <code>null</code>.
     * @see #setSingleParentRemover(SingleParentRemover)
     */
    public SingleParentRemover getSingleParentRemover() {
        return remover;
    }
    
    /**
     * Exchanges the handler that removes stations with only one or none children.
     * @param remover the new handler, can be <code>null</code> to disable the
     * feature.
     */
    public void setSingleParentRemover( SingleParentRemover remover ){
        if( this.remover != null ){
            this.remover.uninstall( this );
        }
        
        this.remover = remover;
        
        if( this.remover != null ){
            this.remover.install( this );
            this.remover.testAll( this );
        }
    }
    
    /**
     * Gets the behavior that tells which stations can have which children.
     * @return the behavior
     * @see #addAcceptance(DockAcceptance)
     * @see #removeAcceptance(DockAcceptance)
     */
    public MultiDockAcceptance getAcceptance() {
        return acceptance;
    }
    
    /**
     * Adds a rule that decides which station can have which children. 
     * The <code>acceptance</code> does not override the
     * <code>accept</code>-methods of {@link Dockable#accept(DockStation) Dockable}
     * and {@link DockStation#accept(Dockable) DockStation}.
     * @param acceptance the additional rule
     */
    public void addAcceptance( DockAcceptance acceptance ) {
        this.acceptance.add( acceptance );
    }
    
    /**
     * Removes a that decided which station could have which children.
     * @param acceptance the rule to remove
     */
    public void removeAcceptance( DockAcceptance acceptance ){
        this.acceptance.remove( acceptance );
    }

    /**
     * Gets the guard which decides, which popups should be allowed.
     * @return the guard
     * @see #setPopupSuppressor(ActionPopupSuppressor)
     */
    public ActionPopupSuppressor getPopupSuppressor() {
        return popupSuppressor;
    }
    
    /**
     * Sets the guard which decides, which popups with {@link DockAction DockActions}
     * are allowed to show up, and which popups will be suppressed.
     * @param popupSuppressor the guard
     */
    public void setPopupSuppressor( ActionPopupSuppressor popupSuppressor ) {
        if( popupSuppressor == null )
            throw new IllegalArgumentException( "suppressor must not be null" );
        this.popupSuppressor = popupSuppressor;
    }
    
    /**
     * Gets the factory for a {@link DockActionSource} which is used
     * if no other offer was {@link ActionOffer#interested(Dockable) interested}
     * in a {@link Dockable}. 
     * @return the default offer
     */
    public ActionOffer getDefaultActionOffer() {
        return defaultActionOffer;
    }
    
    /**
     * Sets the factory for a {@link DockActionSource} which is used
     * if no other offer was {@link ActionOffer#interested(Dockable) interested}
     * in a {@link Dockable}. 
     * @param defaultActionOffer the offer, not <code>null</code>
     */
    public void setDefaultActionOffer( ActionOffer defaultActionOffer ) {
        if( defaultActionOffer == null )
            throw new IllegalArgumentException();
                
        this.defaultActionOffer = defaultActionOffer;
    }
    
    /**
     * Adds a factory for a {@link DockActionSource}. The factory will
     * create a source if it is the first offer which is
     * {@link ActionOffer#interested(Dockable) interested} in a {@link Dockable}.
     * @param offer the algorithm
     */
    public void addActionOffer( ActionOffer offer ){
        if( offer == null )
            throw new IllegalArgumentException();
        actionOffers.add( offer );
    }
    
    /**
     * Removes an earlier added offer.
     * @param offer the factory to remove
     */
    public void removeActionOffer( ActionOffer offer ){
        actionOffers.remove( offer );
    }
    
    /**
     * Sets the theme of this controller. This method ensures that all
     * registered stations know also the new theme.
     * @param theme the new theme
     */
    public void setTheme(DockTheme theme) {
    	if( theme == null )
    		throw new IllegalArgumentException( "Theme must not be null" );
    	
    	if( this.theme != theme ){
    		Dockable focused = null;
    		try{
    			register.setStalled( true );
    			focused = getFocusedDockable();
    			
	    		if( this.theme != null )
	    			this.theme.uninstall( this );
	    		
	    		this.theme = theme;
	    		theme.install( this );
	    		
	    		// update only those station which are registered to this controller
	    		for( DockStation station : register.listDockStations() ){
	    			if( station.getController() == this ){
	    				station.updateTheme();
	    			}
	    		}
    		}
    		finally{
    			register.setStalled( false );
    		}
	    		
    		setFocusedDockable( focused, true );
    	}
	}
    
    /**
     * Gets the current theme of this controller.
     * @return the theme
     */
    public DockTheme getTheme() {
		return theme;
	}
    
    /**
     * A set of properties that can be used at any place.
     * @return the set of properties
     */
    public DockProperties getProperties(){
		return properties;
	}
    
    /**
     * Tells this controller that <code>component</code> somehow represents
     * <code>element</code>, and that events on <code>component</code> belong
     * to <code>element</code>.
     * @param component the representative
     * @param element some element or <code>null</code>
     */
    public void putRepresentative( Component component, DockElement element ){
    	if( element == null )
    		componentToDockElements.remove( component );
    	else
    		componentToDockElements.put( component, element );
    }
    
    /**
     * Searches the element which is parent or equal to <code>representative</code>.
     * This method also searches all {@link DockTitle}s and all
     * <code>Components</code> given by {@link #putRepresentative(Component, DockElement)}.
     * @param representative some component
     * @return the parent or <code>null</code>
     */
    public DockElement searchElement( Component representative ){
    	while( representative != null ){
    		DockElement element = componentToDockElements.get( representative );
    		if( element != null )
    			return element;
    		
    		representative = representative.getParent();
    	}
    	
    	return null;
    }
    
    /**
     * Adds a station to this controller. The controller allows the user to
     * drag and drop children from and to <code>station</code>. If
     * the children of <code>station</code> are stations itself, then
     * they will be added automatically
     * @param station the new station
     */
    public void add( DockStation station ){
    	register.add( station );
    }
    
    /**
     * Removes a station which was managed by this controller.
     * @param station the station to remove
     */
    public void remove( DockStation station ){
        register.remove( station );
    }
    
    /**
     * Gets the number of stations registered at this controller.
     * @return the number of stations
     * @see #add(DockStation)
     */
    public int getStationCount(){
        return register.getStationCount();
    }
    
    /**
     * Gets the station at the specified position.
     * @param index the location
     * @return the station
     */
    public DockStation getStation( int index ){
        return register.getStation( index );
    }
                
    /**
     * Tells whether one of the methods which change the focus is currently
     * running, or not. If the result is <code>true</code>, none should
     * change the focus.
     * @return <code>true</code> if the focus is currently changing
     */
    public boolean isOnFocusing() {
        return onFocusing;
    }
    
    /**
     * Sets the focused {@link Dockable}. If <code>focusedDockable</code>
     * is a station and one of its children has the focus, then nothing will
     * happen.
     * @param focusedDockable the element which should have the focus
     * @see #isOnFocusing()
     */
    public void setAtLeastFocusedDockable( Dockable focusedDockable ) {
        if( this.focusedDockable == null )
            setFocusedDockable( focusedDockable, false );
        
        if( !DockUtilities.isAncestor( focusedDockable, this.focusedDockable ))
            setFocusedDockable( focusedDockable, false );
    }
    
    /**
     * Sets the {@link Dockable} which should have the focus.
     * @param focusedDockable the element with the focus or <code>null</code>
     * @param force <code>true</code> if this controller must ensure
     * that all properties are correct, <code>false</code> if some
     * optimations are allowed. Clients normally can set this argument
     * to <code>false</code>.
     */
    public void setFocusedDockable( Dockable focusedDockable, boolean force ) {
        setFocusedDockable( focusedDockable, force, true );
    }

    /**
     * Sets the {@link Dockable} which should have the focus.
     * @param focusedDockable the element with the focus or <code>null</code>
     * @param force <code>true</code> if this controller must ensure
     * that all properties are correct, <code>false</code> if some
     * optimations are allowed. Clients normally can set this argument
     * to <code>false</code>.
     * @param ensureFocusSet whether to ensure that the focus is set correctly
     * or not.
     */
    public void setFocusedDockable( Dockable focusedDockable, boolean force, boolean ensureFocusSet ) {
    	// ignore more than one call
    	if( onFocusing )
    		return;
    	
    	try{
	        onFocusing = true;
	        
	        if( force || this.focusedDockable != focusedDockable ){
	            this.focusedDockable = focusedDockable;
	            
	            for( Map.Entry<DockTitle, Dockable> title : activeTitles.entrySet() ){
	                DockStation parent = title.getValue().getDockParent();
	                if( parent != null )
	                    parent.changed( title.getValue(), title.getKey(), false );
	                else
	                    title.getKey().changed( new DockTitleEvent( title.getValue(), false ));
	            }
	            
	            activeTitles.clear();
	            Dockable dockable = focusedDockable;
	            
	            while( dockable != null ){
	                DockStation station = dockable.getDockParent();
	                if( station != null ){
	                    DockTitle[] titles = dockable.listBoundTitles();
	                    
	                    for( DockTitle title : titles ){
	                        station.changed( dockable, title, true );
	                        activeTitles.put( title, dockable );
	                    }
	                    
	                    station.setFrontDockable( dockable );
	                    dockable = station.asDockable();
	                }
	                else
	                    dockable = null;
	            }
	            
	            if( ensureFocusSet ){
	                SwingUtilities.invokeLater( new Runnable(){
	                    public void run() {
	                        ensureFocusSet();     
	                    }
	                });
	            }
	            
	            fireDockableFocused( focusedDockable );
	        }
    	}
    	finally{
    		onFocusing = false;
    	}
    }
    
    /**
     * Tells whether <code>dockable</code> or one of its children
     * has currently the focus.
     * @param dockable the element which may have the focus
     * @return <code>true</code> if <code>dockable</code> or
     * one of its children is focused
     */
    public boolean isFocused( Dockable dockable ){
        Dockable temp = focusedDockable;
        while( temp != null ){
            if( temp == dockable )
                return true;
            
            DockStation station = temp.getDockParent();
            temp = station == null ? null : station.asDockable();
        }
        return false;
    }
    
    /**
     * Tells whether <code>title</code> is bound to its dockable or not. The
     * behavior is unspecified if the dockable of <code>title</code> is
     * unknown to this controller.
     * @param title the title which might be bound
     * @return <code>true</code> if the title is bound
     */
    public boolean isBound( DockTitle title ){
    	return dockTitleObserver.isBound( title );
    }
    
    /**
     * Ensures that a title or a {@link Component} of the currently
     * {@link #getFocusedDockable() focused Dockable} really
     * has the focus.
     */
    public void ensureFocusSet(){
        Dockable focusedDockable = this.focusedDockable;
        if( focusedDockable != null ){
            Stack<Dockable> front = new Stack<Dockable>();            
            
            Dockable temp = focusedDockable;
            
            while( temp != null ){
                DockStation parent = temp.getDockParent();
                if( parent != null )
                    front.push( temp );
                
                temp = parent == null ? null : parent.asDockable();
            }
            
            while( !front.isEmpty() ){
                Dockable element = front.pop();
                element.getDockParent().setFrontDockable( element );
            }
        
            DockTitle[] titles = focusedDockable.listBoundTitles();
            Component focused = FocusManager.getCurrentManager().getFocusOwner();
            if( focused != null ){
                if( SwingUtilities.isDescendingFrom( focused, focusedDockable.getComponent() ) )
                    return;
                
                for( DockTitle title : titles )
                    if( SwingUtilities.isDescendingFrom( focused, title.getComponent() ))
                        return;
            }
            
            Component component = focusedDockable.getComponent();
            if( component.isFocusable() ){
                component.requestFocus();
            }
            else{
                KeyboardFocusManager.getCurrentKeyboardFocusManager().focusNextComponent( component );
            }
        }
    }
    
    /**
     * Gets the {@link Dockable} which is currently focused.
     * @return the focused element or <code>null</code>
     */
    public Dockable getFocusedDockable() {
        return focusedDockable;
    }
    
    /**
     * Gets the manager of all titles on this controller
     * @return the manager
     */
    public DockTitleManager getDockTitleManager() {
		return dockTitles;
	}
    
    /**
     * Gets the set of icons which are used by this controller.
     * @return the set of icons
     */
    public IconManager getIcons() {
        return icons;
    }
    
    /**
     * Adds <code>guard</code> to this controller. The new 
     * {@link ActionGuard} has no influence on 
     * {@link DockActionSource DockActionSources} which are already
     * created.
     * @param guard the new guard
     */
    public void addActionGuard( ActionGuard guard ){
        if( guard == null )
            throw new IllegalArgumentException( "guard must not be null" );
        
        guards.add( guard );
    }
    
    /**
     * Removes <code>guard</code> from this controller.
     * @param guard the element to remove
     */
    public void removeActionGuard( ActionGuard guard ){
        guards.remove( guard );
    }
    
    /**
     * Creates a list of {@link DockAction DockActions} which can 
     * affect {@link Dockable}.<br>
     * Clients might rather use {@link Dockable#getGlobalActionOffers()} to
     * get a list of actions for a specific Dockable. This method only uses
     * the local information to compute a new source.
     * @param dockable a Dockable whose actions are demanded
     * @return a list of actions
     */
    public DockActionSource listOffers( Dockable dockable ){
        List<DockActionSource> guards = new ArrayList<DockActionSource>();
        List<DockActionSource> parents = new ArrayList<DockActionSource>();
        
        DockStation station = dockable.getDockParent();
        
        while( station != null ){
            parents.add( station.getIndirectActionOffers( dockable ) );
            
            Dockable transform = station.asDockable();
            if( transform != null )
                station = transform.getDockParent();
            else
                station = null;
        }
        
        for( ActionGuard guard : this.guards ){
            if( guard.react( dockable ))
                guards.add( guard.getSource( dockable ) );
        }        
        
        ActionOffer offer = null;
        
        for( ActionOffer temp : actionOffers )
            if( temp.interested( dockable )){
                offer = temp;
                break;
            }
        
        if( offer == null )
            offer = defaultActionOffer;

        DockActionSource parentSource = null;
        
        if( dockable.getDockParent() != null )
        	parentSource = dockable.getDockParent().getDirectActionOffers( dockable );
        
        return offer.getSource( dockable, dockable.getLocalActionOffers(), guards.toArray( new DockActionSource[guards.size()] ),
        		parentSource, parents.toArray( new DockActionSource[ parents.size() ] ));
    }
    
    /**
     * Adds a listener to this controller, the listener will receive events when
     * a {@link DockTitle} is bound or unbound.
     * @param listener the new listener
     */
    public void addDockTitleBindingListener( DockTitleBindingListener listener ){
        if( listener == null )
            throw new NullPointerException( "listener must not be null" );
        dockTitleBindingListeners.add( listener );
    }
    
    /**
     * Removes the observer <code>listener</code> from this controller.
     * @param listener the listener to remove
     */
    public void removeDockTitleBindingListener( DockTitleBindingListener listener ){
        if( listener == null )
            throw new NullPointerException( "listener must not be null" );
        dockTitleBindingListeners.remove( listener );
    }
    
    /**
     * Gets an array of all {@link DockTitleBindingListener} that are currently
     * registered at this controller.
     * @return the modifiable array
     */
    protected DockTitleBindingListener[] dockTitleBindingListeners(){
        return dockTitleBindingListeners.toArray(
                new DockTitleBindingListener[ dockTitleBindingListeners.size() ] );
    }
    
    /**
     * Adds a listener to this controller, the listener will be informed when
     * the focused {@link Dockable} changes.
     * @param listener the new listener
     */
    public void addDockableFocusListener( DockableFocusListener listener ){
        if( listener == null )
            throw new NullPointerException( "listener must not be null" );
        dockableFocusListeners.add( listener );
    }
    
    /**
     * Removes a listener from this controller.
     * @param listener the listener to remove
     */
    public void removeDockableFocusListener( DockableFocusListener listener ){
        if( listener == null )
            throw new NullPointerException( "listener must not be null" );
        dockableFocusListeners.remove( listener );
    }
    
    /**
     * Gets an array of currently registered {@link DockableFocusListener}s.
     * @return the modifiable array
     */
    protected DockableFocusListener[] dockableFocusListeners(){
        return dockableFocusListeners.toArray( new DockableFocusListener[ dockableFocusListeners.size() ] );
    }
    
    /**
     * Informs all listeners that <code>title</code> has been bound
     * to <code>dockable</code>.
     * @param title the bound title
     * @param dockable the owner of <code>title</code>
     */
    protected void fireTitleBound( DockTitle title, Dockable dockable ){
        for( DockTitleBindingListener listener : dockTitleBindingListeners() )
            listener.titleBound( this, title, dockable );
    }
    
    /**
     * Informs all listeners that <code>title</code> is no longer bound
     * to <code>dockable</code>.
     * @param title the unbound title
     * @param dockable the former owner of <code>title</code>
     */
    protected void fireTitleUnbound( DockTitle title, Dockable dockable ){
        for( DockTitleBindingListener listener : dockTitleBindingListeners() )
            listener.titleUnbound( this, title, dockable );
    }
    
    /**
     * Informs all listeners that <code>dockable</code> has gained
     * the focus.
     * @param dockable the owner of the focus, may be <code>null</code>
     */
    protected void fireDockableFocused( Dockable dockable ){
        for( DockableFocusListener listener : dockableFocusListeners() )
            listener.dockableFocused( this, dockable );
    }
    
    /**
     * Informs all listeners that <code>dockable</code> has been selected
     * by <code>station</code>.
     * @param station some {@link DockStation}
     * @param dockable the selected element of <code>station</code>
     */
    protected void fireDockableSelected( DockStation station, Dockable dockable ){
        for( DockableFocusListener listener : dockableFocusListeners() )
            listener.dockableSelected( this, station, dockable );
    }

    /**
     * An observer of the register and all {@link DockStation}s, informs when
     * a {@link DockStation} changes its selected {@link Dockable}.
     * @author Benjamin Sigg
     */
    private class DockableSelectionObserver extends DockRegisterAdapter{
        /** listener added to all {@link DockStation}s */
        private DockStationListener listener = new DockStationAdapter(){
            @Override
            public void dockableSelected( DockStation station, Dockable dockable ) {
                fireDockableSelected( station, dockable );
            }
        };
        
        @Override
        public void dockStationRegistered( DockController controller, DockStation station ) {
            station.addDockStationListener( listener );
        }
        
        @Override
        public void dockStationUnregistered( DockController controller, DockStation station ) {
            station.removeDockStationListener( listener );
        }
    }
    
    /**
     * Observers the {@link DockRegister}, adds listeners to new {@link Dockable}s
     * and {@link DockTitle}s, and collects the components of these elements
     */
    private class DockTitleObserver extends DockRegisterAdapter implements DockTitleBindingListener{
    	/** a set of all known titles */
    	private Set<DockTitle> titles = new HashSet<DockTitle>();

    	/** a listener added to each {@link Dockable} */
    	private DockableListener dockableListener = new DockableAdapter(){
            @Override
            public void titleBound( Dockable dockable, DockTitle title ) {
                titles.add( title );
                putRepresentative( title.getComponent(), dockable );
                
                title.bind();
                fireTitleBound( title, dockable );
                
                DockStation station = dockable.getDockParent();
                boolean focused = false;
                Dockable temp = focusedDockable;
                while( !focused && temp != null ){
                    focused = temp == dockable;
                    DockStation parent = temp.getDockParent();
                    temp = parent == null ? null : parent.asDockable();
                }
                
                if( station == null )
                    title.changed( new DockTitleEvent( dockable, focused ));
                else
                    station.changed( dockable, title, focused );
                
                if( focused )
                    activeTitles.put( title, dockable );
            }
            
            
            @Override
            public void titleUnbound( Dockable dockable, DockTitle title ) {
                titles.remove( title );
                putRepresentative( title.getComponent(), null );
                title.unbind();
                fireTitleUnbound( title, dockable );
            }
    	};
    	
        /**
         * Tells whether title is bound to its {@link Dockable} or not.
         * @param title the title whose state is searched
         * @return the state
         */
        public boolean isBound( DockTitle title ){
            return titles.contains( title );
        }

        public void titleBound( DockController controller, DockTitle title, Dockable dockable ) {
            // ignore
        }
        
        public void titleUnbound( DockController controller, DockTitle title, Dockable dockable ) {
            activeTitles.remove( title );
            DockStation parent = dockable.getDockParent();
            if( parent != null )
                parent.changed( dockable, title, false );
            else
                title.changed( new DockTitleEvent( dockable, false ));
        }

        @Override
        public void dockableRegistering( DockController controller, Dockable dockable ){
        	dockable.addDockableListener( dockableListener );
        }
        
        @Override
        public void dockableRegistered( DockController controller, Dockable dockable ) {
        	putRepresentative( dockable.getComponent(), dockable );
            DockTitle[] titles = dockable.listBoundTitles();
            for( DockTitle title : titles ){
                if( this.titles.add( title )){
                    title.bind();
                    fireTitleBound( title, dockable );
                }
            }
        }

        @Override
        public void dockableUnregistered( DockController controller, Dockable dockable ) {
            dockable.removeDockableListener( dockableListener );
            putRepresentative( dockable.getComponent(), null );
        	
            DockTitle[] titles = dockable.listBoundTitles();
            for( DockTitle title : titles ){
                if( this.titles.remove( title ) ){
                    title.unbind();
                    fireTitleUnbound( title, dockable );
                }
            }
        }
    }
}
