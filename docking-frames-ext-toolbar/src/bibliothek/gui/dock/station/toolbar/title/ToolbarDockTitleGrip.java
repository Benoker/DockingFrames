package bibliothek.gui.dock.station.toolbar.title;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;

import javax.swing.JComponent;

import bibliothek.gui.Dockable;
import bibliothek.gui.ToolbarExtension;
import bibliothek.gui.dock.action.DockAction;
import bibliothek.gui.dock.themes.basic.action.BasicTitleViewItem;
import bibliothek.gui.dock.title.AbstractDockTitle;
import bibliothek.gui.dock.title.DockTitle;
import bibliothek.gui.dock.title.DockTitleFactory;
import bibliothek.gui.dock.title.DockTitleRequest;
import bibliothek.gui.dock.title.DockTitleVersion;

/**
 * A simplistic implementation of a {@link DockTitle}. This particular
 * implementation shows a grip.
 * 
 * @author Herve Guillaume
 */
public class ToolbarDockTitleGrip extends AbstractDockTitle{

	private final Color color;

	/**
	 * Creates a new factory that creates new {@link ToolbarDockTitleGrip}s.
	 * 
	 * @param color
	 *            the color of the title
	 * @return the new factory
	 */
	public static DockTitleFactory createFactory( final Color color ){
		return new DockTitleFactory(){
			@Override
			public void uninstall( DockTitleRequest request ){
				// ignore
			}

			@Override
			public void request( DockTitleRequest request ){
				request.answer(new ToolbarDockTitleGrip(request.getVersion(),
						request.getTarget(), color));
			}

			@Override
			public void install( DockTitleRequest request ){
				// ignore
			}
		};
	}

	public ToolbarDockTitleGrip( DockTitleVersion origin, Dockable dockable,
			Color color ){
		super(dockable, origin, true);
		this.color = color;
	}

	@Override
	protected BasicTitleViewItem<JComponent> createItemFor( DockAction action,
			Dockable dockable ){
		return dockable.getController().getActionViewConverter()
				.createView(action, ToolbarExtension.TOOLBAR_TITLE, dockable);
	}

	@Override
	public Dimension getPreferredSize(){
		final Dimension size = super.getPreferredSize();
		return new Dimension(Math.max(5, size.width), Math.max(5, size.height));
	}

	@Override
	public void setActive( boolean active ){
		super.setActive(active);
		repaint();
	}

	@Override
	public void paintBackground( Graphics g, JComponent component ){
		g.setColor(color);
		g.fillRect(0, 0, getWidth(), getHeight());

		if (isActive()){
			g.setColor(Color.GREEN);
			g.fillRect(0, 0, getWidth(), getHeight());
		}
	}

	@Override
	protected void paintComponent( Graphics g ){
		g.setColor(Color.darkGray);
		final Color shadow = Color.gray;
		final Color clearColor = Color.white;
		final int lineOffset = 5;
		final int headerOffset = 3;
		if (getOrientation().isHorizontal()){
			// Draw a horizontal handle.
			final int width = getSize().width;

			// Draw the light line.
			g.setColor(clearColor);
			g.drawLine(lineOffset, headerOffset, width - lineOffset,
					headerOffset);
			g.drawLine(lineOffset, headerOffset + 1, width - lineOffset,
					headerOffset + 1);

			// Draw the shadow.
			g.setColor(shadow);
			g.drawLine((width - lineOffset) + 1, headerOffset,
					(width - lineOffset) + 1, headerOffset + 2);
			g.drawLine(lineOffset, headerOffset + 2, width - lineOffset,
					headerOffset + 2);

		} else{
			// Draw a vertical handle.
			final int height = getSize().height;

			// Draw the light line.
			g.setColor(clearColor);
			g.drawLine(headerOffset, lineOffset, headerOffset, height
					- lineOffset);
			g.drawLine(headerOffset + 1, lineOffset, headerOffset + 1, height
					- lineOffset);

			// Draw the shadow.
			g.setColor(shadow);
			g.drawLine(headerOffset, (height - lineOffset) + 1,
					headerOffset + 2, (height - lineOffset) + 1);
			g.drawLine(headerOffset + 2, lineOffset, headerOffset + 2, height
					- lineOffset);
		}
	}

}
