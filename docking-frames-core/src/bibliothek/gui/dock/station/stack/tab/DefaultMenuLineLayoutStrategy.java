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
package bibliothek.gui.dock.station.stack.tab;

import bibliothek.gui.dock.station.stack.tab.layouting.Size;

/**
 * The default implementation of {@link MenuLineLayoutStrategy} provides a generic 
 * setting that should work for most cases.
 * @author Benjamin Sigg
 *
 */
public class DefaultMenuLineLayoutStrategy implements MenuLineLayoutStrategy{
	public double getScore( MenuLineLayoutPossibility possibility, Size menuSize, Size infoSize, Size tabSize ){
		double result = 0;
		if( menuSize == null ){
			result += 1;
		}
		else{
			result += menuSize.getScore();
		}
		
		if( infoSize != null ){
			result += 10*infoSize.getScore();
		}
		
		if( tabSize != null ){
			result += 4*tabSize.getScore();
		}
		return result / 15.0; 
	}
}
