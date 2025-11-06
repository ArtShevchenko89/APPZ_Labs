package org.example;

import java.util.*;
import java.util.function.DoubleUnaryOperator;

public class ASD_Lab_7 {

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        while (true) {
            System.out.println("1) Чисельне інтегрування");
            System.out.println("0) Вихід");
            System.out.print("Ваш вибір: ");
            int choice = safeInt(sc);
            if (choice == 0) break;
            if (choice == 1) runIntegration(sc);
            System.out.println();
        }
    }

    // integrand: sqrt(1 - (1/4) * sin^2 x)
    static double integrand(double x) {
        double s = Math.sin(x);
        return Math.sqrt(Math.max(0.0, 1.0 - 0.25 * s * s));
    }

    static void runIntegration(Scanner sc) {
        System.out.println("\nЗАВДАННЯ 1: Інтеграл ∫ sqrt(1 - (1/4) sin^2 x) dx");
        System.out.print("Введіть нижню межу a (наприклад 1): ");
        double a = safeDouble(sc);
        System.out.print("Введіть верхню межу b (наприклад " + (Math.PI/2) + "): ");
        double b = safeDouble(sc);
        System.out.print("Введіть крок h (за замовчуванням 0.2): ");
        double h = safeDouble(sc);

        if (a > b) { double t = a; a = b; b = t; }

        int n = Math.max(1, (int)Math.round((b - a) / h));
        h = (b - a) / n;
        if (n % 2 == 1) { n++; h = (b - a) / n; }

        DoubleUnaryOperator f = ASD_Lab_7::integrand;

        double rect = rectangleMid(f, a, b, n);
        double trap = trapezoid(f, a, b, n);
        double simp = simpson(f, a, b, n);

        double exact = adaptiveSimpson(f, a, b, 1e-12);

        System.out.println("\nРЕЗУЛЬТАТИ (n=" + n + ", h=" + h + "):");
        System.out.printf("Метод прямокутників (середні точки): %.12f%n", rect);
        System.out.printf("Метод трапецій:                      %.12f%n", trap);
        System.out.printf("Метод Сімпсона:                      %.12f%n", simp);
        System.out.printf("Точне (референс, адапт. Сімпсон):    %.12f%n", exact);

        System.out.printf("Похибка прямокутники: %.2e%n", Math.abs(exact - rect));
        System.out.printf("Похибка трапеції:     %.2e%n", Math.abs(exact - trap));
        System.out.printf("Похибка Сімпсона:     %.2e%n", Math.abs(exact - simp));

        System.out.println("\nАналітична форма: I(a,b) = E( b | m ) - E( a | m ), де m = 1/4 — неповний еліптичний інтеграл 2-го роду.");
    }

    static double rectangleMid(DoubleUnaryOperator f, double a, double b, int n) {
        double h = (b - a) / n;
        double sum = 0.0;
        for (int i = 0; i < n; i++) {
            double xMid = a + (i + 0.5) * h;
            sum += f.applyAsDouble(xMid);
        }
        return sum * h;
    }

    static double trapezoid(DoubleUnaryOperator f, double a, double b, int n) {
        double h = (b - a) / n;
        double sum = 0.5 * (f.applyAsDouble(a) + f.applyAsDouble(b));
        for (int i = 1; i < n; i++) sum += f.applyAsDouble(a + i * h);
        return sum * h;
    }

    static double simpson(DoubleUnaryOperator f, double a, double b, int n) {
        if (n % 2 == 1) n++;
        double h = (b - a) / n;
        double sum1 = 0.0, sum2 = 0.0;
        for (int i = 1; i < n; i += 2) sum1 += f.applyAsDouble(a + i * h);
        for (int i = 2; i < n; i += 2) sum2 += f.applyAsDouble(a + i * h);
        return (h / 3.0) * (f.applyAsDouble(a) + 4.0 * sum1 + 2.0 * sum2 + f.applyAsDouble(b));
    }

    static double adaptiveSimpson(DoubleUnaryOperator f, double a, double b, double eps) {
        double c = (a + b) / 2.0;
        double fa = f.applyAsDouble(a), fb = f.applyAsDouble(b), fc = f.applyAsDouble(c);
        double S = simpsonRaw(fa, fb, fc, a, b);
        return adaptiveSimpsonRec(f, a, b, eps, S, fa, fb, fc, 20);
    }

    static double simpsonRaw(double fa, double fb, double fc, double a, double b) {
        return (b - a) / 6.0 * (fa + 4.0 * fc + fb);
    }

    static double adaptiveSimpsonRec(DoubleUnaryOperator f, double a, double b, double eps,
                                     double S, double fa, double fb, double fc, int depth) {
        double c = (a + b) / 2.0;
        double d = (a + c) / 2.0;
        double e = (c + b) / 2.0;
        double fd = f.applyAsDouble(d);
        double fe = f.applyAsDouble(e);
        double Sleft = simpsonRaw(fa, fc, fd, a, c);
        double Sright = simpsonRaw(fc, fb, fe, c, b);
        double S2 = Sleft + Sright;
        if (depth <= 0 || Math.abs(S2 - S) <= 15 * eps) {
            return S2 + (S2 - S) / 15.0;
        }
        return adaptiveSimpsonRec(f, a, c, eps / 2.0, Sleft, fa, fc, fd, depth - 1)
             + adaptiveSimpsonRec(f, c, b, eps / 2.0, Sright, fc, fb, fe, depth - 1);
    }

    // прокидуємо ексепшн
    static int safeInt(Scanner sc) {
        while (true) {
            try { return Integer.parseInt(sc.next()); }
            catch (Exception e) { System.out.print("Невірне ціле. Спробуйте ще: "); }
        }
    }

    static double safeDouble(Scanner sc) {
        while (true) {
            try { return Double.parseDouble(sc.next().replace(",", ".")); }
            catch (Exception e) { System.out.print("Невірне число. Спробуйте ще: "); }
        }
    }
}
