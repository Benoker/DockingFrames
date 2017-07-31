package bibliothek.gui.dock.util.swing;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;

import javax.swing.Icon;
import javax.swing.border.EmptyBorder;

import bibliothek.gui.dock.util.ConfiguredBackgroundPanel;
import bibliothek.gui.dock.util.DockUtilities;
import bibliothek.gui.dock.util.Transparency;
import bibliothek.gui.dock.util.font.FontModifier;

/**
 * A label which draws some text, and can change the layout of the text 
 * between horizontal and vertical.
 * @author Benjamin Sigg
 */
public class OrientedLabel extends ConfiguredBackgroundPanel{
    /** The label which really paints the text */
    private DLabel label = new DLabel();
    
    /** the original font of {@link #label} */
    private Font originalFont;
    
    /** icon painted on this label */
    private Icon icon;
    
    /** icon painted when this label is disabled */
    private Icon disabledIcon;
    
    /** whether to paint a special disabled version of {@link #icon} if disabled */
    private boolean paintDisabledIcon = true;
    
    /** distance between icon and border */
    private int iconOffset = 2;
    
    /** distance between icon and text */
    private int iconTextDistance = 2;
    
    /** whether the {@link #originalFont} has been set */
    private boolean originalFontSet = false;
    
    /** The text on the label */
    private String text;
    
    /** the current angle of this label */
    private Rotation rotation = Rotation.DEGREE_0;
    
    /** if <code>true</code>, then the {@link #icon} is not painted */
    private boolean hideIcon = false;
    
    /**
     * Creates a new label with no text
     */
    public OrientedLabel(){
    	super( Transparency.DEFAULT );
        label.setOpaque( false );
        label.setAlignmentX( 0 );
        label.setBorder( new EmptyBorder( 0, 5, 0, 0 ) );
    }
    
    /**
     * Sets the icon which will be painted on the left or on the top side.
     * @param icon the icon, can be <code>null</code>
     */
    public void setIcon( Icon icon ){
		this.icon = icon;
		disabledIcon = null;
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
     * Whether to build and paint a special disabled version of {@link #getIcon()} if this label is disabled.
     * @param paintDisabledIcon whether to paint the special icon
     */
    public void setPaintDisabledIcon( boolean paintDisabledIcon ) {
		this.paintDisabledIcon = paintDisabledIcon;
		revalidate();
		repaint();
	}
    /**
     * Whether a special disabled version of {@link #getIcon()} is painted if this label is disabled.
     * @return <code>true</code> if an artificial icon is created
     */
    public boolean isPaintDisabledIcon() {
		return paintDisabledIcon;
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
     * Gets the distance between icon and the tree adjacent borders.
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
     * Sets the orientation of this label.
     * @param rotation the orientation, not <code>null</code>
     */
    public void setRotation( Rotation rotation ){
    	if( rotation == null ){
    		throw new IllegalArgumentException( "rotation must not be null" ); 
    	}
    	
    	this.rotation = rotation;
    	revalidate();
    }
    
    /**
     * Sets the orientation. If <code>horizontal</code>, then the rotation is set
     * to 0 degrees, otherwise the rotation is set to 90 degrees.
     * @param horizontal whether the label is painted horizontal
     */
    public void setHorizontal( boolean horizontal ){
		if( horizontal ){
			setRotation( Rotation.DEGREE_0 );
		}
		else{
			setRotation( Rotation.DEGREE_90 );
		}
	}
    
    /**
     * Tells whether the content of this label is painted horizontally.
     * @return whether the label is horizontal
     */
    public boolean isHorizontal(){
    	return rotation == Rotation.DEGREE_0 || rotation == Rotation.DEGREE_180;
	}
    
    /**
     * Tells whether the content of this label is painted vertically.
     * @return whether the label is vertical
     */
    public boolean isVertical(){
    	return !isHorizontal();
    }
    
    /**
     * Sets the text of this label
     * @param text the text, <code>null</code> is allowed
     */
    public void setText( String text ){
        this.text = text;
        label.setText( (text == null || text.length() == 0) ? null : text );
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
    public void setEnabled( boolean enabled ){
    	super.setEnabled( enabled );
    	if( label != null ){
    		label.setEnabled( enabled );
    	}
    }
    
    @Override
    public void setForeground( Color fg ) {
        super.setForeground(fg);
        if( label != null )
            label.setForeground( fg );
    }
    
    @Override
    public Color getForeground(){
	    if( label == null ){
	    	return null;
	    }
	    return label.getForeground();
    }
    
    @Override
    public void setBackground( Color bg ) {
        super.setBackground(bg);
        if( label != null )
            label.setBackground( bg );
    }
    
    @Override
    public Color getBackground(){
    	if( label == null ){
    		return null;
    	}
    	return label.getBackground();
    }
    
    /**
     * Tells whether the icon is not painted.
     * @return whether the icon is hidden
     * @see #setIconHidden(boolean)
     */
    public boolean isIconHidden(){
		return hideIcon;
	}
    
    /**
     * Tells whether the {@link #setIcon(Icon) icon} is hidden. If the icon is hidden, it is not painted
     * and does not take any space. It is however treated normally when serving calls to {@link #getPreferredSize()}.
     * @param hideIcon whether to hide the icon
     */
    public void setIconHidden( boolean hideIcon ){
		if( this.hideIcon != hideIcon ){
			this.hideIcon = hideIcon;
			repaint();
		}
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
    
    /**
     * Gets direct access to the label that is used by this {@link OrientedLabel} to paint its content. This method
     * should be treated with care, modifications to the underlying label may have unexpected side effects.
     * @return the label that paints the content
     */
    public DLabel getLabel(){
		return label;
	}
    
    @Override
    public Dimension getMinimumSize() {
        return getPreferredSize();
    }
    
    @Override
    public Dimension getPreferredSize() {
    	Dimension size = label.getPreferredSize();
    	String text = getText();

    	if( (text == null || text.length() == 0) && icon != null ){
    		return new Dimension( icon.getIconWidth() + 2*iconOffset, icon.getIconHeight()+2*iconOffset );	
    	}
    	
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
    public void paint( Graphics g ){
    	paintComponent( g );
    }
    
    @Override
    public void paintForeground( Graphics g ){
        if( rotation == Rotation.DEGREE_0 ){
        	if( icon == null || isIconHidden() ){
        		label.paint( g );
        	}
        	else{
        		Icon icon = this.icon;
        		if( !isEnabled() && paintDisabledIcon ){
        			if( disabledIcon == null ){
        				disabledIcon = DockUtilities.disabledIcon( this, icon );
        			}
        			icon = disabledIcon;
        		}
        		
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
        else if( rotation == Rotation.DEGREE_90 ){
        	double angle = Math.PI/2.0;
        	if( icon == null || isIconHidden() ){
	            Graphics2D g2 = (Graphics2D)g.create();
	            g2.rotate( angle, 0, 0 );
	            g2.translate( 0, -getWidth() );
	            label.paint( g2 );
	            g2.dispose();
        	}
        	else {
        		int width = getWidth();
        		int height = getHeight();
        		
        		int iconWidth = icon.getIconWidth();
        		int iconHeight = icon.getIconHeight();
        		
        		icon.paintIcon( this, g, (width-iconWidth)/2, iconOffset );
        		int usedUp = iconHeight + iconOffset + iconTextDistance;
        		if( usedUp < height ){
		            Graphics2D g2 = (Graphics2D)g.create( 0, usedUp, width, height-usedUp );
		            g2.rotate( angle, 0, 0 );
		            g2.translate( 0, -getWidth() );
		            label.paint( g2 );
		            g2.dispose();
        		}
        	}
        }
        else if( rotation == Rotation.DEGREE_180 ){
        	double angle = Math.PI;
        	if( icon == null || isIconHidden() ){
	            Graphics2D g2 = (Graphics2D)g.create();
	            g2.rotate( angle, 0, 0 );
	            g2.translate( -getWidth(), -getHeight() );
	            label.paint( g2 );
	            g2.dispose();
        	}
        	else{
        		int width = getWidth();
        		int height = getHeight();
        		
        		int iconWidth = icon.getIconWidth();
        		int iconHeight = icon.getIconHeight();
        		
        		icon.paintIcon( this, g, iconOffset, (height-iconHeight)/2 );
        		int usedUp = iconWidth + iconOffset + iconTextDistance;
        		if( usedUp < width ){
		            Graphics2D g2 = (Graphics2D)g.create( usedUp, 0, width-usedUp, height );
		            g2.rotate( angle, 0, 0 );
		            g2.translate( -width+usedUp, height );
		            label.paint( g2 );
		            g2.dispose();
        		}
        	}
        }
    	else{
        	double angle = Math.PI+Math.PI/2.0;
        	if( icon == null || isIconHidden() ){
	            Graphics2D g2 = (Graphics2D)g.create();
	            g2.rotate( angle, 0, 0 );
	            g2.translate( -getHeight(), 0 );
	            label.paint( g2 );
	            g2.dispose();
        	}
        	else {
        		int width = getWidth();
        		int height = getHeight();
        		
        		int iconWidth = icon.getIconWidth();
        		int iconHeight = icon.getIconHeight();
        		
        		icon.paintIcon( this, g, (width-iconWidth)/2, iconOffset );
        		
        		int usedUp = iconHeight + iconOffset + iconTextDistance;
        		if( usedUp < height ){
		            Graphics2D g2 = (Graphics2D)g.create( 0, usedUp, width, height-usedUp );
		            g2.rotate( angle, 0, 0 );
		            g2.translate( -height, 0 );
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
            label.setBounds( 0, 0, w+30, h );
        else
            label.setBounds( 0, 0, h+30, w );
    }
}
