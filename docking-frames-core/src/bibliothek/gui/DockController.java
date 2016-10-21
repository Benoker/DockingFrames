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
import java.awt.Window;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.Set;

import javax.swing.Icon;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;

import bibliothek.gui.dock.DockElement;
import bibliothek.gui.dock.DockElementRepresentative;
import bibliothek.gui.dock.DockHierarchyLock;
import bibliothek.gui.dock.SplitDockStation;
import bibliothek.gui.dock.accept.DockAcceptance;
import bibliothek.gui.dock.accept.MultiDockAcceptance;
import bibliothek.gui.dock.action.ActionGuard;
import bibliothek.gui.dock.action.ActionOffer;
import bibliothek.gui.dock.action.ActionPopupSuppressor;
import bibliothek.gui.dock.action.DockAction;
import bibliothek.gui.dock.action.DockActionSource;
import bibliothek.gui.dock.action.popup.ActionPopupMenuFactory;
import bibliothek.gui.dock.action.view.ActionViewConverter;
import bibliothek.gui.dock.component.DockComponentManager;
import bibliothek.gui.dock.component.DockComponentRoot;
import bibliothek.gui.dock.control.ComponentHierarchyObserver;
import bibliothek.gui.dock.control.ControllerSetupCollection;
import bibliothek.gui.dock.control.DefaultDockControllerFactory;
import bibliothek.gui.dock.control.DockControllerFactory;
import bibliothek.gui.dock.control.DockRegister;
import bibliothek.gui.dock.control.DockRelocator;
import bibliothek.gui.dock.control.DockRelocatorMode;
import bibliothek.gui.dock.control.DockableSelector;
import bibliothek.gui.dock.control.DoubleClickController;
import bibliothek.gui.dock.control.GlobalMouseDispatcher;
import bibliothek.gui.dock.control.KeyboardController;
import bibliothek.gui.dock.control.PopupController;
import bibliothek.gui.dock.control.SingleParentRemover;
import bibliothek.gui.dock.control.focus.DefaultFocusRequest;
import bibliothek.gui.dock.control.focus.FocusController;
import bibliothek.gui.dock.control.focus.FocusHistory;
import bibliothek.gui.dock.control.focus.FocusRequest;
import bibliothek.gui.dock.control.focus.MouseFocusObserver;
import bibliothek.gui.dock.event.ControllerSetupListener;
import bibliothek.gui.dock.event.DockControllerRepresentativeListener;
import bibliothek.gui.dock.event.DockRegisterAdapter;
import bibliothek.gui.dock.event.DockRegisterListener;
import bibliothek.gui.dock.event.DockStationAdapter;
import bibliothek.gui.dock.event.DockStationListener;
import bibliothek.gui.dock.event.DockTitleBindingListener;
import bibliothek.gui.dock.event.DockableAdapter;
import bibliothek.gui.dock.event.DockableFocusEvent;
import bibliothek.gui.dock.event.DockableFocusListener;
import bibliothek.gui.dock.event.DockableListener;
import bibliothek.gui.dock.event.DockableSelectionEvent;
import bibliothek.gui.dock.event.DockableSelectionListener;
import bibliothek.gui.dock.themes.ThemeManager;
import bibliothek.gui.dock.title.ActivityDockTitleEvent;
import bibliothek.gui.dock.title.DockTitle;
import bibliothek.gui.dock.title.DockTitleManager;
import bibliothek.gui.dock.util.CoreWarningDialog;
import bibliothek.gui.dock.util.DirectWindowProvider;
import bibliothek.gui.dock.util.DockProperties;
import bibliothek.gui.dock.util.DockUtilities;
import bibliothek.gui.dock.util.IconManager;
import bibliothek.gui.dock.util.Priority;
import bibliothek.gui.dock.util.PropertyKey;
import bibliothek.gui.dock.util.TextManager;
import bibliothek.gui.dock.util.UIScheme;
import bibliothek.gui.dock.util.WindowProvider;
import bibliothek.gui.dock.util.WindowProviderListener;
import bibliothek.gui.dock.util.WindowProviderWrapper;
import bibliothek.gui.dock.util.color.ColorManager;
import bibliothek.gui.dock.util.extension.ExtensionManager;
import bibliothek.gui.dock.util.font.FontManager;
import bibliothek.gui.dock.util.icon.DefaultIconScheme;
import bibliothek.gui.dock.util.icon.DockIcon;
import bibliothek.gui.dock.util.icon.DockIconBridge;
import bibliothek.gui.dock.util.property.ConstantPropertyFactory;
import bibliothek.gui.dock.util.text.DefaultTextScheme;
import bibliothek.gui.dock.util.text.TextBridge;
import bibliothek.gui.dock.util.text.TextValue;
import bibliothek.util.Todo;
import bibliothek.util.Todo.Compatibility;
import bibliothek.util.Todo.Version;
import bibliothek.util.Workarounds;

/**
 * A controller connects all the {@link DockStation}s, {@link Dockable}s and
 * other objects that play together in this framework. This class also serves
 * as low-level access point for clients. When using this framework in general, 
 * or {@link DockController} in particular, several rules have
 * to be obeyed:
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
 *  and later {@link #meltLayout()} to temporarily disable automatic actions (like
 *  the fact that a <code>DockStation</code> with only one child gets removed).</li>
 *  <li>If a <code>DockController</code> is no longer needed then the method
 *  {@link #kill()} should be called. This method will ensure that the
 *  object can be reclaimed by the garbage collector. </li>
 * </ul>
 * 
 * @author Benjamin Sigg
 */
public class DockController {
	/** property telling whether this application runs in a restricted environment or not, the default value is the result of {@link DockUI#isSecureEnvironment()} */
	public static final PropertyKey<Boolean> RESTRICTED_ENVIRONMENT = new PropertyKey<Boolean>( "dock.restricted_environment", new ConstantPropertyFactory<Boolean>( DockUI.getDefaultDockUI().isSecureEnvironment() ), true );
	
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
    
    /** Listeners observing the selected {@link Dockable}s */
    private List<DockableSelectionListener> dockableSelectionListeners = new ArrayList<DockableSelectionListener>();
    /** Listeners observing the bound-state of {@link DockTitle}s */
    private List<DockTitleBindingListener> dockTitleBindingListeners = new ArrayList<DockTitleBindingListener>();
    
    /** a special controller listening to AWT-events and changing the focused dockable */
    private MouseFocusObserver focusObserver;
    
    /** central collection of {@link MouseEvent}s */
    private GlobalMouseDispatcher mouseDispatcher;
    
    /** class managing focus transfer between {@link Dockable}s */
    private FocusController focusController;
    
    /** class telling the order in which {@link Dockable}s had the focus */
    private FocusHistory focusHistory;
    
    /** an observer of the bound {@link DockTitle}s */
    private DockTitleObserver dockTitleObserver = new DockTitleObserver();
    /** mapping tells which titles are currently active */
    private Map<DockTitle, Dockable> activeTitles = new HashMap<DockTitle, Dockable>();
    /** a source for {@link DockTitle} */
    private DockTitleManager dockTitles;
    
    /** keeps track of all the {@link DockComponentRoot}s in the realm of this controller */
    private DockComponentManager dockComponentManager;
    
    /** the set of icons used with this controller */
    private IconManager icons;
    /** the set of strings used by this controller */
    private TextManager texts;
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
    
    /** controls the popup menus */
    private PopupController popupController;
   
    /** remover of stations with none or one child */
    private SingleParentRemover remover;
    
    /** a theme describing the look of the stations */
    private ThemeManager theme;
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
    
    /** ensurance against concurrent modifications */
    private DockHierarchyLock lock = new DockHierarchyLock();
    
    /** whether {@link #showCoreWarning()} actually opens a dialog */
    private static boolean showCoreWarning = true;
    
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
    	showCoreWarning();
    }
    
    /**
     * Disables the dialog warning from using the Core API, that pops up when creating a {@link DockController}.
     * @deprecated it really is not a good idea using the Core API, then there is the Common API
     */
    @Deprecated
    public static void disableCoreWarning(){
    	showCoreWarning = false;
    }
    
    /**
     * Opens an annoying dialog warning the developer that he is using the Core API, when he should be using
     * the Common API. This warning can be disabled by calling {@link #disableCoreWarning()}. 
     */
    protected void showCoreWarning(){
    	if( showCoreWarning ){
	    	EventQueue.invokeLater( new Runnable(){
				public void run(){
					CoreWarningDialog.showDialog();
				}
			});
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
        
        extensions = factory.createExtensionManager( this, setup );
        
        properties = new DockProperties( this );
        theme = new ThemeManager( this );
        icons = new IconManager( this );
        icons.setScheme( Priority.DEFAULT, createDefaultIconScheme() );
        colors = new ColorManager( this );
        fonts = new FontManager( this );
        dockTitles = new DockTitleManager( this );
        texts = new TextManager( this );
        texts.setScheme( Priority.DEFAULT, createDefaultTextScheme() );
        
        theme.init();
        
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
    	DockRegisterListener focus = factory.createVisibilityFocusObserver( this, setup );
    	if( focus != null )
    		register.addDockRegisterListener( focus );
    	
    	popupController = factory.createPopupController( this, setup );
    	
    	DockRegisterListener binder = factory.createActionBinder( this, setup );
    	if( binder != null )
    	    register.addDockRegisterListener( binder );
    	
		register.addDockRegisterListener( dockTitleObserver );
		addDockTitleBindingListener( dockTitleObserver );
		register.addDockRegisterListener( new DockableSelectionObserver() );
		
        relocator = factory.createRelocator( this, setup );
        
        defaultActionOffer = factory.createDefaultActionOffer( this, setup );
        focusObserver = factory.createMouseFocusObserver( this, setup );
        focusController = factory.createFocusController( this, setup );
        focusHistory = factory.createFocusHistory( this, setup );
        actionViewConverter = factory.createActionViewConverter( this, setup );
        doubleClickController = factory.createDoubleClickController( this, setup );
        keyboardController = factory.createKeyboardController( this, setup );
        dockableSelector = factory.createDockableSelector( this, setup );
        mouseDispatcher = factory.createGlobalMouseDispatcher( this, setup );
        dockComponentManager = factory.createDockComponentManager( this, setup );
        
        extensions.init();
        
        setTheme( DockUI.getDefaultDockUI().getDefaultTheme().create( this ) );
        
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
        focusController.addDockableFocusListener( new FocusControllerObserver() );
        
        for( ControllerSetupListener listener : setupListeners )
        	listener.done( this );
        
        Workarounds.getDefault().setup( this );
    }
    
    /**
     * Creates the default {@link UIScheme} for the {@link IconManager}.
     * @return the default {@link UIScheme}, should not be <code>null</code>
     */
    protected UIScheme<Icon, DockIcon, DockIconBridge> createDefaultIconScheme(){
    	DefaultIconScheme scheme = new DefaultIconScheme( "data/bibliothek/gui/dock/core/icons.ini", this );
    	scheme.link( PropertyKey.DOCKABLE_ICON, "dockable.default" );
    	scheme.link( PropertyKey.DOCK_STATION_ICON, "dockStation.default" );
    	return scheme;
    }
    
    /**
     * Creates the default {@link UIScheme} for the {@link TextManager}.
     * @return the default {@link UIScheme}, should not be <code>null</code>
     */
    protected UIScheme<String, TextValue, TextBridge> createDefaultTextScheme(){
    	ResourceBundle bundle = ResourceBundle.getBundle( "data.bibliothek.gui.dock.core.locale.text", Locale.getDefault(), 
    			DockController.class.getClassLoader() );
    	
    	List<ResourceBundle> list = texts.loadExtensionBundles( Locale.getDefault() );
    	
    	ResourceBundle[] bundles = list.toArray( new ResourceBundle[ list.size()+1] );
    	bundles[ bundles.length-1 ] = bundle;
    	
    	return new DefaultTextScheme( bundles );
    }
    
    /**
     * Removes listeners and frees resources. This method should be called
     * if this controller is no longer needed. This method should be called
     * only once.
     */
    public void kill(){
    	setRootWindowProvider( null );
    	focusObserver.kill();
	    register.kill();
	    keyboardController.kill();
	    theme.kill();
	    extensions.kill();
	    mouseDispatcher.kill();
    }
    
    /**
     * Tells this controller whether this application runs in a restricted environment or not. Calling this
     * method is equivalent of setting the property {@link #RESTRICTED_ENVIRONMENT}.<br>
     * Please note that setting this property to <code>false</code> in a restricted environment will lead
     * to {@link SecurityException}s and ultimately to unspecified behavior.
     * @param restricted whether restricted algorithms have to be used
     */
    public void setRestrictedEnvironment( boolean restricted ){
    	getProperties().set( RESTRICTED_ENVIRONMENT, restricted );
    }
    
    /**
     * Tells whether this controller uses restricted algorithms for a restricted environment.
     * @return whether restricted algorithms have to be used
     */
    public boolean isRestrictedEnvironment(){
    	return getProperties().get( RESTRICTED_ENVIRONMENT );
    }
    
    /**
     * Gets the current focus manager that tracks the mouse.
     * @return the controller
     */
    public MouseFocusObserver getMouseFocusObserver() {
        return focusObserver;
    }
    
    /**
     * Gets the manager which is responsible for transferring focus between {@link Dockable}s.
     * @return the manager, not <code>null</code>
     */
    public FocusController getFocusController(){
		return focusController;
	}
    
    /**
     * Gets the history of the focused {@link Dockable}s.
     * @return the history, not <code>null</code>
     */
    public FocusHistory getFocusHistory(){
		return focusHistory;
	}
    
    /**
     * Grants access to the {@link GlobalMouseDispatcher} which is responsible for collecting and
     * distributing global {@link MouseEvent}s. Clients may use the dispatcher to listen for
     * {@link MouseEvent}s.
     * @return the dispatcher, not <code>null</code>
     */
    public GlobalMouseDispatcher getGlobalMouseDispatcher(){
		return mouseDispatcher;
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
     * Gets a lock that prevents concurrent modification of the child-parent relationship
     * of {@link Dockable}s and {@link DockStation}s. This lock should only be acquired by
     * {@link DockStation}s.
     * @return the lock
     */
    public DockHierarchyLock getHierarchyLock(){
		return lock;
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
    	return popupController.getPopupSuppressor();
    }
    
    /**
     * Sets the guard which decides, which popups with {@link DockAction DockActions}
     * are allowed to show up, and which popups will be suppressed.
     * @param popupSuppressor the guard
     */
    public void setPopupSuppressor( ActionPopupSuppressor popupSuppressor ) {
    	popupController.setPopupSuppressor( popupSuppressor );
    }
    
    /**
     * Gets the factory which creates new popup menus.
     * @return the factory for creating popup menus, never <code>null</code>
     */
    public ActionPopupMenuFactory getPopupMenuFactory(){
    	return popupController.getPopupMenuFactory();
    }
    
    /**
     * Sets the factory which creates new popup menus.
     * @param factory the factory, not <code>null</code>
     */
    public void setPopupMenuFactory( ActionPopupMenuFactory factory ){
    	popupController.setPopupMenuFactory( factory );
    }
    
    /**
     * Gets the {@link PopupController} which is responsible for managing the
     * popup menus.
     * @return the controller, never <code>null</code>
     */
    public PopupController getPopupController(){
		return popupController;
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
    	this.theme.setTheme( theme );
	}
    
    /**
     * Gets the current theme of this controller.
     * @return the theme
     */
    public DockTheme getTheme() {
		return theme.getTheme();
	}
    
    /**
     * Gets the manager that is responsible for handling the current {@link DockTheme} and 
     * distributing its properties.
     * @return the manager
     */
    public ThemeManager getThemeManager(){
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
     * Gets a manager which keeps track of all the {@link DockComponentRoot}s, and hence of all the {@link Component}s
     * that are known to this {@link DockController}.
     * @return the manager, not <code>null</code>
     */
    public DockComponentManager getDockComponentManager() {
		return dockComponentManager;
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
        
        DockElementRepresentative old = componentToDockElements.put( representative.getComponent(), representative );
    	
    	if( old != null ){
    	    for( DockControllerRepresentativeListener listener : listeners ){
    	        listener.representativeRemoved( this, old );
    	    }
    	}
    	
    	for( DockControllerRepresentativeListener listener : listeners ){
    		listener.representativeAdded( this, representative );
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
     * Searches all registered {@link DockElementRepresentative} whose element is <code>element</code>.
     * @param element the element whose {@link DockElementRepresentative} are searched
     * @return the representatives, may include <code>element</code> as well
     */
    public DockElementRepresentative[] getRepresentatives( DockElement element ){
    	List<DockElementRepresentative> result = new ArrayList<DockElementRepresentative>();
    	for( DockElementRepresentative representative : componentToDockElements.values() ){
    		if( representative.getElement() == element ){
    			result.add( representative );
    		}
    	}
    	return result.toArray( new DockElementRepresentative[ result.size() ] );
    }
    
    /**
     * Adds a station to this controller. The controller allows the user to
     * drag and drop children from and to <code>station</code>. If
     * the children of <code>station</code> are stations itself, then
     * they will be added automatically. The station will be treated as root-station, meaning
     * that <code>station</code> remains registered until it is explicitly removed from the
     * {@link DockRegister}. On the other hand child stations may be removed automatically at any time.<br>
     * Even if <code>station</code> is already known to this controller or a child of a root-station, then
     * <code>station</code> is promoted to root-station.
     * @param station the new station
     */
    public void add( DockStation station ){
    	register.add( station );
    	register.setProtected( station, true );
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
     * running, or not. If the result is <code>true</code>, no-one should
     * change the focus.
     * @return <code>true</code> if the focus is currently changing
     */
    public boolean isOnFocusing() {
        return focusController.isOnFocusing();
    }
    
    /**
     * Sets the focused {@link Dockable}. Nothing happens if <code>focusedDockable</code>
     * is a station and one of its children already has the focus.
     * @param focusedDockable the element which should have the focus
     * @param component the {@link Component} which should receive the focus, can be <code>null</code>.
     * See {@link FocusController#setFocusedDockable(DockElementRepresentative, Component, boolean, boolean, boolean)}.
     * @see #isOnFocusing()
     */
    public void setAtLeastFocusedDockable( Dockable focusedDockable, Component component ) {
    	Dockable current = getFocusedDockable();
    	
        if( current == null ){
            setFocusedDockable( new DefaultFocusRequest( focusedDockable, component, false ) );
        }
        else if( !DockUtilities.isAncestor( focusedDockable, current )){
            setFocusedDockable( new DefaultFocusRequest( focusedDockable, component, false ) );
        }
    }
    
    /**
     * Sets the {@link Dockable} which should have the focus. This is identical of calling
     * {@link #setFocusedDockable(FocusRequest)} with a new {@link DefaultFocusRequest}.
     * @param focusedDockable the element with the focus or <code>null</code>
     * @param force <code>true</code> if this controller must ensure
     * that all properties are correct, <code>false</code> if some
     * optimizations are allowed. Clients normally can set this argument
     * to <code>false</code>.
     */
    public void setFocusedDockable( Dockable focusedDockable, boolean force ) {
    	setFocusedDockable( new DefaultFocusRequest( focusedDockable, force ) );
    }
    
    /**
     * Sets the {@link Dockable} which should have the focus.
     * @param focusedDockable the element with the focus or <code>null</code>
     * @param component the {@link Component} which should receive the focus, can be <code>null</code>.
     * See {@link FocusController#setFocusedDockable(DockElementRepresentative, Component, boolean, boolean, boolean)}.
     * @param force <code>true</code> if this controller must ensure
     * that all properties are correct, <code>false</code> if some
     * optimizations are allowed. Clients normally can set this argument
     * to <code>false</code>.
     * @deprecated clients should use {@link #setFocusedDockable(FocusRequest)} instead
     */
    @Deprecated
    @Todo( compatibility=Compatibility.BREAK_MAJOR, description="remove this method", priority=Todo.Priority.ENHANCEMENT,
    	target=Version.VERSION_1_1_3)
    public void setFocusedDockable( Dockable focusedDockable, Component component, boolean force ) {
    	setFocusedDockable( new DefaultFocusRequest( focusedDockable, component, force ) );
    }

    /**
     * Sets the {@link Dockable} which should have the focus.
     * @param focusedDockable the element with the focus or <code>null</code>
     * @param component the {@link Component} which should receive the focus, can be <code>null</code>.
     * See {@link FocusController#setFocusedDockable(DockElementRepresentative, Component, boolean, boolean, boolean)}.
     * @param force <code>true</code> if this controller must ensure
     * that all properties are correct, <code>false</code> if some
     * optimizations are allowed. Clients normally can set this argument
     * to <code>false</code>.
     * @param ensureFocusSet if <code>true</code>, then this method should make sure that either <code>focusedDockable</code>
     * itself or one of its {@link DockElementRepresentative} is the focus owner 
     * @param ensureDockableFocused  if <code>true</code>, then this method should make sure that <code>focusedDockable</code>
     * is the focus owner. This parameter is stronger that <code>ensureFocusSet</code>
     * @deprecated clients should use {@link #setFocusedDockable(FocusRequest)} instead
     */
    @Deprecated
    @Todo( compatibility=Compatibility.BREAK_MAJOR, description="remove this method", priority=Todo.Priority.ENHANCEMENT,
		target=Version.VERSION_1_1_3)
    public void setFocusedDockable( Dockable focusedDockable, Component component, boolean force, boolean ensureFocusSet, boolean ensureDockableFocused ) {
    	setFocusedDockable( new DefaultFocusRequest( focusedDockable, component, force, ensureFocusSet, ensureDockableFocused ) );
    }
    
    /**
     * Starts a request to set the focused {@link Dockable}.
     * @param request the request to execute, not <code>null</code>
     */
    public void setFocusedDockable( FocusRequest request ){
    	focusController.focus( request );
    }
    
    /**
     * Tells whether <code>dockable</code> or one of its children
     * has currently the focus.
     * @param dockable the element which may have the focus
     * @return <code>true</code> if <code>dockable</code> or
     * one of its children is focused
     */
    public boolean isFocused( Dockable dockable ){
        Dockable temp = getFocusedDockable();
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
    	focusController.ensureFocusSet( false );
    }

    /**
     * Gets the {@link Dockable} which is currently focused.
     * @return the focused element or <code>null</code>
     */
    public Dockable getFocusedDockable() {
        return focusController.getFocusedDockable();
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
     * Gets the set of strings which are used by this controller.
     * @return the set of texts
     */
    public TextManager getTexts(){
		return texts;
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
    	focusController.addDockableFocusListener( listener ); 
    }
    
    /**d
     * Removes a listener from this controller.
     * @param listener the listener to remove
     */
    public void removeDockableFocusListener( DockableFocusListener listener ){
        focusController.removeDockableFocusListener( listener );
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
     * Added to the current {@link FocusController} to track the active titles.
     */
    private class FocusControllerObserver implements DockableFocusListener{
		public void dockableFocused( DockableFocusEvent event ){
			for( Map.Entry<DockTitle, Dockable> title : activeTitles.entrySet() ){
                DockStation parent = title.getValue().getDockParent();
                if( parent != null )
                    parent.changed( title.getValue(), title.getKey(), false );
                else
                    title.getKey().changed( new ActivityDockTitleEvent( title.getValue(), false ));
            }
            
            activeTitles.clear();
            
            Dockable dockable = event.getNewFocusOwner();
            
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
    			handleAddedTitle( dockable, title );
            }
            
            @Override
            public void titleUnbound( Dockable dockable, DockTitle title ) {
            	titles.remove( title );
            	handleRemovedTitle( dockable, title );
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
                title.changed( new ActivityDockTitleEvent( dockable, false ));
        }
        
        private void handleAddedTitle( Dockable dockable, DockTitle title ){
            title.bind();
            fireTitleBound( title, dockable );
            
            DockStation station = dockable.getDockParent();
            boolean focused = false;
            Dockable temp = getFocusedDockable();
            while( !focused && temp != null ){
                focused = temp == dockable;
                DockStation parent = temp.getDockParent();
                temp = parent == null ? null : parent.asDockable();
            }
            
            if( station == null )
                title.changed( new ActivityDockTitleEvent( dockable, focused ));
            else
                station.changed( dockable, title, focused );
            
            if( focused )
                activeTitles.put( title, dockable );
        }
        
        private void handleRemovedTitle( Dockable dockable, DockTitle title ){
            title.unbind();
            fireTitleUnbound( title, dockable );
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
                	handleAddedTitle( dockable, title );
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
                	handleRemovedTitle( dockable, title );
                }
            }
        }
    }
}
