package eu.streamline.hackathon.flink.job;

import eu.streamline.hackathon.common.data.GDELTEvent;
import eu.streamline.hackathon.flink.operations.GDELTInputFormat;
import org.apache.flink.api.common.functions.AggregateFunction;
import org.apache.flink.api.common.functions.FilterFunction;
import org.apache.flink.api.common.functions.MapFunction;
import org.apache.flink.api.java.functions.KeySelector;
import org.apache.flink.api.java.utils.ParameterTool;
import org.apache.flink.core.fs.Path;
import org.apache.flink.streaming.api.TimeCharacteristic;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.functions.AssignerWithPunctuatedWatermarks;
import org.apache.flink.streaming.api.watermark.Watermark;
import org.apache.flink.streaming.api.windowing.time.Time;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nullable;

public class FlinkJavaJob {

	private static final Logger LOG = LoggerFactory.getLogger(FlinkJavaJob.class);

	public static void main(String[] args) {

		ParameterTool params = ParameterTool.fromArgs(args);
		final String pathToGDELT = params.get("path");

		final StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
		env.setStreamTimeCharacteristic(TimeCharacteristic.EventTime);

		DataStream<GDELTEvent> source = env
			.readFile(new GDELTInputFormat(new Path(pathToGDELT)), pathToGDELT).setParallelism(1)
			.assignTimestampsAndWatermarks(new AssignerWithPunctuatedWatermarks<GDELTEvent>() {
				@Override
				public long extractTimestamp(GDELTEvent element, long previousElementTimestamp) {
					return (element.day != null) ? element.day.getTime() : previousElementTimestamp;
				}

				@Nullable
				@Override
				public Watermark checkAndGetNextWatermark(GDELTEvent lastElement, long extractedTimestamp) {
					return null;
				}
			});

		source.print();

		try {
			env.execute("GDELT Analyzer Job");
		} catch (Exception e) {
			LOG.error("Failed to execute Flink job {}", e);
		}



	}

}
