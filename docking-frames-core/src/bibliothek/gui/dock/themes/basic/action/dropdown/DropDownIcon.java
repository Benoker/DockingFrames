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
package bibliothek.gui.dock.themes.basic.action.dropdown;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;

import javax.swing.Icon;
import javax.swing.JComponent;

import bibliothek.gui.DockController;
import bibliothek.gui.Dockable;
import bibliothek.gui.dock.action.DockActionIcon;
import bibliothek.gui.dock.action.DropDownAction;
import bibliothek.gui.dock.action.view.ViewItem;
import bibliothek.gui.dock.themes.color.ActionColor;
import bibliothek.gui.dock.util.DockUtilities;
import bibliothek.gui.dock.util.IconManager;
import bibliothek.gui.dock.util.PropertyValue;
import bibliothek.gui.dock.util.color.ColorCodes;

/**
 * This {@link Icon} is painted on buttons for {@link DropDownAction}s. Usually this icon
 * looks like a small arrow pointing down, and clicking onto this icon should open a
 * drop down menu.
 * @author Benjamin Sigg
 */
@ColorCodes({"action.dropdown.arrow"})
public class DropDownIcon implements Icon{
	public static final String ICON_KEY = "dock.dropdown.icon";
	
	/** The icon that is actually painted, can be replaced */
	private Icon icon;
	
	/** The disabled version of {@link #icon} */
	private Icon disabledIcon;
	
	/** Whether the properties changed since the last access to {@link #disabledIcon} */
	private boolean disabledIconInvalid = true;
	
	/** the owner of {@link #action} */
	private Dockable dockable;
	
	/** The action for which this icon is used */
	private DropDownAction action;
	
	/** The component on which this icon is painted */
	private ViewItem<? extends JComponent> parent;
	
	/** the color of the default icon */
	private IconColor color;
	
	/** A value overriding {@link #icon} */
	private IconValue replacingIcon;
	
	/** the current minimum size an icon is expected to have */
	private PropertyValue<Dimension> minimumSize = new PropertyValue<Dimension>( IconManager.MINIMUM_ICON_SIZE ){
		@Override
		protected void valueChanged( Dimension oldValue, Dimension newValue ){
			resetIcon();
		}
	};
	
	/**
	 * Creates a new icon. The caller should call {@link #init(Dockable, DropDownAction, ViewItem)} to fully
	 * utilize this icon.
	 */
	public DropDownIcon(){
		resetIcon();
	}

	/**
	 * Initializes this icon by setting missing properties.
	 * @param dockable the owner of <code>action</code>, not <code>null</code>
	 * @param action the action for which this icon is used, must not be <code>null</code>
	 * @param parent the {@link JComponent} on which this icon is painted, not <code>null</code>. Please
	 * note that this component must not be cast into a subclass.
	 */
	public void init( Dockable dockable, DropDownAction action, ViewItem<? extends JComponent> parent ){
		if( isInitialized() ){
			throw new IllegalStateException( "this icon is already initialized" );
		}
		
		if( dockable == null ){
			throw new IllegalArgumentException( "dockable must not be null" );
		}
		if( action == null ){
			throw new IllegalArgumentException( "action must not be null" );
		}
		if( parent == null ){
			throw new IllegalArgumentException( "parent must not be null" );
		}
		
		this.dockable = dockable;
		this.action = action;
		this.parent = parent;
		
		color = new IconColor();
		replacingIcon = new IconValue();
		
		resetIcon();
	}
	
	/**
	 * Tells whether {@link #init(Dockable, DropDownAction, ViewItem)} has already been called.
	 * @return <code>true</code> if this icon is initialized
	 */
	public boolean isInitialized(){
		return dockable != null;
	}
	
	/**
	 * Gets the action for which this icon is used.
	 * @return the owner of this icon, not <code>null</code>
	 */
	public DropDownAction getAction(){
		return action;
	}
	
	/**
	 * Gets the {@link Component} on which this icon is painted. 
	 * @return the component, not <code>null</code>
	 */
	public JComponent getParent(){
		if( parent == null ){
			return null;
		}
		return parent.getItem();
	}
	
	/**
	 * Links this icon with <code>controller</code>, this icon may change is appearance
	 * due to the properties stored in <code>controller</code>.
	 * @param controller the properties to use or <code>null</code>
	 */
	public void setController( DockController controller ){
		if( !isInitialized() ){
			throw new IllegalStateException( "this icon must be initialized first" );
		}
		
		color.connect( controller );
		minimumSize.setProperties( controller );
		replacingIcon.setController( controller );
	}
	
	/**
	 * Gets a disabled version of this icon. The returned icon is lazily updated if the
	 * properties of this icon changes.
	 * @return the icon, not <code>null</code>, has always the same size as <code>this</code> icon
	 */
	public Icon getDisabledIcon(){
		if( disabledIcon == null ){
			disabledIcon = new DisabledIcon();
		}
		return disabledIcon;
	}
	
	public int getIconHeight(){
		return icon.getIconHeight();
	}
	
	public int getIconWidth(){
		return icon.getIconWidth();
	}
	
	public void paintIcon( Component c, Graphics g, int x, int y ){
		icon.paintIcon( c, g, x, y );
	}
	
	/**
	 * Recalculates what icon should be shown
	 */
	protected void resetIcon(){
		disabledIconInvalid = true;
		
		int oldWidth = -1;
		int oldHeight = -1;
		
		if( icon != null ){
			oldWidth = icon.getIconWidth();
			oldHeight = icon.getIconHeight();
		}
		
		Icon newIcon = null;
		if( replacingIcon != null ){
			newIcon = replacingIcon.value();
		}
		if( newIcon == null ){
			Dimension size = minimumSize.getValue();
			int width = size.width / 2 - size.width / 16;
			int height = size.height / 2 - size.height / 16;
			
			width = Math.max( 1, width );
			height = Math.max( 1, height );
			
			Color iconColor = null;
			if( color != null ){
				iconColor = color.value();
			}
			
			newIcon = new DefaultIcon( width, height, iconColor );
		}
		
		icon = newIcon;
		
		JComponent parent = getParent();
		if( parent != null ){
			if( oldWidth != icon.getIconHeight() || oldHeight != icon.getIconHeight() ){
				parent.invalidate();
			}
			parent.repaint();
		}
	}
	
	/**
	 * The color used to paint the {@link DefaultIcon}.
	 * @author Benjamin Sigg
	 */
	private class IconColor extends ActionColor{
		public IconColor(){
			super( "action.dropdown.arrow", dockable, action, null );
		}

		@Override
		protected void changed( Color oldValue, Color newValue ){
			resetIcon();
		}		
	}
	
	/**
	 * The icon that may override the default icon
	 * @author Benjamin Sigg
	 */
	private class IconValue extends DockActionIcon{
		public IconValue(){
			super( ICON_KEY, DropDownIcon.this.getAction() );
		}

		@Override
		protected void changed( Icon oldValue, Icon newValue ){
			resetIcon();
		}
	}
	
	/**
	 * A default implementation of an icon, shows a triangle pointing downwards
	 * @author Benjamin Sigg
	 */
	private class DefaultIcon implements Icon{
		/** the color used to paint this icon, can be <code>null</code> */
		private Color color;
		
		/** the width in pixel */
		private int width;
		/** the height in pixel */
		private int height;
		
		/**
		 * Creates a new icon.
		 * @param width the width of this icon in pixel
		 * @param height the height of this icon in pixel
		 * @param color the color of this icon, can be <code>null</code>
		 */
		public DefaultIcon( int width, int height, Color color ){
			this.width = width;
			this.height = height;
			this.color = color;
		}
		
		public int getIconHeight(){
			return width; 
		}
		public int getIconWidth(){
			return height;
		}
		public void paintIcon( Component c, Graphics g, int x, int y ){
			if( color == null ){
				g.setColor( c.getForeground() );
			}
			else{
				g.setColor( color );
			}
			
			int factor = Math.min( width, height );
			if( factor == 7 ){
				x++;
				
				g.drawLine( x, y+1,x+4, y+1 );
				g.drawLine( x+1, y+2, x+3, y+2 );
				g.drawLine( x+2, y+3, x+2, y+3 );	
			}
			else{
				float unit = Math.max( 1, factor / 7.0f );
				
				float xu = x + unit;
				
				int[] xs = new int[3];
				int[] ys = new int[3];
				
				xs[0] = (int)(xu+unit);
				ys[0] = (int)(y+1*unit);
				
				xs[1] = (int)(xu+4*unit);
				ys[1] = (int)(y+1*unit);
				
				xs[2] = (int)(xu+2.5f*unit);
				ys[2] = (int)(y+3*unit);
				
				g.fillPolygon( xs, ys, 3 );
			}
		}
	}
	
	/**
	 * A wrapper around {@link DropDownIcon#icon} painting a disabled version of the icon
	 * @author Benjamin Sigg
	 */
	private class DisabledIcon implements Icon{
		private Icon disabled;
		private Color lastForeground;
		
		public int getIconWidth(){
			return DropDownIcon.this.getIconWidth();
		}
		
		public int getIconHeight(){
			return DropDownIcon.this.getIconHeight();
		}
		
		public void paintIcon( Component c, Graphics g, int x, int y ){
			Color currentForeground = c.getForeground();
			
			if( disabledIconInvalid || disabled == null || lastForeground == null || !lastForeground.equals( currentForeground ) ){
				disabledIconInvalid = false;
				lastForeground = currentForeground;
				disabled = DockUtilities.disabledIcon( getParent(), icon );
			}

			disabled.paintIcon( c, g, x, y );
		}
	}
}
