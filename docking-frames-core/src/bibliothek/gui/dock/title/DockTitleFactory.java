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

package bibliothek.gui.dock.title;

/**
 * A factory for creating instances of {@link DockTitle}. Clients might install
 * {@link DockTitleRequest}s on this factory such that this factory can always
 * change the title that is currently shown. A factory should call 
 * {@link DockTitleRequest#answer(DockTitle)} only when requested so, meaning
 * only if the method {@link #request(DockTitleRequest)} is called. If a factory
 * wishes to exchange a title it should call {@link DockTitleRequest#request()} which
 * in return might then call {@link #request(DockTitleRequest)}. 
 * @author Benjamin Sigg
 */
public interface DockTitleFactory {
    
	/**
	 * Informs this factory that it might need to create a {@link DockTitle} for
	 * <code>request</code>. 
	 * @param request the new request
	 */
	public void install( DockTitleRequest request );
	
	/**
	 * Asks this factory to provide a {@link DockTitle} for <code>request</code>,
	 * this method should call {@link DockTitleRequest#answer(DockTitle)}. Note
	 * that this method may be called for requests that are not installed!
	 * @param request the request to answer
	 */
	public void request( DockTitleRequest request );
	
	/**
	 * Informs this factory that it no longer requires to provide any titles
	 * for <code>request</code>.
	 * @param request the request that is no longer managed by this factory
	 */
	public void uninstall( DockTitleRequest request );
}
