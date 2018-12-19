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
@Table(name = "softwarecompany")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Softwarecompany.findAll", query = "SELECT s FROM Softwarecompany s")
    , @NamedQuery(name = "Softwarecompany.findByScId", query = "SELECT s FROM Softwarecompany s WHERE s.scId = :scId")
    , @NamedQuery(name = "Softwarecompany.findByScName", query = "SELECT s FROM Softwarecompany s WHERE s.scName = :scName")})
public class Softwarecompany implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "sc_id")
    private Integer scId;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 255)
    @Column(name = "sc_name")
    private String scName;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "softwareCId")
    private Collection<Software> softwareCollection;

    public Softwarecompany() {
    }

    public Softwarecompany(Integer scId) {
        this.scId = scId;
    }

    public Softwarecompany(Integer scId, String scName) {
        this.scId = scId;
        this.scName = scName;
    }

    public Integer getScId() {
        return scId;
    }

    public void setScId(Integer scId) {
        this.scId = scId;
    }

    public String getScName() {
        return scName;
    }

    public void setScName(String scName) {
        this.scName = scName;
    }

    @XmlTransient
    public Collection<Software> getSoftwareCollection() {
        return softwareCollection;
    }

    public void setSoftwareCollection(Collection<Software> softwareCollection) {
        this.softwareCollection = softwareCollection;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (scId != null ? scId.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Softwarecompany)) {
            return false;
        }
        Softwarecompany other = (Softwarecompany) object;
        if ((this.scId == null && other.scId != null) || (this.scId != null && !this.scId.equals(other.scId))) {
            return false;
        }
        return true;
    }
    
    public String cName(){
        return ""+scName;
    }

    @Override
    public String toString() {
        return ""+scName;
    }
    
}
