
package put.game2048;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.time.Duration;
import java.util.List;
import com.google.common.base.Stopwatch;
import org.apache.commons.math3.random.MersenneTwister;
import org.apache.commons.math3.random.RandomDataGenerator;
import java.util.Arrays;
import java.util.concurrent.TimeUnit;





public class MyAgent implements Agent {
    public double average_ms = 0;
    public int number_moves = 0;
    public RandomDataGenerator random = new RandomDataGenerator(new MersenneTwister(123));
    String line = null;
    Network net = new Network();

    public MyAgent() {
        net = ImportNetwork("BestNN_Struct_36.txt");
        this.net = net;
    }

    public Network ImportNetwork(String fileName) {
        double[][][] networkRead;
        try {
            FileReader fileReader = new FileReader(fileName);
            BufferedReader bufferedReader = new BufferedReader(fileReader);
            int lineCount = 0;
            while ((line = bufferedReader.readLine()) != null) {
                if (line.contains("Fitness")) {
                    continue;
                }
                lineCount += 1;
                System.out.println(line);
                line = line.replace("true", "");
                line = line.replace("Struct", "");
                line = line.replaceFirst(":", "");
                line = line.replaceFirst("  ", "");
                String[] output = line.split("  ");
                if (lineCount == 1) {

                }
                String[][] outputNew = new String[output.length][3];
                for (int i = 0; i < output.length; i++) {
                    output[i] = output[i].replace(" ", "");
                    output[i] = output[i].replace("-->", ":");
                    output[i] = output[i].replaceFirst(":", "");
                    outputNew[i] = output[i].split(":");
                    System.out.println(Arrays.toString(outputNew[i]));

                }
                int newLayer = 0;
                for (int i = 0; i < outputNew.length; i++) {
                    if (outputNew[i][0].contains("false")) {
                        continue;
                    }
                    if (Integer.parseInt(outputNew[i][1]) > 19 && newLayer == 0) {
                        newLayer = 1;
                        net.addLayer();
                        //System.out.println("hello");
                        //System.out.println(net.weights[0][1][0][0]);
                    } else if (Integer.parseInt(outputNew[i][1]) > 20) {
                        net.addNode(outLayer(outputNew[i][1]));
                        //  System.out.println("hello");
                    }
                    //System.out.println(outputNew[i][1]);
                    //System.out.println(outLayer(outputNew[i][1]));
                    net.addConnection(outLayer(outputNew[i][0]), outLayer(outputNew[i][1]), outNode(outputNew[i][0]), outNode(outputNew[i][1]), Double.parseDouble(outputNew[i][2]));
                    System.out.println(outputNew[i][2]);
                    System.out.println(net.weights[outLayer(outputNew[i][0])][outLayer(outputNew[i][1])][outNode(outputNew[i][0])][outNode(outputNew[i][1])]);
                }


                //System.out.println(Arrays.toString(outputNew[3]));
                //System.out.println(line);

            }


            bufferedReader.close();
        } catch (FileNotFoundException ex) {
            System.out.println(
                    "Unable to open file '" +
                            fileName + "'");
        } catch (IOException ex) {
            System.out.println(
                    "Error reading file '"
                            + fileName + "'");
            // Or we could just do this:
            // ex.printStackTrace();
        }
        return net;

    }

    public int outNode(String node) {
        int nodeNew = Integer.parseInt(node);
        if (nodeNew < 16) {
            return nodeNew;
        } else if (nodeNew < 20) {
            return 20 - nodeNew;
        } else {
            //ystem.out.println(nodeNew-20);
            return nodeNew - 20;
        }
    }

    public int outLayer(String node) {
        int nodeNew = Integer.parseInt(node);
        if (nodeNew < 16) {
            return net.INPUT_LAYER;
        } else if (nodeNew < 20) {
            return net.OUTPUT_LAYER;
        } else {
            return net.INPUT_LAYER + 1;
        }
    }


    // A nonparametric constructor is required

    //Chooses an output based on the probability of that output
    public int choose_output(double[] output_mat) {

        //We then use the output to make a decision
        double random2 = Math.random() * Arrays.stream(output_mat).sum();
        //Sum through matrix until we reach this value
        double sum1 = 0;
        int dir_option = 0;
        for (int i = 0; i < output_mat.length; i++) {
            sum1 += output_mat[i];
            if (sum1 >= random2) {
                dir_option = i;
                break;
            }
        }

        return dir_option;
    }

    public void update_times_stopwatch(Stopwatch stopwatch1) {
        stopwatch1.reset();
        number_moves++;
        average_ms = (number_moves - 1) * average_ms + stopwatch1.elapsed(TimeUnit.MILLISECONDS);
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
        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 4; j++) {
                double value = board.TILE_VALUES[board.getValue(i, j)];
                if (value == 0) {
                    input[inputCount] = value;
                } else {
                    //Using 1/board value instead of actual value
                    input[inputCount] = value / MinimiserLarge;
                }
                //System.out.println(board.getValue(i,j));
                inputCount += 1;
            }
        }
        //Calculating output
        //System.out.println(Arrays.toString(input));
        double[] output = net.calculate(input);
        int dir_int = choose_output(output);
        Action dir_choice = Action.values()[dir_int];
        //Try action 1
        if (possibleActions.contains(dir_choice)) {
            update_times_stopwatch(stopwatch1);
            return (Action) dir_choice;

        } else {
            output[dir_int] = 0;
            dir_int = choose_output(output);
            dir_choice = Action.values()[dir_int];
            //Try action 2
            if (possibleActions.contains(dir_choice)) {
                update_times_stopwatch(stopwatch1);
                return (Action) dir_choice;
            } else {
                output[dir_int] = 0;
                dir_int = choose_output(output);
                dir_choice = Action.values()[dir_int];
                //Try action 3
                if (possibleActions.contains(dir_choice)) {
                    update_times_stopwatch(stopwatch1);
                    return (Action) dir_choice;
                    //Else do action 4
                } else {
                    output[dir_int] = 0;
                    dir_int = choose_output(output);
                    dir_choice = Action.values()[dir_int];
                    update_times_stopwatch(stopwatch1);
                    if (possibleActions.contains(dir_choice)) {
                        return (Action) dir_choice;
                    } else {//FAILSAFE
                        return possibleActions.get(0);
                    }
                }


            }
        }
    }
}