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

package bibliothek.gui.dock.station.flap;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Container;
import java.awt.Cursor;
import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.Graphics;
import java.awt.Insets;
import java.awt.LayoutManager;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Window;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JRootPane;
import javax.swing.SwingUtilities;
import javax.swing.border.BevelBorder;

import bibliothek.gui.DockController;
import bibliothek.gui.DockStation;
import bibliothek.gui.Dockable;
import bibliothek.gui.dock.FlapDockStation;
import bibliothek.gui.dock.FlapDockStation.Direction;
import bibliothek.gui.dock.security.SecureContainer;
import bibliothek.gui.dock.station.DockableDisplayer;
import bibliothek.gui.dock.station.DockableDisplayerListener;
import bibliothek.gui.dock.station.StationChildHandle;
import bibliothek.gui.dock.station.StationPaint;
import bibliothek.gui.dock.themes.ThemeManager;
import bibliothek.gui.dock.themes.border.BorderForwarder;
import bibliothek.gui.dock.title.DockTitle;
import bibliothek.gui.dock.title.DockTitleVersion;
import bibliothek.gui.dock.util.BackgroundAlgorithm;
import bibliothek.gui.dock.util.BackgroundPanel;
import bibliothek.gui.dock.util.Transparency;

/**
 * This window pops up if the user presses one of the buttons of a 
 * {@link FlapDockStation}. The window shows one {@link Dockable}
 */
public class DefaultFlapWindow implements FlapWindow, MouseListener, MouseMotionListener {
	/** the element that is shown on this window */
	private StationChildHandle dockable;

	/** a listener for the current {@link DockableDisplayer} */
	private DockableDisplayerListener displayerListener = new DockableDisplayerListener(){
		public void discard( DockableDisplayer displayer ){
			discardDisplayer();
		}
		public void moveableElementChanged( DockableDisplayer displayer ){
			// ignore
		}
	};
	
	/** the border of the {@link #contentPane} */
	private BorderForwarder contentBorder;

	/** <code>true</code> if the mouse is currently pressed */
	private boolean pressed;

	/** The owner of this window */
	private FlapDockStation station;
	/** The buttons on the station */
	private ButtonPane buttonPane;
	/** Information where the user will drop or move a {@link Dockable} */
	private FlapDropInfo dropInfo;
	/** Information of an ongoing drag and drop operation that is removing the child of this window */
	private boolean removal;

	/** the panel onto which {@link #dockable} is put */
	private JComponent contentPane;

	/** the window on which this {@link DefaultFlapWindow} is shown */
	private Parent window;
	
	/** the parent of the {@link #contentPane} */
	private SecureContainer contentContainer;

	/** the background algorithm */
	private Background background = new Background();
	
	/**
	 * Constructs a new window.
	 * @param station the station which manages this window
	 * @param buttonPane the buttons on the station
	 * @param window the window on which to paint this {@link DefaultFlapWindow}
	 */
	public DefaultFlapWindow( FlapDockStation station, ButtonPane buttonPane, Parent window ){
		this.station = station;
		this.buttonPane = buttonPane;
		this.window = window;

		init();
	}

	private void init(){
		contentContainer = new SecureContainer(){
			@Override
			protected void paintOverlay( Graphics g ){
				if( removal || (dropInfo != null && dropInfo.getCombineTarget() != null) ) {
					Rectangle bounds = new Rectangle(0, 0, getWidth(), getHeight());
					StationPaint paint = DefaultFlapWindow.this.station.getPaint().get();
					if( paint != null ){
						if( dropInfo != null && dropInfo.getCombineTarget() != null ){
							dropInfo.getCombineTarget().paint( g, contentContainer, paint, bounds, bounds );
						}
						else if( removal ){
							paint.drawRemoval( g, getStation(), bounds, bounds );
						}
					}
				}
			}
		};
		
		BackgroundPanel content = new BackgroundPanel( Transparency.SOLID ){
			@Override
			protected void configure( Transparency transparency ){
				// no support for transparency as this is a root component and can never be transparent
			}
			@Override
			protected void setupRenderingHints( Graphics g ) {
				// ignore
			}
		};
		content.setBackground( background );
		contentContainer.getBasePane().setLayout( new BorderLayout() );
		contentContainer.getBasePane().add( content, BorderLayout.CENTER );
		contentContainer.setContentPane( content );
		
		window.setContentPane(contentContainer);
		contentPane = contentContainer.getContentPane();
		contentPane.setOpaque( false );

		contentBorder = new WindowBorder( contentPane );
		contentBorder.setBorder( BorderFactory.createBevelBorder(BevelBorder.RAISED) );
		contentPane.addMouseListener(this);
		contentPane.addMouseMotionListener(this);

		contentPane.setLayout(new LayoutManager(){
			public void addLayoutComponent( String name, Component comp ){
				// do nothing
			}

			public void removeLayoutComponent( Component comp ){
				// do nothing
			}

			public Dimension preferredLayoutSize( Container parent ){
				DockableDisplayer displayer = getDisplayer();

				if( displayer == null )
					return new Dimension(100, 100);

				return displayer.getComponent().getPreferredSize();
			}

			public Dimension minimumLayoutSize( Container parent ){
				DockableDisplayer displayer = getDisplayer();

				if( displayer == null )
					return new Dimension(100, 100);

				return displayer.getComponent().getMinimumSize();
			}

			public void layoutContainer( Container parent ){
				DockableDisplayer displayer = getDisplayer();

				if( displayer != null ) {
					Insets insets = parent.getInsets();
					insets = new Insets(insets.top, insets.left, insets.bottom, insets.right);

					if( station.getDirection() == Direction.SOUTH )
						insets.bottom += station.getWindowBorder();
					else if( station.getDirection() == Direction.NORTH )
						insets.top += station.getWindowBorder();
					else if( station.getDirection() == Direction.EAST )
						insets.right += station.getWindowBorder();
					else
						insets.left += station.getWindowBorder();

					displayer.getComponent().setBounds(insets.left, insets.top, parent.getWidth() - insets.left - insets.right,
							parent.getHeight() - insets.top - insets.bottom);
				}
			}
		});

		window.asComponent().addComponentListener(new ComponentListener(){
			public void componentHidden( ComponentEvent e ){
				// ignore
			}

			public void componentMoved( ComponentEvent e ){
				// ignore				
			}

			public void componentResized( ComponentEvent e ){
				// ignore
			}

			public void componentShown( ComponentEvent e ){
				if( !DefaultFlapWindow.this.station.isFlapWindow(DefaultFlapWindow.this) || getDockable() == null ) {
					// This window should not be visible if it is not used
					// by its former owner
					SwingUtilities.invokeLater(new Runnable(){
						public void run(){
							destroy();
						}
					});
				}
			}
		});
	}
	
	public void setWindowVisible( boolean flag ){
		if( flag ) {
			// Actually this should not be necessary and only
			// prevents some strange bug where the size gets wrong,
			// the origin of the bug remains a mystery.
			updateBounds();
		}
		window.setVisible( flag );
	}
	
	public boolean isWindowVisible(){
		return window != null && window.isVisible();
	}

	public Rectangle getWindowBounds(){
		return window.asComponent().getBounds();
	}
	
	public void destroy(){
		if( window != null ){
			setController( null );
			setDockable( null );
			window.destroy();
			window = null;
		}
	}
	
	public void repaint(){
		if( window != null ){
			window.asComponent().repaint();
		}
	}
	
	public Component getComponent() {
		if( window == null ){
			return null;
		}
		return window.asComponent();
	}

	/**
	 * Tells whether this window is still valid, e.g whether the window can be shown 
	 * in front of its station.
	 * @return whether this window is still valid
	 */
	public boolean isWindowValid(){
		return window != null && window.isParentValid();
	}

	public boolean containsScreenPoint( Point point ){
		point = new Point(point);
		Component parent = window.asComponent();
		SwingUtilities.convertPointFromScreen(point, parent);
		return parent.contains(point);
	}

	/**
	 * Gets the station for which this window is shown.
	 * @return the owner of this window
	 */
	public FlapDockStation getStation(){
		return station;
	}

	/**
	 * Sets information where a {@link Dockable} will be dropped.
	 * @param dropInfo the information or <code>null</code>
	 */
	public void setDropInfo( FlapDropInfo dropInfo ){
		this.dropInfo = dropInfo;
		repaint();
	}

	public void setRemoval( boolean removal ){
		this.removal = removal;
		repaint();
	}
	
	/**
	 * Sets the title which should be displayed.
	 * @param title the title or <code>null</code>
	 */
	public void setDockTitle( DockTitleVersion title ){
		if( dockable != null ) {
			dockable.setTitleRequest(title);
		}
	}

	/**
	 * Gets the title which is currently displayed
	 * @return the title or <code>null</code>
	 */
	public DockTitle getDockTitle(){
		if( dockable == null )
			return null;
		return dockable.getTitle();
	}

	/**
	 * Gets the {@link Dockable} which is shown on this window.
	 * @return The {@link Dockable} or <code>null</code>
	 */
	public Dockable getDockable(){
		if( dockable == null )
			return null;
		return dockable.getDockable();
	}

	/**
	 * Gets the displayer used to show a {@link Dockable}.
	 * @return the displayer, might be <code>null</code>
	 */
	public DockableDisplayer getDisplayer(){
		if( dockable == null )
			return null;
		return dockable.getDisplayer();
	}

	/**
	 * Sets the {@link Dockable} which will be shown on this window.
	 * @param dockable The <code>Dockable</code> or <code>null</code>
	 */
	public void setDockable( Dockable dockable ){
		Container content = getDisplayerParent();

		if( this.dockable != null ) {
			DockableDisplayer displayer = getDisplayer();
			displayer.removeDockableDisplayerListener(displayerListener);
			content.remove(displayer.getComponent());
			this.dockable.destroy();
			this.dockable = null;
		}

		if( dockable != null ) {
			this.dockable = new StationChildHandle(station, station.getDisplayers(), dockable, station.getTitleVersion());
			this.dockable.updateDisplayer();

			DockableDisplayer displayer = getDisplayer();
			displayer.addDockableDisplayerListener(displayerListener);
			content.add(displayer.getComponent());
		}
	}

	/**
	 * Replaces the current {@link DockableDisplayer} with a new instance.
	 */
	protected void discardDisplayer(){
		if( dockable != null ) {
			DockableDisplayer displayer = dockable.getDisplayer();
			displayer.removeDockableDisplayerListener(displayerListener);
			contentPane.remove(displayer.getComponent());

			dockable.updateDisplayer();

			displayer = dockable.getDisplayer();
			displayer.addDockableDisplayerListener(displayerListener);
			contentPane.add(displayer.getComponent());

			updateBounds();
		}
	}

	/**
	 * Gets the container that will become the parent of a {@link DockableDisplayer}.
	 * @return the parent
	 */
	protected Container getDisplayerParent(){
		return contentPane;
	}
	
	public void setController( DockController controller ){
		background.setController( controller );
		contentContainer.setController( controller );
		contentBorder.setController( controller );
	}

	/**
	 * Makes a guess how big the insets around the current {@link Dockable}
	 * of this window are.
	 * @return a guess of the insets
	 */
	public Insets getDockableInsets(){
		DockableDisplayer displayer = dockable.getDisplayer();

		Insets insets = displayer.getDockableInsets();
		displayer.getComponent().getBounds();

		Component parent = window.asComponent();
		
		Point zero = new Point(0, 0);
		zero = SwingUtilities.convertPoint(displayer.getComponent(), zero, parent);

		int deltaX = zero.x;
		int deltaY = zero.y;
		int deltaW = parent.getWidth() - displayer.getComponent().getWidth();
		int deltaH = parent.getHeight() - displayer.getComponent().getHeight();

		insets.left += deltaX;
		insets.top += deltaY;
		insets.right += deltaW - deltaX;
		insets.bottom += deltaH - deltaY;

		return insets;
	}

	/**
	 * Recalculates the size and the location of this window.
	 */
	public void updateBounds(){
		DockableDisplayer displayer = getDisplayer();
		Dockable dockable = displayer == null ? null : displayer.getDockable();
		if( dockable != null ) {
			window.asComponent().validate();
			Point location;
			Dimension size;
			FlapDockStation.Direction direction = station.getDirection();
			int windowSize = station.getWindowSize(dockable);
			Rectangle bounds = station.getExpansionBounds();
			Insets insets = getDockableInsets();

			if( direction == Direction.SOUTH ) {
				windowSize += insets.top + insets.bottom;
				location = new Point(bounds.x, bounds.height);
				size = new Dimension(bounds.width, windowSize);
			}
			else if( direction == Direction.NORTH ) {
				windowSize += insets.top + insets.bottom;
				location = new Point(bounds.x, -windowSize);
				size = new Dimension(bounds.width, windowSize);
			}
			else if( direction == Direction.WEST ) {
				windowSize += insets.left + insets.right;
				location = new Point(-windowSize, bounds.y);
				size = new Dimension(windowSize, bounds.height);
			}
			else {
				windowSize += insets.left + insets.right;
				location = new Point(bounds.width, bounds.y);
				size = new Dimension(windowSize, bounds.height);
			}

			SwingUtilities.convertPointToScreen(location, buttonPane);
			window.setParentLocation(location);
			window.setSize(size);
			window.asComponent().validate();
		}
	}

	public void mouseExited( MouseEvent e ){
		if( !pressed )
			window.asComponent().setCursor(Cursor.getDefaultCursor());
	}

	public void mouseEntered( MouseEvent e ){
		if( station.getDirection() == Direction.SOUTH )
			window.asComponent().setCursor(Cursor.getPredefinedCursor(Cursor.S_RESIZE_CURSOR));
		else if( station.getDirection() == Direction.NORTH )
			window.asComponent().setCursor(Cursor.getPredefinedCursor(Cursor.N_RESIZE_CURSOR));
		else if( station.getDirection() == Direction.EAST )
			window.asComponent().setCursor(Cursor.getPredefinedCursor(Cursor.E_RESIZE_CURSOR));
		else
			window.asComponent().setCursor(Cursor.getPredefinedCursor(Cursor.W_RESIZE_CURSOR));
	}

	public void mousePressed( MouseEvent e ){
		pressed = true;
	}

	public void mouseReleased( MouseEvent e ){
		pressed = false;
	}

	public void mouseDragged( MouseEvent e ){
		if( pressed ) {
			DockableDisplayer displayer = getDisplayer();
			Dockable dockable = displayer == null ? null : displayer.getDockable();
			if( dockable != null ) {
				Point mouse = new Point(e.getX(), e.getY());
				SwingUtilities.convertPointToScreen(mouse, e.getComponent());

				Component flap = station.getComponent();

				Point zero = new Point(0, 0);
				SwingUtilities.convertPointToScreen(zero, flap);

				int size = 0;

				if( station.getDirection() == Direction.SOUTH )
					size = mouse.y - zero.y - flap.getHeight();
				else if( station.getDirection() == Direction.NORTH )
					size = zero.y - mouse.y;
				else if( station.getDirection() == Direction.EAST )
					size = mouse.x - zero.x - flap.getWidth();
				else
					size = zero.x - mouse.x;

				size = Math.max(size, station.getWindowMinSize());
				Insets insets = getDockableInsets();
				if( station.getDirection() == Direction.NORTH || station.getDirection() == Direction.SOUTH )
					size -= insets.top + insets.bottom;
				else
					size -= insets.left + insets.right;

				if( size > 0 ) {
					station.setWindowSize(dockable, size);
				}
			}
		}
	}

	public void mouseClicked( MouseEvent e ){
		// do nothing
	}

	public void mouseMoved( MouseEvent e ){
		// do nothing
	}
	
    /**
     * Represents the border of this window
     * @author Benjamin Sigg
     */
    private class WindowBorder extends BorderForwarder implements FlapWindowBorder{
    	public WindowBorder( JComponent target ){
    		super( FlapWindowBorder.KIND, ThemeManager.BORDER_MODIFIER + ".flap.window", target );
    	}
    	
    	public FlapWindow getWindow(){
    		return DefaultFlapWindow.this;
    	}
    	
    	public FlapDockStation getStation(){
    		return DefaultFlapWindow.this.getStation();
    	}
    }

	
	/**
	 * The background algorithm of this window
	 * @author Benjamin Sigg
	 */
	private class Background extends BackgroundAlgorithm implements FlapWindowBackgroundComponent{
		/**
		 * Creates a new algorithm
		 */
		public Background(){
			super( FlapWindowBackgroundComponent.KIND, ThemeManager.BACKGROUND_PAINT + ".station.flap.window" );
		}
		
		public FlapWindow getWindow(){
			return DefaultFlapWindow.this;
		}

		public DockStation getStation(){
			return station;
		}

		public Component getComponent(){
			return window.asComponent();
		}
	}

	/**
	 * The parent container of a {@link DefaultFlapWindow}.
	 * @author Benjamin Sigg
	 */
	public static interface Parent {
		/**
		 * Tells whether this window is still valid, e.g whether the window can be shown 
		 * in front of its station.
		 * @return whether this parent can still be used
		 */
		public boolean isParentValid();

		/**
		 * Returns <code>this</code> or the representation of <code>this</code>
		 * as {@link Component}.
		 * @return the component that shows a {@link DefaultFlapWindow}
		 */
		public Component asComponent();

		/**
		 * Sets the location of this container on the screen.
		 * @param location the new locations in screen coordinates
		 */
		public void setParentLocation( Point location );

		/**
		 * Sets the size of this container.
		 * @param size the new size
		 */
		public void setSize( Dimension size );

		/**
		 * Tells whether the user can see this container or not.
		 * @return <code>true</code> if this container is visible
		 */
		public boolean isVisible();

		/**
		 * Sets the visibility of this container.
		 * @param flag the visibility
		 */
		public void setVisible( boolean flag );

		/**
		 * Tells this parent what component to paint.
		 * @param content the panel to show
		 */
		public void setContentPane( Container content );

		/**
		 * Gets the component that is painted by this parent.
		 * @return the component
		 */
		public Container getContentPane();
		
		/**
		 * Informs this parent that is it no longer needed
		 */
		public void destroy();
	}

	/**
	 * A parent of a {@link DefaultFlapWindow} that is a {@link JDialog}.
	 * @author Benjamin Sigg
	 */
	public static class DialogParent extends JDialog implements Parent {
		/** the station for which this dialog is used */
		private FlapDockStation station;
		
		/**
		 * Creates a new dialog.
		 * @param owner the owner of this dialog
		 * @param station the station for which this dialog is used
		 */
		public DialogParent( Frame owner, FlapDockStation station ){
			super(owner, false);
			setUndecorated(true);
			getRootPane().setWindowDecorationStyle(JRootPane.NONE);
			this.station = station;
		}
		
		/**
		 * Creates a new dialog.
		 * @param owner the owner of this dialog
		 * @param station the station for which this dialog is used
		 */
		public DialogParent( Dialog owner, FlapDockStation station ){
			super(owner, false);
			setUndecorated(true);
			getRootPane().setWindowDecorationStyle(JRootPane.NONE);
			this.station = station;
		}
		
		public Component asComponent(){
			return this;
		}

		/**
		 * Tells whether this window is still valid, e.g whether the window can be shown 
		 * in front of its station.
		 */
		public boolean isParentValid(){
			Window owner = SwingUtilities.getWindowAncestor(station.getComponent());
			return getOwner() == owner;
		}
		
		public void setParentLocation( Point location ){
			setLocation( location );
		}
		
		public void destroy(){
			dispose();	
		}
	}
}
