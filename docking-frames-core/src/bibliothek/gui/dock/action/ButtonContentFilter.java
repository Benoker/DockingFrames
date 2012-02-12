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
package bibliothek.gui.dock.action;

import bibliothek.gui.Dockable;

/**
 * This filter decides for {@link DockAction}s whether their text is shown on buttons.
 * @author Benjamin Sigg
 * @see DockAction#BUTTON_CONTENT_FILTER
 */
public interface ButtonContentFilter {
	/**
	 * A filter whose {@link #showText(Dockable, DockAction)} method always returns <code>false</code>.
	 */
	public static final ButtonContentFilter NEVER = new ButtonContentFilter(){
		public void uninstall( Dockable dockable, DockAction action ){
			// ignore
		}
		
		public boolean showText( Dockable dockable, DockAction action ){
			return false;
		}
		
		public void removeListener( ButtonContentFilterListener listener ){
			// ignore
		}
		
		public void install( Dockable dockable, DockAction action ){
			// ignore
		}
		
		public void addListener( ButtonContentFilterListener listener ){
			// ignore
		}
	};
	
	/**
	 * A filter whose {@link #showText(Dockable, DockAction)} method always returns <code>true</code>.
	 */
	public static final ButtonContentFilter ALWAYS = new ButtonContentFilter(){
		public void uninstall( Dockable dockable, DockAction action ){
			// ignore
		}
		
		public boolean showText( Dockable dockable, DockAction action ){
			return true;
		}
		
		public void removeListener( ButtonContentFilterListener listener ){
			// ignore
		}
		
		public void install( Dockable dockable, DockAction action ){
			// ignore
		}
		
		public void addListener( ButtonContentFilterListener listener ){
			// ignore
		}
	};
	
	/**
	 * Tells whether the text of <code>action</code>, that is associated with <code>dockable</code>, is to be shown.
	 * @param dockable the {@link Dockable} with which <code>action</code> is connected
	 * @param action the action whose text may be shown
	 * @return <code>true</code> if the text is to be shown, <code>false</code> otherwise
	 */
	public boolean showText( Dockable dockable, DockAction action );
	
	/**
	 * Informs this filter that <code>action</code> which is connected to <code>dockable</code> will
	 * be shown. This method may be called more then once, if the action is shown in more than one
	 * place.
	 * @param dockable the element whose action is going to show up
	 * @param action the action that is going to show up
	 */
	public void install( Dockable dockable, DockAction action );
	
	/**
	 * Informs this filter that <code>action</code> will no longer be shown. This method may be called
	 * more than once if <code>action</code> is shown at more than one place.
	 * @param dockable the element whose action is going to be hidden
	 * @param action the action that is no longer showing up
	 */
	public void uninstall( Dockable dockable, DockAction action );
	
	/**
	 * Adds the observer <code>listener</code> to this filter. The listener must be called if
	 * the result of {@link #showText(Dockable, DockAction)} changes.
	 * @param listener the new listener
	 */
	public void addListener( ButtonContentFilterListener listener );
	
	/**
	 * Removes the observer <code>listener</code> from this filter.
	 * @param listener the observer that is no longer shown
	 */
	public void removeListener( ButtonContentFilterListener listener );
}
