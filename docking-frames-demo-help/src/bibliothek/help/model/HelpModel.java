package bibliothek.help.model;

import java.io.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * The help-model stores a set of {@link Entry}s. The model can be used
 * to search an <code>Entry</code> given its link.
 * @author Benjamin Sigg
 *
 */
public class HelpModel {
    /** the <code>Entry</code>s known to this model */
    private Map<String, Entry> entries = new HashMap<String, Entry>();
    
    /**
     * Creates a new model, reads a set of {@link Entry}s. The
     * <code>Entry</code>s are read using {@link EntryIO#readList(DataInputStream)}.
     * @param path the path to a file that contains the help-pages. The path
     * is resolved using {@link Class#getResourceAsStream(String)}, with
     * <code>HelpModel.class</code> as starting point.
     * @throws IOException if the help can't be loaded
     */
    public HelpModel( String path ) throws IOException{
        DataInputStream in = new DataInputStream( HelpModel.class.getResourceAsStream( path ) );
        List<Entry> list = EntryIO.readList( in );
        in.close();
        for( Entry entry : list )
            entries.put( entry.getType() + ":" + entry.getId(), entry );
    }
    
    /**
     * Gets an {@link Entry} that is identified by its link.
     * @param link a string in the form <code>type:id</code>
     * @return an Entry where <code>type</code> equals {@link Entry#getType()}
     * and <code>id</code> equals {@link Entry#getId()}
     */
    public Entry get( String link ){
        return entries.get( link );
    }
}
