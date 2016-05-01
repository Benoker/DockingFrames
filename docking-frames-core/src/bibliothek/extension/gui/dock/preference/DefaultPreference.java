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

import bibliothek.gui.dock.util.TextManager;
import bibliothek.util.Path;

/**
 * A generic {@link Preference} that can hold any value. This preference does not implement 
 * the {@link #read()} or {@link #write()} method. If a {@link #setDefaultValue(Object) default-value}
 * is set, then this preference activates the operation {@link PreferenceOperation#DEFAULT}, otherwise
 * it shows no operations.<br>
 * This preference is set to be an {@link #isNatural() artificial} preference, subclasses may call
 * {@link #setNatural(boolean)} to change the behavior.
 * @author Benjamin Sigg
 *
 * @param <V> the kind of value this preference holds
 */
public abstract class DefaultPreference<V> extends AbstractPreference<V>{
    private V value;
    private Object valueInfo;
    private Path type;
    
    private String label;
    private PreferenceText labelText;
    
    private String description;
    private PreferenceText descriptionText;

    private V defaultValue;
    private Path path;
    
    private boolean natural = false;
    
    private PreferenceModel model;
    
    /**
     * Creates a new preference.
     * @param type the type of value this preference uses
     * @param path a unique path for this preference, all paths starting with
     * "dock" are reserved for this framework
     */
    public DefaultPreference( Path type, Path path ){
        if( type == null )
            throw new IllegalArgumentException( "type must not be null" );
        
        if( path == null )
            throw new IllegalArgumentException( "path must not be null" );
        
        if( path.getSegmentCount() == 0 )
            throw new IllegalArgumentException( "the root path is not a valid path for a preference" );          
        
        this.type = type;
        this.path = path;
    }
    
    /**
     * Creates a new preference.
     * @param label a short human readable label for this preference
     * @param type the type of value this preference uses
     * @param path a unique path for this preference, all paths starting with
     * "dock" are reserved for this framework
     */
    public DefaultPreference( String label, Path type, Path path ){
        this( type, path );
        setLabel( label );
    }
    
    @Override
    public void addPreferenceListener( PreferenceListener<V> listener ){
    	if( !hasListeners() ){
    		if( model != null ){
    			if( labelText != null ){
        			labelText.setController( model.getController() );
        		}
        		if( descriptionText != null ){
        			descriptionText.setController( model.getController() );
        		}	
    		}
    	}
    	super.addPreferenceListener( listener );
    }
    
    @Override
    public void removePreferenceListener( PreferenceListener<V> listener ){
    	super.removePreferenceListener( listener );
    	if( !hasListeners() ){
    		if( labelText != null ){
    			labelText.setController( null );
    		}
    		if( descriptionText != null ){
    			descriptionText.setController( null );
    		}
    	}
    }
    
    public void setModel( PreferenceModel model ){
    	this.model = model;
    	if( hasListeners() ){
	    	if( labelText != null ){
	    		labelText.setController( model == null ? null : model.getController() );
	    	}
	    	if( descriptionText != null ){
	    		descriptionText.setController( model == null ? null : model.getController() );
	    	}
    	}
    }
    
    public String getLabel() {
    	if( labelText == null ){
    		return label;
    	}
    	
    	if( !hasListeners() && model != null ){
    		labelText.update( model.getController().getTexts() );
    	}
    	
    	return labelText.value();
    }
    
    /**
     * Sets a short human readable label for this preference. Note that
     * changes of the label are not propagated to any listener.
     * @param label the new label
     */
    public void setLabel( String label ) {
        this.label = label;
        if( labelText != null ){
        	labelText.setValue( label );
        }
        else{
        	fireChanged();
        }
    }
    
    /**
     * Sets a unique identifier for the label text, the unique identifier will be used to read
     * a text from the current {@link TextManager}.
     * @param labelId the unique identifier, can be <code>null</code>
     */
    public void setLabelId( String labelId ){
    	if( labelId == null ){
    		if( labelText != null ){
    			labelText.setController( null );
    			labelText = null;
    		}
    	}
    	else{
    		if( labelText == null ){
    			labelText = new PreferenceText( labelId, this ){
					protected void changed( String oldValue, String newValue ){
						fireChanged();
					}
				};
				if( hasListeners() && model != null ){
					labelText.setController( model.getController() );
				}
    		}
    		else{
    			labelText.setId( labelId );
    		}
    	}
    }
    
    public String getDescription() {
    	if( descriptionText == null ){
    		return description;
    	}
    	
    	if( !hasListeners() && model != null ){
    		descriptionText.update( model.getController().getTexts() );
    	}
    	
    	return descriptionText.value();
    }
    
    /**
     * Sets a description of this preference. 
     * @param description a human readable string, can be <code>null</code> and
     * can be formated in HTML
     */
    public void setDescription( String description ) {
        this.description = description;
        if( descriptionText != null ){
        	descriptionText.setValue( description );
        }
        else{
        	fireChanged();
        }
    }
    
    /**
     * Sets a unique identifier for the description text, the unique identifier will be used to read
     * a text from the current {@link TextManager}.
     * @param descriptionId the unique identifier, can be <code>null</code>
     */
    public void setDescriptionId( String descriptionId ){
    	if( descriptionId == null ){
    		if( descriptionText != null ){
    			descriptionText.setController( null );
    			descriptionText = null;
    		}
    	}
    	else{
    		if( descriptionText == null ){
    			descriptionText = new PreferenceText( descriptionId, this ){
					protected void changed( String oldValue, String newValue ){
						fireChanged();
					}
				};
				if( hasListeners() && model != null ){
					descriptionText.setController( model.getController() );
				}
    		}
    		else{
    			descriptionText.setId( descriptionId );
    		}
    	}
    }
    
    public Path getTypePath() {
        return type;
    }
    
    /**
     * Sets information about this preferences value. For example for
     * an integer that could be the upper and the lower bounds. The exact
     * meaning and type of this object depends on {@link #getTypePath()}.
     * @param valueInfo the new information or <code>null</code>
     */
    public void setValueInfo(Object valueInfo) {
		this.valueInfo = valueInfo;
	}
    
    public Object getValueInfo() {
    	return valueInfo;
    }
    
    public V getValue() {
        return value;
    }
    
    public void setValue( V value ) {
        if( this.value != value ){
            this.value = value;
            fireChanged();
        }
    }

    /**
     * Sets the default value of this preference
     * @param defaultValue the new default value
     */
    public void setDefaultValue( V defaultValue ) {
        this.defaultValue = defaultValue;
        fireChanged();
    }
    
    /**
     * Gets the default value of this preference
     * @return the default value, might be <code>null</code>
     */
    public V getDefaultValue() {
        return defaultValue;
    }

    public Path getPath() {
        return path;
    }
    
    /**
     * Sets whether this preference is natural or artificial.
     * @param natural <code>true</code> if natural, <code>false</code>
     * if artificial
     * @see #isNatural()
     * @see PreferenceModel#isNatural(int)
     */
    public void setNatural( boolean natural ) {
        this.natural = natural;
    }
    
    public boolean isNatural() {
        return natural;
    }
    
    @Override
    public PreferenceOperation[] getOperations() {
        if( defaultValue == null )
            return null;
        else
            return new PreferenceOperation[]{ PreferenceOperation.DEFAULT };
    }
    
    @Override
    public boolean isEnabled( PreferenceOperation operation ) {
        if( operation == PreferenceOperation.DEFAULT ){
            if( defaultValue == null )
                return false;
            
            return !defaultValue.equals( getValue() );
        }
        return false;
    }
    
    @Override
    public void doOperation( PreferenceOperation operation ) {
        if( operation == PreferenceOperation.DEFAULT ){
            if( defaultValue != null ){
                setValue( defaultValue );
            }
        }
        else{
            super.doOperation( operation );
        }
    }
}
