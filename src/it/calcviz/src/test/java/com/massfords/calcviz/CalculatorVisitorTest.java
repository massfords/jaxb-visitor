package com.massfords.calcviz;

import org.junit.Assert;
import org.junit.Test;

import java.util.Collections;
import static org.junit.Assert.assertEquals;

/**
 * Basic tests for the calc visitor
 *
 * @author markford
 */
public class CalculatorVisitorTest {
    
    @Test
    public void add() {
        Eval eval = new Eval(
                new Add(lit(1), lit(2)));
        
        assertEquals(3.0, Calc.calc(eval), 0);
    }
    
    @Test
    public void subtract() {

        Eval eval = new Eval(new Subtract(lit(10), lit(4)));

        assertEquals(6.0, Calc.calc(eval), 0);
    }
    
    @Test
    public void multiply() {

        Eval eval = new Eval(new Multiply(lit(3), lit(8)));

        assertEquals(24.0, Calc.calc(eval), 0);
    }

    @Test
    public void divide() {

        Eval eval = new Eval(new Divide(lit(100), lit(5)));

        assertEquals(20.0, Calc.calc(eval), 0);
    }
    
    @Test
    public void pow() {

        Eval eval = new Eval(new Pow(lit(2), lit(3)));

        assertEquals(8.0, Calc.calc(eval), 0);
    }
    
    @Test
    public void negation() {
        Eval eval = new Eval(new Negation(lit(10)));

        assertEquals(-10.0, Calc.calc(eval), 0);
    }
    
    @Test
    public void sin() {
        Eval eval = new Eval(new Sin(lit(1)));
        assertEquals(Math.sin(1), Calc.calc(eval), 0);
    }

    @Test
    public void cos() {
        Cos cos = new Cos().withExp(
                lit(1));
        Eval eval = new Eval(cos);
        assertEquals(Math.cos(1), Calc.calc(eval), 0);
    }
    @Test
    public void tan() {
        Eval eval = new Eval(new Tan(lit(1)));
        assertEquals(Math.tan(1), Calc.calc(eval), 0);
    }
    
    @Test
    public void tetra() {
        Eval eval = new Eval(new Tetra(lit(4),lit(2)));

        assertEquals(65536, Calc.calc(eval), 0);
    }
    
    @Test
    public void square() {
        Eval eval = new Eval(new Sqrt(lit(16)));

        assertEquals(4.0, Calc.calc(eval), 0);
    }
    
    @Test
    public void nroot() {
        Eval eval = new Eval(new Nroot(lit(16), lit(2)));

        assertEquals(4.0, Calc.calc(eval), 0);
    }

    @Test
    public void cuberoot() {
        Eval eval = new Eval(new Nroot(lit(8), lit(3)));

        assertEquals(2.0, Calc.calc(eval), 0);
    }

    @Test
    public void graph() {

        // (4*2) + (10 / (4-2))

        Add add = new Add()
                    .withLeft(
                            new Multiply(lit(4), lit(2)))
                    .withRight(
                            new Divide()
                                .withLeft(lit(10))
                                .withRight(
                                        new Subtract(lit(4), lit(2)))
                    );
        Eval eval = new Eval(add);

        assertEquals(13.0, Calc.calc(eval), 0);
    }

    @Test
    public void variable() {
        // (4*2) + (x / (4-2))

        Add add = new Add()
                .withLeft(
                        new Multiply(lit(4), lit(2)))
                .withRight(
                        new Divide()
                                .withLeft(new Variable("x"))
                                .withRight(
                                        new Subtract(lit(4), lit(2)))
                );
        Eval eval = new Eval(add);

        // x=10, answer = 13
        assertEquals(13.0, Calc.calc(eval, Collections.singletonMap("x", 10d)), 0);
        // x=20, answer = 18
        assertEquals(18.0, Calc.calc(eval, Collections.singletonMap("x", 20d)), 0);
    }
    
    private Literal lit(double d) {
        return new Literal(d);
    }
}
