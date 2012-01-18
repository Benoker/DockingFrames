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
package bibliothek.gui.dock.dockable;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.image.BufferedImage;
import java.util.LinkedList;
import java.util.List;

import javax.swing.SwingUtilities;

import bibliothek.gui.DockController;
import bibliothek.gui.Dockable;
import bibliothek.gui.dock.title.DockTitle;

/**
 * This factory tries to find a common anchestor of {@link DockTitle} and
 * of {@link Dockable} and shows a screenshot of this component.
 * @author Benjamin Sigg
 */
public class AncestorMovingImageFactory extends ScreencaptureMovingImageFactory{
    /**
     * Creates a new factory.
     * @param max the maximal size of the images created by this factory, or <code>null</code>
     * for not having a maximum size
     * @param alpha the transparency, where 0 means completely transparent and 1 means completely opaque
     */
	public AncestorMovingImageFactory( Dimension max, float alpha ){
		super( max, alpha );
	}

	@Override
	public MovingImage create( DockController controller, Dockable dockable ){
		Component[] dockableAncestor = ancestors( dockable.getComponent() );
		int nearest = dockableAncestor.length + 1;
		Component best = null;
		
		for( DockTitle title : dockable.listBoundTitles() ){
			Component[] titleAncestor = ancestors( title.getComponent() );
			
			int index = 0;
			while( index < dockableAncestor.length && index < titleAncestor.length && dockableAncestor[index] == titleAncestor[index]){
				index++;
			}
			index--;
			
			int dist = dockableAncestor.length - index;
			if( dist < nearest ){
				nearest = dist;
				best = dockableAncestor[index];
			}
		}
		
		if( best == null ){
			return super.create( controller, dockable );
		}
		else{
			BufferedImage image = createImageFrom( controller, best );
			
			TrueMovingImage moving = new TrueMovingImage();
			moving.setAlpha( getAlpha() );
	        moving.setImage( image );
	        return moving;
		}
	}
	
	private Component[] ancestors( Component component ){
		Component root = SwingUtilities.getRoot( component );
		if( root == component ){
			return new Component[]{ component };
		}
		
		List<Component> result = new LinkedList<Component>();
		
		while( root != component ){
			result.add( 0, component );
			component = component.getParent();
		}
		
		result.add( 0, root );
		
		return result.toArray( new Component[ result.size() ] );
	}
}
