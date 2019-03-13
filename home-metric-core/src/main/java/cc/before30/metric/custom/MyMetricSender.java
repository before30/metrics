package cc.before30.metric.custom;

import java.io.Closeable;
import java.io.IOException;

public interface MyMetricSender extends Closeable {

    void connect() throws IllegalStateException, IOException;

    void send(String name, String value, long timestamp) throws IOException;

    void flush() throws IOException;

    boolean isConnected();

    int getFailures();

}