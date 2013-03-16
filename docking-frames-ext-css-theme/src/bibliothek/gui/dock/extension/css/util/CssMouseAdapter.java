/*
 * Bibliothek - DockingFrames
 * Library built on Java/Swing, allows the user to "drag and drop"
 * panels containing any Swing-Component the developer likes to add.
 * 
 * Copyright (C) 2012 Benjamin Sigg
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
package bibliothek.gui.dock.extension.css.util;

import java.awt.Component;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.event.MouseInputListener;

import bibliothek.gui.dock.extension.css.CssNode;
import bibliothek.gui.dock.extension.css.doc.CssDocKey;
import bibliothek.gui.dock.extension.css.doc.CssDocKeys;
import bibliothek.gui.dock.extension.css.doc.CssDocText;

/**
 * This {@link MouseListener} can be added to any {@link Component}, it will update the
 * the pseudo classes of a {@link CssNode} with classes like "hover" and "pressed", depending on the
 * {@link MouseEvent}s that are catched from said {@link Component}s.
 * @author Benjamin Sigg
 */
@CssDocKeys({
	@CssDocKey(key="hover", description=@CssDocText(text="Applied if the mouse is hovering over this element")),
	@CssDocKey(key="pressed", description=@CssDocText(text="Applied if the mouse has grabbed this element"))
})
public abstract class CssMouseAdapter extends MouseAdapter implements MouseInputListener{
	private boolean hovering = false;
	private boolean mousePressed = false;
	
	/**
	 * Tells whether the mouse is currently hovering over a {@link Component}.
	 * @return whether the mouse is hovering
	 */
	public boolean isHovering(){
		return hovering;
	}
	
	/**
	 * Tells whether the mouse is currently pressed on a {@link Component}.
	 * @return whether the mouse is pressed
	 */
	public boolean isMousePressed(){
		return mousePressed;
	}
	
	@Override
	public void mouseEntered( MouseEvent e ){
		hovering = true;
		added( "hover" );
	}
	
	@Override
	public void mouseExited( MouseEvent e ){
		hovering = false;
		removed( "hover" );
	}
	
	@Override
	public void mousePressed( MouseEvent e ){
		if( !mousePressed ){
			mousePressed = true;
			added( "pressed" );
		}
	}
	
	@Override
	public void mouseReleased( MouseEvent e ){
		if( mousePressed ){
			int offmask = MouseEvent.BUTTON1_DOWN_MASK | MouseEvent.BUTTON2_DOWN_MASK | MouseEvent.BUTTON3_DOWN_MASK;
			if( (e.getModifiersEx() & offmask) == 0){
				mousePressed = false;
				removed( "pressed" );
			}
		}
	}
	
	/**
	 * Called if <code>pseudoClass</code> has been activated.
	 * @param pseudoClass the pseudo class that was added
	 */
	protected abstract void added( String pseudoClass );
	
	/**
	 * Called if <code>pseudoClass</code> is no longer active.
	 * @param pseudoClass the pseudo class that was removed
	 */
	protected abstract void removed( String pseudoClass );
}
