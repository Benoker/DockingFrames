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

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
/**
 * CIn bietet die Möglichkeit c++ Dateien binär auszulesen.
 * @author Benjamin Sigg
 */
public class CIn extends InputStream{
  private InputStream in;
  /**
   * Konstruktor.
   * @param file Die Datei welche ausgelesen werden soll.
   * @throws IOException Bei Problemen.
   */
  public CIn ( File file ) throws IOException {
    this ( new FileInputStream ( file ));
  }
  /**
   * Konstruktor mit dem InpüutStream der verwendet werden soll.
   * @param in Der zu verwendende InputStream.
   */
  public CIn ( InputStream in ){
    this.in = in;
  }
  /**
   * Returns the number of bytes that can be read (or skipped over)
   * from this input stream without blocking by the next caller of a method for this input stream.
   * @return The number
   */
  @Override
  public int available() throws IOException {
    return in.available();
  }
  /**
   * Schliesst den InputStream.
   * @throws IOException Bei Problemen.
   */
  @Override
  public void close () throws IOException{
    if ( in != null )
      in.close();
  }
  @Override
  public void mark ( int readlimit ){
    in.mark( readlimit );
  }
  @Override
  public boolean markSupported() {
    return in.markSupported();
  }
  /**
   * @return Ergebnis von read aus dem gegebenen InputStream.
   * @throws IOException bei Problemen.
   */
  @Override
  public int read() throws IOException{
    return in.read();
  }
  @Override
  public int read ( byte [] b ) throws IOException {
    return in.read( b );
  }
  /**
   * Liest die nächsten bytes aus. Sie werden als ints gespeichert, damit
   * keine Vorzeichenprobleme auftauchen. Dadurch erhält man die tatsächliche
   * Abfolge von Bits, was für weitere bitweise arbeitenden Operatoren wichtig
   * ist.
   * @param data Der Array, welcher gefüllt werden soll.
   * @throws IOException Bei Problemen.
   */
  public void read ( int [] data ) throws IOException {
    for ( int i = 0; i < data.length; i++)
      data [i] = read ();
  }

  @Override
  public int read ( byte [] b, int off, int len ) throws IOException {
    return in.read( b, off, len );
  }

  @Override
  public void reset () throws IOException {
    in.reset();
  }

  @Override
  public long skip(long n) throws IOException {
    return in.skip( n );
  }

  /**
   * Vereint den Bytearray zu einem Short, der Array muss mindestens Länge 2 haben.
   * @param data Der Array.
   * @return Der Short.
   */
  protected static final short byteToShort ( int [] data ){
    short back = (short)data[1];
    back <<= 8;
    back += (short)data[0];
    return back;
  }
  /**
   * Verwandelt die als Integer gespeicherten Bytes in einen Integer.
   * @param data Die Bytes.
   * @return Der Integer.
   */
  protected static final int byteToInt ( int [] data ){
    int back = 0;

    for ( int i = 0; i < 4; i++){
      back <<= 8;
      back += data [3-i];
    }
    return back;
  }
  /**
   * Verwandelt die als Integer gespeicherten Bytes in einen Long.
   * @param data Die Bytes.
   * @return Der Long.
   */
  protected static final long byteToLong ( int [] data ){
    long back = 0;

    for ( int i = 0; i < 8; i++){
      back <<= 8;
      back += data [7-i];
    }
    return back;
  }
  /**
   * Liest das nächste Byte und gibt es zurück.
   * @return Das Byte.
   * @throws IOException Bei Problemen.
   */
  public byte readByte () throws IOException {
    return (byte)read();
  }
  /**
   * Liest den nächste Short und gibt ihn zurück.
   * @return Der Short.
   * @throws IOException Bei Problemen.
   */
  public short readShort () throws IOException {
    int data[] = new int [2];
    read ( data );
    return byteToShort ( data );
  }
  /**
   * Liest den nächste Integer und gibt ihn zurück.
   * @return Der Integer.
   * @throws IOException Bei Problemen.
   */
  public int readInt () throws IOException {
    int [] data = new int [4];
    read( data );
    return byteToInt ( data );
  }

  public int readWORD () throws IOException {
    return readShort();
  }

  public int readDWORD () throws IOException {
    return readInt();
  }

  /**
   * Liest den nächste Long und gibt ihn zurück.
   * @return Der Long.
   * @throws IOException Bei Problemen.
   */
  public long readLong () throws IOException {
    int [] data = new int [8];
    read( data );
    return byteToLong ( data );
  }
  /**
   * Liest den nächste Float und gibt ihn zurück.
   * @return Der Float.
   * @throws IOException Bei Problemen.
   */
  public float readFloat () throws IOException {
    return Float.intBitsToFloat( readInt() );
  }
  /**
   * Liest den nächste Double und gibt ihn zurück.
   * @return Der Double.
   * @throws IOException Bei Problemen.
   */
  public double readDouble () throws IOException {
    return Double.longBitsToDouble( readLong() );
  }
  /**
   * Der Stream löst sich vom benutzten InputStream. Damit ist sichergestellt, dass
   * dieser CIn niemals den benutzten Stream schliessen wird. Dieser Stream kann
   * also noch anderweitig benutzt werden.
   * @return Der benutzte InputStream.
   */
  public InputStream finish (){
    InputStream stream = in;
    finalize();
    return stream;
  }

  @Override
  protected void finalize() {
    in = null;
  }
}