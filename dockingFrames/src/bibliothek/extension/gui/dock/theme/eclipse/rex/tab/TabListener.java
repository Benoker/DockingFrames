package bibliothek.extension.gui.dock.theme.eclipse.rex.tab;

/**
 * @author Janni Kovacs
 */
public interface TabListener {

	public void tabRemoved(Tab t);

	public void tabChanged(Tab t);

	// NOTE: has been inserted for use in docking frames
	public void tabCloseIconClicked(Tab t);
}
