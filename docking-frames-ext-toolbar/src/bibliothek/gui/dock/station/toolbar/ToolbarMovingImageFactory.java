package bibliothek.gui.dock.station.toolbar;

import java.awt.Component;
import java.awt.Point;

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
		MovingImage image = null;
		if( strategy.isToolbarPart( snatched.getDockable() )){
			image = delegate.create( controller, snatched );
		}
		if( image == null ){
			return null;
		}
		return new OffsetWrapper( image );
	}

	@Override
	public MovingImage create( DockController controller, Dockable dockable ){
		ToolbarStrategy strategy = controller.getProperties().get( ToolbarStrategy.STRATEGY );
		MovingImage image = null;
		if( strategy.isToolbarPart( dockable )){
			image = delegate.create( controller, dockable );
		}
		if( image == null ){
			return null;
		}
		return new OffsetWrapper( image );
	}

	/**
	 * Wrapps around a {@link MovingImage} and changes the offset of the image.
	 * @author Benjamin Sigg
	 */
	private class OffsetWrapper implements MovingImage{
		private MovingImage image;
		
		public OffsetWrapper( MovingImage image ){
			this.image = image;
		}
		
		@Override
		public Point getOffset( Point pressPoint ){
			return new Point( -pressPoint.x, -pressPoint.y );
		}
		
		@Override
		public void bind( boolean transparency ){
			image.bind( transparency );
		}
		
		@Override
		public void unbind(){
			image.unbind();
		}
		
		@Override
		public Component getComponent(){
			return image.getComponent();
		}
	}
}
