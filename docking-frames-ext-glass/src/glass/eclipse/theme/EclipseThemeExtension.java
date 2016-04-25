package glass.eclipse.theme;

import javax.swing.*;
import bibliothek.extension.gui.dock.theme.*;
import bibliothek.gui.*;
import bibliothek.gui.dock.*;
import bibliothek.gui.dock.station.stack.tab.*;
import bibliothek.gui.dock.themes.*;
import bibliothek.gui.dock.title.*;
import bibliothek.gui.dock.util.*;
import bibliothek.gui.dock.util.property.*;
import glass.eclipse.theme.factory.*;
import glass.eclipse.theme.icon.*;

public class EclipseThemeExtension implements DockThemeExtension {
	private DockTheme trigger;

	/**
	 *  Tells the glass eclipse painter which glass parameters should be used to render the glass effect.
	 *  @see CDefaultGlassFactory
	 */
	public static final PropertyKey<IGlassParameterFactory> GLASS_FACTORY = new PropertyKey<IGlassParameterFactory>( "Glass eclipse glass parameter factory",
			new ConstantPropertyFactory<IGlassParameterFactory>( new CDefaultGlassFactory() ), true );

	public EclipseThemeExtension( DockTheme trigger, EclipseTheme theme ) {
		this.trigger = trigger;

		theme.setMovingImageFactory( new CMiniPreviewMovingImageFactory( 128 ), Priority.THEME );
		theme.setPaint( new CGlassStationPaint(), Priority.THEME );

	}

	public void install( DockController controller, DockTheme theme ) {
		// nothing to do
	}

	public void installed( DockController controller, DockTheme theme ) {
		if( trigger == theme ) {
			DockTitleManager manager = controller.getDockTitleManager();
			manager.registerTheme( FlapDockStation.BUTTON_TITLE_ID, CGlassDockTitleFactory.FACTORY );

			IconManager im = controller.getIcons();

			Icon normalizeIcon = createIcon( "images/normalize.png" );
			Icon maximizeIcon = createIcon( "images/maximize.png" );

			im.setIconTheme( "split.normalize", normalizeIcon );
			im.setIconTheme( "split.maximize", maximizeIcon );

			im.setIconTheme( "screen.normalize", normalizeIcon );
			im.setIconTheme( "screen.maximize", maximizeIcon );

			im.setIconTheme( "locationmanager.maximize", maximizeIcon );
			im.setIconTheme( "locationmanager.normalize", normalizeIcon );
			im.setIconTheme( "locationmanager.externalize", createIcon( "images/externalize.png" ) );
			im.setIconTheme( "locationmanager.minimize", createIcon( "images/minimize.png" ) );

			im.setIconTheme( "locationmanager.unexternalize", createIcon( "images/unexternalize.png" ) );
			im.setIconTheme( "locationmanager.unmaximize_externalized", normalizeIcon );

			im.setIconTheme( "close", createIcon( "images/close_active.png" ) );
			im.setIconTheme( "flap.hold", createIcon( "images/pin_active.png" ) );
			im.setIconTheme( "flap.free", createIcon( "images/unpin_active.png" ) );
			im.setIconTheme( "overflow.menu", createIcon( "images/overflow_menu.png" ) );

			im.publish( Priority.CLIENT, TabMenuDockIcon.KIND_TAB_MENU, new CTabMenuOverflowIconBridge() );
		}
	}

	public void uninstall( DockController controller, DockTheme theme ) {
		if( trigger == theme ) {
			IconManager im = controller.getIcons();

			im.clear( Priority.THEME );
		}
	}

	public static ImageIcon createIcon( String path ) {
		return new ImageIcon( CGlassEclipseTabPainter.class.getResource( path ) );
	}
}
