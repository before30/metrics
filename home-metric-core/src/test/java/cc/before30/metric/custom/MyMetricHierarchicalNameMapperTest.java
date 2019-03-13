package cc.before30.metric.custom;

import io.micrometer.core.instrument.Meter;
import io.micrometer.core.instrument.config.NamingConvention;
import io.micrometer.core.instrument.simple.SimpleMeterRegistry;
import org.junit.Assert;
import org.junit.Test;

class MyMetricHierarchicalNameMapperTest {
    private final MyMetricHierarchicalNameMapper nameMapper = new MyMetricHierarchicalNameMapper("stack", "app.name");
    private final Meter.Id id = new SimpleMeterRegistry().counter("my.name",
            "app.name", "MYAPP", "stack", "PROD", "other.tag", "value").getId();

    @Test
    void tagsAsPrefix() {
        Assert.assertTrue(nameMapper.toHierarchicalName(id, NamingConvention.camelCase)
                .equals("PROD.MYAPP.myName.otherTag.value"));
    }

}