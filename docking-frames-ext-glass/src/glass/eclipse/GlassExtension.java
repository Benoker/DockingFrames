/*
 * Bibliothek - DockingFrames
 * Library built on Java/Swing, allows the user to "drag and drop"
 * panels containing any Swing-Component the developer likes to add.
 * 
 * Copyright (C) 2010 Benjamin Sigg
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
package glass.eclipse;

import glass.eclipse.theme.CGlassEclipseColorSchemeExtension;
import glass.eclipse.theme.CGlassEclipseTabPainter;
import glass.eclipse.theme.EclipseTabChoiceExtension;
import glass.eclipse.theme.EclipseThemeExtension;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import bibliothek.extension.gui.dock.preference.preferences.choice.ChoiceExtension;
import bibliothek.extension.gui.dock.preference.preferences.choice.EclipseTabChoice;
import bibliothek.extension.gui.dock.theme.EclipseTheme;
import bibliothek.extension.gui.dock.theme.eclipse.EclipseColorScheme;
import bibliothek.gui.DockController;
import bibliothek.gui.DockTheme;
import bibliothek.gui.dock.themes.ColorScheme;
import bibliothek.gui.dock.themes.DockThemeExtension;
import bibliothek.gui.dock.util.DockProperties;
import bibliothek.gui.dock.util.Priority;
import bibliothek.gui.dock.util.extension.Extension;
import bibliothek.gui.dock.util.extension.ExtensionName;

/**
 * This extensions changes the look of the {@link bibliothek.extension.gui.dock.theme.EclipseTheme}.
 * @author Benjamin Sigg
 */
public class GlassExtension implements Extension{
	public void install( DockController controller ){
		DockProperties properties = controller.getProperties();
		
		properties.set( EclipseTheme.TAB_PAINTER, CGlassEclipseTabPainter.FACTORY, Priority.DEFAULT );
		properties.set( EclipseTheme.PAINT_ICONS_WHEN_DESELECTED, true, Priority.DEFAULT );
	}
	
	public void uninstall( DockController controller ){
		// ignore	
	}
	
	@SuppressWarnings("unchecked")
	public <E> Collection<E> load( DockController controller, ExtensionName<E> extension ){
		List<E> result = new ArrayList<E>();
		
		if( extension.getName().equals( DockThemeExtension.DOCK_THEME_EXTENSION )){
			Object themeParameter = extension.get( DockThemeExtension.THEME_PARAMETER );
			DockTheme trigger = null;
			
			if( themeParameter instanceof EclipseTheme ){
				EclipseTheme theme = (EclipseTheme)themeParameter;
				if( trigger == null ){
					trigger = theme;
				}
				result.add( (E)new EclipseThemeExtension( trigger, theme ) );
			}
		}
		
		if( extension.getName().equals( ChoiceExtension.CHOICE_EXTENSION )){
			Object choice = extension.get( ChoiceExtension.CHOICE_PARAMETER );
			if( choice instanceof EclipseTabChoice ){
				result.add( (E)new EclipseTabChoiceExtension() );
			}
		}
		
		if( extension.getName().equals( ColorScheme.EXTENSION_NAME )){
			Object parameterValue = extension.get( ColorScheme.COLOR_SCHEME_PARAMETER );
			if( parameterValue instanceof EclipseColorScheme ){
				result.add( (E)new CGlassEclipseColorSchemeExtension() );
			}
		}
		
		return result;
	}	
}
