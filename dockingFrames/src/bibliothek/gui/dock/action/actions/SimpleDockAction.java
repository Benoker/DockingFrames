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

package bibliothek.gui.dock.action.actions;

import java.awt.event.KeyEvent;
import java.util.HashMap;
import java.util.Map;

import javax.swing.Icon;
import javax.swing.KeyStroke;

import bibliothek.gui.DockController;
import bibliothek.gui.Dockable;
import bibliothek.gui.dock.DockElement;
import bibliothek.gui.dock.event.DockHierarchyEvent;
import bibliothek.gui.dock.event.DockHierarchyListener;
import bibliothek.gui.dock.event.KeyboardListener;

/**
 * A simple implementation of {@link DockAction}. This action uses for
 * all associated {@link Dockable Dockables} the same settings.
 * @author Benjamin Sigg
 */
public abstract class SimpleDockAction extends AbstractStandardDockAction {
	/** Icon shown if the action is enabled */
	private Icon icon;
	
	/** Icon shown if the action is not enabled */
	private Icon disabledIcon;
    
	/** Text of the action */
    private String text;
    
    /** Tooltip for buttons showing this action */
    private String tooltip;
    
    /** Whether this action can be triggered or not */
    private boolean enabled = true;
    
    /** allows to invoke this event by the keyboard, might be <code>null</code> */
    private KeyStroke accelerator;
    
    private Map<Dockable, DockableKeyForwarder> forwarders =
    	new HashMap<Dockable, DockableKeyForwarder>();
    
    @Override
    protected void bound( Dockable dockable ){
    	super.bound( dockable );
    	DockableKeyForwarder forwarder = new DockableKeyForwarder( dockable );
    	forwarders.put( dockable, forwarder );
    }
    
    @Override
    protected void unbound( Dockable dockable ){
    	super.unbound( dockable );
    	DockableKeyForwarder forwarder = forwarders.remove( dockable );
    	forwarder.destroy();
    }
    
    public Icon getIcon( Dockable dockable ) {
        return icon;
    }

    public String getText( Dockable dockable ) {
        return text;
    }
    
    /**
     * Gets the text that is shown for this action.
     * @return The text of this action, may be <code>null</code>
     * @see #setText(String)
     */
    public String getText() {
        return text;
    }
    
    /**
     * Sets the <code>text</code> which is shown for this action.
     * @param text The text to show, or <code>null</code>
     */
    public void setText( String text ) {
        this.text = text;
        fireActionTextChanged( getBoundDockables() );
    }

    public String getTooltipText( Dockable dockable ) {
        return tooltip;
    }
    
    /**
     * Gets the toopltip-text that is shown for this action.
     * @return The tooltip
     * @see #setTooltipText(String)
     */
    public String getTooltipText() {
        return tooltip;
    }
    
    /**
     * Sets the tooltip-text which is shown for this action.
     * @param tooltip The tooltip for this action
     */
    public void setTooltipText( String tooltip ) {
        this.tooltip = tooltip;
        fireActionTooltipTextChanged( getBoundDockables() );
    }
    
    public boolean isEnabled( Dockable dockable ) {
        return enabled;
    }
    
    /**
     * Gets the enabled-state for this action. Only an action that
     * is enabled can be triggered.
     * @return <code>true</code> if this action can be triggered,
     * <code>false</code> otherwise
     * @see #setEnabled(boolean)
     */
    public boolean isEnabled() {
        return enabled;
    }
    
    /**
     * Sets the enabled-state of this action. This action can be triggered
     * only if it is enabled.
     * @param enabled The state
     */
    public void setEnabled( boolean enabled ) {
        if( this.enabled != enabled ){
            this.enabled = enabled;
            fireActionEnabledChanged( getBoundDockables() );
        }
    }
    
    /**
     * Gets the default-icon that is shown for this action.
     * @return The icon, may be <code>null</code>
     * @see #setIcon(Icon)
     */
    public Icon getIcon(){
        return icon;
    }
    
    /**
     * Sets the default-<code>icon</code> for this action. This icon
     * will be shown when no other icon fits the current states of
     * the action.
     * @param icon The icon, can be <code>null</code>
     */
    public void setIcon( Icon icon ) {
        this.icon = icon;
        fireActionIconChanged( getBoundDockables() );
    }
    
    /**
     * Gets the icon that is shown when this action is not enabled.
     * @return The disabled-icon, may be <code>null</code>
     * @see #setDisabledIcon(Icon)
     * @see #isEnabled()
     */
    public Icon getDisabledIcon() {
        return disabledIcon;
    }
    
    public Icon getDisabledIcon( Dockable dockable ){
    	return disabledIcon;
    }
    
    /**
     * Sets an icon that will be shown when this action is not enabled.
     * @param disabledIcon The disabled-icon, can be <code>null</code>
     * @see #setEnabled(boolean)
     */
    public void setDisabledIcon( Icon disabledIcon ) {
		this.disabledIcon = disabledIcon;
    	fireActionDisabledIconChanged( getBoundDockables() );	
    }
    
    /**
     * Gets the type of {@link KeyEvent} that must happen to trigger this
     * action.
     * @return the type of event or <code>null</code>
     */
    public KeyStroke getAccelerator(){
		return accelerator;
	}
    
    /**
     * Sets the type of event that will trigger this action.
     * @param accelerator the type of event or <code>null</code>.
     */
    public void setAccelerator( KeyStroke accelerator ){
		this.accelerator = accelerator;
	}
    
    /**
     * Called when the event that matches {@link #getAccelerator()} has been
     * fired.
     * @param dockable the element to which the event belongs
     * @param element the source of the event
     * @param event the event itself
     * @return <code>true</code> if the event has been used up
     */
    protected abstract boolean acceleratorTriggered( Dockable dockable, DockElement element, KeyEvent event );
   
    
    /**
     * Listens to all {@link KeyEvent}s concerning one {@link Dockable}.
     * @author Benjamin Sigg
     *
     */
    private class DockableKeyForwarder implements KeyboardListener, DockHierarchyListener{
    	/** the element which is observed by this listener */
    	private Dockable dockable;

    	/**
    	 * Creates a new forwarder.
    	 * @param dockable the element for which the calls will be forwarded
    	 */
    	public DockableKeyForwarder( Dockable dockable ){
    		this.dockable = dockable;
    		dockable.addDockHierarchyListener( this );
    		if( dockable.getController() != null )
    			dockable.getController().getKeyboardController().addListener( this );
    	}
    	
    	public void hierarchyChanged( DockHierarchyEvent event ){
    		// do nothing
    	}
    	
    	public void controllerChanged( DockHierarchyEvent event ){
    		if( event.getController() != null )
    			event.getController().getKeyboardController().removeListener( this );
    		
    		if( dockable.getController() != null )
    			dockable.getController().getKeyboardController().addListener( this );
    	}
    	
    	/**
    	 * Removes all listeners added by this forwarder.
    	 */
    	public void destroy(){
    		if( dockable.getController() != null )
    			dockable.getController().getKeyboardController().removeListener( this );
    		dockable.removeDockHierarchyListener( this );
    	}
    	
    	/**
    	 * Calls {@link AbstractStandardDockAction#acceleratorTriggered(DockElement, KeyEvent)}
    	 * if the event matches the accelerator-{@link KeyStroke}.
    	 * @param element the element on which the event occurred
    	 * @param event the event
    	 * @return <code>true</code> if the event has been consumed
    	 */
    	private boolean forward( DockElement element, KeyEvent event ){
    		if( accelerator != null ){
				if( accelerator.equals( KeyStroke.getKeyStrokeForEvent( event ) )){
					acceleratorTriggered( dockable, element, event );
					return true;
				}
			}
			
			return false;
    	}
    	
		public boolean keyPressed( DockElement element, KeyEvent event ){
			return forward( element, event );
		}

		public boolean keyReleased( DockElement element, KeyEvent event ){
			return forward( element, event );
		}

		public boolean keyTyped( DockElement element, KeyEvent event ){
			return forward( element, event );
		}

		public DockElement getTreeLocation(){
			return dockable;
		}
    }
}
