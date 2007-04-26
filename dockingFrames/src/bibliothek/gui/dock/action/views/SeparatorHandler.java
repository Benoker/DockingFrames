package bibliothek.gui.dock.action.views;

import java.awt.event.ActionListener;

import javax.swing.JComponent;
import javax.swing.JSeparator;
import javax.swing.SwingConstants;

import bibliothek.gui.dock.action.DockAction;
import bibliothek.gui.dock.action.actions.SeparatorAction;
import bibliothek.gui.dock.action.views.buttons.TitleViewItem;
import bibliothek.gui.dock.action.views.menu.MenuViewItem;
import bibliothek.gui.dock.title.DockTitle.Orientation;

/**
 * A handler that shows a {@link JSeparator} for a {@link SeparatorAction}.
 * @author Benjamin Sigg
 */
public class SeparatorHandler implements TitleViewItem<JComponent>, MenuViewItem<JComponent> {
	/** the action for which the separator is shown */
	private SeparatorAction action;
	
	/** the separator */
	private JSeparator separator;
	
	/**
	 * Creates a new handler
	 * @param separator the separator to show
	 * @param action the action for which the action is shown
	 */
	public SeparatorHandler( JSeparator separator, SeparatorAction action ){
		this.action = action;
		this.separator = separator;
		separator.setOrientation( SwingConstants.HORIZONTAL );
	}
	
	public void bind(){
		// ignore
	}
	
	public void addActionListener( ActionListener listener ){
		// ignore
	}
	
	public void removeActionListener( ActionListener listener ){
		// ignore
	}
	
	public void setOrientation( Orientation orientation ){
		if( orientation.isHorizontal() )
			separator.setOrientation( SwingConstants.VERTICAL );
        else
            separator.setOrientation( SwingConstants.HORIZONTAL );
	}

	public DockAction getAction(){
		return action;
	}

	public JComponent getItem(){
		return separator;
	}

	public void unbind(){
		// ignore
	}
}
