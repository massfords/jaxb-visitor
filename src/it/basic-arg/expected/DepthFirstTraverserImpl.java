package org.example.visitor;

import jakarta.annotation.Generated;
import jakarta.xml.bind.JAXBElement;
import org.example.imported.ImportedData;
import org.example.imported.ImportedType;
import org.example.simple.ChoiceElement;
import org.example.simple.ComplexObject;
import org.example.simple.HasJAXBElement;
import org.example.simple.Problem;
import org.example.simple.Recursive;
import org.example.simple.TSimpleRequest;
import org.example.simple.TSimpleResponse;

@Generated("Generated by jaxb-visitor")
public class DepthFirstTraverserImpl<E extends Throwable, A>
        implements Traverser<E, A> {


    @Override
    public void traverse(ImportedData aBean, Visitor<?, E, A> aVisitor, A arg)
            throws E {
        if (aBean.getImportedType() != null) {
            aBean.getImportedType().accept(aVisitor, arg);
        }
    }

    @Override
    public void traverse(ImportedType aBean, Visitor<?, E, A> aVisitor, A arg)
            throws E {
        if (aBean.getMessage() != null) {
            aBean.getMessage().accept(aVisitor, arg);
        }
    }

    @Override
    public void traverse(ImportedType.Message aBean, Visitor<?, E, A> aVisitor, A arg)
            throws E {
    }

    @Override
    public void traverse(ChoiceElement aBean, Visitor<?, E, A> aVisitor, A arg)
            throws E {
        for (Object bean : aBean.getRequestOrResponseOrString()) {
            if (bean instanceof Visitable) {
                ((Visitable) bean).accept(aVisitor, arg);
            } else {
                if (bean instanceof JAXBElement<?>) {
                    if (((JAXBElement<?>) bean).getValue() instanceof Visitable) {
                        ((Visitable) ((JAXBElement<?>) bean).getValue()).accept(aVisitor, arg);
                    }
                }
            }
        }
    }

    @Override
    public void traverse(ComplexObject aBean, Visitor<?, E, A> aVisitor, A arg)
            throws E {
        for (TSimpleRequest bean : aBean.getSimpleRequest()) {
            bean.accept(aVisitor, arg);
        }
        if (aBean.getSimpleResponse() != null) {
            aBean.getSimpleResponse().accept(aVisitor, arg);
        }
        if (aBean.getImportedData() != null) {
            aBean.getImportedData().accept(aVisitor, arg);
        }
        if (aBean.getLocalElement() != null) {
            aBean.getLocalElement().accept(aVisitor, arg);
        }
    }

    @Override
    public void traverse(ComplexObject.LocalElement aBean, Visitor<?, E, A> aVisitor, A arg)
            throws E {
        if (aBean.getSimpleResponse() != null) {
            aBean.getSimpleResponse().accept(aVisitor, arg);
        }
        if (aBean.getMessage() != null) {
            aBean.getMessage().accept(aVisitor, arg);
        }
    }

    @Override
    public void traverse(HasJAXBElement aBean, Visitor<?, E, A> aVisitor, A arg)
            throws E {
        if (aBean.getOptionalAndNillableRequest() != null) {
            aBean.getOptionalAndNillableRequest().getValue().accept(aVisitor, arg);
        }
    }

    @Override
    public void traverse(Problem aBean, Visitor<?, E, A> aVisitor, A arg)
            throws E {
    }

    @Override
    public void traverse(Recursive aBean, Visitor<?, E, A> aVisitor, A arg)
            throws E {
        if (aBean.getRecursive() != null) {
            aBean.getRecursive().accept(aVisitor, arg);
        }
    }

    @Override
    public void traverse(TSimpleRequest aBean, Visitor<?, E, A> aVisitor, A arg)
            throws E {
    }

    @Override
    public void traverse(TSimpleResponse aBean, Visitor<?, E, A> aVisitor, A arg)
            throws E {
    }

}
