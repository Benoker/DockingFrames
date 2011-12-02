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

import javax.swing.KeyStroke;

import bibliothek.extension.gui.dock.preference.PreferenceModel;
import bibliothek.extension.gui.dock.preference.PreferenceTreeModel;
import bibliothek.extension.gui.dock.preference.model.BubbleThemePreferenceModel;
import bibliothek.extension.gui.dock.preference.model.ButtonContentPreferenceModel;
import bibliothek.extension.gui.dock.preference.model.EclipseThemePreferenceModel;
import bibliothek.extension.gui.dock.theme.BubbleTheme;
import bibliothek.extension.gui.dock.theme.EclipseTheme;
import bibliothek.gui.DockController;
import bibliothek.gui.dock.common.preference.CKeyStrokePreferenceModel;
import bibliothek.gui.dock.common.preference.CLayoutPreferenceModel;
import bibliothek.gui.dock.station.flap.button.ButtonContent;
import bibliothek.util.Path;
import bibliothek.util.PathCombiner;

/**
 * A {@link PreferenceModel} that shows the settings of a {@link CControl}.
 * @author Benjamin Sigg
 */
public class CPreferenceModel extends PreferenceTreeModel{
    /**
     * Creates a new model. This constructor sets the behavior of how to
     * create paths for preferences to {@link PathCombiner#SECOND}. This
     * behavior allows reordering of models and preferences in future releases,
     * however forces any preference to have a truly unique path in a global
     * scale.
     * @param control the control whose settings can be changed by this model
     */
    public CPreferenceModel( CControl control ){
        this( control, PathCombiner.SECOND );
    }
    
    /**
     * Creates a new model.
     * @param control the control whose settings can be changed by this model
     * @param combiner how to combine paths of models and of preferences
     */
    public CPreferenceModel( CControl control, PathCombiner combiner ){
        super( combiner, control.getController() );
        DockController controller = control.intern().getController();
        putLinked( new Path( "shortcuts" ), "preference.shortcuts", new CKeyStrokePreferenceModel( controller.getProperties() ) );
        putLinked( new Path( "buttonContent" ), "preference.buttonContent", new ButtonContentPreferenceModel( controller ) );
        putLinked( new Path( "layout" ), "preference.layout", new CLayoutPreferenceModel( control ));
        putLinked( new Path( "layout.BubbleTheme" ), "theme.bubble", new BubbleThemePreferenceModel( controller.getProperties() ));
        putLinked( new Path( "layout.EclipseTheme" ), "theme.eclipse", new EclipseThemePreferenceModel( controller.getProperties() ));
    }
    
    /**
     * Grants access to the preferences concerning the global {@link KeyStroke}s.
     * @return the model, not <code>null</code>
     * @throws IllegalStateException if the model was removed or replaced by the client
     */
    public CKeyStrokePreferenceModel getKeyStrokePreferences(){
    	PreferenceModel model = getModel( new Path( "shortcuts" ) );
    	if( model instanceof CKeyStrokePreferenceModel ){
    		return (CKeyStrokePreferenceModel)model;
    	}
    	else{
    		throw new IllegalStateException( "this model has been removed" );
    	}
    }
    
    /**
     * Grants access to the preferences concerning layout options like "where are the tabs placed?".
     * @return the model, not <code>null</code>
     * @throws IllegalStateException if the model was removed or replaced by the client
     */
    public CLayoutPreferenceModel getLayoutPreferences(){
    	PreferenceModel model = getModel( new Path( "layout" ) );
    	if( model instanceof CLayoutPreferenceModel ){
    		return (CLayoutPreferenceModel)model;
    	}
    	else{
    		throw new IllegalStateException( "this model has been removed" );
    	}
    }
    
    /**
     * Grants access to the preferences concerning the {@link ButtonContent}.
     * @return the model, not <code>null</code>
     * @throws IllegalStateException if the model was removed or replaced by the client
     */
    public ButtonContentPreferenceModel getButtonContent(){
    	PreferenceModel model = getModel( new Path( "buttonContent" ) );
    	if( model instanceof ButtonContentPreferenceModel ){
    		return (ButtonContentPreferenceModel)model;
    	}
    	else{
    		throw new IllegalStateException( "this model has been removed" );
    	}
    }
    
    /**
     * Grants access to the preferences concerning the {@link BubbleTheme}.
     * @return the model, not <code>null</code>
     * @throws IllegalStateException if the model was removed or replaced by the client
     */
    public BubbleThemePreferenceModel getBubbleThemePreferences(){
    	PreferenceModel model = getModel( new Path( "layout.BubbleTheme" ) );
    	if( model instanceof BubbleThemePreferenceModel ){
    		return (BubbleThemePreferenceModel)model;
    	}
    	else{
    		throw new IllegalStateException( "this model has been removed" );
    	}
    }
    
    /**
     * Grants access to the preferences concerning the {@link EclipseTheme}.
     * @return the model, not <code>null</code>
     * @throws IllegalStateException if the model was removed or replaced by the client
     */
    public EclipseThemePreferenceModel getEclipseThemePreferences(){
    	PreferenceModel model = getModel( new Path( "layout.EclipseTheme" ) );
    	if( model instanceof EclipseThemePreferenceModel ){
    		return (EclipseThemePreferenceModel)model;
    	}
    	else{
    		throw new IllegalStateException( "this model has been removed" );
    	}
    }
}
