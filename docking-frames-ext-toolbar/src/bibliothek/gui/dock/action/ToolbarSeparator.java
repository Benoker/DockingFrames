package bibliothek.gui.dock.action;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;

import javax.swing.JComponent;

import bibliothek.gui.dock.action.actions.SeparatorAction;
import bibliothek.gui.dock.themes.basic.action.BasicTitleViewItem;
import bibliothek.gui.dock.title.DockTitle.Orientation;

/**
 * A {@link ToolbarSeparator} draws a very thin line.
 * @author Benjamin Sigg
 */
public class ToolbarSeparator extends JComponent implements BasicTitleViewItem<JComponent> {
	private SeparatorAction action;
	private Orientation orientation = Orientation.NORTH_SIDED;

	public ToolbarSeparator( SeparatorAction action ){
		this.action = action;
		setOpaque( false );
		setFocusable( false );
	}

	@Override
	public void bind(){
		// ignore
	}

	@Override
	public void unbind(){
		// ignore
	}

	@Override
	public JComponent getItem(){
		return this;
	}

	@Override
	public DockAction getAction(){
		return action;
	}

	@Override
	public void setOrientation( Orientation orientation ){
		this.orientation = orientation;
	}

	public Dimension getPreferredSize(){
		if( isPreferredSizeSet() ) {
			return super.getPreferredSize();
		}
		return new Dimension( 1, 1 );
	}

	@Override
	public Dimension getMinimumSize(){
		return getPreferredSize();
	}

	@Override
	protected void paintComponent( Graphics g ){
		g.setColor( Color.WHITE );
		if( orientation.isHorizontal() ) {
			g.drawLine( 0, 3, 0, getHeight() - 4 );
		}
		else {
			g.drawLine( 3, 0, getWidth() - 4, 0 );
		}
	}
}
