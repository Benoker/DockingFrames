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
 
package bibliothek.io;

import java.io.DataOutputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

/**
 * COut bietet die Möglichkeit die primitiven Datentypen umgekehrt zur in Java
 * üblichen Form zu schreiben. Dabei ist die Reihenfolge der bytes gemeint.
 * Dies ermöglicht es, dass c++ Programme die damit geschriebenen Dateien
 * auslesen können.
 * <p>
 * Beachten Sie, dass in Java signed Datatypen verwendet werden, jedoch
 * c++ auch unsigned kennt. Negative Werte können also zu komischen
 * Resultaten führen!
 */

public class COut extends OutputStream{
  private DataOutputStream out = null;
  /**
   * Konstruktor, ihm wird der Pfad der zu schreibenden Datei übergeben.
   * @param path Der Pfad.
   */
  public COut ( String path ){
    try{
      out = new DataOutputStream ( new FileOutputStream ( path ));
    }
    catch ( FileNotFoundException e ){}
  }
  /**
   * Konstruktor mit OutputStream, welcher benutzt werden soll.
   * @param out Der zu benutzende OutputStream.
   */
  public COut ( OutputStream out ){
    this.out = new DataOutputStream ( out );
  }
  /**
   * Schreibt dieses Byte in den benutzten OutputStream.
   * @param b Das Byte.
   * @throws IOException Bei Problemen.
   */
  public void writeByte ( byte b ) throws IOException {
    byte[] data = { b };
    out.write( data );
    out.flush();
  }

  @Override
  public void write ( byte [] b ) throws IOException {
    out.write( b );
    out.flush();
  }

  @Override
  public void write ( byte [] b, int off, int len ) throws IOException {
    out.write( b, off, len );
  }

  public void writeWORD ( int i ) throws IOException {
    writeShort ( (short)i );
  }
  public void writeDWORD ( int i ) throws IOException {
    writeInt ( i );
  }

  /**
   * Schreibt einen Short.
   * @param s Der Short.
   * @throws IOException Bei Problemen.
   */
  public void writeShort ( short s ) throws IOException {
    out.write( toByte ( s ) );
    out.flush();
  }
  /**
   * Schreibt einen Integer.
   * @param i Der Integer.
   * @throws IOException Bei Schreibproblemen
   */
  public void writeInt ( int i ) throws IOException{
    out.write( toByte ( i ) );
    out.flush();
  }

  @Override
  public void write ( int i ) throws IOException {
    out.write( i );
  }

  /**
   * Schreibt einen Long.
   * @param l Der Long.
   * @throws IOException Bei Problemen.
   */
  public void writeLong ( long l ) throws IOException {
    out.write( toByte ( l ) );
    out.flush();
  }
  /**
   * Schreibt einen Float.
   * @param f Der Float.
   * @throws IOException Bei Schreibproblemen
   */
  public void writeFloat ( float f ) throws IOException{
    writeInt ( Float.floatToIntBits( f ) );
  }
  /**
   * Schreibt einen Double.
   * @param d Der Double.
   * @throws IOException Bei Problemen
   */
  public void writeDouble ( double d ) throws IOException {
    writeLong ( Double.doubleToLongBits( d ) );
  }

  protected static final byte[] toByte ( short s ){
    byte[] b = new byte [2];
    b[0] = (byte)s;
    b[1] = (byte) ( s >> 8 );
    return b;
  }

  protected static final byte[] toByte ( int i ){
    byte[] b = new byte [4];

    for ( int a = 0; a < 4; a++)
      b [a] = (byte)( i >> ( 8 * a )) ;

    return b;
  }

  protected static final byte[] toByte ( long l ){
    byte[] b = new byte [8];

    for ( int a = 0; a < 8; a++)
      b [a] = (byte)( l >> ( 8 * a )) ;

    return b;
  }
  
  @Override
  public void flush () throws IOException{
    out.flush();
  }
  /**
   * Beendet die Verbindung zwischen diesem OutputStream und dembenutzten Stream.
   * @return Der benutzte Stream;
   */
  public OutputStream finish (){
    OutputStream stream = out;
    finalize();
    return stream;
  }
  /**
   * Schliesst den OutputStream.
   */
  @Override
  public void close (){
    if ( out != null )
      try{
        out.close();
      } catch ( IOException e ){}
  }

  @Override
  protected void finalize (){
    out = null;
  }
}