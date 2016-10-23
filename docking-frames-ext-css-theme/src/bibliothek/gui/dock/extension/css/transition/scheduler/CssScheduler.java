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
 * The {@link CssScheduler} is responsible for repeatedly invoking animation
 * code. The {@link CssScheduler} may perform optimizations, like coalescing calls.<br>
 * Usually a scheduler should call the animations in the EDT (EventDispatcherThread).
 * @author Benjamin Sigg
 */
public interface CssScheduler {
	/**
	 * Calls {@link CssSchedulable#step(CssScheduler, int)} with some reasonable
	 * delay, usually around 20 milliseconds.
	 * @param job the animation to call
	 * @see #step(CssSchedulable, int)
	 */
	public void step( CssSchedulable job );
	
	/**
	 * Calls {@link CssSchedulable#step(CssScheduler, int)} with a delay of about
	 * <code>delay</code> milliseconds. If more than one call to <code>job</code> is pending,
	 * then this scheduler may ignore this method call. If this method is called during an
	 * execution of {@link CssSchedulable#step(CssScheduler, int)}, then the
	 * next call to <code>step</code> should tell the amount of milliseconds that passed.
	 * @param job the animation to call
	 * @param delay the delay until <code>job</code> is executed
	 */
	public void step( CssSchedulable job, int delay );
}
