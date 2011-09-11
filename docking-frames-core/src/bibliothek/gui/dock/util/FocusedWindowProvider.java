/*
 * Bibliothek - DockingFrames
 * Library built on Java/Swing, allows the user to "drag and drop"
 * panels containing any Swing-Component the developer likes to add.
 * 
 * Copyright (C) 2011 Benjamin Sigg
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
package bibliothek.gui.dock.util;

import java.awt.Window;
import java.awt.event.WindowEvent;
import java.awt.event.WindowFocusListener;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

/**
 * A {@link WindowProvider} monitoring a set of windows, the last window that had the focus
 * is the selected window.
 * @author Benjamin Sigg
 */
public class FocusedWindowProvider extends AbstractWindowProvider{
	private List<Window> history = new LinkedList<Window>();
	private Set<Window> monitored = new HashSet<Window>();
	private Window current;
	
	private WindowFocusListener listener = new WindowFocusListener(){
		public void windowLostFocus( WindowEvent e ){
			// ignore	
		}
		
		public void windowGainedFocus( WindowEvent e ){
			current = e.getWindow();
			fireWindowChanged( current );
			history.remove( current );
			history.add( current );
		}
	};
	
	public Window searchWindow(){
		return current;
	}
	
	public void add( Window window ){
		if( monitored.add( window ) ){
			window.addWindowFocusListener( listener );
			if( window.isActive() ){
				current = window;
				fireWindowChanged( window );
				history.add( current );
			}
			else{
				history.add( 0, window );
			}
		}
	}
	
	public void remove( Window window ){
		monitored.remove( window );
		history.remove( window );
		window.removeWindowFocusListener( listener );
		if( current == window ){
			current = null;
			if( history.size() > 0 ){
				current = history.get( history.size()-1 );
			}
			fireWindowChanged( current );
		}
	}
}
