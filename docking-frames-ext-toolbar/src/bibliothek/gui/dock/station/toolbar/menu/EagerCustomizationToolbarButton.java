/*
 * Bibliothek - DockingFrames
 * Library built on Java/Swing, allows the user to "drag and drop"
 * panels containing any Swing-Component the developer likes to add.
 * 
 * Copyright (C) 2012 Herve Guillaume, Benjamin Sigg
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
 * Herve Guillaume
 * rvguillaume@hotmail.com
 * FR - France
 *
 * Benjamin Sigg
 * benjamin_sigg@gmx.ch
 * CH - Switzerland
 */
package bibliothek.gui.dock.station.toolbar.menu;

import javax.swing.Icon;

import bibliothek.gui.Dockable;
import bibliothek.gui.dock.event.DockableAdapter;
import bibliothek.gui.dock.event.DockableListener;

/**
 * A {@link CustomizationToolbarButton} which uses an already existing
 * {@link Dockable} to gain access to icon and description.
 * 
 * @author Benjamin Sigg
 * 
 */
public class EagerCustomizationToolbarButton extends CustomizationToolbarButton{
	private Dockable dockable;
	private DockableListener listener = new DockableAdapter(){
		public void titleIconChanged( Dockable dockable, Icon oldIcon,
				Icon newIcon ){
			setIcon(newIcon);
		}

		public void titleTextChanged( Dockable dockable, String oldTitle,
				String newTitle ){
			setText(newTitle);
		}

		public void titleToolTipChanged( Dockable dockable, String oldTooltip,
				String newTooltip ){
			setDescription(newTooltip);
		}
	};

	/**
	 * Creates a new button, a {@link DockableListener} is added to
	 * <code>dockable</code> to read icon and description.
	 * 
	 * @param dockable
	 *            the item represented by this button
	 */
	public EagerCustomizationToolbarButton( Dockable dockable ){
		this.dockable = dockable;
	}

	@Override
	protected boolean hasDockable(){
		return true;
	}

	@Override
	protected Dockable getDockable(){
		return dockable;
	}

	@Override
	public void bind( CustomizationMenuCallback callback ){
		dockable.addDockableListener(listener);
		if (dockable.getTitleIcon() != null){
			setIcon(dockable.getTitleIcon());
		} else{
			setText(dockable.getTitleText());
		}
		setDescription(dockable.getTitleToolTip());
		super.bind(callback);
	}

	@Override
	public void unbind(){
		super.unbind();
		dockable.removeDockableListener(listener);
		setIcon(null);
		setDescription(null);
		setText(null);
	}
}
