package bibliothek.gui.dock.common.mode;

import bibliothek.gui.dock.common.intern.CDockable;

public interface NormalModeArea {

	/**
	 * Tells whether <code>dockable</code> is a child of this
	 * station and in a form satisfying the normal-mode criteria.
	 * @param dockable some potential child
	 * @return <code>true</code> if <code>dockable</code> is a child
	 * in normal mode.
	 */
	public boolean isNormalModeChild( CDockable dockable );
}
