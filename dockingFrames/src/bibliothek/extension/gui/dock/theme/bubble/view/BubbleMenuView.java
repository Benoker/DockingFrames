package bibliothek.extension.gui.dock.theme.bubble.view;

import javax.swing.JComponent;

import bibliothek.extension.gui.dock.theme.BubbleTheme;
import bibliothek.gui.Dockable;
import bibliothek.gui.dock.action.ActionPopup;
import bibliothek.gui.dock.action.DockActionSource;
import bibliothek.gui.dock.action.MenuDockAction;

public class BubbleMenuView extends AbstractBubbleView{
	private MenuDockAction action;
	
	public BubbleMenuView( BubbleTheme theme, MenuDockAction action, Dockable dockable ){
		super( theme, action, dockable );
		this.action = action;
	}
	
	@Override
	protected void triggered( final Dockable dockable ){
		final DockActionSource source = action.getMenu( dockable );
        if( source != null && source.getDockActionCount() > 0 ){
            ActionPopup popup = new ActionPopup( false ){
                @Override
                protected Dockable getDockable() {
                    return dockable;
                }

                @Override
                protected DockActionSource getSource() {
                    return source;
                }

                @Override
                protected boolean isEnabled() {
                    return true;
                }
            };
            
            
            JComponent item = getItem();
            if( getOrientation().isHorizontal() )
            	popup.popup( item, 0, item.getHeight() );
            else
            	popup.popup( item, item.getWidth(), 0 );
        }
	}
}
