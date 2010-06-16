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
package bibliothek.extension.gui.dock.theme.eclipse.stack;

import java.awt.Component;

import javax.swing.Icon;

import bibliothek.extension.gui.dock.theme.eclipse.RoundRectButton;
import bibliothek.gui.DockController;
import bibliothek.gui.DockUI;
import bibliothek.gui.Dockable;
import bibliothek.gui.dock.event.IconManagerListener;
import bibliothek.gui.dock.station.stack.menu.AbstractCombinedMenu;
import bibliothek.gui.dock.themes.basic.action.BasicTrigger;

/**
 * A menu used by the {@link EclipseTabPane} to select {@link Dockable}s.
 * @author Benjamin Sigg
 */
public class EclipseMenu extends AbstractCombinedMenu{
	private EclipseTabPane pane;
	private RoundRectButton button;
	
	private IconManagerListener iconListener = new IconManagerListener(){
		public void iconChanged( String key, Icon icon ){
			if( DockUI.OVERFLOW_MENU_ICON.equals( key )){
				if( button != null ){
					button.getModel().setIcon( icon );
				}
			}
		}
	};
	
	/**
	 * Creates a new menu.
	 * @param pane the owner of this menu
	 */
	public EclipseMenu( EclipseTabPane pane ){
		super( pane, pane.getMenuHandler() );
		this.pane = pane;
	}
	
	@Override
	public void setController( DockController controller ){
		DockController old = getController();
		if( old != null ){
			old.getIcons().remove( DockUI.OVERFLOW_MENU_ICON, iconListener );
		}
		
		super.setController( controller );
		
		if( controller != null ){
			controller.getIcons().add( DockUI.OVERFLOW_MENU_ICON, iconListener );
			if( button != null ){
				button.getModel().setIcon( controller.getIcons().getIcon( DockUI.OVERFLOW_MENU_ICON ) );
			}
		}
		else{
			if( button != null ){
				button.getModel().setIcon( null );
			}
		}
	}
	
	@Override
	protected Component createComponent(){
		BasicTrigger trigger = new BasicTrigger(){
        	public void triggered(){
        		open();
        	}
        };
        button = new RoundRectButton( trigger );
        
        DockController controller = getController();
        if( controller != null ){
        	button.getModel().setIcon( controller.getIcons().getIcon( DockUI.OVERFLOW_MENU_ICON ));
        }
        
        return button;
	}

	@Override
	protected void selected( Dockable dockable ){
		pane.setSelectedDockable( dockable );		
	}
}
