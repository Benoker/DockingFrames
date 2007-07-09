package bibliothek.demonstration;

import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Image;

import javax.swing.Icon;

public class Test {
	public static void main( String[] args ){
		Core core = new Core();
		core.startup();
	}
	
	public static class TestDemo implements Demonstration{
		private String name;
		
		public TestDemo( String name ){
			this.name = name;
		}
		
		public String getHTML(){
			return "<html>"+name+"<br><br>Bla bla bla<br>blu blu blu</html>";
		}

		public Icon getIcon(){
			return new Icon(){
				public int getIconHeight(){
					return 16;
				}

				public int getIconWidth(){
					return 16;
				}

				public void paintIcon( Component c, Graphics g, int x, int y ){
					g.setColor( Color.GREEN );
					g.fillOval( x, y, getIconWidth(), getIconHeight() );
				}
			};
		}

		public Image getImage(){
			return null;
		}

		public String getName(){
			return name;
		}

		public void show( Monitor monitor ){
			try {
				monitor.startup();
				Thread.sleep( 3000 );
				monitor.running();
				Thread.sleep( 5000 );
				monitor.shutdown();
			}
			catch( InterruptedException e ) {
				e.printStackTrace();
			}
			
		}
	}
}
