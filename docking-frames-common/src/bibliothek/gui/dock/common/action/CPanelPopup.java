/*
 * Bibliothek - DockingFrames
 * Library built on Java/Swing, allows the user to "drag and drop"
 * panels containing any Swing-Component the developer likes to add.
 * 
 * Copyright (C) 2009 Benjamin Sigg
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
package bibliothek.gui.dock.common.action;

import java.awt.Component;
import java.awt.Point;

import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JMenu;
import javax.swing.JPopupMenu;
import javax.swing.SwingUtilities;

import bibliothek.gui.DockController;
import bibliothek.gui.Dockable;
import bibliothek.gui.dock.action.ActionType;
import bibliothek.gui.dock.action.view.ActionViewConverter;
import bibliothek.gui.dock.action.view.ViewTarget;
import bibliothek.gui.dock.common.action.core.CommonSimpleButtonAction;
import bibliothek.gui.dock.common.action.panel.DialogWindow;
import bibliothek.gui.dock.common.action.panel.MenuWindow;
import bibliothek.gui.dock.common.action.panel.PanelPopupWindow;
import bibliothek.gui.dock.common.action.panel.PanelPopupWindowListener;
import bibliothek.gui.dock.common.intern.action.CDecorateableAction;
import bibliothek.gui.dock.title.DockTitle;
import bibliothek.gui.dock.title.DockTitle.Orientation;

/**
 * This action shows some kind of popup (for example a {@link JDialog}) filled
 * with any content the client likes. This action is intended to be shown
 * as button in a title, but can also be used as menu item in a menu.<br>
 * Clients may override the various <code>onXYZ</code>-methods and create
 * and show their custom popup. In such a case they should call {@link #openPopup(PanelPopupWindow)}
 * to ensure that only one window is open at a time.<br>
 * As long as the user works on the popup-window it does not close automatically,
 * clients can call {@link #closePopup()} to explicitly close it. The 
 * window closes automatically if it loses the focus, clients can call
 * {@link #setCloseOnFocusLost(boolean)} to change that behavior.
 * <br>
 * <b>Note:</b> this action does not support being child of a drop down menu
 * @author Benjamin Sigg
 */
public class CPanelPopup extends CDecorateableAction<CPanelPopup.PanelPopup>{
	/** the kind of action this class represents */
	public static final ActionType<CPanelPopup.PanelPopup> PANEL_POPUP =
		new ActionType<PanelPopup>( "panel popup" );
	
	/**
	 * Tells how a {@link CPanelPopup} behaves if it is a child
	 * of a menu.
	 * @author Benjamin Sigg
	 */
	public static enum MenuBehavior{
		/** the action remains invisible */
		HIDE, 
		
		/** the action pushes the custom component on a {@link JMenu} */
		SUBMENU, 
		
		/** the action shows an undecorated dialog if triggered */
		UNDECORATED_DIALOG, 
		
		/** the action shows a decorated dialog if triggered */
		DECORATED_DIALOG
	}
	
	/**
	 * When the popup should show up if the action is displayed as button.
	 * @author Benjamin Sigg
	 */
	public static enum ButtonBehavior{
		/** the popup shows up if the mouse is pressed */
		OPEN_ON_PRESS,
		
		/** the popup shows up if the mouse is released */
		OPEN_ON_CLICK
	}
	
	/** how to handle the case where this action is child of a menu */
	private MenuBehavior menu = MenuBehavior.UNDECORATED_DIALOG;
	
	/** when to show the popup */
	private ButtonBehavior button = ButtonBehavior.OPEN_ON_CLICK;
	
	/** the content of this {@link CPanelPopup} */
	private JComponent content;
	
	/** current open window */
	private PanelPopupWindow window;
	
	/** whether the window is closed automatically if focus is lost */
	private boolean closeOnFocusLost = true;
	
	/** a listener to {@link #window} */
	private PanelPopupWindowListener listener = new PanelPopupWindowListener(){
		public void closed( PanelPopupWindow window ){
			window.removeListener( listener );
			window = null;
		}
	};
	
	/**
	 * Creates a new action.
	 */
	public CPanelPopup(){
		super( null );
		init( new PanelPopup() );
	}
	
	/**
	 * Sets the component that is shown on a popup dialog/menu/window... by
	 * this {@link CPanelPopup}.
	 * @param content the content, may be <code>null</code>
	 */
	public void setContent( JComponent content ){
		this.content = content;
	}
	
	/**
	 * Gets the contents of this action.
	 * @return the contents
	 */
	public JComponent getContent(){
		return content;
	}
	
	/**
	 * Tells this action how to behave if it is in a menu. This may not have an effect
	 * if the action already is part of a menu.
	 * @param menu the behavior, not <code>null</code>
	 */
	public void setMenuBehavior( MenuBehavior menu ){
		if( menu == null )
			throw new IllegalArgumentException( "menu must not be null" );
		this.menu = menu;
	}
	
	/**
	 * Tells how this action behaves if in a menu.
	 * @return the behavior, not <code>null</code>
	 */
	public MenuBehavior getMenuBehavior(){
		return menu;
	}
	
	/**
	 * Tells this action how to handle buttons.
	 * @param button when to open a popup
	 */
	public void setButtonBehavior( ButtonBehavior button ){
		if( button == null )
			throw new IllegalArgumentException( "button must not be null" );
		this.button = button;
	}
	
	/**
	 * Tells how this action behaves if displayed as button.
	 * @return the behavior, not <code>null</code>
	 */
	public ButtonBehavior getButtonBehavior(){
		return button;
	}
	
	/**
	 * Whether the window should be closed if focus is lost.
	 * @param closeOnFocusLost <code>true</code> if it should close automatically
	 */
	public void setCloseOnFocusLost( boolean closeOnFocusLost ){
		this.closeOnFocusLost = closeOnFocusLost;
	}
	
	/**
	 * Tells whether the window is automatically closed if the focus is lost.
	 * @return whether the popup is closed automatically
	 */
	public boolean isCloseOnFocusLost(){
		return closeOnFocusLost;
	}
	
	/**
	 * Informs this {@link CPanelPopup} that its content is shown and
	 * allows this to handle the closing event. 
	 * @param window the window
	 * @throws IllegalArgumentException if {@link PanelPopupWindow#isOpen()}
	 * return <code>false</code>
	 */
	public void openPopup( PanelPopupWindow window ){
		if( !window.isOpen() ){
			throw new IllegalArgumentException( "window is not open" );
		}
		
		closePopup();
		
		this.window = window;
		this.window.addListener( listener );
	}
	
	/**
	 * Makes the current popup invisible.
	 */
	public void closePopup(){
		if( window != null ){
			window.close();
		}
	}
	
	/**
	 * Tells whether the content of this action is currently being showed.
	 * @return <code>true</code> if the content is visible
	 */
	public boolean isOpen(){
		return window != null && window.isOpen();
	}
	
	/**
	 * Called if the mouse is pressed on the button <code>item</code> of
	 * of a {@link DockTitle} which has orientation <code>orientation</code>.
	 * @param dockable the element for which this panel is shown
	 * @param item the pressed component
	 * @param orientation the orientation of the title
	 */
	protected void onMousePressed( Dockable dockable, JComponent item, Orientation orientation ){
		if( getButtonBehavior() == ButtonBehavior.OPEN_ON_PRESS ){
			openDialog( dockable, item, orientation );
		}
	}

	/**
	 * Called if the mouse is released of the button <code>item</code> of
	 * of a {@link DockTitle} which has orientation <code>orientation</code>.
	 * @param dockable the element for which this panel is shown
	 * @param item the released component
	 * @param orientation the orientation of the title
	 */
	protected void onMouseReleased( Dockable dockable, JComponent item, Orientation orientation ){
		if( getButtonBehavior() == ButtonBehavior.OPEN_ON_CLICK ){
			openDialog( dockable, item, orientation );
		}
	}

	/**
	 * Called if the button <code>item</code> of a {@link DockTitle} which has
	 * orientation <code>orientation</code> was triggered.
	 * @param dockable the element for which this panel is shown
	 * @param item the triggered button
	 * @param orientation the orientation of the title
	 */
	protected void onTrigger( Dockable dockable, JComponent item, Orientation orientation ){
		openDialog( dockable, item, orientation );
	}	
	
	/**
	 * Opens a new undecorated dialog below or aside of <code>item</code>. This method
	 * does nothing if {@link #isOpen()} return <code>true</code>.
	 * @param dockable the element for which this panel is shown
	 * @param item the owner of the new dialog
	 * @param orientation the orientation of the title which shows <code>item</code>
	 */
	protected void openDialog( Dockable dockable, final JComponent item, Orientation orientation ){
		if( isOpen() || content == null )
			return;
		
		final Point location = new Point();
		if( orientation.isHorizontal() ){
			location.y = item.getHeight();
		}
		else{
			location.x = item.getWidth();
		}
		
		SwingUtilities.convertPointToScreen( location, item );

		executeOneDockableHasFocus( dockable, new Runnable() {
			public void run() {
				DialogWindow window = createDialogWindow( item );
				window.setUndecorated( true );
				window.setContent( getContent() );
				window.open( location.x, location.y );
				
				openPopup( window );				
			}
		});
	}
	
	/**
	 * Called if the menu-item representing this action has been
	 * hit.
	 * @param dockable the source of the event
	 */
	protected void onMenuItemTrigger( final Dockable dockable ){
		if( content == null )
			return;
		
		closePopup();
		
		executeOneDockableHasFocus( dockable, new Runnable() {
			public void run() {
				DialogWindow window = createDialogWindow( dockable.getComponent() );
				window.setUndecorated( getMenuBehavior() == MenuBehavior.UNDECORATED_DIALOG );
				window.setContent( getContent() );
				window.open( dockable.getComponent() );
				
				openPopup( window );
			}
		});
	}
	
	/**
	 * Creates a new window which will be used as popup for this {@link CPanelPopup}.
	 * @param owner the owner of the window
	 * @return the new window, not <code>null</code>
	 */
	protected DialogWindow createDialogWindow( Component owner ){
		return new DialogWindow( owner, this );
	}
	
	/**
	 * Called if a menu is opening a submenu in which {@link #getContent() the content}
	 * is to be shown.
	 * @param menu the new parent of the content
	 */
	protected void onMenuTrigger( JPopupMenu menu ){
		if( content == null )
			return;
		
		menu.add( content );
		MenuWindow window = createMenuWindow( menu );
		openPopup( window );
	}
	
	/**
	 * Creates a new window which will be used as popup for this {@link CPanelPopup}.
	 * @param menu the owner of the window
	 * @return the new window, not <code>null</code>
	 */
	protected MenuWindow createMenuWindow( JPopupMenu menu ){
		return new MenuWindow( menu );
	}
	
	/**
	 * Calls <code>run</code> once the owning {@link Dockable} of this action has the focus
	 * @param dockable the element for which this panel is shown
	 * @param run some piece of code to run, usually it will open the popup-dialog created by {@link #createDialogWindow(Component)}.
	 * Should be called by the <code>EDT</code>.
	 */
	protected void executeOneDockableHasFocus( Dockable dockable, Runnable run ){
		DockController controller = dockable.getController();
		if( controller != null ){
			controller.getFocusController().onFocusRequestCompletion( run );
		}
		else {
			run.run();
		}
	}
	
	/**
	 * A custom action shows some dialog or window when triggered
	 * @author Benjamin Sigg
	 *
	 */
	public class PanelPopup extends CommonSimpleButtonAction{
		/**
		 * Creates a new action
		 */
		public PanelPopup(){
			super( CPanelPopup.this );
		}
		
		public <V> V createView( ViewTarget<V> target, ActionViewConverter converter, Dockable dockable ){
			return converter.createView( PANEL_POPUP, this, target, dockable );
		}

		public boolean trigger( Dockable dockable ){
			return false;
		}
		
		/**
		 * Gets the {@link CAction} that is represented by this action.
		 * @return the action, never <code>null</code>
		 */
		public CPanelPopup getAction(){
			return CPanelPopup.this;
		}
		
		/**
		 * Called if the mouse is pressed on the button <code>item</code> of
		 * of a {@link DockTitle} which has orientation <code>orientation</code>.
		 * @param dockable the element for which this panel is shown
		 * @param item the pressed component
		 * @param orientation the orientation of the title
		 */
		public void onMousePressed( Dockable dockable, JComponent item, Orientation orientation ){
			CPanelPopup.this.onMousePressed( dockable, item, orientation );
		}

		/**
		 * Called if the mouse is released of the button <code>item</code> of
		 * of a {@link DockTitle} which has orientation <code>orientation</code>.
		 * @param dockable the element for which this panel is shown
		 * @param item the released component
		 * @param orientation the orientation of the title
		 */
		public void onMouseReleased( Dockable dockable, JComponent item, Orientation orientation ){
			CPanelPopup.this.onMouseReleased( dockable, item, orientation );
		}

		/**
		 * Called if the button <code>item</code> of a {@link DockTitle} which has
		 * orientation <code>orientation</code> was triggered.
		 * @param dockable the element for which this panel is shown
		 * @param item the triggered button
		 * @param orientation the orientation of the title
		 */
		public void onTrigger( Dockable dockable, JComponent item, Orientation orientation ){
			CPanelPopup.this.onTrigger( dockable, item, orientation );
		}
		
		/**
		 * Called if the menu-item representing this action has been
		 * hit.
		 * @param dockable the source of the event
		 */
		public void onMenuItemTrigger( Dockable dockable ){
			CPanelPopup.this.onMenuItemTrigger( dockable );
		}
		
		/**
		 * Called if a menu is opening a submenu in which {@link #getContent() the content}
		 * is to be shown.
		 * @param menu the new parent of the content
		 */
		public void onMenuTrigger( JPopupMenu menu ){
			CPanelPopup.this.onMenuTrigger( menu );
		}
	}
}
