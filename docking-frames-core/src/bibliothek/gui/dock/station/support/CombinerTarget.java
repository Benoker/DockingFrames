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
package bibliothek.gui.dock.station.support;

import java.awt.Component;
import java.awt.Graphics;
import java.awt.Rectangle;

import bibliothek.gui.DockTheme;
import bibliothek.gui.Dockable;
import bibliothek.gui.dock.displayer.DisplayerCombinerTarget;
import bibliothek.gui.dock.station.Combiner;
import bibliothek.gui.dock.station.DockableDisplayer;
import bibliothek.gui.dock.station.StationPaint;

/**
 * Created by a {@link Combiner}, this object tells how two {@link Dockable}s
 * are going to be merged.
 * @author Benjamin Sigg
 */
public interface CombinerTarget {
	/**
	 * Paints some lines on the screen that allow the user to understand of what is
	 * going to happen if he/she releases the mouse now.<br>
	 * Implementations interested in painting something in relation to the position of the mouse
	 * can use the following piece of code to get the mouse position:
	 * <pre><code>
	 * CombinerSource source = ... // the source that was used to create this target
	 * Point mouse = source.getMousePosition();
	 * if( mouse != null ){ 
	 *   mouse = SwingUtilities.convertPoint( source.getOld().getComponent(), mouse, component );</code></pre>	
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
	 * Some {@link Combiner}s may use the combining feature of {@link DockableDisplayer}s
	 * ({@link DockableDisplayer#prepareCombination(CombinerSource, Enforcement)})
	 * to combine some {@link Dockable}s. This method returns the information that was provided by the displayer.
	 * @return the information or <code>null</code>, <code>null</code> is always a valid result
	 */
	public DisplayerCombinerTarget getDisplayerCombination();
}
