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
public abstract class AbstractDockColor implements DockColor {
    /** the value set by a client */
    private Color override;
    /** the value set by a {@link ColorManager} */
    private Color value;
    /** the backup value for emergencies */
    private Color backup;
    
    /** the id for which this {@link DockColor} should listen */
    private String id;
    /** the kind of this {@link DockColor} */
    private Class<?> kind;
    /** the current owner of this {@link DockColor} */
    private ColorManager manager;
    

    /**
     * Creates a new {@link DockColor}.
     * @param id the id of the color for which <code>this</code> should listen
     */
    public AbstractDockColor( String id ){
        this( id, null, null );
    }
    
    /**
     * Creates a new {@link DockColor}.
     * @param id the id of the color for which <code>this</code> should listen
     * @param kind the kind of {@link DockColor} this is
     */
    public AbstractDockColor( String id, Class<? extends DockColor> kind ){
        this( id, kind, null );
    }

    /**
     * Creates a new {@link DockColor}.
     * @param id the id of the color for which <code>this</code> should listen
     * @param backup a backup color, can be <code>null</code>
     */
    public AbstractDockColor( String id, Color backup ){
        this( id, null, backup );
    }
    
    /**
     * Creates a new {@link DockColor}.
     * @param id the id of the color for which <code>this</code> should listen
     * @param kind the kind of {@link DockColor} this is, can be <code>null</code>
     * @param backup a backup color, can be <code>null</code>
     */
    public AbstractDockColor( String id, Class<? extends DockColor> kind, Color backup ){
        if( id == null )
            throw new IllegalArgumentException( "id must no be null" );
        
        this.id = id;
        this.kind = kind == null ? getClass() : kind;
        this.backup = backup;
    }
    
    /**
     * Changes the identifier of this color.
     * @param id the new id, must not be <code>null</code>
     */
    @SuppressWarnings("unchecked")
    public void setId( String id ) {
        if( id == null )
            throw new IllegalArgumentException( "id must not be null" );
        
        this.id = id;
        if( this.manager != null ){
            this.manager.remove( this );
            this.manager.add( id, (Class<DockColor>)kind, this );
        }
    }
    
    /**
     * Gets the identifier of this color.
     * @return the identifier, never <code>null</code>
     */
    public String getId() {
        return id;
    }
    
    /**
     * Changes the kind of this color. The kind is used by the {@link ColorManager}
     * to find out, which {@link ColorProvider} should be used to interact with
     * this {@link DockColor}.
     * @param kind the new kind, not <code>null</code>. The kind should be
     * a class or interfaces that is implemented by this {@link DockColor}.
     */
    @SuppressWarnings("unchecked")
    public void setKind( Class<? extends DockColor> kind ) {
        if( kind == null )
            throw new IllegalArgumentException( "kind must not be null" );
        
        this.kind = kind;
        if( this.manager != null ){
            this.manager.remove( this );
            this.manager.add( id, (Class<DockColor>)kind, this );
        }
    }
    
    /**
     * Gets the kind of this color. See {@link #setKind(Class)}.
     * @return the kind, never <code>null</code>
     */
    @SuppressWarnings("unchecked")
    public Class<? extends DockColor> getKind() {
        return (Class<? extends DockColor>)kind;
    }
    
    /**
     * Sets the manager which owns this {@link DockColor}, automatically
     * add or removes this <code>DockColor</code> from the manager
     * @param manager the new manager, can be <code>null</code>
     */
    @SuppressWarnings("unchecked")
    public void setManager( ColorManager manager ){
        if( manager != this.manager ){
            if( this.manager != null )
                this.manager.remove( this );
            
            this.manager = manager;
            if( this.manager != null )
                this.manager.add( id, (Class<DockColor>)kind, this );
        }
    }
    
    /**
     * This method just calls {@link #setManager(ColorManager)} with the
     * <code>controller</code>s {@link ColorManager}. 
     * @param controller the owner of this {@link DockColor} or <code>null</code>
     */
    public void connect( DockController controller ){
        setManager( controller == null ? null : controller.getColors() );
    }
    
    public void set( Color color ) {
        Color oldColor = color();
        this.value = color;
        Color newColor = color();
        
        if( oldColor != newColor ){
            changed( oldColor, newColor );
        }
    }
    
    /**
     * Gets the first non-<code>null</code> value of the list
     * <code>override</code>, <code>value</code>, <code>backup</code>.
     * @return a color or <code>null</code>
     */
    public Color color(){
        if( override != null )
            return override;
        
        if( value != null )
            return value;
        
        return backup;
    }
    
    /**
     * Called when the color of this {@link AbstractDockColor} has changed
     * @param oldColor the old color, can be <code>null</code> 
     * @param newColor the new color, can be <code>null</code>
     */
    protected abstract void changed( Color oldColor, Color newColor );
    
    /**
     * Sets the override value.
     * @param value the new override or <code>null</code>
     */
    public void setValue( Color value ) {
        Color oldColor = color();
        this.override = value;
        Color newColor = color();
        
        if( oldColor != newColor ){
            changed( oldColor, newColor );
        }
    }
    
    /**
     * Gets the override value.
     * @return the override or <code>null</code>
     */
    public Color getValue() {
        return override;
    }
    
    /**
     * Sets the backup color.
     * @param backup the backup or <code>null</code>
     */
    public void setBackup( Color backup ) {
        Color oldColor = color();
        this.backup = backup;
        Color newColor = color();
        
        if( oldColor != newColor ){
            changed( oldColor, newColor );
        }
    }
    
    /**
     * Gets the backup color.
     * @return the backup or <code>null</code>
     */
    public Color getBackup() {
        return backup;
    }
}
