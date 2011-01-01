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
package bibliothek.extension.gui.dock.preference.model;

import bibliothek.extension.gui.dock.preference.AbstractPreferenceModel;
import bibliothek.extension.gui.dock.preference.PreferenceModel;
import bibliothek.extension.gui.dock.preference.preferences.choice.ButtonContentConditionChoice;
import bibliothek.gui.DockController;
import bibliothek.gui.DockUI;
import bibliothek.gui.dock.FlapDockStation;
import bibliothek.gui.dock.station.flap.button.ButtonContent;
import bibliothek.gui.dock.util.DockProperties;
import bibliothek.gui.dock.util.Priority;
import bibliothek.util.Path;

/**
 * This {@link PreferenceModel} allows the user to set up a {@link ButtonContent} using the static
 * conditions that are defined in {@link ButtonContent}.
 * @author Benjamin Sigg
 */
public class ButtonContentPreferenceModel extends AbstractPreferenceModel{
	private ButtonContentConditionChoice knobChoice;
	private ButtonContentConditionChoice iconChoice;
	private ButtonContentConditionChoice textChoice;
	private ButtonContentConditionChoice childrenChoice;
	private ButtonContentConditionChoice actionsChoice;
	
	private String knob;
	private String icon;
	private String text;
	private String children;
	private String actions;
	
	/**
	 * Creates a new model
	 * @param controller the controller in whose realm this model works
	 */
	public ButtonContentPreferenceModel( DockController controller ){
		super( controller );
		
		knobChoice = new ButtonContentConditionChoice( controller );
		iconChoice = new ButtonContentConditionChoice( controller );
		textChoice = new ButtonContentConditionChoice( controller );
		childrenChoice = new ButtonContentConditionChoice( controller );
		actionsChoice = new ButtonContentConditionChoice( controller );
	}
	
	@Override
	public void write(){
		DockProperties properties = getController().getProperties();
		properties.setOrRemove( FlapDockStation.BUTTON_CONTENT, getContent(), Priority.CLIENT );
		super.write();
	}
	
	@Override
	public void read(){
		DockProperties properties = getController().getProperties();
		setContent( properties.get( FlapDockStation.BUTTON_CONTENT, Priority.CLIENT ) );
		super.read();
	}
	
	/**
	 * Gets the currently selected {@link ButtonContent}.
	 * @return the current content, not <code>null</code>
	 */
	public ButtonContent getContent(){
		return new ButtonContent(
				knobChoice.identifierToValue( knob ),
				iconChoice.identifierToValue( icon ),
				textChoice.identifierToValue( text ),
				childrenChoice.identifierToValue( children ),
				actionsChoice.identifierToValue( actions ));
	}
	
	/**
	 * Sets the property that should be shown.
	 * @param content the property, can be <code>null</code>
	 */
	public void setContent( ButtonContent content ){
		setValue( 0, knobChoice.valueToIdentifier( content.getKnob() ) );
		setValue( 1, iconChoice.valueToIdentifier( content.getIcon() ) );
		setValue( 2, textChoice.valueToIdentifier( content.getText() ) );
		setValue( 3, childrenChoice.valueToIdentifier( content.getChildren() ) );
		setValue( 4, actionsChoice.valueToIdentifier( content.getActions() ) );
	}
	
	public String getLabel( int index ){
		switch( index ){
			case 0: return DockUI.getDefaultDockUI().getString( "preference.buttonContent.knob" );
			case 1: return DockUI.getDefaultDockUI().getString( "preference.buttonContent.icon" );
			case 2: return DockUI.getDefaultDockUI().getString( "preference.buttonContent.text" );
			case 3: return DockUI.getDefaultDockUI().getString( "preference.buttonContent.children" );
			case 4: return DockUI.getDefaultDockUI().getString( "preference.buttonContent.actions" );
			default: throw new IllegalArgumentException( "unkonwn property: " + index );
		}
	}

	public Path getPath( int index ){
		switch( index ){
			case 0: return new Path( "dock.ButtonContent.knob" );
			case 1: return new Path( "dock.ButtonContent.icon" );
			case 2: return new Path( "dock.ButtonContent.text" );
			case 3: return new Path( "dock.ButtonContent.children" );
			case 4: return new Path( "dock.ButtonContent.actions" );
			default: throw new IllegalArgumentException( "unkonwn property: " + index );
		}
	}

	public int getSize(){
		return 5;
	}

	public Path getTypePath( int index ){
		return Path.TYPE_STRING_CHOICE_PATH;
	}

	public Object getValue( int index ){
		switch( index ){
			case 0: return knob;
			case 1: return icon;
			case 2: return text;
			case 3: return children;
			case 4: return actions;
			default: throw new IllegalArgumentException( "unknown value: " + index );
		}
	}

	public Object getValueInfo( int index ){
		switch( index ){
			case 0: return knobChoice;
			case 1: return iconChoice;
			case 2: return textChoice;
			case 3: return childrenChoice;
			case 4: return actionsChoice;
			default: throw new IllegalArgumentException( "unknown value: " + index );
		}
	}

	public void setValue( int index, Object value ){
		switch( index ){
			case 0: knob = (String)value; break;
			case 1: icon = (String)value; break;
			case 2: text = (String)value; break;
			case 3: children = (String)value; break;
			case 4: actions = (String)value; break;
		}
		
		firePreferenceChanged( index, index );
	}
}
