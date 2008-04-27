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
package bibliothek.gui.dock.themes.color;

import java.awt.Color;
import java.util.HashMap;
import java.util.Map;

import bibliothek.gui.dock.themes.ColorProviderFactory;
import bibliothek.gui.dock.util.MultiUIBridge;
import bibliothek.gui.dock.util.UIBridge;
import bibliothek.gui.dock.util.color.ColorManager;
import bibliothek.gui.dock.util.color.DockColor;

/**
 * A factory for a {@link MultiUIBridge}, this factory can contain other
 * factories to fill up the new <code>MultiUIBridge</code>.
 * @author Benjamin Sigg
 *
 * @param <D> the kind of {@link DockColor}s the created {@link UIBridge} will handle
 */
public class MultiColorProviderFactory<D extends DockColor> implements ColorProviderFactory<D, MultiUIBridge<Color, D>> {
    /** the set of factories that will create a child of the MultiColorProvider */
    private Map<String, ColorProviderFactory<? super D, ? extends UIBridge<Color, D>>> factories =
        new HashMap<String, ColorProviderFactory<? super D,? extends UIBridge<Color, D>>>();
    
    /**
     * Sets the factory of a child of the {@link MultiUIBridge} which will
     * be created by this factory.
     * @param key the name of the child
     * @param provider the child or <code>null</code>
     */
    public void put( String key, ColorProviderFactory<? super D, ? extends UIBridge<Color, D>> provider ){
        if( provider == null )
            factories.remove( key );
        else
            factories.put( key, provider );
    }
    
    public MultiUIBridge<Color, D> create( ColorManager manager ) {
        MultiUIBridge<Color, D> multi = new MultiUIBridge<Color, D>( manager );
        
        for( Map.Entry<String, ColorProviderFactory<? super D, ? extends UIBridge<Color, D>>> entry : factories.entrySet()){
            multi.put( entry.getKey(), entry.getValue().create( manager ) );
        }
        
        return multi;
    }
}
