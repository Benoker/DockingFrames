package bibliothek.help.javadoc;

import bibliothek.help.model.Entry;

import com.sun.javadoc.PackageDoc;
import com.sun.javadoc.RootDoc;

public class EntryablePackageList extends AbstractEntryable {
    public EntryablePackageList( RootDoc root ){
        linkln( "All", "class-list", ".all" );
        add( new EntryableClassList( root ) );
        println();
        
        for( PackageDoc child : root.specifiedPackages() ){
            linkln( child.name(), "class-list", child.name() );
            add( new EntryableClassList( child ));
        }
    }
    
    public Entry toEntry() {
        return new Entry( "package-list", "root", "All packages", content(), "class-list:.all", "empty:" );
    }

}
