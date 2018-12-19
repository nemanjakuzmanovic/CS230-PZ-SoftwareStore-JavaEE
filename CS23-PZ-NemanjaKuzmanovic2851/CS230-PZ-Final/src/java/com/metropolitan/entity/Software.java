/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.metropolitan.entity;

import java.io.Serializable;
import java.util.Collection;
import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

/**
 *
 * @author PC
 */
@Entity
@Table(name = "software")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Software.findAll", query = "SELECT s FROM Software s")
    , @NamedQuery(name = "Software.findBySoftwareId", query = "SELECT s FROM Software s WHERE s.softwareId = :softwareId")
    , @NamedQuery(name = "Software.findBySoftwareName", query = "SELECT s FROM Software s WHERE s.softwareName = :softwareName")
    , @NamedQuery(name = "Software.findBySoftwareDesc", query = "SELECT s FROM Software s WHERE s.softwareDesc = :softwareDesc")
    , @NamedQuery(name = "Software.findBySoftwarePrice", query = "SELECT s FROM Software s WHERE s.softwarePrice = :softwarePrice")})
public class Software implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "software_id")
    private Integer softwareId;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 255)
    @Column(name = "software_name")
    private String softwareName;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 255)
    @Column(name = "software_desc")
    private String softwareDesc;
    @Basic(optional = false)
    @NotNull
    @Column(name = "software_price")
    private double softwarePrice;
    @JoinColumn(name = "software_c_id", referencedColumnName = "sc_id")
    @ManyToOne(optional = false)
    private Softwarecompany softwareCId;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "softwareOrderId")
    private Collection<Orders> ordersCollection;

    public Software() {
    }

    public Software(Integer softwareId) {
        this.softwareId = softwareId;
    }

    public Software(Integer softwareId, String softwareName, String softwareDesc, double softwarePrice) {
        this.softwareId = softwareId;
        this.softwareName = softwareName;
        this.softwareDesc = softwareDesc;
        this.softwarePrice = softwarePrice;
    }

    public Integer getSoftwareId() {
        return softwareId;
    }

    public void setSoftwareId(Integer softwareId) {
        this.softwareId = softwareId;
    }

    public String getSoftwareName() {
        return softwareName;
    }

    public void setSoftwareName(String softwareName) {
        this.softwareName = softwareName;
    }

    public String getSoftwareDesc() {
        return softwareDesc;
    }

    public void setSoftwareDesc(String softwareDesc) {
        this.softwareDesc = softwareDesc;
    }

    public double getSoftwarePrice() {
        return softwarePrice;
    }

    public void setSoftwarePrice(double softwarePrice) {
        this.softwarePrice = softwarePrice;
    }

    public Softwarecompany getSoftwareCId() {
        return softwareCId;
    }

    public void setSoftwareCId(Softwarecompany softwareCId) {
        this.softwareCId = softwareCId;
    }

    @XmlTransient
    public Collection<Orders> getOrdersCollection() {
        return ordersCollection;
    }

    public void setOrdersCollection(Collection<Orders> ordersCollection) {
        this.ordersCollection = ordersCollection;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (softwareId != null ? softwareId.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Software)) {
            return false;
        }
        Software other = (Software) object;
        if ((this.softwareId == null && other.softwareId != null) || (this.softwareId != null && !this.softwareId.equals(other.softwareId))) {
            return false;
        }
        return true;
    }

    public String sName() {
        return "" + softwareName;
    }

    public String sName1(){
        return ""+ softwareDesc;
    }
    
    public String sName2(){
        return ""+softwarePrice;
    }
    
    public String sName3(){
        return ""+softwareCId.cName();
    }
    
    @Override
    public String toString() {
        return "" + softwareName;
    }

}
