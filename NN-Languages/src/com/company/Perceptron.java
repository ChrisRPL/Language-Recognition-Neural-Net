package com.company;

import java.util.Collections;
import java.util.List;
import java.util.Vector;
import java.util.concurrent.ThreadLocalRandom;

public class Perceptron {

    private List<Double> weights;
    private ActivationFunction activationFunction;
    private String decisionAttribute;

    public Perceptron(ActivationFunction activationFunction, String decisionAttribute, int vectorSize) {

        this.weights = new Vector<>();
        for (int i=0; i<vectorSize; i++) //W WAGI WŁĄCZAM RÓWNIEŻ WARTOŚĆ THETA
            weights.add(ThreadLocalRandom.current().nextDouble(-1, 1));

        this.activationFunction = activationFunction;
        this.decisionAttribute = decisionAttribute;
    }

    private void deltasRule(Double d, Double y, Double alpha, List<Double> input){

        for (int i=0; i<weights.size(); i++){
            weights.set(i, weights.get(i) + (d-y)*input.get(i)*alpha);
        }
    }

    public boolean train(String realDecisionAttribute, List<Double> input, double alpha){
        Double net = activationFunction.activationFunction(input, weights); //WARTOŚĆ WYJŚCIOWA PERCEPTRONU
        Double realOutcome = decisionAttribute.equals(realDecisionAttribute) ? 1.0 : 0.0; //WARTOŚĆ POŻĄDANA PERCEPTRONU

        //DOPÓKI WARTOŚĆ WYJŚCIOWA JEST RÓŻNA OD POŻĄDANEJ, UCZYMY WAGI REGUŁĄ DELTY I WYLICZAMY PONOWNIE WARTOŚĆ WYJŚCIOWĄ
        if (!net.equals(realOutcome)) {
            while (!net.equals(realOutcome)) {
                deltasRule(realOutcome, net, alpha, input);
                net = activationFunction.activationFunction(input, weights);
            }

            return false; //JEŻELI PERCEPTRON POTRZEBOWAŁ WYUCZYĆ WAGI DLA OBSERWACJI, OZNACZA TO ŻE SIĘ DO KOŃCA NIE WYTRENOWAŁ
        }

        return true; //PERCEPTRON NIE POTRZEBOWAŁ DOSTOSOWAĆ WAG DLA OBSERWACJI
    }

    public void trainPerceptron(List<Observation> trainingData, int limit, double learningRate){
        int goodPredicted = 0;

        for (int i=0; i<limit; i++) {
            goodPredicted = 0;

            for (Observation trainingObservation : trainingData) {
                boolean trainingResult = train(trainingObservation.getDecisionAttribute(), trainingObservation.getConditionalAttributes(), learningRate);
                if (trainingResult)
                    goodPredicted++;
            }
            Collections.shuffle(trainingData);
            if (goodPredicted == trainingData.size()) {
                break;
            }
        }
    }

    public String getDecisionAttribute() {
        return decisionAttribute;
    }

    public List<Double> getWeights() {
        return weights;
    }

    public String toString(){
        return (String) decisionAttribute;
    }
}
