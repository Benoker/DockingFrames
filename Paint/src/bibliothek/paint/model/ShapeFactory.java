package bibliothek.paint.model;


public interface ShapeFactory<S extends Shape> {
    public S create();
    public String getName();
}
