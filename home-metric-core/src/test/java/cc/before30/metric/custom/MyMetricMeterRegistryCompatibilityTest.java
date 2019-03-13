package cc.before30.metric.custom;

import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.MockClock;
import io.micrometer.core.instrument.util.HierarchicalNameMapper;
import io.micrometer.core.lang.Nullable;
import io.micrometer.core.tck.MeterRegistryCompatibilityKit;

import java.time.Duration;

public class MyMetricMeterRegistryCompatibilityTest extends MeterRegistryCompatibilityKit {
    @Override
    public MeterRegistry registry() {
        return new MyMetricMeterRegistry(new MyMetricConfig() {
            @Override
            public boolean enabled() {
                return false;
            }

            @Override
            @Nullable
            public String get(String key) {
                return null;
            }
        }, new MockClock(), HierarchicalNameMapper.DEFAULT);
    }

    @Override
    public Duration step() {
        return MyMetricConfig.DEFAULT.step();
    }
}
