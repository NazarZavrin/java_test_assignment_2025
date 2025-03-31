package com.playtech;

import com.playtech.report.Report;
import com.playtech.util.xml.XmlParser;
import jakarta.xml.bind.JAXBException;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;

public class ReportGenerator {

    public static void main(String[] args) {
        /*
         * if (args.length != 3) {
         * System.err.
         * println("Application should have 3 paths as arguments: csv file path, xml file path and output directory"
         * );
         * System.exit(1);
         * }
         * String csvDataFilePath = args[0], reportXmlFilePath = args[1],
         * outputDirectoryPath = args[2];
         */
        // maybe tou will need \\
        String csvDataFilePath = "input/casino_gaming_results.csv",
                reportXmlFilePath = "input/DailyBetWinLossReport.xml", outputDirectoryPath = "my_output";
        Report report = null;
        try {
            report = XmlParser.parseReport(reportXmlFilePath);
            makeReport(csvDataFilePath, report);
        } catch (JAXBException e) {
            System.err.println("Parsing of the xml file failed:");
            throw new RuntimeException(e);
        }
        // TODO: Implement logic

    }

    private static void makeReport(String csvDataFilePath, Report report) {
        File dataFile = new File(csvDataFilePath);
        if (dataFile.exists() == false) {
            System.err.println("csv file wasn't found");
            System.exit(1);
        }
        try (BufferedReader fileReader = new BufferedReader(new FileReader(dataFile))) {
            List<Map<String, Object>> result = new ArrayList<Map<String, Object>>();
            List<String> fieldNames = new ArrayList<String>();
            String lineText = null;
            int lineNum = 0, max = Integer.MAX_VALUE;
            while ((lineText = fileReader.readLine()) != null) {
                if (lineNum >= max) {
                    break;
                }
                lineNum++;
                // Arrays.stream(line.split(",")).forEach(s -> System.out.print(s + "|"));
                List<String> values = Arrays.asList(lineText.split(","));// get the values from the line of the CSV file
                if (lineNum == 1) {
                    fieldNames = values;
                    continue;
                }
                Map<String, Object> row = new TreeMap<String, Object>();
                for (int i = 0; i < values.size(); i++) {
                    row.put(fieldNames.get(i), values.get(i));// received values put to the Map
                    // rows.get(0).put(report.getInputs().get(i).getName(), values.get(i));//
                }
                // System.out.println(mapToJson(row));
                // List<Map<String, Object>> rows = new ArrayList<Map<String, Object>>();// for transform // delete
                result.add(row);// put one element

                // System.out.println("-----");
                
            }
            report.getTransformers().forEach(t -> t.transform(report, result));
            System.out.println("-----");
            result.stream().limit(7).map(item -> mapToJsonl(item)).forEach(System.out::println);
            // System.out.println(mapToJsonl(result.get(0)));
        } catch (IOException e) {
            System.err.println("File reading error");
            System.exit(1);
        }
    }
    public static <T, U> String mapToJsonl(Map<T, U> map) {
        return map.entrySet().stream()
                .map(e -> String.format("\"%s\": \"%s\"", e.getKey(), e.getValue()))
                .collect(Collectors.joining(", ", "{ ", " }"));
    }
    public static <T, U> String mapToJson(Map<T, U> map) {
        return map.entrySet().stream()
                .map(e -> String.format("\t\"%s\": \"%s\"", e.getKey(), e.getValue()))
                .collect(Collectors.joining(",\n", "{\n", "\n}"));
    }
}
