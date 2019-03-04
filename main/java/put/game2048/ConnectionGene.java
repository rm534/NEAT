package put.game2048;

// Class Declaration
public class ConnectionGene {
    //Instance Variables
    int InputNode;
    int OutputNode;
    double Weight;
    boolean Enabled;
    int InnovationNumber;
    double WeightDeltaFactor = 0.1;

    //Constructor Declaration of Class

    public ConnectionGene(int InputNode, int OutputNode, double Weight, boolean Enabled, int InnovationNumber){
        this.InputNode = InputNode;
        this.OutputNode = OutputNode;
        this.Weight = Weight;
        this.Enabled = Enabled;
        this.InnovationNumber = InnovationNumber;
    }

    //Method (For testing)
    public double getWeight(){

        return Weight;
    }

    //Method rollWeightMutate, using a defined proability rolls to check if its weight should mutate
    //We may want to in future modif the full random using MutStreng
    public boolean rollWeightMutate(double MutateProbability, double TotalRandomProbability, double MutStreng){
        //Total Random Proability refers to an event occuring in which a mutation happens,
        //and the mutation randomly sets the weight from 0-->1 rather than adding or minusing a small random number.

        double rando1 = Math.random();
        //If we roll to mutate
        if(rando1 < MutateProbability){
            double rando2 = Math.random();
            //If we roll total random
            if(rando2 < TotalRandomProbability){
                this.Weight = Math.random();

            }else{//If we roll small change
                double rando3 = Math.random();
                //This constant can be tuned
                if(rando3 < 0.5){
                    this.Weight-= Math.random()*MutStreng;
                }else{
                    this.Weight+= Math.random()*MutStreng;
                }


            }

            return true;
        }
        return false;
    }
}
