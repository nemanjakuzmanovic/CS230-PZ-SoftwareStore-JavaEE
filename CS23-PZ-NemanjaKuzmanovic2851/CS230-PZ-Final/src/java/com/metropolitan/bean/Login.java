/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.metropolitan.bean;

import com.metropolitan.entity.Orders;
import com.metropolitan.entity.User;
import com.metropolitan.session.SessionUtils;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.servlet.http.HttpSession;

/**
 *
 * @author PC
 */
@ManagedBean
@SessionScoped
public class Login implements Serializable {

    private static final long serialVersionUID = 1L;

    @PersistenceContext(unitName = "CS230-PZ-FinalPU")
    private EntityManager em;

    private String userEmail;
    private String userPassword;
    private boolean loggedIn = false;
    private boolean isAdmin = false;

    public boolean isLoggedIn() {
        return loggedIn;
    }

    public boolean isAdmin() {
        return isAdmin;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }

    public String getUserPassword() {
        return userPassword;
    }

    public void setUserPassword(String userPassword) {
        this.userPassword = userPassword;
    }

    //validate login
    public String login() {
        User user = null;
        boolean valid;

        try {
            user = (User) em.createNamedQuery("User.findByUserEmail").setParameter("userEmail", userEmail).getSingleResult();
            valid = validateUser(user);
        } catch (NoResultException e) {
            valid = false;
        }

        if (valid) {
            HttpSession session = SessionUtils.getSession();
            session.setAttribute("data", user);

            loggedIn = true;
            if (userEmail.equalsIgnoreCase("admin@admin.com")) {
                isAdmin = true;

            }
            return "index";
        } else {
            FacesContext.getCurrentInstance().addMessage(
                    null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR,
                            "Wrong credentials",
                            "Please try again"));
            return "Login";
        }
    }

    private boolean validateUser(User user) {
        if (userEmail.equalsIgnoreCase(user.getUserEmail()) && userPassword.equals(user.getUserPassword())) {
            return true;
        }

        return false;
    }

    public int ajD() {
        int id = 0;

        try {
            HttpSession session = SessionUtils.getSession();
            String s = session.getAttribute("data").toString();
            s = s.substring(19, 20);
            id = Integer.parseInt(s);
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
        System.out.println(id);
        return id;
    }

    public List<Orders> getAllOrders() {
        ArrayList<Orders> list;
        list = new ArrayList<>(em.createNamedQuery("Orders.findOrdersByUserId", Orders.class).setParameter("userId", ajD()).getResultList());
        System.out.println("list " + list);
        return list;
    }

    public String logout() {
        HttpSession session = SessionUtils.getSession();
        session.invalidate();
        return "/index";
    }
}
