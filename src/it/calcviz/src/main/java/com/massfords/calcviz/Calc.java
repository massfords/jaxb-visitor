package com.massfords.calcviz;

import java.util.Collections;
import java.util.Map;

/**
 * Simple utility for evaluating expressions. Visits each of the nodes and evaluates the operations in order to
 * produce a literal value.
 *
 * @author markford
 *         Date: 2/25/12
 */
public class Calc {
    /**
     * Evaluates the graph without any variables.
     * @param eval Expression to evaluate.
     * @return result of the evaluation
     */
    public static double calc(Eval eval) throws CalcException {
        return calc(eval, Collections.emptyMap());
    }

    /**
     * Evaluate the expressions and returns the result.
     * @param eval Expression to evaluate
     * @param variables optional variables to inject into the expression as needed.
     */
    public static double calc(Eval eval, Map<String,Double> variables) throws CalcException {
        CalculatorVisitor calcviz = new CalculatorVisitor(variables);
        TraversingVisitor<Literal,CalcException> tv = new TraversingVisitor<>(
                new DepthFirstTraverserImpl<>(), calcviz);
        tv.setTraverseFirst(true);
        eval.accept(tv);
        return calcviz.getResult();
    }
}
