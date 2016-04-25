package bibliothek.extension.gui.dock.theme.eclipse.stack.tab;

import java.awt.Color;
import java.awt.Component;
import java.awt.Window;
import java.awt.event.HierarchyEvent;
import java.awt.event.HierarchyListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.SwingUtilities;
import javax.swing.border.MatteBorder;

import bibliothek.gui.DockController;
import bibliothek.gui.Dockable;
import bibliothek.gui.dock.event.DockableFocusEvent;
import bibliothek.gui.dock.event.DockableFocusListener;
import bibliothek.gui.dock.themes.color.TabColor;
import bibliothek.gui.dock.util.color.ColorCodes;

/**
 * Default implementation of an {@link InvisibleTab}, this 
 * implementation tells an {@link InvisibleTabPane} to use different borders
 * for their children depending on whether they are focused and selected.
 * @author Benjamin Sigg
 */
@ColorCodes({"stack.tab.border", "stack.tab.border.selected", "stack.tab.border.selected.focused", "stack.tab.border.selected.focuslost"})
public class DefaultInvisibleTab implements InvisibleTab{
    protected final TabColor colorStackTabBorder;
    protected final TabColor colorStackTabBorderSelected;
    protected final TabColor colorStackTabBorderSelectedFocused;
    protected final TabColor colorStackTabBorderSelectedFocusLost;
    
    private WindowActiveObserver observer = new WindowActiveObserver();
	
    private InvisibleTabPane pane;
    private Dockable dockable;
    private DockController controller;
    
    private Color oldColor;
    
    private DockableFocusListener focusListener = new DockableFocusListener(){
    	public void dockableFocused( DockableFocusEvent event ){
	    	if( event.getOldFocusOwner() == dockable || event.getNewFocusOwner() == dockable ){	
	    		updateBorder();
	    	}
    	}
    };
    
    /**
     * Creates a new tab.
     * @param pane the owner
     * @param dockable the element this tab represents
     */
    public DefaultInvisibleTab( InvisibleTabPane pane, Dockable dockable ){
    	if( pane == null )
    		throw new IllegalArgumentException( "pane must not be null" );
    	
    	if( dockable == null )
    		throw new IllegalArgumentException( "dockable must not be null" );
    	
    	this.pane = pane;
    	this.dockable = dockable;
    	
    	colorStackTabBorder = new InvisibleTabColor( "stack.tab.border" );
    	colorStackTabBorderSelected = new InvisibleTabColor( "stack.tab.border.selected" );
    	colorStackTabBorderSelectedFocused = new InvisibleTabColor( "stack.tab.border.selected.focused" );
    	colorStackTabBorderSelectedFocusLost = new InvisibleTabColor( "stack.tab.border.selected.focuslost" );
    	
    	updateBorder();
    }
    
    public void setController( DockController controller ){
    	if( this.controller != null )
    		this.controller.removeDockableFocusListener( focusListener );
    	
    	this.controller = controller;
    	
    	if( controller != null )
    		controller.addDockableFocusListener( focusListener );
    	
    	colorStackTabBorder.connect( controller );
    	colorStackTabBorderSelected.connect( controller );
    	colorStackTabBorderSelectedFocused.connect( controller );
    	colorStackTabBorderSelectedFocusLost.connect( controller );
    	
    	if( controller == null || dockable == null )
    		observer.observe( null );
    	else
    		observer.observe( dockable.getComponent() );
    	
    	updateBorder();
    }
    
	private void updateBorder(){
		if( controller != null ){
			Color color;

			Window window = observer.getWindow();
			boolean focusTemporarilyLost = false;

			if( window != null ){
				focusTemporarilyLost = !window.isActive();
			}

			if( pane.getSelectedDockable() == dockable ){
				if( controller.getFocusedDockable() == dockable ){
					if( focusTemporarilyLost )
						color = colorStackTabBorderSelectedFocusLost.value();
					else
						color = colorStackTabBorderSelectedFocused.value();
				}
				else
					color = colorStackTabBorderSelected.value();
			}
			else
				color = colorStackTabBorder.value();

			if( !color.equals( oldColor )){
				oldColor = color;
				pane.setBorder( dockable, new MatteBorder( 2, 2, 2, 2, color) );
			}
		}
	}
	
	/**
	 * This {@link TabColor} calls {@link DefaultInvisibleTab#updateBorder()}
	 * if its color changes.
	 * @author Benjamin Sigg
	 */
	private class InvisibleTabColor extends TabColor{
		/**
		 * Creates a new color
		 * @param id the identifier of this color
		 */
		public InvisibleTabColor( String id ){
			super( id, pane.getStation(), dockable, Color.BLACK );
		}
		
		@Override
		protected void changed( Color oldValue, Color newValue ){
			updateBorder();
		}
	}
	
    /**
     * Listens to the window ancestor of a {@link Component} and calls
     * {@link DefaultInvisibleTab#updateBorder()} if the activation state
     * of the window changes.
     * @author Benjamin Sigg
     */
    private class WindowActiveObserver extends WindowAdapter implements HierarchyListener{
        private Window window;
        private Component component;
        
        /**
         * Sets the component which needs to be observed
         * @param component the observed component
         */
        public void observe( Component component ){
        	if( this.component != component ){
        		if( this.component != null ){
        			if( window != null ){
        				window.removeWindowListener( this );
        				window = null;
        			}
        			this.component.removeHierarchyListener( this );
        		}
        		
        		this.component = component;
        		if( this.component != null ){
        			this.component.addHierarchyListener( this );
        			window = SwingUtilities.getWindowAncestor( component );
        			if( window != null ){
        				window.addWindowListener( this );
        			}
        		}
        	}
        }
        
        /**
         * Gets the currently observed window.
         * @return the window, can be <code>null</code>
         */
        public Window getWindow(){
			return window;
		}
        
        public void hierarchyChanged( HierarchyEvent e ){
            Window newWindow = SwingUtilities.getWindowAncestor(component);

            long lFlags = e.getChangeFlags();
            // update current found window only if parent has changed
            if (window != newWindow && (lFlags & HierarchyEvent.PARENT_CHANGED) != 0) {
               if (window != null) {
                  window.removeWindowListener(this);
               }
               if (newWindow != null) {
                  newWindow.addWindowListener(this);
                  updateBorder();
               }
               window = newWindow;
            }
        }
        
        @Override
        public void windowActivated( WindowEvent e ){
            updateBorder();
        }
        
        @Override
        public void windowDeactivated( WindowEvent e ){
            updateBorder();
        }
    }
}
