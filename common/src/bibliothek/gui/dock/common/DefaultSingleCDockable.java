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
import bibliothek.util.ClientOnly;

/**
 * A <code>DefaultSingleCDockable</code> is an element which has a 
 * {@link #getContentPane() content-pane} where clients can add or remove as many
 * {@link java.awt.Component}s as they whish.
 * @author Benjamin Sigg
 * @see SingleCDockable
 */
@ClientOnly
public class DefaultSingleCDockable extends DefaultCDockable implements SingleCDockable{
    /** a unique id */
    private String id;
    
    /**
     * Creates a new dockable
     * @param id a unique id, not <code>null</code>
     * @param actions the actions shown in the title, can be <code>null</code>.
     * A separator is inserted for every entry that is <code>null</code> of this array.
     */
    public DefaultSingleCDockable( String id, CAction... actions ){
        this( id, null, null, null, null, actions );
    }
    
    /**
     * Creates a new dockable.
     * @param id the unique id, must not be <code>null</code>
     * @param content a <code>Component</code> which will be shown in the middle
     * of this dockable, can be <code>null</code>.
     * @param actions the actions shown in the title, can be <code>null</code>.
     * A separator is inserted for every entry that is <code>null</code> of this array.
     */
    public DefaultSingleCDockable( String id, Component content, CAction... actions ){
        this( id, null, null, content, null, actions );
    }
    
    /**
     * Creates a new dockable.
     * @param id the unique id, must not be <code>null</code>
     * @param title the text shown in the title, can be <code>null</code>
     * @param content a <code>Component</code> which will be shown in the middle
     * of this dockable, can be <code>null</code>.
     * @param actions the actions shown in the title, can be <code>null</code>.
     * A separator is inserted for every entry that is <code>null</code> of this array.
     */
    public DefaultSingleCDockable( String id, String title, Component content, CAction... actions ){
        this( id, null, title, content, null, actions );
    }
    
    /**
     * Creates a new dockable.
     * @param id the unique id, must not be <code>null</code>
     * @param icon the icon shown in the title, can be <code>null</code>
     * @param content a <code>Component</code> which will be shown in the middle
     * of this dockable, can be <code>null</code>.
     * @param actions the actions shown in the title, can be <code>null</code>.
     * A separator is inserted for every entry that is <code>null</code> of this array.
     */
    public DefaultSingleCDockable( String id, Icon icon, Component content, CAction... actions ){
        this( id, icon, null, content, null, actions );
    }
    
    /**
     * Creates a new dockable.
     * @param id the unique id, must not be <code>null</code>
     * @param icon the icon shown in the title, can be <code>null</code>
     * @param title the text shown in the title, can be <code>null</code>
     * @param content a <code>Component</code> which will be shown in the middle
     * of this dockable, can be <code>null</code>.
     * @param actions the actions shown in the title, can be <code>null</code>.
     * A separator is inserted for every entry that is <code>null</code> of this array.
     */
    public DefaultSingleCDockable( String id, Icon icon, String title, Component content, CAction... actions ){
        this( id, icon, title, content, null, actions );
    }
    
    /**
     * Creates a new dockable.
     * @param id the unique id, must not be <code>null</code>
     * @param title the text shown in the title, can be <code>null</code>
     * @param actions the actions shown in the title, can be <code>null</code>.
     * A separator is inserted for every entry that is <code>null</code> of this array.
     */
    public DefaultSingleCDockable( String id, String title, CAction... actions ){
        this( id, null, title, null, null, actions );
    }
    
    /**
     * Creates a new dockable.
     * @param id the unique id, must not be <code>null</code>
     * @param icon the icon shown in the title, can be <code>null</code>
     * @param actions the actions shown in the title, can be <code>null</code>.
     * A separator is inserted for every entry that is <code>null</code> of this array.
     */
    public DefaultSingleCDockable( String id, Icon icon, CAction... actions ){
        this( id, icon, null, null, null, actions );
    }
    
    /**
     * Creates a new dockable.
     * @param id the unique id, must not be <code>null</code>
     * @param icon the icon shown in the title, can be <code>null</code>
     * @param title the text shown in the title, can be <code>null</code>
     * @param actions the actions shown in the title, can be <code>null</code>.
     * A separator is inserted for every entry that is <code>null</code> of this array.
     */
    public DefaultSingleCDockable( String id, Icon icon, String title, CAction... actions ){
        this( id, icon, title, null, null, actions );
    }
    
    /**
     * Creates a new dockable.
     * @param id the unique id, must not be <code>null</code>
     * @param icon the icon shown in the title, can be <code>null</code>
     * @param title the text shown in the title, can be <code>null</code>
     * @param content a <code>Component</code> which will be shown in the middle
     * of this dockable, can be <code>null</code>.
     * @param permissions what actions the user is allowed to do, <code>null</code> will be
     * replaced by {@link DefaultCDockable.Permissions#DEFAULT}.
     * @param actions the actions shown in the title, can be <code>null</code>.
     * A separator is inserted for every entry that is <code>null</code> of this array.
     */
    public DefaultSingleCDockable( String id, Icon icon, String title, Component content, Permissions permissions, CAction... actions ){
        super( permissions == null ? Permissions.DEFAULT : permissions );
        if( id == null )
            throw new NullPointerException( "id must not be null" );
        
        this.id = id;
        if( icon != null ){
            setTitleIcon( icon );
        }
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
     * Gets the id of this dockable. The id is unique among all dockables
     * which are added to the same {@link CControl}.
     * @return the unique id
     */
    public String getUniqueId(){
        return id;
    }
}
