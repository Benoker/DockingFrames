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
package bibliothek.extension.gui.dock.theme.eclipse.rex.tab;

import java.awt.*;
import java.awt.event.*;
import java.awt.font.FontRenderContext;
import java.awt.geom.Rectangle2D;

import javax.swing.Icon;
import javax.swing.JComponent;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.border.Border;
import javax.swing.border.MatteBorder;

import bibliothek.extension.gui.dock.theme.eclipse.EclipseDockActionSource;
import bibliothek.extension.gui.dock.theme.eclipse.rex.RexSystemColor;
import bibliothek.extension.gui.dock.theme.eclipse.rex.RexTabbedComponent;
import bibliothek.gui.DockController;
import bibliothek.gui.Dockable;
import bibliothek.gui.dock.event.DockableListener;
import bibliothek.gui.dock.themes.basic.action.buttons.ButtonPanel;
import bibliothek.gui.dock.title.DockTitle;

/**
 * @author Janni Kovacs
 */
public class RectGradientPainter extends JComponent implements TabComponent {
	public static final TabPainter FACTORY = new TabPainter(){
	    public TabComponent createTabComponent( DockController controller,
	            RexTabbedComponent component, Dockable dockable, int index ) {
	        
	        return new RectGradientPainter( component, dockable, index );
	    }
	    
		public void paintTabStrip( RexTabbedComponent tabbedComponent, Component tabStrip, Graphics g ){
			int selectedIndex = tabbedComponent.getSelectedIndex();
			if (selectedIndex != -1) {
				Rectangle selectedBounds = tabbedComponent.getBoundsAt(selectedIndex);
				int to = selectedBounds.x;
				int from = selectedBounds.x + selectedBounds.width-1;
				int end = tabStrip.getWidth();
				Color lineColor = SystemColor.controlShadow;
				g.setColor(lineColor);
				int y = tabStrip.getHeight()-1;
				
				if (to != 0)
					g.drawLine(-1, y, to-1, y);
				if( from != end )
					g.drawLine(from, y, end, y);
			}
		}
	};
	
	private boolean paintIconWhenInactive = false;

//	private EditableMatteBorder contentBorder = new EditableMatteBorder(2, 2, 2, 2);
	private MatteBorder contentBorder = new MatteBorder(2, 2, 2, 2, Color.BLACK);

	private boolean hasFocus;
	private boolean isSelected;
	private RexTabbedComponent comp;
	private Dockable dockable;
	private int tabIndex;
	private ButtonPanel buttons;
    private Listener dockableListener = new Listener();
	
	public RectGradientPainter( RexTabbedComponent comp, Dockable dockable, int index ){
		this.comp = comp;
		this.dockable = dockable;
		this.tabIndex = index;
		
		setLayout( null );
		setOpaque( false );
		
		buttons = new ButtonPanel( false );
        
        if( buttons != null )
            add( buttons );

		
		addHierarchyListener( new WindowActiveObserver() );
		addMouseListener( new MouseAdapter(){
		    @Override
		    public void mouseClicked( MouseEvent e ) {
		        if( e.getClickCount() == 2 ){
		            DockController controller = RectGradientPainter.this.dockable.getController();
		            if( controller != null ){
		                controller.getDoubleClickController().send( 
		                        RectGradientPainter.this.dockable, e );
		            }
		        }
		    }
		});
	}
	
	public void bind() {
        if( buttons != null )
            buttons.set( dockable, new EclipseDockActionSource(
                    comp.getTheme(), dockable.getGlobalActionOffers(), dockable, true ) );
        dockable.addDockableListener( dockableListener );
        revalidate();
    }
    
    public void unbind() {
        if( buttons != null )
            buttons.set( null );
        dockable.removeDockableListener( dockableListener );
    }

	
	public int getOverlap() {
	    return 0;
	}
	
	public Component getComponent(){
		return this;
	}

	public void setFocused( boolean focused ){
		hasFocus = focused;
		updateBorder();
		repaint();
	}

	public void setIndex( int index ){
		tabIndex = index;
	}

	public void setPaintIconWhenInactive( boolean paint ){
		paintIconWhenInactive = paint;
		revalidate();
	}

	public void setSelected( boolean selected ){
		isSelected = selected;
		updateBorder();
		revalidate();
		repaint();
	}

	public void update(){
		updateBorder();
		revalidate();
	}

	@Override
	public Dimension getPreferredSize() {
		FontRenderContext frc = new FontRenderContext(null, false, false);
		Rectangle2D bounds = UIManager.getFont("Label.font").getStringBounds(dockable.getTitleText(), frc);
		int width = 5 + (int) bounds.getWidth() + 5;
		int height = 23;
		if ((paintIconWhenInactive || isSelected) && dockable.getTitleIcon() != null)
			width += dockable.getTitleIcon().getIconWidth() + 5;
		
		if( buttons != null ){
			Dimension preferred = buttons.getPreferredSize();
			width += preferred.width;
			height = Math.max( height, preferred.height );
		}
		
		return new Dimension(width, height);
	}
	
	@Override
	public void doLayout(){
		if( buttons != null ){
			FontRenderContext frc = new FontRenderContext(null, false, false);
			Rectangle2D bounds = UIManager.getFont("Label.font").getStringBounds(dockable.getTitleText(), frc);
			int x = 5 + (int) bounds.getWidth() + 5;
			if ((paintIconWhenInactive || isSelected) && dockable.getTitleIcon() != null)
				x += dockable.getTitleIcon().getIconWidth() + 5;
			
			if( isSelected )
				x += 5;
			
			Dimension preferred = buttons.getPreferredSize();
			int width = Math.min( preferred.width, getWidth()-x );
			
			buttons.setBounds( x, 0, width, getHeight() );
		}
	}

	public Border getContentBorder() {
		return contentBorder;
	}
	
	private void updateBorder(){
		Color color2;
		
		Window window = SwingUtilities.getWindowAncestor(comp);
		boolean focusTemporarilyLost = false;
		
		if( window != null ){
			focusTemporarilyLost = !window.isActive();
		}
		
		if( hasFocus && !focusTemporarilyLost ){
			color2 = RexSystemColor.getActiveColorGradient();
		}
		else if (hasFocus && focusTemporarilyLost) {
			color2 = RexSystemColor.getInactiveColor();
		}
		else{
			color2 = RexSystemColor.getInactiveColorGradient();
		}
		
		// set border around tab content
		if (!color2.equals(contentBorder.getMatteColor())) {
			contentBorder = new MatteBorder(2, 2, 2, 2, color2);
			if( comp != null )
				comp.updateContentBorder();
		}
	}

	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		int height = getHeight(), width = getWidth();
		Graphics2D g2d = (Graphics2D) g;
		Color lineColor = RexSystemColor.getBorderColor();
		Color color1, color2, colorText;
		boolean focusTemporarilyLost = KeyboardFocusManager.getCurrentKeyboardFocusManager()
				.getActiveWindow() != SwingUtilities.getWindowAncestor(comp);
		
		if( hasFocus && !focusTemporarilyLost ){
			color1 = RexSystemColor.getActiveColor();
			color2 = RexSystemColor.getActiveColorGradient();
			colorText = RexSystemColor.getActiveTextColor(); 
		}
		else if (hasFocus && focusTemporarilyLost) {
			color1 = RexSystemColor.getInactiveColor();
			color2 = RexSystemColor.getInactiveColor();
			colorText = RexSystemColor.getInactiveTextColor();
		}
		else{
			color1 = RexSystemColor.getInactiveColor();
			color2 = RexSystemColor.getInactiveColorGradient();
			colorText = RexSystemColor.getInactiveTextColor();
		}
		
		GradientPaint selectedGradient = new GradientPaint(0, 0, color1, 0, height, color2);
		
		g.setColor(lineColor);
		if (isSelected) {
			Paint old = g2d.getPaint();
			g2d.setPaint(selectedGradient);
			g.fillRect(1, 0, width - 2, height);
			g.drawLine( 0, 1, 0, height );
			g2d.setPaint(old);
			// left
			if (tabIndex != 0) {
				g.drawLine(1, 0, 1, 0);
				g.drawLine(0, 1, 0, height);
			}
			// right
			g.drawLine(width - 2, 0, width - 2, 0);
			g.drawLine(width - 1, 1, width - 1, height);
			// overwrite gradient pixels
			g.setColor(getBackground());
			//g.drawLine(0, 0, 0, 0);
			//g.drawLine(width, 0, width, 0);
		}

		// draw icon
		int iconOffset = 0;
		if (isSelected || paintIconWhenInactive) {
			Icon i = dockable.getTitleIcon();
			if (i != null) {
				i.paintIcon(comp, g, 5, 4);
				iconOffset = i.getIconWidth() + 5;
			}
		}

		// draw separator lines
		if (!isSelected && tabIndex != comp.indexOf(comp.getSelectedTab()) - 1) {
			g.setColor(lineColor);
			g.drawLine(width - 1, 0, width - 1, height);
		}

		// draw text
		g.setColor(colorText);
		g.drawString( dockable.getTitleText(), 5 + iconOffset, height / 2 + g.getFontMetrics().getHeight() / 2 - 2);
	}
	
	private class WindowActiveObserver extends WindowAdapter implements HierarchyListener{
		private Window window;
		
		public void hierarchyChanged( HierarchyEvent e ){
			if( window != null ){
				window.removeWindowListener( this );
				window = null;
			}
			
			window = SwingUtilities.getWindowAncestor( RectGradientPainter.this );
			
			if( window != null ){
				window.addWindowListener( this );
				updateBorder();
				repaint();
			}
		}
		
		@Override
		public void windowActivated( WindowEvent e ){
			updateBorder();
			repaint();
		}
		
		@Override
		public void windowDeactivated( WindowEvent e ){
			updateBorder();
			repaint();
		}
	}
	
	   
    private class Listener implements DockableListener{
        public void titleBound( Dockable dockable, DockTitle title ) {
            // ignore
        }

        public void titleIconChanged( Dockable dockable, Icon oldIcon, Icon newIcon ) {
            repaint();
            revalidate();
        }

        public void titleTextChanged( Dockable dockable, String oldTitle, String newTitle ) {
            repaint();
            revalidate();
        }

        public void titleUnbound( Dockable dockable, DockTitle title ) {
            // ignore
        }       
    }
}
