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

import javax.swing.Icon;

import bibliothek.gui.DockUI;

/**
 * A key for an operation which might be available for a {@link PreferenceEditor}
 * or a {@link PreferenceModel}.
 * @author Benjamin Sigg
 */
public class PreferenceOperation {
    /**
     * Operation for deleting a property.
     */
    public static final PreferenceOperation DELETE = new PreferenceOperation(
            "delete", 
            DockUI.getDefaultDockUI().getIcon( "delete.small" ),
            DockUI.getDefaultDockUI().getString( "preference.operation.delete" ));
    
    /**
     * Operation for setting a property to its default value
     */
    public static final PreferenceOperation DEFAULT = new PreferenceOperation(
            "default", 
            DockUI.getDefaultDockUI().getIcon( "default.small" ),
            DockUI.getDefaultDockUI().getString( "preference.operation.default" ));
    
    private String key;
    
    private Icon icon;
    private String description;
    
    /**
     * Creates a new operation.
     * @param key the unique identifier of this operation
     */
    public PreferenceOperation( String key ){
        if( key == null )
            throw new IllegalArgumentException( "key must not be null" );
        this.key = key;
    }
    
    /**
     * Creates a new operation.
     * @param key the unique identifier of this operation
     * @param icon an icon for this operation, should have a size of 10x10 pixels
     * @param description a small description of this operation
     */
    public PreferenceOperation( String key, Icon icon, String description ){
        this( key );
        setIcon( icon );
        setDescription( description );
    }
    
    @Override
    public int hashCode() {
        return key.hashCode();
    }

    @Override
    public boolean equals( Object obj ) {
        if( obj instanceof PreferenceOperation ){
            return key.equals( ((PreferenceOperation)obj).key );
        }
        
        return false;
    }
    
    /**
     * Gets an icon for this operation. The icon should have a size of 10x10 pixels.
     * @return the icon for this operation
     */
    public Icon getIcon() {
        return icon;
    }
    
    /**
     * Sets an icon for this operation. The icon should have a size of 10x10 pixels.
     * @param icon the new icon
     */
    public void setIcon( Icon icon ) {
        this.icon = icon;
    }
    
    /**
     * Gets a short human readable description of this operation.
     * @return the short description
     */
    public String getDescription() {
        return description;
    }
    
    /**
     * Sets a human readable description of this operation.
     * @param description the description
     */
    public void setDescription( String description ) {
        this.description = description;
    }
}
