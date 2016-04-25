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
package bibliothek.gui.dock.common.menu;

import java.awt.Component;
import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.JMenuItem;

import bibliothek.extension.gui.dock.preference.DefaultPreferenceModel;
import bibliothek.extension.gui.dock.preference.PreferenceDialog;
import bibliothek.extension.gui.dock.preference.PreferenceModel;
import bibliothek.extension.gui.dock.preference.PreferenceTreeDialog;
import bibliothek.extension.gui.dock.preference.PreferenceTreeModel;
import bibliothek.gui.dock.common.CControl;
import bibliothek.gui.dock.common.CPreferenceModel;
import bibliothek.gui.dock.facile.menu.MenuPieceText;
import bibliothek.gui.dock.support.menu.BaseMenuPiece;

/**
 * A menu piece that shows an entry for opening the preferences-dialog. The
 * {@link PreferenceModel model} to show on the dialog can either be set
 * explicitly using {@link #setModel(PreferenceModel)}, or else will be read
 * from {@link CControl#getPreferenceModel()}.<br>
 * Note: clients can use {@link #setup(CControl)} to ensure that the
 * {@link CControl} has a model.
 * @author Benjamin Sigg
 */
public class CPreferenceMenuPiece extends BaseMenuPiece{
	/** text for this menu */
	private MenuPieceText text;
	
	/**
	 * Creates a new {@link CPreferenceMenuPiece}. Reads the model of <code>control</code>,
	 * if <code>control</code> has no model then a new {@link PreferenceModel} will
	 * be created and set.
	 * @param control the control whose model will be shown
	 * @return a new menu piece
	 * @see CControl#getPreferenceModel()
	 * @see CControl#setPreferenceModel(PreferenceModel)
	 */
	public static CPreferenceMenuPiece setup( CControl control ){
		if( control.getPreferenceModel() == null )
			control.setPreferenceModel( new CPreferenceModel( control ));
		
		return new CPreferenceMenuPiece( control );
	}
	
	/** where to store the model */
    private CControl control;

    /** the model which is to be used on this dialog */
    private PreferenceModel model;

    private AbstractAction action = new AbstractAction(){
        public void actionPerformed( ActionEvent e ) {
        	action();
        }
    };
    
    /**
     * Creates a new menu piece.
     * @param control the control for which this piece works, not <code>null</code>
     */
    public CPreferenceMenuPiece( CControl control ) {
    	if( control == null )
    		throw new IllegalArgumentException( "control must not be null" );
    	
        this.control = control;
        
        text = new MenuPieceText( "PreferenceMenuPiece.text", this ){
			protected void changed( String oldValue, String newValue ){
				action.putValue( AbstractAction.NAME, newValue );	
			}
		};
        
        add( new JMenuItem( action ) );
    }
    
    @Override
    public void bind(){
    	super.bind();
    	text.setController( control.getController() );
    }
    
    @Override
    public void unbind(){
    	super.unbind();
    	text.setController( null );
    }
    
    /**
     * Explicitly sets the model which will be shown on the dialog. If 
     * <code>null</code> is set, then this menu will try to show
     * {@link CControl#getPreferenceModel()}.
     * @param model the model to use or <code>null</code>
     */
    public void setModel( PreferenceModel model ) {
		this.model = model;
	}
    
    /**
     * Gets the model which was explicitly set.
     * @return the model or <code>null</code>
     * @see #setModel(PreferenceModel)
     */
    public PreferenceModel getModel() {
		return model;
	}
    
    /**
     * Opens a dialog with the current {@link PreferenceModel}.
     */
    protected void action(){
    	PreferenceModel model = this.model;
    	if( model == null )
    		model = control.getPreferenceModel();
    	if( model == null )
    		model = new DefaultPreferenceModel( control.getController() );
    	
    	Component owner = control.intern().getController().findRootWindow();
    	control.getPreferences().load( model, false );
    	
    	if( model instanceof PreferenceTreeModel ){
    		PreferenceTreeDialog.openDialog( (PreferenceTreeModel)model, owner );
    	}
    	else{
    		PreferenceDialog.openDialog( model, owner );
    	}
    	
    	control.getPreferences().store( model );
    }
}
