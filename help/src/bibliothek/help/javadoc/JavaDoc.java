package bibliothek.help.javadoc;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

import bibliothek.help.javadoc.inspection.InspectionTree;
import bibliothek.help.model.EntryIO;

import com.sun.javadoc.RootDoc;

public class JavaDoc {
    public static void main( String[] args ) {
        // File directory = new File( "./src" );
        // File directory = new File( "C:/Dateien/Java/ProjekteLongterm/dockingFrames/src" );
    	File directory = new File( args[0] );
        
        List<String> docargs = new ArrayList<String>();
        
        docargs.add( "-private");
        
        docargs.add( "-doclet"); 
        docargs.add( "bibliothek.help.javadoc.JavaDoc");
        
        docargs.add( "-docletpath");
        docargs.add( "./bin");
        
        docargs.add( "-sourcepath");
        docargs.add( directory.getAbsolutePath() );

        collect( directory, "", docargs );
        
        com.sun.tools.javadoc.Main.execute( docargs.toArray( new String[ docargs.size() ] ));
    }

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
