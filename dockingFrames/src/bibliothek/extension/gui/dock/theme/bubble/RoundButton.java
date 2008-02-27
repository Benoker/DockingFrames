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

import javax.swing.Icon;
import javax.swing.JComponent;

import bibliothek.gui.DockController;
import bibliothek.gui.Dockable;
import bibliothek.gui.dock.action.DockAction;
import bibliothek.gui.dock.themes.basic.action.BasicButtonModel;
import bibliothek.gui.dock.themes.basic.action.BasicTrigger;
import bibliothek.gui.dock.themes.color.ActionColor;
import bibliothek.gui.dock.util.color.ColorCodes;

/**
 * A round button is a button that has a oval form. Clients should call
 * {@link #setController(DockController)} for optimal usage.
 * @author Benjamin Sigg
 *
 */
@ColorCodes({ "action.button",
    "action.button.enabled",
    "action.button.selected",
    "action.button.selected.enabled",
    "action.button.mouse.enabled",
    "action.button.mouse.selected.enabled",
    "action.button.pressed.enabled",
    "action.button.pressed.selected.enabled"
})
public class RoundButton extends JComponent implements RoundButtonConnectable{
    private BubbleColorAnimation animation;
	
    private BasicButtonModel model;
	
    private RoundActionColor[] colors;
    
    /**
     * Creates a new round button.
     * @param trigger a trigger which gets informed when the user clicks the
     * button.
     * @param dockable the dockable for which this button is used
     * @param action the action for which this button is used
     */
	public RoundButton( BasicTrigger trigger, Dockable dockable, DockAction action ){
		animation = new BubbleColorAnimation();
		
		colors = new RoundActionColor[]{
		        new RoundActionColor( "action.button", dockable, action, Color.WHITE ),
		        new RoundActionColor( "action.button.enabled", dockable, action, Color.LIGHT_GRAY ),
		        new RoundActionColor( "action.button.selected", dockable, action, Color.YELLOW ),
		        new RoundActionColor( "action.button.selected.enabled", dockable, action, Color.ORANGE ),
		        new RoundActionColor( "action.button.mouse.enabled", dockable, action, Color.RED ),
		        new RoundActionColor( "action.button.mouse.selected.enabled", dockable, action, new Color( 128, 0, 0) ),
		        new RoundActionColor( "action.button.pressed.enabled", dockable, action, Color.BLUE ),
		        new RoundActionColor( "action.button.pressed.selected.enabled", dockable, action, Color.MAGENTA )
		};
		
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
    
	/**
	 * Connects this button with a controller, that is necessary to get the
	 * colors for this button.
	 * @param controller the controller, can be <code>null</code>
	 */
	public void setController( DockController controller ){
	    for( RoundActionColor color : colors )
	        color.connect( controller );
	    
	    animation.kick();
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
    	
    	String key = "action.button"+ postfix;
    	for( RoundActionColor color : colors ){
    	    if( key.equals( color.getId() )){
    	        animation.putColor( "button", color.color() );
    	        break;
    	    }
    	}    	
    }
    
    /**
     * A color used in a round button
     * @author Benjamin Sigg
     */
    private class RoundActionColor extends ActionColor{
        public RoundActionColor( String id, Dockable dockable, DockAction action, Color backup ){
            super( id, ActionColor.class, dockable, action, backup );
        }
        @Override
        protected void changed( Color oldColor, Color newColor ) {
            updateColors();
        }
    }
}
