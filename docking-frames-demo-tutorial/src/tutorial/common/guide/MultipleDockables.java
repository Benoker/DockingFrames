package tutorial.common.guide;

import java.awt.Color;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import javax.swing.JMenuBar;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import tutorial.support.JTutorialFrame;
import tutorial.support.Tutorial;
import bibliothek.gui.dock.common.CControl;
import bibliothek.gui.dock.common.CGrid;
import bibliothek.gui.dock.common.DefaultMultipleCDockable;
import bibliothek.gui.dock.common.MultipleCDockableFactory;
import bibliothek.gui.dock.common.MultipleCDockableLayout;
import bibliothek.gui.dock.common.menu.CLayoutChoiceMenuPiece;
import bibliothek.gui.dock.facile.menu.RootMenuPiece;
import bibliothek.util.xml.XElement;

@Tutorial( id="MultipleDockables", title="\"Editors\": MultipleCDockable")
public class MultipleDockables {
	/* There are two kind of CDockables: SingleCDockables and MultipleCDockables. This
	 * example shows how MultipleCDockables are used to build an unknown number of editors.
	 * In this case an editor is some kind of panel where the user can edit something, for
	 * example a text file.
	 * 
	 * In order to use a MultipleCDockable clients normally write three classes. In this
	 * example the three classes are as follows:
	 *  - EditorDockable: the MultipleCDockable that is actually displayed and which is "the editor"
	 *  - EditorLayout: a MultipleCDockableLayout which contains information about the content of 
	 *      an EditorDocakble. The EditorLayout is lightweight, meaning it does not consume a
	 *      lot of memory (directly or indirectly through references).
	 *  - EditorFactory: a factory that creates new EditorDockables given some EditorLayouts.
	 */
	
	public static void main( String[] args ){
		/* We start by setting up a frame and a CControl */
		JTutorialFrame frame = new JTutorialFrame( MultipleDockables.class );
		CControl control = new CControl( frame );
		frame.destroyOnClose( control );
		frame.add( control.getContentArea() );
		
		/* We need to install our EditorFactory early on, otherwise the framework will not
		 * allow us to register the EditorDockables. */
		EditorFactory factory = new EditorFactory();
		control.addMultipleDockableFactory( "file-editor", factory );
		
		/* We now create some dockables and drop them onto the content-area */
		CGrid grid = new CGrid( control );
		grid.add( 0, 0, 1, 1, new EditorDockable( factory, new EditorLayout( "/home/tutorial/one.txt", "one = 1", new Color( 255, 200, 200 ) ) ) );
		grid.add( 0, 1, 1, 1, new EditorDockable( factory, new EditorLayout( "/home/tutorial/two.txt", "two = 2", new Color( 255, 255, 200 ) ) ) );
		grid.add( 1, 0, 1, 1, new EditorDockable( factory, new EditorLayout( "/home/tutorial/three.txt", "three = 3", new Color( 200, 255, 200 ) ) ) );
		grid.add( 1, 1, 1, 1, new EditorDockable( factory, new EditorLayout( "/home/tutorial/four.txt", "four = 4", new Color( 200, 200, 255 ) ) ) );
		control.getContentArea().deploy( grid );
		
		/* The CLayoutChoiceMenuPiece creates a dynamic menu which allows us to
		 * save and load the layout. In doing so we will use the EditorFactory. */
		JMenuBar menubar = new JMenuBar();
		RootMenuPiece layout = new RootMenuPiece( "Layout", false );
		layout.add( new CLayoutChoiceMenuPiece( control, true ) );
		menubar.add( layout.getMenu() );
		frame.setJMenuBar( menubar );
		
		/* and finally we can start the application */
		frame.setVisible( true );
	}
	
	/* This dockable shows the contents of a (fake) text file. */
	private static class EditorDockable extends DefaultMultipleCDockable{
		private JTextArea text;
		
		public EditorDockable( EditorFactory factory, EditorLayout layout ){
			/* it is mandatory to set the factory, the EditorDockable cannot be created 
			 * without it. */
			super( factory );
			
			/* and then we just set up the editor */
			setTitleText( layout.getFileName() );
			
			text = new JTextArea();
			text.setText( layout.getFileContent() );
			text.setBackground( layout.getBackground() );
			add( new JScrollPane( text ) );
		}
	
		/* This convenient method allows us to grab the entire content of this dockable
		 * in one step. */
		public EditorLayout getLayout(){
			return new EditorLayout( getTitleText(), text.getText(), text.getBackground() );
		}
	}
	
	/* This factory builds a link between EditorDockable and EditorLayout */
	private static class EditorFactory implements MultipleCDockableFactory<EditorDockable, EditorLayout>{
		/* An empty layout is required to read a layout from an XML file or from a byte stream */
		public EditorLayout create(){
			return new EditorLayout();
		}

		/* An optional method allowing to reuse 'dockable' when loading a new layout */
		public boolean match( EditorDockable dockable, EditorLayout layout ){
			return dockable.getLayout().equals( layout );
		}

		/* Called when applying a stored layout */
		public EditorDockable read( EditorLayout layout ){
			return new EditorDockable( this, layout );
		}

		/* Called when storing the current layout */
		public EditorLayout write( EditorDockable dockable ){
			return dockable.getLayout();
		}
	}
	
	/* This class describes the content of an EditorDockable. */
	private static class EditorLayout implements MultipleCDockableLayout{
		private String fileName;
		private String fileContent;
		private Color background;
		
		public EditorLayout(){
			// nothing
		}
		
		public EditorLayout( String fileName, String fileContent, Color background ){
			this.fileName = fileName;
			this.fileContent = fileContent;
			this.background = background;
		}
		
		public String getFileName(){
			return fileName;
		}
		
		public String getFileContent(){
			return fileContent;
		}
		
		public Color getBackground(){
			return background;
		}
		
		@Override
		public boolean equals( Object obj ){
			if( this == obj ){
				return true;
			}
			if( obj == null ){
				return false;
			}
			if( getClass() != obj.getClass() ){
				return false;
			}
			EditorLayout other = (EditorLayout) obj;
			return equals( background, other.background ) && 
				equals( fileName, other.fileName ) && 
				equals( fileContent, other.fileContent );
		}
		
		private boolean equals( Object a, Object b ){
			if( a == null ){
				return b == null;
			}
			else{
				return a.equals( b );
			}
		}

		public void readStream( DataInputStream in ) throws IOException{
			fileName = in.readUTF();
			fileContent = in.readUTF();
			background = new Color( in.readInt() );
		}

		public void readXML( XElement element ){
			fileName = element.getElement( "name" ).getString();
			fileContent = element.getElement( "content" ).getString();
			background = new Color( element.getElement( "background" ).getInt() );
		}

		public void writeStream( DataOutputStream out ) throws IOException{
			out.writeUTF( fileName );
			out.writeUTF( fileContent );
			out.writeInt( background.getRGB() );
		}

		public void writeXML( XElement element ){
			element.addElement( "name" ).setString( fileName );
			element.addElement( "content" ).setString( fileContent );
			element.addElement( "background" ).setInt( background.getRGB() );
		}
	}
}
