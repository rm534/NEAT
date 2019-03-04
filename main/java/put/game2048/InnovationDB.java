package put.game2048;

//Defines everything required by the innovation database
public class InnovationDB {


    int InovDatSize = 50000; //This is really bad practice, consider using array lists
    //Contains, innovation number : input node : output node
    int InnovationMatrix[][] = new int[InovDatSize][3];
    int CurrentInnovation = 0;



    public InnovationDB(){
        //this.InnovationMatrix = new int[InovDatSize][3];

    }


    //Methods

    //Check if innovation exists, return inno num if found, -1 if not found
    public int checkInnovationExists(int InputNode,int OutputNode){

        for(int i=0;i<CurrentInnovation+1;i++){
            //If innovation already exists
            if( (InputNode == InnovationMatrix[i][1]) && (OutputNode == InnovationMatrix[i][2])){

                //Return the innovation number
                return i;
            }
        }

        //If innovation wasnt found then return -1
        return -1;
    }


    //Add new innovation to database
    public int addInnovation(int InputNode,int OutputNode){
        //Assign data to db
        InnovationMatrix[CurrentInnovation][0] = CurrentInnovation;
        InnovationMatrix[CurrentInnovation][1] = InputNode;
        InnovationMatrix[CurrentInnovation][2] = OutputNode;
        //Increment Inno num
        CurrentInnovation++;

        return (CurrentInnovation-1);
    }


    //Check if innovation exists, if not add new innovation and return invo number, if it does exist then reutrn the innov number of the innovation
    public int processInnovation(int InputNode,int OutputNode){
        int InovNum = checkInnovationExists(InputNode,OutputNode);

        //If Innovation already exists
        if(InovNum != -1){
            //return the innovation number
            return InovNum;
        }else {//If Innovation is new, add it
            return addInnovation(InputNode, OutputNode);
        }
    }


    //Method
    //returns string format info for a given innov number
    public String getInnovationData(int InnovationNumber){

        String Str1 = Integer.toString(InnovationMatrix[InnovationNumber][1]) + " , " + Integer.toString(InnovationMatrix[InnovationNumber][2]);
        //System.out.println(Str1);

        return Str1;
    }




}
