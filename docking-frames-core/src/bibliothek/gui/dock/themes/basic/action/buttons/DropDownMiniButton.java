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

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Insets;

import javax.swing.Icon;
import javax.swing.border.Border;

import bibliothek.gui.dock.action.DropDownAction;
import bibliothek.gui.dock.themes.basic.action.BasicDropDownButtonHandler;
import bibliothek.gui.dock.themes.basic.action.BasicDropDownButtonModel;
import bibliothek.gui.dock.util.AbstractPaintableComponent;
import bibliothek.gui.dock.util.BackgroundComponent;
import bibliothek.gui.dock.util.BackgroundPaint;
import bibliothek.gui.dock.util.Transparency;

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
        
		dropIcon = handler.getDropDownIcon();
	}
	
	@Override
	public void setForeground( Color fg ){
		super.setForeground( fg );
		repaint();
	}
	
	@Override
	public Dimension getPreferredSize(){
		if( isPreferredSizeSet() )
			return super.getPreferredSize();
		
		Insets insets = getMaxBorderInsets();
		Dimension icon = getContent().getPreferredSize();
        
		if( getModel().getOrientation().isHorizontal() ){
			int width = insets.left + 2*insets.right + icon.width;
			width += dropIcon.getIconWidth();
			
			int height = dropIcon.getIconHeight();
			height = Math.max( height, icon.height );
			height += insets.top + insets.bottom;
			return new Dimension( width, height );
		}
		else{
			int height = insets.top + 2*insets.bottom + icon.height;
			height += dropIcon.getIconHeight();
			
			int width = dropIcon.getIconWidth();
			width = Math.max( width, icon.width );
			width += insets.left + insets.right;			
			return new Dimension( width, height );
		}
	}
	
	@Override
	public void updateUI(){
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
			
			public Transparency getTransparency(){
				return Transparency.DEFAULT;
			}
		};
		paintable.paint( g );
	}
	
	
	@Override
	public void doLayout(){
		MiniButtonContent content = getContent();
		Insets insets = getMaxBorderInsets();
		
		if( getModel().getOrientation().isHorizontal() ){
			int dropWidth = dropIcon.getIconWidth();
			content.setBounds( insets.left+1, insets.top, getWidth()-insets.left-2*insets.right-1-dropWidth, getHeight()-insets.top-insets.bottom );
		}
		else{
			int dropHeight = dropIcon.getIconHeight();
			content.setBounds( insets.left, insets.top+1, getWidth()-insets.left-insets.right, getHeight()-insets.top-2*insets.bottom-1-dropHeight );
		}
	}
	
	private void doPaintForeground( Graphics g ){
		paintContent( g );
		
		Insets insets = getMaxBorderInsets();
		MiniButtonContent content = getContent();
		
		Icon drop = dropIcon;
		if( !isEnabled() ){
			if( disabledDropIcon == null )
				disabledDropIcon = handler.getDisabledDropDownIcon();
			drop = disabledDropIcon;
		}
		
		if( getModel().getOrientation().isHorizontal() ){
			int iconWidth = content.getWidth();
			int dropWidth = dropIcon.getIconWidth();
			
			double sum = insets.left + iconWidth + insets.right + dropWidth + insets.right;
			double factor = getWidth() / sum;
			
//			if( icon != null )
//				icon.paintIcon( this, g, (int)(factor * (insets.left + iconWidth/2) - iconWidth/2), 
//						insets.top+(getHeight()-insets.top-insets.bottom-icon.getIconHeight()) / 2 );
			
			drop.paintIcon( this, g, (int)(factor * (insets.left + insets.right + iconWidth + dropWidth/2) - dropWidth/2 ),
					insets.top+(getHeight()-insets.top-insets.bottom-dropIcon.getIconHeight()) / 2 );
		}
		else{
			int iconHeight = content.getHeight();
			int dropHeight = dropIcon.getIconHeight();
			
			double sum = insets.top + iconHeight + insets.bottom + dropHeight + insets.bottom;
			double factor = getHeight() / sum;
			
//			if( icon != null )
//				icon.paintIcon( this, g, insets.left+(getWidth()-insets.left-insets.right-icon.getIconWidth()) / 2,
//						(int)(factor * (insets.top + iconHeight/2) - iconHeight/2 ));
			
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
			
			if( getModel().getOrientation().isHorizontal() ){
				int iconWidth = getContent().getWidth();
				int dropWidth = dropIcon.getIconWidth();
				
				double sum = insets.left + iconWidth + insets.right + dropWidth + insets.right;
				double factor = getWidth() / sum;
				
				border.paintBorder( this, g, 0, 0, getWidth(), getHeight() );
				
				if( getModel().isMouseOverDropDown() ){
					border.paintBorder( this, g, 0, 0, (int)(factor * (insets.left + insets.right + iconWidth )), getHeight() );
				}
			}
			else{
				int iconHeight = getContent().getHeight();
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
	 * Tells whether the point <code>x/y</code> is over the smaller subbutton
	 * or not.
	 * @param x the x-coordinate
	 * @param y the y-coordinate
	 * @return <code>true</code> if the smaller subbutton is under x/y
	 */
	public boolean isOverDropIcon( int x, int y ){
		if( !contains( x, y ))
			return false;
		
		Insets insets = getMaxBorderInsets();
        
		if( getModel().getOrientation().isHorizontal() ){
			int iconWidth = getContent().getWidth();
			int dropWidth = dropIcon.getIconWidth();
			
			double sum = insets.left + iconWidth + insets.right + dropWidth + insets.right;
			double factor = getWidth() / sum;
			
			int barrier = (int)(factor * (insets.left + insets.right + iconWidth )) - insets.right;
			return x > barrier;
		}
		else{
			int iconHeight = getContent().getHeight();
			int dropHeight = dropIcon.getIconHeight();
			
			double sum = insets.top + iconHeight + insets.bottom + dropHeight + insets.bottom;
			double factor = getHeight() / sum;
			
			int barrier = (int)(factor * (insets.top + insets.bottom + iconHeight )) - insets.bottom;
			return y > barrier;
		}
	}
}
