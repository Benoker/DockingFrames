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
package bibliothek.gui.dock.common.intern;

import java.awt.Container;

import javax.swing.Icon;

import bibliothek.gui.dock.common.action.FAction;
import bibliothek.gui.dock.common.event.FDockableListener;
import bibliothek.gui.dock.common.intern.action.FSeparator;

/**
 * An {@link FDockable} that uses a {@link DefaultFacileDockable} to show
 * its content.
 * @author Benjamin Sigg
 */
public class DefaultFDockable extends AbstractFDockable{
    /** whether this dockable can be minimized */
    private boolean minimizable;
    /** whether this dockable can be maximized */
    private boolean maximizable;
    /** whether this dockable can be put into a dialog */
    private boolean externalizable;
    /** whether this dockable can be closed by the user */
    private boolean closeable;
    /** whether this dockable can be combined with other dockables */
    private boolean stackable;
    
    /** the graphical representation of this dockable */
    private DefaultFacileDockable dockable;
    
    /**
     * Creates a new dockable
     */
    public DefaultFDockable(){
        super( null );
        dockable = new DefaultFacileDockable( this );
        init( dockable );
        
        setMinimizable( true );
        setMaximizable( true );
        setExternalizable( true );
        setStackable( true );
    }
    
    
    /**
     * Gets the container on which the client can pack its components.
     * @return the panel showing the content
     */
    public Container getContentPane(){
        return dockable.getContentPane();
    }
    
    /**
     * Sets the text that is shown as title.
     * @param text the title
     */
    public void setTitleText( String text ){
        dockable.setTitleText( text );
    }
    
    /**
     * Gets the text that is shown as title.
     * @return the title
     */
    public String getTitleText(){
        return dockable.getTitleText();
    }
    
    /**
     * Sets the icon that is shown in the title of this <code>FDockable</code>.
     * @param icon the title-icon
     */
    public void setTitleIcon( Icon icon ){
        dockable.setTitleIcon( icon );
    }
    
    /**
     * Gets the icon that is shown in the title.
     * @return the title-icon, might be <code>null</code>
     */
    public Icon getTitleIcon(){
        return dockable.getTitleIcon();
    }
    
    public boolean isMinimizable(){
        return minimizable;
    }
    
    /**
     * Sets whether the user can minimize this dockable.
     * @param minimizable <code>true</code> if the user can minimize this element
     */
    public void setMinimizable( boolean minimizable ){
        if( this.minimizable != minimizable ){
            this.minimizable = minimizable;
            
            for( FDockableListener listener : listeners() )
                listener.minimizableChanged( this );
            
            FControlAccess control = control();
            if( control != null ){
                control.getStateManager().rebuild( dockable );
                control.getStateManager().ensureValidLocation( this );
            }
        }
    }

    public boolean isMaximizable(){
        return maximizable;
    }
    
    /**
     * Sets whether the user can maximize this dockable.
     * @param maximizable <code>true</code> if the user can maximize this element
     */
    public void setMaximizable( boolean maximizable ){
        if( this.maximizable != maximizable ){
            this.maximizable = maximizable;
            
            for( FDockableListener listener : listeners() )
                listener.maximizableChanged( this );
            
            FControlAccess control = control();
            if( control != null ){
                control.getStateManager().rebuild( dockable );
                control.getStateManager().ensureValidLocation( this );
            }
        }
    }
    
    public boolean isExternalizable(){
        return externalizable;
    }
    
    /**
     * Sets whether the user can externalize this dockable.
     * @param externalizable <code>true</code> if the user can externalize this element
     */
    public void setExternalizable( boolean externalizable ){
        if( this.externalizable != externalizable ){
            this.externalizable = externalizable;
            
            for( FDockableListener listener : listeners() )
                listener.externalizableChanged( this );
            
            FControlAccess control = control();
            if( control != null ){
                control.getStateManager().rebuild( dockable );
                control.getStateManager().ensureValidLocation( this );
            }
        }
    }
    
    @Override
    public boolean isCloseable(){
        return closeable;
    }
    
    /**
     * Sets whether this element can be combined with other dockable to create
     * a stack. Note that this property is ignored if this element is already
     * in a stack.
     * @param stackable <code>true</code> if this element can be combined.
     */
    public void setStackable( boolean stackable ) {
        this.stackable = stackable;
    }
    
    public boolean isStackable() {
        return stackable;
    }
    
    /**
     * Sets whether the user can close this dockable.
     * @param closeable <code>true</code> if the user can close this element
     */
    public void setCloseable( boolean closeable ){
        if( this.closeable != closeable ){
            this.closeable = closeable;     
            updateClose();
            for( FDockableListener listener : listeners() )
                listener.closeableChanged( this );
        }
    }

    /**
     * Adds an action to this dockable. The action will be shown in the
     * popup-menu which belongs to this dockable, and also as button in some titles
     * of this dockable.
     * @param action the new action
     */
    public void addAction( FAction action ){
        dockable.getActions().add( action.intern() );
    }
    
    /**
     * Adds a new action to this dockable.
     * @param index the location of the action
     * @param action the action
     * @see #addAction(FAction)
     */
    public void insertAction( int index, FAction action ){
        dockable.getActions().add( index, action.intern() );
    }
    
    /**
     * Adds a separator to the list of {@link FAction}s of this dockable.
     */
    public void addSeparator(){
        addAction( FSeparator.SEPARATOR );
    }
    
    /**
     * Adds a separator to the list of {@link FAction}s of this dockable.
     * @param index the location of the action
     */
    public void insertSeparator( int index ){
        insertAction( index, FSeparator.SEPARATOR );
    }
    
    /**
     * Removes an action from this dockable
     * @param index the location of the action
     */
    public void removeAction( int index ){
        dockable.getActions().remove( index );
    }
    
    /**
     * Removes an action from this dockable.
     * @param action the action to remove
     */
    public void removeAction( FAction action ){
        dockable.getActions().remove( action.intern() );
    }
    
    @Override
    public DefaultFacileDockable intern() {
        return dockable;
    }
}
