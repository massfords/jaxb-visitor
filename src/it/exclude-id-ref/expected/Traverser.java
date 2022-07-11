package org.prostep.ecadif.vec113.visitor;

import jakarta.annotation.Generated;

import org.prostep.ecadif.vec113.VecContent;
import org.prostep.ecadif.vec113.VecDocumentVersion;
import org.prostep.ecadif.vec113.VecPartVersion;

@Generated("Generated by jaxb-visitor")
public interface Traverser<E extends Throwable> {


    void traverse(VecContent aBean, Visitor<?, E> aVisitor)
            throws E
            ;

    void traverse(VecDocumentVersion aBean, Visitor<?, E> aVisitor)
            throws E
            ;

    void traverse(VecPartVersion aBean, Visitor<?, E> aVisitor)
            throws E
            ;

}
