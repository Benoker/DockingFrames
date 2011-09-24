/**
 * 
 */
package bibliothek.gui.dock;

import java.awt.Color;
import java.awt.Component;
import java.awt.Insets;
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
import bibliothek.gui.dock.station.StationDropOperation;
import bibliothek.gui.dock.station.support.PlaceholderMap;
import bibliothek.gui.dock.station.toolbar.ReferencePoint;
import bibliothek.gui.dock.station.toolbar.ToolbarDropInfo;
import bibliothek.gui.dock.station.toolbar.ToolbarProperty;
import bibliothek.gui.dock.station.toolbar.ToolbarStrategy;
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
public class ToolbarDockStation extends AbstractDockableStation implements OrientedDockStation, ToolbarInterface, ToolbarElementInterface {

	/**
	 * The graphical representation of this station: the pane which contains
	 * component
	 */
	private JPanel groupComponentsPanel = new JPanel();
	/** A list of all children */
	private ArrayList<Dockable> dockables = new ArrayList<Dockable>();

	/**
	 * Graphical orientation of the group of components (vertical or horizontal)
	 */
	private Orientation orientation = Orientation.VERTICAL;

	/**
	 * Constructs a new ToolbarDockStation
	 */
	public ToolbarDockStation(){
		groupComponentsPanel.setLayout( new BoxLayout( groupComponentsPanel, BoxLayout.Y_AXIS ) );
		groupComponentsPanel.setBorder( new CompoundBorder( new EtchedBorder(), new EmptyBorder( new Insets( 5, 5, 5, 5 ) ) ) );
		groupComponentsPanel.setBackground( new Color( 255, 255, 128 ) );
	}

	@Override
	public int getDockableCount(){
		return dockables.size();
	}

	@Override
	public Dockable getDockable( int index ){
		return dockables.get( index );
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
		int index = dockables.indexOf( child );
		return new ToolbarProperty( index, null );
	}

	@Override
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
			return new ToolbarDropInfo<ToolbarDockStation>( dockable, this, mouseX, mouseY ){
				@Override
				public void execute(){
					drop( this );
				}
			};
		}
		else {
			return null;
		}
	}

	private void drop( ToolbarDropInfo<?> dropInfo ){
		if( dropInfo.isMove() ) {
			move( dropInfo.getItem(), dropInfo.getIndex( ReferencePoint.UPPERLEFT ) );
		}
		else {
			drop( dropInfo.getItem(), dropInfo.getIndex( ReferencePoint.BOTTOMRIGHT ) );
		}
	}

	@Override
	public void drop( Dockable dockable ){
		System.out.println( this.toString() + "## drop(Dockable dockable)##" );
		this.drop( dockable, dockables.size() );
	}

	private void move( Dockable dockable, int indexWhereInsert ){
		System.out.println( this.toString() + "## move() ##" );
		System.out.println( "Index move: " + indexWhereInsert );

		DockController controller = getController();

		try {
			if( controller != null ) {
				controller.freezeLayout();
			}

			this.remove( dockable );
			// Warning we remove a dockable before insert it again
			if( indexWhereInsert == 0 ) {
				this.add( (ToolbarGroupDockStation) dockable, indexWhereInsert );
			}
			else {
				this.add( (ToolbarGroupDockStation) dockable, indexWhereInsert - 1 );
			}
		}
		finally {
			if( controller != null ) {
				controller.meltLayout();
			}
		}
	}

	@Override
	public boolean drop( Dockable dockable, DockableProperty property ){
		if( property instanceof ToolbarProperty ) {
			ToolbarProperty toolbar = (ToolbarProperty) property;
			if( toolbar.getSuccessor() != null && toolbar.getIndex() < getDockableCount() ) {
				DockStation child = getDockable( toolbar.getIndex() ).asDockStation();
				if( child != null ) {
					return child.drop( dockable, toolbar.getSuccessor() );
				}
			}
			return drop( dockable, Math.min( getDockableCount(), toolbar.getIndex() ) );
		}

		return false;
	}

	/**
	 * Dropps <code>dockable</code> at location <code>index</code>.
	 * @param dockable the element to add
	 * @param index the location of <code>dockable</code>
	 * @return whether the operation was succesfull or not
	 */
	public boolean drop( Dockable dockable, int index ){
		System.out.println( this.toString() + "## drop(Dockable dockable, int index)##" );
		if( this.accept( dockable ) ) {
			int indexWhereInsert = index;
			dockable = getToolbarStrategy().ensureToolbarLayer( this, dockable );
			if( dockable == null ) {
				return false;
			}
			add( dockable, indexWhereInsert );
		}

		return false;
	}

	@Override
	public void move( Dockable dockable, DockableProperty property ){
		// Todo LATER Auto-generated method stub
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
		int index = this.indexOf( dockable );
		if( index >= 0 ) {
			this.remove( index );
		}
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
		DockUtilities.checkLayoutLocked();
		DockController controller = getController();
		if( controller != null )
			controller.freezeLayout();
		int index = indexOf( old );
		remove( old );
		// the child is a ToolbarGroupDockStation because canReplace()
		// ensure it
		add( (ToolbarGroupDockStation) next, index );
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
		return groupComponentsPanel;
	}

	@Override
	protected void callDockUiUpdateTheme() throws IOException{
		// Todo LATER
	}

	/**
	 * Gets the {@link ToolbarStrategy} that is currently used by this station.
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
		return getToolbarStrategy().isToolbarPart( child );
	}

	@Override
	public boolean accept( DockStation station ){
		System.out.println( this.toString() + "## accept(DockStation station) ##" );
		return getToolbarStrategy().isToolbarGroupPartParent( station, this );
	}

	@Override
	public String toString(){
		return this.getClass().getSimpleName() + '@' + Integer.toHexString( this.hashCode() );
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
		for( Dockable currentDockable : dockables ) {
			if( currentDockable == dockable ) {
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
		DockUtilities.ensureTreeValidity( this, dockable );
		DockUtilities.checkLayoutLocked();
		
		// The first two cases should never happen: 
		// Case 1 is handled by the "ToolbarDockStationMerger"
		// Case 2 is handled by the "ToolbarStrategy.ensureToolbarLayer" method
		
//		if( dockable instanceof ToolbarDockStation ) {
//			// I do a copy of dockables first. If I don't do that, problems
//			// occur. Perhaps due to concurrent access to the dockable (drop
//			// in goal area ==> drag in origin area)?
//			int count = dockable.asDockStation().getDockableCount();
//			ArrayList<ToolbarGroupDockStation> insertDockables = new ArrayList<ToolbarGroupDockStation>();
//			for( int i = 0; i < count; i++ ) {
//				insertDockables.add( (ToolbarGroupDockStation) dockable.asDockStation().getDockable( i ) );
//			}
//			for( int i = 0; i < count; i++ ) {
//				group = insertDockables.get( i );
//				DockHierarchyLock.Token token = DockHierarchyLock.acquireLinking( this, dockable );
//				try {
//					listeners.fireDockableAdding( group );
//					group.setDockParent( this );
//					group.setOrientation( getOrientation() );
//					dockables.add( index, group );
//					groupComponentsPanel.add( group.getComponent(), index );
//					listeners.fireDockableAdded( group );
//					fireDockablesRepositioned( index + 1 );
//				}
//				finally {
//					token.release();
//				}
//			}
//		}
//		else if( dockable instanceof ToolbarGroupDockStation ) {
//			group = (ToolbarGroupDockStation) dockable;
//			DockHierarchyLock.Token token = DockHierarchyLock.acquireLinking( this, dockable );
//			try {
//				listeners.fireDockableAdding( group );
//				group.setDockParent( this );
//				group.setOrientation( getOrientation() );
//				dockables.add( index, group );
//				groupComponentsPanel.add( group.getComponent(), index );
//				listeners.fireDockableAdded( group );
//				fireDockablesRepositioned( index + 1 );
//			}
//			finally {
//				token.release();
//			}
//		}
//		else {
			dockable = getToolbarStrategy().ensureToolbarLayer( this, dockable );
			DockHierarchyLock.Token token = DockHierarchyLock.acquireLinking( this, dockable );
			try {
				listeners.fireDockableAdding( dockable );
				dockable.setDockParent( this );
				if( dockable instanceof OrientedDockStation ){
					((OrientedDockStation)dockable).setOrientation( getOrientation() );
				}
				dockables.add( index, dockable );
				groupComponentsPanel.add( dockable.getComponent(), index );
				listeners.fireDockableAdded( dockable );
				fireDockablesRepositioned( index + 1 );
			}
			finally {
				token.release();
			}
//		}
		groupComponentsPanel.revalidate();
		groupComponentsPanel.repaint();
		fireDockablesRepositioned( index + 1 );
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
		int index = this.indexOf( dockable );
		if( index >= 0 )
			this.remove( index );
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
		Dockable dockable = this.getDockable( index );
		if( getFrontDockable() == dockable )
			setFrontDockable( null );

		DockHierarchyLock.Token token = DockHierarchyLock.acquireUnlinking( this, dockable );
		try {
			listeners.fireDockableRemoving( dockable );
			dockable.setDockParent( null );
			dockables.remove( index );
			groupComponentsPanel.remove( dockable.getComponent() );
			groupComponentsPanel.revalidate();
			groupComponentsPanel.repaint();
			listeners.fireDockableRemoved( dockable );
		}
		finally {
			token.release();
		}
		fireDockablesRepositioned( index );
	}

	@Override
	public void setOrientation( Orientation orientation ){
		switch( orientation ){
			case VERTICAL:
				this.groupComponentsPanel.setLayout( new BoxLayout( groupComponentsPanel, BoxLayout.Y_AXIS ) );
				break;
			case HORIZONTAL:
				this.groupComponentsPanel.setLayout( new BoxLayout( groupComponentsPanel, BoxLayout.X_AXIS ) );
				break;
		}
		for( Dockable d : dockables ) {
			ToolbarGroupDockStation group = (ToolbarGroupDockStation) d;
			group.setOrientation( orientation );
		}
		this.orientation = orientation;
	}

	@Override
	public Orientation getOrientation(){
		return orientation;
	}

}
