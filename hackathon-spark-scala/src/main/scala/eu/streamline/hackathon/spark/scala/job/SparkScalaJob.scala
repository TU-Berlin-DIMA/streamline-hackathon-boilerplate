package eu.streamline.hackathon.spark.scala.job

import eu.streamline.hackathon.spark.scala.operations.GDELTInputReceiver
import org.apache.flink.api.java.utils.ParameterTool
import org.apache.spark.SparkConf
import org.apache.spark.streaming.{Duration, Milliseconds, StreamingContext}


object SparkScalaJob {

  val DEFAULT_BATCH_DURATION = Milliseconds(500)

  def main(args: Array[String]): Unit = {

    val params = ParameterTool.fromArgs(args)
    // path to the file
    val pathToGDELT = params.get("path")
    // micro-batch-duration in milliseconds
    val duration = params.getLong("micro-batch-duration", 1000)

    val conf = new SparkConf().setAppName("GDELT Spark Scala Analyzer")
    val masterURL = conf.get("spark.master", "local[2]")
    conf.setMaster(masterURL)

    val ssc = new StreamingContext(conf, Duration(duration))

    val source = ssc.receiverStream(new GDELTInputReceiver(pathToGDELT))

    source.count().print()

    ssc.start()
    ssc.awaitTermination()
  }

}
