package bibliothek.demonstration.util;

import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JRadioButtonMenuItem;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import bibliothek.demonstration.util.LookAndFeelList.Info;


public class LookAndFeelMenu extends JMenu{
    private JRadioButtonMenuItem defaultButton, systemButton;
    private JRadioButtonMenuItem[] buttons;
    
    private LookAndFeelList list;
    private boolean onChange = false;
    
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
        	public void windowClosed( WindowEvent e ){
        		LookAndFeelMenu.this.list.removeChangeListener( changeListener );
        	}
        });
        
        changed();
    }
    
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
    
    private class SetListener implements ItemListener{
        private JRadioButtonMenuItem item;
        private Info info;
        
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
