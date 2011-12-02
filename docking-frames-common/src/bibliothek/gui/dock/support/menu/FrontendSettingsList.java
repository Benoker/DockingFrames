/*
 * Bibliothek - DockingFrames
 * Library built on Java/Swing, allows the user to "drag and drop"
 * panels containing any Swing-Component the developer likes to add.
 * 
 * Copyright (C) 2007 Benjamin Sigg
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
package bibliothek.gui.dock.support.menu;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.Collator;
import java.util.HashMap;
import java.util.Map;

import javax.swing.JMenuItem;
import javax.swing.JRadioButtonMenuItem;

import bibliothek.gui.DockFrontend;
import bibliothek.gui.dock.event.DockFrontendAdapter;

/**
 * A list of settings (=layouts or perspectives) that are available for a {@link bibliothek.gui.DockFrontend}.
 * The list contains the elements of {@link bibliothek.gui.DockFrontend#getSettings()}.
 * @author Benjamin Sigg
 *
 */
public abstract class FrontendSettingsList extends BaseMenuPiece {
	/** the observed frontend */
	private DockFrontend frontend;
	
	/** a listener adding or removing elements when settings are added or removed from {@link #frontend} */
	private Listener listener = new Listener();
	
	/** the list of visible items */
	private Map<String, JRadioButtonMenuItem> items = new HashMap<String, JRadioButtonMenuItem>();
	
	/** tells how to order the items of this list */
	private Collator order = Collator.getInstance();
	
	/**
	 * Creates a new list.
	 * @param frontend the frontend to observe, can be <code>null</code>
	 */
	public FrontendSettingsList( DockFrontend frontend ){
		setFrontend( frontend );
	}

	/**
	 * Gets the frontend which is currently observed
	 * @return the frontend or <code>null</code>
	 */
	public DockFrontend getFrontend(){
		return frontend;
	}
	
	/**
	 * Changes the frontend which is observed by this list. The list will
	 * always show one item for each setting that can be found in 
	 * {@link DockFrontend#getSettings()}.
	 * @param frontend the frontend to observe, can be <code>null</code>
	 */
	public void setFrontend( DockFrontend frontend ){
		if( this.frontend != frontend ){
			if( isBound() ){
				uninstall();
			}
			
			this.frontend = frontend;
			
			if( isBound() ){
				install();
			}
		}
	}
	
	@Override
	public void bind(){
		if( !isBound() ){
			super.bind();
			install();
		}
	}
	
	@Override
	public void unbind(){
		if( isBound() ){
			super.unbind();
			uninstall();
		}
	}
	
	private void install(){
		if( this.frontend != null ){
			this.frontend.addFrontendListener( listener );
			for( String name : frontend.getSettings() )
				add( name );
		}	
	}
	
	private void uninstall(){
		if( this.frontend != null ){
			int count = getItemCount();
			while( count > 0 ){
				remove( --count );
			}
			items.clear();
			this.frontend.removeFrontendListener( listener );
		}	
	}
	
	/**
	 * Called when the user clicks on an item of this list.
	 * @param name the name of the item
	 */
	protected abstract void action( String name );
	
	/**
	 * Adds an item to the list.
	 * @param name the name of the new setting
	 */
	private void add( final String name ){
		JRadioButtonMenuItem item = new JRadioButtonMenuItem( name );
		item.addActionListener( new ActionListener(){
			public void actionPerformed( ActionEvent e ){
				action( name );
				updateSelection();
			}
		});
		
		insert( preferredIndex( name ), item );
		items.put( name, item );
		updateSelection();
	}
	
	protected int preferredIndex( String name ){
		int index = 0;
		for( String item : items.keySet() ){
			if( order.compare( item, name ) < 0 ){
				index++;
			}
		}
		return index;
	}
	
	/**
	 * Removes an item from the list.
	 * @param name the of the setting that was removed
	 */
	private void remove( String name ){
		JMenuItem item = items.remove( name );
		if( item != null ){
			remove( item );
			updateSelection();
		}
	}
	
	/**
	 * Updates the selection state of the {@link JRadioButtonMenuItem}s that are used
	 * on this menu. Only the one item representing the current layout of the {@link DockFrontend}
	 * is selected.
	 */
	public void updateSelection(){
		String name = frontend.getCurrentSetting();
		for( Map.Entry<String, JRadioButtonMenuItem> entry : items.entrySet() ){
			entry.getValue().setSelected( entry.getKey().equals( name ) );
		}
	}
	
	/**
	 * A listener adding or removing items.
	 * @author Benjamin Sigg
	 */
	private class Listener extends DockFrontendAdapter{
		@Override
		public void deleted( DockFrontend frontend, String name ){
			remove( name );
		}
		@Override
		public void saved( DockFrontend frontend, String name ){
			if( !items.containsKey( name )){
				add( name );
			}
			else{
				updateSelection();
			}
		}
		@Override
		public void read( DockFrontend frontend, String name ){
			if( !items.containsKey( name )){
				add( name );
			}
			else{
				updateSelection();
			}
		}
		
		@Override
		public void loaded( DockFrontend frontend, String name ){
			updateSelection();
		}
	}
}