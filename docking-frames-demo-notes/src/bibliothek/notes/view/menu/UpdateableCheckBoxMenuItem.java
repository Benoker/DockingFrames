package bibliothek.notes.view.menu;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JCheckBoxMenuItem;

/**
 * A {@link JCheckBoxMenuItem} who has an {@link ActionListener} to itself,
 * but whose state can be changed without notifying the listener.
 * @author Benjamin Sigg
 * @see #setSilent(boolean)
 */
public abstract class UpdateableCheckBoxMenuItem extends JCheckBoxMenuItem implements ActionListener{
    /** a flag indicating that the state is silently changed */
	private boolean onUpdate = false;

	/**
	 * Creates a new item.
	 */
	public UpdateableCheckBoxMenuItem(){
		addActionListener( new ActionListener(){
			public void actionPerformed( ActionEvent e ){
				if( !onUpdate ){
					UpdateableCheckBoxMenuItem.this.actionPerformed( e );
				}
			}
		});
	}
	
	/**
	 * Silently changes the state (selected or not) of this item. The
	 * {@link ActionListener#actionPerformed(ActionEvent) actionPerformed}-method
	 * of this item is not invoked when using this method.
	 * @param state the new state
	 */
	public void setSilent( boolean state ){
		onUpdate = true;
		setSelected( state );
		onUpdate = false;
	}
}
