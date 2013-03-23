/*
 * Bibliothek - DockingFrames
 * Library built on Java/Swing, allows the user to "drag and drop"
 * panels containing any Swing-Component the developer likes to add.
 * 
 * Copyright (C) 2013 Benjamin Sigg
 * 
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301  USA
 * 
 * Benjamin Sigg
 * benjamin_sigg@gmx.ch
 * CH - Switzerland
 */
package bibliothek.gui.dock.util;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.lang.reflect.Method;
import java.net.URI;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextPane;
import javax.swing.Timer;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;

import bibliothek.util.Version;

/**
 * The {@link CoreWarningDialog} is an annoying little dialog informing a developer that he/she is using
 * the Core API, even tough the Common API would offer much more features.
 * @author Benjamin Sigg
 */
public class CoreWarningDialog extends JPanel{
	public static void main( String[] args ){
		showDialog();
	}
	
	/**
	 * Opens the dialog, the dialog is modal and will attempt to steal the focus.
	 */
	public static void showDialog(){
		JDialog dialog = new JDialog();
		dialog.setModal( true );
		dialog.setTitle( "Information from DockingFrames " + Version.CURRENT );
		dialog.setDefaultCloseOperation( JDialog.DISPOSE_ON_CLOSE );
		dialog.add( new CoreWarningDialog( dialog ) );
		dialog.pack();
		dialog.setLocationRelativeTo( null );
		dialog.setVisible( true );
	}
	
	private JDialog dialog;
	private JButton close;
	
	private CoreWarningDialog( JDialog dialog ){
		this.dialog = dialog;
		JTextPane info = new JTextPane();
		info.setContentType( "text/html" );
		info.setText( createMessage() );
		info.setEditable( false );
		initHyperlinking( info );
		
		close = new JButton();
		
		setLayout( new GridBagLayout() );
		add( new JScrollPane( info ), new GridBagConstraints( 0, 0, 1, 1, 10.0, 10.0, GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets( 1, 1, 1, 1 ), 0, 0 ));
		add( close, new GridBagConstraints( 0, 1, 1, 1, 1.0, 1.0, GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets( 20, 20, 20, 20 ), 0, 0 ));
		
		startCountdown();
	}
	
	private void initHyperlinking( JTextPane pane ){
		pane.addHyperlinkListener( new HyperlinkListener(){
			public void hyperlinkUpdate( HyperlinkEvent e ){
				if( e.getEventType() == HyperlinkEvent.EventType.ACTIVATED ){
					try{
						Class<?> desktopClass = Class.forName( "java.awt.Desktop" );
						Object desktop = desktopClass.getMethod( "getDesktop" ).invoke( null );
						Method browse = desktopClass.getMethod( "browse", URI.class );
						browse.invoke( desktop, e.getURL().toURI() );
					}
					catch( Exception ex ){
						ex.printStackTrace();
						JOptionPane.showMessageDialog( dialog, "I was unable to open your browser :-(", "Cannot open link", JOptionPane.ERROR_MESSAGE );
					}
				}
			}
		});
	}
	
	private String createMessage(){
		return 
				"<html><body>" +
					"<b>Annoying warning</b><br>" +
					"<br>" +
					"Dear Developer,<br>" +
					"You are using the Core API of DockingFrames. But you are not making use of the Common API, which " +
					"would offer you a wide varity of exciting new features.<br>" +
					"<br>" +
					"Right now your application could have:" +
					"<ul>" +
						"<li>Buttons for minimizing Dockables.</li>" +
						"<li>Drag and drop affecting entire stacks of Dockables, instead of only one Dockable at a time.</li>" +
						"<li>Dockables refusing to resize (but the user can override that behavior).</li>" +
						"<li>Eclipse-like distinction between 'editors' and 'tools' (called Multiple- and SingleCDockable)</li>" +
						"<li>More advanced handling of location, includes tracking of closed Dockables.</li>" +
						"<li>Colors, Fonts, and Icons customized for each Dockable</li>" +
						"<li>An API that is easier to understand and to use than the Core API.</li>" +
					"</ul>" +
					"<br>" +
					"How to get all these goodies? Just include the <b>'docking-frames-common.jar'</b> file into the class-path of this " +
					"application. Visit <a href=\"http://dock.javaforge.com/download.html\">http://dock.javaforge.com</a> to download the files if you are missing them. <br>" +
					"Then create a <i>CControl</i> instead of a <i>DockController</i> or a <i>DockFrontend</i>.<br>" +
					"<br>" +
					"You may also want to have a look at the 'docking-frames-demo-tutorial' project, it shows how to use the Common API.<br>" +
					"And if you still have questions: visit our forum at " +
					"<a href=\"http://forum.byte-welt.net/forumdisplay.php?f=69&langid=2\">http://forum.byte-welt.net/forumdisplay.php?f=69</a>.<br>" +
					"<br>" +
					"If you really want to get rid of this dialog, just call:<br>" +
					"<pre>DockController.disableCoreWarning();</pre><br>" +
					"<br>" +
					"Regards<br>" +
					"Benjamin Sigg" +
				"</body></html>";
	}
	
	private void startCountdown(){
		close.setEnabled( false );
		
		close.addActionListener( new ActionListener(){
			public void actionPerformed( ActionEvent e ){
				dialog.dispose();
			}
		});
		
		updateClose( 4 );
		final Timer timer = new Timer( 1000, null );
		timer.addActionListener( new ActionListener(){
			private int remaining = 4;
			
			public void actionPerformed( ActionEvent e ){
				remaining--;
				updateClose( remaining );
				if( remaining == 0 ){
					timer.stop();
				}
			}
		});
		timer.start();
	}
	
	private void updateClose( int remaining ){
		if( remaining == 0 ){
			close.setEnabled( true );
			close.setText( "Close" );
		}
		else{
			close.setText( "Close (" + remaining + ")" );
		}
	}
}
