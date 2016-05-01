package bibliothek.help.control;

/**
 * A listener to the {@link URManager}.
 * @author Benjamin Sigg
 *
 */
public interface URListener {
    /**
     * Called whenever the number of undo/redo-steps, or the selected 
     * undo/redo-step changes.
     * @param manager the source of the event
     */
    public void changed( URManager manager );
}
