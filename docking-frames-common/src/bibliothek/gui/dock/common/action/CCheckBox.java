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
package bibliothek.gui.dock.common.action;

import javax.swing.Icon;
import javax.swing.JCheckBox;

import bibliothek.gui.dock.common.action.core.CommonSimpleCheckAction;
import bibliothek.gui.dock.common.intern.action.CSelectableAction;

/**
 * An action which behaves like a {@link JCheckBox}.
 * @author Benjamin Sigg
 *
 */
public abstract class CCheckBox extends CSelectableAction<CommonSimpleCheckAction> {
    /**
     * Creates a new checkbox
     */
    public CCheckBox() {
        super( null );
        init( new CommonSimpleCheckAction( this ));
    }
    
    /**
     * Creates a new checkbox using <code>action</code> as internal representation.
     * @param action the internal representation, can be <code>null</code> in which case
     * a subclass should call {@code CSelectableAction<>.init(CommonSimpleCheckAction)}
     */
    protected CCheckBox( CommonSimpleCheckAction action ){
    	super( null );
    	if( action != null ){
    		init( action );
    	}
    }
    
    /**
     * Creates a new checkbox.
     * @param text the text of this checkbox
     * @param icon the icon of this checkbox
     */
    public CCheckBox( String text, Icon icon ){
        this();
        setText( text );
        setIcon( icon );
    }
}
