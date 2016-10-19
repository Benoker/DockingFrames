/*
 * Bibliothek - DockingFrames
 * Library built on Java/Swing, allows the user to "drag and drop"
 * panels containing any Swing-Component the developer likes to add.
 * 
 * Copyright (C) 2008 Benjamin Sigg
 * 
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301  USA
 * 
 * Benjamin Sigg
 * benjamin_sigg@gmx.ch
 * CH - Switzerland
 */
package bibliothek.extension.gui.dock;

import java.awt.Component;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.BorderFactory;
import javax.swing.Icon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.EtchedBorder;

import bibliothek.extension.gui.dock.preference.PreferenceEditor;
import bibliothek.extension.gui.dock.preference.PreferenceEditorCallback;
import bibliothek.extension.gui.dock.preference.PreferenceEditorFactory;
import bibliothek.extension.gui.dock.preference.PreferenceModel;
import bibliothek.extension.gui.dock.preference.PreferenceModelListener;
import bibliothek.extension.gui.dock.preference.PreferenceOperation;
import bibliothek.extension.gui.dock.preference.PreferenceOperationView;
import bibliothek.extension.gui.dock.preference.PreferenceOperationViewListener;
import bibliothek.extension.gui.dock.preference.editor.BooleanEditor;
import bibliothek.extension.gui.dock.preference.editor.ChoiceEditor;
import bibliothek.extension.gui.dock.preference.editor.KeyStrokeEditor;
import bibliothek.extension.gui.dock.preference.editor.LabelEditor;
import bibliothek.extension.gui.dock.preference.editor.ModifierMaskEditor;
import bibliothek.extension.gui.dock.preference.editor.StringEditor;
import bibliothek.gui.Dockable;
import bibliothek.gui.dock.action.ActionContentModifier;
import bibliothek.gui.dock.action.DockAction;
import bibliothek.gui.dock.themes.basic.action.BasicTrigger;
import bibliothek.gui.dock.themes.basic.action.buttons.BasicMiniButton;
import bibliothek.util.Path;

/**
 * A {@link Component} that shows the entries of a {@link PreferenceModel}, the user
 * can edit those entries. Each preference is shown in a {@link PreferenceEditor}, this
 * table uses a map of {@link PreferenceEditorFactory}s to create them. 
 * @author Benjamin Sigg
 */
public class PreferenceTable extends JPanel{
    /** The factories that are available. */
    private Map<Path, PreferenceEditorFactory<?>> factories = new HashMap<Path, PreferenceEditorFactory<?>>();
    
    /** the preferences that are shown in this table */
    private PreferenceModel model;
    
    /** the visible rows */
    private List<Row<?>> rows = new ArrayList<Row<?>>();
    
    /** the panel showing the contents of this table */
    private JPanel panel;
    
    /** the layout used on this panel */
    private GridBagLayout layout;
    
    /** a listener observing {@link #model} */
    private Listener listener = new Listener();
    
    /** the operations visible on this table */
    private List<PreferenceOperation> operations = new ArrayList<PreferenceOperation>();
    
    /** all the views that are currently in use */
    private Map<PreferenceOperation, Operation> operationViews = new HashMap<PreferenceOperation, Operation>();
    
    /** whether the order of the operations should be reversed or not */
    private boolean reverseOrder = true;
    
    /**
     * Creates a new table
     */
    public PreferenceTable(){
        super( new GridBagLayout() );
        layout = new GridBagLayout();
        panel = new JPanel( layout );
        add( panel, new GridBagConstraints( 0, 0, 1, 1, 1.0, 1.0, GridBagConstraints.FIRST_LINE_START,
                GridBagConstraints.HORIZONTAL, new Insets( 0, 0, 0, 0 ), 0, 0 ));
        
        setEditorFactory( Path.TYPE_BOOLEAN_PATH, BooleanEditor.FACTORY );
        setEditorFactory( Path.TYPE_MODIFIER_MASK_PATH, ModifierMaskEditor.FACTORY );
        setEditorFactory( Path.TYPE_KEYSTROKE_PATH, KeyStrokeEditor.FACTORY );
        setEditorFactory( Path.TYPE_STRING_CHOICE_PATH, ChoiceEditor.FACTORY );
        setEditorFactory( Path.TYPE_LABEL, LabelEditor.FACTORY );
        setEditorFactory( Path.TYPE_STRING_PATH, StringEditor.FACTORY );
        
        addOperation( PreferenceOperation.DEFAULT );
        addOperation( PreferenceOperation.DELETE );
    }
    
    /**
     * Creates a new table
     * @param model the model shown on this table
     */
    public PreferenceTable( PreferenceModel model ){
        this();
        setModel( model );
    }
    
    /**
     * Sets the order in which the operations should be shown. Either 
     * left to right or right to left. The default value is <code>true</code>
     * which means right to left.
     * @param reverseOrder how to display the operations
     */
    public void setReverseOrder( boolean reverseOrder ) {
        this.reverseOrder = reverseOrder;
        
        int index = 0;
        for( Row<?> row : rows ){
            row.setIndex( index++ );
        }
    }
    
    /**
     * Tells in which order the operations are shown.
     * @return the order
     * @see #setReverseOrder(boolean)
     */
    public boolean isReverseOrder() {
        return reverseOrder;
    }
    
    /**
     * Adds a new operation at the end of the set of operations. The set of
     * operations tells the order in which the operations appear on the table.
     * @param operation a new operation
     */
    public void addOperation( PreferenceOperation operation ){
        if( !operations.contains( operation )){
            operations.add( operation );
            operationViews.put( operation, new Operation( operation ) );
        }
    }
    
    /**
     * Insert an operation at the given index. The set of operations tells
     * the order in which the operations appear on the table.
     * @param index the index of the new operation
     * @param operation the new operation
     */
    public void insertOperation( int index, PreferenceOperation operation ){
        if( !operations.contains( operation )){
            operations.add( index, operation );
            operationViews.put( operation, new Operation( operation ) );
        }
    }
    
    private int getOperationIndex( PreferenceOperation operation ){
        int index = operations.indexOf( operation );
        if( index < 0 ){
            operations.add( operation );
            index = operations.size()-1;
        }
        
        if( reverseOrder )
            index = operations.size() - 1 - index;
        
        return index;
    }
    
    private void addTable( Component component ){
        panel.add( component );
    }
    
    private void removeTable( Component component ){
        panel.remove( component );
    }

    /**
     * Gets the model that is used on this table.
     * @return the model
     */
    public PreferenceModel getModel() {
        return model;
    }
    
    /**
     * Changes the model of this table.
     * @param model the new model, can be <code>null</code>
     */
    public void setModel( PreferenceModel model ) {
    	if( this.model != model ){
	        if( this.model != null ){
	            this.model.removePreferenceModelListener( listener );
	            listener.preferenceRemoved( this.model, 0, this.model.getSize()-1 );
	        }
	        
	        this.model = model;
	        
	        if( this.model != null ){
	            this.model.addPreferenceModelListener( listener );
	            listener.preferenceAdded( this.model, 0, this.model.getSize()-1 );
	        }
    	}
    }
    
    /**
     * Sets the factory that should be used to create an editor for some 
     * type of object.
     * @param type a path describing the type that will be edited, most
     * times the path is just the name of some class. There is a set of 
     * standard paths defined in {@link Path}
     * @param factory the new factory or <code>null</code> to delete a factory
     */
    public void setEditorFactory( Path type, PreferenceEditorFactory<?> factory ){
        if( factory == null )
            factories.remove( type );
        else
            factories.put( type, factory );
    }
    
    /**
     * Gets the factory which is responsible to create editors for
     * <code>type</code>-objects.
     * @param <V> the kind of objects that get edited
     * @param type the kind of objects that get edited
     * @return the factory for <code>type</code> or <code>null</code>
     */
    @SuppressWarnings("unchecked")
    public <V> PreferenceEditorFactory<V> getEditorFactory( Path type ){
        return (PreferenceEditorFactory<V>)factories.get( type );
    }
    
    /**
     * Creates a new editor for <code>type</code>. 
     * @param <V> the kind of value that gets edited
     * @param type the kind of value that gets edited
     * @return a new editor
     */
    @SuppressWarnings("unchecked")
    protected <V> PreferenceEditor<V> createEditor( Path type ){
        PreferenceEditorFactory<?> factory = factories.get( type );
        if( factory == null )
            throw new IllegalArgumentException( "No editor defined for type '" + type.toString() + "'" );
        return (PreferenceEditor<V>)factory.create();
    }
    
    /**
     * A wrapper around a {@link PreferenceOperation} adding support for {@link PreferenceOperationView}s.
     * @author Benjamin Sigg
     */
    private class Operation{
    	private PreferenceOperation operation;
    	private PreferenceOperationView view;
    	private int usages;
    	
    	/**
    	 * Creates a new wrapper around <code>operation</code>
    	 * @param operation the operation represented by this wrapper
    	 */
    	public Operation( PreferenceOperation operation ){
    		this.operation = operation;
    	}
    	
    	/**
    	 * Gets the operation which is hidden behind this wrapper.
    	 * @return the operation
    	 */
    	public PreferenceOperation getOperation(){
			return operation;
		}
    	
    	/**
    	 * Gets a view of this operation
    	 * @return the view, not <code>null</code>
    	 */
    	public synchronized PreferenceOperationView createView(){
    		if( view == null ){
    			view = operation.create( model );
    		}
    		usages++;
    		return view;
    	}
    	
    	/**
    	 * Informs this operation that its view is no longer required
    	 */
    	public synchronized void freeView(){
    		usages--;
    		if( usages == 0 ){
    			view.destroy();
    			view = null;
    		}
    	}
    }
    
    /**
     * Represents a single row in a {@link PreferenceTable}.
     * @author Benjamin Sigg
     *
     * @param <V> the kind of value in this row
     */
    private class Row<V> implements PreferenceEditorCallback<V>{
        private int index;
        
        private JLabel label;
        private PreferenceEditor<V> editor;
        
        private Map<PreferenceOperation, Button> editorOperations;
        private Map<PreferenceOperation, Button> modelOperations;
        
        private boolean initialized = false;
        
        public Row( PreferenceEditor<V> editor, String label, String description ){
            this.editor = editor;
            this.label = new JLabel( label );
            this.label.setToolTipText( description );
            
            addTable( this.label );
            
            if( editor != null ){
                editor.setCallback( this );
            }
            
            addTable( editor.getComponent() );
        }
        
        public void setIndex( int index ) {
            this.index = index;
            
            layout.setConstraints( label,
                    new GridBagConstraints( 0, index, 1, 1, 1.0, 1.0,
                            GridBagConstraints.LINE_START, GridBagConstraints.NONE,
                            new Insets( 1, 1, 1, 1 ), 0, 0 ) );
            
            if( editor != null ){
                layout.setConstraints( editor.getComponent(),
                        new GridBagConstraints( 1, index, 1, 1, 100.0, 1.0,
                                GridBagConstraints.LINE_START, GridBagConstraints.HORIZONTAL,
                                new Insets( 1, 1, 1, 1 ), 0, 0 ) );
            }
            
            if( !initialized ){
                initialized = true;
                initialize();
            }
            
            if( modelOperations != null ){
                for( Map.Entry<PreferenceOperation, Button> entry : modelOperations.entrySet() ){
                    int location = 2 + getOperationIndex( entry.getKey() );
                    
                    layout.setConstraints( entry.getValue(),
                            new GridBagConstraints( location, index, 1, 1, 1.0, 1.0,
                                    GridBagConstraints.LINE_END, GridBagConstraints.NONE,
                                    new Insets( 1, 0, 1, 0 ), 0, 0 ) );
                }
            }
            
            if( editorOperations != null ){
                for( Map.Entry<PreferenceOperation, Button> entry : editorOperations.entrySet() ){
                    int location = 2 + getOperationIndex( entry.getKey() );
                    
                    layout.setConstraints( entry.getValue(),
                            new GridBagConstraints( location, index, 1, 1, 1.0, 1.0,
                                    GridBagConstraints.LINE_END, GridBagConstraints.NONE,
                                    new Insets( 1, 0, 1, 0 ), 0, 0 ) );
                }
            }
            
            revalidate();
        }
        
        public void setOperation( PreferenceOperation operation, boolean enabled ) {
            setOperation( operation, enabled, true );
        }
        
        @SuppressWarnings("unchecked")
        private void initialize(){
            PreferenceOperation[] operations = model.getOperations( index );
            if( operations != null ){
                for( PreferenceOperation operation : operations ){
                    if( editorOperations == null || !editorOperations.containsKey( operation )){
                    	setOperation( operation, model.isEnabled( index, operation ), false );
                    }
                }
            }
            if( editor != null ){
            	editor.setValueInfo( model.getValueInfo( index ));
                editor.setValue( (V)model.getValue( index ) );
            }
        }
        
        private void setOperation( final PreferenceOperation operation, boolean enabled, final boolean editor ) {
            Map<PreferenceOperation, Button> operations;
            
            if( editor ){
                if( editorOperations == null ){
                    editorOperations = new HashMap<PreferenceOperation, Button>();
                }   
                operations = editorOperations;
            }
            else{
                if( modelOperations == null ){
                    modelOperations = new HashMap<PreferenceOperation, Button>();
                }   
                operations = modelOperations;                
            }
            
            Button button = operations.get( operation );
            if( button == null ){
            	Operation view = operationViews.get( operation );
            	if( view == null ){
            		view = new Operation( operation );
            	}
            	
                button = new Button( view, editor );
                operations.put( operation, button );
                addTable( button );
                
                int location = 2 + getOperationIndex( operation );
                
                layout.setConstraints( button,
                        new GridBagConstraints( location, index, 1, 1, 1.0, 1.0,
                                GridBagConstraints.LINE_END, GridBagConstraints.NONE,
                                new Insets( 1, 0, 1, 0 ), 0, 0 ) );
                
                revalidate();
            }
            
            button.getModel().setEnabled( enabled );
        }

        @SuppressWarnings("unchecked")
        public void changed(){
            if( editor != null ){
                editor.setValue( (V)model.getValue( index ) );
            }
            
            label.setText( model.getLabel( index ) );
            label.setToolTipText( model.getDescription( index ) );
            
            if( modelOperations != null ){
                for( Map.Entry<PreferenceOperation, Button> entry : modelOperations.entrySet() ){
                    boolean enabled = model.isEnabled( index, entry.getKey() );
                    entry.getValue().getModel().setEnabled( enabled );
                }
            }
        }
        
        /**
         * Destroys this row.
         */
        public void destroy(){
            removeTable( label );
            if( editor != null ){
                editor.setCallback( null );
                editor.setValue( null );
                removeTable( editor.getComponent() );
                editor.setValueInfo( null );
            }
            if( editorOperations != null ){
                for( Button button : editorOperations.values() ){
                	button.destroy();
                    removeTable( button );
                }
            }
            if( modelOperations != null ){
                for( Button button : modelOperations.values() ){
                	button.destroy();
                    removeTable( button );
                }
            }
        }
        
        @SuppressWarnings("unchecked")
        public V get() {
            return (V)model.getValue( index );
        }
        
        public void set( V value ) {
            model.setValue( index, value );
        }

        private void doOperation( PreferenceOperation operation ){
            model.doOperation( index, operation );
        }
        
        public PreferenceModel getModel(){
        	return model;
        }
        
        /**
         * A small button that can trigger an operation 
         * @author Benjamin Sigg
         */
        private class Button extends BasicMiniButton implements PreferenceOperationViewListener{
        	private Operation operation;
        	private PreferenceOperationView view;
        	
            public Button( final Operation operation, final boolean editorOperation ){
                super( new BasicTrigger(){
                    public void triggered() {
                        if( editorOperation )
                            editor.doOperation( operation.getOperation() );
                        else
                            doOperation( operation.getOperation() );
                    }
                    public DockAction getAction(){
                    	return null;
                    }
                    public Dockable getDockable(){
                    	return null;
                    }
                }, null );
                
                this.operation = operation;
                view = operation.createView();
                
                view.addListener( this );
                
                getModel().setIcon( ActionContentModifier.NONE_HORIZONTAL, view.getIcon() );
                getModel().setToolTipText( view.getDescription() );
   
                setMouseOverBorder( BorderFactory.createEtchedBorder( EtchedBorder.LOWERED ) );
                setMousePressedBorder( BorderFactory.createLoweredBevelBorder() );
            }

            @Override
            public void setEnabled( boolean enabled ) {
                super.setEnabled( enabled );
                setFocusable( enabled );
            }
            
            public void iconChanged( PreferenceOperationView operation, Icon oldIcon, Icon newIcon ){
            	getModel().setIcon( ActionContentModifier.NONE_HORIZONTAL, newIcon );
            }
            
            public void descriptionChanged( PreferenceOperationView operation, String oldDescription, String newDescription ){
            	getModel().setToolTipText( newDescription );
            }
            
            public void destroy(){
            	view.removeListener( this );
            	operation.freeView();
            }
        }
    }
    
    
    /**
     * Listens to {@link PreferenceTable#model} and updates this table when necessary.
     * @author Benjamin Sigg
     *
     */
    private class Listener implements PreferenceModelListener{
        @SuppressWarnings("unchecked")
        public void preferenceAdded( PreferenceModel model, int beginIndex, int endIndex ){
            for( int index = beginIndex; index <= endIndex; index++ ){
                PreferenceEditor<?> editor = createEditor( model.getTypePath( index ) );
                Row<?> row = new Row( editor, model.getLabel( index ), model.getDescription( index ));
                rows.add( index, row );
                row.setIndex( index );
            }
            
            for( int i = beginIndex, n = rows.size(); i<n; i++ ){
                rows.get( i ).setIndex( i );
            }
            
            revalidate();
        }

        public void preferenceChanged( PreferenceModel model, int beginIndex, int endIndex ){
            for( int i = beginIndex; i <= endIndex; i++ ){
                Row<?> row = rows.get( i );
                row.changed();
            }
            
            revalidate();
        }

        public void preferenceRemoved( PreferenceModel model, int beginIndex, int endIndex ){
            for( int i = endIndex; i >= beginIndex; i-- ){
                Row<?> row = rows.remove( i );
                row.destroy();
            }
            
            for( int i = beginIndex, n = rows.size(); i<n; i++ ){
                rows.get( i ).setIndex( i );
            }
            
            revalidate();
        }
        
    }
}
