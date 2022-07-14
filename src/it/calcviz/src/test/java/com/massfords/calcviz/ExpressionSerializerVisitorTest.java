package com.massfords.calcviz;

import org.junit.Assert;
import org.junit.Test;
import static org.junit.Assert.assertEquals;

/**
 * @author markford
 *         Date: 3/1/12
 */
public class ExpressionSerializerVisitorTest {
    ExpressionSerializerVisitor xp = new ExpressionSerializerVisitor();

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
        eval.accept(xp);

        assertEquals("((4.0 * 2.0) + (10.0 / (4.0 - 2.0)))", xp.toString());
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
        eval.accept(xp);
        
        assertEquals("((4.0 * 2.0) + (x / (4.0 - 2.0)))", xp.toString());
    }

    private Literal lit(double d) {
        return new Literal(d);
    }
}
