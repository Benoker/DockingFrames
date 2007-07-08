package bibliothek.help.javadoc;

import com.sun.javadoc.ClassDoc;
import com.sun.javadoc.FieldDoc;

import bibliothek.help.model.Entry;

public class EntryableFieldList extends AbstractEntryable {
    private ClassDoc doc;
    
    public EntryableFieldList( ClassDoc doc ){
        this.doc = doc;
        
        for( FieldDoc field : doc.fields() ){
            print( field.type() );
            print( " " );
            italic( true );
            linkln( field.name(), "field", field.qualifiedName());
            italic( false );
            add( new EntryableField( field ) );
        }
    }
    
    public Entry toEntry() {
        return new Entry( "field-list", doc.qualifiedName(), content());
    }
}
