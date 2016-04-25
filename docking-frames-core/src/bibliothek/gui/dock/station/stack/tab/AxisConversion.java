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
package bibliothek.gui.dock.station.stack.tab;

import java.awt.Dimension;
import java.awt.Rectangle;

/**
 * An axis conversion transforms coordinates and sizes from one to another
 * system. In general:
 * <ul>
 *   <li>view are coordinates that are seen by the user</li>
 *   <li>model are coordinates in which calculations are performed</li>
 * </ul>
 * @author Benjamin Sigg
 */
public interface AxisConversion {
	/**
	 * Converts <code>size</code> from model to view.
	 * @param size some dimension in the model, not <code>null</code>
	 * @return the converted dimension in the view, must not be <code>null</code> 
	 * nor <code>size</code>
	 */
	public Dimension modelToView( Dimension size );
	
	/**
	 * Converts <code>size</code> from view to model.
	 * @param size some dimension in the view, not <code>null</code>
	 * @return the converted dimension in the model, must not be 
	 * <code>null</code> nor <code>size</code>
	 */
	public Dimension viewToModel( Dimension size );
	
	/**
	 * Converts <code>bounds</code> from model to view.
	 * @param bounds some boundaries given in the model, not <code>null</code>
	 * @return the converted boundaries in the view, must not be <code>null</code>
	 * nor <code>bounds</code>
	 */
	public Rectangle modelToView( Rectangle bounds );
	
	/**
	 * Converts <code>bounds</code> from view to model.
	 * @param bounds some boundaries given in the view, not <code>null</code>
	 * @return the converted boundaries in the model, must not be <code>null</code>
	 * nor <code>bounds</code>
	 */
	public Rectangle viewToModel( Rectangle bounds );
}
