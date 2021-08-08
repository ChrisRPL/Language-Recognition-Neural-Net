package com.company;

import java.util.List;

public class ThresholdFunction implements ActivationFunction {
    @Override
    public Double activationFunction(List<Double> input, List<Double> weights) {
        double net=0.0;

        for (int i=0; i<input.size(); i++){
            net+=input.get(i)*weights.get(i);
        }

        return net >= 0 ? 1.0 : 0.0;
    }
}
