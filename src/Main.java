import java.io.*;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) throws IOException {

        String str;
        Scanner scanner = new Scanner(System.in);
        Calculater calc = new Calculater();
        System.out.println("Для отделения целой части от дробной используйте ','");
        System.out.println("Для выхода из программы введите пустое выражение.");
        for (; ; ) {
            System.out.println("Введите выражение: ");
            str = scanner.nextLine();
            if (str.equals(""))
                break;
            try {
                System.out.println("Результат: " + calc.calculate(str));
                System.out.println();
            } catch (Exception exc) {
                System.out.println(exc);
            }
        }
    }
}
