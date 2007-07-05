package bibliothek.notes.model;

public interface NoteModelListener {
	public void noteAdded( NoteModel model, Note note );
	public void noteRemoved( NoteModel model, Note note );
}
