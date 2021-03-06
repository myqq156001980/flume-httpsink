import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.IOException;

/**
 * Created by sunzqc on 2017/11/23 10:01.
 */


@Controller
@EnableAutoConfiguration
public class Receiver {
    private static final Log LOG = LogFactory.getLog(Receiver.class);

    private static void checkUsage(String[] args) {
        if (args.length != 2) {
            System.err.println("Usage: " + Receiver.class.getSimpleName()
                    + " <stream name> <region>");
            System.exit(1);
        }
    }


    public static void main(String[] args) throws Exception {
        checkUsage(args);

        String streamName = args[0];
        String regionName = args[1];
        MessageWriter.init(streamName, regionName);
        SpringApplication.run(Receiver.class, args);
    }

    @RequestMapping("/test")
    void getData(HttpServletRequest request, HttpServletResponse response) throws IOException {


        BufferedReader br = request.getReader();

        String str, wholeStr = "";
        while ((str = br.readLine()) != null) {
            wholeStr += str;
        }

        MessageWriter.sendMessage(wholeStr);
        LOG.info("receive message :" + wholeStr);

    }
}
