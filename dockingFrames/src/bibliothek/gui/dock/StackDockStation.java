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

package bibliothek.gui.dock;

import java.awt.*;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.swing.Icon;
import javax.swing.JComponent;
import javax.swing.JTabbedPane;
import javax.swing.SwingUtilities;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.MouseInputListener;

import bibliothek.gui.*;
import bibliothek.gui.dock.event.DockStationAdapter;
import bibliothek.gui.dock.event.DockableListener;
import bibliothek.gui.dock.layout.DockableProperty;
import bibliothek.gui.dock.station.AbstractDockableStation;
import bibliothek.gui.dock.station.DisplayerCollection;
import bibliothek.gui.dock.station.DisplayerFactory;
import bibliothek.gui.dock.station.DockableDisplayer;
import bibliothek.gui.dock.station.OverpaintablePanel;
import bibliothek.gui.dock.station.StationPaint;
import bibliothek.gui.dock.station.stack.*;
import bibliothek.gui.dock.station.support.DisplayerFactoryWrapper;
import bibliothek.gui.dock.station.support.DockableVisibilityManager;
import bibliothek.gui.dock.station.support.StationPaintWrapper;
import bibliothek.gui.dock.title.ControllerTitleFactory;
import bibliothek.gui.dock.title.DockTitle;
import bibliothek.gui.dock.title.DockTitleFactory;
import bibliothek.gui.dock.title.DockTitleVersion;
import bibliothek.gui.dock.util.DockUtilities;
import bibliothek.gui.dock.util.PropertyKey;
import bibliothek.gui.dock.util.PropertyValue;

/**
 * On this station, only one of many children is visible. The other children
 * are hidden behind the visible child. There are some buttons where the
 * user can choose which child is visible. This station behaves like
 * a {@link JTabbedPane}.<br>
 * This station tries to register a {@link DockTitleFactory} to its 
 * {@link DockController} with the key {@link #TITLE_ID}.
 * @author Benjamin Sigg
 */
public class StackDockStation extends AbstractDockableStation {
    /** The id of the titlefactory which is used by this station */
    public static final String TITLE_ID = "stack";
    
    /** Key used to read the current {@link StackDockComponentFactory} */
    public static final PropertyKey<StackDockComponentFactory> COMPONENT_FACTORY =
        new PropertyKey<StackDockComponentFactory>( "stack dock component factory" );
    
    /** A list of all children */
    private List<DockableDisplayer> dockables = new ArrayList<DockableDisplayer>();
    
    /**
     * A list of {@link MouseInputListener MouseInputListeners} which are
     * registered at this station.
     */
    private List<MouseInputListener> mouseInputListeners = new ArrayList<MouseInputListener>();
    
    /** A manager for firing events if a child changes its visibility-state */
    private DockableVisibilityManager visibility;
    
    /** A paint to draw lines */
    private StationPaintWrapper paint = new StationPaintWrapper();
    
    /** A factory to create {@link DockableDisplayer} */
    private DisplayerFactoryWrapper displayerFactory = new DisplayerFactoryWrapper();
    
    /** The set of displayers shown on this station */
    private DisplayerCollection displayers;
    
    /** The {@link Dockable} which is currently moved or dropped */
    private Dockable dropping;
    
    /** Tells whether some lines have to be painted, or not */
    private boolean draw = false;
    
    /** The preferred location where {@link #dropping} should be added */
    private Insert insert;
    
     /** The graphical representation of this station */
    private Background background;
    
    /** The panel where components are added */
    private JComponent panel;
    
    /** A Component which shows two or more children of this station */
    private StackDockComponent stackComponent;
    
    /** The current component factory */
    private PropertyValue<StackDockComponentFactory> stackComponentFactory;
    
    /** The version of titles which should be used for this station */
    private DockTitleVersion title;
    
    /** A listener observing the children for changes of their icon or titletext */
    private Listener listener = new Listener();
    
    /**
     * A listener added to the parent of this station. The listener ensures
     * that the visibility-state is always correct. 
     */
    private VisibleListener visibleListener;
    
    /**
     * Constructs a new StackDockStation
     */
    public StackDockStation(){
    	this( null );
    }
    
    /**
     * Constructs a new station and sets the theme.
     * @param theme the theme of the station, may be <code>null</code>
     */
    public StackDockStation( DockTheme theme ){
    	super( theme );
    	init();
    }
    
    /**
     * Creates a new station.
     * @param theme the theme of this station, can be <code>null</code>
     * @param init <code>true</code> if the fields of this object should
     * be initialized, <code>false</code> otherwise. If <code>false</code>,
     * then the subclass has to call {@link #init()} exactly once.
     */
    protected StackDockStation( DockTheme theme, boolean init ){
    	super( theme );
    	if( init )
    		init();
    }
    
    /**
     * Initializes the fields of this object, has to be called exactly once.
     */
    protected void init(){
        visibleListener = new VisibleListener();
        visibility = new DockableVisibilityManager( listeners );
        
        displayers = new DisplayerCollection( this, displayerFactory );
        
        background = createBackground();
        panel = background.getContentPane();
        
        stackComponentFactory = new PropertyValue<StackDockComponentFactory>( COMPONENT_FACTORY ){
            @Override
            protected void valueChanged( StackDockComponentFactory oldValue, StackDockComponentFactory newValue ) {
                if( newValue == null )
                    setStackComponent( createStackDockComponent() );
                else
                    setStackComponent( newValue.create( StackDockStation.this ) );
            }
        };
        
        stackComponent = createStackDockComponent();
        stackComponent.addChangeListener( visibleListener );
    }
    
    /**
     * Creates the panel onto which this station will lay its children.
     * @return the new background
     */
    protected Background createBackground(){
    	return new Background();
    }
    
    /**
     * Creates the {@link StackDockComponent} which will be shown on
     * this station if the station has more then one child.<br>
     * This method is called directly by the constructor.
     * @return the new component
     */
    protected StackDockComponent createStackDockComponent(){
        return new DefaultStackDockComponent();
    }
    
    /**
     * Sets the {@link StackDockComponent} which should be used by this 
     * station. The component is shown when this station has more then 
     * one child. Note that the <code>stackComponent</code> depends also
     * on the property {@link #COMPONENT_FACTORY}, and will be automatically
     * exchanged if that property changes. Clients should use
     * {@link #setStackComponentFactory(StackDockComponentFactory)} if they
     * want to exchange the component permanently. 
     * @param stackComponent the new component
     * @throws IllegalArgumentException if <code>stackComponent</code> is <code>null</code>
     */
    public void setStackComponent( StackDockComponent stackComponent ) {
        if( stackComponent == null )
            throw new IllegalArgumentException( "Component must not be null" );
        
        if( stackComponent != this.stackComponent ){
            if( this.stackComponent != null ){
            	this.stackComponent.setController( null );
                Component component = this.stackComponent.getComponent();
                for( MouseInputListener listener : mouseInputListeners ){
                    component.removeMouseListener( listener );
                    component.removeMouseMotionListener( listener );
                }
            }
            
            if( getDockableCount() < 2 ){
                this.stackComponent.removeChangeListener( visibleListener );
                this.stackComponent = stackComponent;
                stackComponent.addChangeListener( visibleListener );
            }
            else{
                int selected = this.stackComponent.getSelectedIndex();
                this.stackComponent.removeChangeListener( visibleListener );
                this.stackComponent.removeAll();
                panel.removeAll();
                
                this.stackComponent = stackComponent;
                
                for( DockableDisplayer displayer : dockables ){
                    Dockable dockable = displayer.getDockable();
                    stackComponent.insertTab( 
                            dockable.getTitleText(), 
                            dockable.getTitleIcon(), 
                            displayer.getComponent(), 
                            dockable,
                            stackComponent.getTabCount() );
                }
                
                panel.add( stackComponent.getComponent() );
                if( selected >= 0 && selected < stackComponent.getTabCount() )
                    stackComponent.setSelectedIndex( selected );
                
                stackComponent.addChangeListener( visibleListener );
            }
            
            if( stackComponent != null ){
                Component component = stackComponent.getComponent();
                stackComponent.setController( getController() );
                for( MouseInputListener listener : mouseInputListeners ){
                    component.addMouseListener( listener );
                    component.addMouseMotionListener( listener );
                }
            }
        }
    }
    
    /**
     * Gets the currently used {@link StackDockComponent}
     * @return the component
     * @see #setStackComponent(StackDockComponent)
     */
    public StackDockComponent getStackComponent() {
		return stackComponent;
	}
   
    /**
     * Sets the factory which will be used to create a {@link StackDockComponent}
     * for this station.
     * @param factory the new factory, can be <code>null</code> if the default-factory
     * should be used
     */
    public void setStackComponentFactory( StackDockComponentFactory factory ){
        stackComponentFactory.setValue( factory );
    }
    
    /**
     * Gets the factory which is used to create a {@link StackDockComponent}.
     * This method returns <code>null</code> if no factory was set through
     * {@link #setStackComponentFactory(StackDockComponentFactory)}.
     * @return the factory or <code>null</code>
     */
    public StackDockComponentFactory getStackComponentFactory(){
        return stackComponentFactory.getOwnValue();
    }
    
    @Override
    protected void callDockUiUpdateTheme() throws IOException {
    	DockUI.updateTheme( this, new StackDockStationFactory());
    }
   
    @Override
    public void setDockParent( DockStation station ) {
        DockStation old = getDockParent();
        if( old != null )
            old.removeDockStationListener( visibleListener );
        
        super.setDockParent(station);
        
        if( station != null )
            station.addDockStationListener( visibleListener );
        
        visibility.fire();
    }
    
    @Override
    public void setController( DockController controller ) {
        if( this.getController() != controller ){
            for( DockableDisplayer displayer : dockables ){
                DockTitle title = displayer.getTitle();
                if( title != null ){
                    displayer.getDockable().unbind( title );
                    displayer.setTitle( null );
                }
            }
            
            stackComponentFactory.setProperties( controller );
            super.setController(controller);
            stackComponent.setController( controller );
            
            if( controller != null ){
                title = controller.getDockTitleManager().registerDefault( TITLE_ID, ControllerTitleFactory.INSTANCE );
            }
            else
                title = null;
            
            displayers.setController( controller );
            
            for( DockableDisplayer displayer : dockables ){
                if( this.title != null ){
                    DockTitle title = displayer.getDockable().getDockTitle( this.title );
                    displayer.setTitle( title );
                    if( title != null )
                        displayer.getDockable().bind( title );
                }
            }
        }
    }
    
    /**
     * Gets a {@link StationPaint} which is used to paint some lines onto
     * this station. Use a {@link StationPaintWrapper#setDelegate(StationPaint) delegate}
     * to exchange the paint.
     * @return the paint
     */
    public StationPaintWrapper getPaint() {
        return paint;
    }
   
    /**
     * Gets a {@link DisplayerFactory} which is used to create new
     * {@link DockableDisplayer} for this station. Use a 
     * {@link DisplayerFactoryWrapper#setDelegate(DisplayerFactory) delegate}
     * to exchange the factory.
     * @return the factory
     */
    public DisplayerFactoryWrapper getDisplayerFactory() {
        return displayerFactory;
    }
    
    /**
     * Gets the set of {@link DockableDisplayer displayers} used on this station.
     * @return the set of displayers
     */
    public DisplayerCollection getDisplayers() {
        return displayers;
    }
    
    @Override
    public boolean isStationVisible() {
        DockStation parent = getDockParent();
        if( parent != null )
            return parent.isVisible( this );
        else
            return panel.isDisplayable();
    }
    
    @Override
    public boolean isVisible( Dockable dockable ) {
        return isStationVisible() && (dockables.size() == 1 || indexOf( dockable ) == stackComponent.getSelectedIndex() );
    }
    
    public int getDockableCount() {
        return dockables.size();
    }

    public Dockable getDockable( int index ) {
        return dockables.get( index ).getDockable();
    }
    
    public DockableProperty getDockableProperty( Dockable dockable ) {
        return new StackDockProperty( indexOf( dockable ) );
    }
    
    public Dockable getFrontDockable() {
        if( dockables.size() == 0 )
            return null;
        if( dockables.size() == 1 )
            return dockables.get( 0 ).getDockable();
        
        int index = stackComponent.getSelectedIndex();
        if( index >= 0 )
            return dockables.get( index ).getDockable();
        
        return null;
    }
    
    public void setFrontDockable( Dockable dockable ) {
        if( dockables.size() > 1 && dockable != null )
            stackComponent.setSelectedIndex( indexOf( dockable ));
    }
    
    /**
     * Gets the index of a child.
     * @param dockable the child which is searched
     * @return the index of <code>dockable</code> or -1 if it was not found
     */
    public int indexOf( Dockable dockable ){
        for( int i = 0, n = dockables.size(); i<n; i++ )
            if( dockables.get( i ).getDockable() == dockable )
                return i;
        
        return -1;
    }

    public boolean prepareDrop( int x, int y, int titleX, int titleY, boolean checkOverrideZone, Dockable dockable ){
        DockStation parent = getDockParent();
        Point point = new Point( x, y );
        SwingUtilities.convertPointFromScreen( point, panel );
        
        if( parent != null ){
            if( checkOverrideZone && parent.isInOverrideZone( x, y, this, dockable )){
                if( dockables.size() > 1 ){
                    if( setInsert( exactTabIndexAt( point.x, point.y ), dockable ))
                        return true;
                }
                else if( dockables.size() == 1 ){
                    DockTitle title = dockables.get( 0 ).getTitle();
                    if( title != null ){
                        Component component = title.getComponent();
                        Point p = new Point( x, y );
                        SwingUtilities.convertPointFromScreen( p, component );

                        if( component.getBounds().contains( p )){
                            return setInsert( new Insert( 0, true ), dockable );
                        }
                    }
                }
                return false;
            }
        }
        
        return setInsert( tabIndexAt( point.x, point.y ), dockable );
    }

    public void drop(){
    	listeners.fireDockableAdding( dropping );
        add( dropping, insert.tab + (insert.right ? 1 : 0), false );
        listeners.fireDockableAdded( dropping );
    }

    public void drop( Dockable dockable ) {
    	listeners.fireDockableAdding( dockable );
        add( dockable, dockables.size(), false );
        listeners.fireDockableAdded( dockable );
    }
    
    public boolean drop( Dockable dockable, DockableProperty property ) {
        if( property instanceof StackDockProperty ){
            return drop( dockable, (StackDockProperty)property );
        }
        else
            return false;
    }
    
    /**
     * Adds a new child to this station, and tries to match the <code>property</code>
     * as good as possible.
     * @param dockable the new child
     * @param property the preferred location of the child
     * @return <code>true</code> if the child could be added, <code>false</code>
     * if the child couldn't be added
     */
    public boolean drop( Dockable dockable, StackDockProperty property ){
        DockUtilities.ensureTreeValidity( this, dockable );
        int index = property.getIndex();
        
        if( dockables.size() == 0 ){
            if( accept( dockable ) && dockable.accept( this )){
                drop( dockable );
                return true;
            }
            else{
                return false;
            }
        }
        
        index = Math.min( index, dockables.size() );
        DockableProperty successor = property.getSuccessor();
        
        if( index < dockables.size() && successor != null ){
            DockStation station = dockables.get( index ).getDockable().asDockStation();
            if( station != null ){
                if( station.drop( dockable, successor ))
                    return true;
            }
        }
        
        if( accept( dockable ) && dockable.accept( this )){
            add( dockable, index );
            return true;
        }
        else
            return false;
    }
    
    public boolean prepareMove( int x, int y, int titleX, int titleY, boolean checkOverrideZone, Dockable dockable ) {
        DockStation parent = getDockParent();
        Point point = new Point( x, y );
        SwingUtilities.convertPointFromScreen( point, panel );
        
        if( parent != null ){
            if( checkOverrideZone && parent.isInOverrideZone( x, y, this, dockable )){
                if( dockables.size() > 1 ){
                    if( setInsert( exactTabIndexAt( point.x, point.y ), dockable ) )
                        return true;
                }
                return false;
            }
        }
        
        return setInsert( tabIndexAt( point.x, point.y ), dockable );
    }
    
    /**
     * Checks whether <code>child</code> can be inserted at <code>insert</code>.
     * If so, then the field {@link #insert} and {@link #dropping} are set.
     * @param insert the new location
     * @param child the element to insert
     * @return <code>true</code> if the combination is valid
     */
    private boolean setInsert( Insert insert, Dockable child ){
        if( insert != null && accept( child ) && child.accept( this ) && getController().getAcceptance().accept( this, child )){
            this.insert = insert;
            this.dropping = child;
        }
        else{
            this.insert = null;
            this.dropping = null;
        }
        
        return this.insert != null;
    }

    public void move() {
        int index = indexOf( dropping );
        if( index >= 0 ){
            int drop = insert.tab + (insert.right ? 1 : 0 );
            if( index < drop )
                drop--;
            
            remove( index, false );
            add( dropping, drop );
        }
    }
    
    public void move( Dockable dockable, DockableProperty property ) {
        if( property instanceof StackDockProperty ){
            int index = indexOf( dockable );
            if( index < 0 )
                throw new IllegalArgumentException( "dockable not child of this station" );
            
            int destination = ((StackDockProperty)property).getIndex();
            destination = Math.min( destination, getDockableCount() );
            if( destination != index ){
                DockController controller = getController();
                if( controller == null ){
                    remove( index, false );
                    if( destination > index )
                    	destination--;
                    add( dockable, destination );
                }
                else{
                    try{
                        controller.getRegister().setStalled( true );
                        remove( index, false );
                        if( destination > index )
                        	destination--;
                        add( dockable, destination );
                    }
                    finally{
                        controller.getRegister().setStalled( false );
                    }
                }
            }
        }
    }
        
    /**
     * Tells which gap (between tabs) is chosen if the mouse has the coordinates x/y.
     * If there is no tab at this location, a default-tab is chosen.
     * @param x x-coordinate in the system of this station
     * @param y y-coordinate in the system of this station
     * @return the location of a tab
     */
    protected Insert tabIndexAt( int x, int y ){
        if( dockables.size() == 0 )
            return new Insert( 0, false );
        if( dockables.size() == 1 )
            return new Insert( 1, false );
        
        Insert insert = exactTabIndexAt( x, y );
        if( insert == null )
            insert = new Insert( dockables.size()-1, true );
        
        return insert;
    }
    
    /**
     * Gets the gap which is selected when the mouse is at x/y.
     * @param x x-coordinate in the system of this station
     * @param y y-coordinate in the system of this station
     * @return the location of a tab or <code>null</code> if no tab is
     * under x/y
     */
    protected Insert exactTabIndexAt( int x, int y ){
        Point point = SwingUtilities.convertPoint( panel, x, y, stackComponent.getComponent() );
        
        for( int i = 0, n = dockables.size(); i<n; i++ ){
            Rectangle bounds = stackComponent.getBoundsAt( i );
            if( bounds != null && bounds.contains( point )){
                return new Insert( i, bounds.x + bounds.width/2 < point.x );
            }
        }
               
        return null;
    }    

    public void draw() {
        draw = true;
        panel.repaint();
    }

    public void forget() {
        draw = false;
        insert = null;
        dropping = null;
        panel.repaint();
    }

    public <D extends Dockable & DockStation> boolean isInOverrideZone( int x,
            int y, D invoker, Dockable drop ){
        
        DockStation parent = getDockParent();
        if( parent != null )
            return parent.isInOverrideZone( x, y, invoker, drop );
        
        return false;
    }

    public boolean canDrag( Dockable dockable ) {
        return true;
    }
    
    public void drag( Dockable dockable ) {
        int index = indexOf( dockable );
        if( index < 0 )
            throw new IllegalArgumentException( "The dockable is not part of this station." );
        
        listeners.fireDockableRemoving( dockable );
        remove( index, false );
        listeners.fireDockableRemoved( dockable );
    }
    
    public boolean canReplace( Dockable old, Dockable next ) {
        return true;
    }
    
    public void replace( Dockable old, Dockable next ) {
        int index = indexOf( old );
        remove( index );
        add( next, index );
    }
    
    /**
     * Adds a child to this station at the location <code>index</code>.
     * @param dockable the new child
     * @param index the preferred location of the new child
     */
    public void add( Dockable dockable, int index ){
        add( dockable, index, true );
    }
    
    /**
     * Adds a child to this station at the location <code>index</code>.
     * @param dockable the new child
     * @param index the preferred location of the new child
     * @param fire if <code>true</code> the method will fire some events,
     * otherwise the method will run silent
     */
    protected void add( Dockable dockable, int index, boolean fire ){
        DockUtilities.ensureTreeValidity( this, dockable );
            
        if( fire )
        	listeners.fireDockableAdding( dockable );
        dockable.setDockParent( this );
        
        DockTitle title = null;
        
        if( this.title != null ){
            title = dockable.getDockTitle( this.title );
            if( title != null )
                dockable.bind( title );
        }
        
        DockableDisplayer displayer = getDisplayers().fetch( dockable, title );
        
        if( dockables.size() == 0 ){
            dockables.add( displayer );
            panel.add( displayer.getComponent() );
        }
        else{
            if( dockables.size() == 1 ){
                panel.removeAll();
                DockableDisplayer child = dockables.get( 0 );
                stackComponent.addTab( child.getDockable().getTitleText(), child.getDockable().getTitleIcon(), child.getComponent(), child.getDockable() );
                panel.add( stackComponent.getComponent() );
            }
            
            dockables.add( index, displayer );
            stackComponent.insertTab( dockable.getTitleText(), dockable.getTitleIcon(), displayer.getComponent(), dockable, index );
            stackComponent.setSelectedIndex( index );
        }
        
        dockable.addDockableListener( listener );
        panel.validate();
        panel.repaint();
        
        if( fire )
        	listeners.fireDockableAdded( dockable );
    }
    
    /**
     * Removes the child of location <code>index</code>.
     * @param index the location of the child which will be removed
     */
    public void remove( int index ){
        remove( index, true );
    }

    /**
     * Removes the child of location <code>index</code>.
     * @param index the location of the child which will be removed
     * @param fire <code>true</code> if the method should fire some events,
     * <code>false</code> if the method should run silently
     */
    private void remove( int index, boolean fire ){
        if( index < 0 || index >= dockables.size() )
            throw new IllegalArgumentException( "Index out of bounds" );
        
        DockableDisplayer displayer = dockables.get( index );
        Dockable dockable = displayer.getDockable();
        DockTitle title = displayer.getTitle();
        
        if( fire )
        	listeners.fireDockableRemoving( dockable );
        
        getDisplayers().release( displayer );
        
        if( title != null ){
            dockable.unbind( title );
            displayer.setTitle( null );
        }
        
        if( dockables.size() == 1 ){
            panel.remove( dockables.get( 0 ).getComponent() );
            dockables.clear();
        }
        else if( dockables.size() == 2 ){
            panel.remove( stackComponent.getComponent() );
            stackComponent.removeAll();
            dockables.remove( index );
            panel.add( dockables.get( 0 ).getComponent() );
        }
        else{
            stackComponent.remove( index );
            dockables.remove( index );
        }
        
        dockable.removeDockableListener( listener );
        panel.validate();
        panel.repaint();
        
        dockable.setDockParent( null );
        if( fire )
        	listeners.fireDockableRemoved( dockable );
    }

    public Component getComponent() {
        return background;
    }
    
    @Override
    public void addMouseInputListener( MouseInputListener listener ) {
        panel.addMouseListener( listener );
        panel.addMouseMotionListener( listener );
        mouseInputListeners.add( listener );
        
        if( stackComponent != null ){
            stackComponent.getComponent().addMouseListener( listener );
            stackComponent.getComponent().addMouseMotionListener( listener );
        }
    }
    
    @Override
    public void removeMouseInputListener( MouseInputListener listener ) {
        panel.removeMouseListener( listener );
        panel.removeMouseMotionListener( listener );
        mouseInputListeners.remove( listener );
        
        if( stackComponent != null ){
            stackComponent.getComponent().removeMouseListener( listener );
            stackComponent.getComponent().removeMouseMotionListener( listener );
        }
    }
    
    public String getFactoryID() {
        return StackDockStationFactory.ID;
    }
    
    /**
     * Writes the layout of this station into <code>out</code>.
     * @param children A map that tells for every child of this station a unique
     * id.
     * @param out The sink of the information
     * @throws IOException if <code>out</code> throws an exception
     */
    public void write( Map<Dockable, Integer> children, DataOutputStream out ) throws IOException{
        out.writeBoolean( dockables.size() > 0 );
        if( dockables.size() > 0 ){
            out.writeInt( dockables.size() );
            for( DockableDisplayer displayer : dockables ){
                out.writeInt( children.get( displayer.getDockable() ));
            }
            
            if( dockables.size() > 1 )
                out.writeInt( getStackComponent().getSelectedIndex() );
            else
                out.writeInt( 0 );
        }
    }
    
    /**
     * Removes all children from this station and then reads its new layout
     * from <code>in</code>. 
     * @param children A map that tells for some ids which {@link Dockable}
     * should be added
     * @param ignore <code>true</code> if the children on this station should
     * not be changed
     * @param in The source of all information
     * @throws IOException if <code>in</code> throws an exception
     */
    public void read( Map<Integer, Dockable> children, boolean ignore, DataInputStream in ) throws IOException{
        if( !ignore ){
            while( dockables.size() > 0 )
                drag( dockables.get( 0 ).getDockable() );
            
            if( in.readBoolean() ){
                int size = in.readInt();
                for( int i = 0; i < size; i++ ){
                    int id = in.readInt();
                    Dockable dockable = children.get( id );
                    if( dockable != null )
                        drop( dockable );
                }
                
                int selected = in.readInt();
                if( dockables.size() > 1 )
                    if( selected >= 0 && selected < getStackComponent().getTabCount() )
                        getStackComponent().setSelectedIndex( selected );
            }
            
            getComponent().invalidate();
        }
    }
    
    /**
     * A listener for the parent of this station. This listener will fire
     * events if the visibility-state of this station changes.<br>
     * This listener is also added to the {@link StackDockStation#getStackComponent() stack-component}
     * of the station, and ensures that the visible child has the focus.
     * @author Benjamin Sigg
     */
    private class VisibleListener extends DockStationAdapter implements ChangeListener{
        @Override
        public void dockableVisibiltySet( DockStation station, Dockable dockable, boolean visible ) {
            visibility.fire();
        }
        
        public void stateChanged( ChangeEvent e ) {
            DockController controller = getController();
            if( controller != null ){
                Dockable front = getFrontDockable();
                if( front != null )
                    controller.setFocusedDockable( front, false );
            }
            
            visibility.fire();
        }
    }
    
    /**
     * This listener is added to the children of the station. Whenever the
     * icon or the title-text of a child changes, the listener will inform
     * the {@link StackDockStation#getStackComponent() stack-component} about
     * the change.
     * @author Benjamin Sigg
     */
    private class Listener implements DockableListener{
        public void titleBound( Dockable dockable, DockTitle title ) {
            // do nothing
        }

        public void titleUnbound( Dockable dockable, DockTitle title ) {
            // do nothing
        }

        public void titleTextChanged( Dockable dockable, String oldTitle, String newTitle ) {
            if( dockables.size() > 1 ){
                int index = indexOf( dockable );
                stackComponent.setTitleAt( index, newTitle );
            }
        }

        public void titleIconChanged( Dockable dockable, Icon oldIcon, Icon newIcon ) {
            if( dockables.size() > 1 ){
                int index = indexOf( dockable );
                stackComponent.setIconAt( index, newIcon );
            }            
        }
    }
    
    /**
     * This panel is used as base of the station. All children of the station 
     * have this panel as parent too.
     * @author Benjamin Sigg
     */
    protected class Background extends OverpaintablePanel{
    	/**
    	 * Creates a new panel
    	 */
        public Background(){
            getContentPane().setLayout( new GridLayout( 1, 1 ));
        }
        
        @Override
        protected void paintOverlay( Graphics g ) {
            StationPaint paint = getPaint();
            
            if( draw && dockables.size() > 1 && insert != null ){
                Rectangle bounds = null;
                
                if( insert.tab >= 0 && insert.tab < stackComponent.getTabCount() )
                    bounds = stackComponent.getBoundsAt( insert.tab );
                
                if( bounds != null ){
                    Point a = new Point();
                    Point b = new Point();
                    
                    if( insert.right ){
                        insertionLine( 
                                bounds, 
                                insert.tab+1 < stackComponent.getTabCount() ? stackComponent.getBoundsAt( insert.tab+1 ) : null,
                                a, b, true );
                    }
                    else{
                        insertionLine(  
                                insert.tab > 0 ? stackComponent.getBoundsAt( insert.tab-1 ) : null,
                                bounds,
                                a, b, false );
                    }
                    
                    paint.drawInsertionLine( g, StackDockStation.this, a.x, a.y, b.x, b.y );
                }
            }
            
            if( draw ){
                Rectangle bounds = new Rectangle( 0, 0, getWidth(), getHeight() );
                Rectangle insert;
                if( getDockableCount() < 2 )
                    insert = bounds;
                else{
                    Component front = dockables.get( stackComponent.getSelectedIndex() ).getComponent();
                    Point location = new Point( 0, 0 );
                    location = SwingUtilities.convertPoint( front, location, this );
                    insert = new Rectangle( location.x, location.y, front.getWidth(), front.getHeight() );
                }
                
                paint.drawInsertion( g, StackDockStation.this, bounds, insert );
            }
        }
    }
    
    /**
     * When dropping or moving a {@link Dockable}, a line has to be painted
     * between two tabs. This method determines the exact location of that line.
     * @param left the bounds of the tab left to the line, might be <code>null</code> if
     * <code>leftImportant</code> is <code>false</code>.
     * @param right the bounds of the tab right to the line, might be <code>null</code> if
     * <code>leftImportant</code> is <code>true</code>.
     * @param a the first point of the line, should be used as output of this method
     * @param b the second point of the line, should be used as output of this method
     * @param leftImportant <code>true</code> if the mouse is over the left tab, <code>false</code>
     * if the mouse is over the right tab.
     */
    protected void insertionLine( Rectangle left, Rectangle right, Point a, Point b, boolean leftImportant ){
        if( left != null && right != null ){
            int top = Math.max( left.y, right.y );
            int bottom = Math.min( left.y + left.height, right.y + right.height );
            
            if( bottom > top ){
                int dif = bottom - top;
                if( dif >= 0.8*left.height && dif >= 0.8*right.height ){
                    a.x = (left.x+left.width+right.x) / 2;
                    a.y = top;
                    
                    b.x = a.x;
                    b.y = bottom;
                    
                    return;
                }
            }
        }
        
        if( leftImportant ){
            a.x = left.x + left.width;
            a.y = left.y;
            
            b.x = a.x;
            b.y = a.y + left.height;
        }
        else{
            a.x = right.x;
            a.y = right.y;
            
            b.x = a.x;
            b.y = a.y + right.height;
        }
    }
    
    /**
     * The location of a gap between to tabs.
     * @author Benjamin Sigg
     */
    private class Insert{
        /** The location of a base-tab */
        public int tab;
        /** Whether the gap is left or right of {@link #tab}*/
        public boolean right;
        
        /**
         * Constructs a new Gap-location
         * @param tab The location of a base-tab
         * @param right Whether the gap is left or right of <code>tab</code>
         */
        public Insert( int tab, boolean right ){
            this.tab = tab;
            this.right = right;
        }
    }
}
