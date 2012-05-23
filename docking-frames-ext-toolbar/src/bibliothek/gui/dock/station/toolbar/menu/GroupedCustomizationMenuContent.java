/*
 * Bibliothek - DockingFrames
 * Library built on Java/Swing, allows the user to "drag and drop"
 * panels containing any Swing-Component the developer likes to add.
 * 
 * Copyright (C) 2012 Herve Guillaume, Benjamin Sigg
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
 * Herve Guillaume
 * rvguillaume@hotmail.com
 * FR - France
 *
 * Benjamin Sigg
 * benjamin_sigg@gmx.ch
 * CH - Switzerland
 */
package bibliothek.gui.dock.station.toolbar.menu;

import java.awt.Component;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JPanel;

import bibliothek.gui.DockController;

/**
 * The grouped customization menu is a panel showing groups of other {@link CustomizationMenuContent}. The panel
 * can insert titles and separators between its children
 * @author Benjamin Sigg
 */
public class GroupedCustomizationMenuContent implements CustomizationMenuContent{
	/** how many children to present on one line */
	private int columns = 5;
	
	/** The grouped children */
	private List<Group> groups = new ArrayList<GroupedCustomizationMenuContent.Group>();

	/** the panel showing all the children of this menu */
	private JPanel view;
	
	/** the controller in whose realm this menu is shown */
	private DockController controller;
	
	/** callback to the component showing the menu */
	private CustomizationMenuCallback callback;
	
	/** insets added to all components */
	private Insets insets = new Insets( 2, 2, 2, 2 );
	
	/**
	 * Creates a new {@link Group} with a new {@link GroupedCustomizationMenuTitle}, and 
	 * {@link #addGroup(Group) adds} this group to this menu.
	 * @param title the title of the group, can be <code>null</code>
	 * @return the new group
	 */
	public Group addGroup( String title ){
		Group group = new Group( new GroupedCustomizationMenuTitle( title ) );
		addGroup( group );
		return group;
	}
	
	/**
	 * Adds a new group to this menu.
	 * @param group the new group, not <code>null</code>
	 */
	public void addGroup( Group group ){
		if( group == null ){
			throw new IllegalArgumentException( "group is null" );
		}
		if( groups.contains( group )){
			throw new IllegalArgumentException( "group was already added to this menu" );
		}
		if( group.getOwner() != this ){
			throw new IllegalArgumentException( "group was not created using this menu object" );
		}
		groups.add( group );
	}
	
	/**
	 * Gets the number of {@link Group}s of this menu.
	 * @return the number of groups
	 */
	public int getGroupCount(){
		return groups.size();
	}
	
	/**
	 * Gets the <code>index</code>'th group of this menu.
	 * @param index the index of the group
	 * @return
	 */
	public Group getGroup( int index ){
		return groups.get( index );
	}

	/**
	 * Removes <code>group</code> from this menu. Nothing happens if this menu is currently shown.
	 * @param group the group to remove
	 */
	public void removeGroup( Group group ){
		groups.remove( group );
	}
	
	/**
	 * Removes the <code>index</code>'th group from this menu. Nothing happens if this menu is currently shown.
	 * @param index the index of the group to remove
	 */
	public void removeGroup( int index ){
		groups.remove( index );
	}
	
	@Override
	public Component getView(){
		return view;
	}
	
	@Override
	public void setController( DockController controller ){
		if( this.controller != controller ){	
			this.controller = controller;
			if( callback != null ){
				for( Group group : groups ){
					group.setController( controller );
				}
			}
		}
	}
	
	@Override
	public void bind( CustomizationMenuCallback callback ){
		this.callback = callback;
		for( Group group : groups ){
			group.setController( controller );
			group.bind( callback );
		}
		view = new JPanel( new GridBagLayout() );
		int offset = 0;
		for( Group group : groups ){
			offset += group.insertItems( offset );
		}
	}
	
	@Override
	public void unbind(){
		for( Group group : groups ){
			group.unbind();
			group.setController( null );
		}
	}
	
	/**
	 * One group of {@link CustomizationMenuContent}s
	 * @author Benjamin Sigg
	 */
	public class Group{
		private CustomizationMenuContent title;
		
		/** the children of this group */
		private List<CustomizationMenuContent> items = new ArrayList<CustomizationMenuContent>();
		
		/**
		 * Creates a new group using <code>title</code> as title component.
		 * @param title a component shown at the top as header, can be <code>null</code>
		 */
		public Group( CustomizationMenuContent title ){
			this.title = title;
		}
		
		/**
		 * Sets the title component. Nothing happens if the menu is currently shown.
		 * @param title the new title, can be <code>null</code>
		 */
		public void setTitle( CustomizationMenuContent title ){
			this.title = title;
		}
		
		/**
		 * Gets the current title component.
		 * @return the current title, may be <code>null</code>
		 */
		public CustomizationMenuContent getTitle(){
			return title;
		}
		
		private GroupedCustomizationMenuContent getOwner(){
			return GroupedCustomizationMenuContent.this;
		}
		
		private void setController( DockController controller ){
			if( title != null ){
				title.setController( controller );
			}
			for( CustomizationMenuContent item : items ){
				item.setController( controller );
			}
		}
		
		private void bind( CustomizationMenuCallback callback ){
			if( title != null ){
				title.bind( callback );
			}
			for( CustomizationMenuContent item : items ){
				item.bind( callback );
			}
		}
		
		/**
		 * Adds a set of {@link Component}s to the current view, using some 
		 * {@link GridBagConstraints}.
		 * @param offset in which row to start
		 * @return how many rows were used up
		 */
		private int insertItems( int offset ){
			int begin = offset;
			if( title != null ){
				view.add( title.getView(), new GridBagConstraints( 0, offset, columns, 1, 1.0, 0.01, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, insets, 0, 0 ) );
				offset++;
			}
			int index = 0;
			int length = items.size();
			while( index < length ){
				for( int i = 0; i < columns && index < length; i++ ){
					view.add( items.get( index++ ).getView(), new GridBagConstraints( i, offset, 1, 1, 1.0, 1.0, GridBagConstraints.CENTER, GridBagConstraints.BOTH, insets, 0, 0  ));
				}
				offset++;
			}
			return offset - begin;
		}
		
		private void unbind(){
			if( title != null ){
				title.unbind();
			}
			for( CustomizationMenuContent item : items ){
				item.unbind();
			}
		}
		
		/**
		 * Adds <code>item</code> to this group, there is no effect if the menu is currently showing.
		 * @param item the item to add, not <code>null</code>
		 */
		public void add( CustomizationMenuContent item ){
			add( getItemCount(), item );
		}

		/**
		 * Adds <code>item</code> to this group, there is no effect if the menu is currently showing.
		 * @param index to location where to add <code>item</code>
		 * @param item the item to add, not <code>null</code>
		 */
		public void add( int index, CustomizationMenuContent item ){
			items.add( index, item );
		}
		
		/**
		 * Removes <code>item</code> from this group. There is no effect it the menu is currently showing.
		 * @param item the item to remove
		 */
		public void remove( CustomizationMenuContent item ){
			if( items.remove( item ) ){
				
			}
		}
		
		/**
		 * Removes <code>index</code>'th item from this group. There is no effect it the menu is currently showing.
		 * @param index the index of the item to remove
		 */
		public void remove( int index ){
			items.remove( index );
		}
		
		/**
		 * Gets the number of icons in this group
		 * @return the number of icons
		 */
		public int getItemCount(){
			return items.size();
		}
		
		/**
		 * Gets the <code>index</code>'th item of this group.
		 * @param index the index of the item
		 * @return the item at <code>index</code>
		 */
		public CustomizationMenuContent getItem( int index ){
			return items.get( index );
		}
	}
}
