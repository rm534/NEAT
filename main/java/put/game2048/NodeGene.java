package put.game2048;

public class NodeGene {

    //Variables for Object
    int NodeID;

    enum NodeTypes{
        INPUT,OUTPUT,HIDDEN;
    }

    NodeTypes NodeType;

    public NodeGene(int NodeID, NodeTypes NodeType){
        this.NodeID = NodeID;
        this.NodeType = NodeType;
    }

    //Method (for Testing)
    public int getID(){
        return NodeID;
    }
}
