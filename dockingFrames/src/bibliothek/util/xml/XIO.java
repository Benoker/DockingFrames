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
package bibliothek.util.xml;

import java.io.*;
import java.util.LinkedList;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

/**
 * Class used to read and write xml-files. Clients should use
 * {@link #writeUTF(XElement, OutputStream)} and {@link #readUTF(InputStream)} to
 * guarantee maximal independence from the underlying file system.
 * @author Benjamin Sigg
 */
public class XIO {
    /**
     * Writes the contents of <code>element</code> into <code>out</code>.
     * @param element the element to write
     * @param out the stream to write into
     * @throws IOException if an I/O-error occurs
     */
    public static void write( XElement element, Appendable out ) throws IOException{
        out.append( "<?xml version='1.0'?>\n" );
        write( element, 0, out );
    }
    
    /**
     * Writes the contents of <code>element</code> into <code>out</code> using
     * the <code>UTF-8</code> encoding.
     * @param element the element to write
     * @param out the stream to write into
     * @throws IOException if an I/O-error occurs
     */
    public static void writeUTF( XElement element, OutputStream out ) throws IOException{
        write( element, out, "UTF-8" );
    }
    
    /**
     * Writes the contents of <code>element</code> into <code>out</code>.
     * @param element the element to write
     * @param out the stream to write into
     * @param encoding the encoding for the characters
     * @throws IOException if an I/O-error occurs
     */
    public static void write( XElement element, OutputStream out, String encoding ) throws IOException{
        OutputStreamWriter writer = new OutputStreamWriter( out, encoding ){
            @Override
            public void close() throws IOException {
                // ignore
            }
        };
        write( element, writer );
        writer.flush();
    }

    /**
     * Reads a xml file provided from <code>in</code> using <code>UTF-8</code>
     * as encoding.
     * @param in the stream to read from
     * @return the root element
     * @throws IOException if an I/O-error occurs
     */
    public static XElement readUTF( InputStream in ) throws IOException{
        return read( in, "UTF-8" );
    }
    
    /**
     * Reads a xml file provided from <code>in</code> using <code>encoding</code>
     * as encoding.
     * @param in the stream to read from
     * @param encoding the encoding used to decode characters
     * @return the root element
     * @throws IOException if an I/O-error occurs
     */
    public static XElement read( InputStream in, String encoding ) throws IOException{
        InputStreamReader reader = new InputStreamReader( in, encoding ){
            @Override
            public void close() throws IOException {
                // ignore
            }
        };
        
        return read( new InputSource( reader ));
    }

    
    /**
     * Writes the contents of <code>element</code> into <code>out</code>.
     * @param element the element to write
     * @param tabs the number of tabs before the element
     * @param out the stream to write into
     * @throws IOException if an I/O-error occurs
     */
    private static void write( XElement element, int tabs, Appendable out ) throws IOException{
        for( int i = 0; i < tabs; i++ )
            out.append( "\t" );
        
        out.append( "<" );
        out.append( element.getName() );
        for( XAttribute attribute : element.attributes() ){
            out.append( " " );
            out.append( attribute.getName() );
            out.append( "=\"" );
            encode( attribute.getString(), out );
            out.append( "\"" );
        }
        String value = element.getValue();
        XElement[] children = element.children();
        
        if( value.length() == 0 && children.length == 0 ){
            out.append( "/>" );
        }
        else{
            out.append( ">" );
            if( value.length() > 0 ){
                if( children.length > 0 ){
                    out.append( "\n\t" );
                    for( int i = 0; i < tabs; i++ )
                        out.append( "\t" );
                }
                    
                encode( value, out );
            }
            if( children.length > 0 ){
                out.append( "\n" );
                for( XElement child : children ){
                    write( child, tabs+1, out );
                    out.append( "\n" );
                }
                
                for( int i = 0; i < tabs; i++ )
                    out.append( "\t" );
            }
            
            out.append( "</" );
            out.append( element.getName() );
            out.append( ">" );
        }
    }
    
    /**
     * Encodes <code>value</code> such that it is a valid string in a xml-file.
     * @param value the value to encode
     * @param out the stream to write into
     */
    private static void encode( String value, Appendable out ) throws IOException{
        for( int i = 0, n = value.length(); i<n; i++ ){
            char c = value.charAt( i );
            switch( c ){
                case '<':
                    out.append( "&lt;" );
                    break;
                case '>':
                    out.append( "&gt;" );
                    break;
                case '\'':
                    out.append( "&apos;" );
                    break;
                case '"':
                    out.append( "&quot;" );
                    break;
                case '&':
                    out.append( "&amp;" );
                    break;
                default:
                    out.append( c );
                    break;
            }
        }
    }
    
    /**
     * Interprets <code>text</code> as a xml-file and reads it.
     * @param text the content to read, in xml format
     * @return the root element of <code>text</code>
     * @throws IOException if an I/O-error occurs
     */
    public static XElement read( CharSequence text ) throws IOException{
        InputSource in = new InputSource();
        in.setCharacterStream( new StringReader( text.toString() ) );
        return read( in );
    }
    
    /**
     * Reads a xml-file from <code>source</code>.
     * @param source the xml-file
     * @return the root element of the file.
     * @throws IOException if an I/O-error occurs
     */
    public static XElement read( InputSource source ) throws IOException{
        SAXParserFactory factory = SAXParserFactory.newInstance();
        try {
            SAXParser parser = factory.newSAXParser();
            Handler handler = new Handler();
            parser.parse( source, handler );
            return handler.getElement();
        }
        catch( ParserConfigurationException e ) {
            throw new IOException( e.getMessage() );
        }
        catch( SAXException e ) {
            throw new IOException( e.getMessage() );
        }
    }

    /**
     * Reads a xml file provided from <code>reader</code>.
     * @param reader the reader from which characters will be read
     * @return the root element
     * @throws IOException if an I/O-error occurs
     */
    public static XElement read( Reader reader ) throws IOException{
        return read( new InputSource( reader ));
    }
    
    /**
     * A handler used to read from a {@link SAXParser}.
     * @author Benjamin Sigg
     */
    private static class Handler extends DefaultHandler{
        /** the first element that was read */
        private XElement element;
        /** the current stack of active entries */
        private LinkedList<XElement> stack = new LinkedList<XElement>();
        
        /**
         * Gets the first element that was read.
         * @return the first element
         */
        public XElement getElement() {
            return element;
        }
        
        @Override
        public void startElement( String uri, String localName, String name,
                Attributes attributes ) throws SAXException {

            XElement element = new XElement( name );
            if( this.element == null ){
                this.element = element;
            }
            else{
                stack.getFirst().addElement( element );
            }
            
            stack.addFirst( element );
            
            // read the attributes
            for( int i = 0, n = attributes.getLength(); i<n; i++ ){
                XAttribute attr = new XAttribute( attributes.getQName( i ));
                attr.setString( attributes.getValue( i ));
                element.addAttribute( attr );
            }
        }
        
        @Override
        public void characters( char[] ch, int start, int length ) throws SAXException {
            if( length > 0 ){
                String value = new String( ch, start, length );
                String old = stack.getFirst().getValue();
                if( old != null && old.length() > 0 ){
                    value = old + value;
                }
                stack.getFirst().setValue( value );
            }
        }
        
        @Override
        public void endElement( String uri, String localName, String name )
                throws SAXException {

            XElement element = stack.removeFirst();
            element.setValue( element.getString().trim() );
        }
    }
}
