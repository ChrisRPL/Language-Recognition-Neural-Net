package com.company;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.atomic.AtomicLong;

public class NeuralNet {
    private List<Perceptron> perceptrons;
    private List<Observation> trainingInput;
    private List<Observation> testInput;
    private int nnLimit;

    public NeuralNet(String trainingFilePath, String testFilePath, int nnLimit) throws IOException {
        FileDataReader dataReader = new FileDataReader();

        this.perceptrons = new ArrayList<>();
        this.trainingInput = dataReader.readData(trainingFilePath);
        this.testInput = dataReader.readData(testFilePath);
        this.nnLimit = nnLimit;
    }

    public void trainNN() {
        if (perceptrons.size()==0)
            buildNeuronLayer();
        for (Perceptron perceptron : perceptrons) {
            perceptron.trainPerceptron(trainingInput, nnLimit, 0.9);
        }

    }

    public void testNN() throws IOException {
        int goodChecked = 0;
        double efficiency;
        List<String> allGoodExamples = new ArrayList<>();
        Map<String, Map<String, Integer>> errorsMatrix = new LinkedHashMap<>();

        for (Perceptron perceptron : perceptrons){
            Map<String, Integer> countObservations = new LinkedHashMap<>();
            for (Perceptron perceptron1 : perceptrons){
                countObservations.put(perceptron1.getDecisionAttribute(), 0);
            }
            errorsMatrix.put(perceptron.getDecisionAttribute(), countObservations);
        }


        for (Observation x : testInput) {
            String decision = predictObservations(x.getConditionalAttributes());


                if (decision.equals(x.getDecisionAttribute())) {
                    goodChecked++;
                    allGoodExamples.add(decision);
                }
                errorsMatrix.get(x.getDecisionAttribute()).put(decision, errorsMatrix.get(x.getDecisionAttribute()).get(decision) + 1);

        }
        efficiency = goodChecked / (double) testInput.size() * 100;

        for (Perceptron perceptron : perceptrons){
            long activated=0, all=0, negativeAsPositive=0;
            AtomicLong badPredicted = new AtomicLong();
            errorsMatrix.get(perceptron.getDecisionAttribute()).forEach((key, value) -> {
                if (!key.equals(perceptron.getDecisionAttribute()))
                    badPredicted.addAndGet(value);
            });

            activated = allGoodExamples.stream().filter(e->e.equals(perceptron.getDecisionAttribute())).count();
            all = testInput.stream().filter(e->e.getDecisionAttribute().equals(perceptron.getDecisionAttribute())).count();

            for (Map.Entry<String, Map<String,Integer>> entry : errorsMatrix.entrySet()){
                if (!entry.getKey().equals(perceptron.getDecisionAttribute())){
                    for (Map.Entry<String,Integer> integerEntry : entry.getValue().entrySet()){
                        if (integerEntry.getKey().equals(perceptron.getDecisionAttribute()))
                            negativeAsPositive+=integerEntry.getValue();
                    }
                }
            }

            System.out.println();
            System.out.println("Perceptron [" + perceptron.getDecisionAttribute() + "] = " + (activated+(testInput.size()-all)-(all-activated))/(double)testInput.size()*100 + "% (dokładność)");

            System.out.println("    P   N");
            System.out.println("P   " + activated + "\t" + (negativeAsPositive));
            System.out.println("N   " + badPredicted + "\t" + ((testInput.size()-all-negativeAsPositive)));
            System.out.println();
            double precision = activated/(double)(activated+badPredicted.get());
            double recall = activated/(double)(activated+negativeAsPositive);
            System.out.println("P(precyzja) = " + precision);
            System.out.println("R(pełność) = " + recall);
            System.out.println("F-miara = " + 2*precision*recall/(precision+recall));
        }

        System.out.println("Dobre przyklady: " + goodChecked + ", wynik: " + efficiency);
    }


    public void buildNeuronLayer() {
        Set<String> uniqueDecisionAttributes = new HashSet<>();
        trainingInput.forEach(e -> uniqueDecisionAttributes.add(e.getDecisionAttribute()));

        for (String decisionAttribute : uniqueDecisionAttributes) {
            ActivationFunction activationFunction = new ThresholdFunction();
            Perceptron perceptron = new Perceptron(activationFunction, decisionAttribute, trainingInput.get(0).getConditionalAttributes().size());

            perceptrons.add(perceptron);
        }
    }

    public String predictObservations(String text) throws IOException {
        ThresholdFunction thresholdFunction = new ThresholdFunction();
        List<Double> attributes = FileDataReader.prepareDataFromText(text);


            String decision = "";
            for (Perceptron perceptron : perceptrons) {
                double current = thresholdFunction.activationFunction(attributes, perceptron.getWeights());
                if (current == 1){
                    decision = perceptron.getDecisionAttribute();
                    break;
                }
            }

            return decision.equals("") ? perceptrons.get(ThreadLocalRandom.current().nextInt(0, perceptrons.size())).getDecisionAttribute() : decision;

    }

    public String predictObservations(List<Double> input) throws IOException {
        ThresholdFunction thresholdFunction = new ThresholdFunction();

        String decision = "";
        for (Perceptron perceptron : perceptrons) {
            double current = thresholdFunction.activationFunction(input, perceptron.getWeights());
            if (current == 1){
                decision = perceptron.getDecisionAttribute();
                break;
            }
        }

        return decision.equals("") ? perceptrons.get(ThreadLocalRandom.current().nextInt(0, perceptrons.size())).getDecisionAttribute() : decision;

    }
}
