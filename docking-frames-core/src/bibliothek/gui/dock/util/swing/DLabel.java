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
package bibliothek.gui.dock.util.swing;

import javax.swing.JLabel;

import bibliothek.gui.dock.util.font.FontModifier;

/**
 * A modified label which can apply a {@link FontModifier} to its original
 * font. 
 * @author Benjamin Sigg
 */
public class DLabel extends JLabel{
    private FontUpdater updater;

    /**
     * Creates a new label
     */
    public DLabel(){
        updater = new FontUpdater( this );
    }
    
    @Override
    public void updateUI() {
        if( updater == null ){
            super.updateUI();
        }
        else{
            updater.enterUpdateUI();
            super.updateUI();
            updater.leaveUpdateUI();
        }
    }
    
    /**
     * Sets the modifier which is used to update the font of this label.
     * @param modifier the new modifier, can be <code>null</code>
     */
    public void setFontModifier( FontModifier modifier ) {
        updater.setFontModifier( modifier );
    }
    
    /**
     * Gets the modifier which is used to update the font of this label.
     * @return the modifier, may be <code>null</code>
     */
    public FontModifier getFontModifier() {
        return updater.getFontModifier();
    }
}
