package bibliothek.layouts.testing;

import bibliothek.gui.dock.common.MultipleCDockableFactory;

public class MultipleTestFactory implements MultipleCDockableFactory<MultipleTestDockable, MultipleTestLayout>{
    public MultipleTestLayout create() {
        return new MultipleTestLayout();
    }

    public MultipleTestDockable read( MultipleTestLayout layout ) {
        MultipleTestDockable dockable = new MultipleTestDockable( this );
        dockable.setContent( layout.getContent() );
        return dockable;
    }

    public MultipleTestLayout write( MultipleTestDockable dockable ) {
        MultipleTestLayout layout = create();
        layout.setContent( dockable.getContent() );
        return layout;
    }
    
    public boolean match( MultipleTestDockable dockable, MultipleTestLayout layout ){
    	return false;
    }
}