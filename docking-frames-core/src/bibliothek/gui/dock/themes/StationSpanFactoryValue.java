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
package bibliothek.gui.dock.themes;

import bibliothek.gui.DockStation;
import bibliothek.gui.dock.station.span.Span;
import bibliothek.gui.dock.station.span.SpanCallback;
import bibliothek.gui.dock.station.span.SpanFactory;
import bibliothek.gui.dock.station.span.SpanMode;
import bibliothek.gui.dock.util.UIValue;
import bibliothek.util.Path;

/**
 * A {@link StationSpanFactoryValue} offers access to a {@link SpanFactory} that is read from a {@link ThemeManager}.
 * @author Benjamin Sigg
 */
public abstract class StationSpanFactoryValue extends StationThemeItemValue<SpanFactory> implements SpanFactoryValue, SpanFactory{
	/** What kind of {@link UIValue} this is */
	public static final Path KIND_STATION = SpanFactoryValue.KIND_SPAN_FACTORY.append( "station" );
	
	/**
	 * Creates a new value.
	 * @param id the identifier of this value, used to read a resource from the {@link ThemeManager}
	 * @param station the owner of this object
	 */
    public StationSpanFactoryValue( String id, DockStation station ){
    	super( id, KIND_STATION, ThemeManager.SPAN_FACTORY_TYPE, station );
    }
    
    @Override
    public void set( SpanFactory value ){
    	SpanFactory old = get();
    	super.set( value );
    	if( old != get() ){
    		changed();
    	}
    }
    
    /**
     * Called if the current {@link SpanFactory} changed.
     */
    protected abstract void changed();
    
    public Span create( SpanCallback callback ){
    	SpanFactory factory = get();
    	if( factory != null ){
    		return factory.create( callback );	
    	}
    	return new Span(){
			public void set( SpanMode mode ){
				// ignore	
			}
			
			public void mutate( SpanMode mode ){
				// ignore
			}
			
			public int getSize(){
				return 0;
			}
			
			public void configureSize( SpanMode mode, int size ){
				// ignore
			}
		};
    }
}
