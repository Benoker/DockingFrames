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

import bibliothek.gui.Dockable;
import bibliothek.gui.dock.FlapDockStation;
import bibliothek.gui.dock.ScreenDockStation;
import bibliothek.gui.dock.SplitDockStation;
import bibliothek.gui.dock.StackDockStation;
import bibliothek.gui.dock.title.DockTitleFactory;
import bibliothek.gui.dock.title.DockTitleRequest;

/**
 * A {@link DockTitleFactory} which does not create titles for 
 * {@link StackDockStation StackDockStations} but uses another 
 * factory as delegate to create titles for the other stations.
 */
public class NoStackTitleFactory implements DockTitleFactory{
    /** The delegate to create titles */
    private DockTitleFactory base;
    
    /**
     * Creates a new factory
     * @param base the delegate which will be used to create titles for other
     * stations than the {@link StackDockStation}
     */
    public NoStackTitleFactory( DockTitleFactory base ){
        if( base == null )
            throw new IllegalArgumentException( "Base must not be null" );
        
        this.base = base;
    }
    
    public void install( DockTitleRequest request ){
	    base.install( request );	
    }
    
    public void uninstall( DockTitleRequest request ){
    	base.uninstall( request );
    }
    
    public void request( DockTitleRequest request ){
    	Dockable dockable = request.getTarget();
    	
    	if( dockable instanceof StackDockStation ){
            String id = request.getVersion().getID();
            if( id.equals( StackDockStation.TITLE_ID ) ||
                id.equals( FlapDockStation.WINDOW_TITLE_ID ) ||
                id.equals( ScreenDockStation.TITLE_ID ) ||
                id.equals( SplitDockStation.TITLE_ID )){
            	
            	request.answer( null );
            	return;
            }
        }
        
    	base.request( request );
    }
}
