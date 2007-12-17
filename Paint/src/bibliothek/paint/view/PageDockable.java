package bibliothek.paint.view;

import java.awt.GridLayout;

import bibliothek.gui.dock.facile.FMultipleDockable;
import bibliothek.gui.dock.facile.action.FRadioGroup;
import bibliothek.paint.model.ShapeFactory;
import bibliothek.paint.model.ShapeUtils;

public class PageDockable extends FMultipleDockable {
    private Page page;
    
    public PageDockable(){
        super( PageFactory.FACTORY );
        setTitleText( "Page" );
        page = new Page();
        getContentPane().setLayout( new GridLayout( 1, 1 ) );
        getContentPane().add( page );
        
        // add buttons to this dockable
        FRadioGroup group = new FRadioGroup();
        boolean first = true;
        
        for( ShapeFactory<?> factory : ShapeUtils.getFactories() ){
            ShapeSelectionButton button = new ShapeSelectionButton( page, factory );
            group.add( button );
            addAction( button );
            
            // ensure that at least one button is selected
            if( first ){
                first = false;
                button.setSelected( true );
            }
        }
        addSeparator();
    }
    
    public Page getPage() {
        return page;
    }
}
