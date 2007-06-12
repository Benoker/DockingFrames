package bibliothek.gui.dock.themes.basic.action;

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionAdapter;
import java.awt.event.MouseMotionListener;

import javax.swing.Icon;
import javax.swing.JComponent;

import bibliothek.gui.dock.action.DropDownAction;

/**
 * An expanded {@link BasicButtonModel} that can handle the properties needed
 * to represent a {@link DropDownAction}.
 * @author Benjamin Sigg
 */
public abstract class BasicDropDownButtonModel extends BasicButtonModel{
    /** whether the mouse is currently over the dropdown area */
    private boolean overDropDown = false;
    
    /** whether the selected action is currently enabled */
    private boolean selectionEnabled = true;
    
    /**
     * Creates a new model.
     * @param owner the view of this model
     * @param trigger the callback used when the user clicks on the view
     */
    public BasicDropDownButtonModel( JComponent owner, BasicTrigger trigger ) {
        this( owner, trigger, true );
    }
    
    /**
     * Creates a new model.
     * @param owner the view of this model
     * @param trigger the callback used when the user clicks on the view
     * @param createListener whether the model should add a {@link MouseListener} and
     * a {@link MouseMotionListener} to the view or not.
     */
    public BasicDropDownButtonModel( JComponent owner, BasicTrigger trigger, boolean createListener ) {
        super( owner, trigger, createListener );
        if( createListener ){
            Listener listener = new Listener();
            owner.addMouseMotionListener( listener );
        }
    }
    
    @Override
    protected void setMouseInside( boolean mouseInside ) {
        super.setMouseInside( mouseInside );
        if( !mouseInside )
            setMouseOverDropDown( false );
    }
    
    /**
     * Sets whether the mouse is currently over the dropdown area of the 
     * view or not. Clients should not call this method unless the handle all
     * mouse events.
     * @param overDropDown whether the mouse is over the dropdown area
     */
    protected void setMouseOverDropDown( boolean overDropDown ) {
        this.overDropDown = overDropDown;
        changed();
    }
    
    /**
     * Tells whether the mouse is currently over the dropdown area of
     * the view or not.
     * @return whether the mouse is over the dropdown area
     */
    public boolean isMouseOverDropDown() {
        return overDropDown;
    }
    
    /**
     * Sets whether the selected child-action of the represented {@link DropDownAction}
     * is currently enabled or not.
     * @param selectionEnabled whether the action is enabled
     */
    public void setSelectionEnabled( boolean selectionEnabled ) {
        this.selectionEnabled = selectionEnabled;
        changed();
    }
    
    /**
     * Tells whether the selected child-action of the represented
     * {@link DropDownAction} is currently enabled or not.
     * @return whether the action is enabled
     */
    public boolean isSelectionEnabled() {
        return selectionEnabled;
    }
    
    @Override
    public Icon getPaintIcon() {
        return super.getPaintIcon( isEnabled() && isSelectionEnabled() );
    }
    
    /**
     * Tells whether the location x/y is over the dropdown area of the view or not.
     * The coordinates are in system of the {@link #getOwner() owner's} coordinate-system.
     * @param x the x-coordinate
     * @param y the y-coordinate
     * @return <code>true</code> if a click with the mouse should open the
     * selection-menu of the {@link DropDownAction}
     */
    protected abstract boolean inDropDownArea( int x, int y );
    
    /**
     * A listener ensuring that the {@link BasicDropDownButtonModel#isMouseOverDropDown() mouseOverDropDown}
     * property has always the correct value.
     * @author Benjamin Sigg
     */
    private class Listener extends MouseMotionAdapter{
        @Override
        public void mouseMoved( MouseEvent e ) {
            boolean over = inDropDownArea( e.getX(), e.getY() );
            if( over != overDropDown ){
                setMouseOverDropDown( over );
            }
        }
        
        @Override
        public void mouseDragged( MouseEvent e ) {
            boolean over = inDropDownArea( e.getX(), e.getY() );
            if( over != overDropDown ){
                setMouseOverDropDown( over );
            }        
        }
    }
}
