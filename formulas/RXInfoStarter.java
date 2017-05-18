package formulas;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class RXInfoStarter implements ActionListener {

	StarTab mst;
	int node;

	public void actionPerformed(ActionEvent e) {
		// TODO Auto-generated method stub
		new RXInfoDialog(mst,node).setVisible(true);
	}
	
	public RXInfoStarter(StarTab mst, int node){
		this.mst=mst;
		this.node=node;
	}

}
