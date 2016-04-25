package bibliothek.help.javadoc;

import bibliothek.help.model.Entry;

/**
 * <code>Entryables</code> are used to analyze and transform some data that is
 * structured in a tree. They are organized in a tree, where each new 
 * level contains more detailed and local information about an element 
 * found in the original data. The method {@link #toEntry()} is used
 * to transform the <code>Entryable</code> into an {@link Entry}. 
 * <code>Entries</code> are no longer structured as tree, they are just
 * some text containing some links.
 * @author Benjamin Sigg
 *
 */
public interface Entryable {
    /**
     * Transforms this <code>Entryable</code> into an {@link Entry}.
     * @return the entry that may or may not be connected somehow to this
     * <code>Entryable</code>
     */
    public Entry toEntry();
    
    /**
     * Gets a set of other {@link Entryable}s. There must not be
     * any circle when this method is called on various <code>Entryable</code>s,
     * hence the <code>Entryable</code>s must be organized in a tree.
     * @return the children of this node of the tree
     */
    public Entryable[] children();
}
