package bibliothek.help.control;

import bibliothek.help.model.Entry;

/**
 * A view whose content can be asked and changed by the {@link URManager}.
 * @author Benjamin Sigg
 */
public interface Undoable {
    /**
     * Gets the <code>Entry</code> that is currently used by this <code>Undoable</code>.
     * @return the <code>Entry</code> or <code>null</code>
     */
	public Entry getCurrent();
	
	/**
	 * Sets the <code>Entry</code> that should be shown. This method will
	 * be called in case an <i>undo</i> or <i>redo</i>-event occurs.
	 * @param entry the new selection, might be <code>null</code>
	 */
	public void setCurrent( Entry entry );
}
