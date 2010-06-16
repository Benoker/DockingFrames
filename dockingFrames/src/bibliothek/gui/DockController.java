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
import java.awt.Dialog;
import java.awt.EventQueue;
import java.awt.Frame;
import java.awt.KeyboardFocusManager;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Stack;

import javax.swing.FocusManager;
import javax.swing.KeyStroke;
import javax.swing.LookAndFeel;
import javax.swing.SwingUtilities;
import javax.swing.Timer;
import javax.swing.UIManager;

import bibliothek.gui.dock.DockElement;
import bibliothek.gui.dock.DockElementRepresentative;
import bibliothek.gui.dock.SplitDockStation;
import bibliothek.gui.dock.accept.DockAcceptance;
import bibliothek.gui.dock.accept.MultiDockAcceptance;
import bibliothek.gui.dock.action.ActionGuard;
import bibliothek.gui.dock.action.ActionOffer;
import bibliothek.gui.dock.action.ActionPopupSuppressor;
import bibliothek.gui.dock.action.DockAction;
import bibliothek.gui.dock.action.DockActionSource;
import bibliothek.gui.dock.action.view.ActionViewConverter;
import bibliothek.gui.dock.control.ComponentHierarchyObserver;
import bibliothek.gui.dock.control.ControllerSetupCollection;
import bibliothek.gui.dock.control.DefaultDockControllerFactory;
import bibliothek.gui.dock.control.DockControllerFactory;
import bibliothek.gui.dock.control.DockRegister;
import bibliothek.gui.dock.control.DockRelocator;
import bibliothek.gui.dock.control.DockRelocatorMode;
import bibliothek.gui.dock.control.DockableSelector;
import bibliothek.gui.dock.control.DoubleClickController;
import bibliothek.gui.dock.control.KeyboardController;
import bibliothek.gui.dock.control.MouseFocusObserver;
import bibliothek.gui.dock.control.SingleParentRemover;
import bibliothek.gui.dock.event.ControllerSetupListener;
import bibliothek.gui.dock.event.DockControllerRepresentativeListener;
import bibliothek.gui.dock.event.DockRegisterAdapter;
import bibliothek.gui.dock.event.DockRegisterListener;
import bibliothek.gui.dock.event.DockStationAdapter;
import bibliothek.gui.dock.event.DockStationListener;
import bibliothek.gui.dock.event.DockTitleBindingListener;
import bibliothek.gui.dock.event.DockTitleEvent;
import bibliothek.gui.dock.event.DockableAdapter;
import bibliothek.gui.dock.event.DockableFocusEvent;
import bibliothek.gui.dock.event.DockableFocusListener;
import bibliothek.gui.dock.event.DockableListener;
import bibliothek.gui.dock.event.DockableSelectionEvent;
import bibliothek.gui.dock.event.DockableSelectionListener;
import bibliothek.gui.dock.event.UIListener;
import bibliothek.gui.dock.themes.DockThemeExtension;
import bibliothek.gui.dock.title.DockTitle;
import bibliothek.gui.dock.title.DockTitleManager;
import bibliothek.gui.dock.util.DirectWindowProvider;
import bibliothek.gui.dock.util.DockProperties;
import bibliothek.gui.dock.util.DockUtilities;
import bibliothek.gui.dock.util.IconManager;
import bibliothek.gui.dock.util.WindowProvider;
import bibliothek.gui.dock.util.WindowProviderListener;
import bibliothek.gui.dock.util.WindowProviderWrapper;
import bibliothek.gui.dock.util.color.ColorManager;
import bibliothek.gui.dock.util.extension.ExtensionManager;
import bibliothek.gui.dock.util.extension.ExtensionName;
import bibliothek.gui.dock.util.font.FontManager;

/**
 * A controller connects all the {@link DockStation}s, {@link Dockable}s and
 * other objects that play together in this framework. This class also serves
 * as low-level access point for clients. When using this framework in general, 
 * or {@link DockController} in particular, several rules have
 * to be obeied:
 * <ul>
 * 	<li>{@link DockStation}s and {@link Dockable}s build trees. The roots
 *  of these trees need to be registered using {@link #add(DockStation)}.</li>
 *  <li>Each <code>DockController</code> builds its own realm, normally only
 *  objects within such a realm can interact with each other. Drag and drop
 *  operations cannot move a {@link Dockable} from one realm to another.</li>
 *  <li>Most of the interesting actions are only available for {@link Dockable}s
 *  that are within a realm (like drag and drop).</li>
 *  <li>Normally clients do not work with the trees of stations and <code>Dockable</code>s.
 *  If they need to work directly in the tree they should call {@link #freezeLayout()}
 *  and later {@link #meltLayout()} to temporarely disable automatic actions (like
 *  the fact that a <code>DockStation</code> with only one child gets removed).</li>
 *  <li>If a <code>DockController</code> is no longer needed then the method
 *  {@link #kill()} should be called. This method will ensure that the
 *  object can be reclaimed by the garbage collector. </li>
 * </ul>
 * 
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
	
	/** selector allows to select {@link Dockable} using the mouse or the keyboard */
	private DockableSelector dockableSelector;
	
    /** the Dockable which has currently the focus, can be <code>null</code> */
    private Dockable focusedDockable = null;
    
    /** Listeners observing the focused {@link Dockable} */
    private List<DockableFocusListener> dockableFocusListeners = new ArrayList<DockableFocusListener>();
    /** Listeners observing the selected {@link Dockable}s */
    private List<DockableSelectionListener> dockableSelectionListeners = new ArrayList<DockableSelectionListener>();
    /** Listeners observing the bound-state of {@link DockTitle}s */
    private List<DockTitleBindingListener> dockTitleBindingListeners = new ArrayList<DockTitleBindingListener>();
    /** Listeners observing the ui */
    private List<UIListener> uiListeners = new ArrayList<UIListener>();
    
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
    /** map of colors that are used through the realm of this controller */
    private ColorManager colors;
    /** map of fonts that are used through the realm of this controller */
    private FontManager fonts;
    /** extensions to this controller */
    private ExtensionManager extensions;
    
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
    private DockProperties properties;
    
    /** the factory that creates new parts of this controller */
    private DockControllerFactory factory;
    
    /** tells which {@link Component} represents which {@link DockElement} */
    private Map<Component, DockElementRepresentative> componentToDockElements = 
    	new HashMap<Component, DockElementRepresentative>();
    /** a list of listeners listening for changes in {@link #componentToDockElements} */
    private List<DockControllerRepresentativeListener> componentToDockElementsListeners =
        new ArrayList<DockControllerRepresentativeListener>();
    
    /** the root window of the application */
    private WindowProviderWrapper rootWindowProvider;
    /** the current root window, can be <code>null</code> */
    private Window rootWindow;
    
    /** a listener that is added to the {@link UIManager} and gets notified when the {@link LookAndFeel} changes */
    private PropertyChangeListener lookAndFeelObserver = new PropertyChangeListener(){
        public void propertyChange( PropertyChangeEvent evt ) {
            if( "lookAndFeel".equals( evt.getPropertyName() )){
                updateUI();
            }
        }
    };
    
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
        
        properties = new DockProperties( this );
        colors = new ColorManager( this );
        fonts = new FontManager( this );
        
    	rootWindowProvider = new WindowProviderWrapper();
        rootWindowProvider.addWindowProviderListener( new WindowProviderListener(){
            public void windowChanged( WindowProvider provider, Window window ) {
                Window oldWindow = rootWindow;
                rootWindow = window;
                rootWindowChanged( oldWindow, window );
            }
            
            public void visibilityChanged( WindowProvider provider, boolean showing ){
            	// ignore
            }
        });
        
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
    	
    	factory.createPopupController( this, setup );
    	
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
        dockableSelector = factory.createDockableSelector( this, setup );
        
        extensions = factory.createExtensionManager( this, setup );
        extensions.init();
        
        DockUI.getDefaultDockUI().fillIcons( icons );
        
        setTheme( DockUI.getDefaultDockUI().getDefaultTheme().create() );
        
        relocator.addMode( DockRelocatorMode.SCREEN_ONLY );
        relocator.addMode( DockRelocatorMode.NO_COMBINATION );
        
        // set properties here, allows the keys not to have a default value and
        // allows to have the properties present
        properties.set( SplitDockStation.MAXIMIZE_ACCELERATOR,
                KeyStroke.getKeyStroke( KeyEvent.VK_M, InputEvent.CTRL_DOWN_MASK ) );
        
        properties.set( DockFrontend.HIDE_ACCELERATOR,
                KeyStroke.getKeyStroke( KeyEvent.VK_F4, InputEvent.CTRL_DOWN_MASK ) );
        
        properties.set( DockableSelector.INIT_SELECTION, 
                KeyStroke.getKeyStroke( KeyEvent.VK_E, KeyEvent.CTRL_DOWN_MASK | KeyEvent.SHIFT_DOWN_MASK ) );
        
        
        setSingleParentRemover( factory.createSingleParentRemover( this, setup ) );
        
        UIManager.addPropertyChangeListener( lookAndFeelObserver );
        
        for( ControllerSetupListener listener : setupListeners )
            listener.done( this );
    }
    
    /**
     * Removes listeners and frees resources. This method should be called
     * if this controller is no longer needed. This method should be called
     * only once.
     */
    public void kill(){
	    focusObserver.kill();
	    register.kill();
	    keyboardController.kill();
	    theme.uninstall( this );
	    UIManager.removePropertyChangeListener( lookAndFeelObserver );
	    extensions.kill();
	    setRootWindowProvider( null );
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
            if( rootWindow != null )
                componentHierarchyObserver.add( rootWindow );
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
     * Gets the manager that is responsible to convert {@link DockAction}s to 
     * some kind of {@link Component}.
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
     * Freezes the layout. Normally if a client makes a change in the layout
     * (e.g. remove a {@link Dockable} from its parent) additional actions
     * can be triggered (e.g. remove the parent because it has no children
     * left). If the layout is frozen then these implicit actions are not
     * triggered. This method can be called more than once and the layout
     * will remain frozen until {@link #meltLayout()} is called as often
     * as {@link #freezeLayout()}.<br>
     * A side note: internally this method disables the {@link DockRegisterListener}s.
     * Events during the time where the listeners are disabled are collected,
     * conflicting events will cancel each other out, remaining events will be 
     * distributed once {@link #meltLayout()} is called. The effect of this method is
     * equal to the effect when calling {@link DockRegister#setStalled(boolean)}.
     * @return <code>true</code> if the layout was already frozen,
     * <code>false</code> if it was not frozen
     * @see #meltLayout()
     */
    public boolean freezeLayout(){
    	DockRegister register = getRegister();
    	boolean frozen = register.isStalled();
    	getRegister().setStalled( true );
    	return frozen;
    }
    
    /**
     * Tells whether the layout is frozen, see {@link #freezeLayout()}.
     * @return <code>true</code> if the layout is frozen
     */
    public boolean isLayoutFrozen(){
    	return getRegister().isStalled();
    }
    
    /**
     * Melts a frozen layout (see {@link #freezeLayout()}).
     * @return <code>true</code> if the layout remains frozen, <code>false</code>
     * if the layout has melted.
     * @throws IllegalStateException if the layout is not {@link #isLayoutFrozen() frozen}
     * @see #meltLayout()
     */
    public boolean meltLayout(){
    	if( !isLayoutFrozen() )
    		throw new IllegalStateException( "the layout is not frozen" );
    	
    	DockRegister register = getRegister();
    	register.setStalled( false );
    	return register.isStalled();
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
     * Removes a rule that decided which station could have which children.
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
     * Searches the {@link ActionOffer} for <code>dockable</code>.
     * @param dockable the element whose offer is searched
     * @return the offer
     */
    public ActionOffer getActionOffer( Dockable dockable ){
    	for( ActionOffer offer : actionOffers ){
    		if( offer.interested( dockable ))
    			return offer;
    	}
    	return getDefaultActionOffer();
    }
    
    /**
     * Sets the theme of this controller. This method ensures that all
     * registered stations know also the new theme.
     * @param theme the new theme
     */
    public void setTheme( DockTheme theme ){
    	if( theme == null )
    		throw new IllegalArgumentException( "Theme must not be null" );
    	
    	if( this.theme != theme ){
    		for( UIListener listener : uiListeners() )
    			listener.themeWillChange( this, this.theme, theme );
    		
    		DockTheme oldTheme = this.theme;
    		Dockable focused = null;
    		try{
    			register.setStalled( true );
    			focused = getFocusedDockable();
    			
	    		if( this.theme != null )
	    			this.theme.uninstall( this );
	    		
	    		this.theme = theme;
	    		
	    		ExtensionName<DockThemeExtension> name = new ExtensionName<DockThemeExtension>( 
	    				DockThemeExtension.DOCK_THEME_EXTENSION, DockThemeExtension.class, DockThemeExtension.THEME_PARAMETER, theme );
	    		List<DockThemeExtension> extensions = getExtensions().load( name );
	    		
	    		theme.install( this, extensions.toArray( new DockThemeExtension[ extensions.size() ] ) );
	    		dockTitles.registerTheme( DockTitleManager.THEME_FACTORY_ID, theme.getTitleFactory( this ) );
	    		
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
    		
    		for( UIListener listener : uiListeners() )
    			listener.themeChanged( this, oldTheme, theme );
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
     * Adds a listener to this controller, <code>listener</code> will be informed
     * when the map of {@link DockElement}s and the {@link Component}s which 
     * represent them changes.
     * @param listener the new listener, not <code>null</code>
     */
    public void addRepresentativeListener( DockControllerRepresentativeListener listener ){
        if( listener == null )
            throw new IllegalArgumentException( "listener must not be null" );
        componentToDockElementsListeners.add( listener );
    }
    
    /**
     * Removes <code>listener</code> from this controller.
     * @param listener the listener to remove
     */
    public void removeRepresentativeListener( DockControllerRepresentativeListener listener ){
        componentToDockElementsListeners.remove( listener );
    }
    
    /**
     * Informs this controller about a new representative for a {@link DockElement}.
     * Note that each {@link DockElementRepresentative} of this {@link DockController}
     * must have another {@link DockElementRepresentative#getComponent() component}.
     * @param representative the new representative
     * @see #searchElement(Component)
     */
    public void addRepresentative( DockElementRepresentative representative ) {
        DockControllerRepresentativeListener[] listeners = componentToDockElementsListeners
                .toArray( new DockControllerRepresentativeListener[componentToDockElementsListeners.size()] );
        
        DockElementRepresentative old = componentToDockElements.put(
                representative.getComponent(), representative );
    	
    	if( old != null ){
    	    for( DockControllerRepresentativeListener listener : listeners ){
    	        listener.representativeRemoved( this, old );
    	    }
    	}
    	if( representative != null ){
    	    for( DockControllerRepresentativeListener listener : listeners ){
    	        listener.representativeAdded( this, representative );
    	    }
    	}
    }
    
    /**
     * Removes <code>representative</code> from this controller.
     * @param representative the element to remove
     * @see #addRepresentative(DockElementRepresentative)
     */
    public void removeRepresentative( DockElementRepresentative representative ){
        if( componentToDockElements.remove( representative.getComponent() ) != null ){
            DockControllerRepresentativeListener[] listeners = componentToDockElementsListeners
                .toArray( new DockControllerRepresentativeListener[componentToDockElementsListeners.size()] );
        
            for( DockControllerRepresentativeListener listener : listeners ){
                listener.representativeRemoved( this, representative );
            }
        }
    }
    
    /**
     * Searches the element which is parent or equal to <code>representative</code>.
     * This method searches through all elements given by {@link #addRepresentative(DockElementRepresentative)}. 
     * This also includes all {@link Dockable}s and all {@link DockTitle}s.
     * @param representative some component
     * @return the parent or <code>null</code>
     * @see #addRepresentative(DockElementRepresentative)
     */
    public DockElementRepresentative searchElement( Component representative ){
    	while( representative != null ){
    	    DockElementRepresentative element = componentToDockElements.get( representative );
    		if( element != null ){
    		    if( element.getElement().getController() == this )
    		        return element;
    		}
    		
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
     * running, or not. If the result is <code>true</code>, noone should
     * change the focus.
     * @return <code>true</code> if the focus is currently changing
     */
    public boolean isOnFocusing() {
        return onFocusing;
    }
    
    /**
     * Sets the focused {@link Dockable}. Nothing happens if <code>focusedDockable</code>
     * is a station and one of its children already has the focus.
     * @param focusedDockable the element which should have the focus
     * @see #isOnFocusing()
     */
    public void setAtLeastFocusedDockable( Dockable focusedDockable ) {
        if( this.focusedDockable == null ){
            setFocusedDockable( focusedDockable, false );
        }
        else if( !DockUtilities.isAncestor( focusedDockable, this.focusedDockable )){
            setFocusedDockable( focusedDockable, false );
        }
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
	            Dockable oldFocused = this.focusedDockable;
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
	                if( EventQueue.isDispatchThread() ){
    	                SwingUtilities.invokeLater( new Runnable(){
    	                    public void run() {
    	                        ensureFocusSet();     
    	                    }
    	                });
	                }
	                else{
	                    // we are in the wrong Thread, but we can try...
	                    ensureFocusSet();
	                }
	            }
	            
	            if( oldFocused != focusedDockable )
	                fireDockableFocused( oldFocused, focusedDockable );
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
     * @see Dockable#bind(DockTitle)
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
                component.requestFocusInWindow();
                focus( component, 10, 20 );
            }
            else{
                KeyboardFocusManager.getCurrentKeyboardFocusManager().focusNextComponent( component );
            }
        }
    }
    
    /**
     * Ensures that <code>component</code> has the focus and is on the 
     * active window. This is done by waiting <code>delay</code> milliseconds
     * and then checking the current focus owner. If the owner is not <code>component</code>,
     * then the focus is transfered. Checking stops after <code>component</code>
     * is found to be the focus owner, or <code>loops</code> failures were reported.<br>
     * Note: this awkward method to change the focus is necessary because on some
     * systems - like Linux - Java does not handle focus very well.
     * @param component the component which should have the focus
     * @param delay how much time to wait between two checks of the focus
     * @param loops how many times to check
     */
    private void focus( final Component component, int delay, final int loops ){
        final Timer timer = new Timer( delay, null );
        timer.addActionListener( new ActionListener(){
            private int remaining = loops;
            
            public void actionPerformed( ActionEvent e ) {
                remaining--;

                KeyboardFocusManager manager = KeyboardFocusManager.getCurrentKeyboardFocusManager();
                if( manager.getPermanentFocusOwner() != component ){
                    manager.clearGlobalFocusOwner();
                    component.requestFocus();
                    
                    if( remaining > 0 ){
                        timer.restart();
                    }
                }
            }
        });
        
        timer.setRepeats( false );
        timer.start();
    }
    
    /**
     * Gets the {@link Dockable} which is currently focused.
     * @return the focused element or <code>null</code>
     */
    public Dockable getFocusedDockable() {
        return focusedDockable;
    }
    
    /**
     * Gets the selector which can show a popup window such that the user
     * can use the keyboard or the mouse to focus a {@link Dockable}.
     * @return the selector
     */
    public DockableSelector getDockableSelector() {
        return dockableSelector;
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
     * Gets the map of colors which are used by this controller.
     * @return the map of colors
     */
    public ColorManager getColors() {
        return colors;
    }
    
    /**
     * Gets the map of fonts which are used by this controller.
     * @return the map of fonts
     */
    public FontManager getFonts() {
        return fonts;
    }
    
    /**
     * Gets all extensions that are used by this controller.
     * @return all available extensions
     */
    public ExtensionManager getExtensions(){
		return extensions;
	}
    
    /**
     * Sets the window that is used when dialogs have to be shown.
     * @param window the root window, can be <code>null</code>
     * @see #findRootWindow()
     * @see #setRootWindowProvider(WindowProvider)
     */
    public void setRootWindow( Window window ){
        if( window == null )
            setRootWindowProvider( null );
        else
            setRootWindowProvider( new DirectWindowProvider( window ) );
    }
    
    /**
     * Sets the provider which will be used to find a root window
     * for this controller. The root window is used as owner for dialogs.
     * @param window the new provider, can be <code>null</code>
     */
    public void setRootWindowProvider( WindowProvider window ){
        rootWindowProvider.setDelegate( window );
    }
    
    /**
     * Gets the provider which will be used to find a root window for this
     * controller. Note that this is not the same provider as given to
     * {@link #setRootWindowProvider(WindowProvider)}, but one that will
     * always return the same result as the provider set by the client. This
     * method always returns the same object.
     * @return the root window provider, never <code>null</code>
     */
    public WindowProviderWrapper getRootWindowProvider() {
        return rootWindowProvider;
    }
    
    /**
     * Called whenever the root window of this controller changed.
     * @param oldWindow the old root window
     * @param newWindow the new root window
     */
    protected void rootWindowChanged( Window oldWindow, Window newWindow ){
        if( componentHierarchyObserver != null ){
            if( oldWindow != null )
                componentHierarchyObserver.remove( oldWindow );
            
            if( newWindow != null )
                componentHierarchyObserver.add( newWindow );
        }
    }
    
    /**
     * Searches the root-window of the application. Assuming the window is not yet known:
     * uses all {@link DockElement}s known to this controller to search
     * the root window. This method first tries to find a {@link Frame},
     * then a {@link Dialog} and finally returns every {@link Window}
     * that it finds.
     * @return the root window or <code>null</code>
     * @see #setRootWindow(Window)
     */
    public Window findRootWindow(){
        if( rootWindow != null )
            return rootWindow;
        
        Window window = null;
        Dialog dialog = null;
        
        for( DockStation station : getRegister().listRoots() ){
            Dockable dockable = station.asDockable();
            if( dockable != null ){
                Component component = dockable.getComponent();
                Window ancestor = SwingUtilities.getWindowAncestor( component );
                if( ancestor != null ){
                    window = ancestor;
                    
                    if( ancestor instanceof Frame ){
                        return ancestor;
                    }
                    else if( ancestor instanceof Dialog ){
                        dialog = (Dialog)ancestor;
                    }
                }
            }
        }
        
        for( Dockable dockable : getRegister().listDockables() ){
            Component component = dockable.getComponent();
            Window ancestor = SwingUtilities.getWindowAncestor( component );
            if( ancestor != null ){
                window = ancestor;
                
                if( ancestor instanceof Frame ){
                    return ancestor;
                }
                else if( ancestor instanceof Dialog ){
                    dialog = (Dialog)ancestor;
                }
            }
        }
        
        if( dialog != null )
            return dialog;
        
        return window;
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
        
        ActionOffer offer = getActionOffer( dockable );
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
     * Adds a listener to this controller, the listener will be informed when
     * a selected {@link Dockable} changes. A selected {@link Dockable} shown
     * in a special way by its parent {@link DockStation}.
     * @param listener the new listener
     */
    public void addDockableSelectionListener( DockableSelectionListener listener ){
        if( listener == null )
            throw new NullPointerException( "listener must not be null" );
        dockableSelectionListeners.add( listener );
    }
    
    /**
     * Removes a listener from this controller.
     * @param listener the listener to remove
     */
    public void removeDockableSelectionListener( DockableSelectionListener listener ){
        dockableSelectionListeners.remove( listener );
    }
    
    /**
     * Gets an array of currently registered {@link DockableSelectionListener}s.
     * @return the modifiable array
     */
    protected DockableSelectionListener[] dockableSelectionListeners(){
        return dockableSelectionListeners.toArray( new DockableSelectionListener[ dockableSelectionListeners.size() ] );
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
     * @param oldFocused the old owner of the focus, may be <code>null</code>
     * @param newFocused the owner of the focus, may be <code>null</code>
     */
    protected void fireDockableFocused( Dockable oldFocused, Dockable newFocused ){
        DockableFocusEvent event = new DockableFocusEvent( this, oldFocused, newFocused );
        
        for( DockableFocusListener listener : dockableFocusListeners() )
            listener.dockableFocused( event );
    }
    
    /**
     * Informs all listeners that <code>dockable</code> has been selected
     * by <code>station</code>.
     * @param station some {@link DockStation}
     * @param oldSelected the element which was selected earlier
     * @param newSelected the selected element of <code>station</code>
     */
    protected void fireDockableSelected( DockStation station, Dockable oldSelected, Dockable newSelected){
        DockableSelectionEvent event = new DockableSelectionEvent( this, station, oldSelected, newSelected );
        
        for( DockableSelectionListener listener : dockableSelectionListeners() )
            listener.dockableSelected( event );
    }

    /**
     * Adds an {@link UIListener} to this controller, the listener gets
     * notified when the graphical user interface needs an update because
     * the {@link LookAndFeel} changed.
     * @param listener the new listener
     */
    public void addUIListener( UIListener listener ){
        uiListeners.add( listener );
    }
    
    /**
     * Removes a listener from this controller.
     * @param listener the listener to remove
     */
    public void removeUIListener( UIListener listener ){
        uiListeners.remove( listener );
    }
    
    /**
     * Gets all the available {@link UIListener}s.
     * @return the list of listeners
     */
    protected UIListener[] uiListeners(){
    	return uiListeners.toArray( new UIListener[ uiListeners.size() ]);
    }
    
    /**
     * Informs all registered {@link UIListener}s that the user interface
     * needs an update because the {@link LookAndFeel} changed.
     * @see #addUIListener(UIListener)
     * @see #removeUIListener(UIListener)
     */
    public void updateUI(){
        for( UIListener listener : uiListeners() )
            listener.updateUI( this );
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
            public void dockableSelected( DockStation station, Dockable oldSelected, Dockable newSelected ) {
                fireDockableSelected( station, oldSelected, newSelected );
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
            addRepresentative( title );
        }
        
        public void titleUnbound( DockController controller, DockTitle title, Dockable dockable ) {
            removeRepresentative( title );
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
        	addRepresentative( dockable );
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
            removeRepresentative( dockable );
        	
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
