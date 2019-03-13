package cc.before30.metric.custom;

import io.micrometer.core.instrument.MockClock;
import io.micrometer.core.lang.Nullable;
import io.micrometer.shaded.io.netty.channel.ChannelOption;
import io.micrometer.shaded.reactor.core.publisher.Flux;
import io.micrometer.shaded.reactor.netty.udp.UdpServer;
import org.junit.Assert;
import org.junit.Test;

import java.net.DatagramSocket;
import java.time.Duration;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

public class MyMetricMeterRegistryTest {

    private static final int PORT = findAvailableUdpPort();
    private MockClock mockClock = new MockClock();

    private static int findAvailableUdpPort() {
        for (int port = 1024; port <= 65535; port++) {
            try {
                DatagramSocket socket = new DatagramSocket(port);
                socket.close();
                return port;
            } catch (Exception ignored) {
            }
        }
        throw new RuntimeException("no available UDP port");
    }

    @Test
    void metricPrefixes() throws InterruptedException {
        final CountDownLatch receiveLatch = new CountDownLatch(1);

        final MyMetricMeterRegistry registry = new MyMetricMeterRegistry(new MyMetricConfig() {
            @Override
            @Nullable
            public String get(String key) {
                return null;
            }

            @Override
            public Duration step() {
                return Duration.ofSeconds(1);
            }

            @Override
            public MyMetricProtocol protocol() {
                return MyMetricProtocol.JSON;
            }

            @Override
            public int port() {
                return PORT;
            }

            @Override
            public String[] tagsAsPrefix() {
                return new String[]{"application"};
            }
        }, mockClock);

//        MyMetric server = UdpServer.create()
//                .option(ChannelOption.SO_REUSEADDR, true)
//                .host("localhost")
//                .port(PORT)
//                .handle((in, out) -> {
//                    in.receive()
//                            .asString()
//                            .subscribe(line -> {
//
////                                Assert.assertTrue(line.startsWith("APPNAME.myTimer"));
//                                receiveLatch.countDown();
//                            });
//                    return Flux.never();
//                })
//                .bind()
//                .doOnSuccess(v -> {
//                    registry.timer("my.timer", "application", "APPNAME")
//                            .record(1, TimeUnit.MILLISECONDS);
//                    registry.close();
//                })
//                .block(Duration.ofSeconds(10));
//
//        Assert.assertTrue(receiveLatch.await(10, TimeUnit.SECONDS), "line was received");
//        server.dispose();
    }
}
