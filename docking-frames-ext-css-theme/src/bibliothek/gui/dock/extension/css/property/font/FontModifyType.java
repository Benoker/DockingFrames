/*
 * Bibliothek - DockingFrames
 * Library built on Java/Swing, allows the user to "drag and drop"
 * panels containing any Swing-Component the developer likes to add.
 * 
 * Copyright (C) 2013 Benjamin Sigg
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
package bibliothek.gui.dock.extension.css.property.font;

import bibliothek.gui.dock.extension.css.CssDeclarationValue;
import bibliothek.gui.dock.extension.css.CssType;
import bibliothek.gui.dock.extension.css.transition.MiddleTransitionalCssProperty;
import bibliothek.gui.dock.extension.css.transition.TransitionalCssProperty;
import bibliothek.gui.dock.util.font.GenericFontModifier.Modify;

/**
 * Converts a String into a {@link Modify}.
 * @author Benjamin Sigg
 */
public class FontModifyType implements CssType<Modify>{
	@Override
	public Modify convert( CssDeclarationValue value ){
		String text = value.getSingleValue().toLowerCase();
		
		if( "on".equals( text ) || "true".equals( text )){
			return Modify.ON;
		}
		if( "off".equals( text ) || "false".equals( text )){
			return Modify.OFF;
		}
		if( "ignore".equals( text )){
			return Modify.IGNORE;
		}
		if( "reverse".equals( text )){
			return Modify.REVERSE;
		}
		return null;
	}

	@Override
	public TransitionalCssProperty<Modify> createTransition(){
		return new MiddleTransitionalCssProperty<Modify>();
	}	
}
