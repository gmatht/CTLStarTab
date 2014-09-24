package formulas;

import java.awt.*;

import javax.swing.JButton;
import javax.swing.JTextArea;
import javax.swing.JDialog;


public class NodeDialog extends JDialog {

	public NodeDialog(StarTab mst, int node){
		getContentPane().setLayout(new FlowLayout());
		getContentPane().add(new JTextArea("Info about node number "+node+":\n "+mst.getNode(node)));
		
		Node n=mst.getNode(node);
		if (n.isLeaf()){
			
		} else {
		int[] hues=n.oc().getAllHueNumbers();
		JButton[] hb=new JButton[hues.length];
		for(int i=0;i<hues.length;i++){
			hb[i]=new JButton("hue "+hues[i]);
			hb[i].addActionListener(new hueInfoStarter(mst,hues[i]));
			getContentPane().add(hb[i]);
		}
		
		JButton traceback=new JButton("Traceback");
		traceback.addActionListener(new TracebackInfoStarter(mst,node));
		getContentPane().add(traceback);
		
		
		JButton rx=new JButton("R_X");
		rx.addActionListener(new RXInfoStarter(mst,node));
		getContentPane().add(rx);
		
		}
	
	}
}
