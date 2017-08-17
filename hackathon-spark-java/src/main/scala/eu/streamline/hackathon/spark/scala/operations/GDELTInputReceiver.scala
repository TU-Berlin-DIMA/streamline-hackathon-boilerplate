package eu.streamline.hackathon.spark.scala.operations

import java.io.{BufferedReader, InputStreamReader}
import java.nio.charset.StandardCharsets

import eu.streamline.hackathon.common.GDELTParser
import eu.streamline.hackathon.common.data.GDELTEvent
import org.apache.hadoop.conf.Configuration
import org.apache.hadoop.fs.{FileSystem, Path}
import org.apache.spark.storage.StorageLevel
import org.apache.spark.streaming.receiver.Receiver


class GDELTInputReceiver(path: String)
  extends Receiver[GDELTEvent](StorageLevel.DISK_ONLY_2) {

  override def onStart(): Unit = {
    // Start the thread that receives data over a connection
    new Thread("GDELT File Receiver") {

      override def run() {
        receive()
      }
    }.start()
  }

  override def onStop(): Unit = {

  }

  private def receive(): Unit = {
    val pt = new Path(path)
    val fs = FileSystem.get(new Configuration())
    var line: String = null
    try {
      val reader = new BufferedReader(new InputStreamReader(fs.open(pt), StandardCharsets.UTF_8))

      val parser = new GDELTParser()

      line = reader.readLine()
      while (!isStopped && line != null) {
        store(parser.readRecord(line))
        line = reader.readLine()
      }
      reader.close()
    } catch {
      case e: Exception =>
        restart("Reading Failed, restarting the receiver", e)
    }

  }
}
