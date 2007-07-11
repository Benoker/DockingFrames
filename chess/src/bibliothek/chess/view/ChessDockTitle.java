package bibliothek.chess.view;

import java.awt.Color;

import bibliothek.chess.model.Player;
import bibliothek.gui.DockStation;
import bibliothek.gui.Dockable;
import bibliothek.gui.dock.themes.basic.BasicDockTitle;
import bibliothek.gui.dock.title.ControllerTitleFactory;
import bibliothek.gui.dock.title.DockTitle;
import bibliothek.gui.dock.title.DockTitleFactory;
import bibliothek.gui.dock.title.DockTitleVersion;

public class ChessDockTitle extends BasicDockTitle {
    public static final DockTitleFactory FACTORY = new DockTitleFactory(){

        public DockTitle createDockableTitle( Dockable dockable, DockTitleVersion version ) {
            if( dockable instanceof ChessFigure )
                return new ChessDockTitle( dockable, version );
            else
                return ControllerTitleFactory.INSTANCE.createDockableTitle( dockable, version );
        }

        public <D extends Dockable & DockStation> DockTitle createStationTitle( D dockable, DockTitleVersion version ) {
            return ControllerTitleFactory.INSTANCE.createStationTitle( dockable, version );
        }
    };
    
    public ChessDockTitle( Dockable dockable, DockTitleVersion origin ) {
        super( dockable, origin );
        updateUIColors();
    }
    
    @Override
    public void updateUI() {
        super.updateUI();
        updateUIColors();
    }
    
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
