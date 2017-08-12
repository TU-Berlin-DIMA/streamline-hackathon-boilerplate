package eu.streamline.hackathon.flink.scala.job

import java.util.Date

import eu.streamline.hackathon.flink.operations.GDELTInputFormat
import eu.streamline.hackathon.common.data.GDELTEvent
import org.apache.flink.api.java.utils.ParameterTool
import org.apache.flink.core.fs.Path
import org.apache.flink.streaming.api.TimeCharacteristic
import org.apache.flink.streaming.api.functions.AssignerWithPunctuatedWatermarks
import org.apache.flink.streaming.api.scala._
import org.apache.flink.streaming.api.watermark.Watermark

object FlinkScalaJob {

  def main(args: Array[String]): Unit = {

    val parameters = ParameterTool.fromArgs(args)
    val pathToGDELT = parameters.get("path")

    val env = StreamExecutionEnvironment.getExecutionEnvironment
    env.setStreamTimeCharacteristic(TimeCharacteristic.EventTime)

    implicit val typeInfo = createTypeInformation[GDELTEvent]

    val source = env
      .readFile[GDELTEvent](new GDELTInputFormat(new Path(pathToGDELT)), pathToGDELT)
      .setParallelism(1)
      .assignTimestampsAndWatermarks(new AssignerWithPunctuatedWatermarks[GDELTEvent] {
        override def checkAndGetNextWatermark(lastElement: GDELTEvent, extractedTimestamp: Long): Watermark = null

        override def extractTimestamp(element: GDELTEvent, previousElementTimestamp: Long): Long = {
          element.day match {
            case ts: Date => ts.getTime
            case _ => previousElementTimestamp
          }
        }
      })

    source.print()

    env.execute("GDELT Scala Analyzer")

  }

}
