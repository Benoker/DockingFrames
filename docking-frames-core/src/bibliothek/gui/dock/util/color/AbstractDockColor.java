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
package bibliothek.gui.dock.util.color;

import java.awt.Color;

import bibliothek.gui.DockController;
import bibliothek.gui.dock.util.AbstractUIValue;
import bibliothek.gui.dock.util.UIProperties;
import bibliothek.util.Path;

/**
 * A {@link DockColor} that contains more than one {@link Color}:
 * <ul>
 * <li>override: is a value that can be set from outside and overrides all other values</li>
 * <li>value: is the value obtained through the {@link ColorManager}</li>
 * <li>backup: is a color used when all other colors are unavailable</li>
 * </ul><br>
 * This class also has methods to add or remove itself from a {@link ColorManager}.
 * @author Benjamin Sigg
 *
 */
public abstract class AbstractDockColor extends AbstractUIValue<Color, DockColor> implements DockColor {
    /**
     * Creates a new {@link DockColor}.
     * @param id the id of the color for which <code>this</code> should listen
     */
    public AbstractDockColor( String id ){
        super( id );
    }
    
    /**
     * Creates a new {@link DockColor}.
     * @param id the id of the color for which <code>this</code> should listen
     * @param kind the kind of {@link DockColor} this is
     */
    public AbstractDockColor( String id, Path kind ){
        super( id, kind );
    }
    
    /**
     * Creates a new {@link DockColor}.
     * @param id the id of the color for which <code>this</code> should listen
     * @param kind the kind of {@link DockColor} this is, can be <code>null</code>
     * @param backup a backup color, can be <code>null</code>
     */
    public AbstractDockColor( String id, Path kind, Color backup ){
        super( id, kind, backup );
    }
    
    @Override
    protected DockColor me() {
        return this;
    }
    
    /**
     * This method just calls {@link #setManager(UIProperties)} with the
     * <code>controller</code>s {@link ColorManager}. 
     * @param controller the owner of this {@link DockColor} or <code>null</code>
     */
    public void connect( DockController controller ){
        setManager( controller == null ? null : controller.getColors() );
    }
    
    /**
     * Gets the first non-<code>null</code> value of the list
     * <code>override</code>, <code>value</code>, <code>backup</code>.
     * @return a color or <code>null</code>
     */
    public Color color(){
        return value();
    }
}
