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

import javax.swing.LookAndFeel;

import bibliothek.gui.Dockable;
import bibliothek.gui.dock.common.DestroyHook;
import bibliothek.gui.dock.common.CControl;
import bibliothek.gui.dock.facile.lookandfeel.DockableCollector;
import bibliothek.gui.dock.facile.menu.LookAndFeelMenuPiece;
import bibliothek.gui.dock.support.lookandfeel.ComponentCollector;
import bibliothek.gui.dock.support.util.ApplicationResource;
import bibliothek.util.xml.XElement;

/**
 * A menupiece that shows an entry for each available {@link LookAndFeel}. The
 * user can select a LookAndFeel which will be set immediately. 
 * @author Benjamin Sigg
 */
public class CLookAndFeelMenuPiece extends LookAndFeelMenuPiece implements DestroyHook{
    /** a collector collecting all {@link Dockable}s */
    private ComponentCollector dockableCollector;
    
    /**
     * Creates a new menu.
     * @param control needed to load the last {@link LookAndFeel}
     */
    public CLookAndFeelMenuPiece( CControl control ){
        super();
        control.addDestroyHook( this );
        dockableCollector = new DockableCollector( control.intern() );
        getList().addComponentCollector( dockableCollector );
        try {
            control.getResources().put( "CLookAndFeelMenuPiece", new ApplicationResource(){
                public void write( DataOutputStream out ) throws IOException {
                    getList().write( out );
                }
                public void read( DataInputStream in ) throws IOException {
                    getList().read( in );
                }
                public void writeXML( XElement element ) {
                    getList().writeXML( element );
                }
                public void readXML( XElement element ) {
                    getList().readXML( element );
                }
            });
        }
        catch( IOException e ) {
            System.err.println( "Non-lethal IO error:" );
            e.printStackTrace();
        }
    }
    
    @Override
    public void destroy() {
        super.destroy();
        getList().removeComponentCollector( dockableCollector );
    }
}
