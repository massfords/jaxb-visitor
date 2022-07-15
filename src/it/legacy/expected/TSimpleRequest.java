package org.example.simple;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;
import javax.xml.namespace.QName;
import org.example.visitor.Named;
import org.example.visitor.Visitable;
import org.example.visitor.Visitor;


/**
 * <p>Java class for tSimpleRequest complex type.
 *
 * <p>The following schema fragment specifies the expected content contained within this class.
 *
 * <pre>
 * &lt;complexType name="tSimpleRequest"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="request1" type="{http://www.w3.org/2001/XMLSchema}string"/&gt;
 *         &lt;element name="request2" type="{http://www.w3.org/2001/XMLSchema}string"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 *
 *
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "tSimpleRequest", propOrder = {
        "request1",
        "request2"
})
public class TSimpleRequest implements Named, Visitable
{

    @XmlElement(required = true)
    protected String request1;
    @XmlElement(required = true)
    protected String request2;
    @XmlTransient
    private QName jaxbElementName;

    /**
     * Gets the value of the request1 property.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getRequest1() {
        return request1;
    }

    /**
     * Sets the value of the request1 property.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setRequest1(String value) {
        this.request1 = value;
    }

    /**
     * Gets the value of the request2 property.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getRequest2() {
        return request2;
    }

    /**
     * Sets the value of the request2 property.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setRequest2(String value) {
        this.request2 = value;
    }

    public void setJAXBElementName(QName name) {
        this.jaxbElementName = name;
    }

    public QName getJAXBElementName() {
        return this.jaxbElementName;
    }

    public void afterUnmarshal(Unmarshaller u, Object parent) {
        if (parent instanceof JAXBElement<?> ) {
            this.jaxbElementName = ((JAXBElement<?> ) parent).getName();
        }
    }

    public<R, E extends Throwable >R accept(Visitor<R, E> aVisitor)
            throws E
    {
        return aVisitor.visit(this);
    }

}
