/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.metropolitan.controller;

import com.metropolitan.controller.exceptions.NonexistentEntityException;
import com.metropolitan.controller.exceptions.RollbackFailureException;
import com.metropolitan.entity.Orders;
import java.io.Serializable;
import javax.persistence.Query;
import javax.persistence.EntityNotFoundException;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import com.metropolitan.entity.User;
import com.metropolitan.entity.Software;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.transaction.UserTransaction;

/**
 *
 * @author PC
 */
public class OrdersJpaController implements Serializable {

    public OrdersJpaController(UserTransaction utx, EntityManagerFactory emf) {
        this.utx = utx;
        this.emf = emf;
    }
    private UserTransaction utx = null;
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(Orders orders) throws RollbackFailureException, Exception {
        EntityManager em = null;
        try {
            utx.begin();
            em = getEntityManager();
            User userOrderId = orders.getUserOrderId();
            if (userOrderId != null) {
                userOrderId = em.getReference(userOrderId.getClass(), userOrderId.getUserId());
                orders.setUserOrderId(userOrderId);
            }
            Software softwareOrderId = orders.getSoftwareOrderId();
            if (softwareOrderId != null) {
                softwareOrderId = em.getReference(softwareOrderId.getClass(), softwareOrderId.getSoftwareId());
                orders.setSoftwareOrderId(softwareOrderId);
            }
            em.persist(orders);
            if (userOrderId != null) {
                userOrderId.getOrdersCollection().add(orders);
                userOrderId = em.merge(userOrderId);
            }
            if (softwareOrderId != null) {
                softwareOrderId.getOrdersCollection().add(orders);
                softwareOrderId = em.merge(softwareOrderId);
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

    public void edit(Orders orders) throws NonexistentEntityException, RollbackFailureException, Exception {
        EntityManager em = null;
        try {
            utx.begin();
            em = getEntityManager();
            Orders persistentOrders = em.find(Orders.class, orders.getOrderId());
            User userOrderIdOld = persistentOrders.getUserOrderId();
            User userOrderIdNew = orders.getUserOrderId();
            Software softwareOrderIdOld = persistentOrders.getSoftwareOrderId();
            Software softwareOrderIdNew = orders.getSoftwareOrderId();
            if (userOrderIdNew != null) {
                userOrderIdNew = em.getReference(userOrderIdNew.getClass(), userOrderIdNew.getUserId());
                orders.setUserOrderId(userOrderIdNew);
            }
            if (softwareOrderIdNew != null) {
                softwareOrderIdNew = em.getReference(softwareOrderIdNew.getClass(), softwareOrderIdNew.getSoftwareId());
                orders.setSoftwareOrderId(softwareOrderIdNew);
            }
            orders = em.merge(orders);
            if (userOrderIdOld != null && !userOrderIdOld.equals(userOrderIdNew)) {
                userOrderIdOld.getOrdersCollection().remove(orders);
                userOrderIdOld = em.merge(userOrderIdOld);
            }
            if (userOrderIdNew != null && !userOrderIdNew.equals(userOrderIdOld)) {
                userOrderIdNew.getOrdersCollection().add(orders);
                userOrderIdNew = em.merge(userOrderIdNew);
            }
            if (softwareOrderIdOld != null && !softwareOrderIdOld.equals(softwareOrderIdNew)) {
                softwareOrderIdOld.getOrdersCollection().remove(orders);
                softwareOrderIdOld = em.merge(softwareOrderIdOld);
            }
            if (softwareOrderIdNew != null && !softwareOrderIdNew.equals(softwareOrderIdOld)) {
                softwareOrderIdNew.getOrdersCollection().add(orders);
                softwareOrderIdNew = em.merge(softwareOrderIdNew);
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
                Integer id = orders.getOrderId();
                if (findOrders(id) == null) {
                    throw new NonexistentEntityException("The orders with id " + id + " no longer exists.");
                }
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void destroy(Integer id) throws NonexistentEntityException, RollbackFailureException, Exception {
        EntityManager em = null;
        try {
            utx.begin();
            em = getEntityManager();
            Orders orders;
            try {
                orders = em.getReference(Orders.class, id);
                orders.getOrderId();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The orders with id " + id + " no longer exists.", enfe);
            }
            User userOrderId = orders.getUserOrderId();
            if (userOrderId != null) {
                userOrderId.getOrdersCollection().remove(orders);
                userOrderId = em.merge(userOrderId);
            }
            Software softwareOrderId = orders.getSoftwareOrderId();
            if (softwareOrderId != null) {
                softwareOrderId.getOrdersCollection().remove(orders);
                softwareOrderId = em.merge(softwareOrderId);
            }
            em.remove(orders);
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

    public List<Orders> findOrdersEntities() {
        return findOrdersEntities(true, -1, -1);
    }

    public List<Orders> findOrdersEntities(int maxResults, int firstResult) {
        return findOrdersEntities(false, maxResults, firstResult);
    }

    private List<Orders> findOrdersEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(Orders.class));
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

    public Orders findOrders(Integer id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(Orders.class, id);
        } finally {
            em.close();
        }
    }

    public int getOrdersCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<Orders> rt = cq.from(Orders.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }
    
}
