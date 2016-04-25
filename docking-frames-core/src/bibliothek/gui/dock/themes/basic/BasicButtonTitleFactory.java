/*
 * Bibliothek - DockingFrames
 * Library built on Java/Swing, allows the user to "drag and drop"
 * panels containing any Swing-Component the developer likes to add.
 * 
 * Copyright (C) 2008 Benjamin Sigg
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

package bibliothek.gui.dock.themes.basic;

import bibliothek.gui.dock.title.DockTitleFactory;
import bibliothek.gui.dock.title.DockTitleRequest;

/**
 * A {@link DockTitleFactory factory} for the {@link BasicButtonDockTitle}
 * @author Benjamin Sigg
 */
public class BasicButtonTitleFactory implements DockTitleFactory {
    /** A static instance of this factory, can be used everywhere */
    public static final BasicButtonTitleFactory FACTORY = new BasicButtonTitleFactory();

    public void install( DockTitleRequest request ){
	    // ignore	
    }
    
    public void request( DockTitleRequest request ){
    	request.answer( new BasicButtonDockTitle( request.getTarget(), request.getVersion() ) );
    }
    
    public void uninstall( DockTitleRequest request ){
	    // ignore	
    }
}
