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
package bibliothek.gui.dock.support.mode;

/**
 * This class is used by a {@link ModeManager} during a read operation to create
 * entries even if it not entirely sure whether the entry will ever be used.
 * @author Benjamin Sigg
 */
public interface UndoableModeSettings {
	/**
	 * Called during a read operation if {@link ModeManager#createEntryDuringRead(String)}
	 * did already return <code>false</code> for <code>key</code>.
	 * @param key the key of some element that might be important later
	 * @return <code>true</code> if an entry for <code>key</code> should be created
	 * temporarily
	 * @see ModeManager#readSettings(ModeSettings, UndoableModeSettings)
	 */
	public boolean createTemporaryDuringRead( String key );
}
