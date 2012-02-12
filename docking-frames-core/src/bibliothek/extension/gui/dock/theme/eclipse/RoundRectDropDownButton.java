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

import bibliothek.gui.dock.action.DropDownAction;
import bibliothek.gui.dock.control.focus.FocusAwareComponent;
import bibliothek.gui.dock.themes.basic.action.BasicButtonModel;
import bibliothek.gui.dock.themes.basic.action.BasicButtonModelAdapter;
import bibliothek.gui.dock.themes.basic.action.BasicDropDownButtonHandler;
import bibliothek.gui.dock.themes.basic.action.BasicDropDownButtonModel;
import bibliothek.gui.dock.themes.basic.action.buttons.MiniButtonContent;
import bibliothek.gui.dock.util.AbstractPaintableComponent;
import bibliothek.gui.dock.util.BackgroundComponent;
import bibliothek.gui.dock.util.BackgroundPaint;
import bibliothek.gui.dock.util.Transparency;
import bibliothek.gui.dock.util.color.ColorCodes;
import bibliothek.util.Colors;

/**
 * A button with a shape of a roundrect, displaying a {@link DropDownAction}.
 * @author Benjamin Sigg
 */
@ColorCodes({
	"action.button.text",
	"action.button.text.disabled"
})
public class RoundRectDropDownButton extends JComponent implements FocusAwareComponent {
    /** a model containing all information needed to paint this button */
    private BasicDropDownButtonModel model;
    
    /** a handler reacting if this button is pressed */
    private BasicDropDownButtonHandler handler;
    
    /** the icon to show for the area in which the popup-menu could be opened */
    private Icon dropIcon;
    /** a disabled version of {@link #dropIcon} */
    private Icon disabledDropIcon;
    
    /** a piece of code that will be executed after this component requests focus */
    private Runnable afterFocusRequest;
    
    /** Component painting icon and text */
    private MiniButtonContent content;
    
    /**
     * Creates a new button
     * @param handler a handler used to announce that this button is clicked
     */
    public RoundRectDropDownButton( BasicDropDownButtonHandler handler ){
        this.handler = handler;
        model = new BasicDropDownButtonModel( this, handler, handler, true ){
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
        
        content = createButtonContent();
        setLayout( null );
        add( content );
        content.setModel( model );
        content.setForegroundColorId( "action.button.text", "action.button.text.disabled" );
        
        dropIcon = handler.getDropDownIcon();
        
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
    
    /**
     * Creates a new component that paints icon and text
     * @return the new component
     */
    protected MiniButtonContent createButtonContent(){
    	return new MiniButtonContent();
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
        
        Dimension size = content.getPreferredSize();
        
        if( model.getOrientation().isHorizontal() )
            return new Dimension( size.width + 6 + dropIcon.getIconWidth(), size.height+2 );
        else
            return new Dimension( size.width+2, size.height + 6 + dropIcon.getIconHeight() );    
    }
    
    @Override
    public void doLayout(){
        if( model.getOrientation().isHorizontal() ){
        	content.setBounds( 1, 1, getWidth()-5-dropIcon.getIconWidth(), getHeight()-2 );
        }
        else{
        	content.setBounds( 1, 1, getWidth()-2, getHeight()-5-dropIcon.getIconHeight() );
        }
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
			
			public Transparency getTransparency(){
				return Transparency.DEFAULT;
			}
		};
		paintable.paint( g );
    }
    
    private void doPaintBackground( Graphics g ){
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
        }
    }
    
    private void doPaintForeground( Graphics g ){
        Icon drop = dropIcon;
        if( !isEnabled() ){
            if( disabledDropIcon == null )
                disabledDropIcon = handler.getDisabledDropDownIcon();
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
}
