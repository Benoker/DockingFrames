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

import bibliothek.gui.Dockable;
import bibliothek.gui.dock.accept.DockAcceptance;
import bibliothek.gui.dock.action.DockActionSource;
import bibliothek.util.Path;

/**
 * A mode describes a state in which a {@link Dockable} can be. A Dockable
 * can be in exactly one {@link Mode} at a time. Notice that the mode may
 * change through events that are not registered or influenced by this
 * mode.
 * @author Benjamin Sigg
 * @param <H> class storing history information
 */
public interface Mode<H> {
	/**
	 * Gets a {@link DockActionSource} which should be shown on <code>dockable</code>
	 * which is currently in <code>mode</code>. This method will be called
	 * every time when <code>dockable</code> changes its mode.
	 * @param dockable some element, not <code>null</code>
	 * @param mode the mode of <code>dockable</code>, not <code>null</code>
	 * @return the actions for <code>dockable</code>, can be <code>null</code>
	 */
	public DockActionSource getActionsFor( Dockable dockable, Mode<H> mode );
	
	/**
	 * Gets a unique identifier, only this {@link Mode} must have this
	 * identifier. Identifiers with the first segment being "dock" are
	 * reserved for this framework, clients may choose any other identifiers.
	 * @return the identifier, not <code>null</code>, should contain at least
	 * one segment.
	 */
	public Path getUniqueIdentifier();

	/**
	 * Applies this mode to <code>dockable</code>. This method may fail for example because a {@link DockAcceptance}
	 * does prevent <code>dockable</code> from being moved.
	 * @param dockable the element whose mode becomes <code>this</code>
	 * @param history history information that was returned by this mode
	 * on its last call to {@link #current(Dockable)}. May be <code>null</code>
	 * if this mode was never applied or returns <code>null</code> on {@link #current(Dockable)}.
	 * @param set this method has to store all {@link Dockable}s which might have changed their
	 * mode in the set.
	 * @return <code>true</code> if <code>dockable</code> was successfully moved on its parent or to a new parent,
	 * or <code>false</code> if <code>dockable</code> did not change its location
	 */
	public boolean apply( Dockable dockable, H history, AffectedSet set );
	
	/**
	 * Provides history information about the current state of <code>dockable</code>
	 * in respect to this mode.
	 * @param dockable the element
	 * @return history information that is needed when calling {@link #apply(Dockable, Object, AffectedSet)}
	 */
	public H current( Dockable dockable );
	
	/**
	 * Checks whether this mode is a default mode of <code>dockable</code>. A 
	 * default mode is a mode that is chosen per default, if no other mode
	 * is selected. There should be only one default-mode per {@link Dockable}.
	 * @param dockable some dockable, not <code>null</code>
	 * @return whether this is a default mode
	 */
	public boolean isDefaultMode( Dockable dockable );
	
	/**
	 * Tells whether <code>dockable</code> fulfills the requirements of
	 * this mode, meaning whether <code>dockable</code> has this mode. There
	 * should be only at most one mode which returns <code>true</code> for this
	 * question. Please note, the mode selected in the {@link ModeManager} may
	 * be out of date, and should not be considered when checking the
	 * current mode.
	 * @param dockable some dockable, not <code>null</code>
	 * @return whether <code>dockable</code> is in <code>this</code> mode
	 */
	public boolean isCurrentMode( Dockable dockable );

	/**
	 * Gets the current properties of this mode in an independent way.
	 * @param setting a {@link ModeSetting} with the same id as this {@link Mode}. This setting
	 * was created by a {@link ModeSettingFactory} with the same id as this {@link Mode}.
	 */
	public void writeSetting( ModeSetting<H> setting );
	
	/**
	 * Sets the properties of this mode. This method will only be called
	 * with a {@link ModeSetting} that has been created by the current
	 * {@link #getSettingFactory() ModeSettingFactory}
	 * @param setting the new set of properties, not <code>null</code>
	 */
	public void readSetting( ModeSetting<H> setting );
	
	/**
	 * Gets a factory for creating new {@link ModeSetting}s.
	 * @return the factory, can be <code>null</code>
	 */
	public ModeSettingFactory<H> getSettingFactory();
}
