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

package bibliothek.gui.dock.title;

import java.awt.*;

import javax.swing.Icon;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.event.MouseInputListener;

import bibliothek.gui.Dockable;
import bibliothek.gui.dock.action.ActionPopup;
import bibliothek.gui.dock.action.DockAction;
import bibliothek.gui.dock.action.DockActionSource;
import bibliothek.gui.dock.action.view.ViewTarget;
import bibliothek.gui.dock.event.DockTitleEvent;
import bibliothek.gui.dock.event.DockableListener;
import bibliothek.gui.dock.themes.basic.action.BasicTitleViewItem;
import bibliothek.gui.dock.themes.basic.action.buttons.ButtonPanel;

/**
 * An abstract implementation of {@link DockTitle}. This title can have
 * an icon, a title-text and some small buttons to display {@link DockAction actions}.
 * The icon is at the top or left edge, the text in the middle, and the actions
 * at the lower or the right edge of the title. If the orientation of the
 * title is set to {@link Orientation vertical}, the text will be rotated
 * by 90 degrees.<br>
 * This title has also an {@link ActionPopup} which will appear when the user
 * presses the right mouse-button. The popup shows a list of all actions known
 * to this title.<br>
 * The whole logic a {@link DockTitle} needs is implemented in this class,
 * but subclasses may add graphical features - like a border or another
 * background.<br>
 * Subclasses may override {@link #getInnerInsets()} to add a space between
 * border and contents of this title.
 * 
 * @author Benjamin Sigg
 *
 */
public class AbstractDockTitle extends JPanel implements DockTitle {
    /** Insets of the size 0,2,0,0 */
    private static final Insets DEFAULT_INSETS_HORIZONTAL = new Insets( 0, 2, 0, 0 );
    /** Insets of the size 2,0,0,0 */
    private static final Insets DEFAULT_INSETS_VERTICAL = new Insets( 2, 0, 0, 0 );
    
    /** The {@link Dockable} for which this title is shown */
    private Dockable dockable;
    
    /** A label for the title-text */
    private OrientedLabel label = new OrientedLabel();
    /** A panel that displays the action-buttons of this title */
    private ButtonPanel itemPanel;
    
    /** 
     * A listener added to the owned {@link Dockable}. The listener changes the
     * title-text and the icon of this title. 
     */
    private Listener listener = new Listener();
    /** The creator of this title */
    private DockTitleVersion origin;
    
    /** <code>true</code> if this title is currently selected, <code>false</code> otherwise */
    private boolean active = false;
    /** <code>true</code> if this title is currently bound to a {@link Dockable} */
    private boolean bind = false;
    /** Tells whether small buttons for each action should be created and shown, or not */
    private boolean showMiniButtons = true;
    
    /** Whether the layout should be horizontal or vertical */
    private Orientation orientation = Orientation.FREE_HORIZONTAL;
    /** The icon which is shown on this title */
    private Icon icon;
    
    /**
     * Constructs a new title
     * @param dockable the Dockable which is the owner of this title
     * @param origin the version which was used to create this title
     */
    public AbstractDockTitle( Dockable dockable, DockTitleVersion origin ){
        this( dockable, origin, true );
    }
    
    /**
     * Standard constructor
     * @param dockable The Dockable whose title this will be
     * @param origin The version which was used to create this title
     * @param showMiniButtons <code>true</code> if the actions of the Dockable
     * should be shown, <code>false</code> if they should not be visible
     */
    public AbstractDockTitle( Dockable dockable, DockTitleVersion origin, boolean showMiniButtons ){
        this.dockable = dockable;
        this.showMiniButtons = showMiniButtons;
        this.origin = origin;
        
        setLayout( null );
        add( label );
        setActive( false );
        
        if( showMiniButtons ){
        	itemPanel = new ButtonPanel( true ){
        		@Override
        		protected BasicTitleViewItem<JComponent> createItemFor( DockAction action, Dockable dockable ){
        			return AbstractDockTitle.this.createItemFor( action, dockable );
        		}
        	};
            itemPanel.setOpaque( false );
            add( itemPanel );
        }
        
        setOpaque( false );
    }
    
    @Override
    public void paintComponent( Graphics g ) {
        paintBackground( g, this );
        
        if( icon != null ){
            Insets insets = titleInsets();
            if( orientation.isVertical() ){
                int width = getWidth() - insets.left - insets.right;
                icon.paintIcon( this, g, insets.left + (width - icon.getIconWidth())/2, insets.top );
            }
            else{
                int height = getHeight() - insets.top - insets.bottom;
                icon.paintIcon( this, g, insets.left,
                        insets.top + (height - icon.getIconHeight()) / 2 );
            }
        }
    }

    /**
     * Gets the location and the size of the icon.
     * @return the bounds or <code>null</code> if no icon is registered
     */
    public Rectangle getIconBounds(){
        if( icon == null )
            return null;
        
        Insets insets = titleInsets();
        if( orientation.isVertical() ){
            int width = getWidth() - insets.left - insets.right;
            return new Rectangle( insets.left + (width - icon.getIconWidth())/2, insets.top, icon.getIconWidth(), icon.getIconHeight() );
        }
        else{
            int height = getHeight() - insets.top - insets.bottom;
            return new Rectangle( insets.left, insets.top + (height - icon.getIconHeight()) / 2, icon.getIconWidth(), icon.getIconHeight() );
        }
    }
    
    /**
     * Paints the whole background of this title. The default implementation
     * just fills the background with the background color of <code>component</code>.
     * @param g the graphics context used to paint
     * @param component the Component which represents this title
     */
    protected void paintBackground( Graphics g, JComponent component ){
        g.setColor( component.getBackground() );
        g.fillRect( 0, 0, component.getWidth(), component.getHeight() );
    }
    
    /**
     * Sets the icon of this title. The icon is shown on the top or the left
     * edge.
     * @param icon the icon, can be <code>null</code>
     */
    protected void setIcon( Icon icon ){
        this.icon = icon;
        revalidate();
        repaint();
    }
    
    /**
     * Gets the icon of this title.
     * @return the icon or <code>null</code>
     * @see #setIcon(Icon)
     */
    protected Icon getIcon(){
        return icon;
    }
    
    /**
     * Sets the text of this title. The text either painted horizontally or
     * vertically.
     * @param text the text or null
     */
    protected void setText( String text ){
        label.setText( text );
        repaint();
    }
    
    /**
     * Gets the text which is shown on this title.
     * @return the text
     */
    protected String getText(){
        return label.getText();
    }
    
    public void setOrientation( Orientation orientation ) {
        this.orientation = orientation;
        if( showMiniButtons )
        	itemPanel.setOrientation( orientation );
        revalidate();
    }
    
    /**
     * Gets the current orientation.
     * @return the orientation
     * @see #setOrientation(bibliothek.gui.dock.title.DockTitle.Orientation)
     */
    public Orientation getOrientation() {
        return orientation;
    }
    
    public DockTitleVersion getOrigin() {
        return origin;
    }
    
    @Override
    public void setForeground( Color fg ) {
        super.setForeground( fg );
        if( label != null )
            label.setForeground( fg );
    }
    
    @Override
    public void setBackground( Color fg ) {
        super.setBackground( fg );
        
        if( label != null )
            label.setBackground( fg );
    }
    
    @Override
    public Dimension getMinimumSize() {
    	if( icon != null )
    		return new Dimension( icon.getIconWidth(), icon.getIconHeight() );
    	
    	Dimension preferred = getPreferredSize();
    	int min = Math.min( preferred.width, preferred.height );
    	return new Dimension( min, min );
    }
    
    /**
     * Gets the insets that have to be applied between the border and the 
     * content of this title.
     * @return the insets, not <code>null</code>
     */
    protected Insets getInnerInsets(){
        if( getOrientation().isHorizontal() )
            return DEFAULT_INSETS_HORIZONTAL;
        else
            return DEFAULT_INSETS_VERTICAL;
    }
    
    private Insets titleInsets(){
        Insets insets = getInsets();
        
        if( insets == null ){
            return getInnerInsets();
        }
        else{
            insets = new Insets( insets.top, insets.left, insets.bottom, insets.right );
        }
        
        Insets inner = getInnerInsets();
        insets.top += inner.top;
        insets.bottom += inner.bottom;
        insets.left += inner.left;
        insets.right += inner.right;
        
        return insets;
    }
    
    @Override
    public void doLayout(){
        super.doLayout();
        
        Insets insets = titleInsets();
        int x = insets.left;
        int y = insets.top;
        int width = getWidth() - insets.left - insets.right;
        int height = getHeight() - insets.top - insets.bottom;
        
        
        Dimension labelPreferred = label.getPreferredSize();
        
        if( orientation.isHorizontal() ){
            if( icon != null ){
                x += icon.getIconWidth();
                width -= icon.getIconWidth();
            }
            
            if( showMiniButtons && itemPanel.getItemCount() > 0 ){
            	Dimension[] buttonPreferred = itemPanel.getPreferredSizes();
            	
            	int remaining = width - labelPreferred.width;
            	int count = buttonPreferred.length-1;
            	
            	while( count > 0 && buttonPreferred[count].width > remaining )
            		count--;
            	
            	itemPanel.setVisibleActions( count );
            	
            	int buttonWidth = buttonPreferred[count].width;
            	int buttonX = width - buttonWidth;
            	
                label.setBounds( x, y, buttonX, height );
                itemPanel.setBounds( x + buttonX, y, width - buttonX, height );
            }
            else
                label.setBounds( x, y, width, height );
        }
        else{
            if( icon != null ){
                y += icon.getIconWidth();
                height -= icon.getIconWidth();
            }
            
            if( showMiniButtons && itemPanel.getItemCount() > 0 ){
            	Dimension[] buttonPreferred = itemPanel.getPreferredSizes();
            	
            	int remaining = height - labelPreferred.height;
            	int count = buttonPreferred.length-1;
            	
            	while( count > 0 && buttonPreferred[count].height > remaining )
            		count--;
            	
            	itemPanel.setVisibleActions( count );
            	
            	int buttonHeight = buttonPreferred[count].height;
            	int buttonY = height - buttonHeight;
            	
                label.setBounds( x, y, width, buttonY );
                itemPanel.setBounds( x, y + buttonY, width, height - buttonY );
            }
            else
                label.setBounds( x, y, width, height );
        }
    }
    
    public Component getComponent() {
        return this;
    }

    public void addMouseInputListener( MouseInputListener listener ) {
        addMouseListener( listener );
        addMouseMotionListener( listener );
        label.addMouseListener( listener );
        label.addMouseMotionListener( listener );
    }

    public void removeMouseInputListener( MouseInputListener listener ) {
        removeMouseListener( listener );
        removeMouseMotionListener( listener );
        label.removeMouseListener( listener );
        label.removeMouseMotionListener( listener );
    }

    public Point getPopupLocation( Point click ){
        boolean restrained = getText() == null || getText().length() == 0;
        
        Rectangle icon = getIconBounds();
        if( icon != null ){
            if( icon.contains( click )){
            	if( restrained ){
            		// icon must not be the whole title
            		int size = getWidth() * getHeight();
            		if( itemPanel != null && showMiniButtons )
            			size -= itemPanel.getWidth() * itemPanel.getHeight();
            		
            		if( size <= 2 * icon.width * icon.height )
            			return null;
            	}
            	
                if( getOrientation().isHorizontal() )
                    return new Point( icon.x, icon.y + icon.height );
                else
                    return new Point( icon.x + icon.width, icon.y );
            }
        }
        
        return null;
    }
    
    public Dockable getDockable() {
        return dockable;
    }

    /**
     * Sets whether this title should be painted as focused or not.
     * @param active <code>true</code> if the {@link Dockable} of this title
     * has the focus.
     */
    public void setActive( boolean active ) {
        this.active = active;
    }
    
    public void changed( DockTitleEvent event ) {
        setActive( event.isActive() );
    }
    
    public boolean isActive(){
        return active;
    }
    
    @Override
    public Dimension getPreferredSize() {
        Dimension preferred = label.getPreferredSize();
        
        Insets insets = titleInsets();

        if( orientation.isHorizontal() ){
            int width = 0;
            int height = 0;
            if( icon != null ){
                width = icon.getIconWidth();
                height = icon.getIconHeight();
            }
            
            height = Math.max( height, preferred.height );
            width += preferred.width;
            
            if( itemPanel != null ){
            	Dimension items = itemPanel.getPreferredSize();
            	height = Math.max( height, items.height );
            	width += items.width;
            }
            
            if( icon == null )
                width = Math.max( width, 2*height );
            
            return new Dimension( width + insets.left + insets.right,
                    height + insets.top + insets.bottom );
        }
        else{
            int width = 0;
            int height = 0;
            if( icon != null ){
                width = icon.getIconWidth();
                height = icon.getIconHeight();
            }
            
            
            width = Math.max( width, preferred.width );
            height += preferred.height;
            
            if( itemPanel != null ){
            	Dimension items = itemPanel.getPreferredSize();
            	width = Math.max( width, items.width );
            	height += items.height;
            }
            
            if( icon == null )
                height = Math.max( height, 2*width );
            
            return new Dimension( width + insets.left + insets.right,
                    height + insets.top + insets.bottom );
        }            
    }

    /**
     * Creates a new item for <code>action</code> which will be shown on this title.
     * @param action The action which will be triggered by the button
     * @param dockable The {@link Dockable} which will be affected by the action
     * @return the new graphical representation of the action 
     */
    protected BasicTitleViewItem<JComponent> createItemFor( DockAction action, Dockable dockable ){
    	return dockable.getController().getActionViewConverter().createView( 
    			action, ViewTarget.TITLE, dockable );
    }
    
    /**
     * Gets a list of all actions which will be shown on this title.
     * @param dockable the owner of the actions
     * @return the list of actions
     */
    protected DockActionSource getActionSourceFor( Dockable dockable ){
        return dockable.getGlobalActionOffers();
    }
    
    public void bind() {        
        if( bind )
            throw new IllegalArgumentException( "Do not call bind twice!" );
        bind = true;
        
        if( showMiniButtons )
        	itemPanel.set( dockable, getActionSourceFor( dockable ) );
        
        dockable.addDockableListener( listener );
        
        setText( dockable.getTitleText() );
        setIcon( dockable.getTitleIcon() );
        
        revalidate();
    }

    public void unbind() {
        if( !bind )
            throw new IllegalArgumentException( "Do not call unbind twice" );
        bind = false;
        
        dockable.removeDockableListener( listener );
        
        if( showMiniButtons )
        	itemPanel.set( null );
        
        setText( "" );
        setIcon( null );
    }
    
    /**
     * Tells whether this title is bound to a {@link Dockable} or not.
     * @return true if the title is {@link #bind() bound}, <code>false</code>
     * {@link #unbind() otherwise}
     */
    public boolean isBound(){
        return bind;
    }
    
    /**
     * A label which draws some text, and can change the layout of the text 
     * between horizontal and vertical.
     * @author Benjamin Sigg
     */
    private class OrientedLabel extends JPanel{
        /** The label which really paints the text */
        private JLabel label = new JLabel();
        
        /** The text on the label */
        private String text;
        
        /**
         * Creates a new label with no text
         */
        public OrientedLabel(){
            setOpaque( false );
            label.setOpaque( false );
        }
        
        /**
         * Sets the text of this label
         * @param text the text, <code>null</code> is allowed
         */
        public void setText( String text ){
            this.text = text;
            label.setText( text == null ? null : "  " + text );
            revalidate();
            repaint();
        }
        
        /**
         * Gets the text of this label
         * @return the text, may be <code>null</code>
         */
        public String getText(){
            return text;
        }
        
        @Override
        public void setForeground( Color fg ) {
            super.setForeground(fg);
            if( label != null )
                label.setForeground( fg );
        }
        
        @Override
        public void setBackground( Color bg ) {
            super.setBackground(bg);
            if( label != null )
                label.setBackground( bg );
        }
        
        @Override
        public void updateUI() {
            super.updateUI();
            if( label != null )
                label.updateUI();
        }
        
        @Override
        public Dimension getMinimumSize() {
            return getPreferredSize();
        }
        
        @Override
        public Dimension getPreferredSize() {
            Dimension size = label.getPreferredSize();
            if( orientation.isHorizontal() )
                return new Dimension( size.width, size.height );
            else
                return new Dimension( size.height, size.width );
        }
        
        @Override
        public void paint( Graphics g ) {
            if( orientation.isHorizontal() )
                label.paint( g );
            else{
                Graphics2D g2 = (Graphics2D)g.create();
                g2.rotate( Math.PI/2, 0, 0 );
                g2.translate( 0, -getWidth() );
                label.paint( g2 );
            }
        }
        
        @Override
        public void update( Graphics g ) {
            // do nothing
        }
        
        @Override
        public void setBounds( int x, int y, int w, int h ) {
            super.setBounds(x, y, w, h);
            
            if( orientation.isHorizontal() )
                label.setBounds( 0, 0, w, h );
            else
                label.setBounds( 0, 0, h, w );
        }
    }
    
    /**
     * A listener to the {@link Dockable} of this title.
     * @author Benjamin Sigg
     */
    private class Listener implements DockableListener{
        public void titleIconChanged( Dockable dockable, Icon oldIcon, Icon newIcon ) {
            setIcon( newIcon );
        }
        public void titleTextChanged( Dockable dockable, String oldTitle, String newTitle ) {
            setText( newTitle );
        }
        
        public void titleUnbound( Dockable dockable, DockTitle title ) {
            // do nothing
        }
        
        public void titleBound( Dockable dockable, DockTitle title ) {
            // do nothing
        }
        
    }
}
