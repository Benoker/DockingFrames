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

/**
 * A default implementation of a {@link PaintableComponent}.
 * @author Benjamin Sigg
 */
public abstract class AbstractPaintableComponent implements PaintableComponent {
	private BackgroundComponent background;
	private Component component;
	private BackgroundPaint paint;
	
	private boolean backgroundPainted = false;
	private boolean foregroundPainted = false;
	private boolean borderPainted = false;
	private boolean childrenPainted = false;
	private boolean overlayPainted = false;
	
	/**
	 * Creates a new paintable component.
	 * @param background the owner of this {@link PaintableComponent}, can be <code>null</code>
	 * @param component the component which is painted by this object, must not be <code>null</code>
	 * @param paint the algorithm to use for painting, can be <code>null</code>
	 */
	public AbstractPaintableComponent( BackgroundComponent background, Component component, BackgroundPaint paint ){
		if( component == null ){
			throw new IllegalArgumentException( "component must not be null" );
		}
		this.background = background;
		this.component = component;
		this.paint = paint;
	}
	
	public Component getComponent(){
		return component;
	}

	/**
	 * Paints this component using the {@link BackgroundPaint} if present.
	 * @param g the graphics context to use
	 */
	public void paint( Graphics g ){
		backgroundPainted = false;
		foregroundPainted = false;
		
		if( paint != null && background != null ){
			paint.paint( background, this, g );
		}
		if( !backgroundPainted ){
			background( g );
		}
		if( !foregroundPainted ){
			foreground( g );
		}
		if( !borderPainted ){
			border( g );
		}
		if( !childrenPainted ){
			children( g );
		}
		if( !overlayPainted ){
			overlay( g );
		}
	}
	
	public void paintBackground( Graphics g ){
		backgroundPainted = true;
		if( g != null ){
			background( g );
		}
	}
	
	public void paintForeground( Graphics g ){
		foregroundPainted = true;
		if( g != null ){
			foreground( g );
		}
	}
	
	public void paintBorder( Graphics g ){
		borderPainted = true;
		if( g != null ){
			border( g );
		}
	}
	
	public void paintChildren( Graphics g ){
		childrenPainted = true;
		if( g != null ){
			children( g );
		}
	}
	
	public void paintOverlay( Graphics g ){
		overlayPainted = true;
		if( g != null ){
			overlay( g );
		}
	}
	
	/**
	 * Paints the background of the component.
	 * @param g the graphics context to use
	 */
	protected abstract void background( Graphics g );
	
	/**
	 * Paints the foreground of the component.
	 * @param g the graphics context to use
	 */
	protected abstract void foreground( Graphics g );

	/**
	 * Paints the overlay of this component.
	 * @param g the graphics context to use
	 */
	protected abstract void border( Graphics g );
	
	/**
	 * Paints the children of this component.
	 * @param g the graphics context to use
	 */
	protected abstract void children( Graphics g );

	/**
	 * Paints an overlay over the children of this component.
	 * @param g the graphics context to use
	 */
	protected abstract void overlay( Graphics g );
}
