package org.example.visitor;

import javax.annotation.Generated;
import javax.xml.bind.JAXBElement;
import extended.Parameter2;
import extendedJaxbModel.Base;
import extendedJaxbModel.Choice;
import extendedJaxbModel.ParamElem;
import extendedJaxbModel.Single;

@Generated("Generated by jaxb-visitor")
public class DepthFirstTraverserImpl<E extends Throwable>
        implements Traverser<E> {


    @Override
    public void traverse(Base aBean, Visitor<?, E> aVisitor)
            throws E {
        for (extended.Parameter bean : aBean.getParameter()) {
            aVisitor.visit(bean);
        }
    }

    @Override
    public void traverse(Choice aBean, Visitor<?, E> aVisitor)
            throws E {
        for (Object bean : aBean.getParameterOrParameter2OrParamElem()) {
            if (bean instanceof Visitable) {
                ((Visitable) bean).accept(aVisitor);
            } else {
                if (bean instanceof JAXBElement<?>) {
                    if (((JAXBElement<?>) bean).getValue() instanceof Visitable) {
                        ((Visitable) ((JAXBElement<?>) bean).getValue()).accept(aVisitor);
                    }
                } else {
                    if (bean instanceof extended.Parameter) {
                        aVisitor.visit(((extended.Parameter) bean));
                    } else {
                        if (bean instanceof Parameter2) {
                            aVisitor.visit(((Parameter2) bean));
                        }
                    }
                }
            }
        }
    }

    @Override
    public void traverse(ParamElem aBean, Visitor<?, E> aVisitor)
            throws E {
    }

    @Override
    public void traverse(Single aBean, Visitor<?, E> aVisitor)
            throws E {
        if (aBean.getSingleElem() != null) {
            aVisitor.visit(aBean.getSingleElem());
        }
    }

    @Override
    public void traverse(extended.Parameter aBean, Visitor<?, E> aVisitor)
            throws E {
        // details about extended.Parameter are not known at compile time.
        // For now, applications using external classes will have to
        // implement their own traversal logic.
    }

    @Override
    public void traverse(Parameter2 aBean, Visitor<?, E> aVisitor)
            throws E {
        // details about extended.Parameter2  are not known at compile time.
        // For now, applications using external classes will have to
        // implement their own traversal logic.
    }

}
