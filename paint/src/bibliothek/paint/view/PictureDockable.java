package bibliothek.paint.view;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;

import javax.swing.JPanel;
import javax.swing.JScrollPane;

import bibliothek.gui.dock.facile.FMultipleDockable;
import bibliothek.gui.dock.facile.FMultipleDockableFactory;
import bibliothek.gui.dock.facile.action.FRadioGroup;
import bibliothek.paint.model.Picture;
import bibliothek.paint.model.PictureListener;
import bibliothek.paint.model.ShapeFactory;
import bibliothek.paint.model.ShapeUtils;
import bibliothek.paint.util.Resources;
import bibliothek.paint.view.action.EraseLastShape;
import bibliothek.paint.view.action.ShapeSelection;
import bibliothek.paint.view.action.ZoomIn;
import bibliothek.paint.view.action.ZoomOut;

/**
 * A {@link FMultipleDockable} showing one {@link Picture}, using a 
 * {@link Page} to do so.
 * @author Benjamin Sigg
 *
 */
public class PictureDockable extends FMultipleDockable {
	/** the page painting the picture */
    private Page page;

    /** an action erasing elements of the picture */
    private EraseLastShape eraser;
    
    /**
     * A listener to the picture, changing some properties when the picture changes.
     */
    private PictureListener listener = new PictureListener(){
    	public void pictureChanged(){
    		eraser.setEnabled( !getPicture().isEmpty() );
    	}
    };
    
    /**
     * Creates a new Dockable.
     * @param factory the factory which creates this kind of Dockable
     */
    public PictureDockable( FMultipleDockableFactory factory ){
        super( factory );
        setTitleText( "Page" );
        setCloseable( true );
        setMinimizable( true );
        setMaximizable( true );
        setExternalizable( true );
        setTitleIcon( Resources.getIcon( "dockable.picture" ) );
        
        page = new Page();
        getContentPane().setLayout( new GridLayout( 1, 1 ) );
        JPanel background = new JPanel( new GridBagLayout() );
        background.add( page, new GridBagConstraints( 0, 0, 1, 1, 1.0, 1.0, 
        		GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets( 0, 0, 0, 0 ), 0, 0 ) );
        getContentPane().add( new JScrollPane( background ));
        
        // add buttons to this dockable
        addAction( new ZoomIn( page ) );
        addAction( new ZoomOut( page ) );
        addSeparator();
        
        FRadioGroup group = new FRadioGroup();
        boolean first = true;
        
        for( ShapeFactory shapeFactory : ShapeUtils.getFactories() ){
            ShapeSelection button = new ShapeSelection( page, shapeFactory );
            group.add( button );
            addAction( button );
            
            // ensure that at least one button is selected
            if( first ){
                first = false;
                button.setSelected( true );
            }
        }
        eraser = new EraseLastShape( page );
        addAction( eraser );
        addSeparator();
    }
    
    /**
     * Sets the picture which will be painted on this PageDockable.
     * @param picture the new picture
     */
    public void setPicture( Picture picture ){
    	if( getPicture() != null )
    		getPicture().removeListener( listener );
    	
        page.setPicture( picture );
        setTitleText( picture == null ? "" : picture.getName() );
        eraser.setEnabled( picture != null && !picture.isEmpty() );
        
        if( picture != null )
        	picture.addListener( listener );
    }
    
    /**
     * Gets the picture which is painted on this Dockable.
     * @return the picture
     */
    public Picture getPicture(){
        return page.getPicture();
    }
    
    /**
     * Gets the page which paints the {@link Picture} of this PageDockable.
     * @return the page
     */
    public Page getPage() {
        return page;
    }
}
