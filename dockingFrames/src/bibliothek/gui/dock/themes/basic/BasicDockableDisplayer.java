/*
 * Bibliothek - DockingFrames
 * Library built on Java/Swing, allows the user to "drag and drop"
 * panels containing any Swing-Component the developer likes to add.
 * 
 * Copyright (C) 2008 Benjamin Sigg
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
package bibliothek.gui.dock.themes.basic;

import java.awt.*;

import javax.swing.BorderFactory;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.border.BevelBorder;
import javax.swing.border.Border;

import bibliothek.gui.DockController;
import bibliothek.gui.DockStation;
import bibliothek.gui.Dockable;
import bibliothek.gui.dock.displayer.DisplayerFocusTraversalPolicy;
import bibliothek.gui.dock.displayer.DockableDisplayerHints;
import bibliothek.gui.dock.focus.DockFocusTraversalPolicy;
import bibliothek.gui.dock.station.DisplayerCollection;
import bibliothek.gui.dock.station.DisplayerFactory;
import bibliothek.gui.dock.station.DockableDisplayer;
import bibliothek.gui.dock.title.DockTitle;


/**
 * A panel which shows one {@link Dockable} and one {@link DockTitle}. The location
 * of the {@link DockTitle} is always at one of the four borders (left,
 * right, top, bottom). The title may be <code>null</code>, in this case only
 * the Dockable is shown.<br>
 * Clients using a displayer should try to set the {@link #setController(DockController) controller}
 * and the {@link #setStation(DockStation) station} property.<br>
 * Subclasses may override {@link #getComponent(Dockable)}, {@link #addDockable(Component)},
 * {@link #removeDockable(Component)}, {@link #getComponent(DockTitle)}, {@link #addTitle(Component)}
 * and/or {@link #removeTitle(Component)} if they want to introduce a completely
 * new layout needing more {@link Container Containers}.
 * @see DisplayerCollection
 * @see DisplayerFactory
 * @author Benjamin Sigg
 */
public class BasicDockableDisplayer extends JPanel implements DockableDisplayer{
    /** The content of this displayer */
    private Dockable dockable;
    /** The title on this displayer */
    private DockTitle title;
    /** the location of the title */
    private Location location;
    /** the station on which this displayer might be shown */
    private DockStation station;
    /** the controller for which this displayer might be used */
    private DockController controller;
    
    /** the set of hints for this displayer */
    private Hints hints = new Hints();
    /** whether the hint for the border of {@link DockableDisplayerHints} should be respected */
    private boolean respectBorderHint = false;
    /** the default value for the border hint */
    private boolean defaultBorderHint = true;
    
    /**
     * Creates a new displayer
     */
    public BasicDockableDisplayer(){
        this( null, null );
    }
    
    /**
     * Creates a new displayer, sets the title and the content.
     * @param dockable the content, may be <code>null</code>
     * @param title the title, may be <code>null</code>
     */
    public BasicDockableDisplayer( Dockable dockable, DockTitle title ){
        this( dockable, title, Location.TOP );
    }
    
    /**
     * Creates a new displayer, sets the title, its location and the
     * content.
     * @param dockable the content, may be <code>null</code>
     * @param title the title of <code>dockable</code>, can be <code>null</code>
     * @param location the location of the title, can be <code>null</code>
     */
    public BasicDockableDisplayer( Dockable dockable, DockTitle title, Location location ){
        super( null );
        init( dockable, title, location );
    }
   
    /**
     * Creates a new displayer but does not set the properties of the
     * displayer. Subclasses may call {@link #init(Dockable, DockTitle, bibliothek.gui.dock.station.DockableDisplayer.Location) init}
     * to initialize all variables of the new displayer.
     * @param initialize <code>true</code> if all properties should be set
     * to default, <code>false</code> if nothing should happen, and 
     * {@link #init(Dockable, DockTitle, bibliothek.gui.dock.station.DockableDisplayer.Location) init}
     * will be called.
     */
    protected BasicDockableDisplayer( boolean initialize ){
    	super( null );
    	if( initialize ){
    		init( null, null, Location.TOP );
    	}
    }
    
    /**
     * Initialises all properties of this DockableDisplayer. This method should
     * only be called once, by a constructor of a subclass which invoked
     * <code>{@link #BasicDockableDisplayer(boolean) DockableDisplayer( false )}</code>. 
     * @param dockable the content, may be <code>null</code>
     * @param title the title of <code>dockable</code>, can be <code>null</code>
     * @param location the location of the title, can be <code>null</code>
     */
    protected void init( Dockable dockable, DockTitle title, Location location ){
        setTitleLocation( location );
        setDockable( dockable );
        setTitle( title );
        setFocusable( true );
        
        setFocusCycleRoot( true );
        setFocusTraversalPolicy( 
                new DockFocusTraversalPolicy( 
                        new DisplayerFocusTraversalPolicy( this ), true ));
    }
    
    public void setController( DockController controller ) {
        this.controller = controller;
    }
    
    public DockController getController() {
        return controller;
    }
    
    public void setStation( DockStation station ) {
        this.station = station;
    }
    
    public DockStation getStation() {
        return station;
    }
    
    public Dockable getDockable() {
        return dockable;
    }

    public void setDockable( Dockable dockable ) {
        if( this.dockable != null ){
            this.dockable.configureDisplayerHints( null );
            removeDockable( this.dockable.getComponent() );
        }
        
        hints.setShowBorderHint( null );
        this.dockable = dockable;
        
        if( dockable != null ){
            addDockable( dockable.getComponent() );
            this.dockable.configureDisplayerHints( hints );
        }
        
        revalidate();
    }

    public Location getTitleLocation() {
        return location;
    }

    public void setTitleLocation( Location location ) {
        if( location == null )
            location = Location.TOP;
        
        this.location = location;
        
        if( title != null )
            title.setOrientation( orientation( location ));
        
        revalidate();
    }

    /**
     * Determines the orientation of a {@link DockTitle} according to its
     * location on this displayer.
     * @param location the location on this displayer
     * @return the orientation
     */
    protected DockTitle.Orientation orientation( Location location ){
        switch( location ){
            case TOP: return DockTitle.Orientation.NORTH_SIDED;
            case BOTTOM: return DockTitle.Orientation.SOUTH_SIDED;
            case LEFT: return DockTitle.Orientation.WEST_SIDED;
            case RIGHT: return DockTitle.Orientation.EAST_SIDED;
        }
        
        return null;
    }
    
    public DockTitle getTitle() {
        return title;
    }

    public void setTitle( DockTitle title ) {
        if( this.title != null )
            removeTitle( this.title.getComponent() );
        
        this.title = title;
        if( title != null ){
            title.setOrientation( orientation( location ));
            addTitle( title.getComponent() );
        }
        
        revalidate();
    }
    
    /**
     * Inserts a component representing the current {@link #getDockable() dockable}
     * into the layout. This method is never called twice unless 
     * {@link #removeDockable(Component)} is called before. Note that
     * the name "add" is inspired by the method {@link Container#add(Component) add}
     * of <code>Container</code>.
     * @param component the new Component
     */
    protected void addDockable( Component component ){
        add( component );
    }
    
    /**
     * Removes the Component which represents the current {@link #getDockable() dockable}.
     * @param component the component
     */
    protected void removeDockable( Component component ){
        remove( component );
    }
    
    /**
     * Gets the Component which should be used to layout the current
     * Dockable.
     * @param dockable the current Dockable, never <code>null</code>
     * @return the component representing <code>dockable</code>
     */
    protected Component getComponent( Dockable dockable ){
        return dockable.getComponent();
    }
    
    /**
     * Inserts a component representing the current {@link #getTitle() title}
     * into the layout. This method is never called twice unless 
     * {@link #removeTitle(Component)} is called before. Note that
     * the name "add" is inspired by the method {@link Container#add(Component) add}
     * of <code>Container</code>.
     * @param component the new Component
     */
    protected void addTitle( Component component ){
        add( component );
    }
    
    /**
     * Removes the Component which represents the current {@link #getTitle() title}.
     * @param component the component
     */
    protected void removeTitle( Component component ){
        remove( component );
    }
    
    /**
     * Gets the Component which should be used to layout the current
     * DockTitle.
     * @param title the current DockTitle, never <code>null</code>
     * @return the component representing <code>title</code>
     */
    protected Component getComponent( DockTitle title ){
        return title.getComponent();
    }
    
    public boolean titleContains( int x, int y ){
    	DockTitle title = getTitle();
    	if( title == null )
    		return false;
    	
    	Component component = getComponent( title );
    	Point point = new Point( x, y );
    	point = SwingUtilities.convertPoint( this, point, component );
    	point.x -= component.getX();
    	point.y -= component.getY();
    	return component.contains( point );
    }
    
    public Component getComponent(){
    	return this;
    }
    
    @Override
    public Dimension getMinimumSize() {
    	Dimension base;
    	
    	if( title == null && dockable != null )
    		base = getComponent( dockable ).getMinimumSize();
    	else if( dockable == null && title != null )
    		base = getComponent( title ).getMinimumSize();
    	else if( dockable == null && title == null )
    		base = new Dimension( 0, 0 );
    	else if( location == Location.LEFT || location == Location.RIGHT ){
    		Dimension titleSize = getComponent( title ).getMinimumSize();
    		base = getComponent( dockable ).getMinimumSize();
    		base = new Dimension( base.width + titleSize.width, 
    				Math.max( base.height, titleSize.height ));
    	}
    	else{
    		Dimension titleSize = getComponent( title ).getMinimumSize();
    		base = getComponent( dockable ).getMinimumSize();
    		base = new Dimension( Math.max( titleSize.width, base.width ),
    				titleSize.height + base.height );
    	}
    	
    	Insets insets = getInsets();
    	if( insets != null ){
    		base = new Dimension( base.width + insets.left + insets.right,
    				base.height + insets.top + insets.bottom );
    	}
    	return base;
    }
    
    @Override
    public void doLayout(){
        Insets insets = getInsets();
        if( insets == null )
            insets = new Insets(0,0,0,0);
        
        int x = insets.left;
        int y = insets.top;
        int width = getWidth() - insets.left - insets.right;
        int height = getHeight() - insets.top - insets.bottom;
        
        if( title == null && dockable == null )
            return;
        
        width = Math.max( 0, width );
        height = Math.max( 0, height );
        
        if( title == null )
            getComponent( dockable ).setBounds( x, y, width, height );

        else if( dockable == null )
            getComponent( title ).setBounds( x, y, width, height );
        
        else{
            Dimension preferred = getComponent( title ).getPreferredSize();
            
            int preferredWidth = preferred.width;
            int preferredHeight = preferred.height;
            
            if( location == Location.LEFT || location == Location.RIGHT ){
                preferredWidth = Math.min( preferredWidth, width );
                preferredHeight = height;
            }
            else{
                preferredWidth = width;
                preferredHeight = Math.min( preferredHeight, height );
            }
            
            if( location == Location.LEFT ){
                getComponent( title ).setBounds( x, y, preferredWidth, preferredHeight );
                getComponent( dockable ).setBounds( x+preferredWidth, y, width - preferredWidth, height );
            }
            else if( location == Location.RIGHT ){
                getComponent( title ).setBounds( x+width-preferredWidth, y, preferredWidth, preferredHeight );
                getComponent( dockable ).setBounds( x, y, width - preferredWidth, preferredHeight );
            }
            else if( location == Location.BOTTOM ){
                getComponent( title ).setBounds( x, y+height - preferredHeight, preferredWidth, preferredHeight );
                getComponent( dockable ).setBounds( x, y, preferredWidth, height - preferredHeight );
            }
            else{
                getComponent( title ).setBounds( x, y, preferredWidth, preferredHeight );
                getComponent( dockable ).setBounds( x, y+preferredHeight, preferredWidth, height - preferredHeight );
            }
        }
    }
    
    public Insets getDockableInsets() {
        Insets insets = getInsets();
        if( insets == null )
            insets = new Insets(0,0,0,0);
        
        if( title == null && dockable == null )
            return insets;
        
        if( title == null ){
            return insets;
        }
        else if( dockable != null ){
            Dimension preferred = getComponent( title ).getPreferredSize();
            
            if( location == Location.LEFT ){
                insets.left += preferred.width;
            }
            else if( location == Location.RIGHT ){
                insets.right += preferred.width;
            }
            else if( location == Location.BOTTOM ){
                insets.bottom += preferred.height;
            }
            else{
                insets.top += preferred.height;
            }
        }
        
        return insets;
    }
    
    /**
     * Gets the set of hints for displaying this component.
     * @return the set of hints
     */
    protected Hints getHints() {
        return hints;
    }
    
    /**
     * Tells this displayer whether the show border hint of 
     * {@link #getHints()} should be respected or not. The default value
     * is <code>false</code>.
     * @param respectBorderHint <code>true</code> if the hint should be respected,
     * <code>false</code> if not.
     */
    public void setRespectBorderHint( boolean respectBorderHint ) {
        if( this.respectBorderHint != respectBorderHint ){
            this.respectBorderHint = respectBorderHint;
            updateBorder();
        }
    }
    
    /**
     * Whether the show border hint is respected by this displayer.
     * @return <code>true</code> if the hint is respected
     * @see #setRespectBorderHint(boolean)
     */
    public boolean isRespectBorderHint() {
        return respectBorderHint;
    }
    
    /**
     * Sets the default value for the show border hint.
     * @param defaultBorderHint the default value
     */
    public void setDefaultBorderHint( boolean defaultBorderHint ) {
        if( this.defaultBorderHint != defaultBorderHint ){
            this.defaultBorderHint = defaultBorderHint;
            updateBorder();
        }
    }
    
    /**
     * Gets the default value for the show border hint.
     * @return the default value
     */
    public boolean getDefaultBorderHint() {
        return defaultBorderHint;
    }
    
    /**
     * Called when the hint, whether a border should be shown or not, has changed. 
     */
    protected void updateBorder(){
        if( respectBorderHint ){
            boolean show = hints.getShowBorderHint();
            
            if( show ){
                setBorder( getDefaultBorder() );
            }
            else{
                setBorder( null );
            }
        }
    }
    
    /**
     * Gets the default border for this displayer. That can either be
     * a new object or an old border. It should not be <code>null</code>.
     * The standard implementation just returns a new instance of of 
     * {@link BevelBorder}.
     * @return the default border to be used on this displayer
     */
    protected Border getDefaultBorder(){
        return BorderFactory.createBevelBorder( BevelBorder.RAISED );
    }
    
    /**
     * This implementation of {@link DockableDisplayerHints} forwards
     * any changes to its {@link BasicDockableDisplayer}.
     * @author Benjamin Sigg
     */
    protected class Hints implements DockableDisplayerHints{
        private Boolean border;
        
        public void setShowBorderHint( Boolean border ) {
            if( this.border != border ){
                this.border = border;
                updateBorder();
            }
        }
        
        /**
         * Gets the hint that tells whether the border should be shown or not.
         * @return whether the border should be shown or <code>null</code>
         */
        public boolean getShowBorderHint() {
            if( border != null )
                return border.booleanValue();
            
            return defaultBorderHint;
        }
    }
}