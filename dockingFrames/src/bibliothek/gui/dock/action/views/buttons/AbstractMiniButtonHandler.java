/**
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

package bibliothek.gui.dock.action.views.buttons;

import java.util.Set;

import javax.swing.JComponent;

import bibliothek.gui.Dockable;
import bibliothek.gui.dock.action.StandardDockAction;
import bibliothek.gui.dock.event.StandardDockActionListener;
import bibliothek.gui.dock.title.DockTitle.Orientation;

/**
 * A {@link MiniButtonHandler} that forwards icons and tooltips directly
 * from a {@link StandardDockAction}
 * @author Benjamin Sigg
 *
 * @param <D> the type of action used on this handler
 * @param <T> the type of button shown for this handler
 */
public abstract class AbstractMiniButtonHandler<D extends StandardDockAction, T extends MiniButton> implements MiniButtonHandler<D, T>{
	/** the mini-button which is handled by this model */
	private T button;
	
	/** the action whose values are transformed for <code>button</code> */
	private D action;
	
	/** the owner of <code>action</code> */
	private Dockable dockable;
	
	/** a listener to <code>action</code> */
	private Listener listener;
	
	/** the location of the title on which this button is */
	private Orientation orientation = Orientation.FREE_HORIZONTAL;
	
	/**
	 * Creates a new model and sets the model at <code>button</code>.
	 * @param action the action which is observed
	 * @param dockable the owner of <code>action</code>
	 * @param button the button which will be managed by this model
	 */
	public AbstractMiniButtonHandler( D action, Dockable dockable, T button ){
		this.action = action;
		this.button = button;
		this.dockable = dockable;
		
		listener = new Listener();
		button.setHandler( this );
	}
	
	/**
	 * Connects this model to the observed action.
	 */
	public void bind(){
		String tooltip = action.getTooltipText( dockable );
		if( tooltip == null || tooltip.length() == 0 )
			tooltip = action.getText( dockable );
		button.setToolTipText( tooltip );
		button.setIcon( action.getIcon( dockable ) );
		button.setDisabledIcon( action.getDisabledIcon( dockable ) );
		button.setEnabled( action.isEnabled( dockable ) );
		
		action.addDockActionListener( listener );
	}
	
	public void unbind(){
		action.removeDockActionListener( listener );
	}
	
	public T getButton(){
		return button;
	}
	
	public Dockable getDockable(){
		return dockable;
	}
	
	public D getAction(){
		return action;
	}
	
	public JComponent getItem(){
		return button;
	}
	
	public void setOrientation( Orientation orientation ){
		this.orientation = orientation;
	}
	
	public Orientation getOrientation(){
		return orientation;
	}

	/**
	 * Updates the tooltip of {@link #button}
	 */
	private void updateTooltip(){
		String tip = action.getTooltipText( dockable );
        if( tip == null || tip.equals( "" ))
            button.setToolTipText( action.getText( dockable ));
        else
            button.setToolTipText( tip );
	}
	
	/**
	 * A listener intended for {@link MiniButtonHandler#getAction()}.
	 * @author Benjamin Sigg
	 */
	private class Listener implements StandardDockActionListener{
        public void actionEnabledChanged( StandardDockAction action, Set<Dockable> dockables ) {
            if( dockables.contains( dockable )){
                button.setEnabled( action.isEnabled( dockable ));
            }
        }
        
        public void actionIconChanged( StandardDockAction action, Set<Dockable> dockables ) {
            if( dockables.contains( dockable )){
            	button.setIcon( action.getIcon( dockable ) );
            }
        }
        
        public void actionDisabledIconChanged( StandardDockAction action, Set<Dockable> dockables ){
        	if( dockables.contains( dockable )){
        		button.setDisabledIcon( action.getDisabledIcon( dockable ));
        	}
        }
        
        public void actionTextChanged( StandardDockAction action, Set<Dockable> dockables ) {
            if( dockables.contains( dockable )){
                updateTooltip();
            }
        }
        
        public void actionTooltipTextChanged( StandardDockAction action, Set<Dockable> dockables ) {
            if( dockables.contains( dockable )){
                updateTooltip();
            }
        }
	}
}
