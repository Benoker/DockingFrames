/*
 * Bibliothek - DockingFrames
 * Library built on Java/Swing, allows the user to "drag and drop"
 * panels containing any Swing-Component the developer likes to add.
 * 
 * Copyright (C) 2007 Benjamin Sigg
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

package bibliothek.util;

import java.awt.Color;

/**
 * Some fields and methods that make life easier when dealing 
 * with Colors.
 * @author Benjamin Sigg
 */
public final class Colors {
	private Colors(){
		// do not create any instances of this class
	}

	/**
	 * Decreases the brightness of a color.
	 * @param c the color which will be made darker
	 * @param value how much darker the color should be, 1 means black, 0
	 * means <code>c</code> is returned
	 * @return a color which is darker than <code>c</code>
	 */
	public static Color darker ( Color c, double value ){
		if ( c == null )
			return null;
		
		if( value < 0 ){
			return brighter( c, -value );
		}

		if ( value > 1 ) value = 1;

		value = 1 - value;

		return new Color (
				(int)(c.getRed() * value),
				(int)(c.getGreen() * value),
				(int)(c.getBlue() * value)
		);
	}

	/**
	 * Increases the brightness of a color.
	 * @param c the color which will be made brighter
	 * @param value how much brighter the color should be, 1 means
	 * white, 0 means <code>c</code> is returned
	 * @return a color which is brighter than <code>c</code>
	 */
	public static Color brighter( Color c, double value ){
		if ( c == null )
			return null;

		if( value < 0 ){
			return darker( c, -value );
		}
		
		int dr, dg, db;

		if ( value > 1 ){
			value = 1;
		}

		dr = 255 - c.getRed();
		dg = 255 - c.getGreen();
		db = 255 - c.getBlue();

		Color back = new Color (
				c.getRed() + (int)(dr * value ),
				c.getGreen() + (int)(dg * value ),
				c.getBlue() + (int)(db * value )
		);
		return back;
	}
	
	/**
	 * Converts <code>c</code> into HSB and adds <code>delta</code> to the brightness.
	 * @param c the color to convert
	 * @param delta the delta in brightness
	 * @return the new color
	 */
	public static Color deltaBrightness( Color c, double delta ){
		if( c == null ){
			return null;
		}
		
		float[] hsb = Color.RGBtoHSB( c.getRed(), c.getBlue(), c.getGreen(), null );
		hsb[2] += delta;
		hsb[2] = Math.max( 0, Math.min( 1, hsb[2] ));
		return Color.getHSBColor( hsb[0], hsb[1], hsb[2] );
	}
	
	/**
	 * Creates a color which "lies between" the colors <code>a</code>
	 * and <code>b</code>
	 * @param a the first color
	 * @param b the second color
	 * @param value a value between 0 and 1, 0 means <code>a</code> is returned,
	 * 1 means <code>b</code> is returned
	 * @return a color whose rgb-values are between the rgb-values of <code>a</code>
	 * and <code>b</code>
	 */
	public static Color between ( Color a, Color b, double value ){
		if ( value < 0 ) value = 0;
		if ( value > 1 ) value = 1;

		int red = (int)(a.getRed() + value * ( b.getRed() - a.getRed() ));
		int green = (int)(a.getGreen() + value * ( b.getGreen() - a.getGreen() ));
		int blue = (int)(a.getBlue() + value * ( b.getBlue() - a.getBlue() ));

		return new Color ( red, green, blue );
	}
	
	/**
	 * Creates a color that "lies between" the colors <code>a</code>, <code>b</code> and
	 * <code>c</code>. Each color has a weight, and as higher the weight as more of that color
	 * is inside the created color.
	 * @param a the first color to incorporate
	 * @param weightA how much of <code>a</code> goes into the resulting color
	 * @param b the second color to incorporate
	 * @param weightB how much of <code>b</code> goes into the resulting color
	 * @param c the third color to incorporate
	 * @param weightC how much of <code>c</code> goes into the resulting color
	 * @return <code>(a * weightA + b * weightB + c * weightC) / (weightA + weightB + weightC)</code> 
	 */
	public static Color between( Color a, double weightA, Color b, double weightB, Color c, double weightC ){
		double sum = weightA + weightB + weightC;
		
		weightA /= sum;
		weightB /= sum;
		weightC /= sum;
		
		double red = a.getRed() * weightA + b.getRed() * weightB + c.getRed() * weightC;
		double green = a.getGreen() * weightA + b.getGreen() * weightB + c.getGreen() * weightC;
		double blue = a.getBlue() * weightA + b.getBlue() * weightB + c.getBlue() * weightC;
		
		return new Color( Math.max( 0, Math.min( (int)red, 255 ) ), Math.max( 0, Math.min( (int)green, 255 ) ), Math.max( 0, Math.min( (int)blue, 255 ) ) );
	}
	
    public static Color middle( Color a, Color b ){
        return between( a, b, 0.5 );
    }
    
    public static Color fuller( Color color, double factor ){
    	if( color == null ){
    		return null;
    	}
    	
        int r = color.getRed();
        int g = color.getGreen();
        int b = color.getBlue();
        
        int max = Math.max( r, Math.max( g, b ));
        
        if( r < max )
            r *= (1-factor);
        else
            r += r*factor;
        
        if( g < max )
            g *= (1-factor);
        else
            g += g * factor;
            
        if( b < max )
            b *= (1-factor);
        else
            b += b * factor;
        
        return new Color(
                Math.max( 0, Math.min( 255, r )),
                Math.max( 0, Math.min( 255, g )),
                Math.max( 0, Math.min( 255, b ))
        );
    }
    
    public static Color undiffMirror( Color color, double factor ){
    	if( color == null ){
    		return null;
    	}
    	
        int sum = color.getRed() + color.getGreen() + color.getBlue();
        if( sum > (3*255/2.0) )
            return brighter( color, factor );
        else
            return darker( color, factor );
    }
    
    public static Color diffMirror( Color color, double factor ){
    	if( color == null ){
    		return null;
    	}
        int sum = color.getRed() + color.getGreen() + color.getBlue();
        if( sum < (3*255/2.0) )
            return brighter( color, factor );
        else
            return darker( color, factor );
    }

    /**
     * Helper methods calling {@link Color#darker()}.
     * @param color some color to modify, can be <code>null</code>
     * @return the darker color or <code>null</code>
     */
    public static Color darker( Color color ){
    	if( color == null ){
    		return null;
    	}
    	return color.darker();
    }

    /**
     * Helper methods calling {@link Color#brighter()}.
     * @param color some color to modify, can be <code>null</code>
     * @return the brighter color or <code>null</code>
     */
    public static Color brighter( Color color ){
    	if( color == null ){
    		return null;
    	}
    	return color.brighter();
    }
}
