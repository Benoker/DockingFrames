/*
 * Bibliothek - DockingFrames
 * Library built on Java/Swing, allows the user to "drag and drop"
 * panels containing any Swing-Component the developer likes to add.
 * 
 * Copyright (C) 2012 Herve Guillaume, Benjamin Sigg
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
 * Herve Guillaume
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

/**
 * A simplistic implementation of a {@link DockTitle}. This particular
 * implementation shows a line of dot.
 * 
 * @author Herve Guillaume
 */
public class ToolbarDockTitlePoint extends AbstractDockTitle{

	private Color backgroundColor = UIManager.getColor("Button.background");
	private Color pointColor = backgroundColor.darker();
	

	/**
	 * Creates a new factory that creates new {@link ToolbarDockTitlePoint}s.
	 * 
	 * @param color
	 *            the color of the title
	 * @return the new factory
	 */
	public static DockTitleFactory createFactory( final Color backgroundColor,
			final Color pointColor ){
		return new DockTitleFactory(){
			@Override
			public void uninstall( DockTitleRequest request ){
				// ignore
			}

			@Override
			public void request( DockTitleRequest request ){
				request.answer(new ToolbarDockTitlePoint(request.getVersion(),
						request.getTarget(), backgroundColor, pointColor));
			}

			@Override
			public void install( DockTitleRequest request ){
				// ignore
			}
		};
	}

	/**
	 * Creates a new factory that creates new {@link ToolbarDockTitlePoint}s.
	 * 
	 * @param color
	 *            the color of the title
	 * @return the new factory
	 */
	public static DockTitleFactory createFactory(){
		return new DockTitleFactory(){
			@Override
			public void uninstall( DockTitleRequest request ){
				// ignore
			}

			@Override
			public void request( DockTitleRequest request ){
				request.answer(new ToolbarDockTitlePoint(request.getVersion(),
						request.getTarget()));
			}

			@Override
			public void install( DockTitleRequest request ){
				// ignore
			}
		};
	}

	public ToolbarDockTitlePoint( DockTitleVersion origin, Dockable dockable,
			Color backgroundColor, Color pointColor ){
		super(dockable, origin, true);
		this.backgroundColor = backgroundColor;
		this.pointColor = pointColor;
	}

	public ToolbarDockTitlePoint( DockTitleVersion origin, Dockable dockable ){
		super(dockable, origin, true);
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

	private static final int POINT_DISTANCE = 2;

	@Override
	protected void paintComponent( Graphics g ){
		// paint background
		g.setColor(backgroundColor);
		g.fillRect(0, 0, getWidth(), getHeight());
		g.setColor(pointColor);
		if (getOrientation().isHorizontal()){
			// Draw a horizontal handle.
			int x = this.getWidth() / 6 ;
			final int y = this.getHeight() / 2;
			while (x <= (this.getWidth() - (this.getWidth() / 6))){
				g.drawLine(x, y - 1, x, y - 1);
				g.drawLine(x, y + 1, x, y + 1);
				x += POINT_DISTANCE;
			}
		} else{
			// Draw a vertical handle.
			final int x = this.getWidth() / 2;
			int y = this.getHeight() / 6 ;
			while (y < (this.getHeight() - (this.getHeight() / 6))){
				g.drawLine(x - 1, y , x - 1, y);
				g.drawLine(x + 1, y, x + 1, y);
				y += POINT_DISTANCE;
			}

		}
	}

}
