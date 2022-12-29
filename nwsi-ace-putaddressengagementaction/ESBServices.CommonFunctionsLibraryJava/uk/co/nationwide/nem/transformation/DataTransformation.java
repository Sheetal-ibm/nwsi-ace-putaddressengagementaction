package uk.co.nationwide.nem.transformation;

import java.nio.ByteBuffer;
import java.text.DateFormat;
import java.text.Format;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.TimeZone;
import java.util.UUID;

public class DataTransformation {

	/**
	 * The length of bytes
	 */
	private final static int LONG_BYTES = 8;

	/**
	 * The UTC time-stamp format for Java convertions
	 */
	private final static String UTCTimestampFormat = "yyyy-MM-dd'T'HH:mm:ss.SSSZ";

	/**
	 * Create a UUID based on a long and a time stamp, which could represent the
	 * message identifier and current date and time, ensuring that the UUID can
	 * be reversed back into the original long information.
	 * 
	 * @param messageId
	 *            The message identifier
	 * @param timeStamp
	 *            The date-time value to be used
	 * @return the UUID which represents the two longs.
	 * @throws ParseException
	 */
	public static UUID createUUIDFromRequestUAC(String messageId,
			String timeStamp) throws ParseException {
		byte[] destination = null;

		try {
			if (messageId == null || messageId.length() == 0) {
				messageId = "0";
			}

			// Process messageId
			ByteBuffer msgBuffer = ByteBuffer.allocate(LONG_BYTES);
			msgBuffer.putLong(Long.parseLong(messageId));
			byte[] valAsBytes = msgBuffer.array();

			// Process time-stamp
			if (timeStamp.length() >= 19) {
				timeStamp = timeStamp.substring(0, 19) + "Z";
			}

			TimeZone tz = TimeZone.getTimeZone("UTC");
			DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssX");
			df.setTimeZone(tz);
			Date dateTime = null;

			dateTime = df.parse(timeStamp);

			ByteBuffer timeBuffer = ByteBuffer.allocate(LONG_BYTES);
			timeBuffer.putLong(dateTime.getTime());
			byte[] timestampBytes = timeBuffer.array();

			destination = new byte[valAsBytes.length + timestampBytes.length];

			// copy valAsBytes into start of destination
			System.arraycopy(valAsBytes, 0, destination, 0, valAsBytes.length);

			// copy timestampBytes into end of destination
			System.arraycopy(timestampBytes, 0, destination, valAsBytes.length,
					timestampBytes.length);

			msgBuffer.clear();
			timeBuffer.clear();
			msgBuffer = null;
			timeBuffer = null;

		} catch (ParseException e) {
			// TODO Auto-generated catch block
			throw e;
		}
		return getUUIDFromBytes(destination);
	}

	/**
	 * Get time embedded message identifier from the created UUID (created by
	 * createUUIDFromLongs)
	 * 
	 * @param uuid
	 *            The UUID created by createUUIDFromLongs containing the message
	 *            identifier)
	 * @return the message as a long
	 */
	public static long getMessageIdFromUUID(String uuid) {
		byte[] uuidBytes = getBytesFromUUIDString(uuid);
		long messageId = bytesToLong(Arrays.copyOfRange(uuidBytes, 0, 8));

		return messageId;
	}

	/**
	 * Get time embedded time-stamp from the created UUID (created by
	 * createUUIDFromLongs) The time will be returned as a UTC
	 * (yyyy-MM-dd'T'HH:mm:ss.SSSZ)
	 * 
	 * @param uuid
	 *            The UUID created by createUUIDFromLongs containing the
	 *            time-stamp)
	 * @return the time-stamp as a long
	 */
	public static String getTimestampFromUUID(String uuid) {
		return getTimestampFromUUID(uuid, UTCTimestampFormat);
	}

	/**
	 * Get time embedded time-stamp from the created UUID (created by
	 * createUUIDFromLongs)
	 * 
	 * @param uuid
	 *            The UUID created by createUUIDFromLongs containing the
	 *            time-stamp)
	 * @param uuid
	 *            The format to return the time-stamp
	 * @return the time-stamp as a long
	 */
	public static String getTimestampFromUUID(String uuid, String timeFormat) {
		byte[] uuidBytes = getBytesFromUUIDString(uuid);
		long time = bytesToLong(Arrays.copyOfRange(uuidBytes, 8, 16));

		return convertTime(time, timeFormat);
	}

	/**
	 * Convert bytes to a UUID
	 * 
	 * @param bytes
	 *            The byte array to represent as a UUID
	 * @return the converted UUID
	 */
	private static UUID getUUIDFromBytes(byte[] bytes) {
		ByteBuffer byteBuffer = ByteBuffer.wrap(bytes);
		Long high = byteBuffer.getLong();
		Long low = byteBuffer.getLong();
		return new UUID(high, low);
	}

	/**
	 * Convert a UUID to a byte array
	 * 
	 * @param uuid
	 *            The UUID to convert to a byte array
	 * @return The UUID as a byte array
	 */
	private static byte[] getBytesFromUUIDString(String uuid) {
		return getBytesFromUUID(UUID.fromString(uuid));
	}

	/**
	 * Convert a UUID to a byte array
	 * 
	 * @param uuid
	 *            The UUID to convert to a byte array
	 * @return The UUID as a byte array
	 */
	private static byte[] getBytesFromUUID(UUID uuid) {
		ByteBuffer bb = ByteBuffer.wrap(new byte[16]);
		bb.putLong(uuid.getMostSignificantBits());
		bb.putLong(uuid.getLeastSignificantBits());
		return bb.array();
	}

	/**
	 * Convert a byte array to a long
	 * 
	 * @param bytes
	 *            the byte array to convert
	 * @return the byte array represented as a long
	 */
	private static long bytesToLong(byte[] bytes) {
		ByteBuffer buffer = ByteBuffer.allocate(LONG_BYTES);
		buffer.put(bytes);
		buffer.flip();
		return buffer.getLong();
	}

	/**
	 * Convert a system time stamp into a readable date and time
	 * 
	 * @param time
	 *            the system date time stamp
	 * @return a human readable date time stamp
	 */
	private static String convertTime(long time, String timeFormat) {
		Date date = new Date(time);
		Format format = new SimpleDateFormat(timeFormat);
		return format.format(date);
	}
}
