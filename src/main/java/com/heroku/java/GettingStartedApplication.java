package com.heroku.java;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import javax.sql.DataSource;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.Map;
import java.security.SecureRandom;

@SpringBootApplication
@Controller
public class GettingStartedApplication {
    private final DataSource dataSource;

    @Autowired
    public GettingStartedApplication(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @GetMapping("/")
    public String index() {
        return "index";
    }

    @GetMapping("/database")

String database(Map<String, Object> model) {
    try (Connection connection = dataSource.getConnection()) {
        var statement = connection.createStatement();

        statement.executeUpdate(
            "CREATE TABLE IF NOT EXISTS table_timestamp_and_random_string (" +
            "tick timestamp, random_string varchar(50))"
        );

        statement.executeUpdate(
            "INSERT INTO table_timestamp_and_random_string VALUES (now(), '" + getRandomString() + "')"
        );

        var resultSet = statement.executeQuery(
            "SELECT tick, random_string FROM table_timestamp_and_random_string ORDER BY tick DESC"
        );

        var output = new ArrayList<String>();
        while (resultSet.next()) {
            output.add(
                "Read from DB: " +
                resultSet.getTimestamp("tick") +
                " " +
                resultSet.getString("random_string")
            );
        }

        System.out.println ("Sydney Hurst");

        model.put("records", output);
        return "database";

    } catch (Exception e) {
        e.printStackTrace();
        model.put("message", e.getMessage());
        return "error";
    }
}


    private static String getRandomString() {
        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";
        SecureRandom random = new SecureRandom();
        StringBuilder sb = new StringBuilder(12);

        for (int i = 0; i < 12; i++) {
            sb.append(chars.charAt(random.nextInt(chars.length())));
        }
        return sb.toString();
    }

    public static void main(String[] args) {
        SpringApplication.run(GettingStartedApplication.class, args);
    }
}
