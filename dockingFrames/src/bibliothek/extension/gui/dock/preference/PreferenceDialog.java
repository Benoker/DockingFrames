package bibliothek.extension.gui.dock.preference;

import java.awt.Component;

import javax.swing.JComponent;

import bibliothek.extension.gui.dock.PreferenceTable;
import bibliothek.util.Path;

/**
 * A simple dialog showing the contents of one {@link PreferenceModel}.
 * @author Benjamin Sigg
 * @see PreferenceTreeDialog
 */
public class PreferenceDialog extends AbstractPreferenceDialog<PreferenceModel>{
    /**
     * Shows a modal dialog on which the user can change the preferences of
     * <code>controller</code>. This method will call {@link PreferenceModel#read()} and
     * {@link PreferenceModel#write()} to reset or to apply the changes of the user.
     * @param model the model to show on the dialog
     * @param owner the owner of the dialog
     */
    public static void openDialog( PreferenceModel model, Component owner ){
        PreferenceDialog dialog = new PreferenceDialog( model );
        dialog.openDialog( owner, true );
    }
    
	private PreferenceTable table;
	
	/**
	 * Creates a new dialog without model.
	 */
	public PreferenceDialog(){
		this( null );
	}
	
	/**
	 * Creates a new dialog.
	 * @param model the model of the dialog, can be <code>null</code>
	 */
	public PreferenceDialog( PreferenceModel model ){
		super( false, null );
		table = new PreferenceTable();
		init( model );
	}
	
	/**
	 * Sets an editor for some type of values.
	 * @param type the type
	 * @param factory the factory for the new editors
	 * @see PreferenceTable#setEditorFactory(Path, PreferenceEditorFactory)
	 */
	public void setEditorFactory( Path type, PreferenceEditorFactory<?> factory ){
		table.setEditorFactory( type, factory );
	}
	
	/**
	 * Access to the table shown on this dialog. Clients should not change
	 * the {@link PreferenceModel} of the table. But they are allowed to customize
	 * the table, for example by adding new {@link PreferenceOperation}s.
	 * @return the table
	 */
	public PreferenceTable getTable() {
		return table;
	}
	
	@Override
	protected JComponent getContent() {
		return table;
	}
	
	@Override
	protected void setModelForContent( PreferenceModel model ) {
		table.setModel( model );
	}
}
