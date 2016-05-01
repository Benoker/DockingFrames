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
package bibliothek.gui.dock.station;

import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Insets;

import javax.swing.JComponent;
import javax.swing.JLayeredPane;
import javax.swing.JPanel;

import bibliothek.gui.dock.util.BackgroundComponent;
import bibliothek.util.Workarounds;

/**
 * A panel which contains two children, the "base pane" and the "overlay pane".<br>
 * The "overlay pane" is painted above the "base pane" and all its children. It
 * can be used to paint arbitrary figures. Clients can change the painting
 * code by overriding {@link #paintOverlay(Graphics)}.<br>
 * Clients will add new {@link Component}s to the "content pane". Normally the
 * "content pane" is the same as the "base pane", but it is possible to use two
 * different {@link JComponent}s for them. The client that replaces a 
 * "content pane" has to add the new panel to the "base pane". It is possible
 * to put some {@link Container}s between "base pane" and "content pane".
 * @author Benjamin Sigg
 *
 */
public class OverpaintablePanel extends JLayeredPane {
    /** the panel over all other children */
    private Overlay overlay = new Overlay();
    
    /** the panel on which children should be added */
    private JComponent content = new JPanel();
    
    /** the panel which is added to this {@link JLayeredPane} */
    private JComponent base;
    
    /** whether the background should be painted or not */
    private boolean solid = true;
    
    /**
     * Creates a new panel
     */
    public OverpaintablePanel(){
    	base = content;
    	content.setOpaque( false );
    	
        setLayer( base, DEFAULT_LAYER );
        setLayer( overlay, DRAG_LAYER );
        
        add( base );
        add( overlay );
    }
    
    /**
     * Tells this panel whether the background should be painted or not. If solid is <code>true</code>,
     * then the background is painted.<br>
     * This method should not be called by clients directly, this method is intended to be called by 
     * {@link BackgroundComponent}s only.
     * @param solid whether to paint the background or not
     */
    public void setSolid( boolean solid ){
    	this.solid = solid;
    	setOpaque( solid );
    	
    	if( base != content ){
    		base.setOpaque( solid );
    	}
    }
    
    /**
     * Tells whether the background of this panel should be painted or not.
     * @return <code>true</code> if the background is painted, <code>false</code> if this component
     * is transparent
     */
    public boolean isSolid(){
    	return solid;
    }
    
    /**
     * Sets the panel on which clients should add their children. Note that
     * <code>content</code> is not added to the base-panel, that must be done
     * by the client.
     * @param content the contents of this panel
     */
    public void setContentPane( JComponent content ){
        if( content == null )
            throw new IllegalArgumentException( "Content must not be null" );
        
        this.content = content;
        setSolid( isSolid() );
    }
    
    /**
     * Gets the layer on which new components should be inserted.
     * @return the layer
     */
    public JComponent getContentPane(){
        return content;
    }
    
    /**
     * Sets the panel which is added to <code>this</code>, and which is an
     * ancestor of the content-pane. The content-pane is replaced by
     * <code>base</code> when this method is called.
     * @param base the new base
     */
    public void setBasePane( JComponent base ){
    	if( base == null )
    		throw new IllegalArgumentException( "Base must not be null" );
    	
    	content = base;
    	
    	remove( this.base );
		this.base = base;
		
		setLayer( base, DEFAULT_LAYER );
		add( base );
		
		setSolid( isSolid() );
	}
    
    /**
     * The basic panel, directly added to <code>this</code>.
     * @return the basic panel, an ancestor of the content-pane.
     */
    public JComponent getBasePane(){
		return base;
	}
    
    /**
     * Paints the overlay over all components.
     * @param g the graphics to use
     */
    protected void paintOverlay( Graphics g ){
        // do nothing
    }
    
    @Override
    public Dimension getMinimumSize(){
    	if( isMinimumSizeSet() ){
    		return super.getMinimumSize();
    	}
    	Dimension sizeBase = null;
    	if( base != null ){
    		sizeBase = base.getMinimumSize();
    	}
    	Dimension sizeOverlay = null;
    	if( overlay != null ){
    		sizeOverlay = overlay.getMinimumSize();
    	}
    	
    	if( sizeBase == null && sizeOverlay == null ){
    		return super.getMinimumSize();
    	}
    	else{
    		return max( sizeBase, sizeOverlay );
    	}
    }
    
    @Override
    public Dimension getPreferredSize(){
    	if( isPreferredSizeSet() ){
    		return super.getPreferredSize();
    	}
    	Dimension sizeBase = null;
    	if( base != null ){
    		sizeBase = base.getPreferredSize();
    	}
    	Dimension sizeOverlay = null;
    	if( overlay != null ){
    		sizeOverlay = overlay.getPreferredSize();
    	}
    	
    	if( sizeBase == null && sizeOverlay == null ){
    		return super.getPreferredSize();
    	}
    	else{
    		return max( sizeBase, sizeOverlay );
    	}
    }
    
    @Override
    public Dimension getMaximumSize(){
    	if( isMaximumSizeSet() ){
    		return super.getMaximumSize();
    	}
    	Dimension sizeBase = null;
    	if( base != null ){
    		sizeBase = base.getMaximumSize();
    	}
    	Dimension sizeOverlay = null;
    	if( overlay != null ){
    		sizeOverlay = overlay.getMaximumSize();
    	}
    	
    	if( sizeBase == null && sizeOverlay == null ){
    		return super.getMaximumSize();
    	}
    	else{
    		return min( sizeBase, sizeOverlay );
    	}
    }
    
    private Dimension min( Dimension a, Dimension b ){
    	if( a == null ){
    		return b;
    	}
    	if( b == null ){
    		return a;
    	}
    	return new Dimension( Math.min( a.width, b.width ), Math.min( a.height, b.height ));
    }
    
    private Dimension max( Dimension a, Dimension b ){
    	if( a == null ){
    		return b;
    	}
    	if( b == null ){
    		return a;
    	}
    	return new Dimension( Math.max( a.width, b.width ), Math.max( a.height, b.height ));
    }
    
    @Override
    public void doLayout() {
        Insets insets = getInsets();
        int x = 0;
        int y = 0;
        int width = getWidth();
        int height = getHeight();
        
        if( insets != null ){
            x = insets.left;
            y = insets.top;
            width -= insets.left + insets.right;
            height -= insets.top + insets.bottom;
        }
        
        base.setBounds( x, y, width, height );
        overlay.setBounds( x, y, width, height );
    }
        
    private class Overlay extends JPanel{
        public Overlay(){
        	setLayout( null );
            setOpaque( false );
            Workarounds.getDefault().markAsGlassPane( this );
        }
        
        @Override
        public boolean contains( int x, int y ) {
            return false;
        }
        
        @Override
        protected void paintComponent( Graphics g ) {
            paintOverlay( g );
        }
        
        @Override
        public Dimension getMinimumSize(){
        	return new Dimension( 0, 0 );
        }
        
        @Override
        public Dimension getPreferredSize(){
	        return new Dimension( 0, 0 );
        }
    }
}
