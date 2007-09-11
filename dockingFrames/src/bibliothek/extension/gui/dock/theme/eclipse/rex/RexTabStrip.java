package bibliothek.extension.gui.dock.theme.eclipse.rex;

import java.awt.FlowLayout;
import java.awt.Graphics;

import javax.swing.JComponent;

public class RexTabStrip extends JComponent{
	private RexTabbedComponent tabbedComponent;
	
	public RexTabStrip( RexTabbedComponent component ) {
		setLayout( new FlowLayout( FlowLayout.LEFT, 0, 0 ));
		setFocusable(false);
	}

	@Override
	protected void paintComponent( Graphics g ){
		super.paintComponent( g );
		
		g.setClip(0, 0, getWidth(), getHeight());
		tabbedComponent.getTabPainter().paintTabStrip( tabbedComponent, g );
	}
}
