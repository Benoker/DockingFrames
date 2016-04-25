/*
 * Bibliothek - DockingFrames
 * Library built on Java/Swing, allows the user to "drag and drop"
 * panels containing any Swing-Component the developer likes to add.
 * 
 * Copyright (C) 2007 Benjamin Sigg
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
package bibliothek.paint.view;

import java.awt.Container;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.KeyEvent;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import bibliothek.gui.dock.common.DefaultSingleCDockable;
import bibliothek.gui.dock.common.SingleCDockable;
import bibliothek.gui.dock.common.action.CButton;
import bibliothek.paint.model.Picture;
import bibliothek.paint.model.PictureRepository;
import bibliothek.paint.model.PictureRepositoryListener;
import bibliothek.paint.util.Resources;

/**
 * A {@link SingleCDockable} showing the contents of a {@link PictureRepository}.
 * @author Benjamin Sigg
 *
 */
public class PictureRepositoryDockable extends DefaultSingleCDockable{
	/** the list showing the names of the pictures */
    private JList list;
    /** a model containing all pictures */
    private DefaultListModel pictureListModel;
    
    /** the repository */
    private PictureRepository pictures;
    
    /** button used to show a picture */
    private CButton pictureShow;
    /** button used to add a new picture */
    private CButton pictureNew;
    /** button used to delete a picture */
    private CButton pictureDelete;
    
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
        setMaximizable( true );
        setExternalizable( true );
        setTitleText( "Pictures" );
        setTitleIcon( Resources.getIcon( "dockable.list" ) );
        
        pictureListModel = new DefaultListModel();
        list = new JList( pictureListModel );
        list.setSelectionMode( ListSelectionModel.SINGLE_SELECTION );
        
        Container content = getContentPane();
        content.setLayout( new GridBagLayout() );
        content.add( new JScrollPane( list ), new GridBagConstraints( 0, 0, 1, 1, 1.0, 100.0,
                GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                new Insets( 1, 1, 1, 1 ), 0, 0 ) );
        
        pictureNew = new CButton(){
        	@Override
        	protected void action(){
        		String name = askForName();
                if( name != null ){
                    pictures.add( new Picture( name ) );
                }
        	}
        };
        pictureNew.setText( "New picture" );
        pictureNew.setTooltip( "Creates a new picture" );
        pictureNew.setIcon( Resources.getIcon( "picture.add" ) );
        
        pictureDelete = new CButton(){
        	@Override
        	protected void action(){
        		Picture picture = (Picture)list.getSelectedValue();
                if( picture != null ){
                	pictures.remove( picture );
                }
        	}
        };
        pictureDelete.setText( "Delete picture" );
        pictureDelete.setTooltip( "Delete the selected picture" );
        pictureDelete.setIcon( Resources.getIcon( "picture.remove" ) );
        pictureDelete.setEnabled( false );
        
        pictureShow = new CButton(){
        	@Override
        	protected void action(){
        		Picture picture = (Picture)list.getSelectedValue();
                if( picture != null )
                    manager.open( picture );
        	}
        };
        pictureShow.setText( "Show picture" );
        pictureShow.setTooltip( "Open a new view displaying the selected picture" );
        pictureShow.setIcon( Resources.getIcon( "picture.show" ) );
        pictureShow.setAccelerator( KeyStroke.getKeyStroke( KeyEvent.VK_ENTER, 0 ));
        pictureShow.setEnabled( false );
        
        addAction( pictureNew );
        addAction( pictureDelete );
        addAction( pictureShow );
        addSeparator();
        
        pictures.addListener( new PictureRepositoryListener(){
        	public void pictureAdded( Picture picture ){
        		pictureListModel.addElement( picture );
        	}
        	public void pictureRemoved( Picture picture ){
        		pictureListModel.removeElement( picture );
        	}
        });
        list.addListSelectionListener( new ListSelectionListener(){
            public void valueChanged( ListSelectionEvent e ) {
            	boolean enable = list.getSelectedValue() != null;
                pictureDelete.setEnabled( enable );
                pictureShow.setEnabled( enable );
            }
        });
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