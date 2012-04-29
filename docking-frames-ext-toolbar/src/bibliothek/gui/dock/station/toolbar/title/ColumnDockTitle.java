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

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Insets;
import java.awt.Point;
import java.util.ArrayList;
import java.util.List;

import javax.swing.Icon;
import javax.swing.JComponent;
import javax.swing.SwingUtilities;

import bibliothek.gui.Dockable;
import bibliothek.gui.dock.action.DockAction;
import bibliothek.gui.dock.action.DockActionSource;
import bibliothek.gui.dock.action.LineDockActionSource;
import bibliothek.gui.dock.themes.basic.action.BasicTitleViewItem;
import bibliothek.gui.dock.themes.basic.action.buttons.ButtonPanel;
import bibliothek.gui.dock.title.AbstractMultiDockTitle;
import bibliothek.gui.dock.title.DockTitle;
import bibliothek.gui.dock.title.DockTitleVersion;

/**
 * This specialized {@link DockTitle} does not show a text or even an {@link Icon}. Instead it shows several
 * {@link DockActionSource}s, all derived from a single {@link ColumnDockActionSource}. 
 * @author Benjamin Sigg
 */
public abstract class ColumnDockTitle extends AbstractMultiDockTitle {
	private List<ButtonPanel> itemPanels = new ArrayList<ButtonPanel>();
	private ButtonPanel directPanel;
	private ColumnDockActionSource source;

	/**
	 * Creates a new title.
	 * @param dockable the element for which this title is used
	 * @param origin a description telling how this title was created
	 */
	public ColumnDockTitle( Dockable dockable, DockTitleVersion origin ){
		init( dockable, origin );
		
		directPanel = new ButtonPanel( true ){
			@Override
			protected BasicTitleViewItem<JComponent> createItemFor( DockAction action, Dockable dockable ){
				return ColumnDockTitle.this.createItemFor( action, dockable );
			}
		};
		add( directPanel );
	}

	/**
	 * This listener is added to the current {@link #source} and adds or removes {@link #itemPanels}
	 * when necessary.
	 */
	private ColumnDockActionSourceListener listener = new ColumnDockActionSourceListener(){
		@Override
		public void reshaped( ColumnDockActionSource source ){
			revalidate();
		}

		@Override
		public void removed( ColumnDockActionSource source, DockActionSource item, int index ){
			if( isBound() ) {
				ButtonPanel panel = itemPanels.remove( index );
				panel.set( null );
				panel.setController( null );
				remove( panel );
				revalidate();
			}
		}

		@Override
		public void inserted( ColumnDockActionSource source, DockActionSource item, int index ){
			if( isBound() ) {
				createPanel( item, index );
			}
		}
	};

	private void createPanel( DockActionSource item, int index ){
		ButtonPanel panel = new ButtonPanel( true ){
			@Override
			protected BasicTitleViewItem<JComponent> createItemFor( DockAction action, Dockable dockable ){
				return ColumnDockTitle.this.createItemFor( action, dockable );
			}
		};
		panel.set( getDockable(), item );
		panel.setController( getDockable().getController() );
		panel.setOrientation( getOrientation() );
		panel.setToolTipText( getToolTipText() );
		itemPanels.add( index, panel );
		add( panel );
		revalidate();
	}

	@Override
	public void setOrientation( Orientation orientation ){
		if( getOrientation() != orientation ) {
			super.setOrientation( orientation );
			for( ButtonPanel panel : itemPanels ) {
				panel.setOrientation( orientation );
			}
			directPanel.setOrientation( orientation );
			revalidate();
		}
	}

	/**
	 * Gets the {@link ColumnDockActionSource} that should be used for finding the actions
	 * of <code>dockable</code>.
	 * @param dockable the element that is represented by this title.
	 * @return the source for <code>dockable</code> or <code>null</code>
	 */
	protected abstract ColumnDockActionSource getSourceFor( Dockable dockable );
	
	/**
	 * Gets the {@link DockActionSource} which should be used for <code>dockable</code>.
	 * @param dockable the element whose actions are shown
	 * @return all the actions
	 */
	protected DockActionSource getActionSourceFor( Dockable dockable ){
		return new LineDockActionSource( dockable.getGlobalActionOffers() );
	}

	@Override
	public void bind(){
		if( !isBound() ) {
			Dockable dockable = getDockable();
			source = getSourceFor( dockable );
			if( source != null ) {
				for( int i = 0, n = source.getSourceCount(); i < n; i++ ) {
					createPanel( source.getSource( i ), i );
				}
				source.addListener( listener );
			}
			
			directPanel.set( dockable, getActionSourceFor( dockable ) );
		}
		super.bind();
	}

	@Override
	public void unbind(){
		super.unbind();
		if( !isBound() ) {
			if( source != null ) {
				source.removeListener( listener );
				for( ButtonPanel panel : itemPanels ) {
					panel.set( null );
					panel.setController( null );
					remove( panel );
				}
				directPanel.set( null );
				itemPanels.clear();
				revalidate();
				source = null;
			}
		}
	}

	@Override
	protected void updateIcon(){
		// ignore
	}

	@Override
	protected void updateText(){
		// ignore
	}

	private int getOffset( int sourceIndex ){
		int offset = source.getSourceOffset( sourceIndex );

		Component dockable = getDockable().getComponent();
		Point point = new Point( offset, offset );

		if( SwingUtilities.getRoot( this ) == SwingUtilities.getRoot( dockable ) ) {
			point = SwingUtilities.convertPoint( dockable, point, this );
		}

		if( source.getOrientation() == bibliothek.gui.Orientation.VERTICAL ) {
			return point.x;
		}
		else {
			return point.y;
		}
	}

	@Override
	protected void doTitleLayout(){
		if( source == null ) {
			return;
		}

		Insets insets = titleInsets();
		int x = insets.left;
		int y = insets.top;
		int width = getWidth() - insets.left - insets.right;
		int height = getHeight() - insets.top - insets.bottom;

		boolean horizontal = getOrientation().isHorizontal();

		for( int i = 0, n = source.getSourceCount(); i < n; i++ ) {
			int start = getOffset( i );
			int length = source.getSourceLength( i );
			ButtonPanel items = itemPanels.get( i );

			Dimension[] preferred = items.getPreferredSizes();
			Dimension[] directPreferred = null;
			
			if( i+1 == n ){
				directPreferred = directPanel.getPreferredSizes();
				if( horizontal ){
					length -= directPreferred[0].width;
				}
				else{
					length -= directPreferred[0].height;
				}
			}
			
			if( horizontal ) {
				int size = 0;
				int delta = 0;
				for( int j = preferred.length - 1; j >= 0; j-- ) {
					if( preferred[j].width <= length ) {
						size = j;
						delta = length - preferred[j].width;
						break;
					}
				}
				items.setVisibleActions( size );
				items.setBounds( start, y, length - delta, height );
				
				if( i+1 == n ){
					int remaining = width - start - length + delta;
					size = 0;
					delta = 0;
					for( int j = directPreferred.length - 1; j >= 0; j-- ){
						if( directPreferred[j].width <= remaining ) {
							size = j;
							delta = remaining - directPreferred[j].width;
							break;
						}
					}
					directPanel.setVisibleActions( size );
					directPanel.setBounds( x + width - remaining + delta, y, remaining - delta, height );
				}
			}
			else {
				int size = 0;
				int delta = 0;
				for( int j = preferred.length - 1; j >= 0; j-- ) {
					if( preferred[j].height <= length ) {
						size = j;
						delta = length - preferred[j].height;
						break;
					}
				}
				items.setVisibleActions( size );
				items.setBounds( x, start, width, length - delta );
				
				if( i+1 == n ){
					int remaining = height - start - length + delta;
					size = 0;
					delta = 0;
					for( int j = directPreferred.length - 1; j >= 0; j-- ){
						if( directPreferred[j].height <= remaining ){
							size = j;
							delta = remaining - directPreferred[j].height;
							break;
						}
					}
					directPanel.setVisibleActions( size );
					directPanel.setBounds( x, y + height - remaining + delta, width, remaining - delta );
				}
			}
		}
	}

	@Override
	public Dimension getPreferredSize(){
		int w = 0;
		int h = 0;

		if( source != null ) {
			if( getOrientation().isHorizontal() ) {
				for( int i = 0, n = source.getSourceCount(); i < n; i++ ) {
					w = Math.max( w, getOffset( i ) + source.getSourceLength( i ) );
					h = Math.max( h, itemPanels.get( i ).getPreferredSize().height );
				}
			}
			else {
				for( int i = 0, n = source.getSourceCount(); i < n; i++ ) {
					w = Math.max( w, itemPanels.get( i ).getPreferredSize().width );
					h = Math.max( h, getOffset( i ) + source.getSourceLength( i ) );
				}
			}
		}

		w = Math.max( w, 5 );
		h = Math.max( h, 5 );

		Insets insets = titleInsets();

		w += insets.left + insets.right;
		h += insets.top + insets.bottom;

		return new Dimension( w, h );
	}
}
