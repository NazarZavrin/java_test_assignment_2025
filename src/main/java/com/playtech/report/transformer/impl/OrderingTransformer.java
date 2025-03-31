package com.playtech.report.transformer.impl;

import com.playtech.report.Report;
import com.playtech.report.column.Column;
import com.playtech.report.transformer.Transformer;
import com.playtech.report.transformer.impl.OrderingTransformer.Order;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

public class OrderingTransformer implements Transformer {
    public final static String NAME = "Ordering";
    Column input;
    Order order;

    public OrderingTransformer(Column input, Order order) {
        this.input = input;
        this.order = order;
    }

    @Override
    public void transform(Report report, List<Map<String, Object>> rows) {
        for (int i = 0; i < rows.size() - 1; i++) {
            boolean swapped = false;
            for (int j = 0; j < rows.size() - i - 1; j++) {
                Map<String, Object> row = rows.get(j);
                Map<String, Object> nextRow = rows.get(j + 1);
                Object columnValue = row.get(input.getName());
                Object nextColumnValue = nextRow.get(input.getName());
                if (columnValue == null || nextColumnValue == null) {
                    continue;
                }
                Column.DataType columnType = input.getType();
                switch (columnType) {
                    case Column.DataType.DATETIME: {
                        LocalDateTime val = columnValue instanceof String
                                ? LocalDateTime.parse((String) columnValue, DateTimeFormatter.ISO_DATE_TIME)
                                : (LocalDateTime) columnValue;
                        LocalDateTime nextVal = nextColumnValue instanceof String
                                ? LocalDateTime.parse((String) nextColumnValue, DateTimeFormatter.ISO_DATE_TIME)
                                : (LocalDateTime) nextColumnValue;
                        boolean orderIsIncorrect = this.order == Order.ASC ? val.isAfter(nextVal)
                                : val.isBefore(nextVal);
                        if (orderIsIncorrect) {
                            Map<String, Object> temp = row;
                            rows.set(j, nextRow);
                            rows.set(j + 1, temp);
                            swapped = true;
                        }
                        break;
                    }
                    case Column.DataType.DATE: {
                        LocalDate val = columnValue instanceof String
                                ? LocalDate.parse((String) columnValue, DateTimeFormatter.ISO_DATE)
                                : (LocalDate) columnValue;
                        LocalDate nextVal = nextColumnValue instanceof String
                                ? LocalDate.parse((String) nextColumnValue, DateTimeFormatter.ISO_DATE)
                                : (LocalDate) nextColumnValue;
                        boolean orderIsIncorrect = this.order == Order.ASC ? val.isAfter(nextVal)
                                : val.isBefore(nextVal);
                        if (orderIsIncorrect) {
                            Map<String, Object> temp = row;
                            rows.set(j, nextRow);
                            rows.set(j + 1, temp);
                            swapped = true;
                        }
                        break;
                    }
                    default:
                        String val = (String) columnValue;
                        String nextVal = (String) nextColumnValue;
                        boolean orderIsIncorrect = this.order == Order.ASC ? val.compareTo(nextVal) > 0
                                : val.compareTo(nextVal) < 0;
                        if (orderIsIncorrect) {
                            Map<String, Object> temp = row;
                            rows.set(j, nextRow);
                            rows.set(j + 1, temp);
                            swapped = true;
                        }
                        break;
                }
            }
            if (swapped == false) {
                break;
            }
        }
    }

    public enum Order {
        ASC,
        DESC
    }
}
