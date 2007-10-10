package bibliothek.demonstration.util;

import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JRadioButtonMenuItem;
import javax.swing.LookAndFeel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import bibliothek.demonstration.util.LookAndFeelList.Info;

/**
 * A menu that contains an item for each available {@link LookAndFeel}. The
 * set of <code>LookAndFeel</code>s is determined through a {@link LookAndFeelList}.
 * @author Benjamin Sigg
 *
 */
public class LookAndFeelMenu extends JMenu{
    /** the item for the default-<code>LookAndFeel</code> */
    private JRadioButtonMenuItem defaultButton;
    /** the item for the systems imitating <code>LookAndFeel</code> */
    private JRadioButtonMenuItem systemButton;
    /** a list of buttons, one for each {@link LookAndFeel} */
    private JRadioButtonMenuItem[] buttons;
    
    /** the list of available <code>LookAndFeel</code>s */
    private LookAndFeelList list;
    /** whether the <code>LookAndFeel</code> is currently changing or not */
    private boolean onChange = false;
    
    /**
     * Creates a new menu.
     * @param owner the frame in which this menu will be shown. This menu
     * destroys itself when <code>owner</code> is closed.
     * @param list the set of available {@link LookAndFeel}s
     */
    public LookAndFeelMenu( JFrame owner, LookAndFeelList list ){
        this.list = list;
        setText( "Look and Feel" );
        
        defaultButton = new JRadioButtonMenuItem( "Default: " + list.getDefault().getName() );
        defaultButton.addItemListener( new SetListener( defaultButton, list.getDefault() ));
        add( defaultButton );
        
        systemButton = new JRadioButtonMenuItem( list.getSystem().getName() );
        systemButton.addItemListener( new SetListener( systemButton, list.getSystem() ));
        add( systemButton );
        
        addSeparator();
        
        buttons = new JRadioButtonMenuItem[ list.getInfoCount() ];
        for( int i = 0; i < buttons.length; i++ ){
            buttons[i] = new JRadioButtonMenuItem( list.getInfo( i ).getName() );
            SetListener listener = new SetListener( buttons[i], list.getInfo( i ));
            buttons[i].addItemListener( listener );
            add( buttons[i] );
        }
        
        final ChangeListener changeListener = new ChangeListener(){
            public void stateChanged( ChangeEvent e ) {
                changed();
            }
        };
        
        list.addChangeListener( changeListener );
        
        owner.addWindowListener( new WindowAdapter(){
        	@Override
        	public void windowClosing( WindowEvent e ){
        		LookAndFeelMenu.this.list.removeChangeListener( changeListener );
        	}
        });
        
        changed();
    }
    
    /**
     * Called when the {@link LookAndFeel} has been changed. Ensures
     * that the correct items in this menu are selected.
     */
    private void changed(){
        onChange = true;
        
        Info current = list.getCurrent();
        defaultButton.setSelected( list.getDefault() == current );
        systemButton.setSelected( list.getSystem() == current );
        
        for( int i = 0;  i < buttons.length; i++ ){
            buttons[i].setSelected( current == list.getInfo( i ));
        }
        
        onChange = false;
    }
    
    /**
     * A listener to one item of a {@link LookAndFeelMenu}, this listener
     * exchanges the {@link LookAndFeel} when the observed item is clicked.
     * @author Benjamin Sigg
     *
     */
    private class SetListener implements ItemListener{
        /** the observed item */
        private JRadioButtonMenuItem item;
        /** the {@link LookAndFeel} */
        private Info info;
        
        /**
         * Creates a new listener. The listener is not added to
         * item, that's the responsibility of the client.
         * @param item the item that will be observed
         * @param info the {@link LookAndFeel} that will be used
         * when <code>item</code> is clicked
         */
        public SetListener( JRadioButtonMenuItem item, Info info ){
            this.info = info;
            this.item = item;
        }
        
        public void itemStateChanged( ItemEvent e ) {
            if( !onChange ){
                if( item.isSelected() )
                    list.setLookAndFeel( info );
                else
                    item.setSelected( true );
            }
        }
    }
}
