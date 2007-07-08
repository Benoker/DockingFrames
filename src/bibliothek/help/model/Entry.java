package bibliothek.help.model;

import java.util.*;

import bibliothek.help.gui.text.HelpDocument;

public class Entry {
    private static final String[] EMPTY = new String[0];
    
    private String type;
    private String id;
    private String content;
    private String[] details;
    
    /**
     * Creates a new entry
     * @param type the type of the entry
     * @param id the id of the entry
     * @param content the text for the entry
     * @param details links to other entries which should be shown when
     * this entry is shown
     */
    public Entry( String type, String id, String content, String...details ) {
        super();
        this.type = type;
        this.id = id;
        this.content = content;
        this.details = details;
        
        if( details == null || details.length == 0 )
            details = EMPTY;
    }
    
    public String getType() {
        return type;
    }
    
    public String getId() {
        return id;
    }
    
    public String getContent() {
        return content;
    }
    
    public String[] getDetails() {
        return details;
    }
    
    /**
     * Creates the leading to this class/interface/enum-entry.
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
        
        return classes.getLast().toNode();
    }
    
    private static class Intermediate{
        public List<Intermediate> children = new ArrayList<Intermediate>();
        public String type, name;
        
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
    
    private class Token{
        public boolean mode;
        public String[] content;
    }
    
    private class Reader{
        private int offset = 0;
        private StringBuilder builder = new StringBuilder();
        
        private boolean mode = false;
        
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
        
        private Token nextText(){
            offset = next( offset );
            if( builder.length() == 0 )
                return null;
            
            Token t = new Token();
            t.mode = false;
            t.content = new String[]{ builder.toString() };
            return t;
        }
        
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
