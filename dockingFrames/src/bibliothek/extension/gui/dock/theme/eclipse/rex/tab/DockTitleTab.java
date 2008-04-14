package bibliothek.extension.gui.dock.theme.eclipse.rex.tab;

import java.awt.Component;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.border.Border;
import javax.swing.event.MouseInputListener;

import bibliothek.extension.gui.dock.theme.EclipseTheme;
import bibliothek.extension.gui.dock.theme.eclipse.rex.RexSystemColor;
import bibliothek.extension.gui.dock.theme.eclipse.rex.RexTabbedComponent;
import bibliothek.gui.DockController;
import bibliothek.gui.DockStation;
import bibliothek.gui.Dockable;
import bibliothek.gui.dock.StackDockStation;
import bibliothek.gui.dock.title.DockTitle;
import bibliothek.gui.dock.title.DockTitleManager;
import bibliothek.gui.dock.title.DockTitleVersion;
import bibliothek.gui.dock.title.DockTitle.Orientation;

/**
 * A {@link DockTitleTab} is a wrapper around an ordinary {@link DockTitle}
 * to get a {@link TabComponent}.<br>
 * This <code>DockTitleTab</code> will use {@link EclipseDockTitleEvent}s
 * to inform its <code>DockTitle</code> when a property has changed, the
 * method {@link DockTitle#changed(bibliothek.gui.dock.event.DockTitleEvent)}
 * is called for that purpose.
 * @author Benjamin Sigg
 *
 */
public class DockTitleTab implements TabComponent{
    /**
     * A {@link TabPainter} that uses the id {@link EclipseTheme#TAB_DOCK_TITLE}
     * to get a {@link DockTitleVersion} from the {@link DockTitleManager}
     * and create a new {@link DockTitle} which is then wrapped by a
     * {@link DockTitleTab}.
     */
    public static final TabPainter FACTORY = createFactory( ShapedGradientPainter.FACTORY );
    
    /**
     * Creates a new factory which uses <code>fallback</code> to create
     * a {@link TabComponent} when no {@link DockTitle} is available.
     * @param fallback the backup-factory
     * @return a new {@link TabPainter}
     */
    public static final TabPainter createFactory( final TabPainter fallback ){
        return new TabPainter(){
            public TabComponent createTabComponent( DockController controller,
                    RexTabbedComponent component, StackDockStation station, Dockable dockable, int index ) {
                
                DockTitleVersion version = controller.getDockTitleManager().getVersion( EclipseTheme.TAB_DOCK_TITLE );
                DockTitle title = version == null ? null : dockable.getDockTitle( version );
                if( title == null )
                    return fallback.createTabComponent( controller, component, station, dockable, index );
                    
                title.setOrientation( Orientation.NORTH_SIDED );
                return new DockTitleTab( component.getStation(), dockable, title, index );
            }
            
            public TabStripPainter createTabStripPainter( RexTabbedComponent component ) {
                return fallback.createTabStripPainter( component );
            }
            
            public Border getFullBorder( DockController controller, Dockable dockable ) {
                return BorderFactory.createLineBorder( controller.getColors().get( "stack.border" ) );
            }
            
            public Border getFullBorder( DockController controller, DockStation station, RexTabbedComponent component ) {
                return BorderFactory.createLineBorder( controller.getColors().get( "stack.border" ) );
            }
        };
    }
    
    /** the station on which this tab lies, might be <code>null</code> */
    private DockStation station;
    /** the element which is represented by this tab */
    private Dockable dockable;
    /** the visual representation of this tab */
    private DockTitle title;
    
    /** MouseListeners of this tab */
    private List<MouseListener> mouseListeners = new ArrayList<MouseListener>();
    /** MouseMotionListeners of this tab */
    private List<MouseMotionListener> mouseMotionListeners = new ArrayList<MouseMotionListener>();
    
    /** whether this tab is currently focused */
    private boolean focused;
    /** whether this tab is currently selected */
    private boolean selected;
    /** the location of this tab */
    private int index;
    /** whether icons should be painted when this tab is inactive */
    private boolean paintIconWhenInactive;
    
    /**
     * Creates a new tab.
     * @param station the station which uses the tabbed pane, might be <code>null</code>
     * @param dockable the element for which this tab is shown
     * @param title the title which represents the tab
     * @param index the location of this tab
     */
    public DockTitleTab( DockStation station, Dockable dockable, DockTitle title, int index ){
        this.station = station;
        this.dockable = dockable;
        this.title = title;
        this.index = index;
        
        title.addMouseInputListener( new MouseInputListener(){
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
        });
    }
    
    public void bind() {
        dockable.bind( title );
        fire();
    }
    
    public void unbind() {
        dockable.unbind( title );
    }
    
    public void addMouseListener( MouseListener listener ) {
        mouseListeners.add( listener );
    }

    public void addMouseMotionListener( MouseMotionListener listener ) {
        mouseMotionListeners.add( listener );
    }

    public Component getComponent() {
        return title.getComponent();
    }

    public Border getContentBorder() {
        return null;
    }

    public int getOverlap() {
        return 0;
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

    public void setIndex( int index ) {
        this.index = index;
        fire();
    }

    public void setPaintIconWhenInactive( boolean paint ) {
        this.paintIconWhenInactive = paint;
        fire();
    }

    public void setSelected( boolean selected ) {
        this.selected = selected;
        fire();
    }

    public void update() {
        fire();
    }    
    
    /**
     * Fires an event to the {@link DockTitle} of this <code>DockTitleTab</code>.
     */
    protected void fire(){
        EclipseDockTitleEvent event = new EclipseDockTitleEvent( 
                station, dockable, selected, focused, paintIconWhenInactive, index );
        title.changed( event );
    }
}
