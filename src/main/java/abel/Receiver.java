package abel;

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

    public static void main(String[] args) throws Exception {
        SpringApplication.run(Receiver.class, args);
    }

    @RequestMapping("/test")
    void getData(HttpServletRequest request, HttpServletResponse response) throws IOException {


        BufferedReader br = request.getReader();

        String str, wholeStr = "";
        while ((str = br.readLine()) != null) {
            wholeStr += str;
        }

        System.out.println(wholeStr);

    }
}
