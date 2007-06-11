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

package bibliothek.extension.gui.dock.theme.flat;

import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JTabbedPane;
import javax.swing.border.Border;

import bibliothek.gui.dock.station.stack.CombinedStackDockComponent;
import bibliothek.gui.dock.station.stack.CombinedTab;

/**
 * A panel that works like a {@link JTabbedPane}, but the buttons to
 * change between the children are smaller and "flatter" than the
 * buttons of the <code>JTabbedPane</code>.
 * @author Benjamin Sigg
 */
public class FlatTab extends CombinedStackDockComponent<FlatTab.FlatButton>{
    
	@Override
	protected FlatButton createTab(){
		return new FlatButton();
	}
	
	@Override
	protected void destroy( FlatButton tab ){
		// nothing to do
	}
	
    /**
     * A small button which can be clicked by the user.
     * @author Benjamin Sigg
     */
	protected class FlatButton extends JLabel implements CombinedTab{
        /** The location of this button */
        private int index;
        
        public JComponent getComponent(){
        	return this;
        }
        
        @Override
        public Dimension getPreferredSize() {
            Dimension preferred = super.getPreferredSize();
            if( preferred.width < 10 || preferred.height < 10 ){
                preferred = new Dimension( preferred );
                preferred.width = Math.max( preferred.width, 10 );
                preferred.height = Math.max( preferred.height, 10 );
            }
            return preferred;
        }
        
        @Override
        public Dimension getMinimumSize() {
            Dimension min = super.getMinimumSize();
            if( min.width < 10 || min.height < 10 ){
                min = new Dimension( min );
                min.width = Math.max( min.width, 10 );
                min.height = Math.max( min.height, 10 );
            }
            return min;
        }
        
        /**
         * Constructs a new button
         */
        public FlatButton(){
            setOpaque( false );
            setFocusable( true );
            
            addMouseListener( new MouseAdapter(){
                @Override
                public void mousePressed( MouseEvent e ){
                    setSelectedIndex( index );
                    repaint();
                }
            });
            
            setBorder( new Border(){
                public void paintBorder(Component c, Graphics g, int x, int y, int w, int h){
                    Graphics2D g2 = (Graphics2D)g;
                    Paint oldPaint = g2.getPaint();
                    
                    Color dark = c.getBackground().darker();
                    Color bright = c.getBackground().brighter();
                    
                    if( isSelected() )
                        g2.setPaint( new GradientPaint( x, y, getBackground(), x, y+h/2, bright ));
                    else
                        g2.setPaint( new GradientPaint( x, y, getBackground(), x, y+h/2, dark ));
                    
                    g.drawLine( x, y, x, y+h/2 );
                    g.drawLine( x+w-1, y, x+w-1, y+h/2 );
                    
                    if( isSelected() )
                        g2.setPaint( new GradientPaint( x, y+h, getBackground(), x, y+h/2, bright ));
                    else
                        g2.setPaint( new GradientPaint( x, y+h, getBackground(), x, y+h/2, dark ));

                    g.drawLine( x, y+h, x, y+h/2 );
                    g.drawLine( x+w-1, y+h, x+w-1, y+h/2 );
                    
                    g2.setPaint( oldPaint );
                }
                public Insets getBorderInsets(Component c){
                    return new Insets( 0, 1, 0, 1 );
                }
                public boolean isBorderOpaque(){
                    return false;
                }
            });
        }
        
        /**
         * Sets the location of this button. The buttons knows
         * through the {@link FlatTab#selectedIndex selectedIndex}-property whether
         * it is selected or not.
         * @param index the location
         */
        public void setIndex( int index ) {
            this.index = index;
            repaint();
        }
        
        /**
         * Determines whether this button is selected or not.
         * @return <code>true</code> if the button is selected
         */
        public boolean isSelected() {
            return index == getSelectedIndex();
        }
        
        @Override
        public void paintComponent( Graphics g ){
            Graphics2D g2 = (Graphics2D)g;
            Paint oldPaint = g2.getPaint();
            
            int w = getWidth();
            int h = getHeight();
            
            if( isSelected() ){
                GradientPaint gradient = new GradientPaint( 0, 0, getBackground().brighter(),
                        0, h, getBackground() );
                g2.setPaint( gradient );
            }
            else{
                g.setColor( getBackground() );
            }
            g.fillRect( 0, 0, w, h );
            
            g2.setPaint( oldPaint );
            
            super.paintComponent( g );
        }
    }
}
