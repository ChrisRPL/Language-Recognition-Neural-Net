package com.company;

import java.io.IOException;

public class Main {

    public static void main(String[] args) throws IOException {
        NeuralNet neuralNet = new NeuralNet("training_data",
                "test_data", 400);

        neuralNet.trainNN();
        neuralNet.testNN();

        java.awt.EventQueue.invokeLater(() -> new GUI(neuralNet).setVisible(true));
    }
}
