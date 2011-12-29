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
package bibliothek.gui.dock.dockable;

import java.util.List;

import bibliothek.gui.DockController;
import bibliothek.gui.Dockable;
import bibliothek.gui.dock.themes.ThemeDockableMovingImageFactory;
import bibliothek.gui.dock.title.DockTitle;
import bibliothek.gui.dock.util.extension.ExtensionName;
import bibliothek.util.Path;

/**
 * The default implementation of {@link DockableMovingImageFactory} usually behaves exactly like
 * {@link ThemeDockableMovingImageFactory}, but also offers an {@link ExtensionName} for adding additional
 * factories.
 * @author Benjamin Sigg
 */
public class DefaultDockableMovingImageFactory implements DockableMovingImageFactory{
	/**
	 * The name of the {@link ExtensionName} that loads additional {@link DockableMovingImageFactory}s as extension. If
	 * any of the <code>create</code> methods is called, the result is the return value of the first extension that
	 * does not return <code>null</code>.
	 */
	public static final Path FACTORY_EXTENSION = new Path( "dock.movingImageFactory" );
	
	/** the backup factory */
	private DockableMovingImageFactory delegate;
	
	/** the factories that are asked first */
	private List<DockableMovingImageFactory> extensions;
	
	/**
	 * Creates a new factory using <code>controller</code> to load extensions.
	 * @param controller the controller in whose realm this factory is used
	 */
	public DefaultDockableMovingImageFactory( DockController controller ){
		this( controller, new ThemeDockableMovingImageFactory() );
	}
	
	/**
	 * Creates a new factory using <code>controller</code> to load extensions.
	 * @param controller the controller in whose realm this factory is used
	 * @param delegate the backup factory used if no extension is present or no extension has a result
	 */	
	public DefaultDockableMovingImageFactory( DockController controller, DockableMovingImageFactory delegate ){
		extensions = controller.getExtensions().load( new ExtensionName<DockableMovingImageFactory>( FACTORY_EXTENSION, DockableMovingImageFactory.class ) );
		this.delegate = delegate;
	}
	
	public MovingImage create( DockController controller, Dockable dockable ){
		for( DockableMovingImageFactory factory : extensions ){
			MovingImage image = factory.create( controller, dockable );
			if( image != null ){
				return image;
			}
		}
		return delegate.create( controller, dockable );
	}
	
	public MovingImage create( DockController controller, DockTitle snatched ){
		for( DockableMovingImageFactory factory : extensions ){
			MovingImage image = factory.create( controller, snatched );
			if( image != null ){
				return image;
			}
		}
		return delegate.create( controller, snatched );
	}
}
