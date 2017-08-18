package eu.streamline.hackathon.flink.scala.job

import java.util.Date

import eu.streamline.hackathon.common.data.GDELTEvent
import eu.streamline.hackathon.flink.operations.GDELTInputFormat
import org.apache.flink.api.common.functions.FoldFunction
import org.apache.flink.api.java.utils.ParameterTool
import org.apache.flink.core.fs.Path
import org.apache.flink.streaming.api.TimeCharacteristic
import org.apache.flink.streaming.api.functions.timestamps.BoundedOutOfOrdernessTimestampExtractor
import org.apache.flink.streaming.api.scala._
import org.apache.flink.streaming.api.scala.function.WindowFunction
import org.apache.flink.streaming.api.windowing.assigners.TumblingEventTimeWindows
import org.apache.flink.streaming.api.windowing.time.Time
import org.apache.flink.streaming.api.windowing.windows.TimeWindow
import org.apache.flink.util.Collector

object FlinkScalaJob {

  def main(args: Array[String]): Unit = {

    val parameters = ParameterTool.fromArgs(args)
    val pathToGDELT = parameters.get("path")
    val country = parameters.get("country", "USA")

    val env = StreamExecutionEnvironment.getExecutionEnvironment
    env.setStreamTimeCharacteristic(TimeCharacteristic.EventTime)

    implicit val typeInfo = createTypeInformation[GDELTEvent]
    implicit val dateInfo = createTypeInformation[Date]

    val source = env
      .readFile[GDELTEvent](new GDELTInputFormat(new Path(pathToGDELT)), pathToGDELT)
      .setParallelism(1)

    source.filter((event: GDELTEvent) => {
        event.actor1Code_countryCode != null &
      event.actor1Code_countryCode == country
    }).assignTimestampsAndWatermarks(new BoundedOutOfOrdernessTimestampExtractor[GDELTEvent](Time.seconds(0)) {
      override def extractTimestamp(element: GDELTEvent): Long = {
        element.dateAdded.getTime
      }
    }).keyBy((event: GDELTEvent) => {
      event.actor1Code_countryCode
    }).window(TumblingEventTimeWindows.of(Time.days(1)))
    .fold(
        0.0,
        new FoldFunction[GDELTEvent, Double] {
          override def fold(accumulator: Double, value: GDELTEvent) = {
            accumulator + value.avgTone
          }
        },
        new WindowFunction[Double, (String, Double, Date, Date), String, TimeWindow] {
          override def apply(key: String,
                             window: TimeWindow,
                             input: Iterable[Double],
                             out: Collector[(String, Double, Date, Date)]): Unit = {
            out.collect((key, input.head, new Date(window.getStart), new Date(window.getEnd)))
          }
        }
    ).print

    env.execute("Flink Scala GDELT Analyzer")

  }

}
