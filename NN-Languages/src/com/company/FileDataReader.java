package com.company;

import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.List;
import java.util.Vector;

public class FileDataReader implements DataReader {
    @Override
    public List<Observation> readData(String source) {
        List<Observation> observationData = new Vector<>();

        try {
            Files.walkFileTree(Path.of(source), new SimpleFileVisitor<>() {
                Observation observation;
                String decisionAttribute;

                @Override
                public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
                    decisionAttribute = dir.getFileName().toString();

                    return FileVisitResult.CONTINUE;
                }

                @Override
                public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                    observation = new Observation(new Vector<>(), "");
                    observation.setDecisionAttribute(decisionAttribute);
                    Vector<Double> data = prepareDataFromFile(file);
                    observation.setConditionalAttributes(data);
                    observationData.add(observation);


                    return FileVisitResult.CONTINUE;
                }

                @Override
                public FileVisitResult visitFileFailed(Path file, IOException exc) throws IOException {
                    return FileVisitResult.CONTINUE;
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }

        return observationData;
    }

    public Vector<Double> prepareDataFromFile(Path file) throws IOException {
        int[] lettersCount = new int['z' + 1];
        int allLetters = 0;
        Vector<Double> data = new Vector<>();

        Files.lines(file).forEach(e -> {
            String lowered = e.toLowerCase();

            for (int i = 0; i < lowered.length(); i++) {
                if (lowered.charAt(i) >= 'a' && lowered.charAt(i) <= 'z')
                    lettersCount[lowered.charAt(i)]++;
            }
        });

        for (int value : lettersCount) {
            allLetters += value;
        }

        for (int i = 'a'; i <= 'z'; i++) {
            data.add(lettersCount[i] / (double) allLetters);
        }

        data.add(-1.0);

        return data;
    }

    public static Vector<Double> prepareDataFromText(String text) throws IOException {
        int[] lettersCount = new int['z' + 1];
        int allLetters = 0;
        Vector<Double> data = new Vector<>();


        String lowered = text.toLowerCase();

        for (int i = 0; i < lowered.length(); i++) {
            if (lowered.charAt(i) >= 'a' && lowered.charAt(i) <= 'z')
                lettersCount[lowered.charAt(i)]++;
        }


        for (int value : lettersCount) {
            allLetters += value;
        }

        for (int i = 'a'; i <= 'z'; i++) {
            data.add(lettersCount[i] / (double) allLetters);
        }
        data.add(-1.0);

        return data;
    }
}
