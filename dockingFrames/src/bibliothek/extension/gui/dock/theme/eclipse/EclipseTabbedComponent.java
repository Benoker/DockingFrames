package bibliothek.extension.gui.dock.theme.eclipse;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.event.MouseEvent;

import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.JPanel;

import bibliothek.extension.gui.dock.theme.eclipse.rex.RexTabbedComponent;
import bibliothek.extension.gui.dock.theme.eclipse.rex.tab.ShapedGradientPainter;
import bibliothek.extension.gui.dock.theme.eclipse.rex.tab.Tab;
import bibliothek.gui.Dockable;
import bibliothek.gui.dock.action.ActionPopup;
import bibliothek.gui.dock.action.DockActionSource;
import bibliothek.gui.dock.themes.basic.action.buttons.ButtonPanel;

/**
 * @author Janni Kovacs
 */
public class EclipseTabbedComponent extends RexTabbedComponent {
	private EclipseStackDockComponent eclipseStackDockComponent;
	private ButtonPanel itemPanel;
	
	public EclipseTabbedComponent(EclipseStackDockComponent eclipseStackDockComponent) {
		this.eclipseStackDockComponent = eclipseStackDockComponent;
		
		itemPanel = new ButtonPanel( true );
		itemPanel.setBorder(BorderFactory.createEmptyBorder(2, 0, 2, 4));
		itemPanel.setOpaque( false );
		
		setBorder(new EclipseBorder());
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
	protected void popup( final Tab tab, MouseEvent e ){
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

	private class EclipseTabStrip extends JPanel{
		private JPanel strip;
		
		public EclipseTabStrip() {
			setLayout( null );
			setFocusable(false);
			setOpaque( false );
			
			strip = new JPanel( new FlowLayout( FlowLayout.LEFT, 0, 0 ));
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
		protected void paintComponent( Graphics g ){
			super.paintComponent( g );
			getTabPainter().paintTabStrip( EclipseTabbedComponent.this, g );
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
