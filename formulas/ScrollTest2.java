package formulas;



/*
 * ScrollTest2.java
 *
 * Created on August 7, 2003, 10:04 AM
 */

/**
 *
 * @author  C. W. David
 * as helped by Tim Boudreau at SUN
 */

import java.awt.*;
import javax.swing.*;
import java.awt.event.*;

//added to get addWindowListener(this) to work
// after adding jScrollPane1


public class ScrollTest2 extends javax.swing.JFrame implements WindowListener{


//"implements WindowListener" added after dropping JScrollPane into JFrame


    
    /** Creates new form ScrollTest2 */
    public ScrollTest2() {
        initComponents();

        MyComponent figure = new MyComponent();
        jScrollPane1.add(figure);
        //the following are all required to get scrolling to take place:
        figure.setPreferredSize(new Dimension(600,600));

//the above is set large enough to exceed the frame size(below)

        jScrollPane1.setViewportView(figure);
        //end of scrolling required matters.
        int frameWidth = 400;
        int frameHeight = 400;
        this.setSize(frameWidth, frameHeight);
        this.setVisible(true);
    }
    
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    private void initComponents() {//GEN-BEGIN:initComponents
        jScrollPane1 = new javax.swing.JScrollPane();

        addWindowListener(this);

        getContentPane().add(jScrollPane1, java.awt.BorderLayout.CENTER);

        pack();
    }

    // Code for dispatching events from components to event handlers.

    public void windowActivated(java.awt.event.WindowEvent evt) {
    }

    public void windowClosed(java.awt.event.WindowEvent evt) {
    }

    public void windowClosing(java.awt.event.WindowEvent evt) {
        if (evt.getSource() == ScrollTest2.this) {
            ScrollTest2.this.exitForm(evt);
        }
    }

    public void windowDeactivated(java.awt.event.WindowEvent evt) {
    }

    public void windowDeiconified(java.awt.event.WindowEvent evt) {
    }

    public void windowIconified(java.awt.event.WindowEvent evt) {
    }

    public void windowOpened(java.awt.event.WindowEvent evt) {
    }//GEN-END:initComponents
    
    /** Exit the Application */
    private void exitForm(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_exitForm
        System.exit(0);
    }//GEN-LAST:event_exitForm
    
    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        new ScrollTest2().setVisible(true);
    }
    
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JScrollPane jScrollPane1;
    // End of variables declaration//GEN-END:variables


//Here is where the learning (from Tim Boudreau at SUN) comes.
//One needs to have the paint routine inside some kind of container
//so that the container can be scrolled, and the painting only extends to
//the boundaries of the container.
//This, and the next example, show ways to do this.
//This example is the simplest.

    class MyComponent extends JComponent{
        public void paint(Graphics g){
            Graphics2D g2d = (Graphics2D)g;
            int x=0;
            int y=0;
            int width = getSize().width - 1;
            int height = getSize().height - 1;
            g2d.drawLine(0,0, width,height);
            g2d.drawOval(x,y, width,height);
        }
    }
}
