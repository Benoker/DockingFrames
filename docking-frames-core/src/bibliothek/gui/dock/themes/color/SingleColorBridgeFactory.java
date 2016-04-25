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

import bibliothek.gui.dock.themes.ColorBridgeFactory;
import bibliothek.gui.dock.util.color.ColorBridge;
import bibliothek.gui.dock.util.color.ColorManager;

/**
 * A {@link ColorBridgeFactory} that always returns the same instance
 * of {@link ColorBridge}.
 * @author Benjamin Sigg

 */
public class SingleColorBridgeFactory implements ColorBridgeFactory{
    private ColorBridge bridge;
    
    /**
     * Creates a new factory.
     * @param bridge the provider which will be returned at {@link #create(ColorManager)}
     */
    public SingleColorBridgeFactory( ColorBridge bridge ){
        if( bridge == null )
            throw new IllegalArgumentException( "bridge must not be null" );
        
        this.bridge = bridge;
    }
    
    public ColorBridge create( ColorManager manager ) {
        return bridge;
    }
}
