package bibliothek.gui.dock.station.split;

import bibliothek.gui.dock.station.SplitDockStation;

/**
 * An exception thrown if a {@link SplitDockStation} can't combine two
 * children which must be combined.
 * @author Benjamin Sigg
 */
public class SplitDropTreeException extends RuntimeException {
    /** the source of the exception */
    private SplitDockStation station;
    
    /**
     * Creates a new exception.
     * @param station the source of the exception
     * @param message the cause
     */
    public SplitDropTreeException( SplitDockStation station, String message ){
        super( message );
        this.station = station;
    }
    
    /**
     * Gets the station which is the source of this exception.
     * @return the source
     */
    public SplitDockStation getStation() {
        return station;
    }
}
