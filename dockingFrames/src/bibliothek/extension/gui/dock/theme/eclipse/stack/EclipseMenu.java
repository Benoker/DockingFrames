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
import bibliothek.gui.Dockable;
import bibliothek.gui.dock.station.stack.menu.AbstractCombinedMenu;
import bibliothek.gui.dock.station.stack.tab.TabMenuDockIcon;
import bibliothek.gui.dock.themes.basic.action.BasicTrigger;
import bibliothek.gui.dock.themes.icon.TabMenuOverflowIconBridge;
import bibliothek.gui.dock.util.BackgroundAlgorithm;
import bibliothek.gui.dock.util.BackgroundPaint;

/**
 * A menu used by the {@link EclipseTabPane} to select {@link Dockable}s.<br>
 * The icon of the menu is {@link TabMenuOverflowIconBridge#ICON_KEY}, and thus changes automatically
 * when the number of children changes.
 * @author Benjamin Sigg
 */
public class EclipseMenu extends AbstractCombinedMenu{
	private EclipseTabPane pane;
	private RoundRectButton button;
	
	private TabMenuDockIcon icon;
	private Icon currentIcon;
	
	/**
	 * Creates a new menu.
	 * @param pane the owner of this menu
	 */
	public EclipseMenu( EclipseTabPane pane ){
		super( pane, pane.getMenuHandler() );
		this.pane = pane;
		
		icon = new TabMenuDockIcon( TabMenuOverflowIconBridge.ICON_KEY, this ){
			protected void changed( Icon oldValue, Icon newValue ){
				currentIcon = newValue;
				if( button != null ){
					button.getModel().setIcon( newValue );
				}
			}
		};
	}
	
	@Override
	public void setController( DockController controller ){
		super.setController( controller );
		if( controller == null ){
			icon.setManager(  null );
		}
		else{
			icon.setManager( controller.getIcons() );
		}
	}
	
	@Override
	protected void backgroundChanged( BackgroundPaint paint ){
		if( button != null ){
			button.getModel().setBackground( paint, getBackground() );
		}
	}
	
	@Override
	protected Component createComponent(){
		BasicTrigger trigger = new BasicTrigger(){
        	public void triggered(){
        		open();
        	}
        };
        
        button = new RoundRectButton( trigger, null );
        
        button.getModel().setIcon( currentIcon );
        
        return button;
	}
	
	@Override
	protected void ensureComponent(){
		boolean set = button == null;
		super.ensureComponent();
		if( set ){
			BackgroundAlgorithm background = getBackground();
	        button.getModel().setBackground( background.getPaint(), background );
		}
	}

	@Override
	protected void selected( Dockable dockable ){
		pane.setSelectedDockable( dockable );		
	}
}
