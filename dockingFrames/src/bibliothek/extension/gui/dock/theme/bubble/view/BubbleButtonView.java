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
