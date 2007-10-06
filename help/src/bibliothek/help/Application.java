package bibliothek.help;

/**
 * A class that starts this application as a stand-alone application 
 * expecting to have all possible access rights (that means no {@link SecurityManager}
 * installed).
 * @author Benjamin Sigg
 */
public class Application {
    /**
     * Starts the application.
     * @param args are ignored
     */
	public static void main( String[] args ){
		new Core( false, null ).startup();
	}
}
