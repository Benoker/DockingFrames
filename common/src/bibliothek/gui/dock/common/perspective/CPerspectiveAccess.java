package bibliothek.gui.dock.common.perspective;

import bibliothek.gui.dock.perspective.PerspectiveElement;

public interface CPerspectiveAccess{
	/**
	 * Gets the owner of this access object.
	 * @return the owner
	 */
	public CPerspective getOwner();
	
	/**
	 * Searches the unique identifier that matches <code>element</code>.
	 * @param element the element whose identifier is searched
	 * @return the identifier, may be <code>null</code>
	 */
	public String getUniqueId( PerspectiveElement element );

	/**
	 * Stores the element <code>perspective</code> using the identifier <code>id</code>.
	 * @param id the unique identifier of <code>perspective</code>
	 * @param perspective the new perspective
	 */
	public void putDockable( String id, MultipleCDockablePerspective<?> perspective );

}
