package eu.streamline.hackathon.spark.scala.operations

import java.text.SimpleDateFormat
import java.util.{Calendar, TimeZone}

import org.scalatest.{BeforeAndAfterEach, FunSuite}

/**
  * @author behrouz
  */
class DateTest extends FunSuite with BeforeAndAfterEach {

  test("First Day of the Week") {
    val cal = Calendar.getInstance()
    cal.set(Calendar.HOUR_OF_DAY, 0) // ! clear would not reset the hour of day !
    cal.clear(Calendar.MINUTE)
    cal.clear(Calendar.SECOND)
    cal.clear(Calendar.MILLISECOND)
    cal.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY)
    val format = new SimpleDateFormat("yyyy-MM-dd")
    println(format.format(cal.getTime))
  }

}
