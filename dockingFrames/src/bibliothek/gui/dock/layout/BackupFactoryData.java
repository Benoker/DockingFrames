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
package bibliothek.gui.dock.layout;

/**
 * Some client specified data and an identifier for that data. This class
 * is immutable.
 * @author Benjamin Sigg
 * @param <D> the kind of data this {@link BackupFactoryData} stores.
 */
public class BackupFactoryData<D> {
    private String identifier;
    private D data;
    
    /**
     * Creates a new {@link BackupFactoryData}
     * @param identifier the identifier of this piece of data, can be <code>null</code>
     * @param data the client specified data, might be <code>null</code>
     */
    public BackupFactoryData( String identifier, D data ){        
        if( identifier == null )
            throw new IllegalArgumentException( "id must not be null" );
        
        this.identifier = identifier;
        this.data = data;
    }
    
    /**
     * Gets the identifier of this data.
     * @return the identifier, can be <code>null</code>
     */
    public String getIdentifier() {
        return identifier;
    }
    
    /**
     * Gets the data that is wrapped up by this {@link BackupFactoryData}.
     * @return the data, can be <code>null</code>
     */
    public D getData() {
        return data;
    }
}
