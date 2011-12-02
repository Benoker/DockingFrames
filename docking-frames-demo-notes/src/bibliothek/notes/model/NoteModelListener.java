package bibliothek.notes.model;

/**
 * An observer of a {@link NoteModel}. Gets informed whenever a {@link Note}
 * is added or removed from the model.
 * @author Benjamin Sigg
 */
public interface NoteModelListener {
	/**
	 * Called when <code>note</code> has been added to <code>model</code>.
	 * @param model the source of the event
	 * @param note the new Note
	 */
	public void noteAdded( NoteModel model, Note note );
	
	/**
	 * Called when <code>note</code> has been deleted from <code>model</code>.
	 * @param model the source of the event
	 * @param note the Note that has been removed 
	 */
	public void noteRemoved( NoteModel model, Note note );
}
