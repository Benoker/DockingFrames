package bibliothek.gui.dock.station.toolbar;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Point;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.DataBufferInt;
import java.awt.image.DirectColorModel;
import java.awt.image.Raster;
import java.awt.image.SampleModel;
import java.awt.image.WritableRaster;

import javax.swing.JComponent;

import bibliothek.gui.Dockable;
import bibliothek.gui.ToolbarExtension;
import bibliothek.gui.dock.action.DockAction;
import bibliothek.gui.dock.themes.basic.action.BasicTitleViewItem;
import bibliothek.gui.dock.title.AbstractDockTitle;
import bibliothek.gui.dock.title.DockTitle;
import bibliothek.gui.dock.title.DockTitleFactory;
import bibliothek.gui.dock.title.DockTitleRequest;
import bibliothek.gui.dock.title.DockTitleVersion;

/**
 * A simplistic implementation of a {@link DockTitle}. This particular
 * implementation shows a line of dot.
 * 
 * @author Herve Guillaume
 */
public class ToolbarDockTitlePoint extends AbstractDockTitle{
	
	private Color color;
	
	/**
	 * Creates a new factory that creates new {@link ToolbarDockTitlePoint}s.
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
				request.answer(new ToolbarDockTitlePoint(request.getVersion(),
						request.getTarget(), color));
			}

			@Override
			public void install( DockTitleRequest request ){
				// ignore
			}
		};
	}

	public ToolbarDockTitlePoint( DockTitleVersion origin, Dockable dockable,
			Color color ){
		super(dockable, origin, true);
		this.color = color;
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

	@Override
	public void paintBackground( Graphics g, JComponent component ){
		g.setColor(color);
		g.fillRect(0, 0, getWidth(), getHeight());

		int inset = getWidth() / 8;

		if (isActive()){
			g.setColor(Color.GREEN);
			g.fillRect(0, 0, getWidth(), getHeight());
		}
	}

	/**
	 * This is the minimum width when the orientation is vertical. It is the
	 * minimum height when the orientation is horizontal.
	 */
	private static final int HEADER_SIZE = 9;

	private static final Image POINT;
	private static final int POINT_DISTANCE = 4;

	// this model draw an image ==> so the background is behind and invisible
	static{
		ColorModel colorModel = new DirectColorModel(24, 0xff0000, 0x00ff00,
				0x0000ff);
		SampleModel sampleModel = colorModel.createCompatibleSampleModel(3, 3);
		int[] pixels = new int[] { 0xffd6cfc6, 0xffb3b0ab, 0xffefebe7,
				0xffb3b0a3, 0xff8d887a, 0xffffffff, 0xffe7e7e7, 0xffffffff,
				0xfffbffff, };

		DataBufferInt dataBuffer = new DataBufferInt(pixels, 9);
		WritableRaster writableRaster = Raster.createWritableRaster(
				sampleModel, dataBuffer, new Point());
		POINT = new BufferedImage(colorModel, writableRaster, false, null);
	}

	@Override
	protected void paintComponent( Graphics g ){
		if (getOrientation().isHorizontal()){
			// Draw a horizontal handle.
			int x = 4;
			int y = 3;
			while (x < getWidth() - POINT_DISTANCE){
				g.drawImage(POINT, x, y, this);
				x += POINT_DISTANCE;
			}
		} else{
			// Draw a vertical handle.
			int x = 3;
			int y = 4;
			while (y < getHeight() - POINT_DISTANCE){
				g.drawImage(POINT, x, y, this);
				y += POINT_DISTANCE;
			}

		}
	}

}
