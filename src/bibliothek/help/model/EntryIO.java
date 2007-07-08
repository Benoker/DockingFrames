package bibliothek.help.model;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import bibliothek.help.javadoc.Entryable;

public class EntryIO {
    public static void write( Entry entry, DataOutputStream out ) throws IOException{
        out.writeUTF( entry.getType() );
        out.writeUTF( entry.getId() );
        out.writeUTF( entry.getContent() );
        out.writeInt( entry.getDetails().length );
        for( String link : entry.getDetails() )
            out.writeUTF( link );
    }
    
    public static void writeList( Entryable entryable, DataOutputStream out ) throws IOException{
        List<Entry> list = new LinkedList<Entry>();
        collect( entryable, list );
        out.writeInt( list.size() );
        for( Entry entry : list )
            write( entry, out );
    }
    
    private static void collect( Entryable entryable, List<Entry> entries ){
        entries.add( entryable.toEntry() );
        for( Entryable child : entryable.children() )
            collect( child, entries );
    }
    
    public static List<Entry> readList( DataInputStream in ) throws IOException{
        int count = in.readInt();
        List<Entry> result = new ArrayList<Entry>( count );
        for( int i = 0; i < count; i++ )
            result.add( read( in ) );
        return result;
    }
    
    public static Entry read( DataInputStream in ) throws IOException{
        String type = in.readUTF();
        String id = in.readUTF();
        String content = in.readUTF();
        int count = in.readInt();
        String[] details = new String[ count ];
        for( int i = 0; i < count; i++ )
            details[i] = in.readUTF();
        return new Entry( type, id, content, details );
    }
}
