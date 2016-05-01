package bibliothek.help.javadoc;

import java.text.Collator;
import java.util.Arrays;
import java.util.Comparator;

import bibliothek.help.model.Entry;

import com.sun.javadoc.ClassDoc;
import com.sun.javadoc.PackageDoc;
import com.sun.javadoc.RootDoc;

/**
 * An {@link Entryable} that creates a list of classes, interfaces, etc...
 * either of every element that has been documented, or of all elements
 * in a single package.
 * @author Benjamin Sigg
 *
 */
@Content( type="class-list", encoding=Content.Encoding.DOCUMENT)
public class EntryableClassList extends AbstractEntryable{
    /** the package whose elements are listed, might be <code>null</code> */
    private PackageDoc doc;
    
    /**
     * Creates a new list containing all elements.
     * @param doc the root of the documentation
     */
    public EntryableClassList( RootDoc doc ){
        ClassDoc[] docs = doc.classes();
        sort( docs );
        for( ClassDoc clazz : docs ){
            linkln( clazz.name(), "class", clazz.qualifiedName() );
            add( new EntryableClass( clazz ) );
            add( new EntryableHierarchyClass( clazz ));
        }
    }
    
    /**
     * Creates a new list containing the elements of a single package.
     * @param doc the package
     */
    public EntryableClassList( PackageDoc doc ){
        this.doc = doc;
        
        print( "Enums", doc.enums() );
        print( "Interfaces", doc.interfaces() );
        print( "Classes", doc.ordinaryClasses() );
        print( "Exceptions", doc.exceptions() );
        print( "Errors", doc.errors() );
    }
    
    /**
     * Prints a bold title <code>name</code> and then creates
     * links to each element found in <code>docs</code>. Does nothing
     * if <code>docs</code> is empty.
     * @param name the title of the next section
     * @param docs the elements to which a link is created.
     */
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

    /**
     * Sorts the elements of <code>docs</code> by their name using
     * the locale {@link Collator}.
     * @param docs the array that will be sorted
     */
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
