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
package bibliothek.extension.gui.dock.theme.bubble;


import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;

import javax.swing.Icon;
import javax.swing.JComponent;

import bibliothek.extension.gui.dock.theme.BubbleTheme;
import bibliothek.gui.dock.themes.basic.action.BasicButtonModel;
import bibliothek.gui.dock.themes.basic.action.BasicTrigger;


public class RoundButton extends JComponent{
	private BubbleColorAnimation animation;
	
    private BasicButtonModel model;
	
	public RoundButton(BubbleTheme theme, BasicTrigger trigger){
		animation=new BubbleColorAnimation(theme);
        model = new BasicButtonModel( this, trigger ){
            @Override
            public void changed() {
                updateColors();
                repaint();
            }
        };
        
		updateColors();

		animation.addTask(new Runnable() {

			public void run()
			{
				repaint();	
			}

		});
	}
    
    public BasicButtonModel getModel() {
        return model;
    }

    @Override
    public boolean contains( int x, int y ) {
        if( !super.contains( x, y ))
            return false;
        
        double w = getWidth();
        double h = getHeight();
        
        double dx, dy;
        
        if( w > h ){
            double delta = h / w;
            dx = x;
            dy = delta * y;
            h = w;
        }
        else{
            double delta = w / h;
            dx = delta * x;
            dy = y;
            w = h;
        }
        
        dx -= w/2;
        dy -= h/2;
        
        double dist = dx*dx + dy*dy;
        return dist <= w*w/4;
    }
    
	@Override
	public Dimension getPreferredSize() {
	    Dimension icon = model.getMaxIconSize();
        icon.width = Math.max( icon.width, 10 );
        icon.height = Math.max( icon.height, 10 );

        return new Dimension((int)(icon.width*1.5),(int)(icon.height*1.5));
	}

	@Override
	protected void paintComponent(Graphics g) {

		Graphics2D g2 = (Graphics2D)g.create();
		g2.setRenderingHint( RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON );

		g2.setColor(animation.getColor("button"));
		g2.fillOval( 0, 0, getWidth(), getHeight() );
		g2.dispose();

		/*
		 * Icon der DockAction (sofern es ein Icon gibt).
		 */
		//	Icon icon = action.getIcon( dockable );
        Icon icon = model.getPaintIcon();
		if( icon != null ){
			icon.paintIcon( this, g, 
					(getWidth() - icon.getIconWidth()) / 2, 
					(getHeight() - icon.getIconHeight()) / 2 );
		}

	}

    private void updateColors() {
    	
    	String postfix="";
    	boolean mousePressed = model.isMousePressed();
        boolean mouseEntered = model.isMouseInside();
        boolean selected = model.isSelected();
        boolean enabled = model.isEnabled();
        
    	if (enabled&&mousePressed)
    		postfix=".pressed";
    	
    	if (enabled&&mouseEntered&&!mousePressed)
    		postfix=".mouse";
    	
    	if (selected)
    		postfix+=".selected";
    	
    	if (enabled)
    		postfix+=".enabled";
    	
    	animation.putColor("button", "button"+ postfix);    	
    }
}
