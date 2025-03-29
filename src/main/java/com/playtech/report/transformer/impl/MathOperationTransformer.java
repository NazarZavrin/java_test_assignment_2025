package com.playtech.report.transformer.impl;

import com.playtech.report.Report;
import com.playtech.report.column.Column;
import com.playtech.report.transformer.Transformer;
import com.playtech.report.transformer.impl.MathOperationTransformer.MathOperation;

import java.util.List;
import java.util.Map;

public class MathOperationTransformer implements Transformer {
    public final static String NAME = "MathOperation";
    List<Column> inputs;
    MathOperation operation;
    Column output;

    // TODO: Implement transformer logic
    public MathOperationTransformer(List<Column> inputs, MathOperation operation, Column output) {
        this.inputs = inputs;
        this.operation = operation;
        this.output = output;
    }

    @Override
    public void transform(Report report, List<Map<String, Object>> rows) {
        for (int i = 0; i < rows.size(); i++) {
            Map<String, Object> row = rows.get(i);
            // Object[] columnValues = inputs.stream().map(col ->
            // row.get(col.getName())).toArray();
            Object[] columnValues = new Object[inputs.size()];
            for (int j = 0; j < inputs.size(); j++) {
                Column input = inputs.get(j);
                String columnName = input.getName();// input field name
                Object columnValue = row.get(columnName);// input field value
                Column.DataType columnType = input.getType();
                switch (columnType) {
                    case Column.DataType.DOUBLE:
                        columnValue = columnValue instanceof String ? Double.parseDouble((String) columnValue)
                                : columnValue;
                        break;
                    case Column.DataType.INTEGER:
                        columnValue = columnValue instanceof String ? Integer.parseInt((String) columnValue)
                                : columnValue;
                        break;
                    default:
                        break;
                }
                if (j == 0) {
                    columnValues[j] = columnValue;
                    continue;
                }
                switch (operation) {
                    case ADD:
                        columnValues[0] = (Double) columnValues[0] + (Double) columnValue;
                        break;
                    case SUBTRACT:
                        columnValues[0] = (Double) columnValues[0] - (Double) columnValue;
                        break;
                    default:
                        break;
                }
                switch (columnType) {
                    case Column.DataType.DOUBLE:
                        columnValues[0] = (Double) columnValues[0];
                        break;
                    case Column.DataType.INTEGER:
                        columnValues[0] = (Integer) columnValues[0];
                        break;
                    default:
                        break;
                }
            }
            row.put(output.getName(), columnValues[0]);
        }
    }

    public enum MathOperation {
        ADD,
        SUBTRACT,
    }
}
