package bibliothek.help.javadoc;

import java.lang.annotation.*;

import bibliothek.help.model.Entry;

/**
 * A description for {@link Entryable}s, telling what kind of
 * {@link Entry} it creates.
 * @author Benjamin Sigg
 */
@Retention(RetentionPolicy.SOURCE)
@Target({ElementType.TYPE})
@Documented
public @interface Content {
    /**
     * Describes in which way a String is encoded.
     * @author Benjamin Sigg
     *
     */
    public static enum Encoding{
        /** the encoding could be read by {@link Entry#toDocument(bibliothek.help.view.text.HelpDocument) }*/
        DOCUMENT,
        /** the encoding could be read by {@link Entry#toSubHierarchy() }*/
        TREE,
        /** the encoding is specified by the client */
        CUSTOM
    }
    
    /** 
     * the value of the property {@link Entry#getType() Entry.type} 
     */
    public String type();

    /**
     * How the {@link Entry#getContent() content} of the entry will be encoded.
     */
    public Encoding encoding();
}
