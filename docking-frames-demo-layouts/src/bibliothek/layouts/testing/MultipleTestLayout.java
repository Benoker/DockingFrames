package bibliothek.layouts.testing;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import bibliothek.gui.dock.common.MultipleCDockableLayout;
import bibliothek.util.xml.XElement;

public class MultipleTestLayout implements MultipleCDockableLayout{
    private String content;

    public void setContent( String content ) {
        this.content = content;
    }
    
    public String getContent() {
        return content;
    }
    
    public void readStream( DataInputStream in ) throws IOException {
        content = in.readUTF();
    }

    public void readXML( XElement element ) {
        content = element.getString();
    }

    public void writeStream( DataOutputStream out ) throws IOException {
        out.writeUTF( content );
    }

    public void writeXML( XElement element ) {
        element.setString( content );
    }
}
