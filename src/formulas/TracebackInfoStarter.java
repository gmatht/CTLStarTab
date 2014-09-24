package formulas;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class TracebackInfoStarter implements ActionListener {

	StarTab mst;
	int node;

	public void actionPerformed(ActionEvent e) {
		// TODO Auto-generated method stub
		new TracebackInfoDialog(mst,node).setVisible(true);
	}
	
	public TracebackInfoStarter(StarTab mst, int node){
		this.mst=mst;
		this.node=node;
	}

}
