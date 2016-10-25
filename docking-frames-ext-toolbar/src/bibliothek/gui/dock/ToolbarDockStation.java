/*
 * Bibliothek - DockingFrames
 * Library built on Java/Swing, allows the user to "drag and drop"
 * panels containing any Swing-Component the developer likes to add.
 * 
 * Copyright (C) 2012 Herve Guillaume, Benjamin Sigg
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
 * Herve Guillaume
 * rvguillaume@hotmail.com
 * FR - France
 *
 * Benjamin Sigg
 * benjamin_sigg@gmx.ch
 * CH - Switzerland
 */

package bibliothek.gui.dock;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.LayoutManager;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.HierarchyEvent;
import java.awt.event.HierarchyListener;
import java.io.IOException;
import java.util.Map;

import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import bibliothek.gui.DockController;
import bibliothek.gui.DockStation;
import bibliothek.gui.DockUI;
import bibliothek.gui.Dockable;
import bibliothek.gui.Orientation;
import bibliothek.gui.dock.component.DefaultDockStationComponentRootHandler;
import bibliothek.gui.dock.component.DockComponentRootHandler;
import bibliothek.gui.dock.displayer.DockableDisplayerHints;
import bibliothek.gui.dock.event.DockStationAdapter;
import bibliothek.gui.dock.layout.DockableProperty;
import bibliothek.gui.dock.layout.location.AsideAnswer;
import bibliothek.gui.dock.layout.location.AsideRequest;
import bibliothek.gui.dock.security.SecureContainer;
import bibliothek.gui.dock.station.DisplayerCollection;
import bibliothek.gui.dock.station.DisplayerFactory;
import bibliothek.gui.dock.station.DockableDisplayer;
import bibliothek.gui.dock.station.DockableDisplayerListener;
import bibliothek.gui.dock.station.OverpaintablePanel;
import bibliothek.gui.dock.station.PlaceholderMapping;
import bibliothek.gui.dock.station.StationChildHandle;
import bibliothek.gui.dock.station.StationDropItem;
import bibliothek.gui.dock.station.StationDropOperation;
import bibliothek.gui.dock.station.layer.DockStationDropLayer;
import bibliothek.gui.dock.station.stack.StackDockProperty;
import bibliothek.gui.dock.station.support.ConvertedPlaceholderListItem;
import bibliothek.gui.dock.station.support.DockablePlaceholderList;
import bibliothek.gui.dock.station.support.DockableShowingManager;
import bibliothek.gui.dock.station.support.PlaceholderList;
import bibliothek.gui.dock.station.support.PlaceholderListItemAdapter;
import bibliothek.gui.dock.station.support.PlaceholderListItemConverter;
import bibliothek.gui.dock.station.support.PlaceholderListMapping;
import bibliothek.gui.dock.station.support.PlaceholderMap;
import bibliothek.gui.dock.station.support.PlaceholderStrategy;
import bibliothek.gui.dock.station.toolbar.SpanToolbarLayoutManager;
import bibliothek.gui.dock.station.toolbar.ToolbarDockStationFactory;
import bibliothek.gui.dock.station.toolbar.ToolbarDropInfo;
import bibliothek.gui.dock.station.toolbar.ToolbarProperty;
import bibliothek.gui.dock.station.toolbar.layer.ToolbarSlimDropLayer;
import bibliothek.gui.dock.themes.DefaultDisplayerFactoryValue;
import bibliothek.gui.dock.themes.DefaultStationPaintValue;
import bibliothek.gui.dock.themes.ThemeManager;
import bibliothek.gui.dock.themes.basic.BasicDockTitleFactory;
import bibliothek.gui.dock.title.DockTitleFactory;
import bibliothek.gui.dock.title.DockTitleVersion;
import bibliothek.gui.dock.toolbar.expand.ExpandedState;
import bibliothek.gui.dock.util.ConfiguredBackgroundPanel;
import bibliothek.gui.dock.util.DockUtilities;
import bibliothek.gui.dock.util.PropertyKey;
import bibliothek.gui.dock.util.PropertyValue;
import bibliothek.gui.dock.util.Transparency;
import bibliothek.gui.dock.util.extension.Extension;
import bibliothek.gui.dock.util.property.ConstantPropertyFactory;
import bibliothek.util.Path;

/**
 * A {@link Dockable} and a {@link DockStation} which stands for a group of
 * {@link ToolbarItemDockable}. As dockable it can be put in {@link DockStation}
 * which implements marker interface {@link ToolbarInterface}. As DockStation it
 * accept a {@link ToolbarItemDockable} or a {@link ToolbarDockStation}
 * 
 * @author Herve Guillaume
 */
public class ToolbarDockStation extends AbstractToolbarDockStation {

	/** the id of the {@link DockTitleFactory} which is used by this station */
	public static final String TITLE_ID = "toolbar";
	/**
	 * This id is forwarded to {@link Extension}s which load additional
	 * {@link DisplayerFactory}s
	 */
	public static final String DISPLAYER_ID = "toolbar";
	
	/** Key for setting the size of the gap between the children of a {@link ToolbarDockStation}. */
	public static final PropertyKey<Integer> GAP = new PropertyKey<Integer>( "dock.toolbar.gap", new ConstantPropertyFactory<Integer>( Integer.valueOf( 2 ) ), true );
	
	/** Key for setting the size of the gap between the children of a station and the border of the station */
	public static final PropertyKey<Integer> SIDE_GAP = new PropertyKey<Integer>( "dock.toolbar.sidegap", new ConstantPropertyFactory<Integer>( Integer.valueOf( 4 ) ), true );

	/** A list of all children */
	protected DockablePlaceholderList<StationChildHandle> dockables = new DockablePlaceholderList<StationChildHandle>();

	/**
	 * The graphical representation of this station: the pane which contains
	 * component
	 */
	private OverpaintablePanelBase mainPanel;

	/**
	 * Size of the lateral zone where no drop action can be done (Measured in
	 * pixel).
	 */
	private int lateralNodropZoneSize = 2;

	/** current {@link PlaceholderStrategy} */
	private final PropertyValue<PlaceholderStrategy> placeholderStrategy = new PropertyValue<PlaceholderStrategy>( PlaceholderStrategy.PLACEHOLDER_STRATEGY ){
		@Override
		protected void valueChanged( PlaceholderStrategy oldValue, PlaceholderStrategy newValue ){
			dockables.setStrategy( newValue );
		}
	};

	/** a manager to inform listeners about changes in the visibility state */
	private DockableShowingManager visibility;

	/** added to the current parent of this dockable */
	private VisibleListener visibleListener;

	/** the {@link LayoutManager} positioning the children of this station */
	private SpanToolbarLayoutManager layoutManager;

	/** information about the {@link Dockable} that is currently dropped */
	private DropInfo dropInfo;
	
	/** size of the gap between children */
	private PropertyValue<Integer> gap = new PropertyValue<Integer>( GAP ){
		@Override
		protected void valueChanged( Integer oldValue, Integer newValue ){
			layoutManager.setGap( newValue.intValue() );
		}
	};
	
	/** size of the gap between children and border */
	private PropertyValue<Integer> sideGap = new PropertyValue<Integer>( SIDE_GAP ){
		@Override
		protected void valueChanged( Integer oldValue, Integer newValue ){
			layoutManager.setSideGap( newValue.intValue() );
		}
	};

	// ########################################################
	// ############ Initialization Managing ###################
	// ########################################################

	/**
	 * Creates a new {@link ToolbarDockStation}.
	 */
	public ToolbarDockStation(){
		init();
	}

	protected void init(){
		super.init( ThemeManager.BACKGROUND_PAINT + ".station.toolbar" );
		mainPanel = createMainPanel();
		mainPanel.setupLayout();
		paint = new DefaultStationPaintValue( ThemeManager.STATION_PAINT + ".toolbar", this );
		setOrientation( getOrientation() );
		displayerFactory = createDisplayerFactory();
		displayers = new DisplayerCollection( this, displayerFactory, getDisplayerId() );
		displayers.addDockableDisplayerListener( new DockableDisplayerListener(){
			@Override
			public void discard( DockableDisplayer displayer ){
				ToolbarDockStation.this.discard( displayer );
			}
			
			@Override
			public void moveableElementChanged( DockableDisplayer displayer ){
				// ignore
			}
		} );

		setTitleIcon( null );

		visibility = new DockableShowingManager( listeners );
		visibleListener = new VisibleListener();

		getComponent().addHierarchyListener( new HierarchyListener(){
			public void hierarchyChanged( HierarchyEvent e ){
				if( (e.getChangeFlags() & HierarchyEvent.SHOWING_CHANGED) != 0 ) {
					if( getDockParent() == null ) {
						getDockableStateListeners().checkShowing();
					}

					visibility.fire();
				}
			}
		} );
	}
	
	@Override
	protected DockComponentRootHandler createRootHandler() {
		return new DefaultDockStationComponentRootHandler( this, displayers );
	}

	// ########################################################
	// ############ General DockStation Managing ##############
	// ########################################################

	@Override
	public Component getComponent(){
		return mainPanel;
	}
	
	@Override
	public void configureDisplayerHints( DockableDisplayerHints hints ){
		super.configureDisplayerHints( hints );
		if( hints != null ){
			if( hints.getStation() instanceof ScreenDockStation ){
				hints.setShowBorderHint( Boolean.TRUE );
			}
		}
	}

	@Override
	public int getDockableCount(){
		return dockables.dockables().size();
	}

	@Override
	public Dockable getDockable( int index ){
		return dockables.dockables().get( index ).getDockable();
	}

	@Override
	public String getFactoryID(){
		return ToolbarDockStationFactory.ID;
	}

	/**
	 * Sets the size of the two lateral zones where no drop action can be done
	 * (Measured in pixel).
	 * 
	 * @param lateralNodropZoneSize
	 *            the size of the rectangular lateral zones (in pixel)
	 * @throws IllegalArgumentException
	 *             if the size is smaller than 0
	 */
	public void setLateralNodropZoneSize( int lateralNodropZoneSize ){
		if( lateralNodropZoneSize < 0 ) {
			throw new IllegalArgumentException( "borderSideSnapeSize must not be less than 0" );
		}
		this.lateralNodropZoneSize = lateralNodropZoneSize;
	}

	/**
	 * Gets the size of the two lateral zones where no drop action can be done
	 * (Measured in pixel).
	 * 
	 * @return the size of the rectangular lateral zones (in pixel)
	 */
	public int getLateralNodropZoneSize(){
		return lateralNodropZoneSize;
	}

	@Override
	public void setDockParent( DockStation station ){
		DockStation old = getDockParent();
		if( old != null )
			old.removeDockStationListener( visibleListener );

		super.setDockParent( station );

		if( station != null )
			station.addDockStationListener( visibleListener );

		visibility.fire();
	}

	@Override
	public void setController( DockController controller ){
		if( getController() != controller ) {
			if( getController() != null ) {
				dockables.unbind();
			}
			for( final StationChildHandle handle : dockables.dockables() ) {
				handle.setTitleRequest( null );
			}

			super.setController( controller );
			// if not set controller of the DefaultStationPaintValue, call to
			// DefaultStationPaintValue do nothing

			if( controller == null ) {
				title = null;
			}
			else {
				title = registerTitle( controller );
			}
			paint.setController( controller );
			placeholderStrategy.setProperties( controller );
			displayerFactory.setController( controller );
			displayers.setController( controller );
			mainPanel.setController( controller );
			layoutManager.setController( controller );
			gap.setProperties( controller );
			sideGap.setProperties( controller );

			if( controller != null ) {
				dockables.bind();
			}
			for( final StationChildHandle handle : dockables.dockables() ) {
				handle.setTitleRequest( title, true );
			}

			visibility.fire();
		}
	}

	// ########################################################
	// ############ Orientation Managing ######################
	// ########################################################

	@Override
	public void setOrientation( Orientation orientation ){
		// it's very important to change position and orientation of inside
		// dockables first, else doLayout() is done on wrong inside information
		this.orientation = orientation;
		fireOrientingEvent();
		mainPanel.revalidate();
	}

	// ########################################################
	// ############### Drop/Move Managing #####################
	// ########################################################

	@Override
	public DockStationDropLayer[] getLayers(){
		return new DockStationDropLayer[]{ new ToolbarSlimDropLayer( this ) };
	}

	@Override
	public boolean accept( Dockable child ){
		return getToolbarStrategy().isToolbarPart( child );
	}

	@Override
	public boolean accept( DockStation station ){
		return getToolbarStrategy().isToolbarGroupPartParent( station, this, false );
	}

	public boolean accept( DockStation base, Dockable neighbor ){
		return false;
	}

	@Override
	public StationDropOperation prepareDrop( StationDropItem item ){
		// System.out.println(this.toString() + "## prepareDrop(...) ##");
		final DockController controller = getController();

		if( getExpandedState() == ExpandedState.EXPANDED ) {
			return null;
		}

		Dockable dockable = item.getDockable();
		// check if the dockable and the station accept each other
		if( this.accept( dockable ) && dockable.accept( this ) ) {
			// check if controller exist and if the controller accept that
			// the dockable become a child of this station
			if( controller != null ) {
				if( !controller.getAcceptance().accept( this, dockable ) ) {
					return null;
				}
			}
			Point mouse = new Point( item.getMouseX(), item.getMouseY() );
			SwingUtilities.convertPointFromScreen( mouse, mainPanel.getDockablePane() );
			int index = layoutManager.getInsertionIndex( mouse.x, mouse.y );
			DropInfo info = new DropInfo( dockable, index );
			if( info.hasNoEffect() ) {
				return null;
			}
			return info;
		}
		else {
			return null;
		}
	}

	private class DropInfo extends ToolbarDropInfo {
		public DropInfo( Dockable dockable, int index ){
			super( dockable, ToolbarDockStation.this, index );
		}

		@Override
		public void execute(){
			dropInfo = null;
			layoutManager.setExpandedSpan( -1, false );

			if( isMove() ) {
				move( getItem(), getIndex() );
			}
			else {
				drop( getItem(), getIndex() );
			}
		}

		@Override
		public void destroy( StationDropOperation next ){
			if( dropInfo == this ) {
				dropInfo = null;
			}
			if( next == null || next.getTarget() != getTarget() ) {
				layoutManager.setExpandedSpan( -1, true );
			}
			mainPanel.repaint();
		}

		@Override
		public void draw(){
			dropInfo = this;
			layoutManager.setSpanSize( getItem() );
			layoutManager.setExpandedSpan( getIndex(), true );
			mainPanel.repaint();
		}
	}

	@Override
	public void drop( Dockable dockable ){
		// System.out.println(this.toString() + "## drop(Dockable dockable)##");
		this.drop( dockable, getDockableCount(), true );
	}

	/**
	 * Drops <code>dockable</code> at location <code>index</code>.
	 * 
	 * @param dockable
	 *            the element to add
	 * @param index
	 *            the location of <code>dockable</code>
	 * @return whether the operation was successful or not
	 */
	public boolean drop( Dockable dockable, int index ){
		return drop( dockable, index, false );
	}

	protected boolean drop( Dockable dockable, int index, boolean force ){
		// note: merging of two ToolbarGroupDockStations is done by the
		// ToolbarGroupDockStationMerger
		// System.out.println(this.toString()
		// + "## drop(Dockable dockable, int index)##");
		if( force || this.accept( dockable ) ) {
			if( !force ) {
				Dockable replacement = getToolbarStrategy().ensureToolbarLayer( this, dockable );
				if( replacement == null ) {
					return false;
				}
				if( replacement != dockable ) {
					replacement.asDockStation().drop( dockable );
					dockable = replacement;
				}
			}
			add( dockable, index );
			return true;
		}
		return false;
	}

	/**
	 * Adds dockable at the specified index. This method should be called in
	 * case of move action (it means when the dockable to insert already belongs
	 * to this station), because in this case this dockable was removed first.
	 * 
	 * @param dockable
	 *            the dockable to insert
	 * @param index
	 *            the index where insert the dockable
	 */
	protected void move( Dockable dockable, int index ){
		final DockController controller = getController();
		try {
			if( controller != null ) {
				controller.freezeLayout();
			}
			int current = indexOf( dockable );
			if( current == -1 ) {
				throw new IllegalArgumentException( "dockable is not known to this station" );
			}
			if( current < index ) {
				index--;
			}
			if( current != index ) {
				this.add( dockable, index );
			}
		}
		finally {
			if( controller != null ) {
				controller.meltLayout();
			}
		}
	}

	protected void add( Dockable dockable, int index ){
		add( dockable, index, null );
	}

	protected void add( Dockable dockable, int index, Path placeholder ){
		DockUtilities.ensureTreeValidity( this, dockable );
		DockUtilities.checkLayoutLocked();
		// Case where dockable is instance of ToolbarDockStation is handled by
		// the "ToolbarDockStationMerger"
		// Case where dockable is instance of ToolbarGroupDockStation is handled
		// by the "ToolbarStrategy.ensureToolbarLayer" method
		Dockable replacement = getToolbarStrategy().ensureToolbarLayer( this, dockable );
		if( replacement != dockable ) {
			replacement.asDockStation().drop( dockable );
			dockable = replacement;
		}
		
		if( getExpandedState() == ExpandedState.EXPANDED && getDockableCount() == 1){
			DockStation stack = getDockable( 0 ).asDockStation();
			stack.drop( dockable, new StackDockProperty( index, placeholder ) );
		}
		else{
			final DockHierarchyLock.Token token = DockHierarchyLock.acquireLinking( this, dockable );
			try {
				listeners.fireDockableAdding( dockable );
				int inserted = -1;
	
				final StationChildHandle handle = new StationChildHandle( this, displayers, dockable, title );
				handle.updateDisplayer();
	
				if( (placeholder != null) && (dockables.getDockableAt( placeholder ) == null) ) {
					inserted = dockables.put( placeholder, handle );
				}
				else if( placeholder != null ) {
					index = dockables.getDockableIndex( placeholder );
				}
	
				if( inserted == -1 ) {
					getDockables().add( index, handle );
				}
				else {
					index = inserted;
				}
	
				insertAt( handle, index );
				listeners.fireDockableAdded( dockable );
				fireDockablesRepositioned( index + 1 );
			}
			finally {
				token.release();
			}
		}
	}

	protected void insertAt( StationChildHandle handle, int index ){
		final Dockable dockable = handle.getDockable();

		dockable.setDockParent( this );

		mainPanel.getDockablePane().add( handle.getDisplayer().getComponent(), index );
		mainPanel.getDockablePane().invalidate();

		mainPanel.revalidate();
		mainPanel.getContentPane().repaint();
	}

	@Override
	public void drag( Dockable dockable ){
		if( dockable.getDockParent() != this ) {
			throw new IllegalArgumentException( "The dockable cannot be dragged, it is not child of this station." );
		}
		this.remove( dockable );
	}

	/**
	 * Removes <code>dockable</code> from this station.<br>
	 * Note: clients may need to invoke {@link DockController#freezeLayout()}
	 * and {@link DockController#meltLayout()} to ensure none else adds or
	 * removes <code>Dockable</code>s.
	 * 
	 * @param dockable
	 *            the child to remove
	 */
	@Override
	protected void remove( Dockable dockable ){
		DockUtilities.checkLayoutLocked();
		final int index = indexOf( dockable );
		final StationChildHandle handle = dockables.dockables().get( index );

		if( getFrontDockable() == dockable ) {
			setFrontDockable( null );
		}

		final DockHierarchyLock.Token token = DockHierarchyLock.acquireUnlinking( this, dockable );
		try {
			listeners.fireDockableRemoving( dockable );
			dockable.setDockParent( null );

			dockables.remove( index );
			mainPanel.getDockablePane().remove( handle.getDisplayer().getComponent() );
			mainPanel.doLayout();
			mainPanel.revalidate();
			mainPanel.repaint();
			handle.destroy();
			listeners.fireDockableRemoved( dockable );
			fireDockablesRepositioned( index );
		}
		finally {
			token.release();
		}
	}

	/**
	 * Removes the child with the given <code>index</code> from this station.<br>
	 * Note: clients may need to invoke {@link DockController#freezeLayout()}
	 * and {@link DockController#meltLayout()} to ensure no-one else adds or
	 * removes <code>Dockable</code>s.
	 * 
	 * @param index
	 *            the index of the child that will be removed
	 */
	protected void remove( int index ){
		DockUtilities.checkLayoutLocked();
		final StationChildHandle handle = dockables.dockables().get( index );
		final Dockable dockable = getDockable( index );

		if( getFrontDockable() == dockable ) {
			setFrontDockable( null );
		}

		final DockHierarchyLock.Token token = DockHierarchyLock.acquireUnlinking( this, dockable );
		try {
			listeners.fireDockableRemoving( dockable );
			dockable.setDockParent( null );

			dockables.remove( index );
			mainPanel.getDockablePane().remove( handle.getDisplayer().getComponent() );
			mainPanel.doLayout();
			mainPanel.getContentPane().revalidate();
			mainPanel.getContentPane().repaint();
			handle.destroy();
			listeners.fireDockableRemoved( dockable );
			fireDockablesRepositioned( index );
		}
		finally {
			token.release();
		}
	}

	@Override
	public void replace( Dockable old, Dockable next ){
		DockUtilities.checkLayoutLocked();
		final DockController controller = getController();
		if( controller != null ) {
			controller.freezeLayout();
		}
		final int index = indexOf( old );
		remove( old );
		// the child is a ToolbarGroupDockStation because canReplace()
		// ensure it
		add( next, index );
		controller.meltLayout();
	}

	// ########################################################
	// ###################### UI Managing #####################
	// ########################################################

	@Override
	protected void callDockUiUpdateTheme() throws IOException{
		DockUI.updateTheme( this, new ToolbarDockStationFactory() );
	}

	@Override
	protected DefaultDisplayerFactoryValue createDisplayerFactory(){
		return new DefaultDisplayerFactoryValue( ThemeManager.DISPLAYER_FACTORY + ".toolbar", this );
	}

	/**
	 * Gets a unique identifier used to get the {@link DisplayerFactory} for this station.
	 * @return the unique identifier, not <code>null</code>
	 */
	protected String getDisplayerId(){
		return DISPLAYER_ID;
	}

	@Override
	protected DockTitleVersion registerTitle( DockController controller ){
		return controller.getDockTitleManager().getVersion( TITLE_ID, BasicDockTitleFactory.FACTORY );
	}

	/**
	 * Replaces <code>displayer</code> with a new {@link DockableDisplayer}.
	 * 
	 * @param displayer
	 *            the displayer to replace
	 * @throws IllegalArgumentException
	 *             if <code>displayer</code> is not a child of this station
	 */
	@Override
	protected void discard( DockableDisplayer displayer ){
		final Dockable dockable = displayer.getDockable();

		final int index = indexOf( dockable );
		if( index < 0 ) {
			throw new IllegalArgumentException( "displayer is not a child of this station: " + displayer );
		}

		final StationChildHandle handle = dockables.dockables().get( index );

		mainPanel.getDockablePane().remove( handle.getDisplayer().getComponent() );
		handle.updateDisplayer();
		insertAt( handle, index );
	}


	/**
	 * A panel with a fixed size (minimum, maximum and preferred size have
	 * same values).
	 * 
	 * @author Herve Guillaume
	 * 
	 */
	@SuppressWarnings("serial")
	protected class SizeFixedPanel extends ConfiguredBackgroundPanel {
		public SizeFixedPanel(){
			super( Transparency.SOLID );
			setBackground( ToolbarDockStation.this.getBackgroundAlgorithm() );
		}
		
		@Override
		public Dimension getPreferredSize(){
			final Dimension pref = super.getPreferredSize();
			// Insets insets = getInsets();
			// pref.height += insets.top + insets.bottom;
			// pref.width += insets.left + insets.right;
			return pref;
		}

		@Override
		public Dimension getMaximumSize(){
			return getPreferredSize();
		}

		@Override
		public Dimension getMinimumSize(){
			return getPreferredSize();
		}
	}
	
	/**
	 * Called by the constructor, this method creates the main component of this station.
	 * @return the main component, must not be <code>null</code>
	 */
	protected OverpaintablePanelBase createMainPanel(){
		return new OverpaintablePanelBase();
	}
	
	/**
	 * Creates the parent {@link JComponent} of the {@link Dockable}s that are shown in this
	 * station. The default behavior is to create a new {@link SizeFixedPanel}, using
	 * {@link #getBackgroundAlgorithm()} for managing painting.
	 * @return the new content pane
	 */
	@Override
	protected JPanel createBackgroundPanel(){
		return new SizeFixedPanel();
	}
	
	/**
	 * This panel is used as base of the station. All children of the station
	 * have this panel as parent too. It allows to draw arbitrary figures over
	 * the base panel
	 * 
	 * @author Herve Guillaume
	 */
	protected class OverpaintablePanelBase extends SecureContainer {

		/**
		 * Generated serial number
		 */
		private static final long serialVersionUID = -4399008463139189130L;


		/**
		 * The content Pane of this {@link OverpaintablePanel} (with a
		 * BoxLayout)
		 */
		private JComponent dockablePane;

		/**
		 * Creates a new panel
		 */
		public OverpaintablePanelBase(){
			setSolid( false );
		}
		
		/**
		 * Initializes this panel with the default layout, namely exactly one child which is the
		 *  {@link #setDockablePane(JComponent) dockable pane}.
		 */
		public void setupLayout(){
			setDockablePane( createBackgroundPanel() );
			setBasePane( dockablePane );
		}
		
		public void setDockablePane(JComponent pane){
			if(dockablePane != null){
				throw new IllegalStateException( "dockablePane is already set" );
			}
			dockablePane = pane;
			layoutManager = new SpanToolbarLayoutManager( ToolbarDockStation.this, dockablePane ){
				@Override
				protected void revalidate(){
					dockablePane.revalidate();
				}
			};
			dockablePane.setLayout( layoutManager );
		}
		
		public JComponent getDockablePane(){
			return dockablePane;
		}

		@Override
		public Dimension getPreferredSize(){
			return getBasePane().getPreferredSize();
		}

		@Override
		public Dimension getMinimumSize(){
			return getPreferredSize();
		}

		@Override
		public Dimension getMaximumSize(){
			return getPreferredSize();
		}

		private Insets subtractComponent( JComponent component, Insets insets ){
			Point topLeft = new Point( 0, 0 );
			topLeft = SwingUtilities.convertPoint( component, topLeft, this );
			
			insets.left += topLeft.x;
			insets.top += topLeft.y;
			
			insets.right += (getWidth() - component.getWidth()) - topLeft.x;
			insets.bottom += (getHeight() - component.getHeight()) - topLeft.y;
			
			return insets;
		}
		
		@Override
		protected void paintOverlay( Graphics g ){
			final Graphics2D g2D = (Graphics2D) g;
			paintRemoval( g );
			if( dropInfo != null ) {
				Insets insets = dockablePane.getInsets();
				if( insets == null ){
					insets = new Insets( 0, 0, 0, 0 );
				}
				insets = subtractComponent( dockablePane, insets );
				
				int index = dropInfo.getIndex();
				int x, y, width, height;
				if( getOrientation() == Orientation.HORIZONTAL ) {
					if( index == 0 ) {
						x = insets.left;
					}
					else {
						x = dockablePane.getComponent( index - 1 ).getX() + dockablePane.getComponent( index - 1 ).getWidth();
					}
					if( index == dockablePane.getComponentCount() ) {
						width = getWidth() - x - insets.right;
					}
					else {
						width = dockablePane.getComponent( index ).getX() - x;
					}
					y = insets.top;
					height = getHeight() - insets.top - insets.bottom;
				}
				else {
					if( index == 0 ) {
						y = insets.top;
					}
					else {
						y = dockablePane.getComponent( index - 1 ).getY() + dockablePane.getComponent( index - 1 ).getHeight();
					}
					if( index == dockablePane.getComponentCount() ) {
						height = getHeight() - y - insets.top;
					}
					else {
						height = dockablePane.getComponent( index ).getY() - y;
					}
					x = insets.left;
					width = getWidth() - insets.left - insets.right;
				}
				if( width > 0 && height > 0 ){
					Rectangle stationBounds = new Rectangle( 0, 0, getWidth(), getHeight() );
					paint.drawInsertion( g2D, stationBounds, new Rectangle( x, y, width, height ) );
				}
			}
		}

		private void paintRemoval( Graphics g ){
			Dockable removal = getRemoval();
			if( removal != null ) {
				for( StationChildHandle handle : dockables.dockables() ) {
					if( handle.getDockable() == removal ) {
						Dimension size = handle.getDisplayer().getComponent().getSize();
						Point location = new Point(0, 0);
						location = SwingUtilities.convertPoint( handle.getDisplayer().getComponent(), location, this );
						Rectangle bounds = new Rectangle( location, size );
						paint.drawRemoval( g, bounds, bounds );
						break;
					}
				}
			}
		}

		@Override
		public String toString(){
			return this.getClass().getSimpleName() + '@' + Integer.toHexString( hashCode() );
		}

	}

	// ########################################################
	// ############### PlaceHolder Managing ###################
	// ########################################################

	// TODO don't use ToolbarProperty but a custom class, would be much safer
	// if the layout is screwed up

	/**
	 * Creates a new {@link DockableProperty} describing the location of
	 * <code>child</code> on this station. This method is called by
	 * {@link #getDockableProperty(Dockable, Dockable)} once the location and
	 * placeholder of <code>child</code> or <code>target</code> have been
	 * calculated
	 * 
	 * @param child
	 *            a child of this station
	 * @param target
	 *            the item whose position is searched
	 * @param index
	 *            the location of <code>child</code>
	 * @param placeholder
	 *            the placeholder for <code>target</code> or <code>child</code>
	 * @return a new {@link DockableProperty} that stores <code>index</code>,
	 *         <code>placeholder</code> and any other information a subclass
	 *         deems necessary to store
	 */
	protected DockableProperty getDockableProperty( Dockable child, Dockable target, int index, Path placeholder ){
		return new ToolbarProperty( index, placeholder );
	}

	/**
	 * Tells whether the subclass knows how to handle <code>property</code>.
	 * This means that the type of <code>property</code> is the same type as the
	 * result of {@link #getDockableProperty(Dockable, Dockable, int, Path)}
	 * 
	 * @param property
	 *            the property to check
	 * @return <code>true</code> if this subclass knows how to handle the type
	 *         of <code>property</code>
	 */
	protected boolean isValidProperty( DockableProperty property ){
		return property instanceof ToolbarProperty;
	}

	/**
	 * Gets the location of a {@link Dockable} on this station. Called only if
	 * <code>property</code> passed {@link #isValidProperty(DockableProperty)}.
	 * 
	 * @param property
	 *            some property created by
	 *            {@link #getDockableProperty(Dockable, Dockable, int, Path)}
	 * @return the index parameter
	 */
	protected int getIndex( DockableProperty property ){
		return ((ToolbarProperty) property).getIndex();
	}

	protected Path getPlaceholder( DockableProperty property ){
		return ((ToolbarProperty) property).getPlaceholder();
	}

	/**
	 * Grants direct access to the list of {@link Dockable}s, subclasses should
	 * not modify the list unless the fire the appropriate events.
	 * 
	 * @return the list of dockables
	 */
	protected PlaceholderList.Filter<StationChildHandle> getDockables(){
		return dockables.dockables();
	}

	/**
	 * Gets the placeholders of this station using a
	 * {@link PlaceholderListItemConverter} to encode the children. The
	 * converter puts the following parameters for each {@link Dockable} into
	 * the map:
	 * <ul>
	 * <li>id: the integer from <code>children</code></li>
	 * <li>index: the location of the element in the dockables-list</li>
	 * <li>placeholder: the placeholder of the element, might be missing</li>
	 * </ul>
	 * 
	 * @param children
	 *            a unique identifier for each child of this station
	 * @return the map
	 */
	public PlaceholderMap getPlaceholders( final Map<Dockable, Integer> children ){
		final PlaceholderStrategy strategy = getPlaceholderStrategy();

		return dockables.toMap( new PlaceholderListItemAdapter<Dockable, StationChildHandle>(){
			@Override
			public ConvertedPlaceholderListItem convert( int index, StationChildHandle handle ){
				final Dockable dockable = handle.getDockable();

				final Integer id = children.get( dockable );
				if( id == null ) {
					return null;
				}

				final ConvertedPlaceholderListItem item = new ConvertedPlaceholderListItem();
				item.putInt( "id", id );
				item.putInt( "index", index );

				if( strategy != null ) {
					final Path placeholder = strategy.getPlaceholderFor( dockable );
					if( placeholder != null ) {
						item.putString( "placeholder", placeholder.toString() );
						item.setPlaceholder( placeholder );
					}
				}

				return item;
			}
		} );
	}

	/**
	 * Sets a new layout on this station, this method assumes that
	 * <code>map</code> was created by the method {@link #getPlaceholders(Map)}.
	 * 
	 * @param map
	 *            the map to read
	 * @param children
	 *            the new children of this station
	 * @throws IllegalStateException
	 *             if there are children left on this station
	 */
	public void setPlaceholders( PlaceholderMap map, final Map<Integer, Dockable> children ){
		DockUtilities.checkLayoutLocked();
		if( getDockableCount() > 0 ) {
			throw new IllegalStateException( "must not have any children" );
		}
		final DockController controller = getController();

		try {
			if( controller != null ) {
				controller.freezeLayout();
			}

			final DockablePlaceholderList<StationChildHandle> next = new DockablePlaceholderList<StationChildHandle>();

			if( getController() != null ) {
				dockables.setStrategy( null );
				dockables.unbind();
				dockables = next;
			}
			else {
				dockables = next;
			}

			next.read( map, new PlaceholderListItemAdapter<Dockable, StationChildHandle>(){
				private DockHierarchyLock.Token token;
				private int index = 0;

				@Override
				public StationChildHandle convert( ConvertedPlaceholderListItem item ){
					final int id = item.getInt( "id" );
					final Dockable dockable = children.get( id );
					if( dockable != null ) {
						DockUtilities.ensureTreeValidity( ToolbarDockStation.this, dockable );
						token = DockHierarchyLock.acquireLinking( ToolbarDockStation.this, dockable );
						listeners.fireDockableAdding( dockable );
						return new StationChildHandle( ToolbarDockStation.this, displayers, dockable, title );
					}
					return null;
				}

				@Override
				public void added( StationChildHandle handle ){
					try {
						handle.updateDisplayer();
						insertAt( handle, index++ );
						listeners.fireDockableAdded( handle.getDockable() );
					}
					finally {
						token.release();
					}
				}
			} );

			if( getController() != null ) {
				dockables.bind();
				dockables.setStrategy( getPlaceholderStrategy() );
			}
		}
		finally {
			if( controller != null ) {
				controller.meltLayout();
			}
		}
	}

	@Override
	public PlaceholderMap getPlaceholders(){
		return dockables.toMap();
	}

	@Override
	public PlaceholderMapping getPlaceholderMapping() {
		return new PlaceholderListMapping( this, dockables ){
			@Override
			public DockableProperty getLocationAt( Path placeholder ) {
				int index = dockables.getDockableIndex( placeholder );
				return new ToolbarProperty( index, placeholder );
			}
		};
	}
	
	@Override
	public void setPlaceholders( PlaceholderMap placeholders ){
		if( getDockableCount() > 0 ) {
			throw new IllegalStateException( "only allowed if there are not children present" );
		}

		try {
			final DockablePlaceholderList<StationChildHandle> next = new DockablePlaceholderList<StationChildHandle>( placeholders );
			if( getController() != null ) {
				dockables.setStrategy( null );
				dockables.unbind();
				dockables = next;
				dockables.bind();
				dockables.setStrategy( getPlaceholderStrategy() );
			}
			else {
				dockables = next;
			}
		}
		catch( final IllegalArgumentException ex ) {
			// silent
		}
	}

	/**
	 * Gets the {@link PlaceholderStrategy} that is currently in use.
	 * 
	 * @return the current strategy, may be <code>null</code>
	 */
	public PlaceholderStrategy getPlaceholderStrategy(){
		return placeholderStrategy.getValue();
	}

	/**
	 * Sets the {@link PlaceholderStrategy} to use, <code>null</code> will set
	 * the default strategy.
	 * 
	 * @param strategy
	 *            the new strategy, can be <code>null</code>
	 */
	public void setPlaceholderStrategy( PlaceholderStrategy strategy ){
		placeholderStrategy.setValue( strategy );
	}

	@Override
	public DockableProperty getDockableProperty( Dockable child, Dockable target ){
		final int index = indexOf( child );
		Path placeholder = null;
		final PlaceholderStrategy strategy = getPlaceholderStrategy();
		if( strategy != null ) {
			placeholder = strategy.getPlaceholderFor( target == null ? child : target );
			if( placeholder != null ) {
				dockables.dockables().addPlaceholder( index, placeholder );
			}
		}
		return getDockableProperty( child, target, index, placeholder );
	}
	
	public void aside( AsideRequest request ){
		int index = -1;
		int resultIndex = -1;
		
		if( getExpandedState() == ExpandedState.EXPANDED && getDockableCount() == 1 ){
			DockStation stack = getDockable( 0 ).asDockStation();
			AsideAnswer answer = request.forward( stack );
			if( answer.isCanceled() ){
				return;
			}
			DockableProperty answerLocation = answer.getLocation();
			if( answerLocation instanceof StackDockProperty ){
				resultIndex = ((StackDockProperty) answerLocation).getIndex();
			}
		}
		
		DockableProperty location = request.getLocation();
		Path newPlaceholder = request.getPlaceholder();
		if( location instanceof ToolbarProperty ){
			ToolbarProperty toolbarLocation = (ToolbarProperty)location;
			index = dockables.getNextListIndex( toolbarLocation.getIndex(), toolbarLocation.getPlaceholder() );
    		if( newPlaceholder != null ){
    			dockables.list().insertPlaceholder( index, newPlaceholder );
    		}
		}
		else{
			index = dockables.dockables().size();
			if( newPlaceholder != null ){
    			dockables.dockables().insertPlaceholder( index, newPlaceholder );
    		}
		}
		
		if( resultIndex == -1 ){
			resultIndex = index;
		}

		request.answer( new ToolbarProperty( index, newPlaceholder ));
	}

	@Override
	public boolean drop( Dockable dockable, DockableProperty property ){
		if( isValidProperty( property ) ) {
			final boolean acceptable = acceptable( dockable );
			boolean result = false;
			final int index = Math.min( getDockableCount(), getIndex( property ) );

			final Path placeholder = getPlaceholder( property );
			if( (placeholder != null) && (property.getSuccessor() != null) ) {
				final StationChildHandle preset = dockables.getDockableAt( placeholder );
				if( preset != null ) {
					final DockStation station = preset.getDockable().asDockStation();
					if( station != null ) {
						if( station.drop( dockable, property.getSuccessor() ) ) {
							dockables.removeAll( placeholder );
							result = true;
						}
					}
				}
			}

			if( !result && (placeholder != null) ) {
				if( acceptable && dockables.hasPlaceholder( placeholder ) ) {
					add( dockable, index, placeholder );
					result = true;
				}
			}

			if( !result && (dockables.dockables().size() == 0) ) {
				if( acceptable ) {
					drop( dockable );
					result = true;
				}
			}

			if( !result ) {
				if( (index < dockables.dockables().size()) && (property.getSuccessor() != null) ) {
					final DockStation child = getDockable( index ).asDockStation();
					if( child != null ) {
						result = child.drop( dockable, property.getSuccessor() );
					}
				}
			}

			if( !result && acceptable ) {
				result = drop( dockable, index );
			}

			return result;
		}
		return false;
	}

	@Override
	public void move( Dockable dockable, DockableProperty property ){
		// TODO pending
	}

	/**
	 * This listener is added to the parent of this station and will forward an
	 * event to {@link ToolbarContainerDockStation#visibility} if the visibility
	 * of the station changes.
	 * 
	 * @author Benjamin Sigg
	 */
	private class VisibleListener extends DockStationAdapter {
		@Override
		public void dockableShowingChanged( DockStation station, Dockable dockable, boolean visible ){
			if( dockable == ToolbarDockStation.this ) {
				visibility.fire();
			}
		}
	}
}
