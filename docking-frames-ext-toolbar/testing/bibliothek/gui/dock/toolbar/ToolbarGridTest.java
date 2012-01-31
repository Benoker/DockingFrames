package bibliothek.gui.dock.toolbar;

import static org.junit.Assert.*;

import java.awt.Component;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import bibliothek.gui.DockStation;
import bibliothek.gui.Dockable;
import bibliothek.gui.dock.dockable.AbstractDockable;
import bibliothek.gui.dock.station.support.ConvertedPlaceholderListItem;
import bibliothek.gui.dock.station.support.PlaceholderList;
import bibliothek.gui.dock.station.support.PlaceholderListItem;
import bibliothek.gui.dock.station.support.PlaceholderMap;
import bibliothek.gui.dock.station.support.PlaceholderStrategy;
import bibliothek.gui.dock.station.support.PlaceholderStrategyListener;
import bibliothek.gui.dock.station.toolbar.group.ToolbarColumnModel;
import bibliothek.gui.dock.station.toolbar.layout.DockablePlaceholderToolbarGrid;
import bibliothek.gui.dock.station.toolbar.layout.GridPlaceholderList;
import bibliothek.gui.dock.station.toolbar.layout.GridPlaceholderList.Column;
import bibliothek.gui.dock.station.toolbar.layout.PlaceholderToolbarGridConverter;
import bibliothek.gui.dock.util.PropertyKey;
import bibliothek.gui.dock.util.icon.DockIcon;
import bibliothek.util.Path;

public class ToolbarGridTest {
	private TestStory story;

	@Before
	public void setup(){
		story = new TestStory();
	}

	@After
	public void clear(){
		story = null;
	}

	@Test
	public void testOneColumn(){
		story.insert( 0, 0, null );
		story.insert( 0, 1, null );
		story.insert( 0, 2, "a" );
		
		story.assertCell( 0, 0, null );
		story.assertCell( 0, 1, null );
		story.assertCell( 0, 2, "a" );
	}

	private TestGrid copyByReadWrite( TestGrid grid, Collection<Dockable> dockables ){
		Map<Integer, Dockable> idToItem = new HashMap<Integer, Dockable>();
		Map<Dockable, Integer> itemToId = new HashMap<Dockable, Integer>();

		int index = 0;
		for( Dockable item : dockables ) {
			idToItem.put( index, item );
			itemToId.put( item, index );
			index++;
		}

		PlaceholderMap map = grid.toMap( itemToId );
		TestGrid newGrid = new TestGrid();
		newGrid.fromMap( map, idToItem, new PlaceholderToolbarGridConverter<Dockable, ToolbarGridTest.TestItem>(){
			@Override
			public TestItem convert( Dockable dockable, ConvertedPlaceholderListItem item ){
				return new TestItem( (TestDockable) dockable );
			}

			@Override
			public void added( TestItem item ){
				// ignore
			}
		} );
		return newGrid;
	}

	private void assertEqualsGrid( TestGrid gridA, TestGrid gridB ){
		assertEquals( gridA.getColumnCount(), gridB.getColumnCount() );

		GridPlaceholderList<Dockable, DockStation, TestItem> listA = gridA.getGrid();
		GridPlaceholderList<Dockable, DockStation, TestItem> listB = gridB.getGrid();

		assertEquals( listA.list().size(), listB.list().size() );
		assertEquals( listA.dockables().size(), listB.dockables().size() );

		Iterator<GridPlaceholderList<Dockable, DockStation, TestItem>.Item> itemsA = listA.list().iterator();
		Iterator<GridPlaceholderList<Dockable, DockStation, TestItem>.Item> itemsB = listB.list().iterator();

		while( itemsA.hasNext() && itemsB.hasNext() ) {
			assertEqualsColumn( itemsA.next(), itemsB.next() );
		}
		assertEquals( itemsA.hasNext(), itemsB.hasNext() );
	}

	private void assertEqualsColumn( GridPlaceholderList<Dockable, DockStation, TestItem>.Item itemA, GridPlaceholderList<Dockable, DockStation, TestItem>.Item itemB ){
		assertEquals( itemA.isPlaceholder(), itemB.isPlaceholder() );
		assertEquals( itemA.getPlaceholderSet(), itemB.getPlaceholderSet() );

		Column<Dockable, DockStation, TestItem> columnA = itemA.getDockable();
		Column<Dockable, DockStation, TestItem> columnB = itemB.getDockable();

		if( columnA != null && columnB != null ) {
			PlaceholderList<Dockable, DockStation, TestItem> listA = columnA.getList();
			PlaceholderList<Dockable, DockStation, TestItem> listB = columnB.getList();

			assertEqualsList( listA, listB );
		}
	}

	private void assertEqualsList( PlaceholderList<Dockable, DockStation, TestItem> listA, PlaceholderList<Dockable, DockStation, TestItem> listB ){
		assertEquals( listA.list().size(), listB.list().size() );
		assertEquals( listA.dockables().size(), listB.dockables().size() );

		Iterator<PlaceholderList<Dockable, DockStation, TestItem>.Item> itemsA = listA.list().iterator();
		Iterator<PlaceholderList<Dockable, DockStation, TestItem>.Item> itemsB = listB.list().iterator();

		while( itemsA.hasNext() && itemsB.hasNext() ) {
			PlaceholderList<Dockable, DockStation, TestItem>.Item itemA = itemsA.next();
			PlaceholderList<Dockable, DockStation, TestItem>.Item itemB = itemsB.next();

			assertEquals( itemA.getPlaceholderSet(), itemB.getPlaceholderSet() );
			assertEquals( itemA.getDockable(), itemB.getDockable() );
		}

		assertEquals( itemsA.hasNext(), itemsB.hasNext() );
	}

	private class TestStory {
		private TestGrid grid = new TestGrid();
		private List<Dockable> dockables = new ArrayList<Dockable>();

		public TestStory(){
			grid.setStrategy( new TestPlaceholderStrategy() );
		}

		public void insert( int column, int row, String placeholder ){
			TestItem item = new TestItem( placeholder );
			grid.insert( column, row, item );
			dockables.add( item.asDockable() );
			check();
		}

		public void remove( int column, int row ){
			ToolbarColumnModel<TestItem> model = grid.getModel();
			TestItem item = model.getColumn( column ).getItem( row );
			grid.remove( item );
			check();
		}

		private void check(){
			assertEqualsGrid( grid, copyByReadWrite( grid, dockables ) );
		}

		public void assertCell( int column, int row, String placeholder ){
			ToolbarColumnModel<TestItem> model = grid.getModel();
			TestItem item = model.getColumn( column ).getItem( row );
			TestDockable dockable = (TestDockable) item.asDockable();
			assertNotNull( dockable );
			if( placeholder == null ) {
				assertNull( dockable.getId() );
			}
			else {
				assertEquals( placeholder, new Path( "test", dockable.getId() ) );
			}
		}
	}

	private static class TestPlaceholderStrategy implements PlaceholderStrategy {
		@Override
		public void install( DockStation station ){
			// ignore
		}

		@Override
		public void uninstall( DockStation station ){
			// ignore
		}

		@Override
		public void addListener( PlaceholderStrategyListener listener ){
			// ignore
		}

		@Override
		public void removeListener( PlaceholderStrategyListener listener ){
			// ignore
		}

		@Override
		public Path getPlaceholderFor( Dockable dockable ){
			if( dockable instanceof TestDockable ) {
				String id = ((TestDockable) dockable).getId();
				if( id != null ) {
					return new Path( "test", id );
				}
			}
			return null;
		}

		@Override
		public boolean isValidPlaceholder( Path placeholder ){
			return true;
		}

	}

	private static class TestDockable extends AbstractDockable {
		private String id;

		public TestDockable( String id ){
			super( PropertyKey.DOCKABLE_TITLE, PropertyKey.DOCKABLE_TOOLTIP );
			this.id = id;
		}

		@Override
		public Component getComponent(){
			return null;
		}

		@Override
		public DockStation asDockStation(){
			return null;
		}

		@Override
		public String getFactoryID(){
			return null;
		}

		@Override
		protected DockIcon createTitleIcon(){
			return null;
		}

		public String getId(){
			return id;
		}
	}

	private static class TestItem implements PlaceholderListItem<Dockable> {
		private Dockable dockable;

		public TestItem( String id ){
			dockable = new TestDockable( id );
		}

		public TestItem( TestDockable dockable ){
			this.dockable = dockable;
		}

		@Override
		public Dockable asDockable(){
			return dockable;
		}

		@Override
		public boolean equals( Object obj ){
			if( obj.getClass() != getClass() ) {
				return false;
			}
			return ((TestItem) obj).dockable == dockable;
		}
	}

	private static class TestGrid extends DockablePlaceholderToolbarGrid<TestItem> {
		private GridPlaceholderList<Dockable, DockStation, TestItem> grid;

		@Override
		protected GridPlaceholderList<Dockable, DockStation, TestItem> createGrid(){
			grid = super.createGrid();
			return grid;
		}

		public GridPlaceholderList<Dockable, DockStation, TestItem> getGrid(){
			return grid;
		}
	}
}
