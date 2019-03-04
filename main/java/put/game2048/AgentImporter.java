package put.game2048;

import java.io.*;
import java.util.Arrays;


public class AgentImporter {
    String line = null;
    Network net = new Network();

    public AgentImporter() {
        //this.FileName = FileName;
        this.line = line;

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

}
