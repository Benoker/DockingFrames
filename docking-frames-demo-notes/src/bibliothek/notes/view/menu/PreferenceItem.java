package bibliothek.notes.view.menu;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JFrame;
import javax.swing.JMenuItem;

import bibliothek.extension.gui.dock.preference.PreferenceTreeDialog;
import bibliothek.extension.gui.dock.preference.PreferenceTreeModel;
import bibliothek.gui.DockController;

/**
 * This item can show a preference dialog for one {@link DockController}.
 * @author Benjamin Sigg
 */
public class PreferenceItem extends JMenuItem implements ActionListener{
	/** the preferences */
	private PreferenceTreeModel model;
	
	/** the owner of this item */
	private JFrame frame;
	
	/**
	 * Creates a new item.
	 * @param frame the owner of this item
	 * @param preferences the preferences to show
	 */
	public PreferenceItem( JFrame frame, PreferenceTreeModel preferences ){
		this.frame = frame;
		this.model = preferences;
		
		setText( "Preferences" );
		setToolTipText( "Change the behavior of this application" );
		
		addActionListener( this );
	}
	
	public void actionPerformed(ActionEvent e) {
		PreferenceTreeDialog.openDialog( model, frame );
	}
}
