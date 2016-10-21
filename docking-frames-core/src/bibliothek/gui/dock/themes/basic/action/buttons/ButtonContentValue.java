/*
 * Bibliothek - DockingFrames
 * Library built on Java/Swing, allows the user to "drag and drop"
 * panels containing any Swing-Component the developer likes to add.
 * 
 * Copyright (C) 2008 Benjamin Sigg
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
package bibliothek.gui.dock.themes.basic.action.buttons;

import bibliothek.gui.DockStation;
import bibliothek.gui.Dockable;
import bibliothek.gui.dock.FlapDockStation;
import bibliothek.gui.dock.action.DockAction;
import bibliothek.gui.dock.station.flap.button.ButtonContent;
import bibliothek.gui.dock.station.flap.button.ButtonContentFilter;
import bibliothek.gui.dock.station.flap.button.ButtonContentListener;
import bibliothek.gui.dock.util.PropertyValue;

/**
 * A wrapper around an exchangeable {@link ButtonContent} that implements {@link ButtonContentListener} to listen
 * to the current {@link ButtonContent}. This class offers methods to monitor the properties of one {@link Dockable},
 * the class keeps track of old properties and calls the method {@link #propertyChanged()} whenever at least one property
 * truly did change.
 * @author Benjamin Sigg
 */
public abstract class ButtonContentValue extends PropertyValue<ButtonContent> implements ButtonContentListener {
	/** the currently observed element */
	private Dockable dockable;
	
	private ButtonContent theme;
	
	private boolean showKnob;
	private boolean showIcon;
	private boolean showText;
	private boolean showChildren;
	private boolean showActions;
	private boolean filterActions;
	
	/**
	 * Creates a new wrapper
	 * @param theme the defaults of all values, not <code>null</code>. When asking for a property of this
	 * {@link ButtonContent} a value of <code>false</code> will be used as theme default.
	 */
	public ButtonContentValue( ButtonContent theme ){
		super( FlapDockStation.BUTTON_CONTENT );
		this.theme = theme;
	}

	@Override
	protected void valueChanged( ButtonContent oldValue, ButtonContent newValue ){
		if( dockable != null ){
			if( oldValue != null ){
				oldValue.removeListener( dockable, this );
			}
			if( newValue != null ){
				newValue.addListener( dockable, this );
			}
			checkProperties();
		}
	}

	/**
	 * Sets the element whose properties should be monitored. The method {@link #propertyChanged()} will
	 * be called whenever a property of the current {@link ButtonContent} in respect to <code>dockable</code>
	 * did change. 
	 * @param dockable the element to observe or <code>null</code>
	 */
	public void setDockable( Dockable dockable ){
		if( this.dockable != dockable ){
			ButtonContent content =	getValue();
			if( this.dockable != null ){
				theme.removeListener( dockable, this );
				if( content != null ){
					content.removeListener( this.dockable, this );
				}
			}
			this.dockable = dockable;
			if( this.dockable != null ){
				theme.addListener( dockable, this );
				if( content != null ){
					content.addListener( this.dockable, this );
				}
			}
			checkProperties();
		}
	}
	
	public void changed( ButtonContent content, Dockable dockable ){
		if( this.dockable == dockable && getValue() == content ){
			checkProperties();
		}
	}

	private void checkProperties(){
		ButtonContent content = getValue();
		if( content != null && dockable != null ){
			boolean change = false;
			
			if( showKnob != content.showKnob( dockable, theme.showKnob( dockable, false ) ) ){
				showKnob = !showKnob;
				change = true;
			}
			
			if( showIcon != content.showIcon( dockable, theme.showIcon( dockable, false ) ) ){
				showIcon = !showIcon;
				change = true;
			}
			
			if( showText != content.showText( dockable, theme.showText( dockable, false ) ) ){
				showText = !showText;
				change = true;
			}
			
			if( showChildren != content.showChildren( dockable, theme.showChildren( dockable, false ) ) ){
				showChildren = !showChildren;
				change = true;
			}
			
			if( showActions != content.showActions( dockable, theme.showActions( dockable, false ) ) ){
				showActions = !showActions;
				change = true;
			}
			
			if( filterActions != content.filterActions( dockable, theme.filterActions( dockable, false ) ) ){
				filterActions = !filterActions;
				change = true;
			}
			
			if( change ){
				propertyChanged();
			}
		}
	}
	
	/**
	 * Tells whether a knob, where the user can grab the button, should be painted.
	 * @return <code>true</code> if a knob should be visible
	 */
	public boolean isShowKnob(){
		return showKnob;
	}
	
	/**
	 * Tells whether an icon should be painted.
	 * @return <code>true</code> if the title icon should be visible
	 */
	public boolean isShowIcon(){
		return showIcon;
	}
	
	/**
	 * Tells whether a title text should be painted.
	 * @return <code>true</code> if the title text should be visible
	 */
	public boolean isShowText(){
		return showText;
	}
	
	/**
	 * Tells whether buttons to select the child {@link Dockable}s of a {@link DockStation} should
	 * be painted.
	 * @return <code>true</code> if buttons are required
	 */
	public boolean isShowChildren(){
		return showChildren;
	}
	
	/**
	 * Tells whether the default {@link DockAction}s of a {@link Dockable} should be painted.
	 * @return <code>true</code> if the actions should be shown
	 */
	public boolean isShowActions(){
		return showActions;
	}
	
	/**
	 * Tells whether the {@link DockAction}s should be filtered by the current {@link ButtonContentFilter} before
	 * made visible.
	 * @return <code>true</code> if the actions should be filtered
	 */
	public boolean isFilterActions(){
		return filterActions;
	}
	
	/**
	 * Called if at least one property changed. If either the current {@link ButtonContent} or the
	 * current {@link Dockable} is <code>null</code>, then this method is never called.
	 */
	protected abstract void propertyChanged();
}
