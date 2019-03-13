package cc.before30.metric.custom;

import io.micrometer.core.instrument.dropwizard.DropwizardConfig;
import io.micrometer.core.lang.Nullable;

import java.util.concurrent.TimeUnit;

public interface MyMetricConfig extends DropwizardConfig {

    MyMetricConfig DEFAULT = k -> null;

    @Nullable
    String get(String key);

    default String prefix() {
        return "graphite";
    }

    default String[] tagsAsPrefix() {
        return new String[0];
    }

    default TimeUnit rateUnits() {
        String v = get(prefix() + ".rateUnits");
        return v == null ? TimeUnit.SECONDS : TimeUnit.valueOf(v.toUpperCase());
    }

    default TimeUnit durationUnits() {
        String v = get(prefix() + ".durationUnits");
        return v == null ? TimeUnit.MILLISECONDS : TimeUnit.valueOf(v.toUpperCase());
    }

    default String host() {
        String v = get(prefix() + ".host");
        return (v == null) ? "localhost" : v;
    }

    default int port() {
        String v = get(prefix() + ".port");
        return (v == null) ? 2004 : Integer.parseInt(v);
    }

    default boolean enabled() {
        String v = get(prefix() + ".enabled");
        return v == null || Boolean.valueOf(v);
    }

    default MyMetricProtocol protocol() {
        String v = get(prefix() + ".protocol");

        if (v == null)
            return MyMetricProtocol.JSON;

        for (MyMetricProtocol flavor : MyMetricProtocol.values()) {
            if (flavor.toString().equalsIgnoreCase(v))
                return flavor;
        }

        throw new IllegalArgumentException("Unrecognized graphite protocol '" + v + "' (check property " + prefix() + ".protocol)");
    }
}
