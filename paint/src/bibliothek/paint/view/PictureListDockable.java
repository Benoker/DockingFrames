package bibliothek.paint.view;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import bibliothek.gui.dock.facile.FSingleDockable;
import bibliothek.paint.model.Picture;

public class PictureListDockable extends FSingleDockable{
    private JList list;
    private DefaultListModel pictures;
    
    public PictureListDockable( final ViewManager manager ){
        super( "PictureListDockable" );
        
        setCloseable( false );
        setMinimizable( true );
        setMaximizable( false );
        setExternalizable( true );
        setTitleText( "Pictures" );
        
        pictures = new DefaultListModel();
        list = new JList( pictures );
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
                    Picture picture = new Picture( name );
                    pictures.addElement( picture );
                    manager.open( picture );
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
                    pictures.removeElement( picture );
                    manager.closeAll( picture );
                }
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
    
    public Picture getPicture( String name ){
        for( int i = 0, n = pictures.getSize(); i<n; i++ ){
            Picture picture = (Picture)pictures.getElementAt( i );
            if( picture.getName().equals( name ))
                return picture;
        }
        return null;
    }
    
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
            
            if( getPicture( name ) != null ){
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
