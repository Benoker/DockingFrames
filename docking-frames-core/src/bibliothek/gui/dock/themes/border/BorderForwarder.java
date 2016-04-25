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
package bibliothek.gui.dock.themes.border;

import javax.swing.JComponent;
import javax.swing.border.Border;

import bibliothek.gui.DockController;
import bibliothek.gui.dock.themes.ThemeManager;
import bibliothek.gui.dock.util.UIValue;
import bibliothek.util.Path;

/**
 * This helper class implements {@link DockBorder}, provides methods to register
 * itself on the current {@link ThemeManager}, and updates the border of some
 * {@link JComponent} using the current {@link BorderModifier}.
 * @author Benjamin Sigg
 */
public class BorderForwarder implements DockBorder{
	/** the component whose border is going to be replaced */
	private JComponent target;
	/** the original border of {@link #target} */
	private Border border;
	/** the current {@link BorderModifier} */
	private BorderModifier modifier;
	/** whether this forwarder is currently forwarding a border */
	private boolean forwarding = false;
	
	/** the controller which is currently monitored */
	private DockController controller;
	/** the kind of {@link UIValue} this is */
	private Path kind;
	/** the identifier of the value to monitor */
	private String id;
	
	/**
	 * Creates a new forwarder.
	 * @param kind what kind of {@link UIValue} this is
	 * @param id the identifier this {@link UIValue} should monitor
	 * @param target the component whose border is going to be replaced
	 */
	public BorderForwarder( Path kind, String id, JComponent target ){
		if( kind == null ){
			throw new IllegalArgumentException( "kind must not be null" );
		}
		if( id == null ){
			throw new IllegalArgumentException( "id must not be null" );
		}
		if( target == null ){
			throw new IllegalArgumentException( "target must not be null" );
		}
		
		this.kind = kind;
		this.id = id;
		this.target = target;
	}
	
	/**
	 * Sets the basic border of this forwarder. The basic border will be used as
	 * argument for {@link BorderModifier#modify(Border)}.
	 * @param border the new border, can be <code>null</code>
	 */
	public void setBorder( Border border ){
		if( this.border != border ){
			this.border = border;
			if( modifier == null ){
				forward( border );
			}
			else{
				forward( modifier.modify( border ));
			}
		}
	}
	
	public void set( BorderModifier value ){
		if( this.modifier != value ){
			this.modifier = value;
			if( this.modifier == null ){
				forward( border );
			}
			else{
				forward( this.modifier.modify( border ));
			}
		}
	}
	
	/**
	 * Gets the {@link BorderModifier} that is currently used by this forwarder.
	 * @return the current modifier or <code>null</code>
	 */
	public BorderModifier get(){
		return modifier;
	}
	
	/**
	 * Sets the {@link DockController} which should be monitored for the current {@link BorderModifier}.
	 * @param controller the new controller or <code>null</code>
	 */
	public void setController( DockController controller ){
		if( this.controller != controller ){
			if( this.controller != null ){
				this.controller.getThemeManager().remove( this );
			}
			
			this.controller = controller;
			if( this.controller != null ){
				this.controller.getThemeManager().add( id, kind, ThemeManager.BORDER_MODIFIER_TYPE, this );
			}
			else{
				set( null );
			}
		}
	}
	
	/**
	 * Calls {@link JComponent#setBorder(Border)} on the target {@link JComponent} of
	 * this forwarder. During the call {@link #isForwarding()} returns <code>true</code>.
	 * @param border the border to really show
	 */
	protected void forward( Border border ){
		try{
			forwarding = true;
			target.setBorder( border );
		}
		finally{
			forwarding = false;
		}
	}

	/**
	 * Tells whether the current call to {@link JComponent#setBorder(Border)} is executed
	 * by this forwarder.
	 * @return <code>true</code> if currently {@link #forward(Border)} is running
	 */
	public boolean isForwarding(){
		return forwarding;
	}
}
