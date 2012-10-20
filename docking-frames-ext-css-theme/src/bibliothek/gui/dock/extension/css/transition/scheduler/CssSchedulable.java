/*
 * Bibliothek - DockingFrames
 * Library built on Java/Swing, allows the user to "drag and drop"
 * panels containing any Swing-Component the developer likes to add.
 * 
 * Copyright (C) 2012 Benjamin Sigg
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
package bibliothek.gui.dock.extension.css.transition.scheduler;

/**
 * Some kind of animation that can be triggered by the {@link CssScheduler}.
 * @author Benjamin Sigg
 */
public interface CssSchedulable {
	/**
	 * Called with some delay, usually in the EDT, from <code>scheduler</code>.
	 * @param scheduler the scheduler, may be used to schedule a follow up event
	 * @param delay the amount of milliseconds passed since the last call to
	 * {@link CssScheduler#step(CssSchedulable)} from within this method,
	 * or <code>-1</code> if <code>step</code> was not called by this method
	 */
	public void step( CssScheduler scheduler, int delay );
}
