package bibliothek.extension.gui.dock.theme.bubble;

import java.awt.*;

import javax.swing.JPanel;
import javax.swing.border.Border;

import bibliothek.extension.gui.dock.theme.BubbleTheme;
import bibliothek.gui.Dockable;
import bibliothek.gui.dock.DockableDisplayer;
import bibliothek.gui.dock.title.DockTitle;

public class BubbleDisplayer extends DockableDisplayer {
    private int borderSize = 2;
    private JPanel dockable;
    private BubbleColorAnimation animation;
    
    public BubbleDisplayer( BubbleTheme theme, Dockable dockable, DockTitle title ){
        super( dockable, title );
        
        animation = new BubbleColorAnimation( theme );
        animation.putColor( "high", "border.high.inactive" );
        animation.putColor( "low", "border.low.inactive" );
        
        setBorder( null );
    }
    
    @Override
    protected void addDockable( Component component ) {
        ensureDockable();
        dockable.add( component );
    }
    
    @Override
    protected void removeDockable( Component component ) {
        ensureDockable();
        dockable.remove( component );
    }
    
    @Override
    protected Component getComponent( Dockable dockable ) {
        ensureDockable();
        return this.dockable;
    }
    
    private void ensureDockable(){
        if( dockable == null ){
            dockable = new JPanel( new GridLayout( 1, 1 ));
            dockable.setBorder(  new OpenBorder() );
            add( dockable );
        }
    }
    
    private class OpenBorder implements Border{
        public Insets getBorderInsets( Component c ) {
            if( getTitle() == null )
                return new Insets( borderSize, borderSize, borderSize, borderSize );
            else{
                switch( getTitleLocation() ){
                    case BOTTOM: return new Insets( borderSize, borderSize, 0, borderSize );
                    case LEFT: return new Insets( borderSize, 0, borderSize, borderSize );
                    case RIGHT: return new Insets( borderSize, borderSize, borderSize, 0 );
                    case TOP: return new Insets( 0, borderSize, borderSize, borderSize );
                }
            }
            
            // error
            return new Insets( 0, 0, 0, 0 );
        }

        public boolean isBorderOpaque() {
            return true;
        }

        public void paintBorder( Component c, Graphics g, int x, int y, int width, int height ) {
            Color high = animation.getColor( "high" );
            Color low = animation.getColor( "low" );
            
            boolean noTitle = getTitle() == null;
            boolean top = noTitle || getTitleLocation() != Location.TOP;
            boolean left = noTitle || getTitleLocation() != Location.LEFT;
            boolean right = noTitle || getTitleLocation() != Location.RIGHT;
            boolean bottom = noTitle || getTitleLocation() != Location.BOTTOM;
            
            int highSize = borderSize / 2;
            int lowSize = borderSize - highSize;
            
            if( top ){
                g.setColor( high );
                g.fillRect( x, y, width, highSize );
                g.setColor( low );
                g.fillRect( x, y+highSize, width, lowSize );
            }
            
            if( left ){
                g.setColor( high );
                g.fillRect( x, y, highSize, height );
                g.setColor( low );
                if( top )
                    g.fillRect( x+highSize, y+highSize, lowSize, height-highSize );
                else
                    g.fillRect( x+highSize, y, lowSize, height );
            }
            
            if( right ){
                g.setColor( high );
                g.fillRect( x+width-borderSize, y, highSize, height );
                g.setColor( low );
                if( top )
                    g.fillRect( x+width-lowSize, y+highSize, lowSize, height-highSize );
                else
                    g.fillRect( x+width-lowSize, y, lowSize, height );
            }
            
            if( bottom ){
                g.setColor( high );
                g.fillRect( x, y+height-borderSize, width, highSize );
                g.setColor( low );
                g.fillRect( x, y+height-lowSize, width, lowSize );
            }
        }
    }
}
