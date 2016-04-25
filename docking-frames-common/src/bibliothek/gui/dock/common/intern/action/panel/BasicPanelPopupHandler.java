/*
 * Bibliothek - DockingFrames
 * Library built on Java/Swing, allows the user to "drag and drop"
 * panels containing any Swing-Component the developer likes to add.
 * 
 * Copyright (C) 2009 Benjamin Sigg
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
package bibliothek.gui.dock.common.intern.action.panel;

import bibliothek.gui.Dockable;
import bibliothek.gui.dock.common.action.CPanelPopup;
import bibliothek.gui.dock.common.action.CPanelPopup.PanelPopup;
import bibliothek.gui.dock.themes.basic.action.BasicButtonModel;
import bibliothek.gui.dock.themes.basic.action.BasicButtonModelAdapter;
import bibliothek.gui.dock.themes.basic.action.BasicButtonModelListener;
import bibliothek.gui.dock.themes.basic.action.BasicHandler;

/**
 * A handler for a {@link CPanelPopup} which is shown in a title as button.
 * @author Benjamin Sigg
 */
public class BasicPanelPopupHandler extends BasicHandler<CPanelPopup.PanelPopup>{
	
	/** a listener to the model of this handler */
	private BasicButtonModelListener listener = new BasicButtonModelAdapter(){
		@Override
		public void mousePressed( BasicButtonModel model, boolean mousePressed ){
			if( mousePressed ){
				getAction().onMousePressed( getDockable(), getItem(), getModel().getOrientation() );
			}
		}
	};
	
	/**
	 * Creates a new handler.
	 * @param action the action for which this handler is used
	 * @param dockable the element for which the action is used
	 */
	public BasicPanelPopupHandler( PanelPopup action, Dockable dockable ){
		super( action, dockable );
	}
	
	@Override
	public void triggered(){
		if( getModel().isMousePressed() )
			getAction().onMouseReleased( getDockable(), getItem(), getModel().getOrientation() );
		else
			getAction().onTrigger( getDockable(), getItem(), getModel().getOrientation() );
	}
	
	@Override
	public void setModel( BasicButtonModel model ){
		BasicButtonModel old = getModel();
		if( old != null )
			old.removeListener( listener );
		
		super.setModel( model );
		
		if( model != null )
			model.addListener( listener );
	}
}
