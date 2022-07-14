package com.massfords.calcviz;

import org.junit.Assert;
import org.junit.Test;
import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author markford
 *         Date: 3/1/12
 */
public class PartialEvaluatorVisitorTest {

    private final PartialEvaluatorVisitor pev = new PartialEvaluatorVisitor();

    @Test
    public void graphWithVariable() {
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

        eval.accept(pev);

        // partial evaluation result
        Exp result = pev.getResult();

        ExpressionSerializerVisitor xp = new ExpressionSerializerVisitor();
        result.accept(xp);

        assertEquals("(8.0 + (x / 2.0))", xp.toString());

    }

    @Test
    public void graph() {
        // (4*2) + (10 / (4-2))
        Add add = new Add()
                .withLeft(
                        new Multiply(lit(4), lit(2)))
                .withRight(
                        new Divide()
                                .withLeft(new Literal(10))
                                .withRight(
                                        new Subtract(lit(4), lit(2)))
                );


        List<String> steps = new ArrayList<>();
        Exp exp = add;
        while(!(exp instanceof Literal)) {
            exp = partiallyEvaluate(exp);
            steps.add(toString(exp));
        }

        assertEquals(3, steps.size());
        String[] expected = {
            "(8.0 + (10.0 / 2.0))",
            "(8.0 + 5.0)",
            "13.0"
        };
        assertEquals(Arrays.asList(expected), steps);
    }

    private Exp partiallyEvaluate(Exp exp) {
        exp.accept(pev);
        return pev.getResult();
    }

    private String toString(Exp result) throws CalcException {
        ExpressionSerializerVisitor xp = new ExpressionSerializerVisitor();
        result.accept(xp);
        return xp.toString();
    }
    private Literal lit(double d) {
        return new Literal(d);
    }
}
