package bibliothek.testing;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.URL;
import java.net.URLClassLoader;
import java.net.UnknownHostException;
import java.util.Scanner;

import bibliothek.gui.DockController;
import bibliothek.gui.dock.common.CControl;

/**
 * This class starts a TCP connection to a test-server on localhost:8404, asks the test-server
 * for an url for additional classes. Then it asks the test-server about the 
 * class to load and execute.
 * @author Benjamin Sigg
 */
public class TestClient {
	public TestClient( CControl control ){
		try{
			Socket socket = new Socket( "localhost", 8404 );
			PrintWriter out = new PrintWriter( socket.getOutputStream() );
			Scanner in = new Scanner( socket.getInputStream() );
			
			String url = in.nextLine();
			String name = in.nextLine();
			
			out.println( "ack" );
			out.flush();

			ClassLoader parent = TestClient.class.getClassLoader();
			URLClassLoader loader = new URLClassLoader( new URL[]{ new URL( url ) }, parent );
			
			Class<?> clazz = loader.loadClass( name );
			
			Test test = (Test)clazz.newInstance();
			
			socket.close();
			test.start( control );
		}
		catch( UnknownHostException e ){
			e.printStackTrace();
		}
		catch( IOException e ){
			e.printStackTrace();
		}
		catch( ClassNotFoundException e ){
			e.printStackTrace();
		}
		catch( InstantiationException e ){
			e.printStackTrace();
		}
		catch( IllegalAccessException e ){
			e.printStackTrace();
		}
	}
}
