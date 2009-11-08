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
package bibliothek.gui.dock.common.mode;

import bibliothek.gui.Dockable;
import bibliothek.gui.dock.support.mode.NeutralMode;
import bibliothek.gui.dock.support.mode.NeutralModeCallback;

/**
 * This {@link NeutralMode} is used by the {@link MaximizedMode} to
 * de-maximize a {@link Dockable} and later re-maximize it if the maximize-spot
 * is still available.
 * @author Benjamin Sigg
 */
public class UnmaximizedNeutralState implements NeutralMode<Boolean>{
	public NeutralMode<?> getNext( Dockable dockable, Boolean data ){
		return null;
	}

	public Boolean toNeutral( Dockable dockable, NeutralModeCallback callback ){
		// TODO Auto-generated method stub
		return null;
	}

	public void toSpecific( Dockable dockable, Boolean data,
			NeutralModeCallback callback ){
		// TODO Auto-generated method stub
		
	}
	
}
