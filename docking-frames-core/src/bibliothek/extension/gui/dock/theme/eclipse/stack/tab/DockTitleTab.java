package bibliothek.extension.gui.dock.theme.eclipse.stack.tab;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Insets;
import java.awt.Point;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.Icon;
import javax.swing.JPanel;
import javax.swing.border.Border;
import javax.swing.event.MouseInputListener;

import bibliothek.extension.gui.dock.theme.EclipseTheme;
import bibliothek.extension.gui.dock.theme.eclipse.EclipseDockActionSource;
import bibliothek.extension.gui.dock.theme.eclipse.EclipseTabStateInfo;
import bibliothek.extension.gui.dock.theme.eclipse.stack.EclipseTab;
import bibliothek.extension.gui.dock.theme.eclipse.stack.EclipseTabPane;
import bibliothek.gui.DockController;
import bibliothek.gui.DockStation;
import bibliothek.gui.Dockable;
import bibliothek.gui.dock.DockElement;
import bibliothek.gui.dock.action.DockActionSource;
import bibliothek.gui.dock.station.stack.tab.TabConfiguration;
import bibliothek.gui.dock.station.stack.tab.layouting.TabPlacement;
import bibliothek.gui.dock.title.ActionsDockTitleEvent;
import bibliothek.gui.dock.title.DockTitle;
import bibliothek.gui.dock.title.DockTitle.Orientation;
import bibliothek.gui.dock.title.DockTitleManager;
import bibliothek.gui.dock.title.DockTitleRequest;
import bibliothek.gui.dock.title.DockTitleVersion;

/**
 * A {@link DockTitleTab} is a wrapper around an ordinary {@link DockTitle}
 * to get a {@link TabComponent}.<br>
 * This <code>DockTitleTab</code> will use {@link EclipseDockTitleEvent}s
 * to inform its <code>DockTitle</code> when a property has changed, the
 * method {@link DockTitle#changed(bibliothek.gui.dock.title.DockTitleEvent)}
 * is called for that purpose.
 * @author Benjamin Sigg
 * @deprecated Using a custom {@link TabPainter} is the preferred way to modify the tabs 
 */
@Deprecated
public class DockTitleTab implements TabComponent, EclipseTabStateInfo{
    /**
     * A {@link TabPainter} that uses the id {@link EclipseTheme#TAB_DOCK_TITLE}
     * to get a {@link DockTitleVersion} from the {@link DockTitleManager}
     * and create a new {@link DockTitle} which is then wrapped by a
     * {@link DockTitleTab}.
     */
    public static final TabPainter FACTORY = createFactory( ArchGradientPainter.FACTORY );
    
    /**
     * Creates a new factory which uses <code>fallback</code> to create
     * a {@link TabComponent} when no {@link DockTitle} is available.
     * @param fallback the backup-factory
     * @return a new {@link TabPainter}
     */
    public static final TabPainter createFactory( final TabPainter fallback ){
        return new TabPainter(){
        	public TabComponent createTabComponent( EclipseTabPane pane, Dockable dockable ){
        		DockStation station = pane.getStation();
        		DockController controller = station.getController();
        		
        		DockTitleVersion version = controller == null ? null : controller.getDockTitleManager().getVersion( EclipseTheme.TAB_DOCK_TITLE );
                if( version == null )
                    return fallback.createTabComponent( pane, dockable );
                    
                pane.setContentBorderAt( pane.indexOf( dockable ), null );
                return new DockTitleTab( station, dockable, version, pane.getTheme() );
        	}
        	
        	public InvisibleTab createInvisibleTab( InvisibleTabPane pane, Dockable dockable ){
        		return fallback.createInvisibleTab( pane, dockable );
        	}
        	
        	public TabPanePainter createDecorationPainter( EclipseTabPane pane ){
        	    return fallback.createDecorationPainter( pane );
            }
            
        	public Border getFullBorder( BorderedComponent owner, DockController controller, Dockable dockable ){
                return BorderFactory.createLineBorder( controller.getColors().get( "stack.border" ) );
            }
        };
    }
    
    /** the station on which this tab lies, might be <code>null</code> */
    private DockStation station;
    /** the element which is represented by this tab */
    private Dockable dockable;
    /** the visual representation of this tab */
    private DockTitleRequest title;
    /** the location of this tab */
    private TabPlacement placement;
    /** the theme using this tab */
    private EclipseTheme theme;
    
    /** content of this tab */
    private JPanel content;
    
    /** MouseListeners of this tab */
    private List<MouseListener> mouseListeners = new ArrayList<MouseListener>();
    /** MouseMotionListeners of this tab */
    private List<MouseMotionListener> mouseMotionListeners = new ArrayList<MouseMotionListener>();
    
    /** whether this tab is currently focused */
    private boolean focused;
    /** whether this tab is currently selected */
    private boolean selected;
    /** whether icons should be painted when this tab is inactive */
    private boolean paintIconWhenInactive;
    
    /** listener that is added to the current title */
    private MouseInputListener mouseListener = new MouseInputListener(){
        public void mouseClicked( MouseEvent e ) {
            for( MouseListener m : mouseListeners.toArray( new MouseListener[ mouseListeners.size() ] ))
                m.mouseClicked( e );
        }

        public void mouseEntered( MouseEvent e ) {
            for( MouseListener m : mouseListeners.toArray( new MouseListener[ mouseListeners.size() ] ))
                m.mouseEntered( e );
        }

        public void mouseExited( MouseEvent e ) {
            for( MouseListener m : mouseListeners.toArray( new MouseListener[ mouseListeners.size() ] ))
                m.mouseExited( e );
        }

        public void mousePressed( MouseEvent e ) {
            for( MouseListener m : mouseListeners.toArray( new MouseListener[ mouseListeners.size() ] ))
                m.mousePressed( e );
        }

        public void mouseReleased( MouseEvent e ) {
            for( MouseListener m : mouseListeners.toArray( new MouseListener[ mouseListeners.size() ] ))
                m.mouseReleased( e );
        }

        public void mouseDragged( MouseEvent e ) {
            for( MouseMotionListener m : mouseMotionListeners.toArray( new MouseMotionListener[ mouseMotionListeners.size() ] ))
                m.mouseDragged( e );
        }

        public void mouseMoved( MouseEvent e ) {
            for( MouseMotionListener m : mouseMotionListeners.toArray( new MouseMotionListener[ mouseMotionListeners.size() ] ))
                m.mouseMoved( e );
        }
    };
    
    /**
     * Creates a new tab.
     * @param station the station which uses the tabbed pane, might be <code>null</code>
     * @param dockable the element for which this tab is shown
     * @param title the title which represents the tab
     * @param theme the theme which uses this tab
     */
    public DockTitleTab( DockStation station, final Dockable dockable, DockTitleVersion title, EclipseTheme theme ){
    	content = new JPanel( new BorderLayout() );
    	content.setOpaque( false );
    	
        this.station = station;
        this.dockable = dockable;
        this.theme = theme;
        this.title = new DockTitleRequest( station, dockable, title ) {
			@Override
			protected void answer( DockTitle previous, DockTitle title ){
				if( previous != null ){
					content.removeAll();
					dockable.unbind( previous );
					previous.removeMouseInputListener( mouseListener );
				}
				
				if( title != null ){
					dockable.bind( title );
					title.addMouseInputListener( mouseListener );
					updateOrientation();
					content.add( title.getComponent(), BorderLayout.CENTER );
					fire();
				}
			}
		};
    }

    public EclipseTabStateInfo getEclipseTabStateInfo(){
    	return this;
    }
    
    public void setConfiguration( TabConfiguration configuration ){
    	// ignore
    }
    
    public void setOrientation( TabPlacement orientation ){
    	this.placement = orientation;
    	updateOrientation();
    }
    
    private void updateOrientation(){
    	DockTitle title = this.title.getAnswer();
    	if( placement != null && title != null ){
    		switch( placement ){
		    	case TOP_OF_DOCKABLE:
		    		title.setOrientation( Orientation.NORTH_SIDED );
		    		break;
		    	case BOTTOM_OF_DOCKABLE:
		    		title.setOrientation( Orientation.SOUTH_SIDED );
		    		break;
		    	case LEFT_OF_DOCKABLE:
		    		title.setOrientation( Orientation.WEST_SIDED );
		    		break;
		    	case RIGHT_OF_DOCKABLE:
		    		title.setOrientation( Orientation.EAST_SIDED );
		    		break;
		    }
    	}
    }
    
    public void setTab( EclipseTab tab ){
    	// ignore
    }
    
    public void bind() {
    	title.install();
    	title.request();
        fire();
    }
    
    public void unbind() {
    	title.uninstall();
    	title.requestNull();
    }
    
    public void addMouseListener( MouseListener listener ) {
        mouseListeners.add( listener );
    }

    public void addMouseMotionListener( MouseMotionListener listener ) {
        mouseMotionListeners.add( listener );
    }

    public Component getComponent() {
        return content;
    }
    
    public Dimension getMinimumSize( TabComponent[] tabs ){
    	return getComponent().getMinimumSize();
    }
    
    public Dimension getPreferredSize( TabComponent[] tabs ){
    	return getComponent().getPreferredSize();
    }
    
    public DockElement getElement() {
        return title.getTarget();
    }
    
    public Dockable getDockable(){
    	return title.getTarget();
    }
    
    public boolean isUsedAsTitle() {
        return true;
    }
    
    public boolean shouldTransfersFocus(){
	    return true;
    }
    
    public boolean shouldFocus(){
    	return true;
    }
    
    /**
     * Calling this method has no effect, as this tab shows a {@link DockTitle} which has its own
     * mechanism to disable itself.
     */
    public void setEnabled( boolean enabled ){
    	// ignore
    }
    
    public Point getPopupLocation( Point click, boolean popupTrigger ) {
    	DockTitle current = title.getAnswer();
    	if( current == null )
    		return null;
    	return current.getPopupLocation( click, popupTrigger );
    }
    
    public void addMouseInputListener( MouseInputListener listener ) {
    	mouseListeners.add( listener );
    	mouseMotionListeners.add( listener );
    }
    
    public void removeMouseInputListener( MouseInputListener listener ) {
    	mouseListeners.remove( listener );
    	mouseMotionListeners.remove( listener );
    }
    
    public Insets getOverlap( TabComponent other ){
        return new Insets( 0, 0, 0, 0 );
    }

    public void removeMouseListener( MouseListener listener ) {
        mouseListeners.remove( listener );
    }

    public void removeMouseMotionListener( MouseMotionListener listener ) {
        mouseMotionListeners.remove( listener );
    }

    public void setFocused( boolean focused ) {
        this.focused = focused;
        fire();
    }
    
    public boolean isFocused(){
		return focused;
	}

    public void setPaintIconWhenInactive( boolean paint ) {
        this.paintIconWhenInactive = paint;
        fire();
    }

    public void setSelected( boolean selected ) {
        this.selected = selected;
        fire();
    }
    
    public boolean isSelected(){
    	return selected;
    }

    public void setIcon( Icon icon ){
	    // ignore	
    }
    
    public void setText( String text ){
	    // ignore	
    }
    
    public void setTooltip( String tooltip ){
	    // ignore	
    }
    
    /**
     * Updates the selection and focus state of the title of this tab. 
     */
    public void update() {
        fire();
    }    
    
    /**
     * Fires an event to the {@link DockTitle} of this <code>DockTitleTab</code>.
     */
    protected void fire(){
    	DockTitle answer = title.getAnswer();
    	if( answer != null ){
    		EclipseDockTitleEvent eclipseEvent = new EclipseDockTitleEvent( station, dockable, selected, focused, paintIconWhenInactive );
    		answer.changed( eclipseEvent );
    		
    		DockActionSource actions = new EclipseDockActionSource( theme, dockable.getGlobalActionOffers(), getEclipseTabStateInfo(), true );
    		ActionsDockTitleEvent actionEvent = new ActionsDockTitleEvent( station, dockable, actions );
    		answer.changed( actionEvent );
    	}
    }
}
