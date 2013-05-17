package tutorial.common.basics;

import java.awt.Color;

import tutorial.support.ColorSingleCDockable;
import tutorial.support.JTutorialFrame;
import tutorial.support.Tutorial;
import bibliothek.extension.gui.dock.theme.EclipseTheme;
import bibliothek.extension.gui.dock.theme.eclipse.EclipseTabDockActionLocation;
import bibliothek.extension.gui.dock.theme.eclipse.EclipseTabStateInfo;
import bibliothek.gui.dock.common.CControl;
import bibliothek.gui.dock.common.CGrid;
import bibliothek.gui.dock.common.action.CAction;
import bibliothek.gui.dock.common.action.predefined.CCloseAction;
import bibliothek.gui.dock.common.theme.ThemeMap;
import bibliothek.gui.dock.common.theme.eclipse.CommonEclipseThemeConnector;


@Tutorial(title="Hide Close Action", id="HideCloseAction")
public class HideCloseActionExample {
	public static void main( String[] args ){
		/* What if the close action of CDockables should only be visible if on a selected
		 * tab in the EclipseTheme?
		 * 
		 *  The CommonEclipseThemeConnector offers a method that can be overriden to achieve this goal. 
		 * */
		
		/* As usual we need some frame... */
		JTutorialFrame frame = new JTutorialFrame( HideCloseActionExample.class );
		
		/* ... and a control */
		CControl control = new CControl( frame );
		
		/* We set the EclipseTheme and at the same time reconfigure the framework to use our
		 * customized EclipseThemeConnector */
		control.setTheme( ThemeMap.KEY_ECLIPSE_THEME );
		control.putProperty( EclipseTheme.THEME_CONNECTOR, new HidingEclipseThemeConnector( control ) );
		
		/* And now we just add some closeable dockables to the application */
		frame.add( control.getContentArea() );
		
		ColorSingleCDockable red = new ColorSingleCDockable( "Red", Color.RED );
		red.setCloseable( true );
		
		ColorSingleCDockable green = new ColorSingleCDockable( "Green", Color.GREEN );
		green.setCloseable( true );
		
		ColorSingleCDockable blue = new ColorSingleCDockable( "Blue", Color.BLUE );
		blue.setCloseable( true );
		
		CGrid grid = new CGrid( control );
		grid.add( 0, 0, 1, 1, red, green, blue );
		control.getContentArea().deploy( grid );
		
		frame.setVisible( true );
	}
	
	/* The EclipseThemeConnector is reponsible to fine tune the look and feel of the EclipseTheme */
	public static class HidingEclipseThemeConnector extends CommonEclipseThemeConnector{
		public HidingEclipseThemeConnector( CControl control ){
			super( control );
		}

		@Override
		protected EclipseTabDockActionLocation getLocation( CAction action, EclipseTabStateInfo tab ){
			if( action instanceof CCloseAction ){
				/* By redefining the behavior of the close-action, we can hide it if the tab
				 * is not selected */
				if( tab.isSelected() ){
					return EclipseTabDockActionLocation.TAB;
				}
				else{
					return EclipseTabDockActionLocation.HIDDEN;
				}
			}
			return super.getLocation( action, tab );
		}
	}
}
