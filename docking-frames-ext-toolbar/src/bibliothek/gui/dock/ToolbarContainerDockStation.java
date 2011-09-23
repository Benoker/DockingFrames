package bibliothek.gui.dock;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.Point;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.EtchedBorder;

import bibliothek.gui.DockController;
import bibliothek.gui.DockStation;
import bibliothek.gui.Dockable;
import bibliothek.gui.OrientedDockStation.Orientation;
import bibliothek.gui.ToolbarElementInterface;
import bibliothek.gui.ToolbarInterface;
import bibliothek.gui.dock.layout.DockableProperty;
import bibliothek.gui.dock.station.AbstractDockableStation;
import bibliothek.gui.dock.station.StationDropOperation;
import bibliothek.gui.dock.station.support.PlaceholderMap;
import bibliothek.gui.dock.station.toolbar.ReferencePoint;
import bibliothek.gui.dock.station.toolbar.ToolbarContainerDropInfo;
import bibliothek.gui.dock.station.toolbar.ToolbarContainerProperty;
import bibliothek.gui.dock.util.DockUtilities;

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

	/**
	 * The area position
	 * 
	 * @author Herv� Guillaume
	 * 
	 */
	public enum Position {
		NORTH, SOUTH, EAST, WEST, CENTER
	}

	ScreenDockStation screenStation;

	/**
	 * The graphical representation of this station: the pane which contains
	 * toolbars
	 */
	private JPanel borderPanel;
	/** The westPane */
	private JPanel westPanel = new JPanel();
	/** The east pane */
	private JPanel eastPanel = new JPanel();
	/** The north pane */
	private JPanel northPanel = new JPanel();
	/** The south Pane */
	private JPanel southPanel = new JPanel();
	/** The center Pane */
	private JPanel centerPanel = new JPanel();
	/** dockables associate with the west pane */
	private ArrayList<AbstractDockableStation> westDockables = new ArrayList<AbstractDockableStation>();
	/** dockables associate with the east pane */
	private ArrayList<AbstractDockableStation> eastDockables = new ArrayList<AbstractDockableStation>();
	/** dockables associate with the north pane */
	private ArrayList<AbstractDockableStation> northDockables = new ArrayList<AbstractDockableStation>();
	/** dockables associate with the south pane */
	private ArrayList<AbstractDockableStation> southDockables = new ArrayList<AbstractDockableStation>();
	/**
	 * all dockables contain in this dockstation (north, south, west, east and
	 * center)
	 */
	private ArrayList<Dockable> allDockables = new ArrayList<Dockable>();
	/** dockable associate with the center pane */
	private Dockable centerDockable;

	/**
	 * Constructs a new ToolbarContainerDockStation
	 */
	public ToolbarContainerDockStation(){
		borderPanel = new JPanel( new BorderLayout() );
		// Grid Layout allow component pout in the panels to
		// fill all the space
		westPanel = createSidePanel();
		eastPanel = createSidePanel();
		northPanel = createSidePanel();
		southPanel = createSidePanel();
		centerPanel = new JPanel();
		borderPanel.add( westPanel, BorderLayout.WEST );
		borderPanel.add( eastPanel, BorderLayout.EAST );
		borderPanel.add( northPanel, BorderLayout.NORTH );
		borderPanel.add( southPanel, BorderLayout.SOUTH );
		borderPanel.add( centerPanel, BorderLayout.CENTER );
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
		panel.setBackground( new Color( 31, 73, 125 ) );
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
		// Todo LATER. needed to implement persistent storage
		return null;
	}

	@Override
	public void setPlaceholders( PlaceholderMap placeholders ){
		// Todo LATER. needed to implement persistent storage
	}

	@Override
	public DockableProperty getDockableProperty( Dockable child, Dockable target ){
		int index = eastDockables.indexOf( child );
		if( index >= 0 ){
			return new ToolbarContainerProperty( index, Position.EAST, null );
		}
		
		index = westDockables.indexOf( child );
		if( index >= 0 ){
			return new ToolbarContainerProperty( index, Position.WEST, null );
		}
		
		index = northDockables.indexOf( child );
		if( index >= 0 ){
			return new ToolbarContainerProperty( index, Position.NORTH, null );
		}
		
		index = southDockables.indexOf( child );
		if( index >= 0 ){
			return new ToolbarContainerProperty( index, Position.SOUTH, null );
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
			ArrayList<AbstractDockableStation> associateToolbars;
			Position position;
			Point mousePoint = new Point( mouseX, mouseY );
			SwingUtilities.convertPointFromScreen( mousePoint, borderPanel );

			System.out.println( "########### MOUSE: " + mouseX + "/" + mouseY + " ################" );
			if( westPanel.getBounds().contains( mousePoint ) ) {
				associateToolbars = westDockables;
				position = Position.WEST;
			}
			else if( eastPanel.getBounds().contains( mousePoint ) ) {
				associateToolbars = eastDockables;
				position = Position.EAST;
			}
			else if( northPanel.getBounds().contains( mousePoint ) ) {
				associateToolbars = northDockables;
				position = Position.NORTH;
			}
			else if( southPanel.getBounds().contains( mousePoint ) ) {
				associateToolbars = southDockables;
				position = Position.SOUTH;
			}
			else {
				// user can't drag and drop an element in the center area
				// the user can only add programmaticaly a dockable in the
				// center area
				return null;
			}
			if( !(dockable instanceof ToolbarElementInterface) ) {
				// only ToolbarElementInterface can be drop or move in
				// the side areas
				return null;
			}
			System.out.println( "########### POSITION: " + position + " ################" );
			return new ToolbarContainerDropInfo( dockable, this, associateToolbars, position, mouseX, mouseY );
		}
		else {
			return null;
		}
	}

	/**
	 * Adds <code>dockable</code> to this station in the center area if the
	 * dockable is not a {@link ToolbarElementInterface}. Else do nothing
	 * 
	 * @param dockable
	 *            a new child
	 */
	@Override
	public void drop( Dockable dockable ){
		System.out.println( this.toString() + "## drop(Dockable dockable)##" );
		if( !(dockable instanceof ToolbarElementInterface) ) {
			this.drop( dockable, -1, Position.CENTER );
		}
	}

	@Override
	public boolean drop( Dockable dockable, DockableProperty property ){
		if( property instanceof ToolbarContainerProperty ){
			ToolbarContainerProperty toolbar = (ToolbarContainerProperty)property;
			
			if( toolbar.getSuccessor() != null ){
				Dockable preset = null;
				
				if( toolbar.getPosition() == Position.CENTER ){
					preset = centerDockable;
				}
				else{
					List<? extends Dockable> list = getDockables( toolbar.getPosition() );
					if( toolbar.getIndex() < list.size() ){
						preset = list.get( toolbar.getIndex() );
					}
				}
				
				if( preset != null && preset.asDockStation() != null ){
					return preset.asDockStation().drop( dockable, property.getSuccessor() );
				}
			}
			
			if( toolbar.getPosition() == Position.CENTER ){
				return drop( dockable, Position.CENTER );
			}
			else{
				int max = getDockables( toolbar.getPosition() ).size();
				return drop( dockable, Math.min( max, toolbar.getIndex() ), toolbar.getPosition() );
			}
		}
		return false;
	}

	/**
	 * Adds <code>dockable</code> to this station at the given position. The
	 * dockable in the center area mustn't be a {@link ToolbarElementInterface},
	 * and the dockable in one of the side areas must be a
	 * {@link ToolbarElementInterface} : if not, do nothing. The dockable is
	 * added at the last position in the specified area
	 * 
	 * @param dockable
	 *            a new child
	 * @param position
	 *            Refer to the position of area
	 * @return <code>true</code> if dropping was successfull
	 */
	public boolean drop( Dockable dockable, Position position ){
		System.out.println( this.toString() + "## drop(Dockable dockable, String position)##" );
		return this.drop( dockable, getDockables( position ).size(), position );
	}

	/**
	 * Inserts <code>dockable</code> to this station at the given position and
	 * the given index. The dockable in the center area mustn't be a
	 * {@link ToolbarElementInterface}, and the dockable in on of the side areas
	 * must be a {@link ToolbarElementInterface} : if not, do nothing.
	 * 
	 * @param dockable
	 *            a new child
	 * @param position
	 *            Refer to the position of area
	 * @return <code>true</code> if dropping was successfull
	 */
	private boolean drop( Dockable dockable, int index, Position position ){
		System.out.println( this.toString() + "## drop(Dockable dockable, int index, Position position)##" );
		// where the dockable whre drop (WEST, EAST, etc.)?
		return this.add( dockable, index, position );
	}

	public void drop( ToolbarContainerDropInfo dropInfo ){
		if( dropInfo.isMove() ) {
			move( dropInfo.getItem(), dropInfo.getIndex( ReferencePoint.UPPERLEFT ), dropInfo.getPosition() );
		}
		else {
			drop( dropInfo.getItem(), dropInfo.getIndex( ReferencePoint.BOTTOMRIGHT ), dropInfo.getPosition() );
		}
	}

	private void move( Dockable dockable, int indexWhereInsert, Position position ){
		System.out.println( this.toString() + "## move() ##" );
		System.out.println( position );
		switch( position ){
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
				if( dockable instanceof ToolbarElementInterface ) {
					DockController controller = getController();
					
					try{
						System.out.println( "Index move: " + indexWhereInsert );
						
						if( controller != null ){
							controller.freezeLayout();
						}
						this.remove( dockable );
						// Warning we remove a dockable before insert it again
						// this.add(dropInfo.getDragDockable(), indexWhereInsert,
						// dropInfo.getSide());
						if( indexWhereInsert == 0 ) {
							this.add( dockable, indexWhereInsert, position );
						}
						else {
							this.add( dockable, indexWhereInsert - 1, position );
						}
					}
					finally{
						if( controller != null ){
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
		// Todo LATER Auto-generated method stub
		System.out.println( this.toString() + "## move(Dockable dockable, DockableProperty property) ## " + this.toString() );
	}

	@Override
	public <D extends Dockable & DockStation> boolean isInOverrideZone( int x, int y, D invoker, Dockable drop ){
		// TODO Auto-generated method stub
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
			System.out.println( "	TRUE" );
			System.out.println( "	" + old.toString() + " / " + next.toString() );
			return true;
		}
		else {
			System.out.println( "	FALSE" );
			System.out.println( "	" + old.toString() + " / " + next.toString() );
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
		Position position = getPosition( old );
		int index = positionIndexOf( (Dockable) old );
		this.remove( old );
		// the child is a TollbarGroupDockStation because canReplace()
		// ensure it
		add( (ToolbarGroupDockStation) next, index, position );
		controller.meltLayout();
	}

	@Override
	public void replace( DockStation old, Dockable next ){
		System.out.println( this.toString() + "## replace(DockStation old, Dockable next) ## " + this.toString() );
		replace( old.asDockable(), next );
	}

	@Override
	public String getFactoryID(){
		// Todo LATER
		return null;
	}

	@Override
	public Component getComponent(){
		return borderPanel;
	}

	@Override
	protected void callDockUiUpdateTheme() throws IOException{
		// TODO Auto-generated method stub
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
	 * 
	 * 
	 * @param position
	 * @return the dockables associated with specified position
	 */
	public ArrayList<AbstractDockableStation> getDockables( Position position ){
		if( position == Position.WEST ) {
			return this.westDockables;
		}
		else if( position == Position.EAST ) {
			return this.eastDockables;
		}
		else if( position == Position.NORTH ) {
			return this.northDockables;
		}
		else {
			return this.southDockables;
		}
	}

	/**
	 * Gets the panel associate with the specified position
	 * 
	 * @param position
	 *            of the panel
	 * @return the panel at the specified position
	 */
	private JPanel getPanel( Position position ){
		switch( position ){
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
	 * Gets the position of a dockable
	 * 
	 * @param dockable
	 *            the child which is searched
	 * @return the position of a dockable or null if it was not found
	 */
	private Position getPosition( Dockable dockable ){
		if( westDockables.contains( dockable ) ) {
			return Position.WEST;
		}
		else if( eastDockables.contains( dockable ) ) {
			return Position.EAST;
		}
		else if( northDockables.contains( dockable ) ) {
			return Position.NORTH;
		}
		else if( southDockables.contains( dockable ) ) {
			return Position.SOUTH;
		}
		else if( dockable == centerDockable ) {
			return Position.CENTER;
		}
		else {
			return null;
		}
	}

	/**
	 * Gets the orientation of dockables in one area at the specified position
	 * 
	 * @param position
	 *            refer to the area at the given position
	 * @return the orientation
	 */
	public Orientation getOrientation( Position position ){
		if( position == Position.WEST || position == Position.EAST ) {
			return Orientation.VERTICAL;
		}
		else {
			return Orientation.HORIZONTAL;
		}
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
	private int positionIndexOf( Dockable dockable ){
		Position position = getPosition( dockable );
		switch( position ){
			case CENTER:
				return 0;
			case NORTH:
			case SOUTH:
			case WEST:
			case EAST:
				ArrayList<AbstractDockableStation> dockables = getDockables( position );
				int index = 0;
				for( Dockable d : dockables ) {
					if( d == dockable ) {
						return index;
					}
					index++;
				}
				return -1;
			default:
				throw new NullPointerException();
		}
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
		Position position = getPosition( dockable );
		DockHierarchyLock.Token token = DockHierarchyLock.acquireUnlinking( this, dockable );
		try {
			int globalIndex = globalIndexOf( dockable );
			switch( position ){
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
					JPanel panel = getPanel( position );
					ArrayList<AbstractDockableStation> dockables = getDockables( position );
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
		allDockables.addAll( westDockables );
		allDockables.addAll( northDockables );
		allDockables.addAll( eastDockables );
		allDockables.addAll( southDockables );
		allDockables.add( centerDockable );
	}

	/**
	 * Add one dockable at the position and the index position. if Position is
	 * null, then the dockable is added on the center area. The dockable can be
	 * a {@link ComponentDockable}, {@link ToolbarGroupDockStation} or a
	 * {@link ToolbarDockStation} (see method accept()). All the
	 * ComponentDockable extracted from the element are merged together and
	 * wrapped in a {@link ToolbarGroupDockStation} before to be added at index
	 * position
	 * 
	 * @param dockable
	 *            Dockable to add
	 * @param index
	 *            Index where add dockable
	 * @param position
	 *            Position where insert dokckable
	 * @return <code>true</code> if dropping was successfull
	 */
	protected boolean add( Dockable dockable, int index, Position position ){
		DockUtilities.ensureTreeValidity( this, dockable );
		DockUtilities.checkLayoutLocked();
		// DockHierarchyLock.Token token =
		// DockHierarchyLock.acquireLinking(this,
		// dockable);
		switch( position ){
			case CENTER:
				if( !(dockable instanceof ToolbarElementInterface) ) {
					DockHierarchyLock.Token token = DockHierarchyLock.acquireLinking( this, dockable );
					try {
						if( centerDockable != null ) {
							remove( dockable );
						}
						dockable.setDockParent( this );
						listeners.fireDockableAdding( dockable );
						centerPanel.add( dockable.getComponent(), index );
						centerDockable = dockable;
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
				if( dockable instanceof ToolbarElementInterface ) {
					ToolbarDockStation toolbar;
					if( dockable instanceof ToolbarDockStation ) {
						toolbar = (ToolbarDockStation) dockable;
					}
					else {
						toolbar = new ToolbarDockStation();
						toolbar.drop( dockable );
					}
					DockHierarchyLock.Token token = DockHierarchyLock.acquireLinking( this, toolbar );
					try {
						toolbar.setDockParent( this );
						listeners.fireDockableAdding( toolbar );
						ArrayList<AbstractDockableStation> dockables = getDockables( position );
						JPanel panel = getPanel( position );
						toolbar.setOrientation( getOrientation( position ) );
						System.out.println( "########### INDEX: " + index + " ################" );
						panel.add( toolbar.getComponent(), index );
						dockables.add( index, toolbar );
						updateDockables();
						panel.revalidate();
						borderPanel.repaint();
						listeners.fireDockableAdded( toolbar );
						fireDockablesRepositioned( index + 1 );
					}
					finally {
						token.release();
					}
					return true;
				}
				break;
			default:
				throw new IllegalStateException( "Unknown position: " + position );
		}
		
		return false;
	}

}
