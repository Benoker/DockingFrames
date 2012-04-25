/*
 * Bibliothek - DockingFrames
 * Library built on Java/Swing, allows the user to "drag and drop"
 * panels containing any Swing-Component the developer likes to add.
 * 
 * Copyright (C) 2012 Hervé Guillaume, Benjamin Sigg
 * 
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301  USA
 * 
 * Hervé Guillaume
 * rvguillaume@hotmail.com
 * FR - France
 *
 * Benjamin Sigg
 * benjamin_sigg@gmx.ch
 * CH - Switzerland
 */

package bibliothek.gui.dock.station.toolbar.title;

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

	private final Color color;

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
		final Dimension size = super.getPreferredSize();
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

		if (isActive()){
			g.setColor(Color.GREEN);
			g.fillRect(0, 0, getWidth(), getHeight());
		}
	}

	private static final Image POINT;
	private static final int POINT_DISTANCE = 4;

	// this model draw an image ==> so the background is behind and invisible
	static{
		final ColorModel colorModel = new DirectColorModel(24, 0xff0000,
				0x00ff00, 0x0000ff);
		final SampleModel sampleModel = colorModel.createCompatibleSampleModel(
				3, 3);
		final int[] pixels = new int[] { 0xffd6cfc6, 0xffb3b0ab, 0xffefebe7,
				0xffb3b0a3, 0xff8d887a, 0xffffffff, 0xffe7e7e7, 0xffffffff,
				0xfffbffff, };

		final DataBufferInt dataBuffer = new DataBufferInt(pixels, 9);
		final WritableRaster writableRaster = Raster.createWritableRaster(
				sampleModel, dataBuffer, new Point());
		POINT = new BufferedImage(colorModel, writableRaster, false, null);
	}

	@Override
	protected void paintComponent( Graphics g ){
		if (getOrientation().isHorizontal()){
			// Draw a horizontal handle.
			int x = 4;
			final int y = 3;
			while (x < (getWidth() - POINT_DISTANCE)){
				g.drawImage(POINT, x, y, this);
				x += POINT_DISTANCE;
			}
		} else{
			// Draw a vertical handle.
			final int x = 3;
			int y = 4;
			while (y < (getHeight() - POINT_DISTANCE)){
				g.drawImage(POINT, x, y, this);
				y += POINT_DISTANCE;
			}

		}
	}

}
