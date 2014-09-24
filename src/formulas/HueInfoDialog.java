package formulas;

import java.awt.FlowLayout;

import javax.swing.JDialog;
import javax.swing.JTextArea;

public class HueInfoDialog extends JDialog {

	
	public HueInfoDialog(StarTab mst, int hue){
		getContentPane().setLayout(new FlowLayout());
		getContentPane().add(new JTextArea("Info about hue number "+hue+":\n "+mst.getHue(hue)));
	}
	
}
