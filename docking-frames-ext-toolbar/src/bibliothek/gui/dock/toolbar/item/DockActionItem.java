package bibliothek.gui.dock.toolbar.item;

import java.awt.Component;
import java.awt.GridLayout;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.event.MouseInputListener;

import bibliothek.gui.DockController;
import bibliothek.gui.DockTheme;
import bibliothek.gui.Dockable;
import bibliothek.gui.Orientation;
import bibliothek.gui.dock.ToolbarItem;
import bibliothek.gui.dock.ToolbarItemDockable;
import bibliothek.gui.dock.action.DockAction;
import bibliothek.gui.dock.action.view.ViewTarget;
import bibliothek.gui.dock.event.UIListener;
import bibliothek.gui.dock.themes.basic.action.BasicTitleViewItem;

/**
 * A wrapper around a {@link DockAction} allowing the action to show up on a toolbar.
 * @author Benjamin Sigg
 */
public class DockActionItem implements ToolbarItem {
	/**
	 * A target for converting a {@link DockAction} into a {@link Component} that can be shown on
	 * a toolbar. If nothing else is defined, this target will automatically fall back to {@link ViewTarget#TARGET} 
	 */
	public static final ViewTarget<BasicTitleViewItem<JComponent>> TOOLBAR =
			new ViewTarget<BasicTitleViewItem<JComponent>>( "target TOOLBAR" );

	/** the panel actually showing {@link #view} */
	private JPanel content = new JPanel( new GridLayout( 1, 1 ) );
	
	/** the action shown on this dockable */
	private DockAction action;
	
	/** the conversion of {@link #action} into a {@link JComponent} */
	private BasicTitleViewItem<JComponent> view;
	
	/** the orientation of the {@link #view} */
	private Orientation orientation = Orientation.HORIZONTAL;
	
	/** {@link MouseInputListener}s that were added to this {@link Dockable} */
	private List<MouseInputListener> mouseListeners = new ArrayList<MouseInputListener>();
	
	/** the controller in whose realm this item is currently used */
	private DockController controller;
	
	/** the owner of this item */
	private Dockable dockable;
	
	/** whether this item is in use or not */
	private boolean bound = false;
	
	/** Called if the current {@link DockTheme} changes */
	private UIListener uiListener = new UIListener(){
		@Override
		public void themeWillChange( DockController controller, DockTheme oldTheme, DockTheme newTheme ){
			// ignore
		}
		@Override
		public void themeChanged( DockController controller, DockTheme oldTheme, DockTheme newTheme ){
			destroyView();
			createView();
		}
		@Override
		public void updateUI( DockController controller ){
			destroyView();
			createView();	
		}
	};
	
	/**
	 * Creates a new dockable showing {@link #action}
	 * @param action the action to show on this dockable
	 */
	public DockActionItem( DockAction action ){
		if( action == null ){
			throw new IllegalArgumentException( "action must not be null" );
		}
		this.action = action;
	}
	
	@Override
	public void setDockable( ToolbarItemDockable dockable ){
		this.dockable = dockable;	
	}
	
	@Override
	public void setController( DockController controller ){
		if( bound ){
			if( this.controller != null ){
				this.controller.getThemeManager().removeUIListener( uiListener );
				destroyView();
			}
			this.controller = controller;
			if( controller != null ){
				createView();
				controller.getThemeManager().addUIListener( uiListener );
			}
		}
		else{
			this.controller = controller;
		}
	}
	
	@Override
	public void bind(){
		bound = true;
		createView();
	}
	
	@Override
	public void unbind(){
		bound = false;
		destroyView();
	}
	
	private void destroyView(){
		if( view != null ){
			JComponent item = view.getItem();
			for( MouseInputListener listener : mouseListeners ){
				item.removeMouseListener( listener );
				item.removeMouseMotionListener( listener );
			}
			view.unbind();
			view = null;
		}
		action.unbind( dockable );
		content.removeAll();
	}
	
	private void createView(){
		if( bound && controller != null ){
			if( dockable == null ){
				throw new IllegalStateException( "trying to bind the item without knowing its dockable" );
			}
			
			action.bind( dockable );
			view = action.createView( TOOLBAR, controller.getActionViewConverter(), dockable );
			if( view != null ){
				updateOrientation();
				view.bind();
				content.add( view.getItem() );
				JComponent item = view.getItem();
				for( MouseInputListener listener : mouseListeners ){
					item.addMouseListener( listener );
					item.addMouseMotionListener( listener );
				}
			}
		}
	}
	
	@Override
	public void addMouseInputListener( MouseInputListener listener ){
		mouseListeners.add( listener );
		if( view != null ){
			view.getItem().addMouseListener( listener );
			view.getItem().addMouseMotionListener( listener );
		}
	}
	
	public void removeMouseInputListener( MouseInputListener listener ){
		mouseListeners.remove( listener );
		if( view != null ){
			view.getItem().removeMouseListener( listener );
			view.getItem().removeMouseMotionListener( listener );
		}
	}
	
	@Override
	public Component getComponent(){
		return content;
	}
	
	@Override
	public void setSelected( boolean selected ){
		// ignore
	}

	@Override
	public void setOrientation( Orientation orientation ){
		this.orientation = orientation;
	}
	
	private void updateOrientation(){
		if( view != null ){
			if( orientation == Orientation.HORIZONTAL ){
				view.setOrientation( bibliothek.gui.dock.title.DockTitle.Orientation.FREE_HORIZONTAL );
			}
			else{
				view.setOrientation( bibliothek.gui.dock.title.DockTitle.Orientation.FREE_VERTICAL );
			}
		}
	}
}
