package bibliothek.chess.view;

import java.awt.Component;
import java.util.ArrayList;
import java.util.List;

import javax.swing.Icon;
import javax.swing.JLabel;
import javax.swing.event.MouseInputListener;

import bibliothek.chess.model.Figure;
import bibliothek.gui.DockController;
import bibliothek.gui.DockStation;
import bibliothek.gui.Dockable;
import bibliothek.gui.dock.action.DockActionSource;
import bibliothek.gui.dock.event.DockableListener;
import bibliothek.gui.dock.title.DockTitle;
import bibliothek.gui.dock.title.DockTitleVersion;

public class ChessFigure extends JLabel implements Dockable {
	private List<DockableListener> listeners = new ArrayList<DockableListener>();
	private List<DockTitle> titles = new ArrayList<DockTitle>();
	private DockController controller;
	private DockStation parent;
	
	private Figure figure;
	public ChessFigure( Figure figure ){
		this.figure = figure;
		setIcon( figure.getBigIcon() );
	}
	
	public boolean accept( DockStation station ){
		return station instanceof ChessBoard;
	}

	public boolean accept( DockStation base, Dockable neighbor ){
		return false;
	}

	public void addDockableListener( DockableListener listener ){
		listeners.add( listener );
	}

	public void addMouseInputListener( MouseInputListener listener ){
		addMouseMotionListener( listener );
		addMouseListener( listener );
	}

	protected DockableListener[] listListeners(){
		return listeners.toArray( new DockableListener[ listeners.size() ]);
	}
	
	public void bind( DockTitle title ){
		titles.add( title );
		for( DockableListener listener : listListeners() )
			listener.titleBinded( this, title );
	}

	public DockActionSource getActionOffers(){
		// no actions for this figure
		return null;
	}

	public Component getComponent(){
		return this;
	}

	public DockController getController(){
		return controller;
	}

	public DockStation getDockParent(){
		return parent;
	}

	public DockTitle getDockTitle( DockTitleVersion version ){
		return version.createDockable( this );
	}

	public Icon getTitleIcon(){
		return figure.getSmallIcon();
	}

	public String getTitleText(){
		return figure.getName();
	}

	public DockTitle[] listBindedTitles(){
		return titles.toArray( new DockTitle[ titles.size() ] );
	}

	public void removeDockableListener( DockableListener listener ){
		listeners.remove( listener );
	}

	public void removeMouseInputListener( MouseInputListener listener ){
		removeMouseListener( listener );
		removeMouseMotionListener( listener );
	}

	public void setController( DockController controller ){
		this.controller = controller;
	}

	public void setDockParent( DockStation station ){
		parent = station;
	}

	public void unbind( DockTitle title ){
		titles.remove( title );
		for( DockableListener listener : listListeners() )
			listener.titleUnbinded( this, title );
	}

	public DockStation asDockStation(){
		return null;
	}

	public Dockable asDockable(){
		return this;
	}

	public String getFactoryID(){
		return "chess-figure";
	}
}
