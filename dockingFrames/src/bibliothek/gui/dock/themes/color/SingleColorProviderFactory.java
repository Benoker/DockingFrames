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

import bibliothek.gui.dock.themes.ColorProviderFactory;
import bibliothek.gui.dock.util.color.ColorManager;
import bibliothek.gui.dock.util.color.ColorProvider;
import bibliothek.gui.dock.util.color.DockColor;

/**
 * A {@link ColorProviderFactory} that always returns the same instance
 * of {@link ColorProvider}.
 * @author Benjamin Sigg
 *
 * @param <D> the kind of {@link DockColor}s this factories child will handle
 */
public class SingleColorProviderFactory<D extends DockColor> implements ColorProviderFactory<D, ColorProvider<D>>{
    private ColorProvider<D> provider;
    
    /**
     * Creates a new factory.
     * @param provider the provider which will be returned at {@link #create(ColorManager)}
     */
    public SingleColorProviderFactory( ColorProvider<D> provider ){
        if( provider == null )
            throw new IllegalArgumentException( "provider must not be null" );
        
        this.provider = provider;
    }
    
    public ColorProvider<D> create( ColorManager manager ) {
        return provider;
    }
}
