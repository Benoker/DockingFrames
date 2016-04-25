/**
 * Bibliothek - DockingFrames
 * Library built on Java/Swing, allows the user to "drag and drop"
 * panels containing any Swing-Component the developer likes to add.
 * 
 * Copyright (C) 2007 Benjamin Sigg
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

package bibliothek.gui.dock.themes.nostack;

import bibliothek.gui.DockController;
import bibliothek.gui.DockStation;
import bibliothek.gui.Dockable;
import bibliothek.gui.dock.StackDockStation;
import bibliothek.gui.dock.accept.AbstractAcceptance;
import bibliothek.gui.dock.accept.DockAcceptance;
import bibliothek.gui.dock.control.relocator.Merger;

/**
 * A {@link DockAcceptance} which permits the user to set a  
 * {@link StackDockStation} into another <code>StackDockStation</code>.
 * @author Benjamin Sigg
 */
public class NoStackAcceptance extends AbstractAcceptance{
	@Override
    public boolean accept( DockStation parent, Dockable child ) {
        if( parent instanceof StackDockStation ){
            if( child.asDockStation() instanceof StackDockStation ){
            	DockController controller = parent.getController();
            	if( controller != null ){
            		Merger merger = controller.getRelocator().getMerger();
            		if( merger != null ){
            			return merger.canMerge( null, parent, child.asDockStation() );
            		}
            	}

            	return false;
            }
        }
        
        return true;
    }

    @Override
    public boolean accept( DockStation parent, Dockable child, Dockable next ) {
        if( parent instanceof StackDockStation ){
            return false;
        }
        
        if( child instanceof StackDockStation ){
            return false;
        }
        
        if( next instanceof StackDockStation ){
            return false;
        }
        
        return true;
    }
}
