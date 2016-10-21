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
package bibliothek.util.workarounds;

import java.awt.Component;
import java.awt.Shape;
import java.awt.Window;

import bibliothek.gui.DockController;
import bibliothek.util.Workarounds;

/**
 * A {@link Workaround} provides code to workaround an issue that is only present in some versions of the
 * JRE or in some libraries.
 * @author Benjamin Sigg
 */
public interface Workaround {
	/**
	 * Called whenever a new {@link DockController} is created, the {@link Workaround} may modify the controller in 
	 * any way it likes (e.g. install specialized factories).
	 * @param controller the {@link DockController} which was just created and initialized
	 */
	public void setup( DockController controller );
	
	/**
	 * Called for any {@link Component} which is used as glass pane (as invisible panel).
	 * @param component the component that is invisible
	 */
	public void markAsGlassPane( Component component );
	
	/**
	 * Tells whether this {@link Workaround} has the ability to make <code>window</code> translucent.
	 * Translucent means that each pixel of the <code>window</code> can have its own alpha value.
	 * @param window the window to test
	 * @return whether translucency is an option
	 */
	public boolean supportsPerpixelTranslucency( Window window );
	
	/**
	 * Makes the window <code>window</code> translucent. See {@link Workarounds#setTranslucent(Window)} for a more
	 * detailed description.
	 * @param window the window that should be transparent
	 * @return whether translucency is supported for <code>window</code>
	 */
	public boolean setTranslucent( Window window );
	
	/**
	 * Tells whether this {@link Workaround} has the ability to make <code>window</code> transparent.
	 * Transparent means that some pixels of the <code>window</code> cannot be seen.
	 * @param window the window to test
	 * @return whether transparency is an option
	 */
	public boolean supportsPerpixelTransparency( Window window );
	
	/**
	 * Makes the window <code>window</code> transparent in all the regions that are not inside <code>shape</code>.
	 * @param window the window that should be transparent
	 * @param shape the shape of the window, or <code>null</code> if the window should not be transparent
	 * @return whether transparency is supported for <code>window</code>
	 */
	public boolean setTransparent( Window window, Shape shape );
}
