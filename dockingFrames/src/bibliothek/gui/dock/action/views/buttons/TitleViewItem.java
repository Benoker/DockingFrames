package bibliothek.gui.dock.action.views.buttons;

import bibliothek.gui.dock.action.views.ViewItem;
import bibliothek.gui.dock.title.DockTitle;

/**
 * A view item that will be shown between some buttons on a DockTitle.
 * @author Benjamin Sigg
 *
 * @param <A> the type of item wrapped in this object
 */
public interface TitleViewItem<A> extends ViewItem<A> {
	/**
	 * Informs the item about the orientation of the title that uses this 
	 * item.
	 * @param orientation the orientation
	 */
	public void setOrientation( DockTitle.Orientation orientation );
}
