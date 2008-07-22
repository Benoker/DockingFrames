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
package bibliothek.extension.gui.dock;

import bibliothek.extension.gui.dock.preference.PreferenceTreeModel;
import bibliothek.extension.gui.dock.preference.model.BubbleThemePreferenceModel;
import bibliothek.extension.gui.dock.preference.model.EclipseThemePreferenceModel;
import bibliothek.extension.gui.dock.preference.model.KeyStrokePreferenceModel;
import bibliothek.extension.gui.dock.preference.model.LayoutPreferenceModel;
import bibliothek.extension.gui.dock.util.Path;
import bibliothek.extension.gui.dock.util.PathCombiner;
import bibliothek.gui.DockController;
import bibliothek.gui.DockUI;

/**
 * A {@link PreferenceTreeModel} that contains all the preferences that are
 * used in this framework.
 * @author Benjamin Sigg
 */
public class DockingFramesPreference extends PreferenceTreeModel{
    /**
     * Creates a new model. This constructor sets the behavior of how to
     * create paths for preferences to {@link PathCombiner#SECOND}. This
     * behavior allows reordering of models and preferences in future releases,
     * however forces any preference to have a truly unique path in a global
     * scale.
     * @param controller the controller whose preferences this model should 
     * represent
     */
    public DockingFramesPreference( DockController controller ){
        this( controller, PathCombiner.SECOND );
    }
    
    /**
     * Creates a new model.
     * @param controller the controller whose preferences this model should 
     * represent
     * @param combiner how to create preference paths for nested preferences
     */
    public DockingFramesPreference( DockController controller, PathCombiner combiner ){
        super( combiner );
        put( new Path( "shortcuts" ),
                DockUI.getDefaultDockUI().getString( "preference.shortcuts" ), 
                new KeyStrokePreferenceModel( controller.getProperties() ) );
        
        put( new Path( "layout" ),
        		DockUI.getDefaultDockUI().getString( "preference.layout" ),
        		new LayoutPreferenceModel( controller.getProperties() ));
        
        put( new Path( "theme" ), DockUI.getDefaultDockUI().getString( "preference.theme" ), null );
        
        put( new Path( "theme.BubbleTheme" ),
        		DockUI.getDefaultDockUI().getString( "theme.bubble" ),
        		new BubbleThemePreferenceModel( controller.getProperties() ));
        
        put( new Path( "theme.EclipseTheme" ),
        		DockUI.getDefaultDockUI().getString( "theme.eclipse" ),
        		new EclipseThemePreferenceModel( controller.getProperties() ));
    }
}
