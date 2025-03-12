package com.project;

import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.boot.registry.StandardServiceRegistry;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.cfg.Configuration;
import org.hibernate.query.NativeQuery;

import java.io.Serializable;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Manager {
    private static SessionFactory factory;

    public static void createSessionFactory() {
        try {
            Configuration configuration = new Configuration();

            // Add the mapping resources instead of annotated classes
            configuration.addResource("Ciutat.hbm.xml");
            configuration.addResource("Ciutada.hbm.xml");

            StandardServiceRegistry serviceRegistry = new StandardServiceRegistryBuilder()
                    .applySettings(configuration.getProperties())
                    .build();

            factory = configuration.buildSessionFactory(serviceRegistry);
        } catch (Throwable ex) {
            System.err.println("Failed to create sessionFactory object." + ex);
            throw new ExceptionInInitializerError(ex);
        }
    }

    public static void close () {
        factory.close();
    }

    public static Ciutat addCiutat(String nom,String pais,int poblacio){
        Session session = factory.openSession();
        Transaction tx = null;
        Ciutat result = null;
        try {
            tx = session.beginTransaction();
            result = new Ciutat(nom,pais,poblacio);
            session.persist(result);
            tx.commit();
        } catch (HibernateException e) {
            if (tx!=null) tx.rollback();
            e.printStackTrace();
            result = null;
        } finally {
            session.close();
        }
        return result;
    }

    public static Ciutada addCiutada(String name,String cognom,int edat){
        Session session = factory.openSession();
        Transaction tx = null;
        Ciutada result = null;
        try {
            tx = session.beginTransaction();
            result = new Ciutada(name,cognom,edat);
            session.persist(result);
            tx.commit();
        } catch (HibernateException e) {
            if (tx!=null) tx.rollback();
            e.printStackTrace();
            result = null;
        } finally {
            session.close();
        }
        return result;
    }

    public static void updateCiutada(long ciutadaId, String name,String cognom,int edad){
        Session session = factory.openSession();
        Transaction tx = null;
        try {
            tx = session.beginTransaction();
            Ciutada obj = (Ciutada) session.get(Ciutada.class, ciutadaId);
            obj.setNom(name);
            obj.setCognom(cognom);
            obj.setEdat(edad);
            session.merge(obj);
            tx.commit();
        } catch (HibernateException e) {
            if (tx!=null) tx.rollback();
            e.printStackTrace();
        } finally {
            session.close();
        }
    }

    public static void updateCiutat(long ciutatId, String nom, String pais,int poblacio,Set<Ciutada> ciutadans) {
        Session session = factory.openSession();
        Transaction tx = null;
        try {
            tx = session.beginTransaction();

            Ciutat ciut = session.get(Ciutat.class, ciutatId);
            if (ciut == null) {
                throw new RuntimeException("Cart not found with id: " + ciutatId);
            }

            ciut.setNom(nom);
            ciut.setPais(pais);
            ciut.setPoblacio(poblacio);
            if (ciut.getCiutadans() != null) {
                for (Ciutada oldItem : new HashSet<>(ciut.getCiutadans())) {
                    ciut.removeItem(oldItem);
                }
            }

            if (ciutadans != null) {
                for (Ciutada ciuta : ciutadans) {
                    Ciutada managedItem = session.get(Ciutada.class, ciuta.getCiutadaId());
                    if (managedItem != null) {
                        ciut.addItem(managedItem);
                    }
                }
            }

            session.merge(ciut);
            tx.commit();

        } catch (HibernateException e) {
            if (tx != null) tx.rollback();
            e.printStackTrace();
        } finally {
            session.close();
        }
    }

    public static Ciutat getCiutatWithCiutadans(long cartId) {
        Ciutat ciut;
        try (Session session = factory.openSession()) {
            Transaction tx = session.beginTransaction();
            ciut = session.get(Ciutat.class, cartId);
            // Eagerly fetch the items collection
            ciut.getCiutadans().size();
            tx.commit();
        }
        return ciut;
    }

    public static <T> T getById(Class<? extends T> clazz, long id){
        Session session = factory.openSession();
        Transaction tx = null;
        T obj = null;
        try {
            tx = session.beginTransaction();
            obj = clazz.cast(session.get(clazz, id));
            tx.commit();
        } catch (HibernateException e) {
            if (tx!=null) tx.rollback();
            e.printStackTrace();
        } finally {
            session.close();
        }
        return obj;
    }

    public static <T> void delete(Class<? extends T> clazz, Serializable id) {
        Session session = factory.openSession();
        Transaction tx = null;
        try {
            tx = session.beginTransaction();
            T obj = clazz.cast(session.get(clazz, id));
            if (obj != null) {  // Only try to remove if the object exists
                session.remove(obj);
                tx.commit();
            }
        } catch (HibernateException e) {
            if (tx != null) tx.rollback();
            e.printStackTrace();
        } finally {
            session.close();
        }
    }

    public static <T> Collection<?> listCollection(Class<? extends T> clazz) {
        return listCollection(clazz, "");
    }

    public static <T> Collection<?> listCollection(Class<? extends T> clazz, String where){
        Session session = factory.openSession();
        Transaction tx = null;
        Collection<?> result = null;
        try {
            tx = session.beginTransaction();
            if (where.length() == 0) {
                result = session.createQuery("FROM " + clazz.getName(), clazz).list(); // Added class parameter
            } else {
                result = session.createQuery("FROM " + clazz.getName() + " WHERE " + where, clazz).list();
            }
            tx.commit();
        } catch (HibernateException e) {
            if (tx!=null) tx.rollback();
            e.printStackTrace();
        } finally {
            session.close();
        }
        return result;
    }

    public static <T> String collectionToString(Class<? extends T> clazz, Collection<?> collection){
        String txt = "";
        for(Object obj: collection) {
            T cObj = clazz.cast(obj);
            txt += "\n" + cObj.toString();
        }
        if (txt.length() > 0 && txt.substring(0, 1).compareTo("\n") == 0) {
            txt = txt.substring(1);
        }
        return txt;
    }

    public static void queryUpdate(String queryString) {
        Session session = factory.openSession();
        Transaction tx = null;
        try {
            tx = session.beginTransaction();
            NativeQuery<?> query = session.createNativeQuery(queryString, Void.class); // Updated to NativeQuery
            query.executeUpdate();
            tx.commit();
        } catch (HibernateException e) {
            if (tx!=null) tx.rollback();
            e.printStackTrace();
        } finally {
            session.close();
        }
    }

    public static List<Object[]> queryTable(String queryString) {
        Session session = factory.openSession();
        Transaction tx = null;
        List<Object[]> result = null;
        try {
            tx = session.beginTransaction();
            NativeQuery<Object[]> query = session.createNativeQuery(queryString, Object[].class); // Updated to NativeQuery
            result = query.getResultList(); // Changed from list() to getResultList()
            tx.commit();
        } catch (HibernateException e) {
            if (tx!=null) tx.rollback();
            e.printStackTrace();
        } finally {
            session.close();
        }
        return result;
    }

    public static String tableToString(List<Object[]> rows) {
        String txt = "";
        for (Object[] row : rows) {
            for (Object cell : row) {
                txt += cell.toString() + ", ";
            }
            if (txt.length() >= 2 && txt.substring(txt.length() - 2).compareTo(", ") == 0) {
                txt = txt.substring(0, txt.length() - 2);
            }
            txt += "\n";
        }
        if (txt.length() >= 2) {
            txt = txt.substring(0, txt.length() - 1);
        }
        return txt;
    }
}
