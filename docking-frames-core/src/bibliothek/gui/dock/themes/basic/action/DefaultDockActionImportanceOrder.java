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
package bibliothek.gui.dock.themes.basic.action;

import java.util.Arrays;
import java.util.Comparator;

import bibliothek.gui.dock.action.DockAction;

/**
 * This class orders {@link DockAction}s depending on the annotation {@link DockActionImportance}. It
 * can be subclassed in order to override the default behavior.
 * @author Benjamin Sigg
 */
public class DefaultDockActionImportanceOrder implements DockActionImportanceOrder{
	public void order( DockAction[] actions ){
		Arrays.sort( actions, new Comparator<DockAction>(){
			public int compare( DockAction a, DockAction b ){
				double impA = getImportance( a );
				double impB = getImportance( b );
				if( impA > impB ){
					return -1;
				}
				else if( impA < impB ){
					return 1;
				}
				else{
					return 0;
				}
			}
		});
	}

	/**
	 * Tells how important <code>action</code> is. The default implementation just searches for
	 * a {@link DockActionImportance}, but subclasses may change the behavior.
	 * @param action the action whose importance is searched
	 * @return the importance, the default value is 1.0
	 */
	protected double getImportance( DockAction action ){
		DockActionImportance importance = action.getClass().getAnnotation( DockActionImportance.class );
		if( importance == null ){
			return 1.0f;
		}
		else{
			return importance.value();
		}
	}
}
