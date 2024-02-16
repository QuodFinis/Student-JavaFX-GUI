package com.example.studentdatabase;

import javafx.scene.canvas.GraphicsContext;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;
import java.util.stream.Collectors;

public class HistogramAlphaBet {

    Map<Character, Integer> frequency = new HashMap<Character, Integer>();
    Map<Character, Double> probability = new HashMap<Character, Double>();

    HistogramAlphaBet() {
    }

    HistogramAlphaBet(Map<Character, Integer> m) {
        frequency.putAll(m);
    }

    HistogramAlphaBet(HistogramAlphaBet h) {

        frequency.putAll(h.getFrequency());
        probability.putAll(h.getProbability());
    }

    HistogramAlphaBet(String text) {

        String w = text.replaceAll("[^a-zA-Z]", "").toLowerCase();

        for (int i = 0; i < w.length(); i++) {
            Character key = w.charAt(i);
            incrementFrequency(frequency, key);
        }

        probability = getProbability();
    }

    HistogramAlphaBet(ResultSet RS, String fieldKey, String fieldValue) {


        try {
            while (RS.next()) {
                frequency.put(RS.getString(fieldKey).charAt(0), RS.getInt(fieldValue));
            }
        } catch (NoSuchElementException | IllegalStateException | SQLException e) {
            System.out.println(e);
        }
    }

    private static <K> void incrementFrequency(Map<K, Integer> m, K Key) {

        m.putIfAbsent(Key, 0);
        m.put(Key, m.get(Key) + 1);
    }

    public Map<Character, Integer> getFrequency() {
        return frequency;
    }

    public Integer getCumulativeFrequency() {
        return frequency.values().stream().reduce(0, Integer::sum);
    }

    public Map<Character, Integer> sortUpFrequency() {

        return frequency
                .entrySet()
                .stream()
                .sorted(Map.Entry.comparingByValue())
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (e1, e2) -> e2, LinkedHashMap::new));
    }

    public Map<Character, Integer> sortDownFrequency() {

        return frequency
                .entrySet()
                .stream()
                .sorted(Collections.reverseOrder(Map.Entry.comparingByValue()))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (e1, e2) -> e2, LinkedHashMap::new));
    }

    public Map<Character, Double> getProbability() {

        double inverseCumulativeFrequency = 1.0 / getCumulativeFrequency();
        for (Character Key : frequency.keySet()) {
            probability.put(Key, (double) frequency.get(Key) * inverseCumulativeFrequency);
        }

        return probability;
    }

    public Map<Character, Double> sortDownProbability() {

        return getProbability().entrySet()
                .stream()
                .sorted(Collections.reverseOrder(Map.Entry.comparingByValue()))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (e1, e2) -> e2, LinkedHashMap::new));
    }

    public Map<Character, Double> sortUpProbability() {

        return getProbability().entrySet()
                .stream()
                .sorted(Map.Entry.comparingByValue())
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (e1, e2) -> e2, LinkedHashMap::new));
    }

    public Double getSumOfProbability() {
        return probability.values().stream().reduce(0.0, Double::sum);
    }

    public boolean checkSumOfProbability() {
        return getSumOfProbability() == 1;
    }

    @Override
    public String toString() {

        String output = "Frequency of Characters:\n";
        for (Character K : frequency.keySet()) {
            output += K + ": " + frequency.get(K) + "\n";
        }

        return output;
    }

    class MyPieChart {

        Map<Character, Slice> slices = new HashMap<Character, Slice>();

        int N;
        int M;
        MyPoint center;
        double width, height;
        double rotateAngle;

        MyPieChart(int N, int M, MyPoint center, double width, double height, double rotateAngle) {

            this.N = N;
            this.M = M;
            this.center = center;
            this.width = width;
            this.height = height;
            this.rotateAngle = rotateAngle < 360 ? rotateAngle : rotateAngle - 360;

            slices = getMyPieChart();
        }


        public Map<Character, Slice> getMyPieChart() {

            MyColor[] colors = MyColor.getMyColors();
            int colorsSize = colors.length;

            Map<Character, Double> sortedProbability = sortDownProbability();
            Random rand = new Random();
            double sliceStartAngle = this.rotateAngle;
            for (Character key : sortedProbability.keySet()) {
                double sliceValue = sortedProbability.get(key);

                double sliceArcAngle = 360.0 * sliceValue;
                MyColor color = colors[rand.nextInt(colorsSize)];
                String sliceInformation = key + ": " + String.format("%.4f", sliceValue);
                slices.put(key, new Slice(center, width, height, sliceStartAngle, sliceArcAngle, color, sliceInformation));

                sliceStartAngle += sliceArcAngle;
                sliceStartAngle = sliceStartAngle < 360.0 ? sliceStartAngle : sliceStartAngle - 360.0;

            }

            return slices;
        }

        public void draw(GraphicsContext GC) {

            Map<Character, Double> sortedProbability = sortDownProbability();


            GC.clearRect(0.0, 0.0, GC.getCanvas().getWidth(), GC.getCanvas().getHeight());
            GC.setFill(MyColor.GRAY.getJavaFXColor());
            GC.fillRect(0.0, 0.0, GC.getCanvas().getWidth(), GC.getCanvas().getHeight());

            int n = 0;
            double probabilityAllOtherCharacters = 1.0;
            for (Character key : sortedProbability.keySet()) {
                double sliceStartAngle = slices.get(key).getStartAngle();
                double sliceArcAngle = slices.get(key).getArcAngle();
                if (n < N) {
                    slices.get(key).draw(GC);
                    probabilityAllOtherCharacters -= sortedProbability.get(key);
                    n++;
                } else {
                    if (N != M) {
                        String information = "All other characters: " + String.format("%.4f", probabilityAllOtherCharacters);
                        if (sliceStartAngle < rotateAngle) {
                            Slice sliceAllOtherCharacters = new Slice(center, width, height, sliceStartAngle, rotateAngle - sliceStartAngle, MyColor.getRandomColor(), information);
                            sliceAllOtherCharacters.draw(GC);
                        } else {
                            Slice sliceAllOtherCharacters = new Slice(center, width, height, sliceStartAngle, 360.0 - sliceStartAngle + rotateAngle, MyColor.getRandomColor(), information);
                            sliceAllOtherCharacters.draw(GC);
                        }
                        break;
                    }
                }
            }
        }
    }
}