/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.metropolitan.controller;

import com.metropolitan.controller.exceptions.IllegalOrphanException;
import com.metropolitan.controller.exceptions.NonexistentEntityException;
import com.metropolitan.controller.exceptions.RollbackFailureException;
import java.io.Serializable;
import javax.persistence.Query;
import javax.persistence.EntityNotFoundException;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import com.metropolitan.entity.Software;
import com.metropolitan.entity.Softwarecompany;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.transaction.UserTransaction;

/**
 *
 * @author PC
 */
public class SoftwarecompanyJpaController implements Serializable {

    public SoftwarecompanyJpaController(UserTransaction utx, EntityManagerFactory emf) {
        this.utx = utx;
        this.emf = emf;
    }
    private UserTransaction utx = null;
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(Softwarecompany softwarecompany) throws RollbackFailureException, Exception {
        if (softwarecompany.getSoftwareCollection() == null) {
            softwarecompany.setSoftwareCollection(new ArrayList<Software>());
        }
        EntityManager em = null;
        try {
            utx.begin();
            em = getEntityManager();
            Collection<Software> attachedSoftwareCollection = new ArrayList<Software>();
            for (Software softwareCollectionSoftwareToAttach : softwarecompany.getSoftwareCollection()) {
                softwareCollectionSoftwareToAttach = em.getReference(softwareCollectionSoftwareToAttach.getClass(), softwareCollectionSoftwareToAttach.getSoftwareId());
                attachedSoftwareCollection.add(softwareCollectionSoftwareToAttach);
            }
            softwarecompany.setSoftwareCollection(attachedSoftwareCollection);
            em.persist(softwarecompany);
            for (Software softwareCollectionSoftware : softwarecompany.getSoftwareCollection()) {
                Softwarecompany oldSoftwareCIdOfSoftwareCollectionSoftware = softwareCollectionSoftware.getSoftwareCId();
                softwareCollectionSoftware.setSoftwareCId(softwarecompany);
                softwareCollectionSoftware = em.merge(softwareCollectionSoftware);
                if (oldSoftwareCIdOfSoftwareCollectionSoftware != null) {
                    oldSoftwareCIdOfSoftwareCollectionSoftware.getSoftwareCollection().remove(softwareCollectionSoftware);
                    oldSoftwareCIdOfSoftwareCollectionSoftware = em.merge(oldSoftwareCIdOfSoftwareCollectionSoftware);
                }
            }
            utx.commit();
        } catch (Exception ex) {
            try {
                utx.rollback();
            } catch (Exception re) {
                throw new RollbackFailureException("An error occurred attempting to roll back the transaction.", re);
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void edit(Softwarecompany softwarecompany) throws IllegalOrphanException, NonexistentEntityException, RollbackFailureException, Exception {
        EntityManager em = null;
        try {
            utx.begin();
            em = getEntityManager();
            Softwarecompany persistentSoftwarecompany = em.find(Softwarecompany.class, softwarecompany.getScId());
            Collection<Software> softwareCollectionOld = persistentSoftwarecompany.getSoftwareCollection();
            Collection<Software> softwareCollectionNew = softwarecompany.getSoftwareCollection();
            List<String> illegalOrphanMessages = null;
            for (Software softwareCollectionOldSoftware : softwareCollectionOld) {
                if (!softwareCollectionNew.contains(softwareCollectionOldSoftware)) {
                    if (illegalOrphanMessages == null) {
                        illegalOrphanMessages = new ArrayList<String>();
                    }
                    illegalOrphanMessages.add("You must retain Software " + softwareCollectionOldSoftware + " since its softwareCId field is not nullable.");
                }
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            Collection<Software> attachedSoftwareCollectionNew = new ArrayList<Software>();
            for (Software softwareCollectionNewSoftwareToAttach : softwareCollectionNew) {
                softwareCollectionNewSoftwareToAttach = em.getReference(softwareCollectionNewSoftwareToAttach.getClass(), softwareCollectionNewSoftwareToAttach.getSoftwareId());
                attachedSoftwareCollectionNew.add(softwareCollectionNewSoftwareToAttach);
            }
            softwareCollectionNew = attachedSoftwareCollectionNew;
            softwarecompany.setSoftwareCollection(softwareCollectionNew);
            softwarecompany = em.merge(softwarecompany);
            for (Software softwareCollectionNewSoftware : softwareCollectionNew) {
                if (!softwareCollectionOld.contains(softwareCollectionNewSoftware)) {
                    Softwarecompany oldSoftwareCIdOfSoftwareCollectionNewSoftware = softwareCollectionNewSoftware.getSoftwareCId();
                    softwareCollectionNewSoftware.setSoftwareCId(softwarecompany);
                    softwareCollectionNewSoftware = em.merge(softwareCollectionNewSoftware);
                    if (oldSoftwareCIdOfSoftwareCollectionNewSoftware != null && !oldSoftwareCIdOfSoftwareCollectionNewSoftware.equals(softwarecompany)) {
                        oldSoftwareCIdOfSoftwareCollectionNewSoftware.getSoftwareCollection().remove(softwareCollectionNewSoftware);
                        oldSoftwareCIdOfSoftwareCollectionNewSoftware = em.merge(oldSoftwareCIdOfSoftwareCollectionNewSoftware);
                    }
                }
            }
            utx.commit();
        } catch (Exception ex) {
            try {
                utx.rollback();
            } catch (Exception re) {
                throw new RollbackFailureException("An error occurred attempting to roll back the transaction.", re);
            }
            String msg = ex.getLocalizedMessage();
            if (msg == null || msg.length() == 0) {
                Integer id = softwarecompany.getScId();
                if (findSoftwarecompany(id) == null) {
                    throw new NonexistentEntityException("The softwarecompany with id " + id + " no longer exists.");
                }
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void destroy(Integer id) throws IllegalOrphanException, NonexistentEntityException, RollbackFailureException, Exception {
        EntityManager em = null;
        try {
            utx.begin();
            em = getEntityManager();
            Softwarecompany softwarecompany;
            try {
                softwarecompany = em.getReference(Softwarecompany.class, id);
                softwarecompany.getScId();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The softwarecompany with id " + id + " no longer exists.", enfe);
            }
            List<String> illegalOrphanMessages = null;
            Collection<Software> softwareCollectionOrphanCheck = softwarecompany.getSoftwareCollection();
            for (Software softwareCollectionOrphanCheckSoftware : softwareCollectionOrphanCheck) {
                if (illegalOrphanMessages == null) {
                    illegalOrphanMessages = new ArrayList<String>();
                }
                illegalOrphanMessages.add("This Softwarecompany (" + softwarecompany + ") cannot be destroyed since the Software " + softwareCollectionOrphanCheckSoftware + " in its softwareCollection field has a non-nullable softwareCId field.");
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            em.remove(softwarecompany);
            utx.commit();
        } catch (Exception ex) {
            try {
                utx.rollback();
            } catch (Exception re) {
                throw new RollbackFailureException("An error occurred attempting to roll back the transaction.", re);
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public List<Softwarecompany> findSoftwarecompanyEntities() {
        return findSoftwarecompanyEntities(true, -1, -1);
    }

    public List<Softwarecompany> findSoftwarecompanyEntities(int maxResults, int firstResult) {
        return findSoftwarecompanyEntities(false, maxResults, firstResult);
    }

    private List<Softwarecompany> findSoftwarecompanyEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(Softwarecompany.class));
            Query q = em.createQuery(cq);
            if (!all) {
                q.setMaxResults(maxResults);
                q.setFirstResult(firstResult);
            }
            return q.getResultList();
        } finally {
            em.close();
        }
    }

    public Softwarecompany findSoftwarecompany(Integer id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(Softwarecompany.class, id);
        } finally {
            em.close();
        }
    }

    public int getSoftwarecompanyCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<Softwarecompany> rt = cq.from(Softwarecompany.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }
    
}
