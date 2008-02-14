package bibliothek.gui.dock.control;

import java.awt.AWTEvent;
import java.awt.Toolkit;
import java.awt.event.AWTEventListener;
import java.awt.event.KeyEvent;

import bibliothek.gui.DockController;

/**
 * A {@link KeyboardController} that listens to all events through 
 * an {@link java.awt.event.AWTEventListener}.
 * @author Benjamin Sigg
 *
 */
public class DefaultKeyboardController extends KeyboardController {
	private AWTEventListener listener = new AWTEventListener(){
		public void eventDispatched( AWTEvent event ){
			if( event instanceof KeyEvent ){
				KeyEvent key = (KeyEvent)event;
				if( key.getID() == KeyEvent.KEY_PRESSED )
					fireKeyPressed( key );
				else if( key.getID() == KeyEvent.KEY_RELEASED )
					fireKeyReleased( key );
				else if( key.getID() == KeyEvent.KEY_TYPED )
					fireKeyTyped( key );
			}
		}
	};
	
	/**
	 * Creates a new controller
	 * @param controller the realm in which this controller operates
	 */
	public DefaultKeyboardController( DockController controller ){
		super( controller );
		
		try{
			Toolkit.getDefaultToolkit().addAWTEventListener( listener, AWTEvent.KEY_EVENT_MASK );
		}
		catch( SecurityException ex ){
			System.err.println( "Can't register AWTEventListener, support for global KeyEvents disabled" );
		}
	}

	@Override
	public void kill(){
		try{
			Toolkit.getDefaultToolkit().removeAWTEventListener( listener );
		}
		catch( SecurityException ex ){
			// ignore
		}
	}
}
