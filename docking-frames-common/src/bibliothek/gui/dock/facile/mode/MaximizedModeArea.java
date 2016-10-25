/*
 * Bibliothek - DockingFrames
 * Library built on Java/Swing, allows the user to "drag and drop"
 * panels containing any Swing-Component the developer likes to add.
 * 
 * Copyright (C) 2009 Benjamin Sigg
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
package bibliothek.gui.dock.facile.mode;

import bibliothek.gui.DockStation;
import bibliothek.gui.Dockable;
import bibliothek.gui.dock.layout.DockableProperty;
import bibliothek.gui.dock.support.mode.AffectedSet;
import bibliothek.gui.dock.support.mode.Mode;

/**
 * The parent of a set of {@link Dockable}s that are maximized. The {@link MaximizedMode}, which
 * is feed with these areas, assumes that a {@link MaximizedModeArea} is also
 * some other kind of area (e.g. a {@link NormalModeArea}).
 * @author Benjamin Sigg
 */
public interface MaximizedModeArea extends ModeArea{
	
	/**
	 * This method is called before the method {@link Mode#apply(Dockable, Object, AffectedSet) apply}
	 * is executed of a {@link LocationMode} that is not the owner of this area. The element 
	 * <code>event.getDockable()</code> may or may not be a child of this station. This method is called
	 * before {@link #onApply(LocationModeEvent, Dockable)} is called.
	 * @param event detailed information about the event
	 * @return a piece of code executed once <code>apply</code> has finished its job 
	 */
	public Runnable onApply( LocationModeEvent event );
	
	/**
	 * This method is called before the method {@link Mode#apply(Dockable, Object, AffectedSet) apply}
	 * is executed of a {@link LocationMode} that is not the owner of this area. The element
	 * <code>event.getDockable()</code> is a direct or indirect child of this area and maximized. The 
	 * {@link MaximizedMode} suggests to use <code>replacement</code> as direct child
	 * once the old element has been removed. This method must decide how this area
	 * reacts on the pending change, e.g. set its maximized dockable to <code>null</code> and
	 * later re-maximize. The result of this method is a {@link Runnable} which will be executed
	 * once the <code>apply</code> method is finished.<br>
	 * This method is called after {@link #onApply(LocationModeEvent)} is called.
	 * @param event detailed information about the event 
	 * @param replacement the suggested new maximized element
	 * @return a piece of code executed once <code>apply</code> has finished its job
	 */
	public Runnable onApply( LocationModeEvent event, Dockable replacement );
	
	/**
	 * This method is called by {@link MaximizedMode} just before the mode is applied 
	 * to <code>dockable</code>.
	 * @param dockable the new child of this area
	 * @param history future location of <code>dockable</code>
	 * @param set this method has to store all {@link Dockable}s which might have changed their
	 * mode in the set.  
	 */
	public void prepareApply( Dockable dockable, Location history, AffectedSet set );
	
	/**
	 * Tells this parent to change the maximization state of <code>dockable</code>. This 
	 * area may unmaximize other {@link Dockable}s if necessary. 
	 * @param dockable the maximized element, <code>null</code> to indicate
	 * that no element should be maximized.
	 * @param maximized the new state of <code>dockable</code>
	 * @param location the expected location of <code>dockable</code> after this method completed, may be <code>null</code>
	 * @param set this method has to store all {@link Dockable}s which might have changed their
	 * mode in the set.
	 */
	public void setMaximized( Dockable dockable, boolean maximized, Location location, AffectedSet set );
	
	/**
	 * Gets the currently maximized elements.
	 * @return the currently maximized dockables, can be <code>null</code> or empty
	 */
	public Dockable[] getMaximized();
	
	/**
	 * Tells whether this area is representing <code>station</code>. It is
	 * legitimate for an area to represent more than one or no station at all.
	 * @param station some station
	 * @return <code>true</code> if this represents <code>station</code>
	 */
	public boolean isRepresenting( DockStation station );
	
	/**
	 * Tells which mode would be the preferred mode for unmaximization.
	 * @return the preferred unmaximized mode, can be <code>null</code>
	 */
	public LocationMode getUnmaximizedMode();
	
	/**
	 * Gets the location of <code>dockable</code> which is a child
	 * of this station.
	 * @param child the child
	 * @return the location, may be <code>null</code>
	 */
	public DockableProperty getLocation( Dockable child );
}
