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
package bibliothek.gui.dock.facile.menu;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.JMenuItem;

import bibliothek.extension.gui.dock.DockingFramesPreference;
import bibliothek.extension.gui.dock.preference.PreferenceTreeDialog;
import bibliothek.extension.gui.dock.preference.PreferenceTreeModel;
import bibliothek.gui.DockController;
import bibliothek.gui.dock.support.menu.BaseMenuPiece;
import bibliothek.util.ClientOnly;

/**
 * A menu piece that allows the user to change the preferences of the framework.
 * @author Benjamin Sigg
 */
@ClientOnly
public class PreferenceMenuPiece extends BaseMenuPiece{
    private DockController controller;
    
    private PreferenceTreeModel model;
    
    private MenuPieceText text;
    
    private AbstractAction action = new AbstractAction(){
        public void actionPerformed( ActionEvent e ) {
            PreferenceTreeModel model = getModel();
            PreferenceTreeDialog dialog = new PreferenceTreeDialog( model, true );
            dialog.openDialog( controller.findRootWindow(), true );
        }
    };
    
    /**
     * Creates a new unlinked menu piece
     */
    protected PreferenceMenuPiece(){
        this( null );
    }
    
    /**
     * Creates a new menu piece
     * @param controller the controller for which this piece will work
     */
    public PreferenceMenuPiece( DockController controller ){
    	text = new MenuPieceText( "PreferenceMenuPiece.text", this ){
			protected void changed( String oldValue, String newValue ){
				action.putValue( AbstractAction.NAME, newValue );
			}
		};
        add( new JMenuItem( action ) );
        setController( controller );
    }
    
    /**
     * Creates a new model for this piece.
     * @return the model
     */
    protected PreferenceTreeModel createModel(){
        if( controller == null )
            return new PreferenceTreeModel( controller );
        else
            return new DockingFramesPreference( controller );
    }
    
    public PreferenceTreeModel getModel() {
        if( model == null )
            model = createModel();
        return model;
    }
    
    @Override
    public void bind(){
    	super.bind();
    	text.setController( controller );
    }
    
    @Override
    public void unbind(){
    	super.unbind();
    	text.setController( null );
    }
    
    /**
     * Sets the controller for which this piece works.
     * @param controller the controller
     */
    public void setController( DockController controller ) {
        this.controller = controller;
        action.setEnabled( controller != null );
        model = null;
        if( isBound() ){
        	text.setController( controller );
        }
    }
    
    /**
     * Gets the controller for which this piece works.
     * @return the controller
     */
    public DockController getController() {
        return controller;
    }
}
