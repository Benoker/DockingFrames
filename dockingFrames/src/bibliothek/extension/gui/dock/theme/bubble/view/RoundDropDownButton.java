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
package bibliothek.extension.gui.dock.theme.bubble.view;

import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.geom.RoundRectangle2D;

import javax.swing.Icon;
import javax.swing.JComponent;
import javax.swing.event.MouseInputAdapter;

import bibliothek.extension.gui.dock.theme.BubbleTheme;
import bibliothek.extension.gui.dock.theme.bubble.BubbleColorAnimation;
import bibliothek.gui.dock.title.DockTitle.Orientation;
import bibliothek.gui.dock.util.DockUtilities;

/**
 * A button which can be pressed by the user either to execute 
 * a {@link bibliothek.gui.dock.action.DockAction} or to show a popup-menu
 * with a selection of <code>DockActions</code>. This button uses a 
 * {@link BubbleDropDownView} to trigger the various actions.
 * @author Benjamin Sigg
 */
public class RoundDropDownButton extends JComponent{
	/** the animation that changes the colors */
    private BubbleColorAnimation animation;
    /** a handle used to invoke actions */
    private BubbleDropDownView view;
    
    /** whether to layout the button horizontally or vertically */
    private Orientation orientation = Orientation.FREE_HORIZONTAL;
    
    /** the icon to show on this button */
    private Icon icon;
    /** the icon to show on this button if the button is disabled */
    private Icon disabledIcon;
    /** a disabled version of {@link #icon} or <code>null</code> */
    private Icon autoDisabledIcon;
    
    /** the icon to show for the area in which the popup-menu could be opened */
    private Icon dropIcon;
    /** a disabled version of {@link #dropIcon} */
    private Icon disabledDropIcon;
    
    /** a listener added to various components */
    private Listener listener = new Listener();
    
    /**
     * Whether this button is selected or not. Usefull if this button represents
     * a checkbox or a radiobutton
     */
    private boolean selected = false;
    /** Whether the selected action is enabled or not */
    private boolean selectionEnabled = true;
    
    /**
     * Creates a new button
     * @param theme the theme which delivers the colors used to paint this button
     * @param view the view used to trigger actions
     */
    public RoundDropDownButton( BubbleTheme theme, BubbleDropDownView view ){
        animation = new BubbleColorAnimation( theme );
        animation.putColor( "background", "dropdown" );
        animation.addTask( new Runnable(){
            public void run() {
                repaint();
            }
        });
        
        this.view = view;
        
        dropIcon = createDropIcon();
        
        addMouseListener( listener );
        addMouseMotionListener( listener );
    }
    
    @Override
    public void updateUI() {
        super.updateUI();
        if( view != null )
            view.updateUI();
    }
    
    /**
     * Sets whether the selected action is enabled or not. The icon of the
     * action might be painted in another way.
     * @param selectionEnabled the state of the selected action
     */
    public void setSelectionEnabled( boolean selectionEnabled ) {
        this.selectionEnabled = selectionEnabled;
        repaint();
    }
    
    /**
     * Tells whether the selected action is enabled or not.
     * @return <code>true</code> if the selected action is enabled
     */
    public boolean isSelectionEnabled() {
        return selectionEnabled;
    }
    
    /**
     * Sets the icon which will be painted on this button.
     * @param icon the icon or <code>null</code>
     */
    public void setIcon( Icon icon ){
        this.icon = icon;
        autoDisabledIcon = null;
        revalidate();
        repaint();
    }
    
    /**
     * Sets the icon which will be painted on this button if the button
     * is not enabled or the selected action is not enabled.
     * @param disabledIcon the icon or <code>null</code>
     */
    public void setDisabledIcon( Icon disabledIcon ) {
        this.disabledIcon = disabledIcon;
        autoDisabledIcon = null;
        revalidate();
        repaint();
    }
    
    /**
     * Sets whether this button is selected or not.
     * @param selected <code>true</code> if the button is selected
     */
    public void setSelected( boolean selected ) {
        this.selected = selected;
        listener.updateColors();
    }
    
    @Override
    public void setEnabled( boolean enabled ) {
        super.setEnabled( enabled );
        listener.updateColors();
    }
    
    /**
     * Sets whether the layout should be horizontally or vertically.
     * @param orientation the orientation of the layout
     */
    public void setOrientation( Orientation orientation ) {
        this.orientation = orientation;
        revalidate();
    }
    
    /**
     * Gets the orientation of the layout.
     * @return the orientation
     * @see #setOrientation(Orientation)
     */
    public Orientation getOrientation() {
        return orientation;
    }
    
    @Override
    public Dimension getPreferredSize() {
        if( isPreferredSizeSet() )
            return super.getPreferredSize();
        
        int w = -1;
        int h = -1;
        
        if( icon != null ){
            w = icon.getIconWidth();
            h = icon.getIconHeight();
        }
        if( disabledIcon != null ){
            w = Math.max( w, disabledIcon.getIconWidth() );
            h = Math.max( h, disabledIcon.getIconHeight() );
        }
        
        if( w == -1 )
            w = 10;
        
        if( h == -1 )
            h = 10;
        
        if( orientation.isHorizontal() )
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
    	
    	if( orientation.isHorizontal() )
    		rect = new RoundRectangle2D.Double( 0, 0, w, h, h, h );
    	else
    		rect = new RoundRectangle2D.Double( 0, 0, w, h, w, w );
    	
    	return rect.contains( x, y );
    }
    
    @Override
    protected void paintComponent( Graphics g ) {
        Icon drop = dropIcon;
        if( !isEnabled() ){
            if( disabledDropIcon == null )
                disabledDropIcon = DockUtilities.disabledIcon( this, dropIcon );
            drop = disabledDropIcon;
        }
        
        Graphics2D g2 = (Graphics2D)g.create();
        g2.setRenderingHint( RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON );
        
        int x = 0;
        int y = 0;
        int w = getWidth();
        int h = getHeight();
        
        Icon icon = getPaintIcon( isEnabled() && selectionEnabled );
        
        int iconWidth = icon == null ? 10 : icon.getIconWidth();
        int iconHeight = icon == null ? 10 : icon.getIconHeight();
        
        int dropIconWidth = drop == null ? 5 : drop.getIconWidth();
        int dropIconHeight = drop == null ? 5 : drop.getIconHeight();
        
        if( orientation.isHorizontal() ){
            g2.setColor( animation.getColor( "background" ) );
            g2.fillRoundRect( x, y, w, h, h, h );
            
            g2.setColor( animation.getColor( "mouse" ) );
            int mx = x + (int)( 0.5 * 1.25 * iconWidth + 0.5 * (w - 1.25 * dropIconWidth) );
            g2.drawLine( mx, y+1, mx, y+h-2 );
            
            
            if( icon != null ){
                icon.paintIcon( this, g, (int)(x + 0.25 * iconWidth ), y+(h-iconHeight)/2 );
            }
            if( drop != null )
            	drop.paintIcon( this, g, (int)(x + w - 1.25 * dropIconWidth), y+(h-dropIconHeight)/2 );
        }
        else{
            g2.setColor( animation.getColor( "background" ) );
            g2.fillRoundRect( x, y, w, h, w, w );
            
            g2.setColor( animation.getColor( "mouse" ) );
            int my = y + (int)( 0.5 * 1.25 * iconHeight + 0.5 * (h - 1.25 * dropIconHeight) );
            g2.drawLine( x+1, my, x+w-2, my );
            
            
            if( icon != null ){
                icon.paintIcon( this, g, x+(w-iconWidth)/2, (int)(y + 0.25*iconHeight));
            }
            if( drop != null )
            	drop.paintIcon( this, g, x + ( w - dropIconWidth ) / 2, (int)(y+h-1.25*dropIconHeight) );
        }
        
        g2.dispose();
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
     * Gets the icon that will be painted in the center of this button
     * @param enabled whether the enabled or the disabled icon is requested.
     * @return the icon, might be <code>null</code>
     */
    protected Icon getPaintIcon( boolean enabled ){
        if( enabled )
            return icon;
        else{
            if( disabledIcon != null )
                return disabledIcon;
            
            if( autoDisabledIcon == null ){
                autoDisabledIcon = DockUtilities.disabledIcon( this, icon );
            }
                
            return autoDisabledIcon;
        }
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
        
        Icon icon = getPaintIcon( isEnabled() && selectionEnabled );
        
        int iconWidth = icon == null ? 10 : icon.getIconWidth();
        int iconHeight = icon == null ? 10 : icon.getIconHeight();
        
        int dropIconWidth = dropIcon == null ? 5 : dropIcon.getIconWidth();
        int dropIconHeight = dropIcon == null ? 5 : dropIcon.getIconHeight();
        
        if( orientation.isHorizontal() ){
        	int mx = rx + (int)( 0.5 * 1.25 * iconWidth + 0.5 * (rw - 1.25 * dropIconWidth) );
        	return x >= mx;
        }
        else{
        	int my = ry + (int)( 0.5 * 1.25 * iconHeight + 0.5 * (rh - 1.25 * dropIconHeight) );
        	return y >= my;
        }
    }
    
    /**
     * A listener listening to the mouse-events of a {@link RoundDropDownButton}.
     * @author Benjamin Sigg
     */
    private class Listener extends MouseInputAdapter{
    	/** whether the mouse is pressed */
        private boolean pressed = false;
        /** whether the mouse is over the button */
        private boolean entered = false;
        /** Whether the mouse is over the drop-area or not */
        private boolean mouseOverDrop = false;
        
        
        /**
         * Updates the colors of the animation.
         */
        public void updateColors(){
            String postfix = "";
            boolean enabled = isEnabled();
            
            if( selected )
                postfix = ".selected";
            
            if( enabled )
                postfix += ".enabled";
            
            String mouse;
            if( mouseOverDrop && enabled )
            	mouse = "dropdown.line";
            else
            	mouse = "dropdown";
            
            if( pressed && enabled ){
                animation.putColor( "background", "dropdown.pressed" + postfix );
                animation.putColor( "mouse", mouse + ".pressed" + postfix );
            }
            else if( entered && enabled ){
                animation.putColor( "background", "dropdown.mouse" + postfix );
                animation.putColor( "mouse", mouse + ".mouse" + postfix );
            }
            else{
                animation.putColor( "background", "dropdown" + postfix );
                animation.putColor( "mouse", mouse + postfix );
            }
        }
        
        /**
         * Changes the value of {@link #mouseOverDrop} according to the
         * location of the mouse.
         * @param e the location of the mouse
         */
        private void updateMouseOverDrop( MouseEvent e ){
        	boolean next = overDropIcon( e.getX(), e.getY() );
        	if( next != mouseOverDrop ){
        		mouseOverDrop = next;
        		updateColors();
        	}
        }
        
        @Override
        public void mouseEntered( MouseEvent e ) {
            entered = true;
            updateMouseOverDrop( e );
            updateColors();
        }
        @Override
        public void mouseExited( MouseEvent e ) {
            entered = false;
            updateMouseOverDrop( e );
            updateColors();
        }
        @Override
        public void mousePressed( MouseEvent e ) {
            if( !pressed ){
                if( e.getButton() == MouseEvent.BUTTON1 ){
                    pressed = true;
                    updateColors();
                }
                updateMouseOverDrop( e );
            }
        }
        
        @Override
        public void mouseReleased( MouseEvent e ) {
            if( pressed && e.getButton() == MouseEvent.BUTTON1 ){
                pressed = false;
                Point mouse = e.getPoint();
                if( contains( mouse )){
                    if( isEnabled() ){
                        view.trigger( overDropIcon( mouse.x, mouse.y ) );
                    }
                    entered = true;
                }
                else{
                    entered = false;
                }
                updateMouseOverDrop( e );
                updateColors();
            }
        }
        
        @Override
        public void mouseMoved( MouseEvent e ){
        	updateMouseOverDrop( e );
        }
        
        @Override
        public void mouseDragged( MouseEvent e ){
        	updateMouseOverDrop( e );
        }
    }
}
