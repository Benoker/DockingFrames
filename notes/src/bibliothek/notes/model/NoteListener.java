package bibliothek.notes.model;


public interface NoteListener {
	public void textChanged( Note note );
	public void titleChanged( Note note );
	public void iconChanged( Note note );
	public void colorChanged( Note note );
}
