package bibliothek.gui.dock;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.Point;
import java.awt.Rectangle;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Map;

import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.EtchedBorder;

import bibliothek.gui.DockController;
import bibliothek.gui.DockStation;
import bibliothek.gui.Dockable;
import bibliothek.gui.OrientedDockStation;
import bibliothek.gui.OrientedDockStation.Orientation;
import bibliothek.gui.ToolbarElementInterface;
import bibliothek.gui.ToolbarInterface;
import bibliothek.gui.dock.layout.DockableProperty;
import bibliothek.gui.dock.station.AbstractDockableStation;
import bibliothek.gui.dock.station.OverpaintablePanel;
import bibliothek.gui.dock.station.StationDropOperation;
import bibliothek.gui.dock.station.StationPaint;
import bibliothek.gui.dock.station.support.DockablePlaceholderList;
import bibliothek.gui.dock.station.support.PlaceholderMap;
import bibliothek.gui.dock.station.support.PlaceholderStrategy;
import bibliothek.gui.dock.station.toolbar.DefaultToolbarContainerConverter;
import bibliothek.gui.dock.station.toolbar.Position;
import bibliothek.gui.dock.station.toolbar.ToolbarContainerConverter;
import bibliothek.gui.dock.station.toolbar.ToolbarContainerConverterCallback;
import bibliothek.gui.dock.station.toolbar.ToolbarContainerDockStationFactory;
import bibliothek.gui.dock.station.toolbar.ToolbarContainerDropInfo;
import bibliothek.gui.dock.station.toolbar.ToolbarContainerProperty;
import bibliothek.gui.dock.station.toolbar.ToolbarStrategy;
import bibliothek.gui.dock.themes.DefaultStationPaintValue;
import bibliothek.gui.dock.themes.ThemeManager;
import bibliothek.gui.dock.util.DockUtilities;
import bibliothek.gui.dock.util.PropertyValue;
import bibliothek.gui.dock.util.SilentPropertyValue;

/**
 * A {@link Dockable} and a {@link Dockstation} which stands a group of
 * {@link ToolbarDockStation}. As dockable it can be put in every
 * {@link DockStation}. As DockStation it has four sides and one central area.
 * The four sides can received multiple {@link ToolbarElementInterface}. The
 * central area can received one Dockable if it's not a
 * {@link ToolbarElementInterface} . When ToolbarElement are added to one side,
 * all the ComponentDockable extracted from the element are merged together and
 * wrapped in a {@link ToolbarDockstation} before to be added.
 * 
 * @author Herve Guillaume
 */
public class ToolbarContainerDockStation extends AbstractDockableStation implements ToolbarInterface {

	/** The westPane */
	private JPanel westPanel;
	/** The east pane */
	private JPanel eastPanel;
	/** The north pane */
	private JPanel northPanel;
	/** The south Pane */
	private JPanel southPanel;
	/** The center Pane */
	private JPanel centerPanel = new JPanel( new GridLayout( 1, 1 ) );
	/**
	 * The graphical representation of this station: the pane which contains
	 * toolbars
	 */
	protected OverpaintablePanelBase basePanel = new OverpaintablePanelBase();
	/** dockables associate with the west pane */
	private DockablePlaceholderList<Dockable> westDockables = new DockablePlaceholderList<Dockable>();
	/** dockables associate with the east pane */
	private DockablePlaceholderList<Dockable> eastDockables = new DockablePlaceholderList<Dockable>();
	/** dockables associate with the north pane */
	private DockablePlaceholderList<Dockable> northDockables = new DockablePlaceholderList<Dockable>();
	/** dockables associate with the south pane */
	private DockablePlaceholderList<Dockable> southDockables = new DockablePlaceholderList<Dockable>();
	/**
	 * all dockables contain in this dockstation (north, south, west, east and
	 * center)
	 */
	private ArrayList<Dockable> allDockables = new ArrayList<Dockable>();
	
	/**
	 * Dockable associate with the center pane. While this is a list, in reality there is never more than one item
	 * in it. The list is only used for convenience, as it greatly reduces the amount of coded needed to handle
	 * placeholders.
	 */
	private DockablePlaceholderList<Dockable> centerDockable = new DockablePlaceholderList<Dockable>();
	
	/** A paint to draw lines */
	private DefaultStationPaintValue paint;
	/** the index of the closest dockable above the mouse */
	private Integer indexBeneathMouse = null;
	/** closest side of the the closest dockable above the mouse */
	private Position sideAboveMouse = null;
	/** the area beneath the mouse, where the dockabel will be dragged */
	private Position areaBeneathMouse = null;

	/** current {@link PlaceholderStrategy} */
	private PropertyValue<PlaceholderStrategy> placeholderStrategy = new PropertyValue<PlaceholderStrategy>( PlaceholderStrategy.PLACEHOLDER_STRATEGY ){
		@Override
		protected void valueChanged( PlaceholderStrategy oldValue, PlaceholderStrategy newValue ){
			westDockables.setStrategy( newValue );
			eastDockables.setStrategy( newValue );
			southDockables.setStrategy( newValue );
			northDockables.setStrategy( newValue );
			centerDockable.setStrategy( newValue );
		}
	};

	/**
	 * Constructs a new ToolbarContainerDockStation
	 */
	public ToolbarContainerDockStation(){
		basePanel = new OverpaintablePanelBase();
		paint = new DefaultStationPaintValue( ThemeManager.STATION_PAINT + ".toolbar", this );
		// Grid Layout allow component pout in the panels to
		// fill all the space
	}

	/**
	 * Create a side pane for the side areas of this dock station
	 */
	private JPanel createSidePanel(){
		JPanel panel = new JPanel();
		GridLayout layout = new GridLayout( 1, 1 );
		layout.setHgap( 10 );
		layout.setVgap( 10 );
		panel.setLayout( layout );
		panel.setBorder( new CompoundBorder( new EtchedBorder(), new EmptyBorder( new Insets( 5, 5, 5, 5 ) ) ) );
		// panel.setBackground(new Color(31, 73, 125));
		return panel;
	}

	@Override
	public int getDockableCount(){
		return allDockables.size();
	}

	@Override
	public Dockable getDockable( int index ){
		return allDockables.get( index );
	}

	@Override
	public Dockable getFrontDockable(){
		// there's no child which is more important than another
		return null;
	}

	@Override
	public void setFrontDockable( Dockable dockable ){
		// there's no child which is more important than another
	}

	@Override
	public PlaceholderMap getPlaceholders(){
		return createConverter().getPlaceholders( this );
	}

	/**
	 * Gets the layout of this station encoded as {@link PlaceholderMap}.
	 * @param children identifiers for the children
	 * @return the encoded layout, not <code>null</code>
	 */
	public PlaceholderMap getPlaceholders( Map<Dockable, Integer> children ){
		return createConverter().getPlaceholders( this, children );
	}

	@Override
	public void setPlaceholders( PlaceholderMap placeholders ){
		createConverter().setPlaceholders( this, placeholders );
	}

	/**
	 * Sets the layout of this station using the encoded layout from <code>placeholders</code>
	 * @param placeholders the placeholders to read
	 * @param children the children to add
	 */
	public void setPlaceholders( PlaceholderMap placeholders, Map<Integer, Dockable> children ){
		createConverter().setPlaceholders( this, new ToolbarContainerConverterCallback(){
			final int[] index = new int[ Position.values().length ]; 
			
			@Override
			public void adding( Position area, Dockable dockable ){
				listeners.fireDockableAdding( dockable );
			}

			@Override
			public void added( Position area, Dockable dockable ){
				if( area == Position.CENTER ){
					centerPanel.add( dockable.getComponent() );
				}
				else{
					insertAt( area, dockable, index[ area.ordinal() ]++ );
				}
				
				dockable.setDockParent( ToolbarContainerDockStation.this );
				listeners.fireDockableAdded( dockable );
			}

			@Override
			public void setDockables( Position area, DockablePlaceholderList<Dockable> list ){
				ToolbarContainerDockStation.this.setDockables( area, list, false );
			}
			
			public void finished( DockablePlaceholderList<Dockable> list ){
				if( getController() != null ){
					list.bind();
					list.setStrategy( getPlaceholderStrategy() );
				}
			}
			
		}, placeholders, children );
	}

	/**
	 * Exchanges the list of dockables on the side <code>position</code>.
	 * @param position the position whose list gets reloaded
	 * @param map the encoded list
	 * @throws IllegalStateException if there are any children on this station
	 */
	public void setPlaceholders( Position position, PlaceholderMap map ){
		if( getDockableCount() > 0 ){
			throw new IllegalStateException( "there are children on this station" );
		}
		try{
			DockablePlaceholderList<Dockable> next = new DockablePlaceholderList<Dockable>( map );
			setDockables( position, next, true );
		}
		catch( IllegalArgumentException ex ){
			// silent;
		}
	}
	
	/**
	 * Creates a {@link ToolbarContainerConverter} which will be used for one
	 * call.
	 * @return the new converter, not <code>null</code>
	 */
	protected ToolbarContainerConverter createConverter(){
		return new DefaultToolbarContainerConverter();
	}

	/**
	 * Gets the {@link PlaceholderStrategy} that is currently in use.
	 * @return the current strategy, may be <code>null</code>
	 */
	public PlaceholderStrategy getPlaceholderStrategy(){
		return placeholderStrategy.getValue();
	}

	/**
	 * Sets the {@link PlaceholderStrategy} to use, <code>null</code> will set
	 * the default strategy.
	 * @param strategy the new strategy, can be <code>null</code>
	 */
	public void setPlaceholderStrategy( PlaceholderStrategy strategy ){
		placeholderStrategy.setValue( strategy );
	}

	@Override
	public DockableProperty getDockableProperty( Dockable child, Dockable target ){
		for( Position position : Position.values() ) {
			int index = indexOf( position, child );
			if( index >= 0 ) {
				return new ToolbarContainerProperty( index, position, null );
			}
		}

		return new ToolbarContainerProperty( 0, Position.CENTER, null );
	}

	public StationDropOperation prepareDrop( int mouseX, int mouseY, int titleX, int titleY, boolean checkOverrideZone, Dockable dockable ){
		System.out.println( this.toString() + "## prepareDrop(...) ##" );
		DockController controller = getController();
		// check whether this station has to check if the mouse is in the
		// override-zone of its parent & (if this parent exist) if
		// the mouse is in the override-zone
		if( checkOverrideZone & this.getDockParent() != null ) {
			if( this.getDockParent().isInOverrideZone( mouseX, mouseY, this, dockable ) ) {
				return null;
			}
		}
		// check if the dockable and the station accept each other
		if( this.accept( dockable ) & dockable.accept( this ) ) {
			// check if controller exist and if the controller accept that
			// the dockable become a child of this station
			if( controller != null ) {
				if( !controller.getAcceptance().accept( this, dockable ) ) {
					return null;
				}
			}
			DockablePlaceholderList<Dockable> associateToolbars;
			Position area;
			Point mousePoint = new Point( mouseX, mouseY );
			SwingUtilities.convertPointFromScreen( mousePoint, basePanel.getContentPane() );

			System.out.println( "########### MOUSE: " + mouseX + "/" + mouseY + " ################" );
			if( westPanel.getBounds().contains( mousePoint ) ) {
				associateToolbars = westDockables;
				area = Position.WEST;
			}
			else if( eastPanel.getBounds().contains( mousePoint ) ) {
				associateToolbars = eastDockables;
				area = Position.EAST;
			}
			else if( northPanel.getBounds().contains( mousePoint ) ) {
				associateToolbars = northDockables;
				area = Position.NORTH;
			}
			else if( southPanel.getBounds().contains( mousePoint ) ) {
				associateToolbars = southDockables;
				area = Position.SOUTH;
			}
			else {
				// user can't drag and drop an element in the center area
				// the user can only add programmaticaly a dockable in the
				// center area
				return null;
			}
			if( !getToolbarStrategy().isToolbarPart( dockable ) ) {
				// only ToolbarElementInterface can be drop or move in
				// the side areas
				return null;
			}
			System.out.println( "########### AREA: " + area + " ################" );
			return new ToolbarContainerDropInfo( dockable, this, associateToolbars, area, mouseX, mouseY ){
				@Override
				public void execute(){
					drop( this );
				}

				// Note: draw() is called first by the Controller. It seems
				// destroy() is called after, after a new StationDropOperation
				// is created

				@Override
				public void destroy(){
					// without this line, nothing is displayed except if you
					// drag another component
					ToolbarContainerDockStation.this.indexBeneathMouse = null;
					ToolbarContainerDockStation.this.sideAboveMouse = null;
					ToolbarContainerDockStation.this.basePanel.getContentPane().repaint();
				}

				@Override
				public void draw(){
					// without this line, nothing is displayed
					// Reminder: if dockable beneath mouse doesn't exist, then indexOf return -1
					ToolbarContainerDockStation.this.indexBeneathMouse = indexOf( getArea(), getDockableBeneathMouse() );
					ToolbarContainerDockStation.this.sideAboveMouse = this.getSideDockableBeneathMouse();
					ToolbarContainerDockStation.this.areaBeneathMouse = this.getArea();
					// without this line, line is displayed only on the first
					// component met
					ToolbarContainerDockStation.this.basePanel.getContentPane().repaint();
				}
			};

		}
		else {
			return null;
		}
	}

	private void drop( ToolbarContainerDropInfo dropInfo ){
		// Note: Computation of index to insert drag dockable is not the
		// same between a move() and a drop(), because with a move() it is
		// as if the drag dockable were remove first then added again in the
		// list (Note: It's wird beacause indeed drag() is called after
		// move()...)
		// We consider move() occur if :
		// - the dockable added come from this station and stay on the same
		// area
		// We consider drop() occur if :
		// - the dockable added come from another station OR come from this
		// station but come from another area of this station

		// we check if there's dockable in the area where the dockable will be
		// drop
		Position area = dropInfo.getArea();
		if( this.getDockables( area ).dockables().size() == 0 ) {
			// in this case, it's inevitably a drop() action
			drop( dropInfo.getItem(), 0, area );
		}
		// if the dockable has to be drop at the same place (center with regards
		// to itself): nothing to be done
		if( dropInfo.getItemPositionVSBeneathDockable() != Position.CENTER ) {
			int indexBeneathMouse = indexOf( area, dropInfo.getDockableBeneathMouse() );
			int dropIndex;
			boolean isMove = false;
			if( dropInfo.isMove() ) {
				if( dropInfo.getArea() == this.getArea( dropInfo.getItem() ) ) {
					isMove = true;
				}
			}

			if( isMove ) {
				switch( this.getOrientation( area ) ){
					case VERTICAL:
						if( dropInfo.getItemPositionVSBeneathDockable() == Position.SOUTH ) {
							if( dropInfo.getSideDockableBeneathMouse() == Position.SOUTH ) {
								dropIndex = indexBeneathMouse + 1;
							}
							else {
								dropIndex = indexBeneathMouse;
							}
						}
						else {
							if( dropInfo.getSideDockableBeneathMouse() == Position.SOUTH ) {
								dropIndex = indexBeneathMouse;
							}
							else {
								dropIndex = indexBeneathMouse - 1;
							}
						}
						move( dropInfo.getItem(), dropIndex, area );
						break;
					case HORIZONTAL:
						System.out.println( "PositionDragVSBeneathDockable: " + dropInfo.getItemPositionVSBeneathDockable() );
						if( dropInfo.getItemPositionVSBeneathDockable() == Position.EAST ) {
							System.out.println( "IndexBeneathMouse: " + indexBeneathMouse );
							System.out.println( "SideDockableBeneathMouse: " + dropInfo.getSideDockableBeneathMouse() );
							if( dropInfo.getSideDockableBeneathMouse() == Position.EAST ) {
								dropIndex = indexBeneathMouse + 1;
							}
							else {
								dropIndex = indexBeneathMouse;
							}
							System.out.println( "DropIndex: " + dropIndex );
						}
						else {
							if( dropInfo.getSideDockableBeneathMouse() == Position.EAST ) {
								dropIndex = indexBeneathMouse;
							}
							else {
								dropIndex = indexBeneathMouse - 1;
							}
						}
						move( dropInfo.getItem(), dropIndex, area );
						break;
				}

			}
			else {
				int increment = 0;
				if( dropInfo.getSideDockableBeneathMouse() == Position.SOUTH || dropInfo.getSideDockableBeneathMouse() == Position.EAST ) {
					increment++;
				}
				dropIndex = indexBeneathMouse + increment;
				drop( dropInfo.getItem(), dropIndex, area );

			}
		}

	}

	/**
	 * Adds <code>dockable</code> to this station in the center area if the
	 * dockable is not a {@link ToolbarStrategy#isToolbarPart(Dockable)}, else the
	 * dockable is added at the north side.
	 * 
	 * @param dockable
	 *            a new child
	 */
	@Override
	public void drop( Dockable dockable ){
		System.out.println( this.toString() + "## drop(Dockable dockable)##" );
		if( getToolbarStrategy().isToolbarPart( dockable ) ) {
			this.drop( dockable, getDockables( Position.NORTH ).dockables().size(), Position.NORTH );
		}
		else{
			this.drop( dockable, -1, Position.CENTER );
		}
	}

	@Override
	public boolean drop( Dockable dockable, DockableProperty property ){
		if( property instanceof ToolbarContainerProperty ) {
			ToolbarContainerProperty toolbar = (ToolbarContainerProperty) property;

			if( toolbar.getSuccessor() != null ) {
				Dockable preset = null;

				if( toolbar.getArea() == Position.CENTER ) {
					preset = getCenterDockable();
				}
				else {
					DockablePlaceholderList<Dockable> list = getDockables( toolbar.getArea() );

					if( toolbar.getIndex() < list.dockables().size() ) {
						preset = list.dockables().get( toolbar.getIndex() );
					}
				}

				if( preset != null && preset.asDockStation() != null ) {
					return preset.asDockStation().drop( dockable, property.getSuccessor() );
				}
			}

			if( toolbar.getArea() == Position.CENTER ) {
				return drop( dockable, Position.CENTER );
			}
			else {
				int max = getDockables( toolbar.getArea() ).dockables().size();
				return drop( dockable, Math.min( max, toolbar.getIndex() ), toolbar.getArea() );
			}
		}
		return false;
	}

	/**
	 * Adds <code>dockable</code> to this station in the given area. The
	 * dockable in the center area mustn't be a {@link ToolbarElementInterface},
	 * and the dockable in one of the side areas must be a
	 * {@link ToolbarElementInterface} : if not, do nothing. The dockable is
	 * added at the last position in the specified area
	 * 
	 * @param dockable
	 *            a new child
	 * @param area
	 *            Refer to the position of area
	 * @return <code>true</code> if dropping was successfull
	 */
	public boolean drop( Dockable dockable, Position area ){
		System.out.println( this.toString() + "## drop(Dockable dockable, Position area)##" );
		return this.drop( dockable, getDockables( area ).dockables().size(), area );
	}

	/**
	 * Inserts <code>dockable</code> to this station at the given area and the
	 * given index. The dockable in the center area mustn't be a
	 * {@link ToolbarElementInterface}, and the dockable in on of the side areas
	 * must be a {@link ToolbarElementInterface} : if not, do nothing.
	 * 
	 * @param dockable
	 *            a new child
	 * @param area
	 *            Refer to the position of area
	 * @return <code>true</code> if dropping was successfull
	 */
	private boolean drop( Dockable dockable, int index, Position area ){
		System.out.println( this.toString() + "## drop(Dockable dockable, int index, Position area)##" );
		// where the dockable whre drop (WEST, EAST, etc.)?
		return this.add( dockable, index, area );
	}

	private void move( Dockable dockable, int indexWhereInsert, Position area ){
		System.out.println( this.toString() + "## move() ##" );
		System.out.println( area );
		switch( area ){
			case CENTER:
				// center area accept only one child, not a ToolbarELementInterface,
				// and this child has to be added programmatically and not by user
				// drag and drop. So the only move which can happen is to move the
				// child in the center in the... center. Conclusion: nothing to be
				// done. is move
				break;
			case NORTH:
			case SOUTH:
			case WEST:
			case EAST:
				if( getToolbarStrategy().isToolbarPart( dockable ) ) {
					DockController controller = getController();
					try {
						System.out.println( "Index move: " + indexWhereInsert );

						if( controller != null ) {
							controller.freezeLayout();
						}
						this.add( dockable, indexWhereInsert, area );
					}
					finally {
						if( controller != null ) {
							controller.meltLayout();
						}
					}
				}
				break;
			default:
				throw new NullPointerException();
		}
	}

	@Override
	public void move( Dockable dockable, DockableProperty property ){
		// TODO LATER Auto-generated method stub
		System.out.println( this.toString() + "## move(Dockable dockable, DockableProperty property) ## " + this.toString() );
	}

	@Override
	public <D extends Dockable & DockStation> boolean isInOverrideZone( int x, int y, D invoker, Dockable drop ){
		return false;
	}

	@Override
	public boolean canDrag( Dockable dockable ){
		System.out.println( this.toString() + "## canDrag(Dockable dockable) ## " + this.toString() );
		return true;
	}

	@Override
	public void drag( Dockable dockable ){
		System.out.println( this.toString() + "## drag(Dockable dockable) ##" );
		if( dockable.getDockParent() != this )
			throw new IllegalArgumentException( "The dockable cannot be dragged, it is not child of this station." );
		this.remove( dockable );
	}

	@Override
	public boolean canReplace( Dockable old, Dockable next ){
		System.out.println( this.toString() + "## canReplace(Dockable old, Dockable next) ## " + this.toString() );
		if( old.getClass() == next.getClass() ) {
			return true;
		}
		else {
			return false;
		}
	}

	@Override
	public void replace( Dockable old, Dockable next ){
		System.out.println( this.toString() + "## replace(Dockable old, Dockable next) ## " + this.toString() );
		System.out.println( "	" + old.toString() + " / " + next.toString() );
		DockUtilities.checkLayoutLocked();
		DockController controller = getController();
		if( controller != null )
			controller.freezeLayout();
		Position area = getArea( old );
		int index = areaIndexOf( (Dockable) old );
		this.remove( old );
		// the child is a TollbarGroupDockStation because canReplace()
		// ensure it
		add( (ToolbarGroupDockStation) next, index, area );
		controller.meltLayout();
	}

	@Override
	public void replace( DockStation old, Dockable next ){
		System.out.println( this.toString() + "## replace(DockStation old, Dockable next) ## " + this.toString() );
		replace( old.asDockable(), next );
	}

	@Override
	public String getFactoryID(){
		return ToolbarContainerDockStationFactory.ID;
	}

	@Override
	public Component getComponent(){
		return basePanel;
	}

	@Override
	protected void callDockUiUpdateTheme() throws IOException{
		// TODO Auto-generated method stub
	}

	/**
	 * Gets the {@link ToolbarStrategy} that is currently used by this station.
	 * 
	 * @return the strategy, never <code>null</code>
	 */
	public ToolbarStrategy getToolbarStrategy(){
		SilentPropertyValue<ToolbarStrategy> value = new SilentPropertyValue<ToolbarStrategy>( ToolbarStrategy.STRATEGY, getController() );
		ToolbarStrategy result = value.getValue();
		value.setProperties( (DockController) null );
		return result;
	}

	@Override
	public boolean accept( Dockable child ){
		System.out.println( this.toString() + "## accept(Dockable child) ##" );
		return true;
	}

	@Override
	public boolean accept( DockStation station ){
		System.out.println( this.toString() + "## accept(DockStation station) ##" );
		// we can put this dockstation everywhere
		return true;
	}

	@Override
	public String toString(){
		return this.getClass().getSimpleName() + '@' + Integer.toHexString( this.hashCode() );
	}
	
	/**
	 * Gets the {@link Dockable} that is shown in the center.
	 * @return the center dockable, can be <code>null</code>
	 */
	public Dockable getCenterDockable(){
		DockablePlaceholderList.Filter<Dockable> filter = centerDockable.dockables();
		if( filter.size() == 0 ){
			return null;
		}
		else{
			return filter.get( 0 );
		}
	}
	
	/**
	 * Updates the {@link #centerDockable} such that the list contains only <code>dockable</code>
	 * or no element if <code>dockable</code> is <code>null</code>.<br>
	 * This method does not fire any events nor update the ui in any way, it only update the list {@link #centerDockable}.
	 * @param dockable the new center dockable, can be <code>null</code>
	 */
	private void setCenterDockable( Dockable dockable ){
		DockablePlaceholderList.Filter<Dockable> filter = centerDockable.dockables();
		if( dockable == null ){
			if( filter.size() > 0 ){
				filter.remove( 0 );
			}
		}
		else{
			if( filter.size() >= 1 ){
				filter.set( 0, dockable );
			}
			else{
				filter.add( dockable );
			}
		}
	}

	/**
	 * Updates one of the lists containing dockables.
	 * @param area the list to exchange
	 * @param list the new list
	 * @param bind whether the new list should be {@link DockablePlaceholderList#bind() bound}
	 */
	private void setDockables( Position area, DockablePlaceholderList<Dockable> list, boolean bind ){
		if( getController() != null ){
			DockablePlaceholderList<Dockable> oldList = getDockables( area );
			oldList.setStrategy( null );
			oldList.unbind();
		}
		
		switch( area ){
			case EAST:
				eastDockables = list;
				break;
			case WEST:
				westDockables = list;
				break;
			case NORTH:
				northDockables = list;
				break;
			case SOUTH:
				southDockables = list;
				break;
			case CENTER:
				centerDockable = list;
				break;
			default:
				throw new IllegalArgumentException( "invalid area: " + area );
		}
		
		if( getController() != null && bind ){
			list.bind();
			list.setStrategy( getPlaceholderStrategy() );
		}
	}
	
	/**
	 * 
	 * 
	 * @param area
	 * @return the dockables associated in the specified area
	 */
	public DockablePlaceholderList<Dockable> getDockables( Position area ){
		switch( area ){
			case CENTER: return centerDockable;
			case EAST: return eastDockables;
			case WEST: return westDockables;
			case NORTH: return northDockables;
			case SOUTH: return southDockables;
			default: throw new IllegalArgumentException("unknown area: " + area);
		}
	}

	/**
	 * Gets the panel associate with the specified area
	 * 
	 * @param area
	 *            of the panel
	 * @return the panel at the specified area
	 */
	public JPanel getPanel( Position area ){
		switch( area ){
			case NORTH:
				return this.northPanel;
			case SOUTH:
				return this.southPanel;
			case WEST:
				return this.westPanel;
			case EAST:
				return this.eastPanel;
			case CENTER:
				return this.centerPanel;
			default:
				return null;
		}
	}

	/**
	 * Gets the area of a dockable
	 * 
	 * @param dockable
	 *            the child which is searched
	 * @return the area of a dockable or null if it was not found
	 */
	public Position getArea( Dockable dockable ){
		for( Position position : Position.values() ) {
			if( indexOf( position, dockable ) >= 0 ) {
				return position;
			}
		}
		return null;
	}

	/**
	 * Gets the orientation of dockables in one area at the specified area
	 * 
	 * @param area
	 *            refer to the area at the given position
	 * @return the orientation
	 */
	public Orientation getOrientation( Position area ){
		if( area == Position.WEST || area == Position.EAST ) {
			return Orientation.VERTICAL;
		}
		else {
			return Orientation.HORIZONTAL;
		}
	}

	/**
	 * Searches for <code>child</code> in the list of children at the 
	 * side <code>position</code>.
	 * @param position the side at which to search
	 * @param child the child to search
	 * @return the index of <code>child</code> or -1 if not found
	 */
	private int indexOf( Position position, Dockable child ){
		DockablePlaceholderList<Dockable> list = null;
		switch( position ){
			case EAST:
				list = eastDockables;
				break;
			case WEST:
				list = westDockables;
				break;
			case NORTH:
				list = northDockables;
				break;
			case SOUTH:
				list = southDockables;
				break;
		}
		if( list == null && position == Position.CENTER ) {
			if( centerDockable == child ) {
				return 0;
			}
			else {
				return -1;
			}
		}

		int index = 0;
		for( Dockable handle : list.dockables() ) {
			if( handle == child ) {
				return index;
			}
			index++;
		}

		return -1;
	}

	/**
	 * Gets the global index of a child.
	 * 
	 * @param dockable
	 *            the child which is searched
	 * @return the index of <code>dockable</code> or -1 if it was not found
	 */
	private int globalIndexOf( Dockable dockable ){
		int index = 0;
		for( Dockable d : allDockables ) {
			if( d == dockable ) {
				return index;
			}
			index++;
		}
		return -1;
	}

	/**
	 * Give, for a dockable, his index compared to the other dockables in the
	 * same area (NORTH, SOUTH, WEST, EAST, CENTER)
	 * 
	 * @param dockable
	 * @return
	 */
	private int areaIndexOf( Dockable dockable ){
		Position area = getArea( dockable );
		if( area == null ) {
			return -1;
		}
		return indexOf( area, dockable );
	}

	/**
	 * Removes the child with the given <code>index</code> from this station.<br>
	 * Note: clients may need to invoke {@link DockController#freezeLayout()}
	 * and {@link DockController#meltLayout()} to ensure noone else adds or
	 * removes <code>Dockable</code>s.
	 * 
	 * @param index
	 *            the index of the child that will be removed
	 */
	private void remove( Dockable dockable ){
		DockUtilities.checkLayoutLocked();
		Position area = getArea( dockable );
		DockHierarchyLock.Token token = DockHierarchyLock.acquireUnlinking( this, dockable );
		try {
			int globalIndex = globalIndexOf( dockable );
			switch( area ){
				case CENTER:
					listeners.fireDockableRemoving( dockable );
					dockable.setDockParent( null );
					centerDockable = null;
					allDockables.remove( globalIndex );
					centerPanel.remove( dockable.getComponent() );
					centerPanel.revalidate();
					centerPanel.repaint();
					listeners.fireDockableRemoved( dockable );
					fireDockablesRepositioned( globalIndex );
					break;
				case NORTH:
				case SOUTH:
				case WEST:
				case EAST:
					JPanel panel = getPanel( area );
					DockablePlaceholderList.Filter<Dockable> dockables = getDockables( area ).dockables();
					listeners.fireDockableRemoving( dockable );
					dockable.setDockParent( null );
					dockables.remove( dockable );
					allDockables.remove( globalIndex );
					panel.remove( dockable.getComponent() );
					panel.revalidate();
					centerPanel.repaint();
					listeners.fireDockableRemoved( dockable );
					fireDockablesRepositioned( globalIndex );
					break;
				default:
					throw new NullPointerException();
			}
		}
		finally {
			token.release();
		}
	}

	/**
	 * Update the list off all dockables. Used when one of the lists of
	 * dockables associate with one area is updated
	 */
	public void updateDockables(){
		allDockables.clear();
		addAll( westDockables );
		addAll( northDockables );
		addAll( eastDockables );
		addAll( southDockables );
		addAll( centerDockable );
	}

	private void addAll( DockablePlaceholderList<Dockable> list ){
		for( Dockable dockable : list.dockables() ) {
			allDockables.add( dockable );
		}
	}

	/**
	 * Add one dockable in the area and the index position. if area is null,
	 * then the dockable is added on the center area. The dockable can be a
	 * {@link ComponentDockable}, {@link ToolbarGroupDockStation} or a
	 * {@link ToolbarDockStation} (see method accept()). All the
	 * ComponentDockable extracted from the element are merged together and
	 * wrapped in a {@link ToolbarGroupDockStation} before to be added at index
	 * position
	 * 
	 * @param dockable
	 *            Dockable to add
	 * @param index
	 *            Index where add dockable
	 * @param area
	 *            area where insert dokckable
	 * @return <code>true</code> if dropping was successfull
	 */
	protected boolean add( Dockable dockable, int index, Position area ){
		DockUtilities.ensureTreeValidity( this, dockable );
		DockUtilities.checkLayoutLocked();
		// DockHierarchyLock.Token token =
		// DockHierarchyLock.acquireLinking(this,
		// dockable);

		ToolbarStrategy strategy = getToolbarStrategy();

		switch( area ){
			case CENTER:
				if( !strategy.isToolbarPart( dockable ) ) {
					DockHierarchyLock.Token token = DockHierarchyLock.acquireLinking( this, dockable );
					try {
						if( getCenterDockable() != null ) {
							remove( dockable );
						}
						dockable.setDockParent( this );
						listeners.fireDockableAdding( dockable );
						centerPanel.add( dockable.getComponent(), index );
						setCenterDockable( dockable );
						updateDockables();
						centerPanel.revalidate();
						centerPanel.repaint();
						listeners.fireDockableAdded( dockable );
						fireDockablesRepositioned( index + 1 );
						System.out.println( "IN CENTER" );
					}
					finally {
						token.release();
					}
					return true;
				}
				break;
			case NORTH:
			case SOUTH:
			case WEST:
			case EAST:
				if( strategy.isToolbarPart( dockable ) ) {
					dockable = strategy.ensureToolbarLayer( this, dockable );

					DockHierarchyLock.Token token = DockHierarchyLock.acquireLinking( this, dockable );
					try {
						dockable.setDockParent( this );
						listeners.fireDockableAdding( dockable );
						DockablePlaceholderList.Filter<Dockable> dockables = getDockables( area ).dockables();
						
						System.out.println( "########### INDEX: " + index + " ################" );
						dockables.add( index, dockable );
						
						insertAt( area, dockable, index );
						
						////////////
						listeners.fireDockableAdded( dockable );
						fireDockablesRepositioned( index + 1 );
					}
					finally {
						token.release();
					}
					return true;
				}
				break;
			default:
				throw new IllegalStateException( "Unknown area: " + area );
		}

		return false;
	}
	
	private void insertAt( Position area, Dockable dockable, int index ){
		JPanel panel = getPanel( area );
		if( dockable instanceof OrientedDockStation ) {
			((OrientedDockStation) dockable).setOrientation( getOrientation( area ) );
		}
		
		dockable.setDockParent( this );
		updateDockables();
		///////////
		panel.add( dockable.getComponent(), index );
		basePanel.getContentPane().setBounds( 0, 0, basePanel.getContentPane().getPreferredSize().width,
				basePanel.getContentPane().getPreferredSize().height );
		basePanel.setPreferredSize( new Dimension( basePanel.getContentPane().getPreferredSize().width, basePanel.getContentPane()
				.getPreferredSize().height ) );
		basePanel.getContentPane().revalidate();
		basePanel.getContentPane().repaint();
	}
	
	/**
	 * This panel is used as base of the station. All children of the station
	 * have this panel as parent too. It allows to draw arbitrary figures over
	 * the base panel
	 * 
	 * @author Herve Guillaume
	 */
	protected class OverpaintablePanelBase extends OverpaintablePanel {
		/**
		* The content Pane of this {@link OverpaintablePanel}
		*/
		private JPanel content = new JPanel( new BorderLayout() );

		/**
		 * Creates a new panel
		 */
		public OverpaintablePanelBase(){
			westPanel = createSidePanel();
			eastPanel = createSidePanel();
			northPanel = createSidePanel();
			southPanel = createSidePanel();
			content.add( westPanel, BorderLayout.WEST );
			content.add( eastPanel, BorderLayout.EAST );
			content.add( northPanel, BorderLayout.NORTH );
			content.add( southPanel, BorderLayout.SOUTH );
			content.add( centerPanel, BorderLayout.CENTER );
			content.setBounds( 0, 0, content.getPreferredSize().width, content.getPreferredSize().height );
			this.getContentPane().setBounds( 0, 0, this.getContentPane().getPreferredSize().width, this.getContentPane().getPreferredSize().height );
			this.setPreferredSize( new Dimension( this.getContentPane().getPreferredSize().width, this.getContentPane().getPreferredSize().height ) );
			setContentPane( content );
			setBasePane( content );
		}

		@Override
		protected void paintOverlay( Graphics g ){
			DefaultStationPaintValue paint = getPaint();
			if( indexBeneathMouse != null ) {
				if( indexBeneathMouse == -1 ) {
					// it means there's no dockable beneath mouse
					Rectangle rect = getPanel( areaBeneathMouse ).getBounds();
					paint.drawDivider( g, rect );
				}
				else {
					Rectangle rect = getDockables( areaBeneathMouse ).dockables().get( indexBeneathMouse ).getComponent().getBounds();
					if( rect != null ) {
						switch( ToolbarContainerDockStation.this.getOrientation( areaBeneathMouse ) ){
							case VERTICAL:
								int y;
								if( sideAboveMouse == Position.NORTH ) {
									y = rect.y;
								}
								else {
									y = rect.y + rect.height;
								}
								paint.drawInsertionLine( g, rect.x, y, rect.x + rect.width, y );
								break;
							case HORIZONTAL:
								int x;
								if( sideAboveMouse == Position.WEST ) {
									x = rect.x;
								}
								else {
									x = rect.x + rect.width;
								}
								paint.drawInsertionLine( g, x, rect.y, x, rect.y + rect.height );
								break;
						}
					}
				}
			}
		}

		@Override
		public String toString(){
			return this.getClass().getSimpleName() + '@' + Integer.toHexString( this.hashCode() );
		}

	}

	/**
	 * Gets a {@link StationPaint} which is used to paint some lines onto this
	 * station. Use a {@link DefaultStationPaintValue#setDelegate(StationPaint)
	 * delegate} to exchange the paint.
	 * 
	 * @return the paint
	 */
	public DefaultStationPaintValue getPaint(){
		return paint;
	}

	@Override
	public void setController( DockController controller ){
		if( getController() != controller ) {
			if( getController() != null ) {
				westDockables.unbind();
				eastDockables.unbind();
				southDockables.unbind();
				northDockables.unbind();
				centerDockable.unbind();
			}

			super.setController( controller );
			paint.setController( controller );

			placeholderStrategy.setProperties( controller );

			if( controller != null ) {
				westDockables.bind();
				eastDockables.bind();
				southDockables.bind();
				northDockables.bind();
				centerDockable.bind();
			}
		}
	}

}
