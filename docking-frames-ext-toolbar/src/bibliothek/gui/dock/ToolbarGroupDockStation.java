package bibliothek.gui.dock;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Insets;
import java.awt.Point;
import java.awt.Rectangle;
import java.io.IOException;
import java.util.ArrayList;

import javax.swing.BoxLayout;
import javax.swing.JPanel;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.EtchedBorder;

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
import bibliothek.gui.dock.station.toolbar.ReferencePoint;
import bibliothek.gui.dock.station.toolbar.ToolbarDropInfo;
import bibliothek.gui.dock.station.toolbar.ToolbarProperty;
import bibliothek.gui.dock.station.toolbar.ToolbarStrategy;
import bibliothek.gui.dock.themes.DefaultStationPaintValue;
import bibliothek.gui.dock.themes.ThemeManager;
import bibliothek.gui.dock.util.DockUtilities;
import bibliothek.gui.dock.util.SilentPropertyValue;

/**
 * A {@link Dockable} and a {@link Dockstation} which stands for a group of
 * {@link ComponentDockable}. As dockable it can be put in {@link DockStation}
 * which implements marker interface {@link ToolbarInterface}. As DockStation it
 * accept a {@link ComponentDockable} or a {@link ToolbarGroupDockStation}
 * 
 * @author Herve Guillaume
 */
public class ToolbarGroupDockStation extends AbstractDockableStation implements
		OrientedDockStation, ToolbarInterface, ToolbarElementInterface{

	/**
	 * The graphical representation of this station: the pane which contains
	 * component
	 */
	private Background background = new Background();
	/** A list of all children */
	private ArrayList<Dockable> dockables = new ArrayList<Dockable>();
	/**
	 * Graphical orientation of the group of components (vertical or horizontal)
	 */
	private Orientation orientation = Orientation.VERTICAL;

	/** A paint to draw lines */
	private DefaultStationPaintValue paint;
	
	/** where a drag dockable have to be inserted */
	private Integer dropIndex = null;


	/**
	 * Constructs a new ToolbarGroupDockStation
	 */
	public ToolbarGroupDockStation(){
		background = new Background();
		paint = new DefaultStationPaintValue(ThemeManager.STATION_PAINT
				+ ".toolbar", this);
		// background.setLayout(new BoxLayout(background, BoxLayout.Y_AXIS));
		// background.setBorder(new CompoundBorder(new EtchedBorder(),
		// new EmptyBorder(new Insets(5, 5, 5, 5))));
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
			return new ToolbarDropInfo<ToolbarGroupDockStation>(dockable, this,
					mouseX, mouseY){
				@Override
				public void execute(){
					drop(this);
				}

				@Override
				public void destroy(){
					System.out.println(this.toString() + "## destroy() ##");
					// sans cette ligne la barre n'est pa affiché à moins de
					// drager un autre composant
					ToolbarGroupDockStation.this.dropIndex = null;
					ToolbarGroupDockStation.this.background.getContentPane()
							.repaint();
				}

				@Override
				public void draw(){
					System.out.println(this.toString() + "## draw() ##");
					// sans cette ligne la barre n'est jamais affichée
					if (this.isMove()){
						ToolbarGroupDockStation.this.dropIndex = this
								.getIndex(ReferencePoint.UPPERLEFT);
					} else{
						ToolbarGroupDockStation.this.dropIndex = this
								.getIndex(ReferencePoint.BOTTOMRIGHT);
					}
					// sans cette ligne la bare n'est affiché que sur le premier
					// composant rencontré
					ToolbarGroupDockStation.this.background.getContentPane()
							.repaint();
				}
			};
		} else{
			return null;
		}
	}

	private void drop( ToolbarDropInfo<?> dropInfo ){
		if (dropInfo.isMove()){
			move(dropInfo.getItem(),
					dropInfo.getIndex(ReferencePoint.UPPERLEFT));
		} else{
			drop(dropInfo.getItem(),
					dropInfo.getIndex(ReferencePoint.BOTTOMRIGHT));
		}
	}

	@Override
	public void move( Dockable dockable, DockableProperty property ){
		// TODO pending
	}

	private void move( Dockable dockable, int indexWhereInsert ){
		System.out.println(this.toString() + "## move() ##");
		System.out.println("Index move: " + indexWhereInsert);

		DockController controller = getController();
		try{
			if (controller != null){
				controller.freezeLayout();
			}

			this.remove(dockable);
			// Warning we remove a dockable before insert it again
			if (indexWhereInsert == 0){
				this.add((ComponentDockable) dockable, indexWhereInsert);
			} else{
				this.add((ComponentDockable) dockable, indexWhereInsert - 1);
			}
		} finally{
			if (controller != null){
				controller.meltLayout();
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

	public boolean drop( Dockable dockable, int index ){
		// note: merging of two ToolbarGroupDockStations is done by the
		// ToolbarGroupDockStationMerger
		System.out.println(this.toString()
				+ "## drop(Dockable dockable, int index)##");
		if (this.accept(dockable)){
			this.add(dockable, index);
			return true;
		}
		return false;
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
		// System.out.println("Index :" + index);
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
		// the child is a ComponentDockable because canReplace()
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
		System.out.println(this.toString() + "## getFactoryID() ##");
		// Todo LATER
		return null;
	}

	@Override
	public Component getComponent(){
		return background;
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
		return getToolbarStrategy().isToolbarGroupPart(child);
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
	 * Insert one dockable at the index
	 * 
	 * @param dockable
	 *            Dockable to add
	 * @param index
	 *            Index where add dockable
	 */
	private void add( Dockable dockable, int index ){
		DockUtilities.ensureTreeValidity(this, dockable);
		DockUtilities.checkLayoutLocked();
		DockHierarchyLock.Token token = DockHierarchyLock.acquireLinking(this,
				dockable);
		try{
			listeners.fireDockableAdding(dockable);
			dockable.setDockParent(this);
			dockables.add(index, dockable);
			// background.add(dockable.getComponent(), index);
			// background.revalidate();
			// background.repaint();
			background.getContentPane().add(dockable.getComponent(), index);
			background.getContentPane().setBounds(0, 0,
					background.getContentPane().getPreferredSize().width,
					background.getContentPane().getPreferredSize().height);
			background.setPreferredSize(new Dimension(background
					.getContentPane().getPreferredSize().width, background
					.getContentPane().getPreferredSize().height));
			background.getContentPane().revalidate();
			background.getContentPane().repaint();
			listeners.fireDockableAdded(dockable);
			fireDockablesRepositioned(index + 1);
		} finally{
			token.release();
		}
	}

	/**
	 * Removes <code>dockable</code> from this station.<br>
	 * Note: clients may need to invoke {@link DockController#freezeLayout()}
	 * and {@link DockController#meltLayout()} to ensure noone else adds or
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
			background.getContentPane().remove(dockable.getComponent());
			background.getContentPane().setBounds(0, 0,
					background.getContentPane().getPreferredSize().width,
					background.getContentPane().getPreferredSize().height);
			background.setPreferredSize(new Dimension(background
					.getContentPane().getPreferredSize().width, background
					.getContentPane().getPreferredSize().height));
			// handle.setTitle(null);
			// dockable.removeDockableListener(dockableListener);
			// race condition, only required if not called from the EDT
			// buttonPane.resetTitles();
			background.getContentPane().revalidate();
			background.getContentPane().repaint();
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
			this.background.getContentPane()
					.setLayout(
							new BoxLayout(background.getContentPane(),
									BoxLayout.Y_AXIS));
			background.getContentPane().setBounds(0, 0,
					background.getContentPane().getPreferredSize().width,
					background.getContentPane().getPreferredSize().height);
			background.setPreferredSize(new Dimension(background
					.getContentPane().getPreferredSize().width, background
					.getContentPane().getPreferredSize().height));
			break;
		case HORIZONTAL:
			this.background.getContentPane()
					.setLayout(
							new BoxLayout(background.getContentPane(),
									BoxLayout.X_AXIS));
			background.getContentPane().setBounds(0, 0,
					background.getContentPane().getPreferredSize().width,
					background.getContentPane().getPreferredSize().height);
			background.setPreferredSize(new Dimension(background
					.getContentPane().getPreferredSize().width, background
					.getContentPane().getPreferredSize().height));
			break;
		}
		background.getContentPane().revalidate();
		background.getContentPane().repaint();
		this.orientation = orientation;
	}

	@Override
	public Orientation getOrientation(){
		return orientation;
	}

	/**
	 * This panel is used as base of the station. All children of the station
	 * have this panel as parent too.
	 * 
	 * @author Benjamin Sigg
	 */
	protected class Background extends OverpaintablePanel{
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
		public Background(){
			// content.setBackground(panelBackground);
			// content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));
			content.setBorder(new CompoundBorder(new EtchedBorder(),
					new EmptyBorder(new Insets(5, 5, 5, 5))));
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
			// getBasePane().removeAll();
			// getBasePane().setLayout(new BorderLayout());
			// getBasePane().add(content, BorderLayout.CENTER);
		}

		@Override
		protected void paintOverlay( Graphics g ){
			System.out.println(this.toString()
					+ "## paintOverlay(Graphics g) ##");
			DefaultStationPaintValue paint = getPaint();
			// if (dropInfo != null){
			// System.out.println("	NOT NULL DROP INFO");
			//
			// int index;
			// // compute rectangle of component
			// if (dropInfo.isMove()){
			// index = dropInfo.getIndex(ReferencePoint.UPPERLEFT);
			// if (index >= ToolbarGroupDockStation.this.dockables.size()){
			// index = ToolbarGroupDockStation.this.dockables.size() - 1;
			// }
			// } else{
			// index = dropInfo.getIndex(ReferencePoint.BOTTOMRIGHT);
			// }
			// Rectangle rect = dockables.get(index).getComponent()
			// .getBounds();
			// if (rect != null){
			// if (g == null){
			// System.out.println("	NULL GRAPHICS");
			// }
			// paint.drawInsertionLine(g, rect.x, rect.y, rect.x
			// + rect.width, rect.y + rect.height);
			// }
			//
			// }

			if (dropIndex != null){
				System.out.println(this.toString() + "			NOT NULL");
				// if (dropInfo.isMove()){
				Rectangle rect = dockables.get(dropIndex).getComponent()
						.getBounds();
				if (rect != null){
					paint.drawInsertionLine(g, rect.x, rect.y, rect.x
							+ rect.width, rect.y + rect.height);
				}
				// }
			} else{
				System.out.println(this.toString()
						+ "			NULL NULL NULL NULL NULL NULL");
			}

			// if (dropIndex != null){
			// Rectangle bounds = new Rectangle(0, 0, getWidth(), getHeight());
			// Rectangle insert = null;
			// if (getDockableCount() < 2)
			// insert = bounds;
			// else{
			// int index = stackComponent.getSelectedIndex();
			// if (index >= 0){
			// Component front = dockables.dockables().get(index)
			// .getDisplayer().getComponent();
			// Point location = new Point(0, 0);
			// location = SwingUtilities.convertPoint(front, location,
			// this);
			// insert = new Rectangle(location.x, location.y,
			// front.getWidth(), front.getHeight());
			// }
			// }
			//
			// if (insert != null){
			// paint.drawInsertion(g, bounds, insert);
			// }
			// }

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
