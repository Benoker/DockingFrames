/*
 * Bibliothek - DockingFrames
 * Library built on Java/Swing, allows the user to "drag and drop"
 * panels containing any Swing-Component the developer likes to add.
 * 
 * Copyright (C) 2012 Benjamin Sigg
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
package bibliothek.gui.dock.extension.css.property.paint;

import java.awt.AlphaComposite;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Graphics2D;

import bibliothek.gui.dock.extension.css.CssProperty;
import bibliothek.gui.dock.extension.css.CssPropertyContainerListener;
import bibliothek.gui.dock.extension.css.property.shape.CssShape;
import bibliothek.gui.dock.extension.css.transition.types.AbstractTransitionalCssProperty;

/**
 * An algorithm to "fade" from one {@link CssPaint} into another {@link CssPaint}.
 * @author Benjamin Sigg
 */
public class TransitionalCssPaintProperty extends AbstractTransitionalCssProperty<CssPaint>{

	@Override
	protected void update(){
		getCallback().set( new FadedPaint( getSource(), getTarget(), (float)getTransition() ) );
	}

	private class FadedPaint implements CssPaint{
		private CssPaint source;
		private CssPaint target;
		private float transition;
		
		public FadedPaint( CssPaint source, CssPaint target, float transition ){
			this.source = source;
			this.target = target;
			this.transition = transition;
		}

		@Override
		public void paintArea( Graphics g, Component c, CssShape shape ){
			Graphics2D g2 = (Graphics2D)g.create();
			if( source != null ){
				g2.setComposite( AlphaComposite.getInstance( AlphaComposite.SRC_OVER, 1-transition ) );
				source.paintArea( g2, c, shape );
			}
			if( target != null ){
				g2.setComposite( AlphaComposite.getInstance( AlphaComposite.SRC_OVER, transition ) );
				target.paintArea( g2, c, shape );
			}
			g2.dispose();
		}

		@Override
		public void paintBorder( Graphics g, Component c, CssShape shape ){
			Graphics2D g2 = (Graphics2D)g.create();
			if( source != null ){
				g2.setComposite( AlphaComposite.getInstance( AlphaComposite.SRC_OVER, 1-transition ) );
				source.paintBorder( g2, c, shape );
			}
			if( target != null ){
				g2.setComposite( AlphaComposite.getInstance( AlphaComposite.SRC_OVER, transition ) );
				target.paintBorder( g2, c, shape );
			}
			g2.dispose();
		}
		
		@Override
		public String[] getPropertyKeys(){
			return new String[]{};
		}

		@Override
		public CssProperty<?> getProperty( String key ){
			return null;
		}

		@Override
		public void addPropertyContainerListener( CssPropertyContainerListener listener ){
		}

		@Override
		public void removePropertyContainerListener( CssPropertyContainerListener listener ){
		}

		@Override
		public void init( Component component ){
		}
		
		@Override
		public String toString(){
			return getClass().getSimpleName() + "[source=" + source + ", target=" + target + ", transition=" + transition + "]";
		}
	}
}
