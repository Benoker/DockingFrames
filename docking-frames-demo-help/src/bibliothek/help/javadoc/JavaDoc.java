package bibliothek.help.javadoc;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

import bibliothek.help.javadoc.inspection.InspectionTree;
import bibliothek.help.model.Entry;
import bibliothek.help.model.EntryIO;

import com.sun.javadoc.RootDoc;

/**
 * A class that is able to call JavaDoc and uses the output of JavaDoc
 * to create first a tree of {@link Entryable}s, and then transforms
 * these <code>Entryable</code>s into {@link Entry}s that are written into
 * a file "help/help.data".
 * @author Benjamin Sigg
 *
 */
public class JavaDoc {
    /**
     * Reads a set of java-source-files, uses JavaDoc to create a documentation,
     * writes the documentation in a file and presents then an {@link InspectionTree}
     * showing the documentation.
     * @param args the directory in which to find a set of java-source-files
     */
    public static void main( String[] args ) {
        // File directory = new File( "./src" );
        // File directory = new File( "C:/Dateien/Java/ProjekteLongterm/dockingFrames/src" );
    	File directory = new File( args[0] );
        
        List<String> docargs = new ArrayList<String>();
        
        docargs.add( "-protected");
        
        docargs.add( "-doclet"); 
        docargs.add( "bibliothek.help.javadoc.JavaDoc");
        
        docargs.add( "-docletpath");
        docargs.add( "./bin");
        
        docargs.add( "-sourcepath");
        docargs.add( directory.getAbsolutePath() );

        collect( directory, "", docargs );
        
        com.sun.tools.javadoc.Main.execute( docargs.toArray( new String[ docargs.size() ] ));
    }

    /**
     * Collects all files ending with "*.java" in <code>directory</code>,
     * search recursively in the children of <code>directory</code>.
     * @param directory the directory in which java-source-files are searched
     * @param name the name of the package whose files are found in <code>directory</code>
     * @param list an empty list that will be filled with all the packages
     * found in <code>directory</code>
     * @return <code>true</code> if at least one java-source-file is found
     * in <code>directory</code> (children of <code>directory</code> are
     * not included)
     */
    private static boolean collect( File directory, String name, List<String> list ){
        boolean java = false;
        File[] children = directory.listFiles();
        if( children != null ){
	        for( File child : children ){
	            if( child.isDirectory() ){
	                String next = name;
	                if( next.length() > 0 )
	                    next += "." + child.getName();
	                else
	                    next = child.getName();
	                
	                if( collect( child, next, list ) )
	                    list.add( next );
	            }
	            else if( child.getName().endsWith( ".java" ))
	                java = true;
	        }
        }
        return java;
    }
    
    /**
     * Extracts information from <code>root</code> and stores that information
     * in the file-system. The created file can be read by the {@link EntryIO}.<br>
     * This method is called directly by JavaDoc.
     * @param root the documentation created by JavaDoc.
     * @return <code>true</code>
     */
    public static boolean start(RootDoc root) {
        File directory = new File( "help" );
        directory.mkdir();
        
        Entryable entryable = new EntryableRoot( root );
        File file = new File( directory, "help.data" );
        try{
            DataOutputStream out = new DataOutputStream( new BufferedOutputStream( new FileOutputStream( file )));
            EntryIO.writeList( entryable, out );
            out.close();
        }
        catch( IOException ex ){
            ex.printStackTrace();
        }
        
        InspectionTree.inspect( entryable );
        
        return true;
    }
}
