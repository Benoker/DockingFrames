package bibliothek.help.javadoc;

import bibliothek.help.model.Entry;

import com.sun.javadoc.ClassDoc;
import com.sun.javadoc.PackageDoc;
import com.sun.javadoc.RootDoc;

public class EntryableClassList extends AbstractEntryable{
    private PackageDoc doc;
    
    public EntryableClassList( RootDoc doc ){
        for( ClassDoc clazz : doc.classes() ){
            linkln( clazz.name(), "class", clazz.qualifiedName() );
            add( new EntryableClass( clazz ) );
            add( new EntryableHierarchyClass( clazz ));
        }
    }
    
    public EntryableClassList( PackageDoc doc ){
        this.doc = doc;
        for( ClassDoc clazz : doc.allClasses() ){
            linkln( clazz.name(), "class", clazz.qualifiedName() );
        }        
    }

    public Entry toEntry() {
        if( doc == null )
            return new Entry( "class-list", ".all", "All classes", content());
        else
            return new Entry( "class-list", doc.name(), "Classes of " + doc.name(), content());
    }
}
