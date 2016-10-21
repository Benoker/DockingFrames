/*
 * Bibliothek - DockingFrames
 * Library built on Java/Swing, allows the user to "drag and drop"
 * panels containing any Swing-Component the developer likes to add.
 * 
 * Copyright (C) 2010 Benjamin Sigg
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
package bibliothek.gui.dock.util.icon;

import java.awt.Component;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.swing.Icon;

import bibliothek.gui.DockController;
import bibliothek.gui.DockUI;
import bibliothek.gui.dock.station.stack.tab.TabMenuDockIcon;
import bibliothek.gui.dock.themes.icon.TabMenuOverflowIconBridge;
import bibliothek.gui.dock.util.DockUtilities;
import bibliothek.gui.dock.util.UIProperties;
import bibliothek.gui.dock.util.UIScheme;
import bibliothek.gui.dock.util.UISchemeEvent;
import bibliothek.util.Path;

/**
 * This default implementation of an {@link UIScheme} for {@link Icon}s reads an ini-file
 * which consists of "key=icon-path" pairs, and loads all the icons described in that 
 * ini file when needed.
 * @author Benjamin Sigg
 */
public class DefaultIconScheme extends AbstractIconScheme {
	private Map<String, Icon> icons;
	private Map<Path, DockIconBridge> bridges;
	
	/**
	 * A helper class describing a source for icons
	 * @author Benjamin Sigg
	 */
	public static class IconResource{
		private String fileName;
		private String path;
		private ClassLoader loader;
		
		/**
		 * Creates a new source for icons
		 * @param fileName the name of the file that contains "key=path" values
		 * @param path the prefix of the paths that are found in the file
		 * @param loader the {@link ClassLoader} to load the icons
		 */
		public IconResource( String fileName, String path, ClassLoader loader ){
			if( fileName == null ){
				throw new IllegalArgumentException( "fileName must not be null" );
			}
			this.fileName = fileName;
			this.path = path;
			if( loader == null ){
				loader = DefaultIconScheme.class.getClassLoader();
			}
			this.loader = loader;
		}
		
		/**
		 * Gets the name of the file with the "key=path" values.
		 * @return the file name
		 */
		public String getFileName(){
			return fileName;
		}
		
		/**
		 * Gets the prefix of the paths that are found in the icons file.
		 * @return the prefix, can be <code>null</code>
		 */
		public String getPath(){
			return path;
		}
		
		/**
		 * Gets the {@link ClassLoader} which should be used to load files
		 * @return the class loader
		 */
		public ClassLoader getLoader(){
			return loader;
		}
	}
	
	/**
	 * Creates a new scheme loading first the contents of the ini file <code>file</code> and
	 * then the icons that are found by analyzing the content of <code>file</code>.<br>
	 * If no file is found, then this scheme just remains empty.
	 * @param file the file to read
	 * @param controller the {@link DockController} in whose realm this scheme will be used
	 */
	public DefaultIconScheme( String file, DockController controller ){
		this( file, DefaultIconScheme.class.getClassLoader(), controller );
	}
	
	/**
	 * Creates a new scheme loading first the contents of the ini file <code>file</code> and
	 * then the icons that are found by analyzing the content of <code>file</code>.<br>
	 * If no file is found, then this scheme just remains empty.
	 * @param file the file to read
	 * @param loader the {@link ClassLoader} whose {@link ClassLoader#getResource(String)} method
	 * will be used to load any files
	 * @param controller the {@link DockController} in whose realm this scheme will be used
	 */
	public DefaultIconScheme( String file, ClassLoader loader, DockController controller ){
		this( controller, new IconResource( file, null, loader ));
	}
	
	/**
	 * Creates a new scheme loading icons from all the specified resources.
	 * @param controller the {@link DockController} in whose realm this scheme will be used
	 * @param resources a list of files with "key=path" lines telling key and path of the icons to load. If a key
	 * appears more than once, then the last occurrence of the key wins
	 */
	public DefaultIconScheme( DockController controller, IconResource... resources ){
		super( controller );
		
		icons = new HashMap<String, Icon>();
		bridges = new HashMap<Path, DockIconBridge>();
		
		initHardcoded();
		
		for( int i = resources.length-1; i >= 0; i-- ){
			icons.putAll( DockUtilities.loadIcons( resources[i].getFileName(), resources[i].getPath(), icons.keySet(), resources[i].getLoader() ) );
		}
	}
	
	/**
	 * Called by the constructor of this class, initializes some hard coded icons.
	 */
	protected void initHardcoded(){
		setBridge( TabMenuDockIcon.KIND_TAB_MENU, new TabMenuOverflowIconBridge() );
		
		setIcon( DockUI.OVERFLOW_MENU_ICON, new Icon(){
			public int getIconHeight(){
				return 7;
			}

			public int getIconWidth(){
				return 9;
			}

			public void paintIcon( Component c, Graphics g, int x, int y ){
				g = g.create();
				((Graphics2D)g).setRenderingHint( RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON );
				g.setColor( c.getForeground() );
				
				g.fillPolygon( 
						new int[]{ x + 1, x + 8, x + 4 },
						new int[]{ y + 1, y + 1, y + 6 }, 3 );
				g.dispose();
			}
        });
	}

	public DockIconBridge getBridge( Path name, UIProperties<Icon, DockIcon, DockIconBridge> properties ){
		return bridges.get( name );
	}

	public Icon getResource( String name, UIProperties<Icon, DockIcon, DockIconBridge> properties ){
		return icons.get( name );
	}
	
	/**
	 * Changes the icon with name <code>name</code> to <code>icon</code>. Please note that if
	 * <code>name</code> was {@link #link(bibliothek.gui.dock.util.PropertyKey, String) linked}, the newly
	 * set value may be overridden again.
	 * @param name the unique key of the icon
	 * @param icon the new icon, can be <code>null</code>
	 */
	public void setIcon( String name, Icon icon ){
		changed( name, icon );
	}
	
	/**
	 * Sets the {@link DockIconBridge} with type <code>type</code>.
	 * @param type the unique identifier of the type that should be handled by the new bridge
	 * @param bridge the new bridge or <code>null</code>
	 */
	public void setBridge( final Path type, DockIconBridge bridge ){
		if( bridge == null ){
			bridges.remove( type );
		}
		else{
			bridges.put( type, bridge );
		}
		
		fire( new UISchemeEvent<Icon, DockIcon, DockIconBridge>(){
			public Collection<Path> changedBridges( Set<Path> names ){
				List<Path> result = new ArrayList<Path>();
				if( names == null || names.contains( type )){
					result.add( type );
				}
				return result;
			}

			public Collection<String> changedResources( Set<String> names ){
				return Collections.emptySet();
			}

			public UIScheme<Icon, DockIcon, DockIconBridge> getScheme(){
				return DefaultIconScheme.this;
			}
			
		});
	}
	
	@Override
	protected void changed( final String id, Icon icon ){
		if( icon == null ){
			icons.remove( id );
		}
		else{
			icons.put( id, icon );
		}
		
		fire( new UISchemeEvent<Icon, DockIcon, DockIconBridge>(){
			public UIScheme<Icon, DockIcon, DockIconBridge> getScheme(){
				return DefaultIconScheme.this;
			}
			
			public Collection<String> changedResources( Set<String> names ){
				List<String> list = new ArrayList<String>( 1 );
				if( names == null || names.contains( id )){
					list.add( id );
				}
				return list;
			}
			
			public Collection<Path> changedBridges( Set<Path> names ){
				return Collections.emptySet();
			}
		});
	}
}
