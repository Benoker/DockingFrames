package bibliothek.chess.view;

import bibliothek.gui.DockController;
import bibliothek.gui.Dockable;
import bibliothek.gui.dock.dockable.DockableMovingImageFactory;
import bibliothek.gui.dock.dockable.MovingImage;
import bibliothek.gui.dock.themes.BasicTheme;
import bibliothek.gui.dock.themes.basic.BasicMovingImageFactory;
import bibliothek.gui.dock.title.DockTitle;
import bibliothek.gui.dock.title.DockTitleFactory;
import bibliothek.gui.dock.title.DockTitleRequest;
import bibliothek.gui.dock.title.TitleMovingImage;
import bibliothek.gui.dock.util.Priority;
 
/**
 * A theme that can hide all {@link DockTitle}s when used together with a 
 * {@link ChessBoard}.
 * @author Benjamin Sigg
 */
public class HidingTheme extends BasicTheme {
	private  boolean showTitles;
	
	/**
	 * Creates a new theme
	 */
    public HidingTheme(){
    	setShowTitles( false );
    }

    public void setShowTitles( boolean show ){
    	this.showTitles = show;
    	
    	if( show ){
            setMovingImageFactory( new BasicMovingImageFactory(){
                @Override
                public MovingImage create( DockController controller, Dockable dockable ) {
                    if( dockable instanceof ChessFigure ){
                        return new TitleMovingImage( dockable, new ChessDockTitle( dockable, null ) );
                    }
                    else{
                        return super.create( controller, dockable );
                    }
                }
            }, Priority.DEFAULT );
    	}
    	else{
            setMovingImageFactory( new DockableMovingImageFactory(){
                public MovingImage create( DockController controller, Dockable dockable ) {
                    return null;
                }
                public MovingImage create( DockController controller, DockTitle snatched ) {
                    return null;
                }
            }, Priority.DEFAULT );
    	}
    	
    	updateTitleFactory();
    }
    
    private void updateTitleFactory(){
    	DockController controller = getController();
    	if( controller != null ){
	    	if( showTitles ){
	    		controller.getDockTitleManager().registerTheme( "chess-board", null );
	    	}
	    	else{
	            controller.getDockTitleManager().registerTheme( "chess-board", new DockTitleFactory(){
	            	public void install( DockTitleRequest request ){
	    	        	// ignore	
	            	}
	            	
	            	public void request( DockTitleRequest request ){
	            		request.answer( null );
	            	}
	            	
	            	public void uninstall( DockTitleRequest request ){
	    	        	// ignore	
	            	}
	            });
	    	}
    	}
    }
    
    @Override
    public void install( DockController controller ) {
        super.install( controller );
        updateTitleFactory();
    }
    
    @Override
    public void uninstall( DockController controller ) {
        super.uninstall( controller );
        controller.getDockTitleManager().registerTheme( "chess-board", null );
    }
}
