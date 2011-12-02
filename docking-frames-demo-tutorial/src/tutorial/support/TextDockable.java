package tutorial.support;

import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import bibliothek.gui.dock.DefaultDockable;

public class TextDockable extends DefaultDockable{
	private JTextArea area;
	
	public TextDockable( String title ){
		setTitleText( title );
		
		area = new JTextArea();
		area.setLineWrap( false );
		area.setTabSize( 2 );
		area.setEditable( false );
		
		add( new JScrollPane( area ));
	}
	
	public void setText( String text ){
		area.setText( text );
	}
	
	public void appendText( String text ){
		area.append( text );
	}
	
	public String getText(){
		return area.getText();
	}
}
