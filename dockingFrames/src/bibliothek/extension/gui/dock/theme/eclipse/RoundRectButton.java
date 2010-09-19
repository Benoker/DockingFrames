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
import java.awt.EventQueue;
import java.awt.Graphics;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;

import javax.swing.Icon;
import javax.swing.JComponent;

import bibliothek.gui.dock.control.FocusAwareComponent;
import bibliothek.gui.dock.themes.basic.action.BasicButtonModel;
import bibliothek.gui.dock.themes.basic.action.BasicButtonModelAdapter;
import bibliothek.gui.dock.themes.basic.action.BasicTrigger;
import bibliothek.util.Colors;

/**
 * A button that has a round rect shape.
 * @author Benjamin Sigg
 */
public class RoundRectButton extends JComponent implements FocusAwareComponent{
    private BasicButtonModel model;
    private Runnable afterFocusRequest;
    
    /**
     * Creates a new roundrect button.
     * @param trigger a trigger which gets informed when the user clicks the
     * button
     */
    public RoundRectButton( BasicTrigger trigger ){
        model = new BasicButtonModel( this, trigger, true );
        setOpaque( false );
        
        setFocusable( true );
        
        model.addListener( new BasicButtonModelAdapter(){
        	@Override
        	public void mousePressed( BasicButtonModel model, boolean mousePressed ){
        		if( !mousePressed ){
        			requestFocusInWindow();
        			invokeAfterFocusRequest();
        		}
        	}
        });
        
        addFocusListener( new FocusListener(){
            public void focusGained( FocusEvent e ) {
                repaint();
            }
            public void focusLost( FocusEvent e ) {
                repaint();
            }
        });
    }
    
    public void maybeRequestFocus(){
    	afterFocusRequest = null;
    	EventQueue.invokeLater(new Runnable(){
    		public void run(){
    			if( !model.isMousePressed() ){
    				requestFocusInWindow();
    				invokeAfterFocusRequest();
    			}
    		}
    	});
    }
    
    public void invokeOnFocusRequest( Runnable run ){
    	afterFocusRequest = run;
    }
    
    private void invokeAfterFocusRequest(){
    	EventQueue.invokeLater(new Runnable(){
			public void run(){
				if( afterFocusRequest != null ){
					afterFocusRequest.run();
					afterFocusRequest = null;
				}
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
    public Dimension getMinimumSize(){
    	if( isMinimumSizeSet() )
    		return super.getMinimumSize();
    	
    	return getPreferredSize();
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
}
