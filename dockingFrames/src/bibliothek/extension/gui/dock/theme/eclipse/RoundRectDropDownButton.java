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
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;

import javax.swing.Icon;
import javax.swing.JComponent;

import bibliothek.gui.dock.action.DropDownAction;
import bibliothek.gui.dock.themes.basic.action.BasicDropDownButtonHandler;
import bibliothek.gui.dock.themes.basic.action.BasicDropDownButtonModel;
import bibliothek.gui.dock.util.DockUtilities;
import bibliothek.util.Colors;

/**
 * A button with a shape of a roundrect, displaying a {@link DropDownAction}.
 * @author Benjamin Sigg
 */
public class RoundRectDropDownButton extends JComponent {
    /** a model containing all information needed to paint this button */
    private BasicDropDownButtonModel model;
    
    /** a handler reacting if this button is pressed */
    private BasicDropDownButtonHandler handler;
    
    /** the icon to show for the area in which the popup-menu could be opened */
    private Icon dropIcon;
    /** a disabled version of {@link #dropIcon} */
    private Icon disabledDropIcon;
    
    /**
     * Creates a new button
     * @param handler a handler used to announce that this button is clicked
     */
    public RoundRectDropDownButton( BasicDropDownButtonHandler handler ){
        this.handler = handler;
        model = new BasicDropDownButtonModel( this, handler, true ){
            @Override
            protected boolean inDropDownArea( int x, int y ) {
                return RoundRectDropDownButton.this.inDropDownArea( x, y );
            }
            @Override
            public void changed() {
                revalidate();
                super.changed();
            }
        };
        
        setOpaque( false );
        
        dropIcon = createDropIcon();
        
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
     * Gets the model which represents the inner states of this button.
     * @return the model
     */
    public BasicDropDownButtonModel getModel() {
        return model;
    }
    
    @Override
    public Dimension getPreferredSize() {
        if( isPreferredSizeSet() )
            return super.getPreferredSize();
        
        Dimension icon = model.getMaxIconSize();
        int w = Math.max( icon.width+4, 10 );
        int h = Math.max( icon.height+4, 10 );
        
        if( model.getOrientation().isHorizontal() )
            return new Dimension( w + 6 + dropIcon.getIconWidth(), h );
        else
            return new Dimension( w, h + 6 + dropIcon.getIconHeight() );    
    }
    
    @Override
    protected void paintComponent( Graphics g ) {
        Icon drop = dropIcon;
        if( !isEnabled() ){
            if( disabledDropIcon == null )
                disabledDropIcon = DockUtilities.disabledIcon( this, dropIcon );
            drop = disabledDropIcon;
        }
        
        Color background = getBackground();
        
        Color border = null;
        if( model.isMousePressed() ){
            border = Colors.diffMirror( background, 0.3 );
            background = Colors.undiffMirror( background, 0.8 );
        }
        else if( model.isSelected() || model.isMouseInside() ){
            border = Colors.diffMirror( background, 0.3 );
            background = Colors.undiffMirror( background, 0.4 );
        }
        
        int w = getWidth()-1;
        int h = getHeight()-1;
        
        if( border != null ){
            g.setColor( background );
            g.fillRoundRect( 0, 0, w, h, 4, 4 );
            
            g.setColor( border );
            g.drawRoundRect( 0, 0, w, h, 4, 4 );

            if( model.isMouseOverDropDown() ){
                if( model.getOrientation().isHorizontal() ){
                    int x = w - drop.getIconWidth() - 5;
                    g.drawLine( x, 0, x, h );
                }
                else{
                    int y = h - drop.getIconHeight() - 5;
                    g.drawLine( 0, y, w, y );
                }
            }
        }
        
        Icon icon = model.getPaintIcon();
        if( icon != null ){
            if( model.getOrientation().isHorizontal() ){
                icon.paintIcon( this, g, (w -3 - drop.getIconWidth() +1 - icon.getIconWidth())/2, (h +1 - icon.getIconHeight())/2 );
            }
            else{
                icon.paintIcon( this, g, (w +1 - icon.getIconWidth())/2, (h -3 -drop.getIconHeight() +1 - icon.getIconHeight())/2 );
            }
        }
        
        if( model.getOrientation().isHorizontal() ){
            drop.paintIcon( this, g, w - drop.getIconWidth() - 2, (h - drop.getIconHeight())/2 );
        }
        else{
            drop.paintIcon( this, g, (w - drop.getIconWidth())/2, h - drop.getIconHeight() - 2 );
        }
        
        if( hasFocus() && isFocusable() && isEnabled() ){
            g.setColor( Colors.diffMirror( background, 0.4 ) );
            // top left
            g.drawLine( 2, 3, 2, 4 );
            g.drawLine( 3, 2, 4, 2 );
            
            // top right
            g.drawLine( w-2, 3, w-2, 4 );
            g.drawLine( w-3, 2, w-4, 2 );
            
            // bottom left
            g.drawLine( 2, h-3, 2, h-4 );
            g.drawLine( 3, h-2, 4, h-2 );
            
            // bottom right
            g.drawLine( w-2, h-3, w-2, h-4 );
            g.drawLine( w-3, h-2, w-4, h-2 );
        }
    }
    
    /**
     * Tells whether the location <code>x/y</code> is within the area
     * that will always trigger the dropdown menu.
     * @param x some x coordinate
     * @param y some y coordinate
     * @return <code>true</code> if the point x/y is within the dropdown-area
     */
    public boolean inDropDownArea( int x, int y ){
        if( !contains( x, y ))
            return false;
        
        if( model.getOrientation().isHorizontal() ){
            return x >= getWidth() - dropIcon.getIconWidth() - 5;
        }
        else{
            return y >= getHeight() - dropIcon.getIconHeight() - 5;
        }
    }
    
    @Override
    public void updateUI() {
        disabledDropIcon = null;
        
        super.updateUI();
        
        if( handler != null )
            handler.updateUI();
    }
    
    /**
     * Creates an icon that is shown in the smaller subbutton of this button.
     * @return the icon
     */
    protected Icon createDropIcon(){
        return new Icon(){
            public int getIconHeight(){
                return 7;
            }
            public int getIconWidth(){
                return 7;
            }
            public void paintIcon( Component c, Graphics g, int x, int y ){
                x++;
                g.setColor( getForeground() );
                g.drawLine( x, y+1, x+4, y+1 );
                g.drawLine( x+1, y+2, x+3, y+2 );
                g.drawLine( x+2, y+3, x+2, y+3 );
            }
        };
    }
}
