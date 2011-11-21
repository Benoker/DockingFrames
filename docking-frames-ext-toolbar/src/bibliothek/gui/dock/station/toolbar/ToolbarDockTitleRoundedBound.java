package bibliothek.gui.dock.station.toolbar;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Point;
import java.awt.RenderingHints;
import java.awt.Stroke;
import java.awt.geom.Rectangle2D;
import java.awt.geom.RoundRectangle2D;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.DataBufferInt;
import java.awt.image.DirectColorModel;
import java.awt.image.Raster;
import java.awt.image.SampleModel;
import java.awt.image.WritableRaster;

import javax.swing.JComponent;
import javax.swing.UIManager;

import bibliothek.gui.Dockable;
import bibliothek.gui.ToolbarExtension;
import bibliothek.gui.dock.action.DockAction;
import bibliothek.gui.dock.themes.basic.action.BasicTitleViewItem;
import bibliothek.gui.dock.title.AbstractDockTitle;
import bibliothek.gui.dock.title.DockTitle;
import bibliothek.gui.dock.title.DockTitleFactory;
import bibliothek.gui.dock.title.DockTitleRequest;
import bibliothek.gui.dock.title.DockTitleVersion;
import bibliothek.gui.dock.title.DockTitle.Orientation;

/**
 * A implementation of a {@link DockTitle}. This particular implementation shows
 * round rectangular grip. Usually, this kind of title is used as top level
 * title, it means with toolbar and not with group components or components.
 * 
 * @author Herve Guillaume
 */
public class ToolbarDockTitleRoundedBound extends AbstractDockTitle{
	/**
	 * Creates a new factory that creates new {@link ToolbarDockTitle}s.
	 * 
	 * @param color
	 *            the color of the title
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
				request.answer(new ToolbarDockTitleRoundedBound(request
						.getVersion(), request.getTarget(), color));
			}

			@Override
			public void install( DockTitleRequest request ){
				// ignore
			}
		};
	}

	private Color color;
	private Orientation orientation = Orientation.FREE_HORIZONTAL;

	public ToolbarDockTitleRoundedBound( DockTitleVersion origin,
			Dockable dockable, Color color ){
		super(dockable, origin, true);
		this.color = color;
		this.setOpaque(false);
	}

	@Override
	protected BasicTitleViewItem<JComponent> createItemFor( DockAction action,
			Dockable dockable ){
		return dockable.getController().getActionViewConverter()
				.createView(action, ToolbarExtension.TOOLBAR_TITLE, dockable);
	}

	@Override
	public Dimension getPreferredSize(){
		Dimension size = super.getPreferredSize();
		return new Dimension(Math.max(5, size.width), Math.max(5, size.height));
	}

	@Override
	public void setActive( boolean active ){
		super.setActive(active);
		repaint();
	}

	// @Override
	// public void paintBackground( Graphics g, JComponent component ){
	// System.out.println("Paint BACKGROUND");
	// g.setColor( Color.GREEN );
	// g.fillRect( 0, 0, getWidth(), getHeight() );
	//
	// if( isActive() ){
	// g.setColor( Color.GREEN );
	// if( orientation.isHorizontal() ){
	// g.drawLine( 1, getHeight()/2, getWidth()-1, getHeight()/2 );
	// }
	// else{
	// g.drawLine( getWidth()/2, 1, getWidth()/2, getHeight()-1 );
	// }
	// }
	// }

	@Override
	protected void paintComponent( Graphics g ){
		super.paintComponents(g);
		Graphics2D g2D = (Graphics2D) g;
		g2D.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
				RenderingHints.VALUE_ANTIALIAS_ON);
		g2D.setColor(color);
		if (orientation == Orientation.FREE_HORIZONTAL){
			RoundRectangle2D rectangleRounded = new RoundRectangle2D.Double(0,
					0, getWidth(), getHeight() * 2, 8, 8);
			g2D.fill(rectangleRounded);
		} else{
			Rectangle2D rectangle = new Rectangle2D.Double(0, 0, getWidth(),
					getHeight());
			g2D.fill(rectangle);
		}
	}

}