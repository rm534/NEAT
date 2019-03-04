package put.game2048;

import java.util.Random;
import java.util.Arrays;

import java.util.Random;
import java.util.Arrays;

//INPUTS MUST BE ADDED FIRST OR PROBLEMS WILL OCCUR

// Class Declaration
public class Genome {
    double Fitness;
    int MaximumGenomeLength = 50000;

    //Probability for a gene to become reenabled during crossover;
    double DDFlipProb = 0.15;
    double DEFlipProb = 0.40;
    double EEFlipProb = 0.99;

    //Probability that mutation happens
    double GeMutProb = 0.8;
    //Probability that total rnaomd mutation happens
    double TotalRandMutProb = 0.1;

    //Define Empty Connection Gene Array
    ConnectionGene[] CG = new ConnectionGene[MaximumGenomeLength];
    //Define Empty Node Gene Array
    NodeGene[] NG = new NodeGene[MaximumGenomeLength];

    int NodeNum = 0;
    int ConnectionNum = 0;
    int InputNodeNum = 0;
    int OutputNodeNum = 0;
    int HiddenNodeNum = 0;
    InnovationDB INNDB;



    //Constructor Declaration of Class
    //In the future we may want arguments like mutation rate, etc
    public Genome(InnovationDB InnovDB, double Fitness){
        this.INNDB = InnovDB;
        this.Fitness = Fitness;
    }

    //Methods

    //M1 Add Node Gene
    public String addNodeGene(NodeGene.NodeTypes NodeTypeI){

        NG[NodeNum] = new NodeGene(NodeNum, NodeTypeI);
        NodeNum ++;
        if(NodeTypeI == NodeGene.NodeTypes.INPUT){
            InputNodeNum++;
        }else if(NodeTypeI == NodeGene.NodeTypes.OUTPUT){
            OutputNodeNum++;
        }else{
            HiddenNodeNum++;
        }


        return "Node Added " + Integer.toString(NodeNum-1);
    }


    //M2 Add Connection Gene
    //Add Con Gene needs to add nodes if they do not exist

    public String addConGene(int InNode, int OutNode, double Weight, boolean Enabled){

        //Process the innovation and return either a new or existing innovation number
        int InnNum = INNDB.processInnovation(InNode,OutNode);

        CG[ConnectionNum] = new ConnectionGene(InNode,OutNode,Weight,Enabled,InnNum);
        ConnectionNum++;

        //Check Global Innovation List

        //Assign Innovation Number


        return "Connection Added IN: " + Integer.toString(InnNum) + " : " + Integer.toString(InNode) + " , " + Integer.toString(OutNode);

    }


    //Method Mutate Add Node
    public String mutateAddNode() {
        System.out.println("work work work work work");
        //Choose a random connection
        //Disable that connection
        //Add a new node
        //We need some kind of layering system
        //We will need to rename layers of all neurons further forward

        //We will code it so you can have both recurrent and feedfoward networks


        //Create List of all non disabled connections
        int[] NDARR = new int[MaximumGenomeLength];
        int NDnum = 0;
        for (int i = 0; i < ConnectionNum; i++) {
            if (CG[i].Enabled == true) {
                NDARR[NDnum] = i;
                NDnum++;
            }
        }

        //Pick a random entry from the list
        int rando = (int) Math.round(Math.random() * NDnum);

        CG[NDARR[rando]].Enabled = false;
        //We need to do layering eventually
        addNodeGene(NodeGene.NodeTypes.HIDDEN);
        addConGene(CG[NDARR[rando]].InputNode,NodeNum-1,1.0,true);
        addConGene(NodeNum-1,CG[NDARR[rando]].OutputNode,CG[NDARR[rando]].Weight,true);

        return "Mutate Add Node: " + Integer.toString(NodeNum-1) + " Connections added: " + Integer.toString(CG[NDARR[rando]].InputNode) + " , " + Integer.toString(NodeNum-1) + " " + Integer.toString(NodeNum-1) + " , " + Integer.toString(CG[NDARR[rando]].OutputNode);

    }



    //Method Mutate Add Connection
    //Finds two unconnected nodes
    //Makes a connection between them

    //We must make it so there is an argument passed that either prevents or allows recurrence or feedfoward nature.
    public String mutateAddConnection(){
        //Select a random node
        //Select another random node and check if they are connnected
        //If connected then remove then random node from the list and choose another random node form the list
        //If unconnected connect them
        //If list is exhausted then remove initial random node from og list select another and then random through all nodes in list to check for connection
        //If initial node list is exhausted all connections have been made and method should return FALSE or somthing.

        //Make Node Order Array

        int N_1,N_2;
        N_1 = -1;
        N_2 = -1;
        int [] NNAR1 = new int [InputNodeNum+HiddenNodeNum];
        for(int i=0; i<InputNodeNum; i++){
            NNAR1[i] = i;
        }
        for(int i=0; i<HiddenNodeNum; i++){
            NNAR1[InputNodeNum+i] = InputNodeNum+OutputNodeNum+i;
        }

        System.out.println(Arrays.toString(NNAR1));
        shuffleArray(NNAR1);


        getnewcon:
        for(int i=0; i<NNAR1.length; i++){
            //Create and shuffle new array
            int [] NNAR2 = new int [OutputNodeNum+HiddenNodeNum];
            for(int u=0; u<NodeNum-InputNodeNum; u++){
                NNAR2[u] = u+InputNodeNum;
            }
            System.out.println("NAR2"  + Arrays.toString(NNAR2));
            shuffleArray(NNAR2);

            //Look for connections halt when one is found
            for(int j=0; j<NNAR2.length; j++){
                //If a connection does not exist, and connection is not made to self
                if((doesConnectionExist(NNAR1[i],NNAR2[j]) == false) && (NNAR1[i] != NNAR2[j])){
                    N_1 = NNAR1[i];
                    N_2 = NNAR2[j];


                    //Label break to escape
                    break getnewcon;
                }else{

                    N_1 = -1;
                    N_2 = -1;
                }
            }
        }

        //If a valid connection can be made
        if((N_1 != -1) && (N_2 != -1)){

            //Add connection in feed forward
            /*if(N_1 > N_2){
                //Weight is currently 1
                return " Mutate " + addConGene(N_2,N_1,1.0,true);

            }else {
                //Weight is currently 1
                return "Mutate " + addConGene(N_1, N_2,1.0,true);
            }*/

            return "Mutate " + addConGene(N_1, N_2,1.0,true);

        }else{
            return "Failed to Mutate Add Connection, No valid connections available";
        }
    }



    //This method checks if a connection exists,
    //IT NEEDS TO CHECK FROM LOWER NUMBER TO HIGHER AS WE ONLY ALLOW FORWARD CONNECTIONS (DEAL WITH THIS BETTER IN FUTURE)
    public boolean doesConnectionExist(int InputNode, int OutputNode){
        //int LowNode,HighNode;


        //OLD CODE TO BE WORKED ON IN FUTURE
        //Arrange in feed forward
        /*if(InputNode < OutputNode){
            LowNode = InputNode;
            HighNode = OutputNode;
        }else{
            LowNode = OutputNode;
            HighNode = InputNode;
        }*/

        for(int i=0; i<ConnectionNum; i++){
            if(CG[i].InputNode == InputNode && CG[i].OutputNode == OutputNode) {
                //If a connection is found
                return true;
            }
        }
        //If no connection is found
        return false;
    }


    //A method to test the innovation database
    public void testDB(){
        INNDB.processInnovation(1,2);
        INNDB.processInnovation(2,3);


    }

    //A method to shuffle an array
    private static void shuffleArray(int[] array)
    {
        int index, temp;
        Random random = new Random();
        for (int i = array.length - 1; i > 0; i--)
        {
            index = random.nextInt(i + 1);
            temp = array[index];
            array[index] = array[i];
            array[i] = temp;
        }
    }


    //Mutate Genome Method
    //Choose one of a few mutations to make



    //Method Crossover
    //This accepts an argument which is a pointer to another genome and will crossover the two genomes and return a new genome as an output

    public Genome genomeCrossover(Genome Parent2){
        //Go to first gene on fittest parent
        //Check if same innovation is on unfit parent
        //If yes then randomly choose one of the genes to inherit
        //If no then add the excess or disjoint gene from fittest parent to child and repeat for next gene on fittest parent.

        Genome FittestParent,UnfitParent,ChildGenome;
        //Random fitness is given here
        ChildGenome = new Genome(INNDB,2);

        if(Parent2.Fitness > Fitness){
            FittestParent = Parent2;
            UnfitParent = this;
        }else{
            FittestParent = this;
            UnfitParent = Parent2;
        }

        for(int i=0; i<FittestParent.ConnectionNum; i++){
            ConnectionGene Fge = FittestParent.CG[i];
            int CurGeneInnovNum = Fge.InnovationNumber;
            boolean MatchFound = false;
            for(int j=0; j<UnfitParent.ConnectionNum; j++){
                //If they have a matching gene
                if(CurGeneInnovNum == UnfitParent.CG[j].InnovationNumber){
                    ConnectionGene ge = UnfitParent.CG[j];


                    //Some probability of it being enabled is needed

                    //Choose a gene randomly from the parents

                    //Make a decision on if the gene should be enabled
                    double rand2 = Math.random();
                    boolean newEN = false;
                    if(ge.Enabled == true){
                        //EE
                        if(Fge.Enabled == true){
                            if(rand2 < EEFlipProb){
                                newEN = true;
                            }
                        }else{//ED
                            if(rand2 < DEFlipProb){
                                newEN = true;
                            }
                        }
                    }else{
                        //DE
                        if(Fge.Enabled == true){
                            if(rand2 < DEFlipProb){
                                newEN = true;
                            }
                        }else{//DD
                            if(rand2 < DDFlipProb){
                                newEN = true;
                            }
                        }
                    }

                    int choice = (int) Math.round(Math.random());
                    if(choice == 0){
                        ChildGenome.addConGene(ge.InputNode,ge.OutputNode,ge.Weight,newEN);
                    }else{
                        ChildGenome.addConGene(Fge.InputNode,Fge.OutputNode,Fge.Weight,newEN);
                    }


                    MatchFound = true;
                    break;
                }
            }
            //If they don't have the gene
            if(MatchFound == false){

                ChildGenome.addConGene(Fge.InputNode,Fge.OutputNode,Fge.Weight,Fge.Enabled);
                //System.out.println("Adding Gene");
                //ChildGenome.addConGene(1,3,2,true);
            }



        }

        //Setup variables for childGenome
        ChildGenome.NG = FittestParent.NG;
        ChildGenome.NodeNum = FittestParent.NodeNum;
        ChildGenome.InputNodeNum = FittestParent.InputNodeNum;
        ChildGenome.OutputNodeNum = FittestParent.OutputNodeNum;
        ChildGenome.HiddenNodeNum = FittestParent.HiddenNodeNum;








        return ChildGenome;
    }

    //Crossover with mutation
    //SET CONMUTSTRENGTH TO 0.1 to get original functionality
    public Genome genomeCrossover_mutation(Genome Parent2, double ConGeneMutRate,double ConGeneBigMutRate,double ConMutStrength,double AddNodeMutRate,double AddConMutRate){
        Genome ChildGenome = this.genomeCrossover(Parent2);

        //Probability that a connection gene mutates
        //Probability that the mutation (con gene) makes it go completely random
        //Probability that affects how many add node mutations occur (% of total nodes)(We check this prob Node num times)
        //Probability that affects how many connections are added (% of total number of connections)(We check this prob connection num times)
        //Strength of weight mutations

        //Rollinf for addition of nodes
        for(int i=0; i<AddNodeMutRate; i++){
            double roll1 = Math.random();
            if(roll1 < 0.1){
                ChildGenome.mutateAddNode();
            }
        }
        //Rolling for addition of connections
        for(int i=0; i<AddConMutRate; i++){
            double roll2 = Math.random();
            if(roll2 < 0.1){
                ChildGenome.mutateAddConnection();
            }
        }
        //Rolling for weight mutations
        ChildGenome.mutateWeightsGenome(ConGeneMutRate,ConGeneBigMutRate,ConMutStrength);



        return ChildGenome;
    }






    //Method to see structure
    public String getGenomeStructure(){
        String StructString;
        StructString = "Struct : ";
        String CW;
        for(int i=0; i<ConnectionNum; i++){
            //Limit weight string lengths
            if(Double.toString(CG[i].Weight).length() > 5){
                CW = (Double.toString(CG[i].Weight)).substring(0,5);
            }else{
                CW = Double.toString(CG[i].Weight);
            }
            StructString += " " + Boolean.toString(CG[i].Enabled) + ":" + Integer.toString(CG[i].InputNode) + "-->" + Integer.toString(CG[i].OutputNode) + ":" + CW + " ";
        }

        return StructString;
    }


    //Method To mutate some weights of the genome
    public int mutateWeightsGenome(double GenMutProb,double GeneTotalRandMutProb,double ConMutStrength){
        int mutenum = 0;
        //Go through list of genes
        //Run rollMutate for each one

        for(int i=0; i<ConnectionNum; i++){
            if(CG[i].rollWeightMutate(GenMutProb,GeneTotalRandMutProb,ConMutStrength) == true){
                mutenum++;
            }
        }

        return mutenum;
    }


    //Method Full mutation of genome taking all the parameters that we want
    //The point is that we run this function once for each genome when we do breeding
    public String performFullMutation(){

        return "Did X mutations";
    }

    //This function runs code to add 16 inputs and 4 outputs as well as to connect them
    public void initialise_2048_gene_struct(){

        //Add 16 inputs
        for(int i = 0;i<16;i++){
            System.out.println(this.addNodeGene(NodeGene.NodeTypes.INPUT));
        }
        //Add 4 outputs
        for(int i = 0;i<4;i++){
            System.out.println(this.addNodeGene(NodeGene.NodeTypes.OUTPUT));
        }

        //Fully Connect Inputs and Outputs
        for(int i = 0;i<this.NodeNum-4;i++){
            //The four output nodes connected to each input with a random weight
            System.out.println(this.addConGene(i,16,Math.random(),true));
            System.out.println(this.addConGene(i,17,Math.random(),true));
            System.out.println(this.addConGene(i,18,Math.random(),true));
            System.out.println(this.addConGene(i,19,Math.random(),true));
        }
    }

    //Performs some initial mutations
    //Currently this simply does 2 mutate add node mutations
    public void initial_mutation(){
        System.out.println(this.mutateAddNode());
        System.out.println(this.mutateAddNode());
    }





}