package bibliothek.gui.dock.station.toolbar.menu;

import java.awt.Component;
import java.util.ArrayList;
import java.util.List;

import javax.swing.Box;
import javax.swing.JComponent;

import bibliothek.gui.DockController;

/**
 * A {@link CustomizationMenuContent} using a vertical {@link Box} to show a set
 * of other {@link CustomizationMenuContent}s.
 * 
 * @author "Herve Guillaume"
 * 
 */
public class CustomizationMenuContentVerticalBox implements CustomizationMenuContent{
	/** all the children of this box */
	private final List<CustomizationMenuContent> content = new ArrayList<CustomizationMenuContent>();

	/** the currently used view */
	private Box view;

	/** the controller in whose realm this grid is used */
	private DockController controller;

	@Override
	public Component getView(){
		return view;
	}

	@Override
	public void setController( DockController controller ){
		this.controller = controller;
		for (final CustomizationMenuContent item : content){
			item.setController(controller);
		}
	}

	@Override
	public void bind( CustomizationMenuCallback callback ){
		view = Box.createVerticalBox();
		view.setOpaque(true);
		for (final CustomizationMenuContent item : content){
			item.bind(callback);
			if (item.getView() instanceof JComponent){
				((JComponent) item.getView())
						.setAlignmentX(Component.LEFT_ALIGNMENT);
			}
			view.add(item.getView());
		}
	}

	@Override
	public void unbind(){
		view.removeAll();
		view = null;

		for (final CustomizationMenuContent item : content){
			item.unbind();
		}
	}

	/**
	 * Adds <code>item</code> to this box. It is the clients responsibility to
	 * ensure that <code>item</code> is not already used by another object. If
	 * the menu is currently visible, then calling this method has no immediate
	 * effect.
	 * 
	 * @param item
	 *            the item to add, not <code>null</code>
	 */
	public void add( CustomizationMenuContent item ){
		content.add(item);
		item.setController(controller);
	}

	/**
	 * Adds <code>item</code> to this box. It is the clients responsibility to
	 * ensure that <code>item</code> is not already used by another object. If
	 * the menu is currently visible, then calling this method has no immediate
	 * effect.
	 * 
	 * @param index
	 *            the location where to insert <code>item</code>
	 * @param item
	 *            the item to add, not <code>null</code>
	 */
	public void add( int index, CustomizationMenuContent item ){
		content.add(index, item);
		item.setController(controller);
	}

	/**
	 * Removes the <code>index</code>'th item from this box. If the menu is
	 * currently visible, then calling this method has no immediate effect.
	 * 
	 * @param index
	 *            the index of the item to remove
	 */
	public void remove( int index ){
		final CustomizationMenuContent item = content.remove(index);
		item.setController(null);
	}

	/**
	 * Removes <code>item</code> from this box. If the menu is currently
	 * visible, then calling this method has no immediate effect.
	 * 
	 * @param item
	 *            the item to remove
	 */
	public void remove( CustomizationMenuContent item ){
		if (content.remove(item)){
			item.setController(null);
		}
	}

	/**
	 * Gets the number of items on this grid.
	 * 
	 * @return the number of items
	 */
	public int getItemCount(){
		return content.size();
	}

	/**
	 * Gets the <code>index</code>'th item of this grid.
	 * 
	 * @param index
	 *            the index of the item
	 * @return the item, not <code>null</code>
	 */
	public CustomizationMenuContent getItem( int index ){
		return content.get(index);
	}

}
