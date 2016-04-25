/*
 * Bibliothek - DockingFrames
 * Library built on Java/Swing, allows the user to "drag and drop"
 * panels containing any Swing-Component the developer likes to add.
 * 
 * Copyright (C) 2010 Benjamin Sigg
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
package bibliothek.gui.dock.util;

import java.awt.Component;
import java.awt.Graphics;
import java.awt.LayoutManager;

import javax.swing.JPanel;


/**
 * This {@link JPanel} implements {@link PaintableComponent} and 
 * can use a {@link BackgroundAlgorithm} to paint its background.
 * @author Benjamin Sigg
 */
public abstract class BackgroundPanel extends JPanel implements PaintableComponent{
	private BackgroundAlgorithm background;
	
	/** whether this panel is transparent, paints some or all pixels */
	private Transparency transparency;
	
	/** added to the {@link BackgroundAlgorithm} that paints this component */
	private BackgroundAlgorithmListener listener = new BackgroundAlgorithmListener(){
		public void transparencyChanged( BackgroundAlgorithm algorithm, Transparency transparency ){
			configure( transparency );
		}
	};
	
	/**
	 * Creates a new panel.
	 * @param transparency how many pixels are painted
	 */
	public BackgroundPanel( Transparency transparency ){
		setTransparency( transparency );
	}
	
	/**
	 * Creates a new panel setting a default {@link LayoutManager}.
	 * @param layout the layout manager, can be <code>null</code>
	 * @param transparency how many pixels are painted
	 */
	public BackgroundPanel( LayoutManager layout, Transparency transparency ){
		super( layout );
		setTransparency( transparency );
	}
	
	/**
	 * Sets the background algorithm that should be used by this panel.
	 * @param background the background algorithm
	 */
	public void setBackground( BackgroundAlgorithm background ){
		if( this.background != null ){
			this.background.removeListener( listener );
		}
		this.background = background;
		if( background != null ){
			background.addListener( listener );
			configure( background.getTransparency() );
		}
	}
	
	/**
	 * Called if the {@link Transparency} of the {@link BackgroundAlgorithm} changed, this panel
	 * should configure itself to met the requested transparency settings.
	 * @param transparency the setting to use
	 */
	protected abstract void configure( Transparency transparency );
	
	/**
	 * Gets the algorithm that paints the background of this panel.
	 * @return the algorithm, can be <code>null</code>
	 */
	public BackgroundAlgorithm getBackgroundAlgorithm(){
		return background;
	}
	
	@Override
	public void paint( Graphics g ){
		setupRenderingHints( g );
		if( background == null || background.getPaint() == null ){
			super.paint( g );
			paintOverlay( g );
		}
		else{
			background.paint( this, g );
		}
	}
	
	/**
	 * Called before painting on this panel happens. Allows to apply rendering hints (or other settings) to
	 * <code>g</code>.
	 * @param g the painting context
	 */
	protected abstract void setupRenderingHints( Graphics g );

	protected void paintComponent( Graphics g ){
		paintBackground( g );
		paintForeground( g );
	}

	public void setTransparency( Transparency transparency ){
		this.transparency = transparency;
		setOpaque( transparency == Transparency.SOLID );
	}
	
	public Transparency getTransparency(){
		return transparency;
	}
	
	public Component getComponent(){
		return this;
	}
	
	public void paintBackground( Graphics g ){
		if( transparency != Transparency.TRANSPARENT ){
			super.paintComponent( g );
		}
	}
	
	public void paintForeground( Graphics g ){
		// ignore	
	}
	
	@Override
	public void paintBorder( Graphics g ){
		super.paintBorder( g );
	}
	
	@Override
	public void paintChildren( Graphics g ){
		super.paintChildren( g );
	}
	
	public void paintOverlay( Graphics g ){
		// ignore
	}
}
