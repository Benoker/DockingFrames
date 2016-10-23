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

package bibliothek.gui.dock.station.toolbar;

import java.awt.Component;
import java.awt.Point;

import bibliothek.gui.DockController;
import bibliothek.gui.Dockable;
import bibliothek.gui.dock.dockable.DockableMovingImageFactory;
import bibliothek.gui.dock.dockable.MovingImage;
import bibliothek.gui.dock.title.DockTitle;

/**
 * A {@link ToolbarMovingImageFactory} is a filter that forwards any {@link Dockable} which is a 
 * {@link ToolbarStrategy#isToolbarPart(Dockable) toolbar part} to some other factory.
 * @author Benjamin Sigg
 */
public class ToolbarMovingImageFactory implements DockableMovingImageFactory{
	private DockableMovingImageFactory delegate;
	
	/**
	 * Creates a new factory
	 * @param delegate the factory that will actually create {@link MovingImage}s, if <code>this</code> does include
	 * a {@link Dockable}
	 */
	public ToolbarMovingImageFactory( DockableMovingImageFactory delegate ){
		this.delegate = delegate;
	}
	
	@Override
	public MovingImage create( DockController controller, DockTitle snatched ){
		ToolbarStrategy strategy = controller.getProperties().get( ToolbarStrategy.STRATEGY );
		MovingImage image = null;
		if( strategy.isToolbarPart( snatched.getDockable() )){
			image = delegate.create( controller, snatched );
		}
		if( image == null ){
			return null;
		}
		return new OffsetWrapper( image );
	}

	@Override
	public MovingImage create( DockController controller, Dockable dockable ){
		ToolbarStrategy strategy = controller.getProperties().get( ToolbarStrategy.STRATEGY );
		MovingImage image = null;
		if( strategy.isToolbarPart( dockable )){
			image = delegate.create( controller, dockable );
		}
		if( image == null ){
			return null;
		}
		return new OffsetWrapper( image );
	}

	/**
	 * Wraps around a {@link MovingImage} and changes the offset of the image.
	 * @author Benjamin Sigg
	 */
	private class OffsetWrapper implements MovingImage{
		private MovingImage image;
		
		public OffsetWrapper( MovingImage image ){
			this.image = image;
		}
		
		@Override
		public Point getOffset( Point pressPoint ){
			return new Point( -pressPoint.x, -pressPoint.y );
		}
		
		@Override
		public void bind( boolean transparency ){
			image.bind( transparency );
		}
		
		@Override
		public void unbind(){
			image.unbind();
		}
		
		@Override
		public Component getComponent(){
			return image.getComponent();
		}
	}
}
