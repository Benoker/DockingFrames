package tutorial.core.basics;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import javax.swing.BorderFactory;
import javax.swing.Timer;
import javax.swing.border.Border;

import tutorial.support.ColorDockable;
import tutorial.support.JTutorialFrame;
import tutorial.support.Tutorial;
import bibliothek.gui.DockController;
import bibliothek.gui.dock.FlapDockStation;
import bibliothek.gui.dock.ScreenDockStation;
import bibliothek.gui.dock.SplitDockStation;
import bibliothek.gui.dock.displayer.DisplayerDockBorder;
import bibliothek.gui.dock.station.split.SplitDockGrid;
import bibliothek.gui.dock.themes.ThemeManager;
import bibliothek.gui.dock.themes.basic.action.buttons.MiniButton;
import bibliothek.gui.dock.themes.border.BorderModifier;
import bibliothek.gui.dock.util.Priority;
import bibliothek.gui.dock.util.UIBridge;
import bibliothek.gui.dock.util.UIValue;

@Tutorial( id="BorderModifier", title="Border" )
public class BorderModifierExample {
	/* Clients can install "BorderModifiers" to modify any Border used by the framework. Basically a BorderModifier
	 * gets the original border, and can decide what to do with that border. The border may be replaced, it may
	 * be modified, or it may be just used as it is. */
	public static void main( String[] args ) throws IOException{
		/* setting up frame and controller as usual */
		JTutorialFrame frame = new JTutorialFrame( BorderModifierExample.class );
		
		DockController controller = new DockController();
		controller.setRootWindow( frame );
		frame.destroyOnClose( controller );
		

		/* We now install our modifier. In this example we create an UIBridge of type "AlteringBridge" to apply the
		 * border to only a subset of components. */
		final AlteringBridge bridge = new AlteringBridge();
		/* The priority "CLIENT" means that we override any other setting. The "kind" tells us that we will only
		 * receive listeners of type "DisplayerDockBorder". The "BORDER_MODIFIER_TYPE" ensure type safety, and
		 * "bridge" is the object we install. */
		controller.getThemeManager().publish( Priority.CLIENT, DisplayerDockBorder.KIND, ThemeManager.BORDER_MODIFIER_TYPE, bridge );
		
		/* We may also set a modifier directly using a key. The modifier is then applied to all places which use the key. */
		controller.getThemeManager().setBorderModifier( MiniButton.BORDER_KEY_MOUSE_OVER, new BorderModifier(){
			public Border modify( Border border ){
				return BorderFactory.createEtchedBorder( new Color( 150, 255, 150 ), new Color( 0, 150, 0 ) );
			}
		});
		
		/* As this application runs together with other applications, we have to make sure it
		 * is cleaned up if closed. */
		frame.runOnClose( new Runnable(){
			public void run(){
				bridge.destroy();
			}
		});
		
		/* And now we set up different DockStations and Dockables */
		SplitDockStation splitDockStation = new SplitDockStation();
		controller.add( splitDockStation );
		frame.add( splitDockStation );
		
		SplitDockGrid grid = new SplitDockGrid();
		
		grid.addDockable(  0,  0, 100, 20, new ColorDockable( "Red", Color.RED ));
		grid.addDockable(  0, 20,  30, 50, new ColorDockable( "Blue", Color.BLUE ));
		grid.addDockable(  0, 70,  30, 30, new ColorDockable( "Yellow", Color.YELLOW ));
		grid.addDockable( 30, 20,  80, 80, new ColorDockable( "White", Color.WHITE ));
		grid.addDockable( 30, 20,  80, 80, new ColorDockable( "Black", Color.BLACK ));
		
		splitDockStation.dropTree( grid.toTree() );
		
		FlapDockStation flapDockStation = new FlapDockStation();
		controller.add( flapDockStation );
		flapDockStation.add( new ColorDockable( "Green", Color.GREEN ));
		frame.add( flapDockStation.getComponent(), BorderLayout.NORTH );
		
		ScreenDockStation screenDockStation = new ScreenDockStation( controller.getRootWindowProvider() );
		controller.add( screenDockStation );
		
		/* Now we make all frames and windows visible. */
		frame.setVisible( true );
		screenDockStation.setShowing( true );
	}
	
	/* This "UIBridge" is responsible for installing our BorderModifier. In this example
	 * we create a Timer and alter the BorderModifier each second. This means that the 
	 * application blinks in various colors. It also shows that a client can alter the
	 * modifier at any time and the framework will react immediately. */
	private static class AlteringBridge implements UIBridge<BorderModifier, UIValue<BorderModifier>>, ActionListener{
		/* our first modifier creates a red border */
		private BorderModifier redModifier = new BorderModifier(){
			public Border modify( Border border ){
				return BorderFactory.createEtchedBorder( new Color( 255, 150, 150 ), new Color( 150, 0, 0 ) );
			}
		};
		/* our second modifier creates a blue border */
		private BorderModifier blueModifier = new BorderModifier(){
			public Border modify( Border border ){
				return BorderFactory.createEtchedBorder( new Color( 150, 150, 255 ), new Color( 0, 0, 150 ) );
			}
		};
		
		/* UIValues are listeners, and we use these listeners to inform the framework
		 * about new BorderModifiers to use */
		private Set<UIValue<BorderModifier>> listeners = new HashSet<UIValue<BorderModifier>>();
		
		/* The timer triggering a change of the modifier */
		private Timer timer;
		
		/* Whether to use the red or the blue borders */
		private boolean state = true;
		
		public AlteringBridge(){
			timer = new Timer( 1000, this );
			timer.start();
		}
		
		public void destroy(){
			timer.stop();
			timer.removeActionListener( this );
		}
		
		public void actionPerformed( ActionEvent e ){
			state = !state;
			
			/* For changing the modifier we just iterate over all listeners and install
			 * the new modifier with "set". */
			for( UIValue<BorderModifier> border : listeners ){
				if( state ){
					border.set( redModifier );
				}
				else{
					border.set( blueModifier );
				}
			}
		}
		
		/* Tells whether we should pay attention to some border. We only pay attention
		 * to those borders which are shown directly on a SplitDockStation.
		 * Note that we can cast uiValue to DisplayerDockBorder because of the restrictions
		 * we applied when "publishing" the bridge on line 50. */
		private boolean shouldManage( UIValue<BorderModifier> uiValue ){
			DisplayerDockBorder displayer = (DisplayerDockBorder)uiValue;
			return displayer.getDisplayer().getStation() instanceof SplitDockStation;
		}
		
		public void add( String id, UIValue<BorderModifier> uiValue ){
			if( shouldManage( uiValue )){
				listeners.add( (DisplayerDockBorder)uiValue );
			}
		}

		public void remove( String id, UIValue<BorderModifier> uiValue ){
			listeners.remove( uiValue );
		}

		/* This method may be called any time for installed listeners. */
		public void set( String id, BorderModifier value, UIValue<BorderModifier> uiValue ){
			if( shouldManage( uiValue )){
				if( state ){
					uiValue.set( redModifier );
				}
				else{
					uiValue.set( blueModifier );
				}
			}
			else{
				uiValue.set( value );
			}
		}
	}
}