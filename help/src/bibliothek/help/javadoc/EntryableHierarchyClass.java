package bibliothek.help.javadoc;

import java.util.HashSet;
import java.util.Set;

import bibliothek.help.model.Entry;

import com.sun.javadoc.ClassDoc;

public class EntryableHierarchyClass extends AbstractEntryable{
    private ClassDoc doc;
    
    public EntryableHierarchyClass( ClassDoc doc ){
        this.doc = doc;
        putClass( doc, null );
    }
    
    private void putClass( ClassDoc doc, Set<String> collecting ){
        Set<String> done = new HashSet<String>();
        
        if( doc.superclass() != null )
            putClass( doc.superclass(), done );
        
        mode( "class", type( doc ), doc.name(), doc.qualifiedName() );
        for( ClassDoc inter : doc.interfaces() )
            putInterface( inter, done, collecting );
    }
    
    private void putInterface( ClassDoc doc, Set<String> done, Set<String> collecting ){
        if( !done.contains( doc.qualifiedName() )){
            if( collecting != null )
                collecting.add( doc.qualifiedName() );
            
            mode( "interface", doc.name(), doc.qualifiedTypeName() );
            ClassDoc[] subs = doc.interfaces();
            if( subs.length > 0 ){
                mode( "tree", "+" );
                for( ClassDoc sub : subs )
                    putInterface( sub, done, collecting );
                mode( "tree", "-" );
            }
        }
    }
    
    private String type( ClassDoc doc ){
        if( doc.isInterface() )
            return "i";
        if( doc.isEnum() )
            return "e";
        if( doc.isClass() )
            return "c";
        return "?";
    }
    
    public Entry toEntry() {
        return new Entry( "hierarchy-class", doc.qualifiedName(), "Hierarchy of " + doc.qualifiedName(), content(), 
                "class:" + doc.qualifiedName() );
    }
}
