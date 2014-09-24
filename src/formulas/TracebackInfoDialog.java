package formulas;


	import java.awt.Canvas;
import java.awt.FlowLayout;

	import javax.swing.JDialog;
import javax.swing.JTextArea;

	public class TracebackInfoDialog extends JDialog {

		
		public TracebackInfoDialog(StarTab mst, int node){
			getContentPane().setLayout(new FlowLayout());
			
			/*
			String s="";		
			int cnn=node;
			while (cnn>=0){
				s=" Node "+cnn+": "+mst.getNode(cnn).oc()+"\n"+s;
				cnn=mst.getNode(cnn).getParent();
			}	
			JTextArea jta=new JTextArea("Traceback:\n"+s);
			getContentPane().add(jta);
			*/
			
			TracebackCanvas cv=new TracebackCanvas(mst,node);
			getContentPane().add(cv);
		}
		
	}