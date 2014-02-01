package tutorial.support;

import java.awt.Color;
import java.awt.Component;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.ClipboardOwner;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;

import javax.swing.JScrollPane;
import javax.swing.JTextPane;
import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultStyledDocument;
import javax.swing.text.Element;
import javax.swing.text.ParagraphView;
import javax.swing.text.Style;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledEditorKit;
import javax.swing.text.View;
import javax.swing.text.ViewFactory;

public class CodePanel {
	private JTextPane textPane;
	private String code;
	private static String[] KEYWORDS = {
		"private", "protected", "public", "package", "import",
		"class", "interface", "extends", "implements", "instanceof", "super", "abstract",
		"if", "else", "while", "do", "for", "switch", "case", "return", "new",
		"volatile", "transient", "final", "static",
		"byte", "short", "char", "int", "long", "float", "double", "boolean", "null", "true", "false", "void",
		"try", "catch", "finally", "this"
	};
	
	public CodePanel(){
		textPane = new JTextPane();
		
		StyledEditorKit editorKit = new StyledEditorKit(){
			private ViewFactory factory = new ViewFactory(){
				public View create( Element elem ){
					View result = getOldViewFactory().create( elem );
					if( result instanceof ParagraphView ){
						return new ParagraphView( elem ){
						    public void layout( int width, int height ){
						        super.layout( Short.MAX_VALUE, height );
						    }
						    
						    public float getMinimumSpan( int axis ){
						        return super.getPreferredSpan( axis );
						    }
						    
						    
						};
					}
					return result;
				}
			};
			
			private ViewFactory getOldViewFactory(){
				return super.getViewFactory();
			}
			
			@Override
			public ViewFactory getViewFactory(){
				return factory;
			}
		};
		
		textPane.setEditorKit( editorKit );
		textPane.setEditable( false );
	}
	
	public Component toComponent(){
		return new JScrollPane( textPane );
	}
	
	public void copy(){
		Clipboard board = Toolkit.getDefaultToolkit().getSystemClipboard();
		StringSelection selection = new StringSelection( code );
		board.setContents( selection, new ClipboardOwner(){
			public void lostOwnership( Clipboard clipboard, Transferable contents ){
				// ignore	
			}
		});
	}
	
	public void setCode( String code ){
		this.code = code;
		try{
			DefaultStyledDocument document = new DefaultStyledDocument();
			
			Style textStyle = document.addStyle( "text", null );
			Style keywordStyle = document.addStyle( "keyword", textStyle );
			Style commentStyle = document.addStyle( "comment", textStyle );
			Style stringStyle = document.addStyle( "string", textStyle );
			
			StyleConstants.setFontFamily( textStyle, "Monospaced" );
			StyleConstants.setBold( textStyle, false );
			
			StyleConstants.setForeground( keywordStyle, new Color( 0, 0, 150 ));
			StyleConstants.setBold( keywordStyle, true );
			
			StyleConstants.setForeground( commentStyle, new Color( 0, 150, 0 ));
			
			StyleConstants.setForeground( stringStyle, Color.BLUE );

			
			boolean inString = false;
			boolean inMultiLineComment = false;
			boolean inSingleLineComment = false;
			
			int blockStart = 0;
			
			int length = code.length();
			int documentLength = 0;
			
			for( int i = 0; i < length; i++ ){
				if( inString ){
					if( code.charAt( i ) == '"' && code.charAt( i-1 ) != '\\' ){
						inString = false;
						document.insertString( documentLength, code.substring( blockStart, i ), stringStyle );
						documentLength = document.getLength();
						blockStart = i+1;
					}
				}
				else if( inSingleLineComment ){
					if( code.charAt( i ) == '\n' || code.charAt( i ) == '\r' ){
						inSingleLineComment = false;
						document.insertString( documentLength, code.substring( blockStart, i ), commentStyle );
						documentLength = document.getLength();
						blockStart = i;
					}
				}
				else if( inMultiLineComment ){
					if( code.charAt( i ) == '/' && code.charAt( i-1 ) == '*' ){
						inMultiLineComment = false;
						document.insertString( documentLength, code.substring( blockStart, i+1 ), commentStyle );
						documentLength = document.getLength();
						blockStart = i+1;
					}
				}
				else {
					char c = code.charAt( i );
					if( !Character.isJavaIdentifierPart( c )){
						boolean isKeyword = false;
						
						if( i > blockStart ){
							String word = code.substring( blockStart, i );
							
							for( String keyword : KEYWORDS ){
								if( keyword.equals( word )){
									isKeyword = true;
									break;
								}
							}
							
							if( isKeyword ){
								if( documentLength < blockStart ){
									document.insertString( documentLength, code.substring( documentLength, blockStart ), textStyle );
									documentLength = document.getLength();
									
								}
								document.insertString( documentLength, word, keywordStyle );
								documentLength = document.getLength();
							}
						}
						
						if( c == '"'){
							blockStart = i;
							if( documentLength < blockStart ){
								document.insertString( documentLength, code.substring( documentLength, blockStart ), textStyle );
								documentLength = document.getLength();
							}
							inString = true;
						}
						else if( c == '/' ){
							blockStart = i;
							if( documentLength < blockStart ){
								document.insertString( documentLength, code.substring( documentLength, blockStart ), textStyle );
								documentLength = document.getLength();
								
							}
							if( i+1 < length && code.charAt( i+1 ) == '*' ){
								inMultiLineComment = true;
							}
							else if( i+1 < length && code.charAt( i+1 ) == '/' ){
								inSingleLineComment = true;
							}
						}
						else{
							blockStart = i+1;
						}
					}
				}
			}
			
			if( documentLength < length ){
				if( inString ){
					document.insertString( documentLength, code.substring( documentLength ), stringStyle );
				}
				else if( inSingleLineComment || inMultiLineComment ){
					document.insertString( documentLength, code.substring( documentLength ), commentStyle );
				}
				else{
					document.insertString( documentLength, code.substring( documentLength ), textStyle );
				}
			}
			
			textPane.setDocument( document );
		}
		catch( BadLocationException e ){
			e.printStackTrace();
		}
	}
}
