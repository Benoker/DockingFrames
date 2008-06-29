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

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import bibliothek.extension.gui.dock.preference.PreferenceModel;
import bibliothek.extension.gui.dock.preference.PreferenceStorage;
import bibliothek.extension.gui.dock.preference.PreferenceTreeModel;
import bibliothek.gui.dock.common.CControl;
import bibliothek.gui.dock.facile.menu.PreferenceMenuPiece;
import bibliothek.gui.dock.support.util.ApplicationResource;
import bibliothek.util.xml.XElement;

/**
 * A menu piece that shows an entry for opening the preferences-dialog. This
 * piece will register an {@link ApplicationResource} at the {@link CControl}
 * in order to store the preferences. 
 * @author Benjamin Sigg
 */
public class CPreferenceMenuPiece extends PreferenceMenuPiece{
    private CControl control;
    private PreferenceStorage storage;
    
    /**
     * Creates a new menu piece.
     * @param control the control for which this piece works
     */
    public CPreferenceMenuPiece( CControl control ) {
        super( control.intern().getController() );
        
        this.control = control;
        storage = new PreferenceStorage();
        
        try {
            control.getResources().put( "CPreferenceMenuPiece", new ApplicationResource(){
                public void read( DataInputStream in ) throws IOException {
                    storage.read( in );
                    PreferenceTreeModel model = getModel();
                    storage.load( model, false );
                    storage.clear();
                    model.write();
                }

                public void readXML( XElement element ) {
                    storage.readXML( element );
                    PreferenceTreeModel model = getModel();
                    storage.load( model, false );
                    storage.clear();
                    model.write();
                }

                public void write( DataOutputStream out ) throws IOException {
                    PreferenceModel model = getModel();
                    model.read();
                    storage.store( getModel() );
                    storage.write( out );
                    storage.clear();
                }

                public void writeXML( XElement element ) {
                    PreferenceModel model = getModel();
                    model.read();
                    storage.store( getModel() );
                    storage.writeXML( element );
                    storage.clear();
                }
            });
        }
        catch( IOException e ) {
            System.err.println( "Non-lethal IO-error:" );
            e.printStackTrace();
        }
    }

}
