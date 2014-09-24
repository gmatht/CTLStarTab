/*
 * TreeNode.java
 *
 * Created on 15 May 2005, 12:50
 */

package formulas;

/**
 *
 * @author  mark
 */
public class TreeNode {
    
    private TreeNode[] child;
    private Symbol content;
    private boolean reality;
    
    /** Creates a new instance of TreeNode */
    public TreeNode(int arity, Symbol c, boolean r) {
        child=new TreeNode[arity];
        content=c;
        reality=r;
    }
    
   public TreeNode(){
       child=new TreeNode[0];
       content=new Symbol('*');
       reality=false;
   }
    
   public boolean isReal(){
       return reality;
   }
   
   public TreeNode getChildNode(int num){
       return child[num];   
   }
   
   public Symbol getContent(){
       return content;
   }
   
   public boolean isBinary(){
       return (child.length==2);
   }
   
   public boolean isUnary(){
       return (child.length==1);
   }
   
   public boolean isZeroary(){
       return (child.length==0);
   }
   
   public void setChild(int num,TreeNode a){
       child[num]=a;
   }
   
   public String recToString(){
       if (isZeroary()) return content.toString();
       if (isUnary())
           return content.toString()+child[0].recToString();
        if (isBinary())
            return content.toString()+"("+child[0].recToString()+","+child[1].recToString()+")";
        return "";
   }
   
}
