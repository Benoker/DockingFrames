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

import bibliothek.extension.gui.dock.preference.editor.KeyStrokeEditor;
import bibliothek.gui.DockController;
import bibliothek.gui.Dockable;
import bibliothek.gui.dock.DockElement;
import bibliothek.gui.dock.action.ActionContentModifier;
import bibliothek.gui.dock.action.DockAction;
import bibliothek.gui.dock.disable.DisablingStrategy;
import bibliothek.gui.dock.event.DockHierarchyEvent;
import bibliothek.gui.dock.event.DockHierarchyListener;
import bibliothek.gui.dock.event.KeyboardListener;
import bibliothek.gui.dock.station.LayoutLocked;

/**
 * A simple implementation of {@link DockAction}. This action uses for
 * all associated {@link Dockable Dockables} the same settings.
 * @author Benjamin Sigg
 */
public abstract class SimpleDockAction extends AbstractStandardDockAction implements SharingStandardDockAction {
	/** the icons that are used by this action */
	private Map<ActionContentModifier, Icon> icons = new HashMap<ActionContentModifier, Icon>();
    
	/** Text of the action */
    private String text;
    
    /** Tooltip for buttons showing this action */
    private String tooltip;
    
    /** Whether this action can be triggered or not */
    private boolean enabled = true;
    
    /** allows to invoke this event by the keyboard, might be <code>null</code> */
    private KeyStroke accelerator;
    
    /** whether to globally listen for all key events */
    private boolean globalAccelerator = false;
    
    /** the {@link Dockable} which is represented by this action and for which drag and drop support may be enabled */
    private Dockable representative;
    
    private Map<Dockable, DockableKeyForwarder> forwarders = new HashMap<Dockable, DockableKeyForwarder>();
    
    /**
     * Creates a new action
     * @param monitorDisabling whether the current {@link DisablingStrategy} should be monitored
     */
    public SimpleDockAction( boolean monitorDisabling ){
    	super( monitorDisabling );
    }
    
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
    
    public Icon getIcon( Dockable dockable, ActionContentModifier modifier ){
        return icons.get( modifier );
    }

    public String getText( Dockable dockable ) {
        return text;
    }
    
    public String getText() {
        return text;
    }
    
    public void setText( String text ) {
        this.text = text;
        fireActionTextChanged( getBoundDockables() );
    }

    public String getTooltipText( Dockable dockable ) {
    	return getTooltipText();
    }
    
    public String getTooltipText(){
    	if( accelerator == null )
    		return tooltip;

    	String acceleratorText = KeyStrokeEditor.toString( accelerator, true );

    	if( tooltip == null )
    		return acceleratorText;
    	else
    		return tooltip + " (" + acceleratorText + ")";
    }
    
    public String getTooltip() {
        return tooltip;
    }
    
    public void setTooltip( String tooltip ) {
        this.tooltip = tooltip;
        fireActionTooltipTextChanged( getBoundDockables() );
    }
    
    @Override
    public boolean isEnabled( Dockable dockable ) {
        return enabled && super.isEnabled( dockable );
    }
    
    public boolean isEnabled() {
        return enabled;
    }
    
    public void setEnabled( boolean enabled ) {
        if( this.enabled != enabled ){
            this.enabled = enabled;
            fireActionEnabledChanged( getBoundDockables() );
        }
    }
    
    public Icon getIcon(){
        return icons.get( ActionContentModifier.NONE );
    }
    
    public void setIcon( Icon icon ) {
    	setIcon( ActionContentModifier.NONE, icon );
    }
    
    public ActionContentModifier[] getIconContexts( Dockable dockable ){
    	return icons.keySet().toArray( new ActionContentModifier[ icons.size() ] );
    }
    
    public Icon getDisabledIcon() {
    	return icons.get( ActionContentModifier.DISABLED );
    }
    
    public void setDisabledIcon( Icon icon ) {
		setIcon( ActionContentModifier.DISABLED, icon );	
    }
    
    /**
     * Gets the icon which is shown if the conditions of <code>modifier</code> are met.
     * @param modifier the conditions to met
     * @return the icon to show or <code>null</code> if not set
     */
    public Icon getIcon( ActionContentModifier modifier ){
    	return icons.get( modifier );
    }
    
    /**
     * Sets the icon that is to be used when the conditions of <code>modifier</code> are met.
     * @param modifier the conditions to met
     * @param icon the icon to use or <code>null</code>
     */
    public void setIcon( ActionContentModifier modifier, Icon icon ){
    	if( icon == null ){
    		icons.remove( modifier );
    	}
    	else{
    		icons.put( modifier, icon );
    	}
    	fireActionIconChanged( modifier, getBoundDockables() );
    }
    
    public void setDockableRepresentation( Dockable dockable ){
    	if( this.representative != dockable ){
    		this.representative = dockable;
    		fireActionRepresentativeChanged( getBoundDockables() );
    	}
    }
    
    public Dockable getDockableRepresentation( Dockable dockable ){
    	return representative;
    }
    
    public Dockable getDockableRepresentation(){
    	return representative;
    }
    
    public KeyStroke getAccelerator(){
		return accelerator;
	}
    
    public void setAccelerator( KeyStroke accelerator ){
		this.accelerator = accelerator;
		fireActionTooltipTextChanged( getBoundDockables() );
	}
    
    public void setAcceleratorIsGlobal( boolean global ) {
    	this.globalAccelerator = global;
    }
    
    public boolean isAcceleratorGlobal() {
    	return globalAccelerator;
    }
    
    /**
     * Called when the user hit the {@link #setAccelerator(KeyStroke) accelerator}.
     * This method directly calls <code>trigger( dockable )</code>, subclasses
     * might override this method to further analyze <code>event</code>.
     * @param event the triggering event
     * @param dockable the source of the event
     * @return <code>true</code> if this action could do anything, <code>false</code>
     * if this action was not able to react in any way to the event.
     */
    protected boolean trigger( KeyEvent event, Dockable dockable ){
        return trigger( dockable );
    }
    
    /**
     * Listens to all {@link KeyEvent}s concerning one {@link Dockable}.
     * @author Benjamin Sigg
     */
    @LayoutLocked( locked=false )
    private class DockableKeyForwarder implements KeyboardListener, DockHierarchyListener{
    	/** the element which is observed by this listener */
    	private Dockable dockable;

    	/** the controller which is currently observed by this forwarder, can be <code>null</code> */
    	private DockController controller;
    	
    	/** whether this forwarder has been destroyed */
    	private boolean destroyed = false;
    	
    	/**
    	 * Creates a new forwarder.
    	 * @param dockable the element for which the calls will be forwarded
    	 */
    	public DockableKeyForwarder( Dockable dockable ){
    		this.dockable = dockable;
    		dockable.addDockHierarchyListener( this );
    		setController( dockable.getController() );
    	}
    	
    	public void hierarchyChanged( DockHierarchyEvent event ){
    		// do nothing
    	}
    	
    	public void controllerChanged( DockHierarchyEvent event ){
    		setController( dockable.getController() );
    	}
    	
    	private void setController( DockController controller ){
    		if( this.controller != null )
    			this.controller.getKeyboardController().removeListener( this );
    		
    		if( destroyed ){
    			this.controller = null;
    		}
    		else{
    			this.controller = controller;
    		}
    		
    		if( this.controller != null ){
    			this.controller.getKeyboardController().addListener( this );
    		}
    	}
    	
    	/**
    	 * Removes all listeners added by this forwarder.
    	 */
    	public void destroy(){
    		destroyed = true;
    		setController( null );
    		dockable.removeDockHierarchyListener( this );
    	}
    	
    	/**
    	 * Calls {@link DockAction#trigger(Dockable)}
    	 * if the event matches the accelerator-{@link KeyStroke}.
    	 * @param element the element on which the event occurred
    	 * @param event the event
    	 * @return <code>true</code> if the event has been consumed
    	 */
    	private boolean forward( DockElement element, KeyEvent event ){
    		if( accelerator != null ){
				if( accelerator.equals( KeyStroke.getKeyStrokeForEvent( event ) )){
					return trigger( event, dockable );
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
			if( isAcceleratorGlobal() ){
				return null;
			}
			else{
				return dockable;
			}
		}
		
		@Override
		public String toString(){
			return getClass().getSimpleName() + " -> " + dockable.getTitleText() + " -> " + getText();
		}
    }
}
