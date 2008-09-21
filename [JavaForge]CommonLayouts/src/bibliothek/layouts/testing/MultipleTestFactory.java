package bibliothek.layouts.testing;

import java.awt.Color;

import bibliothek.gui.dock.common.MultipleCDockableFactory;

public class MultipleTestFactory implements MultipleCDockableFactory<MultipleTestDockable, MultipleTestLayout>{
    private Color color;
    
    public MultipleTestFactory( Color color ){
        this.color = color;
    }
    
    public Color getColor() {
        return color;
    }
    
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
}