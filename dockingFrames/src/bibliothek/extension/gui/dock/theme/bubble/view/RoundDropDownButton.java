package bibliothek.extension.gui.dock.theme.bubble.view;

import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.Icon;
import javax.swing.JComponent;

import bibliothek.extension.gui.dock.theme.BubbleTheme;
import bibliothek.extension.gui.dock.theme.bubble.BubbleColorAnimation;
import bibliothek.gui.dock.title.DockTitle.Orientation;
import bibliothek.gui.dock.util.DockUtilities;

public class RoundDropDownButton extends JComponent{
    private BubbleColorAnimation animation;
    private BubbleDropDownView view;
    
    private Orientation orientation = Orientation.FREE_HORIZONTAL;
    
    private Icon icon;
    private Icon disabledIcon;
    private Icon autoDisabledIcon;
    
    private Icon dropIcon;
    private Icon disabledDropIcon;
    
    private Listener listener = new Listener();
    
    private boolean selected = false;
    private boolean selectionEnabled = true;
    
    public RoundDropDownButton( BubbleTheme theme, BubbleDropDownView view ){
        animation = new BubbleColorAnimation( theme );
        animation.putColor( "background", "dropdown" );
        animation.addTask( new Runnable(){
            public void run() {
                repaint();
            }
        });
        
        this.view = view;
        
        dropIcon = createDropIcon();
        
        addMouseListener( listener );
    }
    
    @Override
    public void updateUI() {
        super.updateUI();
        if( view != null )
            view.updateUI();
    }
    
    public void setSelectionEnabled( boolean selectionEnabled ) {
        this.selectionEnabled = selectionEnabled;
        repaint();
    }
    
    public boolean isSelectionEnabled() {
        return selectionEnabled;
    }
    
    public void setIcon( Icon icon ){
        this.icon = icon;
        autoDisabledIcon = null;
        revalidate();
        repaint();
    }
    
    public void setDisabledIcon( Icon disabledIcon ) {
        this.disabledIcon = disabledIcon;
        autoDisabledIcon = null;
        revalidate();
        repaint();
    }
    
    public void setSelected( boolean selected ) {
        this.selected = selected;
        listener.updateColors();
    }
    
    public void setOrientation( Orientation orientation ) {
        this.orientation = orientation;
        revalidate();
    }
    
    public Orientation getOrientation() {
        return orientation;
    }
    
    @Override
    public Dimension getPreferredSize() {
        if( isPreferredSizeSet() )
            return super.getPreferredSize();
        
        int w = -1;
        int h = -1;
        
        if( icon != null ){
            w = icon.getIconWidth();
            h = icon.getIconHeight();
        }
        if( disabledIcon != null ){
            w = Math.max( w, disabledIcon.getIconWidth() );
            h = Math.max( h, disabledIcon.getIconHeight() );
        }
        
        if( w == -1 )
            w = 10;
        
        if( h == -1 )
            h = 10;
        
        if( orientation.isHorizontal() )
            return new Dimension( (int)(1.5 * w + 2*dropIcon.getIconWidth()), (int)(1.5 * h));
        else
            return new Dimension( (int)(1.5 * w), (int)(1.5 * h + 2 * dropIcon.getIconHeight()) );
    }
    
    @Override
    protected void paintComponent( Graphics g ) {
        Icon drop = dropIcon;
        if( !isEnabled() ){
            if( disabledDropIcon == null )
                disabledDropIcon = DockUtilities.disabledIcon( this, dropIcon );
            drop = disabledDropIcon;
        }
        
        Graphics2D g2 = (Graphics2D)g.create();
        g2.setRenderingHint( RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON );
        
        int x = 0;
        int y = 0;
        int w = getWidth();
        int h = getHeight();
        
        Icon icon = getPaintIcon( isEnabled() && selectionEnabled );
        
        if( orientation.isHorizontal() ){
            g2.setColor( animation.getColor( "background" ) );
            g2.fillRoundRect( x, y, w, h, h/2, h/2 );
            if( icon != null ){
                icon.paintIcon( this, g, (int)(x+0.25*icon.getIconWidth()), y+(h-icon.getIconHeight())/2 );
            }
            drop.paintIcon( this, g, (int)(x+w-1.25*drop.getIconWidth()), y+(h-drop.getIconHeight())/2 );
        }
        else{
            g2.setColor( animation.getColor( "background" ) );
            g2.fillRoundRect( x, y, w, h, w/2, w/2 );
            if( icon != null ){
                icon.paintIcon( this, g, x+(w-icon.getIconWidth())/2, (int)(y + 0.25*icon.getIconHeight()));
            }
            drop.paintIcon( this, g, x+(w-icon.getIconWidth())/2, (int)(y+h-1.25*drop.getIconHeight()) );
        }
        
        g2.dispose();
    }
    
    private void triggered( Point mouse ){
        boolean dropdown = false;
        if( orientation.isHorizontal() )
            dropdown = mouse.x > getWidth() - 2 * dropIcon.getIconWidth();
        else{
            dropdown = mouse.y > getHeight() - 2 * dropIcon.getIconHeight();
        }
        view.trigger( dropdown );
    }
    
    /**
     * Creates an icon that is shown in the smaller subbutton of this button.
     * @return the icon
     */
    protected Icon createDropIcon(){
        return new Icon(){
            public int getIconHeight(){
                return 7;
            }
            public int getIconWidth(){
                return 7;
            }
            public void paintIcon( Component c, Graphics g, int x, int y ){
                x++;
                g.setColor( getForeground() );
                g.drawLine( x, y+1, x+4, y+1 );
                g.drawLine( x+1, y+2, x+3, y+2 );
                g.drawLine( x+2, y+3, x+2, y+3 );
            }
        };
    }
    
    /**
     * Gets the icon that will be painted in the center of this button
     * @param enabled whether the enabled or the disabled icon is requested.
     * @return the icon, might be <code>null</code>
     */
    protected Icon getPaintIcon( boolean enabled ){
        if( enabled )
            return icon;
        else{
            if( disabledIcon != null )
                return disabledIcon;
            
            if( autoDisabledIcon == null ){
                autoDisabledIcon = DockUtilities.disabledIcon( this, icon );
            }
                
            return autoDisabledIcon;
        }
    }
    
    private class Listener extends MouseAdapter{
        private boolean pressed = false;
        private boolean entered = false;
        
        public void updateColors(){
            String postfix = "";
            if( selected )
                postfix = ".selected";
            
            if( pressed )
                animation.putColor( "background", "dropdown.pressed" + postfix );
            else if( entered )
                animation.putColor( "background", "dropdown.mouse" + postfix );
            else
                animation.putColor( "background", "dropdown" + postfix );
        }
        
        @Override
        public void mouseEntered( MouseEvent e ) {
            entered = true;
            updateColors();
        }
        @Override
        public void mouseExited( MouseEvent e ) {
            entered = false;
            updateColors();
        }
        @Override
        public void mousePressed( MouseEvent e ) {
            if( !pressed ){
                if( e.getButton() == MouseEvent.BUTTON1 ){
                    pressed = true;
                    updateColors();
                }
            }
        }
        
        @Override
        public void mouseReleased( MouseEvent e ) {
            if( pressed && e.getButton() == MouseEvent.BUTTON1 ){
                pressed = false;
                Point mouse = e.getPoint();
                if( contains( mouse )){
                    triggered( mouse );
                    entered = true;
                }
                else{
                    entered = false;
                }
                updateColors();
            }
        }
    }
}
