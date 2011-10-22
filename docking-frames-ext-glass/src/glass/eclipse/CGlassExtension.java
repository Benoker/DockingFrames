package glass.eclipse;

import glass.eclipse.theme.EclipseThemeExtension;
import glass.eclipse.theme.GlassEclipseTabTransmitterFactory;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import bibliothek.extension.gui.dock.theme.EclipseTheme;
import bibliothek.gui.DockController;
import bibliothek.gui.DockTheme;
import bibliothek.gui.dock.common.theme.CDockTheme;
import bibliothek.gui.dock.common.theme.CEclipseTheme;
import bibliothek.gui.dock.common.theme.color.CColorBridgeExtension;
import bibliothek.gui.dock.themes.DockThemeExtension;
import bibliothek.gui.dock.util.extension.Extension;
import bibliothek.gui.dock.util.extension.ExtensionName;

/**
 * This extensions changes the look of the {@link bibliothek.extension.gui.dock.theme.EclipseTheme}, this
 * extension is only loadable if the Common project is in the classpath
 * @author Benjamin Sigg
 */
public class CGlassExtension implements Extension{

	public void install( DockController controller ){
		// ignore
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
			
			if( themeParameter instanceof CDockTheme ){
				trigger = (DockTheme)themeParameter;
				themeParameter = ((CDockTheme)themeParameter).intern();
			
				if( themeParameter instanceof EclipseTheme ){
					EclipseTheme theme = (EclipseTheme)themeParameter;
					if( trigger == null ){
						trigger = theme;
					}
					result.add( (E)new EclipseThemeExtension( trigger, theme ) );
				}
			}
		}

		if( extension.getName().equals( CColorBridgeExtension.EXTENSION_NAME )){
			Object parameterValue = extension.get( CColorBridgeExtension.PARAMETER_NAME );
			if( parameterValue instanceof CEclipseTheme ){
				result.add( (E) new GlassEclipseTabTransmitterFactory() );
			}
		}
		
		return result;
	}	
}
