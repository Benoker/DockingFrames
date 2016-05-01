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
package bibliothek.gui.dock.util.swing;

import java.awt.Font;
import java.awt.event.HierarchyEvent;
import java.awt.event.HierarchyListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.JComponent;

import bibliothek.gui.dock.util.font.FontModifier;

/**
 * A {@link FontUpdater} wraps around a {@link JComponent} and changes
 * the font of the component using a {@link FontModifier}.<br>
 * Components that use this updater must call {@link #enterUpdateUI()}
 * end {@link #leaveUpdateUI()} before and after {@link JComponent#updateUI()}
 * is called.<br>
 * Components should create an instance of this updater before they are
 * added to any other component.
 * @author Benjamin Sigg
 *
 */
public class FontUpdater {
    /** tells how to change the font */
    private FontModifier modifier;
    
    /** unchanged version of the font */
    private Font original;
    
    /** whether {@link #updateFont()} is currently running */
    private boolean onFontUpdate = false;
    
    /** the component to observe */
    private JComponent component;
    
    /**
     * Creates a new updater
     * @param component the component whose font will be changed
     */
    public FontUpdater( JComponent component ){
        this.component = component;
        
        component.addPropertyChangeListener( "font", new PropertyChangeListener(){
            public void propertyChange( PropertyChangeEvent evt ) {
                if( !onFontUpdate ){
                    original = (Font)evt.getNewValue();
                    updateFont();
                }
            }
        });
        
        component.addHierarchyListener( new HierarchyListener(){
            public void hierarchyChanged( HierarchyEvent e ) {
                // the font can be based on a font from a parent component, 
                // so every change in the hierarchy can result in a new font
                updateFont();
            }
        });
        
        original = component.getFont();
    }
    
    /**
     * Informs this updater that {@link JComponent#updateUI()} is
     * about to start.
     */
    public void enterUpdateUI(){
        try{
            onFontUpdate = false;
            component.setFont( original );
        }
        finally{
            onFontUpdate = false;
        }
    }
    
    /**
     * Informs this updater that {@link JComponent#updateUI()} has
     * been executed.
     */
    public void leaveUpdateUI(){
        updateFont();
    }

    /**
     * Sets the modifier which is used to update the font of this label.
     * @param modifier the new modifier, can be <code>null</code>
     */
    public void setFontModifier( FontModifier modifier ) {
        if( this.modifier != modifier ){
            this.modifier = modifier;
            updateFont();
        }
    }
    
    /**
     * Gets the modifier which is used to update the font of this label.
     * @return the modifier, may be <code>null</code>
     */
    public FontModifier getFontModifier() {
        return modifier;
    }
    
    private void updateFont(){
        try{
            onFontUpdate = true;
            component.setFont( original );
            Font base = component.getFont();
            Font modified;
            if( base == null || modifier == null ){
                modified = original;
            }
            else{
                modified = modifier.modify( base );
            }
            component.setFont( modified );
        }
        finally{
            onFontUpdate = false;
        }
    }
}
