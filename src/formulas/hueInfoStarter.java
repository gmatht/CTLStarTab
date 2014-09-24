package formulas;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;

public class hueInfoStarter implements ActionListener {
	
	StarTab mst;
	int hue;

	public void actionPerformed(ActionEvent e) {
		// TODO Auto-generated method stub
		new HueInfoDialog(mst,hue).setVisible(true);
	}
	
	public hueInfoStarter(StarTab mst, int hue){
		this.mst=mst;
		this.hue=hue;
	}

}
