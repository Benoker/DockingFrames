package tutorial.common.basics;

import java.awt.BorderLayout;
import java.awt.Color;

import tutorial.support.ColorSingleCDockable;
import tutorial.support.JTutorialFrame;
import tutorial.support.Tutorial;
import bibliothek.gui.DockController;
import bibliothek.gui.DockStation;
import bibliothek.gui.Dockable;
import bibliothek.gui.dock.DockHierarchyLock;
import bibliothek.gui.dock.ScreenDockStation;
import bibliothek.gui.dock.SplitDockStation;
import bibliothek.gui.dock.accept.DockAcceptance;
import bibliothek.gui.dock.common.CControl;
import bibliothek.gui.dock.common.CGrid;
import bibliothek.gui.dock.common.CStation;
import bibliothek.gui.dock.common.action.predefined.CBlank;
import bibliothek.gui.dock.common.event.CDockableStateListener;
import bibliothek.gui.dock.common.intern.CDockable;
import bibliothek.gui.dock.common.intern.DefaultCDockable;
import bibliothek.gui.dock.common.intern.ui.CSingleParentRemover;
import bibliothek.gui.dock.common.mode.ExtendedMode;
import bibliothek.gui.dock.event.DockStationAdapter;
import bibliothek.gui.dock.station.LayoutLocked;
import bibliothek.gui.dock.title.NullTitleFactory;

@Tutorial(title="Splitting externalized Dockables", id="SplittingExternalized")
public class SplittingExternalizedDockablesExample {
	public static void main( String[] args ){
		/* In this example we will replace the default behavior of externalized CDockables: instead of being stacked,
		 * we will allow them to be split (as if they would be children of a CGridArea or CWorkingArea).
		 * 
		 * This will be a deep intrusion into the inner workings of the framework. We will not implement all features
		 * that would be required to fully support this kind of behavior, but it will be enough for more simple
		 * applications.
		 * 
		 * Be aware that this example reaches into places of the framework that may suddenly change their API, we are
		 * getting far away from the nice, public API. */
		
		/* Like in every example, we need a JFrame... */
		JTutorialFrame frame = new JTutorialFrame( SplittingExternalizedDockablesExample.class );
		
		/* ... and a controller */
		CControl control = new CControl( frame );
		
		/* We are going to make sure, that any Dockable that is externalized (= put onto a ScreenDockStation) is
		 * actually put onto a SplitDockStation that we create on the fly.
		 * 
		 * Usually the framework would throw away any DockStation that has 0 or 1 children. Here we are replacing
		 * this behavior with an algorithm that does not remove SplitDockStations that have 1 child and that were
		 * put on a ScreenDockStation. */
		control.getController().setSingleParentRemover( new SplitAwareSingleParentRemover( control ) );

		/* We are now accessing the ScreenDockStation itself (it always has the same unique identifier), and add
		 * a DockStationListener to it. The listener will be informed whenever a Dockable is put onto the 
		 * station, and we can replace the Dockable with a SplitDockStation. */
		CStation<?> screen = control.getStation( CControl.EXTERNALIZED_STATION_ID );
		screen.getStation().addDockStationListener( new SplitInserter( control ) );
		
		/* The last thing we want is a user stacking a Dockable with our floating SplitDockStations. 
		 * With this DockAcceptance we instruct the framework to never allow such an action to happen. */
		control.getController().addAcceptance( new CombiningPreventer() );
		
		/* A SplitDockStation usually does automatically create a "maximize" button, but CDockables already have on.
		 * We are hiding the "maximize" button of the CDockable if we are on a floating SplitDockStation. */
		control.addStateListener( new MaximizeButtonDisabler() );

		/* Finally we do not want the floating SplitDockStations to have a title. Since a ScreenDockStation does only
		 * have floating SplitDockStations as children, we can safely disable all titles. */
		control.getController().getDockTitleManager().registerClient( ScreenDockStation.TITLE_ID, new NullTitleFactory() );
		
		/* And the remaining part of the initialization is like in most of the other examples */
		frame.destroyOnClose( control );
		frame.add( control.getContentArea(), BorderLayout.CENTER );
		
		CGrid grid = new CGrid( control );
		grid.add( 0, 0, 1, 1, new ColorSingleCDockable( "red", Color.RED ) );
		grid.add( 1, 0, 1, 1, new ColorSingleCDockable( "green", Color.GREEN ) );
		grid.add( 0, 1, 1, 1, new ColorSingleCDockable( "blue", Color.BLUE ) );
		grid.add( 1, 1, 1, 1, new ColorSingleCDockable( "yellow", Color.YELLOW ) );
		control.getContentArea().deploy( grid );
		
		frame.setVisible( true );
	}
	
	/* This algorithm decides which DockStations should be automatically removed by the framework */
	private static class SplitAwareSingleParentRemover extends CSingleParentRemover{
		public SplitAwareSingleParentRemover( CControl control ){
			super( control );
		}

		@Override
		protected boolean shouldTest( DockStation station ){
			if( station instanceof SplitDockStation ){
				if( station.getDockableCount() == 1 ){
					if( station.asDockable().getDockParent() instanceof ScreenDockStation ){
						/* prevents removal of floating SplitDockStations that have only one child */
						return false;
					}
				}
			}
			return super.shouldTest( station );
		}
	}
	
	/* This listener replaces any Dockable that is added to a ScreenDockStation with a new SplitDockStation.
	 * 
	 * The framework has some built in sanity checks, warning developers when their application tries to do 
	 * bad stuff. One of the checks is to ensure, that the tree of Dockables and DockStations is never modified
	 * concurrently. And modifying the layout from within a DockStationListener usually is a sign that an application
	 * violates that condition.
	 * With @LayoutLocked we inform the framework that we know exactly what we are doing, and disable the warning. */
	@LayoutLocked(locked=false)
	public static class SplitInserter extends DockStationAdapter {
		private CControl control;
		
		public SplitInserter( CControl control ){
			this.control = control;
		}
		
		public void dockableAdded( DockStation station, final Dockable dockable ){
			if( !(dockable instanceof SplitDockStation) ) {
				DockHierarchyLock lock = control.getController().getHierarchyLock();

				/* This method is called while the tree is modified, if we would try to replace the Dockable right
				 * now the framework would raise an exception. 
				 * Instead we instruct the framework to replace the Dockable at the next possible opportunity. */
				lock.onRelease( new Runnable(){
					public void run(){
						checkAndReplace( dockable );
					}
				});
			}
		}
		
		private void checkAndReplace( Dockable dockable ){
			DockStation station = dockable.getDockParent();
			if( !(station instanceof ScreenDockStation) ) {
				// just some sanity checks - we do not expect this piece of code to be ever executed.
				return;
			}
			
			/* And now we just replace the new Dockable "dockable" with "split" */
			SplitDockStation split = new SplitDockStation();
			
			DockController controller = control.getController();
			
			try {
				/* disable events while rearanging our layout */
				controller.freezeLayout();
				
				station.replace( dockable, split );
				split.drop( dockable );
			}
			finally {
				/* and enable events after we finished */
				controller.meltLayout();
			}
		}
	};
	
	/* A DockAcceptance instructs the framework to ignore certain drag and drop operations... */
	private static class CombiningPreventer implements DockAcceptance{
		public boolean accept( DockStation parent, Dockable child ){
			return true;
		}
		
		public boolean accept( DockStation parent, Dockable child, Dockable next ){
			/* ... in this case we ignore any drag and drop operation that would put a floating
			 * SplitDockStation into a stack. */
			return !(parent instanceof ScreenDockStation);
		}
	}
	
	/* If a CDockable is in the externalized mode, then we hide the default "maximize" button. */
	public static class MaximizeButtonDisabler implements CDockableStateListener {
		public void visibilityChanged( CDockable cd ){
			// ignore
		}

		public void extendedModeChanged( CDockable cd, ExtendedMode mode ){
			if( cd instanceof DefaultCDockable ) {
				DefaultCDockable dockable = (DefaultCDockable) cd;
				if( mode.equals( ExtendedMode.EXTERNALIZED ) ) {
					dockable.putAction( CDockable.ACTION_KEY_MAXIMIZE, CBlank.BLANK );
				}
				else {
					dockable.putAction( CDockable.ACTION_KEY_MAXIMIZE, null );
				}
			}
		}
	}
}
