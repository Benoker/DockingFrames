package bibliothek.paint;

import javax.swing.JFrame;
import javax.swing.JMenuBar;

import bibliothek.gui.dock.common.menu.RootMenuPiece;
import bibliothek.gui.dock.common.menu.SubmenuPiece;
import bibliothek.gui.dock.facile.FControl;
import bibliothek.gui.dock.facile.menu.FLayoutChoiceMenuPiece;
import bibliothek.gui.dock.facile.menu.FLookAndFeelMenuPiece;
import bibliothek.gui.dock.facile.menu.FSingleDockableListMenuPiece;
import bibliothek.gui.dock.facile.menu.FThemeMenuPiece;
import bibliothek.gui.dock.support.menu.SeparatingMenuPiece;
import bibliothek.paint.model.PictureRepository;
import bibliothek.paint.view.ViewManager;

public class Core {
    private boolean secure;
    
    public Core( boolean secure ){
        this.secure = secure;
    }
    
    public void startup(){
        JFrame frame = new JFrame( "Paint" );
        frame.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
        
        FControl control = new FControl( frame, secure );

        RootMenuPiece settings = new RootMenuPiece( "View", false );
        settings.add( new FSingleDockableListMenuPiece( control ));
        settings.add( new SeparatingMenuPiece( new FLayoutChoiceMenuPiece( control, false ), true, false, false ));
        
        RootMenuPiece layout = new RootMenuPiece( "Layout", false );
        layout.add( new SubmenuPiece( "LookAndFeel", true, new FLookAndFeelMenuPiece( control )));
        layout.add( new SubmenuPiece( "Layout", true, new FThemeMenuPiece( control )));
        JMenuBar bar = new JMenuBar();
        bar.add( settings.getMenu() );
        bar.add( layout.getMenu() );
        frame.setJMenuBar( bar );
        
        frame.getContentPane().add( control.getCenter().getComponent() );
        
        PictureRepository pictures = new PictureRepository();
        ViewManager manager = new ViewManager( control, pictures );
        
        frame.setBounds( 20, 20, 600, 500 );
        frame.setVisible( true );
    }
}
