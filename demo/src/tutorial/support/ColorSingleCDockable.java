package tutorial.support;

import java.awt.Color;

import javax.swing.JPanel;

import bibliothek.gui.dock.common.DefaultSingleCDockable;

public class ColorSingleCDockable extends DefaultSingleCDockable{
	private JPanel panel = new JPanel();
	
	public ColorSingleCDockable( String title, Color color ){
		this( title, color, 1.0f );
	}
	
	public ColorSingleCDockable( String title, Color color, float brightness ){
		super( title );
		setTitleText( title );

		if( brightness != 1.0 ){
			float[] hsb = Color.RGBtoHSB( color.getRed(), color.getGreen(), color.getBlue(), null );
			
			hsb[1] = Math.min( 1.0f, hsb[1] / brightness );
			hsb[2] = Math.min( 1.0f, hsb[2] * brightness );
			
			color = Color.getHSBColor( hsb[0], hsb[1], hsb[2] );
		}

		setColor( color );
	}
	
	public void setColor( Color color ){
		panel = new JPanel();
		panel.setOpaque( true );
		panel.setBackground( color );
		add( panel );
		setTitleIcon( new ColorIcon( color ) );
	}
	
	public Color getColor(){
		return panel.getBackground();
	}
}
