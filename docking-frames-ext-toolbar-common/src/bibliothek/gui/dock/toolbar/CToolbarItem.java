package bibliothek.gui.dock.toolbar;

import bibliothek.gui.dock.common.CStation;
import bibliothek.gui.dock.common.SingleCDockable;
import bibliothek.gui.dock.common.intern.AbstractCDockable;
import bibliothek.gui.dock.common.intern.CommonDockable;
import bibliothek.gui.dock.toolbar.intern.CommonComponentDockable;

/**
 * A {@link CToolbarItem} is an item (e.g. a button) that is shown in a toolbar.
 * @author Benjamin Sigg
 */
public class CToolbarItem extends AbstractCDockable implements SingleCDockable{
	private String id;
	
	/**
	 * Creates a new item.
	 * @param id the unique identifier of this item, not <code>null</code>
	 */
	public CToolbarItem( String id ){
		if( id == null ){
			throw new IllegalArgumentException( "id must not be null" );
		}
		this.id = id;
	}
	
	@Override
	public String getUniqueId(){
		return id;
	}
	
	@Override
	public boolean isMinimizable(){
		return false;
	}

	@Override
	public boolean isMaximizable(){
		return false;
	}

	@Override
	public boolean isExternalizable(){
		return false;
	}

	@Override
	public boolean isStackable(){
		return false;
	}

	@Override
	public boolean isCloseable(){
		return false;
	}

	@Override
	public CStation<?> asStation(){
		return null;
	}
	
	@Override
	public CommonComponentDockable intern(){
		return (CommonComponentDockable)super.intern();
	}

	@Override
	protected CommonComponentDockable createCommonDockable(){
		return new CommonComponentDockable( this );
	}
}
