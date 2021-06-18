package com.amex.ea.example.spring.codility;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/*
Implement a simple Spring boot API.
You are given a Spring MVC application and your task is to build new RESTful web service.

You should implement two variants of reading the /healthcheck resource using JSON as the response data format:

Method to read simple healthcheck
A request to GET /healthcheck?format=short should return a simple message:

 { "status": "OK" }

Method to read detailed healthcheck
A request to GET /healthcheck?format=full should return the application status and current time (formatted as an ISO 8601 string):

 { "currentTime": "2018-06-06T21:59:36Z", "status": "OK" }

Other requirements and hints
1. Performance is not assessed; focus on correctness and code quality.

2. If parameters' values do not match the specified possible values or if no parameter is present, you should return HTTP status code 400 ("Bad Request").

3. Responses should have Content-Type header with appropriate value (application/json).

4. If you need to create multiple classes, you can use nested classes or define multiple classes in one source file.

5. You can use only the following libraries (and their dependencies):

Spring Web MVC (v. 5.0.7.RELEASE)
FasterXML Jackson, Jackson Datatype JSR310 (v. 2.9.6)
 */
@RestController
public class SpringHealthCheck {

    private HttpHeaders getResponseHeaders() {
      final HttpHeaders responseHeaders = new HttpHeaders();
      responseHeaders.set("Content-Type", "application/json");
      return responseHeaders;
    }

    @GetMapping(value = "/healthcheck", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> healthcheck(@RequestParam(name = "format") String formatName) {
      final List<Format> formats = FormatsProvider.getAllFormats();
      for (Format format: formats) {
        if (format.getName().equals(formatName)) {
          return ResponseEntity.ok().headers(getResponseHeaders()).body(format.getOutput());
        }
      }
      return ResponseEntity.badRequest().body("");
    }
  }

   class FormatsProvider {
    public static List<Format> getAllFormats() {
      return Arrays.asList(new FormatShort(), new FormatFull());
    }
  }
  interface Format {
    public String getName();
    public String getOutput();
  }

   class FormatShort implements Format {
    private static final String NAME = "short";

    @Override
    public String getName() {
      return NAME;
    }

    @Override
    public String getOutput() {
      return "{ \"status\": \"OK\" } ";
    }
  }

   class FormatFull implements Format {
    private static final String NAME = "full";
    private static final String UTC = "UTC";
    private TimeProvider timeProvider = new TimeProvider(UTC);

    @Override
    public String getName() {
      return NAME;
    }

    @Override
    public String getOutput() {
      return "{\"currentTime\": \"" + timeProvider.getCurrentTimeISO() + "\", \"status\": \"OK\" } ";
    }
  }

   class TimeProvider {
    private static final String ISO_DATE_FORMAT = "yyyy-MM-dd'T'HH:mm:ss'Z'";
    private String timeZone;

    public TimeProvider(final String timeZone) {
      this.timeZone = timeZone;
    }

    private DateFormat getISODateFormat() {
      final DateFormat dateFormat = new SimpleDateFormat(ISO_DATE_FORMAT);
      dateFormat.setTimeZone(TimeZone.getTimeZone(this.timeZone));
      return dateFormat;
    }

    public String getCurrentTimeISO() {
      final DateFormat dateFormat = getISODateFormat();
      return dateFormat.format(new Date());
    }
  }

