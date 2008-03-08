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
package bibliothek.sizeAndColor;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.*;

import bibliothek.gui.dock.common.ColorMap;

/**
 * A {@link ColorButton} changes an entry of the {@link ColorMap}. It can
 * also delete an entry from the map.
 * @author Benjamin Sigg
 */
public class ColorButton extends JPanel{
    private ColorMap map;
    private String key;
    
    private Color color = Color.RED;
    private JCheckBox selected;

    /**
     * Creates a new button.
     * @param map the map which will be changed by this button
     * @param key the key of the entry
     * @param color the initial value
     */
    public ColorButton( ColorMap map, String key, Color color ){
        this.map = map;
        this.color = color;
        this.key = key;
        
        selected = new JCheckBox( key );
        JButton button = new JButton( new ColorIcon() );
        
        setLayout( new BorderLayout() );
        add( selected, BorderLayout.CENTER );
        add( button, BorderLayout.EAST );
        
        selected.addActionListener( new ActionListener(){
            public void actionPerformed( ActionEvent e ) {
                transmit();
            }
        });
        button.addActionListener( new ActionListener(){
            public void actionPerformed( ActionEvent e ) {
                changeColor();
            }
        });
    }
    
    /**
     * Opens a {@link JColorChooser} and lets the user change the color.
     */
    private void changeColor(){
        Color color = JColorChooser.showDialog( this, key, this.color );
        if( color != null ){
            this.color = color;
            repaint();
            transmit();
        }
    }
    
    /**
     * Transmits the current setting to the {@link ColorMap} of this button.
     */
    private void transmit(){
        if( selected.isSelected() ){
            map.setColor( key, color );
        }
        else{
            map.setColor( key, null );
        }
    }
    
    /**
     * An icon that paints a rectangle in one color.
     * @author Benjamin Sigg
     */
    private class ColorIcon implements Icon{
        public int getIconHeight() {
            return 20;
        }

        public int getIconWidth() {
            return 20;
        }

        public void paintIcon( Component c, Graphics g, int x, int y ) {
            g.setColor( color );
            g.fillRect( x, y, 20, 20 );
        }
    }
}
