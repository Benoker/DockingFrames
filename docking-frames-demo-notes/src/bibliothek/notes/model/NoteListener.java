package bibliothek.notes.model;

/**
 * A listener to a {@link Note}.
 * @author Benjamin Sigg
 *
 */
public interface NoteListener {
	/**
	 * Called when the message of <code>note</code> has been changed.
	 * @param note the source of the event
	 * @see Note#getText()
	 */
	public void textChanged( Note note );
	
	/**
	 * Called when the short description of <code>note</code> has been changed.
	 * @param note the source of the event
	 * @see Note#getTitle()
	 */
	public void titleChanged( Note note );
	
	/**
	 * Called when the image of <code>note</code> has been changed.
	 * @param note the source of the event
	 * @see Note#getIcon()
	 */
	public void iconChanged( Note note );
	
	/**
	 * Called when the color of <code>note</code> has been changed.
	 * @param note the source of the event
	 * @see Note#getColor()
	 */
	public void colorChanged( Note note );
}
