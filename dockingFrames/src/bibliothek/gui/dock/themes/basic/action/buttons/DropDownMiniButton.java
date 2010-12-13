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

package bibliothek.gui.dock.themes.basic.action.buttons;

import java.awt.*;

import javax.swing.Icon;
import javax.swing.border.Border;

import bibliothek.gui.dock.action.DropDownAction;
import bibliothek.gui.dock.themes.basic.action.BasicDropDownButtonHandler;
import bibliothek.gui.dock.themes.basic.action.BasicDropDownButtonModel;
import bibliothek.gui.dock.util.AbstractPaintableComponent;
import bibliothek.gui.dock.util.BackgroundComponent;
import bibliothek.gui.dock.util.BackgroundPaint;
import bibliothek.gui.dock.util.DockUtilities;

/**
 * A {@link MiniButton} that shows a {@link DropDownAction}. The button is
 * divided into two sub-buttons. The smaller subbutton opens a menu where
 * the user can select an action, the greater subbutton shows the selected
 * action.
 * @author Benjamin Sigg
 *
 */
public class DropDownMiniButton extends MiniButton<BasicDropDownButtonModel> {
	/** The icon to show in the smaller subbutton */
	private Icon dropIcon;
	
	/** A disabled version of {@link #dropIcon} */
	private Icon disabledDropIcon;
	
	/** The color of the dropicon */
	private Color dropIconColor;
	
    private BasicDropDownButtonHandler handler;
    
	/**
	 * Creates a new button.
	 * @param handler handler for the internal states of this button
	 */
	public DropDownMiniButton( BasicDropDownButtonHandler handler ){
        super( null );
        
        this.handler = handler;
        
        BasicDropDownButtonModel model = new BasicDropDownButtonModel( this, handler, handler ){
            @Override
            public void changed() {
                updateBorder();
                repaint();
            }
            
            @Override
            protected boolean inDropDownArea( int x, int y ) {
                return isOverDropIcon( x, y );
            }
            
        };
        setModel( model );
        
		dropIcon = createDropIcon();
	}
	
	/**
	 * Sets the color that will be used to paint the icon on the smaller
	 * sub-button.
	 * @param dropIconColor the color, <code>null</code> if the default-color
	 * should be used.
	 */
	public void setDropIconColor( Color dropIconColor ){
		this.dropIconColor = dropIconColor;
		disabledDropIcon = null;
		repaint();
	}
	
	/**
	 * Gets the color that is used to paint the small drop-down icon.
	 * @return the color, can be <code>null</code>
	 */
	public Color getDropIconColor(){
		return dropIconColor;
	}
	
	@Override
	public void setForeground( Color fg ){
		disabledDropIcon = null;
		super.setForeground( fg );
		repaint();
	}
	
	@Override
	public Dimension getPreferredSize(){
		if( isPreferredSizeSet() )
			return super.getPreferredSize();
		
		Insets insets = getMaxBorderInsets();
		Dimension icon = getModel().getMaxIconSize();
        
        icon.width = Math.max( icon.width, 16 );
        icon.height = Math.max( icon.height, 16 );
		
		if( getModel().getOrientation().isHorizontal() ){
			int width = insets.left + 2*insets.right + icon.width;
			width += dropIcon.getIconWidth();
			
			int height = dropIcon.getIconHeight();
			if( icon == null )
				height = Math.max( height, 16 );
			else
				height = Math.max( height, icon.height );
			
			height += insets.top + insets.bottom;
			return new Dimension( width, height );
		}
		else{
			int height = insets.top + 2*insets.bottom + icon.height;
			height += dropIcon.getIconHeight();
			
			int width = dropIcon.getIconWidth();
			if( icon == null )
				width = Math.max( width, 16 );
			else
				width = Math.max( width, icon.width );
			
			width += insets.left + insets.right;			
			return new Dimension( width, height );
		}
	}
	
	@Override
	public void updateUI(){
		disabledDropIcon = null;
        if( handler != null )
            handler.updateUI();
        
		super.updateUI();
	}
	
	@Override
	public void paint( Graphics g ){
		BasicDropDownButtonModel model = getModel();
		BackgroundPaint paint = model.getBackground();
		BackgroundComponent component = model.getBackgroundComponent();
		
		AbstractPaintableComponent paintable = new AbstractPaintableComponent( component, this, paint ){
			protected void background( Graphics g ){
				// ignore
			}
			
			protected void foreground( Graphics g ){
				doPaintForeground( g );
			}
			
			protected void border( Graphics g ){
				doPaintBorder( g );
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
		paintable.paint( g );
	}
	
	private void doPaintForeground( Graphics g ){
		Icon icon = getModel().getPaintIcon();
		Insets insets = getMaxBorderInsets();
		
		Icon drop = dropIcon;
		if( !isEnabled() ){
			if( disabledDropIcon == null )
				disabledDropIcon = DockUtilities.disabledIcon( this, dropIcon );
			drop = disabledDropIcon;
		}
		
		if( getModel().getOrientation().isHorizontal() ){
			int iconWidth = icon == null ? 16 : icon.getIconWidth();
			int dropWidth = dropIcon.getIconWidth();
			
			double sum = insets.left + iconWidth + insets.right + dropWidth + insets.right;
			double factor = getWidth() / sum;
			
			if( icon != null )
				icon.paintIcon( this, g, (int)(factor * (insets.left + iconWidth/2) - iconWidth/2), 
						insets.top+(getHeight()-insets.top-insets.bottom-icon.getIconHeight()) / 2 );
			
			drop.paintIcon( this, g, (int)(factor * (insets.left + insets.right + iconWidth + dropWidth/2) - dropWidth/2 ),
					insets.top+(getHeight()-insets.top-insets.bottom-dropIcon.getIconHeight()) / 2 );
		}
		else{
			int iconHeight = icon == null ? 16 : icon.getIconHeight();
			int dropHeight = dropIcon.getIconHeight();
			
			double sum = insets.top + iconHeight + insets.bottom + dropHeight + insets.bottom;
			double factor = getHeight() / sum;
			
			if( icon != null )
				icon.paintIcon( this, g, insets.left+(getWidth()-insets.left-insets.right-icon.getIconWidth()) / 2,
						(int)(factor * (insets.top + iconHeight/2) - iconHeight/2 ));
			
			drop.paintIcon( this, g, insets.left+(getWidth()-insets.left-insets.right-dropIcon.getIconWidth()) / 2,
					(int)(factor * (insets.top + insets.bottom + iconHeight + dropHeight/2 ) - dropHeight/2 ) );
		}
		
		if( isFocusOwner() && isFocusable() && isEnabled() ){
		    paintFocus( g );
		}
	}
	
	private void doPaintBorder( Graphics g ){
		Border border = getBorder();
		
		if( border != null ){
			Insets insets = getMaxBorderInsets();
			Icon icon = getModel().getPaintIcon();
			
			if( getModel().getOrientation().isHorizontal() ){
				int iconWidth = icon == null ? 16 : icon.getIconWidth();
				int dropWidth = dropIcon.getIconWidth();
				
				double sum = insets.left + iconWidth + insets.right + dropWidth + insets.right;
				double factor = getWidth() / sum;
				
				border.paintBorder( this, g, 0, 0, getWidth(), getHeight() );
				
				if( getModel().isMouseOverDropDown() ){
					border.paintBorder( this, g, 0, 0, (int)(factor * (insets.left + insets.right + iconWidth )), getHeight() );
				}
			}
			else{
				int iconHeight = icon == null ? 16 : icon.getIconHeight();
				int dropHeight = dropIcon.getIconHeight();
				
				double sum = insets.top + iconHeight + insets.bottom + dropHeight + insets.bottom;
				double factor = getHeight() / sum;
				
				if( getModel().isMouseOverDropDown() ){
					border.paintBorder( this, g, 0, 0, getWidth(), (int)(factor * (insets.top + insets.bottom + iconHeight )) );
				}
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
				if( dropIconColor == null )
					g.setColor( getForeground() );
				else
					g.setColor( dropIconColor );
				g.drawLine( x, y+1, x+4, y+1 );
				g.drawLine( x+1, y+2, x+3, y+2 );
				g.drawLine( x+2, y+3, x+2, y+3 );
			}
		};
	}
	
	/**
	 * Tells whether the point <code>x/y</code> is over the smaller subbutton
	 * or not.
	 * @param x the x-coordinate
	 * @param y the y-coordinate
	 * @return <code>true</code> if the smaller subbutton is under x/y
	 */
	public boolean isOverDropIcon( int x, int y ){
		if( !contains( x, y ))
			return false;
		
		Dimension icon = getModel().getMaxIconSize();
		Insets insets = getMaxBorderInsets();
        
		if( getModel().getOrientation().isHorizontal() ){
			int iconWidth = icon.width < 16 ? 16 : icon.width;
			int dropWidth = dropIcon.getIconWidth();
			
			double sum = insets.left + iconWidth + insets.right + dropWidth + insets.right;
			double factor = getWidth() / sum;
			
			int barrier = (int)(factor * (insets.left + insets.right + iconWidth )) - insets.right;
			return x > barrier;
		}
		else{
			int iconHeight = icon.height < 16 ? 16 : icon.height;
			int dropHeight = dropIcon.getIconHeight();
			
			double sum = insets.top + iconHeight + insets.bottom + dropHeight + insets.bottom;
			double factor = getHeight() / sum;
			
			int barrier = (int)(factor * (insets.top + insets.bottom + iconHeight )) - insets.bottom;
			return y > barrier;
		}
	}
}
