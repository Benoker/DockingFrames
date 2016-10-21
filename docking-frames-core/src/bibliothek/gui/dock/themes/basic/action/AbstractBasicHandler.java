/*
 * Bibliothek - DockingFrames
 * Library built on Java/Swing, allows the user to "drag and drop"
 * panels containing any Swing-Component the developer likes to add.
 * 
 * Copyright (C) 2010 Benjamin Sigg
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
import java.util.HashMap;
import java.util.Map;

import bibliothek.gui.DockController;
import bibliothek.gui.Dockable;
import bibliothek.gui.dock.action.ActionDockBorder;
import bibliothek.gui.dock.action.DockAction;
import bibliothek.gui.dock.action.DockActionBackgroundComponent;
import bibliothek.gui.dock.themes.ThemeManager;
import bibliothek.gui.dock.themes.border.BorderModifier;
import bibliothek.gui.dock.util.BackgroundAlgorithm;
import bibliothek.gui.dock.util.BackgroundPaint;
import bibliothek.gui.dock.util.UIValue;

/**
 * Collection of methods that are interesting for classes that wrap around a {@link BasicButtonModel}.
 * @author Benjamin Sigg
 */
public class AbstractBasicHandler<D extends DockAction, M extends BasicButtonModel> implements BasicResourceInitializer{
	/** the action which is observed */
    private D action;
	
    /** the model which is handled by this handler */
    private M model;
    
	/** the dockable for which the action is displayed */
    private Dockable dockable;
    
	/** the background algorithm to be used  */
    private Background background = new Background();
    
    /** all the borders which are managed by this handler */
    private Map<String, BorderHandle> borders = new HashMap<String, BorderHandle>();
    
    /** whether this handler is in use */
    private boolean bound = false;

    /**
     * Creates a new handler.
     * @param action the action which will be observed.
     * @param dockable the dockable for which the action is shown
     */
    public AbstractBasicHandler( D action, Dockable dockable ){
        if( action == null )
            throw new IllegalArgumentException( "Action must not be null" );
        
        this.dockable = dockable;
        this.action = action;
    }
    
    /**
     * Gets the dockable whose action is handled.
     * @return the dockable, not <code>null</code>
     */
    public Dockable getDockable(){
		return dockable;
	}
    
    /**
     * Gets the action which is read by this handler.
     * @return the action, not <code>null</code>
     */
    public D getAction(){
		return action;
	}
    
    /**
     * Gets the model which is written by this handler.
     * @return the model, not <code>null</code>
     */
    public M getModel(){
		return model;
	}
    
    public void ensureBorder( BasicButtonModel model, String key ){
	    addBorder( key );
    }
    
    /**
     * Adds a connection between the {@link ThemeManager} and the model of this handler which transfers
     * the {@link BorderModifier} with identifier <code>key</code> to the model. Nothing happens if such a
     * connection already exists.
     * @param key the identifier of the {@link BorderModifier} to transfer
     */
    public void addBorder( String key ){
    	if( borders.get( key ) == null ){
	    	BorderHandle handle = new BorderHandle( key );
	    	borders.put( key, handle );
	    	if( bound ){
	    		handle.setController( dockable.getController() );
	    	}
    	}
    }
    
    /**
     * Sets the model to which all properties of the {@link #getAction() action}
     * are transferred.
     * @param model the model
     */
    public void setModel( M model ) {
        this.model = model;
        for( BorderHandle handle : borders.values() ){
        	if( handle.modifier != null ){
        		model.setBorder( handle.id, handle.modifier );
        	}
        }
        if( bound ){
        	model.setController( dockable.getController() );
        }
    }
    

    public void bind(){
    	if( !bound ){
    		bound = true;
    		DockController controller = dockable.getController();
    		
	        background.setController( controller );
	        
	        for( BorderHandle handle : borders.values() ){
	        	handle.setController( controller );
	        }
	        
	        model.setController( controller );
    	}
    }
    
    public void unbind(){
    	if( bound ){
    		bound = false;
    		
	        background.setController( null );
	        
	        for( BorderHandle handle : borders.values() ){
	        	handle.setController( null );
	        }
	        
	        model.setController( null );
    	}
    }
    
    /**
     * Tells whether {@link #bind()} was called.
     * @return <code>true</code> if this handler is in use, <code>false</code> otherwise
     */
    public boolean isBound(){
		return bound;
	}
    
    /**
     * The background algorithm to be used by the {@link BasicHandler#getModel() model}.
     * @author Benjamin Sigg
     */
    private class Background extends BackgroundAlgorithm implements DockActionBackgroundComponent{
    	public Background(){
    		super( DockActionBackgroundComponent.KIND, ThemeManager.BACKGROUND_PAINT + ".action" );
    	}
    	
    	@Override
    	public void set( BackgroundPaint value ){
    		super.set( value );
    		model.setBackground( getPaint(), this );
    	}
    	
		public DockAction getAction(){
			return action;
		}

		public Dockable getDockable(){
			return dockable;
		}

		public Component getComponent(){
			return model.getOwner();
		}
    }
    
    /**
     * A {@link BorderModifier} that is used by the representation of the model. 
     * @author Benjamin Sigg
     */
    private class BorderHandle implements ActionDockBorder{
    	/** the identifier of this value */
    	private String id;
    	
    	/** the current modifier of the border */
    	private BorderModifier modifier;
    	
    	/** the current controller */
    	private DockController controller;
    	
    	/**
    	 * Creates a new wrapper.
    	 * @param id the identifier of this value
    	 */
    	public BorderHandle( String id ){
    		this.id = id;
    	}
    	
    	/**
    	 * Links this {@link UIValue} with <code>controller</code>.
    	 * @param controller the new source of values, can be <code>null</code>
    	 */
    	public void setController( DockController controller ){
    		if( this.controller != null ){
    			this.controller.getThemeManager().remove( this );
    		}
			this.controller = controller;
			if( this.controller == null ){
				set( null );
			}
			else{
				this.controller.getThemeManager().add( id, ActionDockBorder.KIND, ThemeManager.BORDER_MODIFIER_TYPE, this );
			}
		}

    	public void set( BorderModifier value ){
    		if( modifier != value ){
    			modifier = value;
    			if( model != null ){
    				model.setBorder( id, value );
    			}
    		}
    	}

		public DockAction getAction(){
			return action;
		}

		public Dockable getDockable(){
			return dockable;
		}
    }
}
