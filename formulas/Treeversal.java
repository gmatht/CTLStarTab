package formulas;

import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.*;

public class Treeversal extends Canvas implements MouseListener {
	
	private int width=1600;
	private int depth=500;
	private StarTab mst;
	private Pair[] locations;

	/**
	 * @param args
	 */
	

	    public void paint(Graphics graphics)
	    {
	       	int numnodes=mst.getNumberOfNodes();
	    	locations=new Pair[numnodes];
	    	//graphics.drawLine(width/2,1, width/2, depth-1);
	    	//drawSubtree(width/2,1,1,width/2-1,25,depth-1,graphics);
	    	//drawSubtree(width/2,1,width/2+1,width-1,25,depth-1,graphics);
	    	draw(0,1,width-1,30,depth-1,graphics);
	    	graphics.drawString(" "+mst.getPhi().abbrev(),2,25);
	    }
	    /*
	    
	    public static void main(String[] args)
	    {
	        Treeversal canvas = new Treeversal();                      // We initialize our class here
	        JFrame frame = new JFrame();
	        frame.setSize(canvas.width, canvas.depth);
	        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	        frame.getContentPane().add(canvas);                    // Here we add it to the frame
	        frame.setVisible(true);
	    }
	    */
	    
	    private void drawSubtree(int px, int py, int lb, int rb, int sd, int ed, Graphics g){
	    	if (lb>=rb-1) return;
	    	if (sd>=ed-1) return;
	    	g.drawLine(px,py,(lb+rb)/2,sd);
	    	int nsd=sd+25;
	    	if (nsd>(sd+ed)/2) nsd=(sd+ed)/2;
	    	drawSubtree((lb+rb)/2,sd,lb,(lb+rb)/2,nsd,ed,g);
	    	drawSubtree((lb+rb)/2,sd,(lb+rb)/2,rb,nsd,ed,g);
	    }
	    
	    private void draw(int n,int lb,int rb,int sd,int ed,Graphics g){
	    	if (lb>=rb-1) return;
	    	if (sd>=ed-1) return;
	    	if (mst==null) return;
	    	Node tn=mst.getNode(n);
	    	g.drawString(" "+n,(lb+rb)/2,sd);
	    	locations[n]=new Pair((lb+rb)/2,sd);
	    	int k=tn.numsucc();
	    	if (k==0) return;
	    	int nlb=lb;
	    	int sw=(rb-lb)/k;
	    	if (sw<1) return;
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
	    
	    public Treeversal(StarTab mst){
	    	this.mst=mst;
	        JFrame frame = new JFrame();
	        frame.setSize(width, depth);
	        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	        //frame.getContentPane().add(this); 
	        frame.getContentPane().add(new JScrollPane(this));
	        frame.setSize(400,400);
	        frame.setTitle("This is the main frame: do not close!");
	        frame.setVisible(true);
	        this.addMouseListener(this);
	    }
	    
	    public Treeversal(){
	    	
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
			for(int j=0; j<locations.length;j++){
				if (locations[j]==null){
					
				} else {
					double d=(mx-locations[j].x())*(mx-locations[j].x())+(my-locations[j].y())*(my-locations[j].y());
					if (d<dist) {
						closestNode=j;
						dist=d;
					}
				}
			}
			new NodeDialog(mst,closestNode).setVisible(true);

			
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
