package bibliothek.notes.view.menu;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.swing.ButtonGroup;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JRadioButtonMenuItem;

import bibliothek.gui.DockFrontend;
import bibliothek.gui.DockTheme;
import bibliothek.gui.dock.themes.NoStackTheme;
import bibliothek.notes.view.themes.NoteBasicTheme;
import bibliothek.notes.view.themes.NoteBubbleTheme;
import bibliothek.notes.view.themes.NoteFlatTheme;
import bibliothek.notes.view.themes.NoteSmoothTheme;

public class ThemeMenu extends JMenu{
	private DockFrontend frontend;
	private ButtonGroup group;
	
	private List<JRadioButtonMenuItem> items = new ArrayList<JRadioButtonMenuItem>();
	private List<DockTheme> themes = new ArrayList<DockTheme>();
	
	public ThemeMenu( DockFrontend frontend ){
		this.frontend = frontend;
		setText( "Theme" );
		
		group = new ButtonGroup();
		
		add( createItem( "BasicTheme", new NoteBasicTheme() ));
		add( createItem( "NoStack-BasicTheme", new NoStackTheme( new NoteBasicTheme() ) ));
		add( createItem( "SmoothTheme", new NoteSmoothTheme() ));
		add( createItem( "NoStack-SmoothTheme", new NoStackTheme( new NoteSmoothTheme() )));
		add( createItem( "FlatTheme", new NoteFlatTheme() ));
		add( createItem( "NoStack-FlatTheme", new NoStackTheme( new NoteFlatTheme() )));
		add( createItem( "BubbleTheme", new NoteBubbleTheme() ));
		add( createItem( "NoStack-BubbleTheme", new NoStackTheme( new NoteBubbleTheme() )));
	}
	
	private JMenuItem createItem( String title, final DockTheme theme ){
		JRadioButtonMenuItem item = new JRadioButtonMenuItem( title );
		items.add( item );
		themes.add( theme );
		
		group.add( item );
		item.addActionListener( new ActionListener(){
			public void actionPerformed( ActionEvent e ){
				frontend.getController().setTheme( theme );
			}
		});
		return item;
	}
	
	public void read( DataInputStream in ) throws IOException{
		int index = in.readInt();
		if( index < 0 )
			index = 0;
		
		items.get( index ).setSelected( true );
		frontend.getController().setTheme( themes.get( index ) );
	}
	
	public void write( DataOutputStream out ) throws IOException{
		int index = -1;
		for( int i = 0, n = items.size(); i<n; i++ ){
			if( items.get( i ).isSelected() ){
				index = i;
				break;
			}
		}
		out.writeInt( index );
	}
}
