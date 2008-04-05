package bibliothek.notes;

import java.awt.AWTEvent;
import java.awt.Toolkit;
import java.awt.event.AWTEventListener;
import java.awt.event.FocusEvent;

/**
 * The startup-class if this application should be treated
 * as a stand-alone, non-restricted application.
 * @author Benjamin Sigg
 *
 */
public class Application {
    /**
     * Entrypoint
     * @param args ignored
     */
	public static void main( String[] args ){
Toolkit.getDefaultToolkit().addAWTEventListener( new AWTEventListener(){
    public void eventDispatched( AWTEvent event ) {
         if( event instanceof FocusEvent ){   
             FocusEvent e = (FocusEvent)event;
             if( e.getID() == FocusEvent.FOCUS_GAINED ){
                 System.out.println( e.getComponent() );
             }
         }
    }
}, FocusEvent.FOCUS_EVENT_MASK );
	    
		Core core = new Core( false, null );
		core.startup();
	}
}
