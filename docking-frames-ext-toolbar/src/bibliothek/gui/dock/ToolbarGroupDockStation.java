package bibliothek.gui.dock;

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
import bibliothek.gui.dock.util.DockUtilities;
import bibliothek.util.Todo;
import bibliothek.util.Todo.Compatibility;
import bibliothek.util.Todo.Priority;
import bibliothek.util.Todo.Version;

/**
 * A {@link Dockable} and a {@link Dockstation} which stands for a group of
 * {@link ComponentDockable}. As dockable it can be put in {@link DockStation}
 * which implements marker interface {@link ToolbarInterface}. As DockStation it
 * accept a {@link ComponentDockable} or a {@link ToolbarGroupDockStation}
 * 
 * @author Herve Guillaume
 */
public class ToolbarGroupDockStation extends AbstractDockableStation implements OrientedDockStation, ToolbarInterface, ToolbarElementInterface {

	/**
	 * The graphical representation of this station: the pane which contains
	 * component
	 */
	private JPanel componentsPanel = new JPanel();
	/** A list of all children */
	private ArrayList<Dockable> dockables = new ArrayList<Dockable>();
	/**
	 * Graphical orientation of the group of components (vertical or horizontal)
	 */
	private Orientation orientation = Orientation.VERTICAL;
	
	/**
	 * Constructs a new ToolbarGroupDockStation
	 */
	public ToolbarGroupDockStation(){
		componentsPanel.setLayout( new BoxLayout( componentsPanel, BoxLayout.Y_AXIS ) );
		componentsPanel.setBorder( new CompoundBorder( new EtchedBorder(), new EmptyBorder( new Insets( 5, 5, 5, 5 ) ) ) );
	}

	@Override
	public int getDockableCount(){
		return dockables.size();
	}

	@Override
	public Dockable getDockable( int index ){
		return dockables.get( index );
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
			return new ToolbarDropInfo<ToolbarGroupDockStation>( dockable, this, mouseX, mouseY ){
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
		if( dropInfo.isMove() ){
			move( dropInfo.getItem(), dropInfo.getIndex( ReferencePoint.UPPERLEFT ));
		}
		else{
			drop( dropInfo.getItem(), dropInfo.getIndex( ReferencePoint.BOTTOMRIGHT ));
		}
	}
	
	@Override
	public void move( Dockable dockable, DockableProperty property ){
		// TODO pending
	}
	
	private void move( Dockable dockable, int indexWhereInsert ){
		System.out.println( this.toString() + "## move() ##" );
		System.out.println( "Index move: " + indexWhereInsert );
		
		DockController controller = getController();
		try{
			if( controller != null ){
				controller.freezeLayout();
			}
			
			this.remove( dockable );
			// Warning we remove a dockable before insert it again
			if( indexWhereInsert == 0 ) {
				this.add( (ComponentDockable) dockable, indexWhereInsert );
			}
			else {
				this.add( (ComponentDockable) dockable, indexWhereInsert - 1 );
			}
		}
		finally{
			if( controller != null ){
				controller.meltLayout();
			}
		}
	}

	@Override
	public void drop( Dockable dockable ){
		System.out.println( this.toString() + "## drop(Dockable dockable)##" );
		this.drop( dockable, dockables.size() );
	}

	@Override
	public boolean drop( Dockable dockable, DockableProperty property ){
		if( property instanceof ToolbarProperty ){
			ToolbarProperty toolbar = (ToolbarProperty)property;
			if( toolbar.getSuccessor() != null && toolbar.getIndex() < getDockableCount()){
				DockStation child = getDockable( toolbar.getIndex() ).asDockStation();
				if( child != null ){
					return child.drop( dockable, toolbar.getSuccessor() );
				}
			}
			return drop( dockable, Math.min( getDockableCount(), toolbar.getIndex() ));
		}
		
		return false;
	}

	@Todo( compatibility=Compatibility.COMPATIBLE, priority=Priority.MINOR, target=Version.VERSION_1_1_1,
			description="make use of the Merger interface")
	public boolean drop( Dockable dockable, int index ){
		System.out.println( this.toString() + "## drop(Dockable dockable, int index)##" );
		if( this.accept( dockable ) ) {
			int indexWhereInsert = index;
			if( dockable instanceof ToolbarGroupDockStation ) {
				// WARNING: if I don't do a copy of dockables, problem occurs.
				// Perhaps due to concurrent access to the dockable (drop in
				// goal area ==> drag in origin area)?
				
				int count = dockable.asDockStation().getDockableCount();
				ArrayList<ComponentDockable> insertDockables = new ArrayList<ComponentDockable>();
				for( int i = 0; i < count; i++ ) {
					insertDockables.add( (ComponentDockable) dockable.asDockStation().getDockable( i ) );
				}
				for( int i = 0; i < count; i++ ) {
					this.add( insertDockables.get( i ), indexWhereInsert );
					indexWhereInsert++;
				}
				return true;
			}
			else {
				// one ComponentDockable only is added
				this.add( (ComponentDockable) dockable, indexWhereInsert );
				return true;
			}
		}
		return false;
	}

	@Override
	public <D extends Dockable & DockStation> boolean isInOverrideZone( int x, int y, D invoker, Dockable drop ){
		return false;
	}

	@Override
	public boolean canDrag( Dockable dockable ){
		System.out.println( this.toString() + "## canDrag(Dockable dockable) ## " );
		return true;
	}

	@Override
	public void drag( Dockable dockable ){
		System.out.println( this.toString() + "## drag(Dockable dockable) ##" );
		if( dockable.getDockParent() != this )
			throw new IllegalArgumentException( "The dockable cannot be dragged, it is not child of this station." );
		int index = this.indexOf( dockable );
		// System.out.println("Index :" + index);
		if( index >= 0 ) {
			this.remove( index );
		}
	}

	@Override
	public boolean canReplace( Dockable old, Dockable next ){
		System.out.println( this.toString() + "## canReplace(Dockable old, Dockable next) ## " );
		if( old.getClass() == next.getClass() ) {
			return true;
		}
		else {
			return false;
		}
	}

	@Override
	public void replace( Dockable old, Dockable next ){
		System.out.println( this.toString() + "## replace(Dockable old, Dockable next) ## " );
		DockUtilities.checkLayoutLocked();
		DockController controller = getController();
		if( controller != null )
			controller.freezeLayout();
		int index = indexOf( old );
		remove( old );
		// the child is a ComponentDockable because canReplace()
		// ensure it
		add( (ComponentDockable) next, index );
		controller.meltLayout();
	}

	@Override
	public void replace( DockStation old, Dockable next ){
		System.out.println( this.toString() + "## replace(DockStation old, Dockable next) ## " );
		replace( old.asDockable(), next );

	}

	@Override
	public String getFactoryID(){
		System.out.println( this.toString() + "## getFactoryID() ##" );
		// Todo LATER
		return null;
	}

	@Override
	public Component getComponent(){
		return componentsPanel;
	}

	@Override
	protected void callDockUiUpdateTheme() throws IOException{
		// Todo LATER
	}

	@Override
	public boolean accept( Dockable child ){
		System.out.println( this.toString() + "## accept(Dockable child) ##" );
		if( child instanceof ComponentDockable || child instanceof ToolbarGroupDockStation )
			return true;
		return false;
	}

	@Override
	public boolean accept( DockStation station ){
		System.out.println( this.toString() + "## accept(DockStation station) ##" );
		if( station instanceof ToolbarInterface ) {
			return true;
		}
		else {
			return false;
		}
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
	 * Insert one dockable at the index
	 * 
	 * @param dockable
	 *            Dockable to add
	 * @param index
	 *            Index where add dockable
	 */
	private void add( ComponentDockable dockable, int index ){
		DockUtilities.ensureTreeValidity( this, dockable );
		DockUtilities.checkLayoutLocked();
		DockHierarchyLock.Token token = DockHierarchyLock.acquireLinking( this, dockable );
		try {
			listeners.fireDockableAdding( dockable );
			dockable.setDockParent( this );
			dockables.add( index, dockable );
			componentsPanel.add( dockable.getComponent(), index );
			componentsPanel.revalidate();
			componentsPanel.repaint();
			listeners.fireDockableAdded( dockable );
			fireDockablesRepositioned( index + 1 );
		}
		finally {
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
			componentsPanel.remove( dockable.getComponent() );
			// handle.setTitle(null);
			// dockable.removeDockableListener(dockableListener);
			// race condition, only required if not called from the EDT
			// buttonPane.resetTitles();
			componentsPanel.revalidate();
			componentsPanel.repaint();
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
			this.componentsPanel.setLayout( new BoxLayout( componentsPanel, BoxLayout.Y_AXIS ) );
			break;
		case HORIZONTAL:
			this.componentsPanel.setLayout( new BoxLayout( componentsPanel, BoxLayout.X_AXIS ) );
			break;
		}
		this.orientation = orientation;
	}

	@Override
	public Orientation getOrientation(){
		return orientation;
	}

}
