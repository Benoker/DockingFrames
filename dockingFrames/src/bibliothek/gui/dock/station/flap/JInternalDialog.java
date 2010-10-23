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
package bibliothek.gui.dock.station.flap;

import java.awt.Component;
import java.awt.Container;
import java.awt.GridLayout;
import java.awt.Point;

import javax.swing.JDesktopPane;
import javax.swing.JInternalFrame;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import bibliothek.gui.dock.FlapDockStation;

/**
 * The parent "window" for a {@link DefaultFlapWindow} that is shown in a {@link JInternalFrame}.
 * @author Benjamin Sigg
 */
public abstract class JInternalDialog  extends JPanel implements DefaultFlapWindow.Parent{
	/** the parent of this dialog */
	private JDesktopPane desktop;
	/** the owner of this dialog */
	private FlapDockStation station;
	
	/**
	 * Creates a new dialog.
	 * @param desktop the parent of this dialog
	 * @param station the owner of this dialog
	 */
	public JInternalDialog( JDesktopPane desktop, FlapDockStation station ){
		this.desktop = desktop;
		this.station = station;
		setLayout( new GridLayout( 1, 1 ) );
		setVisible( false );
		desktop.add( this );
		desktop.setLayer( this, JDesktopPane.MODAL_LAYER );
	}
	
	public Container getContentPane(){
		if( getComponentCount() == 0 ){
			return null;
		}
		return (Container)getComponent( 0 );
	}
	
	public void setContentPane( Container content ){
		removeAll();
		if( content != null ){
			add( content );
		}
	}
	
	public Component asComponent(){
		return this;
	}
	
	public void destroy(){
		setVisible( false );
		desktop.remove( this );
	}
	
	public boolean isParentValid(){
		return getDesktopPaneOf( station ) == desktop;
	}
	

	/**
	 * Searches the {@link JDesktopPane} which shows <code>station</code>.
	 * @param station the station whose parent is searched
	 * @return the parent or <code>null</code>
	 */
	protected abstract JDesktopPane getDesktopPaneOf( FlapDockStation station );
	
	public void setParentLocation( Point location ){
		Point delta = new Point( 0, 0 );
		SwingUtilities.convertPointToScreen( delta, desktop );
		
		Point finalResult = new Point( location.x - delta.x, location.y - delta.y );
		setLocation( finalResult );
	}
}