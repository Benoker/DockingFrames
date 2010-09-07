package tutorial.support;

import java.awt.Color;

import javax.swing.JPanel;

import bibliothek.gui.dock.DefaultDockable;

public class ColorDockable extends DefaultDockable{
	public ColorDockable( String title, Color color ){
		this( title, color, 1.0f );
	}
	
	public ColorDockable( String title, Color color, float brightness ){
		setTitleText( title );

		if( brightness != 1.0 ){
			float[] hsb = Color.RGBtoHSB( color.getRed(), color.getGreen(), color.getBlue(), null );
			
			hsb[1] = Math.min( 1.0f, hsb[1] / brightness );
			hsb[2] = Math.min( 1.0f, hsb[2] * brightness );
			
			color = Color.getHSBColor( hsb[0], hsb[1], hsb[2] );
		}
		
		JPanel panel = new JPanel();
		panel.setOpaque( true );
		panel.setBackground( color );
		add( panel );
	}
}
