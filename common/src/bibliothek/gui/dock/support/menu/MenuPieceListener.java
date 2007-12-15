package bibliothek.gui.dock.support.menu;

import java.awt.Component;

public interface MenuPieceListener {
	/**
     * Adds an item to the menu.
     * @param child a {@link MenuPiece} whose parent is <code>this</code> and which
     * want's to add the item
     * @param index the index of the item, measured relatively to all items of <code>owner</code>
     * @param items the new items
     */
    public abstract void insert( MenuPiece child, int index, Component... items );
    
    /**
     * Removes an item from the menu.
     * @param child a {@link MenuPiece} whose parent is <code>this</code> and which
     * want's to remove the item
     * @param index the index of the item, measured relatively to all items of <code>owner</code>
     * @param length the number of items to remove
     */
    public abstract void remove( MenuPiece child, int index, int length );
}
