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
package bibliothek.gui.dock.common.action.predefined;

import bibliothek.gui.Dockable;
import bibliothek.gui.dock.action.view.ActionViewConverter;
import bibliothek.gui.dock.action.view.ViewTarget;
import bibliothek.gui.dock.common.action.CAction;
import bibliothek.gui.dock.common.action.core.CommonDockAction;

/**
 * An action which is never visible, can be used as placeholder
 * or in cases where an action would normally be required and <code>null</code>
 * would be replaced by a default action. 
 * @author Benjamin Sigg
 */
public class CBlank extends CAction {
    /**
     * A public instance of {@link CBlank}.
     */
    public static final CBlank BLANK = new CBlank();
    
    /**
     * Creates a new blank action
     */
    protected CBlank(){
        init( new CommonDockAction(){
            public void bind( Dockable dockable ) {
                // ignore
            }

            public <V> V createView( ViewTarget<V> target, ActionViewConverter converter, Dockable dockable ) {
                return null;
            }

            public boolean trigger( Dockable dockable ) {
                return false;
            }

            public void unbind( Dockable dockable ) {
                // ignore
            }
            
            public CAction getAction(){
            	return CBlank.this;
            }
        });
    }
}
