package bibliothek.chess.view;

import java.awt.*;
import java.util.ArrayList;
import java.util.EventListener;
import java.util.List;

import javax.swing.Icon;
import javax.swing.JComponent;
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
import bibliothek.gui.dock.DockableDisplayer;
import bibliothek.gui.dock.DockableProperty;
import bibliothek.gui.dock.action.DockActionSource;
import bibliothek.gui.dock.event.DockStationListener;
import bibliothek.gui.dock.event.DockTitleEvent;
import bibliothek.gui.dock.station.DisplayerCollection;
import bibliothek.gui.dock.station.DisplayerFactory;
import bibliothek.gui.dock.station.OverpaintablePanel;
import bibliothek.gui.dock.title.DockTitle;
import bibliothek.gui.dock.title.DockTitleVersion;

public class ChessBoard extends OverpaintablePanel implements DockStation, ChessListener {
	private List<DockStationListener> listeners = new ArrayList<DockStationListener>();
	
	private DisplayerFactory displayerFactory = new ChessDisplayerFactory();
	private DisplayerCollection displayerCollection;
	
	private DockController controller;
	private DockTheme theme;
	
	private Field[][] fields;
	private List<Field> usedFieldList = new ArrayList<Field>();
	
	private DropInfo drop;
	
	private Board board;
	private PawnReplaceDialog pawnReplaceDialog;
	
	private Color dark = new Color( 209, 139, 71 );
	private Color light = new Color( 255, 206, 158 );
	
	public ChessBoard( PawnReplaceDialog pawnReplaceDialog ){
	    this.pawnReplaceDialog = pawnReplaceDialog;
		setLayout( null );
		
		displayerCollection = new DisplayerCollection( this, displayerFactory );
		
		fields = new Field[8][8];
		for( int r = 0; r < 8; r++ )
			for( int c = 0; c < 8; c++ )
				fields[r][c] = new Field( r, c );
		
		setContentPane( new ContentPane() );
		setPreferredSize( new Dimension( 8*64, 8*64 ) );
	}
	
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
	
	public Color getDark(){
		return dark;
	}
	
	public void setDark( Color dark ){
		this.dark = dark;
		repaint();
	}
	
	public Color getLight(){
		return light;
	}
	
	public void setLight( Color light ){
		this.light = light;
		repaint();
	}
	
	public boolean accept( Dockable child ){
		return child instanceof ChessFigure;
	}

	public void addDockStationListener( DockStationListener listener ){
		listeners.add( listener );
	}
	
	protected DockStationListener[] listListeners(){
		return listeners.toArray( new DockStationListener[ listeners.size() ] );
	}

	public boolean canCompare( DockStation station ){
		return false;
	}

	public boolean canDrag( Dockable dockable ){
	    ChessFigure figure = (ChessFigure)dockable;
		return figure.getFigure().getPlayer() == board.getPlayer();
	}

	public boolean canReplace( Dockable old, Dockable next ){
		return false;
	}

	public void changed( Dockable dockable, DockTitle title, boolean active ){
		title.changed( new DockTitleEvent( dockable, active ) );

	}

	public int compare( DockStation station ){
		return 0;
	}

	public void drag( Dockable dockable ){
		Field field = getFieldOf( dockable );
		if( field != null )
			put( field.getRow(), field.getColumn(), null );
	}

	public void draw(){
		if( drop != null ){
			drop.drawing = true;
			repaint();
		}
	}

	public void drop(){
		if( drop != null && drop.valid ){
			put( drop.row, drop.column, drop.figure );
		}
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
	
	private Field getFieldOf( Dockable figure ){
		for( Field field : usedFieldList )
			if( field.getFigure() == figure )
				return field;
		
		return null;
	}

	public void forget(){
		drop = null;
		repaint();
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

	public DockableProperty getDockableProperty( Dockable dockable ){
		Field field = getFieldOf( dockable );
		return new ChessBoardProperty( field.getRow(), field.getColumn() );
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

	public <D extends Dockable & DockStation> boolean isInOverrideZone( int x, int y, D invoker, Dockable drop ){
		return false;
	}

	public boolean isStationVisible(){
		return isShowing();
	}

	public boolean isVisible( Dockable dockable ){
		return isStationVisible();
	}

	public void move(){
		if( drop != null && drop.valid ){
			put( drop.row, drop.column, drop.figure );
		}
	}

	public boolean prepareDrop( int mouseX, int mouseY, int titleX, int titleY, Dockable dockable ){
		return prepare( mouseX, mouseY, (ChessFigure)dockable );
	}

	public boolean prepareMove( int mouseX, int mouseY, int titleX, int titleY, Dockable dockable ){
		return prepare( mouseX, mouseY, (ChessFigure)dockable );
	}
	
	private boolean prepare( int x, int y, ChessFigure figure ){
		Point location = new Point( x, y );
		SwingUtilities.convertPointFromScreen( location, this );

		int w = getWidth();
		int h = getHeight();
		
		if( location.x < 0 || location.y < 0 || location.x > w || location.y > h ){
			return false;
		}
		

		int r = -1;
		int c = 7;
		
		while( c*w/8 > location.x )
			c--;
		
		while( (7-r)*h/8 > location.y )
			r++;
		
		if( drop == null || drop.figure != figure ){
			drop = new DropInfo();
			
			figure.getFigure().reachable( new Board.CellVisitor(){
			    public boolean visit( int r, int c, Figure figure ) {
			        drop.targets[r][c] = true;
			        return true;
			    }
			});
		}
		
		drop.figure = figure;
		
		drop.row = r;
		drop.column = c;
		
		drop.valid = drop.targets[drop.row][drop.column];
		
		return true;
	}

	public void removeDockStationListener( DockStationListener listener ){
		listeners.remove( listener );
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
	
	private class DropInfo{
		public boolean drawing = false;
		public ChessFigure figure = null;
		public int row;
		public int column;
		public boolean valid;
		public boolean[][] targets = new boolean[8][8];
	}
	
	private int x( int c ){
	    return c*getWidth()/8;
	}
	
	private int y( int r ){
	    return (7-r)*getHeight()/8;
	}
	
	private int w( int c ){
	    return x( c+1 ) - x( c );
	}
	
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
				JComponent component = field.getDisplayer();
				int r = field.getRow();
				int c = field.getColumn();
				component.setBounds( x(c), y(r), w(c), h(r) );
			}
		}
	}
	
	private class Field{
		private DockableDisplayer displayer;
		private ChessFigure figure;
		private int row;
		private int column;
		
		public Field( int row, int column ){
			this.row = row;
			this.column = column;
		}
		
		public int getRow(){
			return row;
		}
		
		public int getColumn(){
			return column;
		}
		
		public DockableDisplayer getDisplayer(){
			return displayer;
		}
		
		public ChessFigure getFigure(){
			return figure;
		}
		
		public void transfer( Field other ){
			set( null );
			if( other.figure != null ){
				this.displayer = other.displayer;
				this.figure = other.figure;
			
				other.displayer = null;
				other.figure = null;
				
				usedFieldList.remove( other );
				usedFieldList.add( this );
				
				revalidate();
			}
		}
		
		public void set( ChessFigure figure ){
			ChessFigure old = this.figure;
			
			if( this.figure != null ){
				getContentPane().remove( displayer );
				displayerCollection.release( displayer );
				
				this.figure = null;
				this.displayer = null;
			}
			
			this.figure = figure;
			
			if( this.figure != null ){
				displayer = displayerCollection.fetch( figure, null );
				getContentPane().add( displayer );
			}
			
			if( old == null && figure != null )
				usedFieldList.add( this );
			else if( old != null && figure == null )
				usedFieldList.remove( this );
			
			updateTitle();
			getContentPane().revalidate();
		}
	
		public void updateTitle(){
            if( displayer != null ){
                DockTitle title = displayer.getTitle();
                if( title != null ){
                    figure.unbind( title );
                    title = null;
                }
                
                if( controller != null ){
                    DockTitleVersion version = controller.getDockTitleManager().getVersion( "chess-board" );
                    if( version != null ){
                        title = figure.getDockTitle( version );
                        if( title != null )
                            figure.bind( title );
                    }
                }
                displayer.setTitle( title );
            }
        }
	}
}
