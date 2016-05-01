package bibliothek.help.model;

import java.util.*;

import bibliothek.help.view.text.HelpDocument;

/**
 * An <code>Entry</code> is some content that will be presented to the user.
 * Every <code>Entry</code> has fields for identification, a small
 * description (the title) and a set of links to other <code>Entries</code>
 * which contain more detailed or additional information to the content
 * of this <code>Entry</code>. 
 * @author Benjamin Sigg
 *
 */
public class Entry {
    /** an array of length 0 */
    private static final String[] EMPTY = new String[0];
    
    /**
     * The general type, tells who and how this {@link Entry} can be shown.
     * The exact meaning depends on the client.
     */
    private String type;
    /** an identifier, unique for all <code>Entries</code> with the same {@link #type} */
    private String id;
    /** a small description */
    private String title;
    /** the data that will be processed and presented to the user */
    private String content;
    /** links to other <code>Entries</code> */
    private String[] details;
    
    /**
     * Creates a new entry
     * @param type the type of the entry
     * @param id the id of the entry
     * @param title the title of this entry
     * @param content the text for the entry
     * @param details Links to other entries which should be shown when
     * this entry is shown. Each link should have the form <code>type:id</code>.
     */
    public Entry( String type, String id, String title, String content, String...details ) {
        super();
        this.type = type;
        this.id = id;
        this.title = title;
        this.content = content;
        
        if( details == null || details.length == 0 )
            details = EMPTY;
        
        this.details = details;
    }
    
    /**
     * Tells who and how this <code>Entry</code> will be presented to the
     * user. The exact meaning of this property depends on the client.
     * @return the general type
     */
    public String getType() {
        return type;
    }
    
    /**
     * Gets an identifier which is unique for all <code>Entries</code>
     * with the same {@link #getType() type}.
     * @return the unique id
     */
    public String getId() {
        return id;
    }
    
    /**
     * Gets a small description of this <code>Entry</code>.
     * @return a small text
     */
    public String getTitle(){
		return title;
	}
    
    /**
     * Gets the data that will be presented to the user. Some content
     * is encoded and must be processed further before presenting.
     * @return the content
     */
    public String getContent() {
        return content;
    }
    
    /**
     * Gets a set of links to other <code>Entries</code>. The other
     * <code>Entries</code> contain more information related to the
     * content of this <code>Entry</code>.
     * @return links of the form <code>type:id</code>
     */
    public String[] getDetails() {
        return details;
    }
    
    /**
     * Decodes the content of this <code>Entry</code> and produces a tree.
     * @return the tree
     */
    public HierarchyNode toSubHierarchy(){
        Token t;
        Reader reader = new Reader();
        
        LinkedList<Intermediate> classes = new LinkedList<Intermediate>();
        LinkedList<Intermediate> interfaceStack = new LinkedList<Intermediate>();
        
        Intermediate current = null;
        
        while( (t = reader.next()) != null ){
            if( t.mode ){
                if( t.content[0].equals( "class" )){
                    
                    current = new Intermediate();
                    current.type = t.content[1];
                    current.name = t.content[2];
                    if( !classes.isEmpty() )
                        current.children.add( classes.getLast() );
                    classes.add( current );
                }
                else if( t.content[0].equals( "interface" )){
                    Intermediate next = new Intermediate();
                    next.type = "i";
                    next.name = t.content[1];
                    if( interfaceStack.isEmpty() )
                        current.children.add( next );
                    else
                        interfaceStack.getLast().children.add( next );
                }
                else if( t.content[0].equals( "tree" )){
                    if( t.content[1].equals( "+" )){
                        if( interfaceStack.isEmpty() )
                            interfaceStack.addLast( current.children.get( current.children.size()-1 ) );
                        else
                            interfaceStack.addLast( interfaceStack.getLast().children.get( interfaceStack.getLast().children.size()-1 ) );
                    }
                    else if( t.content[1].equals( "-" )){
                        interfaceStack.removeLast();
                    }
                }
            }
        }
        
        if( classes.isEmpty() )
        	return null;
        
        return classes.getLast().toNode();
    }
    
    /**
     * A class used to create the resulting tree of {@link Entry#toSubHierarchy()}.
     * @author Benjamin Sigg
     */
    private static class Intermediate{
        /** children of this node */
        public List<Intermediate> children = new ArrayList<Intermediate>();
        /** the nodes type (interface, class, ...) */
        public String type;
        /** the text of the node */
        public String name;
        
        /**
         * Transforms this <code>Intermediate</code> in an {@link HierarchyNode},
         * transforms also the children of this node.
         * @return a tree with the content of this <code>Intermediate</code> as root
         */
        public HierarchyNode toNode(){
            HierarchyNode[] subs = new HierarchyNode[ children.size() ];
            for( int i = 0; i < subs.length; i++ )
                subs[i] = children.get( i ).toNode();
            
            return new HierarchyNode( name, type, subs );
        }
    }
    
    /**
     * Tries to read the content of this Entry as document.
     * @param destination the document to write into, can be <code>null</code>
     * @return the document that was written
     */
    public HelpDocument toDocument( HelpDocument destination ){
        if( destination == null )
            destination = new HelpDocument();
        
        Set<String> modes = new HashSet<String>();
        Reader reader = new Reader();
        Token token;
        
        while( (token = reader.next()) != null ){
            if( token.mode ){
                if( token.content[0].equals( "link" )){
                    destination.appendLink( token.content[2], token.content[1], modes );
                }
                else if( token.content[0].equals( "mode" )){
                    String input = token.content[1];
                    if( input.startsWith( "+" ))
                        modes.add( input.substring( 1 ));
                    else
                        modes.remove( input.substring( 1 ));
                }
            }
            else
                destination.appendText( token.content[0], modes );
        }
        
        return destination;
    }
    
    /**
     * A token is some text or tag that is read from the encoded 
     * {@link Entry#content} of the enclosing {@link Entry}.
     * @author Benjamin Sigg
     */
    private class Token{
        /** whether this <code>Token</code> is a tag or just simple text */
        public boolean mode;
        /** the entries of a tag or if {@link #mode} is <code>false</code> the text between tags */
        public String[] content;
    }
    
    /**
     * A <code>Reader</code> decodes the {@link Entry#content} of an {@link Entry}
     * and produces a stream of {@link Entry.Token}s. 
     * @author Benjamin Sigg
     *
     */
    private class Reader{
        /** the next char to read from the {@link Entry#content} */
        private int offset = 0;
        /** buffer used to collect single chars */
        private StringBuilder builder = new StringBuilder();
        
        /** whether the next {@link Entry.Token} is expected to be a tag or not */
        private boolean mode = false;
        
        /**
         * Reads the next {@link Token} from the {@link Entry#content}. The 
         * <code>Token</code> is either a tag or a text.
         * @return the token or <code>null</code> if the end of the stream
         * is reached
         */
        public Token next(){
            while( offset < content.length() ){
                Token t;
                if( mode )
                    t = nextMode();
                else
                    t = nextText();
                
                mode = !mode;
                
                if( t != null )
                    return t;
            }
            return null;
        }
        
        /**
         * Decodes the text between two tags.
         * @return the text or <code>null</code> if there is nothing to read
         */
        private Token nextText(){
            offset = next( offset );
            if( builder.length() == 0 )
                return null;
            
            Token t = new Token();
            t.mode = false;
            t.content = new String[]{ builder.toString() };
            return t;
        }
        
        /**
         * Decodes the next tag.
         * @return the tag
         */
        private Token nextMode(){
            List<String> list = new ArrayList<String>();
            while( offset < content.length() && 
                    ( offset == 0 || content.charAt( offset-1 ) != ']' )){
                offset = next( offset );
                list.add( builder.toString() );
            }
            Token t = new Token();
            t.mode = true;
            t.content = list.toArray( new String[ list.size() ] );
            return t;
        }
        
        /**
         * Reads char from the {@link Entry#content} until the end
         * is reached, or a single character '[', '|' or ']' is found.<br>
         * The result is stored in the {@link #builder}.
         * @param offset the first char to read
         * @return <code>offset + buffer.getLength()</code>
         */
        private int next( int offset ){
            builder.setLength( 0 );
            int n = content.length();
            boolean armed = false;
            char last = 0;
            
            while( offset < n ){
                char c = content.charAt( offset );
                offset++;
                
                if( c == '|' || c == '[' || c == ']'){
                    if( armed && c != last )
                        return offset-1;
                    
                    if( armed )
                        builder.append( c );
                    
                    armed = !armed;
                }
                else if( armed ){
                    return offset-1;
                }
                else
                    builder.append( c );
                
                last = c;
            }
            
            return offset;
        }
    }
}
