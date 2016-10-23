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

package bibliothek.gui.dock.station.toolbar.group;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.RenderingHints;
import java.awt.geom.RoundRectangle2D;

import javax.swing.JComponent;

import bibliothek.gui.Dockable;
import bibliothek.gui.ToolbarExtension;
import bibliothek.gui.dock.ToolbarGroupDockStation;
import bibliothek.gui.dock.action.DockAction;
import bibliothek.gui.dock.station.toolbar.title.ColumnDockActionSource;
import bibliothek.gui.dock.station.toolbar.title.ColumnDockTitle;
import bibliothek.gui.dock.themes.basic.action.BasicTitleViewItem;
import bibliothek.gui.dock.themes.color.TitleColor;
import bibliothek.gui.dock.title.DockTitleFactory;
import bibliothek.gui.dock.title.DockTitleRequest;
import bibliothek.gui.dock.title.DockTitleVersion;
import bibliothek.gui.dock.util.Transparency;

/**
 * A specialized title that should only be used for {@link ToolbarGroupDockStation}s.
 * @author Benjamin Sigg
 */
public class ToolbarGroupTitle extends ColumnDockTitle {
	/**
	 * A factory creating new {@link ToolbarGroupTitle}s.
	 */
	public static final DockTitleFactory FACTORY = new DockTitleFactory(){
		@Override
		public void uninstall( DockTitleRequest request ){
			// ignore
		}

		@Override
		public void request( DockTitleRequest request ){
			request.answer( new ToolbarGroupTitle( request.getTarget(), request.getVersion() ) );
		}

		@Override
		public void install( DockTitleRequest request ){
			// ignore
		}
	};

	/**
	 * The background color of this title.
	 */
	protected TitleColor color;
	
	/**
	 * Creates a new title.
	 * @param dockable the element, preferably a {@link ToolbarGroupDockStation}, which is represented
	 * by this title.
	 * @param origin how to create this title
	 */
	public ToolbarGroupTitle( Dockable dockable, DockTitleVersion origin ){
		super( dockable, origin );
		setTransparency( Transparency.DEFAULT );
		
		color = new TitleColor( "extension.toolbar.group.title", this, new Color( 80, 80, 80 ) ){
			@Override
			protected void changed( Color oldValue, Color newValue ){
				repaint();
			}
		};
		
		addColor( color );
	}

	protected BasicTitleViewItem<JComponent> createItemFor( DockAction action, Dockable dockable ){
		return dockable.getController().getActionViewConverter().createView( action, ToolbarExtension.TOOLBAR_TITLE, dockable );
	}

	@Override
	protected Insets getInnerInsets(){
		return new Insets( 0, 0, 0, 0 );
	}
	
	@Override
	protected ColumnDockActionSource getSourceFor( Dockable dockable ){
		if( dockable instanceof ToolbarGroupDockStation ) {
			return ((ToolbarGroupDockStation) dockable).getExpandActionSource();
		}
		return null;
	}

	@Override
	public void setActive( boolean active ){
		super.setActive( active );
		repaint();
	}

	@Override
	public void paintBackground( Graphics g, JComponent component ){
		final Graphics2D g2D = (Graphics2D) g.create();
		g2D.setRenderingHint( RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON );
		g2D.setColor( color.color() );
		if( getOrientation().isHorizontal() ) {
			final RoundRectangle2D rectangleRounded = new RoundRectangle2D.Double( 0, 0, getWidth(), getHeight() * 2, 8, 8 );
			g2D.fill( rectangleRounded );
		}
		else {
			final RoundRectangle2D rectangleRounded = new RoundRectangle2D.Double( 0, 0, getWidth() * 2, getHeight(), 8, 8 );
			g2D.fill( rectangleRounded );
		}
		g2D.dispose();
	}
}
