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

import java.util.ResourceBundle;

import bibliothek.gui.dock.common.CControl;
import bibliothek.gui.dock.common.intern.CDockable;
import bibliothek.gui.dock.common.intern.action.CExtendedModeAction;
import bibliothek.gui.dock.common.mode.CLocationModeManager;
import bibliothek.gui.dock.common.mode.ExtendedMode;
import bibliothek.gui.dock.support.util.Resources;

/**
 * An action which externalizes each {@link CDockable} to which is is added.
 * @author Benjamin Sigg
 */
public class CExternalizeAction extends CExtendedModeAction{
    /**
     * Creates a new action
     * @param control the control for which this action will be used
     */
    public CExternalizeAction( CControl control ){
        super( control, ExtendedMode.EXTERNALIZED, "externalize", CLocationModeManager.ICON_MANAGER_KEY_EXTERNALIZE, CControl.KEY_GOTO_EXTERNALIZED );
        
        ResourceBundle bundle = Resources.getBundle();
        
        setText( bundle.getString( "externalize.in" ) );
        setTooltip( bundle.getString( "externalize.in.tooltip" ) );
    }
}
