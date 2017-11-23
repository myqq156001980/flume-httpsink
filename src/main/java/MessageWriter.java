import com.amazonaws.AmazonClientException;
import com.amazonaws.regions.Region;
import com.amazonaws.regions.RegionUtils;
import com.amazonaws.services.kinesis.AmazonKinesis;
import com.amazonaws.services.kinesis.AmazonKinesisClientBuilder;
import com.amazonaws.services.kinesis.model.DescribeStreamResult;
import com.amazonaws.services.kinesis.model.PutRecordRequest;
import com.amazonaws.services.kinesis.model.ResourceNotFoundException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.nio.ByteBuffer;

/**
 * Created by sunzqc on 2017/11/23 16:01.
 */
public class MessageWriter {

    private static final Log LOG = LogFactory.getLog(MessageWriter.class);
    private static AmazonKinesis kinesisClient;
    private static String streamName;

    /**
     * Checks if the stream exists and is active
     *
     * @param streamName Name of stream
     */
    public static void validateStream(String streamName) {
        try {
            DescribeStreamResult result = kinesisClient.describeStream(streamName);
            if (!"ACTIVE".equals(result.getStreamDescription().getStreamStatus())) {
                System.err.println("Stream " + streamName + " is not active. Please wait a few moments and try again.");
                System.exit(1);
            }
        } catch (ResourceNotFoundException e) {
            System.err.println("Stream " + streamName + " does not exist. Please create it in the console.");
            System.err.println(e);
            System.exit(1);
        } catch (Exception e) {
            System.err.println("Error found while describing the stream " + streamName);
            System.err.println(e);
            System.exit(1);
        }
    }


    /**
     * Uses the Kinesis client to send the stock trade to the given stream.
     *
     * @param mess message to send
     */
    public static void sendMessage(String mess) {
        byte[] bytes = mess.getBytes();
        // The bytes could be null if there is an issue with the JSON serialization by the Jackson JSON library.
        if (bytes == null) {
            LOG.warn("Could not get JSON bytes for stock trade");
            return;
        }

        LOG.info("Putting message: " + mess);
        PutRecordRequest putRecord = new PutRecordRequest();
        putRecord.setStreamName(streamName);
        // We use the ticker symbol as the partition key, as explained in the tutorial.
        putRecord.setPartitionKey("message-key");
        putRecord.setData(ByteBuffer.wrap(bytes));

        try {
            kinesisClient.putRecord(putRecord);
        } catch (AmazonClientException ex) {
            LOG.warn("Error sending record to Amazon Kinesis.", ex);
        }
    }

    public static void init(String streamName, String regionName) throws Exception {

        Region region = RegionUtils.getRegion(regionName);
        if (region == null) {
            System.err.println(regionName + " is not a valid AWS region.");
            System.exit(1);
        }

        AmazonKinesisClientBuilder clientBuilder = AmazonKinesisClientBuilder.standard();

        clientBuilder.setRegion(regionName);
        clientBuilder.setCredentials(CredentialUtils.getCredentialsProvider());
        clientBuilder.setClientConfiguration(ConfigurationUtils.getClientConfigWithUserAgent());
        kinesisClient = clientBuilder.build();
        // Validate that the stream exists and is active
        validateStream(streamName);
        MessageWriter.streamName = streamName;
    }


}
