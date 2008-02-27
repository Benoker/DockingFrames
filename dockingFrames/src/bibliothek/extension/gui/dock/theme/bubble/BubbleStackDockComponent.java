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

import java.awt.*;
import java.awt.event.*;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import bibliothek.extension.gui.dock.theme.BubbleTheme;
import bibliothek.gui.DockController;
import bibliothek.gui.Dockable;
import bibliothek.gui.dock.StackDockStation;
import bibliothek.gui.dock.control.RemoteRelocator;
import bibliothek.gui.dock.control.RemoteRelocator.Reaction;
import bibliothek.gui.dock.station.stack.CombinedStackDockComponent;
import bibliothek.gui.dock.station.stack.CombinedTab;
import bibliothek.gui.dock.themes.color.TabColor;
import bibliothek.gui.dock.util.color.ColorCodes;

/**
 * A {@link bibliothek.gui.dock.station.stack.StackDockComponent StackDockComponent} 
 * used by a {@link BubbleTheme}. This component can animate its tabs.
 * @author Benjamin Sigg
 */
@ColorCodes({ "stack.tab.top.active.mouse",
    "stack.tab.bottom.active.mouse",
    "stack.tab.border.active.mouse",
    "stack.tab.text.active.mouse",
    
    "stack.tab.top.inactive.mouse",
    "stack.tab.bottom.inactive.mouse",
    "stack.tab.border.inactive.mouse",
    "stack.tab.tex.inactive.mouse",
    
    "stack.tab.top.active",
    "stack.tab.bottom.active",
    "stack.tab.border.active",
    "stack.tab.text.active",
    
    "stack.tab.top.inactive",
    "stack.tab.bottom.inactive",
    "stack.tab.border.inactive",
    "stack.tab.text.inactive"})
public class BubbleStackDockComponent extends CombinedStackDockComponent<BubbleStackDockComponent.Tab> {
	/** the size of the arc of the round tabs */
	private int arc = 6;
	/** the size of the border of the tabs */
	private int borderSize = 3;
	/** the free space around text and icon of the tabs */
	private Insets insets = new Insets( borderSize, borderSize, 0, borderSize );
	/** the station for which this component is used */
	private StackDockStation station;
	
	/**
	 * Creates a new component.
	 * @param station the station on which this component is used
	 */
	public BubbleStackDockComponent( StackDockStation station ){
		this.station = station;
	}
	
	@Override
	protected Tab createTab( Dockable dockable ){
		Tab tab = new Tab( dockable );
		addChangeListener( tab );
		return tab;
	}

	@Override
	protected void destroy( Tab tab ){
		removeChangeListener( tab );
        tab.animation.stop();
	}
	
	@Override
	public void setController( DockController controller ){
		super.setController( controller );
		for( int i = 0, n = getTabCount(); i<n; i++ )
			getTab( i ).setController( controller );
	}

	/**
	 * Some color needed on a {@link Tab}.
	 * @author Benjamin Sigg
	 *
	 */
	protected class BubbleTabColor extends TabColor{
	    private Tab tab;
	    private int state;
	    private String animationId;
	    private BubbleColorAnimation animation;
	    
	    public BubbleTabColor( Tab tab, int state, String id, String animationId, BubbleColorAnimation animation, Dockable dockable, Color backup ){
	        super( id, TabColor.class, station, dockable, backup);
	        this.tab = tab;
	        this.state = state;
	        this.animationId = animationId;
	        this.animation = animation;
	    }
	    
	    /**
	     * Transmits the color of this {@link TabColor} if the state is
	     * correct.
	     */
	    public void transmit(){
	        if( tab.getState() == state ){
                animation.putColor( animationId, color() );
            }
	    }
	    
	    @Override
	    protected void changed( Color oldColor, Color newColor ) {
	        if( tab.getState() == state ){
	            animation.putColor( animationId, newColor );
	        }
	    }
	}
	
	/**
	 * A tab of the StackDockComponent
	 * @author Benjamin Sigg
	 */
	protected class Tab extends JPanel implements CombinedTab, ChangeListener, Runnable{
		/** the location of this tab */
		private int index = 0;
		/** a label showing text and icon for this tab */
		private JLabel label = new JLabel();
		/** an animation used when the mouse enters or leaves this tab */
		private BubbleColorAnimation animation;
		/** whether the mouse is inside this tab or not */
        private boolean mouse = false;
        /** the Dockable for which this tab is used */
        private Dockable dockable;
        
        /** the remote device to do drag & drop */
        private RemoteRelocator relocator;
        
        private BubbleTabColor topActiveMouse;
        private BubbleTabColor bottomActiveMouse;
        private BubbleTabColor borderActiveMouse;
        private BubbleTabColor textActiveMouse;
        
        private BubbleTabColor topInactiveMouse;
        private BubbleTabColor bottomInactiveMouse;
        private BubbleTabColor borderInactiveMouse;
        private BubbleTabColor textInactiveMouse;
     
        private BubbleTabColor topActive;
        private BubbleTabColor bottomActive;
        private BubbleTabColor borderActive;
        private BubbleTabColor textActive;
        
        private BubbleTabColor topInactive;
        private BubbleTabColor bottomInactive;
        private BubbleTabColor borderInactive;
        private BubbleTabColor textInactive;
        
        private BubbleTabColor[] colors;
        
        private static final int STATE_ACTIVE = 1;
        private static final int STATE_MOUSE = 2;
        
        private int state = 0;
        
        /**
         * Creates a new tab
         * @param dockable the element whose title will be shown on this tab
         */
		public Tab( Dockable dockable ){
			this.dockable = dockable;
			
            animation = new BubbleColorAnimation();
            animation.addTask( this );
            
            topActiveMouse = new BubbleTabColor( this, STATE_ACTIVE | STATE_MOUSE, "stack.tab.top.active.mouse", "top", animation, dockable, Color.RED.brighter() );
            bottomActiveMouse = new BubbleTabColor( this, STATE_ACTIVE | STATE_MOUSE, "stack.tab.bottom.active.mouse", "bottom", animation, dockable, Color.RED.darker() );
            borderActiveMouse = new BubbleTabColor( this, STATE_ACTIVE | STATE_MOUSE, "stack.tab.border.active.mouse", "border", animation, dockable, Color.RED.darker().darker() );
            textActiveMouse = new BubbleTabColor( this, STATE_ACTIVE | STATE_MOUSE, "stack.tab.text.active.mouse", "text", animation, dockable, Color.BLACK );
            
            topInactiveMouse = new BubbleTabColor( this, STATE_MOUSE, "stack.tab.top.inactive.mouse", "top", animation, dockable, Color.BLUE.brighter() );
            bottomInactiveMouse = new BubbleTabColor( this, STATE_MOUSE, "stack.tab.bottom.inactive.mouse", "bottom", animation, dockable, Color.BLUE.darker() );
            borderInactiveMouse = new BubbleTabColor( this, STATE_MOUSE, "stack.tab.border.inactive.mouse", "border", animation, dockable, Color.BLUE.darker().darker() );
            textInactiveMouse = new BubbleTabColor( this, STATE_MOUSE, "stack.tab.tex.inactive.mouse", "text", animation, dockable, Color.BLACK );
            
            topActive = new BubbleTabColor( this, STATE_ACTIVE, "stack.tab.top.active", "top", animation, dockable, Color.WHITE );
            bottomActive = new BubbleTabColor( this, STATE_ACTIVE, "stack.tab.bottom.active", "bottom", animation, dockable, Color.LIGHT_GRAY );
            borderActive = new BubbleTabColor( this, STATE_ACTIVE, "stack.tab.border.active", "border", animation, dockable, Color.DARK_GRAY );
            textActive = new BubbleTabColor( this, STATE_ACTIVE, "stack.tab.text.active", "text", animation, dockable, Color.BLACK );
            
            topInactive = new BubbleTabColor( this, 0, "stack.tab.top.inactive", "top", animation, dockable, Color.DARK_GRAY );
            bottomInactive = new BubbleTabColor( this, 0, "stack.tab.bottom.inactive", "bottom", animation, dockable, Color.BLACK );
            borderInactive = new BubbleTabColor( this, 0, "stack.tab.border.inactive", "border", animation, dockable, Color.LIGHT_GRAY );
            textInactive = new BubbleTabColor( this, 0, "stack.tab.text.inactive", "text", animation, dockable, Color.WHITE );
            
            colors = new BubbleTabColor[]{
                    topActiveMouse, bottomActiveMouse, borderActiveMouse, textActiveMouse,
                    topInactiveMouse, bottomInactiveMouse, borderInactiveMouse, textInactiveMouse,
                    topActive, bottomActive, borderActive, textActive,
                    topInactive, bottomInactive, borderInactive, textInactive };
            
            setController( getController() );
            checkAnimation();
            
			setOpaque( false );
			add( label );
			setLayout( null );
			
			MouseListener listener = new MouseAdapter(){
				@Override
				public void mouseClicked( MouseEvent e ){
					setSelectedIndex( index );
				}
                
                @Override
                public void mouseEntered( MouseEvent e ) {
                    mouse = true;
                    if( getSelectedIndex() == index ){
                        animation.putColor( "top", topActiveMouse.color() );
                        animation.putColor( "bottom", bottomActiveMouse.color() );
                        animation.putColor( "border", borderActiveMouse.color() );
                        animation.putColor( "text", textActiveMouse.color());
                    }
                    else{
                        animation.putColor( "top", topInactiveMouse.color() );
                        animation.putColor( "bottom", bottomInactiveMouse.color() );
                        animation.putColor( "border", borderInactiveMouse.color() );
                        animation.putColor( "text", textInactiveMouse.color());
                    }
                    state = state | STATE_MOUSE;
                }
                
                @Override
                public void mouseExited( MouseEvent e ) {
                    mouse = false;
                    if( getSelectedIndex() == index ){
                        animation.putColor( "top", topActive.color() );
                        animation.putColor( "bottom", bottomActive.color() );
                        animation.putColor( "border", borderActive.color() );
                        animation.putColor( "text", textActive.color());
                    }
                    else{
                        animation.putColor( "top", topInactive.color() );
                        animation.putColor( "bottom", bottomInactive.color() );
                        animation.putColor( "border", borderInactive.color() );
                        animation.putColor( "text", textInactive.color());
                    }
                    
                    state = state & ~STATE_MOUSE;
                }
                
                @Override
                public void mousePressed( MouseEvent e ){
                	if( !e.isConsumed() && relocator != null ){
                		Point mouse = e.getPoint();
                		SwingUtilities.convertPointToScreen( mouse, e.getComponent() );
                		Reaction reaction = relocator.init( mouse.x, mouse.y, 0, 0, e.getModifiersEx() );
                		switch( reaction ){
                			case BREAK_CONSUMED:
                			case CONTINUE_CONSUMED:
                				e.consume();
                				break;
                		}
                	}
                }
                
                @Override
                public void mouseReleased( MouseEvent e ){
                	if( !e.isConsumed() && relocator != null ){
                		Point mouse = e.getPoint();
                		SwingUtilities.convertPointToScreen( mouse, e.getComponent() );
                		Reaction reaction = relocator.drop( mouse.x, mouse.y, e.getModifiersEx() );
                		switch( reaction ){
                			case BREAK_CONSUMED:
                			case CONTINUE_CONSUMED:
                				e.consume();
                				break;
                		}
                	}
                }
			};
			
			MouseMotionListener motion = new MouseMotionAdapter(){
				@Override
				public void mouseDragged( MouseEvent e ){
                	if( !e.isConsumed() && relocator != null ){
                		Point mouse = e.getPoint();
                		SwingUtilities.convertPointToScreen( mouse, e.getComponent() );
                		Reaction reaction = relocator.drag( mouse.x, mouse.y, e.getModifiersEx() );
                		switch( reaction ){
                			case BREAK_CONSUMED:
                			case CONTINUE_CONSUMED:
                				e.consume();
                				break;
                		}
                	}
				}
			};
			
			addMouseListener( listener );
			addMouseMotionListener( motion );
			label.addMouseListener( listener );
			label.addMouseMotionListener( motion );
		}
		
		public int getState() {
            return state;
        }
		
		public void setController( DockController controller ){
			if( controller == null )
				relocator = null;
			else
				relocator = controller.getRelocator().createRemote( dockable );
			
			for( BubbleTabColor color : colors )
			    color.connect( controller );
			
			animation.kick();
		}
		
        public void run() {
            label.setForeground( animation.getColor( "text" ));
            repaint();
        }
        
		@Override
		public Dimension getPreferredSize(){
			Dimension size = label.getPreferredSize();
			return new Dimension( 
					size.width+2*borderSize+insets.left+insets.right,
					size.height+arc+insets.top+insets.bottom );
		}
		
		@Override
		public Dimension getMinimumSize(){
			return getPreferredSize();
		}
		
		@Override
		public void doLayout(){
			label.setBounds(
					borderSize+insets.left, 
					insets.top, 
					getWidth()-borderSize-insets.left-insets.right,
					getHeight()-arc-insets.top-insets.bottom );
		}
		
		@Override
		public void paintComponent( Graphics g ){
			Color bottom = animation.getColor( "bottom" );
            Color top = animation.getColor( "top" );
            Color border = animation.getColor( "border" );
			
			int w = getWidth();
			int h = getHeight();

            // Rectangle clip = g.getClipBounds();
            
            Graphics2D g2 = (Graphics2D)g.create();
            g2.setRenderingHint( RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON );
                        
            // draw border
			g2.setColor( border );
            g2.fillRoundRect( 0, -arc, w, h+arc, 2*arc, 2*arc );
            
            // draw background
            g2.setPaint( new GradientPaint( 0, 0, top, 0, h-borderSize, bottom ) );
            g2.fillRoundRect( borderSize, -arc, w-2*borderSize, h+arc-borderSize, 2*arc, 2*arc );
			
            // draw text and icon
			Graphics child = g.create( label.getX(), label.getY(), label.getWidth(), label.getHeight() );
			label.update( child );
			child.dispose();
			
            // draw horizon
			g2.setPaint( new GradientPaint( 0, 0, new Color( 150, 150, 150 ), 0, h/2, Color.WHITE ));
			g2.setComposite( AlphaComposite.getInstance( AlphaComposite.SRC_ATOP, 0.4f ) );
			g2.fillRect( 0, 0, w, h/2 );
            
			g2.dispose();
		}
		
		@Override
		protected void paintChildren( Graphics g ){
			// stop
		}
		
		public JComponent getComponent(){
			return this;
		}

		public void stateChanged( ChangeEvent e ){
            checkAnimation();
            label.setForeground( animation.getColor( "text" ));
		}
		
		public void setIndex( int index ){
			this.index = index;
            checkAnimation();
            label.setForeground( animation.getColor( "text" ));
		}
        
		/**
		 * Ensures that {@link #animation} uses the correct set of color pairs.
		 */
        private void checkAnimation(){
            if( getSelectedIndex() == index ){
                if( mouse ){
                    state = STATE_ACTIVE | STATE_MOUSE;
                }
                else{
                    state = STATE_ACTIVE;
                }
            }
            else{
                if( mouse ){
                    state = STATE_MOUSE;
                }
                else{
                    state = 0;
                    
                }
            }
            
            for( BubbleTabColor color : colors )
                color.transmit();
        }
		
		public void setIcon( Icon icon ){
			label.setIcon( icon );
		}
		
		public void setText( String text ){
			label.setText( text );
		}
	}
}
