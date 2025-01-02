package cat;

import org.json.JSONObject;
import java.util.*;
import java.math.BigInteger;
import java.nio.file.Files;
import java.nio.file.Paths;

public class sham {
    static class Point {
        double x;
        double y;

        Point(double x, double y) {
            this.x = x;
            this.y = y;
        }
    }

    // Decode value from given base to decimal using BigInteger
    private static BigInteger decodeValue(String valueStr, int base) {
        valueStr = valueStr.toLowerCase();
        BigInteger result = BigInteger.ZERO;
        BigInteger baseBI = BigInteger.valueOf(base);
        
        for (char digit : valueStr.toCharArray()) {
            int digitValue;
            if (Character.isDigit(digit)) {
                digitValue = digit - '0';
            } else {
                digitValue = digit - 'a' + 10;
            }
            result = result.multiply(baseBI).add(BigInteger.valueOf(digitValue));
        }
        return result;
    }

    private static double basisPolynomial(int j, List<Point> points) {
        double result = 1.0;
        for (int m = 0; m < points.size(); m++) {
            if (m != j) {
                double xj = points.get(j).x;
                double xm = points.get(m).x;
                result *= (-xm / (xj - xm));
            }
        }
        return result;
    }

    private static long lagrangeInterpolation(List<Point> points, int k) {
        points = points.subList(0, k);
        double secret = 0;

        for (int j = 0; j < points.size(); j++) {
            secret += points.get(j).y * basisPolynomial(j, points);
        }

        return Math.round(secret);
    }

    public static long processTestCase(String jsonContent) {
        try {
            JSONObject testcase = new JSONObject(jsonContent);
            int k = testcase.getJSONObject("keys").getInt("k");
            List<Point> points = new ArrayList<>();
            
            for (String key : testcase.keySet()) {
                if (!key.equals("keys")) {
                    int x = Integer.parseInt(key);
                    JSONObject point = testcase.getJSONObject(key);
                    int base = Integer.parseInt(point.getString("base"));
                    String value = point.getString("value");
                    BigInteger y = decodeValue(value, base);
                    points.add(new Point(x, y.doubleValue()));
                }
            }
            
            points.sort((a, b) -> Double.compare(a.x, b.x));
            return lagrangeInterpolation(points, k);
        } catch (Exception e) {
            e.printStackTrace();
            return -1;
        }
    }

    public static void main(String[] args) {
        try {
            String testCase1Content = new String(Files.readAllBytes(Paths.get("src/cat/testcase/testcase1.json")));
            String testCase2Content = new String(Files.readAllBytes(Paths.get("src/cat/testcase/testcase2.json")));

            long secret1 = processTestCase(testCase1Content);
            long secret2 = processTestCase(testCase2Content);

            System.out.println("Secret for Test Case 1: " + secret1);
            System.out.println("Secret for Test Case 2: " + secret2);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}