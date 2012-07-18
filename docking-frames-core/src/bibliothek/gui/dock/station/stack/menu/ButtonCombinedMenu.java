/*
 * Bibliothek - DockingFrames
 * Library built on Java/Swing, allows the user to "drag and drop"
 * panels containing any Swing-Component the developer likes to add.
 * 
 * Copyright (C) 2011 Benjamin Sigg
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
package bibliothek.gui.dock.station.stack.menu;

import java.awt.Component;

import javax.swing.Icon;

import bibliothek.gui.DockController;
import bibliothek.gui.Dockable;
import bibliothek.gui.dock.action.ActionContentModifier;
import bibliothek.gui.dock.action.DockAction;
import bibliothek.gui.dock.station.stack.CombinedHandler;
import bibliothek.gui.dock.station.stack.CombinedMenu;
import bibliothek.gui.dock.station.stack.tab.TabMenuDockIcon;
import bibliothek.gui.dock.station.stack.tab.TabPane;
import bibliothek.gui.dock.themes.basic.action.BasicButtonModel;
import bibliothek.gui.dock.themes.basic.action.BasicTrigger;
import bibliothek.gui.dock.themes.icon.TabMenuOverflowIconBridge;
import bibliothek.gui.dock.util.BackgroundAlgorithm;
import bibliothek.gui.dock.util.BackgroundPaint;

/**
 * A {@link CombinedMenu} intended to be subclasses. Subclasses build a simple {@link Component} which serves as some 
 * kind of button, and allow this menu to set the icon, text, etc.. through a {@link BasicButtonModel}. 
 * @author Benjamin Sigg
 * @param <B> the button created and used by this menu
 */
public abstract class ButtonCombinedMenu<B extends Component> extends AbstractCombinedMenu{
	private B button;
	
	private TabMenuDockIcon icon;
	private Icon currentIcon;
	
	/**
	 * Creates a new menu.
	 * @param pane the owner of this menu
	 * @param handler handler for making this menu visible or invisible and change the z order 
	 */
	public ButtonCombinedMenu( TabPane pane, CombinedHandler<? super AbstractCombinedMenu> handler ){
		super( pane, handler );
		
		icon = new TabMenuDockIcon( TabMenuOverflowIconBridge.ICON_KEY, this ){
			protected void changed( Icon oldValue, Icon newValue ){
				currentIcon = newValue;
				if( button != null ){
					getModel( button ).setIcon( ActionContentModifier.NONE, newValue );
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
			getModel( button ).setBackground( paint, getBackground() );
		}
	}
	
	@Override
	protected Component createComponent(){
		BasicTrigger trigger = new BasicTrigger(){
        	public void triggered(){
        		open();
        	}
        	public DockAction getAction(){
        		return null;
        	}
        	public Dockable getDockable(){
        		return null;
        	}
        };
        
        button = createButton( trigger );
        
        getModel( button ).setIcon( ActionContentModifier.NONE, currentIcon );
        
        return button;
	}
	
	/**
	 * Gets the button that was created by {@link #createButton(BasicTrigger)}.
	 * @return the button, may be <code>null</code>
	 */
	public B getButton(){
		return button;
	}
	
	/**
	 * Creates a new button for this menu. There are no limitations of what a button really is, as long
	 * as it is a {@link Component}.
	 * @param trigger a trigger to call {@link #open()}, can be ignored.
	 * @return the newly created button
	 */
	protected abstract B createButton( BasicTrigger trigger );
	
	/**
	 * Gets a {@link BasicButtonModel} which is used to interact with <code>button</code>, this method
	 * may be called many times for the same button. 
	 * @param button the button whose model is requested
	 * @return the model, not <code>null</code>
	 */
	protected abstract BasicButtonModel getModel( B button );
	
	@Override
	protected void ensureComponent(){
		boolean set = button == null;
		super.ensureComponent();
		if( set ){
			BackgroundAlgorithm background = getBackground();
			getModel( button ).setBackground( background.getPaint(), background );
		}
	}

	@Override
	protected void selected( Dockable dockable ){
		getTabParent().setSelectedDockable( dockable );		
	}
}
