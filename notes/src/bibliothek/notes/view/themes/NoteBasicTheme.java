package bibliothek.notes.view.themes;

import javax.swing.JComponent;

import bibliothek.gui.DockController;
import bibliothek.gui.Dockable;
import bibliothek.gui.dock.action.view.ActionViewConverter;
import bibliothek.gui.dock.action.view.ViewGenerator;
import bibliothek.gui.dock.action.view.ViewTarget;
import bibliothek.gui.dock.themes.BasicTheme;
import bibliothek.gui.dock.themes.basic.action.BasicTitleViewItem;
import bibliothek.gui.dock.themes.basic.action.buttons.BasicMiniButton;
import bibliothek.gui.dock.themes.basic.action.dropdown.DropDownViewItem;
import bibliothek.gui.dock.themes.basic.action.menu.MenuViewItem;
import bibliothek.notes.view.actions.IconAction;
import bibliothek.notes.view.actions.icon.IconButtonHandler;

/**
 * A theme that installs an {@link ActionViewConverter} for the {@link IconAction},
 * using the look of the original theme.
 * @author Benjamin Sigg
 */
public class NoteBasicTheme extends BasicTheme {
	@Override
	public void install( DockController controller ){
		super.install( controller );
		
		ActionViewConverter converter = controller.getActionViewConverter();
		
		converter.putTheme( IconAction.ICON, ViewTarget.TITLE, 
				new ViewGenerator<IconAction, BasicTitleViewItem<JComponent>>(){

			public BasicTitleViewItem<JComponent> create( ActionViewConverter converter, IconAction action, Dockable dockable ){
				IconButtonHandler handler = new IconButtonHandler( action, dockable );
				BasicMiniButton button = new BasicMiniButton( handler, handler );
				handler.setModel( button.getModel() );
				return handler;
			}
		});
		
		converter.putTheme( IconAction.ICON, ViewTarget.MENU, 
				new ViewGenerator<IconAction, MenuViewItem<JComponent>>(){

			public MenuViewItem<JComponent> create( ActionViewConverter converter, IconAction action, Dockable dockable ){
				return null;
			}
		});
		
		converter.putTheme( IconAction.ICON, ViewTarget.DROP_DOWN, 
				new ViewGenerator<IconAction, DropDownViewItem>(){

			public DropDownViewItem create( ActionViewConverter converter, IconAction action, Dockable dockable ){
				return null;
			}
		});
	}
	
	@Override
	public void uninstall( DockController controller ){
		super.uninstall( controller );
		
		ActionViewConverter converter = controller.getActionViewConverter();
		converter.putTheme( IconAction.ICON, ViewTarget.TITLE, null );
		converter.putTheme( IconAction.ICON, ViewTarget.MENU, null );
		converter.putTheme( IconAction.ICON, ViewTarget.DROP_DOWN, null );
	}
}
