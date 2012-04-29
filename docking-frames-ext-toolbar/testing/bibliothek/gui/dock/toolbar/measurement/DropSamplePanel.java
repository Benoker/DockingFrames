/*
 * Bibliothek - DockingFrames
 * Library built on Java/Swing, allows the user to "drag and drop"
 * panels containing any Swing-Component the developer likes to add.
 * 
 * Copyright (C) 2012 Herve Guillaume, Benjamin Sigg
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
 * Herve Guillaume
 * rvguillaume@hotmail.com
 * FR - France
 *
 * Benjamin Sigg
 * benjamin_sigg@gmx.ch
 * CH - Switzerland
 */

package bibliothek.gui.dock.toolbar.measurement;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Composite;
import java.awt.EventQueue;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;

import javax.swing.BorderFactory;
import javax.swing.SwingUtilities;
import javax.swing.Timer;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;

import bibliothek.gui.dock.station.OverpaintablePanel;
import bibliothek.gui.dock.station.layer.DockStationDropLayer;

public class DropSamplePanel extends OverpaintablePanel{
	private BufferedImage overlay;
	private DropSample sample;
	private boolean useSampleColors = true;
	
	public DropSamplePanel(){
		getContentPane().setLayout( new GridLayout( 1, 1 ) );
		getContentPane().setBorder( BorderFactory.createCompoundBorder( new EmptyBorder( 50, 50, 50, 50 ), new LineBorder( Color.BLACK, 1 ) ));
	}
	
	public void setSample( DropSample sample ){
		this.sample = sample;
		getContentPane().removeAll();
		getContentPane().add( sample.getComponent() );
		resetOverlay();
	}
	
	public void setUseSampleColors( boolean useSampleColors ){
		this.useSampleColors = useSampleColors;
		resetOverlay();
	}
	
	public boolean isUseSampleColors(){
		return useSampleColors;
	}
	
	private void resetOverlay(){
		if( isVisible() && getWidth() > 0 && getHeight() > 0 ){
			overlay = new BufferedImage( getWidth(), getHeight(), BufferedImage.TYPE_INT_ARGB );
			fill( overlay, 0 );
		}
	}
	
	private void refill( final BufferedImage image ){
		Timer timer = new Timer( 1000, new ActionListener(){
			@Override
			public void actionPerformed( ActionEvent e ){
				EventQueue.invokeLater( new Runnable(){
					@Override
					public void run(){
						if( image == overlay ){
							fill( overlay, 0 );
						}
					}
				} );	
			}
		} );
		timer.setRepeats( false );
		timer.start();
	}
	
	private void fill( final BufferedImage image, final int y ){
		EventQueue.invokeLater( new Runnable(){
			@Override
			public void run(){
				if( image == overlay && sample != null ){
					paint( image, y );
					int next = y + 1;
					if( next < image.getHeight() ){
						fill( image, next );
					}
					else{
						refill( image );
					}
				}
			}
		} );
	}
	
	private void paint( BufferedImage image, int y ){
		int width = image.getWidth();
		Graphics g = image.createGraphics();
		((Graphics2D)g).setBackground( new Color( 0, 0, 0, 0 ) );
		
		for( int x = 0; x < width; x++ ){
			Point point = new Point( x, y );
			SwingUtilities.convertPointToScreen( point, getContentPane() );

			int layer = layerAt( point.x, point.y );
			if( layer >= 0 ){
				Color color;
				if( useSampleColors ){
					color = sample.dropAt( point.x, point.y );
				}
				else{
					color = layerColor( layer );
				}
				g.setColor( color );
				g.fillRect( x, y, 1, 1 );
			}
			else{
				g.clearRect( x, y, 1, 1 );
			}
		}
		
		g.dispose();
		repaint();
	}
	
	private int layerAt( int x, int y ){
		if( sample == null ){
			return -1;
		}
		int index = 0;
		for( DockStationDropLayer layer : sample.getStation().getLayers() ){
			if( layer.contains( x, y )){
				return index;
			}
			index++;
		}
		return -1;
	}
	
	private Color layerColor( int layer ){
		switch( layer ){
			case 0: return Color.RED;
			case 1: return Color.GREEN;
			case 2: return Color.BLUE;
			case 3: return Color.YELLOW;
			case 4: return Color.CYAN;
			case 5: return Color.MAGENTA;
			default: throw new IllegalArgumentException( "did not except a station with that many layers, need to define more colors" );
		}
	}
	
	@Override
	protected void paintOverlay( Graphics g ){
		if( overlay == null || overlay.getWidth() != getWidth() || overlay.getHeight() != getHeight() ){
			resetOverlay();
		}
		if( overlay != null ){
			Graphics2D g2 = (Graphics2D)g;
	        
	        Composite old = g2.getComposite();
	        g2.setComposite( AlphaComposite.getInstance( AlphaComposite.SRC_OVER, 0.33f ));

			g.drawImage( overlay, 0, 0, this );
			
			g2.setComposite( old );
		}
	}
}
