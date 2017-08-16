package eu.streamline.hackathon.spark.job;

import eu.streamline.hackathon.common.data.GDELTEvent;
import eu.streamline.hackathon.spark.scala.operations.GDELTInputReceiver;
import org.apache.flink.api.java.utils.ParameterTool;
import org.apache.spark.SparkConf;
import org.apache.spark.streaming.Duration;
import org.apache.spark.streaming.api.java.JavaReceiverInputDStream;
import org.apache.spark.streaming.api.java.JavaStreamingContext;

/**
 * @author behrouz
 */
public class SparkJavaJob {

    public static void main(String[] args) throws InterruptedException {

        ParameterTool params = ParameterTool.fromArgs(args);
        // path to the file
        final String pathToGDELT = params.get("path");
        // micro-batch-duration in milliseconds
        final Long duration = params.getLong("micro-batch-duration", 1000);

        SparkConf conf = new SparkConf().setAppName("GDELT Spark Java Analyzer");
        String masterURL = conf.get("spark.master", "local[2]");
        conf.setMaster(masterURL);

        JavaStreamingContext jssc = new JavaStreamingContext(conf, new Duration(duration));

        JavaReceiverInputDStream<GDELTEvent> source = jssc
                .receiverStream(new GDELTInputReceiver(pathToGDELT));

        source.print();

        jssc.start();
        jssc.awaitTermination();

    }
}
