import org.json.JSONObject;
import org.json.JSONException;
import java.io.FileReader;
import java.io.IOException;
import java.math.BigInteger;
public class PolynomialSecret {
    public static void main(String[] args) {
        String filePath = "testcase.json";
        JSONObject jsonObject = readJsonFile(filePath);
        if (jsonObject == null) {
            System.out.println("Error reading JSON file.");
            return;
        }
        JSONObject keys = jsonObject.getJSONObject("keys");
        int n = keys.getInt("n");
        int k = keys.getInt("k");
        int[][] points = new int[n][2];
        for (int i = 1; i <= n; i++) {
            JSONObject root = jsonObject.getJSONObject(String.valueOf(i));
            int x = i;
            String base = root.getString("base");
            String value = root.getString("value");
            int y = decodeBase(value, Integer.parseInt(base));
            points[i - 1][0] = x;
            points[i - 1][1] = y;
        }
        int[] xValues = new int[k];
        int[] yValues = new int[k];
        for (int i = 0; i < k && i < n; i++) {
            xValues[i] = points[i][0];
            yValues[i] = points[i][1];
        }
        BigInteger secret = calculateConstantTerm(xValues, yValues, k);
        System.out.println("Secret (constant term c): " + secret);
    }
    private static JSONObject readJsonFile(String filePath) {
        try (FileReader reader = new FileReader(filePath)) {
            StringBuilder jsonContent = new StringBuilder();
            int c;
            while ((c = reader.read()) != -1) {
                jsonContent.append((char) c);
            }
            return new JSONObject(jsonContent.toString());
        } catch (IOException | JSONException e) {
            e.printStackTrace();
            return null;
        }
    }
    private static int decodeBase(String value, int base) {
        return new BigInteger(value, base).intValue();
    }
    private static BigInteger calculateConstantTerm(int[] xValues, int[] yValues, int k) {
        BigInteger c = BigInteger.ZERO;
        for (int i = 0; i < k; i++) {
            BigInteger term = BigInteger.valueOf(yValues[i]);
            for (int j = 0; j < k; j++) {
                if (i != j) {
                    BigInteger numerator = BigInteger.valueOf(-xValues[j]);
                    BigInteger denominator = BigInteger.valueOf(xValues[i] - xValues[j]);
                    term = term.multiply(numerator).divide(denominator);
                }
            }
            c = c.add(term);
        }
        return c;
    }
}
