package codingchica.demo.test.dropwizard.component.steps;

import io.cucumber.core.options.CurlOption;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Map;
import java.util.TreeMap;

/** This class houses the state data that needs to be passed between cucumber steps. */
public class APICallWorld {
  String protocol = null;
  String server = "localhost";
  int port = 0;
  String path = null;
  String endpoint = null;
  CurlOption.HttpMethod httpMethod = null;
  Map<String, String> requestHeaders = new TreeMap<>();
  URL url = null;
  HttpURLConnection connection;
}
