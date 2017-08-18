package eu.streamline.hackathon.spark.scala.job

import java.text.SimpleDateFormat
import java.util.{Calendar, Date}

import eu.streamline.hackathon.spark.scala.operations.GDELTInputReceiver
import org.apache.flink.api.java.utils.ParameterTool
import org.apache.spark.SparkConf
import org.apache.spark.streaming.{Duration, State, StateSpec, StreamingContext}


object SparkScalaJob {

  def main(args: Array[String]): Unit = {

    val params = ParameterTool.fromArgs(args)
    val pathToGDELT = params.get("path")
    val duration = params.getLong("micro-batch-duration", 1000)
    val country = params.get("country", "USA")

    val conf = new SparkConf().setAppName("Spark Scala GDELT Analyzer")
    val masterURL = conf.get("spark.master", "local[*]")
    conf.setMaster(masterURL)

    val ssc = new StreamingContext(conf, Duration(duration))
    ssc.checkpoint("checkpoint/")

    val mappingFunc = (weekOfYear: Date, avgTone: Option[Double], state: State[Double]) => {
      val avg = (avgTone.getOrElse(0.0) + state.getOption.getOrElse(0.0)) / 2.0
      val output = (weekOfYear, avg)
      state.update(avg)
      output
    }

    val source = ssc.receiverStream(new GDELTInputReceiver(pathToGDELT))
    source
      .filter(event => event.actor1Code_countryCode != null & event.actor1Code_countryCode == country)
      .map(
        event => {
          val cal = Calendar.getInstance()
          cal.setTime(event.dateAdded)
          cal.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY)

          (cal.getTime, event.avgTone.toDouble)
        }
      )
      .reduceByKey((t1, t2) => t1 + t2)
      .mapWithState(StateSpec.function(mappingFunc))
      .map(event => {
        val dateFormat = new SimpleDateFormat("yyyy-MM-dd")
        s"Country($country), Week(${dateFormat.format(event._1)}), AvgTone(${event._2}))"
      })
      .print()


    ssc.start()
    ssc.awaitTermination()
  }

}
