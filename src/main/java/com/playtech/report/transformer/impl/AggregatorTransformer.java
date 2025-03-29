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
import java.util.stream.Collectors;

public class AggregatorTransformer implements Transformer {
    public static final String NAME = "Aggregator";
    Column groupByColumn;
    List<AggregateBy> aggregateColumns;

    // TODO: Implement transformer logic
    public AggregatorTransformer(Column groupByColumn, List<AggregateBy> aggregateColumns) {
        /*this.groupByColumn = groupByColumn;
        this.aggregateColumns = aggregateColumns;*/
    }
    /*public AggregatorTransformer(){}*/

    @Override
    public void transform(Report report, List<Map<String, Object>> rows) {
        
        /*List<Map<String, Object>> result = new ArrayList<Map<String, Object>>();
        
        for (int i = 0; i < rows.size(); i++) {
            
        }
        // System.out.println(rows.stream().collect(Collectors.groupingBy(row -> row.get("Device"), Collectors.toList())).get("Desktop"));
        System.exit(2);*/

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
        public AggregateBy(){}

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
