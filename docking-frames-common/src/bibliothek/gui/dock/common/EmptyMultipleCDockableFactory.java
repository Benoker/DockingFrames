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
package bibliothek.gui.dock.common;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import bibliothek.util.xml.XElement;

/**
 * A factory that does not store anything.
 * @author Benjamin Sigg
 * @param <F> the type of dockable this factory handles.
 */
public abstract class EmptyMultipleCDockableFactory<F extends MultipleCDockable> implements MultipleCDockableFactory<F, MultipleCDockableLayout> {
    /**
     * Creates a new instance of the {@link MultipleCDockable} that is
     * represented by this factory.
     * @return the new dockable, might be <code>null</code>
     */
    public abstract F createDockable();
    
    public MultipleCDockableLayout create() {
        return new EmptyLayout();
    }

    public F read( MultipleCDockableLayout layout ) {
        return createDockable();
    }

    public MultipleCDockableLayout write( F dockable ) {
        return new EmptyLayout();
    }
    
    public boolean match( F dockable, MultipleCDockableLayout layout ){
    	return false;
    }
    
    /**
     * A layout that does not contain any value
     * @author Benjamin Sigg
     */
    private static class EmptyLayout implements MultipleCDockableLayout{
        public void readStream( DataInputStream in ) throws IOException {
            // ignore
        }

        public void readXML( XElement element ) {
            // ignore
        }

        public void writeStream( DataOutputStream out ) throws IOException {
            // ignore
        }

        public void writeXML( XElement element ) {
            // ignore
        }
    }
}
