package main.db;

import org.hibernate.Session;
import org.hibernate.Transaction;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class UserDaoImpl implements UserDao {
    @Override
    public void addUser(User user) throws SQLException {
        Session session = null;
        Transaction transaction = null;
        try {
            session = HibernateUtil.getSessionFactory().openSession();
            transaction = session.beginTransaction();
            session.saveOrUpdate(user);
            transaction.commit();
        } catch (Exception e)
        {
            if (transaction != null)
                transaction.rollback();
            throw new SQLException(e);
        } finally {
            if (session != null && session.isOpen())
                session.close();
        }
    }

    @Override
    public void updateUser(User user, int id) throws SQLException {
        Session session = null;
        Transaction transaction = null;
        try {
            session = HibernateUtil.getSessionFactory().openSession();
            transaction = session.beginTransaction();
            User us = getUser(id);
            us.setIsAdmin(user.getIsAdmin());
            us.setAge(user.getAge());
            us.setName(user.getName());
            us.setCreatedDate(user.getCreatedDate());

            session.update(us);

            transaction.commit();
        } catch (Exception e)
        {
            if (transaction != null)
                transaction.rollback();
            throw new SQLException(e);
        } finally {
            if (session != null && session.isOpen())
                session.close();
        }
    }

    @Override
    public void deleteUser(int id) throws SQLException {
        Session session = null;
        Transaction transaction = null;
        try {
            session = HibernateUtil.getSessionFactory().openSession();
            transaction = session.beginTransaction();
            session.delete(getUser(id));
            transaction.commit();
        } catch (Exception e)
        {
            if (transaction != null)
                transaction.rollback();
            throw new SQLException(e);
        } finally {
            if (session != null && session.isOpen())
                session.close();
        }
    }

    @Override
    public User getUser(int id) throws SQLException {
        User res;
        Session session = null;
        Transaction transaction = null;
        try {
            session = HibernateUtil.getSessionFactory().openSession();
            transaction = session.beginTransaction();
            res = session.get(User.class, id);
            transaction.commit();
        } catch (Exception e)
        {
            if (transaction != null)
                transaction.rollback();
            throw new SQLException(e);
        } finally {
            if (session != null && session.isOpen())
                session.close();
        }
        return res;
    }

    @Override
    public List<User> getUsersByName(String name) throws SQLException {
        List<User> result = getAllUsers();

        for (User user : new ArrayList<>(result))
        {
            if (!user.getName().equals(name))
                result.remove(user);
        }

        return result;
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<User> getAllUsers() throws SQLException {
        Session session = null;
        List users;
        Transaction transaction = null;
        try {
            session = HibernateUtil.getSessionFactory().openSession();
            transaction = session.beginTransaction();
            users = session.createQuery("FROM main.db.User").list();
            transaction.commit();
        } catch (Exception e)
        {
            if (transaction != null)
                transaction.rollback();
            throw new SQLException(e);
        } finally {
            if (session != null && session.isOpen())
                session.close();
        }
        return users;
    }
}
