package bibliothek.extension.gui.dock.theme.eclipse.rex.tab;

import java.awt.Component;
import java.awt.Graphics;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.border.Border;
import javax.swing.event.MouseInputListener;

import bibliothek.extension.gui.dock.theme.eclipse.rex.RexTabbedComponent;
import bibliothek.gui.DockController;
import bibliothek.gui.DockStation;
import bibliothek.gui.Dockable;
import bibliothek.gui.dock.title.DockTitle;
import bibliothek.gui.dock.title.DockTitleVersion;

public class RexDockTitleTab implements TabComponent{
    public static final TabPainter FACTORY = new TabPainter(){
        public TabComponent createTabComponent( DockController controller,
                RexTabbedComponent component, Dockable dockable, int index ) {
            
            DockTitleVersion version = controller.getDockTitleManager().getVersion( "eclipse.tab" );
            DockTitle title = dockable.getDockTitle( version );
            if( title == null )
                return ShapedGradientPainter.FACTORY.createTabComponent( controller, component, dockable, index );
                
            return new RexDockTitleTab( component.getStation(), dockable, title, index );
        }
        public void paintTabStrip( RexTabbedComponent tabbedComponent,
                Component tabStrip, Graphics g ) {

            // do nothing
        }
    };
    
    private DockStation station;
    private Dockable dockable;
    private DockTitle title;
    
    private List<MouseListener> mouseListeners = new ArrayList<MouseListener>();
    private List<MouseMotionListener> mouseMotionListeners = new ArrayList<MouseMotionListener>();
    
    private boolean focused;
    private boolean selected;
    private int index;
    private boolean paintIconWhenInactive;
    
    public RexDockTitleTab( DockStation station, Dockable dockable, DockTitle title, int index ){
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
    
    protected void fire(){
        RexDockTitleEvent event = new RexDockTitleEvent( 
                station, dockable, selected, focused, paintIconWhenInactive, index );
        title.changed( event );
    }
}
