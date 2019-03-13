package cc.before30.metric.custom;

import com.codahale.metrics.MetricRegistry;
import io.micrometer.core.instrument.Clock;
import io.micrometer.core.instrument.dropwizard.DropwizardClock;
import io.micrometer.core.instrument.dropwizard.DropwizardMeterRegistry;
import io.micrometer.core.instrument.util.HierarchicalNameMapper;
import io.micrometer.core.lang.Nullable;

import java.net.InetSocketAddress;
import java.util.concurrent.TimeUnit;

public class MyMetricMeterRegistry extends DropwizardMeterRegistry {
    private final MyMetricConfig config;
    private final MyMetricReporter reporter;

    public MyMetricMeterRegistry(MyMetricConfig config, Clock clock) {
        this(config, clock, new MyMetricHierarchicalNameMapper(config.tagsAsPrefix()));
    }

    public MyMetricMeterRegistry(MyMetricConfig config, Clock clock, HierarchicalNameMapper nameMapper) {
        this(config, clock, nameMapper, new MetricRegistry());
    }

    public MyMetricMeterRegistry(MyMetricConfig config, Clock clock, HierarchicalNameMapper nameMapper,
                                 MetricRegistry metricRegistry) {
        this(config, clock, nameMapper, metricRegistry, defaultGraphiteReporter(config, clock, metricRegistry));
    }

    public MyMetricMeterRegistry(MyMetricConfig config, Clock clock, HierarchicalNameMapper nameMapper,
                                 MetricRegistry metricRegistry, MyMetricReporter reporter) {
        super(config, metricRegistry, nameMapper, clock);

        this.config = config;
        config().namingConvention(new MyMetricNamingConvention());
        this.reporter = reporter;

        start();
    }

    private static MyMetricReporter defaultGraphiteReporter(MyMetricConfig config, Clock clock, MetricRegistry metricRegistry) {
        return MyMetricReporter.forRegistry(metricRegistry)
                .withClock(new DropwizardClock(clock))
                .convertRatesTo(config.rateUnits())
                .convertDurationsTo(config.durationUnits())
                .build(getGraphiteSender(config));
    }

    private static MyMetricSender getGraphiteSender(MyMetricConfig config) {
        InetSocketAddress address = new InetSocketAddress(config.host(), config.port());
        switch (config.protocol()) {
            case JSON:
            default:
                return new MyMetric(address);
        }
    }

    public void stop() {
        if (config.enabled()) {
            reporter.stop();
        }
    }

    public void start() {
        if (config.enabled()) {
            reporter.start(config.step().getSeconds(), TimeUnit.SECONDS);
        }
    }

    @Override
    public void close() {
        if (config.enabled()) {
            reporter.report();
        }
        stop();
        if (config.enabled()) {
            reporter.close();
        }
        super.close();
    }

    @Override
    @Nullable
    protected Double nullGaugeValue() {
        return null;
    }
}
