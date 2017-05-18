package formulas;


import java.awt.FlowLayout;

import javax.swing.JDialog;
import javax.swing.JTextArea;

public class RXInfoDialog extends JDialog {

	
	public RXInfoDialog(StarTab mst, int node){
		getContentPane().setLayout(new FlowLayout());
		Node pnode=mst.getNode(node);
		String s="Parent Node "+node+": "+pnode.oc()+"\n";
		
		for(int j=0; j<mst.getNode(node).numsucc();j++){
			int sn=pnode.succ(j);
			s=s+" Successor Node "+sn+": \n";
			Node snode=mst.getNode(sn);
			if (snode.isLeaf()){
				s=s+" is a leaf.\n";
			} else {
				for(int h=0;h<snode.oc().size();h++){
					int shn=snode.oc().getHue(h);
					int phn=mst.getTraceback(shn,pnode.oc().getAllHueNumbers());
					s=s+" hue "+shn+" traces back to parent hue "+phn+".\n";
				}
			}
			s=s+"\n";
		}
		
		JTextArea jta=new JTextArea("RX:\n"+s);
		getContentPane().add(jta);
	}
	
}
