/*
 * Bibliothek - DockingFrames
 * Library built on Java/Swing, allows the user to "drag and drop"
 * panels containing any Swing-Component the developer likes to add.
 * 
 * Copyright (C) 2007 Benjamin Sigg
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
package bibliothek.extension.gui.dock.theme.bubble.view;


import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Set;

import javax.swing.JComponent;

import bibliothek.extension.gui.dock.theme.BubbleTheme;
import bibliothek.gui.Dockable;
import bibliothek.gui.dock.action.ButtonDockAction;
import bibliothek.gui.dock.action.DockAction;
import bibliothek.gui.dock.action.StandardDockAction;
import bibliothek.gui.dock.action.views.buttons.TitleViewItem;
import bibliothek.gui.dock.event.StandardDockActionListener;
import bibliothek.gui.dock.title.DockTitle.Orientation;

public class BubbleButtonView implements TitleViewItem<JComponent> {

	
	RoundButton button;
	ButtonDockAction action;
	Dockable dockable;
	Listener listener=new Listener();
	
	public BubbleButtonView(BubbleTheme theme, ButtonDockAction action, Dockable dockable){
		
		button= new RoundButton(theme);
		this.action= action;
		this.dockable=dockable;
		button.addActionListener(new ActionListener(){
			
			public void actionPerformed(ActionEvent e) {
				BubbleButtonView.this.action.action(BubbleButtonView.this.dockable);				
			}
		});
	}
	
	public void bind() {
		
		action.addDockActionListener(listener);
        button.setIcon(action.getIcon(dockable));
	}

	public DockAction getAction() {
		
		return action;
	}

	public JComponent getItem() {
		
		return button;
	}

	public void unbind() {
		
		action.removeDockActionListener(listener);

	}

	public void setOrientation(Orientation orientation) {
		//ignore
		
	}
	
	private class Listener implements StandardDockActionListener{

		public void actionDisabledIconChanged( StandardDockAction action, Set<Dockable> dockables ){
			// ignore
		}

		public void actionEnabledChanged( StandardDockAction action, Set<Dockable> dockables ){
			// ignore
		}

		public void actionIconChanged( StandardDockAction action, Set<Dockable> dockables ){
			//repaint();
			button.setIcon(action.getIcon(dockable));
		}

		public void actionTextChanged( StandardDockAction action, Set<Dockable> dockables ){
			//updateTooltip();
		}

		public void actionTooltipTextChanged( StandardDockAction action, Set<Dockable> dockables ){
			//updateTooltip();
		}
	}



	
	
}
