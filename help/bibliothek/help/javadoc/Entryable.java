package bibliothek.help.javadoc;

import bibliothek.help.model.Entry;

public interface Entryable {
    public Entry toEntry();
    public Entryable[] children();
}
