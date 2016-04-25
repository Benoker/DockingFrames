package bibliothek.demonstration.util;

import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.LookAndFeel;

import bibliothek.gui.dock.facile.menu.LookAndFeelMenuPiece;
import bibliothek.gui.dock.facile.menu.RootMenuPiece;
import bibliothek.gui.dock.support.lookandfeel.LookAndFeelList;

/**
 * A menu that contains an item for each available {@link LookAndFeel}. The
 * set of <code>LookAndFeel</code>s is determined through a {@link LookAndFeelList}.
 * @author Benjamin Sigg
 *
 */
public class LookAndFeelMenu extends JMenu{
    /**
     * Creates a new menu.
     * @param owner the frame in which this menu will be shown. This menu
     * destroys itself when <code>owner</code> is closed.
     * @param list the set of available {@link LookAndFeel}s
     */
    public LookAndFeelMenu( JFrame owner, LookAndFeelList list ){
        setText( "Look and Feel" );
        RootMenuPiece root = new RootMenuPiece( this );
        root.add( new LookAndFeelMenuPiece( owner, list ) );
    }
}