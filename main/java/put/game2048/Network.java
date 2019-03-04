package put.game2048;

import java.util.Arrays;

public class Network {

    public double[][] output;
    public double[][][][] weights; //input layer,  output layer, input neuron, output neuron
    public double[][] bias;

    public int[] NETWORK_LAYER_SIZES = {16, 4};
    public final int INPUT_SIZES;
    public final int OUTPUT_SIZES;
    public int NETWORK_SIZE;
    public int INPUT_LAYER = 0;
    public int NEW_LAYER = 1;
    public int OUTPUT_LAYER;

    public Network() {
        this.NETWORK_LAYER_SIZES = NETWORK_LAYER_SIZES;
        this.INPUT_SIZES = NETWORK_LAYER_SIZES[0];
        this.OUTPUT_SIZES = NETWORK_LAYER_SIZES[1];
        this.NETWORK_SIZE = NETWORK_LAYER_SIZES.length;
        this.OUTPUT_LAYER = NETWORK_SIZE - 1;
        this.output = new double[NETWORK_SIZE][];
        this.weights = new double[NETWORK_SIZE][NETWORK_SIZE][INPUT_SIZES][OUTPUT_SIZES];
        this.bias = new double[NETWORK_SIZE][];


        initialiseNetwork();

    }

    private void initialiseNetwork() {

        for (int i = 0; i < NETWORK_SIZE; i++) {
            this.output[i] = new double[NETWORK_LAYER_SIZES[i]];
            this.bias[i] = new double[NETWORK_LAYER_SIZES[i]];
            this.bias[i] = NetworkTools.createRandomArray(NETWORK_LAYER_SIZES[i], 0.3, 0.7);
        }

        for (int inputNeuron = 0; inputNeuron < INPUT_SIZES; inputNeuron++) {
            for (int outputNeuron = 0; outputNeuron < OUTPUT_SIZES; outputNeuron++) {
                weights[0][1][inputNeuron][outputNeuron] = Math.random();
            }
        }
    }

    public void addLayer() {
        //Adding an extra layer to the network size
        NETWORK_SIZE += 1;
        OUTPUT_LAYER += 1;
        //creating a new array for network layer size for new size
        int[] newNetworkLayerSize = new int[NETWORK_SIZE];
        //Initialising this new network layer size with new layer
        for (int layer = 0; layer < NETWORK_SIZE; layer++) {
            if (layer == INPUT_LAYER) {
                //New input layer
                newNetworkLayerSize[layer] = INPUT_SIZES;
            } else if (layer == NEW_LAYER) {
                newNetworkLayerSize[layer] = 1;
            } else if (layer == OUTPUT_LAYER) {
                //New output layer
                newNetworkLayerSize[layer] = OUTPUT_SIZES;
            } else {
                //Copying old layers
                newNetworkLayerSize[layer] = NETWORK_LAYER_SIZES[layer - 1];
            }

        }
        //Setting network layer sizes to new sizes
        NETWORK_LAYER_SIZES = newNetworkLayerSize;

        //Initialising all other variables with new layer
        double[][] newOutput = initNewOutput();
        double[][][][] newWeights = initNewWeights();
        double[][] newBias = initNewBias();

        mimicConnectionsLayer(newWeights);
        mimicBiasLayer(newBias);
        weights = newWeights;
        bias = newBias;

    }

    public void addNode(int layer) {
        NETWORK_LAYER_SIZES[layer] += 1;
        double[][][][] newWeights = initNewWeights();
        double[][] newOutput = initNewOutput();
        double[][] newBias = initNewBias();
        mimicConnectionsNode(newWeights, layer);
        mimicBiasNode(newBias, layer);
        output = newOutput;
        bias = newBias;
        weights = newWeights;


    }

    //This function also functions as modifyConnection
    public void addConnection(int inputLayer, int outputLayer, int inputNode, int outputNode, double weight) {
        weights[inputLayer][outputLayer][inputNode][outputNode] = weight;
    }

    public void removeConnection(int inputLayer, int outputLayer, int inputNode, int outputNode) {
        weights[inputLayer][outputLayer][inputNode][outputNode] = 0;
    }

    public void mimicConnectionsLayer(double[][][][] newWeights) {
        for (int inputLayer = 0; inputLayer < NETWORK_SIZE; inputLayer++) {

            for (int outputLayer = 0; outputLayer < NETWORK_SIZE; outputLayer++) {
                for (int inputNode = 0; inputNode < NETWORK_LAYER_SIZES[inputLayer]; inputNode++) {
                    for (int outputNode = 0; outputNode < NETWORK_LAYER_SIZES[outputLayer]; outputNode++) {

                        if (inputLayer == 1 || outputLayer == 1 || inputLayer >= outputLayer) {

                            continue;
                        } else if (inputLayer == 0) {
                            if (weights[inputLayer][outputLayer - 1][inputNode][outputNode] > 0) {

                                newWeights[inputLayer][outputLayer][inputNode][outputNode] = weights[inputLayer][outputLayer - 1][inputNode][outputNode];
                            }
                        } else if (inputLayer == 1) {
                            newWeights[inputLayer][outputLayer][inputNode][outputNode] = Math.random();
                        } else if (inputLayer > 1) {
                            if (weights[inputLayer - 1][outputLayer - 1][inputNode][outputNode] > 0) {
                                newWeights[inputLayer][outputLayer][inputNode][outputNode] = weights[inputLayer - 1][outputLayer - 1][inputNode][outputNode];
                            }
                        }
                    }
                }
            }
        }
        weights = newWeights;
    }

    public void mimicConnectionsNode(double[][][][] newWeights, int layer) {
        for (int inputLayer = 0; inputLayer < NETWORK_SIZE; inputLayer++) {
            for (int outputLayer = 0; outputLayer < NETWORK_SIZE; outputLayer++) {
                if (inputLayer >= outputLayer) {
                    continue;
                }
                for (int inputNode = 0; inputNode < NETWORK_LAYER_SIZES[inputLayer]; inputNode++) {
                    for (int outputNode = 0; outputNode < NETWORK_LAYER_SIZES[outputLayer]; outputNode++) {
                        if ((outputLayer == layer || inputLayer == layer) && (inputNode == (NETWORK_LAYER_SIZES[layer] - 1) || outputNode == (NETWORK_LAYER_SIZES[layer] - 1))) {
                            newWeights[inputLayer][outputLayer][inputNode][outputNode] = 0;
                        } else {
                            newWeights[inputLayer][outputLayer][inputNode][outputNode] = weights[inputLayer][outputLayer][inputNode][outputNode];
                        }


                    }
                }
            }
        }
    }

    public void mimicBiasLayer(double[][] newBias) {
        for (int layer = 0; layer < NETWORK_SIZE; layer++) {
            for (int node = 0; node < NETWORK_LAYER_SIZES[layer]; node++) {
                if (layer == 0) {
                    newBias[layer][node] = bias[layer][node];
                } else if (layer == 1) {
                    newBias[layer][node] = (double) (Math.random() * (0.7 - 0.3) + 0.3);
                } else if (layer > 1) {
                    newBias[layer][node] = bias[layer - 1][node];
                }
            }
        }
    }

    public void mimicBiasNode(double[][] newBias, int layerNew) {
        for (int layer = 0; layer < NETWORK_SIZE; layer++) {
            for (int node = 0; node < NETWORK_LAYER_SIZES[layer]; node++) {
                if (layer == layerNew && node == NETWORK_LAYER_SIZES[layerNew] - 1) {
                    newBias[layer][node] = (double) (Math.random() * (0.7 - 0.3) + 0.3);
                } else {
                    newBias[layer][node] = bias[layer][node];
                    ;
                }
            }
        }
    }


    public double[][][][] initNewWeights() {
        double[][][][] newWeights = new double[NETWORK_SIZE][NETWORK_SIZE][][];
        for (int i = 0; i < NETWORK_SIZE - 1; i++) {
            for (int j = 0; j < NETWORK_SIZE - 1; j++) {
                newWeights[i][j + 1] = new double[NETWORK_LAYER_SIZES[i]][NETWORK_LAYER_SIZES[j + 1]];
            }
        }
        for (int inputLayer = 0; inputLayer < NETWORK_SIZE; inputLayer++) {
            for (int outputLayer = 0; outputLayer < NETWORK_SIZE; outputLayer++) {
                for (int inputNode = 0; inputNode < NETWORK_LAYER_SIZES[inputLayer]; inputNode++) {
                    for (int outputNode = 0; outputNode < NETWORK_LAYER_SIZES[outputLayer]; outputNode++) {
                        if (inputLayer >= outputLayer) {
                            //System.out.println("[+] Continue ");
                            continue;

                        } else {
                            newWeights[inputLayer][outputLayer][inputNode][outputNode] = 0;
                        }
                    }
                }
            }
        }
        return newWeights;
    }

    public double[][] initNewOutput() {
        double[][] newOutput = new double[NETWORK_SIZE][];
        for (int i = 0; i < NETWORK_SIZE; i++) {
            newOutput[i] = new double[NETWORK_LAYER_SIZES[i]];
        }
        return newOutput;
    }

    public double[] calculate(double[] input) {
        if (input.length != INPUT_SIZES) return null;
        output[0] = input;
        for (int layer = 1; layer < NETWORK_SIZE; layer++) {
            for (int neuron = 0; neuron < NETWORK_LAYER_SIZES[layer]; neuron++) {
                double sum = 0;
                for (int prevNeuron = 0; prevNeuron < NETWORK_LAYER_SIZES[layer - 1]; prevNeuron++) {
                    sum += output[layer - 1][prevNeuron] * weights[layer - 1][layer][prevNeuron][neuron];
                }
                sum += bias[layer][neuron];
                output[layer][neuron] = sigmoid(sum);
            }
        }
        return output[NETWORK_SIZE - 1];
    }

    private double sigmoid(double x) {
        return 1d / (1 + Math.exp(-x));
    }

    public double[][] initNewBias() {
        double[][] newBias = new double[NETWORK_SIZE][];
        for (int i = 0; i < NETWORK_SIZE; i++) {
            newBias[i] = new double[NETWORK_LAYER_SIZES[i]];
        }
        return newBias;
    }


    //This function takes a genome as an argument and converts it into a neural network
    //Due to how this has been programmed the genome needs a fully connected set of input and output neurons size 16,4.
    //We also feed it an initialised network
    public Network convertGenomeToANN(Genome GeN1){

        Network tnet = new Network();

        //Parse genome and add non input and output nodes
        //We add our hidden layer
        tnet.addLayer();
        for(int i = 0; i < GeN1.NodeNum-(20);i++) {
            tnet.addNode(1);

        }
        System.out.println("Added:" + Integer.toString(GeN1.NodeNum-(20)) + "Nodes");
        //CODE HERE
        //Parse connections and add or modify them, modifying is the same as adding addConnection()
        //We do this first for inputs and outputs
        for(int i = 0; i < 16*4;i++){
            //Update weight if enabled
            if(GeN1.CG[i].Enabled == true){
                int In_ID = GeN1.CG[i].InputNode;
                int Out_ID = GeN1.CG[i].OutputNode-16;
                System.out.println("NNM:" + " Connecting_IO: " + Integer.toString(In_ID) + " " + Integer.toString(Out_ID));
                tnet.addConnection(0,2,In_ID,Out_ID,GeN1.CG[i].Weight);
            }else{//If the the connection is disabled remove it
                int In_ID = GeN1.CG[i].InputNode;
                int Out_ID = GeN1.CG[i].OutputNode-16;
                System.out.println("NNM:" + " DisConnecting_IO: " + Integer.toString(In_ID) + " " + Integer.toString(Out_ID));
                tnet.removeConnection(0,2,In_ID,Out_ID);
            }
        }


        //We then perform this process for the hidden nodes
        for(int i = 16*4; i < GeN1.ConnectionNum;i++){

            int In_ID = GeN1.CG[i].InputNode;
            int Out_ID = GeN1.CG[i].OutputNode;
            int In_lay = 1;
            int Out_lay = 1;
            //If inputnode is inputneuron
            if(GeN1.NG[In_ID].NodeType == NodeGene.NodeTypes.INPUT){
                Out_ID -= 20;//Start from hidden list
                In_lay = 0;

            }else{//Therefor input node is hidden
                //if outputnode is an outputneuron
                if(GeN1.NG[Out_ID].NodeType == NodeGene.NodeTypes.OUTPUT) {
                    Out_ID -= 16;//Start from output list
                    In_ID -= 20;
                    Out_lay = 2;
                }else{// Hidden to hidden so both -20
                    Out_ID -= 20;
                    In_ID -= 20;
                }

            }



            //Update weight if enabled
            if(GeN1.CG[i].Enabled == true){


                System.out.println("NNM:" + " Connecting_HID: " + Integer.toString(In_ID) + " " + Integer.toString(Out_ID));
                tnet.addConnection(In_lay,Out_lay,In_ID,Out_ID,GeN1.CG[i].Weight);
            }else{//If the the connection is disabled remove it




                System.out.println("NNM:" + " DisConnecting_HID: " + Integer.toString(In_ID) + " " + Integer.toString(Out_ID));
                tnet.removeConnection(In_lay,Out_lay,In_ID,Out_ID);
            }

        }

        return tnet;
    }




    public static void main(String[] args) {
        //Initialising a network
        Network net = new Network();
        //THis prints some weird stuff
        System.out.println(Arrays.toString(net.weights));
        //Print a specific weight
        System.out.println(net.weights[0][1][2][3]);
        System.out.println(net.bias[0][1]);
        //Adds a layer to the network
        net.addLayer();
        //Adds a node to the network ina layer
        net.addNode(1);
        System.out.println(Arrays.toString(net.NETWORK_LAYER_SIZES));
        net.addLayer();
        net.addNode(2);
        net.addNode(1);
        System.out.println(Arrays.toString(net.NETWORK_LAYER_SIZES));
        net.addConnection(0, 1, 2, 1, 0.32);
        System.out.println(net.weights[0][1][2][1]);
        net.removeConnection(0, 1, 2, 1);
        System.out.println(net.weights[0][1][2][1]);
        System.out.println(net.output[0][0]);
        double[] input= {0.2, 0.3,0.4,0.5,0.2, 0.3,0.4,0.5,0.2, 0.3,0.4,0.5,0.2, 0.3,0.4,0.5};
        double[] output = net.calculate(input);
        System.out.println(Arrays.toString(output));
    }
}
