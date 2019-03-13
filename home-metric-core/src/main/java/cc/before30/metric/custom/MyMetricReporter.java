package cc.before30.metric.custom;

import com.codahale.metrics.Timer;
import com.codahale.metrics.*;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.*;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import static com.codahale.metrics.MetricAttribute.*;

@Slf4j
public class MyMetricReporter extends ScheduledReporter {
    public static MyMetricReporter.Builder forRegistry(MetricRegistry registry) {
        return new MyMetricReporter.Builder(registry);
    }

    public static class Builder {
        private final MetricRegistry registry;
        private Clock clock;
        private String prefix;
        private TimeUnit rateUnit;
        private TimeUnit durationUnit;
        private MetricFilter filter;
        private ScheduledExecutorService executor;
        private boolean shutdownExecutorOnStop;
        private Set<MetricAttribute> disabledMetricAttributes;

        private Builder(MetricRegistry registry) {
            this.registry = registry;
            this.clock = Clock.defaultClock();
            this.prefix = null;
            this.rateUnit = TimeUnit.SECONDS;
            this.durationUnit = TimeUnit.MILLISECONDS;
            this.filter = MetricFilter.ALL;
            this.executor = null;
            this.shutdownExecutorOnStop = true;
            this.disabledMetricAttributes = Collections.emptySet();
        }

        public MyMetricReporter.Builder shutdownExecutorOnStop(boolean shutdownExecutorOnStop) {
            this.shutdownExecutorOnStop = shutdownExecutorOnStop;
            return this;
        }

        public MyMetricReporter.Builder scheduleOn(ScheduledExecutorService executor) {
            this.executor = executor;
            return this;
        }

        public MyMetricReporter.Builder withClock(Clock clock) {
            this.clock = clock;
            return this;
        }

        public MyMetricReporter.Builder prefixedWith(String prefix) {
            this.prefix = prefix;
            return this;
        }

        public MyMetricReporter.Builder convertRatesTo(TimeUnit rateUnit) {
            this.rateUnit = rateUnit;
            return this;
        }

        public MyMetricReporter.Builder convertDurationsTo(TimeUnit durationUnit) {
            this.durationUnit = durationUnit;
            return this;
        }

        public MyMetricReporter.Builder filter(MetricFilter filter) {
            this.filter = filter;
            return this;
        }

        public MyMetricReporter.Builder disabledMetricAttributes(Set<MetricAttribute> disabledMetricAttributes) {
            this.disabledMetricAttributes = disabledMetricAttributes;
            return this;
        }

        public MyMetricReporter build(MyMetric myMetric) {
            return build((MyMetricSender) myMetric);
        }

        public MyMetricReporter build(MyMetricSender myMetric) {
            return new MyMetricReporter(registry,
                    myMetric,
                    clock,
                    prefix,
                    rateUnit,
                    durationUnit,
                    filter,
                    executor,
                    shutdownExecutorOnStop,
                    disabledMetricAttributes);
        }
    }

    private final MyMetricSender myMetric;
    private final Clock clock;
    private final String prefix;

    protected MyMetricReporter(MetricRegistry registry,
                               MyMetricSender myMetric,
                               Clock clock,
                               String prefix,
                               TimeUnit rateUnit,
                               TimeUnit durationUnit,
                               MetricFilter filter,
                               ScheduledExecutorService executor,
                               boolean shutdownExecutorOnStop,
                               Set<MetricAttribute> disabledMetricAttributes) {
        super(registry, "graphite-reporter", filter, rateUnit, durationUnit, executor, shutdownExecutorOnStop,
                disabledMetricAttributes);
        this.myMetric = myMetric;
        this.clock = clock;
        this.prefix = prefix;
    }

    @Override
    @SuppressWarnings("rawtypes")
    public void report(SortedMap<String, Gauge> gauges,
                       SortedMap<String, Counter> counters,
                       SortedMap<String, Histogram> histograms,
                       SortedMap<String, Meter> meters,
                       SortedMap<String, Timer> timers) {
        final long timestamp = clock.getTime() / 1000;

        // oh it'd be lovely to use Java 7 here
        try {
            myMetric.connect();

            for (Map.Entry<String, Gauge> entry : gauges.entrySet()) {
                reportGauge(entry.getKey(), entry.getValue(), timestamp);
            }

            for (Map.Entry<String, Counter> entry : counters.entrySet()) {
                reportCounter(entry.getKey(), entry.getValue(), timestamp);
            }

            for (Map.Entry<String, Histogram> entry : histograms.entrySet()) {
                reportHistogram(entry.getKey(), entry.getValue(), timestamp);
            }

            for (Map.Entry<String, Meter> entry : meters.entrySet()) {
                reportMetered(entry.getKey(), entry.getValue(), timestamp);
            }

            for (Map.Entry<String, Timer> entry : timers.entrySet()) {
                reportTimer(entry.getKey(), entry.getValue(), timestamp);
            }
            myMetric.flush();
        } catch (IOException e) {
            log.warn("Unable to report to Graphite", myMetric, e);
        } finally {
            try {
                myMetric.close();
            } catch (IOException e1) {
                log.warn("Error closing Graphite", myMetric, e1);
            }
        }
    }

    @Override
    public void stop() {
        try {
            super.stop();
        } finally {
            try {
                myMetric.close();
            } catch (IOException e) {
                log.debug("Error disconnecting from Graphite", myMetric, e);
            }
        }
    }

    private void reportTimer(String name, Timer timer, long timestamp) throws IOException {
        final Snapshot snapshot = timer.getSnapshot();
        sendIfEnabled(MAX, name, convertDuration(snapshot.getMax()), timestamp);
        sendIfEnabled(MEAN, name, convertDuration(snapshot.getMean()), timestamp);
        sendIfEnabled(MIN, name, convertDuration(snapshot.getMin()), timestamp);
        sendIfEnabled(STDDEV, name, convertDuration(snapshot.getStdDev()), timestamp);
        sendIfEnabled(P50, name, convertDuration(snapshot.getMedian()), timestamp);
        sendIfEnabled(P75, name, convertDuration(snapshot.get75thPercentile()), timestamp);
        sendIfEnabled(P95, name, convertDuration(snapshot.get95thPercentile()), timestamp);
        sendIfEnabled(P98, name, convertDuration(snapshot.get98thPercentile()), timestamp);
        sendIfEnabled(P99, name, convertDuration(snapshot.get99thPercentile()), timestamp);
        sendIfEnabled(P999, name, convertDuration(snapshot.get999thPercentile()), timestamp);
        reportMetered(name, timer, timestamp);
    }

    private void reportMetered(String name, Metered meter, long timestamp) throws IOException {
        sendIfEnabled(COUNT, name, meter.getCount(), timestamp);
        sendIfEnabled(M1_RATE, name, convertRate(meter.getOneMinuteRate()), timestamp);
        sendIfEnabled(M5_RATE, name, convertRate(meter.getFiveMinuteRate()), timestamp);
        sendIfEnabled(M15_RATE, name, convertRate(meter.getFifteenMinuteRate()), timestamp);
        sendIfEnabled(MEAN_RATE, name, convertRate(meter.getMeanRate()), timestamp);
    }

    private void reportHistogram(String name, Histogram histogram, long timestamp) throws IOException {
        final Snapshot snapshot = histogram.getSnapshot();
        sendIfEnabled(COUNT, name, histogram.getCount(), timestamp);
        sendIfEnabled(MAX, name, snapshot.getMax(), timestamp);
        sendIfEnabled(MEAN, name, snapshot.getMean(), timestamp);
        sendIfEnabled(MIN, name, snapshot.getMin(), timestamp);
        sendIfEnabled(STDDEV, name, snapshot.getStdDev(), timestamp);
        sendIfEnabled(P50, name, snapshot.getMedian(), timestamp);
        sendIfEnabled(P75, name, snapshot.get75thPercentile(), timestamp);
        sendIfEnabled(P95, name, snapshot.get95thPercentile(), timestamp);
        sendIfEnabled(P98, name, snapshot.get98thPercentile(), timestamp);
        sendIfEnabled(P99, name, snapshot.get99thPercentile(), timestamp);
        sendIfEnabled(P999, name, snapshot.get999thPercentile(), timestamp);
    }

    private void sendIfEnabled(MetricAttribute type, String name, double value, long timestamp) throws IOException {
        if (getDisabledMetricAttributes().contains(type)) {
            return;
        }
        myMetric.send(prefix(name, type.getCode()), format(value), timestamp);
    }

    private void sendIfEnabled(MetricAttribute type, String name, long value, long timestamp) throws IOException {
        if (getDisabledMetricAttributes().contains(type)) {
            return;
        }
        myMetric.send(prefix(name, type.getCode()), format(value), timestamp);
    }

    private void reportCounter(String name, Counter counter, long timestamp) throws IOException {
        myMetric.send(prefix(name, COUNT.getCode()), format(counter.getCount()), timestamp);
    }

    private void reportGauge(String name, Gauge<?> gauge, long timestamp) throws IOException {
        final String value = format(gauge.getValue());
        if (value != null) {
            myMetric.send(prefix(name), value, timestamp);
        }
    }

    private String format(Object o) {
        if (o instanceof Float) {
            return format(((Float) o).doubleValue());
        } else if (o instanceof Double) {
            return format(((Double) o).doubleValue());
        } else if (o instanceof Byte) {
            return format(((Byte) o).longValue());
        } else if (o instanceof Short) {
            return format(((Short) o).longValue());
        } else if (o instanceof Integer) {
            return format(((Integer) o).longValue());
        } else if (o instanceof Long) {
            return format(((Long) o).longValue());
        } else if (o instanceof BigInteger) {
            return format(((BigInteger) o).doubleValue());
        } else if (o instanceof BigDecimal) {
            return format(((BigDecimal) o).doubleValue());
        } else if (o instanceof Boolean) {
            return format(((Boolean) o) ? 1 : 0);
        }
        return null;
    }

    private String prefix(String... components) {
        return MetricRegistry.name(prefix, components);
    }

    private String format(long n) {
        return Long.toString(n);
    }

    protected String format(double v) {
        // the Carbon plaintext format is pretty underspecified, but it seems like it just wants
        // US-formatted digits
        return String.format(Locale.US, "%2.2f", v);
    }
}
