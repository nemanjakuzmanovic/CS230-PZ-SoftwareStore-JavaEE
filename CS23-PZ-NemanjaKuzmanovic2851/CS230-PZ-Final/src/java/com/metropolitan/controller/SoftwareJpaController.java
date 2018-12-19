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
import com.metropolitan.entity.Softwarecompany;
import com.metropolitan.entity.Orders;
import com.metropolitan.entity.Software;
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
public class SoftwareJpaController implements Serializable {

    public SoftwareJpaController(UserTransaction utx, EntityManagerFactory emf) {
        this.utx = utx;
        this.emf = emf;
    }
    private UserTransaction utx = null;
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(Software software) throws RollbackFailureException, Exception {
        if (software.getOrdersCollection() == null) {
            software.setOrdersCollection(new ArrayList<Orders>());
        }
        EntityManager em = null;
        try {
            utx.begin();
            em = getEntityManager();
            Softwarecompany softwareCId = software.getSoftwareCId();
            if (softwareCId != null) {
                softwareCId = em.getReference(softwareCId.getClass(), softwareCId.getScId());
                software.setSoftwareCId(softwareCId);
            }
            Collection<Orders> attachedOrdersCollection = new ArrayList<Orders>();
            for (Orders ordersCollectionOrdersToAttach : software.getOrdersCollection()) {
                ordersCollectionOrdersToAttach = em.getReference(ordersCollectionOrdersToAttach.getClass(), ordersCollectionOrdersToAttach.getOrderId());
                attachedOrdersCollection.add(ordersCollectionOrdersToAttach);
            }
            software.setOrdersCollection(attachedOrdersCollection);
            em.persist(software);
            if (softwareCId != null) {
                softwareCId.getSoftwareCollection().add(software);
                softwareCId = em.merge(softwareCId);
            }
            for (Orders ordersCollectionOrders : software.getOrdersCollection()) {
                Software oldSoftwareOrderIdOfOrdersCollectionOrders = ordersCollectionOrders.getSoftwareOrderId();
                ordersCollectionOrders.setSoftwareOrderId(software);
                ordersCollectionOrders = em.merge(ordersCollectionOrders);
                if (oldSoftwareOrderIdOfOrdersCollectionOrders != null) {
                    oldSoftwareOrderIdOfOrdersCollectionOrders.getOrdersCollection().remove(ordersCollectionOrders);
                    oldSoftwareOrderIdOfOrdersCollectionOrders = em.merge(oldSoftwareOrderIdOfOrdersCollectionOrders);
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

    public void edit(Software software) throws IllegalOrphanException, NonexistentEntityException, RollbackFailureException, Exception {
        EntityManager em = null;
        try {
            utx.begin();
            em = getEntityManager();
            Software persistentSoftware = em.find(Software.class, software.getSoftwareId());
            Softwarecompany softwareCIdOld = persistentSoftware.getSoftwareCId();
            Softwarecompany softwareCIdNew = software.getSoftwareCId();
            Collection<Orders> ordersCollectionOld = persistentSoftware.getOrdersCollection();
            Collection<Orders> ordersCollectionNew = software.getOrdersCollection();
            List<String> illegalOrphanMessages = null;
            for (Orders ordersCollectionOldOrders : ordersCollectionOld) {
                if (!ordersCollectionNew.contains(ordersCollectionOldOrders)) {
                    if (illegalOrphanMessages == null) {
                        illegalOrphanMessages = new ArrayList<String>();
                    }
                    illegalOrphanMessages.add("You must retain Orders " + ordersCollectionOldOrders + " since its softwareOrderId field is not nullable.");
                }
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            if (softwareCIdNew != null) {
                softwareCIdNew = em.getReference(softwareCIdNew.getClass(), softwareCIdNew.getScId());
                software.setSoftwareCId(softwareCIdNew);
            }
            Collection<Orders> attachedOrdersCollectionNew = new ArrayList<Orders>();
            for (Orders ordersCollectionNewOrdersToAttach : ordersCollectionNew) {
                ordersCollectionNewOrdersToAttach = em.getReference(ordersCollectionNewOrdersToAttach.getClass(), ordersCollectionNewOrdersToAttach.getOrderId());
                attachedOrdersCollectionNew.add(ordersCollectionNewOrdersToAttach);
            }
            ordersCollectionNew = attachedOrdersCollectionNew;
            software.setOrdersCollection(ordersCollectionNew);
            software = em.merge(software);
            if (softwareCIdOld != null && !softwareCIdOld.equals(softwareCIdNew)) {
                softwareCIdOld.getSoftwareCollection().remove(software);
                softwareCIdOld = em.merge(softwareCIdOld);
            }
            if (softwareCIdNew != null && !softwareCIdNew.equals(softwareCIdOld)) {
                softwareCIdNew.getSoftwareCollection().add(software);
                softwareCIdNew = em.merge(softwareCIdNew);
            }
            for (Orders ordersCollectionNewOrders : ordersCollectionNew) {
                if (!ordersCollectionOld.contains(ordersCollectionNewOrders)) {
                    Software oldSoftwareOrderIdOfOrdersCollectionNewOrders = ordersCollectionNewOrders.getSoftwareOrderId();
                    ordersCollectionNewOrders.setSoftwareOrderId(software);
                    ordersCollectionNewOrders = em.merge(ordersCollectionNewOrders);
                    if (oldSoftwareOrderIdOfOrdersCollectionNewOrders != null && !oldSoftwareOrderIdOfOrdersCollectionNewOrders.equals(software)) {
                        oldSoftwareOrderIdOfOrdersCollectionNewOrders.getOrdersCollection().remove(ordersCollectionNewOrders);
                        oldSoftwareOrderIdOfOrdersCollectionNewOrders = em.merge(oldSoftwareOrderIdOfOrdersCollectionNewOrders);
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
                Integer id = software.getSoftwareId();
                if (findSoftware(id) == null) {
                    throw new NonexistentEntityException("The software with id " + id + " no longer exists.");
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
            Software software;
            try {
                software = em.getReference(Software.class, id);
                software.getSoftwareId();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The software with id " + id + " no longer exists.", enfe);
            }
            List<String> illegalOrphanMessages = null;
            Collection<Orders> ordersCollectionOrphanCheck = software.getOrdersCollection();
            for (Orders ordersCollectionOrphanCheckOrders : ordersCollectionOrphanCheck) {
                if (illegalOrphanMessages == null) {
                    illegalOrphanMessages = new ArrayList<String>();
                }
                illegalOrphanMessages.add("This Software (" + software + ") cannot be destroyed since the Orders " + ordersCollectionOrphanCheckOrders + " in its ordersCollection field has a non-nullable softwareOrderId field.");
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            Softwarecompany softwareCId = software.getSoftwareCId();
            if (softwareCId != null) {
                softwareCId.getSoftwareCollection().remove(software);
                softwareCId = em.merge(softwareCId);
            }
            em.remove(software);
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

    public List<Software> findSoftwareEntities() {
        return findSoftwareEntities(true, -1, -1);
    }

    public List<Software> findSoftwareEntities(int maxResults, int firstResult) {
        return findSoftwareEntities(false, maxResults, firstResult);
    }

    private List<Software> findSoftwareEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(Software.class));
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

    public Software findSoftware(Integer id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(Software.class, id);
        } finally {
            em.close();
        }
    }

    public int getSoftwareCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<Software> rt = cq.from(Software.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }
    
}
