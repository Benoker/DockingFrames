package tutorial.core.guide;

import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Set;

import javax.swing.Icon;

import tutorial.support.JTutorialFrame;
import tutorial.support.TextDockable;
import tutorial.support.Tutorial;
import bibliothek.extension.gui.dock.theme.SmoothTheme;
import bibliothek.gui.DockController;
import bibliothek.gui.Dockable;
import bibliothek.gui.dock.SplitDockStation;
import bibliothek.gui.dock.action.ActionContentModifier;
import bibliothek.gui.dock.action.DefaultDockActionSource;
import bibliothek.gui.dock.action.DockAction;
import bibliothek.gui.dock.action.LocationHint;
import bibliothek.gui.dock.action.SelectableDockAction;
import bibliothek.gui.dock.action.actions.SelectableDockActionGroup;
import bibliothek.gui.dock.action.actions.SimpleButtonAction;
import bibliothek.gui.dock.action.actions.SimpleDropDownAction;
import bibliothek.gui.dock.action.actions.SimpleMenuAction;
import bibliothek.gui.dock.action.actions.SimpleSelectableAction;
import bibliothek.gui.dock.event.SelectableDockActionListener;
import bibliothek.gui.dock.themes.NoStackTheme;
import bibliothek.util.Colors;

@Tutorial(title="Actions", id="Actions")
public class ActionsExample {
	public static void main( String[] args ){
		/* You know what a toolbar is: a set of buttons located at the top of your application. In 
		 * some applications the contents of the toolbar change depending on what the user
		 * currently does.
		 * 
		 * In this framework there is the notion of a DockAction. DockActions are shown on the title
		 * of a Dockable and can be used to fulfill a similar role than buttons in a toolbar. 
		 * 
		 * There are various implementations of DockActions, this example shows some of them. It
		 * is possible to write custom actions, e.g. to show a JComboBox in the title, but that
		 * will be explained in another example. */
		
		/* Setting up frame, station, controller, ... as usual */
		JTutorialFrame frame = new JTutorialFrame( ActionsExample.class );
		DockController controller = new DockController();
		controller.setRootWindow( frame );
		controller.setTheme( new NoStackTheme( new SmoothTheme() ));
		frame.destroyOnClose( controller );
		
		SplitDockStation station = new SplitDockStation();
		controller.add( station );
		frame.add( station );
		
		/* We will print some text on this Dockable whenever we press some button. Also
		 * we add different DockActions to this Dockable */
		TextDockable dockable = new TextDockable( "Dockable" );
		station.drop( dockable );
		
		/* We cannot add actions directly to a Dockable, we add a group of actions. Any change
		 * to that group will immediately be reflected in the user interface.
		 * There are several sources for these group of actions and the framework must
		 * somehow order them. For this the LocationHint is used. In our case we tell the
		 * framework that the groups origin is a Dockable, and that the group should be displayed 
		 * on the left (compared to other groups).
		 * The general notion is that actions on the left side are used by only one or few Dockables,
		 * while the actions on the right side are used by many or all Dockables. */
		DefaultDockActionSource actions = new DefaultDockActionSource( new LocationHint( LocationHint.DOCKABLE, LocationHint.LEFT ));
		
		/* We start by creating a simple button. This action much behaves like a JButton */
		actions.add( setupButtonAction( "Top Button", dockable ) );
		
		/* A separator is a line that visually separates some actions from each other. Basically a JSeparator. */
		actions.addSeparator();
		
		/* Now we add some radio-actions. These behave like a set of JRadioButtons. */
		setupRadioActions( actions, dockable );
		actions.addSeparator();
		
		/* A menu behaves like a JButton that opens a JPopupMenu if clicked */
		actions.add( setupMenuActions( dockable ) );
		
		/* A drop-down menu shows several other actions. If the user clicks on
		 * one of the actions the menu changes its icon and text and behaves like
		 * that action. */
		actions.add( setupDropDownMenu( dockable ) );
		actions.addSeparator();
		
		/* Finally we forward the group of actions to our Dockable */
		dockable.setActionOffers( actions );
		
		frame.setVisible( true );
	}
	
	private static DockAction setupButtonAction( String text, TextDockable target ){
		return setupButtonAction( text, target, Color.YELLOW );
	}
	
	private static DockAction setupButtonAction( final String text, final TextDockable target, Color color ){
		/* Creating a button: the SimpleButtonAction is used similar to a JButton */
		SimpleButtonAction button = new SimpleButtonAction();
		button.setText( text );
		button.setIcon( new OvalIcon( color ) );
		button.setIcon( ActionContentModifier.NONE_HOVER, new OvalIcon( Colors.darker( color, 0.1 ) ) );
		button.setIcon( ActionContentModifier.NONE_PRESSED, new OvalIcon( Colors.darker( color, 0.2 ) ) );
		
		button.addActionListener( new ActionListener(){
			public void actionPerformed( ActionEvent e ){
				target.appendText( "You clicked button '" + text + "'\n" );
			}
		});
		
		return button;
	}
	
	private static void setupRadioActions( DefaultDockActionSource source, TextDockable target ){
		/* SimpleSelectableAction.Radio behaves like a JRadioButton. */
		SimpleSelectableAction radio1 = new SimpleSelectableAction.Radio();
		SimpleSelectableAction radio2 = new SimpleSelectableAction.Radio();
		SimpleSelectableAction radio3 = new SimpleSelectableAction.Radio();
		
		/* Several radio actions can be grouped with a SelectableDockActionGroup.
		 * Only one button in a group can be selected */
		SelectableDockActionGroup group = new SelectableDockActionGroup();
		setupRadioAction( 1, radio1, group, target );
		setupRadioAction( 2, radio2, group, target );
		setupRadioAction( 3, radio3, group, target );
		
		source.add( radio1, radio2, radio3 );
	}
	
	private static void setupRadioAction( final int index, final SimpleSelectableAction radio, SelectableDockActionGroup group, final TextDockable target ){
		group.addAction( radio );
		radio.setText( "Radio " + index );
		radio.setTooltip( "This is radio-button Nr. " + index );
		
		radio.setIcon( new OvalIcon( Color.RED ));
		radio.setIcon( ActionContentModifier.NONE_HOVER, new OvalIcon( new Color( 255, 150, 150 ) ) );
		radio.setIcon( ActionContentModifier.NONE_PRESSED, new OvalIcon( new Color( 255, 200, 200 ) ) );
		radio.setSelectedIcon( new OvalIcon( Color.GREEN ));
		radio.setSelectedIcon( ActionContentModifier.NONE_HOVER, new OvalIcon( new Color( 150, 255, 150 ) ) );
		radio.setSelectedIcon( ActionContentModifier.NONE_PRESSED, new OvalIcon( new Color( 200, 255, 200 ) ) );
		
		radio.addSelectableListener( new SelectableDockActionListener(){
			/* A DockAction may be used by more than one Dockable. Hence this listener 
			 * gets a set containing all the Dockables which are affected by the new
			 * selection state of the action. */
			public void selectedChanged( SelectableDockAction action, Set<Dockable> dockables ){
				target.appendText( "Selected " + index + ": " + radio.isSelected() + "\n" );
			}
		});
	}
	
	private static DockAction setupMenuActions( TextDockable target ){
		/* A menu shows a group of actions, hence we need another
		 * DefaultDockActionSource to collect the actions */
		DefaultDockActionSource actions = new DefaultDockActionSource();
		/* We just add some simple buttons */
		actions.add( setupButtonAction( "Menu Button 1", target ) );
		actions.add( setupButtonAction( "Menu Button 2", target ) );
		actions.add( setupButtonAction( "Menu Button 3", target ) );
		
		SimpleMenuAction menu = new SimpleMenuAction( actions );
		menu.setIcon( new OvalIcon( Color.WHITE ));
		menu.setText( "Menu" );
		
		return menu;
	}
	
	private static DockAction setupDropDownMenu( TextDockable target ){
		SimpleDropDownAction menu = new SimpleDropDownAction();
		
		/* A drop-down menu offers methods to add actions directly. */
		menu.add( setupButtonAction( "Drop Down Button 1", target, Color.RED ) );
		menu.add( setupButtonAction( "Drop Down Button 2", target, Color.GREEN ) );
		menu.add( setupButtonAction( "Drop Down Button 3", target, new Color( 100, 100, 255 ) ) );
		
		/* The default behavior of SimpleDropDownAction is to replace
		 * icon and text if one of its actions is called.
		 * If you do not like the behavior:
		 *  - Call "menu.setFilter" to set a filter that tells the action what to show
		 *  - Check the methods offered by "SimpleDropDownItemAction", most of the
		 *    actions shown in this example extend that class. */
		menu.setIcon( new OvalIcon( Color.WHITE ));
		menu.setText( "Dropdown" );
		
		return menu;
	}
	
	/* An icon to ensure that our actions are visible */
	private static class OvalIcon implements Icon{
		private Color color;
		
		public OvalIcon( Color color ){
			this.color = color;
		}
		
		public int getIconHeight(){
			return 16;
		}

		public int getIconWidth(){
			return 16;
		}

		public void paintIcon( Component c, Graphics g, int x, int y ){
			int w = getIconWidth();
			int h = getIconHeight();
			
			g.setColor( color );
			g.fillOval( x, y, w, h );
			
			g.setColor( Color.BLACK );
			g.drawOval( x, y, w, h );
			
			g.fillRect( x + w/4, y+h/4, w/6, h/4 );
			g.fillRect( x + w - w/4 - w/6, y+h/4, w/6, h/4 );
			
			g.drawLine( x+w/4, y+h/2+h/6, x+w/4+w/6, y+h/2+h/3 );
			g.drawLine( x+w/4+w/6, y+h/2+h/3, x+w-w/4-w/6, y+h/2+h/3 );
			g.drawLine( x+w-w/4, y+h/2+h/6, x+w-w/4-w/6, y+h/2+h/3 );
		}
		
	}
}
