package com.massfords.calcviz;

import java.util.Map;
import java.util.Stack;

/**
 * Evaluates the expression and returns the result. This visitor uses a stack to keep track of the results while
 * it evaluates the expression. The assumption here is that we traverse the child nodes before visiting the parent
 * node. In this way, the values for the binary and unary expressions will already have been evaluated and each
 * of the operations is able to perform its simple operation.
 *
 * @author markford
 *         Date: 2/25/12
 */
public class CalculatorVisitor extends BaseVisitor<Literal,CalcException> {
    /**
     * maintains our state as we traverse the graph
     */
    private final Stack<Literal> stack = new Stack<>();

    /**
     * values to use for variables
     */
    private final Map<String, Double> variables;

    /**
     * Creates the visitor with the given map of variables.
     * @param variables optional map of variable names to values.
     */
    public CalculatorVisitor(Map<String,Double> variables) {
        this.variables = variables;
    }

    /**
     * Pops the left and right values off the stack and adds them.
     */
    @Override
    public Literal visit(Add add) {
        Literal right = pop();
        Literal left = pop();
        return push(left.getValue() + right.getValue());
    }

    /**
     * Pops the left and right values off the stack and subtracts them.
     */
    @Override
    public Literal visit(Subtract sub) {
        Literal right = pop();
        Literal left = pop();
        return push(left.getValue() - right.getValue());
    }

    /**
     * Pops the left and right values off the stack and multiplies them.
     */
    @Override
    public Literal visit(Multiply mult) {
        Literal right = pop();
        Literal left = pop();
        return push(left.getValue() * right.getValue());
    }

    /**
     * Pops the left and right values off the stack and divides them.
     */
    @Override
    public Literal visit(Divide div) throws DivideByZeroException {
        Literal right = pop();
        Literal left = pop();
        if (right.getValue()==0) throw new DivideByZeroException();
        return push(left.getValue() / right.getValue());
    }

    /**
     * Pops the left and right values off the stack and raises the left value to the right value.
     */
    @Override
    public Literal visit(Pow pow) {
        Literal right = pop();
        Literal left = pop();
        return push(Math.pow(left.getValue(), right.getValue()));
    }

    /**
     * Pops a value off the stack and negates it
     */
    @Override
    public Literal visit(Negation neg) {
        Literal literal = pop();
        return push(literal.getValue() * -1);
    }

    /**
     * Pops a value off the stack and passes it to the Math.sin function
     */
    @Override
    public Literal visit(Sin sin) {
        Literal literal = pop();
        return push(Math.sin(literal.getValue()));
    }

    /**
     * Pops a value off the stack and passes it to the Math.cos function
     */
    @Override
    public Literal visit(Cos cos) {
        Literal literal = pop();
        return push(Math.cos(literal.getValue()));
    }

    /**
     * Pops a value off the stack and passes it to the Math.tan function
     */
    @Override
    public Literal visit(Tan tan) {
        Literal literal = pop();
        return push(Math.tan(literal.getValue()));
    }

    /**
     * An iterated exponentiation operation aka <a href="http://oeis.org/wiki/Tetration">tetration</a>
     */
    @Override
    public Literal visit(Tetra tetra) {
        Literal right = pop();
        Literal left = pop();
        
        // the left is our loop and it should be an integer
        double d = left.getValue();
        double value;
        if (d == 0) {
            value = 1;
        } else {
            double base = right.getValue();
            value = base;
            int count = (int) d;
            for(int i=0; i<count-1; i++) {
                value = Math.pow(base, value);
            }
        }
        return push(value);
    }

    /**
     * Substitutes the value in the variable into the expression.
     * @param var
     */
    @Override
    public Literal visit(Variable var) {
        Double d = variables.get(var.getName());
        if (d == null) {
            throw new IllegalArgumentException("unknown variable name:" + var.getName());
        }
        return push(d);
    }

    /**
     * Pops a value off the stack and passes it to the Math.tan function
     */
    @Override
    public Literal visit(Sqrt sqrt) {
        Literal literal = pop();
        return push(Math.sqrt(literal.getValue()));
    }

    /**
     * Pops the left and right values off the stack and takes the nth value of the left value where the right value
     * is the nth value (e.g. 2 is square root, 3 is cube root, etc).
     */
    @Override
    public Literal visit(Nroot nroot) {
        Literal right = pop();
        Literal left = pop();
        return push(Math.pow(left.getValue(), 1/right.getValue()));
    }

    /**
     * Pushes the literal value onto the stack
     * @param literal
     */
    @Override
    public Literal visit(Literal literal) {
        return push(literal);
    }

    /**
     * Pushes the value onto the stack
     * @param lit
     */
    private Literal push(Literal lit) {
        stack.push(lit);
        return lit;
    }

    /**
     * Convenience method to push a literal onto the stack
     * @param d
     */
    private Literal push(double d) {
        Literal lit = new Literal();
        lit.setValue(d);
        stack.push(lit);
        return lit;
    }

    /**
     * Pops the literal off the stack
     * @return
     */
    private Literal pop() {
        return stack.pop();
    }

    /**
     * Pops the final answer off the stack
     * @return Literal result that's been calculated as a result of our visitation.
     */
    public double getResult() {
        Literal literal = pop();
        return literal.getValue();
    }
}
