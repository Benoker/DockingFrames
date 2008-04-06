package bibliothek.notes;


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
		Core core = new Core( false, null );
		core.startup();
	}
}
