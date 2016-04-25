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

package bibliothek.gui.dock.themes;

import bibliothek.gui.DockStation;
import bibliothek.gui.DockTheme;
import bibliothek.gui.DockUI;
import bibliothek.gui.Dockable;
import bibliothek.gui.dock.layout.location.AsideRequest;
import bibliothek.gui.dock.station.Combiner;
import bibliothek.gui.dock.station.support.CombinerSource;
import bibliothek.gui.dock.station.support.CombinerTarget;
import bibliothek.gui.dock.station.support.Enforcement;
import bibliothek.gui.dock.themes.basic.BasicCombiner;
import bibliothek.gui.dock.util.UIValue;
import bibliothek.util.Path;

/**
 * A <code>StationCombinerValue</code> encloses a {@link Combiner} and uses
 * the combiner as delegate. If the wrapper has no delegate, it uses
 * the {@link DockUI} to get a combiner from the current {@link DockTheme}.
 * @author Benjamin Sigg
 *
 */
public class StationCombinerValue extends StationThemeItemValue<Combiner> implements CombinerValue, Combiner {
	/** What kind of {@link UIValue} this is */
	public static final Path KIND_STATION = CombinerValue.KIND_COMBINER.append( "station" );
	
	/**
	 * Creates a new value.
	 * @param id the identifier of this value, used to read a resource from the {@link ThemeManager}
	 * @param station the owner of this object
	 */
    public StationCombinerValue( String id, DockStation station ){
    	super( id, KIND_STATION, ThemeManager.COMBINER_TYPE, station );
    }

    public CombinerTarget prepare( CombinerSource source, Enforcement force ){
    	Combiner combiner = get();
    	if( combiner == null ){
    		if( force.getForce() > 0.5f ){
    			combiner = new BasicCombiner();
    		}
    		else{
    			return null;
    		}
    	}
    	
    	return combiner.prepare( source, force );
    }

    public Dockable combine( CombinerSource source, CombinerTarget target ){
    	Combiner combiner = get();
    	if( combiner == null ){
   			combiner = new BasicCombiner();
    	}

        return combiner.combine( source, target );
    }
    
    public void aside( AsideRequest request ){
    	Combiner combiner = get();
    	if( combiner == null ){
    		combiner = new BasicCombiner();
    	}
    	combiner.aside( request );
    }
}
