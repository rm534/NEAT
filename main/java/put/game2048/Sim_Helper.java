package put.game2048;

import org.apache.commons.math3.random.MersenneTwister;
import org.apache.commons.math3.random.RandomDataGenerator;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.Arrays;
import java.util.Random;


public class Sim_Helper {
    final Duration ACTION_TIME_LIMIT = Duration.ofMillis(1);
    int ParentNumber = 0;
    int PopulationSize = 0;
    Genome[] GeneDB;
    Network[] NetworkDB;
    NeuralAgent[] AgentDB;
    MultipleGamesResult[] Game_ResultsDB;
    double[] FitnessDB;
    int[] ParentList;
    int[] DeadList;
    Genome[] ChildDB;
    double[][] ScoreDB;

    //Init those mats with popsize
    public Sim_Helper(int PopulationSize, int ParentNumber){

        if(ParentNumber > PopulationSize){
            ParentNumber = PopulationSize - 1;
        }
        if(ParentNumber % 2 == 0){
            //If even do nothing

        }else{
            //If odd add 1
            ParentNumber++;
        }

        this.ParentNumber = ParentNumber;
        this.PopulationSize=  PopulationSize;
        this.GeneDB = new Genome[this.PopulationSize];
        this.NetworkDB = new Network[this.PopulationSize];
        this.AgentDB = new NeuralAgent[this.PopulationSize];
        this.Game_ResultsDB = new MultipleGamesResult[this.PopulationSize];
        this.FitnessDB = new double[this.PopulationSize];
        this.ParentList = new int[this.ParentNumber];
        this.DeadList = new int[this.ParentNumber/2];
        this.ChildDB = new Genome[this.ParentNumber/2];
        this.ScoreDB = new double[this.PopulationSize][2];
    }

    public Genome create_2048_genome(InnovationDB INN){
        Genome TG1 = new Genome(INN,2);
        //Running the 2048 Genome initialise method
        TG1.initialise_2048_gene_struct();
        //Performs a few initial mutations
        TG1.initial_mutation();
        return TG1;
    }

    //This function creates x random genomes and stores them within this object
    public Genome[] create_x_genomes(int Xg, InnovationDB INN){

        Genome[] GeneDB_tr = new Genome[Xg];
        for(int i=0; i<Xg; i++){
            GeneDB_tr[i] = create_2048_genome(INN);
        }

        GeneDB = GeneDB_tr;
        return GeneDB;
    }

    //Creates network
    public Network[] create_nets_from_gene_list(){

        Network[] NetworkDB_tr = new Network[GeneDB.length];
        for(int i=0; i<GeneDB.length; i++){
            Network Net1 = new Network();
            NetworkDB_tr[i] = Net1.convertGenomeToANN(GeneDB[i]);
        }

        NetworkDB = NetworkDB_tr;
        return NetworkDB;
    }

    //Simulate a singular network
    public void simulate_network(){




    }
    //simulates all networks
    public void simulate_all_networks(){



    }

    //Converts all networks to agents with the networks.
    public NeuralAgent[] create_agent_array(){

        for(int i=0; i<NetworkDB.length;i++){
            AgentDB[i] = new NeuralAgent(NetworkDB[i]);
        }
        return AgentDB;
    }

    //Simulates all agents in the agent database
    public MultipleGamesResult[] simulate_all_agents(int GameNumber){


        for(int i=0; i<AgentDB.length;i++){

            RandomDataGenerator random = new RandomDataGenerator(new MersenneTwister(123));
            Game_ResultsDB[i] = new Game(ACTION_TIME_LIMIT).playMultiple_agent(AgentDB[i], GameNumber, random);
        }


        return Game_ResultsDB;
    }

    //Also fills the scoresdb
    public void calculate_fitnesses(int GameNumber,double Time_W, double Score_W, double Move_W){
        fill_score_db();
        for(int i=0; i<Game_ResultsDB.length; i++){

            //Fitness is simply the mean score
            double Time_Fit = 1-AgentDB[i].average_ms;
            double Score_Fit = Game_ResultsDB[i].getScore().getMean();
            double Move_Fit = AgentDB[i].number_moves/GameNumber;
            double Total_Fit = (Time_W * Time_Fit) + (Score_W * Score_Fit) + (Move_W * Move_Fit);
            FitnessDB[i] = Total_Fit/GeneDB[i].NodeNum; // We are trying some penalise

        }

    }

    public void fill_score_db(){

        for(int i=0; i<Game_ResultsDB.length; i++){
            System.out.println("WORKING");
            //GetScores
            ScoreDB[i][0] = Game_ResultsDB[i].getScore().getMean();
            ScoreDB[i][1] = Game_ResultsDB[i].getScore().getMax();
        }
    }


    //tournament_size*parent_number < population_size
    public int[] get_parent_array(int Population_Size,int Parent_Number, int Tournament_Size){


        //Tournament Selection
        // initialisation of variables to be used in tournament selection
        int[] tournament_chosen=new int[Population_Size];


        Random randomNum = new Random();
        int t_choice;
        int[] parent_chosen=new int[Population_Size];
        int[] parent_list=new int[Parent_Number];

        double t_min;
        int p_max;

        // make sure array of chosen to breed/remove is clear
        for (int i = 0; i < Population_Size; i= i+1){
            parent_chosen[i]=0;
        }

        // tournament selection to chose best parents
        for (int h = 0; h < Parent_Number ; h = h+1) {
            for (int i = 0; i < Population_Size; i = i + 1) {
                tournament_chosen[i] = 0;
            }

            t_min=0;
            p_max=0;

            for (int j = 0; j < Tournament_Size; j = j + 1) {

                t_choice = randomNum.nextInt(Population_Size);
                //If its in the tournament or is a parent ignore it and reset
                if (tournament_chosen[t_choice]==1){//THIS WAS MODEED
                    j=j-1;
                }
                else{
                    //otherwise its in the tournament
                    tournament_chosen[t_choice]=1;

                    if (FitnessDB[t_choice]>t_min){
                        t_min= FitnessDB[t_choice];
                        p_max=t_choice;
                    }
                }
                if(j == Tournament_Size-1){
                    parent_chosen[p_max]=1;
                    parent_list[h]=p_max;
                }


            }
        }

        /*
        double[] FitnessDB_MOD = FitnessDB;
        System.out.println("DID WE GET HERE ?");
        //this will get Parent_Number fittest parents
        for(int i=0; i<Parent_Number; i++) {
            //get fittest
            ParentList[i] = get_most_fit(FitnessDB_MOD);
            //set that member fitness to 0
            FitnessDB_MOD[ParentList[i]] = 0;
        }*/
        //Always ensure that the best genome from the last generation is carried forward
        int best2 = this.get_fittest_genome();
        if(Arrays.asList(parent_list).contains(best2) != true){
            parent_list[0] = best2;
        }

        ParentList = parent_list;
        return ParentList;
    }

    //Returns the fittest member id
    public int get_most_fit(double[] FitnessDB){

        int current_fittest = 0;
        double current_best_fitness = 0;
        for(int i=0; i<FitnessDB.length; i++){
            if(FitnessDB[i] > current_best_fitness){
                current_best_fitness = FitnessDB[i];
                current_fittest = i;
            }
        }
        return current_fittest;
    }

    public int get_least_fit(double[] FitnessDB){

        int current_unfittest = 0;
        double current_worst_fitness = FitnessDB[0];
        for(int i=0; i<FitnessDB.length; i++){
            if(FitnessDB[i] < current_worst_fitness && FitnessDB[i] != -1){
                current_worst_fitness = FitnessDB[i];
                current_unfittest = i;
            }
        }
        return current_unfittest;
    }


    //Breeds parents from the parent array and returns a child array half the size
    public Genome[] breed_parent_array(double ConGeneMutRate,double ConGeneBigMutRate,double ConMutStrength,double AddNodeMutRate,double AddConMutRate){

        //This will bree 2n of the array together
        int cj = 0;
        for(int i=0; i<ParentList.length;i+=2){
            ChildDB[cj] = GeneDB[ParentList[i]].genomeCrossover_mutation(GeneDB[ParentList[i+1]],ConGeneMutRate,ConGeneBigMutRate,ConMutStrength,AddNodeMutRate,AddConMutRate);
            cj++;
        }
        //If we have a odd number of members we breed the last with a random
        if(ParentList.length % 2 == 0){
            //if even do nothing
        }else{//its odd m8
            ChildDB[cj] = GeneDB[ParentList[ParentList.length-1]].genomeCrossover_mutation(GeneDB[ParentList[(int) Math.round(Math.random()*(ParentList.length-2))]],ConGeneMutRate,ConGeneBigMutRate,ConMutStrength,AddNodeMutRate,AddConMutRate);
        }

        return ChildDB;

    }

    //Gets a list of member who should die
    public int[] get_dead_genome_array(int Population_Size,int Dead_Number, int Tournament_Size){

        //Tournament Selection
        // initialisation of variables to be used in tournament selection
        int[] tournament_chosen=new int[Population_Size];


        Random randomNum = new Random();
        int t_choice;
        int[] dead_chosen=new int[Population_Size];
        int[] dead_list=new int[Dead_Number];


        double t_min;
        int p_max;

        // make sure array of chosen to breed/remove is clear
        for (int i = 0; i < Population_Size; i= i+1){
            dead_chosen[i]=0;
        }

        // tournament selection to chose best parents
        for (int h = 0; h < Dead_Number ; h = h+1) {
            for (int i = 0; i < Population_Size; i = i + 1) {
                tournament_chosen[i] = 0;
            }

            t_min=0;
            p_max=0;

            for (int j = 0; j < Tournament_Size; j = j + 1) {

                t_choice = randomNum.nextInt(Population_Size);
                //If its in the tournament or is a parent ignore it and reset
                if (tournament_chosen[t_choice]==1 || dead_chosen[t_choice]==1){
                    j=j-1;
                }
                else{
                    //otherwise its in the tournament
                    tournament_chosen[t_choice]=1;

                    if (FitnessDB[t_choice]<t_min || t_min == 0){
                        t_min= FitnessDB[t_choice];
                        p_max=t_choice;
                    }
                }
                if(j == Tournament_Size-1){
                    dead_chosen[p_max]=1;
                    dead_list[h]=p_max;
                }


            }
        }





        /*
        double[] FitnessDB_MOD = FitnessDB;

        //this will get Parent_Number fittest parents
        for(int i=0; i<Dead_Number; i++) {
            //get least fit
            DeadList[i] = get_least_fit(FitnessDB_MOD);
            //set that member fitness to -1, get least fit ignores -1s
            FitnessDB_MOD[ParentList[i]] = -1;
        }
        */

        DeadList = dead_list;
        return DeadList;

    }

    public void replace_dead_genomes_with_children(){

        for(int i=0; i<DeadList.length; i++){
            GeneDB[DeadList[i]] = ChildDB[i];


        }


    }


    //Writes the structure of the best neural network to file
    public void write_best_NN_to_file(String filename){
        int fittestgen = get_fittest_genome();
        Genome BestGen = GeneDB[fittestgen];
        String outString = BestGen.getGenomeStructure();
        double fitness = get_highest_fitness();
        String TS = "Fitness: " + Double.toString(fitness) + "\r\n" + outString;
        write_value_to_file(filename,TS);


    }

    //private String path;

    //Used to write fitnesses to files so they can be later plotted
    public void write_value_to_file(String file_name, String text){
        try (Writer writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file_name), StandardCharsets.UTF_8))) {
            writer.write(text);
        }
        catch (IOException ex) {
            // Handle me
        }




    }

    //Returns the integer which is the ID of the fittest genome
    //Goes through fitness DB
    public int get_fittest_genome(){
        int fittest_gen=0;
        double cb_fitness = 0;
        for(int i=0; i<FitnessDB.length;i++){
            if(FitnessDB[i] > cb_fitness){
                fittest_gen = i;
                cb_fitness = FitnessDB[i];
            }
        }
        return fittest_gen;
    }

    public double get_highest_fitness(){
        int fittest_gen=0;
        double cb_fitness = 0;
        for(int i=0; i<FitnessDB.length;i++){
            if(FitnessDB[i] > cb_fitness){
                fittest_gen = i;
                cb_fitness = FitnessDB[i];
            }
        }
        return cb_fitness;
    }

    public double get_best_score(){
        int fittest_gen=0;
        double cb_fitness = 0;
        for(int i=0; i<ScoreDB.length;i++){
            if(ScoreDB[i][1] > cb_fitness){
                fittest_gen = i;
                cb_fitness = ScoreDB[i][1];
            }
        }
        return cb_fitness;

    }


    public double get_best_average_score(){
        int fittest_gen=0;
        double cb_fitness = 0;
        for(int i=0; i<ScoreDB.length;i++){
            if(ScoreDB[i][0] > cb_fitness){
                fittest_gen = i;
                cb_fitness = ScoreDB[i][0];
            }
        }
        return cb_fitness;

    }

    public double get_average_score(){
        double fitsum = 0;
        for(int i=0; i<ScoreDB.length;i++){
            fitsum+=ScoreDB[i][0];
        }
        return (fitsum/ScoreDB.length);
    }


    public double get_average_fitness(){
        double fitsum = 0;
        for(int i=0; i<FitnessDB.length;i++){
            fitsum+=FitnessDB[i];
        }
        return (fitsum/FitnessDB.length);
    }








}
