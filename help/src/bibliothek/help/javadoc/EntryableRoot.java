package bibliothek.help.javadoc;

import com.sun.javadoc.RootDoc;

import bibliothek.help.model.Entry;

/**
 * An {@link Entryable} that acts as root of a tree of <code>Entryable</code>s.
 * This <code>Entryable</code> does not collect any data from the
 * javadoc, but gives other <code>Entryable</code>s the opportunity to
 * collect data.
 * @author Benjamin Sigg
 *
 */
@Content(type="empty",encoding=Content.Encoding.CUSTOM)
public class EntryableRoot extends AbstractEntryable {
    /**
     * Creates a new root.
     * @param doc the javadoc of a java-project
     */
	public EntryableRoot( RootDoc doc ){
		add( new EntryablePackageList( doc ));
	}
	
	public Entry toEntry(){
		return new Entry( "empty", "", "", "" );
	}
}
