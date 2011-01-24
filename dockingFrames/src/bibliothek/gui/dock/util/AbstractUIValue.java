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
package bibliothek.gui.dock.util;

import bibliothek.gui.dock.util.color.AbstractDockColor;
import bibliothek.util.Path;

/**
 * An abstract implementation of {@link UIValue}. This class contains more than
 * just one resource:
 * <ul>
 * <li>override: is a value that can be set from outside and overrides all other values</li>
 * <li>value: is the value obtained through the {@link UIProperties}</li>
 * <li>backup: is a value used when all other values are unavailable</li>
 * </ul><br>
 * This class also has methods to add or remove itself from a {@link UIProperties}. 
 * 
 * @author Benjamin Sigg
 * @param <V> the kind of values this {@link UIValue} handles
 * @param <U> the kind of {@link UIValue} that the {@link UIProperties} will handle.
 * This class is either abstract, or a subclass of <code>U</code>.
 */
public abstract class AbstractUIValue<V, U extends UIValue<V>> implements UIValue<V> {
    /** the value set by a client */
    private V override;
    /** the value set by a {@link UIProperties} */
    private V value;
    /** the backup value for emergencies */
    private V backup;
    
    /** the id for which this {@link UIValue} should listen */
    private String id;
    /** the kind of this {@link UIValue} */
    private Path kind;
    /** the current owner of this {@link UIValue} */
    private UIProperties<V, U, ?> manager;
    
    /** an override value of <code>null</code> is returned by {@link #value()} */
    private boolean overrideNull = false;

    /**
     * Creates a new {@link UIValue}.
     * @param id the id of the resource for which <code>this</code> should listen for
     */
    public AbstractUIValue( String id ){
        this( id, null, null );
    }
    
    /**
     * Creates a new {@link UIValue}.
     * @param id the id of the resource for which <code>this</code> should listen for
     * @param kind the kind of {@link UIValue} this is
     */
    public AbstractUIValue( String id, Path kind ){
        this( id, kind, null );
    }

    /**
     * Creates a new {@link UIValue}.
     * @param id the id of the resource for which <code>this</code> should listen for
     * @param backup a backup resource, can be <code>null</code>
     */
    public AbstractUIValue( String id, V backup ){
        this( id, null, backup );
    }
    
    /**
     * Creates a new {@link UIValue}.
     * @param id the id of the resource for which <code>this</code> should listen for
     * @param kind the kind of {@link UIValue} this is, can be <code>null</code>
     * @param backup a backup resource, can be <code>null</code>
     */
    public AbstractUIValue( String id, Path kind, V backup ){
        if( id == null )
            throw new IllegalArgumentException( "id must no be null" );
        
        if( kind == null )
            throw new IllegalArgumentException( "kind must not be null" );
        
        this.id = id;
        this.kind = kind;
        this.backup = backup;
    }
    
    /**
     * Returns <code>this</code>. This method can only be implemented when
     * the generic boundaries are met, so this methods ensures that <code>this</code>
     * is really an <code>U</code>, or <code>abstract</code>.
     * @return <code>this</code>
     */
    protected abstract U me();
    
    /**
     * Changes the identifier of this value.
     * @param id the new id, must not be <code>null</code>
     */
    public void setId( String id ) {
        if( id == null )
            throw new IllegalArgumentException( "id must not be null" );
        
        this.id = id;
        if( this.manager != null ){
            U me = me();
            this.manager.remove( me );
            this.manager.add( id, kind, me );
        }
    }
    
    /**
     * Gets the identifier of this value.
     * @return the identifier, never <code>null</code>
     */
    public String getId() {
        return id;
    }
    
    /**
     * Changes the kind of this value. The kind is used by the {@link UIProperties}
     * to find out, which {@link UIBridge} should be used to interact with
     * this {@link UIValue}.
     * @param kind the new kind, not <code>null</code>. The kind should be
     * a class or interfaces that is implemented by this {@link UIValue}.
     */
    public void setKind( Path kind ) {
        if( kind == null )
            throw new IllegalArgumentException( "kind must not be null" );
        
        this.kind = kind;
        if( this.manager != null ){
            U me = me();
            this.manager.remove( me );
            this.manager.add( id, kind, me );
        }
    }
    
    /**
     * Gets the kind of this value. See {@link #setKind(Path)}.
     * @return the kind, never <code>null</code>
     */
    public Path getKind() {
        return kind;
    }
    
    /**
     * Sets the manager which owns this {@link UIValue}, <code>this</code> will
     * automatically be added or removed from the current <code>manager</code>.
     * @param manager the new manager, can be <code>null</code>
     */
    public void setManager( UIProperties<V, U, ?> manager ){
        if( manager != this.manager ){
            U me = me();
            if( this.manager != null )
                this.manager.remove( me );
            
            this.manager = manager;
            
            if( this.manager != null )
                this.manager.add( id, kind, me );
        }
    }

    
    public void set( V value ) {
        V oldValue = value();
        this.value = value;
        V newValue = value();
        
        if( oldValue != newValue ){
            changed( oldValue, newValue );
        }
    }
    
    /**
     * Updates the value of this {@link UIValue} without actually installing <code>this</code>
     * on <code>manager</code>.
     * @param manager the manager from which to read the value
     */
    public void update( UIProperties<V, U, ?> manager ){
    	manager.get( id, kind, me() );
    }
    
    /**
     * Gets the first non-<code>null</code> value of the list
     * <code>override</code>, <code>value</code>, <code>backup</code>.
     * @return a resource or <code>null</code>
     */
    public V value(){
        if( overrideNull || override != null )
            return override;
        
        if( value != null )
            return value;
        
        return backup;
    }
    
    /**
     * Called when the resource of this {@link AbstractDockColor} has changed
     * @param oldValue the old value, can be <code>null</code> 
     * @param newValue the new value, can be <code>null</code>
     */
    protected abstract void changed( V oldValue, V newValue );
    
    /**
     * Sets the override value.
     * @param value the new override or <code>null</code>
     */
    public void setValue( V value ) {
    	setValue( value, false );
    }
    
    /**
     * Sets the override value. Please note that some modules won't work properly if {@link #value()} returns
     * <code>null</code>, use <code>forceNull</code> with care.
     * @param value the new value, can be <code>null</code>
     * @param forceNull if <code>true</code> and <code>value</code> is <code>null</code>, then
     * the result of {@link #value()} is <code>null</code> too
     */
    public void setValue( V value, boolean forceNull ){
        V oldValue = value();
        this.override = value;
        this.overrideNull = forceNull;
        V newValue = value();
        
        if( oldValue != newValue ){
            changed( oldValue, newValue );
        }
    }
    
    /**
     * Gets the override value.
     * @return the override or <code>null</code>
     */
    public V getValue() {
        return override;
    }
    
    /**
     * Sets the backup value.
     * @param backup the backup or <code>null</code>
     */
    public void setBackup( V backup ) {
        V oldColor = value();
        this.backup = backup;
        V newColor = value();
        
        if( oldColor != newColor ){
            changed( oldColor, newColor );
        }
    }
    
    /**
     * Gets the backup value.
     * @return the backup or <code>null</code>
     */
    public V getBackup() {
        return backup;
    }

}
