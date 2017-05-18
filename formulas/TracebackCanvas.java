package formulas;

import java.awt.Canvas;
import java.awt.Graphics;

public class TracebackCanvas extends Canvas {
	
	int nn;
	StarTab mst;
	int width=500;
	int height=2000;
	
	public TracebackCanvas(StarTab mst,int nn){
		this.nn=nn;
		this.mst=mst;
		setSize(width,height);
		
	}
	
    public void paint(Graphics graphics){
		graphics.drawString("Hue Traces to node "+nn, 2,30);
		
		int cnn=nn;
		int ctr=0;
		while (cnn>=0){
			ctr++;
			cnn=mst.getNode(cnn).getParent();
		}
		int[] ancs=new int[ctr];
		cnn=nn;
		for(int i=0;i<ctr;i++) {
			ancs[ctr-1-i]=cnn;
			cnn=mst.getNode(cnn).getParent();
		}
		int[][] hue=new int[ctr][];
		int[][] x=new int[ctr][];
		int[][] y=new int[ctr][];
		
		for(int i=0;i<ctr;i++){
			graphics.drawLine(1, 30+i*40, width-1,30+i*40);
			graphics.drawLine(1, 30+i*40, 1,70+i*40);
			graphics.drawLine(1, 70+i*40, width-1,70+i*40);
			graphics.drawLine(width-1, 30+i*40, width-1,70+i*40);
			graphics.drawString("Node "+ancs[i], 5, 45+i*40);
			
			Node cn=mst.getNode(ancs[i]);
			if (cn.isLeaf()){
				graphics.drawString("LEAF ", width/2, 65+i*40);
			} else {
			int nhs=cn.oc().size();
			hue[i]=new int[nhs];
			x[i]=new int[nhs];
			y[i]=new int[nhs];
			
			for(int h=0;h<nhs;h++){
				hue[i][h]=cn.oc().getHue(h);
				x[i][h]=5+(width/(nhs+1))*(h+1);
				y[i][h]=65+i*40;
				graphics.drawString("hue "+hue[i][h],x[i][h] ,y[i][h] );
				
				if (i>0) {//not root: do traceback
					Node pnode=mst.getNode(ancs[i-1]);
					int phn=mst.getTraceback(hue[i][h],pnode.oc().getAllHueNumbers());
					int phix=-1;
					for(int j=0; j<hue[i-1].length;j++){
						if (hue[i-1][j]==phn) phix=j;
					}
					if (phix>-1){
						graphics.drawLine(x[i][h],y[i][h], x[i-1][phix],y[i-1][phix]);
					}
				}
			}
			
			
			}
		}
	}

}
