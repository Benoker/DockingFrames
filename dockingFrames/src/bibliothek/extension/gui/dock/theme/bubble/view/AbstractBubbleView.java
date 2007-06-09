package bibliothek.extension.gui.dock.theme.bubble.view;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Set;

import javax.swing.JComponent;

import bibliothek.extension.gui.dock.theme.BubbleTheme;
import bibliothek.gui.Dockable;
import bibliothek.gui.dock.action.DockAction;
import bibliothek.gui.dock.action.StandardDockAction;
import bibliothek.gui.dock.action.views.buttons.TitleViewItem;
import bibliothek.gui.dock.event.StandardDockActionListener;
import bibliothek.gui.dock.title.DockTitle.Orientation;

public abstract class AbstractBubbleView implements TitleViewItem<JComponent>{	
	private RoundButton button;
	private Dockable dockable;
	private Listener listener=new Listener();
	private StandardDockAction action;
	private Orientation orientation = Orientation.FREE_HORIZONTAL;
	
	public AbstractBubbleView( BubbleTheme theme, StandardDockAction action, Dockable dockable ){
		button= new RoundButton(theme);
		
		this.dockable=dockable;
		this.action = action;
		
		button.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e) {
				triggered( AbstractBubbleView.this.dockable );				
			}
		});
	}
	
	/**
	 * Invoked when the button was clicked.
	 * @param dockable the dockable for which the action is shown
	 */
	protected abstract void triggered( Dockable dockable );
	
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
	
	protected RoundButton getButton(){
		return button;
	}
	
	public Dockable getDockable(){
		return dockable;
	}

	public void unbind() {
		
		action.removeDockActionListener(listener);

	}

	public void setOrientation( Orientation orientation ){
		this.orientation = orientation;
	}
	
	public Orientation getOrientation(){
		return orientation;
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
