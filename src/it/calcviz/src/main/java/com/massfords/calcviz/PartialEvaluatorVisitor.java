package com.massfords.calcviz;

import java.util.Collections;
import java.util.Stack;

/**
 * Transforms the graph by evaluating any of the operations that have literal
 * arguments. This provides a means to evaluate the graph one step at a time.
 *
 * @author markford
 *         Date: 3/1/12
 */
public class PartialEvaluatorVisitor extends BaseVisitor<Literal,CalcException> {
    /**
     * maintains our state
     */
    private final Stack<Exp> stack = new Stack<>();
    /**
     * Used to evaluate each of the operations
     */
    private final CalculatorVisitor calculatorVisitor = new CalculatorVisitor(
            Collections.emptyMap());

    /**
     * Kicks off the visitor
     * @param eval
     */
    @Override
    public Literal visit(Eval eval) throws CalcException {
        return eval.getExpression().accept(this);
    }

    /**
     * Evaluates the operation if both of its args are literals
     * @param add
     */
    @Override
    public Literal visit(Add add) throws CalcException {
        return binop(add);
    }

    /**
     * Evaluates the operation if both of its args are literals
     * @param div
     */
    @Override
    public Literal visit(Divide div) throws CalcException {
        return binop(div);
    }

    /**
     * Evaluates the operation if both of its args are literals
     * @param mul
     */
    @Override
    public Literal visit(Multiply mul) throws CalcException {
        return binop(mul);
    }

    /**
     * Evaluates the operation if both of its args are literals
     * @param pow
     */
    @Override
    public Literal visit(Pow pow) throws CalcException {
        return binop(pow);
    }

    /**
     * Evaluates the operation if both of its args are literals
     * @param nroot
     */
    @Override
    public Literal visit(Nroot nroot) throws CalcException {
        return binop(nroot);
    }

    /**
     * Evaluates the operation if both of its args are literals
     * @param sub
     */
    @Override
    public Literal visit(Subtract sub) throws CalcException {
        return binop(sub);
    }

    /**
     * Evaluates the operation if its arg is a literal
     * @param cos
     */
    @Override
    public Literal visit(Cos cos) throws CalcException {
        return unop(cos);
    }

    /**
     * Pushes the literal onto the stack
     * @param lit
     */
    @Override
    public Literal visit(Literal lit) {
        stack.push(lit);
        return lit;
    }

    /**
     * Evaluates the operation if its args is a literal
     * @param neg
     */
    @Override
    public Literal visit(Negation neg) throws CalcException {
        return unop(neg);
    }

    /**
     * Evaluates the operation if its args is a literal
     * @param sin
     */
    @Override
    public Literal visit(Sin sin) throws CalcException {
        return unop(sin);
    }

    /**
     * Evaluates the operation if its args is a literal
     * @param sqrt
     */
    @Override
    public Literal visit(Sqrt sqrt) throws CalcException {
        return unop(sqrt);
    }

    /**
     * Evaluates the operation if its args is a literal
     * @param tan
     */
    @Override
    public Literal visit(Tan tan) throws CalcException {
        return unop(tan);
    }

    @Override
    public Literal visit(Tetra tetra) throws CalcException {
        return binop(tetra);
    }

    /**
     * Pushes the var onto the stack
     * @param var
     */
    @Override
    public Literal visit(Variable var) {
        stack.push(var);
        return null;
    }

    /**
     * Gets the result of the operation.
     */
    public Exp getResult() {
        return stack.pop();
    }

    /**
     * Common operation for all of the unary operations.
     * We'll evaluate the operation if its single arg is a literal.
     * If not, we'll continue traversing.
     * @param unop
     */
    private Literal unop(UnaryOp unop) throws CalcException {
        if (isSimple(unop)) {
            unop.getExp().accept(calculatorVisitor);
            unop.accept(calculatorVisitor);
            Literal lit = new Literal(calculatorVisitor.getResult());
            stack.push(lit);
            return lit;
        } else {
            stack.push(unop);
            unop.getExp().accept(this);
            unop.withExp(stack.pop());
            return null;
        }
    }

    /**
     * Common operation for all of the binary operations.
     * We'll evaluate the operation if both of its args are literals.
     * If not, we'll continue traversing.
     * @param binop
     */
    private Literal binop(BinaryOp binop) throws CalcException {
        if (isSimple(binop)) {
            binop.getLeft().accept(calculatorVisitor);
            binop.getRight().accept(calculatorVisitor);
            binop.accept(calculatorVisitor);
            Literal lit = new Literal(calculatorVisitor.getResult());
            stack.push(lit);
            return lit;
        } else {
            stack.push(binop);
            binop.getLeft().accept(this);
            binop.getRight().accept(this);
            binop.withRight(stack.pop()).withLeft(stack.pop());
            return null;
        }
    }

    /**
     * Returns true if both args for the operation are literals
     * @param binop
     */
    private boolean isSimple(BinaryOp binop) {
        return binop.getLeft() instanceof Literal && binop.getRight() instanceof Literal;
    }

    /**
     * REturns true if the arg for the operation is a literal
     * @param uop
     */
    private boolean isSimple(UnaryOp uop) {
        return uop.getExp() instanceof Literal;
    }
}
