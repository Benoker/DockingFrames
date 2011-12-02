package bibliothek.layouts.controlling;

import java.awt.GridLayout;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.swing.JScrollPane;

import bibliothek.extension.gui.dock.PreferenceTable;
import bibliothek.extension.gui.dock.preference.*;
import bibliothek.gui.dock.common.CControl;
import bibliothek.gui.dock.common.CControlRegister;
import bibliothek.gui.dock.common.DefaultSingleCDockable;
import bibliothek.gui.dock.common.MultipleCDockableFactory;
import bibliothek.gui.dock.common.event.CControlListener;
import bibliothek.gui.dock.common.intern.CDockable;
import bibliothek.layouts.Core;
import bibliothek.layouts.Icons;
import bibliothek.layouts.testing.MultipleTestDockable;
import bibliothek.layouts.testing.MultipleTestFactory;
import bibliothek.util.Path;

public class ModifyMultiDockable extends DefaultSingleCDockable{
    private Map<String, MultipleTestFactory> allFactories = new HashMap<String, MultipleTestFactory>();
    private List<String> factoryOrder = new LinkedList<String>();
    
    private static final PreferenceOperation ADD_MULTI_FACTORY = new PreferenceOperation( "add factory", Icons.get( "add factory" ), "CControl.add( String, MultipleCDockableFactory" );
    private static final PreferenceOperation REMOVE_MULTI_FACTORY = new PreferenceOperation( "remove factory", Icons.get( "remove factory" ), "CControl.removeSingleBackupFactory" );
    
    private static final PreferenceOperation ADD_MULTI_DOCKABLE = new PreferenceOperation( "add", Icons.get( "add dockable" ), "CControl.add( MultipleCDockable )" );
    
    private Core core;
    private CControl coreControl;
    
    private PreferenceTable table;
    private DefaultPreferenceModel model;
    
    public ModifyMultiDockable( Core core, CControl control ){
        super( "modify multi" );
        this.coreControl = control;
        setTitleText( "Multiple Dockables" );
        this.core = core;

        model = new DefaultPreferenceModel( control.getController() );
        table = new PreferenceTable( model );
        setLayout( new GridLayout( 1, 1 ) );
        add( new JScrollPane( table ));
        
        updateTable();
        
        core.getEnvironment().getEnvironmentControl().addControlListener( new CControlListener(){
            public void added( CControl control, CDockable dockable ) {
                // ignore
            }

            public void closed( CControl control, CDockable dockable ) {
                if( dockable instanceof MultipleTestDockable ){
                    MultipleTestDockable multi = (MultipleTestDockable)dockable;
                    
                    for( int i = 0, n = model.getSize(); i<n; i++ ){
                        Preference<?> preference = model.getPreference( i );
                        if( preference instanceof Entry ){
                            Entry entry = (Entry)preference;
                            if( entry.getFactory() == multi.getFactory() ){
                                entry.setCount( entry.getCount()-1 );
                                break;
                            }
                        }
                    }
                }                
            }

            public void opened( CControl control, CDockable dockable ) {
                // ignore
            }

            public void removed( CControl control, CDockable dockable ) {
                // ignore   
            }
        });
    }
    
    public void updateTable(){
        CControl control = core.getEnvironment().getEnvironmentControl();
        CControlRegister register = control.getRegister();
        
        model = new DefaultPreferenceModel( coreControl.getController() );
        for( String id : factoryOrder ){
            MultipleCDockableFactory<?, ?> factory = register.getFactory( id );
            int count = 0;
            if( factory != null ){
                count = register.listMultipleDockables( factory ).size();
            }
            model.add( new Entry( id, count, allFactories.get( id )) );
        }
        
        model.add( new NewEntry() );
        table.setModel( model );
    }
    
    private class Entry extends AbstractPreference<String>{
        private String id;
        private int count;
        private MultipleTestFactory factory;
        
        public Entry( String id, int count, MultipleTestFactory factory ){
            this.id = id;
            this.count = count;
            this.factory = factory;
        }
        
        public void setModel( PreferenceModel model ){
	        // ignore	
        }
        
        public int getCount() {
            return count;
        }
        
        public void setCount( int count ) {
            this.count = count;
            fireChanged();
        }
        
        public MultipleTestFactory getFactory() {
            return factory;
        }
        
        public String getDescription() {
            return "Represents a MultipleCDockableFactory";
        }

        @Override
        public PreferenceOperation[] getOperations() {
            return new PreferenceOperation[]{
                    PreferenceOperation.DELETE,
                    ADD_MULTI_DOCKABLE,
                    REMOVE_MULTI_FACTORY,
                    ADD_MULTI_FACTORY
            };
        }
        
        @Override
        public boolean isEnabled( PreferenceOperation operation ) {
            if( operation == PreferenceOperation.DELETE )
                return true;
            
            boolean present = core.getEnvironment().getEnvironmentControl().getMultipleDockableFactory( id ) != null;
            
            if( operation == ADD_MULTI_DOCKABLE )
                return present;
            if( operation == ADD_MULTI_FACTORY )
                return !present;
            if( operation == REMOVE_MULTI_FACTORY )
                return present;
            return false;
        }
        
        @Override
        public void doOperation( PreferenceOperation operation ) {
            CControl control = core.getEnvironment().getEnvironmentControl();
            
            if( operation == PreferenceOperation.DELETE ){
                control.removeMultipleDockableFactory( id );
                allFactories.remove( id );
                factoryOrder.remove( id );
            }
            if( operation == ADD_MULTI_FACTORY ){
                control.addMultipleDockableFactory( id, factory );
            }
            if( operation == REMOVE_MULTI_FACTORY ){
                control.removeMultipleDockableFactory( id );
            }
            if( operation == ADD_MULTI_DOCKABLE ){
                MultipleTestDockable dockable = new MultipleTestDockable( factory );
                control.addDockable( dockable );
                dockable.setVisible( true );
            }
            
            updateTable();
        }
        
        public String getLabel() {
            return "'" + id + "': ";
        }

        public Path getPath() {
            return new Path( "factory", id );
        }

        public Path getTypePath() {
            return Path.TYPE_LABEL;
        }

        public String getValue() {
            return String.valueOf( count );
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
        private String value = "";
        
        public String getDescription() {
            return "Creates a new factory";
        }

        public String getLabel() {
            return "New: ";
        }

        public void setModel( PreferenceModel model ){
	        // ignore	
        }
        
        public Path getPath() {
            return new Path( "newentry" );
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
            return new PreferenceOperation[]{ ADD_MULTI_FACTORY };
        }
        
        @Override
        public boolean isEnabled( PreferenceOperation operation ) {
            if( value.length() == 0 )
                return false;
            
            if( allFactories.containsKey( value ))
                return false;
            
            if( !Path.isValidPath( value ))
                return false;
            
            return true;
        }
        
        @Override
        public void doOperation( PreferenceOperation operation ) {
            if( operation == ADD_MULTI_FACTORY ){
                MultipleTestFactory factory = new MultipleTestFactory();
                allFactories.put( value, factory );
                factoryOrder.add( value );
                core.getEnvironment().getEnvironmentControl().addMultipleDockableFactory( value, factory );
                updateTable();
            }
        }
        
        public void read(){
        	// ignore
        }
        
        public void write(){
	        // ignore	
        }
    }
}
