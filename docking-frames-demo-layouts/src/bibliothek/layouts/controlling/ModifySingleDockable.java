package bibliothek.layouts.controlling;

import java.awt.GridLayout;
import java.text.Collator;
import java.util.Arrays;
import java.util.Set;

import javax.swing.JScrollPane;

import bibliothek.extension.gui.dock.PreferenceTable;
import bibliothek.extension.gui.dock.preference.AbstractPreference;
import bibliothek.extension.gui.dock.preference.DefaultPreferenceModel;
import bibliothek.extension.gui.dock.preference.Preference;
import bibliothek.extension.gui.dock.preference.PreferenceModel;
import bibliothek.extension.gui.dock.preference.PreferenceOperation;
import bibliothek.gui.dock.common.CControl;
import bibliothek.gui.dock.common.DefaultSingleCDockable;
import bibliothek.layouts.Core;
import bibliothek.layouts.Icons;
import bibliothek.layouts.testing.SingleTestDockable;
import bibliothek.layouts.testing.SingleTestFactory;
import bibliothek.util.Path;

public class ModifySingleDockable extends DefaultSingleCDockable{
    private static final PreferenceOperation ADD_SINGLE_BACKUP_FACTORY = new PreferenceOperation( "add backup factory", Icons.get( "add factory" ), "CControl.addSingleBackupFactory" );
    private static final PreferenceOperation REMOVE_SINGLE_BACKUP_FACTORY = new PreferenceOperation( "remove backup factory", Icons.get( "remove factory" ), "CControl.removeSingleBackupFactory" );
    
    private static final PreferenceOperation ADD_SINGLE_DOCKABLE = new PreferenceOperation( "add", Icons.get( "add dockable" ), "CControl.add( SingleCDockable )" );
    private static final PreferenceOperation REMOVE_SINGLE_DOCKABLE = new PreferenceOperation( "remove", Icons.get( "remove dockable" ), "CControl.remove( SingleCDockable )" );
    
    private Core core;
    private DefaultPreferenceModel model;
    
    public ModifySingleDockable( Core core, CControl control ){
        super( "modify single" );
        setTitleText( "Single Dockables" );
     
        this.core = core;
        model = new DefaultPreferenceModel( control.getController() );
        
        PreferenceTable table = new PreferenceTable( model );
        setLayout( new GridLayout( 1, 1 ) );
        add( new JScrollPane( table ));
        
        updateTable();
    }
    
    public void updateTable(){
        CControl control = core.getEnvironment().getEnvironmentControl();
        Set<String> idSet = control.getRegister().listSingleDockables();
        String[] ids = idSet.toArray( new String[ idSet.size() ] );
        
        Arrays.sort( ids, Collator.getInstance() );
                
        model.removeAll();
        
        for( String id : ids ){
            model.add( new Entry( id ));
        }
        model.add( new NewEntry() );
    }
    
    private class Entry extends AbstractPreference<String>{
        private String id;
        
        private boolean backupFactorySet = false;
        private boolean dockableSet = false;
        
        public Entry( String id ){
            this.id = id;
            
            CControl control = core.getEnvironment().getEnvironmentControl();
            backupFactorySet = control.getSingleDockableFactory( id ) != null;
            dockableSet = control.getSingleDockable( id ) != null;
        }
        
        public void setModel( PreferenceModel model ){
	        // ignore	
        }
        
        public String getLabel() {
            return "";
        }

        public String getDescription() {
            return "Allows to add or remove a SingleCDockable";
        }

        public Path getPath() {
            return new Path( "single", id );
        }

        public Path getTypePath() {
            return Path.TYPE_LABEL;
        }

        @Override
        public PreferenceOperation[] getOperations() {
            return new PreferenceOperation[]{
                    PreferenceOperation.DELETE,
                    REMOVE_SINGLE_BACKUP_FACTORY,
                    ADD_SINGLE_BACKUP_FACTORY,
                    REMOVE_SINGLE_DOCKABLE,
                    ADD_SINGLE_DOCKABLE
            };
        }
        
        @Override
        public boolean isEnabled( PreferenceOperation operation ) {
            if( operation == REMOVE_SINGLE_BACKUP_FACTORY ){
                return backupFactorySet;
            }
            
            if( operation == ADD_SINGLE_BACKUP_FACTORY ){
                return !backupFactorySet;
            }
            
            if( operation == REMOVE_SINGLE_DOCKABLE ){
                return dockableSet;
            }
            
            if( operation == ADD_SINGLE_DOCKABLE ){
                return !dockableSet;
            }
            
            return false;
        }
        
        @Override
        public void doOperation( PreferenceOperation operation ) {
            if( operation == ADD_SINGLE_BACKUP_FACTORY ){
                backupFactorySet = true;
                core.getEnvironment().getEnvironmentControl().addSingleDockableFactory( id, new SingleTestFactory() );
            }
            if( operation == REMOVE_SINGLE_BACKUP_FACTORY ){
                backupFactorySet = false;
                core.getEnvironment().getEnvironmentControl().removeSingleDockableFactory( id );
            }
            if( operation == ADD_SINGLE_DOCKABLE ){
                dockableSet = true;
                SingleTestDockable dockable = new SingleTestDockable( id, false );
                core.getEnvironment().getEnvironmentControl().addDockable( dockable );
                dockable.setVisible( true );
            }
            if( operation == REMOVE_SINGLE_DOCKABLE ){
                dockableSet = false;
                core.getEnvironment().getEnvironmentControl().removeSingleDockable( id );
            }
            fireChanged();
        }
        
        public String getValue() {
            return "'" + id + "': ";
        }

        public Object getValueInfo() {
            return null;
        }

        public boolean isNatural() {
            return true;
        }

        public void setValue( String value ) {
            // ignore
        }
        
        public void read(){
        	// ignore
        }
        
        public void write(){
	        // ignore	
        }
    }
    
    private class NewEntry extends AbstractPreference<String>{
        private String value;
        
        public void setModel( PreferenceModel model ){
	        // ignore	
        }
        
        public String getDescription() {
            return "Create a new entry.";
        }

        public String getLabel() {
            return "New:";
        }

        public Path getPath() {
            return new Path( "new.single" );
        }

        public Path getTypePath() {
            return Path.TYPE_STRING_PATH;
        }

        public String getValue() {
            return value;
        }

        public Object getValueInfo() {
            return null;
        }

        public boolean isNatural() {
            return true;
        }

        public void setValue( String value ) {
            this.value = value;
            fireChanged();
        }
        
        @Override
        public PreferenceOperation[] getOperations() {
            return new PreferenceOperation[]{
                    ADD_SINGLE_BACKUP_FACTORY,
                    ADD_SINGLE_DOCKABLE
            };
        }
        
        @Override
        public boolean isEnabled( PreferenceOperation operation ) {
            if( !Path.isValidPath( value ))
                return false;
            
            for( int i = 0, n = model.getSize(); i<n; i++ ){
                Preference<?> preference = model.getPreference( i );
                if( preference instanceof Entry ){
                    if( ((Entry)preference).id.equals( value ))
                        return false;
                }
            }
            return true;
        }
        
        @Override
        public void doOperation( PreferenceOperation operation ) {
            if( operation == ADD_SINGLE_BACKUP_FACTORY ){
                core.getEnvironment().getEnvironmentControl().addSingleDockableFactory( value, new SingleTestFactory() );
            }
            if( operation == ADD_SINGLE_DOCKABLE ){
                SingleTestDockable dockable = new SingleTestDockable( value, false );
                core.getEnvironment().getEnvironmentControl().addDockable( dockable );
                dockable.setVisible( true );
            }
            updateTable();
        }
        
        public void read(){
        	// ignore
        }
        
        public void write(){
	        // ignore	
        }
    }
}
