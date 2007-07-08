package bibliothek.help.javadoc;

import java.text.Collator;
import java.util.Arrays;
import java.util.Comparator;

import bibliothek.help.model.Entry;

import com.sun.javadoc.ClassDoc;
import com.sun.javadoc.PackageDoc;
import com.sun.javadoc.RootDoc;

public class EntryableClassList extends AbstractEntryable{
    private PackageDoc doc;
    
    public EntryableClassList( RootDoc doc ){
        ClassDoc[] docs = doc.classes();
        sort( docs );
        for( ClassDoc clazz : docs ){
            linkln( clazz.name(), "class", clazz.qualifiedName() );
            add( new EntryableClass( clazz ) );
            add( new EntryableHierarchyClass( clazz ));
        }
    }
    
    public EntryableClassList( PackageDoc doc ){
        this.doc = doc;
        
        print( "Enums", doc.enums() );
        print( "Interfaces", doc.interfaces() );
        print( "Classes", doc.ordinaryClasses() );
        print( "Exceptions", doc.exceptions() );
        print( "Errors", doc.errors() );
    }
    
    private void print( String name, ClassDoc[] docs ){
        if( docs.length > 0 ){
            bold( true );
            print( name );
            bold( false );
            println();
            sort( docs );
            for( ClassDoc clazz : docs ){
                linkln( clazz.name(), "class", clazz.qualifiedName() );
            }
            println();
        }
    }

    private void sort( ClassDoc[] docs ){
        Arrays.sort( docs, new Comparator<ClassDoc>(){
            private Collator collator = Collator.getInstance();
            
            public int compare( ClassDoc o1, ClassDoc o2 ) {
                return collator.compare( o1.name(), o2.name() );
            }
        });
    }
    
    public Entry toEntry() {
        if( doc == null )
            return new Entry( "class-list", ".all", "All classes", content());
        else
            return new Entry( "class-list", doc.name(), "Classes of " + doc.name(), content());
    }
}
