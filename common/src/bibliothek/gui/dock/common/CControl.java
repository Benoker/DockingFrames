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
import java.awt.event.*;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.*;

import javax.swing.JFrame;
import javax.swing.KeyStroke;

import bibliothek.extension.gui.dock.theme.SmoothTheme;
import bibliothek.extension.gui.dock.theme.eclipse.EclipseTabDockAction;
import bibliothek.gui.*;
import bibliothek.gui.dock.DockElement;
import bibliothek.gui.dock.FlapDockStation;
import bibliothek.gui.dock.ScreenDockStation;
import bibliothek.gui.dock.SplitDockStation;
import bibliothek.gui.dock.action.ActionGuard;
import bibliothek.gui.dock.action.DockAction;
import bibliothek.gui.dock.action.DockActionSource;
import bibliothek.gui.dock.common.event.*;
import bibliothek.gui.dock.common.intern.*;
import bibliothek.gui.dock.common.intern.station.CFlapLayoutManager;
import bibliothek.gui.dock.common.intern.station.CLockedResizeLayoutManager;
import bibliothek.gui.dock.common.intern.station.ScreenResizeRequestHandler;
import bibliothek.gui.dock.common.intern.theme.CSmoothTheme;
import bibliothek.gui.dock.common.location.CExternalizedLocation;
import bibliothek.gui.dock.event.DockAdapter;
import bibliothek.gui.dock.event.DockableFocusListener;
import bibliothek.gui.dock.event.DoubleClickListener;
import bibliothek.gui.dock.event.KeyboardListener;
import bibliothek.gui.dock.facile.action.CloseAction;
import bibliothek.gui.dock.facile.action.StateManager;
import bibliothek.gui.dock.frontend.Setting;
import bibliothek.gui.dock.layout.DockSituationIgnore;
import bibliothek.gui.dock.support.util.ApplicationResource;
import bibliothek.gui.dock.support.util.ApplicationResourceManager;
import bibliothek.gui.dock.themes.NoStackTheme;
import bibliothek.gui.dock.util.PropertyKey;
import bibliothek.gui.dock.util.PropertyValue;
import bibliothek.util.Version;
import bibliothek.util.xml.XElement;

/**
 * Manages the interaction between {@link SingleCDockable}, {@link MultipleCDockable}
 * and the {@link CContentArea}.<br>
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
    
    /** the unique id of the station that handles the externalized dockables */
    public static final String EXTERNALIZED_STATION_ID = "external";
    
    /** the unique id of the default-{@link CContentArea} created by this control */
    public static final String CONTENT_AREA_STATIONS_ID = "ccontrol";
    
    /** connection to the real DockingFrames */
	private DockFrontend frontend;
	
	/** the set of known factories */
	private Map<String, FactoryProperties> factories = 
		new HashMap<String, FactoryProperties>();
	
	/** list of all dockables registered to this control */
	private List<CDockable> dockables =
	    new ArrayList<CDockable>();
	
	/** list of all {@link SingleCDockable}s */
	private List<SingleCDockable> singleDockables =
	    new ArrayList<SingleCDockable>();
	
	/** A factory used to create missing {@link SingleCDockable}s */
	private CommonSingleDockableFactory backupFactory;
	
	/** the set of {@link MultipleCDockable}s */
	private List<MultipleCDockable> multiDockables = 
		new ArrayList<MultipleCDockable>();
	
	/** access to internal methods of some {@link CDockable}s */
	private Map<CDockable, CDockableAccess> accesses = new HashMap<CDockable, CDockableAccess>();
	
	/** a manager allowing the user to change the extended-state of some {@link CDockable}s */
	private CStateManager stateManager;
	
	/** the default location of newly opened {@link CDockable}s */
	private CLocation defaultLocation;
	
	/** the center component of the main-frame */
	private CContentArea content;
	
	/** the whole list of contentareas known to this control, includes {@link #content} */
	private List<CContentArea> contents = new ArrayList<CContentArea>();
	
	/** the stations known to this control */
	private List<CStation> stations = new ArrayList<CStation>();
	
	/** Access to the internal methods of this control */
	private CControlAccess access = new Access();
	
	/** manager used to store and read configurations */
	private ApplicationResourceManager resources = new ApplicationResourceManager();
	
	/** a list of listeners which are to be informed when this control is no longer in use */
	private List<DestroyHook> hooks = new ArrayList<DestroyHook>();
	
	/** factory used to create new elements for this control */
	private CControlFactory factory;
	
	/** the list of listeners to this {@link CControl} */
	private List<CControlListener> listeners = new ArrayList<CControlListener>();
	
	/** the list of resize-listeners */
	private List<ResizeRequestListener> resizeListeners = new ArrayList<ResizeRequestListener>();
	
	/** the collection of global listeners */
	private CListenerCollection listenerCollection = new CListenerCollection();

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
     * @param frame the main frame of the application, needed to create
     * dialogs for externalized {@link CDockable}s
     * @param restrictedEnvironment whether this application runs in a
     * restricted environment and is not allowed to listen for global events.
     */
    public CControl( JFrame frame, boolean restrictedEnvironment ){
        this( frame, restrictedEnvironment ? new SecureControlFactory() : new EfficientControlFactory() );
    }
	
	/**
	 * Creates a new control
	 * @param frame the main frame of the application, needed to create
	 * dialogs for externalized {@link CDockable}s
	 * @param factory a factory which is used to create new elements for this
	 * control.
	 */
	public CControl( JFrame frame, CControlFactory factory ){
	    this.factory = factory;
	    
	    DockController controller = factory.createController( this );
	    controller.setSingleParentRemover( new CSingleParentRemover( this ) );
	    
	    initFocusListeners( controller );
	    initInputListener( controller );
	    
		frontend = new DockFrontend( controller, frame ){
		    @Override
		    protected Setting createSetting() {
		        CSetting setting = new CSetting();
		        setting.setModes(
		                new StateManager.StateManagerSetting<StateManager.Location>( 
		                        new StateManager.LocationConverter() ) );
		        return setting;
		    }
		    
		    @Override
		    public Setting getSetting( boolean entry ) {
		        CSetting setting = (CSetting)super.getSetting( entry );
		        setting.setModes( stateManager.getSetting( new StateManager.LocationConverter() ) );
		        return setting;
		    }
		    
		    @Override
		    public void setSetting( Setting setting, boolean entry ) {
		        if( entry ){
                    stateManager.normalizeAllWorkingAreaChildren();
                }
		        
		        super.setSetting( setting, entry );
		        stateManager.setSetting( ((CSetting)setting).getModes() );
		    }
		};
		frontend.setIgnoreForEntry( new DockSituationIgnore(){
		    public boolean ignoreChildren( DockStation station ) {
		        CStation cstation = getStation( station );
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
		frontend.setShowHideAction( false );
		
		frontend.getController().setTheme( new NoStackTheme( new CSmoothTheme( this, new SmoothTheme())));
		frontend.getController().addActionGuard( new ActionGuard(){
		    public boolean react( Dockable dockable ) {
		        return dockable instanceof CommonDockable;
		    }
		    public DockActionSource getSource( Dockable dockable ) {
		        return ((CommonDockable)dockable).getClose();
		    }
		});
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
		
		frontend.getController().addAcceptance( new StackableAcceptance() );
		frontend.getController().addAcceptance( new WorkingAreaAcceptance( access ) );
		frontend.getController().addAcceptance( new ExtendedModeAcceptance( access ) );
		
		backupFactory = new CommonSingleDockableFactory( this );
		frontend.registerFactory( backupFactory );
		frontend.registerBackupFactory( backupFactory );
		
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
		}
		catch( IOException ex ){
		    System.err.println( "Non lethal IO-error:" );
		    ex.printStackTrace();
		}
		
		initExtendedModes( frame );
		initProperties();
	}
	
	/**
	 * Creates and adds the listeners needed to track the focus.
	 * @param controller the controller which will be observed
	 */
	private void initFocusListeners( DockController controller ){
	    controller.addDockableFocusListener( new DockableFocusListener(){
	        public void dockableFocused( DockController controller,
	                Dockable oldFocused, Dockable newFocused ) {

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
	        public void dockableSelected( DockController controller,
	                DockStation station, Dockable oldSelected,
	                Dockable newSelected ) {
	            // ignore
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
	 * Sets up the {@link #stateManager}.
	 * @param frame base for the {@link ScreenDockStation}
	 */
	private void initExtendedModes( JFrame frame ){
        stateManager = new CStateManager( access );
        
        final ScreenDockStation screen = factory.createScreenDockStation( frame );
        
        // frontend.addRoot( screen, EXTERNALIZED_STATION_ID );
        CStation screenStation = new AbstractCStation( screen, EXTERNALIZED_STATION_ID, CExternalizedLocation.STATION ){
            private ScreenResizeRequestHandler handler = new ScreenResizeRequestHandler( screen );
            
            @Override
            protected void install( CControlAccess access ) {
                access.getOwner().addResizeRequestListener( handler );
                access.getStateManager().add( EXTERNALIZED_STATION_ID, screen );
            }
            @Override
            protected void uninstall( CControlAccess access ) {
                access.getOwner().removeResizeRequestListener( handler );
                access.getStateManager().remove( EXTERNALIZED_STATION_ID );
            }
        };
        
        add( screenStation, true );
        
        screen.setShowing( frame.isVisible() );
        frame.addComponentListener( new ComponentListener(){
            public void componentShown( ComponentEvent e ) {
                screen.setShowing( true );
            }
            public void componentHidden( ComponentEvent e ) {
                screen.setShowing( false );
            }
            public void componentMoved( ComponentEvent e ) {
                // ignore
            }
            public void componentResized( ComponentEvent e ) {
                // ignore
            }
        });
	}
	
	/**
	 * Sets up the default properties.
	 */
	private void initProperties(){
        putProperty( KEY_MAXIMIZE_CHANGE, KeyStroke.getKeyStroke( KeyEvent.VK_M, InputEvent.CTRL_MASK ) );
        putProperty( KEY_GOTO_EXTERNALIZED, KeyStroke.getKeyStroke( KeyEvent.VK_E, InputEvent.CTRL_MASK ) );
        putProperty( KEY_GOTO_NORMALIZED, KeyStroke.getKeyStroke( KeyEvent.VK_N, InputEvent.CTRL_MASK ) );
        putProperty( KEY_CLOSE, KeyStroke.getKeyStroke( KeyEvent.VK_F4, InputEvent.CTRL_MASK ) );
        putProperty( SplitDockStation.LAYOUT_MANAGER, new CLockedResizeLayoutManager() );
        putProperty( FlapDockStation.LAYOUT_MANAGER, new CFlapLayoutManager() );	    
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
	public void removeKeybaordListener( CKeyboardListener listener ){
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
	 * Gets a list of currently registered listeners.
	 * @return the listeners
	 */
	private CControlListener[] listeners(){
	    return listeners.toArray( new CControlListener[ listeners.size() ] );
	}
	
	/**
	 * Writes a map using the unique identifiers of each {@link SingleCDockable} to
	 * tell to which {@link CWorkingArea} it belongs.
	 * @param out the stream to write into
	 * @throws IOException if an I/O error occurs
	 */
	private void writeWorkingAreas( DataOutputStream out ) throws IOException{
	    Map<String,String> map = new HashMap<String, String>();
	    
	    for( SingleCDockable dockable : singleDockables ){
	        CStation area = dockable.getWorkingArea();
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
	    for( SingleCDockable dockable : singleDockables ){
	        CStation area = dockable.getWorkingArea();
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
	    Map<String, CStation> areas = new HashMap<String, CStation>();
	    
	    for( CStation station : stations ){
	        if( station.isWorkingArea() ){
	            areas.put( station.getUniqueId(), station );
	        }
	    }
	    
	    for( SingleCDockable dockable : this.singleDockables ){
	        dockables.put( dockable.getUniqueId(), dockable );
	    }
	    
	    for( int i = 0, n = in.readInt(); i<n; i++ ){
	        String key = in.readUTF();
	        String value = in.readUTF();
	        
	        CDockable dockable = dockables.get( key );
	        if( dockable != null ){
	            CStation area = areas.get( value );
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
        Map<String, CStation> areas = new HashMap<String, CStation>();
        
        for( CStation station : stations ){
            if( station.isWorkingArea() ){
                areas.put( station.getUniqueId(), station );
            }
        }
        
        for( SingleCDockable dockable : this.singleDockables ){
            dockables.put( dockable.getUniqueId(), dockable );
        }
        
        for( XElement xarea : element.getElements( "area" )){
            String key = xarea.getString( "child" );
            String value = xarea.getString( "id" );
            
            CDockable dockable = dockables.get( key );
            if( dockable != null ){
                CStation area = areas.get( value );
                dockable.setWorkingArea( area );
            }
        }
    }
	
	/**
	 * Frees as much resources as possible. This {@link CControl} will no longer
	 * work correctly after this method was called.
	 */
	public void destroy(){
	    frontend.getController().kill();
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
	    CWorkingArea area = factory.createWorkingArea( uniqueId );
	    add( area );
	    add( area, false );
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
	    add( area, true );
	    return area;
	}
	
	/**
	 * Creates a new area where normalized {@link CDockable}s can be stored.
	 * This method adds the new area directly as a root station to this control
	 * @param uniqueId a unique identifier
	 * @return the new area
	 */
	public CGridArea createGridArea( String uniqueId ){
	    CGridArea area = new CGridArea( this, uniqueId, false );
	    add( area, true );
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
        
        for( CStation station : stations ){
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
        
        for( CContentArea center : contents ){
            if( center.getUniqueId().equals( uniqueId ))
                throw new IllegalArgumentException( "There exists already a CContentArea with the unique id " + uniqueId );
        }
        
        CContentArea center = new CContentArea( this, uniqueId );
        if( frontend.getDefaultStation() == null )
            frontend.setDefaultStation( center.getCenter() );
        addContentArea( center );
        return center;
    }
    
    /**
     * Adds a new {@link CContentArea} to this control.
     * @param content the new area
     * @throws IllegalArgumentException if the area is already in use or if
     * the area was not created using <code>this</code>
     */
    public void addContentArea( CContentArea content ){
        if( content == null )
            throw new NullPointerException( "content must not be null" );
        
        if( content.getControl() != this )
            throw new IllegalArgumentException( "content was not created using this CControl" );
        
        if( contents.contains( content ))
            throw new IllegalArgumentException( "content already in use" );
        
        contents.add( content );
        boolean check = !(this.content == null || content != this.content);
        
        for( CStation station : content.getStations() ){
            add( station, true, check );
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
	    
		if( this.content == content )
			throw new IllegalArgumentException( "The default-contentarea can't be removed" );
		
		if( contents.remove( content ) ){
		    for( CStation station : content.getStations() ){
		        remove( station );
		    }
		}
	}
	
	/**
	 * Gets an unmodifiable list of all {@link CContentArea}s registered at
	 * this control
	 * @return the list of contentareas
	 */
	public List<CContentArea> getContentAreas(){
		return Collections.unmodifiableList( contents );
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
	 * Note: clients should be careful when working with the state manager. 
	 * Changing the properties of the state manager might introduce failures that
	 * are not visible directly.
	 * @return the manager
	 */
	public CStateManager getStateManager() {
        return stateManager;
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
	    frontend.getController().getProperties().set( key, value );
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
	 * Gets the element that should be in the center of the mainframe.
	 * @return the center of the mainframe of the application
	 */
	public CContentArea getContentArea() {
	    if( content == null ){
	        content = createContentArea( CONTENT_AREA_STATIONS_ID );
	        if( frontend.getDefaultStation() == null )
	            frontend.setDefaultStation( content.getCenter() );
	    }
	    
        return content;
    }
	
	/**
	 * Adds an additional station to this control.
	 * @param station the new station
	 * @param root <code>true</code> if the station should become a root station,
	 * which means that the station will not have any parent. <code>false</code>
	 * if the station will have another parent, that is often the case if the
	 * station is a {@link CDockable} as well.
	 */
    public void add( CStation station, boolean root ){
        add( station, root, true );
    }
    
    /**
     * Adds an additional station to this control.
     * @param station the new station
     * @param root <code>true</code> if the station should become a root station,
     * which means that the station will not have any parent. <code>false</code>
     * if the station will have another parent, that is often the case if the
     * station is a {@link CDockable} as well.
     * @param check if <code>true</code> a check of the unique id is performed,
     * otherwise the station is just put into, perhaps wrongly replacing
     * other stations.
     */    
    private void add( CStation station, boolean root, boolean check ){
        String id = station.getUniqueId();
        if( check )
            checkStationIdentifierUniqueness( id );
        
        if( root ){
            frontend.addRoot( station.getStation(), id );
        }
        
        station.setControl( access );
        stations.add( station );
    }
    
    /**
     * Removes a {@link CStation} from this control. It is unspecified what
     * happens with the children on <code>station</code>
     * @param station the statio to remove
     */
    public void remove( CStation station ){
        if( stations.remove( station ) ){
            frontend.removeRoot( station.getStation() );
            station.setControl( null );
        }
    }
    
    /**
     * Gets an unmodifiable list of all stations that are currently 
     * registered at this control.
     * @return the list of stations
     */
    public List<CStation> getStations(){
        return Collections.unmodifiableList( stations );
    }
    
    /**
     * Searches the {@link CStation} whose {@link CStation#getStation() internal representation}
     * is <code>intern</code>.
     * @param intern the internal representation
     * @return the station or <code>null</code>
     */
    public CStation getStation( DockStation intern ){
        for( CStation station : stations ){
            if( station.getStation() == intern )
                return station;
        }
        return null;
    }
	
	/**
	 * Adds a dockable to this control. The dockable can be made visible afterwards.
	 * @param <S> the type of the new element
	 * @param dockable the new element to show
	 * @return <code>dockable</code>
	 */
	public <S extends SingleCDockable> S add( S dockable ){
		if( dockable == null )
			throw new NullPointerException( "dockable must not be null" );
		
		if( dockable.getControl() != null )
			throw new IllegalStateException( "dockable is already part of a control" );

		dockable.setControl( access );
		String id = toSingleId( dockable.getUniqueId() );
		accesses.get( dockable ).setUniqueId( id );
		frontend.add( dockable.intern(), id );
		frontend.setHideable( dockable.intern(), true );
		dockables.add( dockable );
		singleDockables.add( dockable );
		
		for( CControlListener listener : listeners() )
            listener.added( CControl.this, dockable );
		
		return dockable;
	}
	   
    /**
     * Removes a dockable from this control. The dockable is made invisible.
     * @param dockable the element to remove
     */
    public void remove( SingleCDockable dockable ){
        if( dockable == null )
            throw new NullPointerException( "dockable must not be null" );
        
        if( dockable.getControl() == access ){
            dockable.setVisible( false );
            frontend.remove( dockable.intern() );
            dockables.remove( dockable );
            singleDockables.remove( dockable );
            dockable.setControl( null );
            
            if( backupFactory.getFactory( dockable.getUniqueId() ) == null )
                stateManager.remove( dockable.intern() );
            else
                stateManager.reduceToEmpty( dockable.intern() );
            
            for( CControlListener listener : listeners() )
                listener.removed( CControl.this, dockable );
        }
    }
    
    /**
     * Adds a backup factory to this control. The backup factory will be used
     * to create and add a {@link SingleCDockable} when one is requested that
     * is not yet in the cache.
     * @param id the id of the dockable that might be requested
     * @param backupFactory the new factory
     */
    public void addSingleBackupFactory( String id, SingleCDockableBackupFactory backupFactory ){
        this.backupFactory.add( id, backupFactory );
        
        id = toSingleId( id );
        stateManager.addEmpty( id );
        frontend.addEmpty( id );
    }
    
    /**
     * Removes a backup factory from this control.
     * @param id the name of the factory
     * @see #addSingleBackupFactory(String, SingleCDockableBackupFactory)
     */
    public void removeSingleBackupFactory( String id ){
        this.backupFactory.remove( id );
        
        id = toSingleId( id );
        stateManager.removeEmpty( id );
        frontend.removeEmpty( id );
    }
    
    private String toSingleId( String id ){
        return "single " + id;
    }
    
	/**
	 * Adds a dockable to this control. The dockable can be made visible afterwards.
	 * @param <F> the type of the new element
	 * @param dockable the new element to show
	 * @return <code>dockable</code>
	 */
	public <F extends MultipleCDockable> F add( F dockable ){
	    Set<String> ids = new HashSet<String>();

        String factory = access.getFactoryId( dockable.getFactory() );
        if( factory == null ){
            throw new IllegalStateException( "the factory for a MultipleCDockable is not registered: " + dockable.getFactory() );
        }
         
        for( MultipleCDockable multi : multiDockables ){
            if( factory.equals( access.getFactoryId( multi.getFactory() ))){
                ids.add( accesses.get( multi ).getUniqueId() );
            }
        }
        
        int count = 0;
        String id = count + " " + factory;
        while( ids.contains( "multi " + id )){
            count++;
            id = count + " " + factory;
        }
        
        return add( dockable, id );
	}

	/**
     * Adds a dockable to this control. The dockable can be made visible afterwards.
     * This method will throw an exception when the unique identifier is already
     * in use. Clients better use {@link #add(MultipleCDockable)}.
     * @param <M> the type of the new element
     * @param dockable the new element to show
     * @param uniqueId id the unique id of the new element
     * @return <code>dockable</code>
     * @throws IllegalArgumentException if the unique identifier is already in
     * use, if <code>dockable</code> is already used elsewhere, if there is
     * no factory for <code>dockable</code>
     * @throws NullPointerException if any argument is <code>null</code>
     */
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
		
		uniqueId = "multi " + uniqueId;
		
        for( MultipleCDockable multi : multiDockables ){
            if( factory.equals( access.getFactoryId( multi.getFactory() ))){
                String id = accesses.get( multi ).getUniqueId();
                if( uniqueId.equals( id ))
                    throw new IllegalArgumentException( "The unique identifier is already in use: " + uniqueId );
            }
        }
		
		dockable.setControl( access );
		accesses.get( dockable ).setUniqueId( uniqueId );
		multiDockables.add( dockable );
		dockables.add( dockable );
		
		for( CControlListener listener : listeners() )
            listener.added( CControl.this, dockable );
		
		return dockable;
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
			multiDockables.remove( dockable );
			dockables.remove( dockable );
			String factory = access.getFactoryId( dockable.getFactory() );
			
			if( factory == null ){
				throw new IllegalStateException( "the factory for a MultipleDockable is not registered" );
			}
			
			factories.get( factory ).count--;
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
	    return dockables.size();
	}
	
	/**
	 * Gets the index'th dockable that is registered in this control
	 * @param index the index of the element
	 * @return the selected dockable
	 */
	public CDockable getCDockable( int index ){
	    return dockables.get( index );
	}
	
	/**
	 * Adds a factory to this control. The factory will create {@link MultipleCDockable}s
	 * when a layout is loaded.
	 * @param id the unique id of the factory
	 * @param factory the new factory
	 */
	public void add( final String id, final MultipleCDockableFactory<?,?> factory ){
		if( id == null )
			throw new NullPointerException( "id must not be null" );
		
		if( factory == null )
			throw new NullPointerException( "factory must not be null" );
		
		if( factories.containsKey( id )){
			throw new IllegalArgumentException( "there is already a factory named " + id );
		}
		
		FactoryProperties properties = new FactoryProperties();
		properties.factory = factory;
		
		factories.put( id, properties );
		
		frontend.registerFactory( new CommonMultipleDockableFactory( id, factory, access ) );
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
	 * Sets the {@link CMaximizeBehavior}. The behavior decides what happens
	 * when the user want's to maximize or to un-maximize a {@link CDockable}.
	 * @param behavior the new behavior, not <code>null</code>
	 */
	public void setMaximizeBehavior( CMaximizeBehavior behavior ){
		stateManager.setMaximizeBehavior( behavior );
	}
	
	/**
	 * Makes sure that all {@link CDockable}s are maximized onto the area
	 * which is registered under the given unique id.
	 * @param id the unique id of the area
	 * @see CGridArea#getUniqueId()
	 * @see CContentArea#getCenterIdentifier()
	 */
	public void setMaximizeArea( String id ){
	    stateManager.setMaximizingStation( id );
	}
	
	/**
	 * Gets the currently used maximize-behavior.
	 * @return the behavior, not <code>null</code>
	 * @see #setMaximizeBehavior(CMaximizeBehavior)
	 */
	public CMaximizeBehavior getMaximizeBehavior(){
		return stateManager.getMaximizeBehavior();
	}
	
	/**
	 * Sets the theme of the elements in the realm of this control.
	 * @param theme the new theme
	 */
	public void setTheme( DockTheme  theme ){
	    frontend.getController().setTheme( theme );
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
	 * {@link CDockable#isResizeLocked()}. The behavior of that case is not
	 * specified, but clients can assume that the locked components introduce
	 * additional resize requests.
	 */
	public void handleResizeRequests(){
	    ResizeRequestListener[] listeners = resizeListeners.toArray( new ResizeRequestListener[ resizeListeners.size() ] );
	    for( ResizeRequestListener listener : listeners )
	        listener.handleResizeRequest();
	    
	    for( CDockable dockable : dockables )
	        dockable.getAndClearResizeRequest();
	}
	
	/**
	 * Gets the representation of the layer beneath the common-layer.
	 * @return the entry point to DockingFrames
	 */
	public DockFrontend intern(){
		return frontend;
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
     * This is the same as calling <code>getResources().readStream( out )</code>.
	 * @param in the stream to read from
	 * @throws IOException if the stream can't be read
     */
	public void read( DataInputStream in ) throws IOException{
		getResources().readStream( in );
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
	 * Properties associated with one factory.
	 * @author Benjamin Sigg
	 *
	 */
	private static class FactoryProperties{
		/** the associated factory */
		public MultipleCDockableFactory<?,?> factory;
		/** the number of {@link MultipleCDockable} that belong to {@link #factory} */
		public int count = 0;
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
	    
	    public <F extends MultipleCDockable> F add( F dockable, String uniqueId ) {
	        return CControl.this.add( dockable, uniqueId );
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
			frontend.hide( dockable.intern() );
		}
		
		public void show( CDockable dockable ){
		    CStation area = dockable.getWorkingArea();
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
				if( !frontend.hasLocation( dockable.intern() ))
					location = defaultLocation;
			}
			
			if( location == null ){
			    frontend.show( dockable.intern() );
			}
			else{
				stateManager.setLocation( dockable.intern(), location );
			}
			stateManager.ensureValidLocation( dockable );
		}
		
		public boolean isVisible( CDockable dockable ){
			return frontend.isShown( dockable.intern() );
		}
		
		public String getFactoryId( MultipleCDockableFactory<?,?> factory ){
			for( Map.Entry<String, FactoryProperties> entry : factories.entrySet() ){
				if( entry.getValue().factory == factory )
					return entry.getKey();
			}
			
			return null;
		}
		
		public CStateManager getStateManager() {
		    return stateManager;
		}
		
		public DockAction createCloseAction( final CDockable fdockable ) {
		    if( closeAction == null )
		        closeAction = new CCloseAction();
		    
		    return closeAction;
		}
	}
	
	/**
	 * Action that can close {@link CDockable}s
	 * @author Benjamin Sigg
	 */
	@EclipseTabDockAction
	private class CCloseAction extends CloseAction{
	    /**
	     * Creates a new action
	     */
	    public CCloseAction(){
	        super( frontend.getController() );
	        new PropertyValue<KeyStroke>( KEY_CLOSE, frontend.getController() ){
	            @Override
	            protected void valueChanged( KeyStroke oldValue, KeyStroke newValue ) {
	                setAccelerator( newValue );
	            }
	        };
	    }
	    
	    @Override
        protected void close( Dockable dockable ) {
	        CDockable cdockable = ((CommonDockable)dockable).getDockable();
	        if( cdockable.getExtendedMode() == CDockable.ExtendedMode.MAXIMIZED )
	            cdockable.setExtendedMode( CDockable.ExtendedMode.NORMALIZED );
	        cdockable.setVisible( false );
        }
	}
}
