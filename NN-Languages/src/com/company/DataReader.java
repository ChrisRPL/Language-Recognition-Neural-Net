package com.company;

import java.util.List;

public interface DataReader {
    public List<Observation> readData(String source);
}
