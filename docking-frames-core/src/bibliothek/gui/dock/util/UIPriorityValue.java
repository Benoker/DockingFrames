package bibliothek.gui.dock.util;

import bibliothek.util.FrameworkOnly;

/**
 * A {@link PriorityValue} that supports working with {@link UIScheme}s.
 * @author Benjamin Sigg
 * @param <T> the kind of item this {@link PriorityValue} stores
 */
@FrameworkOnly
public class UIPriorityValue<T> extends PriorityValue<UIPriorityValue.Value<T>> {
	/**
	 * Sets the value of <code>this</code> using a value that may be
	 * derived from <code>scheme</code>.
	 * @param priority the priority of the value to set
	 * @param value the value that is to be set, can be <code>null</code>
	 * @param scheme the scheme that created <code>value</code> or <code>null</code>, ignored
	 * if <code>value</code> is <code>null</code>
	 * @return <code>true</code> if the value of this scheme changed because of
	 * the call to this method
	 */
	public boolean set( Priority priority, T value, UIScheme<?, ?, ?> scheme ){
		T old = getValue();
		
		if( value == null ){
			set( priority, null );
		}
		else if( scheme == null ){
			set( priority, new DirectValue<T>( value ));	
		}
		else{
			set( priority, new SchemeValue<T>( value, scheme ));
		}
		
		return old != getValue();
	}
	
	/**
	 * Gets the scheme that created the entry with <code>priority</code>.
	 * @param priority the priority of the entry that is queried
	 * @return the scheme that was used to create the value or <code>null</code>
	 */
	public UIScheme<?,?,?> getScheme( Priority priority ){
		Value<T> value = get( priority );
		if( value instanceof SchemeValue<?> ){
			return ((SchemeValue<?>)value).scheme;
		}
		return null;
	}
	
	/**
	 * Tells whether all entries of this value are either <code>null</code> or 
	 * are created by an {@link UIScheme}.
	 * @return <code>true</code> if all entries of this value are <code>null</code> or created
	 */
	public boolean isAllScheme(){
		for( Priority priority : Priority.values() ){
			if( getScheme( priority ) == null && get( priority ) != null ){
				return false;
			}
		}
		return true;
	}
	
	/**
	 * Gets the current value of this {@link UIPriorityValue}.
	 * @return the current value or <code>null</code>
	 */
	public T getValue(){
		Value<T> value = get();
		if( value == null ){
			return null;
		}
		return value.getValue();
	}
	
	/**
	 * Gets the current value of {@link UIPriorityValue} on level <code>priority</code>.
	 * @param priority the level to search, not <code>null</code>
	 * @return the value or <code>null</code>
	 */
	public T getValue( Priority priority ){
		Value<T> value = get( priority );
		if( value == null ){
			return null;
		}
		return value.getValue();
	}
	
    /**
     * Represents a single entry in this map.
     * @author Benjamin Sigg
     *
     * @param <T> the kind of value this entry represents
     */
    public interface Value<T>{
    	public T getValue();
    }
    
    /**
     * Represents an entry which was set directly by a client.
     * @author Benjamin Sigg
     * @param <T> the kind of value this entry represents
     */
    private static class DirectValue<T> implements Value<T>{
    	private T value;
    	
    	/**
    	 * Creates a new entry.
    	 * @param value the value of this entry, can be <code>null</code>
    	 */
    	public DirectValue( T value ){
    		this.value = value;
    	}
    	
    	public T getValue(){
	    	return value;
    	}
    }
    
    /**
     * Represents an entry which was created by reading from an {@link UIScheme}.
     * @author Benjamin Sigg
     * @param <T> the kind of value this entry represents
     */
    private static class SchemeValue<T> implements Value<T>{
    	private T value;
    	private UIScheme<?, ?, ?> scheme;
    	
    	/**
    	 * Creates a new entry.
    	 * @param value the value of this entry, can be <code>null</code>
    	 * @param scheme the scheme that was used to create this entry, must not be <code>null</code>
    	 */
    	public SchemeValue( T value, UIScheme<?, ?, ?> scheme ){
    		this.value = value;
    		this.scheme = scheme;
    	}
    	
    	public T getValue(){
	    	return value;
    	}
    }
}
