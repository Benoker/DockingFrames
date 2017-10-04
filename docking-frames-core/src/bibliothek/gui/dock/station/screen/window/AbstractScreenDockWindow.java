/*
 * Bibliothek - DockingFrames
 * Library built on Java/Swing, allows the user to "drag and drop"
 * panels containing any Swing-Component the developer likes to add.
 * 
 * Copyright (C) 2008 Benjamin Sigg
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
package bibliothek.gui.dock.station.screen.window;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Container;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.LayoutManager;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Window;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

import javax.swing.Icon;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.SwingUtilities;

import bibliothek.gui.DockController;
import bibliothek.gui.Dockable;
import bibliothek.gui.dock.ScreenDockStation;
import bibliothek.gui.dock.event.DockableAdapter;
import bibliothek.gui.dock.event.DockableListener;
import bibliothek.gui.dock.security.SecureContainer;
import bibliothek.gui.dock.station.DockableDisplayer;
import bibliothek.gui.dock.station.StationPaint;
import bibliothek.gui.dock.station.screen.ScreenDockWindow;
import bibliothek.gui.dock.station.screen.ScreenDockWindowListener;
import bibliothek.gui.dock.station.screen.magnet.MagnetizedOperation;
import bibliothek.gui.dock.station.screen.window.ScreenDockWindowBorder.Position;
import bibliothek.gui.dock.station.support.CombinerTarget;
import bibliothek.gui.dock.themes.ThemeManager;
import bibliothek.gui.dock.themes.border.BorderForwarder;
import bibliothek.gui.dock.util.BackgroundAlgorithm;
import bibliothek.gui.dock.util.BackgroundPanel;
import bibliothek.gui.dock.util.Transparency;
import bibliothek.util.Workarounds;

/**
 * This abstract implementation of {@link ScreenDockWindow} uses a {@link DockableDisplayer}
 * to show the {@link Dockable}. It can operate with any window. Clients need to
 * call {@link #init(Component, Container, WindowConfiguration, boolean)} to put this object onto some
 * {@link Container}.
 * @author Benjamin Sigg
 */
public abstract class AbstractScreenDockWindow extends DisplayerScreenDockWindow{
    /** the component which represents the window */
    private Component window;

    /** the elements to display */
    private DockableDisplayer displayer;

    /** the parent of {@link #displayer}, used to paint */
    private SecureContainer content;

    /** how to paint a combination */
    private CombinerTarget combination;

    /** whether a drag and drop operation currently removes the child of this window */
    private boolean removal = false;
    
    /** the explicit set icon */
    private Icon titleIcon = null;
    
    /** the explicit set text */
    private String titleText = null;
    
    /** the background algorithm for this window */
    private BackgroundAlgorithm background;
    
    /** the panel which paints the background */
    private BackgroundPanel contentBackground;
    
    /** the default border of this window */
    private ScreenDockWindowBorder border;
    
    /** the current modifier of the border */
    private WindowBorder borderModifier;
    
    /** responsible for updating the shape of this window */
    private ScreenWindowShapeAdapter shape;
    
    /** a listener added to the <code>Dockable</code> of this window, updates icon and title text */
    private DockableListener listener = new DockableAdapter(){
        @Override
        public void titleIconChanged( Dockable dockable, Icon oldIcon, Icon newIcon ) {
            updateTitleIcon();
        }
        
        @Override
        public void titleTextChanged( Dockable dockable, String oldTitle, String newTitle ) {
            updateTitleText();
        }
    };
    
    /** a listener added to this window, will inform other listeners about a changed fullscreen mode when this window is resized */ 
    private ScreenDockWindowListener windowListener = new ScreenDockWindowListener() {
    	/** the last remembered state */
    	private boolean remembered = false;
    	
    	public void visibilityChanged( ScreenDockWindow window ) {
			if( isFullscreen() != remembered ){
				fireFullscreenChanged();
			}
		}
		
		public void shapeChanged( ScreenDockWindow window ) {
			if( isFullscreen() != remembered ){
				fireFullscreenChanged();
			}
		}
		
		public void fullscreenStateChanged( ScreenDockWindow window ) {
			remembered = isFullscreen();
		}
		
		public void windowClosing( ScreenDockWindow window ){
			// ignore
		}
	};

    /**
     * Creates a new window. Subclasses must call {@link #init(Component, Container, WindowConfiguration, boolean)}
     * when using this constructor.
     * @param station the owner of this window
     * @param configuration the configuration to apply during creation of this window
     */
    protected AbstractScreenDockWindow( ScreenDockStation station, WindowConfiguration configuration ){
        super( station, configuration );
    }
    
    /**
     * Creates a new window. 
     * @param station the owner of this window
     * @param configuration the configuration to apply during creation of this window
     * @param window the root component of this window
     * @param contentParent the container onto which the contents of this window will be put
     * @see #init(Component, Container, WindowConfiguration, boolean)
     */
    public AbstractScreenDockWindow( ScreenDockStation station, WindowConfiguration configuration, Component window, Container contentParent ){
        super( station, configuration );
        init( window, contentParent, configuration, configuration.isResizeable() );
    }

    /**
     * Initializes this window.
     * @param window the component which represents the window. This component
     * will be used when calling methods like {@link #setWindowBounds(Rectangle, Position)}. It
     * is the root of this whole window.
     * @param contentParent the container which will be used as parent for the
     * contents of this window. This method will change the {@link LayoutManager}
     * and add a child to <code>contentParent</code>. This component can be
     * the same as <code>window</code>.
     * @param borderAllowed If <code>true</code> and if {@link WindowConfiguration#isResizeable()}, then a new border is installed
     * for the {@link #getDisplayerParent() displayer parent}, and some {@link MouseListener}s
     * are installed. When the mouse is over the border it will change the cursor
     * and the user can resize or move the window. If <code>false</code>
     * nothing happens and the resizing system has to be implemented by the
     * subclass.
     */
    protected void init( Component window, Container contentParent, WindowConfiguration configuration, boolean borderAllowed ){
        if( window == null )
            throw new IllegalArgumentException( "window must not be null" );
        
        if( contentParent == null )
            throw new IllegalArgumentException( "contentParent must not be null" );
        
        this.window = window;
        
        content = createContent( configuration );
        content.setController( getController() );
        if( configuration.isResizeable() ){
        	contentParent.setLayout( new GridLayout( 1, 1 ) );
        }
        else{
        	contentParent.setLayout( new ResizingLayoutManager( this, window ) );
        }
        contentParent.add( content );

        Container parent = getDisplayerParent();
        parent.setLayout( new GridLayout( 1, 1 ));

        if( (configuration.isResizeable() || configuration.isMoveOnBorder()) && borderAllowed ){
            if( parent instanceof JComponent && configuration.getBorderFactory() != null ){
            	border = configuration.getBorderFactory().create( this, (JComponent)parent );
            	border.setController( getController() );
            	borderModifier = new WindowBorder( (JComponent)parent );
            	borderModifier.setBorder( border );
            	borderModifier.setController( getController() );
            }

            Listener listener = new Listener();
            parent.addMouseListener( listener );
            parent.addMouseMotionListener( listener );
            parent.addComponentListener( listener );
        }
        
        window.addComponentListener( new ComponentAdapter() {
        	@Override
        	public void componentResized( ComponentEvent e ) {
	        	fireShapeChanged();
        	}
        	
        	@Override
        	public void componentMoved( ComponentEvent e ) {
        		fireShapeChanged();
        	}
		});
        
        addScreenDockWindowListener( windowListener );
    }
    
    @Override
    protected WindowMover createTitleMover(){
    	return new WindowMover( this ){
    		@Override
    		protected void convertPointToScreen( Point point, Component component ){
    			AbstractScreenDockWindow.this.convertPointToScreen( point, component );
    		}
    	};
    }
    
    @Override
    public void setController( DockController controller ){
    	super.setController( controller );
    	content.setController( controller );
    	if( border != null ){
    		border.setController( controller );
    	}
    	if( borderModifier != null ){
    		borderModifier.setController( controller );
    	}
    }
    
    /**
     * Sets the algorithm which is responsible for updating the shape of this window. The algorithm remains
     * active until {@link #destroy()} is called.<br>
     * Note: This method does nothing if {@link Workarounds#supportsTransparency(Window)} returns <code>false</code>.
     * @param window the {@link Window} representing <code>this</code>, it should be {@link #getWindowComponent()}
     * @param shape the algorithm defining the new shape
     */
    protected void setShape( Window window, ScreenWindowShape shape ){
    	if( Workarounds.getDefault().supportsTransparency( window )){
    		if( this.shape != null ){
    			this.shape.disable();
    			this.shape = null;
    		}
    		if( shape != null ){
    			this.shape = new ScreenWindowShapeAdapter( this, window );
    			this.shape.setShape( shape );
    		}
    	}
    }

    @Override
    protected Component getWindowComponent() {
        return window;
    }
    
    @Override
    protected void setBackground( BackgroundAlgorithm background ){
	    this.background = background;
	    if( contentBackground != null ){
	    	contentBackground.setBackground( background );
	    }
	    window.repaint();
    }

    @Override
    public void setDockable( Dockable dockable ) {
        Dockable old = getDockable();
        if( old != null ){
            old.removeDockableListener( listener );
        }
        
        super.setDockable( dockable );
        
        if( dockable != null ){
            dockable.addDockableListener( listener );
        }
        
        updateTitleIcon();
        updateTitleText();
    }
    
    @Override
    protected void showDisplayer( DockableDisplayer displayer ) {
        if( this.displayer != displayer ){
            if( this.displayer != null ){
                getDisplayerParent().remove( this.displayer.getComponent() );
            }

            this.displayer = displayer;

            if( displayer != null ){
                getDisplayerParent().add( displayer.getComponent() );
            }
        }

        validate();
    }

    /**
     * Explicitly sets the icon of the title.
     * @param titleIcon the new icon or <code>null</code> to use the
     * {@link Dockable}s icon.
     */
    public void setTitleIcon( Icon titleIcon ) {
        this.titleIcon = titleIcon;
        updateTitleIcon();
    }
    
    /**
     * Gets the icon which should be used in the title.
     * @return the icon
     */
    protected Icon getTitleIcon(){
        if( titleIcon != null )
            return titleIcon;
        
        Dockable dockable = getDockable();
        if( dockable == null )
            return null;
        
        return dockable.getTitleIcon();
    }
    
    /**
     * Called when the icon of the title should be updated.
     * @see #getTitleIcon()
     */
    protected void updateTitleIcon(){
        // nothing to do
    }
    
    /**
     * Explicitly sets the text of the title.
     * @param titleText the new text or <code>null</code> to use
     * the {@link Dockable}s title text.
     */
    public void setTitleText( String titleText ) {
        this.titleText = titleText;
        updateTitleText();
    }
    
    /**
     * Gets the text which should be used in the title.
     * @return the text, might be <code>null</code>
     */
    protected String getTitleText(){
        if( titleText != null )
            return titleText;
        
        Dockable dockable = getDockable();
        if( dockable == null )
            return null;
        
        return dockable.getTitleText();
    }
    
    /**
     * Called when the text of the title should be updated.
     * @see #getTitleText()
     */    
    protected void updateTitleText(){
        // nothing to do
    }
    
    public Rectangle getWindowBounds() {
        return window.getBounds();
    }
    
    public Component getComponent() {
    	return window;
    }

    public void setPaintCombining( CombinerTarget target ){
        this.combination = target; 
        window.repaint();
    }
    
    public void setPaintRemoval( boolean removal ){
    	this.removal = removal;
    	window.repaint();
    }

    public void setVisible( boolean visible ) {
    	if( visible != isVisible() ){
	        window.setVisible( visible );
	        fireVisibilityChanged();
	        checkWindowBoundsAsync();
    	}
    }
    
    public boolean isVisible() {
	    return window.isVisible();
    }
    
    /**
     * Sets the boundaries of this window. If the boundaries are not valid, then this method tries to ensure that
     * the edge or side <i>opposite</i> to <code>position</code> gets at the intended position. This is a convenient
     * method for resizing the window when the mouse is dragging the edge or side at <code>position</code>.
     * @param bounds the new bounds
     * @param position the <i>opposite</i> of the edge or side that is fixed
     */
    public void setWindowBounds( Rectangle bounds, Position position ){
    	Rectangle valid = getStation().getBoundaryRestriction().check( this, bounds );
    	if( valid == null ){
    		setWindowBounds( bounds );
    	}
    	else{
    		switch( position ){
    			case E:
    				bounds = new Rectangle( bounds.x, valid.y, valid.width, valid.height );
    				break;
    			case N:
    				bounds = new Rectangle( valid.x, bounds.y + bounds.height - valid.height, valid.width, valid.height );
    				break;
    			case S:
    				bounds = new Rectangle( valid.x, bounds.y, valid.width, valid.height );
    				break;
    			case W:
    				bounds = new Rectangle( bounds.x + bounds.width - valid.width, bounds.y, valid.width, valid.height );
    				break;
    			case NE:
    				bounds = new Rectangle( bounds.x, bounds.y + bounds.height - valid.height, valid.width, valid.height );
    				break;
    			case NW:
    				bounds = new Rectangle( bounds.x + bounds.width - valid.width, bounds.y + bounds.height - valid.height, valid.width, valid.height );
    				break;
    			case SE:
    				bounds = new Rectangle( bounds.x, bounds.y, valid.width, valid.height );
    				break;
    			case SW:
    				bounds = new Rectangle( bounds.x + bounds.width - valid.width, bounds.y, valid.width, valid.height );
    				break;
    			default:
    				bounds = valid;
    				break;
    		}
    		setWindowBounds( bounds );
    	}
    }
    
    public void setWindowBounds( Rectangle bounds ){
        Rectangle valid = getStation().getBoundaryRestriction().check( this, bounds );

        if( valid != null ){
        	bounds = valid;
        }

        if( !window.getBounds().equals( bounds )){
        	window.setBounds( bounds );
        	invalidate();
        	validate();
        }
    }

    /**
     * Adds an event into the EDT that calls {@link #checkWindowBounds()} at a later time, 
     * the boundaries will only be checked if this window is visible.
     */
    public void checkWindowBoundsAsync(){
    	SwingUtilities.invokeLater( new Runnable() {
			public void run() {
				if( isVisible() ){
					checkWindowBounds();
				}
			}
		});
    }
    
    public void checkWindowBounds() {
        Rectangle valid = getStation().getBoundaryRestriction().check( this );
        if( valid != null ){
            window.setBounds( valid );
        }
    }

    /**
     * Invalidates the layout of this window.
     * @see Component#invalidate()
     */
    public void invalidate(){
        window.invalidate();
    }

    public void validate() {
        window.validate();
    }

    /**
     * Sets the current cursor of this window
     * @param cursor the cursor
     * @see Component#setCursor(Cursor)
     */
    protected void setCursor( Cursor cursor ){
        window.setCursor( cursor );
    }
    
    /**
     * Converts <code>point</code> which is relative to <code>component</code> to a point on the screen.
     * @param point the point to modify
     * @param component specifies the coordinate system
     * @see SwingUtilities#convertPointToScreen(Point, Component)
     */
    protected void convertPointToScreen( Point point, Component component ){
    	SwingUtilities.convertPointToScreen( point, component );
    }

    /**
     * Makes a guess how big the insets of the {@link Dockable} compared to
     * the whole dialog are.
     * @return the insets, only a guess
     */
    public Insets getDockableInsets(){
        Container parent = getDisplayerParent();
        Insets parentInsets = parent.getInsets();
        if( parentInsets == null )
            parentInsets = new Insets( 0, 0, 0, 0 );

        Point zero = new Point( 0, 0 );
        zero = SwingUtilities.convertPoint( parent, zero, window );

        parentInsets.left += zero.x;
        parentInsets.top += zero.y;
        parentInsets.right += window.getWidth() - parent.getWidth() - zero.x;
        parentInsets.bottom += window.getHeight() - parent.getHeight() - zero.y;

        if( displayer == null )
            return parentInsets;

        Insets insets = displayer.getDockableInsets();
        parentInsets.top += insets.top;
        parentInsets.bottom += insets.bottom;
        parentInsets.left += insets.left;
        parentInsets.right += insets.right;

        return parentInsets;
    }

    /**
     * Creates the component that will be used as 
     * {@link JDialog#setContentPane(Container) content-pane}.
     * This method is invoked by the constructor.
     * @param configuration the configuration of this window
     * @return the new content pane
     */
    protected SecureContainer createContent( WindowConfiguration configuration ){
    	if( configuration.isTransparent() ){
    		contentBackground = new BackgroundPanel( Transparency.TRANSPARENT ){
    			@Override
    			protected void configure( Transparency transparency ){
    				setTransparency( transparency );
    			}
    			@Override
    			protected void setupRenderingHints( Graphics g ) {
    				// ignore
    			}
    		};
    	}
    	else{
	    	contentBackground = new BackgroundPanel( Transparency.DEFAULT ){
	    		@Override
	    		protected void configure( Transparency transparency ){
	    			// does not support transparency as this is a root component
	    		}
    			@Override
    			protected void setupRenderingHints( Graphics g ) {
    				// ignore
    			}
	    	};
    	}
    	contentBackground.setBackground( background );
    	
    	SecureContainer panel = new SecureContainer(){
            @Override
            protected void paintOverlay( Graphics g ) {
            	boolean removal = AbstractScreenDockWindow.this.removal;
            	if( isMoveOnTitleGrab() ){
            		removal = false;
            	}
            	
            	if( combination != null || removal ){
                    ScreenDockStation station = getStation();
                    StationPaint paint = station.getPaint().get();
                    if( paint != null ){
	                    Insets insets = getInsets();
	
	                    Rectangle bounds = new Rectangle( 0, 0, getWidth(), getHeight() );
	                    Rectangle insert = new Rectangle( 2*insets.left, 2*insets.top, 
	                            getWidth() - 2*(insets.left+insets.right),
	                            getHeight() - 2*(insets.top+insets.bottom ));
	
	                    if( combination != null ){
	                    	combination.paint( g, contentBackground, paint, bounds, insert );
	                    }
	                    else if( removal ){
	                    	paint.drawRemoval( g, station, bounds, insert );
	                    }
                    }
                }
            }
        };
        
        if( configuration.isTransparent() ){
        	panel.setSolid( false );
        }
        panel.setContentPane( contentBackground );
        panel.getBasePane().setLayout( new BorderLayout(){
        	private Dimension lastMinimumSize;
        	
        	@Override
        	public void layoutContainer( Container target ){
        		Dimension minimumSize = minimumLayoutSize( target );
        		if( lastMinimumSize == null || !lastMinimumSize.equals( minimumSize )){
        			lastMinimumSize = minimumSize;
        			checkWindowBounds();
        		}
        		super.layoutContainer( target );
        	}
        } );
        panel.getBasePane().add( contentBackground, BorderLayout.CENTER );

        return panel;
    }

    /**
     * Gets the container on which the displayer is shown.
     * @return the parent of the displayer
     */
    protected Container getDisplayerParent(){
        return content.getContentPane();
    }

    /**
     * Gets the displayer which is shown on this dialog.
     * @return The displayer, may be <code>null</code>
     * @see #showDisplayer(DockableDisplayer)
     */
    public DockableDisplayer getDisplayer() {
        return displayer;
    }
    
    public void destroy(){
	    if( shape != null ){
	    	shape.disable();
	    }
    }
    
    /**
     * Represents the border of this window
     * @author Benjamin Sigg
     */
    private class WindowBorder extends BorderForwarder implements ScreenDockWindowDockBorder{
    	public WindowBorder( JComponent target ){
    		super( ScreenDockWindowDockBorder.KIND, ThemeManager.BORDER_MODIFIER + ".screen.window", target );
    	}
    	
    	public ScreenDockWindow getWindow(){
    		return AbstractScreenDockWindow.this;
    	}
    }

    private class Listener implements MouseListener, MouseMotionListener, ComponentListener{
        private boolean pressed = false;
        private Position position = Position.NOTHING;

        private Point start;
        private Rectangle bounds;
        private MagnetizedOperation attraction;

        private void updateBorder(){
        	if( border != null ){
        		if( pressed ){
        			border.setMousePressed( position );
        			border.setMouseOver( null );
        		}
        		else{
        			border.setMousePressed( null );
        			border.setMouseOver( position );
        		}
        		
            	border.setCornerSize( corner() );
            	WindowConfiguration configuration = getConfiguration();
            	if( configuration == null ){
            		border.setMoveable( false );
            		border.setResizeable( false );
            		
            		border.setMoveSize( 0 );
            		border.setCornerSize( 0 );
            	}
            	else{
            		border.setMoveable( configuration.isMoveOnBorder() );
            		border.setResizeable( configuration.isResizeable() );
            		
            		if( configuration.isMoveOnBorder() ){
            			border.setMoveSize( getDisplayerParent().getWidth()/3 );	
            		}
            		else{
            			border.setMoveSize( 0 );
            		}
            	}
        	}
        }
        
        public void componentHidden( ComponentEvent e ){
        	// ignore
        }
        
        public void componentMoved( ComponentEvent e ){
        	// ignore
        }
        public void componentResized( ComponentEvent e ){
        	updateBorder();
        }
        public void componentShown( ComponentEvent e ){
	        // ignore	
        }
        
        private int corner(){
        	WindowConfiguration configuration = getConfiguration();
        	if( configuration == null || !configuration.isResizeable() ){
        		return 0;
        	}
        	
            Container component = getDisplayerParent();
            Insets insets = component.getInsets();

        	int corner = Math.max( Math.max( insets.top, insets.bottom ), Math.max( insets.left, insets.right ) ) * 5;
        	corner = Math.max( 25, Math.min( 50, corner ) );
        	
        	corner = Math.min( Math.min( component.getHeight()/2, component.getWidth()/3 ), corner );
        	return corner;
        }
        
        public void mouseClicked( MouseEvent e ) {
            // do nothing
        }

        public void mousePressed( MouseEvent e ) {
            updateCursor( e );
            if( !pressed ){
                if( e.getButton() == MouseEvent.BUTTON1 ){
                    pressed = true;
                    start = e.getPoint();
                    convertPointToScreen( start, e.getComponent() );
                    bounds = getWindowBounds();
                    updateBorder();
                    attraction = getStation().getMagnetController().start( AbstractScreenDockWindow.this );
                }
            }
        }

        public void mouseReleased( MouseEvent e ) {
            if( pressed && e.getButton() == MouseEvent.BUTTON1 ){
                pressed = false;
                updateCursor( e );
                checkWindowBounds();
                invalidate();
                validate();
                attraction.stop();
                attraction = null;
            }
        }

        public void mouseEntered( MouseEvent e ) {
            if( !pressed && e.getButton() == MouseEvent.NOBUTTON ){
                updateCursor( e );
                position = Position.NOTHING;
            }
        }

        public void mouseExited( MouseEvent e ) {
            if( !pressed && e.getButton() == MouseEvent.NOBUTTON ){
                setCursor( Cursor.getDefaultCursor() );
                position = Position.NOTHING;
                updateBorder();
            }
        }

        public void mouseDragged( MouseEvent e ) {
            if( pressed ){
                Point point = e.getPoint();
                convertPointToScreen( point, e.getComponent() );

                int dx = point.x - start.x;
                int dy = point.y - start.y;

                Rectangle bounds = new Rectangle( this.bounds );

                int min = 10;

                if( position == Position.N || position == Position.NE || position == Position.NW ){
                    bounds.height -= dy;
                    bounds.y += dy;

                    if( bounds.height < min ){
                        bounds.y -= min - bounds.height;
                        bounds.height = min;
                    }
                }
                if( position == Position.E || position == Position.NE || position == Position.SE ){
                    bounds.width += dx;
                    if( bounds.width < min )
                        bounds.width = min;
                }
                if( position == Position.S || position == Position.SE || position == Position.SW ){
                    bounds.height += dy;
                    if( bounds.height < min )
                        bounds.height = min;
                }
                if( position == Position.W || position == Position.SW || position == Position.NW ){
                    bounds.width -= dx;
                    bounds.x += dx;

                    if( bounds.width < min ){
                        bounds.x -= min - bounds.width;
                        bounds.width = min;
                    }
                }
                if( position == Position.MOVE ){
                    bounds.x += dx;
                    bounds.y += dy;
                }

                bounds = attraction.attract( bounds );
                
                setWindowBounds( bounds, position );
                updateBorder();
                invalidate();
                validate();
            }
        }

        public void mouseMoved( MouseEvent e ) {
            if( !pressed && e.getButton() == MouseEvent.NOBUTTON )
                updateCursor( e );
        }


        private void updateCursor( MouseEvent e ){
            Container component = getDisplayerParent();
            Insets insets = component.getInsets();

            boolean valid = e.getComponent() == component && e.getY() <= insets.top || e.getY() >= component.getHeight() - insets.bottom ||
            	e.getX() <= insets.left || e.getX() >= component.getWidth() - insets.right;

            if( valid ){
            	WindowConfiguration configuration = getConfiguration();
            	if( configuration.isMoveOnBorder() && !configuration.isResizeable()){
            		setCursor( Cursor.getPredefinedCursor( Cursor.MOVE_CURSOR ));
            		position = Position.MOVE;
            	}
            	else{
	            	int corner = corner();
	            	
	                boolean top = e.getY() <= corner;
	                boolean left = e.getX() <= corner;
	                boolean bottom = e.getY() >= component.getHeight() - corner;
	                boolean right = e.getX() >= component.getWidth() - corner;
	
	                if( top && left ){
	                    setCursor( Cursor.getPredefinedCursor( Cursor.NW_RESIZE_CURSOR ) );
	                    position = Position.NW;
	                }
	                else if( top && right ){
	                    setCursor( Cursor.getPredefinedCursor( Cursor.NE_RESIZE_CURSOR ) );
	                    position = Position.NE;
	                }
	                else if( bottom && right ){
	                    setCursor( Cursor.getPredefinedCursor( Cursor.SE_RESIZE_CURSOR ) );
	                    position = Position.SE;
	                }
	                else if( bottom && left ){
	                    setCursor( Cursor.getPredefinedCursor( Cursor.SW_RESIZE_CURSOR ) );
	                    position = Position.SW;
	                }
	                else if( top ){
	                    int width = component.getWidth();
	                    if( configuration.isMoveOnBorder() && e.getX() > width / 3 && e.getX() < width / 3 * 2 ){
	                        setCursor( Cursor.getPredefinedCursor( Cursor.MOVE_CURSOR ));
	                        position = Position.MOVE;
	                    }
	                    else{
	                        setCursor( Cursor.getPredefinedCursor( Cursor.N_RESIZE_CURSOR ) );
	                        position = Position.N;
	                    }
	                }
	                else if( bottom ){
	                    setCursor( Cursor.getPredefinedCursor( Cursor.S_RESIZE_CURSOR ) );
	                    position = Position.S;
	                }
	                else if( left ){
	                    setCursor( Cursor.getPredefinedCursor( Cursor.W_RESIZE_CURSOR ) );
	                    position = Position.W;
	                }
	                else if( right ){
	                    setCursor( Cursor.getPredefinedCursor( Cursor.E_RESIZE_CURSOR ) );
	                    position = Position.E;
	                }
	                else{
	                    setCursor( Cursor.getDefaultCursor() );
	                    position = Position.NOTHING;
	                }
            	}
            }
            else{
                setCursor( Cursor.getDefaultCursor() );
                position = Position.NOTHING;
            }
            updateBorder();
        }
    }
}
