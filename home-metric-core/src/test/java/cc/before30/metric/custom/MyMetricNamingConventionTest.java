package cc.before30.metric.custom;

import io.micrometer.core.instrument.Meter;
import io.micrometer.core.instrument.config.NamingConvention;
import org.junit.Test;

public class MyMetricNamingConventionTest {
    private MyMetricNamingConvention convention = new MyMetricNamingConvention();

    @Test
    void name() {
//        assertThat(convention.name("name([{id}])/1", Meter.Type.TIMER)).isEqualTo("name___id____1");
    }

    @Test
    void dotNotationIsConvertedToCamelCase() {
//        assertThat(convention.name("gauge.size", Meter.Type.GAUGE)).isEqualTo("gaugeSize");
    }

    @Test
    void respectDelegateNamingConvention() {
        CustomNamingConvention delegateNamingConvention = new CustomNamingConvention();

//        MyMetricNamingConvention convention = new MyMetricNamingConvention(delegateNamingConvention);

//        assertThat(convention.name("my.name", Meter.Type.TIMER)).isEqualTo("name: my.name");
//        assertThat(convention.tagKey("my.tag.key")).isEqualTo("key: my.tag.key");
//        assertThat(convention.tagValue("my.tag.value")).isEqualTo("value: my.tag.value");
    }

    private static class CustomNamingConvention implements NamingConvention {

        @Override
        public String name(String name, Meter.Type type, String baseUnit) {
            return "name: " + name;
        }

        @Override
        public String tagKey(String key) {
            return "key: " + key;
        }

        @Override
        public String tagValue(String value) {
            return "value: " + value;
        }

    }
}
