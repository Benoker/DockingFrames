package bibliothek.help.javadoc;

import com.sun.javadoc.FieldDoc;

import bibliothek.help.model.Entry;

public class EntryableField extends AbstractEntryable {
    private FieldDoc doc;
    
    public EntryableField( FieldDoc doc ){
        this.doc = doc;
        
        bold( true );
        println( "Containing class:" );
        bold( false );
        linkln( doc.containingClass().qualifiedName(), "class", doc.containingClass().qualifiedName()  );
        println();
        bold( true );
        println( "Name:" );
        bold( false );
        print( doc.modifiers() );
        print( " " );
        print( doc.type() );
        print( " " );
        println( doc.name() );
        
        if( doc.commentText() != null ){
            println();
            bold( true );
            println( "Comment:" );
            bold( false );
            println( doc.commentText() );
        }
    }
    
    public Entry toEntry() {
        return new Entry( "field", doc.qualifiedName(), content(),
                "class:"+doc.containingClass().qualifiedName() );
    }

}
