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
import bibliothek.extension.gui.dock.preference.PreferenceModelListener;
import bibliothek.extension.gui.dock.preference.PreferenceModelText;
import bibliothek.extension.gui.dock.preference.preferences.choice.ButtonContentConditionChoice;
import bibliothek.gui.DockController;
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
	private ButtonContentConditionChoice filterChoice;
	
	private String knob;
	private String icon;
	private String text;
	private String children;
	private String actions;
	private String filter;
	
	private PreferenceModelText knobDescription;
	private PreferenceModelText iconDescription;
	private PreferenceModelText textDescription;
	private PreferenceModelText childrenDescription;
	private PreferenceModelText actionsDescription;
	private PreferenceModelText filterDescription;
	
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
		filterChoice = new ButtonContentConditionChoice( controller );
	
		knobDescription = new PreferenceModelText( "preference.buttonContent.knob", this ){
			protected void changed( String oldValue, String newValue ){
				firePreferenceChanged( 0, 0 );
			}
		};
		iconDescription = new PreferenceModelText( "preference.buttonContent.icon", this ){
			protected void changed( String oldValue, String newValue ){
				firePreferenceChanged( 1, 1 );
			}
		};
		textDescription = new PreferenceModelText( "preference.buttonContent.text", this ){
			protected void changed( String oldValue, String newValue ){
				firePreferenceChanged( 2, 2 );
			}
		};
		childrenDescription = new PreferenceModelText( "preference.buttonContent.children", this ){
			protected void changed( String oldValue, String newValue ){
				firePreferenceChanged( 3, 3 );
			}
		};
		actionsDescription = new PreferenceModelText( "preference.buttonContent.actions", this ){
			protected void changed( String oldValue, String newValue ){
				firePreferenceChanged( 4, 4 );
			}
		};
		filterDescription = new PreferenceModelText( "preference.buttonContent.filter", this ){
			protected void changed( String oldValue, String newValue ){
				firePreferenceChanged( 5, 5 );
			}
		};
	}
	
	@Override
	public void addPreferenceModelListener( PreferenceModelListener listener ){
		if( !hasListeners() ){
			knobDescription.setController( getController() );
			iconDescription.setController( getController() );
			textDescription.setController( getController() );
			childrenDescription.setController( getController() );
			actionsDescription.setController( getController() );
			filterDescription.setController( getController() );
		}
		super.addPreferenceModelListener( listener );
	}
	
	@Override
	public void removePreferenceModelListener( PreferenceModelListener listener ){
		super.removePreferenceModelListener( listener );
		if( !hasListeners() ){
			knobDescription.setController( null );
			iconDescription.setController( null );
			textDescription.setController( null );
			childrenDescription.setController( null );
			actionsDescription.setController( null );
			filterDescription.setController( null );
		}
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
				actionsChoice.identifierToValue( actions ),
				filterChoice.identifierToValue( filter ));
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
		setValue( 5, filterChoice.valueToIdentifier( content.getActions() ) );
	}
	
	public String getLabel( int index ){
		PreferenceModelText text;
		
		switch( index ){
			case 0: text = knobDescription; break;
			case 1: text = iconDescription; break;
			case 2: text = textDescription; break;
			case 3: text = childrenDescription; break;
			case 4: text = actionsDescription; break;
			case 5: text = filterDescription; break;
			default: throw new IllegalArgumentException( "unkonwn property: " + index );
		}
		
		if( !hasListeners() ){
			text.update( getController().getTexts() );
		}
		return text.value();
	}

	public Path getPath( int index ){
		switch( index ){
			case 0: return new Path( "dock.ButtonContent.knob" );
			case 1: return new Path( "dock.ButtonContent.icon" );
			case 2: return new Path( "dock.ButtonContent.text" );
			case 3: return new Path( "dock.ButtonContent.children" );
			case 4: return new Path( "dock.ButtonContent.actions" );
			case 5: return new Path( "dock.ButtonContent.filter" );
			default: throw new IllegalArgumentException( "unkonwn property: " + index );
		}
	}

	public int getSize(){
		return 6;
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
			case 5: return filter;
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
			case 5: return filterChoice;
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
			case 5: filter = (String)value; break;
		}
		
		firePreferenceChanged( index, index );
	}
}
