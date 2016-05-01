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
import java.util.ArrayList;
import java.util.List;

import bibliothek.gui.DockController;
import bibliothek.gui.dock.themes.ThemeManager;
import bibliothek.util.Path;

/**
 * A utility class for managing {@link BackgroundPaint}, {@link BackgroundComponent}
 * and {@link PaintableComponent} at the same time.
 * @author Benjamin Sigg
 */
public abstract class BackgroundAlgorithm implements BackgroundComponent{
	private Path kind;
	private String id;
	
	private DockController controller;
	private BackgroundPaint paint;
	
	/** all the observers of this algorithm */
	private List<BackgroundAlgorithmListener> listeners = new ArrayList<BackgroundAlgorithmListener>( 2 );
	
	/** how to paint the background */
	private Transparency transparency = Transparency.DEFAULT;
	
	/**
	 * Creates a new algorithm.
	 * @param kind the kind of {@link UIValue} this is
	 * @param id the identifier of this {@link UIValue}
	 */
	public BackgroundAlgorithm( Path kind, String id ){
		this.kind = kind;
		this.id = id;
	}
	
	/**
	 * Adds an observer to this algorithm. The observer will be informed when properties of this algorithm change.
	 * @param listener the new observer, not <code>null</code>
	 */
	public void addListener( BackgroundAlgorithmListener listener ){
		listeners.add( listener );
	}
	
	/**
	 * Removes the observer <code>listener</code> from this algorithm.
	 * @param listener the listener to remove
	 */
	public void removeListener( BackgroundAlgorithmListener listener ){
		listeners.remove( listener );
	}
	
	/**
	 * Sets the source of the {@link BackgroundPaint}.
	 * @param controller the new controller, can be <code>null</code>
	 */
	public void setController( DockController controller ){
		if( this.controller != null ){
			this.controller.getThemeManager().remove( this );
		}
		this.controller = controller;
		if( this.controller != null ){
			this.controller.getThemeManager().add( id, kind, ThemeManager.BACKGROUND_PAINT_TYPE, this );
		}
		else{
			set( null );
		}
	}
	
	public void repaint(){
		getComponent().repaint();	
	}
	
	public void set( BackgroundPaint value ){
		if( this.paint != null ){
			this.paint.uninstall( this );
		}
		this.paint = value;
		if( this.paint != null ){
			this.paint.install( this );
		}
	}
	
	public void setTransparency( Transparency transparency ){
		if( transparency == null ){	
			throw new IllegalArgumentException( "transparency must not be null" );
		}
		if( this.transparency != transparency ){
			this.transparency = transparency;
			for( BackgroundAlgorithmListener listener : listeners.toArray( new BackgroundAlgorithmListener[ listeners.size() ] )){
				listener.transparencyChanged( this, this.transparency );
			}
		}
	}
	
	public Transparency getTransparency(){
		return transparency;
	}
	
	/**
	 * Gets the {@link BackgroundPaint} of this {@link UIValue}.
	 * @return the value, can be <code>null</code>
	 */
	public BackgroundPaint getPaint(){
		return paint;
	}
	
	/**
	 * Paints <code>component</code> using the graphics context <code>g</code>. This method
	 * ensures that {@link PaintableComponent#paintBackground(Graphics)} and 
	 * {@link PaintableComponent#paintForeground(Graphics)} are not called with a <code>null</code> argument.
	 * <code>component</code> does not need to track how often its paint-methods are called, that is done
	 * by this method.
	 * @param component the component to paint
	 * @param g the graphics context to use
	 */
	public void paint( final PaintableComponent component, final Graphics g ){
		if( paint == null ){
			component.paintBackground( g );
			component.paintForeground( g );
			component.paintBorder( g );
			component.paintChildren( g );
			component.paintOverlay( g );
		}
		else{
			Paintable paintable = new Paintable( component );
			paintable.paint( g );
		}
	}
	
	/**
	 * Wrapper around a {@link PaintableComponent}.
	 * @author Benjamin Sigg
	 */
	private class Paintable implements PaintableComponent{
		private PaintableComponent delegate;
		
		private boolean backgroundPainted = false;
		private boolean foregroundPainted = false;
		private boolean borderPainted = false;
		private boolean childrenPainted = false;
		private boolean overlayPainted = false;
		
		/**
		 * Creates a new wrapper.
		 * @param delegate the delegate of this wrapper
		 */
		public Paintable( PaintableComponent delegate ){
			this.delegate = delegate;
		}

		public Component getComponent(){
			return delegate.getComponent();
		}

		public void paintBackground( Graphics g ){
			backgroundPainted = true;
			if( g != null ){
				delegate.paintBackground( g );
			}
		}

		public void paintForeground( Graphics g ){
			foregroundPainted = true;
			if( g != null ){
				delegate.paintForeground( g );
			}
		}
		
		public void paintBorder( Graphics g ){
			borderPainted = true;
			if( g != null ){
				delegate.paintBorder( g );
			}
		}
		
		public void paintChildren( Graphics g ){
			childrenPainted = true;
			if( g != null ){
				delegate.paintChildren( g );
			}
		}
		
		public void paintOverlay( Graphics g ){
			overlayPainted = true;
			if( g != null ){
				delegate.paintOverlay( g );
			}
		}
		
		public Transparency getTransparency(){
			return delegate.getTransparency();
		}
		
		/**
		 * Paints this {@link Paintable} using the graphics context.
		 * @param g the graphics context to paint
		 */
		public void paint( Graphics g ){
			paint.paint( BackgroundAlgorithm.this, this, g );
			if( !backgroundPainted ){
				paintBackground( g );
			}
			if( !foregroundPainted ){
				paintForeground( g );
			}
			if( !borderPainted ){
				paintBorder( g );
			}
			if( !childrenPainted ){
				paintChildren( g );
			}
			if( !overlayPainted ){
				paintOverlay( g );
			}
		}
	}
}
