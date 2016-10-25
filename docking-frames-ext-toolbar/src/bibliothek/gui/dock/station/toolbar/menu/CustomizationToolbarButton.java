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

import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.LayoutManager;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.Icon;
import javax.swing.JLabel;
import javax.swing.JLayeredPane;
import javax.swing.JToggleButton;

import bibliothek.gui.DockController;
import bibliothek.gui.DockStation;
import bibliothek.gui.Dockable;
import bibliothek.gui.dock.util.icon.DockIcon;

/**
 * The {@link CustomizationToolbarButton} is a button that allows to add one
 * {@link Dockable} to a {@link DockStation}. This button also offers an
 * indication telling whether the {@link Dockable} is already shown on another
 * station.
 * 
 * @author Benjamin Sigg
 */
public abstract class CustomizationToolbarButton implements
		CustomizationMenuContent{
	/**
	 * The different locations where a {@link Dockable} can be in respect to
	 * this button
	 */
	public static enum ItemLocation{
		/** The {@link Dockable} is not visible anywhere */
		INVISIBLE,
		/**
		 * The {@link Dockable} is visible, but it is part of another
		 * {@link DockStation}
		 */
		ELSEWHERE,
		/**
		 * The {@link Dockable} is visible and it is part of this
		 * {@link DockStation}
		 */
		HERE
	}

	private DockController controller;

	private Icon icon;
	private String description;
	private String text;

	private JLayeredPane base;
	private JToggleButton button;
	private JLabel elsewhere;

	private CustomizationMenuCallback callback;

	/** the icon of {@link #here} */
	private DockIcon elsewhereIcon = new DockIcon(
			"toolbar.customization.check", DockIcon.KIND_ICON){
		@Override
		protected void changed( Icon oldValue, Icon newValue ){
			if (elsewhere != null){
				elsewhere.setIcon(newValue);
			}
		}
	};
	
	@Override
	public void setController( DockController controller ){
		this.controller = controller;
		if (callback != null){
		elsewhereIcon.setController(controller);
		}
	}

	@Override
	public Component getView(){
		return base;
	}

	@Override
	public void bind( CustomizationMenuCallback callback ){
		this.callback = callback;
		button = new JToggleButton();
		button.setIcon(icon);
		button.setToolTipText(description);
		button.setText(text);
		button.setOpaque(false);
		
		elsewhere = new JLabel();
		elsewhereIcon.setController(controller);
		elsewhere.setIcon(elsewhereIcon.value());
		elsewhere.setVisible(false);

		base = new JLayeredPane();
		base.add(button);
		base.add(elsewhere);
		base.setLayer(button, JLayeredPane.DEFAULT_LAYER);
		base.setLayer(elsewhere, JLayeredPane.MODAL_LAYER);
		base.setLayout(new LayoutManager(){
			@Override
			public void removeLayoutComponent( Component comp ){
				// ignore
			}

			@Override
			public Dimension preferredLayoutSize( Container parent ){
				return new Dimension(button.getPreferredSize().width
						+ elsewhere.getPreferredSize().width + 1, button
						.getPreferredSize().height);
			}

			@Override
			public Dimension minimumLayoutSize( Container parent ){
				return button.getMinimumSize();
			}

			@Override
			public void layoutContainer( Container parent ){
				if (parent.getComponentCount() == 2){
					int width = parent.getWidth();
					int height = parent.getHeight();
					Dimension preferred = elsewhere.getPreferredSize();
					int labelWidth = Math.min(preferred.width, width - 1);
					int labelHeight = Math.min(preferred.height, height - 1);
					button.setBounds(labelWidth + 2, 0, width - labelWidth - 2, height);
					elsewhere.setBounds(1, 0, labelWidth,
							labelHeight);
				}
			}

			@Override
			public void addLayoutComponent( String name, Component comp ){
				// ignore
			}
		});

		button.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed( ActionEvent e ){
				ItemLocation location = getItemLocation();
				setItemVisible(location != ItemLocation.HERE);
			}
		});

		select();
	}

	private void select(){
		ItemLocation location = getItemLocation();
		elsewhere.setVisible(location == ItemLocation.HERE || location == ItemLocation.ELSEWHERE);
		button.setSelected(location == ItemLocation.HERE);
	}

	/**
	 * Gets the current location of the {@link Dockable} that is described by
	 * this button.
	 * 
	 * @return the current location
	 * @throws IllegalStateException
	 *             if {@link #bind(CustomizationMenuCallback)} was not called
	 */
	protected ItemLocation getItemLocation(){
		if (callback == null){
			throw new IllegalStateException(
					"this information is only available if the button has been bound");
		}

		if (!hasDockable()){
			return ItemLocation.INVISIBLE;
		}
		Dockable item = getDockable();
		DockStation parent = item.getDockParent();
		if (parent == null){
			return ItemLocation.INVISIBLE;
		}
		DockStation owner = callback.getOwner();
		while (parent != null){
			if (parent == owner){
				return ItemLocation.HERE;
			}
			item = parent.asDockable();
			if (item == null){
				parent = null;
			} else{
				parent = item.getDockParent();
			}
		}
		return ItemLocation.ELSEWHERE;
	}

	/**
	 * Removes the {@link Dockable} from its current parent and maybe appends it
	 * to the owner of this button.
	 * 
	 * @param visible
	 *            whether the item should be visible or not
	 */
	protected void setItemVisible( boolean visible ){
		Dockable item = getDockable();
		DockStation parent = item.getDockParent();
		if (parent != null){
			parent.drag(item);
		}

		if (visible){
			CustomizationToolbarButton.this.callback.append(item);
		}

		select();
	}

	@Override
	public void unbind(){
		base = null;
		button = null;
		elsewhere = null;
		elsewhereIcon.setController(null);
	}

	/**
	 * Sets the icon which should be shown on the button.
	 * 
	 * @param icon
	 *            the new icon, can be <code>null</code>
	 */
	public void setIcon( Icon icon ){
		this.icon = icon;
		if (button != null){
			button.setIcon(icon);
		}
	}
	
	/**
	 * Sets the text which should be shown on the button.
	 * 
	 * @param icon
	 *            the new text, can be <code>null</code>
	 */
	public void setText( String text){
		this.text = text;
		if (button != null){
			button.setText(text);
		}
	}

	/**
	 * Sets a text which describes the meaning of the button.
	 * 
	 * @param description
	 *            the description, can be <code>null</code>
	 */
	public void setDescription( String description ){
		this.description = description;
		if (button != null){
			button.setToolTipText(description);
		}
	}

	/**
	 * Tells whether the {@link Dockable} of this button, accessible by calling
	 * {@link #getDockable()}, is already present. If the item is not yet
	 * present, then it cannot be visible or selected at this time.
	 * 
	 * @return whether the {@link Dockable} already exists
	 */
	protected abstract boolean hasDockable();

	/**
	 * Gets the element that is put onto a toolbar. This method may create the
	 * {@link Dockable} lazily in the very moment it is used the first time.
	 * 
	 * @return the item to show on the toolbar
	 */
	protected abstract Dockable getDockable();
}
