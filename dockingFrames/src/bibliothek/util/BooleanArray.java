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

import java.io.Serializable;

/**
 * Bietet die Möglichkeit Booleans platzsparend zu speichern, indem
 * die Booleans bitweise gespeichert werden.
 * @author Benjamin Sigg
 * @version 1.0
 */
public class BooleanArray implements Serializable{
  private int length = 0;

  private byte [] array;

  /**
   * Standartkonstruktor.
   * @param length Die Anzahl Elemente, die dieser Array später aufnehmen soll,
   * muss grösser gleich 0 sein.
   */
  public BooleanArray( int length ) {
    init ( length );
  }

  public BooleanArray ( int length, boolean value ){
    this ( length );
    fill ( value );
  }
	
	public BooleanArray ( BooleanArray original ){
		this ( original.length() );
		
		for ( int i = 0; i < array.length; i++ )
			array[i] = original.array[i];		
	}
	
  /**
   * Ein leerer Konstruktor, der nichts macht. Subklassen
   * müssen später {@link #init init} aufrufen, wenn der ByteArray
   * initialisiert werden muss.
   */
  protected BooleanArray (){}

  /**
   * Der eigentliche Konstruktor. Hier wird der Bytearray initialisiert.
   * @param length Die Anzahl Booleans, die gespeichert werden sollen.
   */
  protected void init ( int length ){
    if ( length < 0 )
      throw new IllegalArgumentException ( "Length muss grössergleich 0 sein.");

    this.length = length;
    array = new byte [ (int)Math.ceil( length / 8.0 ) ];
  }

  /**
   * Setzt den Wert an der Stelle Index.
   * @param index Der Index des Wertes, der verändert werden möchte.
   * @param value Der neue Wert.
   * @return Der alte Wert.
   */
  public boolean set ( int index, boolean value ){
    if ( index < 0 || index >= length() )
      throw new ArrayIndexOutOfBoundsException ( index );

    int indexByte = getByte ( index );
    int indexBit = getBit ( index );

    byte mask = (byte)(1 << indexBit);

    boolean befor = (array [ indexByte ] & mask) != 0;

    if ( value )
      array [ indexByte ] |= mask;
    else
      array [ indexByte ] &= ~mask;

    return befor;
  }

  /**
   * Liefert den Wert an der Stelle index.
   * @param index Der Index des Wertes.
   * @return Den Wert.
   */
  public boolean get ( int index ){
    if ( index < 0 || index >= length() )
      throw new ArrayIndexOutOfBoundsException ( index );

    int indexByte = getByte ( index );
    int indexBit = getBit ( index );

    byte mask = (byte)(1 << indexBit);

    return (array [ indexByte ] & mask) != 0;
  }

  /**
   * Füllt den gesammten Array mit einem bestimmten Wert.
   * @param value Der Wert, mit dem der ganze Array gefüllt werden soll.
   */
  public void fill ( boolean value ){
    fill ( value, 0, length );
  }

  /**
   * Setzt einen Teil des Arrays auf den angegebenen Wert.
   * @param value Der neue Wert.
   * @param start Der erste Index, der neu gesetzt werden soll.
   * @param size Die Anzahl Elemente, die verändert werden sollen.
   */
  public void fill ( boolean value, int start, int size ){
    if ( start < 0 )
      throw new ArrayIndexOutOfBoundsException ( start );

    if ( size < 0 )
      throw new ArrayIndexOutOfBoundsException ( "Size muss grösser gleich 0 sein." );

    if ( start + size > length )
      throw new ArrayIndexOutOfBoundsException ( start + size );

    if ( size == 0 )
      return;

    int startByte = getByte ( start );
    int startBit = getBit ( start );

    int endByte = getByte ( start + size - 1 );
    int endBit = getBit ( start + size - 1 );

    byte mask = 0;

    for ( int i = startByte; i <= endByte; i++ ){
      // Maske kreieren.
      mask = 0;
      for ( int j = i == startByte ? startBit : 0 ;
                j <= (i == endByte  ? endBit   : 7); j++ ){

        mask |= 1 << j;
      }

      if ( value )
        array [ i ] |= mask;
      else
        array [ i ] &= ~mask;

    }
  }

  /**
   * Liefert die Länge dieses Arrays.
   * @return Die Anzahl Elemente.
   */
  public int length (){
    return length;
  }

  /**
   * Liefert den Index des Bytes, welches diesen boolean speichert.
   * @param index Der Index eines Wertes.
   * @return Der Index des Bytes, das diesen Wert speichert.
   */
  protected int getByte ( int index ){
    return (int)Math.floor( index / 8.0 );
  }

  /**
   * Liefert den Index Bits, welches diesen Boolean repräsentiert.
   * @param index Der Index eines Booleans.
   * @return Der Index des Bits, welches diesen Wert speichert.
   */
  protected int getBit ( int index ){
    return index % 8;
  }

  /**
   * Schreibt den BooleanArray in einen echten Array um.
   * @return Die Werte als Booleans gespeichert.
   */
  public boolean [] toArray (){
    boolean [] back = new boolean [ length ];

    int count = 0;
    int index = 0;

    for ( int i = 0; i < length; i++ ){
      back[i] = (array [index] & 1 << count) != 0;

      count++;

      if ( count == 8 ){
        count = 0;
        index++;
      }
    }

    return back;
  }

}