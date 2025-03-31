package com.playtech.report.transformer.impl;

import com.playtech.report.Report;
import com.playtech.report.column.Column;
import com.playtech.report.transformer.Transformer;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlIDREF;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;

public class AggregatorTransformer implements Transformer {
    public static final String NAME = "Aggregator";
    Column groupByColumn;
    List<AggregateBy> aggregateColumns;

    public AggregatorTransformer(Column groupByColumn, List<AggregateBy> aggregateColumns) {
        this.groupByColumn = groupByColumn;
        this.aggregateColumns = aggregateColumns;
    }

    @Override
    public void transform(Report report, List<Map<String, Object>> rows) {
        List<Map<String, Object>> result = new ArrayList<Map<String, Object>>();
        // Map<Object,List<Map<String,Object>>> groups =
        // rows.stream().collect(Collectors.groupingBy(row ->
        // row.get(groupByColumn.getName()), Collectors.toList()));
        int rowIndex = 0;
        for (; rowIndex < rows.size(); rowIndex++) {
            Map<String, Object> row = rows.get(rowIndex);
            Map<String, Object> newRow = new TreeMap<String, Object>();
            boolean groupByValueWasFound = false;
            for (int newRowIndex = 0; newRowIndex < result.size(); newRowIndex++) {
                if (result.get(newRowIndex).get(this.groupByColumn.getName()).equals(row.get(this.groupByColumn.getName()))) {
                    newRow = result.get(newRowIndex);
                    groupByValueWasFound = true;
                    break;
                }
            }
            newRow.put(this.groupByColumn.getName(), row.get(this.groupByColumn.getName()));
            for (int aggColIndex = 0; aggColIndex < this.aggregateColumns.size(); aggColIndex++) {
                AggregateBy aggCol = this.aggregateColumns.get(aggColIndex);
                Object columnValue = row.get(aggCol.input.getName());
                if (columnValue == null) {
                    continue;
                }
                Column.DataType columnType = aggCol.input.getType();
                switch (columnType) {
                    case Column.DataType.DOUBLE: {
                        columnValue = columnValue instanceof String
                                ? Double.parseDouble((String) columnValue)
                                : columnValue == null ? 0.0 : columnValue;
                        Double prev = (Double) newRow.get(aggCol.getOutput().getName());
                        prev = prev == null ? 0.0 : prev;// if prev was null it was converted to 0.0
                        prev += (Double) columnValue;
                        if (aggCol.output.getType() == Column.DataType.INTEGER) {
                            newRow.put(aggCol.getOutput().getName(), Math.round(prev));
                        } else {
                            newRow.put(aggCol.getOutput().getName(), prev);
                        }
                        break;
                    }
                    case Column.DataType.INTEGER: {
                        columnValue = columnValue instanceof String
                                ? Integer.parseInt((String) columnValue)
                                : columnValue == null ? 0 : columnValue;
                        Integer prev = (Integer) newRow.get(aggCol.getOutput().getName());
                        prev = prev == null ? 0 : prev;// if prev was null it was converted to 0
                        prev += (Integer) columnValue;
                        if (aggCol.output.getType() == Column.DataType.DOUBLE) {
                            newRow.put(aggCol.getOutput().getName(), (double) prev);
                        } else {
                            newRow.put(aggCol.getOutput().getName(), prev);
                        }
                        break;
                    }
                    default:
                        break;
                }
            }
            if (groupByValueWasFound == false) {
                result.add(newRow);
            }
        }
        // !!! AVG result divide by rowIndex
        // System.out.println(result.stream().filter(map -> map.get("StartDate").equals("2021-04-15")).toList());
        // System.out.println(result.stream().limit(7).toList());
        rows.clear();
        result.forEach(row -> rows.add(row));
    }

    @XmlAccessorType(XmlAccessType.FIELD)
    public static class AggregateBy {
        @XmlIDREF
        private Column input;
        private Method method;
        @XmlIDREF
        private Column output;

        public AggregateBy(Column input, Method method, Column output) {
            this.input = input;
            this.method = method;
            this.output = output;
        }

        public AggregateBy() {
        }

        public Column getInput() {
            return input;
        }

        public Column getOutput() {
            return output;
        }

        public Method getMethod() {
            return method;
        }
    }

    public enum Method {
        SUM,
        AVG
    }

}
