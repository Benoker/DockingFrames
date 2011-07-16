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
package bibliothek.gui.dock.util.text;

import bibliothek.gui.DockController;
import bibliothek.gui.dock.util.AbstractUIValue;
import bibliothek.gui.dock.util.IconManager;
import bibliothek.gui.dock.util.TextManager;
import bibliothek.gui.dock.util.UIValue;
import bibliothek.util.Path;

/**
 * An observer to a {@link TextManager}.
 * @author Benjamin Sigg
 */
public abstract class TextValue extends AbstractUIValue<String, TextValue>{
	/** What kind of {@link UIValue} this is */
	public static final Path KIND_TEXT = new Path( "text" );

	/**
	 * Creates a new {@link TextValue}.
	 * @param id the unique identifier of this string
	 * @param kind what kind of {@link UIValue} this is
	 */
	public TextValue( String id, Path kind ){
		super( id, kind );
	}
	
	/**
	 * Creates a new {@link TextValue}.
	 * @param id the unique identifier of this string
	 * @param kind what kind of {@link UIValue} this is
	 * @param backup the string to be used if no other string is found
	 */
	public TextValue( String id, Path kind, String backup ){
		super( id, kind, backup );
	}
	
	/**
	 * Sets the {@link IconManager} of <code>controller</code>
	 * @param controller the controller to observe, can be <code>null</code>
	 */
	public void setController( DockController controller ){
		if( controller == null ){
			setManager( null );
		}
		else{
			setManager( controller.getTexts() );
		}
	}

	@Override
	protected TextValue me(){
		return this;
	}
}
