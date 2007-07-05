package bibliothek.notes.view.menu;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JCheckBoxMenuItem;

public abstract class UpdateableCheckBoxMenuItem extends JCheckBoxMenuItem implements ActionListener{
	private boolean onUpdate = false;

	public UpdateableCheckBoxMenuItem(){
		addActionListener( new ActionListener(){
			public void actionPerformed( ActionEvent e ){
				if( !onUpdate ){
					UpdateableCheckBoxMenuItem.this.actionPerformed( e );
				}
			}
		});
	}
	
	public void setSilent( boolean state ){
		onUpdate = true;
		setSelected( state );
		onUpdate = false;
	}
}
