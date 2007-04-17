package bibliothek.gui.dock.action.actions;

import bibliothek.gui.dock.Dockable;

/**
 * This class can create keys for Dockables.
 * 
 * @author Benjamin Sigg
 *
 * @param <K> the type of key generate by this class
 */
public interface GroupKeyGenerator<K> {
	/**
	 * Generates a new key. Every value except <code>null</code> is a valid
	 * result.
	 * @param dockable the dockable for which a key is requested.
	 * @return the new key
	 */
	public K generateKey( Dockable dockable );
}
