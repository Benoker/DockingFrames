package bibliothek.test;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTextPane;
import javax.swing.JTree;
import javax.swing.Timer;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.TreePath;

public class InspectionPanel extends JPanel {
	private JTree tree;
	private JTextPane console;
	private InspectionTree model;

	public InspectionPanel( Object root, InspectionGraph graph ){
		setLayout( new BorderLayout() );
		
		JSplitPane split = new JSplitPane( JSplitPane.VERTICAL_SPLIT );
		add( split, BorderLayout.CENTER );
		
		model = new InspectionTree( graph.getNode( root ), graph );
		tree = new JTree( model );
		console = new JTextPane();
		
		split.setTopComponent( new JScrollPane( tree ));
		split.setBottomComponent( new JScrollPane( console ));
		
		tree.setEditable( false );
		console.setEditable( false );
		
		tree.addTreeSelectionListener( new TreeSelectionListener() {
			public void valueChanged( TreeSelectionEvent e ){
				updateText();
			}
		});
		
		tree.setSelectionRow( 0 );
		
		Timer timer = new Timer( 500, new ActionListener() {
			public void actionPerformed( ActionEvent e ){
				model.update( true );
				updateText();
			}
		});
		timer.setRepeats( true );
		timer.start();
	}
	
	private void updateText(){
		TreePath path = tree.getSelectionPath();
		if( path == null ){
			console.setText( "" );
		}
		else{
			String text = String.valueOf( path.getLastPathComponent() );
			if( !text.equals( console.getText() )){
				console.setText( text );
			}
		}
	}
}
