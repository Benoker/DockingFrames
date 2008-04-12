package bibliothek.gui.dock.themes.basic;

import javax.swing.BorderFactory;

import bibliothek.gui.dock.focus.DefaultDockableSelection;

public class BasicDockableSelection extends DefaultDockableSelection {
    
    public BasicDockableSelection(){
        setBorder( BorderFactory.createRaisedBevelBorder() );
    }
    
}
