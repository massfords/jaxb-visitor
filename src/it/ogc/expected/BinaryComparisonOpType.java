package net.opengis.fes._2;

import java.util.ArrayList;
import java.util.List;
import jakarta.xml.bind.JAXBElement;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlElementRef;
import jakarta.xml.bind.annotation.XmlType;
import ogc.visitor.Visitable;
import ogc.visitor.Visitor;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "BinaryComparisonOpType", propOrder = { "expression" })
public class BinaryComparisonOpType extends ComparisonOpsType implements Visitable {

    @XmlElementRef(name = "expression", namespace = "http://www.opengis.net/fes/2.0", type = JAXBElement.class)
    protected List<JAXBElement<?>> expression;
    @XmlAttribute(name = "matchCase")
    protected Boolean matchCase;
    @XmlAttribute(name = "matchAction")
    protected MatchActionType matchAction;

    public List<JAXBElement<?>> getExpression() {
        if (expression == null) {
            expression = new ArrayList<JAXBElement<?>>();
        }
        return this.expression;
    }

    public boolean isMatchCase() {
        if (matchCase == null) {
            return true;
        } else {
            return matchCase;
        }
    }

    public void setMatchCase(Boolean value) {
        this.matchCase = value;
    }

    public MatchActionType getMatchAction() {
        if (matchAction == null) {
            return MatchActionType.ANY;
        } else {
            return matchAction;
        }
    }

    public void setMatchAction(MatchActionType value) {
        this.matchAction = value;
    }

    public <R, E extends Throwable> R accept(Visitor<R, E> aVisitor) throws E {
        return aVisitor.visit(this);
    }

}
