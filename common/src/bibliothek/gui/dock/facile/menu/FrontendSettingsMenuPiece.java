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
package bibliothek.gui.dock.facile.menu;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Set;

import javax.swing.JMenuItem;
import javax.swing.JOptionPane;

import bibliothek.gui.DockFrontend;
import bibliothek.gui.dock.support.menu.SeparatingMenuPiece;
import bibliothek.gui.dock.support.util.Resources;

/**
 * A piece of a menu that allows to save, load and delete settings
 * from a {@link DockFrontend}.
 * @author Benjamin Sigg
 */
public class FrontendSettingsMenuPiece extends NodeMenuPiece {
	/** menu to delete items */
	private FrontendSettingsDeleteList delete;
	/** menu to load items */
	private FrontendSettingsLoadList load;
	
	/** the frontend which might be modified by this piece */
	private DockFrontend frontend;
	
	/**
	 * Creates a new piece.
	 * @param frontend the frontend which might be modified by this piece, can be <code>null</code>
	 * @param loadAsSubmenu whether the list of loadable settings should be in a submenu
	 */
	public FrontendSettingsMenuPiece( DockFrontend frontend, boolean loadAsSubmenu ){
		delete = new FrontendSettingsDeleteList( frontend );
		load = new FrontendSettingsLoadList( frontend );
		this.frontend = frontend;
		
		FreeMenuPiece menu = new FreeMenuPiece();
		
		JMenuItem save = new JMenuItem( Resources.getBundle().getString( "FrontendSettingsMenuPiece.save" ) );
		JMenuItem saveAs = new JMenuItem( Resources.getBundle().getString( "FrontendSettingsMenuPiece.saveAs" ) );
		
		menu.add( save );
		menu.add( saveAs );
		
		if( loadAsSubmenu ){
			RootMenuPiece rootLoad = new RootMenuPiece( Resources.getBundle().getString( "FrontendSettingsMenuPiece.load" ), true );
			menu.add( rootLoad.getMenu() );
			rootLoad.add( load );
		}
		
		RootMenuPiece rootDelete = new RootMenuPiece( Resources.getBundle().getString( "FrontendSettingsMenuPiece.delete" ), true );
		rootDelete.add( delete );
		menu.add( rootDelete.getMenu() );
		
		add( menu );
		if( !loadAsSubmenu ){
			add( new SeparatingMenuPiece( load, true, false, false ) );
		}
		
		save.addActionListener( new ActionListener(){
			public void actionPerformed( ActionEvent e ){
				save( (Component)e.getSource() );
			}
		});
		
		saveAs.addActionListener( new ActionListener(){
			public void actionPerformed( ActionEvent e ){
				saveAs( (Component)e.getSource() );
			}
		});
	}
	
	/**
	 * Gets the frontend which might be modified by this menu.
	 * @return the frontend, an be <code>null</code>
	 */
	public DockFrontend getFrontend(){
		return frontend;
	}
	
	/**
	 * Sets the frontend which might be modified by this menu.
	 * @param frontend the frontend, can be <code>null</code>
	 */
	public void setFrontend( DockFrontend frontend ){
		this.frontend = frontend;
		load.setFrontend( frontend );
		delete.setFrontend( frontend );
	}
	
	/**
	 * Saves the current setting.
	 * @param owner the component which should be used as owner of any
	 * necessary dialog
	 */
	public void save( Component owner ){
		if( frontend != null ){
			if( frontend.getCurrentSetting() == null )
				saveAs( owner );
			else
				frontend.save();
		}
	}

	/**
	 * Saves the current setting under a new name.
	 * @param owner which should be used as owner of any
	 * necessary dialog
	 */
	public void saveAs( Component owner ){
		if( frontend != null ){
			String input = JOptionPane.showInputDialog( owner,  Resources.getBundle().getString( "FrontendSettingsMenuPiece.saveAsInput" ));
			if( input != null ){
				Set<String> settings = frontend.getSettings();
				if( settings.contains( input )){
					int count = 1;
					while( settings.contains( input + " (" + count + ")" ))
						count++;
					input = input + " (" + count + ")";
				}
				frontend.save( input );
			}
		}
	}
}

