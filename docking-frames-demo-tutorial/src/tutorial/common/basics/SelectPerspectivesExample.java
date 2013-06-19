package tutorial.common.basics;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;

import tutorial.support.ColorSingleCDockable;
import tutorial.support.JTutorialFrame;
import tutorial.support.Tutorial;
import bibliothek.gui.dock.common.CControl;
import bibliothek.gui.dock.common.SingleCDockable;
import bibliothek.gui.dock.common.SingleCDockableFactory;
import bibliothek.gui.dock.common.perspective.CContentPerspective;
import bibliothek.gui.dock.common.perspective.CControlPerspective;
import bibliothek.gui.dock.common.perspective.CGridPerspective;
import bibliothek.gui.dock.common.perspective.CPerspective;
import bibliothek.gui.dock.common.perspective.CWorkingPerspective;
import bibliothek.gui.dock.common.perspective.SingleCDockablePerspective;
import bibliothek.util.filter.RegexFilter;

@Tutorial(id="SelectPerspectives", title="Handle perspectives")
public class SelectPerspectivesExample {
	public static void main( String[] args ){
		/* This application showcases how the perspective API can be used to load layouts. In fact
		 * this example shows two cases:
		 *  - using perspectives and the "load" and "save" methods to handle them. In this case the contents of the
		 *    working-area is never affected by the changes. If the user changed the layout, then these changes are
		 *    not lost.
		 *  - using "setPerspective" to override the current layout, including the working-area 
		 * 
		 */
		JTutorialFrame frame = new JTutorialFrame( SelectPerspectivesExample.class );
		final CControl control = new CControl( frame );
		
		/* we are going to automatically produce Dockables, with different colors */
		control.addSingleDockableFactory( new RegexFilter( "blue.*" ), new ColoredFactory( Color.BLUE ) );
		control.addSingleDockableFactory( new RegexFilter( "red.*" ), new ColoredFactory( Color.RED ) );
		
		/* we are going to introduce a CWorkingPerspective in the layout, this factory is need to convert
		 * the CWorkingPerspective into a CWorkingArea. Without this factory, the station would just be ignored. */
		control.addSingleDockableFactory( "work", new SingleCDockableFactory(){
			public SingleCDockable createBackup( String id ){
				return control.createWorkingArea( id );
			}
		} );
		
		frame.destroyOnClose( control );
		
		/* initializing the required stations before setting up the perspectives */
		frame.add( control.getContentArea() );
		
		/* creating some menus... */
		JMenuBar menubar = new JMenuBar();
		frame.setJMenuBar( menubar );
		
		JMenu saved = new JMenu( "Saved" );
		JMenu apply = new JMenu( "Apply" );
		menubar.add( saved );
		menubar.add( apply );
		
		/* ... for loading and storing layouts */
		saved.add( saveItem( control, new BlueSquareBuilder() ) );
		saved.add( saveItem( control, new RedStackBuilder() ) );
		
		/* ... or for overriding layouts */
		apply.add( applyItem( control, new BlueSquareBuilder() ) );
		apply.add( applyItem( control, new RedStackBuilder() ) );
		
		frame.setVisible( true );
	}
	
	private static JMenuItem saveItem( final CControl control, final PerspectiveBuilder builder ){
		/* Note how in this case the perspective is saved during startup of the application! */
		savePerspective( control.getPerspectives(), builder );
		JMenuItem item = new JMenuItem( builder.getName() );
		item.addActionListener( new ActionListener(){
			public void actionPerformed( ActionEvent e ){
				/* save the current layout... */
				control.save();
				/* ... and load a new one (or the same one) */
				control.load( builder.getName() );
			}
		} );
		return item;
	}
	
	private static JMenuItem applyItem( final CControl control, final PerspectiveBuilder builder ){
		JMenuItem item = new JMenuItem( builder.getName() );
		item.addActionListener( new ActionListener(){
			public void actionPerformed( ActionEvent e ){
				/* Note how in this case the perspectives are created at the exact moment when we need them. */
				applyPerspective( control.getPerspectives(), builder );
			}
		} );
		return item;
	}
	
	private static void savePerspective( CControlPerspective perspectives, PerspectiveBuilder builder ){
		CPerspective perspective = buildPerspective( perspectives, builder );

		/* this method just saves the layout, it can be applied with CControl.load */
		perspectives.setPerspective( builder.getName(), perspective );
	}
	
	private static void applyPerspective( CControlPerspective perspectives, PerspectiveBuilder builder ){
		CPerspective perspective = buildPerspective( perspectives, builder );
		
		/* this method applies the layout right now */
		perspectives.setPerspective( perspective, true );
	}
	
	/* The remaining methods are just building up the different perspectives */
	private static CPerspective buildPerspective( CControlPerspective perspectives, PerspectiveBuilder builder ){
		CPerspective perspective = perspectives.createEmptyPerspective();
		CContentPerspective content = perspective.getContentArea();
		CWorkingPerspective working = new  CWorkingPerspective( "work" );
		perspective.addStation( working );
		builder.build( perspective, content, working );
		return perspective;
	}
	
	private static class ColoredFactory implements SingleCDockableFactory{
		private Color color;
		
		public ColoredFactory( Color color ){
			this.color = color;
		}
		
		public SingleCDockable createBackup( String id ){
			int white = Integer.valueOf( id.split( " " )[1] );
			Color selected = new Color( add( color.getRed(), white ), add( color.getGreen(), white ), add( color.getBlue(), white ));
			return new ColorSingleCDockable( id, selected );
		}
		
		private int add( int color, int white ){
			return Math.min( 255, color + 20 * white );
		}
	}
	
	private interface PerspectiveBuilder{
		public String getName();
		
		public void build(CPerspective perspective, CContentPerspective content, CWorkingPerspective working);
	}
	
	private static class BlueSquareBuilder implements PerspectiveBuilder{
		public String getName(){
			return "Blue square";
		}
		
		public void build( CPerspective perspective, CContentPerspective content, CWorkingPerspective working ){
			CGridPerspective center = content.getCenter();
			
			center.gridAdd( 0, 0, 1, 1, working );
			center.gridAdd( 0, 1, 1, 1, new SingleCDockablePerspective( "blue 0" ) );
			center.gridAdd( 1, 0, 1, 1, new SingleCDockablePerspective( "blue 1" ) );
			center.gridAdd( 1, 1, 1, 1, new SingleCDockablePerspective( "blue 2" ) );
			center.gridDeploy();
			
			working.gridAdd( 0, 0, 1, 1, new SingleCDockablePerspective( "blue 3") );
			working.gridAdd( 0, 1, 1, 1, new SingleCDockablePerspective( "blue 4") );
			working.gridAdd( 1, 0, 1, 1, new SingleCDockablePerspective( "blue 5") );
			working.gridAdd( 1, 1, 1, 1, new SingleCDockablePerspective( "blue 6") );
			working.gridDeploy();
		}
	}
	
	private static class RedStackBuilder implements PerspectiveBuilder{
		public String getName(){
			return "Red stack";
		}
		
		public void build( CPerspective perspective, CContentPerspective content, CWorkingPerspective working ){
			CGridPerspective center = content.getCenter();
			
			center.gridAdd( 1, 0, 1, 2, working );
			center.gridAdd( 0, 0, 1, 1, new SingleCDockablePerspective( "red 0" ) );
			center.gridAdd( 0, 0, 1, 1, new SingleCDockablePerspective( "red 1" ) );
			center.gridAdd( 0, 1, 1, 1, new SingleCDockablePerspective( "red 2" ) );
			center.gridDeploy();
			
			working.gridAdd( 0, 0, 1, 1, new SingleCDockablePerspective( "red 3") );
			working.gridAdd( 0, 0, 1, 1, new SingleCDockablePerspective( "red 4") );
			working.gridAdd( 0, 1, 1, 1, new SingleCDockablePerspective( "red 5") );
			working.gridAdd( 0, 1, 1, 1, new SingleCDockablePerspective( "red 6") );
			working.gridDeploy();
		}
	}
}
