package bibliothek.gui.dock;

/**
 * A listener that can be added to an {@link AbstractToolbarDockStation}.
 * @author Benjamin Sigg
 */
public interface ToolbarDockStationListener {
	/**
	 * Called if {@link AbstractToolbarDockStation#expand()} was executed.
	 * @param station the source of the event
	 */
	public void expanded( AbstractToolbarDockStation station );
	
	/**
	 * Called if {@link AbstractToolbarDockStation#shrink()} was executed.
	 * @param station the source of the event
	 */
	public void shrunk( AbstractToolbarDockStation station );
}
