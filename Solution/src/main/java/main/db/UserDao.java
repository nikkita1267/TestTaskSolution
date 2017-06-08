package main.db;

import java.sql.SQLException;
import java.util.List;

public interface UserDao {
    void addUser(User user) throws SQLException;
    void updateUser(User user, int id) throws SQLException;
    void deleteUser(int id) throws SQLException;
    User getUser(int id) throws SQLException;
    List<User> getUsersByName(String name) throws SQLException;
    List<User> getAllUsers() throws SQLException;
}
