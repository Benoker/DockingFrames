package bibliothek.gui.dock.station.toolbar;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Point;

import javax.swing.JComponent;
import javax.swing.event.MouseInputListener;

import bibliothek.gui.Dockable;
import bibliothek.gui.dock.DockElement;
import bibliothek.gui.dock.title.ActivityDockTitleEvent;
import bibliothek.gui.dock.title.DockTitle;
import bibliothek.gui.dock.title.DockTitleEvent;
import bibliothek.gui.dock.title.DockTitleFactory;
import bibliothek.gui.dock.title.DockTitleRequest;
import bibliothek.gui.dock.title.DockTitleVersion;

/**
 * A very simplistic implementation of a {@link DockTitle}. This particular implementation
 * shows a line with a width or height of 3 pixels and a custom color. 
 * @author Benjamin Sigg
 */
public class ToolbarDockTitle extends JComponent implements DockTitle{
	/**
	 * Creates a new factory that creates new {@link ToolbarDockTitle}s.
	 * @param color the color of the title
	 * @return the new factory
	 */
	public static DockTitleFactory createFactory( final Color color ){
		return new DockTitleFactory(){
			@Override
			public void uninstall( DockTitleRequest request ){
				// ignore
			}
			
			@Override
			public void request( DockTitleRequest request ){
				request.answer( new ToolbarDockTitle( request.getVersion(), request.getTarget(), color ) );
			}
			
			@Override
			public void install( DockTitleRequest request ){
				// ignore
			}
		};
	}
	
	private Dockable dockable;
	private Color color;
	private Orientation orientation = Orientation.FREE_HORIZONTAL;
	private boolean active = false;
	private DockTitleVersion origin;
	
	public ToolbarDockTitle( DockTitleVersion origin, Dockable dockable, Color color ){
		this.dockable = dockable;
		this.color = color;
		this.origin = origin;
	}
	
	@Override
	public DockElement getElement(){
		return dockable;
	}

	@Override
	public boolean isUsedAsTitle(){
		return true;
	}

	@Override
	public boolean shouldFocus(){
		return true;
	}

	@Override
	public boolean shouldTransfersFocus(){
		return false;
	}

	@Override
	public Point getPopupLocation( Point click, boolean popupTrigger ){
		return null; // no support for popups
	}

	@Override
	public Component getComponent(){
		return this;
	}

	@Override
	public void addMouseInputListener( MouseInputListener listener ){
		addMouseListener( listener );
		addMouseMotionListener( listener );
	}

	@Override
	public void removeMouseInputListener( MouseInputListener listener ){
		removeMouseListener( listener );
		removeMouseMotionListener( listener );
	}

	@Override
	public Dockable getDockable(){
		return dockable;
	}

	@Override
	public void setOrientation( Orientation orientation ){
		this.orientation = orientation;
		invalidate();
		repaint();
	}

	@Override
	public Orientation getOrientation(){
		return orientation;
	}

	@Override
	public void changed( DockTitleEvent event ){
		if( event instanceof ActivityDockTitleEvent ){
			active = ((ActivityDockTitleEvent)event).isActive();
			repaint();
		}
	}

	@Override
	public boolean isActive(){
		return active;
	}

	@Override
	public void bind(){
		// nothing to do
	}

	@Override
	public void unbind(){
		// nothing to do	
	}

	@Override
	public DockTitleVersion getOrigin(){
		return origin;
	}
	
	@Override
	public Dimension getPreferredSize(){
		if( orientation.isHorizontal() ){
			return new Dimension( 10, 3 );
		}
		else{
			return new Dimension( 3, 10 );
		}
	}
	
	@Override
	protected void paintComponent( Graphics g ){
		g.setColor( color );
		g.fillRect( 0, 0, getWidth(), getHeight() );
		
		if( isActive() ){
			g.setColor( Color.BLACK );
			if( orientation.isHorizontal() ){
				g.drawLine( 1, getHeight()/2, getWidth()-1, getHeight()/2 );
			}
			else{
				g.drawLine( getWidth()/2, 1, getWidth()/2, getHeight()-1 );
			}
		}
	}
}
