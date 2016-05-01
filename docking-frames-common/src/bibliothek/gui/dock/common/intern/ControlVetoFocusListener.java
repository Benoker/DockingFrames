package bibliothek.gui.dock.common.intern;

import bibliothek.gui.DockController;
import bibliothek.gui.Dockable;
import bibliothek.gui.dock.common.CControl;
import bibliothek.gui.dock.common.event.CVetoFocusListener;
import bibliothek.gui.dock.control.focus.FocusController;
import bibliothek.gui.dock.event.FocusVetoListener;
import bibliothek.gui.dock.title.DockTitle;
import bibliothek.util.FrameworkOnly;

/**
 * This listener observes a {@link DockController} and forwards
 * all calls to a {@link CVetoFocusListener}.
 * @author Benjamin Sigg
 */
@FrameworkOnly
public class ControlVetoFocusListener implements FocusVetoListener{
	private CControl control;
	private CVetoFocusListener callback;
	
	/**
	 * Creates a new veto focus listener.
	 * @param control the control in whose realm this listener operates
	 * @param callback the callback to be called if an event is triggered
	 */
	public ControlVetoFocusListener( CControl control, CVetoFocusListener callback ){
		this.control = control;
		this.callback = callback;
	}

	private FocusVeto veto( Dockable dockable ){
		Dockable current = control.intern().getController().getFocusedDockable();
		
		if( current != dockable ){
			if( current instanceof CommonDockable ){
				if( !callback.willLoseFocus( ((CommonDockable)current).getDockable() ))
					return FocusVeto.VETO;
			}

			if( dockable instanceof CommonDockable ){
				if( !callback.willGainFocus( ((CommonDockable)dockable).getDockable() ))
					return FocusVeto.VETO;
			}
		}
		return FocusVeto.NONE;
	}
	
	public FocusVeto vetoFocus( FocusController controller, DockTitle title ){
		return veto( title.getDockable() );
	}

	public FocusVeto vetoFocus( FocusController controller, Dockable dockable ){
		return veto( dockable );
	}
}
