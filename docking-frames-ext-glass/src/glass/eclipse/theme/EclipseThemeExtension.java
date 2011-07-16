package glass.eclipse.theme;

import javax.swing.Icon;
import javax.swing.ImageIcon;

import bibliothek.extension.gui.dock.theme.EclipseTheme;
import bibliothek.gui.DockController;
import bibliothek.gui.DockTheme;
import bibliothek.gui.dock.themes.DockThemeExtension;
import bibliothek.gui.dock.util.IconManager;
import bibliothek.gui.dock.util.Priority;

public class EclipseThemeExtension implements DockThemeExtension{
	private DockTheme trigger;
	
	public EclipseThemeExtension( DockTheme trigger, EclipseTheme theme ){
		this.trigger = trigger;
		
		theme.setMovingImageFactory( new CMiniPreviewMovingImageFactory( 128 ), Priority.THEME );
		theme.setPaint( new CGlassStationPaint(), Priority.THEME );
	}
	
	public void install( DockController controller, DockTheme theme ){
		// nothing to do
	}
	
	public void installed( DockController controller, DockTheme theme ){
		if( this.trigger == theme ){
			IconManager im = controller.getIcons();
	
			Icon normalizeIcon = createIcon("images/normalize.png");
			Icon maximizeIcon = createIcon("images/maximize.png");
			
			im.setIconTheme("split.normalize", normalizeIcon);
			im.setIconTheme("split.maximize", maximizeIcon);
			
			im.setIconTheme("screen.normalize", normalizeIcon);
			im.setIconTheme("screen.maximize", maximizeIcon);
			
			im.setIconTheme("locationmanager.maximize", maximizeIcon);
			im.setIconTheme("locationmanager.normalize", normalizeIcon);
			im.setIconTheme("locationmanager.externalize", createIcon("images/externalize.png"));
			im.setIconTheme("locationmanager.minimize", createIcon("images/minimize.png"));
			
			im.setIconTheme("locationmanager.unexternalize", createIcon("images/unexternalize.png"));
			im.setIconTheme("locationmanager.unmaximize_externalized", normalizeIcon);
			
			im.setIconTheme("close", createIcon("images/close_active.png"));
			im.setIconTheme("flap.hold", createIcon("images/pin_active.png"));
			im.setIconTheme("flap.free", createIcon("images/unpin_active.png"));
			im.setIconTheme("overflow.menu", createIcon("images/overflow_menu.png"));
		}
	}
	
	public void uninstall( DockController controller, DockTheme theme ){
		if( this.trigger == theme ){
			IconManager im = controller.getIcons();

			im.clear( Priority.THEME );
		}
	}
	
	public static ImageIcon createIcon(String path) {
		return new ImageIcon( CGlassEclipseTabPainter.class.getResource( path ));
	}
}
