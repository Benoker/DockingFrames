/*
 * Bibliothek - DockingFrames
 * Library built on Java/Swing, allows the user to "drag and drop"
 * panels containing any Swing-Component the developer likes to add.
 * 
 * Copyright (C) 2007 Benjamin Sigg
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
package bibliothek.extension.gui.dock.theme.eclipse;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.event.MouseEvent;

import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.border.Border;

import bibliothek.extension.gui.dock.theme.EclipseTheme;
import bibliothek.extension.gui.dock.theme.eclipse.rex.RexTabbedComponent;
import bibliothek.extension.gui.dock.theme.eclipse.rex.tab.ShapedGradientPainter;
import bibliothek.extension.gui.dock.theme.eclipse.rex.tab.TabStripLayoutManager;
import bibliothek.gui.DockController;
import bibliothek.gui.DockStation;
import bibliothek.gui.Dockable;
import bibliothek.gui.dock.action.ActionPopup;
import bibliothek.gui.dock.action.DockActionSource;
import bibliothek.gui.dock.themes.basic.action.buttons.ButtonPanel;
import bibliothek.gui.dock.util.PropertyValue;

/**
 * @author Janni Kovacs
 */
public class EclipseTabbedComponent extends RexTabbedComponent {
	private EclipseStackDockComponent eclipseStackDockComponent;
	private ButtonPanel itemPanel;
	
	private PropertyValue<Border> border = new PropertyValue<Border>( EclipseTheme.FULL_BORDER ){
	    @Override
	    protected void valueChanged( Border oldValue, Border newValue ) {
	        setBorder( newValue );
	    }
	};
	
	public EclipseTabbedComponent(EclipseStackDockComponent eclipseStackDockComponent, EclipseTheme theme, DockStation station ) {
	    super( theme, station );
		this.eclipseStackDockComponent = eclipseStackDockComponent;
		
		itemPanel = new ButtonPanel( true );
		itemPanel.setBorder(BorderFactory.createEmptyBorder(2, 0, 2, 4));
		itemPanel.setOpaque( false );
		
		setBorder( null );
		setTabStrip( new EclipseTabStrip() );
		setTabPainter( ShapedGradientPainter.FACTORY );
	}

	public JComponent getTabStrip() {
		return tabStrip;
	}
	
	public void set( Dockable dockable, DockActionSource source ){
		itemPanel.set( dockable, source );
	}
	
	@Override
	public void setController( DockController controller ) {
	    super.setController( controller );
	    border.setProperties( controller == null ? null : controller.getProperties() );
	}
	
	@Override
	protected void popup( final Dockable tab, MouseEvent e ){
		if( !e.isConsumed() && e.isPopupTrigger() ){
			ActionPopup popup = new ActionPopup( true ){
				@Override
				protected Dockable getDockable(){
					int index = indexOf( tab );
					return eclipseStackDockComponent.getDockable( index );
				}

				@Override
				protected DockActionSource getSource(){
					return getDockable().getGlobalActionOffers();
				}

				@Override
				protected boolean isEnabled(){
					return true;
				}
			};
			
			popup.popup( e.getComponent(), e.getX(), e.getY() );
		}
	}
	

    @Override
    public void paint( Graphics g ) {
        super.paint( g );
        paintBorder( g );
    }

	private class EclipseTabStrip extends JPanel{
		private JPanel strip;
		
		public EclipseTabStrip() {
			setLayout( null );
			setFocusable(false);
			setOpaque( false );
			
			strip = new JPanel( new TabStripLayoutManager( EclipseTabbedComponent.this ) );
			strip.setOpaque( false );
			
			addImpl( strip, null, -1 );
			addImpl( itemPanel, null, -1 );
			
			setComponentZOrder( itemPanel, 0 );
			setComponentZOrder( strip, 1 );
		}
		
		@Override
		public void removeAll(){
			strip.removeAll();
		}
		
		@Override
		public Component add( Component comp ){
			return strip.add( comp );
		}
		
		@Override
		public void remove( Component comp ){
			strip.remove( comp );
		}

		@Override
		protected void paintComponent( Graphics g ){
			super.paintComponent( g );
			getTabPainter().paintTabStrip( EclipseTabbedComponent.this, this, g );
		}
		
		@Override
		public Dimension getMinimumSize(){
			return getPreferredSize();
		}
		
		@Override
		public Dimension getPreferredSize(){
			Dimension strip = this.strip.getPreferredSize();
			Dimension item = itemPanel.getPreferredSize();
			
			return new Dimension( strip.width + item.width, Math.max( strip.height, item.height ));
		}
		
		@Override
		public void doLayout(){
			Dimension stripPreferred = strip.getPreferredSize();
			Dimension[] itemPreferred = itemPanel.getPreferredSizes();
			
			int stripWidth = 0;
			
			if( stripPreferred.width + itemPreferred[0].width <= getWidth() ){
				stripWidth = stripPreferred.width;
			}
			else{
				stripWidth = Math.max( getWidth()/2, getWidth() - itemPreferred[0].width );
			}
			
			int remaining = getWidth() - stripWidth;
			int count = itemPreferred.length-1;
			
			while( count > 0 && itemPreferred[count].width > remaining )
				count--;
			
			int width = Math.min( remaining, itemPreferred[count].width );
			
			strip.setBounds( 0, 0, getWidth(), getHeight() );
			itemPanel.setVisibleActions( count );
			itemPanel.setBounds( getWidth()-width, 0, width, getHeight() );
		}
	}
}
