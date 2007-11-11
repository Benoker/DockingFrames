package bibliothek.gui.dock.title;

import java.awt.Component;

import bibliothek.gui.Dockable;
import bibliothek.gui.dock.dockable.MovingImage;

/**
 * A moving image that uses a {@link DockTitle} to paint its content.
 * @author Benjamin Sigg
 *
 */
public class TitleMovingImage implements MovingImage {
    
    /** the element which is represented by this image */
    private Dockable dockable;
    /** the contents of this image */
    private DockTitle title;
    
    /**
     * Creates a new image.
     * @param dockable the element which is represented by this image
     * @param title the contents of this image
     */
    public TitleMovingImage( Dockable dockable, DockTitle title ){
        if( dockable == null )
            throw new IllegalArgumentException( "Dockable must not be null" );
        
        if( title == null )
            throw new IllegalArgumentException( "Title must not be null" );
        
        this.dockable = dockable;
        this.title = title;
    }
    
    public void bind() {
        dockable.bind( title );
    }

    public Component getComponent() {
        return title.getComponent();
    }

    public void unbind() {
        dockable.unbind( title );
    }
}
