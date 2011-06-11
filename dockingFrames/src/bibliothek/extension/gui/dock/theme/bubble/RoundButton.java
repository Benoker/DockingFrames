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

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.Stroke;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;

import javax.swing.Icon;
import javax.swing.JComponent;

import bibliothek.gui.DockController;
import bibliothek.gui.Dockable;
import bibliothek.gui.dock.action.DockAction;
import bibliothek.gui.dock.themes.basic.action.BasicButtonModel;
import bibliothek.gui.dock.themes.basic.action.BasicButtonModelAdapter;
import bibliothek.gui.dock.themes.basic.action.BasicResourceInitializer;
import bibliothek.gui.dock.themes.basic.action.BasicTrigger;
import bibliothek.gui.dock.themes.color.ActionColor;
import bibliothek.gui.dock.util.AbstractPaintableComponent;
import bibliothek.gui.dock.util.BackgroundComponent;
import bibliothek.gui.dock.util.BackgroundPaint;
import bibliothek.gui.dock.util.IconManager;
import bibliothek.gui.dock.util.PropertyValue;
import bibliothek.gui.dock.util.color.AbstractDockColor;
import bibliothek.gui.dock.util.color.ColorCodes;
import bibliothek.gui.dock.util.color.DockColor;

/**
 * A round button is a button that has a oval form. Clients should call
 * {@link #setController(DockController)} for optimal usage.
 * @author Benjamin Sigg
 *
 */
@ColorCodes({
    "action.button",
    "action.button.focus",
    "action.button.enabled",
    "action.button.enabled.focus",
    "action.button.selected",
    "action.button.selected.focus",
    "action.button.selected.enabled",
    "action.button.selected.enabled.focus",
    "action.button.mouse.enabled",
    "action.button.mouse.enabled.focus",
    "action.button.mouse.selected.enabled",
    "action.button.mouse.selected.enabled.focus",
    "action.button.pressed.enabled",
    "action.button.pressed.enabled.focus",
    "action.button.pressed.selected.enabled",
    "action.button.pressed.selected.enabled.focus"
})
public class RoundButton extends JComponent implements RoundButtonConnectable{
    private BubbleColorAnimation animation;
	
    private BasicButtonModel model;
	
    private AbstractDockColor[] colors;
    
    private boolean paintFocusBorder = true;
    
    /** the expected minimum size of any icon */
    private PropertyValue<Dimension> minimumIconSize = new PropertyValue<Dimension>( IconManager.MINIMUM_ICON_SIZE ){
		@Override
		protected void valueChanged( Dimension oldValue, Dimension newValue ){
			revalidate();
		}
	};
    
    /**
     * Creates a new round button.
     * @param trigger a trigger which gets informed when the user clicks the
     * button.
     * @param initializer a strategy to lazily initialize resources, can be <code>null</code>
     * @param dockable the dockable for which this button is used
     * @param action the action for which this button is used
     */
	public RoundButton( BasicTrigger trigger, BasicResourceInitializer initializer, Dockable dockable, DockAction action ){
		setFocusable( true );
		
		animation = new BubbleColorAnimation();
		
		colors = createColors( dockable, action );
		
        model = new BasicButtonModel( this, trigger, initializer ){
            @Override
            public void changed() {
                updateColors();
                repaint();
            }
        };
        
        model.addListener( new BasicButtonModelAdapter(){
        	@Override
        	public void mousePressed( BasicButtonModel model, boolean mousePressed ){
        		if( mousePressed ){
        			requestFocusInWindow();
        		}
        	}
        });
        
		updateColors();

		animation.addTask(new Runnable() {
			public void run(){
				repaint();	
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
	
	protected AbstractDockColor[] createColors( Dockable dockable, DockAction action ){
		return new AbstractDockColor[]{
		        createColor( "action.button", dockable, action, Color.WHITE ),
		        createColor( "action.button.enabled", dockable, action, Color.LIGHT_GRAY ),
		        createColor( "action.button.selected", dockable, action, Color.YELLOW ),
		        createColor( "action.button.selected.enabled", dockable, action, Color.ORANGE ),
		        createColor( "action.button.mouse.enabled", dockable, action, Color.RED ),
		        createColor( "action.button.mouse.selected.enabled", dockable, action, new Color( 128, 0, 0) ),
		        createColor( "action.button.pressed.enabled", dockable, action, Color.BLUE ),
		        createColor( "action.button.pressed.selected.enabled", dockable, action, Color.MAGENTA ),
		        
		        createColor( "action.button.focus", dockable, action, Color.DARK_GRAY ),
		        createColor( "action.button.enabled.focus", dockable, action, Color.DARK_GRAY ),
		        createColor( "action.button.selected.focus", dockable, action, Color.DARK_GRAY ),
		        createColor( "action.button.selected.enabled.focus", dockable, action, Color.DARK_GRAY ),
		        createColor( "action.button.mouse.enabled.focus", dockable, action, Color.DARK_GRAY ),
		        createColor( "action.button.mouse.selected.enabled.focus", dockable, action, Color.DARK_GRAY ),
		        createColor( "action.button.pressed.enabled.focus", dockable, action, Color.DARK_GRAY ),
		        createColor( "action.button.pressed.selected.enabled.focus", dockable, action, Color.DARK_GRAY )};
	}
	
	/**
	 * Creates a new {@link DockColor} representing one color used by this button.
	 * @param key the unique identifier of the color
	 * @param dockable the dockable for which the color is used, may be <code>null</code>
	 * @param action the action represented by this button, may be <code>null</code>
	 * @param backup the backup color that is used if no other color was found
	 * @return the new {@link DockColor}
	 */
	protected AbstractDockColor createColor( String key, Dockable dockable, DockAction action, Color backup ){
		return new RoundActionColor( key, dockable, action, backup );
	}
	
	/**
	 * Access to the {@link BubbleColorAnimation} which is responsible for repainting this button. Subclasses
	 * may use this method to insert or modify custom colors.
	 * @return the animtation
	 */
	protected BubbleColorAnimation getAnimation(){
		return animation;
	}
	
	/**
	 * Sets whether a special border should be painted if this button is focused.
	 * @param paintFocusBorder whether to paint the border
	 */
	public void setPaintFocusBorder( boolean paintFocusBorder ){
		this.paintFocusBorder = paintFocusBorder;
	}
	
	/**
	 * Tells whether a special border is painted if this button is focused.
	 * @return whether to paint a special border
	 */
	public boolean isPaintFocusBorder(){
		return paintFocusBorder;
	}
	
	/**
	 * Connects this button with a controller, that is necessary to get the
	 * colors for this button.
	 * @param controller the controller, can be <code>null</code>
	 */
	public void setController( DockController controller ){
	    for( AbstractDockColor color : colors )
	        color.connect( controller );
	    
	    animation.kick();
	    minimumIconSize.setProperties( controller );
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
	    Dimension min = minimumIconSize.getValue();
	    
        icon.width = Math.max( icon.width, min.width );
        icon.height = Math.max( icon.height, min.height );

        return new Dimension((int)(icon.width*1.5),(int)(icon.height*1.5));
	}

	@Override
	protected void paintComponent(Graphics g) {
		Graphics2D g2 = (Graphics2D)g.create();
		g2.setRenderingHint( RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON );

		BackgroundPaint paint = model.getBackground();
		BackgroundComponent component = model.getBackgroundComponent();
		if( paint == null ){
			doPaintBackground( g2 );
			doPaintForeground( g2 );
		}
		else{
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
			paintable.paint( g2 );
		}
		
		g2.dispose();
	}
	
	/**
	 * Paints the background of this button.
	 * @param g the graphics context to use
	 */
	protected void doPaintBackground( Graphics g ){
		Graphics2D g2 = (Graphics2D)g;
		g2.setColor(animation.getColor("button"));
		g2.fillOval( 0, 0, getWidth(), getHeight() );
	}
	
	/**
	 * Paints the foreground of this button.
	 * @param g the graphics context to use
	 */
	protected void doPaintForeground( Graphics g ){
		Graphics2D g2 = (Graphics2D)g;
		
        Icon icon = model.getPaintIcon();
		if( icon != null ){
			icon.paintIcon( this, g, 
					(getWidth() - icon.getIconWidth()) / 2, 
					(getHeight() - icon.getIconHeight()) / 2 );
		}

		if( paintFocusBorder && hasFocus() && isFocusable() && isEnabled() ){
		    Stroke stroke = g2.getStroke();
            g2.setStroke( new BasicStroke( 3f ) );
		    g2.setColor( animation.getColor( "focus" ) );
		    g2.drawOval( 1, 1, getWidth()-3, getHeight()-3 );
		    g2.setStroke( stroke );
		}
	}

	/**
	 * Reads all {@link DockColor}s of this {@link RoundButton} and updates the animation
	 * if necessary.
	 */
    protected void updateColors() {
    	String postfix="";
    	boolean mousePressed = model.isMousePressed();
        boolean mouseEntered = model.isMouseInside();
        boolean selected = model.isSelected();
        boolean enabled = model.isEnabled();
        
    	if( enabled && mousePressed )
    		postfix = ".pressed";
    	
    	if( enabled && mouseEntered && !mousePressed )
    		postfix = ".mouse";
    	
    	if( selected )
    		postfix += ".selected";
    	
    	if( enabled )
    		postfix += ".enabled";
    	
    	String key = "action.button" + postfix;
    	for( AbstractDockColor color : colors ){
    	    if( key.equals( color.getId() )){
    	        animation.putColor( "button", color.value() );
    	        break;
    	    }
    	}
        key += ".focus";
        for( AbstractDockColor color : colors ){
            if( key.equals( color.getId() )){
                animation.putColor( "focus", color.value() );
                break;
            }
        }       
    }
    
    /**
     * Searches for the {@link AbstractDockColor} with id <code>color</code> and sets this
     * color in the {@link #getAnimation() animation}.
     * @param key the key of the color in the animation
     * @param color the key of the {@link AbstractDockColor}
     */
    protected void animate( String key, String color ){
    	for( AbstractDockColor value : colors ){
    		if( value.getId().equals( color )){
    			animation.putColor( key, value.color() );
    			return;
    		}
    	}
    }
    
    /**
     * A color used in a round button
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
