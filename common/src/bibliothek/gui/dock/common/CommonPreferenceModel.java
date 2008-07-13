/*
 * Bibliothek - DockingFrames
 * Library built on Java/Swing, allows the user to "drag and drop"
 * panels containing any Swing-Component the developer likes to add.
 * 
 * Copyright (C) 2008 Benjamin Sigg
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

import bibliothek.extension.gui.dock.preference.PreferenceModel;
import bibliothek.extension.gui.dock.preference.PreferenceTreeModel;
import bibliothek.extension.gui.dock.preference.model.BubbleThemePreferenceModel;
import bibliothek.extension.gui.dock.preference.model.EclipseThemePreferenceModel;
import bibliothek.extension.gui.dock.util.Path;
import bibliothek.gui.DockController;
import bibliothek.gui.DockUI;
import bibliothek.gui.dock.common.preference.CKeyStrokePreferenceModel;
import bibliothek.gui.dock.common.preference.CLayoutPreferenceModel;

/**
 * A {@link PreferenceModel} that shows the settings of a {@link CControl}.
 * @author Benjamin Sigg
 */
public class CommonPreferenceModel extends PreferenceTreeModel{
    /**
     * Creates a new model.
     * @param control the control whose settings can be changed by this model
     */
    public CommonPreferenceModel( CControl control ){
        DockController controller = control.intern().getController();
        
        put( new Path( "shortcuts" ),
                DockUI.getDefaultDockUI().getString( "preference.shortcuts" ), 
                new CKeyStrokePreferenceModel( controller.getProperties() ) );
        
        put( new Path( "layout" ),
                DockUI.getDefaultDockUI().getString( "preference.layout" ),
                new CLayoutPreferenceModel( control ));
        
        put( new Path( "layout.BubbleTheme" ),
                DockUI.getDefaultDockUI().getString( "theme.bubble" ),
                new BubbleThemePreferenceModel( controller.getProperties() ));
        
        put( new Path( "layout.EclipseTheme" ),
                DockUI.getDefaultDockUI().getString( "theme.eclipse" ),
                new EclipseThemePreferenceModel( controller.getProperties() ));
    }
}
