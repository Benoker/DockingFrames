/*
 * Bibliothek - DockingFrames
 * Library built on Java/Swing, allows the user to "drag and drop"
 * panels containing any Swing-Component the developer likes to add.
 * 
 * Copyright (C) 2008 Benjamin Sigg
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
package bibliothek.extension.gui.dock.preference;

import java.util.ArrayList;
import java.util.List;

import javax.swing.Icon;

import bibliothek.gui.DockUI;
import bibliothek.gui.dock.util.IconManager;

/**
 * Represents an operation that a {@link PreferenceEditor} or a {@link PreferenceModel} can
 * execute, e.g. "reset value to default". This class does not contain any code to
 * perform the operation, it just serves as key for the operation. The actual implementation
 * is part of the editor or the model.
 * @author Benjamin Sigg
 */
public class PreferenceOperation {
    /**
     * Operation for deleting a property.
     */
    public static final PreferenceOperation DELETE = new PreferenceOperation(
            "delete", 
            null,
            DockUI.getDefaultDockUI().getString( "preference.operation.delete" ));
    
    /**
     * Operation for setting a property to its default value
     */
    public static final PreferenceOperation DEFAULT = new PreferenceOperation(
            "default", 
            null,
            DockUI.getDefaultDockUI().getString( "preference.operation.default" ));
    
    static{
    	DELETE.setIconId( "delete.small" );
    	DEFAULT.setIconId( "default.small" );
    }
    
    private String key;
    
    private Icon icon;
    private String iconId = "null";
    
    private String description;
    
    /** all the views of this operation */
    private List<View> views = new ArrayList<View>();
    
    /**
     * Creates a new operation.
     * @param key the unique identifier of this operation
     */
    public PreferenceOperation( String key ){
        if( key == null )
            throw new IllegalArgumentException( "key must not be null" );
        this.key = key;
    }
    
    /**
     * Creates a new operation.
     * @param key the unique identifier of this operation
     * @param icon an icon for this operation, should have a size of 10x10 pixels
     * @param description a small description of this operation
     */
    public PreferenceOperation( String key, Icon icon, String description ){
        this( key );
        setIcon( icon );
        setDescription( description );
    }
    
    /**
     * Creates and returns a view of this {@link PreferenceOperation} for
     * <code>model</code>.
     * @param model the model using the operation
     * @return the view
     */
    public PreferenceOperationView create( PreferenceModel model ){
    	return new View( model );
    }
    
    @Override
    public int hashCode() {
        return key.hashCode();
    }

    @Override
    public boolean equals( Object obj ) {
        if( obj instanceof PreferenceOperation ){
            return key.equals( ((PreferenceOperation)obj).key );
        }
        
        return false;
    }
    
    /**
     * Gets an icon for this operation. The icon should have a size of 10x10 pixels.
     * @return the icon for this operation
     */
    public Icon getIcon() {
        return icon;
    }
    
    /**
     * Sets an icon for this operation. The icon should have a size of 10x10 pixels.
     * @param icon the new icon, can be <code>null</code>
     */
    public void setIcon( Icon icon ) {
        this.icon = icon;
        for( View view : views ){
        	view.icon.setValue( icon );
        }
    }
    
    /**
     * Gets the current identifier for the icon of this operation.
     * @return the identifier
     * @see #setIconId(String)
     */
    public String getIconId(){
		return iconId;
	}
    
    /**
     * Sets the identifier for the icon, the identifier will be used to read an icon 
     * from the {@link IconManager}.
     * @param iconId the new id, can not be <code>null</code>
     */
    public void setIconId( String iconId ){
    	if( iconId == null ){
    		throw new IllegalArgumentException( "iconId must not be null" );
    	}
    	
    	this.iconId = iconId;
    	for( View view : views ){
    		view.icon.setId( iconId );
    	}
    }
    
    /**
     * Gets a short human readable description of this operation.
     * @return the short description
     */
    public String getDescription() {
        return description;
    }
    
    /**
     * Sets a human readable description of this operation.
     * @param description the description
     */
    public void setDescription( String description ) {
        this.description = description;
    }
    
    /**
     * The view of a {@link PreferenceOperation}
     * @author Benjamin Sigg
     */
    private class View implements PreferenceOperationView{
    	private List<PreferenceOperationViewListener> listeners = new ArrayList<PreferenceOperationViewListener>();
    
    	private PreferenceOperationIcon icon;
    	private Icon currentIcon;
    	
    	public View( PreferenceModel model ){
    		views.add( this );
    		
    		icon = new PreferenceOperationIcon( iconId, getOperation() ){
    			@Override
    			protected void changed( Icon oldValue, Icon newValue ){
    				currentIcon = newValue;
    				fireIconChanged( oldValue, newValue );
    			}
    		};
    		icon.setValue( PreferenceOperation.this.icon );
    		icon.setManager( model.getController().getIcons() );
    	}

		public void destroy(){
			views.remove( this );
			icon.setManager( null );
		}

		public String getDescription(){
			return PreferenceOperation.this.getDescription();
		}

		public Icon getIcon(){
			return currentIcon;
		}

		public PreferenceOperation getOperation(){
			return PreferenceOperation.this;
		}
    	
		private void fireIconChanged( Icon oldIcon, Icon newIcon ){
			for( PreferenceOperationViewListener listener : listeners ){
				listener.iconChanged( this, oldIcon, newIcon );
			}
		}
		
		private void fireDescriptionChanged( String oldDescription, String newDescription ){
			for( PreferenceOperationViewListener listener : listeners ){
				listener.descriptionChanged( this, oldDescription, newDescription );
			}
		}
		
		public void addListener( PreferenceOperationViewListener listener ){
			listeners.add( listener );
		}

		
		public void removeListener( PreferenceOperationViewListener listener ){
			listeners.remove( listener );
		}
    }
}
