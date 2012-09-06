package bibliothek.gui.dock.extension.css.property;

import bibliothek.gui.dock.extension.css.CssProperty;
import bibliothek.gui.dock.extension.css.CssPropertyContainer;
import bibliothek.gui.dock.extension.css.CssPropertyContainerListener;

/**
 * A {@link CssProperty} which offers a value that itself is a {@link CssPropertyContainer}, this {@link CssProperty}
 * offers the properties of the value to the outside world. 
 * 
 * @author Benjamin Sigg
 *
 * @param <T> the kind of value presented by this property
 */
public abstract class AbstractContainerCssProperty<T extends CssPropertyContainer> extends AbstractCssPropertyContainer implements CssProperty<T>{
	private T property;
	private CssPropertyContainerListener propertyListener = new CssPropertyContainerListener(){
		@Override
		public void propertyRemoved( CssPropertyContainer source, String key, CssProperty<?> property ){
			firePropertyRemoved( key, property );
		}
		
		@Override
		public void propertyAdded( CssPropertyContainer source, String key, CssProperty<?> property ){
			firePropertyAdded( key, property );
		}
	};
	
	@Override
	public String[] getPropertyKeys(){
		if( property == null ){
			return new String[]{};
		}
		else{
			return property.getPropertyKeys();
		}
	}

	@Override
	public CssProperty<?> getProperty( String key ){
		if( property == null ){
			return null;
		}
		else{
			return property.getProperty( key );
		}
	}

	@Override
	public final void set( T value ){
		if( this.property != value ){
			if( isBound() && this.property != null ){
				for( String key : getPropertyKeys() ){
					firePropertyRemoved( key, getProperty( key ) );
				}
			}
			this.property = value;
			if( isBound() && this.property != null ){
				for( String key : getPropertyKeys() ){
					firePropertyAdded( key, getProperty( key ) );
				}
			}
			propertyChanged( this.property );
		}
	}
	
	/**
	 * Called if the value of this property changed.
	 * @param value the new value, can be <code>null</code>
	 */
	protected abstract void propertyChanged( T value );

	@Override
	protected void bind(){
		if( property != null ){
			property.addPropertyContainerListener( propertyListener );
		}
	}

	@Override
	protected void unbind(){
		if( property != null ){
			property.removePropertyContainerListener( propertyListener );
		}
	}
}
