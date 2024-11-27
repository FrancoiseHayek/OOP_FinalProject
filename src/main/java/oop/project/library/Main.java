package oop.project.library;

import oop.project.library.scenarios.Result;
import oop.project.library.scenarios.Scenarios;

import java.util.Map;
import java.util.Scanner;

public class Main {

    public static void main(String[] args) {
        var scanner = new Scanner(System.in);
        while (true) {
            var input = scanner.nextLine();
            if (input.equals("exit")) {
                break;
            }
            try {
                var result = Scenarios.parse(input);
                switch (result) {
                    case Result.Success<Map<String, Object>> success -> System.out.println(success.value());
                    case Result.Failure<Map<String, Object>> failure -> System.out.println("Error: " + failure.error());
                }
            } catch (Exception e) {
                System.out.println("Unexpected exception: " + e.getClass().getName() + ", " + e.getMessage());
            }
        }
    }

}
