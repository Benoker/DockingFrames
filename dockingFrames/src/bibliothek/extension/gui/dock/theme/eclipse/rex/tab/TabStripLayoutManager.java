package bibliothek.extension.gui.dock.theme.eclipse.rex.tab;

import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.LayoutManager;

public class TabStripLayoutManager implements LayoutManager {
	public void addLayoutComponent( String name, Component comp ){
		// ignore
	}

	public void layoutContainer( Container parent ){
		synchronized( parent.getTreeLock() ){
			int componentCount = parent.getComponentCount();
			Dimension[] preferreds = new Dimension[ componentCount ];
			int preferredWidthSum = 0;
			
			for( int i = 0; i < componentCount; i++ ){
				preferreds[ i ] = parent.getComponent( i ).getPreferredSize();
				preferredWidthSum += preferreds[ i ].width;
			}
			
			// can't do anything
			if( preferredWidthSum <= 0 )
				return;
			
			double ratio = Math.min( 1.0, parent.getWidth() / (double)preferredWidthSum );
			
			int x = 0;
			int parentHeight = parent.getHeight();
			
			for( int i = 0; i < componentCount; i++ ){
				Component child = parent.getComponent( i );
				
				int width = (int)Math.round( ratio * preferreds[i].width );
				int height = Math.min( parentHeight, preferreds[i].height );
				
				child.setBounds( x, parentHeight-height, width, height );
				x += width;
			}
		}
	}

	public Dimension minimumLayoutSize( Container parent ){
		synchronized( parent.getTreeLock() ){
			int width = 0;
			int height = 0;
			
			for( int i = 0, n = parent.getComponentCount(); i<n; i++ ){
				Component child = parent.getComponent( i );
				Dimension size = child.getMinimumSize();
				
				width += size.width;
				height = Math.max( height, size.height );
			}
			return new Dimension( width, height );
		}
	}

	public Dimension preferredLayoutSize( Container parent ){
		synchronized( parent.getTreeLock() ){
			int width = 0;
			int height = 0;
			
			for( int i = 0, n = parent.getComponentCount(); i<n; i++ ){
				Component child = parent.getComponent( i );
				Dimension size = child.getPreferredSize();
				
				width += size.width;
				height = Math.max( height, size.height );
			}
			return new Dimension( width, height );
		}
	}

	public void removeLayoutComponent( Component comp ){
		// ignore
	}
}
