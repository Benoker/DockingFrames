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
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.geom.RoundRectangle2D;

import javax.swing.Icon;
import javax.swing.JComponent;

import bibliothek.gui.DockController;
import bibliothek.gui.Dockable;
import bibliothek.gui.dock.action.DockAction;
import bibliothek.gui.dock.themes.basic.action.BasicDropDownButtonHandler;
import bibliothek.gui.dock.themes.basic.action.BasicDropDownButtonModel;
import bibliothek.gui.dock.themes.color.ActionColor;
import bibliothek.gui.dock.util.AbstractPaintableComponent;
import bibliothek.gui.dock.util.BackgroundComponent;
import bibliothek.gui.dock.util.BackgroundPaint;
import bibliothek.gui.dock.util.DockUtilities;
import bibliothek.gui.dock.util.color.ColorCodes;

/**
 * A button which can be pressed by the user either to execute 
 * a {@link bibliothek.gui.dock.action.DockAction} or to show a popup-menu
 * with a selection of <code>DockActions</code>. This button uses a 
 * {@link BasicDropDownButtonHandler} to manage its internal states.
 * @author Benjamin Sigg
 */

@ColorCodes({
    "action.dropdown",
    "action.dropdown.enabled",
    "action.dropdown.selected",
    "action.dropdown.selected.enabled",
    "action.dropdown.mouse.enabled",
    "action.dropdown.mouse.selected.enabled",
    "action.dropdown.pressed.enabled",
    "action.dropdown.pressed.selected.enabled",
    
    "action.dropdown.focus",
    "action.dropdown.enabled.focus",
    "action.dropdown.selected.focus",
    "action.dropdown.selected.enabled.focus",
    "action.dropdown.mouse.enabled.focus",
    "action.dropdown.mouse.selected.enabled.focus",
    "action.dropdown.pressed.enabled.focus",
    "action.dropdown.pressed.selected.enabled.focus",
    
    "action.dropdown.line",
    "action.dropdown.line.enabled",
    "action.dropdown.line.selected",
    "action.dropdown.line.selected.enabled",
    "action.dropdown.line.mouse.enabled",
    "action.dropdown.line.mouse.selected.enabled",
    "action.dropdown.line.pressed.enabled",
    "action.dropdown.line.pressed.selected.enabled"})
public class RoundDropDownButton extends JComponent implements RoundButtonConnectable{
	/** the animation that changes the colors */
    private BubbleColorAnimation animation;
    
    /** a model containing all information needed to paint this button */
    private BasicDropDownButtonModel model;
    
    /** a handler reacting if this button is pressed */
    private BasicDropDownButtonHandler handler;
    
    /** the icon to show for the area in which the popup-menu could be opened */
    private Icon dropIcon;
    /** a disabled version of {@link #dropIcon} */
    private Icon disabledDropIcon;
    
    /** the colors used on this button */
    private RoundActionColor[] colors;
    
    /**
     * Creates a new button
     * @param handler a handler used to announce that this button is clicked
     * @param dockable the element for which the action is shown
     * @param action the action that is shown
     */
    public RoundDropDownButton( BasicDropDownButtonHandler handler, Dockable dockable, DockAction action ){
        animation = new BubbleColorAnimation();
        
        colors = new RoundActionColor[]{
                new RoundActionColor( "action.dropdown", dockable, action, Color.WHITE ),
                new RoundActionColor( "action.dropdown.enabled", dockable, action, Color.LIGHT_GRAY ),
                new RoundActionColor( "action.dropdown.selected", dockable, action, Color.YELLOW ),
                new RoundActionColor( "action.dropdown.enabled.selected", dockable, action, Color.ORANGE ),
                new RoundActionColor( "action.dropdown.mouse.enabled", dockable, action, Color.RED ),
                new RoundActionColor( "action.dropdown.mouse.selected.enabled", dockable, action, new Color( 128, 0, 0 ) ),
                new RoundActionColor( "action.dropdown.pressed.enabled", dockable, action, Color.BLUE ),
                new RoundActionColor( "action.dropdown.pressed.selected.enabled", dockable, action, Color.MAGENTA ),

                new RoundActionColor( "action.dropdown.focus", dockable, action, Color.DARK_GRAY ),
                new RoundActionColor( "action.dropdown.enabled.focus", dockable, action, Color.DARK_GRAY ),
                new RoundActionColor( "action.dropdown.selected.focus", dockable, action, Color.DARK_GRAY ),
                new RoundActionColor( "action.dropdown.enabled.selected.focus", dockable, action, Color.DARK_GRAY ),
                new RoundActionColor( "action.dropdown.mouse.enabled.focus", dockable, action, Color.DARK_GRAY ),
                new RoundActionColor( "action.dropdown.mouse.selected.enabled.focus", dockable, action, Color.DARK_GRAY ),
                new RoundActionColor( "action.dropdown.pressed.enabled.focus", dockable, action, Color.DARK_GRAY ),
                new RoundActionColor( "action.dropdown.pressed.selected.enabled.focus", dockable, action, Color.DARK_GRAY ),
                
                new RoundActionColor( "action.dropdown.line", dockable, action, Color.DARK_GRAY ),
                new RoundActionColor( "action.dropdown.line.enabled", dockable, action, Color.DARK_GRAY ),
                new RoundActionColor( "action.dropdown.line.selected", dockable, action, Color.DARK_GRAY ),
                new RoundActionColor( "action.dropdown.line.enabled.selected", dockable, action, Color.DARK_GRAY ),
                new RoundActionColor( "action.dropdown.line.mouse.enabled", dockable, action, Color.DARK_GRAY ),
                new RoundActionColor( "action.dropdown.line.mouse.selected.enabled", dockable, action, Color.DARK_GRAY ),
                new RoundActionColor( "action.dropdown.line.pressed.enabled", dockable, action, Color.DARK_GRAY ),
                new RoundActionColor( "action.dropdown.line.pressed.selected.enabled", dockable, action, Color.DARK_GRAY ),
        };
        
        animation.addTask( new Runnable(){
            public void run() {
                repaint();
            }
        });
        
        this.handler = handler;
        dropIcon = createDropIcon();
        
        model = new BasicDropDownButtonModel( this, handler, handler ){
            @Override
            public void changed() {
                updateColors();
                revalidate();
                repaint();
            }
            
            @Override
            protected boolean inDropDownArea( int x, int y ) {
                return overDropIcon( x, y );
            }
        };
        
        addFocusListener( new FocusListener(){
            public void focusGained( FocusEvent e ) {
                repaint();
            }
            public void focusLost( FocusEvent e ) {
                repaint();
            }
        });
        
        updateColors();
    }
    
    public void setController( DockController controller ) {
        for( RoundActionColor color : colors ){
            color.connect( controller );
        }
        
        animation.kick();
    }
    
    public BasicDropDownButtonModel getModel() {
        return model;
    }
    
    @Override
    public void updateUI() {
        disabledDropIcon = null;
        
        super.updateUI();
        
        if( handler != null )
            handler.updateUI();
    }
    
    @Override
    public Dimension getPreferredSize() {
        if( isPreferredSizeSet() )
            return super.getPreferredSize();
        
        Dimension icon = model.getMaxIconSize();
        int w = Math.max( icon.width, 10 );
        int h = Math.max( icon.height, 10 );
        
        if( model.getOrientation().isHorizontal() )
            return new Dimension( (int)(1.5 * w + 1 + 1.5*dropIcon.getIconWidth()), (int)(1.5 * h));
        else
            return new Dimension( (int)(1.5 * w), (int)(1.5 * h + 1 + 1.5 * dropIcon.getIconHeight()) );
    }
    
    @Override
    public boolean contains( int x, int y ){
    	if( !super.contains( x, y ))
    		return false;
    	
    	int w = getWidth();
    	int h = getHeight();
    	RoundRectangle2D rect;
    	
    	if( model.getOrientation().isHorizontal() )
    		rect = new RoundRectangle2D.Double( 0, 0, w, h, h, h );
    	else
    		rect = new RoundRectangle2D.Double( 0, 0, w, h, w, w );
    	
    	return rect.contains( x, y );
    }
    
    @Override
    protected void paintComponent( Graphics g ) {
    	BasicDropDownButtonModel model = getModel();
    	BackgroundPaint paint = model.getBackground();
    	BackgroundComponent component = model.getBackgroundComponent();
    	
    	AbstractPaintableComponent paintable = new AbstractPaintableComponent( component, this, paint ){
			protected void foreground( Graphics g ){
				doPaintForeground( g );	
			}
			
			protected void background( Graphics g ){
				doPaintBackground( g );
			}
			
			protected void border( Graphics g ){
				// ignore	
			}
			
			protected void children( Graphics g ){
				// ignore	
			}
			
			protected void overlay( Graphics g ){
				// ignore
			}
			
			public boolean isSolid(){
				return false;
			}
			
			public boolean isTransparent(){
				return false;
			}
		};
		
		Graphics2D g2 = (Graphics2D)g.create();
        g2.setRenderingHint( RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON );
        
		paintable.paint( g2 );
		
		g2.dispose();
    }
    
    private void doPaintBackground( Graphics g ){
        Graphics2D g2 = (Graphics2D)g;
        
        int x = 0;
        int y = 0;
        int w = getWidth();
        int h = getHeight();
        
        if( model.getOrientation().isHorizontal() ){
            g2.setColor( animation.getColor( "background" ) );
            g2.fillRoundRect( x, y, w, h, h, h );
        }
        else{
            g2.setColor( animation.getColor( "background" ) );
            g2.fillRoundRect( x, y, w, h, w, w );
        }
    }
    
    private void doPaintForeground( Graphics g ){
        Icon drop = dropIcon;
        if( !isEnabled() ){
            if( disabledDropIcon == null )
                disabledDropIcon = DockUtilities.disabledIcon( this, dropIcon );
            drop = disabledDropIcon;
        }
        
        Graphics2D g2 = (Graphics2D)g;
        
        int x = 0;
        int y = 0;
        int w = getWidth();
        int h = getHeight();
        
        Dimension size = model.getMaxIconSize();
        Icon icon = model.getPaintIcon();
        
        int iconWidth = size.width < 10 ? 10 : size.width;
        int iconHeight = size.height < 10 ? 10 : size.height;
        
        int dropIconWidth = drop == null ? 5 : drop.getIconWidth();
        int dropIconHeight = drop == null ? 5 : drop.getIconHeight();
        
        if( model.getOrientation().isHorizontal() ){
            g2.setColor( animation.getColor( "mouse" ) );
            int mx = x + (int)( 0.5 * 1.25 * iconWidth + 0.5 * (w - 1.25 * dropIconWidth) );
            g2.drawLine( mx, y+1, mx, y+h-2 );
            
            
            if( icon != null ){
                icon.paintIcon( this, g, (int)(x + 0.25 * iconWidth ), y+(h-iconHeight)/2 );
            }
            if( drop != null )
            	drop.paintIcon( this, g, (int)(x + w - 1.25 * dropIconWidth), y+(h-dropIconHeight)/2 );
            
            if( hasFocus() && isFocusable() && isEnabled() ){
                Stroke stroke = g2.getStroke();
                g2.setStroke( new BasicStroke( 3f ) );
                g2.setColor( animation.getColor( "focus" ) );
                g2.drawRoundRect( x+1, y+1, w-3, h-3, h, h );
                g2.setStroke( stroke );
            }
        }
        else{
            g2.setColor( animation.getColor( "mouse" ) );
            int my = y + (int)( 0.5 * 1.25 * iconHeight + 0.5 * (h - 1.25 * dropIconHeight) );
            g2.drawLine( x+1, my, x+w-2, my );
            
            
            if( icon != null ){
                icon.paintIcon( this, g, x+(w-iconWidth)/2, (int)(y + 0.25*iconHeight));
            }
            if( drop != null )
            	drop.paintIcon( this, g, x + ( w - dropIconWidth ) / 2, (int)(y+h-1.25*dropIconHeight) );
            
            if( hasFocus() && isFocusable() && isEnabled() ){
                Stroke stroke = g2.getStroke();
                g2.setStroke( new BasicStroke( 3f ) );
                g2.setColor( animation.getColor( "focus" ) );
                g2.drawRoundRect( x+1, y+1, w-3, h-3, w, w );
                g2.setStroke( stroke );
            }
        }
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
    
    /**
     * Tells whether the point x,y is over the icon that represents the drop-area.
     * @param x the x-coordinate
     * @param y the y-coordinate
     * @return <code>true</code> if pressing the mouse at that location would
     * open a popup menu
     */
    public boolean overDropIcon( int x, int y ){
    	if( !contains( x, y ))
    		return false;
    	
        int rx = 0;
        int ry = 0;
        int rw = getWidth();
        int rh = getHeight();
        
        Dimension icon = model.getMaxIconSize();
        
        int iconWidth = icon.width < 10 ? 10 : icon.width;
        int iconHeight = icon.height < 10 ? 10 : icon.height;
        
        int dropIconWidth = dropIcon == null ? 5 : dropIcon.getIconWidth();
        int dropIconHeight = dropIcon == null ? 5 : dropIcon.getIconHeight();
        
        if( model.getOrientation().isHorizontal() ){
        	int mx = rx + (int)( 0.5 * 1.25 * iconWidth + 0.5 * (rw - 1.25 * dropIconWidth) );
        	return x >= mx;
        }
        else{
        	int my = ry + (int)( 0.5 * 1.25 * iconHeight + 0.5 * (rh - 1.25 * dropIconHeight) );
        	return y >= my;
        }
    }

    /**
     * Updates the colors of the animation.
     */
    public void updateColors(){
        String postfix = "";
        
        boolean selected = model.isSelected();
        boolean enabled = model.isEnabled();
        boolean pressed = model.isMousePressed();
        boolean entered = model.isMouseInside();
        boolean mouseOverDrop = model.isMouseOverDropDown();
        
        if( selected )
            postfix = ".selected";
        
        if( enabled )
            postfix += ".enabled";
        
        String mouse;
        if( mouseOverDrop && enabled )
            mouse = "dropdown.line";
        else
            mouse = "dropdown";
        
        String background;
        
        if( pressed && enabled ){
            background = "action.dropdown.pressed" + postfix;
            mouse = "action." + mouse + ".pressed" + postfix;
        }
        else if( entered && enabled ){
            background = "action.dropdown.mouse" + postfix;
            mouse = "action." + mouse + ".mouse" + postfix;
        }
        else{
            background = "action.dropdown" + postfix;
            mouse = "action." + mouse + postfix;
        }
        
        String focus = background + ".focus";
        
        for( RoundActionColor color : colors ){
            if( background.equals( color.getId() ))
                animation.putColor( "background", color.value() );
            if( mouse.equals( color.getId() ))
                animation.putColor( "mouse", color.value() );
            if( focus.equals( color.getId() ))
                animation.putColor( "focus", color.value() );
        }
    }
    
    /**
     * A color used in a round dropdown button
     * @author Benjamin Sigg
     */
    private class RoundActionColor extends ActionColor{
        public RoundActionColor( String id, Dockable dockable, DockAction action, Color backup ){
            super( id, dockable, action, backup );
        }
        @Override
        protected void changed( Color oldColor, Color newColor ) {
            updateColors();
        }
    }
}
