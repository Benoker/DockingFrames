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
package bibliothek.gui.dock.frontend;

import java.io.IOException;

import bibliothek.gui.DockFrontend;
import bibliothek.gui.Dockable;
import bibliothek.gui.dock.DockElement;
import bibliothek.gui.dock.control.DockRegister;
import bibliothek.gui.dock.layout.DockLayoutComposition;
import bibliothek.gui.dock.layout.DockLayoutInfo;
import bibliothek.gui.dock.layout.DockSituation;
import bibliothek.gui.dock.layout.DockableProperty;
import bibliothek.gui.dock.layout.PropertyTransformer;
import bibliothek.gui.dock.perspective.Perspective;
import bibliothek.gui.dock.perspective.PerspectiveElement;
import bibliothek.gui.dock.station.support.PlaceholderStrategy;
import bibliothek.util.xml.XElement;
import bibliothek.util.xml.XException;

/**
 * Used by a {@link DockFrontend} to read layout information and apply new
 * positions for {@link Dockable}s.<br>
 * While this interface (like any other interface in this framework) can be implemented
 * by clients, they are not supposed to do so.
 * @author Benjamin Sigg
 */
public interface LayoutChangeStrategy {
	/**
	 * Changes the layout of <code>frontend</code> by reading and applying <code>setting</code>. 
	 * The {@link DockRegister} is {@link DockRegister#isStalled() stalled} while this method runs.<br>
	 * This method should use the {@link VetoManager} provided by <code>frontend</code> to ensure
	 * that all operations are legal.
	 * @see #shouldUpdateLayoutOnAdd(Dockable)
	 * @param frontend internal information about a {@link DockFrontend}
	 * @param setting the layout to read and apply
	 * @param entry <code>true</code> if <code>setting</code> contains only little information about
	 * the layout, <code>false</code> if there is much information abut the layout.
	 * @return <code>true</code> if the layout has been applied, <code>false</code> if
	 * the operation was canceled due of any reason
	 * @throws IOException in case of some stream that cannot be read
	 * @throws XException in case of some {@link XElement} that cannot be read
	 */
	public boolean setLayout( DockFrontendInternals frontend, Setting setting, boolean entry ) throws IOException, XException;
	
    /**
     * Creates a new {@link DockSituation} that is used to write and read the current setting from and
     * to a stream.<br>
     * Note: the result of this method is used to read and write data from a file, the frontend 
     * expects that always the same format (i.e. the same kind of {@link DockSituation}) is used.
     * @param frontend the frontend for which the situation is required
     * @param entry <code>true</code> if the situation is used for a regular setting,
     * <code>false</code> if the situation is used as the final setting which will
     * be loaded the next time the application starts.
     * @return the situation
     */
	public DockSituation createSituation( DockFrontendInternals frontend, boolean entry );
	
	/**
	 * Creates a {@link Perspective} that is used to read and write perspectives related to <code>frontend</code>.
     * @param frontend the frontend for which the situation is required
     * @param entry <code>true</code> if the situation is used for a regular setting,
     * <code>false</code> if the situation is used as the final setting which will
     * be loaded the next time the application starts. 
     * @param cache a cache that takes {@link DockElement}s and returns the matching {@link PerspectiveElement}. The cache
     * also offers methods to convert ids and {@link PerspectiveElement}s directly
     * @return the new perspective
	 */
	public DockFrontendPerspective createPerspective( DockFrontendInternals frontend, boolean entry, FrontendPerspectiveCache cache );
	
	/**
	 * Creates a converter for reading and writing {@link DockableProperty}s.
	 * @param frontend the frontend for which the converter is required
	 * @return the new converter
	 */
	public PropertyTransformer createTransformer( DockFrontendInternals frontend );
	
	/**
     * Tries to fill the property {@link DockLayoutInfo#getLocation() location}
     * for each element in <code>layout</code>.
     * @param frontend the frontend which calls this method
     * @param situation the situation to use for transforming information
     * @param layout the layout to estimate
     */
    public void estimateLocations( DockFrontendInternals frontend, DockSituation situation, DockLayoutComposition layout );

    /**
     * Gets the default {@link PlaceholderStrategy} which should be used to filter placeholders by <code>frontend</code>.
     * @param frontend information about the {@link DockFrontend} that needs the strategy
     * @return the strategy, can be <code>null</code>
     */
	public PlaceholderStrategy getPlaceholderStrategy( DockFrontendInternals frontend );

	/**
	 * Called when <code>dockable</code> is added to a {@link DockFrontend}, and the frontend already knows the layout which should be used
	 * for <code>dockable</code>. If the result is <code>true</code>, then all children of <code>dockable</code> are removed, and reloaded to apply the layout
	 * that is stored in the frontend.<br>
	 * Subclasses may return <code>false</code> if {@link #setLayout(DockFrontendInternals, Setting, boolean)} is currently executed, preventing concurrent
	 * modifications of the dock-tree.  
	 * @param dockable the dockable that is added to a {@link DockFrontend}
	 * @return <code>true</code> if the layout of <code>dockable</code> should be updated.
	 */
	public boolean shouldUpdateLayoutOnAdd( Dockable dockable );
}
