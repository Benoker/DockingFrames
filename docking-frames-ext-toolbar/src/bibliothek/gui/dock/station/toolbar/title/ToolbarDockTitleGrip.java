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
 * implementation shows a grip.
 * 
 * @author Herve Guillaume
 */
public class ToolbarDockTitleGrip extends AbstractDockTitle{

	private Color backgroundColor = UIManager.getColor("Button.background");
	private Color gripShadowColor = Color.gray;
	private Color gripColor = Color.white;

	/**
	 * Creates a new factory that creates new {@link ToolbarDockTitleGrip}s.
	 * 
	 * @param color
	 *            the color of the title
	 * @return the new factory
	 */
	public static DockTitleFactory createFactory( final Color backgroundColor,
			final Color gripColor, final Color gripShadowColor ){
		return new DockTitleFactory(){
			@Override
			public void uninstall( DockTitleRequest request ){
				// ignore
			}

			@Override
			public void request( DockTitleRequest request ){
				request.answer(new ToolbarDockTitleGrip(request.getVersion(),
						request.getTarget(), backgroundColor, gripColor,
						gripShadowColor));
			}

			@Override
			public void install( DockTitleRequest request ){
				// ignore
			}
		};
	}

	/**
	 * Creates a new factory that creates new {@link ToolbarDockTitleGrip}s.
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
				request.answer(new ToolbarDockTitleGrip(request.getVersion(),
						request.getTarget()));
			}

			@Override
			public void install( DockTitleRequest request ){
				// ignore
			}
		};
	}

	public ToolbarDockTitleGrip( DockTitleVersion origin, Dockable dockable,
			Color backgroundColor, Color gripColor, Color gripShadowColor ){
		super(dockable, origin, true);
		this.backgroundColor = backgroundColor;
		this.gripColor = gripColor;
		this.gripShadowColor = gripShadowColor;
	}

	public ToolbarDockTitleGrip( DockTitleVersion origin, Dockable dockable ){
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

	@Override
	protected void paintComponent( Graphics g ){

		final int lineOffset = 5;
		final int headerOffset = 3;
		// paint background
		g.setColor(backgroundColor);
		g.fillRect(0, 0, getWidth(), getHeight());
		if (getOrientation().isHorizontal()){
			// Draw a horizontal handle.
			final int width = getSize().width;

			// Draw the light line.
			g.setColor(gripColor);
			g.drawLine(lineOffset, headerOffset, width - lineOffset,
					headerOffset);
			g.drawLine(lineOffset, headerOffset + 1, width - lineOffset,
					headerOffset + 1);

			// Draw the shadow.
			g.setColor(gripShadowColor);
			g.drawLine((width - lineOffset) + 1, headerOffset,
					(width - lineOffset) + 1, headerOffset + 2);
			g.drawLine(lineOffset, headerOffset + 2, width - lineOffset,
					headerOffset + 2);

		} else{
			// Draw a vertical handle.
			final int height = getSize().height;

			// Draw the light line.
			g.setColor(gripColor);
			g.drawLine(headerOffset, lineOffset, headerOffset, height
					- lineOffset);
			g.drawLine(headerOffset + 1, lineOffset, headerOffset + 1, height
					- lineOffset);

			// Draw the shadow.
			g.setColor(gripShadowColor);
			g.drawLine(headerOffset, (height - lineOffset) + 1,
					headerOffset + 2, (height - lineOffset) + 1);
			g.drawLine(headerOffset + 2, lineOffset, headerOffset + 2, height
					- lineOffset);
		}
	}

}
