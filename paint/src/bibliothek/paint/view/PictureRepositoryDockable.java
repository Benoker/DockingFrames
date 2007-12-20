package bibliothek.paint.view;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import bibliothek.gui.dock.facile.FSingleDockable;
import bibliothek.paint.model.Picture;
import bibliothek.paint.model.PictureRepository;
import bibliothek.paint.model.PictureRepositoryListener;

/**
 * A {@link FSingleDockable} showing the contents of a {@link PictureRepository}.
 * @author Benjamin Sigg
 *
 */
public class PictureRepositoryDockable extends FSingleDockable{
	/** the list showing the names of the pictures */
    private JList list;
    /** a model containing all pictures */
    private DefaultListModel pictureListModel;
    
    /** the repository */
    private PictureRepository pictures;
    
    /**
     * Creates a new dockable.
     * @param manager the manager used to handle all operations concerning
     * dockables.
     */
    public PictureRepositoryDockable( final ViewManager manager ){
        super( "PictureListDockable" );
        pictures = manager.getPictures();
        
        setCloseable( true );
        setMinimizable( true );
        setMaximizable( false );
        setExternalizable( true );
        setTitleText( "Pictures" );
        
        pictureListModel = new DefaultListModel();
        list = new JList( pictureListModel );
        list.setSelectionMode( ListSelectionModel.SINGLE_SELECTION );
        
        Container content = getContentPane();
        content.setLayout( new GridBagLayout() );
        content.add( new JScrollPane( list ), new GridBagConstraints( 0, 0, 1, 1, 1.0, 100.0,
                GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                new Insets( 1, 1, 1, 1 ), 0, 0 ) );
        
        JPanel buttons = new JPanel( new GridLayout( 1, 3 ) );
        content.add( buttons, new GridBagConstraints( 0, 1, 1, 1, 1.0, 1.0,
                GridBagConstraints.LAST_LINE_END, GridBagConstraints.NONE,
                new Insets( 1, 1, 1, 1 ), 0, 0 ));
        
        
        createButton( buttons, "New", "Creates a new picture", new ActionListener(){
            public void actionPerformed( ActionEvent e ) {
                String name = askForName();
                if( name != null ){
                    pictures.add( new Picture( name ) );
                }
            }
        });
        
        createPictureButton( buttons, "Open", "Open a new view displaying the selected picture", new ActionListener(){
            public void actionPerformed( ActionEvent e ) {
                Picture picture = (Picture)list.getSelectedValue();
                if( picture != null )
                    manager.open( picture );
            }
        });
        
        createPictureButton( buttons, "Delete", "Delete the selected picture", new ActionListener(){
            public void actionPerformed( ActionEvent e ) {
                Picture picture = (Picture)list.getSelectedValue();
                if( picture != null ){
                	pictures.remove( picture );
                }
            }
        });
        
        pictures.addListener( new PictureRepositoryListener(){
        	public void pictureAdded( Picture picture ){
        		pictureListModel.addElement( picture );
        	}
        	public void pictureRemoved( Picture picture ){
        		pictureListModel.removeElement( picture );
        	}
        });
    }
    
    private void createPictureButton( Container parent, String text, String tooltip, ActionListener listener ){
        final JButton button = createButton( parent, text, tooltip, listener );
        button.setEnabled( list.getSelectedValue() != null );
        list.addListSelectionListener( new ListSelectionListener(){
            public void valueChanged( ListSelectionEvent e ) {
                button.setEnabled( list.getSelectedValue() != null );
            }
        });
    }
    
    private JButton createButton( Container parent, String text, String tooltip, ActionListener listener ){
        JButton button = new JButton( text );
        button.setToolTipText( tooltip );
        parent.add( button );
        button.addActionListener( listener );
        return button;
    }
    
    /**
     * Opens a dialog and asks the user to input a name for a new picture. The
     * user can't choose a name of a picture that already exists.
     * @return the name or <code>null</code>
     */
    public String askForName(){
        String name = null;
        String message = "Please choose a name for the new picture";
        while( true ){
            name = JOptionPane.showInputDialog(
                    getContentPane(),
                    message,
                    "New picture",
                    JOptionPane.QUESTION_MESSAGE );
            
            if( name == null )
                return null;
            
            name = name.trim();
            
            if( pictures.getPicture( name ) != null ){
                message = "There exists already a picture with the name \"" + name + "\"\n" +
                    "Please choose another name";
            }
            else if( !name.matches( ".*([a-zA-Z]|[0-9])+.*" ) ){
                message = "The name must containt at least one letter or digit";                
            }
            else
                return name;
        }
    }
}
