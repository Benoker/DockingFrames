/*
 * Bibliothek License
 * ==================
 * 
 * Except where otherwise noted, all of the documentation and software included
 * in the bibliothek package is copyrighted by Benjamin Sigg.
 * 
 * Copyright (C) 2001-2005 Benjamin Sigg. All rights reserved.
 * 
 * This software is provided "as-is," without any express or implied warranty.
 * In no event shall the author be held liable for any damages arising from the
 * use of this software.
 * 
 * Permission is granted to anyone to use this software for any purpose,
 * including commercial applications, and to alter and redistribute it,
 * provided that the following conditions are met:
 * 
 * 1. All redistributions of source code files must retain all copyright
 *    notices that are currently in place, and this list of conditions without
 *    modification.
 * 
 * 2. All redistributions in binary form must retain all occurrences of the
 *    above copyright notice and web site addresses that are currently in
 *    place (for example, in the About boxes).
 * 
 * 3. The origin of this software must not be misrepresented; you must not
 *    claim that you wrote the original software. If you use this software to
 *    distribute a product, an acknowledgment in the product documentation
 *    would be appreciated but is not required.
 * 
 * 4. Modified versions in source or binary form must be plainly marked as
 *    such, and must not be misrepresented as being the original software.
 * 
 * 
 * Benjamin sigg
 * benjamin_sigg@gmx.ch
 * 
 */
 
package bibliothek.util;

import java.awt.Rectangle;

/**
 * Funktioniert wie seine Superklasse, allerdings werden die Daten wie in
 * einem 2Dimensionalen Array behandelt.
 * @author Benjamin Sigg
 * @version 1.0
 */
public class BooleanArray2D extends BooleanArray {
  private int lengthX;
  private int lengthY;

  public BooleanArray2D ( int x, int y ) {
    if ( x < 0 || y < 0 )
      throw new IllegalArgumentException ( "X oder Y ist kleiner 0" );

    init ( x * y );

    lengthX = x;
    lengthY = y;
  }
  public BooleanArray2D ( int x, int y, boolean value ){
    this ( x, y );
    fill ( value );
  }

  public int getLengthX(){
    return lengthX;
  }
  public int getLengthY(){
    return lengthY;
  }

  public boolean set ( int x, int y, boolean value ){
    if ( x < 0 || y < 0 )
      throw new ArrayIndexOutOfBoundsException ( "x oder y kleiner 0" );

    if ( x >= getLengthX() || y >= getLengthY())
      throw new ArrayIndexOutOfBoundsException ( "x oder y zu gross" );

    int index = x * getLengthY() + y;
    return set ( index, value );
  }
  public boolean get ( int x, int y ){
    if ( x < 0 || y < 0 )
      throw new ArrayIndexOutOfBoundsException ( "x oder y kleiner 0" );

    if ( x >= getLengthX() || y >= getLengthY())
      throw new ArrayIndexOutOfBoundsException ( "x oder y zu gross" );

    return get ( x * getLengthY() + y );
  }

  public void fill ( boolean value, Rectangle rec ){
    fill ( value, rec.x, rec.y, rec.width, rec.height );
  }

  /**
   * Füllt ein Rechteck des Arrays mit einem bestimmten Wert.
   * @param value Der zu setzende Wert.
   * @param x x-Koordinate des Rechtecks.
   * @param y y-Koordinate des Rechtecks.
   * @param width Weite (in x) des Rechtecks, muss positiv sein.
   * @param height Höhe (in y) des Rechtecks, muss positiv sein.
   */
  public void fill ( boolean value, int x, int y, int width, int height ){
    if ( x < 0 || y < 0 )
      throw new ArrayIndexOutOfBoundsException ( "x oder y kleiner 0" );

    if ( x + width > getLengthX() || y + height > getLengthY())
      throw new ArrayIndexOutOfBoundsException ( "x oder y zu gross" );

    if ( width == 0 || height == 0 )
      return;

    for ( int i = x; i < x + width; i++ ){
      int start = i * getLengthX() + y;
      int size = height;
      fill ( value, start, size );
    }
  }
}