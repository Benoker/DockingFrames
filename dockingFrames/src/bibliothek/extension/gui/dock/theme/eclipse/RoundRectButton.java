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
package bibliothek.extension.gui.dock.theme.eclipse;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;

import javax.swing.Icon;
import javax.swing.JComponent;

import bibliothek.gui.dock.themes.basic.action.BasicButtonModel;
import bibliothek.gui.dock.themes.basic.action.BasicTrigger;
import bibliothek.util.Colors;

/**
 * A button that has a round rect shape.
 * @author Benjamin Sigg
 */
public class RoundRectButton extends JComponent{
    private BasicButtonModel model;
    
    /**
     * Creates a new roundrect button.
     * @param trigger a trigger which gets informed when the user clicks the
     * button
     */
    public RoundRectButton( BasicTrigger trigger ){
        model = new BasicButtonModel( this, trigger, true );
        setOpaque( false );
        
        addFocusListener( new FocusListener(){
            public void focusGained( FocusEvent e ) {
                repaint();
            }
            public void focusLost( FocusEvent e ) {
                repaint();
            }
        });
    }
    
    /**
     * Gets the model that is used by this button.
     * @return the set of properties of this button
     */
    public BasicButtonModel getModel() {
        return model;
    }
    
    @Override
    public Dimension getPreferredSize() {
        if( isPreferredSizeSet() )
            return super.getPreferredSize();
        
        Dimension size = model.getMaxIconSize();
        size.width = Math.max( 10, size.width + 4 );
        size.height = Math.max( 10, size.height + 4 );
        return size;
    }
    
    @Override
    protected void paintComponent( Graphics g ) {
        Color background = getBackground();
        
        Color border = null;
        if( model.isMousePressed() ){
            border = Colors.diffMirror( background, 0.3 );
            background = Colors.undiffMirror( background, 0.6 );
        }
        else if( model.isSelected() || model.isMouseInside() ){
            border = Colors.diffMirror( background, 0.3 );
            background = Colors.undiffMirror( background, 0.3 );
        }
        
        int w = getWidth()-1;
        int h = getHeight()-1;
        
        if( border != null ){
            g.setColor( background );
            g.fillRoundRect( 0, 0, w, h, 4, 4 );
            
            g.setColor( border );
            g.drawRoundRect( 0, 0, w, h, 4, 4 );
        }
        
        Icon icon = model.getPaintIcon();
        if( icon != null ){
            icon.paintIcon( this, g, (w +1 - icon.getIconWidth())/2, (h +1 - icon.getIconHeight())/2 );
        }
        
        if( hasFocus() && isFocusable() && isEnabled() ){
            g.setColor( Colors.diffMirror( background, 0.4 ) );
            // top left
            g.drawLine( 3, 2, 4, 2 );
            g.drawLine( 2, 3, 2, 4 );
            
            // top right
            g.drawLine( 3, w-2, 4, w-2 );
            g.drawLine( 2, w-3, 2, w-4 );
            
            // bottom left
            g.drawLine( h-3, 2, h-4, 2 );
            g.drawLine( h-2, 3, h-2, 4 );
            
            // bottom right
            g.drawLine( h-3, w-2, h-4, w-2 );
            g.drawLine( h-2, w-3, h-2, w-4 );
        }
    }
}
