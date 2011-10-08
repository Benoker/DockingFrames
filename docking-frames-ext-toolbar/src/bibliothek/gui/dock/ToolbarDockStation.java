package bibliothek.gui.dock;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Insets;
import java.awt.Rectangle;
import java.io.IOException;
import java.util.ArrayList;

import javax.swing.BoxLayout;
import javax.swing.JPanel;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.EtchedBorder;
import javax.swing.border.TitledBorder;

import bibliothek.gui.DockController;
import bibliothek.gui.DockStation;
import bibliothek.gui.Dockable;
import bibliothek.gui.OrientedDockStation;
import bibliothek.gui.ToolbarElementInterface;
import bibliothek.gui.ToolbarInterface;
import bibliothek.gui.dock.layout.DockableProperty;
import bibliothek.gui.dock.station.AbstractDockableStation;
import bibliothek.gui.dock.station.OverpaintablePanel;
import bibliothek.gui.dock.station.StationDropOperation;
import bibliothek.gui.dock.station.StationPaint;
import bibliothek.gui.dock.station.support.PlaceholderMap;
import bibliothek.gui.dock.station.toolbar.Position;
import bibliothek.gui.dock.station.toolbar.ToolbarDropInfo;
import bibliothek.gui.dock.station.toolbar.ToolbarProperty;
import bibliothek.gui.dock.station.toolbar.ToolbarStrategy;
import bibliothek.gui.dock.themes.DefaultStationPaintValue;
import bibliothek.gui.dock.themes.ThemeManager;
import bibliothek.gui.dock.util.DockUtilities;
import bibliothek.gui.dock.util.SilentPropertyValue;

/**
 * A {@link Dockable} and a {@link Dockstation} which stands a group of
 * {@link ToolbarGroupDockStation}. As dockable it can be put in
 * {@link DockStation} which implements marker interface
 * {@link ToolbarInterface} or in {@link ScreenDockStation}, so that a
 * ToolbarDockStation can be floattable. As DockStation it accepts a
 * {@link ToolbarElementInterface}. All the ComponentDockable extracted from the
 * element are merged together and wrapped in a {@link ToolbarGroupDockStation}
 * before to be added
 * 
 * @author Herve Guillaume
 */
public class ToolbarDockStation extends AbstractDockableStation implements
		OrientedDockStation, ToolbarInterface, ToolbarElementInterface{

	/**
	 * The graphical representation of this station: the pane which contains
	 * component
	 */
	protected OverpaintablePanelBase basePanel = new OverpaintablePanelBase();
	/** A list of all children */
	private ArrayList<Dockable> dockables = new ArrayList<Dockable>();
	/**
	 * Graphical orientation of the group of components (vertical or horizontal)
	 */
	private Orientation orientation = null;
	/** A paint to draw lines */
	private DefaultStationPaintValue paint;
	/** the index of the closest dockable above the mouse */
	private Integer indexBeneathMouse = null;
	/** closest side of the the closest dockable above the mouse */
	private Position sideBeneathMouse = null;

	/**
	 * Constructs a new ToolbarDockStation
	 */
	public ToolbarDockStation(){
		basePanel = new OverpaintablePanelBase();
		paint = new DefaultStationPaintValue(ThemeManager.STATION_PAINT
				+ ".toolbar", this);
		// basePanel.setLayout( new BoxLayout( basePanel, BoxLayout.Y_AXIS ) );
		// basePanel.setBorder( new CompoundBorder( new EtchedBorder(), new
		// EmptyBorder( new Insets( 5, 5, 5, 5 ) ) ) );
		// basePanel.setBackground( new Color( 255, 255, 128 ) );
	}

	@Override
	public int getDockableCount(){
		return dockables.size();
	}

	@Override
	public Dockable getDockable( int index ){
		return dockables.get(index);
	}

	public ArrayList<Dockable> getDockables(){
		return this.dockables;
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
		int index = dockables.indexOf(child);
		return new ToolbarProperty(index, null);
	}

	@Override
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
			return new ToolbarDropInfo<ToolbarDockStation>(dockable, this,
					mouseX, mouseY){
				@Override
				public void execute(){
					drop(this);
				}

				// Note: draw() is called first by the Controller. It seems
				// destroy() is called after, after a new StationDropOperation
				// is created

				@Override
				public void destroy(){
					// without this line, nothing is displayed except if you
					// drag another component
					ToolbarDockStation.this.indexBeneathMouse = null;
					ToolbarDockStation.this.sideBeneathMouse = null;
					ToolbarDockStation.this.basePanel.getContentPane()
							.repaint();
				}

				@Override
				public void draw(){
					// without this line, nothing is displayed
					ToolbarDockStation.this.indexBeneathMouse = ToolbarDockStation.this
							.getDockables().indexOf(
									this.getDockableBeneathMouse());
					ToolbarDockStation.this.sideBeneathMouse = this.getSideDockableBeneathMouse();
					// without this line, line is displayed only on the first
					// component met
					ToolbarDockStation.this.basePanel.getContentPane()
							.repaint();
				}
			};
		} else{
			return null;
		}
	}

	/**
	 * Drop thanks to information collect by dropInfo
	 * 
	 * @param dropInfo
	 */
	private void drop( ToolbarDropInfo<?> dropInfo ){
		if (dropInfo.getItemPositionVSBeneathDockable() != Position.CENTER){
			// Note: Computation of index to insert drag dockable is not the same
			// between a move() and a drop(), because with a move() it is as if the
			// drag dockable were remove first then added again in the list
			// (Note: It's wird beacause indeed drag() is called after move()...)
			int dropIndex;
			int indexBeneathMouse = this.getDockables().indexOf(
					dropInfo.getDockableBeneathMouse());
			if (dropInfo.isMove()){
				switch (this.getOrientation()) {
				case VERTICAL:
					if (dropInfo.getItemPositionVSBeneathDockable() == Position.SOUTH){
						if (dropInfo.getSideDockableBeneathMouse() == Position.SOUTH){
							dropIndex = indexBeneathMouse + 1;
						} else{
							dropIndex = indexBeneathMouse;
						}
					} else{
						if (dropInfo.getSideDockableBeneathMouse() == Position.SOUTH){
							dropIndex = indexBeneathMouse;
						} else{
							dropIndex = indexBeneathMouse - 1;
						}
					}
					move(dropInfo.getItem(), dropIndex);
					break;
				case HORIZONTAL:
					if (dropInfo.getItemPositionVSBeneathDockable() == Position.EAST){
						if (dropInfo.getSideDockableBeneathMouse() == Position.EAST){
							dropIndex = indexBeneathMouse + 1;
						} else{
							dropIndex = indexBeneathMouse;
						}
					} else{
						if (dropInfo.getSideDockableBeneathMouse() == Position.EAST){
							dropIndex = indexBeneathMouse;
						} else{
							dropIndex = indexBeneathMouse - 1;
						}
					}
					move(dropInfo.getItem(), dropIndex);
					break;
				}
			} else{
				int increment = 0;
				if (dropInfo.getSideDockableBeneathMouse() == Position.SOUTH
						|| dropInfo.getSideDockableBeneathMouse() == Position.EAST){
					increment++;
				}
				dropIndex = indexBeneathMouse + increment;
				drop(dropInfo.getItem(), dropIndex);
			}
		}
	}

	@Override
	public void drop( Dockable dockable ){
		System.out.println(this.toString() + "## drop(Dockable dockable)##");
		this.drop(dockable, dockables.size());
	}

	@Override
	public boolean drop( Dockable dockable, DockableProperty property ){
		if (property instanceof ToolbarProperty){
			ToolbarProperty toolbar = (ToolbarProperty) property;
			if (toolbar.getSuccessor() != null
					&& toolbar.getIndex() < getDockableCount()){
				DockStation child = getDockable(toolbar.getIndex())
						.asDockStation();
				if (child != null){
					return child.drop(dockable, toolbar.getSuccessor());
				}
			}
			return drop(dockable,
					Math.min(getDockableCount(), toolbar.getIndex()));
		}
		return false;
	}

	/**
	 * Dropps <code>dockable</code> at location <code>index</code>.
	 * 
	 * @param dockable
	 *            the element to add
	 * @param index
	 *            the location of <code>dockable</code>
	 * @return whether the operation was succesfull or not
	 */
	public boolean drop( Dockable dockable, int index ){
		// note: merging of two ToolbarGroupDockStations is done by the
		// ToolbarGroupDockStationMerger
		System.out.println(this.toString()
				+ "## drop(Dockable dockable, int index)##");
		if (this.accept(dockable)){
			dockable = getToolbarStrategy().ensureToolbarLayer(this, dockable);
			if (dockable == null){
				return false;
			}
			add(dockable, index);
			return true;
		}
		return false;
	}

	private void move( Dockable dockable, int indexWhereInsert ){
		System.out.println(this.toString() + "## move() ##");
		System.out.println("Index move: " + indexWhereInsert);
		DockController controller = getController();
		try{
			if (controller != null){
				controller.freezeLayout();
			}
			this.add(dockable, indexWhereInsert);
		} finally{
			if (controller != null){
				controller.meltLayout();
			}
		}
	}

	@Override
	public void move( Dockable dockable, DockableProperty property ){
		// TODO pending
	}

	@Override
	public <D extends Dockable & DockStation> boolean isInOverrideZone( int x,
			int y, D invoker, Dockable drop ){
		return false;
	}

	@Override
	public boolean canDrag( Dockable dockable ){
		System.out.println(this.toString()
				+ "## canDrag(Dockable dockable) ## ");
		return true;
	}

	@Override
	public void drag( Dockable dockable ){
		System.out.println(this.toString() + "## drag(Dockable dockable) ##");
		if (dockable.getDockParent() != this)
			throw new IllegalArgumentException(
					"The dockable cannot be dragged, it is not child of this station.");
		int index = this.indexOf(dockable);
		if (index >= 0){
			this.remove(index);
		}
	}

	@Override
	public boolean canReplace( Dockable old, Dockable next ){
		System.out.println(this.toString()
				+ "## canReplace(Dockable old, Dockable next) ## ");
		if (old.getClass() == next.getClass()){
			return true;
		} else{
			return false;
		}
	}

	@Override
	public void replace( Dockable old, Dockable next ){
		System.out.println(this.toString()
				+ "## replace(Dockable old, Dockable next) ## ");
		DockUtilities.checkLayoutLocked();
		DockController controller = getController();
		if (controller != null)
			controller.freezeLayout();
		int index = indexOf(old);
		remove(old);
		// the child is a ToolbarGroupDockStation because canReplace()
		// ensure it
		add(next, index);
		controller.meltLayout();
	}

	@Override
	public void replace( DockStation old, Dockable next ){
		System.out.println(this.toString()
				+ "## replace(DockStation old, Dockable next) ## ");
		replace(old.asDockable(), next);
	}

	@Override
	public String getFactoryID(){
		// Todo LATER
		return null;
	}

	@Override
	public Component getComponent(){
		return basePanel;
	}

	@Override
	protected void callDockUiUpdateTheme() throws IOException{
		// Todo LATER
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
		return getToolbarStrategy().isToolbarPart(child);
	}

	@Override
	public boolean accept( DockStation station ){
		System.out.println(this.toString()
				+ "## accept(DockStation station) ##");
		return getToolbarStrategy().isToolbarGroupPartParent(station, this);
	}

	@Override
	public String toString(){
		return this.getClass().getSimpleName() + '@'
				+ Integer.toHexString(this.hashCode());
	}

	/**
	 * Gets the location of <code>dockable</code> in the component-panel.
	 * 
	 * @param dockable
	 *            the {@link Dockable} to search
	 * @return the location or -1 if the child was not found
	 */
	private int indexOf( Dockable dockable ){
		int index = 0;
		for (Dockable currentDockable : dockables){
			if (currentDockable == dockable){
				return index;
			}
			index++;
		}
		return -1;
	}

	/**
	 * Insert one dockable at the index. The dockable can be a
	 * {@link ComponentDockable}, {@link ToolbarGroupDockStation} or a
	 * {@link ToolbarDockStation} (see method accept()). All the
	 * ComponentDockable extracted from the element are merged together and
	 * wrapped in a {@link ToolbarGroupDockStation} before to be inserted at the
	 * index
	 * 
	 * @param dockable
	 *            Dockable to add
	 * @param index
	 *            Index where add dockable
	 */
	private void add( Dockable dockable, int index ){
		DockUtilities.ensureTreeValidity(this, dockable);
		DockUtilities.checkLayoutLocked();
		// Case where dockable is instance of ToolbarDockStation is handled by
		// the "ToolbarDockStationMerger"
		// Case where dockable is instance of ToolbarGroupDockStation is handled
		// by the "ToolbarStrategy.ensureToolbarLayer" method
		dockable = getToolbarStrategy().ensureToolbarLayer(this, dockable);
		DockHierarchyLock.Token token = DockHierarchyLock.acquireLinking(this,
				dockable);
		try{
			listeners.fireDockableAdding(dockable);
			dockable.setDockParent(this);
			if (dockable instanceof OrientedDockStation){
				if (getOrientation() != null){
					// it would be possible that this station was not already
					// oriented. This is the case when this station is
					// instantiated but not drop in any station (e.g.
					// ToolbarContainerDockStation) which could give it an
					// orientation
					((OrientedDockStation) dockable)
							.setOrientation(getOrientation());
				}
			}
			dockables.add(index, dockable);
			basePanel.getContentPane().add(dockable.getComponent(), index);
			basePanel.getContentPane().setBounds(0, 0,
					basePanel.getContentPane().getPreferredSize().width,
					basePanel.getContentPane().getPreferredSize().height);
			basePanel.setPreferredSize(new Dimension(basePanel.getContentPane()
					.getPreferredSize().width, basePanel.getContentPane()
					.getPreferredSize().height));
			basePanel.getContentPane().revalidate();
			basePanel.getContentPane().repaint();
			listeners.fireDockableAdded(dockable);
			fireDockablesRepositioned(index + 1);
		} finally{
			token.release();
		}
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
	private void remove( Dockable dockable ){
		int index = this.indexOf(dockable);
		if (index >= 0)
			this.remove(index);
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
	private void remove( int index ){
		DockUtilities.checkLayoutLocked();
		Dockable dockable = this.getDockable(index);
		if (getFrontDockable() == dockable)
			setFrontDockable(null);

		DockHierarchyLock.Token token = DockHierarchyLock.acquireUnlinking(
				this, dockable);
		try{
			listeners.fireDockableRemoving(dockable);
			dockable.setDockParent(null);
			dockables.remove(index);
			basePanel.getContentPane().remove(dockable.getComponent());
			basePanel.getContentPane().setBounds(0, 0,
					basePanel.getContentPane().getPreferredSize().width,
					basePanel.getContentPane().getPreferredSize().height);
			basePanel.setPreferredSize(new Dimension(basePanel.getContentPane()
					.getPreferredSize().width, basePanel.getContentPane()
					.getPreferredSize().height));
			basePanel.getContentPane().revalidate();
			basePanel.getContentPane().repaint();
			listeners.fireDockableRemoved(dockable);
		} finally{
			token.release();
		}
		fireDockablesRepositioned(index);
	}

	@Override
	public void setOrientation( Orientation orientation ){
		switch (orientation) {
		case VERTICAL:
			this.basePanel.getContentPane()
					.setLayout(
							new BoxLayout(basePanel.getContentPane(),
									BoxLayout.Y_AXIS));
			basePanel.getContentPane().setBounds(0, 0,
					basePanel.getContentPane().getPreferredSize().width,
					basePanel.getContentPane().getPreferredSize().height);
			basePanel.setPreferredSize(new Dimension(basePanel.getContentPane()
					.getPreferredSize().width, basePanel.getContentPane()
					.getPreferredSize().height));
			break;
		case HORIZONTAL:
			this.basePanel.getContentPane()
					.setLayout(
							new BoxLayout(basePanel.getContentPane(),
									BoxLayout.X_AXIS));
			basePanel.getContentPane().setBounds(0, 0,
					basePanel.getContentPane().getPreferredSize().width,
					basePanel.getContentPane().getPreferredSize().height);
			basePanel.setPreferredSize(new Dimension(basePanel.getContentPane()
					.getPreferredSize().width, basePanel.getContentPane()
					.getPreferredSize().height));
			break;
		}
		for (Dockable d : dockables){
			if (d instanceof OrientedDockStation){
				OrientedDockStation group = (OrientedDockStation) d;
				group.setOrientation(orientation);
			}
		}
		basePanel.getContentPane().revalidate();
		basePanel.getContentPane().repaint();
		this.orientation = orientation;
	}

	@Override
	public Orientation getOrientation(){
		return orientation;
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
		 * Generated serial number
		 */
		private static final long serialVersionUID = -4399008463139189130L;
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
			content.setBorder(new TitledBorder(ToolbarDockStation.this
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
			DefaultStationPaintValue paint = getPaint();
			if (indexBeneathMouse != null){
				Rectangle rect = dockables.get(indexBeneathMouse).getComponent()
						.getBounds();
				if (rect != null){
					switch (ToolbarDockStation.this.getOrientation()) {
					case VERTICAL:
						int y;
						if (sideBeneathMouse == Position.NORTH){
							y = rect.y;
						} else{
							y = rect.y + rect.height;
						}
						paint.drawInsertionLine(g, rect.x, y, rect.x
								+ rect.width, y);
						break;
					case HORIZONTAL:
						int x;
						if (sideBeneathMouse == Position.WEST){
							x = rect.x;
						} else{
							x = rect.x + rect.width;
						}
						paint.drawInsertionLine(g, x, rect.y, x, rect.y
								+ rect.height);
						break;
					}
				}
			}
		}

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
