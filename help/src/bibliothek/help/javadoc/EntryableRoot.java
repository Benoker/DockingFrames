package bibliothek.help.javadoc;

import com.sun.javadoc.RootDoc;

import bibliothek.help.model.Entry;

public class EntryableRoot extends AbstractEntryable {
	public EntryableRoot( RootDoc doc ){
		add( new EntryablePackageList( doc ));
	}
	
	public Entry toEntry(){
		return new Entry( "empty", "", "", "" );
	}
}
