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
import bibliothek.notes.view.themes.*;
import bibliothek.util.xml.XElement;

/**
 * A menu that allows the selection of a new {@link DockTheme}.
 * @author Benjamin Sigg
 *
 */
public class ThemeMenu extends JMenu{
    /** the root of the dock-tree */
	private DockFrontend frontend;
	/** ensures that only one of the radio-items is selected */
	private ButtonGroup group;
	
	/** the children of this menu */
	private List<JRadioButtonMenuItem> items = new ArrayList<JRadioButtonMenuItem>();
	/** the themes available to the user */
	private List<DockTheme> themes = new ArrayList<DockTheme>();
	
	/**
	 * Creates a new menu
	 * @param frontend the root of the dock-tree
	 */
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
		add( createItem( "Eclipse", new NoteEclipseTheme() ));
	}
	
	/**
	 * Creates a new item of this menu. The new item allows to select
	 * the {@link DockTheme} <code>theme</code>.
	 * @param title the text of the item
	 * @param theme the theme that can be selected by clicking onto the new item
	 * @return the new item
	 */
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
	
	/**
     * Writes which theme is currently selected.
     * @param out the stream to write into
     * @throws IOException if the method can't write into <code>out</code>
     */
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
	
	/**
	 * Reads which theme was selected when the application shut down
	 * the last time.
	 * @param in the stream to read from
	 * @throws IOException if <code>in</code> can't be read
	 */
	public void read( DataInputStream in ) throws IOException{
		int index = in.readInt();
		if( index < 0 )
			index = 0;
		
		items.get( index ).setSelected( true );
		frontend.getController().setTheme( themes.get( index ) );
	}
	
	/**
     * Writes which theme is currently selected.
     * @param element the xml-element to write into
     */
    public void writeXML( XElement element ){
        int index = -1;
        for( int i = 0, n = items.size(); i<n; i++ ){
            if( items.get( i ).isSelected() ){
                index = i;
                break;
            }
        }
        element.setInt( index );
    }
    
    /**
     * Reads which theme was selected when the application shut down
     * the last time.
     * @param element the xml-element to read from
     */
    public void readXML( XElement element ){
        int index = element.getInt();
        if( index < 0 )
            index = 0;
        
        items.get( index ).setSelected( true );
        frontend.getController().setTheme( themes.get( index ) );
    }
    
}
