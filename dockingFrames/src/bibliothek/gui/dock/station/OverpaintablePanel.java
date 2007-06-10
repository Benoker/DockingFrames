/**
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
package bibliothek.gui.dock.station;

import java.awt.Graphics;

import javax.swing.JComponent;
import javax.swing.JLayeredPane;
import javax.swing.JPanel;

/**
 * A panel which contains two children: one child is used to add other
 * children, the other child is used to paint over all children. Subclasses
 * can change the painting routine by overriding {@link #paintOverlay(Graphics)}.
 * @author Benjamin Sigg
 *
 */
public class OverpaintablePanel extends JLayeredPane {
    /** the panel over all other children */
    private Overlay overlay = new Overlay();
    
    /** the panel on which children should be added */
    private JComponent panel = new JPanel();
    
    /**
     * Creates a new panel
     */
    public OverpaintablePanel(){
        setLayer( panel, DEFAULT_LAYER );
        setLayer( overlay, DRAG_LAYER );
        
        add( panel );
        add( overlay );
    }
    
    /**
     * Sets the panel on which clients should add their children.
     * @param content the contents of this panel
     */
    public void setContentPane( JComponent content ){
        if( content == null )
            throw new IllegalArgumentException( "Content must not be null" );
        
        remove( panel );
        panel = content;
        
        setLayer( content, DEFAULT_LAYER );
        add( content );
    }
    
    /**
     * Gets the layer on which new components should be inserted.
     * @return the layer
     */
    public JComponent getContentPane(){
        return panel;
    }
    
    /**
     * Paints the overlay over all components.
     * @param g the graphics to use
     */
    protected void paintOverlay( Graphics g ){
        // do nothing
    }
    
    @Override
    public void doLayout() {
        panel.setBounds( 0, 0, getWidth(), getHeight() );
        overlay.setBounds( 0, 0, getWidth(), getHeight() );
    }
    
    private class Overlay extends JPanel{
        public Overlay(){
            setOpaque( false );
        }
        
        @Override
        public boolean contains( int x, int y ) {
            return false;
        }
        
        @Override
        protected void paintComponent( Graphics g ) {
            paintOverlay( g );
        }
    }
}
