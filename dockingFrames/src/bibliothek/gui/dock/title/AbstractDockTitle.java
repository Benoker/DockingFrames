/**
 * Bibliothek - DockingFrames
 * Library built on Java/Swing, allows the user to "drag and drop"
 * panels containing any Swing-Component the developer likes to add.
 * 
 * Copyright (C) 2007 Benjamin Sigg
 * 
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * Benjamin Sigg
 * benjamin_sigg@gmx.ch
 * 
 * Wunderklingerstr. 59
 * 8215 Hallau
 * CH - Switzerland
 */


package bibliothek.gui.dock.title;

import java.awt.*;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.*;
import javax.swing.event.MouseInputListener;

import bibliothek.gui.DockController;
import bibliothek.gui.dock.Dockable;
import bibliothek.gui.dock.action.ActionPopup;
import bibliothek.gui.dock.action.DockAction;
import bibliothek.gui.dock.action.DockActionSource;
import bibliothek.gui.dock.action.views.ViewItem;
import bibliothek.gui.dock.action.views.ViewTarget;
import bibliothek.gui.dock.action.views.buttons.TitleViewItem;
import bibliothek.gui.dock.event.DockActionSourceListener;
import bibliothek.gui.dock.event.DockTitleEvent;
import bibliothek.gui.dock.event.DockableListener;

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
 * background.
 * 
 * @author Benjamin Sigg
 *
 */
public class AbstractDockTitle extends JPanel implements DockTitle {
    /** The {@link Dockable} for which this title is shown */
    private Dockable dockable;
    
    /** A label for the title-text */
    private OrientedLabel label = new OrientedLabel();
    /** A panel that displays the action-buttons of this title */
    private JPanel itemPanel;
    
    /** The buttons of this title, each of them represents one action */
    private Map<DockAction, TitleViewItem<JComponent>> items = new HashMap<DockAction, TitleViewItem<JComponent>>();
    
    /** A list of all actions that are on this title */
    private List<DockAction> actions = new ArrayList<DockAction>();
    
    /** 
     * A listener added to the owned {@link Dockable}. The listener changes the
     * title-text and the icon of this title. 
     */
    private Listener listener = new Listener();
    /** A list of actions that should be shown on this title */
    private DockActionSource source;
    /** The creator of this title */
    private DockTitleVersion origin;
    
    /** <code>true</code> if this title is currently selected, <code>false</code> otherwise */
    private boolean active = false;
    /** <code>true</code> if this title is currently binded to a {@link Dockable} */
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
            //itemPanel = new JPanel();
        	itemPanel = new ItemPanel();
            itemPanel.setOpaque( false );
            add( itemPanel );
        }
        
        setOpaque( false );
        
        addMouseInputListener( new ActionPopup( true ){
            @Override
            protected Dockable getDockable() {
                return AbstractDockTitle.this.dockable;
            }

            @Override
            protected DockActionSource getSource() {
                return source;
            }
            
            @Override
            public void mouseClicked( MouseEvent e ) {
                if( getText() == null || getText().length() == 0 )
                    return;
                
                Point location = SwingUtilities.convertPoint(
                        e.getComponent(), e.getX(), e.getY(), AbstractDockTitle.this );
                
                Rectangle icon = getIconBounds();
                if( icon != null ){
                    if( icon.contains( location ))
                        popup( AbstractDockTitle.this, icon.x, icon.y + icon.height );
                    else
                        super.mouseClicked(e);
                }
            }

            @Override
            protected boolean isEnabled() {
                if( !isBinded() )
                    return false;
                
                DockController controller = AbstractDockTitle.this.dockable.getController();
                if( controller == null )
                    return false;
                
                return !controller.isOnMove();
            }
        });
    }
    
    @Override
    public void paintComponent( Graphics g ) {
        paintBackground( g, this );
        
        if( icon != null ){
            Insets insets = getInsets();
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
        
        Insets insets = getInsets();
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
        if( items != null )
        	for( TitleViewItem<JComponent> item : items.values() )
        		item.setOrientation( orientation );
        
        
        invalidate();
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
        
        if( items != null )
            for( ViewItem<JComponent> item : items.values() )
                item.getItem().setForeground( fg );
    }
    
    @Override
    public void setBackground( Color fg ) {
        super.setBackground( fg );
        
        if( label != null )
            label.setBackground( fg );
        
        if( items != null )
            for( ViewItem<JComponent> item : items.values() )
                item.getItem().setBackground( fg );
    }
    
    @Override
    public Dimension getMinimumSize() {
    	if( icon != null )
    		return new Dimension( icon.getIconWidth(), icon.getIconHeight() );
    	
    	Dimension preferred = getPreferredSize();
    	int min = Math.min( preferred.width, preferred.height );
    	return new Dimension( min, min );
    }
    
    @Override
    public void doLayout(){
        super.doLayout();
        
        Insets insets = getInsets();
        int x, y, width, height;
        if( insets == null ){
            x = 0;
            y = 0;
            width = getWidth();
            height = getHeight();
        }
        else{
            x = insets.left;
            y = insets.top;
            width = getWidth() - insets.left - insets.right;
            height = getHeight() - insets.top - insets.bottom;
        }
        
        Dimension labelPreferred = label.getPreferredSize();
        
        if( orientation.isHorizontal() ){
            if( icon != null ){
                x += icon.getIconWidth();
                width -= icon.getIconWidth();
            }
            
            if( items.size() > 0 && showMiniButtons ){
            	Dimension buttonPreferred = itemPanel.getPreferredSize();
            	
                int buttonWidth = Math.min( buttonPreferred.width, 
                        (int)(width * buttonPreferred.width / (double)(buttonPreferred.width + labelPreferred.width) ));
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
            
            if( items.size() > 0 && showMiniButtons ){
            	Dimension buttonPreferred = itemPanel.getPreferredSize();
                int buttonHeight = Math.min( buttonPreferred.height, 
                		(int)(height * buttonPreferred.height / (double)(buttonPreferred.height + labelPreferred.height) ));
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
        
        Insets insets = getInsets();

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
    protected TitleViewItem<JComponent> createItemFor( DockAction action, Dockable dockable ){
    	return dockable.getController().getActionViewConverter().createView( 
    			action, ViewTarget.TITLE, dockable );
    }
    
    public void bind() {        
        if( bind )
            throw new IllegalArgumentException( "Do not call bind twice!" );
        bind = true;
        
        source = dockable.getController().listOffers( dockable );
        dockable.addDockableListener( listener );
        source.addDockActionSourceListener( listener );
        
        int length = source.getDockActionCount();
        
        for( int i = 0; i<length; i++ ){
            DockAction action = source.getDockAction( i );
            action.bind( dockable );
            this.actions.add( action );
            if( showMiniButtons ){
                TitleViewItem<JComponent> item = createItemFor( action, dockable );
                if( item != null ){
	                item.bind();
	                item.setOrientation( getOrientation() );
	                itemPanel.add( item.getItem() );
	                items.put( action, item );
	                item.getItem().setForeground( getForeground() );
	                item.getItem().setBackground( getBackground() );
                }
            }
        }
        
        setText( dockable.getTitleText() );
        setIcon( dockable.getTitleIcon() );
        
        revalidate();
    }

    public void unbind() {
        if( !bind )
            throw new IllegalArgumentException( "Do not call unbind twice" );
        bind = false;
        dockable.removeDockableListener( listener );
        
        if( showMiniButtons ){
            for( ViewItem<JComponent> item : items.values() ){
                itemPanel.remove( item.getItem() );
                item.unbind();
            }
        }
        for( DockAction action : actions )
            action.unbind( dockable );
        
        setIcon( null );
        
        items.clear();
        actions.clear();
        source.removeDockActionSourceListener( listener );
        source = null;
    }
    
    /**
     * Tells whether this title is binded to a {@link Dockable} or not.
     * @return true if the title is {@link #bind() binded}, <code>false</code>
     * {@link #unbind() otherwise}
     */
    public boolean isBinded(){
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
     * A listener to the {@link Dockable} and the {@link DockActionSource}
     * of this title.
     * @author Benjamin Sigg
     *
     */
    private class Listener implements DockActionSourceListener, DockableListener{
        public void titleIconChanged( Dockable dockable, Icon oldIcon, Icon newIcon ) {
            setIcon( newIcon );
        }
        public void titleTextChanged( Dockable dockable, String oldTitle, String newTitle ) {
            setText( newTitle );
        }
        
        public void titleUnbinded( Dockable dockable, DockTitle title ) {
            // do nothing
        }
        
        public void titleBinded( Dockable dockable, DockTitle title ) {
            // do nothing
        }
        
        public void actionsAdded( DockActionSource source, int firstIndex, int lastIndex ) {
            for( int i = firstIndex; i <= lastIndex; i++ ){
                DockAction action = source.getDockAction( i );
                action.bind( dockable );
                if( showMiniButtons ){
                    TitleViewItem<JComponent> item = createItemFor( action, getDockable() );
                    if( item != null ){
	                    item.bind();
	                    item.setOrientation( getOrientation() );
	                    item.getItem().setForeground( getForeground() );
		                item.getItem().setBackground( getBackground() );
	                    items.put( action, item );
                    }
                }
                actions.add( i, action );
            }
            update();
        }
        public void actionsRemoved( DockActionSource source, int firstIndex, int lastIndex ) {
            for( int i = lastIndex; i >= firstIndex; i-- ){
            	DockAction action = actions.remove( i );
                
                if( showMiniButtons ){
                	TitleViewItem<JComponent> item = items.remove( action );
                    if( item != null )
                    	item.unbind();
                }
                
                action.unbind( dockable );
            }
            
            update();
        }
        
        /**
         * Ensures that all mini buttons are visible, and the layout
         * is up to date.
         */
        private void update(){
            if( showMiniButtons ){
                itemPanel.removeAll();
                
                for( DockAction action : actions ){
                	TitleViewItem<JComponent> item = items.get( action );
                	if( item != null ){
                		itemPanel.add( item.getItem() );
                	}
                }
                            
                revalidate();
            }
        }
    }
    
    /**
     * Panel that shows the items.
     * @author Benjamin Sigg
     */
    private class ItemPanel extends JPanel{
    	/**
    	 * Creates a new panel
    	 */
    	public ItemPanel(){
    		setLayout( null );
    	}
    	
    	@Override
    	public Dimension getMinimumSize(){
    		return getPreferredSize();
    	}
    	
    	@Override
    	public Dimension getPreferredSize(){
    		int width = 0;
    		int height = 0;
    		
    		if( orientation.isHorizontal() ){
    			for( int i = 0, n = getComponentCount(); i<n; i++ ){
    				Dimension preferred = getComponent( i ).getPreferredSize();
    				width += preferred.width;
    				height = Math.max( height, preferred.height );
    			}
    		}
    		else{
    			for( int i = 0, n = getComponentCount(); i<n; i++ ){
    				Dimension preferred = getComponent( i ).getPreferredSize();
    				width = Math.max( width, preferred.width );
    				height += preferred.height;
    			}
    		}
    		
    		return new Dimension( width, height );
    	}
    	
    	@Override
    	public void doLayout(){
    		Dimension current = getPreferredSize();
    		
    		if( orientation.isHorizontal() ){
    			if( current.width <= 0 )
    				return;
    			
    			int x = 0;
    			int height = getHeight();
    			int width = getWidth();
    			
    			if( width > current.width ){
    				x += width - current.width;
    				width = current.width;
    			}
    			
    			for( int i = 0, n = getComponentCount(); i<n; i++ ){
    				Component c = getComponent( i );
    				Dimension preferred = c.getPreferredSize();
    				if( current.width <= 0 ){
    					if( width <= 0 )
    						width = 1;
    					
    					current.width = width;
    				}
    				
    				if( width == current.width ){
    					c.setBounds( x, 0, preferred.width, height );
    				}
    				else{
    					double factor = width / (double)current.width;
    					c.setBounds( x, 0, (int)(factor * preferred.width), height );
    				}
    				
    				current.width -= preferred.width;
    				width -= c.getWidth();
    				x += c.getWidth();
    				
    			}
    		}
    		else{
    			if( current.width <= 0 )
    				return;
    			
    			int y = 0;
    			int height = getHeight();
    			int width = getWidth();
    			
    			if( height > current.height ){
    				y += height - current.height;
    				height = current.height;
    			}
    			
    			for( int i = 0, n = getComponentCount(); i<n; i++ ){
    				Component c = getComponent( i );
    				Dimension preferred = c.getPreferredSize();
    				if( current.height <= 0 ){
    					if( height <= 0 )
    						height = 1;
    					current.height = height;
    				}
    				
    				if( height == current.height ){
    					c.setBounds( 0, y, width, preferred.height );
    				}
    				else{
    					double factor = height / (double)current.height;
    					c.setBounds( 0, y, width, (int)(factor * preferred.height) );
    				}
    				
    				current.height -= preferred.height;
    				height -= c.getHeight();
    				y += c.getHeight();
    			}
    		}
    	}
    }
}
