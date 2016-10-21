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

import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.Point;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.SwingUtilities;
import javax.swing.border.BevelBorder;
import javax.swing.border.Border;

import bibliothek.gui.DockController;
import bibliothek.gui.DockStation;
import bibliothek.gui.Dockable;
import bibliothek.gui.dock.DockElementRepresentative;
import bibliothek.gui.dock.action.DockAction;
import bibliothek.gui.dock.action.DockActionSource;
import bibliothek.gui.dock.component.DockComponentConfiguration;
import bibliothek.gui.dock.component.DockComponentRootHandler;
import bibliothek.gui.dock.displayer.DisplayerBackgroundComponent;
import bibliothek.gui.dock.displayer.DisplayerCombinerTarget;
import bibliothek.gui.dock.displayer.DisplayerDockBorder;
import bibliothek.gui.dock.displayer.DisplayerFocusTraversalPolicy;
import bibliothek.gui.dock.displayer.DockableDisplayerHints;
import bibliothek.gui.dock.displayer.SingleTabDecider;
import bibliothek.gui.dock.event.SingleTabDeciderListener;
import bibliothek.gui.dock.focus.DockFocusTraversalPolicy;
import bibliothek.gui.dock.station.DisplayerCollection;
import bibliothek.gui.dock.station.DisplayerFactory;
import bibliothek.gui.dock.station.DockableDisplayer;
import bibliothek.gui.dock.station.DockableDisplayerListener;
import bibliothek.gui.dock.station.stack.action.DockActionDistributor;
import bibliothek.gui.dock.station.stack.action.DockActionDistributor.Target;
import bibliothek.gui.dock.station.stack.action.DockActionDistributorSource;
import bibliothek.gui.dock.station.support.CombinerSource;
import bibliothek.gui.dock.station.support.Enforcement;
import bibliothek.gui.dock.themes.ThemeManager;
import bibliothek.gui.dock.themes.border.BorderForwarder;
import bibliothek.gui.dock.title.ActionsDockTitleEvent;
import bibliothek.gui.dock.title.DockTitle;
import bibliothek.gui.dock.util.BackgroundAlgorithm;
import bibliothek.gui.dock.util.ConfiguredBackgroundPanel;
import bibliothek.gui.dock.util.PropertyKey;
import bibliothek.gui.dock.util.PropertyValue;
import bibliothek.gui.dock.util.Transparency;
import bibliothek.gui.dock.util.UIValue;


/**
 * A panel which shows one {@link Dockable} and one {@link DockTitle}. The location
 * of the {@link DockTitle} is always at one of the four borders (left,
 * right, top, bottom). The title may be <code>null</code>, in this case only
 * the <code>Dockable</code> is shown.<br>
 * Clients using a displayer should try to set the {@link #setController(DockController) controller}
 * and the {@link #setStation(DockStation) station} property.<br>
 * Subclasses may override {@link #getComponent(Dockable)} and/or {@link #getComponent(DockTitle)}
 * if they want to introduce a completely new layout needing more {@link Container Containers}.
 * @see DisplayerCollection
 * @see DisplayerFactory
 * @author Benjamin Sigg
 */
public class BasicDockableDisplayer extends ConfiguredBackgroundPanel implements DockableDisplayer{
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
    
    /** whether to show the inner border if a single tab is in use */
    private boolean singleTabShowInnerBorder = true;
    
    /** whether to show the outer border if a single tab is in use */
    private boolean singleTabShowOuterBorder = true;
    
    /** whether the tab is shown below the title, if there is a tab and a title */
    private boolean tabInside = false;
    
    /** all listeners known to this displayer */
    private List<DockableDisplayerListener> listeners = new ArrayList<DockableDisplayerListener>();
    
    /** the background algorithm of this panel */
    private Background background = new Background();
    
    /** the border strategy of this panel */
    private DisplayerBorder baseBorder;
    
    /** the border strategy of the content panel of this displayer */
    private DisplayerBorder contentBorder;
    
    /** this listener gets added to the current {@link SingleTabDecider} */
    private SingleTabDeciderListener singleTabListener = new SingleTabDeciderListener(){
    	public void showSingleTabChanged( SingleTabDecider source, Dockable dockable ){
    		if( dockable == BasicDockableDisplayer.this.dockable ){
    			updateDecorator();
    		}
    	}
    };
    
    /** the current {@link SingleTabDecider} */
    private PropertyValue<SingleTabDecider> decider = new PropertyValue<SingleTabDecider>( SingleTabDecider.SINGLE_TAB_DECIDER ){
    	@Override
    	protected void valueChanged( SingleTabDecider oldValue, SingleTabDecider newValue ){
    		if( oldValue != null )
    			oldValue.removeSingleTabDeciderListener( singleTabListener );
    		
    		if( newValue != null )
    			newValue.addSingleTabDeciderListener( singleTabListener );
    		
    		updateDecorator();
    	}
    };
    
    /** decorates this displayer */
    private BasicDockableDisplayerDecorator decorator;
    
    /** a listener added to {@link #decorator} */
    private BasicDockableDisplayerDecoratorListener decoratorListener = new BasicDockableDisplayerDecoratorListener(){
		public void moveableElementChanged( BasicDockableDisplayerDecorator decorator ){
			fireMoveableElementChanged();	
		}
	};
    
    /** the result {@link SingleTabDecider#showSingleTab(DockStation, Dockable)} returned */
    private boolean singleTabShowing;
    /** whether an update of the decorator is pending */
    private boolean pendingForcedUpdateDecorator = false;
    
    /** Tells whether to use a {@link DockActionDistributorSource} */
    private boolean stacked = false;
    
    
    /** the panel that shows the content of this displayer */
    private DisplayerContentPane content;
    
    /** notifies clients about the {@link Component}s of this displayer */
    private DockComponentRootHandler rootHandler;
    
    /**
     * Creates a new displayer
     * @param station the station for which this displayer is needed
     */
    public BasicDockableDisplayer( DockStation station ){
        this( station, null, null );
    }
    
    /**
     * Creates a new displayer, sets the title and the content.
     * @param station the station for which this displayer is needed
     * @param dockable the content, may be <code>null</code>
     * @param title the title, may be <code>null</code>
     */
    public BasicDockableDisplayer( DockStation station, Dockable dockable, DockTitle title ){
        this( station, dockable, title, Location.TOP );
    }
    
    /**
     * Creates a new displayer, sets the title, its location and the
     * content.
     * @param station the station for which this displayer is needed
     * @param dockable the content, may be <code>null</code>
     * @param title the title of <code>dockable</code>, can be <code>null</code>
     * @param location the location of the title, can be <code>null</code>
     */
    public BasicDockableDisplayer( DockStation station, Dockable dockable, DockTitle title, Location location ){
        super( new GridLayout( 1, 1 ), Transparency.DEFAULT );
        init( station, dockable, title, location );
    }
   
    /**
     * Creates a new displayer but does not set the properties of the
     * displayer. Subclasses may call {@link #init(DockStation, Dockable, DockTitle, bibliothek.gui.dock.station.DockableDisplayer.Location) init}
     * to initialize all variables of the new displayer.
     * @param station the station for which this displayer is needed
     * @param initialize <code>true</code> if all properties should be set
     * to default, <code>false</code> if nothing should happen, and 
     * {@link #init(DockStation, Dockable, DockTitle, bibliothek.gui.dock.station.DockableDisplayer.Location) init}
     * will be called.
     */
    protected BasicDockableDisplayer( DockStation station, boolean initialize ){
    	super( new GridLayout( 1, 1 ), Transparency.DEFAULT );
    	if( initialize ){
    		init( station, null, null, Location.TOP );
    	}
    }
    
    /**
     * Initializes all properties of this DockableDisplayer. This method should
     * only be called once, by a constructor of a subclass which invoked
     * <code>{@link #BasicDockableDisplayer(DockStation, boolean) DockableDisplayer( false )}</code>.
     * @param station the station for which this displayer is needed 
     * @param dockable the content, may be <code>null</code>
     * @param title the title of <code>dockable</code>, can be <code>null</code>
     * @param location the location of the title, can be <code>null</code>
     */
    protected void init( DockStation station, Dockable dockable, DockTitle title, Location location ){
//    	content.setOpaque( false );
    	content = createContentPane();
    	content.setBackground( background );
    	
    	setDecorator( new MinimalDecorator() );
    	setBackground( background );
    	
        setTitleLocation( location );
        setStation( station );
        setDockable( dockable );
        setTitle( title );
        setFocusable( true );
        
        setFocusCycleRoot( true );
        setFocusTraversalPolicy( new DockFocusTraversalPolicy( new DisplayerFocusTraversalPolicy( this ), true ));
        
        baseBorder = new DisplayerBorder( this, "basic.base" );
        contentBorder = new DisplayerBorder( content, "basic.content" );
        
        rootHandler = createRootHandler();
        rootHandler.addRoot( getComponent() );
    }
    
    /**
     * Creates the {@link DockComponentRootHandler} for this displayer. The root handler is required to inform the client
     * about all the {@link Component}s that are related to this displayer.
     * @return the new root handler
     */
    protected DockComponentRootHandler createRootHandler(){
    	return new DockComponentRootHandler( this ) {
			protected TraverseResult shouldTraverse( Component component ) {
				// do not visit title or Dockable
				DockTitle title = getTitle();
				if( title != null && title.getComponent() == component ){
					return TraverseResult.EXCLUDE;
				}
				
				Dockable dockable = getDockable();
				if( dockable != null && dockable.getComponent() == component ){
					return TraverseResult.EXCLUDE;
				}
				
				return TraverseResult.INCLUDE_CHILDREN;
			}
		};
    }
    
    /**
     * Creates a new {@link DisplayerContentPane} which will be used to show the contents of this
     * displayer.
     * @return the new content pane, not <code>null</code>
     */
    protected DisplayerContentPane createContentPane(){
    	return new DisplayerContentPane();
    }
    
    /**
     * Exchanges the decorator of this displayer.
     * @param decorator the new decorator
     */
    protected void setDecorator( BasicDockableDisplayerDecorator decorator ){
    	if( decorator == null )
    		throw new IllegalArgumentException( "decorator must not be null" );
    	
    	if( this.decorator != null ){
    		this.decorator.setDockable( null, null );
    		this.decorator.setController( null );
    		this.decorator.removeDecoratorListener( decoratorListener );
    	}
    	this.decorator = decorator;
    	
    	this.decorator.addDecoratorListener( decoratorListener );
    	this.decorator.setController( controller );
    	
    	resetDecorator();
    	
    	if( title != null ){
    		title.changed( new ActionsDockTitleEvent( dockable, decorator.getActionSuggestion() ) );
    	}
    	
    	fireMoveableElementChanged();
    	revalidate();
    	repaint();
    }
    
    /**
     * Replaces the current {@link BasicDockableDisplayerDecorator decorator} if necessary.
     */
    protected void updateDecorator(){
    	updateDecorator( false );
    }
    
    /**
     * Replaces the current {@link BasicDockableDisplayerDecorator decorator} if necessary.
     * @param force whether to force an update
     */
    protected void updateDecorator( boolean force ){
    	if( force ){
    		pendingForcedUpdateDecorator = true;
    	}
    	
    	if( dockable != null && station != null ){
    		boolean decision = decider.getValue().showSingleTab( station, dockable );
    		if( pendingForcedUpdateDecorator || decision != singleTabShowing ){
    			pendingForcedUpdateDecorator = false;
    			singleTabShowing = decision;
    			if( singleTabShowing )
    				setDecorator( createTabDecorator() );
    			else if( isStacked() )
    				setDecorator( createStackedDecorator() );
    			else
    				setDecorator( createMinimalDecorator() );
    		}
    		
    		updateBorder();
    	}
    }
    
    /**
     * Tells whether this displayer currently is showing a single tab.
     * @return whether a tab is shown
     */
    public boolean isSingleTabShowing(){
		return singleTabShowing;
	}
    
    /**
     * Tells this displayer that it is used inside a tabbed environment. This displayer will call
     * {@link #createStackedDecorator()} instead of {@link #createMinimalDecorator()}.
     * @param stacked whether this displayer is part of a stack of displayers
     */
    public void setStacked( boolean stacked ){
    	if( this.stacked != stacked ){
    		this.stacked = stacked;
    		updateDecorator( true );
    	}
	}
    
    /**
     * Tells this displayer that it is used inside a tabbed environment. This displayer will call
     * {@link #createStackedDecorator()} instead of {@link #createMinimalDecorator()}.
     * @return whether this displayer is part of a stack of displayers
     */
    public boolean isStacked(){
		return stacked;
	}
    
    /**
     * Creates a new {@link MinimalDecorator} that will be shown on this displayer.
     * @return the new decorator
     */
    protected BasicDockableDisplayerDecorator createMinimalDecorator(){
		return new MinimalDecorator();
	}
    
    /**
     * Creates a new decorator that will be shown in this displayer if the displayer is
     * shown alongside a tab {@link #setStacked(boolean)}. The default implementation
     * return {@link #createMinimalDecorator()}. Subclasses may call {@link #createStackedDecorator(PropertyKey)}
     * to easily create a fitting decorator.
     * @return the new decorator
     * @see #createStackedDecorator(PropertyKey)
     * @see #setStacked(boolean)
     */
    protected BasicDockableDisplayerDecorator createStackedDecorator(){
    	return createMinimalDecorator();
    }
    
    /**
     * Creates a new {@link MinimalDecorator} that uses a restricted set of {@link DockAction}s.
     * @param distributor the key to the filter for the actions
     * @return the new decorator
     */
    protected BasicDockableDisplayerDecorator createStackedDecorator( final PropertyKey<DockActionDistributor> distributor ){
    	return new MinimalDecorator(){
			private DockActionDistributorSource source = new DockActionDistributorSource( Target.TITLE, distributor );
			
			@Override
			public void setDockable( Component content, Dockable dockable ){
				super.setDockable( content, dockable );
				source.setDockable( dockable );
			}
			
			@Override
			public DockActionSource getActionSuggestion(){
				return source;
			}
		};
    }
    
    /**
     * Creates a new {@link TabDecorator} that will be shown on this displayer.
     * @return the new decorator
     */
    protected BasicDockableDisplayerDecorator createTabDecorator(){
    	return new TabDecorator( station, null );
    }
    
    public void setController( DockController controller ) {
    	rootHandler.setController( null );
    	this.controller = controller;
    	decider.setProperties( controller );
    	decorator.setController( controller );
    	background.setController( controller );
    	baseBorder.setController( controller );
    	contentBorder.setController( controller );
    	resetDecorator();
    	rootHandler.setController( controller );
    }
    
    public DockController getController() {
        return controller;
    }
    
    public void setComponentConfiguration( DockComponentConfiguration configuration ) {
	    rootHandler.setConfiguration( configuration );	
    }
    
    public DockComponentConfiguration getComponentConfiguration() {
    	return rootHandler.getConfiguration();
    }
    
    public void addDockableDisplayerListener( DockableDisplayerListener listener ){
	    listeners.add( listener );	
    }
    
    public void removeDockableDisplayerListener( DockableDisplayerListener listener ){
    	listeners.remove( listener );
    }
    
    /**
     * Gets a list of all listeners currently registered at this displayer.
     * @return the list of listeners
     */
    protected DockableDisplayerListener[] listeners(){
    	return listeners.toArray( new DockableDisplayerListener[ listeners.size() ] );
    }
    
    /**
     * Calls {@link DockableDisplayerListener#moveableElementChanged(DockableDisplayer)} on any
     * listener that is registered.
     */
    protected void fireMoveableElementChanged(){
    	for( DockableDisplayerListener listener : listeners() ){
    		listener.moveableElementChanged( this );
    	}
    }
    
    public void setStation( DockStation station ) {
        this.station = station;
        updateDecorator();
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
        }
        
        hints.setShowBorderHint( null );
        this.dockable = dockable;
        
        updateDecorator();
    	resetDecorator();
    	
        if( dockable != null ){
            this.dockable.configureDisplayerHints( hints );
        }
        
        revalidate();
    }
    
    /**
     * Resets the decorator, this method removes all {@link Component}s from this displayer, then adds them again
     * in the order that is necessary according to the current settings
     */
    protected void resetDecorator(){
    	removeAll();
    	
    	if( tabInside ){
    		if( dockable == null ){
    			content.setDockable( null );
    			decorator.setDockable( null, null );
    		}
    		else{
	    		content.setDockable( null );
	    		decorator.setDockable( getComponent( dockable ), dockable );
	    		content.setDockable( decorator.getComponent() );
    		}
    		
    		add( content );
    	}
    	else{
    		if( dockable == null ){
    			content.setDockable( null );
    		}
    		else{
    			content.setDockable( getComponent( dockable ) );
    		}
    		decorator.setDockable( content, dockable );
    		Component newComponent = decorator.getComponent();
        	if( newComponent != null ){
        		add( newComponent );
        	}
    	}
    }

    public Location getTitleLocation() {
        return location;
    }

    public void setTitleLocation( Location location ) {
        if( location == null )
            location = Location.TOP;
        
        this.location = location;
        
        content.setTitleLocation( location );
        
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
        this.title = title;
        if( title == null ){
        	content.setTitle( null );
        }
        else{
        	content.setTitle( getComponent( title ) );
        }
        
        if( title != null ){
            title.setOrientation( orientation( location ));
            
            if( decorator != null ){
            	title.changed( new ActionsDockTitleEvent( dockable, decorator.getActionSuggestion() ) );
            }
        }
        
        fireMoveableElementChanged();
        revalidate();
    }
    
    public DockElementRepresentative getMoveableElement(){
    	if( title != null ){
    		return title;
    	}
    	if( decorator != null ){
    		DockElementRepresentative result = decorator.getMoveableElement();
    		if( result != null ){
    			return result;
    		}
    	}
    	return getDockable();
    }

    public Point getTitleCenter() {
    	DockElementRepresentative moveable = getMoveableElement();
    	if( moveable == null ){
    		return null;
    	}
    	Component component = moveable.getComponent();
    	
    	if( !SwingUtilities.isDescendingFrom( component, getComponent() )){
    		return null;
    	}
    	
    	Point topLeft = new Point( 0, 0 );
    	
    	topLeft = SwingUtilities.convertPoint( component, topLeft, getComponent() );
    	Dimension size = component.getSize();
    	
    	return new Point( topLeft.x + size.width/2, topLeft.y + size.height/2 );
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
    
    public Insets getDockableInsets() {
        Insets insets = getInsets();
        if( insets == null )
            insets = new Insets(0,0,0,0);
        
        Insets decorator = this.decorator.getDockableInsets();
        insets.left += decorator.left;
        insets.right += decorator.right;
        insets.top += decorator.top;
        insets.bottom += decorator.bottom;
        
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
     * Sets whether an inner border should be shown if a single tab is in use.
     * @param singleTabShowInnerBorder whether the inner border should be visible
     */
    public void setSingleTabShowInnerBorder( boolean singleTabShowInnerBorder ){
		this.singleTabShowInnerBorder = singleTabShowInnerBorder;
		updateBorder();
	}
    
    /**
     * Tells whether an inner border is shown if a single tab is in use.
     * @return whether the border is shown
     */
    public boolean isSingleTabShowInnerBorder(){
		return singleTabShowInnerBorder;
	}
    
    /**
     * Sets whether an outer border should be shown if a single tab is in use.
     * @param singleTabShowOuterBorder whether the outer border should be visible
     */
    public void setSingleTabShowOuterBorder( boolean singleTabShowOuterBorder ){
		this.singleTabShowOuterBorder = singleTabShowOuterBorder;
		updateBorder();
	}
    
    /**
     * Tells whether an outer border is shown if a single tab is in use.
     * @return whether the border is shown
     */
    public boolean isSingleTabShowOuterBorder(){
		return singleTabShowOuterBorder;
	}
    
    /**
     * Tells whether the tab is shown below the title, if there is a tab and a title.
     * @return the location of the tab in respect to the title, the default value is <code>false</code>
     */
    public boolean isTabInside(){
		return tabInside;
	}
    
    /**
     * Sets the location of the tab (if present) in respect to the title (if present).
     * @param tabInside <code>true</code> if the tab is to be shown nearer to the center than the title
     */
    public void setTabInside( boolean tabInside ){
		this.tabInside = tabInside;
		resetDecorator();
	}
    
    @Override
    public void updateUI(){
    	super.updateUI();
    	updateBorder();
    }
    
    /**
     * Called when the hint, whether a border should be shown or not, has changed. 
     */
    protected void updateBorder(){
    	if( singleTabShowing ){
    		if( singleTabShowInnerBorder )
    			setContentBorder( getDefaultBorder() );
    		else
    			setContentBorder( null );
    		
    		if( singleTabShowOuterBorder )
    			setBaseBorder( getDefaultBorder() );
    		else
    			setBaseBorder( null );
    	}
    	else{
    		setContentBorder( null );
    		
    		if( respectBorderHint ){
                boolean show = hints.getShowBorderHint();
                
                if( show ){
                	setBaseBorder( getDefaultBorder() );
                }
                else{
                	setBaseBorder( null );
                }
            }
    		else{
    			if( defaultBorderHint )
    				setBaseBorder( getDefaultBorder() );
    			else
    				setBaseBorder( null );
    		}
    	}
    }
    
    /**
     * Sets the border that wraps around the entire displayer.
     * @param border the new border, can be <code>null</code>
     */
    public void setBaseBorder( Border border ){
    	if( baseBorder != null ){
    		baseBorder.setBorder( border );
    	}
    }
    
    /**
     * Sets the border that wraps around the content component.
     * @param border the new border, can be <code>null</code>
     */
    public void setContentBorder( Border border ){
    	if( contentBorder != null ){
    		contentBorder.setBorder( border );
    	}
    }

    public DisplayerCombinerTarget prepareCombination( CombinerSource source, Enforcement force ){
    	if( decorator instanceof TabDecorator ){
    		TabDisplayerCombinerTarget target = new TabDisplayerCombinerTarget( this, ((TabDecorator)decorator).getStackComponent(), source, force );
    		if( target.isValid() ){
    			return target;
    		}
    	}
    	return null;
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
        
        public DockStation getStation(){
        	return station;
        }
        
        public void setShowBorderHint( Boolean border ) {
            if( this.border != border ){
                this.border = border;
                updateBorder();
            }
        }
        
        /**
         * Gets the hint that tells whether the border should be shown or not.
         * @return whether the border should be shown
         */
        public boolean getShowBorderHint() {
            if( border != null )
                return border.booleanValue();
            
            return defaultBorderHint;
        }
    }
    
    /**
     * The background of this {@link BasicDockableDisplayer}.
     * @author Benjamin Sigg
     */
    private class Background extends BackgroundAlgorithm implements DisplayerBackgroundComponent{
    	/**
    	 * Creates a new object
    	 */
    	public Background(){
    		super( DisplayerBackgroundComponent.KIND, ThemeManager.BACKGROUND_PAINT + ".displayer");
    	}
    	
    	public Component getComponent(){
    		return BasicDockableDisplayer.this;
    	}
    	
    	public DockableDisplayer getDisplayer(){
    		return BasicDockableDisplayer.this;
    	}
    }
    
    /**
     * The border of this displayer.
     * @author Benjamin Sigg
     */
    protected class DisplayerBorder extends BorderForwarder implements DisplayerDockBorder{
    	/**
    	 * Creates a new object.
    	 * @param target the component whose border will be set
    	 * @param idSuffix suffix for the identifier of this {@link UIValue}
    	 */
    	public DisplayerBorder( JComponent target, String idSuffix ){
    		super( DisplayerDockBorder.KIND, ThemeManager.BORDER_MODIFIER + ".displayer." + idSuffix, target );
    	}
    	
		public DockableDisplayer getDisplayer(){
			return BasicDockableDisplayer.this;
		}
    }
}
