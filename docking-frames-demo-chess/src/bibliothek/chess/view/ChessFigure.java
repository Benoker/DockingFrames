package bibliothek.chess.view;

import java.awt.Component;
import java.awt.Point;
import java.util.ArrayList;
import java.util.List;

import javax.swing.Icon;
import javax.swing.JLabel;
import javax.swing.event.MouseInputListener;

import bibliothek.chess.model.Figure;
import bibliothek.gui.DockController;
import bibliothek.gui.DockStation;
import bibliothek.gui.Dockable;
import bibliothek.gui.dock.DockElement;
import bibliothek.gui.dock.action.DefaultDockActionSource;
import bibliothek.gui.dock.action.DockActionSource;
import bibliothek.gui.dock.component.DockComponentConfiguration;
import bibliothek.gui.dock.displayer.DisplayerRequest;
import bibliothek.gui.dock.displayer.DockableDisplayerHints;
import bibliothek.gui.dock.dockable.DockableStateListener;
import bibliothek.gui.dock.dockable.DockHierarchyObserver;
import bibliothek.gui.dock.event.DockHierarchyListener;
import bibliothek.gui.dock.event.DockableListener;
import bibliothek.gui.dock.title.DockTitle;
import bibliothek.gui.dock.title.DockTitleRequest;

/**
 * A label showing an icon to represent a figure of chess. This label implements
 * {@link Dockable} in order to be shown on a {@link ChessBoard}.
 * @author Benjamin Sigg
 */
public class ChessFigure extends JLabel implements Dockable {
	/** list of listeners which will be informed when a property of this Dockable changes */
	private List<DockableListener> listeners = new ArrayList<DockableListener>();
	/** list of titles bound to this Dockable */
	private List<DockTitle> titles = new ArrayList<DockTitle>();
	/** the controller which is responsible for this Dockable */
	private DockController controller;
	/** the station on which this Dockable lies */
	private DockStation parent;
	/** an observer ensuring that {@link bibliothek.gui.dock.event.DockHierarchyEvent}s are send properly */
	private DockHierarchyObserver hierarchyObserver;
	
	/** the figure which is represented by this label */
	private Figure figure;

	/**
	 * Creates a new {@link ChessFigure}
	 * @param figure the figure which is represented by this label
	 */
	public ChessFigure( Figure figure ){
		this.figure = figure;
		setIcon( figure.getBigIcon() );
		setHorizontalAlignment( CENTER );
		setVerticalAlignment( CENTER );
		hierarchyObserver = new DockHierarchyObserver( this );
	}
	
	/**
	 * Gets the figure which is represented by this label.
	 * @return the figure
	 */
	public Figure getFigure() {
        return figure;
    }
	
	/**
	 * Sets the figure for which this label shown an icon.
	 * @param figure the figure
	 */
	public void setFigure( Figure figure ) {
        Icon oldIcon = this.figure.getSmallIcon();
        String oldTitle = this.figure.getName();
        
        this.figure = figure;
        setIcon( figure.getBigIcon() );
        
        for( DockableListener listener : listeners ){
            listener.titleIconChanged( this, oldIcon, figure.getSmallIcon() );
            listener.titleTextChanged( this, oldTitle, figure.getName() );
        }
    }
	
	public boolean accept( DockStation station ){
		return station instanceof ChessBoard;
	}

	public boolean accept( DockStation base, Dockable neighbor ){
		return false;
	}

	public void addDockableStateListener( DockableStateListener listener ){
		// ignore
	}
	
	public void removeDockableStateListener( DockableStateListener listener ){
		// ignore
	}
	
	public void addDockableListener( DockableListener listener ){
		listeners.add( listener );
	}

	public void addMouseInputListener( MouseInputListener listener ){
		addMouseMotionListener( listener );
		addMouseListener( listener );
	}

	/**
	 * Gets an independent list of all registered {@link DockableListener}.
	 * @return the listeners
	 */
	protected DockableListener[] listListeners(){
		return listeners.toArray( new DockableListener[ listeners.size() ]);
	}
	
	public void bind( DockTitle title ){
		titles.add( title );
		for( DockableListener listener : listListeners() )
			listener.titleBound( this, title );
	}

	public DockActionSource getLocalActionOffers(){
		// no actions for this figure
		return null;
	}
	
	public DockActionSource getGlobalActionOffers(){
		// no actions for this figure
		return new DefaultDockActionSource();
	}

	public Component getComponent(){
		return this;
	}
	
	public DockElement getElement() {
	    return this;
	}
	
	public boolean isUsedAsTitle() {
		return false;
	}
	
	public boolean shouldFocus(){
    	return true;
    }
	
	public boolean shouldTransfersFocus(){
		return false;
	}
	
	public Point getPopupLocation( Point click, boolean popupTrigger ) {
	    return null;
	}

	public DockController getController(){
		return controller;
	}

	public DockStation getDockParent(){
		return parent;
	}

	public void requestDockTitle( DockTitleRequest request ){
		// ignore	
	}
	
	public void requestDisplayer( DisplayerRequest request ){
		// ignore
	}
	
	public Icon getTitleIcon(){
		return figure.getSmallIcon();
	}

	public String getTitleText(){
		return figure.getName();
	}
	
	public String getTitleToolTip() {
	    return null;
	}

	public DockTitle[] listBoundTitles(){
		return titles.toArray( new DockTitle[ titles.size() ] );
	}

	public void removeDockableListener( DockableListener listener ){
		listeners.remove( listener );
	}

	public void removeMouseInputListener( MouseInputListener listener ){
		removeMouseListener( listener );
		removeMouseMotionListener( listener );
	}
	
	public void addDockHierarchyListener( DockHierarchyListener listener ){
		hierarchyObserver.addDockHierarchyListener( listener );
	}
	
	public void removeDockHierarchyListener( DockHierarchyListener listener ){
		hierarchyObserver.removeDockHierarchyListener( listener );
	}

	public void setController( DockController controller ){
		this.controller = controller;
		hierarchyObserver.controllerChanged( controller );
	}

	public void setDockParent( DockStation station ){
		parent = station;
		hierarchyObserver.update();
	}

	public void unbind( DockTitle title ){
		titles.remove( title );
		for( DockableListener listener : listListeners() )
			listener.titleUnbound( this, title );
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
	
	public void configureDisplayerHints( DockableDisplayerHints hints ) {
	    // ignore
	}
	
	public boolean isDockableShowing(){
		return isDockableVisible();
	}
	
	@Deprecated
	public boolean isDockableVisible(){
		// ignore
		return true;
	}
	
	public DockComponentConfiguration getComponentConfiguration() {
		// not required for this example
		return null;
	}
	
	public void setComponentConfiguration( DockComponentConfiguration configuration ) {
		// not required for this example
	}
}
