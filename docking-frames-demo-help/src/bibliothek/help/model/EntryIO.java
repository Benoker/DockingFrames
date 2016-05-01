package bibliothek.help.model;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import bibliothek.help.javadoc.Entryable;

/**
 * A class intended to read and write whole lists of {@link Entry}s.
 * @author Benjamin Sigg
 *
 */
public class EntryIO {
    /**
     * Writes the properties of <code>entry</code> into <code>out</code>.
     * @param entry the element to store
     * @param out the stream to write into
     * @throws IOException if writing is not possible
     * @see #read(DataInputStream)
     */
    public static void write( Entry entry, DataOutputStream out ) throws IOException{
        out.writeUTF( entry.getType() );
        out.writeUTF( entry.getId() );
        out.writeUTF( entry.getTitle() );
        out.writeUTF( entry.getContent() );
        out.writeInt( entry.getDetails().length );
        for( String link : entry.getDetails() )
            out.writeUTF( link );
    }
    
    /**
     * Transforms the tree with root <code>Entryable</code> into a list
     * of {@link Entry}s, and then writes these <code>Entry</code>s into 
     * <code>out</code>.
     * @param entryable the root of a set of <code>Entryable</code>s
     * @param out stream to write into
     * @throws IOException if writing is not possible
     * @see #readList(DataInputStream)
     */
    public static void writeList( Entryable entryable, DataOutputStream out ) throws IOException{
        List<Entry> list = new LinkedList<Entry>();
        collect( entryable, list );
        out.writeInt( list.size() );
        for( Entry entry : list )
            write( entry, out );
    }
    
    /**
     * Calls {@link Entryable#toEntry()} and stores the {@link Entry} in 
     * <code>entries</code>. The root of <code>Entryable</code>s is traversed
     * recursively.
     * @param entryable the root of a tree of <code>Entryable</code>s
     * @param entries the newly created <code>Entry</code>s
     */
    private static void collect( Entryable entryable, List<Entry> entries ){
        entries.add( entryable.toEntry() );
        for( Entryable child : entryable.children() )
            collect( child, entries );
    }
    
    /**
     * Reads a list of {@link Entry}s, the list should have been written
     * by {@link #writeList(Entryable, DataOutputStream) writeList}.
     * @param in the stream to read from
     * @return a set of <code>Entry</code>s
     * @throws IOException if the stream can't be read
     * @see #writeList(Entryable, DataOutputStream)
     */
    public static List<Entry> readList( DataInputStream in ) throws IOException{
        int count = in.readInt();
        List<Entry> result = new ArrayList<Entry>( count );
        for( int i = 0; i < count; i++ )
            result.add( read( in ) );
        return result;
    }
    
    /**
     * Reads a single {@link Entry} from <code>in</code>. The <code>Entry</code>
     * should have been written by {@link #write(Entry, DataOutputStream) write}.
     * @param in the stream to read from
     * @return the newly read <code>Entry</code>
     * @throws IOException if the stream can't be read
     * @see #write(Entry, DataOutputStream)
     */
    public static Entry read( DataInputStream in ) throws IOException{
        String type = in.readUTF();
        String id = in.readUTF();
        String title = in.readUTF();
        String content = in.readUTF();
        int count = in.readInt();
        String[] details = new String[ count ];
        for( int i = 0; i < count; i++ )
            details[i] = in.readUTF();
        return new Entry( type, id, title, content, details );
    }
}
