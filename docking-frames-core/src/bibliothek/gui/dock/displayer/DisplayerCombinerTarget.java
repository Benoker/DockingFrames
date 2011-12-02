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
package bibliothek.gui.dock.displayer;

import java.awt.Component;
import java.awt.Graphics;
import java.awt.Rectangle;

import bibliothek.gui.DockStation;
import bibliothek.gui.DockTheme;
import bibliothek.gui.Dockable;
import bibliothek.gui.dock.DockElement;
import bibliothek.gui.dock.station.DockableDisplayer;
import bibliothek.gui.dock.station.StationPaint;
import bibliothek.gui.dock.station.support.CombinerSource;

/**
 * Created by a {@link DockableDisplayer}, tells how exactly a {@link Dockable} is dropped
 * over a {@link DockableDisplayer}. 
 * @author Benjamin Sigg
 */
public interface DisplayerCombinerTarget {
	/**
	 * Allows this operation to paint some markings onto the screen.
	 * @param g the graphics context to use
	 * @param component the component on which <code>g</code> paints.
	 * @param paint painting algorithms fitting to the current {@link DockTheme}
	 * @param stationBounds an estimate of the area which will be affected by inserting
	 * the combined {@link Dockable}, not <code>null</code>
	 * @param dockableBounds the boundaries which a representation of the combined {@link Dockable}
	 * has. If possible this is the exact location and size, including any title. If the
	 * station cannot exactly tell where the {@link Dockable} is going to be 
	 * (e.g. when {@link CombinerSource#getSize()} returned <code>null</code>), then
	 * this may be the location and size of a title. Never <code>null</code>. 
	 */
	public void paint( Graphics g, Component component, StationPaint paint, Rectangle stationBounds, Rectangle dockableBounds );
	
	/**
	 * Executes this operation. Usually that means to invoke some method like
	 * {@link DockStation#drag(Dockable)}.<br>
	 * @param source the source of information to use for the execution, this may not be the same object as was used to create
	 * this {@link DisplayerCombinerTarget}.
	 * @throws IllegalStateException if the tree of {@link DockElement}s changed or if this method was already called.
	 * @return the replacement {@link Dockable} for the old item
	 */
	public Dockable execute( CombinerSource source );
}
