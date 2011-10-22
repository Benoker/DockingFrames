package bibliothek.gui.dock;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Insets;
import java.awt.Rectangle;
import java.io.IOException;
import java.util.Map;

import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.EtchedBorder;


import bibliothek.gui.DockController;
import bibliothek.gui.DockStation;
import bibliothek.gui.Dockable;
import bibliothek.gui.Orientation;
import bibliothek.gui.Position;
import bibliothek.gui.PositionedDockStation;
import bibliothek.gui.OrientedDockStation;
import bibliothek.gui.ToolbarElementInterface;
import bibliothek.gui.ToolbarInterface;
import bibliothek.gui.dock.layout.DockableProperty;
import bibliothek.gui.dock.station.AbstractDockableStation;
import bibliothek.gui.dock.station.OverpaintablePanel;
import bibliothek.gui.dock.station.StationDropOperation;
import bibliothek.gui.dock.station.StationPaint;
import bibliothek.gui.dock.station.support.ConvertedPlaceholderListItem;
import bibliothek.gui.dock.station.support.DockablePlaceholderList;
import bibliothek.gui.dock.station.support.PlaceholderList;
import bibliothek.gui.dock.station.support.PlaceholderListItemAdapter;
import bibliothek.gui.dock.station.support.PlaceholderListItemConverter;
import bibliothek.gui.dock.station.support.PlaceholderMap;
import bibliothek.gui.dock.station.support.PlaceholderStrategy;
import bibliothek.gui.dock.station.toolbar.ToolbarDockStationFactory;
import bibliothek.gui.dock.station.toolbar.ToolbarDockTitleRequest;
import bibliothek.gui.dock.station.toolbar.ToolbarDropInfo;
import bibliothek.gui.dock.station.toolbar.ToolbarProperty;
import bibliothek.gui.dock.station.toolbar.ToolbarStrategy;
import bibliothek.gui.dock.themes.DefaultStationPaintValue;
import bibliothek.gui.dock.themes.ThemeManager;
import bibliothek.gui.dock.title.DockTitle;
import bibliothek.gui.dock.title.DockTitleRequest;
import bibliothek.gui.dock.util.DockUtilities;
import bibliothek.gui.dock.util.PropertyValue;
import bibliothek.gui.dock.util.SilentPropertyValue;
import bibliothek.util.Path;

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
		PositionedDockStation, OrientedDockStation, ToolbarInterface,
		ToolbarElementInterface{

	/**
	 * The graphical representation of this station: the pane which contains
	 * component
	 */
	protected OverpaintablePanelBase mainPanel = new OverpaintablePanelBase();
	/** A list of all children */
	private DockablePlaceholderList<Dockable> dockables = new DockablePlaceholderList<Dockable>();
	/** Graphical position of the group on components (NORTH, SOUTH, WEST, EAST) */
	private Position position = Position.NORTH;
	/** A paint to draw lines */
	private DefaultStationPaintValue paint;
	/** the index of the closest dockable above the mouse */
	private Integer indexBeneathMouse = null;
	/** closest side of the the closest dockable above the mouse */
	private Position sideBeneathMouse = null;
	/** the request needed to display title */
	private ToolbarDockTitleRequest titleRequest = null;

	/** current {@link PlaceholderStrategy} */
	private PropertyValue<PlaceholderStrategy> placeholderStrategy = new PropertyValue<PlaceholderStrategy>(
			PlaceholderStrategy.PLACEHOLDER_STRATEGY){
		@Override
		protected void valueChanged( PlaceholderStrategy oldValue,
				PlaceholderStrategy newValue ){
			dockables.setStrategy(newValue);
		}
	};

	public void setTitleRequest( ToolbarDockTitleRequest titleRequest ){
		this.titleRequest = titleRequest;
	}
	
	public ToolbarDockTitleRequest getTitleRequest() {
		return this.titleRequest;
	}

	/**
	 * Constructs a new ToolbarDockStation
	 */
	public ToolbarDockStation(){
		mainPanel = new OverpaintablePanelBase();
		paint = new DefaultStationPaintValue(ThemeManager.STATION_PAINT
				+ ".toolbar", this);
		setPosition(this.position);
		// basePanel.setLayout( new BoxLayout( basePanel, BoxLayout.Y_AXIS ) );
		// basePanel.setBorder( new CompoundBorder( new EtchedBorder(), new
		// EmptyBorder( new Insets( 5, 5, 5, 5 ) ) ) );
		// basePanel.setBackground( new Color( 255, 255, 128 ) );
	}

	@Override
	public int getDockableCount(){
		return dockables.dockables().size();
	}

	@Override
	public Dockable getDockable( int index ){
		return dockables.dockables().get(index);
	}

	/**
	 * Grants direct access to the list of {@link Dockable}s, sublcasses should
	 * not modify the list unless the fire the appropriate events.
	 * 
	 * @return the list of dockables
	 */
	protected PlaceholderList.Filter<Dockable> getDockables(){
		return dockables.dockables();
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

		return dockables
				.toMap(new PlaceholderListItemAdapter<Dockable, Dockable>(){
	@Override
					public ConvertedPlaceholderListItem convert( int index,
							Dockable dockable ){
						Integer id = children.get(dockable);
						if (id == null){
							return null;
						}

						ConvertedPlaceholderListItem item = new ConvertedPlaceholderListItem();
						item.putInt("id", id);
						item.putInt("index", index);

						if (strategy != null){
							Path placeholder = strategy
									.getPlaceholderFor(dockable);
							if (placeholder != null){
								item.putString("placeholder",
										placeholder.toString());
								item.setPlaceholder(placeholder);
							}
						}

						return item;
					}
				});
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
	public void setPlaceholders( PlaceholderMap map,
			final Map<Integer, Dockable> children ){
		DockUtilities.checkLayoutLocked();
		if (getDockableCount() > 0){
			throw new IllegalStateException("must not have any children");
		}
		DockController controller = getController();

		try{
			if (controller != null){
				controller.freezeLayout();
			}

			DockablePlaceholderList<Dockable> next = new DockablePlaceholderList<Dockable>();

			if (getController() != null){
				dockables.setStrategy(null);
				dockables.unbind();
				dockables = next;
			} else{
				dockables = next;
			}

			next.read(map,
					new PlaceholderListItemAdapter<Dockable, Dockable>(){
						private DockHierarchyLock.Token token;
						private int index = 0;

						@Override
						public Dockable convert(
								ConvertedPlaceholderListItem item ){
							int id = item.getInt("id");
							Dockable dockable = children.get(id);
							if (dockable != null){
								DockUtilities.ensureTreeValidity(
										ToolbarDockStation.this, dockable);
								token = DockHierarchyLock.acquireLinking(
										ToolbarDockStation.this, dockable);
								listeners.fireDockableAdding(dockable);
								return dockable;
							}
							return null;
						}

						@Override
						public void added( Dockable dockable ){
							try{
								// this would be the correct place to create
								// DockTitle and similar stuff.

								insertAt(dockable, index++);
								listeners.fireDockableAdded(dockable);
							} finally{
								token.release();
							}
						}
					});

			if (getController() != null){
				dockables.bind();
				dockables.setStrategy(getPlaceholderStrategy());
			}
		} finally{
			if (controller != null){
				controller.meltLayout();
			}
		}
	}

	@Override
	public PlaceholderMap getPlaceholders(){
		return dockables.toMap();
	}

	@Override
	public void setPlaceholders( PlaceholderMap placeholders ){
		if (getDockableCount() > 0){
			throw new IllegalStateException(
					"only allowed if there are not children present");
	}

		try{
			DockablePlaceholderList<Dockable> next = new DockablePlaceholderList<Dockable>(
					placeholders);
			if (getController() != null){
				dockables.setStrategy(null);
				dockables.unbind();
				dockables = next;
				dockables.bind();
				dockables.setStrategy(getPlaceholderStrategy());
			} else{
				dockables = next;
			}
		} catch (IllegalArgumentException ex){
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
		placeholderStrategy.setValue(strategy);
	}

	@Override
	public DockableProperty getDockableProperty( Dockable child, Dockable target ){
		int index = indexOf( child );
		Path placeholder = null;
		PlaceholderStrategy strategy = getPlaceholderStrategy();
		if( strategy != null ){
			placeholder = strategy.getPlaceholderFor( target == null ? child : target );
			if( placeholder != null ){
				dockables.dockables().addPlaceholder( index, placeholder );
			}
		}
		return new ToolbarProperty( index, placeholder );
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
					ToolbarDockStation.this.mainPanel.repaint();
				}

				@Override
				public void draw(){
					// without this line, nothing is displayed
					ToolbarDockStation.this.indexBeneathMouse = indexOf(getDockableBeneathMouse());
					ToolbarDockStation.this.sideBeneathMouse = this
							.getSideDockableBeneathMouse();
					// without this line, line is displayed only on the first
					// component met
					ToolbarDockStation.this.mainPanel.repaint();
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
			// Note: Computation of index to insert drag dockable is not the
			// same
			// between a move() and a drop(), because with a move() it is as if
			// the
			// drag dockable were remove first then added again in the list
			// (Note: It's wird beacause indeed drag() is called after
			// move()...)
			int dropIndex;
			int indexBeneathMouse = indexOf(dropInfo.getDockableBeneathMouse());
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
		this.drop(dockable, getDockableCount());
	}

	@Override
	public boolean drop( Dockable dockable, DockableProperty property ){
		if (property instanceof ToolbarProperty){
			ToolbarProperty toolbar = (ToolbarProperty) property;
			
			boolean acceptable = acceptable( dockable );
			boolean result = false;
			int index = Math.min( getDockableCount(), toolbar.getIndex() );
			
			Path placeholder = toolbar.getPlaceholder();
			if( placeholder != null && toolbar.getSuccessor() != null ){
				Dockable preset = dockables.getDockableAt( placeholder );
				if( preset != null ){
					DockStation station = preset.asDockStation();
					if( station != null ){
						if( station.drop( dockable, toolbar.getSuccessor() )){
							dockables.removeAll( placeholder );
							result = true;
						}
					}
				}
			}
			
			if( !result && placeholder != null ){
				if( acceptable && dockables.hasPlaceholder( placeholder )){
					add( dockable, index, placeholder );
					result = true;
				}
			}
			
			if( !result && dockables.dockables().size() == 0 ){
				if( acceptable ){
					drop( dockable );
					result = true;
				}
			}
			
			if( !result ){
				if( index < dockables.dockables().size() && toolbar.getSuccessor() != null ){
					DockStation child = getDockable( index ).asDockStation();
					if (child != null){
						result = child.drop(dockable, toolbar.getSuccessor());
					}
				}
			}
			
			if( !result && acceptable ){
				result = drop( dockable, index );
			}
			
			return result;
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
		System.out.println(this.toString() + "## move() ## ==> Index: "
				+ indexWhereInsert);
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
		return ToolbarDockStationFactory.ID;
	}

	@Override
	public Component getComponent(){
		return mainPanel;
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
	public int indexOf( Dockable dockable ){
		int index = 0;
		for (Dockable currentDockable : dockables.dockables()){
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
		add( dockable, index, null );
	}
	private void add( Dockable dockable, int index, Path placeholder ){
		DockUtilities.ensureTreeValidity(this, dockable);
		DockUtilities.checkLayoutLocked();
		// Case where dockable is instance of ToolbarDockStation is handled by
		// the "ToolbarDockStationMerger"
		// Case where dockable is instance of ToolbarGroupDockStation is handled
		// by the "ToolbarStrategy.ensureToolbarLayer" method
		dockable = getToolbarStrategy().ensureToolbarLayer(this, dockable);
		DockHierarchyLock.Token token = DockHierarchyLock.acquireLinking(this, dockable);
		try{
			listeners.fireDockableAdding(dockable);			
			int inserted = -1;
			
			if( placeholder != null && dockables.getDockableAt( placeholder ) == null ){
				inserted = dockables.put( placeholder, dockable );
			}
			else if( placeholder != null ){
				index = dockables.getDockableIndex( placeholder );
			}
			
			if( inserted == -1 ){
				dockables.dockables().add( index, dockable );
			}
			else{
				index = inserted;
			}
			
			insertAt( dockable, index );
			listeners.fireDockableAdded(dockable);
			fireDockablesRepositioned(index + 1);
		} finally{
			token.release();
		}
	}

	private void insertAt( Dockable dockable, int index ){
			dockable.setDockParent(this);
			if (dockable instanceof PositionedDockStation){
				if (getPosition() != null){
					// it would be possible that this station was not already
					// positioned. This is the case when this station is
					// instantiated but not drop in any station (e.g.
					// ToolbarContainerDockStation) which could give it a
					// position
				((PositionedDockStation) dockable).setPosition(getPosition());
				}
			}
			mainPanel.getContentPane().add(dockable.getComponent(), index);
		mainPanel.getContentPane().setBounds(0, 0,
				mainPanel.getContentPane().getPreferredSize().width,
				mainPanel.getContentPane().getPreferredSize().height);
		mainPanel.setPreferredSize(new Dimension(mainPanel.getContentPane()
				.getPreferredSize().width, mainPanel.getContentPane()
				.getPreferredSize().height));
		mainPanel.doLayout();
		mainPanel.getContentPane().revalidate();
		mainPanel.getContentPane().repaint();
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
			mainPanel.getContentPane().remove(dockable.getComponent());
			mainPanel.doLayout();
			mainPanel.getContentPane().revalidate();
			mainPanel.getContentPane().repaint();
			listeners.fireDockableRemoved(dockable);
			fireDockablesRepositioned(index);
		} finally{
			token.release();
		}
	}

	@Override
	public Orientation getOrientation(){
		switch (position) {
		case NORTH:
		case SOUTH:
			return Orientation.HORIZONTAL;
		case WEST:
		case EAST:
			return Orientation.VERTICAL;
		case CENTER:
			return null;
		}
		throw new IllegalStateException();
	}

	@Override
	public void setOrientation( Orientation orientation ){
		// not supported: the orientation have to be dependant of the position
	}

	@Override
	public void setPosition( Position position ){
		System.out.println(this.toString()
				+ "## setPosition( Position position ) ## ==> " + position);
		this.position = position;
		// it's very important to change position and orientation of inside
		// dockables first, else doLayout() is done on wrong inside information
		for (Dockable d : dockables.dockables()){
			if (d instanceof PositionedDockStation){
				PositionedDockStation group = (PositionedDockStation) d;
				group.setPosition(this.getPosition());
			}
		}
		 this.mainPanel.doLayout();
	}

	@Override
	public Position getPosition(){
		return this.position;
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
		 * The content Pane of this {@link OverpaintablePanel} (with a
		 * BoxLayout)
		 */
		private JPanel contentPane = new SizeFixedPanel();
		/** This pane will contain a {@link DockTitle} (with a BoxLayout) */
		private JPanel titlePane = new SizeFixedPanel();

		/**
		 * This pane is the base of this OverpaintablePanel and contains both
		 * title and content panes (with a BoxLayout)
		 * A panel with a fixed size (minimum, maximum and preferred size are
		 * same values). Computation of the size are take insets into account.
		 * 
		 * @author Herve Guillaume
		 * 
		 */
		@SuppressWarnings("serial")
		private class SizeFixedPanel extends JPanel{
			@Override
			public Dimension getPreferredSize(){
				Dimension pref = super.getPreferredSize();
				Insets insets = getInsets();
				pref.height += insets.top + insets.bottom;
				pref.width += insets.left + insets.right;
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
		 * This pane is the base of this OverpaintablePanel and contains both
		 * title and content panes (with a BoxLayout)
		 */
		@SuppressWarnings("serial")
		private JPanel basePane = new SizeFixedPanel(){
			@Override
			public Dimension getPreferredSize(){
				Dimension titlePreferredSize = getTitlePane()
						.getPreferredSize();
				Dimension contentPreferredSize = getContentPane()
						.getPreferredSize();
				Dimension basePreferredSize = null;
				switch (ToolbarDockStation.this.getPosition()) {
				case NORTH:
				case SOUTH:
					basePreferredSize = new Dimension(
							contentPreferredSize.width
									+ titlePreferredSize.width,
							contentPreferredSize.height);
					break;
				case WEST:
				case EAST:
					basePreferredSize = new Dimension(
							contentPreferredSize.width,
							titlePreferredSize.height
									+ contentPreferredSize.height);
					break;
				case CENTER:
					basePreferredSize = this.getPreferredSize();
				}
				Insets insets = basePane.getInsets();
				basePreferredSize.height += insets.top + insets.bottom;
				basePreferredSize.width += insets.left + insets.right;
				return basePreferredSize;
			};
		};

		/**
		 * Creates a new panel
		 */
		public OverpaintablePanelBase(){
			basePane.setBorder(new CompoundBorder(new EtchedBorder(),
					new EmptyBorder(new Insets(5, 5, 5, 5))));
			if (ToolbarDockStation.this.getClass() == ToolbarGroupDockStation.class) {
			JLabel label = new JLabel("**");
			titlePane.add(label);
			titlePane.setBackground(Color.YELLOW);
			}
			basePane.add(titlePane);
			basePane.setBackground(Color.GREEN);
			contentPane.setBackground(Color.RED);
			basePane.add(contentPane);
			setBasePane(basePane);
			setContentPane(contentPane);
		}

		
		/**
		 * Gets the title pane which will hold a DockTitle 
		 * @return
		 */
		public JPanel getTitlePane(){
			return this.titlePane;
		}

		@Override
		public void doLayout(){
			System.out.println(this.toString() + "## doLayout() ##");
			updateAlignment();
			super.doLayout();
		}

		@Override
		public Dimension getPreferredSize(){
			return getBasePane().getPreferredSize();
		}

		@Override
		public Dimension getMinimumSize(){
			return this.getPreferredSize();
		}

		@Override
		public Dimension getMaximumSize(){
			return this.getPreferredSize();
		}

		/**
		 * Update alignment with regards to the current orientation of this
		 * {@linl ToolbarDockStation}
		 */
		private void updateAlignment(){
			if (ToolbarDockStation.this.getPosition() != null){
				switch (ToolbarDockStation.this.getPosition()) {
				case NORTH:
				case SOUTH:
					titlePane.setLayout(new BoxLayout(titlePane,
							BoxLayout.X_AXIS));
					contentPane.setLayout(new BoxLayout(contentPane,
							BoxLayout.X_AXIS));
					basePane.setLayout(new BoxLayout(basePane, BoxLayout.X_AXIS));
					titlePane.setAlignmentY(Component.CENTER_ALIGNMENT);
					contentPane.setAlignmentY(Component.CENTER_ALIGNMENT);
					basePane.setAlignmentY(Component.CENTER_ALIGNMENT);
					titlePane.setAlignmentX(Component.LEFT_ALIGNMENT);
					contentPane.setAlignmentX(Component.LEFT_ALIGNMENT);
					basePane.setAlignmentX(Component.LEFT_ALIGNMENT);
					break;
				case WEST:
				case EAST:
					titlePane.setLayout(new BoxLayout(titlePane,
							BoxLayout.Y_AXIS));
					contentPane.setLayout(new BoxLayout(contentPane,
							BoxLayout.Y_AXIS));
					basePane.setLayout(new BoxLayout(basePane, BoxLayout.Y_AXIS));
					titlePane.setAlignmentY(Component.TOP_ALIGNMENT);
					contentPane.setAlignmentY(Component.TOP_ALIGNMENT);
					basePane.setAlignmentY(Component.TOP_ALIGNMENT);
					titlePane.setAlignmentX(Component.CENTER_ALIGNMENT);
					contentPane.setAlignmentX(Component.CENTER_ALIGNMENT);
					basePane.setAlignmentX(Component.CENTER_ALIGNMENT);
					break;
				case CENTER:
					break;
				default:
					throw new IllegalArgumentException();
				}
			}
		}

		@Override
		protected void paintOverlay( Graphics g ){
			DefaultStationPaintValue paint = getPaint();
			if (indexBeneathMouse != null){
				Rectangle rect = dockables.dockables().get(indexBeneathMouse)
						.getComponent().getBounds();
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
		if (getController() != controller){
			if (getController() != null){
				dockables.unbind();
			}

		super.setController(controller);
		// if not set controller of the DefaultStationPaintValue, call to
		// DefaultStationPaintValue do nothing
		paint.setController(controller);
			placeholderStrategy.setProperties(controller);

			if (controller != null){
				dockables.bind();
						}
					}
	}

	public JPanel getTitlePane() {
		return this.mainPanel.getTitlePane();
	}

}
