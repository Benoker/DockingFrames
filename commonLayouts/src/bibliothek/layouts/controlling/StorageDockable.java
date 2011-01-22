package bibliothek.layouts.controlling;

import java.awt.BorderLayout;

import javax.swing.JOptionPane;
import javax.swing.JScrollPane;

import bibliothek.extension.gui.dock.PreferenceTable;
import bibliothek.extension.gui.dock.preference.AbstractPreference;
import bibliothek.extension.gui.dock.preference.DefaultPreferenceModel;
import bibliothek.extension.gui.dock.preference.PreferenceModel;
import bibliothek.extension.gui.dock.preference.PreferenceOperation;
import bibliothek.gui.dock.common.CControl;
import bibliothek.gui.dock.common.DefaultSingleCDockable;
import bibliothek.gui.dock.support.util.ApplicationResourceManager;
import bibliothek.layouts.Core;
import bibliothek.layouts.Icons;
import bibliothek.util.Path;
import bibliothek.util.xml.XElement;

/**
 * Shows a list of layout-settings. Each entry in this list represent the output
 * of {@link ApplicationResourceManager#writeStream(java.io.DataOutputStream)} or
 * {@link ApplicationResourceManager#writeXML(bibliothek.util.xml.XElement)}.
 * @author Benjamin Sigg
 */
public class StorageDockable extends DefaultSingleCDockable{
    private static final PreferenceOperation STORE_XML = new PreferenceOperation( "xml", Icons.get( "xml" ), "Store the current layout as xml" );
    private static final PreferenceOperation STORE_BINARY = new PreferenceOperation( "binary", Icons.get( "binary" ), "Store the current layout in binary format" );
    private static final PreferenceOperation LOAD = new PreferenceOperation( "load", Icons.get( "load" ), "Load the layout of this entry" );

    private Core core;
    
    private DefaultPreferenceModel model;
    private int entryCount = 0;

    public StorageDockable( Core core, CControl control ){
        super( "storage" );
        setTitleText( "Storage" );
        this.core = core;

        model = new DefaultPreferenceModel( control.getController() );
        model.add( new Entry( ++entryCount ) );
        
        PreferenceTable table = new PreferenceTable();
        table.setModel( model );
        
        setLayout( new BorderLayout() );
        add( new JScrollPane( table ), BorderLayout.CENTER );
    }

    private class Entry extends AbstractPreference<Object>{
        private int index;
        private Object data;
        private String label;
        
        public Entry( int index ){
            this.index = index;
            label = "empty";
        }
        
        public void setModel( PreferenceModel model ){
	        // ignore	
        }
        
        @Override
        public PreferenceOperation[] getOperations() {
            return new PreferenceOperation[]{
                    PreferenceOperation.DELETE,
                    LOAD,
                    STORE_BINARY,
                    STORE_XML
            };
        }

        @Override
        public boolean isEnabled( PreferenceOperation operation ) {
            if( operation == STORE_XML || operation == STORE_BINARY )
                return true;

            if( operation == LOAD || operation == PreferenceOperation.DELETE )
                return data != null;

            return false;
        }

        @Override
        public void doOperation( PreferenceOperation operation ) {
            try{
                if( operation == STORE_XML || operation == STORE_BINARY ){
                    if( operation == STORE_XML ){
                        XElement element = new XElement( "root" );
                        core.getEnvironment().getEnvironmentControl().getResources().writeXML( element );
                        data = element;
                        label = "xml";
                    }
                    if( operation == STORE_BINARY ){
                        data = core.getEnvironment().getEnvironmentControl().getResources().writeArray();
                        label = "binary";
                    }
                    fireChanged();
                    if( model.indexOf( this ) == model.getSize()-1 ){
                        model.add( new Entry( ++entryCount ) );
                    }
                }
                if( operation == LOAD ){
                    if( data instanceof XElement ){
                        core.getEnvironment().getEnvironmentControl().getResources().readXML( (XElement)data );
                    }
                    if( data instanceof byte[] ){
                        core.getEnvironment().getEnvironmentControl().getResources().readArray( (byte[])data );
                    }
                    
                    core.getSingleDockables().updateTable();
                    core.getMultiDockables().updateTable();
                }
                if( operation == PreferenceOperation.DELETE ){
                    model.remove( this );
                }
            }
            catch( Exception ex ){
                ex.printStackTrace();
                JOptionPane.showMessageDialog( getContentPane(), "Unable to perform operation:\n" + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE );
            }
        }

        public String getDescription() {
            return "Stores one set of layouts, contains the same information as one layout-file would contain.";
        }

        public String getLabel() {
            return "Layout Nr. " + index + ": ";
        }

        public Path getPath() {
            return new Path( "layout.nr" + index );
        }

        public Path getTypePath() {
            return Path.TYPE_LABEL;
        }

        public Object getValue() {
            return label;
        }

        public Object getValueInfo() {
            return null;
        }

        public boolean isNatural() {
            return true;
        }

        public void setValue( Object value ) {
            // ignore
        }
        
        public void read(){
	        // ignore	
        }
        
        public void write(){
	        // ignore	
        }
    }
}
