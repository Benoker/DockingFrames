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
package bibliothek.gui.dock.common;

import java.awt.BorderLayout;
import java.awt.Component;

import javax.swing.Icon;

import bibliothek.gui.dock.common.action.CAction;
import bibliothek.gui.dock.common.intern.DefaultCDockable;
import bibliothek.gui.dock.common.intern.CControlAccess;
import bibliothek.gui.dock.dockable.DefaultDockableFactory;
import bibliothek.gui.dock.dockable.IconHandling;
import bibliothek.util.ClientOnly;

/**
 * A {@link MultipleCDockable} that contains a {@link #getContentPane() content-pane}
 * where the client might add or remove as many {@link java.awt.Component}s as
 * it wishes.
 * @author Benjamin Sigg
 * @see MultipleCDockable
 */
@ClientOnly
public class DefaultMultipleCDockable extends DefaultCDockable implements MultipleCDockable{
    /** a factory needed to store or load this dockable */
    private MultipleCDockableFactory<?,?> factory;
    
    /** whether to remove this dockable from the controller when closing or not */
    private boolean removeOnClose = true;
    
    /**
     * Creates a new dockable
     * @param factory the factory which created or could create this
     * kind of dockable. A value of <code>null</code> will default ot the {@link NullMultipleCDockableFactory}.
     * @param actions the actions shown in the title, can be <code>null</code>.
     * A separator is inserted for every entry that is <code>null</code> of this array.
     */
    public DefaultMultipleCDockable( MultipleCDockableFactory<?,?> factory, CAction... actions ){
        this( factory, null, IconHandling.REPLACE_NULL_ICON, null, null, null, actions );
    }
    
    /**
     * Creates a new dockable.
     * @param factory the factory which created or could create this
     * kind of dockable. A value of <code>null</code> will default ot the {@link NullMultipleCDockableFactory}.
     * @param content a <code>Component</code> which will be shown in the middle
     * of this dockable, can be <code>null</code>.
     * @param actions the actions shown in the title, can be <code>null</code>.
     * A separator is inserted for every entry that is <code>null</code> of this array.
     */
    public DefaultMultipleCDockable( MultipleCDockableFactory<?,?> factory, Component content, CAction... actions ){
        this( factory, null, IconHandling.REPLACE_NULL_ICON, null, content, null, actions );
    }
    
    /**
     * Creates a new dockable.
     * @param factory the factory which created or could create this
     * kind of dockable. A value of <code>null</code> will default ot the {@link NullMultipleCDockableFactory}.
     * @param title the text shown in the title, can be <code>null</code>
     * @param content a <code>Component</code> which will be shown in the middle
     * of this dockable, can be <code>null</code>.
     * @param actions the actions shown in the title, can be <code>null</code>.
     * A separator is inserted for every entry that is <code>null</code> of this array.
     */
    public DefaultMultipleCDockable( MultipleCDockableFactory<?,?> factory, String title, Component content, CAction... actions ){
        this( factory, null, IconHandling.REPLACE_NULL_ICON, title, content, null, actions );
    }
    
    /**
     * Creates a new dockable.
     * @param factory the factory which created or could create this
     * kind of dockable. A value of <code>null</code> will default ot the {@link NullMultipleCDockableFactory}.
     * @param icon the icon shown in the title, can be <code>null</code>
     * @param content a <code>Component</code> which will be shown in the middle
     * of this dockable, can be <code>null</code>.
     * @param actions the actions shown in the title, can be <code>null</code>.
     * A separator is inserted for every entry that is <code>null</code> of this array.
     */
    public DefaultMultipleCDockable( MultipleCDockableFactory<?,?> factory, Icon icon, Component content, CAction... actions ){
        this( factory, icon, IconHandling.KEEP_NULL_ICON, null, content, null, actions );
    }
    
    /**
     * Creates a new dockable.
     * @param factory the factory which created or could create this
     * kind of dockable. A value of <code>null</code> will default ot the {@link NullMultipleCDockableFactory}.
     * @param icon the icon shown in the title, can be <code>null</code>
     * @param title the text shown in the title, can be <code>null</code>
     * @param content a <code>Component</code> which will be shown in the middle
     * of this dockable, can be <code>null</code>.
     * @param actions the actions shown in the title, can be <code>null</code>.
     * A separator is inserted for every entry that is <code>null</code> of this array.
     */
    public DefaultMultipleCDockable( MultipleCDockableFactory<?,?> factory, Icon icon, String title, Component content, CAction... actions ){
        this( factory, icon, IconHandling.KEEP_NULL_ICON, title, content, null, actions );
    }
    
    /**
     * Creates a new dockable.
     * @param factory the factory which created or could create this
     * kind of dockable. A value of <code>null</code> will default ot the {@link NullMultipleCDockableFactory}.
     * @param title the text shown in the title, can be <code>null</code>
     * @param actions the actions shown in the title, can be <code>null</code>.
     * A separator is inserted for every entry that is <code>null</code> of this array.
     */
    public DefaultMultipleCDockable( MultipleCDockableFactory<?,?> factory, String title, CAction... actions ){
        this( factory, null, IconHandling.REPLACE_NULL_ICON, title, null, null, actions );
    }
    
    /**
     * Creates a new dockable.
     * @param factory the factory which created or could create this
     * kind of dockable. A value of <code>null</code> will default ot the {@link NullMultipleCDockableFactory}.
     * @param icon the icon shown in the title, can be <code>null</code>
     * @param actions the actions shown in the title, can be <code>null</code>.
     * A separator is inserted for every entry that is <code>null</code> of this array.
     */
    public DefaultMultipleCDockable( MultipleCDockableFactory<?,?> factory, Icon icon, CAction... actions ){
        this( factory, icon, IconHandling.KEEP_NULL_ICON, null, null, null, actions );
    }
    
    /**
     * Creates a new dockable.
     * @param factory the factory which created or could create this
     * kind of dockable. A value of <code>null</code> will default ot the {@link NullMultipleCDockableFactory}.
     * @param icon the icon shown in the title, can be <code>null</code>
     * @param title the text shown in the title, can be <code>null</code>
     * @param actions the actions shown in the title, can be <code>null</code>.
     * A separator is inserted for every entry that is <code>null</code> of this array.
     */
    public DefaultMultipleCDockable( MultipleCDockableFactory<?,?> factory, Icon icon, String title, CAction... actions ){
        this( factory, icon, IconHandling.KEEP_NULL_ICON, title, null, null, actions );
    }

    /**
     * Creates a new dockable.
     * @param factory the factory which created or could create this
     * kind of dockable. A value of <code>null</code> will default ot the {@link NullMultipleCDockableFactory}.
     * @param icon the icon shown in the title, can be <code>null</code>
     * @param title the text shown in the title, can be <code>null</code>
     * @param content a <code>Component</code> which will be shown in the middle
     * of this dockable, can be <code>null</code>.
     * @param permissions what actions the user is allowed to do, <code>null</code> will be
     * replaced by {@link bibliothek.gui.dock.common.intern.DefaultCDockable.Permissions#DEFAULT}.
     * @param actions the actions shown in the title, can be <code>null</code>.
     * A separator is inserted for every entry that is <code>null</code> of this array.
     */
    public DefaultMultipleCDockable( MultipleCDockableFactory<?,?> factory, Icon icon, String title, Component content, Permissions permissions, CAction... actions ){
    	this( factory, icon, IconHandling.KEEP_NULL_ICON, title, content, permissions, actions );
    }
    
    /**
     * Creates a new dockable.
     * @param factory the factory which created or could create this
     * kind of dockable. A value of <code>null</code> will default ot the {@link NullMultipleCDockableFactory}.
     * @param icon the icon shown in the title, can be <code>null</code>
     * @param iconHandling what to do if <code>icon</code> is <code>null</code>
     * @param title the text shown in the title, can be <code>null</code>
     * @param content a <code>Component</code> which will be shown in the middle
     * of this dockable, can be <code>null</code>.
     * @param permissions what actions the user is allowed to do, <code>null</code> will be
     * replaced by {@link bibliothek.gui.dock.common.intern.DefaultCDockable.Permissions#DEFAULT}.
     * @param actions the actions shown in the title, can be <code>null</code>.
     * A separator is inserted for every entry that is <code>null</code> of this array.
     */
    public DefaultMultipleCDockable( MultipleCDockableFactory<?,?> factory, Icon icon, IconHandling iconHandling, String title, Component content, Permissions permissions, CAction... actions ){
        super( permissions == null ? Permissions.DEFAULT : permissions );
        if( factory == null ){
        	factory = NullMultipleCDockableFactory.NULL;
        }
        this.factory = factory;
        
        setTitleIconHandling( iconHandling );
        setTitleIcon( icon );
        if( title != null ){
            setTitleText( title );
        }
        if( content != null ){
            getContentPane().setLayout( new BorderLayout() );
            getContentPane().add( content, BorderLayout.CENTER );
        }
        if( actions != null ){
            for( CAction action : actions ){
                if( action != null )
                    addAction( action );
                else
                    addSeparator();
            }
        }
    }
    
    /**
     * Gets the factory that created this dockable.
     * @return the factory, not <code>null</code>
     */
    public MultipleCDockableFactory<?,?> getFactory(){
        return factory;
    }
    
    @Override
    public void setControlAccess( CControlAccess control ){
        super.setControlAccess( control );
        if( control == null )
            intern().setFactoryID( DefaultDockableFactory.ID );
        else
            intern().setFactoryID( control.getFactoryId( factory ));
    }
    
    public boolean isRemoveOnClose() {
        return removeOnClose;
    }
    
    /**
     * Sets whether this dockable will be removed from the {@link CControl} when
     * made invisible.
     * @param removeOnClose <code>true</code> if this element should be removed
     * automatically.
     */
    public void setRemoveOnClose( boolean removeOnClose ) {
        this.removeOnClose = removeOnClose;
    }
}
