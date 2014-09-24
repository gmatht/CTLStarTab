package formulas;

import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.*;

public class Treeversal2 extends Canvas implements MouseListener {
	
	private int width=1600;
	private int depth=900;
	private StarTab mst;
	private Pair[] locations;
	private int topNodeDisplayed=0;


	    public void paint(Graphics graphics)
	    {
	       	int numnodes=mst.getNumberOfNodes();
	    	locations=new Pair[numnodes];
	    	//graphics.drawLine(width/2,1, width/2, depth-1);
	    	//drawSubtree(width/2,1,1,width/2-1,25,depth-1,graphics);
	    	//drawSubtree(width/2,1,width/2+1,width-1,25,depth-1,graphics);
	    	draw(topNodeDisplayed,1,width-1,30,depth-1,graphics);
	    	graphics.drawString(" "+mst.getPhi().abbrev(),2,25);
	    	graphics.drawString(" "+numnodes+" nodes",(int)Math.floor(width*0.75),25);
	    }

	    
	    private void draw(int n,int lb,int rb,int sd,int ed,Graphics g){
	    	//if (lb>=rb-1) return;
	    	if (sd>=ed-1) return;
	    	if (mst==null) return;
	    	Node tn=mst.getNode(n);
	    	g.drawString(" "+n,(lb+rb)/2,sd);
	    	locations[n]=new Pair((lb+rb)/2,sd);
	    	int k=tn.numsucc();
	    	if (k==0) return;
	    	int nlb=lb;
	    	int sw=(rb-lb)/k;
	    	//if (sw<1) return;
	    	int nsd=sd+25;
	    	if (nsd>(sd+ed)/2) nsd=(sd+ed)/2;
	    	for(int j=0; j<k ; j++){
	    		int cnn=tn.succ(j);
	    		if (n>=cnn){ //uplink
	    			g.setColor(Color.RED);
	    			g.drawLine((lb+rb)/2, sd, ((nlb+sw/2)+((lb+rb)/2))/2, (nsd+sd)/2); //short line for uplink
	    			g.drawLine(((nlb+sw/2)+((lb+rb)/2))/2, (nsd+sd)/2, locations[cnn].x()-25, locations[cnn].y()-25);
	    			g.drawLine(locations[cnn].x()-25, locations[cnn].y()-25, locations[cnn].x(), locations[cnn].y());
	    		} else { //not uplink
	    			g.setColor(Color.BLACK);
	    			g.drawLine((lb+rb)/2, sd, nlb+sw/2, nsd); 
	    			draw(cnn,nlb,nlb+sw,nsd,ed,g);
	    		}
	    		nlb=nlb+sw;
	    	}
	    }
	    
	    public Treeversal2(StarTab mst){
	    	this.mst=mst;
	        JFrame frame = new JFrame();
	        frame.setSize(width, depth);
                try {
                    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                    //If we cannot set EXIT_ON_CLOSE, it matters less if they close this frame.
                    //Also we don't want to freak out applet users by popping
                    //up a frame and demanding they don't close it
	            frame.setTitle("This is the main frame: do not close!");                    
                } catch (Exception e) {
                    // We are probably running in an applet.
                }
	        //frame.getContentPane().add(this); 
	        frame.getContentPane().add(new JScrollPane(this));
	        frame.setSize(400,400);
	        frame.setVisible(true);
	        this.addMouseListener(this);
	    }
	    
	    public Treeversal2(){
	    	
	    }
	    
	    public void update(){
	    	repaint();
	    }

		public void mouseClicked(MouseEvent e) {
			// TODO Auto-generated method stub
			int mx=e.getX();
			int my=e.getY();
			
			int closestNode=-1;
			double dist=1000*1000;
			boolean veryClose=false;
			for(int j=0; j<locations.length;j++){
				if (locations[j]==null){
					
				} else {
					double d=(mx-locations[j].x())*(mx-locations[j].x())+(my-locations[j].y())*(my-locations[j].y());
					if (Math.abs(dist-d)<6) veryClose=true;
					if (d<dist) {
						closestNode=j;
						dist=d;
					}
				}
			}
			if ((closestNode==topNodeDisplayed) && (closestNode !=0)) veryClose=true; //to allow up movements
			if (!veryClose)
				new NodeDialog(mst,closestNode).setVisible(true);
			else {
				if (closestNode==0) topNodeDisplayed=0;
				else topNodeDisplayed=mst.getNode(closestNode).getParent();
				repaint();
			}

			
		}

		public void mouseEntered(MouseEvent e) {
			// TODO Auto-generated method stub
			
		}

		public void mouseExited(MouseEvent e) {
			// TODO Auto-generated method stub
			
		}

		public void mousePressed(MouseEvent e) {
			// TODO Auto-generated method stub
			
		}

		public void mouseReleased(MouseEvent e) {
			// TODO Auto-generated method stub
			
		}
	    
	    
	}
