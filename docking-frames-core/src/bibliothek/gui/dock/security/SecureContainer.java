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
package bibliothek.gui.dock.security;

import java.awt.event.MouseEvent;

import javax.swing.JComponent;

import bibliothek.gui.DockController;
import bibliothek.gui.dock.control.focus.MouseFocusObserver;
import bibliothek.gui.dock.station.OverpaintablePanel;
import bibliothek.gui.dock.util.PropertyValue;

/**
 * A {@link SecureContainer} is a {@link OverpaintablePanel} which can also wrap its
 * content pane into a {@link GlassedPane} to catch {@link MouseEvent}s and call the
 * the {@link MouseFocusObserver} to transfer focus if necessary.
 * @author Benjamin Sigg
 */
public class SecureContainer extends OverpaintablePanel {
	/** the controller which decides whether a restricted environment is in use */
	private DockController controller;
	
	/** the currently used {@link GlassedPane} */
	private GlassedPane pane;
	
	private PropertyValue<Boolean> restricted = new PropertyValue<Boolean>( DockController.RESTRICTED_ENVIRONMENT ){
		@Override
		protected void valueChanged( Boolean oldValue, Boolean newValue ){
			checkRestricted();
		}
	};
	
	/**
	 * Sets the controller which tells whether a restricted environment is in use.
	 * @param controller the controller to inform about {@link MouseEvent}s or <code>null</code>
	 */
	public void setController( DockController controller ){
		this.controller = controller;
		if( pane != null ){
			pane.setController( controller );
		}
		restricted.setProperties( controller );
		checkRestricted();
	}
	
	/**
	 * Gets the controller which tells whether a restricted environment is in use.
	 * @return the controller or <code>null</code>
	 */
	public DockController getController(){
		return controller;
	}
	
	/**
	 * Checks whether a {@link GlassedPane} should be used or not
	 */
	private void checkRestricted(){
		if( restricted.getProperties() != null ){
			setUseGlassPane( restricted.getValue() );
		}
	}

	/**
	 * Tells whether a {@link GlassedPane} is currently in use.
	 * @return <code>true</code> if a glassed pane is in use
	 */
	protected boolean isUseGlassPane(){
		return pane != null;
	}
	
	/**
	 * Changes whether this container should use a {@link GlassedPane} or not. This method
	 * is called automatically by this {@link SecureContainer}.
	 * @param use <code>true</code> if the {@link GlassedPane} should be created
	 * and installed, <code>false</code> if the {@link GlassedPane} should be
	 * uninstalled
	 */
	protected void setUseGlassPane( boolean use ){
		if( use ){
			if( pane == null ){
				pane = createGlassPane();
				pane.setController( controller );
				install( pane );
			}
		}
		else{
			if( pane != null ){
				uninstall( pane );
				pane.setController( null );
				pane = null;
			}
		}
	}

	/**
	 * Wraps the current {@link #getContentPane() content pane} into <code>pane</code>
	 * and sets <code>pane</code> as base panel.
	 * @param pane the new base panel
	 */
	protected void install( GlassedPane pane ){
		JComponent content = getContentPane();
		setBasePane( pane );
		pane.setContentPane( content );
		setContentPane( content );		
	}
	
	/**
	 * Sets the current {@link GlassedPane#getContentPane() content pane} of <code>pane</code>
	 * as {@link #setBasePane(JComponent) base panel} of this {@link OverpaintablePanel}.
	 * @param pane the panel which should no longer be used
	 */
	protected void uninstall( GlassedPane pane ){
		JComponent content = getContentPane();
		pane.setContentPane( null );
		setBasePane( content );
	}
	
	/**
	 * Creates the {@link GlassedPane} which will wrap around the content of
	 * this panel. This method may be called more than once.
	 * @return the new pane, not <code>null</code>
	 */
	protected GlassedPane createGlassPane(){
		return new GlassedPane();
	}
}
