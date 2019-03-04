package put.game2048;

import com.google.common.base.Preconditions;
import com.google.common.base.Stopwatch;
import org.apache.commons.math3.random.MersenneTwister;
import org.apache.commons.math3.random.RandomDataGenerator;
import put.ci.cevo.util.RandomUtils;

import java.time.Duration;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;


public class NeuralAgent implements Agent{

    public double average_ms = 0;
    public int number_moves = 0;
    public Network net;

    public RandomDataGenerator random = new RandomDataGenerator(new MersenneTwister(123));

    //Initialise the Agent, build the net
    public NeuralAgent(Network in_net){
        this.net = in_net;
    }


    //Chooses an output based on the probability of that output
    public int choose_output(double[] output_mat){

        //We then use the output to make a decision
        double random2 = Math.random()*Arrays.stream(output_mat).sum();
        //Sum through matrix until we reach this value
        double sum1 = 0;
        int dir_option = 0;
        for(int i=0; i<output_mat.length;i++){
            sum1 += output_mat[i];
            if(sum1 >= random2){
                dir_option = i;
                break;
            }
        }


        return dir_option;
    }


    public void update_times_stopwatch(Stopwatch stopwatch1){
        stopwatch1.reset();
        number_moves++;
        average_ms = (number_moves-1)*average_ms + stopwatch1.elapsed(TimeUnit.MILLISECONDS);
    }


    public Action chooseAction(Board board, List<Action> possibleActions, Duration maxTime) {
        Stopwatch stopwatch1 = new Stopwatch();
        stopwatch1.start();
        int MinimiserLarge = 65536;
        //Setting up new code for chooseAction
        //New Network
        //Network net = new Network();
        //reading board state into an input array
        double[] input = new double[16];
        int inputCount = 0;
        for (int i = 0; i < 4; i++){
            for(int j = 0; j < 4; j++){
                double value = board.TILE_VALUES[board.getValue(i, j)];
                if(value == 0){
                    input[inputCount] = value;
                }
                else {
                    //Using 1/board value instead of actual value
                    input[inputCount] = value/MinimiserLarge;
                }
                //System.out.println(board.getValue(i,j));
                inputCount += 1;
            }
        }
        //Calculating output
        //System.out.println(Arrays.toString(input));
        double[] output= net.calculate(input);
        int dir_int = choose_output(output);
        Action dir_choice = Action.values()[dir_int];
        //Try action 1
        if(possibleActions.contains(dir_choice)){
            update_times_stopwatch(stopwatch1);
            return (Action) dir_choice;

        }else{
            output[dir_int] = 0;
            dir_int = choose_output(output);
            dir_choice = Action.values()[dir_int];
            //Try action 2
            if(possibleActions.contains(dir_choice)){
                update_times_stopwatch(stopwatch1);
                return (Action) dir_choice;
            }else{
                output[dir_int] = 0;
                dir_int = choose_output(output);
                dir_choice = Action.values()[dir_int];
                //Try action 3
                if(possibleActions.contains(dir_choice)){
                    update_times_stopwatch(stopwatch1);
                    return (Action) dir_choice;
                    //Else do action 4
                }else{
                    output[dir_int] = 0;
                    dir_int = choose_output(output);
                    dir_choice = Action.values()[dir_int];
                    update_times_stopwatch(stopwatch1);
                    if(possibleActions.contains(dir_choice)) {
                        return (Action) dir_choice;
                    }else{//FAILSAFE
                        return possibleActions.get(0);
                    }
                }


            }
        }


        //return (Action)RandomUtils.pickRandom(possibleActions.toArray(), random);
        /*if (possibleActions.contains(Action.RIGHT)) {
            return (Action) Action.RIGHT;
        } else if (possibleActions.contains(Action.LEFT)) {
            // If cannot go right, then first move
            return (Action) Action.LEFT;
        }else if (possibleActions.contains(Action.UP)) {
            // If cannot go right, then first move
            return (Action) Action.UP;
        }else{
            return (Action) Action.DOWN;
        }*/

    }

}
