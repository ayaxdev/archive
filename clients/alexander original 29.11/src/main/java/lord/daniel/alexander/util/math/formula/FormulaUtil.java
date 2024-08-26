package lord.daniel.alexander.util.math.formula;

/**
 * Written by 'Daniel.' for Alexander client.
 * Please don't use the code.
 *
 * @author Daniel.
 */
public class FormulaUtil {

    public static float getProjectileMotion(double velocity, double gravity, double x, double y) {
        return (float) -Math.toDegrees(Math.atan2(Math.pow(velocity, 2) - Math.sqrt(Math.pow(velocity, 4) - gravity * (gravity * Math.pow(x, 2) + 2 * y * Math.pow(velocity, 2))), gravity * x));
    }

    public static float getPercent(float part, float whole) {
        return part * 100 / whole;
    }

    public static float getPart(float whole, float percent) {
        return whole * percent / 100;
    }

    public static float getWhole(float part, float percent) {
        return part * 100 / percent;
    }

    public static double getABC(int a, int b, int c) {
        return (-b + Math.sqrt(Math.pow(b, 2) - 4 * a * c)) / (2*a);
    }

    public static float getAverage(float... number) {
        float total = 0;
        for(float num : number) {
            total += num;
        }
        return total / number.length;
    }

}