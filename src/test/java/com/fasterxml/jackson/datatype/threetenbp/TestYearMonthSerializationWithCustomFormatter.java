package com.fasterxml.jackson.datatype.threetenbp;

import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.junit.Assert.assertThat;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.datatype.threetenbp.deser.YearMonthDeserializer;
import com.fasterxml.jackson.datatype.threetenbp.ser.YearMonthSerializer;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

import org.threeten.bp.YearMonth;
import org.threeten.bp.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collection;

@RunWith(Parameterized.class)
public class TestYearMonthSerializationWithCustomFormatter {
    private final DateTimeFormatter formatter;

    public TestYearMonthSerializationWithCustomFormatter(DateTimeFormatter formatter) {
        this.formatter = formatter;
    }

    @Test
    public void testSerialization() throws Exception {
        YearMonth dateTime = YearMonth.now();
        assertThat(serializeWith(dateTime, formatter), containsString(dateTime.format(formatter)));
    }

    private String serializeWith(YearMonth dateTime, DateTimeFormatter f) throws Exception {
        ObjectMapper mapper = new ObjectMapper().registerModule(new SimpleModule()
            .addSerializer(new YearMonthSerializer(f)));
        return mapper.writeValueAsString(dateTime);
    }

    @Test
    public void testDeserialization() throws Exception {
        YearMonth dateTime = YearMonth.now();
        assertThat(deserializeWith(dateTime.format(formatter), formatter), equalTo(dateTime));
    }

    private YearMonth deserializeWith(String json, DateTimeFormatter f) throws Exception {
        ObjectMapper mapper = new ObjectMapper().registerModule(new SimpleModule()
            .addDeserializer(YearMonth.class, new YearMonthDeserializer(f)));
        return mapper.readValue("\"" + json + "\"", YearMonth.class);
    }

    @Parameters
    public static Collection<Object[]> customFormatters() {
        Collection<Object[]> formatters = new ArrayList<Object[]>();
        formatters.add(new Object[]{DateTimeFormatter.ofPattern("uuuu-MM")});
        formatters.add(new Object[]{DateTimeFormatter.ofPattern("uu-M")});
        return formatters;
    }
}
