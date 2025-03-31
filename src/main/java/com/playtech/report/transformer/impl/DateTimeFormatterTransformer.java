package com.playtech.report.transformer.impl;

import com.playtech.report.Report;
import com.playtech.report.column.Column;
import com.playtech.report.column.Column.DataType;
import com.playtech.report.transformer.Transformer;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;

public class DateTimeFormatterTransformer implements Transformer {
    public static final String NAME = "DateTimeFormatter";
    Column input;
    String format;
    Column output;

    // TODO: Implement transformer logic
    public DateTimeFormatterTransformer(Column input, String format, Column output) {
        this.input = input;
        this.format = format;
        this.output = output;
    }

    @Override
    public void transform(Report report, List<Map<String, Object>> rows) {
        for (int i = 0; i < rows.size(); i++) {
            Map<String, Object> row = rows.get(i);
            Object columnValue = row.get(input.getName());
            if (columnValue == null) {
                continue;
            }
            Column.DataType columnType = input.getType();
            LocalDateTime dateTime = LocalDateTime.now();
            switch (columnType) {
                case Column.DataType.DATETIME:
                    dateTime = columnValue instanceof String 
                            ? LocalDateTime.parse((String) columnValue, DateTimeFormatter.ISO_DATE_TIME)
                            : (LocalDateTime) columnValue;
                    break;
                case Column.DataType.DATE:
                    dateTime = columnValue instanceof String 
                            ? LocalDate.parse((String) columnValue, DateTimeFormatter.ISO_DATE).atStartOfDay()
                            : ((LocalDate) columnValue).atStartOfDay();
                    break;
                default:
                    dateTime = (LocalDateTime) columnValue;
                    break;
            }
            DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern(this.format);
            String formatted = dateTime.format(dateTimeFormatter);
            row.put(output.getName(), formatted);
        }
    }
}
