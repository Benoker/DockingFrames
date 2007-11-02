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

import bibliothek.gui.dock.DockAcceptance;
import bibliothek.gui.dock.IconManager;
import bibliothek.gui.dock.SingleParentRemover;
import bibliothek.gui.dock.accept.MultiDockAcceptance;
import bibliothek.gui.dock.action.*;
import bibliothek.gui.dock.action.view.ActionViewConverter;
import bibliothek.gui.dock.control.*;
import bibliothek.gui.dock.event.DockAdapter;
import bibliothek.gui.dock.event.DockControllerListener;
import bibliothek.gui.dock.event.DockRegisterListener;
import bibliothek.gui.dock.event.DockTitleEvent;
import bibliothek.gui.dock.themes.BasicTheme;
import bibliothek.gui.dock.title.DockTitle;
import bibliothek.gui.dock.title.DockTitleManager;
import bibliothek.gui.dock.util.DockProperties;
import bibliothek.gui.dock.util.DockUtilities;

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
	
	/** a manager handling drag and drop */
	private DockRelocator relocator;
	
    /** the Dockable which has currently the focus, can be <code>null</code> */
    private Dockable focusedDockable = null;
    
    /** observer of this controller */
    private List<DockControllerListener> listeners = new ArrayList<DockControllerListener>();
        
    /** <code>true</code> while the controller actively changes the focus */
    private boolean onFocusing = false;
    /** a special controller listening to AWT-events and changing the focused dockable */
    private MouseFocusObserver focusObserver;
    
    /** an observer of the {@link DockTitle} */
    private TitleListener titleListener = new TitleListener();
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
   
    /** whether stations with none or one child will be removed */
    private boolean singleParentRemove = false;
    /** remover of stations with none or one child */
    private SingleParentRemover remover;
    
    /** a theme describing the look of the stations */
    private DockTheme theme;
    /** a set of properties */
    private DockProperties properties = new DockProperties();
    
    /**
     * Creates a new controller. 
     */
    public DockController(){
    	this( true );
    }
    
    /**
     * Creates a new controller but does not initiate the properties of this
     * controller if not wished. Clients should call the method 
     * {@link #initiate()} if the pass <code>false</code> to this constructor.
     * Otherwise the behavior of this controller is unspecified.
     * @param initiate <code>true</code> if all properties should be initiated
     */
    protected DockController( boolean initiate ){
    	if( initiate ){
    		initiate();
    	}
    }
    
    /**
     * Initializes all properties of this controller. This method should be
     * called only once. This method can be called by a subclass if the 
     * subclass used {@link #DockController(boolean)} with an argument <code>false</code>.
     * @see #createRegister()
     * @see #createRelocator()
     * @see #createDefaultActionOffer()
     * @see #createMouseFocusObserver()
     * @see #createActionViewConverter()
     * @see #createFocusController()
     * @see #createPopupController()
     */
    protected final void initiate(){
    	register = createRegister();
    	DockRegisterListener focus = createFocusController();
    	if( focus != null )
    		register.addDockRegisterListener( focus );
    	
    	DockRegisterListener popup = createPopupController();
    	if( popup != null )
    		register.addDockRegisterListener( popup );
    	
		register.addDockRegisterListener( titleListener );
        relocator = createRelocator();
        
        for( DockControllerListener listener : listeners ){
            register.addDockRegisterListener( listener );
            relocator.addDockRelocatorListener( listener );
        }
    	
        defaultActionOffer = createDefaultActionOffer();
        focusObserver = createMouseFocusObserver();
        actionViewConverter = createActionViewConverter();
        
        DockUI.getDefaultDockUI().fillIcons( icons );
        
        setTheme( new BasicTheme() );
    }
    
    /**
     * Removes listeners and frees resources. This method should be called
     * if this controller is no longer needed.
     */
    public void kill(){
	    focusObserver.kill();
	    register.kill();
    }
    
    /**
     * Creates a new register for this controller.
     * @return the new register
     */
    protected DockRegister createRegister(){
    	return new DockRegister( this );
    }
    
    /**
     * Creates a new relocator for this controller.
     * @return the relocator
     */
    protected DockRelocator createRelocator(){
    	return new DefaultDockRelocator( this );
    }
    
    /**
     * Creates a listener which will observe all stations to ensure that
     * the focused {@link Dockable} is always visible.
     * @return the listener or <code>null</code>
     */
    protected DockRegisterListener createFocusController(){
    	return new FocusController( this );
    }
    
    /**
     * Creates a listener which will open a popup-menu for each title
     * or dockable known to this controller.
     * @return the new listener or <code>null</code>
     */
    protected DockRegisterListener createPopupController(){
    	return new PopupController( this );
    }
    
    /**
     * Creates the focus-controller of this controller.
     * @return the controller, not <code>null</code>
     */
    protected MouseFocusObserver createMouseFocusObserver(){
        return new DefaultMouseFocusObserver( this );
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
     * Gets the manager for handling drag and drop operations.
     * @return the manager
     */
    public DockRelocator getRelocator(){
		return relocator;
	}
    
    /**
     * Creates the converter that will transform actions into views.
     * @return the new converter.
     */
    protected ActionViewConverter createActionViewConverter(){
    	return new ActionViewConverter();
    }

    /**
     * Gets the current {@link ActionViewConverter}.
     * @return the converter
     */
    public ActionViewConverter getActionViewConverter(){
    	return actionViewConverter;
    }
    
    /**
     * Creates the default action offer. This {@link ActionOffer} will
     * be used if no other offer was interested in a Dockable.
     * @return the offer, must not be <code>null</code>
     */
    protected ActionOffer createDefaultActionOffer(){
        return new DefaultActionOffer();
    }
    
    /**
     * Tells whether stations with only one child are removed or not.
     * @return <code>true</code> if stations with one or less
     * children are removed automatically
     * @see #setSingleParentRemove(boolean)
     */
    public boolean isSingleParentRemove(){
        return singleParentRemove;
    }
    
    /**
     * Sets whether stations with one or none child are removed automatically
     * or not. This property has a great effect on some stations, clients shouldn't
     * change the value once the first station is {@link #add(DockStation) added}.
     * @param remove <code>true</code> if stations with one or less
     * children are removed
     * @see #createSingleParentRemover()
     */
    public void setSingleParentRemove( boolean remove ){
        if( singleParentRemove != remove ){
            if( remove ){
                if( remover == null )
                    remover = createSingleParentRemover();
                
                remover.install( this );
            }
            else{
                remover.uninstall( this );
            }
            singleParentRemove = remove;
        }
    }
    
    /**
     * Creates a {@link SingleParentRemover} that will be used to remove
     * some stations from this controller.
     * @return The remover
     * @see #setSingleParentRemove(boolean)
     */
    protected SingleParentRemover createSingleParentRemover(){
    	return new SingleParentRemover();
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
    		Dockable focused = getFocusedDockable();
    		
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
     * Gest the number of stations registered at this controller.
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
        
        if( !DockUtilities.isAnchestor( focusedDockable, this.focusedDockable ))
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
	            
	            if( ensureFocusSet )
	                ensureFocusSet();
	            
	            firedockableFocused( focusedDockable );
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
    	return titleListener.isBound( title );
    }
    
    /**
     * Ensures that a title or a {@link Component} of the currently
     * {@link #getFocusedDockable() focused Dockable} really
     * has the focus.
     */
    public void ensureFocusSet(){
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
     * Adds an observer to this controller.
     * @param listener the observer
     */
    public void addDockControllerListener( DockControllerListener listener ){
        listeners.add( listener );
        if( register != null )
            register.addDockRegisterListener( listener );
        if( relocator != null )
            relocator.addDockRelocatorListener( listener );
    }
    
    /**
     * Removes an observer from this controller.
     * @param listener the observer to remove
     */
    public void removeDockControllerListener( DockControllerListener listener ){
        listeners.remove( listener );
        if( register != null )
            register.removeDockRegisterListener( listener );
        if( relocator != null )
            relocator.removeDockRelocatorListener( listener );
    }
    
    /**
     * Lists all {@link DockControllerListener} of this station. The list is
     * independent from the original list.
     * @return the list of listeners
     */
    protected DockControllerListener[] listDockControllerListener(){
        return listeners.toArray( new DockControllerListener[ listeners.size() ]);
    }

    
    /**
     * Informs all listeners that <code>title</code> has been bound
     * to <code>dockable</code>.
     * @param title the bound title
     * @param dockable the owner of <code>title</code>
     */
    protected void fireTitleBound( DockTitle title, Dockable dockable ){
        for( DockControllerListener listener : listDockControllerListener() )
            listener.titleBound( this, title, dockable );
    }
    
    /**
     * Informs all listeners that <code>title</code> is no longer bound
     * to <code>dockable</code>.
     * @param title the unbound title
     * @param dockable the former owner of <code>title</code>
     */
    protected void fireTitleUnbound( DockTitle title, Dockable dockable ){
        for( DockControllerListener listener : listDockControllerListener() )
            listener.titleUnbound( this, title, dockable );
    }
    
    /**
     * Informs all listeners that <code>dockable</code> has gained
     * the focus.
     * @param dockable the owner of the focus, may be <code>null</code>
     */
    protected void firedockableFocused( Dockable dockable ){
        for( DockControllerListener listener : listDockControllerListener() )
            listener.dockableFocused( this, dockable );
    }
    

    /**
     * Observers this controller and registers listeners to all new titles.
     */
    private class TitleListener extends DockAdapter{
    	/** a set of all known titles */
    	private Set<DockTitle> titles = new HashSet<DockTitle>();

        /**
         * Tells whether title is bound to its {@link Dockable} or not.
         * @param title the title whose state is searched
         * @return the state
         */
        public boolean isBound( DockTitle title ){
            return titles.contains( title );
        }
        
        @Override
        public void titleBound( Dockable dockable, DockTitle title ) {
        	titles.add( title );
        	
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
        public void titleUnbound( DockController controller, DockTitle title, Dockable dockable ) {
            activeTitles.remove( title );
            DockStation parent = dockable.getDockParent();
            if( parent != null )
                parent.changed( dockable, title, false );
            else
                title.changed( new DockTitleEvent( dockable, false ));
        }
        
        @Override
        public void titleUnbound( Dockable dockable, DockTitle title ) {
            titles.remove( title );
            title.unbind();
            fireTitleUnbound( title, dockable );
        }

        @Override
        public void dockableRegistering( DockController controller, Dockable dockable ){
        	dockable.addDockableListener( this );
        }
        
        @Override
        public void dockableRegistered( DockController controller, Dockable dockable ) {
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
            dockable.removeDockableListener( this );
        	
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
