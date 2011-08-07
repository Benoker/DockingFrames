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

import bibliothek.gui.dock.util.BackgroundComponent.Transparency;

/**
 * This {@link JPanel} implements {@link PaintableComponent} and 
 * can use a {@link BackgroundAlgorithm} to paint its background.
 * @author Benjamin Sigg
 */
public abstract class BackgroundPanel extends JPanel implements PaintableComponent{
	private BackgroundAlgorithm background;
	
	/** whether no pixels of this panel are painted */
	private boolean transparent;
	
	/** added to the {@link BackgroundAlgorithm} that paints this component */
	private BackgroundAlgorithmListener listener = new BackgroundAlgorithmListener(){
		public void transparencyChanged( BackgroundAlgorithm algorithm, Transparency transparency ){
			configure( transparency );
		}
	};
	
	/**
	 * Creates a new panel.
	 * @param solid whether all pixels of this panel are painted
	 * @param transparent whether no pixels of this panel are painted
	 */
	public BackgroundPanel( boolean solid, boolean transparent ){
		setSolid( solid );
		this.transparent = transparent;
	}
	
	/**
	 * Creates a new panel setting a default {@link LayoutManager}.
	 * @param layout the layout manager, can be <code>null</code>
	 * @param solid whether all pixels of this panel are painted
	 * @param transparent whether no pixels of this panel are painted
	 */
	public BackgroundPanel( LayoutManager layout, boolean solid, boolean transparent ){
		super( layout );
		setSolid( solid );
		this.transparent = transparent;
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
		if( background == null || background.getPaint() == null ){
			super.paint( g );
			paintOverlay( g );
		}
		else{
			background.paint( this, g );
		}
	}

	protected void paintComponent( Graphics g ){
		paintBackground( g );
		paintForeground( g );
	}

	/**
	 * Sets whether this panel paints every pixel or not.
	 * @param solid <code>true</code> if every pixel is painted
	 */
	public void setSolid( boolean solid ){
		setOpaque( solid );
	}
	
	public boolean isSolid(){
		return isOpaque();
	}
	
	/**
	 * Sets whether this panel is completely transparent or not.
	 * @param transparent <code>true</code> if this panel does not paint any pixel
	 */
	public void setTransparent( boolean transparent ){
		this.transparent = transparent;
	}
	
	public boolean isTransparent(){
		return transparent;
	}
	
	public Component getComponent(){
		return this;
	}
	
	public void paintBackground( Graphics g ){
		if( !isTransparent() ){
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
