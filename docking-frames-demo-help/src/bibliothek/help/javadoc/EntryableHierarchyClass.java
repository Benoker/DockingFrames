package bibliothek.help.javadoc;

import java.util.HashSet;
import java.util.Set;

import bibliothek.help.model.Entry;

import com.sun.javadoc.ClassDoc;

/**
 * An {@link Entryable} that creates a tree showing the type-hierarchy
 * of a class or interface. The tree contains all classes and interfaces
 * which are extended or implemented by the starting class or interface.<br>
 * Some interfaces might occur more than once in the tree, because they
 * might be implemented by different classes and interfaces.
 * @author Benjamin Sigg
 *
 */
@Content(type="hierarchy-class", encoding=Content.Encoding.TREE)
public class EntryableHierarchyClass extends AbstractEntryable{
    /** the root of the tree */
    private ClassDoc doc;
    
    /**
     * Creates a new type-hierarchy.
     * @param doc the root of the tree
     */
    public EntryableHierarchyClass( ClassDoc doc ){
        this.doc = doc;
        putClass( doc, null );
    }
    
    /**
     * Writes the name of the class <code>doc</code> and collects all
     * children of the node <code>doc</code>.
     * @param doc a class
     * @param collecting a set that will be filled with each interface
     * that was found by this method.
     */
    private void putClass( ClassDoc doc, Set<String> collecting ){
        Set<String> done = new HashSet<String>();
        
        if( doc.superclass() != null )
            putClass( doc.superclass(), done );
        
        mode( "class", type( doc ), doc.name(), doc.qualifiedName() );
        for( ClassDoc inter : doc.interfaces() )
            putInterface( inter, done, collecting );
    }
    
    /**
     * Writes the name of the interface <code>doc</code> and collects all
     * children of the node <code>doc</code>.
     * @param doc an interface
     * @param done the names of interfaces that should not be recorded, if
     * <code>doc</code> is contained in <code>done</code>, nothing will happen.
     * <code>null</code> is read as empty set.
     * @param collecting a set into which each interface is written, that is
     * collected by this method. That means <code>doc</code> and the whole
     * subtree that has <code>doc</code> as root.
     */
    private void putInterface( ClassDoc doc, Set<String> done, Set<String> collecting ){
        if( done == null || !done.contains( doc.qualifiedName() )){
            if( collecting != null )
                collecting.add( doc.qualifiedName() );
            
            mode( "interface", doc.name(), doc.qualifiedTypeName() );
            ClassDoc[] subs = doc.interfaces();
            if( subs.length > 0 ){
                mode( "tree", "+" );
                for( ClassDoc sub : subs )
                    putInterface( sub, null, collecting );
                mode( "tree", "-" );
            }
        }
    }
    
    /**
     * Gets a character identifying the type of <code>doc</code>.
     * @param doc a class, interface, enum or an unknown type
     * @return a letter identifying the type of <code>doc</code>
     */
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
