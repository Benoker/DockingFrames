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
package bibliothek.gui.dock.common;

import java.awt.Component;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.Set;

import javax.swing.JFrame;
import javax.swing.KeyStroke;

import bibliothek.extension.gui.dock.preference.PreferenceModel;
import bibliothek.extension.gui.dock.preference.PreferenceStorage;
import bibliothek.extension.gui.dock.theme.EclipseTheme;
import bibliothek.gui.DockController;
import bibliothek.gui.DockFrontend;
import bibliothek.gui.DockStation;
import bibliothek.gui.DockTheme;
import bibliothek.gui.Dockable;
import bibliothek.gui.dock.DockElement;
import bibliothek.gui.dock.DockFactory;
import bibliothek.gui.dock.FlapDockStation;
import bibliothek.gui.dock.ScreenDockStation;
import bibliothek.gui.dock.SplitDockStation;
import bibliothek.gui.dock.action.DockAction;
import bibliothek.gui.dock.common.action.predefined.CCloseAction;
import bibliothek.gui.dock.common.event.CControlListener;
import bibliothek.gui.dock.common.event.CDockablePropertyListener;
import bibliothek.gui.dock.common.event.CDockableStateListener;
import bibliothek.gui.dock.common.event.CDoubleClickListener;
import bibliothek.gui.dock.common.event.CFocusListener;
import bibliothek.gui.dock.common.event.CKeyboardListener;
import bibliothek.gui.dock.common.event.CVetoClosingListener;
import bibliothek.gui.dock.common.event.CVetoFocusListener;
import bibliothek.gui.dock.common.event.ResizeRequestListener;
import bibliothek.gui.dock.common.group.CGroupBehavior;
import bibliothek.gui.dock.common.intern.CControlAccess;
import bibliothek.gui.dock.common.intern.CControlFactory;
import bibliothek.gui.dock.common.intern.CDockFrontend;
import bibliothek.gui.dock.common.intern.CDockable;
import bibliothek.gui.dock.common.intern.CDockableAccess;
import bibliothek.gui.dock.common.intern.CListenerCollection;
import bibliothek.gui.dock.common.intern.CPlaceholderStrategy;
import bibliothek.gui.dock.common.intern.CSetting;
import bibliothek.gui.dock.common.intern.CommonDockable;
import bibliothek.gui.dock.common.intern.CommonMultipleDockableFactory;
import bibliothek.gui.dock.common.intern.CommonMultipleDockableLayout;
import bibliothek.gui.dock.common.intern.CommonSingleDockableFactory;
import bibliothek.gui.dock.common.intern.ControlVetoClosingListener;
import bibliothek.gui.dock.common.intern.ControlVetoFocusListener;
import bibliothek.gui.dock.common.intern.EfficientControlFactory;
import bibliothek.gui.dock.common.intern.MutableCControlRegister;
import bibliothek.gui.dock.common.intern.action.CActionOffer;
import bibliothek.gui.dock.common.intern.station.CFlapLayoutManager;
import bibliothek.gui.dock.common.intern.station.CLockedResizeLayoutManager;
import bibliothek.gui.dock.common.intern.ui.CSingleParentRemover;
import bibliothek.gui.dock.common.intern.ui.CommonSingleTabDecider;
import bibliothek.gui.dock.common.intern.ui.ExtendedModeAcceptance;
import bibliothek.gui.dock.common.intern.ui.StackableAcceptance;
import bibliothek.gui.dock.common.intern.ui.WorkingAreaAcceptance;
import bibliothek.gui.dock.common.layout.FullLockConflictResolver;
import bibliothek.gui.dock.common.layout.RequestDimension;
import bibliothek.gui.dock.common.mode.CLocationMode;
import bibliothek.gui.dock.common.mode.CLocationModeManager;
import bibliothek.gui.dock.common.mode.CMaximizedMode;
import bibliothek.gui.dock.common.mode.CMaximizedModeArea;
import bibliothek.gui.dock.common.mode.ExtendedMode;
import bibliothek.gui.dock.common.perspective.CControlPerspective;
import bibliothek.gui.dock.common.perspective.CStackPerspective;
import bibliothek.gui.dock.common.theme.ThemeMap;
import bibliothek.gui.dock.common.theme.eclipse.CommonEclipseThemeConnector;
import bibliothek.gui.dock.control.DockRegister;
import bibliothek.gui.dock.control.focus.DefaultFocusStrategy;
import bibliothek.gui.dock.displayer.SingleTabDecider;
import bibliothek.gui.dock.event.DockAdapter;
import bibliothek.gui.dock.event.DockableFocusEvent;
import bibliothek.gui.dock.event.DockableFocusListener;
import bibliothek.gui.dock.event.DoubleClickListener;
import bibliothek.gui.dock.event.KeyboardListener;
import bibliothek.gui.dock.facile.mode.LocationModeManager;
import bibliothek.gui.dock.facile.station.split.ConflictResolver;
import bibliothek.gui.dock.facile.station.split.DefaultConflictResolver;
import bibliothek.gui.dock.frontend.FrontendEntry;
import bibliothek.gui.dock.frontend.MissingDockableStrategy;
import bibliothek.gui.dock.layout.DockSituationIgnore;
import bibliothek.gui.dock.perspective.PerspectiveDockable;
import bibliothek.gui.dock.station.stack.StackDockPerspective;
import bibliothek.gui.dock.station.stack.StackDockStationFactory;
import bibliothek.gui.dock.station.stack.StackDockStationLayout;
import bibliothek.gui.dock.station.support.PlaceholderStrategy;
import bibliothek.gui.dock.support.util.ApplicationResource;
import bibliothek.gui.dock.support.util.ApplicationResourceManager;
import bibliothek.gui.dock.themes.ThemeFactory;
import bibliothek.gui.dock.util.DirectWindowProvider;
import bibliothek.gui.dock.util.NullWindowProvider;
import bibliothek.gui.dock.util.Priority;
import bibliothek.gui.dock.util.PropertyKey;
import bibliothek.gui.dock.util.WindowProvider;
import bibliothek.gui.dock.util.icon.DefaultIconScheme;
import bibliothek.gui.dock.util.property.ConstantPropertyFactory;
import bibliothek.gui.dock.util.text.DefaultTextScheme;
import bibliothek.util.Filter;
import bibliothek.util.Todo;
import bibliothek.util.Version;
import bibliothek.util.Todo.Compatibility;
import bibliothek.util.xml.XElement;
import bibliothek.util.xml.XException;
import bibliothek.util.xml.XIO;

/**
 * Manages the interaction between {@link SingleCDockable}, {@link MultipleCDockable}
 * and {@link CStation}s.<br>
 * Clients should call <code>read</code> and <code>write</code> of the
 * {@link ApplicationResourceManager}, accessible through {@link #getResources()}, 
 * to store or load the configuration.<br>
 * Clients which do no longer need a {@link CControl} can call {@link #destroy()}
 * to free resources.
 * @author Benjamin Sigg
 *
 */
public class CControl {
	/**
	 * A key for this {@link CControl}. Will be set with the highest priority. To be used
	 * wherever a {@link DockController} but not a {@link CControl} is accessible.
	 */
	public static final PropertyKey<CControl> CCONTROL = new PropertyKey<CControl>( "ccontrol" );
	
    /**
     * {@link KeyStroke} used to change a {@link CDockable} into maximized-state,
     * or to go out of maximized-state when needed.
     */
    public static final PropertyKey<KeyStroke> KEY_MAXIMIZE_CHANGE = 
        new PropertyKey<KeyStroke>( "ccontrol.maximize_change" );

    /**
     * {@link KeyStroke} used to change a {@link CDockable} into
     * maximized-state.
     */
    public static final PropertyKey<KeyStroke> KEY_GOTO_MAXIMIZED =
        new PropertyKey<KeyStroke>( "ccontrol.goto_maximized" );

    /**
     * {@link KeyStroke} used to change a {@link CDockable} into
     * normalized-state.
     */
    public static final PropertyKey<KeyStroke> KEY_GOTO_NORMALIZED =
        new PropertyKey<KeyStroke>( "ccontrol.goto_normalized" );

    /**
     * {@link KeyStroke} used to change a {@link CDockable} into
     * minimized-state.
     */
    public static final PropertyKey<KeyStroke> KEY_GOTO_MINIMIZED =
        new PropertyKey<KeyStroke>( "ccontrol.goto_minimized" );

    /**
     * {@link KeyStroke} used to change a {@link CDockable} into
     * externalized-state.
     */
    public static final PropertyKey<KeyStroke> KEY_GOTO_EXTERNALIZED =
        new PropertyKey<KeyStroke>( "ccontrol.goto_externalized" );

    /**
     * {@link KeyStroke} used to close a {@link CDockable}.
     */
    public static final PropertyKey<KeyStroke> KEY_CLOSE = 
        new PropertyKey<KeyStroke>( "ccontrol.close" );

    /**
     * {@link ConflictResolver} used to determine what happens when there is
     * a conflict between two resize requests on a {@link SplitDockStation} like
     * {@link CGridArea}, {@link CWorkingArea} or {@link CContentArea}. 
     * @see DefaultConflictResolver
     * @see FullLockConflictResolver
     */
    public static final PropertyKey<ConflictResolver<RequestDimension>> RESIZE_LOCK_CONFLICT_RESOLVER =
        new PropertyKey<ConflictResolver<RequestDimension>>( 
                "ccontrol.resize_lock_conflict_resolver", 
                new ConstantPropertyFactory<ConflictResolver<RequestDimension>>( new DefaultConflictResolver<RequestDimension>()), true );

    /** the unique id of the station that handles the externalized dockables */
    public static final String EXTERNALIZED_STATION_ID = "external";

    /** the unique id of the default-{@link CContentArea} created by this control */
    public static final String CONTENT_AREA_STATIONS_ID = "ccontrol";

    /** connection to the real DockingFrames */
    private CDockFrontend frontend;

    /** strategy what to do when reading layout information of a missing dockable */
    private MissingCDockableStrategy missingStrategy = MissingCDockableStrategy.PURGE;

    /** access to internal methods of some {@link CDockable}s */
    private Map<CDockable, CDockableAccess> accesses = new HashMap<CDockable, CDockableAccess>();

    /** a manager allowing the user to change the extended-state of some {@link CDockable}s */
    private CLocationModeManager locationManager;

    /** the default location of newly opened {@link CDockable}s */
    private CLocation defaultLocation;

    /** a list of available {@link DockTheme}s */
    private ThemeMap themes;

    /** Access to the internal methods of this control */
    private CControlAccess access = new Access();

    /** manager used to store and read configurations */
    private ApplicationResourceManager resources = new ApplicationResourceManager();

    /** a list of listeners which are to be informed when this control is no longer in use */
    private List<DestroyHook> hooks = new ArrayList<DestroyHook>();

    /** factory used to create new elements for this control */
    private CControlFactory factory;

    /** the {@link CDockable}s and other elements used by this control */
    private MutableCControlRegister register;

    /** the list of listeners to this {@link CControl} */
    private List<CControlListener> listeners = new ArrayList<CControlListener>();

    /** the list of resize-listeners */
    private List<ResizeRequestListener> resizeListeners = new ArrayList<ResizeRequestListener>();

    /** the collection of global listeners */
    private CListenerCollection listenerCollection = new CListenerCollection();

    /** the preferences used by this instance of {@link CControl} */
    private PreferenceStorage preferences = new PreferenceStorage();

    /** the model which is used to translate between {@link #preferences} and <code>this</code> */
    private PreferenceModel preferenceModel;

    /**
     * Creates a new control. Note that a control should know the main
     * window of the application, thus {@link CControl#CControl(WindowProvider)}
     * would be the better choice than this constructor.
     */
    public CControl(){
        this( new NullWindowProvider(), false );
    }

    /**
     * Creates a new control
     * @param frame the main frame of the application, needed to create
     * dialogs for externalized {@link CDockable}s
     */
    public CControl( JFrame frame ){
        this( frame, false );
    }

    /**
     * Creates a new control
     * @param restrictedEnvironment whether this application runs in a
     * restricted environment and is not allowed to listen for global events.
     */
    public CControl( boolean restrictedEnvironment ){
        this( new NullWindowProvider(), restrictedEnvironment );
    }

    /**
     * Creates a new control
     * @param window a provider for the main window of this application. Needed
     * to create dialogs for externalized {@link CDockable}s. Must not be <code>null</code>, but
     * its search method may return <code>null</code>
     */
    public CControl( WindowProvider window ){
        this( window, false );
    }

    /**
     * Creates a new control
     * @param frame the main frame of the application, needed to create
     * dialogs for externalized {@link CDockable}s
     * @param restrictedEnvironment whether this application runs in a
     * restricted environment and is not allowed to listen for global events.
     */
    public CControl( JFrame frame, boolean restrictedEnvironment ){
        this( frame == null ? new NullWindowProvider() : new DirectWindowProvider( frame ), restrictedEnvironment );
    }

    /**
     * Creates a new control
     * @param window a provider for the main window of this application. Needed
     * to create dialogs for externalized {@link CDockable}s. Must not be <code>null</code>, but
     * its search method may return <code>null</code>
     * @param restrictedEnvironment whether this application runs in a
     * restricted environment and is not allowed to listen for global events.
     */
    public CControl( WindowProvider window, boolean restrictedEnvironment ){
        this( window, new EfficientControlFactory() );
        getController().setRestrictedEnvironment( restrictedEnvironment );
    }


    /**
     * Creates a new control
     * @param frame the main frame of the application, needed to create
     * dialogs for externalized {@link CDockable}s
     * @param factory a factory which is used to create new elements for this
     * control.
     */
    public CControl( JFrame frame, CControlFactory factory ){
        this( frame == null ? new NullWindowProvider() : new DirectWindowProvider( frame ), factory );
    }

    /**
     * Creates a new control
     * @param window a provider for the main window of this application. Needed
     * to create dialogs for externalized {@link CDockable}s. Must not be <code>null</code>, but
     * its search method may return <code>null</code>
     * @param factory a factory which is used to create new elements for this
     * control.
     */
    public CControl( WindowProvider window, CControlFactory factory ){
    	this( window, factory, true );
    }
    
    /**
     * Creates a new control
     * @param window a provider for the main window of this application. Needed
     * to create dialogs for externalized {@link CDockable}s. Must not be <code>null</code>, but
     * its search method may return <code>null</code>
     * @param factory a factory which is used to create new elements for this
     * control.
     * @param init if <code>true</code> then this constructor calls {@link #init(WindowProvider, CControlFactory)},
     * otherwise this constructor does nothing and returns immediately. Subclasses should call
     * {@link #init(WindowProvider, CControlFactory)} in that case.
     */
    protected CControl( WindowProvider window, CControlFactory factory, boolean init ){
    	if( init ){
    		init( window, factory );
    	}
    }

    /**
     * Initializes the fields of this {@link CControl}. This method is called during construction
     * of this {@link CControl}. Subclasses may use {@link #CControl(WindowProvider, CControlFactory, boolean)}
     * to create an uninitialized {@link CControl} and then call this method by themselves. 
     * @param window a provider for the main window of this application. Needed
     * to create dialogs for externalized {@link CDockable}s. Must not be <code>null</code>, but
     * its search method may return <code>null</code>
     * @param factory a factory which is used to create new elements for this
     * control.
     */
    protected void init( WindowProvider window, CControlFactory factory ){
        if( window == null ){
            throw new IllegalArgumentException( "window must not be null, however its search method may return null" );
        }

        this.factory = factory;
        
        register = factory.createRegister( this );
        DockController controller = factory.createController( this );
        controller.getProperties().set( CCONTROL, this, Priority.CLIENT );
        controller.getProperties().finalize( CCONTROL );
        controller.setSingleParentRemover( new CSingleParentRemover( this ) );

        initFocusListeners( controller );
        initInputListener( controller );

        frontend = factory.createFrontend( access, controller );
        frontend.setOwner( window );

        frontend.setMissingDockableStrategy( new MissingDockableStrategy(){
            public boolean shouldStoreHidden( String key ) {
                return shouldStore( key );
            }
            public boolean shouldStoreShown( String key ) {
                return shouldStore( key );
            }
            public <L> boolean shouldCreate( DockFactory<?,?,L> factory, L data ) {
                if( factory instanceof CommonMultipleDockableFactory && data instanceof CommonMultipleDockableLayout ){
                    return CControl.this.shouldCreate( 
                            ((CommonMultipleDockableFactory)factory).getFactory(), 
                            (CommonMultipleDockableLayout)data );
                }
                return false;
            }
        });

        setIgnoreWorkingForEntry( true );
        frontend.setShowHideAction( false );

        frontend.getController().addActionOffer( new CActionOffer( this ) );
        
        frontend.getController().getRegister().addDockRegisterListener( new DockAdapter(){
            @Override
            public void dockableRegistered( DockController controller, Dockable dockable ) {
                if( dockable instanceof CommonDockable ){
                    CDockable cdock = ((CommonDockable)dockable).getDockable();
                    CDockableAccess access = accesses.get( cdock );
                    if( access != null ){
                        access.informVisibility( true );
                    }

                    for( CControlListener listener : listeners() )
                        listener.opened( CControl.this, cdock );
                }
            }

            @Override
            public void dockableUnregistered( DockController controller, Dockable dockable ) {
                if( dockable instanceof CommonDockable ){
                    CDockable cdock = ((CommonDockable)dockable).getDockable();
                    CDockableAccess access = accesses.get( cdock );
                    if( access != null ){
                        access.informVisibility( false );
                    }

                    for( CControlListener listener : listeners() )
                        listener.closed( CControl.this, cdock );

                    if( cdock instanceof MultipleCDockable ){
                        MultipleCDockable multiple = (MultipleCDockable)cdock;
                        if( multiple.isRemoveOnClose() ){
                            remove( multiple );
                        }
                    }
                }
            }
        });
        
        frontend.getController().getFocusController().addVetoListener( new ControlVetoFocusListener( this, listenerCollection.getVetoFocusListener() ) );
        frontend.getController().getFocusController().setStrategy( new DefaultFocusStrategy( frontend.getController() ){
			public Component getFocusComponent( Dockable dockable, Component mouseClicked ){
				if( mouseClicked != null ){
					if( mouseClicked.isFocusable() || focusable( mouseClicked )){
						return mouseClicked;
					}
				}
				
				if( dockable instanceof CommonDockable ){
					Component result = ((CommonDockable)dockable).getDockable().getFocusComponent();
					if( result != null ){
						return result;
					}
				}
				return super.getFocusComponent( dockable, null );
			}
		});
        
        frontend.addVetoableListener( new ControlVetoClosingListener( this, listenerCollection.getVetoClosingListener() ) );

        frontend.getController().addAcceptance( new StackableAcceptance() );
        frontend.getController().addAcceptance( new WorkingAreaAcceptance( access ) );
        frontend.getController().addAcceptance( new ExtendedModeAcceptance( access ) );

        initFactories();
        
        themes = new ThemeMap( this );

        initPersistentStorage();

        initExtendedModes();
        initProperties();
        initIcons();
        initTexts();

        setTheme( ThemeMap.KEY_SMOOTH_THEME );
    }

    /**
     * Creates and adds the listeners needed to track the focus.
     * @param controller the controller which will be observed
     */
    private void initFocusListeners( DockController controller ){
        controller.addDockableFocusListener( new DockableFocusListener(){
            public void dockableFocused( DockableFocusEvent event ) {
                Dockable oldFocused = event.getOldFocusOwner();
                Dockable newFocused = event.getNewFocusOwner();

                if( oldFocused != null && oldFocused instanceof CommonDockable ){
                    CDockable oldC = ((CommonDockable)oldFocused).getDockable();
                    CDockableAccess access = accesses.get( oldC );
                    if( access != null ){
                        access.getFocusListener().focusLost( oldC );
                    }

                    listenerCollection.getFocusListener().focusLost( oldC );
                }
                if( newFocused != null && newFocused instanceof CommonDockable ){
                    CDockable newC = ((CommonDockable)newFocused).getDockable();
                    CDockableAccess access = accesses.get( newC );
                    if( access != null ){
                        access.getFocusListener().focusGained( newC );
                    }

                    listenerCollection.getFocusListener().focusGained( newC );
                }
            }
        });
    }

    private void initInputListener( DockController controller ){
        controller.getKeyboardController().addListener( new KeyboardListener(){
            public boolean keyPressed( DockElement element, KeyEvent event ) {
                if( element instanceof CommonDockable ){
                    CDockable source = ((CommonDockable)element).getDockable();
                    CDockableAccess access = accesses.get( source );
                    if( access != null ){
                        if( access.getKeyboardListener().keyPressed( source, event ))
                            return true;
                    }
                    return listenerCollection.getKeyboardListener().keyPressed( source, event );
                }
                return false;
            }

            public boolean keyReleased( DockElement element, KeyEvent event ) {
                if( element instanceof CommonDockable ){
                    CDockable source = ((CommonDockable)element).getDockable();
                    CDockableAccess access = accesses.get( source );
                    if( access != null ){
                        if( access.getKeyboardListener().keyReleased( source, event ))
                            return true;
                    }
                    return listenerCollection.getKeyboardListener().keyReleased( source, event );
                }
                return false;
            }

            public boolean keyTyped( DockElement element, KeyEvent event ) {
                if( element instanceof CommonDockable ){
                    CDockable source = ((CommonDockable)element).getDockable();
                    CDockableAccess access = accesses.get( source );
                    if( access != null ){
                        if( access.getKeyboardListener().keyTyped( source, event ))
                            return true;
                    }
                    return listenerCollection.getKeyboardListener().keyTyped( source, event );
                }
                return false;
            }

            public DockElement getTreeLocation() {
                return null;
            }	        
        });

        controller.getDoubleClickController().addListener( new DoubleClickListener(){
            public boolean process( Dockable dockable, MouseEvent event ) {
                if( dockable instanceof CommonDockable ){
                    CDockable source = ((CommonDockable)dockable).getDockable();
                    CDockableAccess access = accesses.get( source );
                    if( access != null ){
                        if( access.getDoubleClickListener().clicked( source, event ))
                            return true;
                    }
                    return listenerCollection.getDoubleClickListener().clicked( source, event );
                }
                return false;                
            }

            public DockElement getTreeLocation() {
                return null;
            }
        });
    }

    /**
     * Sets up the {@link #locationManager}.
     */
    private void initExtendedModes(){
    	locationManager = new CLocationModeManager( access );
    	initExternalizeArea();
    }
    
    /**
     * Called during construction of this {@link CControl}, this method creates a new 
     * {@link CExternalizeArea} and registers it as root-station using the unique identifier
     * {@value #EXTERNALIZED_STATION_ID}.<br>
     * Subclasses may override this method and not create a {@link CExternalizeArea} or create
     * a customized {@link CExternalizeArea}.
     */
    protected void initExternalizeArea(){
    	addStation( new CExternalizeArea( this, EXTERNALIZED_STATION_ID ), true );
    }
    
    /**
     * Called during construction of this {@link CControl}, this method adds {@link DockFactory}s
     * to the {@link #intern() intern representation} of this {@link CControl}.
     */
    protected void initFactories(){
        CommonSingleDockableFactory backupFactory = register.getBackupFactory();
        frontend.registerFactory( backupFactory );
        frontend.registerBackupFactory( backupFactory );
        frontend.registerFactory( new StackDockStationFactory(){
        	@Override
        	public StackDockPerspective layoutPerspective( StackDockStationLayout layout, Map<Integer, PerspectiveDockable> children ){
        		CStackPerspective stack = new CStackPerspective();
        		layoutPerspective( stack, layout, children );
        		return stack;
        	}
        });
    }

    /**
     * Sets up the default properties. While subclasses can override this method, they should call
     * this method first. Some parts of this {@link CControl} will not work correctly if the wrong
     * properties are set or if no properties are set at all.
     */
    protected void initProperties(){
        putProperty( KEY_MAXIMIZE_CHANGE, KeyStroke.getKeyStroke( KeyEvent.VK_M, InputEvent.CTRL_MASK ) );
        putProperty( KEY_GOTO_EXTERNALIZED, KeyStroke.getKeyStroke( KeyEvent.VK_E, InputEvent.CTRL_MASK ) );
        putProperty( KEY_GOTO_NORMALIZED, KeyStroke.getKeyStroke( KeyEvent.VK_N, InputEvent.CTRL_MASK ) );
        putProperty( KEY_CLOSE, KeyStroke.getKeyStroke( KeyEvent.VK_F4, InputEvent.CTRL_MASK ) );
        putProperty( SplitDockStation.LAYOUT_MANAGER, new CLockedResizeLayoutManager( this ) );
        putProperty( FlapDockStation.LAYOUT_MANAGER, new CFlapLayoutManager() );
        putProperty( EclipseTheme.THEME_CONNECTOR, new CommonEclipseThemeConnector( this ) );
        putProperty( SingleTabDecider.SINGLE_TAB_DECIDER, new CommonSingleTabDecider( this ) );
        putProperty( PlaceholderStrategy.PLACEHOLDER_STRATEGY, new CPlaceholderStrategy( this ) );
    }
    
    /**
     * Sets up all the default icons used in the ralm of this {@link CControl}.
     */
    protected void initIcons(){
    	DefaultIconScheme scheme = new DefaultIconScheme( getController(),
    			new DefaultIconScheme.IconResource( "data/icons.ini", null, DockController.class.getClassLoader() ),
    			new DefaultIconScheme.IconResource( "data/bibliothek/gui/dock/icons/icons.ini", "data/bibliothek/gui/dock/icons/", CControl.class.getClassLoader() ));
    	scheme.link( PropertyKey.DOCKABLE_ICON, "dockable.default" );
    	scheme.link( PropertyKey.DOCK_STATION_ICON, "dockStation.default" );
    	getController().getIcons().setScheme( Priority.DEFAULT, scheme );
    }
    
    /**
     * Sets up all the default text that is used in the realm of this {@link CControl}
     */
    protected void initTexts(){
    	initTexts( Locale.getDefault() );
    }
    
    /**
     * Re-initializes the default text that is used in the realm of this {@link CControl}.
     * @param locale the new language, must not be <code>null</code>
     */
    public void setLanguage( Locale locale ){
    	initTexts( locale );
    }
    
    /**
     * Sets up all the default text that is used in the realm of this {@link CControl}
     * @param locale what language to use
     */
    protected void initTexts( Locale locale ){
    	ResourceBundle bundleCore = ResourceBundle.getBundle( "data.locale.text", locale, DockController.class.getClassLoader() );
    	ResourceBundle bundleCommon = ResourceBundle.getBundle( "data.bibliothek.gui.dock.locale.common", locale, CControl.class.getClassLoader() );
    	
    	getController().getTexts().setScheme( Priority.DEFAULT, new DefaultTextScheme( bundleCommon, bundleCore ) );
    }

    /**
     * Creates new {@link ApplicationResource}s and registers them at the
     * {@link #getResources() ApplicationResourceManager} of this {@link CControl}. While subclasses
     * can override this method, they should be aware that missing {@link ApplicationResource}s will
     * break persistent storage for the location and size of {@link Dockable}s.
     */
    protected void initPersistentStorage(){
        try{
            resources.put( "ccontrol.frontend", new ApplicationResource(){
                public void write( DataOutputStream out ) throws IOException {
                    Version.write( out, Version.VERSION_1_0_4 );
                    writeWorkingAreas( out );
                    frontend.write( out );
                }
                public void read( DataInputStream in ) throws IOException {
                    Version version = Version.read( in );
                    version.checkCurrent();
                    readWorkingAreas( in );
                    frontend.read( in );
                }
                public void writeXML( XElement element ) {
                    writeWorkingAreasXML( element.addElement( "areas" ) );
                    frontend.writeXML( element.addElement( "frontend" ) );
                }
                public void readXML( XElement element ) {
                    readWorkingAreasXML( element.getElement( "areas" ) );
                    frontend.readXML( element.getElement( "frontend" ) );
                }
            });

            resources.put( "ccontrol.preferences", new ApplicationResource(){
                public void read( DataInputStream in ) throws IOException {
                    Version version = Version.read( in );
                    version.checkCurrent();
                    preferences.read( in );

                    if( preferenceModel != null ){
                        preferences.load( preferenceModel, false );
                        preferenceModel.write();
                    }
                }

                public void readXML( XElement element ) {
                    preferences.readXML( element );

                    if( preferenceModel != null ){
                        preferences.load( preferenceModel, false );
                        preferenceModel.write();
                    }
                }

                public void write( DataOutputStream out ) throws IOException {
                    if( preferenceModel != null ){
                        preferenceModel.read();
                        preferences.store( preferenceModel );
                    }

                    Version.write( out, Version.VERSION_1_0_6 );
                    preferences.write( out );
                }

                public void writeXML( XElement element ) {
                    if( preferenceModel != null ){
                        preferenceModel.read();
                        preferences.store( preferenceModel );
                    }

                    preferences.writeXML( element );
                }

            });
        }
        catch( IOException ex ){
            System.err.println( "Non lethal IO-error:" );
            ex.printStackTrace();
        }
    }
    
    /**
     * Stores the mapping of {@link MultipleCDockableFactory} and 
     * {@link MultipleCDockable}s in <code>setting</code>.
     * @param setting the setting to fill
     */
    private void fillMultiFactories( CSetting setting ){
        for( String id : register.listMultipleDockableFactories() ){
            MultipleCDockableFactory<?, ?> factory = register.getFactory( id );
            List<MultipleCDockable> dockables = register.listMultipleDockables( factory );
            if( !dockables.isEmpty() ){
                List<String> ids = new ArrayList<String>( dockables.size() );
                for( MultipleCDockable dockable : dockables ){
                    String uniqueId = accesses.get( dockable ).getUniqueId();
                    ids.add( register.multiToNormalId( uniqueId ) );
                }
                setting.putMultipleFactoryDockables( id, ids );
            }
        }
    }

    /**
     * Adds a listener to this control.
     * @param listener the new listener
     */
    public void addControlListener( CControlListener listener ){
        if( listener == null )
            throw new IllegalArgumentException( "Listener must not be null" );
        listeners.add( listener );
    }

    /**
     * Removes a listener from this control.
     * @param listener the listener to remove
     */
    public void removeControlListener( CControlListener listener ){
        listeners.remove( listener );
    }

    /**
     * Adds a new focus listener to this control. The listener gets informed
     * about changes in the focus.
     * @param listener the new listener
     */
    public void addFocusListener( CFocusListener listener ){
        listenerCollection.addFocusListener( listener );
    }

    /**
     * Removes a listener from this control.
     * @param listener the listener to remove
     */
    public void removeFocusListener( CFocusListener listener ){
        listenerCollection.removeFocusListener( listener );
    }
    
    /**
     * Adds a new veto focus listener to this control. The listener gets
     * informed about pending changes in the focus.
     * @param listener the new listener
     */
    public void addVetoFocusListener( CVetoFocusListener listener ){
    	listenerCollection.addVetoFocusListener( listener );
    }
    
    /**
     * Removes a listener from this control.
     * @param listener the listener to remove
     */
    public void removeVetoFocusListener( CVetoFocusListener listener ){
    	listenerCollection.removeVetoFocusListener( listener );
    }

    /**
     * Adds a global state listener. This has the same effect as adding
     * a state listener to each {@link CDockable} that is known to this 
     * control.
     * @param listener the new listener
     */
    public void addStateListener( CDockableStateListener listener ){
        listenerCollection.addCDockableStateListener( listener );
    }

    /**
     * Removes a global state listener.
     * @param listener the listener to remove
     */
    public void removeStateListener( CDockableStateListener listener ){
        listenerCollection.removeCDockableStateListener( listener );
    }

    /**
     * Adds a global property listener. This has the same effect as adding
     * a property listener to each {@link CDockable} that is known to this
     * control.
     * @param listener the new listener
     */
    public void addPropertyListener( CDockablePropertyListener listener ){
        listenerCollection.addCDockablePropertyListener( listener );
    }

    /**
     * Removes a global listener from this control.
     * @param listener the listener to remove
     */
    public void removePropertyListener( CDockablePropertyListener listener ){
        listenerCollection.removeCDockablePropertyListener( listener );
    }

    /**
     * Adds a global keyboard listener to this control. The listener gets 
     * informed whenever a key is touched on a {@link Component} which is a child
     * of a {@link CDockable}.<br>
     * Note: listeners directly added to a {@link CDockable} will always
     * be informed first.<br>
     * Note: if a listener processes the event, then the other listeners will
     * not be informed.
     * @param listener the new listener
     */
    public void addKeyboardListener( CKeyboardListener listener ){
        listenerCollection.addKeyboardListener( listener );
    }

    /**
     * Removes a listener from this control.
     * @param listener the listener to remove
     */
    public void removeKeyboardListener( CKeyboardListener listener ){
        listenerCollection.removeKeyboardListener( listener );
    }

    /**
     * Adds a key listener to this control that will be informed about any
     * {@link KeyEvent} that gets processed or analyzed by this control. Especially
     * any event that gets forwarded to a {@link CKeyboardListener} gets also
     * forwarded to <code>listener</code>.
     * @param listener the new listener
     */
    public void addGlobalKeyListener( KeyListener listener ){
        intern().getController().getKeyboardController().addGlobalListener( listener );
    }

    /**
     * Removes a global {@link KeyListener} from this control.
     * @param listener the listener to remove
     */
    public void removeGlobalKeyListener( KeyListener listener ){
        intern().getController().getKeyboardController().removeGlobalListener( listener );
    }

    /**
     * Adds a global mouse double click listener to this control. The listener gets 
     * informed whenever the mouse is clicked twice on a {@link Component} which
     * is a child of a {@link CDockable}.<br>
     * Note: listeners directly added to a {@link CDockable} will always
     * be informed first.<br>
     * Note: if a listener processes the event, then the other listeners will
     * not be informed.
     * @param listener the new listener
     */
    public void addDoubleClickListener( CDoubleClickListener listener ){
        listenerCollection.addDoubleClickListener( listener );
    }

    /**
     * Removes a listener from this control.
     * @param listener the listener to remove
     */
    public void removeDoubleClickListener( CDoubleClickListener listener ){
        listenerCollection.removeDoubleClickListener( listener );
    }

    /**
     * Adds <code>listener</code> to this control, the listener will be informed whenever a set of
     * {@link CDockable}s is about to be closed.<br>
     * {@link CVetoClosingListener}s added to the {@link CControl} are invoked before listeners that
	 * are added to a {@link CDockable}.
     * @param listener the new listener, not <code>null</code>
     */
    public void addVetoClosingListener( CVetoClosingListener listener ){
    	listenerCollection.addVetoClosingListener( listener );
    }
    
    /**
     * Removes a listener from this control.
     * @param listener the listener to remove
     */
    public void removeVetoClosingListener( CVetoClosingListener listener ){
    	listenerCollection.removeVetoClosingListener( listener );
    }
    
    /**
     * Gets a list of currently registered listeners.
     * @return the listeners
     */
    private CControlListener[] listeners(){
        return listeners.toArray( new CControlListener[ listeners.size() ] );
    }

    /**
     * Informs this {@link CControl} whether location of {@link CDockable}s that are associated with a 
     * {@link CStation#isWorkingArea() working area} should be stored when storing a layout.<br>
     * This method installs a {@link DockSituationIgnore} on the intern {@link DockFrontend}, the filter is only
     * used for "normal entries", "final entries" (does stored when the application shuts down) are not affected.<br>
     * The default value for this property is <code>true</code>. 
     * @param ignore if <code>true</code> then some {@link CDockable}s are filtered out, otherwise their location
     * is stored.
     */
    public void setIgnoreWorkingForEntry( boolean ignore ){
    	if( ignore ){
	    	frontend.setIgnoreForEntry( new DockSituationIgnore(){
		        public boolean ignoreChildren( DockStation station ) {
		            CStation<?> cstation = getStation( station );
		            if( cstation != null )
		                return cstation.isWorkingArea();
		
		            return false;
		        }
		        public boolean ignoreElement( DockElement element ) {
		            if( element instanceof CommonDockable ){
		                CDockable cdockable = ((CommonDockable)element).getDockable();
		                if( cdockable.getWorkingArea() != null )
		                    return true;
		            }
		            return false;
		        }
		    });
    	}
    	else{
    		frontend.setIgnoreForEntry( null );
    	}
    }
    
    /**
     * Writes a map using the unique identifiers of each {@link SingleCDockable} to
     * tell to which {@link CWorkingArea} it belongs.
     * @param out the stream to write into
     * @throws IOException if an I/O error occurs
     */
    private void writeWorkingAreas( DataOutputStream out ) throws IOException{
        Map<String,String> map = new HashMap<String, String>();

        for( SingleCDockable dockable : register.getSingleDockables() ){
            CStation<?> area = dockable.getWorkingArea();
            if( area != null ){
                map.put( dockable.getUniqueId(), area.getUniqueId() );
            }
        }

        out.writeInt( map.size() );
        for( Map.Entry<String, String> entry : map.entrySet() ){
            out.writeUTF( entry.getKey() );
            out.writeUTF( entry.getValue() );
        }
    }

    /**
     * Writes a map of all {@link SingleCDockable}s and their {@link CWorkingArea}.
     * @param element the element to write into
     */
    private void writeWorkingAreasXML( XElement element ){
        for( SingleCDockable dockable : register.getSingleDockables() ){
            CStation<?> area = dockable.getWorkingArea();
            if( area != null ){
                XElement xarea = element.addElement( "area" );
                xarea.addString( "id", area.getUniqueId() );
                xarea.addString( "child", dockable.getUniqueId() );
            }
        }
    }

    /**
     * Reads a map telling for each {@link SingleCDockable} to which {@link CWorkingArea}
     * it belongs.
     * @param in the stream to read from
     * @throws IOException if an I/O error occurs
     */
    private void readWorkingAreas( DataInputStream in ) throws IOException{
        Map<String, SingleCDockable> dockables = new HashMap<String, SingleCDockable>();
        Map<String, CStation<?>> areas = new HashMap<String, CStation<?>>();

        for( CStation<?> station : register.getStations() ){
            if( station.isWorkingArea() ){
                areas.put( station.getUniqueId(), station );
            }
        }

        for( SingleCDockable dockable : register.getSingleDockables() ){
            dockables.put( dockable.getUniqueId(), dockable );
        }

        for( int i = 0, n = in.readInt(); i<n; i++ ){
            String key = in.readUTF();
            String value = in.readUTF();

            CDockable dockable = dockables.get( key );
            if( dockable != null ){
                CStation<?> area = areas.get( value );
                dockable.setWorkingArea( area );
            }
        }
    }

    /**
     * Reads a map telling for each {@link SingleCDockable} to which {@link CWorkingArea}
     * it belongs.
     * @param element the xml element to read from
     */
    private void readWorkingAreasXML( XElement element ){
        Map<String, SingleCDockable> dockables = new HashMap<String, SingleCDockable>();
        Map<String, CStation<?>> areas = new HashMap<String, CStation<?>>();

        for( CStation<?> station : register.getStations() ){
            if( station.isWorkingArea() ){
                areas.put( station.getUniqueId(), station );
            }
        }

        for( SingleCDockable dockable : register.getSingleDockables() ){
            dockables.put( dockable.getUniqueId(), dockable );
        }

        for( XElement xarea : element.getElements( "area" )){
            String key = xarea.getString( "child" );
            String value = xarea.getString( "id" );

            CDockable dockable = dockables.get( key );
            if( dockable != null ){
                CStation<?> area = areas.get( value );
                dockable.setWorkingArea( area );
            }
        }
    }

    /**
     * Frees as much resources as possible. This {@link CControl} will no longer
     * work correctly after this method was called.
     */
    public void destroy(){
        frontend.kill();
        for( DestroyHook hook : hooks )
            hook.destroy();
    }
    
    /**
     * Creates and adds a new {@link CWorkingArea} to this control. The area
     * is not made visible by this method.
     * @param uniqueId the unique id of the area
     * @return the new area
     */
    public CWorkingArea createWorkingArea( String uniqueId ){
        CWorkingArea area = new CWorkingArea( this, uniqueId );
        addDockable( area );
        addStation( area, true );
        return area;
    }

    /**
     * Creates a new area where minimized {@link CDockable}s can be stored. This
     * method adds the new area directly as a root station to this control.
     * @param uniqueId a unique identifier
     * @return the new area
     */
    public CMinimizeArea createMinimizeArea( String uniqueId ){
        CMinimizeArea area = new CMinimizeArea( this, uniqueId );
        addStation( area, true );
        return area;
    }

    /**
     * Creates a new area where normalized {@link CDockable}s can be stored.
     * This method adds the new area directly as a root station to this control
     * @param uniqueId a unique identifier
     * @return the new area
     */
    public CGridArea createGridArea( String uniqueId ){
        CGridArea area = new CGridArea( this, uniqueId );
        addStation( area, true );
        if( frontend.getDefaultStation() == null )
            frontend.setDefaultStation( area.getStation() );
        return area;
    }

    /**
     * Ensures the uniqueness of the identifier <code>uniqueId</code>. Throws
     * various exceptions if the id is not unique.
     * @param uniqueId the id that might be unique
     */
    private void checkStationIdentifierUniqueness( String uniqueId ){
        if( uniqueId == null )
            throw new NullPointerException( "uniqueId must not be null" );

        if( CContentArea.getCenterIdentifier( CONTENT_AREA_STATIONS_ID ).equals( uniqueId ) )
            throw new IllegalArgumentException( "The id " + uniqueId + " is reserved for special purposes" );
        if( CContentArea.getEastIdentifier( CONTENT_AREA_STATIONS_ID ).equals( uniqueId ) )
            throw new IllegalArgumentException( "The id " + uniqueId + " is reserved for special purposes" );
        if( CContentArea.getWestIdentifier( CONTENT_AREA_STATIONS_ID ).equals( uniqueId ) )
            throw new IllegalArgumentException( "The id " + uniqueId + " is reserved for special purposes" );
        if( CContentArea.getSouthIdentifier( CONTENT_AREA_STATIONS_ID ).equals( uniqueId ) )
            throw new IllegalArgumentException( "The id " + uniqueId + " is reserved for special purposes" );
        if( CContentArea.getNorthIdentifier( CONTENT_AREA_STATIONS_ID ).equals( uniqueId ) )
            throw new IllegalArgumentException( "The id " + uniqueId + " is reserved for special purposes" );

        for( CStation<?> station : register.getStations() ){
            if( station.getUniqueId().equals( uniqueId )){
                throw new IllegalArgumentException( "There exists already a station with id: " + uniqueId );    
            }
        }
    }

    /**
     * Creates and adds a new {@link CContentArea}.
     * @param uniqueId the unique id of the new contentarea, the id must be unique
     * in respect to all other contentareas which are registered at this control.
     * @return the new contentarea
     * @throws IllegalArgumentException if the id is not unique
     * @throws NullPointerException if the id is <code>null</code>
     */
    public CContentArea createContentArea( String uniqueId ){
        if( uniqueId == null )
            throw new NullPointerException( "uniqueId must not be null" );

        for( CContentArea center : register.getContentAreas() ){
            if( center.getUniqueId().equals( uniqueId ))
                throw new IllegalArgumentException( "There exists already a CContentArea with the unique id " + uniqueId );
        }

        DockStation defaultStation = frontend.getDefaultStation();

        boolean noDefaultStation = defaultStation == null || defaultStation instanceof ScreenDockStation;

        CContentArea center = new CContentArea( this, uniqueId );
        addContentArea( center );

        if( noDefaultStation )
            frontend.setDefaultStation( center.getCenter() );

        return center;
    }

    /**
     * Adds a new {@link CContentArea} to this control.
     * @param content the new area
     * @throws IllegalArgumentException if the area is already in use or if
     * the area was not created using <code>this</code>
     * @deprecated this method has not the same behavior as {@link #createContentArea(String)},
     * this method will either be removed in future releases or change its
     * behavior
     */
    @Deprecated
    @Todo( priority=Todo.Priority.MAJOR, compatibility=Compatibility.BREAK_MAJOR, target=Todo.Version.VERSION_1_1_0,
    		description="remove this method, replace by something better" )
    public void addContentArea( CContentArea content ){
        if( content == null )
            throw new NullPointerException( "content is null" );

        if( content.getControl() != this )
            throw new IllegalArgumentException( "content was not created using this CControl" );

        register.addContentArea( content );

        CContentArea defaultArea = register.getDefaultContentArea();

        boolean check = !(defaultArea == null || content != defaultArea);

        for( CStation<?> station : content.getStations() ){
            addStation( station, true, check );
        }
    }

    /**
     * Removes <code>content</code> from the list of known contentareas. This also removes
     * the stations of <code>content</code> from this control. Elements aboard the
     * stations are made invisible, but not removed from this control.
     * @param content the contentarea to remove
     * @throws IllegalArgumentException if the default-contentarea equals <code>content</code>
     */
    public void removeContentArea( CContentArea content ){
        if( content == null )
            throw new NullPointerException( "content must not be null" );

        if( register.getDefaultContentArea() == content )
            throw new IllegalArgumentException( "The default-contentarea can't be removed" );

        if( register.removeContentArea( content ) ){
            for( CStation<?> station : content.getStations() ){
                remove( station );
            }
        }
    }

    /**
     * Gets the set of dockables, stations and other elemnts that are used
     * by this control.
     * @return the set of elements, never <code>null</code>
     */
    public CControlRegister getRegister(){
        return register;
    }

    /**
     * Gets an unmodifiable list of all {@link CContentArea}s registered at
     * this control
     * @return the list of content-areas
     */
    public List<CContentArea> getContentAreas(){
        return register.getContentAreas();
    }

    /**
     * Gets the factory which is mainly used to create new elements for this
     * control.
     * @return the factory
     */
    public CControlFactory getFactory() {
        return factory;
    }

    /**
     * Gets the manager that is responsible to handle all changes of the
     * modes (maximized, normalized, ... ) of {@link Dockable}s.<br>
     * Note: clients should be careful when working with the location manager. 
     * Changing the properties of the location manager might introduce failures that
     * are not visible directly.
     * @return the manager
     */
    public CLocationModeManager getLocationManager() {
        return locationManager;
    }

    /**
     * Adds a destroy-hook. The hook is called when this {@link CControl} is
     * destroyed through {@link #destroy()}.
     * @param hook the new hook
     */
    public void addDestroyHook( DestroyHook hook ){
        if( hook == null )
            throw new NullPointerException( "hook must not be null" );
        hooks.add( hook );
    }

    /**
     * Removes a destroy-hook from this {@link CControl}.
     * @param hook the hook to remove
     */
    public void removeDestroyHook( DestroyHook hook ){
        hooks.remove( hook );
    }

    /**
     * Grants access to the manager that reads and stores configurations
     * of the common-project.<br>
     * Clients can add their own {@link ApplicationResource}s to this manager,
     * however clients are strongly discouraged from removing {@link ApplicationResource}
     * which they did not add by themself.
     * @return the persistent storage
     */
    public ApplicationResourceManager getResources() {
        return resources;
    }

    /**
     * Changes the value of a property. Some properties are:
     * <ul>
     * <li>{@link #KEY_MAXIMIZE_CHANGE}</li>
     * <li>{@link #KEY_GOTO_EXTERNALIZED}</li>
     * <li>{@link #KEY_GOTO_MAXIMIZED}</li>
     * <li>{@link #KEY_GOTO_MINIMIZED}</li>
     * <li>{@link #KEY_GOTO_NORMALIZED}</li>
     * <li>{@link #KEY_CLOSE}</li>
     * </ul>
     * @param <A> the type of the value
     * @param key the name of the property
     * @param value the new value, can be <code>null</code>
     */
    public <A> void putProperty( PropertyKey<A> key, A value ){
    	putProperty( key, value, Priority.CLIENT );
    }
    
    /**
     * Changes the value of a property.
     * @param <A> the type of the value
     * @param key the name of the property
     * @param priority the priority of the new value
     * @param value the new value, can be <code>null</code>
     * @see #putProperty(PropertyKey, Object)
     */
    protected <A> void putProperty( PropertyKey<A> key, A value, Priority priority ){
        frontend.getController().getProperties().set( key, value, priority );
    }

    /**
     * Gets the value of a property.
     * @param <A> the type of the property
     * @param key the name of the property
     * @return the value or <code>null</code>
     */
    public <A> A getProperty( PropertyKey<A> key ){
        return frontend.getController().getProperties().get( key );
    }

    /**
     * Gets the element that should be in the center of the mainframe. The {@link CContentArea}
     * is created the first time this method is called.
     * @return the center of the mainframe of the application
     */
    public CContentArea getContentArea() {
        CContentArea content = register.getDefaultContentArea();

        if( content == null ){
            content = createContentArea( CONTENT_AREA_STATIONS_ID );
            register.setDefaultContentArea( content );
        }

        return content;
    }

    /**
     * Adds an additional station to this control. Most {@link CStation}s should
     * be root-stations, even if they are nested. 
     * @param station the new station
     * @param root <code>true</code> if the station should become a root station.
     * A root station often does not have a parent, the location of a {@link CDockable}
     * is always relative to its youngest parent that is a root station.
     */
    public void addStation( CStation<?> station, boolean root ){
        add( station, root );
    }
    
    /**
     * Adds an additional station to this control.
     * @deprecated will be removed in a future release, please use
     * {@link #addStation(CStation, boolean)} instead. 
     * @param station the new station
     * @param root whether the new station should be a root station
     * @see #addStation(CStation, boolean)
     */
    @Deprecated
    @Todo( compatibility=Compatibility.BREAK_MAJOR, priority=Todo.Priority.MINOR, target=Todo.Version.VERSION_1_1_1,
    		description="remove this method" )
    public void add( CStation<?> station, boolean root ){
    	addStation( station, root, true );
    }

    /**
     * Adds an additional station to this control.
     * @param station the new station
     * @param root <code>true</code> if the station should become a root station.
     * A root station often does not have a parent, the location of a {@link CDockable}
     * is always relative to its youngest parent that is a root station.
     * @param check if <code>true</code> a check of the unique id is performed,
     * otherwise the station is just put in, perhaps wrongly replacing
     * other stations.
     */    
    private void addStation( CStation<?> station, boolean root, boolean check ){
        String id = station.getUniqueId();
        if( check ){
            checkStationIdentifierUniqueness( id );
        }

        if( root ){
            frontend.addRoot( id, station.getStation() );
        }

        station.setControl( access );
        register.addStation( station );
    }

    /**
     * Removes a {@link CStation} from this control. It is unspecified what
     * happens with the children on <code>station</code>
     * @param station the station to remove
     */
    public void remove( CStation<?> station ){
        if( register.removeStation( station ) ){
            frontend.removeRoot( station.getStation() );
            station.setControl( null );
        }
    }

    /**
     * Gets an unmodifiable list of all stations that are currently 
     * registered at this control.
     * @return the list of stations
     */
    public List<CStation<?>> getStations(){
        return register.getStations();
    }

    /**
     * Searches the {@link CStation} whose {@link CStation#getStation() internal representation}
     * is <code>intern</code>.
     * @param intern the internal representation
     * @return the station or <code>null</code>
     */
    public CStation<?> getStation( DockStation intern ){
        for( CStation<?> station : register.getStations() ){
            if( station.getStation() == intern )
                return station;
        }
        return null;
    }
    
    /**
     * Searches the {@link CStation} with unique identifier <code>id</code>.
     * @param id the identifier
     * @return the station or <code>null</code>
     */
    public CStation<?> getStation( String id ){
    	for( CStation<?> station : register.getStations() ){
    		if( station.getUniqueId().equals( id )){
    			return station;
    		}
    	}
    	return null;
    }

    /**
     * Adds a dockable to this control. The dockable can be made visible afterwards. This method will do nothing
     * if <code>dockable</code> was already registered at this {@link CControl}.
     * @param <S> the type of the new element
     * @param dockable the new element to show
     * @return <code>dockable</code>
     * @throws IllegalArgumentException if <code>dockable</code> already is registered at another {@link CControl}
     * or if the unique id of <code>dockable</code> already is used for another object
     */
    @Todo( compatibility=Compatibility.COMPATIBLE, priority=Todo.Priority.BUG, target=Todo.Version.VERSION_1_1_0,
    		description="check unique identifier of dockable to make sure it is a valid identifier, maybe not at this location..." )
    public <S extends SingleCDockable> S addDockable( S dockable ){
    	return add( dockable );
    }
    
    /**
     * Adds a dockable to this control.
     * @param <S> the type of the new element
     * @param dockable the new element to show
     * @return <code>dockable</code>
     * @deprecated will be removed in a future release, please use {@link #addDockable(SingleCDockable)} instead.
     */
    @Deprecated
    @Todo( compatibility=Compatibility.BREAK_MAJOR, priority=Todo.Priority.MINOR, target=Todo.Version.VERSION_1_1_1,
    		description="remove this method" )
    public <S extends SingleCDockable> S add( S dockable ){
        if( dockable == null )
            throw new NullPointerException( "dockable must not be null" );

        boolean alreadyKnown = dockable.getControl() == access;
        
        if( dockable.getControl() != null && !alreadyKnown ){
            throw new IllegalArgumentException( "dockable is already part of a control" );
        }

        SingleCDockable preset = register.getSingleDockable( dockable.getUniqueId() );

        if( preset != null ){
        	if( preset == dockable ){
        		return dockable;
        	}
        	else{
        		throw new IllegalArgumentException( "unique id \'" + dockable.getUniqueId() + "\' already in use for another SingleCDockable" );
        	}
        }
        
        
        if( !alreadyKnown ){
        	dockable.setControl( access );
        }

        String id = register.toSingleId( dockable.getUniqueId() );
        accesses.get( dockable ).setUniqueId( id );
        frontend.addDockable( id, dockable.intern() );
        frontend.setHideable( dockable.intern(), true );

        register.addSingleDockable( dockable );

        for( CControlListener listener : listeners() )
            listener.added( CControl.this, dockable );

        return dockable;
    }

    /**
     * Searches for the {@link SingleCDockable} which has the unique identifier
     * <code>id</code>.
     * @param id the identifier to look out for
     * @return the element with that identifier or <code>null</code>
     */
    public SingleCDockable getSingleDockable( String id ){
        for( SingleCDockable dockable : register.getSingleDockables() ){
            if( dockable.getUniqueId().equals( id )){
                return dockable;
            }
        }
        return null;
    }

    /**
     * Removes the {@link SingleCDockable} with the identifier <code>id</code>.
     * This has the same effect as calling {@link #remove(SingleCDockable)}.
     * @param id the id of the element to remove
     * @return <code>true</code> if the element was removed, <code>false</code>
     * otherwise
     */
    public boolean removeSingleDockable( String id ){
        for( SingleCDockable dockable : register.getSingleDockables() ){
            if( dockable.getUniqueId().equals( id )){
                return remove( dockable );
            }
        }
        return false;
    }

    /**
     * Removes <code>dockable</code> from this control. The location information
     * for <code>dockable</code> remains stored if either there is a 
     * {@link #addSingleBackupFactory(String, SingleCDockableFactory) SingleCDockableBackupFactory}
     * registered or the {@link #setMissingStrategy(MissingCDockableStrategy) MissingCDockableStrategy}
     * tells to store the values.
     * @param dockable the element to remove
     * @return true if the element was removed, <code>false</code> otherwise
     */
    public boolean remove( SingleCDockable dockable ){
        if( dockable == null )
            throw new NullPointerException( "dockable must not be null" );

        if( dockable.getControl() == access ){
            dockable.setVisible( false );
            frontend.remove( dockable.intern() );
            register.removeSingleDockable( dockable );
            dockable.setControl( null );

            for( CControlListener listener : listeners() )
                listener.removed( CControl.this, dockable );

            return true;
        }

        return false;
    }
    
    /**
     * Adds a factory to this control. The factory will be used
     * to create and add a {@link SingleCDockable} when one is requested that
     * is not yet in the cache.<br>
     * If there is already information for <code>id</code> available and
     * <code>id</code> should be visible, then the factory will be used
     * instantaneously.<br>
     * Factories added with a specific identifier always have higher priority than factories
     * added with a filter, see {@link #addSingleDockableFactory(Filter, SingleCDockableFactory)}.
     * @param id the id of the dockable that might be requested
     * @param backupFactory the new factory
     */
    public void addSingleDockableFactory( String id, SingleCDockableFactory backupFactory ){
    	addSingleBackupFactory( id, backupFactory );
    }

    /**
     * Adds a backup factory to this control. The backup factory will be used
     * to create and add a {@link SingleCDockable} when one is requested that
     * is not yet in the cache.<br>
     * If there is already information for <code>id</code> available and
     * <code>id</code> should be visible, then the factory will be used
     * Instantaneously.
     * @deprecated will be removed in a future release, please use {@link #addSingleDockableFactory(String, SingleCDockableFactory)}
     * instead
     * @param id the id of the dockable that might be requested
     * @param backupFactory the new factory
     */
    @Deprecated
    @Todo( compatibility=Compatibility.BREAK_MAJOR, priority=Todo.Priority.MINOR, target=Todo.Version.VERSION_1_1_1,
    		description="remove this method" )
    public void addSingleBackupFactory( String id, SingleCDockableFactory backupFactory ){
        register.getBackupFactory().add( id, backupFactory );

        String singleId = register.toSingleId( id );
        
       // This would happen automatically when loading a layout. However code reading 
       // the entries of DockFrontend is now informed about the possible existence of
       // such an identifier
       locationManager.addEmpty( singleId );
       frontend.addEmpty( singleId );

        // if there is already layout information for id, then load this information now
        FrontendEntry entry = frontend.getFrontendEntry( singleId );
        if( entry != null && entry.getDockable() == null && entry.isShown() ){
            SingleCDockable dockable = backupFactory.createBackup( id );
            if( dockable != null ){
                addDockable( dockable );
                if( entry.isShown() || !dockable.isCloseable() ){
                    dockable.setVisible( true );
                }
            }
        }
    }
    
    /**
     * Adds a factory to this control. The factory will be used
     * to create and add a {@link SingleCDockable} when one is requested that
     * is not yet in the cache.<br>
     * If there is already information for identifiers that are included by <code>ids</code> available and
     * if they should be visible, then the <code>factory</code> will be used instantaneously to create these elements. 
     * During this action <code>factory</code> has a higher priority than any other factory.<br>
     * Factories added with a general filter always have lower priority than factories that were added
     * with a specific identifier. The factories are stored in a list and a search starts at the front of that
     * list, so a factory added early has higher priority than a factory that was added lately.
     * @param ids a filter telling which dockables can be handled by <code>factory</code>
     * @param factory the new factory
     */
    public void addSingleDockableFactory( Filter<String> ids, SingleCDockableFactory factory ){
    	register.getBackupFactory().add( ids, factory );
    	
    	for( FrontendEntry entry : frontend.listFrontendEntries() ){
    		if( entry.getDockable() == null && entry.isShown() ){
    			if( register.isSingleId( entry.getKey() )){
	    			String id = register.singleToNormalId( entry.getKey() );
	    			if( ids.includes( id )){
	    				SingleCDockable dockable = factory.createBackup( id );
	    	            if( dockable != null ){
	    	                addDockable( dockable );
	    	                if( entry.isShown() || !dockable.isCloseable() ){
	    	                    dockable.setVisible( true );
	    	                }
	    	            }	
	    			}
    			}
    		}
    	}
    }

    /**
     * Searches the {@link SingleCDockableFactory} which is responsible for creating the
     * {@link SingleCDockable} with identifier <code>id</code>. This method first searches
     * for a factory which was added with a specific identifier ({@link #addSingleDockableFactory(String, SingleCDockableFactory)}),
     * if nothing is found then the factories with a filter are searched ({@link #addSingleDockableFactory(Filter, SingleCDockableFactory)}).
     * @param id the identifier of some factory
     * @return the factory or <code>null</code>
     */
    public SingleCDockableFactory getSingleDockableFactory( String id ){
    	return register.getBackupFactory().getFactory( id );
    }
    
    /**
     * Searches for the {@link SingleCDockableFactory} which was registered
     * with the key <code>id</code>. Also searches the general factories that were added
     * with a filter. 
     * @deprecated will be removed in a future release, please use {@link #getSingleDockableFactory(String)} instead
     * @param id the identifier of some factory
     * @return the factory or <code>null</code>
     */
    @Deprecated
    @Todo( compatibility=Compatibility.BREAK_MAJOR, priority=Todo.Priority.MINOR, target=Todo.Version.VERSION_1_1_1,
    		description="remove this method" )
    public SingleCDockableFactory getSingleBackupFactory( String id ){
        return register.getBackupFactory().getFactory( id );
    }

    /**
     * Removes a factory from this control. Location information for
     * <code>id</code> will be deleted if neither a {@link #add(SingleCDockable) SingleCDockable}
     * is added nor the {@link #setMissingStrategy(MissingCDockableStrategy) MissingCDockableStrategy}
     * tells to store the information.
     * @param id the name of the factory
     * @see #addSingleBackupFactory(String, SingleCDockableFactory)
     */
    public void removeSingleDockableFactory( String id ){
    	removeSingleBackupFactory( id );
    }
    
    /**
     * Removes all occurrences of <code>factory</code>. Any location information that was held
     * because of the existence of <code>factory</code> will be removed as well.
     * @param factory the factory to remove
     */
    public void removeSingleDockableFactory( SingleCDockableFactory factory ){
    	register.getBackupFactory().remove( factory );

    	for( FrontendEntry entry : frontend.listFrontendEntries() ){
    		if( entry.getDockable() == null && entry.isShown() ){
    			if( register.isSingleId( entry.getKey() )){
	    			String id = register.singleToNormalId( entry.getKey() );
	    			
	    	        if( !missingStrategy.shouldStoreSingle( id )){
	    	            locationManager.removeEmpty( entry.getKey());
	    	            frontend.removeEmpty( entry.getKey() );
	    	        }
    			}
    		}
    	}
    }
    
    /**
     * Removes a factory from this control. Location information for
     * <code>id</code> will be deleted if neither a {@link #add(SingleCDockable) SingleCDockable}
     * is added nor the {@link #setMissingStrategy(MissingCDockableStrategy) MissingCDockableStrategy}
     * tells to store the information.
     * @deprecated will be removed in a future release, please use {@link #removeSingleDockableFactory(String)} instead
     * @param id the name of the factory
     * @see #addSingleBackupFactory(String, SingleCDockableFactory)
     */
    @Deprecated
    @Todo( compatibility=Compatibility.BREAK_MAJOR, priority=Todo.Priority.MINOR, target=Todo.Version.VERSION_1_1_1,
    		description="remove this method" )
    public void removeSingleBackupFactory( String id ){
        register.getBackupFactory().remove( id );

        if( !missingStrategy.shouldStoreSingle( id )){
            id = register.toSingleId( id );

            locationManager.removeEmpty( id );
            frontend.removeEmpty( id );
        }
    }

    /**
     * Adds a dockable to this control. The dockable can be made visible afterwards. A random identifier 
     * is assigned to <code>dockable</code>, clients can also use {@link #addDockable(String, MultipleCDockable)} if
     * they want to specify the identifier themselves.
     * @param <M> the type of the new element
     * @param dockable the new element to show
     * @return <code>dockable</code>
     */
    public <M extends MultipleCDockable> M addDockable( M dockable) {
    	return add( dockable );
    }
    
    /**
     * Adds a dockable to this control. The dockable can be made visible afterwards.
     * @deprecated will be removed in a future release, please use {@link #addDockable(MultipleCDockable)} instead.
     * @param <M> the type of the new element
     * @param dockable the new element to show
     * @return <code>dockable</code>
     */
    @Deprecated
    @Todo( compatibility=Compatibility.BREAK_MAJOR, priority=Todo.Priority.MINOR, target=Todo.Version.VERSION_1_1_1,
    		description="remove this method" )
    public <M extends MultipleCDockable> M add( M dockable ){
        Set<String> ids = new HashSet<String>();

        String factory = access.getFactoryId( dockable.getFactory() );
        if( factory == null ){
            throw new IllegalStateException( "the factory for a MultipleCDockable is not registered: " + dockable.getFactory() );
        }

        for( MultipleCDockable multi : register.getMultipleDockables() ){
            if( factory.equals( access.getFactoryId( multi.getFactory() ))){
                ids.add( accesses.get( multi ).getUniqueId() );
            }
        }

        int count = 0;
        String id = count + " " + factory;
        while( ids.contains( register.toMultiId( id ) ) ){
            count++;
            id = count + " " + factory;
        }

        return add( dockable, id );
    }

    /**
     * Adds a dockable to this control. The dockable can be made visible afterwards.
     * This method will throw an exception when the unique identifier is already
     * in use. Clients can also use {@link #addDockable(MultipleCDockable)} if they want to assign a 
     * random identifier to <code>dockable</code>.
     * @param <M> the type of the new element
     * @param uniqueId id the unique id of the new element
     * @param dockable the new element to show
     * @return <code>dockable</code>
     * @throws IllegalArgumentException if the unique identifier is already in
     * use, if <code>dockable</code> is already used elsewhere, if there is
     * no factory for <code>dockable</code>
     * @throws NullPointerException if any argument is <code>null</code>
     */
    public <M extends MultipleCDockable> M addDockable( String uniqueId, M dockable ){
    	return add( dockable, uniqueId );
    }
    
    /**
     * Adds a dockable to this control. The dockable can be made visible afterwards.
     * This method will throw an exception when the unique identifier is already
     * in use. Clients better use {@link #add(MultipleCDockable)}.
     * @deprecated will be removed in a future release, please use {@link #addDockable(String, MultipleCDockable)} instead
     * @param <M> the type of the new element
     * @param dockable the new element to show
     * @param uniqueId id the unique id of the new element
     * @return <code>dockable</code>
     * @throws IllegalArgumentException if the unique identifier is already in
     * use, if <code>dockable</code> is already used elsewhere, if there is
     * no factory for <code>dockable</code>
     * @throws NullPointerException if any argument is <code>null</code>
     */
    @Deprecated
    @Todo( compatibility=Compatibility.BREAK_MAJOR, priority=Todo.Priority.MINOR, target=Todo.Version.VERSION_1_1_1, 
    		description="remove this method" )
    public <M extends MultipleCDockable> M add( M dockable, String uniqueId ){
        if( dockable == null )
            throw new NullPointerException( "dockable must not be null" );

        if( uniqueId == null )
            throw new NullPointerException( "uniqueId must not be null" );

        String factory = access.getFactoryId( dockable.getFactory() );
        if( factory == null ){
            throw new IllegalStateException( "the factory for a MultipleCDockable is not registered: " + dockable.getFactory() );
        }

        if( dockable.getControl() != null )
            throw new IllegalStateException( "dockable is already part of a control" );

        uniqueId = register.toMultiId( uniqueId );

        for( MultipleCDockable multi : register.getMultipleDockables() ){
            String id = accesses.get( multi ).getUniqueId();
            if( uniqueId.equals( id )){
                throw new IllegalArgumentException( "The unique identifier is already in use: " + uniqueId );
            }
        }

        dockable.setControl( access );
        accesses.get( dockable ).setUniqueId( uniqueId );

        register.addMultipleDockable( dockable );

        for( CControlListener listener : listeners() )
            listener.added( CControl.this, dockable );

        return dockable;
    }
    
    /**
     * Replaces <code>oldDockable</code> with <code>newDockable</code>. The new dockable
     * inherits settings and location of the old one.
     * @param oldDockable the old dockable, not <code>null</code>
     * @param newDockable the new dockable, not <code>null</code>
     */
    public void replace( MultipleCDockable oldDockable, MultipleCDockable newDockable ){
    	if( oldDockable == null )
    		throw new IllegalArgumentException( "old dockable must not be null" );
    	if( newDockable == null )
    		throw new IllegalArgumentException( "new dockable must not be null" );
    	
    	if( oldDockable.getControl() != access )
    		throw new IllegalArgumentException( "old dockable not registered at this CControl" );
    	
    	if( newDockable.getControl() != null )
    		throw new IllegalArgumentException( "new dockable alread registered at some CControl" );
    	
    	String id = accesses.get( oldDockable ).getUniqueId();
    	
    	boolean frontendEmpty = frontend.isEmpty( id );
    	if( !frontendEmpty ){
    		frontend.addEmpty( id );
    	}
    	
    	boolean locationEmpty = locationManager.isEmpty( id );
    	if( !locationEmpty ){
    		locationManager.addEmpty( id );
    	}
    	
    	id = register.multiToNormalId( id );
    	
    	remove( oldDockable );
    	add( newDockable, id );
    	
    	if( !frontendEmpty ){
    		frontend.removeEmpty( id );
    	}
    	if( !locationEmpty ){
    		locationManager.removeEmpty( id );
    	}
    }

    /**
     * Searches and returns the one {@link MultipleCDockable} which uses
     * the unique identifier <code>id</code>.
     * @param id the identifier to look out for
     * @return the element using <code>id</code> or <code>null</code> if nothing
     * was found
     */
    public MultipleCDockable getMultipleDockable( String id ){
        id = register.toMultiId( id );
        for( MultipleCDockable dockable : register.getMultipleDockables() ){
            if( accesses.get( dockable ).getUniqueId().equals( id )){
                return dockable;
            }
        }
        return null;
    }

    private boolean shouldStore( String id ){
        if( register.isSingleId( id )){
        	if( register.getBackupFactory().getFactory( register.singleToNormalId( id ) ) != null ){
        		return true;
        	}
            return missingStrategy.shouldStoreSingle( register.singleToNormalId( id ) );
        }
        else{
            return missingStrategy.shouldStoreMulti( register.multiToNormalId( id ) );
        }
    }    
    
    private String shouldStore( CDockable dockable ){
    	String key = null;
    	
    	if( dockable instanceof SingleCDockable ){
    		key = ((SingleCDockable)dockable).getUniqueId();
    		key = register.toSingleId( key );
    	}
    	else if( dockable instanceof MultipleCDockable ){
    		MultipleCDockableFactory<?, ?> factory = ((MultipleCDockable)dockable).getFactory();
    		
    		for( Map.Entry<String, MultipleCDockableFactory<?, ?>> entry : register.getFactories().entrySet() ){
    			if( entry.getValue() == factory ){
    				key = entry.getKey();
    				break;
    			}
    		}
    		
    		if( key == null ){
    			return null;
    		}
    		
    		key = register.toMultiId( key );
    	}
    	
    	if( shouldStore( key )){
			return key;
		}
		else{
			return null;
		}
    }

    @SuppressWarnings("unchecked")
    private boolean shouldCreate( MultipleCDockableFactory<?, ?> factory, CommonMultipleDockableLayout layout ){
        String uniqueId = layout.getId();

        String multiId = register.toMultiId( uniqueId );

        for( MultipleCDockable multi : register.getMultipleDockables() ){
            if( accesses.get( multi ).getUniqueId().equals( multiId )){
                return false;
            }
        }

        String factoryId = access.getFactoryId( factory );
        MultipleCDockableFactory<?, MultipleCDockableLayout> normalizedFactory = (MultipleCDockableFactory<?, MultipleCDockableLayout>)factory;
        return missingStrategy.shouldCreate( factoryId, normalizedFactory, uniqueId, layout.getLayout() );
    }

    /**
     * Removes a dockable from this control. The dockable is made invisible.
     * @param dockable the element to remove
     */
    public void remove( MultipleCDockable dockable ){
        if( dockable == null )
            throw new NullPointerException( "dockable must not be null" );

        if( dockable.getControl() == access ){
            dockable.setVisible( false );
            frontend.remove( dockable.intern() );

            register.removeMultipleDockable( dockable );

            dockable.setControl( null );

            for( CControlListener listener : listeners() )
                listener.removed( CControl.this, dockable );
        }
    }

    /**
     * Gets the number of {@link CDockable}s that are registered in this
     * {@link CControl}.
     * @return the number of dockables
     */
    public int getCDockableCount(){
        return register.getDockableCount();
    }

    /**
     * Gets the index'th dockable that is registered in this control
     * @param index the index of the element
     * @return the selected dockable
     */
    public CDockable getCDockable( int index ){
        return register.getDockable( index );
    }

    /**
     * Adds a factory to this control. The factory will create {@link MultipleCDockable}s
     * when a layout is loaded.
     * @param id the unique id of the factory
     * @param factory the new factory
     * @deprecated use {@link #addMultipleDockableFactory(String, MultipleCDockableFactory)}
     */
    @Deprecated
    public void add( final String id, final MultipleCDockableFactory<?,?> factory ){
        if( id == null )
            throw new NullPointerException( "id must not be null" );

        if( factory == null )
            throw new NullPointerException( "factory must not be null" );

        if( register.getCommonMultipleDockableFactory( id ) != null ){
            throw new IllegalArgumentException( "there is already a factory named " + id );
        }

        if( access.getFactoryId( factory ) != null ){
            throw new IllegalArgumentException( "this factory-object is already in use and cannot be added a second time" );
        }

        CommonMultipleDockableFactory cfactory = new CommonMultipleDockableFactory( id, factory, access );

        register.putCommonMultipleDockableFactory( id, cfactory );
        frontend.registerFactory( cfactory );
    }



    /**
     * Adds a factory to this control. The factory will create {@link MultipleCDockable}s
     * when a layout is loaded.
     * @param id the unique id of the factory
     * @param factory the new factory
     */
    public void addMultipleDockableFactory( String id, MultipleCDockableFactory<?, ?> factory ){
        add( id, factory );
    }

    /**
     * Searches for the {@link MultipleCDockableFactory} with the identifier
     * <code>id</code>.
     * @param id the identifier to search for
     * @return the factory or <code>null</code>
     */
    public MultipleCDockableFactory<?, ?> getMultipleDockableFactory( String id ){
        return register.getFactory( id );
    }


    /**
     * Removes the {@link MultipleCDockableFactory} with identifier <code>id</code>
     * from this control. As a side effect all {@link MultipleCDockable}s which
     * use that factory are removed as well. Nothing happens if there is no
     * factory registered with <code>id</code>.
     * @param id the identifier of the factory to remove
     */
    public void removeMultipleDockableFactory( String id ){
        CommonMultipleDockableFactory factory = register.removeCommonMultipleDockableFactory( id );
        if( factory != null ){
            frontend.unregisterFactory( factory );

            List<MultipleCDockable> toRemove = new ArrayList<MultipleCDockable>();
            for( MultipleCDockable dockable : register.getMultipleDockables() ){
                if( dockable.getFactory() == factory.getFactory() ){
                    toRemove.add( dockable );
                }
            }

            for( MultipleCDockable dockable : toRemove ){
                remove( dockable );
            }
        }
    }

    /**
     * Sets the location where {@link CDockable}s are opened when there is
     * nothing else specified for these <code>CDockable</code>s.
     * @param defaultLocation the location, can be <code>null</code>
     */
    public void setDefaultLocation( CLocation defaultLocation ){
        this.defaultLocation = defaultLocation;
    }

    /**
     * Gets the location where {@link CDockable}s are opened when nothing else
     * is specified.
     * @return the location, might be <code>null</code>
     * @see #setDefaultLocation(CLocation)
     */
    public CLocation getDefaultLocation(){
        return defaultLocation;
    }	

    /**
     * Makes sure that all {@link CDockable}s are maximized onto the area
     * which is registered under the given unique id.
     * @param id the unique id of the area
     * @see CGridArea#getUniqueId()
     * @see CContentArea#getCenterIdentifier()
     */
    public void setMaximizeArea( String id ){
    	CMaximizedMode mode = locationManager.getMaximizedMode();
    	CMaximizedModeArea area = mode.get( id );
    	if( area == null )
    		throw new IllegalArgumentException( "No area registered with key '" + id + "'" );
    	mode.setDefaultArea( area );
    }

    /**
     * Sets the {@link CMaximizeBehavior}. The behavior decides what happens
     * when the user wants to maximize or to un-maximize a {@link CDockable}.
     * @param behavior the new behavior, not <code>null</code>
     */
    public void setMaximizeBehavior( CMaximizeBehavior behavior ){
        locationManager.getMaximizedMode().setMaximizeBehavior( behavior );
    }

    /**
     * Gets the currently used maximize-behavior.
     * @return the behavior, not <code>null</code>
     * @see #setMaximizeBehavior(CMaximizeBehavior)
     */
    public CMaximizeBehavior getMaximizeBehavior(){
        return locationManager.getMaximizedMode().getMaximizeBehavior();
    }

    /**
     * Sets the {@link CGroupBehavior}. The behavior decides what happens when the user wants to change
     * the {@link ExtendedMode} of a {@link CDockable}.<br>
     * To be exact: the group behavior is applied for a call to {@link CDockable#setExtendedMode(ExtendedMode)}
     * respective a call to {@link LocationModeManager#setMode(Dockable, ExtendedMode)}. The buttons that are
     * visible to the user all link to these methods.  
     * @param behavior the new behavior, not <code>null</code>
     */
    public void setGroupBehavior( CGroupBehavior behavior ){
    	locationManager.setGroupBehavior( behavior );
    }
    
    /**
     * Gets the currently used {@link CGroupBehavior}.
     * @return the current behavior, not <code>null</code>
     * @see #setGroupBehavior(CGroupBehavior)
     */
    public CGroupBehavior getGroupBehavior(){
    	return locationManager.getGroupBehavior();
    }
    
    /**
     * Sets the theme of the elements in the realm of this control.
     * @param theme the new theme
     * @deprecated replaced by {@link #setTheme(String)}. While this method still
     * works, the theme will not get stored persistent and any module using
     * the {@link ThemeMap} ({@link #getThemes()}) will not be informed about
     * the change.
     */
    @Deprecated
    public void setTheme( DockTheme theme ){
        frontend.getController().setTheme( theme );
    }

    /**
     * Sets the theme of the elements in the realm of this control. The String
     * <code>theme</code> is used as key for {@link ThemeMap#select(String)}.
     * @param theme the name of the theme, this might be one of 
     * {@link ThemeMap#KEY_BASIC_THEME}, {@link ThemeMap#KEY_BUBBLE_THEME},
     * {@link ThemeMap#KEY_ECLIPSE_THEME}, {@link ThemeMap#KEY_FLAT_THEME}
     * or {@link ThemeMap#KEY_SMOOTH_THEME}. This can also be a any other
     * string which was used for {@link ThemeMap#put(String, ThemeFactory)},
     * {@link ThemeMap#add(String, ThemeFactory)} or {@link ThemeMap#insert(int, String, ThemeFactory)}.
     */
    public void setTheme( String theme ){
        themes.select( theme );
    }

    /**
     * Gets the list of installed themes.
     * @return the list of themes
     */
    public ThemeMap getThemes(){
        return themes;
    }
    
    /**
     * Grants access to the perspective API which allows clients to build complex layouts without
     * the need to create any {@link CDockable dockables} or {@link CStation stations}.
     * @return access a wrapper around this {@link CControl} allowing to inspect and modify the layouts
     * that are available
     * @see #load(String)
     * @see #save(String)
     */
    public CControlPerspective getPerspectives(){
    	return new CControlPerspective( access );
    }

    /**
     * Sets the root window of the application. The root window is used
     * as owner of any dialog that is created. Already existing dialogs
     * will not change their owner.
     * @param window the new owner, can be <code>null</code>
     */
    public void setRootWindow( WindowProvider window ){
        frontend.setOwner( window );
    }

    /**
     * Gets the root window of the application. Note that this method might
     * not return the same object as given to {@link #setRootWindow(WindowProvider)},
     * however the provide returned by this method will return the same window
     * as specified by {@link #setRootWindow(WindowProvider)}.
     * @return the provider, never <code>null</code>
     */
    public WindowProvider getRootWindow(){
        return frontend.getOwner();
    }

    /**
     * Gets the storage container for {@link PreferenceModel}s for this control.
     * The contents of this container are stored in the
     * {@link #getResources() resource manager}.
     * @return the storage for preferences
     * @see #getResources()
     */
    public PreferenceStorage getPreferences(){
        return preferences;
    }

    /**
     * Sets the {@link PreferenceModel} which will be used to translate between
     * <code>this</code> and the {@link #getPreferences() preferences}. This
     * model can be set to <code>null</code>.<br>
     * The default value of this property is <code>null</code>.
     * @param preferenceModel the new model, it will used to translate
     * the contents of {@link #getPreferences()} immediately, can be <code>null</code>
     */
    public void setPreferenceModel( PreferenceModel preferenceModel ) {
        if( this.preferenceModel != null ){
            this.preferenceModel.read();
            preferences.store( this.preferenceModel );
        }
        this.preferenceModel = preferenceModel;
        if( preferenceModel != null ){
            preferences.load( preferenceModel, false );
            preferenceModel.write();
        }
    }

    /**
     * Gets the preference model which is used to translate between the 
     * {@link #getPreferences() preferences} and <code>this</code>.
     * @return the model, can be <code>null</code>
     * @see #setPreferenceModel(PreferenceModel)
     */
    public PreferenceModel getPreferenceModel() {
        return preferenceModel;
    }

    /**
     * Sets the strategy that tells what to do if layout information of a missing
     * {@link CDockable} is found.
     * @param missingStrategy the strategy, <code>null</code> will set
     * the default strategy
     */
    public void setMissingStrategy( MissingCDockableStrategy missingStrategy ) {
        if( missingStrategy == null ){
            this.missingStrategy = MissingCDockableStrategy.PURGE;
        }
        else{
            this.missingStrategy = missingStrategy;
        }
    }

    /**
     * Gets the strategy that tells what to do if layout information of a missing
     * {@link CDockable} is found.
     * @return the strategy, never <code>null</code>
     */
    public MissingCDockableStrategy getMissingStrategy() {
        return missingStrategy;
    }

    /**
     * Adds a {@link ResizeRequestListener} to this {@link CControl}. The listener
     * will be informed when the resize requests of a {@link CDockable} should
     * be processed. 
     * @param listener the new listener, not <code>null</code>
     */
    public void addResizeRequestListener( ResizeRequestListener listener ){
        if( listener == null )
            throw new NullPointerException( "listener must not be null" );
        resizeListeners.add( listener );
    }

    /**
     * Removes a {@link ResizeRequestListener} from this {@link CControl}.
     * @param listener the listener to remove
     */
    public void removeResizeRequestListener( ResizeRequestListener listener ){
        resizeListeners.remove( listener );
    }

    /**
     * Informs all {@link ResizeRequestListener}s, that the
     * {@link CDockable#getAndClearResizeRequest() resize request} of all 
     * <code>CDockable</code>s should be processed. There are no
     * guarantees that a resize requests can be granted or even gets processed.<br>
     * All requests, independent from whether they were processed, will be deleted 
     * by this method.<br>
     * Note that a request might conflict with a "resize lock"
     * {@link CDockable#isResizeLockedHorizontally()} and 
     * {@link CDockable#isResizeLockedVertically()}. The behavior of that case is not
     * specified, but clients can assume that the locked components introduce
     * additional resize requests.
     */
    public void handleResizeRequests(){
        ResizeRequestListener[] listeners = resizeListeners.toArray( new ResizeRequestListener[ resizeListeners.size() ] );
        for( ResizeRequestListener listener : listeners )
            listener.handleResizeRequest( this );

        for( CDockable dockable : register.getDockables() )
            dockable.getAndClearResizeRequest();
    }

    /**
     * Gets the representation of the layer beneath the common-layer.
     * @return the entry point to DockingFrames
     */
    public CDockFrontend intern(){
        return frontend;
    }
    
    /**
     * Gets the {@link DockController} which is used by this {@link CControl}. 
     * @return the core system of the framework
     */
    public DockController getController(){
    	return intern().getController();
    }
    
    /**
     * Tells this control whether basic modes like "normalized", "minimized" or "externalized" are forced upon
     * {@link Dockable}s after loading a persistent layout. Basically if this property is set, then all {@link Dockable}s
     * are un-maximized after a layout change. The default value of this property is <code>true</code>.<br>
     * The reasons behind forcing basic modes are:
     * <ul>
     * 	<li>If the user changes the layout, he/she most likely would like to see the effects. A maximized {@link Dockable} would
     *  hide the effects.</li>
     *  <li>For the user re-maximizing an element requires no more than one click with the mouse. It's a cheap operation.</li>
     *  <li>It is an additional layer of security preventing {@link Dockable}s from being in the wrong position if the client
     *  was stared with new settings.</li>
     * </ul>
     * @param revert whether non-basic modes should be forbidden when loading a persistent layout
     */
    public void setRevertToBasicModes( boolean revert ){
    	intern().setRevertToBasicModes( revert );
    }
    
    /**
     * Tells whether basic modes are forcibly applied when loading a persistent layout.
     * @return whether the non-basic modes are forbidden
     * @see #setRevertToBasicModes(boolean)
     */
    public boolean isRevertToBasicModes(){
    	return intern().isRevertToBasicModes();
    }

    /**
     * Writes the current and all known layouts into <code>file</code>.<br>
     * This is the same as calling <code>getResources().writeFile( file )</code>.
     * @param file the file to override
     * @throws IOException if the file can't be written
     */
    public void write( File file ) throws IOException{
        getResources().writeFile( file );
    }

    /**
     * Writes the current and all known layouts into <code>out</code>.<br>
     * This is the same as calling <code>getResources().writeStream( out )</code>.
     * @param out the stream to write into
     * @throws IOException if the stream is not writable
     */
    public void write( DataOutputStream out ) throws IOException{
        getResources().writeStream( out );
    }

    /**
     * Writes the current and all known layouts into <code>element</code>.<br>
     * This is the same as calling <code>getResources().writeXML( element )</code>.
     * @param element the element to write into
     */
    public void writeXML( XElement element ){
        getResources().writeXML( element );
    }

    /**
     * Writes the current and all known layouts into <code>file</code> in xml format.
     * @param file the file to write into
     * @throws IOException if the file is not writable
     */
    public void writeXML( File file ) throws IOException{
        XElement root = new XElement( "root" );
        getResources().writeXML( root );
        BufferedOutputStream out = new BufferedOutputStream( new FileOutputStream( file ));
        XIO.writeUTF( root, out );
        out.close();
    }
    
    /**
     * Reads the current and other known layouts from <code>file</code>.<br>
     * This is the same as calling <code>getResources().readFile( file )</code>.
     * @param file the file to read from
     * @throws IOException if the file can't be read
     */
    public void read( File file ) throws IOException{
        getResources().readFile( file );
    }

    /**
     * Reads the current and other known layouts from <code>in</code>.<br>
     * This is the same as calling <code>getResources().readStream( in )</code>.
     * @param in the stream to read from
     * @throws IOException if the stream can't be read
     */
    public void read( DataInputStream in ) throws IOException{
        getResources().readStream( in );
    }
    
    /**
     * Reads the current and other known layouts from <code>element</code>.<br>
     * This is the same as calling <code>getResources().readXML( element )</code>.
     * @param element the element to read
     * @throws XException if the xml file has the wrong structure
     */
    public void readXML( XElement element ){
        getResources().readXML( element );
    }
    
    /**
     * Reads the current and other known layouts from <code>file</code>.
     * @param file the file to open and to read
     * @throws IOException if the file cannot be read
     * @throws XException if the xml file has the wrong structure
     */
    public void readXML( File file ) throws IOException{
        BufferedInputStream in = new BufferedInputStream( new FileInputStream( file ));
        XElement element = XIO.readUTF( in );
        in.close();
        readXML( element );
    }

    /**
     * Stores the current layout with the given name.
     * @param name the name of the current layout.
     */
    public void save( String name ){
        frontend.save( name );
    }

    /**
     * Loads an earlier stored layout.
     * @param name the name of the layout.
     */
    public void load( String name ){
        frontend.load( name );
    }

    /**
     * Deletes a layout that has been stored earlier.
     * @param name the name of the layout to delete
     */
    public void delete( String name ){
        frontend.delete( name );
    }

    /**
     * Gets a list of all layouts that are currently known.
     * @return the list of layouts
     */
    public String[] layouts(){
        Set<String> settings = frontend.getSettings();
        return settings.toArray( new String[ settings.size() ] );
    }

    /**
     * A class giving access to the internal methods of the enclosing
     * {@link CControl}.
     * @author Benjamin Sigg
     */
    private class Access implements CControlAccess{
        /** action used to close {@link CDockable}s  */
        private CCloseAction closeAction;

        public CControl getOwner(){
            return CControl.this;
        }

        public void link( CDockable dockable, CDockableAccess access ) {
            if( access == null ){
                accesses.remove( dockable );
                dockable.removeCDockablePropertyListener( listenerCollection.getCDockablePropertyListener() );
                dockable.removeCDockableStateListener( listenerCollection.getCDockableStateListener() );
            }
            else{
                if( accesses.put( dockable, access ) == null ){
                    dockable.addCDockablePropertyListener( listenerCollection.getCDockablePropertyListener() );
                    dockable.addCDockableStateListener( listenerCollection.getCDockableStateListener() );
                }
            }
        }

        public CDockableAccess access( CDockable dockable ) {
            return accesses.get( dockable );
        }

        public void hide( CDockable dockable ){
        	if( !dockable.isVisible() )
        		return;
        	
            DockRegister register = frontend.getController().getRegister();
            register.setStalled( true );
            try{
            	Map<Dockable, ExtendedMode> nonBasic = new HashMap<Dockable, ExtendedMode>();
            	
            	for( Dockable check : locationManager.listDockables() ){
            		if( check != dockable.intern() ){
	            		CLocationMode mode = locationManager.getCurrentMode( check );
	            		if( mode != null && !mode.isBasicMode() ){
	            			nonBasic.put( check, mode.getExtendedMode() );
	            		}
            		}
            	}
            	
                boolean changes = locationManager.ensureBasicModes();

                frontend.hide( dockable.intern() );

                if( changes ){
                	for( Map.Entry<Dockable, ExtendedMode> entry : nonBasic.entrySet() ){
                		if( frontend.isShown( entry.getKey() ) && locationManager.isModeAvailable( entry.getKey(), entry.getValue() )){
                			locationManager.setMode( entry.getKey(), entry.getValue() );
                		}
                	}
                }
            }
            finally{
                register.setStalled( false );
            }
        }

        public void show( CDockable dockable ){
        	if( dockable.isVisible() )
        		return;
        	
            DockRegister register = frontend.getController().getRegister();
            register.setStalled( true );
            try{
                CStation<?> area = dockable.getWorkingArea();
                if( area != null && area.asDockable() != null ){
                    if( !area.asDockable().isVisible() ){
                        throw new IllegalStateException( "A dockable that wants to be on a CWorkingArea can't be made visible unless the CWorkingArea is visible." );
                    }
                }

                CDockableAccess access = access( dockable );
                CLocation location = null;
                if( access != null ){
                    location = access.internalLocation();
                }
                if( location == null ){
                    if( !frontend.hasLocation( dockable.intern() )){
                    	if( area != null )
                    		location = area.getStationLocation();
                    	else
                    		location = defaultLocation;
                    }
                }

                if( location == null ){
                    frontend.show( dockable.intern(), false );
                }
                else{
                    locationManager.setLocation( dockable.intern(), location );
                }
                locationManager.ensureValidLocation( dockable );
            }
            finally{
                register.setStalled( false );
            }
        }

        public boolean isVisible( CDockable dockable ){
            return frontend.isShown( dockable.intern() );
        }

        public String getFactoryId( MultipleCDockableFactory<?,?> factory ){
            for( Map.Entry<String, MultipleCDockableFactory<?, ?>> entry : register.getFactories().entrySet() ){
                if( entry.getValue() == factory ){
                    return entry.getKey();
                }
            }

            return null;
        }

        public CLocationModeManager getLocationManager() {
            return locationManager;
        }

        public DockAction createCloseAction( final CDockable fdockable ) {
            if( closeAction == null )
                closeAction = new CCloseAction( CControl.this );

            return closeAction.intern();
        }

        public MutableCControlRegister getRegister() {
            return register;
        }

        public boolean shouldStore( String key ) {
            return CControl.this.shouldStore( key );
        }
        
        public String shouldStore( CDockable dockable ) {
            return CControl.this.shouldStore( dockable );
        }

        public void fillMultiFactories( CSetting setting ) {
            CControl.this.fillMultiFactories( setting );
        }
    }
}
