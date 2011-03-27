/*
 * Bibliothek - DockingFrames
 * Library built on Java/Swing, allows the user to "drag and drop"
 * panels containing any Swing-Component the developer likes to add.
 * 
 * Copyright (C) 2009 Benjamin Sigg
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
package bibliothek.extension.gui.dock.theme.eclipse.stack.tab;

import java.util.Arrays;

/**
 * An arch is a mathematical description of a curve, the curve is within a
 * given rectangle, starting at the top left and ending at the bottom right
 * edge. The curve is horizontal at the beginning and at the end. The curve 
 * is within a discrete space, and there is exactly one y-value for each given
 * x-value. 
 * @author Benjamin Sigg
 */
public class Arch {
	private int width;
	private int height;
	
	private int[] values;
	
	/**
	 * Creates a new arch.
	 * @param width the width of the arch
	 * @param height the height of the arch
	 * @throws IllegalArgumentException if either of <code>width</code> or
	 * <code>height</code> is smaller than 1
	 */
	public Arch( int width, int height ){
		if( width < 1 || height < 1 )
			throw new IllegalArgumentException( "width or height smaller than 1: " + width + ", " + height );
		
		this.width = width;
		this.height = height;
		
		values = new int[ width ];
		
		calculate();
	}
	
	/**
	 * Gets the width of the arch.
	 * @return the width
	 */
	public int getWidth(){
		return width;
	}
	
	/**
	 * Gets the height of the arch.
	 * @return the height
	 */
	public int getHeight(){
		return height;
	}
	
	/**
	 * Gets the position of the arch at <code>x</code>. <code>x</code> must
	 * be between 0 (incl.) and {@link #getWidth() width} (excl.). The result
	 * will be between 0 (incl.) and {@link #getHeight() height} (excl.).
	 * @param x the x component
	 * @return the associated y component
	 */
	public int getValue( int x ){
		return values[x];
	}
	
	private void calculate(){
		int lineWidth = lineH( width );
		int lineHeight = lineV( height );
		
		int archWidth = (width - lineWidth) / 2;
		int archHeight = (height - lineHeight) / 2;
		
		Arrays.fill( values, -1 );
		
		// arches
		double[] sample = arch( 0, 0, archWidth, archHeight, lineHeight / (double)lineWidth );
		for( int x = 0; x < archWidth; x++ ){
			int y = (int)(sample[x]+0.5 );
			
			values[x] = y;
			values[width-1-x] = height-1-y;
		}
		
		// line (painted in two halfes to ensure symmetry)
		sample = line( archWidth, archHeight, archWidth+lineWidth/2-1, archHeight+lineHeight/2-1 );
		for( int x = 0; x < sample.length; x++ ){
			int y = (int)(sample[x]+0.5 );
		
			values[x+archWidth] = y;
			values[width-1-x-archWidth] = height-1-y;
		}
		
		// fill lines without blips
		
		if( values[0] == -1 ){
			values[0] = 0;
			values[values.length-1] = height-1;
		}
		
		for( int x = 1, n = width/2; x <= n; x++ ){
			if( values[x] == -1 ){
				values[x] = values[x-1];
				values[width-x-1] = values[width-x];
			}
		}
	}
	
	private double[] line( int x1, int y1, int x2, int y2 ){
		double[] sample = new double[ x2 - x1 + 1 ];
		
		for( int t = 0; t < sample.length; t++ ){
			for( int t2 = 0; t2 < 10; t2++ ){
				double value = (10*t + t2)/10.0/sample.length;
				
				double bx = x1 + (value)*(x2-x1+1);
				double by = y1 + (value)*(y2-y1+1);
				
				sample[ (int)bx - x1 ] += 0.1 * by;
			}
		}
		
		return sample;
	}
	
	private double[] arch( int x, int y, int width, int height, double slope ){
		double cutLine = y;
		double cutX = cutWithZero( x+width, y+height, slope, cutLine );
		double cutY = cutLine;
		
		double cutM = Math.tan( Math.PI - Math.atan( slope ) / 2.0 );
		double cutC = cutY - cutM*cutX;
		
		double cutDelta = -0.6;
		
		cutX += cutDelta;
		cutY = cutM * cutX + cutC;
		
		double[] sample = new double[ width ];
		
		for( int t = 0; t < width; t++ ){
			for( int t2 = 0; t2 < 10; t2++ ){
				double value = (10*t + t2)/10.0/width;
				
				double bx = bezier( value, x, cutX, x+width );
				double by = bezier( value, y, cutY, y+height );
				
				sample[ (int)bx - x ] += 0.1 * by;
			}
		}
		return sample;
	}
	
	private double bezier( double t, double p0, double p1, double p2 ){
		return (1-t)*(1-t)*p0 + 2*t*(1-t)*p1 + t*t*p2;
	}
	
	private double cutWithZero( double x, double y, double m, double line ){
		double c = y - m*x;
		return (line-c) / m;
	}
	
	private int lineH( int width ){
		int line = width / 3;
		
		int mod = width % 3;
		
		if( mod == 1 )
			return line+1;
		
		return line;
	}
	
	private int lineV( int height ){
		int line = height / 2;
		if( (height-line) % 2 == 1 )
			line++;
		
		return line;
	}

	@Override
	public String toString(){
		StringBuilder builder = new StringBuilder();
		for( int y = 0; y < height; y++ ){
			if( y > 0 )
				builder.append( "\n" );
			
			if( y < 10 )
				builder.append( " " );
			builder.append( y );
			builder.append( ": " );
			
			for( int x = 0; x < width; x++ ){
				if( values[x] == y ){
					builder.append( "*" );
				}
				else{
					builder.append( " " );
				}
			}
		}
		
		return builder.toString();
	}
	
	@Override
	public int hashCode(){
		final int prime = 31;
		int result = 1;
		result = prime * result + height;
		result = prime * result + width;
		return result;
	}

	@Override
	public boolean equals( Object obj ){
		if( this == obj )
			return true;
		if( obj == null )
			return false;
		if( getClass() != obj.getClass() )
			return false;
		Arch other = (Arch)obj;
		if( height != other.height )
			return false;
		if( width != other.width )
			return false;
		return true;
	}
}
