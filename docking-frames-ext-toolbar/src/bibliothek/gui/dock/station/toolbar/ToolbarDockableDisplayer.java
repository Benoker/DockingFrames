package bibliothek.gui.dock.station.toolbar;

import bibliothek.gui.DockStation;
import bibliothek.gui.Dockable;
import bibliothek.gui.Orientation;
import bibliothek.gui.dock.displayer.DisplayerRequest;
import bibliothek.gui.dock.station.DisplayerFactory;
import bibliothek.gui.dock.station.DockableDisplayer;
import bibliothek.gui.dock.station.OrientationObserver;
import bibliothek.gui.dock.station.OrientedDockStation;
import bibliothek.gui.dock.themes.basic.BasicDockableDisplayer;
import bibliothek.gui.dock.title.DockTitle;
import bibliothek.gui.dock.util.Transparency;

/**
 * A simple implementation of a {@link DockableDisplayer} that can be used by
 * toolbar-{@link DockStation}s. This displayer is aware of the fact, that some
 * {@link DockStation}s have an orientation and may update its own orientation
 * automatically.
 * 
 * @author Benjamin Sigg
 */
public class ToolbarDockableDisplayer extends BasicDockableDisplayer {
	/**
	 * A factory creating new {@link ToolbarDockableDisplayer}s.
	 */
	public static final DisplayerFactory FACTORY = new DisplayerFactory(){
		@Override
		public void request( DisplayerRequest request ){
			ToolbarDockableDisplayer displayer = new ToolbarDockableDisplayer( request.getParent(), request.getTarget(), request.getTitle() );
			displayer.setDefaultBorderHint( false );
			displayer.setRespectBorderHint( true );
			request.answer( displayer );
		}
	};

	/** Keeps track of the orientation of the current {@link Dockable} and updates the location of the title if necessary */
	private OrientationObserver observer;
	
	/**
	 * Creates a new displayer.
	 * 
	 * @param station
	 *            the owner of this displayer
	 * @param dockable
	 *            the element shown on this displayer, can be <code>null</code>
	 * @param title
	 *            the title shown on this displayer, can be <code>null</code>
	 */
	public ToolbarDockableDisplayer( DockStation station, Dockable dockable, DockTitle title ){
		super( station );
		setTransparency( Transparency.TRANSPARENT );
		setDockable( dockable );
		setTitle( title );
	}
	
	@Override
	public void setDockable( Dockable dockable ){
		Dockable oldDockable = getDockable();
		if( oldDockable != dockable ){
			if( observer != null ){
				observer.destroy();
				observer = null;
			}
			super.setDockable( dockable );
			if( dockable != null ){
				observer = new OrientationObserver( dockable ){
					@Override
					protected void orientationChanged( Orientation current ){
						setOrientation( current );
					}
				};
				setOrientation( getOrientation() );
			}
		}
	}
	
	/**
	 * Tries to find out the current {@link Orientation} of the {@link Dockable}.
	 * @return the current orientation, may be <code>null</code>
	 */
	protected Orientation getOrientation(){
		if( observer != null ){
			Orientation result = observer.getOrientation();
			if( result != null ){
				return result;
			}
		}
		
		Dockable dockable = getDockable();
		if( dockable == null ){
			return null;
		}
		
		if( dockable instanceof OrientedDockStation ){
			return ((OrientedDockStation)dockable).getOrientation();
		}
		
		return null;
	}
	
	/**
	 * Called if the orientation of the current {@link Dockable} changed. 
	 * @param orientation the new orientation, can be <code>null</code>
	 */
	protected void setOrientation( Orientation orientation ){
		if( orientation != null ){
			if( orientation == Orientation.HORIZONTAL ){
				setTitleLocation( Location.LEFT );
			}
			else{
				setTitleLocation( Location.TOP );
			}
		}
	}
}
