package bibliothek.chess.view;

import java.awt.Color;

import bibliothek.chess.model.Player;
import bibliothek.gui.Dockable;
import bibliothek.gui.dock.themes.basic.BasicDockTitle;
import bibliothek.gui.dock.title.ControllerTitleFactory;
import bibliothek.gui.dock.title.DockTitle;
import bibliothek.gui.dock.title.DockTitleFactory;
import bibliothek.gui.dock.title.DockTitleRequest;
import bibliothek.gui.dock.title.DockTitleVersion;

/**
 * A {@link DockTitle} sometimes shown atop of a {@link ChessFigure}. This title
 * can either be "white" or "black", the color depends on the owner of the
 * figure for which this title is shown.
 * @author Benjamin Sigg
 */
public class ChessDockTitle extends BasicDockTitle {
	/**
	 * A factory creating instances of {@link ChessDockTitle}
	 */
    public static final DockTitleFactory FACTORY = new DockTitleFactory(){
    	public void install( DockTitleRequest request ){
    		Dockable dockable = request.getTarget();
    		if( !(dockable instanceof ChessFigure )){
    			ControllerTitleFactory.INSTANCE.install( request );
    		}
    	}
    	
    	public void uninstall( DockTitleRequest request ){
    		Dockable dockable = request.getTarget();
    		if( !(dockable instanceof ChessFigure )){
    			ControllerTitleFactory.INSTANCE.uninstall( request );
    		}
    	}
    	
    	public void request( DockTitleRequest request ){ 
    		Dockable dockable = request.getTarget();
    		if( dockable instanceof ChessFigure ){
                request.answer( new ChessDockTitle( dockable, request.getVersion() ) );
    		}
            else{
                ControllerTitleFactory.INSTANCE.request( request );
            }
    	}
    };
   
    /**
     * Creates a new title.
     * @param dockable the element for which this title is shown
     * @param origin for what purpose this title was created
     */
    public ChessDockTitle( Dockable dockable, DockTitleVersion origin ) {
        super( dockable, origin );
        updateUIColors();
    }
    
    @Override
    public void updateUI() {
        super.updateUI();
        updateUIColors();
    }
    
    /**
     * Ensures that the colors of this title are set correctly.
     */
    private void updateUIColors(){
        Dockable dockable = getDockable();
        if( dockable != null ){
            ChessFigure figure = (ChessFigure)dockable;
            if( figure.getFigure().getPlayer() == Player.WHITE ){
                setActiveLeftColor( Color.WHITE );
                setActiveRightColor( Color.LIGHT_GRAY );
                setActiveTextColor( Color.BLACK );
                
                setInactiveLeftColor( Color.LIGHT_GRAY );
                setInactiveRightColor( Color.GRAY );
                setInactiveTextColor( Color.DARK_GRAY );
            }
            else{
                setActiveLeftColor( Color.DARK_GRAY );
                setActiveRightColor( Color.BLACK );
                setActiveTextColor( Color.WHITE );
                
                setInactiveLeftColor( Color.GRAY );
                setInactiveRightColor( Color.DARK_GRAY );
                setInactiveTextColor( Color.LIGHT_GRAY );            
            }
        }
    }
}
