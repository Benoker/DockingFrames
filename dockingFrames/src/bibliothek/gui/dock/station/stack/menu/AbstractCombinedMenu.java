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
package bibliothek.gui.dock.station.stack.menu;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Rectangle;

import javax.swing.Icon;

import bibliothek.gui.DockController;
import bibliothek.gui.Dockable;
import bibliothek.gui.dock.station.stack.CombinedMenu;
import bibliothek.gui.dock.station.stack.tab.TabPane;
import bibliothek.gui.dock.util.PropertyValue;

/**
 * An abstract implementation of {@link CombinedMenu}, this menu
 * delegates creation and management of its {@link Component} to its
 * subclasses and uses a {@link CombinedMenuContent} to show its content.
 * @author Benjamin Sigg
 */
public abstract class AbstractCombinedMenu implements CombinedMenu{
	/** owner of this menu */
	private TabPane parent;
	
	/** the button of this menu */
	private Component component;
	
	/** the content of this menu */
	private PropertyValue<CombinedMenuContent> content = new PropertyValue<CombinedMenuContent>( 
			CombinedMenuContent.MENU_CONTENT ){
		@Override
		protected void valueChanged( CombinedMenuContent oldValue, CombinedMenuContent newValue ){
			// ignore
		}
	};
	
	/** the items of this menu */
	private Entry[] entries;
	
	/** the controller in whose realm this menu is used */
	private DockController controller;
	
	/**
	 * Creates a new menu.
	 * @param parent the owner of this menu, must not be <code>null</code>
	 * @param dockables the items of this menu, must not be <code>null</code> or empty
	 */
	public AbstractCombinedMenu( TabPane parent, Dockable[] dockables ){
		if( parent == null )
			throw new IllegalArgumentException( "parent must not be null" );
		
		if( dockables == null )
			throw new IllegalArgumentException( "dockables must not be null" );
		
		if( dockables.length < 1 )
			throw new IllegalArgumentException( "dockables must contain at least one entry" );
		
		entries = new Entry[ dockables.length ];
		for( int i = 0; i < entries.length; i++ ){
			Entry entry = new Entry();
			entry.dockable = dockables[i];
			entry.text = entry.dockable.getTitleText();
			entry.tooltip = entry.dockable.getTitleToolTip();
			entry.icon = entry.dockable.getTitleIcon();
		}
		
		
	}
	
	/**
	 * Ensures that {@link #createComponent()} is called and its result
	 * stored.
	 */
	protected void ensureComponent(){
		if( component == null ){
			component = createComponent();
		}
	}
	
	/**
	 * Creates the button which will always be visible. The user needs to
	 * click onto that button in order to show the content of this menu. This
	 * method will be called only once and not from a constructor of
	 * {@link AbstractCombinedMenu}.
	 * @return the new button
	 */
	protected abstract Component createComponent();
	
	/**
	 * Opens a menu where the user can select a {@link Dockable}.
	 */
	public void open(){
		Handler handler = new Handler();
		handler.open();
	}
	
	/**
	 * Called once the menu is closed, the default implementation does
	 * nothing.
	 */
	protected void closed(){
		// nothing
	}
	
	public void setController( DockController controller ){
		this.controller = controller;
		content.setProperties( controller );
	}
	
	/**
	 * Gets the controller in whose realm this menu is used.
	 * @return the controller, might be <code>null</code>
	 */
	public DockController getController(){
		return controller;
	}
	
	/**
	 * Called if this menu was open, an element was selected and the menu closed.
	 * @param dockable the selected element
	 */
	protected abstract void selected( Dockable dockable );
	
	public Component getComponent(){
		ensureComponent();
		return component;
	}

	public void setIcon( int index, Icon icon ){
		entries[ index ].icon = icon;
	}

	public void setText( int index, String text ){
		entries[ index ].text = text;
	}

	public void setTooltip( int index, String tooltip ){
		entries[ index ].tooltip = tooltip;
	}

	public Dockable[] getDockables(){
		Dockable[] result = new Dockable[ entries.length ];
		for( int i = 0; i < result.length; i++ ){
			result[i] = entries[i].dockable;
		}
		return result;
	}

	public Rectangle getBounds(){
		ensureComponent();
		return component.getBounds();
	}

	public Dimension getMaximumSize(){
		ensureComponent();
		return component.getMaximumSize();
	}

	public Dimension getMinimumSize(){
		ensureComponent();
		return component.getMinimumSize();
	}

	public Dimension getPreferredSize(){
		ensureComponent();
		return component.getPreferredSize();
	}

	public TabPane getTabParent(){
		return parent;
	}

	public void setBounds( Rectangle bounds ){
		ensureComponent();
		component.setBounds( bounds );
	}
	
	/**
	 * A handler for handling opening, closing and clean up of a {@link CombinedMenuContent}.
	 * @author Benjamin Sigg
	 */
	private class Handler implements CombinedMenuContentListener{
		private CombinedMenuContent menu;
		
		/**
		 * Creates a new handler using the current menu.
		 */
		public Handler(){
			menu = content.getValue();
		}
		
		/**
		 * Opens the menu of this handler.
		 */
		public void open(){
			if( menu == null )
				return;
			
			Component component = getComponent();
			
			CombinedMenuContent.Item[] items = new CombinedMenuContent.Item[ entries.length ];
			for( int i = 0; i < items.length; i++ ){
				items[i] = entries[i].toItem();
			}
			menu.addCombinedMenuContentListener( this );
			menu.open( controller, component, 0, component.getHeight(), items );
		}
		
		public void opened( CombinedMenuContent menu ){
			// ignore	
		}
		
		public void canceled( CombinedMenuContent menu ){
			menu.removeCombinedMenuContentListener( this );
		}
		
		public void selected( CombinedMenuContent menu, Dockable selection ){
			menu.removeCombinedMenuContentListener( this );
			AbstractCombinedMenu.this.selected( selection );
		}
	}
	
	/**
	 * One entry of this menu. 
	 */
	private class Entry{
		/** the element of this entry */
		public Dockable dockable;
		/** the title */
		public String text;
		/** description of this entry */
		public String tooltip;
		/** small icon */
		public Icon icon;
		
		/**
		 * Creates a new item out of this entry.
		 * @return the new item
		 */
		public CombinedMenuContent.Item toItem(){
			return new CombinedMenuContent.Item( dockable, text, tooltip, icon );
		}
	}
}
