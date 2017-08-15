package eu.streamline.hackathon.spark.scala.job

import eu.streamline.hackathon.spark.scala.operations.GDELTInputReceiver
import org.apache.flink.api.java.utils.ParameterTool
import org.apache.spark.SparkConf
import org.apache.spark.streaming.{Milliseconds, StreamingContext}


object SparkScalaJob {
  /**
    * Micro Batch Duration
    * Available Durations:
    * [[org.apache.spark.streaming.Milliseconds]]
    * [[org.apache.spark.streaming.Seconds]]
    * [[org.apache.spark.streaming.Minutes]]
    * or Custom [[org.apache.spark.streaming.Duration]]
    */
  val DEFAULT_BATCH_DURATION = Milliseconds(500)

  def main(args: Array[String]): Unit = {

    val conf = new SparkConf()
      // minimum of 2 threads must be used
      .setMaster("local[2]")
      .setAppName("GDELT Spark Scala Analyzer")
    val ssc = new StreamingContext(conf, DEFAULT_BATCH_DURATION)

    val pathToGDELT = args(0)

    val source = ssc.receiverStream(new GDELTInputReceiver(pathToGDELT))

    source.print()

    ssc.start()
    ssc.awaitTermination()
  }

}
