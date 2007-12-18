package bibliothek.paint.model;

/**
 * A factory used to create new {@link Shape}s.
 * @author Benjamin Sigg
 */
public interface ShapeFactory {
    /**
     * Creates a new shape.
     * @return the new shape
     */
    public Shape create();
    
    /**
     * Gets the name of this factory. The name should be a short,
     * human readable description of this factory.
     * @return the name
     */
    public String getName();
}
