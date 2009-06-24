/*
 * Bibliothek - DockingFrames
 * Library built on Java/Swing, allows the user to "drag and drop"
 * panels containing any Swing-Component the developer likes to add.
 * 
 * Copyright (C) 2009 Benjamin Sigg
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
 * Benjamin Sigg
 * benjamin_sigg@gmx.ch
 * CH - Switzerland
 */
package bibliothek.extension.gui.dock.theme.eclipse.stack;

import java.awt.Component;
import java.awt.Dimension;

import bibliothek.extension.gui.dock.theme.eclipse.EclipseDockActionSource;
import bibliothek.extension.gui.dock.theme.eclipse.EclipseThemeConnector;
import bibliothek.gui.DockController;
import bibliothek.gui.Dockable;
import bibliothek.gui.dock.action.DockAction;
import bibliothek.gui.dock.action.DockActionSource;
import bibliothek.gui.dock.station.stack.CombinedInfoComponent;
import bibliothek.gui.dock.station.stack.tab.AbstractTabPaneComponent;
import bibliothek.gui.dock.station.stack.tab.TabPane;
import bibliothek.gui.dock.station.stack.tab.layouting.LayoutBlock;
import bibliothek.gui.dock.station.stack.tab.layouting.Size;
import bibliothek.gui.dock.station.stack.tab.layouting.Size.Type;
import bibliothek.gui.dock.themes.basic.action.buttons.ButtonPanel;

/**
 * This component shows a subset of {@link DockAction}s of the currently selected
 * {@link Dockable} of its parent {@link TabPane}. The global {@link EclipseThemeConnector}
 * is used to determine which actions to show, only non-tab actions are shown.
 * @author Benjamin Sigg
 */
public class EclipseTabInfo extends AbstractTabPaneComponent implements CombinedInfoComponent, LayoutBlock{
	private EclipseTabPane pane;
	private ButtonPanel buttons;
	
	private Dockable dockable;
	
	/**
	 * Creates a new component.
	 * @param pane the owner of this info
	 */
	public EclipseTabInfo( EclipseTabPane pane ){
		super( pane );
		this.pane = pane;
		buttons = new ButtonPanel( true );
	}
	
	/**
	 * Sets the element whose actions should be shown on this info.
	 * @param dockable the item, can be <code>null</code>
	 */
	public void setSelection( Dockable dockable ){
		this.dockable = dockable;
		updateContent();
	}
	
	/**
	 * Using the current {@link DockController} and {@link Dockable}, this
	 * method updates the {@link DockActionSource} which selects the
	 * actions of this info.
	 */
	protected void updateContent(){
		if( dockable == null ){
			buttons.set( null, null );
		}
		else{
			EclipseDockActionSource source = new EclipseDockActionSource( pane.getTheme(), dockable.getGlobalActionOffers(), dockable, false );
			buttons.set( dockable, source );
		}
	}
	
	@Override
	public Component getComponent(){
		return buttons;
	}

	public boolean isPaneVisible(){
		return pane.getInfoHandler().isVisible( this );
	}

	public void setPaneVisible( boolean visible ){
		pane.getInfoHandler().setVisible( this, visible );
	}
	
	public int getZOrder(){
		return pane.getInfoHandler().getZOrder( this );
	}
	
	public void setZOrder( int order ){
		pane.getInfoHandler().setZOrder( this, order );	
	}
	
	public LayoutBlock toLayoutBlock(){
		return this;
	}
	
	public Size[] getSizes(){
		Dimension[] sizes = buttons.getPreferredSizes();
		Size[] result = new Size[ sizes.length ];
		
		for( int i = 0; i < sizes.length; i++ ){
			Type type;
			if( i+1 == sizes.length )
				type = Type.PREFERRED;
			else
				type = Type.MINIMUM;
			
			result[i] = new CountingSize( type, sizes[i], i );
		}
		
		return result;
	}
	
	public void setLayout( Size size ){
		if( size instanceof CountingSize ){
			buttons.setVisibleActions( ((CountingSize)size).getCount() );
		}
		else{
			throw new IllegalArgumentException( "size not created by this component" );
		}
	}
	
	public void setBounds( int x, int y, int width, int height ){
		buttons.setBounds( x, y, width, height );
	}
	
	/**
	 * Size also counting the number of actions that are visible when applied.
	 * @author Benjamin Sigg
	 */
	private static class CountingSize extends Size{
		private int count;
		
		/**
		 * Creates a new size.
		 * @param type what kind of size this represents
		 * @param size the amount of pixels needed
		 * @param count the number of actions shown
		 */
		public CountingSize( Type type, Dimension size, int count ){
			super( type, size );
			this.count = count;
		}
		
		/**
		 * Gets the number of actions that are shown.
		 * @return the number of actions
		 */
		public int getCount(){
			return count;
		}
	}
}
