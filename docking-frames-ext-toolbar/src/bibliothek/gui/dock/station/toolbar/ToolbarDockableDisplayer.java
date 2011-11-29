package bibliothek.gui.dock.station.toolbar;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;

import javax.swing.border.Border;
import javax.swing.border.LineBorder;

import bibliothek.gui.DockStation;
import bibliothek.gui.Dockable;
import bibliothek.gui.Orientation;
import bibliothek.gui.dock.displayer.DisplayerRequest;
import bibliothek.gui.dock.station.DisplayerFactory;
import bibliothek.gui.dock.station.DockableDisplayer;
import bibliothek.gui.dock.station.OrientedDockStation;
import bibliothek.gui.dock.station.OrientingDockStation;
import bibliothek.gui.dock.station.OrientingDockStationEvent;
import bibliothek.gui.dock.station.OrientingDockStationListener;
import bibliothek.gui.dock.themes.basic.BasicDockableDisplayer;
import bibliothek.gui.dock.title.DockTitle;

/**
 * A simple implementation of a {@link DockableDisplayer} that can be used by toolbar-{@link DockStation}s. This displayer
 * is aware of the fact, that some {@link DockStation}s have an orientation and may update its own orientation automatically. 
 * @author Benjamin Sigg
 */
public class ToolbarDockableDisplayer extends BasicDockableDisplayer{
	/**
	 * Creates a new {@link DisplayerFactory} for creating new {@link ToolbarDockableDisplayer}s with a {@link LineBorder}
	 * using the color <code>color</code>.
	 * @param color the color of the {@link LineBorder}
	 * @param autoOrientation if enabled, then the {@link Orientation} of any {@link OrientedDockStation} is set automatically
	 * depending on the current shape of the {@link ToolbarDockableDisplayer}
	 * @return the new factory
	 */
	public static final DisplayerFactory createColorBorderFactory( final Color color, final boolean autoOrientation ){
		return new DisplayerFactory(){
			@Override
			public void request( DisplayerRequest request ){
				ToolbarDockableDisplayer displayer = new ToolbarDockableDisplayer( request.getParent(), request.getTarget(), request.getTitle(), autoOrientation );
				//displayer.setDefaultBorder( BorderFactory.createLineBorder( color, 2 ) );
				displayer.setDefaultBorderHint( true );
				displayer.setRespectBorderHint( false );
				request.answer( displayer );
			}
		};
	}
	
	/**
	 * A listener added to the {@link #getStation() station} of this displayer.
	 */
	private OrientingDockStationListener listener = new OrientingDockStationListener(){
		@Override
		public void changed( OrientingDockStationEvent event ){
			Dockable dockable = getDockable();
			if( dockable != null && event.isAffected( dockable )){
				updateOrientation();
			}
		}
	};
	
	private Border defaultBorder;
	
	/**
	 * Creates a new displayer.
	 * @param station the owner of this displayer
	 * @param dockable the element shown on this displayer, can be <code>null</code>
	 * @param title the title shown on this displayer, can be <code>null</code>
	 * @param autoOrientation if <code>autoOrientation</code> is enabled and <code>dockable</code> is an {@link OrientedDockStation}, then
	 * this displayer automatically calls {@link OrientedDockStation#setOrientation(Orientation)} depending on the current boundaries
	 */
	public ToolbarDockableDisplayer( DockStation station, Dockable dockable, DockTitle title, boolean autoOrientation ){
		super( station, dockable, title );
		this.setTransparent(true);
		if( autoOrientation && dockable.asDockStation() instanceof OrientedDockStation ){
			getComponent().addComponentListener( new ComponentAdapter(){
				public void componentResized( ComponentEvent e ){
					Dimension size = getComponent().getSize();
					if( size.width > size.height ){
						((OrientedDockStation)getDockable().asDockStation()).setOrientation( Orientation.HORIZONTAL );
					}
					else{
						((OrientedDockStation)getDockable().asDockStation()).setOrientation( Orientation.VERTICAL );
					}
					updateOrientation();
				}
			});
		}
	}
	
	public void setDefaultBorder( Border defaultBorder ){
		this.defaultBorder = defaultBorder;
	}
	
	@Override
	protected Border getDefaultBorder(){
		return defaultBorder;
	}

	@Override
	public void setStation( DockStation station ){
		DockStation old = getStation();
		if( old != null && old instanceof OrientingDockStation ){
			((OrientingDockStation)old).removeOrientingDockStationListener( listener );
		}
		
		super.setStation( station );
		
		if( station != null && station instanceof OrientingDockStation ){
			((OrientingDockStation)station).addOrientingDockStationListener( listener );
		}
		
		updateOrientation();
	}
	
	@Override
	public void setDockable( Dockable dockable ){
		super.setDockable( dockable );
		updateOrientation();
	}
	
	protected void updateOrientation(){
		DockStation station = getStation();
		Dockable dockable = getDockable();
		
		Orientation orientation = null;
		
		if( station instanceof OrientingDockStation && dockable != null ){
			orientation = ((OrientingDockStation)station).getOrientationOf( dockable );
		}
		else if( dockable != null && dockable.asDockStation() instanceof OrientedDockStation ){
			orientation = ((OrientedDockStation)dockable.asDockStation()).getOrientation();
		}
		
		if( orientation != null ){
			switch( orientation ){
				case HORIZONTAL:
					setTitleLocation( Location.LEFT );
					break;
				case VERTICAL:
					setTitleLocation( Location.TOP );
					break;
				default:
					throw new IllegalStateException( "unknown orientation: " + orientation );
			}
		}
		
	}
}
