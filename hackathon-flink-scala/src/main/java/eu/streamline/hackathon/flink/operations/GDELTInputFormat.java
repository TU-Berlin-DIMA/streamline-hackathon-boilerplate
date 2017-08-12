package eu.streamline.hackathon.flink.operations;

import eu.streamline.hackathon.common.GDELTParser;
import eu.streamline.hackathon.common.data.GDELTEvent;
import org.apache.flink.api.common.io.DelimitedInputFormat;
import org.apache.flink.core.fs.Path;

import java.io.IOException;

public class GDELTInputFormat extends DelimitedInputFormat<GDELTEvent> {

	private static final long serialVersionUID = 1L;

	/**
	 * Code of \r, used to remove \r from a line when the line ends with \r\n.
	 */
	private static final byte CARRIAGE_RETURN = (byte) '\r';

	/**
	 * Code of \n, used to identify if \n is used as delimiter.
	 */
	private static final byte NEW_LINE = (byte) '\n';

	/**
	 * The name of the charset to use for decoding.
	 */
	private String charsetName = "UTF-8";

	private GDELTParser parser;


	public GDELTInputFormat(Path filePath) {
		super(filePath, null);
		parser = new GDELTParser();
	}

	@Override
	public GDELTEvent readRecord(GDELTEvent reuse, byte[] bytes, int offset, int numBytes) throws IOException {
		if (this.getDelimiter() != null && this.getDelimiter().length == 1
				&& this.getDelimiter()[0] == NEW_LINE && offset + numBytes >= 1
				&& bytes[offset + numBytes - 1] == CARRIAGE_RETURN){
			numBytes -= 1;
		}
		String line = new String(bytes, offset, numBytes, charsetName);
		try {
			return parser.readRecord(line);
		} catch (Exception e) {
			throw new IOException(e);
		}
	}
}
