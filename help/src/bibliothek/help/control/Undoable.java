package bibliothek.help.control;

import bibliothek.help.model.Entry;

public interface Undoable {
	public Entry getCurrent();
	public void setCurrent( Entry entry );
}
