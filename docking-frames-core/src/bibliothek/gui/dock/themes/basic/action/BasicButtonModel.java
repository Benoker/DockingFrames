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
package bibliothek.gui.dock.themes.basic.action;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.ActionMap;
import javax.swing.Icon;
import javax.swing.InputMap;
import javax.swing.JComponent;
import javax.swing.KeyStroke;
import javax.swing.event.MouseInputAdapter;
import javax.swing.event.MouseInputListener;

import bibliothek.gui.DockController;
import bibliothek.gui.Dockable;
import bibliothek.gui.dock.DockElement;
import bibliothek.gui.dock.DockElementRepresentative;
import bibliothek.gui.dock.action.ActionContentModifier;
import bibliothek.gui.dock.action.DockAction;
import bibliothek.gui.dock.event.DockHierarchyEvent;
import bibliothek.gui.dock.event.DockHierarchyListener;
import bibliothek.gui.dock.themes.border.BorderModifier;
import bibliothek.gui.dock.title.DockTitle;
import bibliothek.gui.dock.title.DockTitle.Orientation;
import bibliothek.gui.dock.util.BackgroundComponent;
import bibliothek.gui.dock.util.BackgroundPaint;
import bibliothek.gui.dock.util.DockUtilities;
import bibliothek.util.container.Triple;

/**
 * A class containing all properties and methods needed to handle a button-component
 * that shows the contents of a {@link DockAction}.<br>
 * A model is normally instantiated by a {@link JComponent} which uses <code>this</code>
 * as argument for the constructor of the model. The component can use a subclass
 * of the model to override {@link #changed()}, which is invoked every time when
 * a property of this model changes. The model will add some listeners to
 * the button and update its properties when necessary.
 * @author Benjamin Sigg
 */
public class BasicButtonModel {
    /** whether this model is selected or not */
    private boolean selected = false;
    
    /** the icons shown for this model */
    private Map<ActionContentModifier, Icon> icons = new HashMap<ActionContentModifier, Icon>();
    
    /** automatically created icons used when this model is not enabled */
    private Map<ActionContentModifier, Icon> disabledIcons = new HashMap<ActionContentModifier, Icon>();    
    
    /** the element which is represented by the action */
    private DockActionRepresentative representative;
    
    /** whether the mouse is inside the button or not */
    private boolean mouseInside = false;
    /** whether the first button of the mouse is currently pressed or not */
    private boolean mousePressed = false;
    
    /** the text of the action */
    private String text;
    
    /** the graphical representation of this model */
    private JComponent owner;
    
    /** the orientation of the view */
    private Orientation orientation = Orientation.FREE_HORIZONTAL;
    
    /** a callback used when the user clicked on the view */
    private BasicTrigger trigger;
    
    /** to initialize resources, can be <code>null</code> */
    private BasicResourceInitializer initializer;
    
    /** the algorithm that should be used to paint the background of a component */
    private BackgroundPaint background;
    
    /** the source of {@link #background} */
    private BackgroundComponent backgroundComponent;
    
    /** listeners that were added to this model */
    private List<BasicButtonModelListener> listeners = new ArrayList<BasicButtonModelListener>();
    
    /** a list of borders to use by the associated button */
    private Map<String, BorderModifier> borders = new HashMap<String, BorderModifier>();
    
    /** the controller in whose realm this model is used */
    private DockController controller;
    
    /**
     * Creates a new model.
     * @param owner the view of this model
     * @param trigger the callback used when the user clicks on the view
     * @param initializer a strategy to lazily initialize resources, can be <code>null</code>
     */
    public BasicButtonModel( JComponent owner, BasicTrigger trigger, BasicResourceInitializer initializer ){
        this( owner, trigger, initializer, true );
    }

    /**
     * Creates a new model.
     * @param owner the view of this model
     * @param trigger the callback used when the user clicks on the view
     * @param initializer a strategy to lazily initialize resources, can be <code>null</code>
     * @param createListener whether to create and add a {@link MouseListener} and
     * a {@link MouseMotionListener} to <code>owner</code>. If this argument
     * is <code>false</code>, then the client is responsible to update all
     * properties of this model.
     */
    public BasicButtonModel( JComponent owner, BasicTrigger trigger, BasicResourceInitializer initializer, boolean createListener ){
        this.owner = owner;
        this.trigger = trigger;
        this.initializer = initializer;
        
        if( createListener ){
            Listener listener = new Listener();
            owner.addMouseListener( listener );
            owner.addMouseMotionListener( listener );
        }
        
        List<Triple<KeyStroke, String, Action>> actions = listActions();
        if( actions != null ){
            InputMap inputMap = owner.getInputMap();
            ActionMap actionMap = owner.getActionMap();
            
            for( Triple<KeyStroke, String, Action> action : actions ){
                inputMap.put( action.getA(), action.getB() );
                actionMap.put( action.getB(), action.getC() );
            }
        }
    }
    
    /**
     * Gets a list of {@link KeyStroke}s, String keys and {@link Action}s which
     * are to be applied to the {@link #getOwner() owner} of this model. 
     * @return the list of actions
     */
    protected List<Triple<KeyStroke, String, Action>> listActions(){
        List<Triple<KeyStroke, String, Action>> actions = new ArrayList<Triple<KeyStroke,String,Action>>();
        
        Triple<KeyStroke, String, Action> select = new Triple<KeyStroke, String, Action>();
        select.setA( KeyStroke.getKeyStroke( KeyEvent.VK_SPACE, 0, false ) );
        select.setB( "button_model_select" );
        select.setC( new AbstractAction(){
            public void actionPerformed(java.awt.event.ActionEvent e){
                setMousePressed( true );
            }
        });
        actions.add( select );
        
        Triple<KeyStroke, String, Action> trigger = new Triple<KeyStroke, String, Action>();
        trigger.setA( KeyStroke.getKeyStroke( KeyEvent.VK_SPACE, 0, true ) );
        trigger.setB( "button_model_trigger" );
        trigger.setC( new AbstractAction(){
            public void actionPerformed(java.awt.event.ActionEvent e){
                if( mousePressed ){
                    setMousePressed( false );
                    if( isEnabled() ){
                        trigger();
                    }
                }
            }
        });
        actions.add( trigger );
        
        return actions;
    }
    
    /**
     * Adds a listener to this model.
     * @param listener the new listener
     */
    public void addListener( BasicButtonModelListener listener ){
    	if( listener == null )
    		throw new NullPointerException( "listener must not be null" );
    	listeners.add( listener );
    }
    
    /**
     * Informs this model about the {@link DockController} in whose realm it is used.
     * @param controller the realm in which this model works
     */
    public void setController( DockController controller ){
    	if( this.controller != null ){
    		DockController old = this.controller;
    		this.controller = null;
    		for( BasicButtonModelListener listener : listeners() ){
    			listener.unbound( this, old );
    		}
    	}
    	
    	if( controller != null ){
    		this.controller = controller;
    		for( BasicButtonModelListener listener : listeners() ){
    			listener.bound( this, this.controller );
    		}
    	}
    }
    
    /**
     * Removes a listener from this model.
     * @param listener the listener to remove
     */
    public void removeListener( BasicButtonModelListener listener ){
    	listeners.remove( listener );
    }
    
    /**
     * Gets all the listeners that are known to this model.
     * @return the listeners
     */
    protected BasicButtonModelListener[] listeners(){
    	return listeners.toArray( new BasicButtonModelListener[ listeners.size() ] );
    }
    
    /**
     * Gets the view which paints the properties of this model.
     * @return the view
     */
    public JComponent getOwner() {
        return owner;
    }
    
    /**
     * Sets the algorithm which should be used to paint the background of the owner.
     * @param background the background algorithm, can be <code>null</code>
     * @param backgroundComponent the source of <code>background</code>. Must not be <code>null</code> if 
     * <code>background</code> is not <code>null</code>, must represents {@link #getOwner()} as {@link Component}.
     */
    public void setBackground( BackgroundPaint background, BackgroundComponent backgroundComponent ){
		if( this.background != background ){
			if( background != null ){
				if( backgroundComponent == null ){
					throw new IllegalArgumentException( "backgroundComponent must not be null" );
				}
				if( backgroundComponent.getComponent() != getOwner() ){
					throw new IllegalArgumentException( "backgroundComponent must exactly represent 'getOwner()'" );
				}
			}
			
			BackgroundPaint old = this.background;
			this.background = background;
			this.backgroundComponent = backgroundComponent;
			
			for( BasicButtonModelListener listener : listeners() ){
				listener.backgroundChanged( this, old, background );
			}
		}
	}
    
    /**
     * Gets the algorithm which should be used to paint the background of components.
     * @return the background, can be <code>null</code>
     */
    public BackgroundPaint getBackground(){
		return background;
	}
    
    /**
     * Gets the source of {@link #getBackground()}. 
     * @return the source, can be <code>null</code> if {@link #getBackground()} returns <code>null</code>
     */
    public BackgroundComponent getBackgroundComponent(){
		return backgroundComponent;
	}
    
    /**
     * Gets the {@link DockAction} which is handled by this model. This method may return <code>null</code>
     * because not every button actually is connected to a {@link DockAction}.
     * @return the action or <code>null</code>
     */
    public DockAction getAction(){
    	if( trigger == null ){
    		return null;
    	}
    	return trigger.getAction();
    }
    
    /**
     * Gets the {@link Dockable} for which the button is shown. This method may return <code>null</code>
     * because not every button is connected to a {@link Dockable}.
     * @return the dockable or <code>null</code>
     */
    public Dockable getDockable(){
    	if( trigger == null ){
    		return null;
    	}
    	return trigger.getDockable();
    }
    
    /**
     * Sets the border for some state of the component that displays this model. Which identifiers
     * for <code>key</code> are actually used depends on that component.
     * @param key the key of the border
     * @param border the new border or <code>null</code>
     */
    public void setBorder( String key, BorderModifier border ){
        BorderModifier oldBorder = borders.get( key );
        if( oldBorder != border ){
        	if( border == null ){
        		borders.remove( key );
        	}
        	else{
        		borders.put( key, border );
        	}
        	for( BasicButtonModelListener listener : listeners() ){
        		listener.borderChanged( this, key, oldBorder, border );
        	}
        }
    }
    
    /**
     * Gets the border which is used for the state <code>key</code>. The exact value of
     * key depends on the component which shows this model.
     * @param key the key for some border
     * @return the border or <code>null</code> if not found
     */
    public BorderModifier getBorder( String key ){
    	if( initializer != null ){
    		initializer.ensureBorder( this, key );
    	}
    	return borders.get( key );
    }
    
    /**
     * Removes any icon that was ever set by {@link #setIcon(ActionContentModifier, Icon)}.
     */
    public void clearIcons(){
    	for( ActionContentModifier key : getIconContexts() ){
    		setIcon( key, null );
    	}
    }
    
    /**
     * Gets all the {@link ActionContentModifier}s for which an icon is set. 
     * @return all the contexts in which an icon is available
     */
    public ActionContentModifier[] getIconContexts(){
    	return icons.keySet().toArray( new ActionContentModifier[ icons.size() ] );
    }

    /**
     * Sets the text of this button, some button implementations may ignore the text.
     * @param text the new text, can be <code>null</code>
     */
    public void setText( String text ){
		String oldText = this.text;
		this.text = text;
		for( BasicButtonModelListener listener : listeners() ){
			listener.textChanged( this, oldText, text );
		}
		changed();
	}
    
    /**
     * Gets the text of this button.
     * @return the text, which may be <code>null</code>
     */
    public String getText(){
		return text;
	}
    
    /**
     * Sets the icon which is normally shown on the view.
     * @param modifier the context in which to use the icon, not <code>null</code>
     * @param icon the new icon, can be <code>null</code>
     */
    public void setIcon( ActionContentModifier modifier, Icon icon ){
    	Icon oldIcon = icons.remove( modifier );
    	if( icon == null ){
    		icons.remove( modifier );
    	}
    	else{
    		icons.put( modifier, icon );
    	}
        disabledIcons.remove( modifier );
        for( BasicButtonModelListener listener : listeners() ){
        	listener.iconChanged( this, modifier, oldIcon, icon );
        }
        changed();
    }
    
    /**
     * Sets the <code>selected</code> property. The view may be painted in
     * a different way dependent on this value.
     * @param selected the new value
     */
    public void setSelected( boolean selected ) {
    	if( this.selected != selected ){
	        this.selected = selected;
	        for( BasicButtonModelListener listener : listeners() ){
	        	listener.selectedStateChanged( this, selected );
	        }
	        changed();
    	}
    }
    
    /**
     * Tells whether this model is selected or not.
     * @return the property
     */
    public boolean isSelected() {
        return selected;
    }
    
    /**
     * Sets the <code>enabled</code> property of this model. A model will not
     * react on a mouse-click if it is not enabled.
     * @param enabled the value
     */
    public void setEnabled( boolean enabled ) {
        owner.setEnabled( enabled );
        if( !enabled ){
            setMousePressed( false );
        }
        
        for( BasicButtonModelListener listener : listeners() ){
        	listener.enabledStateChanged( this, enabled );
        }
        
        changed();
    }
    
    /**
     * Tells whether this model reacts on mouse-clicks or not.
     * @return the property
     */
    public boolean isEnabled() {
        return owner.isEnabled();
    }
    
    /**
     * Sets the text which should be used as tooltip. The text is directly
     * forwarded to the {@link #getOwner() owner} of this model using
     * {@link JComponent#setToolTipText(String) setToolTipText}.
     * @param tooltip the text, can be <code>null</code>
     */
    public void setToolTipText( String tooltip ){
        String old = owner.getToolTipText();
        
        for( BasicButtonModelListener listener : listeners() ){
        	listener.tooltipChanged( this, old, tooltip );
        }
        
        owner.setToolTipText( tooltip );
    }
    
    /**
     * Tells this model which orientation the {@link DockTitle} has, on which
     * the view of this model is displayed.
     * @param orientation the orientation, not <code>null</code>
     */
    public void setOrientation( Orientation orientation ) {
        if( orientation == null  )
            throw new IllegalArgumentException( "Orientation must not be null" );
        
        Orientation old = this.orientation;
        this.orientation = orientation;
        
        
        for( BasicButtonModelListener listener : listeners() ){
        	listener.orientationChanged( this, old, orientation );
        }
        
        changed();
    }
    
    /**
     * Sets the {@link Dockable} for which a {@link DockElementRepresentative} has to be installed.
     * @param dockable the dockable to monitor, can be <code>null</code>
     */
    public void setDockableRepresentative( Dockable dockable ){
    	if( representative != null ){
    		representative.unbind();
    		representative = null;
    	}
    	if( dockable != null ){
    		representative = new DockActionRepresentative( dockable );
    		representative.bind();
    	}
    }
    
    /**
     * Gets the orientation of the {@link DockTitle} on which the view of
     * this model is displayed.
     * @return the orientation
     * @see #setOrientation(DockTitle.Orientation)
     */
    public Orientation getOrientation() {
        return orientation;
    }
    
    /**
     * Called whenever a property of the model has been changed. The 
     * default behavior is just to call {@link Component#repaint() repaint}
     * of the {@link #getOwner() owner}. Clients are encouraged to override
     * this method.
     */
    public void changed(){
        owner.repaint();
    }
    
    /**
     * Gets the maximum size the icons need.
     * @return the maximum size of all icons
     */
    public Dimension getMaxIconSize(){
        int w = 0;
        int h = 0;
        
        for( Icon icon : icons.values() ){
            w = Math.max( w, icon.getIconWidth() );
            h = Math.max( h, icon.getIconHeight() );
        }
        
        return new Dimension( w, h );
    }
    
    /**
     * Gets the icon which should be painted on the view.
     * @return the icon to paint, can be <code>null</code>
     */
    public Icon getPaintIcon(){
        return getPaintIcon( isEnabled() );
    }
    
    /**
     * Gets the icon which should be painted on the view.
     * @param enabled whether the enabled or the disabled version of the
     * icon is requested.
     * @return the icon or <code>null</code>
     */
    public Icon getPaintIcon( boolean enabled ){
    	ActionContentModifier modifier;
    	if( enabled ){
    		if( mousePressed ){
    			if( orientation == null || orientation.isHorizontal() ){
    				modifier = ActionContentModifier.NONE_PRESSED_HORIZONTAL;
    			}
    			else{
    				modifier = ActionContentModifier.NONE_PRESSED_VERTICAL;
    			}
    		}
    		else if( mouseInside ){
    			if( orientation == null || orientation.isHorizontal() ){
    				modifier = ActionContentModifier.NONE_HOVER_HORIZONTAL;
    			}
    			else{
    				modifier = ActionContentModifier.NONE_HOVER_VERTICAL;
    			}
    		}
    		else{
    			if( orientation == null || orientation.isHorizontal() ){
    				modifier = ActionContentModifier.NONE_HORIZONTAL;
    			}
    			else{
    				modifier = ActionContentModifier.NONE_VERTICAL;
    			}
    		}
    	}
    	else{
    		if( mousePressed ){
    			if( orientation == null || orientation.isHorizontal() ){
    				modifier = ActionContentModifier.DISABLED_PRESSED_HORIZONTAL;
    			}
    			else{
    				modifier = ActionContentModifier.DISABLED_PRESSED_VERTICAL;
    			}
    		}
    		else if( mouseInside ){
    			if( orientation == null || orientation.isHorizontal() ){
    				modifier = ActionContentModifier.DISABLED_HOVER_HORIZONTAL;
    			}
    			else{
    				modifier = ActionContentModifier.DISABLED_HOVER_VERTICAL;
    			}
    		}
    		else{
    			if( orientation == null || orientation.isHorizontal() ){
    				modifier = ActionContentModifier.DISABLED_HORIZONTAL;
    			}
    			else{
    				modifier = ActionContentModifier.DISABLED_VERTICAL;
    			}
    		}
    	}
    	
    	List<ActionContentModifier> modifiers = new LinkedList<ActionContentModifier>();
    	modifiers.add( modifier );
    	
    	while( !modifiers.isEmpty() ){
    		modifier = modifiers.remove( 0 );
    		Icon icon = icons.get( modifier );
    		if( icon != null ){
    			if( !enabled && modifier.isEnabled() ){
    				Icon disabled = disabledIcons.get( modifier );
    				if( disabled == null && !disabledIcons.containsKey( modifier )){
    					disabled = DockUtilities.disabledIcon( owner, icon );
    					disabledIcons.put( modifier, disabled );
    				}
    				if( disabled != null ){
    					icon = disabled;
    				}
    			}
    			return icon;
    		}
    		for( ActionContentModifier backup : modifier.getBackup() ){
    			modifiers.add( backup );
    		}
    	}
    	        
        // no icon to show
        return null;
    }
    
    /**
     * Changes the <code>mouseInside</code> property. The property tells whether
     * the mouse is currently inside the border of the {@link #getOwner() owner}
     * or not. Clients should not call this method unless they handle all
     * mouse events.
     * @param mouseInside whether the mouse is inside
     */
    protected void setMouseInside( boolean mouseInside ) {
        if( this.mouseInside != mouseInside ){
	    	this.mouseInside = mouseInside;
	        
	        for( BasicButtonModelListener listener : listeners() ){
	        	listener.mouseInside( this, mouseInside );
	        }
	        
	        changed();
        }
    }
    
    /**
     * Tells whether the mouse currently is inside the {@link #getOwner() owner}
     * or not.
     * @return <code>true</code> if the mouse is inside
     */
    public boolean isMouseInside() {
        return mouseInside;
    }
    
    /**
     * Changes the <code>mousePressed</code> property. The property tells
     * whether the left mouse button is currently pressed or not. Clients
     * should not invoke this method unless they handle all mouse events.
     * @param mousePressed whether button 1 is pressed
     */
    protected void setMousePressed( boolean mousePressed ) {
        if( this.mousePressed != mousePressed ){
	    	this.mousePressed = mousePressed;
	        
	        for( BasicButtonModelListener listener : listeners() ){
	        	listener.mousePressed( this, mousePressed );
	        }
	        
	        changed();
        }
    }
    
    /**
     * Tells whether the left mouse button is currently pressed or not.
     * @return <code>true</code> if the button is pressed
     */
    public boolean isMousePressed() {
        return mousePressed;
    }
    
    /**
     * Called when the left mouse button has been pressed and released within
     * the {@link #getOwner() owner} and when this model is {@link #isEnabled() enabled}.
     */
    protected void trigger(){
    	if( trigger != null ){
    		trigger.triggered();
    	}
        
        for( BasicButtonModelListener listener : listeners() ){
        	listener.triggered();
        }
    }
    
    /**
     * A mouse listener observing the view of the enclosing model.
     * @author Benjamin Sigg
     */
    private class Listener extends MouseInputAdapter{
        @Override
        public void mouseEntered( MouseEvent e ) {
            setMouseInside( true );
        }
        @Override
        public void mouseExited( MouseEvent e ) {
            setMouseInside( false );
        }
        @Override
        public void mouseDragged( MouseEvent e ) {
            boolean inside = owner.contains( e.getX(), e.getY() );
            if( inside != mouseInside )
                setMouseInside( inside );
        }
        @Override
        public void mousePressed( MouseEvent e ) {
            if( !mousePressed && e.getButton() == MouseEvent.BUTTON1 ){
                setMousePressed( true );
            }
        }
        @Override
        public void mouseReleased( MouseEvent e ) {
            if( mousePressed && e.getButton() == MouseEvent.BUTTON1 ){
                boolean inside = owner.contains( e.getX(), e.getY() );
                if( inside && isEnabled() ){
                    trigger();
                }
                
                setMousePressed( false );
                if( mouseInside != inside )
                    setMouseInside( inside );
            }
        }
    }
    
    /**
     * A wrapper around the represented {@link Dockable}.
     * @author Benjamin Sigg
     */
    private class DockActionRepresentative implements DockElementRepresentative, DockHierarchyListener{
    	private Dockable dockable;
    	private DockController controller;
    	
    	/**
    	 * Creates a new representative
    	 * @param dockable the represented {@link Dockable}
    	 */
    	public DockActionRepresentative( Dockable dockable ){
    		this.dockable = dockable;
    	}
    	
    	public void bind(){
    		dockable.addDockHierarchyListener( this );
    		controller = dockable.getController();
    		if( controller != null ){
    			controller.addRepresentative( this );
    		}
    	}
    	
    	public void unbind(){
    		dockable.removeDockHierarchyListener( this );
    		if( controller != null ){
    			controller.removeRepresentative( this );
    			controller = null;
    		}
    	}
    	
    	public void hierarchyChanged( DockHierarchyEvent event ){
    		// ignore
    	}
    	
    	public void controllerChanged( DockHierarchyEvent event ){
    		if( controller != null ){
    			controller.removeRepresentative( this );
    			controller = null;
    		}
    		controller = dockable.getController();
    		if( controller != null ){
    			controller.addRepresentative( this );
    		}
    	}
    	
		public void addMouseInputListener( MouseInputListener listener ){
			getOwner().addMouseListener( listener );
			getOwner().addMouseMotionListener( listener );
		}

		public Component getComponent(){
			return getOwner();
		}

		public DockElement getElement(){
			return dockable;
		}

		public Point getPopupLocation( Point click, boolean popupTrigger ){
			if( popupTrigger ){
				return click;
			}
			return null;
		}

		public boolean isUsedAsTitle(){
			return false;
		}

		public void removeMouseInputListener( MouseInputListener listener ){
			getOwner().removeMouseListener( listener );
			getOwner().removeMouseMotionListener( listener );
		}

		public boolean shouldFocus(){
			return false;
		}
		
		public boolean shouldTransfersFocus(){
			return true;
		}
    }
}
