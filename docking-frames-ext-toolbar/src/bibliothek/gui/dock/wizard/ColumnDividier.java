package bibliothek.gui.dock.wizard;

import java.awt.Rectangle;

import bibliothek.gui.dock.SplitDockStation.Orientation;
import bibliothek.gui.dock.station.split.Divideable;

/**
 * Represents a special {@link Divideable} that is at the end of the columns and allows the user to resize
 * the entire station.
 * @author Benjamin Sigg
 */
public class ColumnDividier implements Divideable{
	private WizardSplitDockStation station;
	
	public ColumnDividier( WizardSplitDockStation station ){
		this.station = station;
	}
	
	@Override
	public double getDividerAt( int x, int y ){
		double width = station.getWidth();
		double height = station.getHeight();
		
		switch( station.getSide() ){
			case RIGHT:
			case LEFT:
				return x / width;
			case BOTTOM:
			case TOP:
				return y / height;
			default:
				throw new IllegalStateException( "unknown side: " + station.getSide() );
		}
	}

	@Override
	public Rectangle getDividerBounds( double divider, Rectangle bounds ){
		if( bounds == null ){
			bounds = new Rectangle();
		}
		
		int gap = station.getDividerSize();
		int width = station.getWidth() - gap;
		int height = station.getHeight() - gap;
		
		switch( station.getSide() ){
			case RIGHT:
			case LEFT:
				bounds.x = (int)(divider * width);
				bounds.y = 0;
				bounds.width = gap;
				bounds.height = height + gap;
				break;
			case TOP:
			case BOTTOM:
				bounds.x = 0;
				bounds.width = width + gap;
				bounds.y = (int)(divider * height);
				bounds.height = gap;
		}
		return bounds;
	}

	@Override
	public Orientation getOrientation(){
		return station.getSide().getHeaderOrientation();
	}

	@Override
	public double getDivider(){
		switch( station.getSide() ){
			case RIGHT:
			case BOTTOM:
				return 0;
			case LEFT:
			case TOP:
				return 1;
			default:
				throw new IllegalStateException( "unknown side: " + station.getSide() );
		}
	}

	@Override
	public void setDivider( double dividier ){
		// ignored
	}

	@Override
	public double validateDivider( double divider ){
		return divider;
	}
	
}
