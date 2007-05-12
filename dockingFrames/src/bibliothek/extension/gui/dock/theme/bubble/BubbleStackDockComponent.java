package bibliothek.extension.gui.dock.theme.bubble;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.geom.RoundRectangle2D;

import javax.swing.Icon;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import bibliothek.extension.gui.dock.theme.BubbleTheme;
import bibliothek.gui.dock.station.stack.CombinedStackDockComponent;
import bibliothek.gui.dock.station.stack.CombinedTab;

public class BubbleStackDockComponent extends CombinedStackDockComponent<BubbleStackDockComponent.Tab> {
	private BubbleTheme theme;
	private int arc = 6;
	private int borderSize = 3;
	private Insets insets = new Insets( borderSize, borderSize, 0, borderSize );
	
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

	private class Tab extends JPanel implements CombinedTab, ChangeListener, Runnable{
		private int index = 0;
		private JLabel label = new JLabel();
		private BubbleColorAnimation animation;
        private boolean mouse = false;
        
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
			
			// Border
			Graphics2D g2 = (Graphics2D)g.create();
			g2.setColor( border );
			g2.setClip( new RoundRectangle2D.Double( 0, -arc, w, h+arc, 2*arc, 2*arc ));
			g2.fillRect( 0, 0, w, h );
			
			g2.setClip( new RoundRectangle2D.Double( borderSize, -arc, w-2*borderSize, h+arc-borderSize, 2*arc, 2*arc ));
			g2.setPaint( new GradientPaint( 0, 0, top, 0, h-borderSize, bottom ) );
			g2.fillRect( 0, 0, w, h );
			
			Graphics child = g.create( label.getX(), label.getY(), label.getWidth(), label.getHeight() );
			label.paint( child );
			child.dispose();
			
			g2.setClip( 0, 0, w, h );
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
        
        private void checkAnimation(){
            String source, destination;
            
            if( getSelectedIndex() == index ){
                if( mouse ){
                    source = "active";
                    destination = "active.mouse";
                }
                else{
                    source = "active.mouse";
                    destination = "active";
                }
            }
            else{
                if( mouse ){
                    source = "inactive";
                    destination = "inactive.mouse";
                }
                else{
                    source = "inactive.mouse";
                    destination = "inactive";
                }
            }
            
            animation.putColors( "top", "tab.top." + source, "tab.top." + destination );
            animation.putColors( "bottom", "tab.bottom." + source, "tab.bottom." + destination );
            animation.putColors( "border", "tab.border." + source, "tab.border." + destination );
            animation.putColors( "text", "tab.text." + source, "tab.text." + destination );
        }
		
		public void setIcon( Icon icon ){
			label.setIcon( icon );
		}
		
		public void setText( String text ){
			label.setText( text );
		}
	}
}
