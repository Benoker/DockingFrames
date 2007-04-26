package bibliothek.gui.dock.action.views.buttons;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Insets;
import java.awt.event.MouseEvent;

import javax.swing.Icon;
import javax.swing.border.Border;

import bibliothek.gui.DockUtilities;
import bibliothek.gui.dock.action.DropDownAction;
import bibliothek.gui.dock.title.DockTitle;

/**
 * A {@link MiniButton} that shows a {@link DropDownAction}. The button is
 * divided into two sub-buttons. The smaller subbutton opens a menu where
 * the user can select an action, the greater subbutton shows the selected
 * action.
 * @author Benjamin Sigg
 *
 */
public class DropDownMiniButton extends MiniButton {
	/** The icon to show in the smaller subbutton */
	private Icon dropIcon;
	
	/** A disabled version of {@link #dropIcon} */
	private Icon disabledDropIcon;
	
	/** Tells whether the mouse is currently over the small subbutton or not */
	private boolean mouseOverDropIcon = false;
	
	/** The orientation, whether the buttons should be aligned horizontal or vertical */
	private DockTitle.Orientation orientation = DockTitle.Orientation.FREE_HORIZONTAL;
	
	/** A handler that manages this button */
	private DropDownMiniButtonHandler<?, ?> handler;
	
	/** Whether the selected action is enabled or not */
	private boolean selectionEnabled = true;
	
	/** The color of the dropicon */
	private Color dropIconColor;
	
	/**
	 * Creates a new button.
	 */
	public DropDownMiniButton(){
		setListener( new ButtonListener() );
		dropIcon = createDropIcon();
	}
	
	/**
	 * Sets the orientation of this button. The orientation tells whether
	 * the subbuttons should be aligned horizontal or vertical.
	 * @param orientation the orientation
	 */
	public void setOrientation( DockTitle.Orientation orientation ){
		this.orientation = orientation;
		repaint();
	}
	
	/**
	 * Gets the orientation of this button.
	 * @return the orientation
	 * @see #setOrientation(bibliothek.gui.dock.title.DockTitle.Orientation)
	 */
	public DockTitle.Orientation getOrientation(){
		return orientation;
	}
	
	/**
	 * Sets the handler which will manage this button.
	 * @param handler the handler, not <code>null</code>
	 */
	public void setHandler( DropDownMiniButtonHandler<?, ?> handler ){
		super.setHandler( handler );
		this.handler = handler;
	}
	
	/**
	 * Sets whether the selected action is enabled or not.
	 * @param selectionEnabled the state of the selected action
	 */
	public void setSelectionEnabled( boolean selectionEnabled ){
		this.selectionEnabled = selectionEnabled;
		repaint();
	}
	
	/**
	 * Tells whether the selected action is enabled or not. 
	 * @return the state
	 */
	public boolean isSelectionEnabled(){
		return selectionEnabled;
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
	protected void action(){
		if( mouseOverDropIcon ){
			if( handler != null )
				handler.popupTriggered();
		}
		else if( isSelectionEnabled() )
			super.action();
	}
	
	@Override
	public Dimension getPreferredSize(){
		if( isPreferredSizeSet() )
			return super.getPreferredSize();
		
		Insets insets = getMaxBorderInsets();
		Icon icon = getIcon();
		
		if( orientation.isHorizontal() ){
			int width = insets.left + 2*insets.right;
			if( icon == null )
				width += 16;
			else
				width += icon.getIconWidth();
			
			width += dropIcon.getIconWidth();
			
			int height = dropIcon.getIconHeight();
			if( icon == null )
				height = Math.max( height, 16 );
			else
				height = Math.max( height, icon.getIconHeight() );
			
			height += insets.top + insets.bottom;
			return new Dimension( width, height );
		}
		else{
			int height = insets.top + 2*insets.bottom;
			if( icon == null )
				height += 16;
			else
				height += icon.getIconHeight();
			
			height += dropIcon.getIconHeight();
			
			int width = dropIcon.getIconWidth();
			if( icon == null )
				width = Math.max( width, 16 );
			else
				width = Math.max( width, icon.getIconWidth() );
			
			width += insets.left + insets.right;			
			return new Dimension( width, height );
		}
	}
	
	@Override
	public void updateUI(){
		disabledDropIcon = null;
        handler.updateUI();
		super.updateUI();
	}
	
	@Override
	public void paint( Graphics g ){
		Border border = getBorder();
		Icon icon = getPaintIcon( isSelectionEnabled() );
		Insets insets = getMaxBorderInsets();
		
		Icon drop = dropIcon;
		if( !isEnabled() ){
			if( disabledDropIcon == null )
				disabledDropIcon = DockUtilities.disabledIcon( this, dropIcon );
			drop = disabledDropIcon;
		}
		
		if( orientation.isHorizontal() ){
			int iconWidth = icon == null ? 16 : icon.getIconWidth();
			int dropWidth = dropIcon.getIconWidth();
			
			double sum = insets.left + iconWidth + insets.right + dropWidth + insets.right;
			double factor = getWidth() / sum;
			
			if( border != null ){
				border.paintBorder( this, g, 0, 0, getWidth(), getHeight() );
				
				if( mouseOverDropIcon ){
					border.paintBorder( this, g, 0, 0, (int)(factor * (insets.left + insets.right + iconWidth )), getHeight() );
				}
			}
			
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
			
			if( border != null ){
				border.paintBorder( this, g, 0, 0, getWidth(), getHeight() );
				
				if( mouseOverDropIcon ){
					border.paintBorder( this, g, 0, 0, getWidth(), (int)(factor * (insets.top + insets.bottom + iconHeight )) );
				}
			}
			
			if( icon != null )
				icon.paintIcon( this, g, insets.left+(getWidth()-insets.left-insets.right-icon.getIconWidth()) / 2,
						(int)(factor * (insets.top + iconHeight/2) - iconHeight/2 ));
			
			drop.paintIcon( this, g, insets.left+(getWidth()-insets.left-insets.right-dropIcon.getIconWidth()) / 2,
					(int)(factor * (insets.top + insets.bottom + iconHeight + dropHeight/2 ) - dropHeight/2 ) );
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
		
		Icon icon = getPaintIcon( true );
		Insets insets = getMaxBorderInsets();
		
		if( orientation.isHorizontal() ){
			int iconWidth = icon == null ? 16 : icon.getIconWidth();
			int dropWidth = dropIcon.getIconWidth();
			
			double sum = insets.left + iconWidth + insets.right + dropWidth + insets.right;
			double factor = getWidth() / sum;
			
			int barrier = (int)(factor * (insets.left + insets.right + iconWidth )) - insets.right;
			return x > barrier;
		}
		else{
			int iconHeight = icon == null ? 16 : icon.getIconHeight();
			int dropHeight = dropIcon.getIconHeight();
			
			double sum = insets.top + iconHeight + insets.bottom + dropHeight + insets.bottom;
			double factor = getHeight() / sum;
			
			int barrier = (int)(factor * (insets.top + insets.bottom + iconHeight )) - insets.bottom;
			return y > barrier;
		}
	}
	
	/**
	 * A Mouse/MouseMotionListener to this button. Ensures that the button is
	 * painted correctly when the user moves the mouse.
	 * @author Benjamin Sigg
	 */
	protected class ButtonListener extends Listener{
		/** whether an update has been done or not */
		protected boolean updateDone = false;
		
		/**
		 * Prepares the properties that will be needed in the next update.
		 * @param e the mouse event that will trigger an update
		 * @see #updateBorder()
		 */
		protected void prepareUpdate( MouseEvent e ){
			boolean overDropIcon = isOverDropIcon( e.getX(), e.getY() );
			if( overDropIcon != mouseOverDropIcon ){
				mouseOverDropIcon = overDropIcon;
				updateDone = false;
			}
		}
		
		/**
		 * Ensures that an update is done if needed.
		 * @see #updateBorder()
		 */
		protected void ensureUpdate(){
			if( !updateDone )
				updateBorder();
		}
		
		@Override
		public void mouseDragged( MouseEvent e ){
			prepareUpdate( e );
			super.mouseDragged( e );
			ensureUpdate();
		}
		
		@Override
		public void mouseMoved( MouseEvent e ){
			prepareUpdate( e );
			super.mouseMoved( e );
			ensureUpdate();
		}
		
		@Override
		public void mouseEntered( MouseEvent e ){
			prepareUpdate( e );
			super.mouseEntered( e );
			ensureUpdate();
		}
		
		@Override
		public void mouseClicked( MouseEvent e ){
			prepareUpdate( e );
			super.mouseClicked( e );
			ensureUpdate();
		}
		
		@Override
		public void mouseExited( MouseEvent e ){
			prepareUpdate( e );
			super.mouseExited( e );
			ensureUpdate();
		}
		
		@Override
		public void mousePressed( MouseEvent e ){
			prepareUpdate( e );
			super.mousePressed( e );
			ensureUpdate();
		}
		
		@Override
		public void mouseReleased( MouseEvent e ){
			prepareUpdate( e );
			super.mouseReleased( e );
			ensureUpdate();
		}
		
		@Override
		public void updateBorder(){
			updateDone = true;
			super.updateBorder();
			repaint();
		}
	}
}
