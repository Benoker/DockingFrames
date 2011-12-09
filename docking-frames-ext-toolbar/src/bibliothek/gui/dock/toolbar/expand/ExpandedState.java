package bibliothek.gui.dock.toolbar.expand;

import bibliothek.gui.Dockable;

/**
 * Describes the state of a {@link Dockable} as seen by the
 * {@link ExpandableToolbarItemStrategyListener} and by
 * {@link ExpandableToolbarItem}.
 * 
 * @author Benjamin Sigg
 */
public enum ExpandedState{
	/** an expanded {@link Dockable} has the largest possible size */
	EXPANDED,
	/** a stretched {@link Dockable} has a medium size */
	STRETCHED,
	/** a shrunk {@link Dockable} has the smallest possible size */
	SHRUNK
}
