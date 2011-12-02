package bibliothek.extension.gui.dock.preference;

import java.awt.Component;

import javax.swing.JComponent;

import bibliothek.extension.gui.dock.PreferenceTable;
import bibliothek.util.Path;

/**
 * A simple dialog showing the contents of one {@link PreferenceModel}. This dialog uses
 * a {@link PreferenceTable} to show the model. Clients can use the static method
 * {@link #openDialog(PreferenceModel, Component)} to quickly create and show a dialog.
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
        PreferenceDialog dialog = new PreferenceDialog( model, true );
        dialog.openDialog( owner, true );
    }
    
	private PreferenceTable table;
	
	/**
	 * Creates a new dialog without model.
     * @param destroyOnClose if set to <code>true</code>, then {@link #destroy()} is automatically called
     * if {@link #close()} is called. Clients have to call {@link #destroy()} manually if they are not
     * using {@link #openDialog(Component, boolean)}.
	 */
	public PreferenceDialog( boolean destroyOnClose ){
		this( null, destroyOnClose );
	}
	
	/**
	 * Creates a new dialog.
	 * @param model the model of the dialog, can be <code>null</code>
	 * @param destroyOnClose if set to <code>true</code>, then {@link #destroy()} is automatically called
     * if {@link #close()} is called. Clients have to call {@link #destroy()} manually if they are not
     * using {@link #openDialog(Component, boolean)}.
	 */
	public PreferenceDialog( PreferenceModel model, boolean destroyOnClose ){
		super( false, null, destroyOnClose );
		table = new PreferenceTable();
		init( model, destroyOnClose );
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
