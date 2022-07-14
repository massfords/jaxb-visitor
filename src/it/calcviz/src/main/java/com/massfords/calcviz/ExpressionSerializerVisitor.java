package com.massfords.calcviz;

/**
 *
 * Serializes the expression to a string.
 *
 * @author markford
 *         Date: 3/1/12
 */
public class ExpressionSerializerVisitor extends BaseVisitor<Void, CalcException> {
    /**
     * keeps track of the expression we're building.
     */
    private final StringBuilder sb = new StringBuilder();

    /**
     * Gets the result of the serialization
     * @return gets the serialized expression
     */
    public String toString() {
        return sb.toString();
    }

    /**
     * Kicks off visiting the expression
     * @param eval
     */
    @Override
    public Void visit(Eval eval) throws CalcException {
        return eval.getExpression().accept(this);
    }

    /**
     * Joins the two args with a plus operator
     * @param add
     */
    @Override
    public Void visit(Add add) throws CalcException {
        return binaryOp(add, " + ");
    }

    /**
     * Joins the two args with a divide operator
     * @param div
     */
    @Override
    public Void visit(Divide div) throws CalcException {
        return binaryOp(div, " / ");
    }

    /**
     * Joins the two args with a multiplication operator
     * @param mult
     */
    @Override
    public Void visit(Multiply mult) throws CalcException {
        return binaryOp(mult, " * ");
    }

    /**
     * Raises the first arg to the second arg
     * @param pow
     */
    @Override
    public Void visit(Pow pow) throws CalcException {
        return binaryOp(pow, " ^ ");
    }

    /**
     * Joins the two args with a subtraction operator
     * @param sub
     */
    @Override
    public Void visit(Subtract sub) throws CalcException {
        return binaryOp(sub, " - ");
    }

    /**
     * Writes the cosine as a function
     * @param cos
     */
    @Override
    public Void visit(Cos cos) throws CalcException {
        sb.append("cos(");
        Void v = cos.getExp().accept(this);
        sb.append(")");
        return v;
    }

    /**
     * Writes the sine as a function
     * @param sin
     */
    @Override
    public Void visit(Sin sin) throws CalcException {
        sb.append("sin(");
        Void v = sin.getExp().accept(this);
        sb.append(")");
        return v;
    }

    /**
     * Writes the tangent as a function
     * @param tan
     */
    @Override
    public Void visit(Tan tan) throws CalcException {
        sb.append("tan(");
        Void v = tan.getExp().accept(this);
        sb.append(")");
        return v;
    }

    /**
     * Writes the name of the variable
     * @param var
     */
    @Override
    public Void visit(Variable var) {
        sb.append(var.getName());
        return null;
    }

    /**
     * Writes the literal value
     * @param lit
     */
    @Override
    public Void visit(Literal lit) {
        sb.append(lit.getValue());
        return null;
    }

    /**
     * Writes the negation as an operation on the current value
     * @param neg
     */
    @Override
    public Void visit(Negation neg) throws CalcException {
        sb.append("-(");
        Void v = neg.getExp().accept(this);
        sb.append(")");
        return v;
    }

    /**
     * Writes the nth root as a function
     * @param nroot
     */
    @Override
    public Void visit(Nroot nroot) throws CalcException {
        sb.append("nroot(");
        Void v = binaryOp(nroot, " , ");
        sb.append(")");
        return v;
    }

    /**
     * Writes the tetra as a function
     * @param tetra
     */
    @Override
    public Void visit(Tetra tetra) throws CalcException {
        sb.append("tetra(");
        Void v = binaryOp(tetra, " , ");
        sb.append(")");
        return v;
    }

    /**
     * Writes the square root as a function
     * @param sqrt
     */
    @Override
    public Void visit(Sqrt sqrt) throws CalcException {
        sb.append("sqrt(");
        Void v = sqrt.getExp().accept(this);
        sb.append(")");
        return v;
    }

    /**
     * Convenience method for writing all of the binary operations
     * @param binop
     * @param op
     */
    private Void binaryOp(BinaryOp binop, String op) throws CalcException {
        sb.append("(");
        binop.getLeft().accept(this);
        sb.append(op);
        binop.getRight().accept(this);
        sb.append(")");
        return null;
    }
}
