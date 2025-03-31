package com.playtech.report.transformer.impl;

import com.playtech.report.Report;
import com.playtech.report.column.Column;
import com.playtech.report.column.Column.DataType;
import com.playtech.report.transformer.Transformer;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class StringFormatterTransformer implements Transformer {
    public final static String NAME = "StringFormatter";
    List<Column> inputs;
    String format;
    Column output;

    // TODO: Implement transformer logic
    public StringFormatterTransformer(List<Column> inputs, String format, Column output) {
        this.inputs = inputs;
        this.format = format;
        this.output = output;
    }

    @Override
    public void transform(Report report, List<Map<String, Object>> rows) {
        for (int i = 0; i < rows.size(); i++) {
            Map<String, Object> row = rows.get(i);
            Object[] columnValues = inputs.stream().map(col -> row.get(col.getName())).filter(x -> x != null).toArray();
            for (int j = 0; j < inputs.size(); j++) {
                Column input = inputs.get(j);
                String columnName = input.getName();// input field name
                Object columnValue = row.get(columnName);// input field value
                if (columnValue == null) {
                    continue;
                }
                // System.out.println(columnName);
                Column.DataType columnType = input.getType();
                switch (columnType) {
                    case Column.DataType.DOUBLE:
                        columnValue = columnValue.getClass() == String.class ? Double.parseDouble((String) columnValue)
                                : columnValue;
                        break;
                    case Column.DataType.INTEGER:
                        columnValue = columnValue.getClass() == String.class ? Integer.parseInt((String) columnValue)
                                : columnValue;
                        break;
                    default:
                        break;
                }
                // String formatted = String.format(format, columnValue);
                // row.put(output.getName(), formatted);
            }
            if (columnValues.length > 0) {
                String formatted = String.format(format, columnValues);
                row.put(output.getName(), formatted);
            }
        }
        // return rows;
    }
}
