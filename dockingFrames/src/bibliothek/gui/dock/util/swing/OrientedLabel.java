package bibliothek.gui.dock.util.swing;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;

import javax.swing.Icon;
import javax.swing.JPanel;

import bibliothek.gui.dock.util.font.FontModifier;

/**
 * A label which draws some text, and can change the layout of the text 
 * between horizontal and vertical.
 * @author Benjamin Sigg
 */
public class OrientedLabel extends JPanel{
    /** The label which really paints the text */
    private DLabel label = new DLabel();
    
    /** the original font of {@link #label} */
    private Font originalFont;
    
    /** icon painted on this label */
    private Icon icon;
    
    /** distance between icon and border */
    private int iconOffset = 2;
    
    /** distance between icon and text */
    private int iconTextDistance = 2;
    
    /** whether the {@link #originalFont} has been set */
    private boolean originalFontSet = false;
    
    /** The text on the label */
    private String text;
    
    /** the current orientation */
    private boolean horizontal = true;
    
    /**
     * Creates a new label with no text
     */
    public OrientedLabel(){
        setOpaque( false );
        label.setOpaque( false );
        label.setAlignmentX( 0 );
    }
    
    /**
     * Sets the icon which will be painted on the left or on the top side.
     * @param icon the icon, can be <code>null</code>
     */
    public void setIcon( Icon icon ){
		this.icon = icon;
		revalidate();
		repaint();
	}
    
    /**
     * Gets the icon of this label
     * @return the icon, can be <code>null</code>
     */
    public Icon getIcon(){
		return icon;
	}
    
    /**
     * Sets the distance between icon and the three adjacent borders.
     * @param iconOffset the distance
     */
    public void setIconOffset( int iconOffset ){
		this.iconOffset = iconOffset;
		revalidate();
		repaint();
	}
    
    /**
     * Gets the distance between icon the the tree adjacent borders.
     * @return the distance
     */
    public int getIconOffset(){
		return iconOffset;
	}
    
    /**
     * Sets the distance between icon and text.
     * @param iconTextDistance the gap
     */
    public void setIconTextDistance( int iconTextDistance ){
		this.iconTextDistance = iconTextDistance;
		revalidate();
		repaint();
	}
    
    /**
     * Gets the distance between icon and text.
     * @return the gap
     */
    public int getIconTextDistance(){
		return iconTextDistance;
	}
    
    /**
     * Sets the orientation.
     * @param horizontal whether the label is painted horizontal
     */
    public void setHorizontal( boolean horizontal ){
		this.horizontal = horizontal;
		revalidate();
	}
    
    /**
     * Gets the orientation.
     * @return whether the label is painted horizontal
     */
    public boolean isHorizontal(){
		return horizontal;
	}
    
    /**
     * Sets the text of this label
     * @param text the text, <code>null</code> is allowed
     */
    public void setText( String text ){
        this.text = text;
        label.setText( text == null ? null : "  " + text );
        revalidate();
        repaint();
    }
    
    /**
     * Gets the text of this label
     * @return the text, may be <code>null</code>
     */
    public String getText(){
        return text;
    }
    
    @Override
    public void setForeground( Color fg ) {
        super.setForeground(fg);
        if( label != null )
            label.setForeground( fg );
    }
    
    @Override
    public void setBackground( Color bg ) {
        super.setBackground(bg);
        if( label != null )
            label.setBackground( bg );
    }
    
    @Override
    public void updateUI() {
        super.updateUI();
        if( label != null ){
            originalFontSet = false;
            originalFont = null;
            label.setFont( null );
            
            label.updateUI();
            
            updateFonts();
        }
    }
    
    /**
     * Called by {@link #updateUI()} if the fonts need to be
     * updated, the default implementation does nothing
     */
    protected void updateFonts(){
    	// nothing
    }
    
    @Override
    public void setFont( Font font ) {
        super.setFont( font );
        if( label != null ){
            if( !originalFontSet ){
                originalFontSet = true;
                originalFont = label.getFont();
            }
            
            if( font != null ){
                label.setFont( font );
            }
            else{
                label.setFont( originalFont );
                originalFont = null;
                originalFontSet = false;
            }
            
            revalidate();
            repaint();
        }
    }
    
    /**
     * Sets the modifier for the current font.
     * @param modifier the modifier
     * @see DLabel#setFontModifier(FontModifier)
     */
    public void setFontModifier( FontModifier modifier ) {
        label.setFontModifier( modifier );
        revalidate();
        repaint();
    }
    
    /**
     * Gets the font modifier of this label.
     * @return the modifier
     */
    public FontModifier getFontModifier(){
    	return label.getFontModifier();
    }
    
    @Override
    public Dimension getMinimumSize() {
        return getPreferredSize();
    }
    
    @Override
    public Dimension getPreferredSize() {
    	Dimension size = label.getPreferredSize();
        if( isHorizontal() ){
        	if( icon == null )
        		return new Dimension( size.width+5, size.height );
        	
        	return new Dimension( size.width+5+iconOffset+iconTextDistance+icon.getIconWidth(), Math.max( size.height, icon.getIconHeight()+2*iconOffset ));
        }
        else{
        	if( icon == null )
        		return new Dimension( size.height, size.width+5 );
        	
        	return new Dimension( Math.max( size.height, icon.getIconWidth()+2*iconOffset ), size.width+5+iconOffset+iconTextDistance+icon.getIconHeight() );
        }
    }
    
    @Override
    public void paint( Graphics g ) {
        if( isHorizontal() ){
        	if( icon == null ){
        		label.paint( g );
        	}
        	else{
        		int width = getWidth();
        		int height = getHeight();
        		
        		int iconWidth = icon.getIconWidth();
        		int iconHeight = icon.getIconHeight();
        		
        		icon.paintIcon( this, g, iconOffset, (height-iconHeight)/2 );
        		
        		int usedUp = iconWidth + iconOffset + iconTextDistance;
        		if( usedUp < width ){
        			g = g.create( usedUp, 0, width-usedUp, height );
        			label.paint( g );
        			g.dispose();
        		}
        	}
        }
        else{
        	if( icon == null ){
	            Graphics2D g2 = (Graphics2D)g.create();
	            g2.rotate( Math.PI/2, 0, 0 );
	            g2.translate( 0, -getWidth() );
	            label.paint( g2 );
	            g2.dispose();
        	}
        	else{
        		int width = getWidth();
        		int height = getHeight();
        		
        		int iconWidth = icon.getIconWidth();
        		int iconHeight = icon.getIconHeight();
        		
        		icon.paintIcon( this, g, (width-iconWidth)/2, iconOffset );
        		
        		int usedUp = iconHeight + iconOffset + iconTextDistance;
        		if( usedUp < height ){
		            Graphics2D g2 = (Graphics2D)g.create( 0, usedUp, width, height-usedUp );
		            g2.rotate( Math.PI/2, 0, 0 );
		            g2.translate( 0, -getWidth() );
		            label.paint( g2 );
		            g2.dispose();
        		}
        	}
        }
    }
    
    @Override
    public void update( Graphics g ) {
        // do nothing
    }
    
    @Override
    public void setBounds( int x, int y, int w, int h ) {
        super.setBounds(x, y, w, h);
        
        if( isHorizontal() )
            label.setBounds( 0, 0, w, h );
        else
            label.setBounds( 0, 0, h, w );
    }
}
