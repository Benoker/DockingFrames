package tutorial.support.sets;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Set;

import javax.imageio.ImageIO;
import javax.swing.event.TreeModelListener;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreePath;

import tutorial.TutorialExtension;
import tutorial.support.Tutorial;

public class TutorialTreeModel implements TreeModel{
	private Node root;
	private Set<TutorialExtension> extensions;
	
	public TutorialTreeModel( Class<?> root, Set<TutorialExtension> extensions ) throws InstantiationException, IllegalAccessException{
		this.extensions = extensions;
		this.root = new Node( root );
	}
	
	public void addTreeModelListener( TreeModelListener l ){
		// ignore
	}
	
	public void removeTreeModelListener( TreeModelListener l ){
		// ignore
	}

	public void valueForPathChanged( TreePath path, Object newValue ){
		// ignore	
	}
	
	public Object getChild( Object parent, int index ){
		return ((Node)parent).children[ index ];
	}

	public int getChildCount( Object parent ){
		TutorialTreeModel.Node[] children = ((Node)parent).children;
		if( children == null ){
			return 0;
		}
		
		return children.length;
	}

	public int getIndexOfChild( Object parent, Object child ){
		int index = 0;
		for( Node check : ((Node)parent).children ){
			if( check == child ){
				return index;
			}
			index++;
		}
		return -1;
	}

	public Object getRoot(){
		return root;
	}

	public boolean isLeaf( Object node ){
		return ((Node)node).isLeaf();
	}

	
	public class Node{
		private Class<?> clazz;
		private Node[] children;
		
		private String title;
		private boolean titleSet = false;
		
		private String description;
		private boolean descriptionSet = false;
		
		private BufferedImage image;
		private boolean imageSet = false;
		
		public Node( Class<?> clazz ) throws InstantiationException, IllegalAccessException{
			this.clazz = clazz;
			
			if( TutorialSet.class.isAssignableFrom( clazz )){
				TutorialSet set = (TutorialSet) clazz.newInstance();
				for( TutorialExtension extension : extensions ){
					set.append( extension );
				}
				children = new Node[ set.getChildren().length ];
				for( int i = 0; i < children.length; i++ ){
					children[i] = new Node( set.getChildren()[ i ]);
				}
			}
		}
		
		public boolean isLeaf(){
			return !TutorialSet.class.isAssignableFrom( clazz );
		}
		
		public String getTitle(){
			if( !titleSet ){
				titleSet = true;
				Tutorial tutorial = (Tutorial)clazz.getAnnotation( Tutorial.class );
				if( tutorial == null ){
					title = clazz.getSimpleName();
				}
				else{
					title = tutorial.title();
				}
			}
			return title;
		}
		
		@Override
		public String toString(){
			return getTitle();
		}
		
		public Class<?> getMainClass(){
			try {
				if( clazz.getMethod( "main", String[].class ) != null ){
					return clazz;
				}
				else{
					return null;
				}
			}
			catch( SecurityException e ) {
				e.printStackTrace();
				return null;
			}
			catch( NoSuchMethodException e ) {
				return null;
			}
		}
		
		public String getDescription(){
			if( !descriptionSet ){
				Tutorial tutorial = (Tutorial) clazz.getAnnotation( Tutorial.class );
				String id = tutorial == null ? null : tutorial.id();
				if( id != null ){
					descriptionSet = true;
				
					try{
						InputStream in = clazz.getResourceAsStream( "/data/tutorial/" + id + ".html");
						InputStreamReader reader = new InputStreamReader( in, "UTF-8" );
						StringBuilder builder = new StringBuilder();
						int next;
						while( (next = reader.read()) != -1 ){
							builder.append( (char)next );
						}
						reader.close();
						description = builder.toString();
					}
					catch( IOException e ){
						e.printStackTrace();
						description = "<html><body>" + e.getMessage() + "</body></html>";
					}
				}
			}
			return description;
		}
		
		public BufferedImage getImage(){
			if( !imageSet ){
				imageSet = true;
				Tutorial tutorial = (Tutorial) clazz.getAnnotation( Tutorial.class );
				String id = tutorial == null ? null : tutorial.id();
				if( id != null ){
					try{
						InputStream in = clazz.getResourceAsStream( "/data/tutorial/" + id + ".png" );
						if( in == null ){
							return null;
						}
						image = ImageIO.read( in );
						in.close();
					}
					catch( IOException e ){
						e.printStackTrace();
					}
				}
			}
			return image;
		}
		
		public String getCode() throws IOException{
			String name = clazz.getName();
			name = "/" + name.replace('.', '/') + ".java";
			InputStream in = clazz.getResourceAsStream( name );
			if( in == null ){
				File file = new File( "src" + name );
				if( file.canRead() ){
					in = new FileInputStream( file );
				}
			}
			if( in != null ){
				StringBuilder builder = new StringBuilder();
				InputStreamReader reader = new InputStreamReader( in, "UTF-8" );
				int next;
				while( (next = reader.read()) != -1 ){
					if( next == '\t' ){
						builder.append( "    " );
					}
					else{
						builder.append( (char)next );
					}
				}
				reader.close();
				return builder.toString();
			}
			return "n/a";
		}
	}
}
