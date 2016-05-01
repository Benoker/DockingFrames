package bibliothek.help.control;

import java.util.List;

import bibliothek.help.model.Entry;

/**
 * A listener to the {@link LinkManager}. This listener will be notified
 * when the user clicks onto a link and new pages have to be selected. 
 * @author Benjamin Sigg
 *
 */
public interface Linking {
    /**
     * Called when a new set of pages has to be shown. The <code>list</code>
     * contains several entries that are associated with the current selection.
     * The list is sorted by the importance of the entries, where more 
     * important entries are to be found at the beginning.
     * @param list the list of newly selected pages
     */
    public void selected( List<Entry> list );
}
