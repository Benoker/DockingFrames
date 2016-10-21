/*
 * Bibliothek - DockingFrames
 * Library built on Java/Swing, allows the user to "drag and drop"
 * panels containing any Swing-Component the developer likes to add.
 * 
 * Copyright (C) 2007 Benjamin Sigg
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

package bibliothek.gui.dock.event;

import java.awt.Component;

import bibliothek.gui.Dockable;
import bibliothek.gui.dock.control.focus.FocusController;
import bibliothek.gui.dock.title.DockTitle;

/**
 * This listener is added to the {@link FocusController} and allows to
 * interrupt a change of the focus. This listener stop focus transfer
 * if the transfer is initialized by the framework itself. If the client calls
 * a method like {@link Component#requestFocusInWindow()} then the framework
 * may not be able to prevent the focus change from happening.
 * @author Benjamin Sigg
 */
public interface FocusVetoListener {
	/**
	 * Tells how to react on a potential change of the focus.
	 * @author Benjamin Sigg
	 */
	public static enum FocusVeto{
		/** No veto, allows the focus to be transferred to a new element */
		NONE,
		
		/** 
		 * Focus transfer is canceled, and the event leading to the transfer
		 * is consumed (meaning: the event is marked as invalid and components
		 * will not process it).
		 */
		VETO,
		
		/**
		 * Focus transfer is canceled, but the event leading to the transfer is 
		 * allowed to be processed. This behavior can lead to components wrongly
		 * process the event and make hide a {@link Dockable} that should
		 * have the focus. Client code should in general use {@link #VETO}.
		 */
		VETO_NO_CONSUME
	}
	
    /**
     * Invoked when the focus should change because the user did something
     * with <code>title</code>.
     * @param controller the controller who will change the focus
     * @param title the title from which the focus-change was initialized
     * @return whether to cancel the focus transfer, not <code>null</code>
     */
    public FocusVeto vetoFocus( FocusController controller, DockTitle title );
    
    /**
     * Invoked when the focus should change because the user did something
     * with <code>dockable</code>.
     * @param controller the controller who will change the focus
     * @param dockable the {@link Dockable} from which the focus-change was initialized
     * @return whether to cancel the focus transfer, not <code>null</code> 
     */
    public FocusVeto vetoFocus( FocusController controller, Dockable dockable );
}
