package put.game2048;

import java.time.Duration;
import java.util.Arrays;

import org.apache.commons.math3.random.MersenneTwister;
import org.apache.commons.math3.random.RandomDataGenerator;
import org.junit.Assert;
import org.junit.Test;
import put.ci.cevo.games.game2048.Game2048;

//import static com.sun.org.apache.xalan.internal.xsltc.compiler.util.Type.Int;

public class GameTest {
	final Duration ACTION_TIME_LIMIT = Duration.ofMillis(1);

	@Test
	public void regressionBlindReflexAgent() {
		RandomDataGenerator random = new RandomDataGenerator(new MersenneTwister(123));
		MultipleGamesResult result = new Game(ACTION_TIME_LIMIT).playMultiple(BlindReflexAgent::new, 10000, random);
		System.out.println(result.toCvsRow());
		Assert.assertEquals(2287.195, result.getScore().getMean(), 0.001);
	}

	@Test
	public void regressionRandomAgent() {
		RandomDataGenerator random = new RandomDataGenerator(new MersenneTwister(123));
		MultipleGamesResult result = new Game(ACTION_TIME_LIMIT).playMultiple(RandomAgent::new, 10000, random);
		System.out.println(result.toCvsRow());
		Assert.assertEquals(1238.0348, result.getScore().getMean(), 0.001);
	}
	/*@Test
	public void writetofile() {

		Sim_Helper Sim1 = new Sim_Helper(10,10);
		Sim1.write_value_to_file("bing.txt");

	}*/



	@Test
	public void NNtest(){
		//Simulation params
		String save_num = "16";
		//Number of games to test each net for
		int GameNumber = 1000;
		//PARENT NUMBER MUST BE EVEN
		int PopulationSize = 200;
		int SimulationCycles = 100;
		int ParentNumber = 50;
		int TournamentSizeParents = 50;
		int TournamentSizeDead = 50;

		double ConGeneMutRate = 0.1;
		double ConGeneBigMutRate = 0.05;
		double ConMutStrength = 30;//0.1
		//How many rolls at a 10% chance would you like ?
		double AddNodeMutRate = 1;//0.05 //without this it would be proportional to size of network
		double AddConMutRate = 1;//0.05

		double Time_W = 0.1;
		double Score_W = 0.89;
		double Moves_W = 0.01;
		String Variables = Integer.toString(GameNumber) + "," + Integer.toString(PopulationSize) + "," +Integer.toString(SimulationCycles) + "," +Integer.toString(ParentNumber) + "," +Integer.toString(TournamentSizeParents ) + "," +
				Integer.toString(TournamentSizeDead) + "," +Double.toString(ConGeneMutRate) + "," +Double.toString(ConGeneBigMutRate) + "," +Double.toString(ConMutStrength) + "," +Double.toString(AddNodeMutRate) + "," +
				Double.toString(AddConMutRate) + "," +Double.toString(Time_W) + "," +Double.toString(Score_W) + "," +Double.toString(Moves_W);
		//May want to include these from genome

		//double DDFlipProb = 0.15;
    	//double DEFlipProb = 0.40;
    	//double EEFlipProb = 0.99;

		double[] BestFitMat = new double[SimulationCycles];
		double[] BestScoreMat = new double[SimulationCycles];
		double[] TotalAverageFitness = new double[SimulationCycles];
		double[] BestAverageScore = new double[SimulationCycles];
		double[] TotalAverageScore = new double[SimulationCycles];



		System.out.println("Hi W0rld");

		//Create the innovation database
		InnovationDB INN = new InnovationDB();

		Sim_Helper Sim1 = new Sim_Helper(PopulationSize,ParentNumber);
		Genome[] Genos = Sim1.create_x_genomes(PopulationSize,INN);
		Sim1.create_nets_from_gene_list();
		Sim1.create_agent_array();
		MultipleGamesResult[] results = Sim1.simulate_all_agents(GameNumber);
		//WARNING, THE NUMBER OF PARENTS & DEAD MUST BE KEPT CONSTANT OR THE DEFINED MATRICES IN THE SIM WILL BE TOO SMALL
		//Sim1.assign_results(results);

		Sim1.calculate_fitnesses(GameNumber,Time_W,Score_W,Moves_W);
		//tournament_size*parent_number < population_size
		Sim1.get_parent_array(PopulationSize,ParentNumber,TournamentSizeParents);
		Sim1.breed_parent_array(ConGeneMutRate,ConGeneBigMutRate,ConMutStrength,AddNodeMutRate,AddConMutRate);
		Sim1.get_dead_genome_array(PopulationSize,ParentNumber/2,TournamentSizeDead);
		Sim1.replace_dead_genomes_with_children();
		//We then want to go back to the top of the function and go again
		for(int i=0;i<results.length;i++){
			System.out.println("Genome Results: "+"SimNum: "+Integer.toString(1)+"Genome: " + Integer.toString(i));
			System.out.println(results[i].toCvsRow());
		}
		//After each run through we want to build the arrays

		BestFitMat[0] = Sim1.get_highest_fitness();
		BestScoreMat[0] = Sim1.get_best_score();
		TotalAverageFitness[0] = Sim1.get_average_fitness();
		BestAverageScore[0] = Sim1.get_best_average_score();
		TotalAverageScore[0] = Sim1.get_average_score();


		for(int i=0; i<SimulationCycles-1; i++) {
			Sim1.create_nets_from_gene_list();
			Sim1.create_agent_array();
			results = Sim1.simulate_all_agents(GameNumber);
			//WARNING, THE NUMBER OF PARENTS & DEAD MUST BE KEPT CONSTANT OR THE DEFINED MATRICES IN THE SIM WILL BE TOO SMALL
			//Sim1.assign_results(results);
			Sim1.calculate_fitnesses(GameNumber,Time_W,Score_W,Moves_W);
			//tournament_size*parent_number < population_size
			Sim1.get_parent_array(PopulationSize,ParentNumber,TournamentSizeParents);
			Sim1.breed_parent_array(ConGeneMutRate,ConGeneBigMutRate,ConMutStrength,AddNodeMutRate,AddConMutRate);
			Sim1.get_dead_genome_array(PopulationSize,ParentNumber/2,TournamentSizeDead);

			//If last run the first save the best network
			if(i == SimulationCycles-2){

				Sim1.write_best_NN_to_file("BestNN_Struct_"+save_num+".txt");
			}


			Sim1.replace_dead_genomes_with_children();

			BestFitMat[i+1] = Sim1.get_highest_fitness();
			BestScoreMat[i+1] = Sim1.get_best_score();
			TotalAverageFitness[i+1] = Sim1.get_average_fitness();
			BestAverageScore[i+1] = Sim1.get_best_average_score();
			TotalAverageScore[i+1] = Sim1.get_average_score();


		}

		for(int i=0;i<results.length;i++){
			System.out.println("Genome Results: "+"SimNum: "+Integer.toString(SimulationCycles)+"Genome: " + Integer.toString(i));
			System.out.println(results[i].toCvsRow());
		}


		//Write your results to  a file !

		String DataString = Arrays.toString(BestFitMat)+"\r\n"+Arrays.toString(BestScoreMat)+"\r\n"+Arrays.toString(TotalAverageFitness)+"\r\n"+Arrays.toString(BestAverageScore)+"\r\n"+Arrays.toString(TotalAverageScore)+"\r\n";
		System.out.println(DataString);
		String total_Str =  "Variables: " + Variables + "\r\n" + DataString;
		Sim1.write_value_to_file("data_"+save_num+".txt",total_Str);
		//Write the best network structure out
		//Sim1.write_best_NN_to_file();

	}


}