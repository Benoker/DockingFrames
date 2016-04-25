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

package bibliothek.gui.dock.title;

import bibliothek.gui.DockController;
import bibliothek.gui.DockTheme;

/**
 * This {@link DockTitleFactory factory} delegates every call to
 * the methods to the factory provided by the {@link DockTheme theme} of
 * the involved {@link DockController controller}
 * @author Benjamin Sigg
 */
public class ControllerTitleFactory implements DockTitleFactory{
	/**
	 * An instance of this factory.<br>
	 * Note: it is safe to use {@link #install(DockTitleRequest)} and
	 * {@link #uninstall(DockTitleRequest)} on this singleton, the call will be forwarded
	 * to the {@link DockController} of the calling {@link DockTitleRequest}.
	 */
    public static final ControllerTitleFactory INSTANCE = new ControllerTitleFactory();
    
    private DockTitleVersion getControllerVersion( DockTitleRequest request ){
    	return request.getVersion().getController().getDockTitleManager().getVersion( DockTitleManager.THEME_FACTORY_ID );
    }
    
    public void install( DockTitleRequest request ){
	    getControllerVersion( request ).install( request );	
    }
    
    public void request( DockTitleRequest request ){
    	getControllerVersion( request ).request( request );
    }
    
    public void uninstall( DockTitleRequest request ){
	    getControllerVersion( request ).uninstall( request );	
    }
}
