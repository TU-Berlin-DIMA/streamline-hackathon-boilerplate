package eu.streamline.hackathon.spark;

import eu.streamline.hackathon.common.data.GDELTEvent;
import eu.streamline.hackathon.spark.scala.operations.GDELTInputReceiver;
import org.apache.spark.SparkConf;
import org.apache.spark.streaming.Duration;
import org.apache.spark.streaming.Milliseconds;
import org.apache.spark.streaming.api.java.JavaReceiverInputDStream;
import org.apache.spark.streaming.api.java.JavaStreamingContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author behrouz
 */
public class SparkJavaJob {
    private static final Logger LOG = LoggerFactory.getLogger(SparkJavaJob.class);

    /**
     * Micro Batch Duration
     * Available Durations:
     *
     * @see org.apache.spark.streaming.Milliseconds
     * @see org.apache.spark.streaming.Seconds
     * @see org.apache.spark.streaming.Minutes
     * or Custom:
     * @see org.apache.spark.streaming.Duration
     */
    private static Duration DEFAULT_MICRO_BATCH_DURATION = Milliseconds.apply(500);

    public static void main(String[] args) throws InterruptedException {

        SparkConf conf = new SparkConf()
                .setMaster("local[2]")
                .setAppName("GDELT Spark Java Analyzer");
        JavaStreamingContext jssc = new JavaStreamingContext(conf, DEFAULT_MICRO_BATCH_DURATION);

        String pathToGDELT = args[0];

        JavaReceiverInputDStream<GDELTEvent> source = jssc
                .receiverStream(new GDELTInputReceiver(pathToGDELT));

        source.print();

        jssc.start();
        jssc.awaitTermination();

    }
}
