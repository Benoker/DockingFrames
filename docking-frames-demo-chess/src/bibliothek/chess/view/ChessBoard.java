package bibliothek.chess.view;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.List;

import javax.swing.Icon;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import bibliothek.chess.model.Board;
import bibliothek.chess.model.ChessListener;
import bibliothek.chess.model.Figure;
import bibliothek.chess.model.Player;
import bibliothek.gui.DockController;
import bibliothek.gui.DockStation;
import bibliothek.gui.DockTheme;
import bibliothek.gui.Dockable;
import bibliothek.gui.dock.action.DockActionSource;
import bibliothek.gui.dock.component.DockComponentConfiguration;
import bibliothek.gui.dock.displayer.DisplayerCombinerTarget;
import bibliothek.gui.dock.displayer.DisplayerRequest;
import bibliothek.gui.dock.dockable.DockableStateListener;
import bibliothek.gui.dock.event.DockStationListener;
import bibliothek.gui.dock.layout.DockableProperty;
import bibliothek.gui.dock.layout.location.AsideRequest;
import bibliothek.gui.dock.station.DisplayerCollection;
import bibliothek.gui.dock.station.DisplayerFactory;
import bibliothek.gui.dock.station.DockableDisplayer;
import bibliothek.gui.dock.station.OverpaintablePanel;
import bibliothek.gui.dock.station.PlaceholderMapping;
import bibliothek.gui.dock.station.StationChildHandle;
import bibliothek.gui.dock.station.StationDragOperation;
import bibliothek.gui.dock.station.StationDropItem;
import bibliothek.gui.dock.station.StationDropOperation;
import bibliothek.gui.dock.station.layer.DefaultDropLayer;
import bibliothek.gui.dock.station.layer.DockStationDropLayer;
import bibliothek.gui.dock.station.support.CombinerTarget;
import bibliothek.gui.dock.station.support.PlaceholderMap;
import bibliothek.gui.dock.title.ActivityDockTitleEvent;
import bibliothek.gui.dock.title.DockTitle;
import bibliothek.gui.dock.title.DockTitleRequest;
import bibliothek.gui.dock.title.DockTitleVersion;

/**
 * A {@link DockStation} that shows only {@link ChessFigure ChessFigures} as children.
 * The children are organized in a grid of size 8x8. This grid is colored using
 * a {@link #setLight(Color) light} and a {@link #setDark(Color) dark} color.
 * @author Benjamin Sigg
 */
public class ChessBoard extends OverpaintablePanel implements DockStation, ChessListener {
	/** Listener which are informed when anything on this station changes */
	private List<DockStationListener> listeners = new ArrayList<DockStationListener>();
	
	/** A factory creating instances of {@link DockableDisplayer} used to show instances of {@link ChessFigure}*/
	private DisplayerFactory displayerFactory = new ChessDisplayerFactory();
	/** The set of currently used {@link DockableDisplayer} */
	private DisplayerCollection displayerCollection;
	
	/** The controller of the DockingFrames */
	private DockController controller;
	/** The theme used to create graphical elements of this station */
	private DockTheme theme;
	
	/** The fields of the grid */
	private Field[][] fields;
	/** A list of the fields which are currently occupied by a {@link ChessFigure} */
	private List<Field> usedFieldList = new ArrayList<Field>();
	
	/** Information about the next drop-action */
	private DropInfo drop;
	
	/** 
	 * A description of the state of the game. Used to determine which figure
	 * can be moved where, and what information should be painted onto this
	 * {@link ChessBoard}. 
	 */
	private Board board;
	/**
	 * A dialog that pops up when a pawn reaches the other side of the board 
	 * and has to be replaced by a new figure 
	 */
	private PawnReplaceDialog pawnReplaceDialog;
	
	/** A color used to paint the dark fields of the board */
	private Color dark = new Color( 209, 139, 71 );
	/** A color used to paint the bright fields of the board */
	private Color light = new Color( 255, 206, 158 );
	
	/**
	 * Creates a new board, but does not add any figures to it.
	 * @param pawnReplaceDialog a dialog used to let the user choose a replacement
	 * for a pawn that reaches the last row.
	 */
	public ChessBoard( PawnReplaceDialog pawnReplaceDialog ){
	    this.pawnReplaceDialog = pawnReplaceDialog;
		setLayout( null );
		
		displayerCollection = new DisplayerCollection( this, displayerFactory, "chess" );
		
		fields = new Field[8][8];
		for( int r = 0; r < 8; r++ )
			for( int c = 0; c < 8; c++ )
				fields[r][c] = new Field( r, c );
		
		setBasePane( new ContentPane() );
		setPreferredSize( new Dimension( 8*64, 8*64 ) );
	}
	
	/**
	 * Sets the model of this ChessBoard. The <code>board</code> contains
	 * information about location and possible moves of every figure. This
	 * ChessBoard will report all actions from the user to <code>board</code>.
	 * @param board the new board, not <code>null</code>
	 */
	public void setBoard( Board board ){
	    if( this.board != null )
	        this.board.removeListener( this );
	    
	    this.board = board;
	    
		for( int r = 0; r < 8; r++ ){
		    for( int c = 0; c < 8; c++ ){
		        Figure figure = board.getFigure( r, c );
		        if( figure != null )
		            put( r, c, new ChessFigure( figure ) );
		        else
		            put( r, c, null );
		    }
		}
		
		board.addListener( this );
		revalidate();
		repaint();
	}
	
	/**
	 * Gets the color which is used to fill the dark fields of the grid.
	 * @return the dark color
	 */
	public Color getDark(){
		return dark;
	}
	
	/**
	 * Sets the color which is used to fill the dark fields of the grid.
	 * @param dark the new color, not <code>null</code>
	 */
	public void setDark( Color dark ){
		this.dark = dark;
		repaint();
	}
	
	/**
	 * Gets the color which is used to fill the bright fields of the grid.
	 * @return the bright color
	 */
	public Color getLight(){
		return light;
	}
	
	/**
	 * Sets the color which is used to fill the bright fields of the grid
	 * @param light the new color, not <code>null</code>
	 */
	public void setLight( Color light ){
		this.light = light;
		repaint();
	}
	
	public PlaceholderMap getPlaceholders(){
		return null;
	}
	
	public void setPlaceholders( PlaceholderMap placeholders ){
		// ignore	
	}
	
	public PlaceholderMapping getPlaceholderMapping() {
		throw new UnsupportedOperationException( "not supported" );
	}
	
	public boolean accept( Dockable child ){
		return child instanceof ChessFigure;
	}

	public void addDockElementListener( DockableStateListener listener ){
		// ignore
	}
	
	public void removeDockElementListener( DockableStateListener listener ){
		// ignore
	}
	
	public void addDockStationListener( DockStationListener listener ){
		listeners.add( listener );
	}
	
	public DockComponentConfiguration getComponentConfiguration() {
		// not required for this example
		return null;
	}
	
	public void setComponentConfiguration( DockComponentConfiguration configuration ) {
		// not required for this example
	}
	
	/**
	 * Creates an independent list containing all instances of
	 * {@link DockStationListener} which are currently registered.
	 * @return the new list
	 */
	protected DockStationListener[] listListeners(){
		return listeners.toArray( new DockStationListener[ listeners.size() ] );
	}

	public boolean canDrag( Dockable dockable ){
	    ChessFigure figure = (ChessFigure)dockable;
		return figure.getFigure().getPlayer() == board.getPlayer();
	}

	public boolean canReplace( Dockable old, Dockable next ){
		return false;
	}

	public void changed( Dockable dockable, DockTitle title, boolean active ){
		title.changed( new ActivityDockTitleEvent( dockable, active ) );

	}

	public void requestChildDockTitle( DockTitleRequest request ){
		// ignore	
	}
	
	public void requestChildDisplayer( DisplayerRequest request ){
		// ignore
	}

	public void drag( Dockable dockable ){
		Field field = getFieldOf( dockable );
		if( field != null )
			put( field.getRow(), field.getColumn(), null );
	}

	public void drop( Dockable dockable ){
		throw new IllegalStateException( "Can't just drop a figure on a chess board" );
	}

	public boolean drop( Dockable dockable, DockableProperty property ){
		if( property instanceof ChessBoardProperty ){
			ChessBoardProperty location = (ChessBoardProperty)property;
			put( location.getRow(), location.getColumn(), (ChessFigure)dockable );
			return true;
		}
		
		return false;
	}
	
	/**
	 * Ensures that the field <code>row/column</code> shows <code>figure</code>.
	 * @param row the row of the field
	 * @param column the column of the field
	 * @param figure the figure to show, can be <code>null</code> if the field
	 * should be cleared.
	 */
	public void put( int row, int column, ChessFigure figure ){
		Field field = fields[row][column];
		ChessFigure old = field.getFigure();
		
		if( old != null ){
			for( DockStationListener listener : listListeners() )
				listener.dockableRemoving( this, old );
			
			field.set( null );
			old.setDockParent( null );
			
			for( DockStationListener listener : listListeners() )
				listener.dockableRemoved( this, old );
		}
		
		if( figure != null ){
			Field oldField = getFieldOf( figure );
			if( oldField == null ){
				for( DockStationListener listener : listListeners() )
					listener.dockableAdding( this, figure );
				
				field.set( figure );
				figure.setDockParent( this );
				
				for( DockStationListener listener : listListeners() )
					listener.dockableAdded( this, figure );
			}
			else{
				field.transfer( oldField );
				if( !board.isEmpty( oldField.getRow(), oldField.getColumn() )){
				    board.move( oldField.getRow(), oldField.getColumn(), row, column );
				    Figure pawn = board.pawnReplacement();
				    if( pawn != null ){
				        pawn = pawnReplaceDialog.replace( pawn );
				        board.put( pawn );
				        figure.setFigure( pawn );
				    }
				    
				    board.switchPlayer();
				}
			}
		}
	}
	
	/**
	 * Gets the field which shows <code>figure</code>.
	 * @param figure the figure whose field is searched
	 * @return the field or <code>null</code> if nothing was found
	 */
	private Field getFieldOf( Dockable figure ){
		for( Field field : usedFieldList )
			if( field.getFigure() == figure )
				return field;
		
		return null;
	}

	public DockController getController(){
		return controller;
	}

	public DockActionSource getDirectActionOffers( Dockable dockable ){
		// no actions
		return null;
	}

	public Dockable getDockable( int index ){
		return usedFieldList.get( index ).getFigure();
	}

	public int getDockableCount(){
		return usedFieldList.size();
	}

	public DockableProperty getDockableProperty( Dockable dockable, Dockable ignored ){
		Field field = getFieldOf( dockable );
		return new ChessBoardProperty( field.getRow(), field.getColumn() );
	}
	
	public void aside( AsideRequest request ){
		// no support for this feature required
	}

	public Dockable getFrontDockable(){
		return null;
	}

	public DockActionSource getIndirectActionOffers( Dockable dockable ){
		// no offers from this station
		return null;
	}

	public Rectangle getStationBounds(){
		Point location = new Point( 0, 0 );
		SwingUtilities.convertPointToScreen( location, this );
		return new Rectangle( location.x, location.y, getWidth(), getHeight() );
	}

	public DockTheme getTheme(){
		return theme;
	}

	public boolean isStationShowing(){
		return isStationVisible();
	}
	
	@Deprecated
	public boolean isStationVisible(){
		return isShowing();
	}

	public boolean isChildShowing( Dockable dockable ){
		return isVisible( dockable );
	}
	
	@Deprecated
	public boolean isVisible( Dockable dockable ){
		return isStationVisible();
	}

	public DockStationDropLayer[] getLayers(){
		return new DockStationDropLayer[]{
				new DefaultDropLayer( this )
		};
	}
	
	public void move( Dockable dockable, DockableProperty property ) {
	    // do nothing
	}

	public StationDragOperation prepareDrag( Dockable dockable ){
		// do nothing
		return null;
	}
	
	public StationDropOperation prepareDrop( StationDropItem item ){
		return prepare( item.getMouseX(), item.getMouseY(), (ChessFigure)item.getDockable() );
	}
	
	/**
	 * Prepares to move or drop a figure on this board.
	 * @param x the x-coordinate of the mouse
	 * @param y the y-coordinate of the mouse
	 * @param figure the figure which is grabbed
	 * @return <code>true</code> if a valid location for <code>figure</code>
	 * was found, <code>false</code> otherwise
	 */
	private StationDropOperation prepare( int x, int y, ChessFigure figure ){
		Point location = new Point( x, y );
		SwingUtilities.convertPointFromScreen( location, this );

		int w = getWidth();
		int h = getHeight();
		
		if( location.x < 0 || location.y < 0 || location.x > w || location.y > h ){
			return null;
		}
		

		int r = -1;
		int c = 7;
		
		while( c*w/8 > location.x )
			c--;
		
		while( (7-r)*h/8 > location.y )
			r++;
		
		final DropInfo drop = new DropInfo();
			
		figure.getFigure().reachable( new Board.CellVisitor(){
		    public boolean visit( int r, int c, Figure figure ) {
		        drop.targets[r][c] = true;
		        return true;
		    }
		});
		
		drop.figure = figure;
		
		drop.row = r;
		drop.column = c;
		
		drop.valid = drop.targets[drop.row][drop.column];
		
		return drop;
	}

	public void removeDockStationListener( DockStationListener listener ){
		listeners.remove( listener );
	}
	
	public void replace( DockStation old, Dockable next ){
		replace( old.asDockable(), next );
	}

	public void replace( Dockable old, Dockable next ){
		Field field = getFieldOf( old );
		if( field == null )
			throw new IllegalArgumentException( "Unknown dockable" );
		put( field.getRow(), field.getColumn(), (ChessFigure)next );
	}

	public void setController( DockController controller ){
		this.controller = controller;
		controller.getDockTitleManager().registerDefault( "chess-board", ChessDockTitle.FACTORY );
		displayerCollection.setController( controller );
		
		for( Field field : usedFieldList )
		    field.updateTitle();
	}

	public void setFrontDockable( Dockable dockable ){
		// ignore
	}

	public void updateTheme(){
		if( controller != null ){
			this.theme = controller.getTheme();
			
			for( Field field : usedFieldList )
			    field.updateTitle();
			
			revalidate();
		}
	}

	public DockStation asDockStation(){
		return this;
	}

	public Dockable asDockable(){
		return null;
	}

	public String getFactoryID(){
		return "chess-board";
	}
	
	public void killed( int r, int c, Figure figure ) {
	    // ensure removed
	    ChessFigure view = fields[r][c].getFigure();
	    if( view != null && view.getFigure() == figure )
	        put( r, c, null );
	}
	
	public void moved( int sr, int sc, int dr, int dc, Figure figure ) {
	    ChessFigure view = fields[sr][sc].getFigure();
	    if( view != null && view.getFigure() == figure )
	        put( dr, dc, view );
	}
	
	public void playerSwitched( Player player ) {
	    // ignore
	}
	
	/**
	 * Calculates the smallest x-coordinate which is still part of the column <code>c</code>.
	 * @param c a column
	 * @return the smallest x-coordinate in c
	 */
	private int x( int c ){
	    return c*getWidth()/8;
	}
	
	/**
	 * Calculates the smallest y-coordinate which is still part of the row <code>r</code>.
	 * @param r a row
	 * @return the smallest y-coordinate in r
	 */
	private int y( int r ){
	    return (7-r)*getHeight()/8;
	}
	
	/**
	 * Calculates the width of column <code>c</code>.
	 * @param c a column
	 * @return the width
	 */
	private int w( int c ){
	    return x( c+1 ) - x( c );
	}
	
	/**
	 * Calculates the height of row <code>r</code>.
	 * @param r a row
	 * @return the height
	 */
	private int h( int r ){
	    return y( r-1 ) - y( r );
	}
	
	@Override
	protected void paintOverlay( Graphics g ){
		if( drop != null ){
		    final Graphics2D g2 = (Graphics2D)g.create();
		    g2.setComposite( AlphaComposite.getInstance( AlphaComposite.SRC_OVER, 0.5f ) );
			g2.setColor( Color.GREEN );
			
			for( int r = 0; r < 8; r++ ){
			    for( int c = 0; c < 8; c++ ){
			        if( drop.targets[r][c] ){
			            g2.fillRect( x( c ), y( r ), w( c ), h( r ) );
			        }
			    }
			}
			
			int r = drop.row;
            int c = drop.column;
            
			if( !drop.valid ){
				g2.setColor( Color.RED );
				g2.fillRect( x( c ), y( r ), w( c ), h( r ) );
			}
			
			Icon icon = drop.figure.getFigure().getBigIcon();
			icon.paintIcon( this, g2, x(c)+(w(c)-icon.getIconWidth())/2, y(r) + (h(r)-icon.getIconHeight())/2 );
			
			g2.dispose();
		}
	}
	
	public boolean isAutoRemoveable(){
		return false;
	}

	/**
	 * An instance of DropInfo contains all information needed to execute
	 * a drag and drop-action on a {@link ChessBoard}. 
	 * @author Benjamin Sigg
	 */
	private class DropInfo implements StationDropOperation{
		/** the figure which is grabbed */
		public ChessFigure figure = null;
		/** the target row */
		public int row;
		/** the target column */
		public int column;
		/** whether the target is a valid destination or not */
		public boolean valid;
		/** which fields are valid targets */
		public boolean[][] targets = new boolean[8][8];
		
		public boolean isMove(){
			return true;
		}
		
		public void draw(){
			drop = this;
			repaint();
		}
		
		public void destroy( StationDropOperation next ){
			if( drop == this ){
				drop = null;
				repaint();
			}
		}
		
		public void execute(){
			if( valid ){
				put( row, column, figure );
			}
		}
		
		public CombinerTarget getCombination(){
			return null;
		}
		
		public DisplayerCombinerTarget getDisplayerCombination(){
			return null;
		}
		
		public DockStation getTarget(){
			return ChessBoard.this;
		}
		
		public Dockable getItem(){
			return figure;
		}
	}
	
	/**
	 * A panel that paints a 8x8 grid and shows the children of a {@link ChessBoard}. 
	 * @author Benjamin Sigg
	 */
	private class ContentPane extends JPanel{
		@Override
		protected void paintComponent( Graphics g ){
			for( int r = 0; r < 8; r++ ){
				for( int c = 0; c < 8; c++ ){
					if( (r+c) % 2 == 0 )
					    g.setColor( dark );
		            else
					    g.setColor( light );
					    
					g.fillRect( x( c ), y( r ), w( c ), h( r ) );
				}
			}
		}
		
		@Override
		public void doLayout(){
			for( Field field : usedFieldList ){
				Component component = field.getHandle().getDisplayer().getComponent();
				int r = field.getRow();
				int c = field.getColumn();
				component.setBounds( x(c), y(r), w(c), h(r) );
			}
		}
	}
	
	/**
	 * A field of the grid of a {@link ChessBoard}. A field contains all
	 * information needed to paint it.
	 * @author Benjamin Sigg
	 */
	private class Field{
		/** the displayer is used to show <code>figure</code> */
		private StationChildHandle handle;
		/** the figure on this field, might be <code>null</code> */
		private ChessFigure figure;
		/** the row in which this field lies */
		private int row;
		/** the column in which this field lies */
		private int column;
		
		/**
		 * Creates a new field.
		 * @param row the row in which this field lies
		 * @param column the column in which this field lies
		 */
		public Field( int row, int column ){
			this.row = row;
			this.column = column;
		}
		
		/**
		 * Gets the row in which this field lies.
		 * @return the row
		 */
		public int getRow(){
			return row;
		}
		
		/**
		 * Gets the column in which this field lies.
		 * @return the column
		 */
		public int getColumn(){
			return column;
		}
	
		/**
		 * Gets the element and its visualization.
		 * @return the figure or <code>null</code>
		 */
		public StationChildHandle getHandle(){
			return handle;
		}
		
		/**
		 * Gets the figure which is shown on this field.
		 * @return the figure, may be <code>null</code>
		 */
		public ChessFigure getFigure(){
			return figure;
		}
		
		/**
		 * Moves the figure of this field to the empty field <code>other</code>.
		 * @param other the destination
		 */
		public void transfer( Field other ){
			set( null );
			if( other.figure != null ){
				this.handle = other.handle;
				this.figure = other.figure;
			
				other.handle = null;
				other.figure = null;
				
				usedFieldList.remove( other );
				usedFieldList.add( this );
				
				revalidate();
			}
		}
		
		/**
		 * Sets the {@link Dockable} which is shown on this field.
		 * @param figure the new dockable, might be <code>null</code>
		 */
		public void set( ChessFigure figure ){
			ChessFigure old = this.figure;
			
			if( this.figure != null ){
				getContentPane().remove( handle.getDisplayer().getComponent() );
				
				handle.destroy();
				this.figure = null;
				this.handle = null;
			}
			
			this.figure = figure;
			
			if( this.figure != null ){
				DockTitleVersion version = null;
				if( controller != null )
					version = controller.getDockTitleManager().getVersion( "chess-board" );
				handle = new StationChildHandle( ChessBoard.this, displayerCollection, figure, version );
				handle.updateDisplayer();
				getContentPane().add( handle.getDisplayer().getComponent() );
			}
			
			if( old == null && figure != null )
				usedFieldList.add( this );
			else if( old != null && figure == null )
				usedFieldList.remove( this );
			
			getContentPane().revalidate();
		}
	
		/**
		 * Ensures that the correct {@link DockTitle} for the current
		 * {@link #set(ChessFigure) figure} is shown.
		 */
		public void updateTitle(){
			if( handle != null ){
				if( controller == null ){
					handle.setTitleRequest( null );
				}
				else{
					DockTitleVersion version = controller.getDockTitleManager().getVersion( "chess-board" );
					handle.setTitleRequest( version );
				}
			}
        }
	}
}
