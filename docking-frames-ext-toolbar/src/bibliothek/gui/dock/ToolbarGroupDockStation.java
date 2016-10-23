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

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.LayoutManager;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.AdjustmentEvent;
import java.awt.event.AdjustmentListener;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import bibliothek.gui.DockController;
import bibliothek.gui.DockStation;
import bibliothek.gui.DockUI;
import bibliothek.gui.Dockable;
import bibliothek.gui.Orientation;
import bibliothek.gui.dock.component.DefaultDockStationComponentRootHandler;
import bibliothek.gui.dock.component.DockComponentRootHandler;
import bibliothek.gui.dock.event.DockStationListener;
import bibliothek.gui.dock.layout.DockableProperty;
import bibliothek.gui.dock.layout.location.AsideAnswer;
import bibliothek.gui.dock.layout.location.AsideRequest;
import bibliothek.gui.dock.security.SecureContainer;
import bibliothek.gui.dock.station.DisplayerCollection;
import bibliothek.gui.dock.station.DisplayerFactory;
import bibliothek.gui.dock.station.DockableDisplayer;
import bibliothek.gui.dock.station.DockableDisplayerListener;
import bibliothek.gui.dock.station.PlaceholderMapping;
import bibliothek.gui.dock.station.StationChildHandle;
import bibliothek.gui.dock.station.StationDropItem;
import bibliothek.gui.dock.station.StationDropOperation;
import bibliothek.gui.dock.station.layer.DockStationDropLayer;
import bibliothek.gui.dock.station.support.ConvertedPlaceholderListItem;
import bibliothek.gui.dock.station.support.PlaceholderList.Level;
import bibliothek.gui.dock.station.support.PlaceholderMap;
import bibliothek.gui.dock.station.support.PlaceholderStrategy;
import bibliothek.gui.dock.station.toolbar.ToolbarGroupDockStationFactory;
import bibliothek.gui.dock.station.toolbar.ToolbarGroupDockStationLayout;
import bibliothek.gui.dock.station.toolbar.ToolbarStrategy;
import bibliothek.gui.dock.station.toolbar.group.ColumnScrollBar;
import bibliothek.gui.dock.station.toolbar.group.ColumnScrollBarFactory;
import bibliothek.gui.dock.station.toolbar.group.DefaultToolbarGroupDividierStrategy;
import bibliothek.gui.dock.station.toolbar.group.SlimScrollbar;
import bibliothek.gui.dock.station.toolbar.group.ToolbarColumn;
import bibliothek.gui.dock.station.toolbar.group.ToolbarColumnModel;
import bibliothek.gui.dock.station.toolbar.group.ToolbarGroupDividerStrategy;
import bibliothek.gui.dock.station.toolbar.group.ToolbarGroupDividerStrategyFactory;
import bibliothek.gui.dock.station.toolbar.group.ToolbarGroupDropInfo;
import bibliothek.gui.dock.station.toolbar.group.ToolbarGroupExpander;
import bibliothek.gui.dock.station.toolbar.group.ToolbarGroupHeader;
import bibliothek.gui.dock.station.toolbar.group.ToolbarGroupHeaderFactory;
import bibliothek.gui.dock.station.toolbar.group.ToolbarGroupPlaceholderMapping;
import bibliothek.gui.dock.station.toolbar.group.ToolbarGroupProperty;
import bibliothek.gui.dock.station.toolbar.layer.ToolbarGroupInnerLayer;
import bibliothek.gui.dock.station.toolbar.layer.ToolbarGroupOuterLayer;
import bibliothek.gui.dock.station.toolbar.layout.DockablePlaceholderToolbarGrid;
import bibliothek.gui.dock.station.toolbar.layout.PlaceholderToolbarGridConverter;
import bibliothek.gui.dock.station.toolbar.layout.ToolbarGridLayoutManager;
import bibliothek.gui.dock.station.toolbar.title.ColumnDockActionSource;
import bibliothek.gui.dock.themes.DefaultDisplayerFactoryValue;
import bibliothek.gui.dock.themes.DefaultStationPaintValue;
import bibliothek.gui.dock.themes.ThemeManager;
import bibliothek.gui.dock.themes.basic.BasicDockTitleFactory;
import bibliothek.gui.dock.title.DockTitleFactory;
import bibliothek.gui.dock.title.DockTitleVersion;
import bibliothek.gui.dock.toolbar.expand.ExpandedState;
import bibliothek.gui.dock.util.DockUtilities;
import bibliothek.gui.dock.util.PropertyKey;
import bibliothek.gui.dock.util.PropertyValue;
import bibliothek.gui.dock.util.SilentPropertyValue;
import bibliothek.gui.dock.util.extension.Extension;
import bibliothek.gui.dock.util.property.ConstantPropertyFactory;
import bibliothek.util.Path;
import bibliothek.util.Todo;
import bibliothek.util.Todo.Compatibility;
import bibliothek.util.Todo.Priority;
import bibliothek.util.Todo.Version;

/**
 * A {@link Dockable} and a {@link DockStation} which stands a group of
 * {@link ToolbarDockStation}. As <code>Dockable</code> it can be put in
 * <code>DockStation</code> which implements marker interface
 * {@link ToolbarInterface} or in {@link ScreenDockStation}, so that a
 * <code>ToolbarDockStation</code> can be floattable. As
 * <code>DockStation</code>, it accepts a {@link ToolbarElementInterface}. If
 * the element is not a <code>ToolbarElementInterface</code>, it is wrapped in a
 * <code>ToolbarDockStation</code> before to be added.
 * 
 * @author Herve Guillaume
 */
public class ToolbarGroupDockStation extends AbstractToolbarDockStation {

	/** the id of the {@link DockTitleFactory} which is used by this station */
	public static final String TITLE_ID = "toolbar.group";
	/**
	 * This id is forwarded to {@link Extension}s which load additional
	 * {@link DisplayerFactory}s
	 */
	public static final String DISPLAYER_ID = "toolbar.group";

	/** Key for the factory that creates new {@link ColumnScrollBar}s */
	public static final PropertyKey<ColumnScrollBarFactory> SCROLLBAR_FACTORY = new PropertyKey<ColumnScrollBarFactory>( "dock.scrollbarFactory", new ConstantPropertyFactory<ColumnScrollBarFactory>( SlimScrollbar.FACTORY ), true );

	/** Key for a factory that creates new {@link ToolbarGroupHeader}s */
	public static final PropertyKey<ToolbarGroupHeaderFactory> HEADER_FACTORY = new PropertyKey<ToolbarGroupHeaderFactory>( "dock.toolbarGroupHeaderFactory" );

	/** Key for a strategy for painting borders between the {@link Dockable}s */
	public static final PropertyKey<ToolbarGroupDividerStrategyFactory> DIVIDER_STRATEGY_FACTORY = new PropertyKey<ToolbarGroupDividerStrategyFactory>( "dock.toolbarGroupDividerStrategy", new ConstantPropertyFactory<ToolbarGroupDividerStrategyFactory>( DefaultToolbarGroupDividierStrategy.FACTORY ), true );
	
	/** A list of all children organized in columns and lines */
	private final DockablePlaceholderToolbarGrid<StationChildHandle> dockables = new DockablePlaceholderToolbarGrid<StationChildHandle>();

	/** Responsible for managing the {@link ExpandedState} of the children */
	private ToolbarGroupExpander expander;

	/** The {@link PlaceholderStrategy} that is used by {@link #dockables} */
	private final PropertyValue<PlaceholderStrategy> placeholderStrategy = new PropertyValue<PlaceholderStrategy>( PlaceholderStrategy.PLACEHOLDER_STRATEGY ){
		@Override
		protected void valueChanged( PlaceholderStrategy oldValue, PlaceholderStrategy newValue ){
			dockables.setStrategy( newValue );
		}
	};

	/** factory creating the current divider strategy */
	private PropertyValue<ToolbarGroupDividerStrategyFactory> dividerStrategyFactory = new PropertyValue<ToolbarGroupDividerStrategyFactory>( DIVIDER_STRATEGY_FACTORY ){
		@Override
		protected void valueChanged( ToolbarGroupDividerStrategyFactory oldValue, ToolbarGroupDividerStrategyFactory newValue ){
			if( newValue == null ){
				setDividerStrategy( null );
			}
			else{
				setDividerStrategy( newValue.create( ToolbarGroupDockStation.this ));
			}
		}
	};
	
	/** Responsible for painting a border between the {@link Dockable}s, can be <code>null</code> */
	private ToolbarGroupDividerStrategy dividerStrategy;

	/**
	 * The graphical representation of this station: the pane which contains
	 * component
	 */
	private OverpaintablePanelBase mainPanel;

	/**
	 * Size of the border outside this station where a {@link Dockable} will
	 * still be considered to be dropped onto this station. Measured in pixel.
	 */
	private int borderSideSnapSize = 10;
	/**
	 * Whether the bounds of this station are slightly bigger than the station
	 * itself. Used together with {@link #borderSideSnapSize} to grab Dockables
	 * "out of the sky". The default is <code>true</code>.
	 */
	private boolean allowSideSnap = true;

	/**
	 * The {@link LayoutManager} that is currently used to set the location and
	 * size of all children. Can be <code>null</code> if no
	 * {@link #setOrientation(Orientation) orientation} is set.
	 */
	private ToolbarGridLayoutManager<StationChildHandle> layoutManager;

	/**
	 * Information about the drop operation that is currently in progress
	 */
	private ToolbarGroupDropInfo dropInfo;

	/** the factory that creates new scrollbars */
	private PropertyValue<ColumnScrollBarFactory> scrollbarFactory = new PropertyValue<ColumnScrollBarFactory>( SCROLLBAR_FACTORY ){
		@Override
		protected void valueChanged( ColumnScrollBarFactory oldValue, ColumnScrollBarFactory newValue ){
			resetScrollbars();
		}
	};

	/** the scrollbars that are currently shown on this station */
	private Map<Integer, ColumnScrollBar> scrollbars = new HashMap<Integer, ColumnScrollBar>();

	/** this listener is added to all {@link ColumnScrollBar}s and ensures an update of positions if the bar changes its value */
	private AdjustmentListener adjustmentListener = new AdjustmentListener(){
		@Override
		public void adjustmentValueChanged( AdjustmentEvent e ){
			mainPanel.dockablePane.revalidate();
		}
	};

	public ToolbarGridLayoutManager<StationChildHandle> getLayoutManager(){
		return layoutManager;
	}

	/** the factory used to create new headers for this station */
	private PropertyValue<ToolbarGroupHeaderFactory> headerFactory = new PropertyValue<ToolbarGroupHeaderFactory>( HEADER_FACTORY ){
		@Override
		protected void valueChanged( ToolbarGroupHeaderFactory oldValue, ToolbarGroupHeaderFactory newValue ){
			ToolbarGroupHeader header = null;
			if( newValue != null ) {
				header = newValue.create( ToolbarGroupDockStation.this );
			}

			if( groupHeader != null ) {
				mainPanel.removeHeaderCopmonent( groupHeader.getComponent() );
				groupHeader.destroy();
			}
			groupHeader = header;
			if( groupHeader != null ) {
				groupHeader.setOrientation( getOrientation() );
				mainPanel.addHeaderComponent( groupHeader.getComponent() );
			}
		}
	};

	/** the current component at the top end of this station */
	private ToolbarGroupHeader groupHeader;

	// ########################################################
	// ############ Initialization Managing ###################
	// ########################################################

	/**
	 * Creates a new {@link ToolbarGroupDockStation}.
	 */
	public ToolbarGroupDockStation(){
		init();
	}

	protected void init(){
		init( ThemeManager.BACKGROUND_PAINT + ".station.toolbar.group" );
		mainPanel = new OverpaintablePanelBase();
		paint = new DefaultStationPaintValue( ThemeManager.STATION_PAINT + ".toolbar.group", this );
		setOrientation( getOrientation() );
		displayerFactory = createDisplayerFactory();
		displayers = new DisplayerCollection( this, displayerFactory, getDisplayerId() );
		displayers.addDockableDisplayerListener( new DockableDisplayerListener(){
			@Override
			public void discard( DockableDisplayer displayer ){
				ToolbarGroupDockStation.this.discard( displayer );
			}
			@Override
			public void moveableElementChanged( DockableDisplayer displayer ){
				// ignore	
			}
		} );

		setTitleIcon( null );
		expander = new ToolbarGroupExpander( this );
		setDividerStrategy( dividerStrategyFactory.getValue().create( this ) );
	}
	
	@Override
	protected DockComponentRootHandler createRootHandler() {
		return new DefaultDockStationComponentRootHandler( this, displayers );
	}

	// ########################################################
	// ################### Class Utilities ####################
	// ########################################################

	/**
	 * Gets access to a simplified view of the contents of this station.
	 * 
	 * @return a model describing all the columns that are shown on this station
	 */
	public ToolbarColumnModel<Dockable,StationChildHandle> getColumnModel(){
		return dockables.getModel();
	}

	/**
	 * Gets the column location of the <code>dockable</code>.
	 * 
	 * @param dockable
	 *            the {@link Dockable} to search
	 * @return the column location or -1 if the child was not found
	 */
	public int column( Dockable dockable ){
		return dockables.getColumn( dockable );
	}

	/**
	 * Gets the line location of the <code>dockable</code>.
	 * 
	 * @param dockable
	 *            the {@link Dockable} to search
	 * @return the line location or -1 if the child was not found
	 */
	public int line( Dockable dockable ){
		return dockables.getLine( dockable );
	}

	/**
	 * Gets the number of column of <code>this</code>.
	 * 
	 * @return the number of column
	 */
	public int columnCount(){
		return dockables.getColumnCount(); // column(getDockable(getDockableCount()
											// - 1)) + 1;
	}

	/**
	 * Gets the number of lines in <code>column</code>.
	 * 
	 * @param column
	 *            the column
	 * @return the number of lines in <code>column</code>
	 */
	public int lineCount( int column ){
		return dockables.getLineCount( column );
	}

	/**
	 * Gets the dockable at the specified <code>column</code> and
	 * <code>line</code>.
	 * 
	 * @param columnIndex
	 *            the column index
	 * @param line
	 *            the line index
	 * @return the dockable or <code>null</code> if there's no dockable at the
	 *         specified indexes.
	 */
	public Dockable getDockable( int columnIndex, int line ){
		StationChildHandle handle = getHandle( columnIndex, line );
		if( handle == null ) {
			return null;
		}
		return handle.asDockable();
	}

	/**
	 * Gets the {@link StationChildHandle} which is used at the given position.
	 * 
	 * @param columnIndex
	 *            the column in which to search
	 * @param line
	 *            the line in the column
	 * @return the item or <code>null</code> if the indices are out of bounds
	 */
	public StationChildHandle getHandle( int columnIndex, int line ){
		ToolbarColumnModel<Dockable,StationChildHandle> model = getColumnModel();
		if( columnIndex < 0 || columnIndex >= model.getColumnCount() ) {
			return null;
		}
		ToolbarColumn<Dockable,StationChildHandle> column = model.getColumn( columnIndex );
		if( line < 0 || line >= column.getDockableCount() ) {
			return null;
		}
		return column.getItem( line );
	}

	/**
	 * Gets the {@link StationChildHandle} which displays <code>dockable</code>.
	 * @param dockable the item to search
	 * @return the handle showing <code>dockable</code> or <code>null</code> if not found
	 */
	public StationChildHandle getHandle( Dockable dockable ){
		ToolbarColumn<Dockable,StationChildHandle> column = getColumnModel().getColumn( dockable );
		if( column == null ) {
			return null;
		}
		for( int i = 0, n = column.getDockableCount(); i < n; i++ ) {
			StationChildHandle handle = column.getItem( i );
			if( handle.getDockable() == dockable ) {
				return handle;
			}
		}
		return null;
	}

	/**
	 * Tells if <code>dockable</code> is the last dockable in its column.
	 * 
	 * @param dockable
	 *            the dockable
	 * @return true if the dockable it's the last in its column, false if it's
	 *         not or if it doesn't belong to <code>this</code> dockstation.
	 */
	public boolean isLastOfColumn( Dockable dockable ){
		int index = indexOf( dockable );
		int column = column( dockable );
		if( index == (getDockableCount() - 1) ) {
			return true;
		}
		else if( column( getDockable( index + 1 ) ) != column ) {
			return true;
		}
		else {
			return false;
		}
	}

	// ########################################################
	// ############ General DockStation Managing ##############
	// ########################################################

	/**
	 * Gets the {@link ToolbarStrategy} that is currently used by this station.
	 * 
	 * @return the strategy, never <code>null</code>
	 */
	@Override
	public ToolbarStrategy getToolbarStrategy(){
		DockController controller = getController();
		DockStation parent = getDockParent();
		while( controller == null && parent != null ){
			controller = parent.getController();
			Dockable parentDockable = parent.asDockable();
			if( parentDockable == null ){
				parent = null;
			}
			else{
				parent = parentDockable.getDockParent();
			}
		}
		
		final SilentPropertyValue<ToolbarStrategy> value = new SilentPropertyValue<ToolbarStrategy>( ToolbarStrategy.STRATEGY, controller );
		final ToolbarStrategy result = value.getValue();
		value.setProperties( (DockController) null );
		return result;
	}

	@Override
	public Component getComponent(){
		return mainPanel;
	}

	@Override
	public int getDockableCount(){
		return dockables.size();
	}

	@Override
	public Dockable getDockable( int index ){
		return dockables.get( index ).asDockable();
	}

	@Override
	public String getFactoryID(){
		return ToolbarGroupDockStationFactory.ID;
	}

	/**
	 * Gets a unique identifier used to get the {@link DisplayerFactory} for this station.
	 * @return the unique identifier, not <code>null</code>
	 */
	protected String getDisplayerId(){
		return DISPLAYER_ID;
	}

	/**
	 * Sets whether {@link Dockable Dockables} which are dragged near the
	 * station are captured and added to this station.
	 * 
	 * @param allowSideSnap
	 *            <code>true</code> if the station can snap Dockables which are
	 *            near.
	 * @see #setBorderSideSnapSize(int)
	 */
	public void setAllowSideSnap( boolean allowSideSnap ){
		this.allowSideSnap = allowSideSnap;
	}

	/**
	 * Tells whether the station can grab Dockables which are dragged near the
	 * station.
	 * 
	 * @return <code>true</code> if grabbing is allowed
	 * @see #setAllowSideSnap(boolean)
	 */
	public boolean isAllowSideSnap(){
		return allowSideSnap;
	}

	/**
	 * There is an invisible border around the station. If a {@link Dockable} is
	 * dragged inside this border, its considered to be on the station and will
	 * be dropped into.
	 * 
	 * @param borderSideSnapSize
	 *            the size of the border in pixel
	 * @throws IllegalArgumentException
	 *             if the size is smaller than 0
	 */
	public void setBorderSideSnapSize( int borderSideSnapSize ){
		if( borderSideSnapSize < 0 ) {
			throw new IllegalArgumentException( "borderSideSnapeSize must not be less than 0" );
		}

		this.borderSideSnapSize = borderSideSnapSize;
	}

	/**
	 * Gets the size of the invisible border around the station where a dockable
	 * can be dropped.
	 * 
	 * @return the size in pixel
	 * @see #setBorderSideSnapSize(int)
	 */
	public int getBorderSideSnapSize(){
		return borderSideSnapSize;
	}

	@Override
	public void setController( DockController controller ){
		if( getController() != controller ) {
			if( getController() != null ) {
				dockables.unbind();
			}
			Iterator<StationChildHandle> iter = dockables.items();
			while( iter.hasNext() ) {
				iter.next().setTitleRequest( null );
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
			expander.setController( controller );
			placeholderStrategy.setProperties( controller );
			displayerFactory.setController( controller );
			displayers.setController( controller );
			mainPanel.setController( controller );
			layoutManager.setController( controller );
			scrollbarFactory.setProperties( controller );
			headerFactory.setProperties( controller );
			dividerStrategyFactory.setProperties( controller );

			if( getController() != null ) {
				dockables.bind();
			}

			iter = dockables.items();
			while( iter.hasNext() ) {
				iter.next().setTitleRequest( title, true );
			}
		}
	}

	// ########################################################
	// ############ Orientation Managing ######################
	// ########################################################

	@Override
	public void setOrientation( Orientation orientation ){
		if( orientation == null ) {
			throw new IllegalArgumentException( "orientation must not be null" );
		}

		// it's very important to change position and orientation of inside
		// dockables first, else doLayout() is done on wrong inside information
		this.orientation = orientation;
		fireOrientingEvent();
		for( ColumnScrollBar scrollbar : scrollbars.values() ) {
			scrollbar.setOrientation( orientation );
		}
		if( groupHeader != null ) {
			groupHeader.setOrientation( orientation );
			mainPanel.removeHeaderCopmonent( groupHeader.getComponent() );
			mainPanel.addHeaderComponent( groupHeader.getComponent() );
		}
		mainPanel.updateAlignment();
		mainPanel.revalidate();
	}

	// ########################################################
	// ############### Drop/Move Managing #####################
	// ########################################################

	@Override
	public DockStationDropLayer[] getLayers(){
		return new DockStationDropLayer[]{
				new ToolbarGroupInnerLayer( this, mainPanel.dockablePane ), 
				new ToolbarGroupOuterLayer( this, mainPanel.dockablePane ) };
	}

	@Override
	public boolean accept( Dockable child ){
		return getToolbarStrategy().isToolbarGroupPart( child );
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
			// check if controller exists and if the controller accepts that
			// the dockable becomes a child of this station
			if( controller != null ) {
				if( !controller.getAcceptance().accept( this, dockable ) ) {
					return null;
				}
			}

			Point mouse = new Point( item.getMouseX(), item.getMouseY() );
			SwingUtilities.convertPointFromScreen( mouse, mainPanel.dockablePane );

			int column = getColumnAt( mouse );
			int line = -1;
			if( column >= 0 && column < columnCount() ) {
				Rectangle columnBounds = layoutManager.getBounds( column );

				if( getOrientation() == Orientation.VERTICAL ) {
					int x = columnBounds.x;
					int width = columnBounds.width;

					if( x + width / 5 <= mouse.x && mouse.x <= x + width * 4 / 5 ) {
						line = layoutManager.getInsertionLineAt( column, mouse.y );
					}
					else if( mouse.x >= x + width * 4 / 5 ) {
						column++;
					}
				}
				else {
					int y = columnBounds.y;
					int height = columnBounds.height;
					if( y + height / 5 <= mouse.y && mouse.y <= y + height * 4 / 5 ) {
						line = layoutManager.getInsertionLineAt( column, mouse.x );
					}
					else if( mouse.y >= y + height * 4 / 5 ) {
						column++;
					}
				}
			}
			if( column == -1 ) {
				column = 0;
			}
			
			boolean effect = true;
			if( dockable.getDockParent() == this ){
				int currentColumn = column( dockable );
				int currentLine = line( dockable );
				effect = currentColumn != column || (currentLine != line && currentLine != line-1 );
				effect = effect && !(line == -1 && lineCount( currentColumn ) == 1 && (currentColumn == column || currentColumn == column-1));
			}

			return new ToolbarGroupDropInfo( dockable, ToolbarGroupDockStation.this, column, line, effect ){
				@Override
				public void execute(){
					dropInfo = null;
					drop( this );
				}

				// Note: draw() is called first by the Controller. It seems
				// destroy() is called after, after a new StationDropOperation
				// is created

				@Override
				public void destroy( StationDropOperation next ){
					if( next == null || next.getTarget() != getTarget() ) {
						layoutManager.mutate();
					}
					dropInfo = null;
					mainPanel.repaint();
				}

				@Override
				public void draw(){
					dropInfo = this;
					int column = getColumn();
					int line = getLine();
					
					if( hasEffect() ){
						layoutManager.mutate( column, line );
					}
					else{
						layoutManager.mutate();
					}
					mainPanel.repaint();
				}
			};
		}
		else {
			return null;
		}
	}

	/**
	 * Tells which column covers the point <code>location</code>. If <code>location</code> is outside
	 * this station, then a non-existing column <code>-1</code> or {@link #columnCount()} is returned.
	 * @param location some point on this {@link Component}
	 * @return the column covering <code>location</code>, can be <code>-1</code> or {@link #columnCount()}
	 */
	public int getColumnAt( Point location ){
		int pos;
		int max;
		if( getOrientation() == Orientation.VERTICAL ) {
			pos = location.x;
			max = mainPanel.getWidth();
		}
		else {
			pos = location.y;
			max = mainPanel.getHeight();
		}

		if( pos < 0 ) {
			return -1;
		}
		if( pos >= max ) {
			return columnCount();
		}

		return layoutManager.getColumnAt( pos );
	}

	/**
	 * Drops thanks to information collect by dropInfo.
	 * 
	 * @param dropInfoGroup
	 */
	protected void drop( ToolbarGroupDropInfo dropInfoGroup ){
		int line = dropInfoGroup.getLine();
		if( line == -1 ) {
			drop( dropInfoGroup.getItem(), dropInfoGroup.getColumn() );
		}
		else {
			drop( dropInfoGroup.getItem(), dropInfoGroup.getColumn(), line );
		}
	}

	@Override
	public void drop( Dockable dockable ){
		drop( dockable, 0, 0 );
	}

	/**
	 * Drops the <code>dockable</code> at the specified line and column.
	 * 
	 * @param dockable
	 *            the dockable to insert
	 * @param column
	 *            the column where insert
	 * @param line
	 *            the line where insert
	 * @return true if the dockable has been inserted, false otherwise
	 */
	public boolean drop( Dockable dockable, int column, int line ){
		return drop( dockable, column, line, false );
	}

	public boolean drop( Dockable dockable, int column, int line, boolean force ){
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
			add( dockable, column, line );
			return true;
		}
		return false;
	}

	private void add( Dockable dockable, int column, int line ){
		DockUtilities.ensureTreeValidity( this, dockable );
		DockUtilities.checkLayoutLocked();
		Dockable replacement = getToolbarStrategy().ensureToolbarLayer( this, dockable );
		if( replacement != dockable ) {
			replacement.asDockStation().drop( dockable );
			dockable = replacement;
		}

		final DockHierarchyLock.Token token = DockHierarchyLock.acquireLinking( this, dockable );
		try {
			listeners.fireDockableAdding( dockable );

			dockable.setDockParent( this );
			final StationChildHandle handle = createHandle( dockable );
			// add in the list of dockable
			final int before = dockables.getColumnCount();
			dockables.insert( column, line, handle );
			// add in the main panel
			addComponent( handle );
			listeners.fireDockableAdded( dockable );
			fireDockablesRepositioned( dockable, before != dockables.getColumnCount() );
		}
		finally {
			token.release();
		}
	}

	/**
	 * Creates a new {@link StationChildHandle} that wraps around
	 * <code>dockable</code>. This method does not add the handle to any list or
	 * fire any events, that is the callers responsibility. Callers should also
	 * call {@link #addComponent(StationChildHandle)} with the new handle.
	 * @param dockable the element that is to be wrapped
	 * @return a new {@link StationChildHandle} for the element
	 */
	private StationChildHandle createHandle( Dockable dockable ){
		final StationChildHandle handle = new StationChildHandle( this, displayers, dockable, title );
		handle.updateDisplayer();
		return handle;
	}

	/**
	 * Adds <code>handle</code> to the {@link #mainPanel} of this station. Note
	 * that this method only cares about the {@link Component}-{@link Container}
	 * relationship, it does not store <code>handle</code> in the
	 * {@link #dockables} list.
	 * @param handle the handle to add
	 */
	private void addComponent( StationChildHandle handle ){
		mainPanel.dockablePane.add( handle.getDisplayer().getComponent() );
		mainPanel.getContentPane().revalidate();
	}

	/**
	 * Removes <code>handle</code> of the {@link #mainPanel} of this station.
	 * Note that this method only cares about the {@link Component}-
	 * {@link Container} relationship, it does not remove <code>handle</code> of
	 * the {@link #dockables} list.
	 * 
	 * @param handle
	 *            the handle to remove
	 */
	private void removeComponent( StationChildHandle handle ){
		mainPanel.dockablePane.remove( handle.getDisplayer().getComponent() );
		mainPanel.getContentPane().revalidate();
	}

	/**
	 * Drops the <code>dockable</code> in a new column.
	 * 
	 * @param dockable
	 *            the dockable to insert
	 * @param column
	 *            the column index to create
	 * @return true if the dockable has been inserted, false otherwise
	 */
	public boolean drop( Dockable dockable, int column ){
		return drop( dockable, column, false );
	}

	public boolean drop( Dockable dockable, int column, boolean force ){
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
			add( dockable, column );
			return true;
		}
		return false;
	}

	private void add( Dockable dockable, int column ){
		DockUtilities.ensureTreeValidity( this, dockable );
		DockUtilities.checkLayoutLocked();
		Dockable replacement = getToolbarStrategy().ensureToolbarLayer( this, dockable );
		if( replacement != dockable ) {
			replacement.asDockStation().drop( dockable );
			dockable = replacement;
		}
		final DockHierarchyLock.Token token = DockHierarchyLock.acquireLinking( this, dockable );
		try {
			listeners.fireDockableAdding( dockable );
			dockable.setDockParent( this );
			final StationChildHandle handle = createHandle( dockable );
			// add in the list of dockable
			final int before = dockables.getColumnCount();
			dockables.insert( column, handle, false );
			// add in the main panel
			addComponent( handle );
			listeners.fireDockableAdded( dockable );
			fireDockablesRepositioned( dockable, before != dockables.getColumnCount() );
		}
		finally {
			token.release();
		}
	}

	@Override
	public void drag( Dockable dockable ){
		if( dockable.getDockParent() != this ) {
			throw new IllegalArgumentException( "not a child of this station: " + dockable );
		}
		remove( dockable );
	}

	@Override
	protected void remove( Dockable dockable ){
		DockUtilities.checkLayoutLocked();
		final int column = column( dockable );
		final int before = dockables.getColumnCount();

		final StationChildHandle handle = dockables.get( dockable );
		final DockHierarchyLock.Token token = DockHierarchyLock.acquireUnlinking( this, dockable );
		try {
			listeners.fireDockableRemoving( dockable );

			dockables.remove( handle );
			removeComponent( handle );
			handle.destroy();
			dockable.setDockParent( null );

			listeners.fireDockableRemoved( dockable );
			fireColumnRepositioned( column, before != dockables.getColumnCount() );
			mainPanel.repaint(); // if we don't call repaint, the station is
			// not repaint when we remove a ToolbarDockStation in a column and
			// that
			// it doesn't affect the number of column
		}
		finally {
			token.release();
		}
	}

	@Override
	public void replace( Dockable old, Dockable next ){
		// TODO Auto-generated method stub
		DockUtilities.checkLayoutLocked();
		final DockController controller = getController();
		if( controller != null ) {
			controller.freezeLayout();
		}
		final int column = dockables.getColumn( old );
		final int line = dockables.getLine( old );
		final int beforeCount = dockables.getColumnCount();
		remove( old );
		// if remove the old dockable delete a column we have to recreate it
		if( beforeCount != dockables.getColumnCount() ) {
			add( next, column );
		}
		else {
			add( next, column, line );
		}
		controller.meltLayout();
	}

	@Override
	public boolean canReplace( Dockable old, Dockable next ){
		return acceptable( next ) && getToolbarStrategy().isToolbarGroupPartParent( this, next, true );
	}

	/**
	 * Fires
	 * {@link DockStationListener#dockablesRepositioned(DockStation, Dockable[])}
	 * for all {@link Dockable}s that are in the same column as
	 * <code>dockable</code>, including <code>dockable</code>.
	 * 
	 * @param dockable
	 *            some item from a column that changed
	 * @param all
	 *            whether there should be an event for all the columns after
	 *            <code>dockable</code> as well
	 */
	protected void fireDockablesRepositioned( Dockable dockable ){
		fireDockablesRepositioned( dockable, false );
	}

	/**
	 * Fires
	 * {@link DockStationListener#dockablesRepositioned(DockStation, Dockable[])}
	 * for all {@link Dockable}s that are in the same column as
	 * <code>dockable</code>, including <code>dockable</code>.
	 * 
	 * @param dockable
	 *            some item from a column that changed
	 * @param all
	 *            whether there should be an event for all the columns after the
	 *            column of the dockable as well
	 */
	protected void fireDockablesRepositioned( Dockable dockable, boolean all ){
		fireColumnRepositioned( column( dockable ), all );
	}

	/**
	 * Fires
	 * {@link DockStationListener#dockablesRepositioned(DockStation, Dockable[])}
	 * for all {@link Dockable}s in the given <code>column</code>.
	 * 
	 * @param column
	 *            the column for which to fire an event
	 * @param all
	 *            whether there should be an event for all the columns after
	 *            <code>column</code> as well
	 */
	protected void fireColumnRepositioned( int column, boolean all ){
		final List<Dockable> list = new ArrayList<Dockable>();
		final int end = all ? dockables.getColumnCount() : column + 1;

		for( int i = column; i < end; i++ ) {
			final Iterator<StationChildHandle> items = dockables.getColumnContent( i );
			while( items.hasNext() ) {
				list.add( items.next().getDockable() );
			}
		}

		if( list.size() > 0 ) {
			listeners.fireDockablesRepositioned( list.toArray( new Dockable[list.size()] ) );
		}
	}

	// ########################################################
	// ###################### UI Managing #####################
	// ########################################################

	/**
	 * Gets a {@link ColumnDockActionSource} which allows to modify the
	 * {@link ExpandedState} of the children of this station.
	 * 
	 * @return the source, not <code>null</code>
	 */
	public ColumnDockActionSource getExpandActionSource(){
		return expander.getActions();
	}

	/**
	 * Gets the factory for the {@link #getHeaderComponent() header component} which is currently
	 * in use.
	 * @return the current factory, can be <code>null</code>
	 * @see #HEADER_FACTORY
	 */
	public ToolbarGroupHeaderFactory getHeaderComponentFactory(){
		return headerFactory.getValue();
	}

	/**
	 * Sets the factory for the {@link #getHeaderComponent() header component}. A value of <code>null</code>
	 * will be interpreted as a request to reinstall the default factory.
	 * @param factory the new factory, can be <code>null</code> in order to reinstall the default factory
	 * @see #HEADER_FACTORY
	 */
	public void setHeaderComponentFactory( ToolbarGroupHeaderFactory factory ){
		headerFactory.setValue( factory );
	}

	/**
	 * Gets the {@link Component} which is currently shown at the top of this station.
	 * @return the component or <code>null</code>
	 * @see #HEADER_FACTORY
	 */
	public ToolbarGroupHeader getHeaderComponent(){
		return groupHeader;
	}

	@Override
	protected void callDockUiUpdateTheme() throws IOException{
		DockUI.updateTheme( this, new ToolbarGroupDockStationFactory() );
	}

	@Override
	protected DefaultDisplayerFactoryValue createDisplayerFactory(){
		return new DefaultDisplayerFactoryValue( ThemeManager.DISPLAYER_FACTORY + ".toolbar.group", this );
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

		final StationChildHandle handle = dockables.get( dockable );
		if( handle == null ) {
			throw new IllegalArgumentException( "displayer is not child of this station: " + displayer );
		}

		removeComponent( handle );
		handle.updateDisplayer();
		addComponent( handle );
	}

	private void resetScrollbars(){
		for( Map.Entry<Integer, ColumnScrollBar> entry : scrollbars.entrySet() ) {
			ColumnScrollBar replacement = scrollbarFactory.getValue().create( this );
			mainPanel.dockablePane.remove( entry.getValue().getComponent() );
			entry.setValue( replacement );
			mainPanel.dockablePane.add( entry.getValue().getComponent() );
		}
	}

	private void setDividerStrategy( ToolbarGroupDividerStrategy dividerStrategy ){
		this.dividerStrategy = dividerStrategy;
		layoutManager.setDividerStrategy( dividerStrategy );
		mainPanel.revalidate();
	}
	
	public Rectangle getDropGapBoundaries(){
		if( dropInfo == null ){
			return null;
		}
		
		Component dockablePane = mainPanel.dockablePane;
		Point zero = new Point( 0, 0 );
		zero = SwingUtilities.convertPoint( dockablePane, zero, mainPanel );

		Rectangle gapBounds;

		if( dropInfo.getLine() == -1 ) {
			gapBounds = layoutManager.getGapBounds( dropInfo.getColumn(), false );
		}
		else {
			gapBounds = layoutManager.getGapBounds( dropInfo.getColumn(), dropInfo.getLine() );
		}

		gapBounds.x += zero.x;
		gapBounds.y += zero.y;
	
		return gapBounds;
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
		 * The direct parent {@link Container} of {@link Dockable}s.
		 */
		private final JPanel dockablePane = new JPanel(){
			protected void paintComponent( Graphics g ){
				super.paintComponent( g );
				if( dividerStrategy != null ){
					dividerStrategy.paint( this, g, layoutManager );
				}
			}
		};

		/**
		 * The parent of {@link #dockablePane}, adds insets, borders and other
		 * decorations.
		 */
		private JPanel decorationPane = createBackgroundPanel();

		/**
		 * Creates a new panel
		 */
		public OverpaintablePanelBase(){
			setBasePane( decorationPane );
			setSolid( false );
			decorationPane.setOpaque( false );
			dockablePane.setOpaque( false );

			decorationPane.setLayout( new BorderLayout() );
			decorationPane.add( dockablePane, BorderLayout.CENTER );
		}

		public void addHeaderComponent( Component header ){
			if( getOrientation() == Orientation.HORIZONTAL ) {
				decorationPane.add( header, BorderLayout.WEST );
			}
			else {
				decorationPane.add( header, BorderLayout.NORTH );
			}
		}

		public void removeHeaderCopmonent( Component header ){
			decorationPane.remove( header );
		}

		@Override
		public Dimension getPreferredSize(){
			// this is a workaround because the boundaries of the children are required to
			// compute the preferred size of this components. It is not pretty...
			if( dockablePane != null && !dockablePane.isValid() ){
				dockablePane.doLayout();
			}
			
			return getBasePane().getPreferredSize();
		}

		@Override
		public Dimension getMinimumSize(){
			return getBasePane().getPreferredSize();
		}

		@Override
		public Dimension getMaximumSize(){
			return getBasePane().getPreferredSize();
		}

		/**
		 * Update alignment with regards to the current orientation of this
		 * {@link ToolbarGroupDockStation}
		 */
		public void updateAlignment(){
			final Orientation orientation = getOrientation();
			if( orientation != null ) {
				if( layoutManager != null ) {
					layoutManager.setController( null );
				}

				layoutManager = new ToolbarGridLayoutManager<StationChildHandle>( dockablePane, orientation, dockables, ToolbarGroupDockStation.this ){
					@Override
					protected Component toComponent( StationChildHandle item ){
						return item.getDisplayer().getComponent();
					}

					@Override
					protected void setShowScrollbar( int column, boolean show ){
						boolean change = false;

						if( show ) {
							if( !scrollbars.containsKey( column ) ) {
								ColumnScrollBar bar = scrollbarFactory.getValue().create( ToolbarGroupDockStation.this );
								bar.setOrientation( getOrientation() );
								scrollbars.put( column, bar );
								dockablePane.add( bar.getComponent() );
								bar.addAdjustmentListener( adjustmentListener );
								change = true;
							}
						}
						else {
							ColumnScrollBar bar = scrollbars.remove( column );
							if( bar != null ) {
								dockablePane.remove( bar.getComponent() );
								bar.removeAdjustmentListener( adjustmentListener );
								change = true;
							}
						}
						if( change ) {
							revalidateLater();
						}
					}

					@Override
					protected int getScrollbarValue( int column, int required, int available ){
						ColumnScrollBar scrollbar = scrollbars.get( column );
						if( scrollbar == null ) {
							return 0;
						}
						scrollbar.setValues( required, available );
						return scrollbar.getValue();
					}

					@Override
					protected Component getScrollbar( int column ){
						ColumnScrollBar scrollbar = scrollbars.get( column );
						if( scrollbar == null ) {
							return null;
						}
						return scrollbar.getComponent();
					}

				};
				dockablePane.setLayout( layoutManager );
				layoutManager.setDividerStrategy( dividerStrategy );
				layoutManager.setController( getController() );
			}
			mainPanel.revalidate();
		}

		/**
		 * Asynchronously revalidates {@link #decorationPane}, but only if its size did not change
		 */
		private void revalidateLater(){
			if( orientation == Orientation.VERTICAL ) {
				final int height = decorationPane.getHeight();
				EventQueue.invokeLater( new Runnable(){
					@Override
					public void run(){
						if( height == decorationPane.getHeight() ) {
							decorationPane.revalidate();
						}
					}
				} );
			}
			else {
				final int width = decorationPane.getWidth();
				EventQueue.invokeLater( new Runnable(){
					@Override
					public void run(){
						if( width == decorationPane.getWidth() ) {
							decorationPane.revalidate();
						}
					}
				} );
			}
		}

		@Override
		protected void paintOverlay( Graphics g ){
			final Graphics2D g2D = (Graphics2D) g;

			paintDrag( g );

			if( dropInfo != null && dropInfo.hasEffect() ) {
				Point zero = new Point( 0, 0 );
				zero = SwingUtilities.convertPoint( dockablePane, zero, this );
				
				Rectangle gapBounds = getDropGapBoundaries();
				paint.drawInsertion( g2D, new Rectangle( zero.x, zero.y, dockablePane.getWidth(), dockablePane.getHeight() ), gapBounds );
			}
		}

		private void paintDrag( Graphics g ){
			Dockable removal = getRemoval();
			if( removal != null ) {
				StationChildHandle handle = getHandle( removal );
				if( handle != null ) {
					Rectangle bounds = handle.getDisplayer().getComponent().getBounds();

					Component dockablePane = mainPanel.dockablePane;
					Point zero = new Point( 0, 0 );
					zero = SwingUtilities.convertPoint( dockablePane, zero, this );
					bounds.x += zero.x;
					bounds.y += zero.y;

					getPaint().drawRemoval( g, bounds, bounds );
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

	@Override
	public PlaceholderMap getPlaceholders(){
		PlaceholderMap map = dockables.toMap();
		writeLayoutArguments( map );
		return map;
	}

	/**
	 * Converts this station into a {@link PlaceholderMap} using
	 * <code>identifiers</code> to remember which {@link Dockable} was at which
	 * location.
	 * 
	 * @param identifiers
	 *            the identifiers to apply
	 * @return <code>this</code> as map
	 */
	public PlaceholderMap getPlaceholders( Map<Dockable, Integer> identifiers ){
		PlaceholderMap map = dockables.toMap( identifiers );
		writeLayoutArguments( map );
		return map;
	}

	@Override
	public PlaceholderMapping getPlaceholderMapping() {
		return new ToolbarGroupPlaceholderMapping( this, dockables );
	}
	
	@Override
	public void setPlaceholders( PlaceholderMap placeholders ){
		dockables.fromMap( placeholders );
		readLayoutArguments( placeholders );
	}

	public void setPlaceholders( PlaceholderMap placeholders, Map<Integer, Dockable> children ){
		DockUtilities.checkLayoutLocked();
		if( getDockableCount() > 0 ) {
			throw new IllegalStateException( "this station still has children" );
		}

		final DockController controller = getController();
		readLayoutArguments( placeholders );

		try {
			if( controller != null ) {
				controller.freezeLayout();
			}

			dockables.setStrategy( null );
			dockables.unbind();

			dockables.fromMap( placeholders, children, new PlaceholderToolbarGridConverter<Dockable, StationChildHandle>(){
				@Override
				public StationChildHandle convert( Dockable dockable, ConvertedPlaceholderListItem item ){
					listeners.fireDockableAdding( dockable );

					dockable.setDockParent( ToolbarGroupDockStation.this );
					final StationChildHandle handle = createHandle( dockable );
					addComponent( handle );

					return handle;
				}

				@Override
				public void added( StationChildHandle item ){
					listeners.fireDockableAdded( item.getDockable() );
				}
			} );

			if( controller != null ) {
				dockables.bind();
			}
			dockables.setStrategy( placeholderStrategy.getValue() );
		}
		finally {
			if( controller != null ) {
				controller.meltLayout();
			}
		}
	}

	private void writeLayoutArguments( PlaceholderMap map ){
		ToolbarGroupDockStationLayout.writeOrientation( map, getOrientation() );
	}

	private void readLayoutArguments( PlaceholderMap map ){
		Orientation orientation = ToolbarGroupDockStationLayout.readOrientation( map );
		if( orientation != null ){
			setOrientation( orientation );
		}
	}

	@Override
	public DockableProperty getDockableProperty( Dockable child, Dockable target ){
		final int column = column( child );
		final int line = line( child );

		if( target == null ) {
			target = child;
		}

		final PlaceholderStrategy strategy = placeholderStrategy.getValue();
		Path placeholder = null;
		if( strategy != null ) {
			placeholder = strategy.getPlaceholderFor( target );
			if( (placeholder != null) && (column >= 0) && (line >= 0) ) {
				dockables.addPlaceholder( column, line, placeholder );
			}
		}

		return new ToolbarGroupProperty( column, line, placeholder );
	}
	
	@Override
	@Todo( compatibility=Compatibility.COMPATIBLE, priority=Priority.ENHANCEMENT, target=Version.VERSION_1_1_2,
		description="Implement this feature")
	public void aside( AsideRequest request ){
		DockableProperty location = request.getLocation();
		int column = -1;
		int line = -1;
		Path newPlaceholder = request.getPlaceholder();
		
		if( location instanceof ToolbarGroupProperty ){
			ToolbarGroupProperty groupLocation = (ToolbarGroupProperty)location;
			
			Path placeholder = groupLocation.getPlaceholder();
			if( placeholder != null ){
				column = dockables.getColumn( placeholder );
			}
			if( column == -1 ){
				placeholder = null;
				column = groupLocation.getColumn();
			}
			
			int totalColumnCount = dockables.getTotalColumnCount();
			column = Math.max( 0, Math.min( column, totalColumnCount ) );
			
			if( placeholder != null && column < totalColumnCount ){
				line = dockables.getLine( column, placeholder );
			}
			else if( column == totalColumnCount ){
				line = 0;
			}
			
			if( line == -1 ){
				line = groupLocation.getLine();
				line = Math.max( 0, Math.min( line, dockables.getLineCount( column ) ) );
			}
			
			if( groupLocation.getSuccessor() == null ){
				// insert
				if( newPlaceholder != null ){
					dockables.insertPlaceholder( column, line, newPlaceholder );
				}
			}
			else{
				// add to existing location
				if( newPlaceholder != null ){
					dockables.addPlaceholder( column, line, newPlaceholder );
					Dockable existing = dockables.getModel().getColumn( column ).getDockable( line );
					if( existing.asDockStation() != null ){
						AsideAnswer answer = request.forward( existing.asDockStation() );
						if( answer.isCanceled() ){
							return;
						}
					}
				}
			}
		}
		else {
			column = 0;
			if( dockables.getColumnCount() > 0 ){
				line = dockables.getLineCount( column );
			}
			else{
				line = 0;
			}
		}
		
		request.answer( new ToolbarGroupProperty( column, line, newPlaceholder ) );
	}

	@Override
	public boolean drop( Dockable dockable, DockableProperty property ){
		if( property instanceof ToolbarGroupProperty ) {
			return drop( dockable, (ToolbarGroupProperty) property );
		}
		return false;
	}

	/**
	 * Tries to drop <code>dockable</code> at <code>property</code>.
	 * 
	 * @param dockable
	 *            the element to drop
	 * @param property
	 *            the location of <code>dockable</code>
	 * @return <code>true</code> if dropping was successful, <code>false</code>
	 *         otherwise
	 */
	public boolean drop( Dockable dockable, ToolbarGroupProperty property ){
		final Path placeholder = property.getPlaceholder();

		int column = property.getColumn();
		int line = property.getLine();

		if( placeholder != null ) {
			if( dockables.hasPlaceholder( placeholder ) ) {
				final StationChildHandle child = dockables.get( placeholder );
				if( child == null ) {
					if( acceptable( dockable ) ) {
						Dockable replacement = getToolbarStrategy().ensureToolbarLayer( this, dockable );
						if( replacement == null ) {
							return false;
						}

						DockController controller = getController();
						if( controller != null ) {
							controller.freezeLayout();
						}
						try {
							dropAt( replacement, placeholder );

							if( replacement != dockable ) {
								if( property.getSuccessor() != null ) {
									if( !replacement.asDockStation().drop( dockable, property.getSuccessor() ) ) {
										replacement.asDockStation().drop( dockable );
									}
								}
								else {
									replacement.asDockStation().drop( dockable );
								}
							}
						}
						finally {
							if( controller != null ) {
								controller.meltLayout();
							}
						}

						return true;
					}
				}
				else {
					if( drop( child, dockable, property ) ) {
						return true;
					}

					column = dockables.getColumn( child.getDockable() );
					line = dockables.getLine( column, child.getDockable() ) + 1;
				}
			}
		}
		else {
			if( column >= 0 && column < columnCount() ) {
				if( line >= 0 && line < lineCount( column ) ) {
					DockStation child = getDockable( column, line ).asDockStation();
					if( child != null && child.drop( dockable, property.getSuccessor() ) ) {
						return true;
					}
				}
			}
		}

		if( !acceptable( dockable ) ) {
			return false;
		}

		line = Math.max( 0, line );
		// column = Math.max( 0, column );
		return drop( dockable, column, line );
	}

	/**
	 * Drops <code>dockable</code> at the location described by
	 * <code>placeholder</code>. This method must only be called if
	 * <code>placeholder</code> points to a valid location.
	 * 
	 * @param dockable
	 *            the dockable to drop
	 * @param placeholder
	 *            the location of <code>dockable</code>, must be valid
	 */
	private void dropAt( Dockable dockable, Path placeholder ){

		DockUtilities.checkLayoutLocked();
		final DockHierarchyLock.Token token = DockHierarchyLock.acquireLinking( this, dockable );
		try {
			DockUtilities.ensureTreeValidity( this, dockable );
			listeners.fireDockableAdding( dockable );
			final int before = dockables.getColumnCount();

			dockable.setDockParent( this );
			final StationChildHandle handle = createHandle( dockable );
			dockables.put( placeholder, handle );
			addComponent( handle );

			listeners.fireDockableAdded( dockable );
			fireDockablesRepositioned( dockable, before != dockables.getColumnCount() );
		}
		finally {
			token.release();
		}
	}

	@SuppressWarnings("static-method")
	private boolean drop( StationChildHandle parent, Dockable child, ToolbarGroupProperty property ){
		if( property.getSuccessor() == null ) {
			return false;
		}

		final DockStation station = parent.getDockable().asDockStation();
		if( station == null ) {
			return false;
		}

		return station.drop( child, property.getSuccessor() );
	}

	@Override
	public void move( Dockable dockable, DockableProperty property ){
		if( property instanceof ToolbarGroupProperty ) {
			move( dockable, (ToolbarGroupProperty) property );
		}
	}

	private void move( Dockable dockable, ToolbarGroupProperty property ){
		final int sourceColumn = column( dockable );
		final int sourceLine = line( dockable );

		boolean empty = false;
		int destinationColumn = property.getColumn();
		int destinationLine = property.getLine();

		final Path placeholder = property.getPlaceholder();
		if( placeholder != null ) {
			final int column = dockables.getColumn( placeholder );
			if( column != -1 ) {
				final int line = dockables.getLine( column, placeholder );
				if( line != -1 ) {
					empty = true;
					destinationColumn = column;
					destinationLine = line;
				}
			}
		}

		if( !empty ) {
			// ensure destination valid
			destinationColumn = Math.min( destinationColumn, dockables.getColumnCount() );
			if( (destinationColumn == dockables.getColumnCount()) || (destinationColumn == -1) ) {
				destinationLine = 0;
			}
			else {
				destinationLine = Math.min( destinationLine, dockables.getLineCount( destinationColumn ) );
			}
		}

		Level level;
		if( empty ) {
			level = Level.BASE;
		}
		else {
			level = Level.DOCKABLE;
		}
		dockables.move( sourceColumn, sourceLine, destinationColumn, destinationLine, level );
		mainPanel.getContentPane().revalidate();
	}
}
