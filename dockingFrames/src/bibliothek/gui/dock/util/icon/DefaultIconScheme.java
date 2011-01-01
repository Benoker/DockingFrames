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
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.swing.Icon;

import bibliothek.gui.DockController;
import bibliothek.gui.DockUI;
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
	
	/**
	 * Creates a new scheme loading first the contents of the ini file <code>file</code> and
	 * then the icons that are found by analyzing the content of <code>file</code>.<br>
	 * If no file is found, then this scheme just remains empty.
	 * @param file the file to read
	 * @param controller the {@link DockController} in whose realm this scheme will be used
	 * @see #DefaultIconScheme(String, ClassLoader)
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
		super( controller );
		
		icons = DockUtilities.loadIcons( file, null, loader );
		
        icons.put( DockUI.OVERFLOW_MENU_ICON, new Icon(){
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
		return null;
	}

	public Icon getResource( String name, UIProperties<Icon, DockIcon, DockIconBridge> properties ){
		return icons.get( name );
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
				if( names.contains( id )){
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
