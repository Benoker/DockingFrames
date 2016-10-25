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

import java.awt.Component;
import java.awt.Container;
import java.awt.LayoutManager;

import javax.swing.Icon;

import bibliothek.gui.dock.common.CStation;
import bibliothek.gui.dock.common.action.CAction;
import bibliothek.gui.dock.common.action.CSeparator;
import bibliothek.gui.dock.dockable.IconHandling;
import bibliothek.util.FrameworkOnly;

/**
 * An {@link CDockable} that uses a {@link DefaultCommonDockable} to show
 * its content.<br>
 * Subclasses may override {@link #createCommonDockable()} to provide a custom subclass
 * of {@link DefaultCommonDockable}, note that {@link #createCommonDockable()} is called
 * as soon as the internal representation is required, e.g. for setting a property like
 * the title or the icon.
 * @author Benjamin Sigg
 */
@FrameworkOnly
public class DefaultCDockable extends AbstractCDockable{
    /**
     * Describes what the user can do with the enclosing {@link DefaultCDockable}.<br>
     * A name like <code>X_Y</code> tells, that feature <code>X</code> and
     * feature <code>Y</code> are available. The features are:
     * <ul>
     *  <li>MIN: whether the dockable can be minimized</li>
     *  <li>MAX: whether the dockable can be maximized</li>
     *  <li>EXT: whether the dockable can be externalized</li>
     *  <li>STACK: whether the dockable can be combined with other dockables, this
     *  feature normally should be allowed.</li>
     *  <li>CLOSE: whether the dockable can be closed by the user through an action 
     *  (normally a "x" in the right corner of the title)</li>
     * </ul>
     * @author Benjamin Sigg
     */
    public static enum Permissions{
        /** no permissions at all */
        NONE                    ( false, false, false, false, false ),
        /** all permissions */
        ALL                     ( true, true, true, true, true ),
        /** all permissions except close */
        DEFAULT                 ( true, true, true, true, false ),
        
        MIN                     ( true, false, false, false, false ),
        MAX                     ( false, true, false, false, false ),
        EXT                     ( false, false, true, false, false ),
        STACK                   ( false, false, false, true, false ),
        CLOSE                   ( false, false, false, false, true ),
        
        MIN_MAX                 ( true, true, false, false, false ),
        MIN_EXT                 ( true, false, true, false, false ),
        MIN_STACK               ( true, false, false, true, false ),
        MIN_CLOSE               ( true, false, false, false, true ),
        MAX_EXT                 ( false, true, true, false, false ),
        MAX_STACK               ( false, true, false, true, false ),
        MAX_CLOSE               ( false, true, false, false, true ),
        EXT_STACK               ( false, false, true, true, false ),
        EXT_CLOSE               ( false, false, true, false, true ),
        STACK_CLOSE             ( false, false, false, true, true ),
        
        MIN_MAX_EXT             ( true, true, true, false, false ),
        MIN_MAX_STACK           ( true, true, false, true, true ),
        MIN_MAX_CLOSE           ( true, true, false, false, true ),
        
        MIN_EXT_STACK           ( true, false, true, true, false ),
        MIN_EXT_CLOSE           ( true, false, true, false, true ),
        
        MIN_STACK_CLOSE         ( true, false, false, true, true ),
        
        MAX_EXT_STACK           ( false, true, true, true, false ),
        MAX_EXT_CLOSE           ( false, true, true, false, true ),
        
        MAX_STACK_CLOSE         ( false, true, false, true, true ),
        
        MIN_MAX_EXT_STACK       ( true, true, true, true, false ),
        MIN_MAX_EXT_CLOSE       ( true, true, true, false, true ),
        MIN_MAX_STACK_CLOSE     ( true, true, false, true, true ),
        MIN_EXT_STACK_CLOSE     ( true, false, true, true, true ),
        MAX_EXT_STACK_CLOSE     ( false, true, true, true, true ),
        
        MIN_MAX_EXT_STACK_CLOSE ( true, true, true, true, true );
        
        
        /** the user can minimize the dockable */
        private boolean minimizable;
        /** the user can maximize the dockable */
        private boolean maximizable;
        /** the user can externalize the dockable */
        private boolean externalizable;
        /** the user can stack the dockable */
        private boolean stackable;
        /** the user can close the dockable */
        private boolean closeable;
        
        /**
         * Creates a new Permissions.
         * @param min {@link #minimizable}
         * @param max {@link #maximizable}
         * @param ext {@link #externalizable}
         * @param stack {@link #stackable}
         * @param close {@link #closeable}
         */
        private Permissions( boolean min, boolean max, boolean ext, boolean stack, boolean close ){
            this.minimizable = min;
            this.maximizable = max;
            this.externalizable = ext;
            this.stackable = stack;
            this.closeable = close;
        }
        
        /**
         * Represents the property {@link DefaultCDockable#isCloseable()}.
         * @return <code>true</code> if the user can close the dockable
         */
        public boolean isCloseable() {
            return closeable;
        }
        
        /**
         * Represents the property {@link DefaultCDockable#isExternalizable()}.
         * @return <code>true</code> if the user can externalize the dockable
         */
        public boolean isExternalizable() {
            return externalizable;
        }
        
        /**
         * Represents the property {@link DefaultCDockable#isMaximizable()}.
         * @return <code>true</code> if the user can maximize the dockable
         */
        public boolean isMaximizable() {
            return maximizable;
        }
        
        /**
         * Represents the property {@link DefaultCDockable#isMinimizable()}.
         * @return <code>true</code> if the user can minimize the dockable
         */
        public boolean isMinimizable() {
            return minimizable;
        }
        
        /**
         * Represents the property {@link DefaultCDockable#isStackable()}.
         * @return <code>true</code> if the user can combine the dockable with
         * other dockables
         */
        public boolean isStackable() {
            return stackable;
        }
    }
    
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
    
    /**
     * Creates a new dockable
     */
    public DefaultCDockable(  ){
        this( Permissions.DEFAULT );
    }
    
    /**
     * Creates a new dockable.
     * @param permission the permissions of this dockable
     */
    public DefaultCDockable( Permissions permission ){
        setMinimizable( permission.isMinimizable() );
        setMaximizable( permission.isMaximizable() );
        setExternalizable( permission.isExternalizable() );
        setStackable( permission.isStackable() );
        setCloseable( permission.isCloseable() );
    }
    
    @Override
    protected DefaultCommonDockable createCommonDockable(){
	    return new DefaultCommonDockable( this, getClose() );
    }
    
    /**
     * Gets the container on which the client can pack its components.
     * @return the panel showing the content
     */
    public Container getContentPane(){
        return intern().getContentPane();
    }
    
    /**
     * Sets the {@link LayoutManager} of the {@link #getContentPane() content pane}.
     * @param layout the new layout manager
     */
    public void setLayout( LayoutManager layout ){
        getContentPane().setLayout( layout );
    }
    
    /**
     * Adds <code>component</code> to the content pane.
     * @param component the new component
     */
    public void add( Component component ){
        getContentPane().add( component );
    }
    
    /**
     * Adds <code>component</code> to the content pane.
     * @param component the new component
     * @param constraints constraints for the {@link #setLayout(LayoutManager) layout manager}
     */
    public void add( Component component, Object constraints ){
        getContentPane().add( component, constraints );
    }
    
    /**
     * Removes <code>component</code> from the content pane.
     * @param component the component to remove
     */
    public void remove( Component component ){
        getContentPane().remove( component );
    }
    
    /**
     * Sets the text that is shown as title.
     * @param text the title
     */
    public void setTitleText( String text ){
        intern().setTitleText( text );
    }
    
    /**
     * Gets the text that is shown as title.
     * @return the title
     */
    public String getTitleText(){
        return intern().getTitleText();
    }
    
    /**
     * Sets the tooltip that should be shown on the title of this dockable.
     * @param text the new tooltip, can be <code>null</code>
     */
    public void setTitleToolTip( String text ){
        intern().setTitleToolTip( text );
    }
    
    /**
     * Gets the tooltip that is shown on the title of this dockable.
     * @return the tooltip or <code>null</code>
     */
    public String getTitleToolTip(){
        return intern().getTitleToolTip();
    }
    
    /**
     * Sets the behavior of {@link #setTitleIcon(Icon)} in case of a <code>null</code> argument. Either
     * the icon is replaced by the default icon, or just not shown.
     * @param handling the new behavior, not <code>null</code>
     */
    public void setTitleIconHandling( IconHandling handling ){
    	intern().setTitleIconHandling( handling );
    }
    
    /**
     * Gets the behavior of {@link #setTitleIcon(Icon)}.
     * @return the behavior, not <code>null</code>
     */
    public IconHandling getTitleIconHandling(){
    	return intern().getTitleIconHandling();
    }
    
    /**
     * Sets the icon that is shown in the title of this <code>CDockable</code>. The exact behavior
     * of this method depends on the {@link IconHandling} that was set by {@link #setTitleIconHandling(IconHandling)}.
     * @param icon the title-icon
     */
    public void setTitleIcon( Icon icon ){
        intern().setTitleIcon( icon );
    }
    
    /**
     * Gets the icon that is shown in the title.
     * @return the title-icon, might be <code>null</code>
     */
    public Icon getTitleIcon(){
        return intern().getTitleIcon();
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
            
            listenerCollection.getCDockablePropertyListener().minimizableChanged( this );
            
            CControlAccess control = control();
            if( control != null ){
                control.getLocationManager().ensureValidLocation( this );
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
            
            listenerCollection.getCDockablePropertyListener().maximizableChanged( this );
            
            CControlAccess control = control();
            if( control != null ){
                control.getLocationManager().ensureValidLocation( this );
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
            
            listenerCollection.getCDockablePropertyListener().externalizableChanged( this );
            
            CControlAccess control = control();
            if( control != null ){
                control.getLocationManager().ensureValidLocation( this );
            }
        }
    }
    
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
            listenerCollection.getCDockablePropertyListener().closeableChanged( this );
        }
    }

    /**
     * Adds an action to this dockable. The action will be shown in the
     * popup-menu which belongs to this dockable, and also as button in some titles
     * of this dockable.
     * @param action the new action
     */
    public void addAction( CAction action ){
        intern().getActions().add( action );
    }
    
    /**
     * Adds a new action to this dockable.
     * @param index the location of the action
     * @param action the action
     * @see #addAction(CAction)
     */
    public void insertAction( int index, CAction action ){
        intern().getActions().insert( index, action );
    }
    
    /**
     * Adds a separator to the list of {@link CAction}s of this dockable.
     */
    public void addSeparator(){
        addAction( CSeparator.SEPARATOR );
    }
    
    /**
     * Adds a separator to the list of {@link CAction}s of this dockable.
     * @param index the location of the action
     */
    public void insertSeparator( int index ){
        insertAction( index, CSeparator.SEPARATOR );
    }
    
    /**
     * Removes an action from this dockable
     * @param index the location of the action
     */
    public void removeAction( int index ){
        intern().getActions().remove( index );
    }
    
    /**
     * Removes an action from this dockable.
     * @param action the action to remove
     */
    public void removeAction( CAction action ){
        intern().getActions().remove( action );
    }
    
    /**
     * Gets the number of {@link CAction}s that were added to this dockable.
     * @return the number of actions
     */
    public int getActionCount(){
    	return intern().getActions().getDockActionCount();
    }
    
    /**
     * Gets the <code>index</code>'th action of this dockable. Be aware that
     * the result might be {@link CSeparator#SEPARATOR}.
     * @param index the location of an action
     * @return the action
     */
    public CAction getAction( int index ){
    	return intern().getActions().getAction( index );
    }
    
    @Override
    public DefaultCommonDockable intern() {
    	return (DefaultCommonDockable)super.intern();
    }
    
    public CStation<?> asStation(){
    	return null;
    }
}
