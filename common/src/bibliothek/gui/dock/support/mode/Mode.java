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
package bibliothek.gui.dock.support.mode;

import bibliothek.extension.gui.dock.util.Path;
import bibliothek.gui.Dockable;
import bibliothek.gui.dock.action.DockActionSource;

/**
 * A mode describes a state in which a {@link Dockable} can be. A Dockable
 * can be in exactly one {@link Mode} at a time.
 * @author Benjamin Sigg
 */
public interface Mode {
	/**
	 * Gets a {@link DockActionSource} which should be shown on <code>dockable</code>
	 * which is currently in <code>mode</code>. This method will be called
	 * every time when <code>dockable</code> changes its mode.
	 * @param dockable some element, not <code>null</code>
	 * @param mode the mode of <code>dockable</code>, not <code>null</code>
	 * @return the actions for <code>dockable</code>, can be <code>null</code>
	 */
	public DockActionSource getActionsFor( Dockable dockable, Mode mode );
	
	/**
	 * Gets a unique identifier, only this {@link Mode} must have this
	 * identifier. Identifiers with the first segment being "dock" are
	 * reserved for this framework, clients may choose any other identifiers.
	 * @return the identifier, not <code>null</code>, should contain at least
	 * one segment.
	 */
	public Path getUniqueIdentifier();
	
	/**
	 * Gets the neutral mode for <code>dockable</code>. The neutral mode
	 * is applied before changing the mode of any {@link Dockable}.
	 * @param dockable the element whose neutral mode is asked
	 * @return the neutral mode or <code>null</code>
	 * @see NeutralMode
	 */
	public NeutralMode<?> getNeutralMode( Dockable dockable );
	
	/**
	 * Applies this mode to <code>dockable</code> which is currently
	 * in the neutral state of <code>mode</code>.
	 * @param dockable the element whose mode becomes <code>this</code>
	 * @param mode its old mode
	 */
	public void apply( Dockable dockable, Mode mode );
	
	/**
	 * Checks whether this mode is a default mode of <code>dockable</code>. A 
	 * default mode is a mode that is choosen per default, if no other mode
	 * is selected. There should be only one default-mode per {@link Dockable}.
	 * @param dockable some dockable, not <code>null</code>
	 * @return whether this is a default mode
	 */
	public boolean isDefaultMode( Dockable dockable );
	
	/**
	 * Tells whether <code>dockable</code> fulfills the requirements of
	 * this mode, meaning whether <code>docakble</code> has this mode. There
	 * should be only at most one mode which returns <code>true</code> for this
	 * question. Please note, the mode selected in the {@link ModeManager} may
	 * be out of date, and should not be considered when checking the
	 * current mode.
	 * @param dockable some dockable, not <code>null</code>
	 * @return whether <code>dockable</code> is in <code>this</code> mode
	 */
	public boolean isCurrentMode( Dockable dockable );
}
