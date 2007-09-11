package bibliothek.extension.gui.dock.theme.eclipse.rex.tab;

import java.awt.Component;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

import javax.swing.border.Border;

public interface TabComponent {
	public Component getComponent();
	
	public void setSelected( boolean selected );
	
	public void setFocused( boolean focused );
	
	public void setIndex( int index );
	
	public void setPaintIconWhenInactive( boolean paint );
	
	/**
	 * Called when a property of the tab has been changed and this 
	 * component has to reevaluate its content.
	 */
	public void update();

	public void addMouseListener( MouseListener listener );
	
	public void addMouseMotionListener( MouseMotionListener listener );
	
	public void removeMouseListener( MouseListener listener );
	
	public void removeMouseMotionListener( MouseMotionListener listener );

	public Border getContentBorder();
}
