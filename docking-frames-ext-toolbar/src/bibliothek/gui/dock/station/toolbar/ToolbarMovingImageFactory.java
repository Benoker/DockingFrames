package bibliothek.gui.dock.station.toolbar;

import bibliothek.gui.DockController;
import bibliothek.gui.Dockable;
import bibliothek.gui.dock.dockable.DockableMovingImageFactory;
import bibliothek.gui.dock.dockable.MovingImage;
import bibliothek.gui.dock.title.DockTitle;

/**
 * A {@link ToolbarMovingImageFactory} is a filter that forwards any {@link Dockable} which is a 
 * {@link ToolbarStrategy#isToolbarPart(Dockable) toolbar part} to some other factory.
 * @author Benjamin Sigg
 */
public class ToolbarMovingImageFactory implements DockableMovingImageFactory{
	private DockableMovingImageFactory delegate;
	
	/**
	 * Creates a new factory
	 * @param delegate the factory that will actually create {@link MovingImage}s, if <code>this</code> does include
	 * a {@link Dockable}
	 */
	public ToolbarMovingImageFactory( DockableMovingImageFactory delegate ){
		this.delegate = delegate;
	}
	
	@Override
	public MovingImage create( DockController controller, DockTitle snatched ){
		ToolbarStrategy strategy = controller.getProperties().get( ToolbarStrategy.STRATEGY );
		if( strategy.isToolbarPart( snatched.getDockable() )){
			return delegate.create( controller, snatched );
		}
		return null;
	}

	@Override
	public MovingImage create( DockController controller, Dockable dockable ){
		ToolbarStrategy strategy = controller.getProperties().get( ToolbarStrategy.STRATEGY );
		if( strategy.isToolbarPart( dockable )){
			return delegate.create( controller, dockable );
		}
		return null;
	}

}
