//import java.awt.*;
import javax.swing.*;
import java.io.*;
import java.nio.charset.*;

class JTextAreaOutputStream extends OutputStream {
    JTextArea ta;
    String charset;
    public JTextAreaOutputStream(JTextArea ta) {
        this.ta = ta;
        charset = null;
    }
    public JTextAreaOutputStream(JTextArea ta, String charset) throws UnsupportedCharsetException {
        this(ta);
        // force exception on unsupported charset now to avoid exceptions in write()
        Charset.forName(charset);
        this.charset = charset;
    }
    public void write(byte[] b) {
        write(b, 0, b.length);
	//int x=ta.getSelectionEnd();
	//ta.select(x,x);
    }
    public void write(byte[] b, int off, int len) {
        // XXX TextArea.append() is not threadsafe. Use JTextArea.
        String s;
	Thread.yield(); // let another thread have some time perhaps to stop this one.
	if (Thread.currentThread().isInterrupted()) {
	      throw new RuntimeException("Stopped by ifInterruptedStop()");
	}
	// It is possible that the thread will be interrupted before we write, but that shouldn't be too serious.
        try {
            if(charset == null) s = new String(b, off, len); 
            else s = new String(b, off, len, charset);
        } catch(UnsupportedEncodingException ex) {
            throw new Error("encoding support was already verified", ex);
        }
        synchronized(ta) {
            ta.append(s);
        }
	//int x=ta.getSelectionEnd();
	//ta.select(x,x);
	javax.swing.text.Document d = ta.getDocument();
	ta.select(d.getLength(), d.getLength()); 
	/*try {
		flush();
	} catch(java.io.IOException ex) {
            throw new Error("java.io.IOException", ex);
        }*/
    }
    public void write(int b) {
        byte[] tmp = {(byte)b};
        write(tmp);
	//int x=ta.getSelectionEnd();
	//ta.select(x,x);
    }
}
