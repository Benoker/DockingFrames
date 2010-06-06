/*
 * Bibliothek - DockingFrames
 * Library built on Java/Swing, allows the user to "drag and drop"
 * panels containing any Swing-Component the developer likes to add.
 * 
 * Copyright (C) 2008 Benjamin Sigg
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
package bibliothek.extension.gui.dock.preference;

import java.awt.Component;

import javax.swing.JComponent;
import javax.swing.tree.TreeCellRenderer;

import bibliothek.extension.gui.dock.PreferenceTable;
import bibliothek.extension.gui.dock.PreferenceTreePanel;
import bibliothek.util.Path;

/**
 * A simple dialog showing a {@link PreferenceTreeModel} on a {@link PreferenceTreePanel}.
 * @author Benjamin Sigg
 */
public class PreferenceTreeDialog extends AbstractPreferenceDialog<PreferenceTreeModel>{
    /**
     * Shows a modal dialog on which the user can change the preferences of
     * <code>controller</code>. This method will call {@link PreferenceModel#read()} and
     * {@link PreferenceModel#write()} to reset or to apply the changes of the user.
     * @param model the model to show on the dialog
     * @param owner the owner of the dialog
     */
    public static void openDialog( PreferenceTreeModel model, Component owner ){
        PreferenceTreeDialog dialog = new PreferenceTreeDialog( model );
        dialog.openDialog( owner, true );
    }
    
    /** shows the model of this dialog */
    private PreferenceTreePanel panel;
    
    /**
     * Creates a new dialog without model.
     */
    public PreferenceTreeDialog(){
    	this( null );
    }
    
    /**
     * Creates a new dialog.
     * @param model the model to show on the dialog
     */
    public PreferenceTreeDialog( PreferenceTreeModel model ){
    	super( false, null );
    	panel = new PreferenceTreePanel();
    	init( model );
    }

	/**
	 * Sets an editor for some type of values.
	 * @param type the type
	 * @param factory the factory for the new editors
	 * @see PreferenceTable#setEditorFactory(Path, PreferenceEditorFactory)
	 */
	public void setEditorFactory( Path type, PreferenceEditorFactory<?> factory ){
		panel.setEditorFactory( type, factory );
	}
	
	/**
	 * Access to the {@link PreferenceTreePanel} which is shown on this
	 * dialog. Clients should not change the {@link PreferenceTreeModel} of
	 * that panel. But they are allowed to customize the panel, for example
	 * to change the {@link TreeCellRenderer}.
	 * @return the panel of this dialog
	 */
	public PreferenceTreePanel getPanel() {
		return panel;
	}
	
    @Override
    protected JComponent getContent() {
    	return panel;
    }
    
    @Override
    protected void setModelForContent( PreferenceTreeModel model ) {
    	panel.setModel( model );
    }
}
