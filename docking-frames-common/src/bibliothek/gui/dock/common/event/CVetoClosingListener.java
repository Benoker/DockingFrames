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
package bibliothek.gui.dock.common.event;

import bibliothek.gui.dock.common.CControl;
import bibliothek.gui.dock.common.CWorkingArea;
import bibliothek.gui.dock.common.MultipleCDockable;
import bibliothek.gui.dock.common.MultipleCDockableFactory;
import bibliothek.gui.dock.common.intern.CDockable;

/**
 * This listener can be called before a {@link CDockable} or a set of {@link CDockable}s are closed. Subclasses
 * can fire a veto indicating that some {@link CDockable} must not yet be closed.<br>
 * Please note:
 * <ul>
 * 	<li>It may not always be possible to cancel a closing-operation.</li>
 * 	<li>There is no guarantee that {@link #closing(CVetoClosingEvent)} is called before {@link #closed(CVetoClosingEvent)}</li>
 * 	<li>The event-object passed to {@link #closing(CVetoClosingEvent)} and {@link #closed(CVetoClosingEvent)} may not be equal, even if they describe the same event</li>
 * 	<li>Events may be split: if {@link #closing(CVetoClosingEvent)} was called once and handled three dockables, then {@link #closed(CVetoClosingEvent)} may be called three times to handle one dockable</li>
 * 	<li> {@link MultipleCDockable}s that are not children of a {@link CWorkingArea} are closed and re-opened if the layout changes ({@link CControl#load(String)}). This may be
 *  prevented with a correct implementation of {@link MultipleCDockableFactory#match(MultipleCDockable, bibliothek.gui.dock.common.MultipleCDockableLayout) MultipleCDockableFactory.match}. </li>
 * </ul>
 * @author Benjamin Sigg
 * @see CControl#addVetoClosingListener(CVetoClosingListener)
 * @see CControl#removeVetoClosingListener(CVetoClosingListener)
 * @see CDockable#addVetoClosingListener(CVetoClosingListener)
 * @see CDockable#removeVetoClosingListener(CVetoClosingListener)
 */
public interface CVetoClosingListener {
	/**
	 * Called before a set of {@link CDockable}s gets closed. This method may be invoked
	 * with events that are already canceled, check the {@link CVetoClosingEvent#isCanceled()} 
	 * property. 
	 * @param event the event that will happen but may be canceled
	 */
	public void closing( CVetoClosingEvent event );
	
	/**
	 * Called after a set of {@link CDockable}s has been closed. This
	 * method may be called without {@link #closing(CVetoClosingEvent)} been
	 * called beforehand.
	 * @param event the event that has already happened
	 */
	public void closed( CVetoClosingEvent event );
}
