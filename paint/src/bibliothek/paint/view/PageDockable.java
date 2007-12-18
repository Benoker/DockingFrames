package bibliothek.paint.view;

import java.awt.GridLayout;

import bibliothek.gui.dock.facile.FMultipleDockable;
import bibliothek.gui.dock.facile.FMultipleDockableFactory;
import bibliothek.gui.dock.facile.action.FRadioGroup;
import bibliothek.paint.model.Picture;
import bibliothek.paint.model.ShapeFactory;
import bibliothek.paint.model.ShapeUtils;

public class PageDockable extends FMultipleDockable {
    private Page page;
    
    public PageDockable( FMultipleDockableFactory factory ){
        super( factory );
        setTitleText( "Page" );
        setCloseable( true );
        setMinimizable( true );
        setMaximizable( true );
        setExternalizable( true );
        
        page = new Page();
        getContentPane().setLayout( new GridLayout( 1, 1 ) );
        getContentPane().add( page );
        
        // add buttons to this dockable
        FRadioGroup group = new FRadioGroup();
        boolean first = true;
        
        for( ShapeFactory shapeFactory : ShapeUtils.getFactories() ){
            ShapeSelectionButton button = new ShapeSelectionButton( page, shapeFactory );
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
    
    public void setPicture( Picture picture ){
        page.setPicture( picture );
        setTitleText( picture == null ? "" : picture.getName() );
    }
    
    public Picture getPicture(){
        return page.getPicture();
    }
    
    public Page getPage() {
        return page;
    }
}
