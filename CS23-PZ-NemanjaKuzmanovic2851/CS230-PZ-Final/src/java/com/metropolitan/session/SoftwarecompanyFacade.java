/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.metropolitan.session;

import com.metropolitan.entity.Softwarecompany;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

/**
 *
 * @author PC
 */
@Stateless
public class SoftwarecompanyFacade extends AbstractFacade<Softwarecompany> {

    @PersistenceContext(unitName = "CS230-PZ-FinalPU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public SoftwarecompanyFacade() {
        super(Softwarecompany.class);
    }
    
}
