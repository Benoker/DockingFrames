package bibliothek.help.javadoc;

import java.text.Collator;
import java.util.Arrays;
import java.util.Comparator;

import bibliothek.help.model.Entry;

import com.sun.javadoc.PackageDoc;
import com.sun.javadoc.RootDoc;

/**
 * An {@link Entryable} that creates a list of all packages.
 * @author Benjamin Sigg
 *
 */
@Content(type="package-list",encoding=Content.Encoding.DOCUMENT)
public class EntryablePackageList extends AbstractEntryable {
    /**
     * Creates a new list of packages.
     * @param root the whole documentation of a java-project.
     */
    public EntryablePackageList( RootDoc root ){
        linkln( "All", "class-list", ".all" );
        add( new EntryableClassList( root ) );
        println();
        
        PackageDoc[] docs = root.specifiedPackages();
        Arrays.sort( docs, new Comparator<PackageDoc>(){
        	private Collator collator = Collator.getInstance();
        	
        	public int compare( PackageDoc o1, PackageDoc o2 ){
        		return collator.compare( o1.name(), o2.name() );
        	}
        });
        
        for( PackageDoc child : docs ){
            linkln( child.name(), "class-list", child.name() );
            add( new EntryableClassList( child ));
        }
    }
    
    public Entry toEntry() {
        return new Entry( "package-list", "root", "All packages", content(), "class-list:.all", "empty:" );
    }

}
