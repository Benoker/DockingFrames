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

import java.awt.Color;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Hashtable;
import java.util.Vector;

/**
 * Some fields and methods that make life easier when dealing 
 * with Colors.
 * @author Benjamin Sigg
 * @version 1.0
 */
public final class Colors {
  public static final Color ALICEBLUE		= new Color ( 15792383 );
  public static final Color ANTIQUEWHITE 	= new Color ( 16444375 );
  public static final Color AQUAMARINE 		= new Color ( 8388564 );
  public static final Color AZURE 			= new Color ( 15794175 );
  public static final Color BEIGE 			= new Color ( 16119260 );
  public static final Color BLUEVIOLET 		= new Color ( 9055202 );
  public static final Color BROWN 			= new Color ( 10824234 );
  public static final Color BURLYWOOD 		= new Color ( 14596231 );
  public static final Color CADETBLUE 		= new Color ( 6266528 );
  public static final Color CHARTREUSE 		= new Color ( 8388352 );
  public static final Color CHOCOLATE 		= new Color ( 13789470 );
  public static final Color CORAL 			= new Color ( 16744272 );
  public static final Color DARKGRAY 		= new Color ( 11119017 );
  public static final Color DARKGREEN 		= new Color ( 25600 );
  public static final Color DARKKHAKI 		= new Color ( 12433259 );
  public static final Color DARKMAGENTA 	= new Color ( 9109643 );
  public static final Color DARKOLIVEGREEN 	= new Color ( 5597999 );
  public static final Color DARKORANGE 		= new Color ( 16747520 );
  public static final Color DARKORCHID 		= new Color ( 10040012 );
  public static final Color DARKRED 		= new Color ( 9109504 );
  public static final Color DARKSALMON 		= new Color ( 15308410 );
  public static final Color DARKSEAGREEN 	= new Color ( 9419919 );
  public static final Color DARKSLATEBLUE 	= new Color ( 4734347 );
  public static final Color DARKSLATEGRAY 	= new Color ( 3100495 );
  public static final Color DARKTURQUOISE 	= new Color ( 52945 );
  public static final Color DARKVIOLET 		= new Color ( 9699539 );
  public static final Color DEEPPINK 		= new Color ( 16716947 );
  public static final Color DEEPSKYBLUE 	= new Color ( 49151 );
  public static final Color DIMGRAY 		= new Color ( 6908265 );
  public static final Color DODGERBLUE 		= new Color ( 2003199 );
  public static final Color FIREBRICK 		= new Color ( 11674146 );
  public static final Color FLORALWHITE 	= new Color ( 16775920 );
  public static final Color FORESTGREEN 	= new Color ( 2263842 );
  public static final Color GAINSBORO 		= new Color ( 14474460 );
  public static final Color GHOSTWHITE 		= new Color ( 16316671 );
  public static final Color GOLD 			= new Color ( 16766720 );
  public static final Color GOLDENROD 		= new Color ( 14329120 );
  public static final Color GREENYELLOW 	= new Color ( 11403055 );
  public static final Color HONEYDEW 		= new Color ( 15794160 );
  public static final Color HOTPINK 		= new Color ( 16738740 );
  public static final Color INDIANRED 		= new Color ( 13458524 );
  public static final Color INDIGO 			= new Color ( 4915330 );
  public static final Color IVORY 			= new Color ( 16777200 );
  public static final Color KHAKI 			= new Color ( 15787660 );
  public static final Color LAVENDER 		= new Color ( 15132410 );
  public static final Color LABENDERBLUSH 	= new Color ( 16773365 );
  public static final Color LAWNGREEN 		= new Color ( 8190976 );
  public static final Color LEMONCHIFFON 	= new Color ( 16775885 );
  public static final Color	LIGHTBLUE 		= new Color ( 11393254 );
  public static final Color LIGHTCORAL 		= new Color ( 15761536 );
  public static final Color LIGHTCYAN 		= new Color ( 14745599 );
  public static final Color LIGHTGOLDENRODYELLOW = new Color ( 16448210 );
  public static final Color LIGHTGREEN 		= new Color ( 9498256 );
  public static final Color LIGHTGREY 		= new Color ( 13882323 );
  public static final Color LIGHTPINK 		= new Color ( 16758465 );
  public static final Color LIGHTSALMON 	= new Color ( 16752762 );
  public static final Color LIGHTSEAGREEN 	= new Color ( 2142890 );
  public static final Color LIGHTSKYBLUE 	= new Color ( 8900346 );
  public static final Color LIGHTSLATEGRAY 	= new Color ( 7833753 );
  public static final Color LIGHTSTEELBLUE 	= new Color ( 11584734 );
  public static final Color LIGHTYELLOW 	= new Color ( 16777184 );
  public static final Color LIMEGREEN 		= new Color ( 3329330 );
  public static final Color LINEN 			= new Color ( 16445670 );
  public static final Color MEDIUMAQUAMARINE = new Color ( 6737322 );
  public static final Color MEDIUMBLUE 		= new Color ( 205 );
  public static final Color MEDIUMORCHID 	= new Color ( 12211667 );
  public static final Color MEDIUMPURPLE 	= new Color ( 9662672 );
  public static final Color MEDIUMSEAGREEN 	= new Color ( 3978097 );
  public static final Color MEDIUMSLATEBLUE = new Color ( 8087790 );
  public static final Color MEDIUMSPRINGGREEN = new Color ( 64154 );
  public static final Color MEDIUMTURQOISE 	= new Color ( 4772300 );
  public static final Color MEDIUMVIOLETRED = new Color ( 13047173 );
  public static final Color MIDNIGHTBLUE 	= new Color ( 1644912 );
  public static final Color MINTCREAM 		= new Color ( 16121850 );
  public static final Color MISTYROSE 		= new Color ( 16770273 );
  public static final Color MOCCASIN 		= new Color ( 16770229 );
  public static final Color NAVAJOWHITE 	= new Color ( 16768685 );
  public static final Color OLDLACE 		= new Color ( 16643558 );
  public static final Color OLIVEDRAB 		= new Color ( 7048739 );
  public static final Color ORANGE 			= new Color ( 16753920 );
  public static final Color ORANGERED 		= new Color ( 16729344 );
  public static final Color ORCHID			= new Color ( 14315734 );
  public static final Color PALETGOLDENROD 	= new Color ( 15657130 );
  public static final Color PALETGREEN 		= new Color ( 10025880 );
  public static final Color PALETTURQUOISE 	= new Color ( 11529966 );
  public static final Color PALETVIOLETRED 	= new Color ( 14381203 );
  public static final Color PAPAYAWHIP 		= new Color ( 16773077 );
  public static final Color PEACHPUFF		= new Color ( 16767673 );
  public static final Color PERU 			= new Color ( 13468991 );
  public static final Color PINK	 		= new Color ( 16761035 );
  public static final Color PLUM 			= new Color ( 14524637 );
  public static final Color POWDERBLUE 		= new Color ( 11591910 );
  public static final Color ROSYBROWN 		= new Color ( 12357519 );
  public static final Color ROYALBLUE 		= new Color ( 4286945 );
  public static final Color SADDLEBROWN 	= new Color ( 9127187 );
  public static final Color SALMON 			= new Color ( 16416882 );
  public static final Color SANDYBROWN 		= new Color ( 16032864 );
  public static final Color SEAGREEN 		= new Color ( 3050327 );
  public static final Color SEASHELL 		= new Color ( 16774638 );
  public static final Color SIENNA 			= new Color ( 10506797 );
  public static final Color SKYBLUE 		= new Color ( 8900331 );
  public static final Color SLATEBLUE 		= new Color ( 6970061 );
  public static final Color SLATEGRAY 		= new Color ( 7372944 );
  public static final Color SNOW 			= new Color ( 16775930 );
  public static final Color SPRINGGREEN 	= new Color ( 65407 );
  public static final Color STEELBLUE 		= new Color ( 4620980 );
  public static final Color TAN 			= new Color ( 13808780 );
  public static final Color THISTLE 		= new Color ( 14204888 );
  public static final Color TOMATO			= new Color ( 16737095 );
  public static final Color TURQUOISE		= new Color ( 4251856 );
  public static final Color VIOLET			= new Color ( 15631086 );
  public static final Color WHEAT 			= new Color ( 16113331 );
  public static final Color WHITESMOKE 		= new Color ( 16119285 );
  public static final Color YELLOWGREEN 	= new Color ( 10145074 );

  /**
   * Kreiert eine Liste aller public static Color, die diese Klasse enth�lt.
   * @return Ein Hashtable, Schl�ssel sind die Namen der Farben.
   */
  public static Hashtable<String, Color> createColorList (){
    Field [] field = Colors.class.getFields();
    Hashtable<String, Color> table = new Hashtable<String, Color> ();

    for ( int i = 0; i < field.length; i++ ){
      if ( Modifier.isStatic( field[i].getModifiers() ) &&
           Modifier.isPublic( field[i].getModifiers() ) &&
           field[i].getType().equals( Color.class ) ){
        try {
          table.put( field[i].getName(), (Color)field[i].get(null) );
        }
        catch (IllegalArgumentException ex) {}
        catch (IllegalAccessException ex) {}
      }
    }

    return table;
  }

  /**
   * Erstellt einen Array mit allen vorhanden Farben.
   * @return alle Farben.
   */
  public static Color [] createColorArray(){
    Field [] field = Colors.class.getFields();

    Vector<Color> vector = new Vector<Color>();

    for ( int i = 0; i < field.length; i++ ){
      if ( Modifier.isStatic( field[i].getModifiers() ) &&
           Modifier.isPublic( field[i].getModifiers() ) &&
           field[i].getType().equals( Color.class ) ){
        try {
          vector.add( (Color)field[i].get(null) );
        }
        catch (IllegalArgumentException ex) {}
        catch (IllegalAccessException ex) {}
      }
    }

    return vector.toArray( new Color [vector.size()]);
  }
  
  /**
   * Generiert eine Farbe aus einem Regenbogen.
   * @param index Der Index der Farbe, eine Zahl zwischen 0 und 1
   * @return Die Farbe
   */
  public static Color rainbow( double index ) {
	  if( index < 0 || index > 1 )
		  throw new IllegalArgumentException( "Index must be >= 0 and <= 1" );
	  
		index *= 5;

		// RGB = 100
		if( index <= 1 )
			return new Color( 255, (int)(255 * index), 0 );
		// RGB = 110
		if( --index <= 1 )
			return new Color( (int)(255 * (1 - index)), 255, 0 );
		// RGB = 010
		if( --index <= 1 )
			return new Color( 0, 255, (int)(255 * index) );

		// RGB = 011
		if( --index <= 1 )
			return new Color( 0, (int)(255 * (1 - index)), 255 );
		// RGB = 001
		if( --index <= 1 )
			return new Color( (int)(255 * index), 0, 255 );
			
		return new Color( 255, 0, 255 );
	}
  
  /**
   * Liefert den invertierten Wert von c.
   */
  public static Color invert ( Color c ){
    if ( c == null )
      return null;
      return new Color ( 0x00FFFFFF - c.getRGB());
  }
  /**
   * Erh�ht die Helligkeit von c.
   * <p>
   * @param c Die Grundfarbe.
   * @param value Ein Wert zwischen 0 und 1, er gibt an, wiefest die Farbe
   * erhellt werden soll, 0 bedeutet gar nicht, 1 bedeutet weiss.
   * @deprecated {@link #brighter(Color, double)} sollte benutzt werden.
   */
  @Deprecated
  public static Color clearer ( Color c, double value ){
    if ( c == null )
      return null;

    int dr, dg, db;

    if ( value > 1 ) value = 1;
    if ( value < 0 ) value = 0;

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
   * Verringert die Helligkeit von c.
   * @param c Die Grundfarbe.
   * @param value Ein Wert zwischen 0 und 1, 1 bedeutet schwarz, 0 bedeutet, dass
   * die Grundfarbe zur�ckgegeben wird.
   */
  public static Color darker ( Color c, double value ){
    if ( c == null )
      return null;

    if ( value > 1 ) value = 1;
    if ( value < 0 ) value = 0;

    value = 1 - value;

    return new Color (
      (int)(c.getRed() * value),
      (int)(c.getGreen() * value),
      (int)(c.getBlue() * value)
    );
  }
  
  public static Color brighter( Color c, double value ){
      return clearer( c, value );
  }
  
  /**
   * Verringert oder erh�ht die Helligkeit der Farbe.
   * @param c Die Grundfarbe.
   * @param value Ein Wert zwischen 0 und 1, 0 bedeutet schwarz, 1 weiss und
   * 0.5 dass die Grundfarbe zur�ckgegeben wird.
   */
  public static Color brightness ( Color c, double value ){
    if ( c == null )
      return c;

    if ( value > 1 ) value = 1;
    if ( value < 0 ) value = 0;

    value -= 0.5;
    value *= 2;

    if ( value >= 0 )
      return clearer ( c, value );
    else
      return darker ( c, -value );
  }
  /**
   * Ver�ndert die Helligkeit so, dass die neue Farbe ein sichtbarer
   * Unterschied zur Grundfarbe aufweist.
   * <p>
   * Die Methode entscheidet selbst, ob die Helligkeit erh�ht oder
   * verringert werden muss.
   * @param c Die Grundfarbe
   * @param value Die Ver�nderung, ein Wert zwischen 0 und 1, bei 0 kommt
   * immer die Grundfarbe zur�ck, bei 1 immer schwarz oder weiss.
   */
  public static Color autoColor (Color c, double value){
    if ( c == null )
      return null;

    if ( isDark ( c ) )
      return clearer ( c, value );
    else
      return darker ( c, value );
  }
  /**
   * Berechnet, ob diese Farbe dunkel oder hell ist.
   * <p>
   * Dazu werden die einzelnen Farbkomponenten (rot, gr�n, blau) mit
   * dem Maximalen Wert 255 verglichen.
   * Ist der durchschnittliche Wert kleiner als die H�lfte des Maximums, wird
   * true zur�ckgegeben.
   */
  public static boolean isDark ( Color c ){
    double summe = c.getRed() + c.getGreen() + c.getBlue();

    return (summe / (3.0 * 255.0)) < 0.5;
  }
  /**
   * Berechnet, ob diese Farbe dunkel oder hell ist.
   * <p>
   * @return Der Umkehrwert von {@link #isDark isDark( Color )}.
   */
  public static boolean isClear ( Color c ){
    return ! isDark(c);
  }
  /**
   * Erechnet eine Farbe zwischen a und b.
   * @param a Die 1. Farbe.
   * @param b Die 2. Farbe.
   * @param value Zwischen 0 und 1.
   * @return Wenn value = 0, a, wenn value = 1, b, ansonsten eine Mischung dazwischen.
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
   * Ensures that the color <code>constant</code> and the color
   * <code>changeable</code> are different. The same result would be achieved
   * by invoking.
   * {@link #different(Color, Color, float) different( constant, changeable, 0.3f )}.
   * @param constant The constant color
   * @param changeable The color that might be changed
   * @return <code>changeable</code> or a new created color
   */
  public static Color different( Color constant, Color changeable ){
      return different( constant, changeable, 0.3f );
  }
  
  /**
   * Ensures that the color <code>constant</code> and the color
   * <code>changeable</code> are different.
   * @param constant The color that will not change
   * @param changeable The color that might be changed
   * @param distance The minimal difference of brightness
   * @return <code>changeable</code> or a new created color
   */
  public static Color different( Color constant, Color changeable, float distance ){
      float[] constHSB = Color.RGBtoHSB( constant.getRed(), constant.getGreen(), constant.getBlue(), null );
      float[] changeHSB = Color.RGBtoHSB( changeable.getRed(), changeable.getGreen(), changeable.getBlue(), null );
      
      float constB = constHSB[2];
      float changeB = changeHSB[2];
      
      if( Math.abs( constB - changeB ) < distance ){
          if( constB > changeB ){
              changeB = constB - distance;
              if( changeB < 0 )
                  changeB = Math.min( constB + distance, 1f );
          }
          else{
              changeB = constB + distance;
              if( changeB > 1 )
                  changeB = Math.max( constB - distance, 0f );
          }
      }
      else
          return changeable;
      
      changeHSB[2] = changeB;
      return Color.getHSBColor( changeHSB[0], changeHSB[1], changeHSB[2] );
  }
}




