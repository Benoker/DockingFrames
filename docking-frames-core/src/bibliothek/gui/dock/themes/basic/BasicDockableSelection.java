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
package bibliothek.gui.dock.themes.basic;

import java.awt.*;
import java.awt.image.BufferedImage;

import javax.swing.BorderFactory;
import javax.swing.JComponent;

import bibliothek.gui.Dockable;
import bibliothek.gui.dock.dockable.ScreencaptureMovingImageFactory;
import bibliothek.gui.dock.focus.DefaultDockableSelection;

/**
 * The {@link BasicDockableSelection} adds an additional image of the 
 * currently selected {@link Dockable} to the selection.
 * @author Benjamin Sigg
 */
public class BasicDockableSelection extends DefaultDockableSelection {
    private ImagePainter painter;
    
    public BasicDockableSelection(){
        setBorder( BorderFactory.createRaisedBevelBorder() );
        
        painter = new ImagePainter( new Dimension( 300, 200 ) );
        
        removeAll();
        
        setLayout( new GridBagLayout() );
        add( painter, new GridBagConstraints( 0, 0, 1, 1, 2.0, 1.0, GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                new Insets( 0, 0, 0, 0 ), 0, 0 ));
        add( getList(), new GridBagConstraints( 1, 0, 1, 1, 1.0, 1.0, GridBagConstraints.CENTER,
                GridBagConstraints.BOTH, new Insets( 0, 0, 0, 0 ), 0, 0 ));
        revalidate();        
    }
    
    @Override
    protected void setSelection( Dockable dockable ) {
        super.setSelection( dockable );
        painter.paint( dockable );
    }
    
    @Override
    public void updateUI() {
        super.updateUI();
        if( painter != null )
            painter.updateUI();
    }
    
    private class ImagePainter extends JComponent{
        private BufferedImage image;
        private ScreencaptureMovingImageFactory factory;
        
        public ImagePainter( Dimension size ){
            factory = new ScreencaptureMovingImageFactory( size );
            setPreferredSize( new Dimension( size.width + 4, size.height + 4 ) );
        }
        
        private void paint( Dockable dockable ){
            if( dockable == null )
                image = null;
            else{
                image = factory.createImageFrom( getController(), dockable );
            }
            repaint();
        }
        
        @Override
        protected void paintComponent( Graphics g ) {
            super.paintComponent( g );
            if( image != null ){
                int x = (getWidth() - image.getWidth()) / 2;
                int y = (getHeight() - image.getHeight()) / 2;
                
                g.drawImage( image, x, y, this );
                
                g.setColor( getForeground() );
                g.drawRect( x-1, y-1, image.getWidth(), image.getHeight() );
            }
        }
    }
}
