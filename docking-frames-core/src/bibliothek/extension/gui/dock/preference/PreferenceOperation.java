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

import bibliothek.gui.dock.util.IconManager;
import bibliothek.gui.dock.util.TextManager;

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
    public static final PreferenceOperation DELETE = new PreferenceOperation( "delete" );
    
    /**
     * Operation for setting a property to its default value
     */
    public static final PreferenceOperation DEFAULT = new PreferenceOperation( "default" );
    
    static{
    	DELETE.setIconId( "delete.small" );
    	DELETE.setDescriptionId( "preference.operation.delete" );
    	
    	DEFAULT.setIconId( "default.small" );
    	DEFAULT.setDescriptionId( "preference.operation.default" );
    }
    
    private String key;
    
    private Icon icon;
    private String iconId = "null";
    
    private String description;
    private String descriptionId = "null";
    
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
		if (obj == this) {
			return true;
		}

		if (obj == null) {
			return false;
		}

        if( obj.getClass() == this.getClass() ){
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
     * Sets the unique identifier of the description. The identifier is used to read a
     * string from the {@link TextManager}.
     * @param descriptionId the identifier, not <code>null</code>
     */
    public void setDescriptionId( String descriptionId ){
    	if( descriptionId == null ){
    		throw new IllegalArgumentException( "descriptionId must not be null" );
    	}
    	this.descriptionId = descriptionId;
    	for( View view : views ){
    		view.description.setId( descriptionId );
    	}
    }
    
    /**
     * Sets a human readable description of this operation.
     * @param description the description
     */
    public void setDescription( String description ) {
        this.description = description;
        for( View view : views ){
        	view.description.setValue( description );
        }
    }
    
    /**
     * The view of a {@link PreferenceOperation}
     * @author Benjamin Sigg
     */
    private class View implements PreferenceOperationView{
    	private List<PreferenceOperationViewListener> listeners = new ArrayList<PreferenceOperationViewListener>();
    
    	private PreferenceOperationIcon icon;
    	
    	private PreferenceOperationText description;
    	
    	public View( PreferenceModel model ){
    		views.add( this );
    		
    		icon = new PreferenceOperationIcon( iconId, getOperation() ){
    			@Override
    			protected void changed( Icon oldValue, Icon newValue ){
    				fireIconChanged( oldValue, newValue );
    			}
    		};
    		icon.setValue( PreferenceOperation.this.icon );
    		icon.setManager( model.getController().getIcons() );
    		
    		description = new PreferenceOperationText( descriptionId, getOperation() ){
				protected void changed( String oldValue, String newValue ){
					fireDescriptionChanged( oldValue, newValue );
				}
			};
			description.setValue( PreferenceOperation.this.description );
			description.setController( model.getController() );
    	}

		public void destroy(){
			views.remove( this );
			icon.setManager( null );
			description.setController( null );
		}

		public String getDescription(){
			return description.value();
		}

		public Icon getIcon(){
			return icon.value();
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
