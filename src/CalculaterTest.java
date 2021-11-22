import org.junit.jupiter.api.Assertions;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

class CalculaterTest {

    @org.junit.jupiter.api.Test
    void calculate() throws Exception {
        Calculater calc = new Calculater();

        Assertions.assertEquals(BigDecimal.valueOf(-5).setScale(2),calc.calculate("-5"));
        Assertions.assertEquals(BigDecimal.valueOf(7).setScale(2),calc.calculate("5+2"));
        Assertions.assertEquals(BigDecimal.valueOf(4.5).setScale(2),calc.calculate("9/2"));
        Assertions.assertEquals(BigDecimal.valueOf(0.7).setScale(2),calc.calculate("9,7-9"));
        Assertions.assertEquals(BigDecimal.valueOf(16).setScale(2),calc.calculate("4^2"));
        Assertions.assertEquals(BigDecimal.valueOf(10).setScale(2),calc.calculate("(5*4)/2"));
    }
}