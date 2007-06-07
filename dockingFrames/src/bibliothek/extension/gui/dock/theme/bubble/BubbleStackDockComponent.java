/**
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
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.Icon;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import bibliothek.extension.gui.dock.theme.BubbleTheme;
import bibliothek.gui.dock.station.stack.CombinedStackDockComponent;
import bibliothek.gui.dock.station.stack.CombinedTab;

/**
 * A {@link bibliothek.gui.dock.station.stack.StackDockComponent StackDockComponent} 
 * used by a {@link BubbleTheme}. This component can animate its tabs.
 * @author Benjamin Sigg
 */
public class BubbleStackDockComponent extends CombinedStackDockComponent<BubbleStackDockComponent.Tab> {
	/** the theme which uses this component */
	private BubbleTheme theme;
	/** the size of the arc of the round tabs */
	private int arc = 6;
	/** the size of the border of the tabs */
	private int borderSize = 3;
	/** the free space around text and icon of the tabs */
	private Insets insets = new Insets( borderSize, borderSize, 0, borderSize );
	
	/**
	 * Creates a new component.
	 * @param theme the theme which uses this component
	 */
	public BubbleStackDockComponent( BubbleTheme theme ){
		if( theme == null )
			throw new IllegalArgumentException( "Theme must not be null" );
		
		this.theme = theme;
	}
	
	@Override
	protected Tab createTab(){
		Tab tab = new Tab();
		addChangeListener( tab );
		return tab;
	}

	@Override
	protected void destroy( Tab tab ){
		removeChangeListener( tab );
        tab.animation.stop();
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
        
        /**
         * Creates a new tab
         */
		public Tab(){
            animation = new BubbleColorAnimation( theme );
            animation.addTask( this );
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
                        animation.putColor( "top", "tab.top.active.mouse" );
                        animation.putColor( "bottom", "tab.bottom.active.mouse" );
                        animation.putColor( "border", "tab.border.active.mouse" );
                        animation.putColor( "text", "tab.text.active.mouse" );
                    }
                    else{
                        animation.putColor( "top", "tab.top.inactive.mouse" );
                        animation.putColor( "bottom", "tab.bottom.inactive.mouse" );
                        animation.putColor( "border", "tab.border.inactive.mouse" );
                        animation.putColor( "text", "tab.text.inactive.mouse" );
                    }
                }
                
                @Override
                public void mouseExited( MouseEvent e ) {
                    mouse = false;
                    if( getSelectedIndex() == index ){
                        animation.putColor( "top", "tab.top.active" );
                        animation.putColor( "bottom", "tab.bottom.active" );
                        animation.putColor( "border", "tab.border.active" );
                        animation.putColor( "text", "tab.text.active" );
                    }
                    else{
                        animation.putColor( "top", "tab.top.inactive" );
                        animation.putColor( "bottom", "tab.bottom.inactive" );
                        animation.putColor( "border", "tab.border.inactive" );
                        animation.putColor( "text", "tab.text.inactive" );
                    }
                }
			};
			
			addMouseListener( listener );
			label.addMouseListener( listener );
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
            // String source, destination;
            String destination;
            
            if( getSelectedIndex() == index ){
                if( mouse ){
                 //   source = "active";
                    destination = "active.mouse";
                }
                else{
                 //   source = "active.mouse";
                    destination = "active";
                }
            }
            else{
                if( mouse ){
                //    source = "inactive";
                    destination = "inactive.mouse";
                }
                else{
                //    source = "inactive.mouse";
                    destination = "inactive";
                }
            }
            
            /*
            animation.putColors( "top", "tab.top." + source, "tab.top." + destination );
            animation.putColors( "bottom", "tab.bottom." + source, "tab.bottom." + destination );
            animation.putColors( "border", "tab.border." + source, "tab.border." + destination );
            animation.putColors( "text", "tab.text." + source, "tab.text." + destination );
            */
            
            animation.putColor( "top", "tab.top." + destination );
            animation.putColor( "bottom", "tab.bottom." + destination );
            animation.putColor( "border", "tab.border." + destination );
            animation.putColor( "text", "tab.text." + destination );
        }
		
		public void setIcon( Icon icon ){
			label.setIcon( icon );
		}
		
		public void setText( String text ){
			label.setText( text );
		}
	}
}
