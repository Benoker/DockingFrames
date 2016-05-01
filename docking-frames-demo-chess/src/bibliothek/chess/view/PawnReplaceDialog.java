package bibliothek.chess.view;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JPanel;

import bibliothek.chess.model.Figure;
import bibliothek.chess.util.Utils;

/**
 * A dialog offering four buttons. The buttons tell into which figure a 
 * pawn is transformed.
 * @author Benjamin Sigg
 * 
 */
public class PawnReplaceDialog extends JDialog{
	/** button for a transformation of a pawn into a queen */
    private JButton queen = new JButton();
    /** button for a transformation of a pawn into a rock */
    private JButton rock = new JButton();
    /** button for a transformation of a pawn into a bishop */
    private JButton bishop = new JButton();
    /** button for a transformation of a pawn into a knight */
    private JButton knight = new JButton();
    
    /** the pawn which will be transformed */
    private Figure current;
    
    /**
     * Creates a new dialog.
     * @param owner the frame over which the dialog has to appear
     */
    public PawnReplaceDialog( JFrame owner ){
        super( owner, "Replace", true );
        setDefaultCloseOperation( DO_NOTHING_ON_CLOSE );
        
        setLayout( new GridBagLayout() );
        JPanel panel = new JPanel( new GridLayout( 1, 4 ));
        add( panel, new GridBagConstraints( 0, 0, 1, 1, 1.0, 1.0,
                GridBagConstraints.CENTER, GridBagConstraints.NONE,
                new Insets( 2, 2, 2, 2 ), 0, 0 ));
        
        panel.add( queen );
        panel.add( rock );
        panel.add( bishop );
        panel.add( knight );
        
        queen.addActionListener( new Listener( Figure.Type.QUEEN ) );
        rock.addActionListener( new Listener( Figure.Type.ROCK ) );
        bishop.addActionListener( new Listener( Figure.Type.BISHOP ) );
        knight.addActionListener( new Listener( Figure.Type.KNIGHT ) );
    }
    
    /**
     * Pops up the dialog and waits until the user has chosen a figure into
     * which <code>pawn</code> will be transformed.
     * @param pawn the pawn which will be transformed
     * @return the replacement for <code>pawn</code>
     */
    public Figure replace( Figure pawn ){
        current = pawn;
        
        queen.setIcon( Utils.getChessIcon( Figure.Type.QUEEN.getSign(), current.getPlayer(), 48 ) );
        rock.setIcon( Utils.getChessIcon( Figure.Type.ROCK.getSign(), current.getPlayer(), 48 ) );
        bishop.setIcon( Utils.getChessIcon( Figure.Type.BISHOP.getSign(), current.getPlayer(), 48 ) );
        knight.setIcon( Utils.getChessIcon( Figure.Type.KNIGHT.getSign(), current.getPlayer(), 48 ) );
        
        pack();
        setLocationRelativeTo( getOwner() );
        
        setVisible( true );
        
        Figure selection = current;
        current = null;
        return selection;
    }
    
    /**
     * A listener used for one of the four buttons of the {@link PawnReplaceDialog}.
     * @author Benjamin Sigg
     */
    private class Listener implements ActionListener{
    	/** the figure for which the button stands */
        private Figure.Type type;
        
        /**
         * Creates a new listener.
         * @param type the figure for which the observed button stands
         */
        public Listener( Figure.Type type ){
            this.type = type;
        }
        
        public void actionPerformed( ActionEvent e ) {
            setVisible( false );
            current = new Figure( current.getBoard(), current.getPlayer(), type, current.getRow(), current.getColumn());
        }
    }
}
