/*
 * Bibliothek - DockingFrames
 * Library built on Java/Swing, allows the user to "drag and drop"
 * panels containing any Swing-Component the developer likes to add.
 * 
 * Copyright (C) 2011 Benjamin Sigg
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
package bibliothek.gui.dock.station.stack;

import java.awt.Component;
import java.awt.Dimension;

import bibliothek.gui.DockController;
import bibliothek.gui.Dockable;
import bibliothek.gui.dock.action.DockAction;
import bibliothek.gui.dock.action.DockActionSource;
import bibliothek.gui.dock.station.stack.tab.AbstractTabPaneComponent;
import bibliothek.gui.dock.station.stack.tab.LonelyTabPaneComponent;
import bibliothek.gui.dock.station.stack.tab.TabPane;
import bibliothek.gui.dock.station.stack.tab.TabPaneListener;
import bibliothek.gui.dock.station.stack.tab.layouting.LayoutBlock;
import bibliothek.gui.dock.station.stack.tab.layouting.Size;
import bibliothek.gui.dock.station.stack.tab.layouting.TabPlacement;
import bibliothek.gui.dock.station.stack.tab.layouting.Size.Type;
import bibliothek.gui.dock.themes.basic.action.buttons.ButtonPanel;

/**
 * An {@link AbstractTabPaneComponent} that was specifically designed for showing a set of {@link DockAction}s.
 * @author Benjamin Sigg
 */
public abstract class DockActionCombinedInfoComponent extends AbstractTabPaneComponent implements CombinedInfoComponent, LayoutBlock{
	private CombinedStackDockComponent<?, ?, ?> pane;
	private ButtonPanel buttons;
	
	private Dockable dockable;
	
	/** a listener that is added to {@link #pane} */
	private TabPaneListener listener = new TabPaneListener(){
		public void selectionChanged( TabPane pane ){
			setSelection( pane.getSelectedDockable() );
		}
		
		public void removed( TabPane pane, Dockable dockable ){
			// ignore
		}
		
		public void infoComponentChanged( TabPane pane, LonelyTabPaneComponent oldInfo, LonelyTabPaneComponent newInfo ){
			// ignore
		}
		
		public void added( TabPane pane, Dockable dockable ){
			// ignore
		}
		public void controllerChanged( TabPane pane, DockController controller ){
			setController( controller );
		}
	};
	
	/**
	 * Creates a new component.
	 * @param pane the owner of this info
	 */
	public DockActionCombinedInfoComponent( CombinedStackDockComponent<?, ?, ?> pane ){
		super( pane );
		this.pane = pane;
		buttons = new ButtonPanel( true );
		
		pane.addTabPaneListener( listener );
		setController( pane.getController() );
	}
	
	/**
	 * Informs this component that it should release any remaining resources.
	 */
	public void destroy(){
		pane.removeTabPaneListener( listener );
		setController( null );
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
	 * Gets the element which is currently shown on this info.
	 * @return the selected item, can be <code>null</code>
	 */
	public Dockable getSelection(){
		return dockable;
	}
	
	/**
	 * Sets the {@link DockController} in whose realm this panel is used. This method is usually called
	 * automatically by the {@link TabPaneListener} that is added to the owner of this panel.
	 * @param controller the controller, can be <code>null</code>
	 */
	public void setController( DockController controller ){
		buttons.setController( controller );
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
			buttons.set( dockable, createActionSource( dockable ) );
		}
	}
	
	/**
	 * Creates a new {@link DockActionSource} for <code>dockable</code>.
	 * @param dockable the element for which the actions are required
	 * @return the new source of actions
	 */
	protected abstract DockActionSource createActionSource( Dockable dockable );
	
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
	
	@Override
	public void setOrientation( TabPlacement orientation ){
		super.setOrientation( orientation );
		buttons.setOrientation( orientation.toOrientation() );
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
			
			result[i] = new CountingSize( type, sizes[i], i, (i+1) / (double)(sizes.length) );
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
		 * @param score how much this size is liked
		 */
		public CountingSize( Type type, Dimension size, int count, double score ){
			super( type, size, score );
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