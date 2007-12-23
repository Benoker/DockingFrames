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
package bibliothek.gui.dock.facile;

import java.awt.Component;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.KeyEvent;
import java.io.*;
import java.util.*;

import javax.swing.JFrame;
import javax.swing.KeyStroke;

import bibliothek.extension.gui.dock.theme.SmoothTheme;
import bibliothek.extension.gui.dock.theme.eclipse.EclipseTabDockAction;
import bibliothek.gui.DockController;
import bibliothek.gui.DockFrontend;
import bibliothek.gui.Dockable;
import bibliothek.gui.dock.DockFactory;
import bibliothek.gui.dock.action.ActionGuard;
import bibliothek.gui.dock.action.DockAction;
import bibliothek.gui.dock.action.DockActionSource;
import bibliothek.gui.dock.common.action.CloseAction;
import bibliothek.gui.dock.common.action.StateManager;
import bibliothek.gui.dock.event.DockAdapter;
import bibliothek.gui.dock.facile.intern.*;
import bibliothek.gui.dock.station.ScreenDockStation;
import bibliothek.gui.dock.support.util.ApplicationResource;
import bibliothek.gui.dock.support.util.ApplicationResourceManager;
import bibliothek.gui.dock.themes.NoStackTheme;
import bibliothek.gui.dock.util.PropertyKey;
import bibliothek.gui.dock.util.PropertyValue;

/**
 * Manages the interaction between {@link FSingleDockable}, {@link FMultipleDockable}
 * and the {@link FCenter}.<br>
 * Clients should call <code>read</code> and <code>write</code> of the
 * {@link ApplicationResourceManager}, accessible through {@link #getResources()}, 
 * to store or load the configuration.<br>
 * Clients which do no longer need a {@link FControl} can call {@link #destroy()}
 * to free resources.
 * @author Benjamin Sigg
 *
 */
public class FControl {
    /**
     * {@link KeyStroke} used to change a {@link FDockable} into maximized-state,
     * or to go out of maximized-state when needed.
     */
    public static final PropertyKey<KeyStroke> KEY_MAXIMIZE_CHANGE = 
        new PropertyKey<KeyStroke>( "fcontrol.maximize_change" );
    
    /**
     * {@link KeyStroke} used to change a {@link FDockable} into
     * maximized-state.
     */
    public static final PropertyKey<KeyStroke> KEY_GOTO_MAXIMIZED =
        new PropertyKey<KeyStroke>( "fcontrol.goto_maximized" );
    
    /**
     * {@link KeyStroke} used to change a {@link FDockable} into
     * normalized-state.
     */
    public static final PropertyKey<KeyStroke> KEY_GOTO_NORMALIZED =
        new PropertyKey<KeyStroke>( "fcontrol.goto_normalized" );
    
    /**
     * {@link KeyStroke} used to change a {@link FDockable} into
     * minimized-state.
     */
    public static final PropertyKey<KeyStroke> KEY_GOTO_MINIMIZED =
        new PropertyKey<KeyStroke>( "fcontrol.goto_minimized" );
    
    /**
     * {@link KeyStroke} used to change a {@link FDockable} into
     * externalized-state.
     */
    public static final PropertyKey<KeyStroke> KEY_GOTO_EXTERNALIZED =
        new PropertyKey<KeyStroke>( "fcontrol.goto_externalized" );
    
    /**
     * {@link KeyStroke} used to close a {@link FDockable}.
     */
    public static final PropertyKey<KeyStroke> KEY_CLOSE = 
        new PropertyKey<KeyStroke>( "fcontrol.close" );
    
    /** connection to the real DockingFrames */
	private DockFrontend frontend;
	
	/** the set of known factories */
	private Map<String, FactoryProperties> factories = 
		new HashMap<String, FactoryProperties>();
	
	/** list of all dockables registered to this control */
	private List<FDockable> dockables =
	    new ArrayList<FDockable>();
	
	/** the set of {@link FMultipleDockable}s */
	private List<FMultipleDockable> multiDockables = 
		new ArrayList<FMultipleDockable>();
	
	/** access to internal methods of some {@link FDockable}s */
	private Map<FDockable, FDockableAccess> accesses = new HashMap<FDockable, FDockableAccess>();
	
	/** a manager allowing the user to change the extended-state of some {@link FDockable}s */
	private FStateManager stateManager;
	
	/** the center component of the main-frame */
	private FCenter center;
	
	/** Access to the internal methods of this control */
	private FControlAccess access = new Access();
	
	/** manager used to store and read configurations */
	private ApplicationResourceManager resources = new ApplicationResourceManager();
	
	/** a list of listeners which are to be informed when this control is no longer in use */
	private List<DestroyHook> hooks = new ArrayList<DestroyHook>();
	
	/** factory used to create new elements for this control */
	private FControlFactory factory;
	

    /**
     * Creates a new control
     * @param frame the main frame of the application, needed to create
     * dialogs for externalized {@link FDockable}s
     */
    public FControl( JFrame frame ){
        this( frame, false );
    }
	
	/**
     * Creates a new control
     * @param frame the main frame of the application, needed to create
     * dialogs for externalized {@link FDockable}s
     * @param restrictedEnvironment whether this application runs in a
     * restricted environment and is not allowed to listen for global events.
     */
    public FControl( JFrame frame, boolean restrictedEnvironment ){
        this( frame, restrictedEnvironment ? new SecureControlFactory() : new EfficientControlFactory() );
    }
	
	/**
	 * Creates a new control
	 * @param frame the main frame of the application, needed to create
	 * dialogs for externalized {@link FDockable}s
	 * @param factory a factory which is used to create new elements for this
	 * control.
	 */
	public FControl( JFrame frame, FControlFactory factory ){
	    this.factory = factory;
	    
		frontend = new DockFrontend( factory.createController(), frame ){
		    @Override
		    protected void save( DataOutputStream out, boolean entry ) throws IOException {
		        super.save( out, entry );
		        if( entry )
		            stateManager.write( new StateManager.LocationStreamTransformer(), out );
		    }
		    @Override
		    protected void load( DataInputStream in, boolean entry ) throws IOException {
		        super.load( in, entry );
		        if( entry )
		            stateManager.read( new StateManager.LocationStreamTransformer(), in );
		    }
		};
		frontend.setShowHideAction( false );
		frontend.getController().setTheme( new NoStackTheme( new SmoothTheme() ) );
		frontend.getController().addActionGuard( new ActionGuard(){
		    public boolean react( Dockable dockable ) {
		        return dockable instanceof FacileDockable;
		    }
		    public DockActionSource getSource( Dockable dockable ) {
		        return ((FacileDockable)dockable).getDockable().getClose();
		    }
		});
		frontend.getController().getRegister().addDockRegisterListener( new DockAdapter(){
		    @Override
		    public void dockableRegistered( DockController controller, Dockable dockable ) {
		        if( dockable instanceof FacileDockable ){
		            FDockableAccess access = accesses.get( ((FacileDockable)dockable).getDockable() );
		            if( access != null ){
		                access.informVisibility( true );
		            }
		        }
		    }
		    
		    @Override
		    public void dockableUnregistered( DockController controller, Dockable dockable ) {
		        if( dockable instanceof FacileDockable ){
                    FDockableAccess access = accesses.get( ((FacileDockable)dockable).getDockable() );
                    if( access != null ){
                        access.informVisibility( false );
                    }
                }
		    }
		});
		
		try{
    		resources.put( "frontend", new ApplicationResource(){
    		    public void write( DataOutputStream out ) throws IOException {
    		        frontend.write( out );
    		    }
    		    public void read( DataInputStream in ) throws IOException {
    		        frontend.read( in );
    		    }
    		});
		}
		catch( IOException ex ){
		    System.err.println( "Non lethel IO-error:" );
		    ex.printStackTrace();
		}
		
		stateManager = new FStateManager( access );
		center = new FCenter( access );
		
		final ScreenDockStation screen = factory.createScreenDockStation( frame );
		stateManager.add( "screen", screen );
		frontend.addRoot( screen, "screen" );
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
		
		// set some default values
		putProperty( KEY_MAXIMIZE_CHANGE, KeyStroke.getKeyStroke( KeyEvent.VK_M, KeyEvent.CTRL_MASK ) );
		putProperty( KEY_GOTO_EXTERNALIZED, KeyStroke.getKeyStroke( KeyEvent.VK_E, KeyEvent.CTRL_MASK ) );
		putProperty( KEY_GOTO_NORMALIZED, KeyStroke.getKeyStroke( KeyEvent.VK_N, KeyEvent.CTRL_MASK ) );
		putProperty( KEY_CLOSE, KeyStroke.getKeyStroke( KeyEvent.VK_C, KeyEvent.CTRL_MASK ) );
	}
	
	/**
	 * Frees as much resources as possible. This {@link FControl} will no longer
	 * work correctly after this method was called.
	 */
	public void destroy(){
	    frontend.getController().kill();
	    for( DestroyHook hook : hooks )
	        hook.destroy();
	}
	
	/**
	 * Gets the factory which is mainly used to create new elements for this
	 * control.
	 * @return the factory
	 */
	public FControlFactory getFactory() {
        return factory;
    }
	
	/**
	 * Monitors <code>component</code> for mouse and key-events.
	 * @param component the component to monitor
	 * @return either <code>component</code> or a new component replacing
	 * <code>component</code>.
	 */
	public Component monitor( Component component ){
	    return factory.monitor( component, this );
	}
	
	/**
	 * Adds a destroy-hook. The hook is called when this {@link FControl} is
	 * destroyed through {@link #destroy()}.
	 * @param hook the new hook
	 */
	public void addDestroyHook( DestroyHook hook ){
	    if( hook == null )
	        throw new NullPointerException( "hook must not be null" );
	    hooks.add( hook );
	}
	
	/**
	 * Removes a destroy-hook from this {@link FControl}.
	 * @param hook the hook to remove
	 */
	public void removeDestroyHook( DestroyHook hook ){
	    hooks.remove( hook );
	}
	
	/**
	 * Grants access to the manager that reads and stores configurations
	 * of the facile-framework.<br>
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
	public FCenter getCenter() {
        return center;
    }
	
	/**
	 * Adds a dockable to this control. The dockable can be made visible afterwards.
	 * @param dockable the new element to show
	 * @return <code>dockable</code>
	 */
	public <F extends FSingleDockable> F add( F dockable ){
		if( dockable == null )
			throw new NullPointerException( "dockable must not be null" );
		
		if( dockable.getControl() != null )
			throw new IllegalStateException( "dockable is already part of a control" );

		dockable.setControl( access );
		String id = "single " + dockable.getId();
		accesses.get( dockable ).setUniqueId( id );
		frontend.add( dockable.intern(), id );
		frontend.setHideable( dockable.intern(), true );
		dockables.add( dockable );
		return dockable;
	}
	
	/**
	 * Adds a dockable to this control. The dockable can be made visible afterwards.
	 * @param dockable the new element to show
	 * @return <code>dockable</code>
	 */
	public <F extends FMultipleDockable> F add( F dockable ){
	    String factory = access.getFactoryId( dockable.getFactory() );
        if( factory == null ){
            throw new IllegalStateException( "the factory for a MultipleDockable is not registered" );
        }
        
        int count = 0;
        for( FMultipleDockable multi : multiDockables ){
            if( factory.equals( access.getFactoryId( multi.getFactory() )))
                count++;
        }
        String id = "multi " + count + " " + factory;
        return add( dockable, id );
	}

	/**
     * Adds a dockable to this control. The dockable can be made visible afterwards.
     * @param dockable the new element to show
     * @param uniqueId id the unique id of the new element
     * @return <code>dockable</code>
     */
	private <F extends FMultipleDockable> F add( F dockable, String uniqueId ){
		if( dockable == null )
			throw new NullPointerException( "dockable must not be null" );
		
		if( dockable.getControl() != null )
			throw new IllegalStateException( "dockable is already part of a control" );
		
		dockable.setControl( access );
		accesses.get( dockable ).setUniqueId( uniqueId );
		multiDockables.add( dockable );
		dockables.add( dockable );
		return dockable;
	}
	
	/**
	 * Removes a dockable from this control. The dockable is made invisible.
	 * @param dockable the element to remove
	 */
	public void remove( FSingleDockable dockable ){
		if( dockable == null )
			throw new NullPointerException( "dockable must not be null" );
		
		if( dockable.getControl() == access ){
			dockable.setVisible( false );
			frontend.remove( dockable.intern() );
			dockables.remove( dockable );
			dockable.setControl( null );
		}
	}
	
	/**
	 * Removes a dockable from this control. The dockable is made invisible.
	 * @param dockable the element to remove
	 */
	public void remove( FMultipleDockable dockable ){
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
		}
	}
	
	/**
	 * Adds a factory to this control. The factory will create {@link FMultipleDockable}s
	 * when a layout is loaded.
	 * @param id the unique id of the factory
	 * @param factory the new factory
	 */
	public void add( final String id, final FMultipleDockableFactory factory ){
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
		
		frontend.registerFactory( new DockFactory<FacileDockable>(){
			public String getID(){
				return id;
			}

			public FacileDockable read( Map<Integer, Dockable> children, boolean ignoreChildren, DataInputStream in ) throws IOException{
				String id = in.readUTF();
			    FMultipleDockable dockable = factory.read( in );
			    if( dockable != null ){
			        add( dockable, id );
				    return dockable.intern();
			    }
			    return null;
			}

			public void read( Map<Integer, Dockable> children, boolean ignoreChildren, FacileDockable preloaded, DataInputStream in ) throws IOException{
				// ignore
			}

			public void write( FacileDockable element, Map<Dockable, Integer> children, DataOutputStream out ) throws IOException{
				out.writeUTF( accesses.get( element.getDockable() ).getUniqueId() );
			    factory.write( (FMultipleDockable)element.getDockable(), out );
			}
		});
	}
	
	/**
	 * Gets the representation of the layer beneath the facile-layer.
	 * @return the entry point to DockingFrames
	 */
	public DockFrontend intern(){
		return frontend;
	}
	
	/**
	 * Writes the current and all known layouts into <code>file</code>.
	 * @param file the file to override
	 * @throws IOException if the file can't be written
	 */
	public void write( File file ) throws IOException{
		DataOutputStream out = new DataOutputStream( new BufferedOutputStream( new FileOutputStream( file )));
		write( out );
		out.close();
	}
	
	/**
	 * Writes the current and all known layouts into <code>out</code>.
	 * @param out the stream to write into
	 * @throws IOException if the stream is not writable
	 */
	public void write( DataOutputStream out ) throws IOException{
		out.writeInt( 1 );
		frontend.write( out );
	}
	
	/**
	 * Reads the current and other known layouts from <code>file</code>.
	 * @param file the file to read from
	 * @throws IOException if the file can't be read
	 */
	public void read( File file ) throws IOException{
		DataInputStream in = new DataInputStream( new BufferedInputStream( new FileInputStream( file )));
		read( in );
		in.close();
	}
	
	/**
	 * Reads the current and other known layouts from <code>in</code>.
	 * @param in the stream to read from
	 * @throws IOException if the stream can't be read
	 */
	public void read( DataInputStream in ) throws IOException{
		int version = in.readInt();
		if( version != 1 )
			throw new IOException( "Version of stream unknown, expected 1 but found: " + version );
		
		frontend.read( in );
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
	private class FactoryProperties{
		/** the associated factory */
		public FMultipleDockableFactory factory;
		/** the number of {@link FMultipleDockable} that belong to {@link #factory} */
		public int count = 0;
	}
	
	/**
	 * Ensures that all {@link Dockable}s are in a valid state (minimized,
	 * maximized, normalized or externalized).
	 */
	protected void ensureValidModes(){
	    for( FDockable dockable : dockables.toArray( new FDockable[ dockables.size() ] ) ){
	        stateManager.ensureValidMode( dockable );
	    }
	}
	
	/**
	 * A class giving access to the internal methods of the enclosing
	 * {@link FControl}.
	 * @author Benjamin Sigg
	 */
	private class Access implements FControlAccess{
	    /** action used to close {@link FDockable}s  */
	    private FCloseAction closeAction;
	    
	    public FControl getOwner(){
			return FControl.this;
		}
	    
	    public void link( FDockable dockable, FDockableAccess access ) {
	        if( access == null )
	            accesses.remove( dockable );
	        else{
	            accesses.put( dockable, access );
	        }
	    }
	    
	    public FDockableAccess access( FDockable dockable ) {
	        return accesses.get( dockable );
	    }
		
		public void hide( FDockable dockable ){
			frontend.hide( dockable.intern() );
			ensureValidModes();
		}
		
		public void show( FDockable dockable ){
			frontend.show( dockable.intern() );
			stateManager.ensureValidMode( dockable );
		}
		
		public boolean isVisible( FDockable dockable ){
			return frontend.isShown( dockable.intern() );
		}
		
		public String getFactoryId( FMultipleDockableFactory factory ){
			for( Map.Entry<String, FactoryProperties> entry : factories.entrySet() ){
				if( entry.getValue().factory == factory )
					return entry.getKey();
			}
			
			return null;
		}
		
		public FStateManager getStateManager() {
		    return stateManager;
		}
		
		public DockAction createCloseAction( final FDockable fdockable ) {
		    if( closeAction == null )
		        closeAction = new FCloseAction();
		    
		    return closeAction;
		}
	}
	
	/**
	 * Action that can close {@link FDockable}s
	 * @author Benjamin Sigg
	 */
	@EclipseTabDockAction
	private class FCloseAction extends CloseAction{
	    /**
	     * Creates a new action
	     */
	    public FCloseAction(){
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
	        FDockable fdockable = ((FacileDockable)dockable).getDockable();
	        if( fdockable.getExtendedMode() == FDockable.ExtendedMode.MAXIMIZED )
	            fdockable.setExtendedMode( FDockable.ExtendedMode.NORMALIZED );
	        fdockable.setVisible( false );
        }
	}
}
