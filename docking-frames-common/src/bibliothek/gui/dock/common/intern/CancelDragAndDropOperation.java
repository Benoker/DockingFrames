/*
 * Bibliothek - DockingFrames
 * Library built on Java/Swing, allows the user to "drag and drop"
 * panels containing any Swing-Component the developer likes to add.
 * 
 * Copyright (C) 2016 Benjamin Sigg
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
package bibliothek.gui.dock.common.intern;

import java.awt.event.KeyEvent;

import javax.swing.KeyStroke;

import bibliothek.gui.dock.DockElement;
import bibliothek.gui.dock.common.CControl;
import bibliothek.gui.dock.common.event.CKeyboardListener;

public class CancelDragAndDropOperation implements CKeyboardListener {
	private CControl control;

	public CancelDragAndDropOperation( CControl control ) {
		this.control = control;
	}

	public DockElement getTreeLocation() {
		return null;
	}

	private boolean dragAndDropHappening() {
		return control.getController().getRelocator().isOnMove();
	}

	private boolean isCancelKeyStroke( KeyEvent event ) {
		KeyStroke keyStroke = control.getProperty( CControl.KEY_CANCEL_OPERATION );
		if( keyStroke == null ) {
			return false;
		}

		KeyStroke observedKeyStroke = KeyStroke.getKeyStrokeForEvent( event );
		return keyStroke.equals( observedKeyStroke );
	}

	private void cancelOperation() {
		control.getController().getRelocator().cancel();
	}

	public boolean keyPressed( CDockable source, KeyEvent event ) {
		if( event.isConsumed() ) {
			return false;
		}

		if( dragAndDropHappening() && isCancelKeyStroke( event ) ) {
			cancelOperation();
			return true;
		} else {
			return false;
		}
	}

	public boolean keyReleased( CDockable source, KeyEvent event ) {
		return false;
	}

	public boolean keyTyped( CDockable source, KeyEvent event ) {
		return false;
	}
}
