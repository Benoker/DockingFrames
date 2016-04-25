package bibliothek.help.view.dock;

import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.BorderFactory;
import javax.swing.Icon;
import javax.swing.JComponent;
import javax.swing.event.MouseInputListener;

import bibliothek.gui.DockFrontend;
import bibliothek.gui.Dockable;
import bibliothek.gui.dock.DockElement;
import bibliothek.gui.dock.DockElementRepresentative;

/**
 * A button that represents a {@link Dockable}. This button only paints
 * the icon of the {@link Dockable}.
 * @author Benjamin Sigg
 */
public class DockableButton extends JComponent implements DockElementRepresentative{
    private DockFrontend frontend;
    private Dockable dockable;
    
    public DockableButton( DockFrontend frontend, Dockable dockable ){
        this.frontend = frontend;
        this.dockable = dockable;
        setBorder( BorderFactory.createEtchedBorder() );
        setToolTipText( dockable.getTitleText() );
        
        addMouseListener( new MouseAdapter(){
            @Override
            public void mouseClicked( MouseEvent e ) {
                if( !e.isPopupTrigger() ){
                    if( !e.isConsumed() ){
                        e.consume();
                        DockableButton.this.frontend.show( DockableButton.this.dockable );
                    }
                }
            }
        });
    }
    
    @Override
    public Dimension getPreferredSize() {
        Insets insets = getInsets();
        Icon icon = dockable.getTitleIcon();
        
        int width = insets.left + insets.right;
        int height = insets.top + insets.bottom;
        
        if( icon != null ){
            width += icon.getIconWidth() + 2;
            height += icon.getIconHeight() + 2;
        }
        
        return new Dimension( width, height ); 
    }
    
    @Override
    protected void paintComponent( Graphics g ) {
        super.paintComponent( g );
        Icon icon = dockable.getTitleIcon();
        if( icon != null ){
            icon.paintIcon( this, g, 
                    (getWidth()-icon.getIconWidth())/2, 
                    (getHeight()-icon.getIconHeight())/2 );
        }
    }
    
    public void addMouseInputListener( MouseInputListener listener ) {
        addMouseListener( listener );
        addMouseMotionListener( listener );
    }

    public void removeMouseInputListener( MouseInputListener listener ) {
        removeMouseListener( listener );
        removeMouseMotionListener( listener );
    }
    
    public Component getComponent() {
        return this;
    }

    public DockElement getElement() {
        return dockable;
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
        if( popupTrigger )
            return click;
        
        return null;
    }
}
