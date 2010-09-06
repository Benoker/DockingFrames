package tutorial.support;

import java.awt.Color;

import javax.swing.JPanel;

import bibliothek.gui.dock.DefaultDockable;

public class ColorDockable extends DefaultDockable{
	public ColorDockable( String title, Color color ){
		setTitleText( title );

		JPanel panel = new JPanel();
		panel.setOpaque( true );
		panel.setBackground( color );
		add( panel );
	}
}
