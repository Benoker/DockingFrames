package bibliothek.gui.dock;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.Point;
import java.awt.Rectangle;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.EtchedBorder;
import javax.swing.border.TitledBorder;

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
import bibliothek.gui.dock.station.support.PlaceholderMap;
import bibliothek.gui.dock.station.toolbar.ToolbarContainerDropInfo;
import bibliothek.gui.dock.station.toolbar.ToolbarContainerProperty;
import bibliothek.gui.dock.station.toolbar.ToolbarStrategy;
import bibliothek.gui.dock.station.toolbar.Position;
import bibliothek.gui.dock.themes.DefaultStationPaintValue;
import bibliothek.gui.dock.util.DockUtilities;
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
public class ToolbarContainerDockStation extends AbstractDockableStation
		implements ToolbarInterface{

	/**
	 * The graphical representation of this station: the pane which contains
	 * toolbars
	 */
	private JPanel borderPanel = new JPanel(new BorderLayout());
	/** The westPane */
	private JPanel westPanel;
	/** The east pane */
	private JPanel eastPanel;
	/** The north pane */
	private JPanel northPanel;
	/** The south Pane */
	private JPanel southPanel;
	/** The center Pane */
	private JPanel centerPanel = new JPanel(new GridLayout(1, 1));
	/** dockables associate with the west pane */
	private ArrayList<Dockable> westDockables = new ArrayList<Dockable>();
	/** dockables associate with the east pane */
	private ArrayList<Dockable> eastDockables = new ArrayList<Dockable>();
	/** dockables associate with the north pane */
	private ArrayList<Dockable> northDockables = new ArrayList<Dockable>();
	/** dockables associate with the south pane */
	private ArrayList<Dockable> southDockables = new ArrayList<Dockable>();
	/**
	 * all dockables contain in this dockstation (north, south, west, east and
	 * center)
	 */
	private ArrayList<Dockable> allDockables = new ArrayList<Dockable>();
	/** dockable associate with the center pane */
	private Dockable centerDockable;
	/** A paint to draw lines */
	private DefaultStationPaintValue paint;
	/** the index of the closest dockable above the mouse */
	private Integer indexAboveMouse = null;
	/** closest side of the the closest dockable above the mouse */
	private Position sideAboveMouse = null;

	/**
	 * Constructs a new ToolbarContainerDockStation
	 */
	public ToolbarContainerDockStation(){
		// Grid Layout allow component pout in the panels to
		// fill all the space
		westPanel = createSidePanel();
		eastPanel = createSidePanel();
		northPanel = createSidePanel();
		southPanel = createSidePanel();
		borderPanel.add(westPanel, BorderLayout.WEST);
		borderPanel.add(eastPanel, BorderLayout.EAST);
		borderPanel.add(northPanel, BorderLayout.NORTH);
		borderPanel.add(southPanel, BorderLayout.SOUTH);
		borderPanel.add(centerPanel, BorderLayout.CENTER);
	}

	/**
	 * Create a side pane for the side areas of this dock station
	 */
	private JPanel createSidePanel(){
		JPanel panel = new JPanel();
		GridLayout layout = new GridLayout(1, 1);
		layout.setHgap(10);
		layout.setVgap(10);
		panel.setLayout(layout);
		panel.setBorder(new CompoundBorder(new EtchedBorder(), new EmptyBorder(
				new Insets(5, 5, 5, 5))));
		panel.setBackground(new Color(31, 73, 125));
		return panel;
	}

	@Override
	public int getDockableCount(){
		return allDockables.size();
	}

	@Override
	public Dockable getDockable( int index ){
		return allDockables.get(index);
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
		int index = eastDockables.indexOf(child);
		if (index >= 0){
			return new ToolbarContainerProperty(index, Position.EAST, null);
		}

		index = westDockables.indexOf(child);
		if (index >= 0){
			return new ToolbarContainerProperty(index, Position.WEST, null);
		}

		index = northDockables.indexOf(child);
		if (index >= 0){
			return new ToolbarContainerProperty(index, Position.NORTH, null);
		}

		index = southDockables.indexOf(child);
		if (index >= 0){
			return new ToolbarContainerProperty(index, Position.SOUTH, null);
		}

		return new ToolbarContainerProperty(0, Position.CENTER, null);
	}

	public StationDropOperation prepareDrop( int mouseX, int mouseY,
			int titleX, int titleY, boolean checkOverrideZone, Dockable dockable ){
		System.out.println(this.toString() + "## prepareDrop(...) ##");
		DockController controller = getController();
		// check whether this station has to check if the mouse is in the
		// override-zone of its parent & (if this parent exist) if
		// the mouse is in the override-zone
		if (checkOverrideZone & this.getDockParent() != null){
			if (this.getDockParent().isInOverrideZone(mouseX, mouseY, this,
					dockable)){
				return null;
			}
		}
		// check if the dockable and the station accept each other
		if (this.accept(dockable) & dockable.accept(this)){
			// check if controller exist and if the controller accept that
			// the dockable become a child of this station
			if (controller != null){
				if (!controller.getAcceptance().accept(this, dockable)){
					return null;
				}
			}
			ArrayList<Dockable> associateToolbars;
			Position area;
			Point mousePoint = new Point(mouseX, mouseY);
			SwingUtilities.convertPointFromScreen(mousePoint, borderPanel);

			System.out.println("########### MOUSE: " + mouseX + "/" + mouseY
					+ " ################");
			if (westPanel.getBounds().contains(mousePoint)){
				associateToolbars = westDockables;
				area = Position.WEST;
			} else if (eastPanel.getBounds().contains(mousePoint)){
				associateToolbars = eastDockables;
				area = Position.EAST;
			} else if (northPanel.getBounds().contains(mousePoint)){
				associateToolbars = northDockables;
				area = Position.NORTH;
			} else if (southPanel.getBounds().contains(mousePoint)){
				associateToolbars = southDockables;
				area = Position.SOUTH;
			} else{
				// user can't drag and drop an element in the center area
				// the user can only add programmaticaly a dockable in the
				// center area
				return null;
			}
			if (!getToolbarStrategy().isToolbarPart(dockable)){
				// only ToolbarElementInterface can be drop or move in
				// the side areas
				return null;
			}
			System.out.println("########### AREA: " + area
					+ " ################");
			return new ToolbarContainerDropInfo(dockable, this,
					associateToolbars, area, mouseX, mouseY);
		} else{
			return null;
		}
	}

	private void drop( ToolbarContainerDropInfo dropInfo ){		
		// Note: Computation of index to insert drag dockable is not the same
		// between a move() and a drop(), because with a move(), we remove
		// first the drag dockable before we add it again in the list
		// We consider move() occur if :
		// - the dockable added come from this station and stay on the same area
		// We consider drop() occur if :
		// - the dockable added come from another station OR come from this station
		// but come from another area of this station
		if (dropInfo.getDragDockablePosition() != Position.CENTER){
			int dropIndex;
			if (dropInfo.isMove()){
				switch (this.getOrientation()) {
				case VERTICAL:
					if (dropInfo.getDragDockablePosition() == Position.SOUTH){
						if (dropInfo.getSide() == Position.SOUTH){
							dropIndex = dropInfo.getIndex() + 1;
						} else{
							dropIndex = dropInfo.getIndex();
						}
					} else{
						if (dropInfo.getSide() == Position.SOUTH){
							dropIndex = dropInfo.getIndex();
						} else{
							dropIndex = dropInfo.getIndex() - 1;
						}
					}
					move(dropInfo.getItem(), dropIndex);
					break;
				case HORIZONTAL:
					if (dropInfo.getDragDockablePosition() == Position.EAST){
						if (dropInfo.getSide() == Position.EAST){
							dropIndex = dropInfo.getIndex() + 1;
						} else{
							dropIndex = dropInfo.getIndex();
						}
					} else{
						if (dropInfo.getSide() == Position.EAST){
							dropIndex = dropInfo.getIndex();
						} else{
							dropIndex = dropInfo.getIndex() - 1;
						}
					}
					move(dropInfo.getItem(), dropIndex);
					break;
				}
			} else{
				int increment = 0;
				if (dropInfo.getSide() == Position.SOUTH
						|| dropInfo.getSide() == Position.EAST){
					increment++;
				}
				dropIndex = dropInfo.getIndex() + increment;
				drop(dropInfo.getItem(), dropIndex);
			}
		}
		
		
		

		// if( dropInfo.isMove() ) {
		// move( dropInfo.getItem(), dropInfo.getIndex( ReferencePoint.UPPERLEFT
		// ), dropInfo.getArea() );
		// }
		// else {
		// drop( dropInfo.getItem(), dropInfo.getIndex(
		// ReferencePoint.BOTTOMRIGHT ), dropInfo.getArea() );
		// }
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
		System.out.println(this.toString() + "## drop(Dockable dockable)##");
		if (!(getToolbarStrategy().isToolbarPart(dockable))){
			this.drop(dockable, -1, Position.CENTER);
		}
	}

	@Override
	public boolean drop( Dockable dockable, DockableProperty property ){
		if (property instanceof ToolbarContainerProperty){
			ToolbarContainerProperty toolbar = (ToolbarContainerProperty) property;

			if (toolbar.getSuccessor() != null){
				Dockable preset = null;

				if (toolbar.getArea() == Position.CENTER){
					preset = centerDockable;
				} else{
					List<? extends Dockable> list = getDockables(toolbar
							.getArea());
					if (toolbar.getIndex() < list.size()){
						preset = list.get(toolbar.getIndex());
					}
				}

				if (preset != null && preset.asDockStation() != null){
					return preset.asDockStation().drop(dockable,
							property.getSuccessor());
				}
			}

			if (toolbar.getArea() == Position.CENTER){
				return drop(dockable, Position.CENTER);
			} else{
				int max = getDockables(toolbar.getArea()).size();
				return drop(dockable, Math.min(max, toolbar.getIndex()),
						toolbar.getArea());
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
		System.out.println(this.toString()
				+ "## drop(Dockable dockable, Position area)##");
		return this.drop(dockable, getDockables(area).size(), area);
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
		System.out.println(this.toString()
				+ "## drop(Dockable dockable, int index, Position area)##");
		// where the dockable whre drop (WEST, EAST, etc.)?
		return this.add(dockable, index, area);
	}

	private void move( Dockable dockable, int indexWhereInsert, Position area ){
		System.out.println(this.toString() + "## move() ##");
		System.out.println(area);
		switch (area) {
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
			if (getToolbarStrategy().isToolbarPart(dockable)){
				DockController controller = getController();

				try{
					System.out.println("Index move: " + indexWhereInsert);

					if (controller != null){
						controller.freezeLayout();
					}
					this.remove(dockable);
					// Warning we remove a dockable before insert it again
					// this.add(dropInfo.getDragDockable(), indexWhereInsert,
					// dropInfo.getSide());
					if (indexWhereInsert == 0){
						this.add(dockable, indexWhereInsert, area);
					} else{
						this.add(dockable, indexWhereInsert - 1, area);
					}
				} finally{
					if (controller != null){
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
		System.out.println(this.toString()
				+ "## move(Dockable dockable, DockableProperty property) ## "
				+ this.toString());
	}

	@Override
	public <D extends Dockable & DockStation> boolean isInOverrideZone( int x,
			int y, D invoker, Dockable drop ){
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean canDrag( Dockable dockable ){
		System.out.println(this.toString()
				+ "## canDrag(Dockable dockable) ## " + this.toString());
		return true;
	}

	@Override
	public void drag( Dockable dockable ){
		System.out.println(this.toString() + "## drag(Dockable dockable) ##");
		if (dockable.getDockParent() != this)
			throw new IllegalArgumentException(
					"The dockable cannot be dragged, it is not child of this station.");
		this.remove(dockable);
	}

	@Override
	public boolean canReplace( Dockable old, Dockable next ){
		System.out.println(this.toString()
				+ "## canReplace(Dockable old, Dockable next) ## "
				+ this.toString());
		if (old.getClass() == next.getClass()){
			System.out.println("	TRUE");
			System.out.println("	" + old.toString() + " / " + next.toString());
			return true;
		} else{
			System.out.println("	FALSE");
			System.out.println("	" + old.toString() + " / " + next.toString());
			return false;
		}
	}

	@Override
	public void replace( Dockable old, Dockable next ){
		System.out.println(this.toString()
				+ "## replace(Dockable old, Dockable next) ## "
				+ this.toString());
		System.out.println("	" + old.toString() + " / " + next.toString());
		DockUtilities.checkLayoutLocked();
		DockController controller = getController();
		if (controller != null)
			controller.freezeLayout();
		Position area = getArea(old);
		int index = areaIndexOf((Dockable) old);
		this.remove(old);
		// the child is a TollbarGroupDockStation because canReplace()
		// ensure it
		add((ToolbarGroupDockStation) next, index, area);
		controller.meltLayout();
	}

	@Override
	public void replace( DockStation old, Dockable next ){
		System.out.println(this.toString()
				+ "## replace(DockStation old, Dockable next) ## "
				+ this.toString());
		replace(old.asDockable(), next);
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

	/**
	 * Gets the {@link ToolbarStrategy} that is currently used by this station.
	 * 
	 * @return the strategy, never <code>null</code>
	 */
	public ToolbarStrategy getToolbarStrategy(){
		SilentPropertyValue<ToolbarStrategy> value = new SilentPropertyValue<ToolbarStrategy>(
				ToolbarStrategy.STRATEGY, getController());
		ToolbarStrategy result = value.getValue();
		value.setProperties((DockController) null);
		return result;
	}

	@Override
	public boolean accept( Dockable child ){
		System.out.println(this.toString() + "## accept(Dockable child) ##");
		return true;
	}

	@Override
	public boolean accept( DockStation station ){
		System.out.println(this.toString()
				+ "## accept(DockStation station) ##");
		// we can put this dockstation everywhere
		return true;
	}

	@Override
	public String toString(){
		return this.getClass().getSimpleName() + '@'
				+ Integer.toHexString(this.hashCode());
	}

	/**
	 * 
	 * 
	 * @param area
	 * @return the dockables associated in the specified area
	 */
	public ArrayList<Dockable> getDockables( Position area ){
		if (area == Position.WEST){
			return this.westDockables;
		} else if (area == Position.EAST){
			return this.eastDockables;
		} else if (area == Position.NORTH){
			return this.northDockables;
		} else{
			return this.southDockables;
		}
	}

	/**
	 * Gets the panel associate with the specified area
	 * 
	 * @param area
	 *            of the panel
	 * @return the panel at the specified area
	 */
	private JPanel getPanel( Position area ){
		switch (area) {
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
		if (westDockables.contains(dockable)){
			return Position.WEST;
		} else if (eastDockables.contains(dockable)){
			return Position.EAST;
		} else if (northDockables.contains(dockable)){
			return Position.NORTH;
		} else if (southDockables.contains(dockable)){
			return Position.SOUTH;
		} else if (dockable == centerDockable){
			return Position.CENTER;
		} else{
			return null;
		}
	}

	/**
	 * Gets the orientation of dockables in one area at the specified area
	 * 
	 * @param area
	 *            refer to the area at the given position
	 * @return the orientation
	 */
	public Orientation getOrientation( Position area ){
		if (area == Position.WEST || area == Position.EAST){
			return Orientation.VERTICAL;
		} else{
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
		for (Dockable d : allDockables){
			if (d == dockable){
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
		Position area = getArea(dockable);
		switch (area) {
		case CENTER:
			return 0;
		case NORTH:
		case SOUTH:
		case WEST:
		case EAST:
			ArrayList<Dockable> dockables = getDockables(area);
			int index = 0;
			for (Dockable d : dockables){
				if (d == dockable){
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
		Position area = getArea(dockable);
		DockHierarchyLock.Token token = DockHierarchyLock.acquireUnlinking(
				this, dockable);
		try{
			int globalIndex = globalIndexOf(dockable);
			switch (area) {
			case CENTER:
				listeners.fireDockableRemoving(dockable);
				dockable.setDockParent(null);
				centerDockable = null;
				allDockables.remove(globalIndex);
				centerPanel.remove(dockable.getComponent());
				centerPanel.revalidate();
				centerPanel.repaint();
				listeners.fireDockableRemoved(dockable);
				fireDockablesRepositioned(globalIndex);
				break;
			case NORTH:
			case SOUTH:
			case WEST:
			case EAST:
				JPanel panel = getPanel(area);
				ArrayList<Dockable> dockables = getDockables(area);
				listeners.fireDockableRemoving(dockable);
				dockable.setDockParent(null);
				dockables.remove(dockable);
				allDockables.remove(globalIndex);
				panel.remove(dockable.getComponent());
				panel.revalidate();
				centerPanel.repaint();
				listeners.fireDockableRemoved(dockable);
				fireDockablesRepositioned(globalIndex);
				break;
			default:
				throw new NullPointerException();
			}
		} finally{
			token.release();
		}
	}

	/**
	 * Update the list off all dockables. Used when one of the lists of
	 * dockables associate with one area is updated
	 */
	public void updateDockables(){
		allDockables.clear();
		allDockables.addAll(westDockables);
		allDockables.addAll(northDockables);
		allDockables.addAll(eastDockables);
		allDockables.addAll(southDockables);
		allDockables.add(centerDockable);
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
		DockUtilities.ensureTreeValidity(this, dockable);
		DockUtilities.checkLayoutLocked();
		// DockHierarchyLock.Token token =
		// DockHierarchyLock.acquireLinking(this,
		// dockable);

		ToolbarStrategy strategy = getToolbarStrategy();

		switch (area) {
		case CENTER:
			if (!strategy.isToolbarPart(dockable)){
				DockHierarchyLock.Token token = DockHierarchyLock
						.acquireLinking(this, dockable);
				try{
					if (centerDockable != null){
						remove(dockable);
					}
					dockable.setDockParent(this);
					listeners.fireDockableAdding(dockable);
					centerPanel.add(dockable.getComponent(), index);
					centerDockable = dockable;
					updateDockables();
					centerPanel.revalidate();
					centerPanel.repaint();
					listeners.fireDockableAdded(dockable);
					fireDockablesRepositioned(index + 1);
					System.out.println("IN CENTER");
				} finally{
					token.release();
				}
				return true;
			}
			break;
		case NORTH:
		case SOUTH:
		case WEST:
		case EAST:
			if (strategy.isToolbarPart(dockable)){
				dockable = strategy.ensureToolbarLayer(this, dockable);

				DockHierarchyLock.Token token = DockHierarchyLock
						.acquireLinking(this, dockable);
				try{
					dockable.setDockParent(this);
					listeners.fireDockableAdding(dockable);
					ArrayList<Dockable> dockables = getDockables(area);
					JPanel panel = getPanel(area);
					if (dockable instanceof OrientedDockStation){
						((OrientedDockStation) dockable)
								.setOrientation(getOrientation(area));
					}
					System.out.println("########### INDEX: " + index
							+ " ################");
					panel.add(dockable.getComponent(), index);
					dockables.add(index, dockable);
					updateDockables();
					panel.revalidate();
					borderPanel.repaint();
					listeners.fireDockableAdded(dockable);
					fireDockablesRepositioned(index + 1);
				} finally{
					token.release();
				}
				return true;
			}
			break;
		default:
			throw new IllegalStateException("Unknown area: " + area);
		}

		return false;
	}

	/**
	 * This panel is used as base of the station. All children of the station
	 * have this panel as parent too. It allows to draw arbitrary figures over
	 * the base panel
	 * 
	 * @author Herve Guillaume
	 */
	protected class OverpaintablePanelBase extends OverpaintablePanel{
		/**
		 * The content Pane of this {@link OverpaintablePanel}
		 */
		private JPanel content = new JPanel();

		/**
		 * Creates a new panel
		 */
		public OverpaintablePanelBase(){
			// content.setBorder(new CompoundBorder(new EtchedBorder(),
			// new EmptyBorder(new Insets(5, 5, 5, 5))));
			content.setBorder(new TitledBorder(ToolbarContainerDockStation.this
					.getClass().getSimpleName()));
			content.setBounds(0, 0, content.getPreferredSize().width,
					content.getPreferredSize().height);
			this.getContentPane().setBounds(0, 0,
					this.getContentPane().getPreferredSize().width,
					this.getContentPane().getPreferredSize().height);
			this.setPreferredSize(new Dimension(this.getContentPane()
					.getPreferredSize().width, this.getContentPane()
					.getPreferredSize().height));
			setContentPane(content);
			setBasePane(content);
		}

		@Override
		protected void paintOverlay( Graphics g ){
			// DefaultStationPaintValue paint = getPaint();
			// if (indexAboveMouse != null){
			// Rectangle rect = dockables.get(indexAboveMouse).getComponent()
			// .getBounds();
			// if (rect != null){
			// switch (ToolbarContainerDockStation.this.getOrientation()) {
			// case VERTICAL:
			// int y;
			// if (sideAboveMouse == Position.NORTH){
			// y = rect.y;
			// } else{
			// y = rect.y + rect.height;
			// }
			// paint.drawInsertionLine(g, rect.x, y, rect.x
			// + rect.width, y);
			// break;
			// case HORIZONTAL:
			// int x;
			// if (sideAboveMouse == Position.WEST){
			// x = rect.x;
			// } else{
			// x = rect.x + rect.width;
			// }
			// paint.drawInsertionLine(g, x, rect.y, x, rect.y
			// + rect.height);
			// break;
			// }
			// }
		}

		// }

		@Override
		public String toString(){
			return this.getClass().getSimpleName() + '@'
					+ Integer.toHexString(this.hashCode());
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
		super.setController(controller);
		// if not set controller of the DefaultStationPaintValue, call to
		// DefaultStationPaintValue do nothing
		paint.setController(controller);
	}

}
