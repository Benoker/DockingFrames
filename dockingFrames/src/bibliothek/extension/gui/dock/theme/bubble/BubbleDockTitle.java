package bibliothek.extension.gui.dock.theme.bubble;

import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.geom.RoundRectangle2D;

import javax.swing.JComponent;
import javax.swing.event.MouseInputAdapter;

import bibliothek.extension.gui.dock.theme.BubbleTheme;
import bibliothek.gui.Dockable;
import bibliothek.gui.dock.title.AbstractDockTitle;
import bibliothek.gui.dock.title.DockTitleVersion;

public class BubbleDockTitle extends AbstractDockTitle {
    private BubbleTheme theme;
    private BubbleColorAnimation animation;
    
    private boolean mouse = false;
    private int arc = 16;
    
    public BubbleDockTitle( BubbleTheme theme, Dockable dockable, DockTitleVersion origin ) {
        super( dockable, origin );
        setOpaque( false );
        
        if( theme == null )
            throw new IllegalArgumentException( "theme must not be null" );
        
        this.theme = theme;
        initAnimation();
    }
    
    public BubbleDockTitle( BubbleTheme theme, Dockable dockable, DockTitleVersion origin, boolean showMiniButtons ){
        super( dockable, origin, showMiniButtons );
        setOpaque( false );
        
        if( theme == null )
            throw new IllegalArgumentException( "theme must not be null" );
        
        this.theme = theme;
        initAnimation();
    }
    
    private void initAnimation(){
        animation = new BubbleColorAnimation( theme );
        updateAnimation( false );
        animation.addTask( new Runnable(){
            public void run() {
                pulse();
            }
        });
        
        setForeground( animation.getColor( "text" ));
        
        addMouseInputListener( new MouseInputAdapter(){
            @Override
            public void mouseEntered( MouseEvent e ) {
                updateAnimation( true );
            }
            
            @Override
            public void mouseExited( MouseEvent e ) {
                updateAnimation( false );
            }
        });
    }
    
    @Override
    public void setActive( boolean active ) {
        if( isActive() != active ){
            super.setActive( active );
            updateAnimation( mouse );
        }
    }
    
    protected void updateAnimation( boolean mouse ){
        this.mouse = mouse;
        
        String postfix = "";
        if( isActive() ){
            if( mouse )
                postfix = "active.mouse";
            else
                postfix = "active";
        }
        else{
            if( mouse )
                postfix = "inactive.mouse";
            else
                postfix = "inactive";            
        }
        
        animation.putColor( "top",     "title.top." + postfix );
        animation.putColor( "bottom",  "title.bottom." + postfix );
        animation.putColor( "text",    "title.text." + postfix );
    }
    
    protected void pulse(){
        setForeground( animation.getColor( "text" ));
        repaint();
    }
    
    @Override
    protected Insets getInnerInsets() {
        int edge = arc / 4;
        
        switch( getOrientation() ){
            case EAST_SIDED: return new Insets( edge, 0, edge, edge );
            case FREE_HORIZONTAL: return new Insets( edge, edge, edge, edge );
            case FREE_VERTICAL: return new Insets( edge, edge, edge, edge );
            case NORTH_SIDED: return new Insets( edge, edge, 0, edge );
            case SOUTH_SIDED: return new Insets( 0, edge, edge, edge );
            case WEST_SIDED: return new Insets( edge, edge, edge, 0 );
            default: return super.getInnerInsets();
        }
    }
    
    @Override
    public void paint( Graphics g ) {
        super.paint( g );
        
        // draw horizon
        Graphics2D g2 = (Graphics2D)g.create();
        
        Insets insets = getInsets();
        int x = 0, y = 0;
        int w = getWidth();
        int h = getHeight();
        if( insets != null ){
            x = insets.left;
            y = insets.top;
            w -= insets.left + insets.right;
            h -= insets.top + insets.bottom;
        }
        
        Rectangle clip = g.getClipBounds();
        if( clip == null ){
            clip = new Rectangle( x, y, w, h );
        }
        
        // set clipping area
        setRoundRectClip( g2, x, y, w, h );
        
        g2.clipRect( clip.x, clip.y, clip.width, clip.height );
        
        if( getOrientation().isHorizontal() ){
            g2.setPaint( new GradientPaint( 0, 0, new Color( 150, 150, 150 ), 0, h/2, Color.WHITE ));
            g2.setComposite( AlphaComposite.getInstance( AlphaComposite.SRC_ATOP, 0.4f ) );
            g2.fillRect( 0, 0, w, h/2 );
        }
        else{
            g2.setPaint( new GradientPaint( 0, 0, new Color( 150, 150, 150 ), w/2, 0, Color.WHITE ));
            g2.setComposite( AlphaComposite.getInstance( AlphaComposite.SRC_ATOP, 0.4f ) );
            g2.fillRect( 0, 0, w/2, h );
        }
        
        g2.dispose();
    }

    @Override
    protected void paintBackground( Graphics g, JComponent component ) {
        Graphics2D g2 = (Graphics2D)g.create();
        g2.setRenderingHint( RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON );
        
        Insets insets = getInsets();
        int x = 0, y = 0;
        int w = component.getWidth();
        int h = component.getHeight();
        if( insets != null ){
            x = insets.left;
            y = insets.top;
            w -= insets.left + insets.right;
            h -= insets.top + insets.bottom;
        }
        
        Rectangle clipRect = g.getClipBounds();
        if( clipRect == null ){
            clipRect = new Rectangle( x, y, w, h );
        }
        
        // set clipping area
        setRoundRectClip( g2, x, y, w, h );
        
        g2.clipRect( clipRect.x, clipRect.y, clipRect.width, clipRect.height );
        
        // set color
        Color top = animation.getColor( "top" );
        Color bottom = animation.getColor( "bottom" );
        
        if( getOrientation().isHorizontal() )
            g2.setPaint( new GradientPaint( 0, 0, top, 0, h, bottom ));
        else
            g2.setPaint( new GradientPaint( 0, 0, top, w, 0, bottom ));
        
        // draw
        g2.fillRect( clipRect.x, clipRect.y, clipRect.width, clipRect.height );
        
        g2.dispose();
    }
    
    private void setRoundRectClip( Graphics2D g2, int x, int y, int w, int h ){
        switch( getOrientation() ){
            case FREE_HORIZONTAL:
            case FREE_VERTICAL:
                g2.setClip( new RoundRectangle2D.Double( x, y, w, h, arc, arc ));
                break;
            case EAST_SIDED:
                g2.setClip( new RoundRectangle2D.Double( x-arc, y, w+arc, h, arc, arc ) );
                break;
            case NORTH_SIDED:
                g2.setClip( new RoundRectangle2D.Double( x, y, w, h+arc, arc, arc ) );
                break;
            case SOUTH_SIDED:
                g2.setClip( new RoundRectangle2D.Double( x, y-arc, w, h+arc, arc, arc ) );
                break;
            case WEST_SIDED:
                g2.setClip( new RoundRectangle2D.Double( x, y, w+arc, h, arc, arc ) );
                break;
        }
    }
}
