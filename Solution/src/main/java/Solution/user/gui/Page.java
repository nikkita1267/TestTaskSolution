package Solution.user.gui;

import com.vaadin.ui.Layout;
import Solution.user.User;
import Solution.user.dao.UserDao;
import com.vaadin.ui.Grid;

import java.sql.SQLException;

/**
 * Create page at {@see Grid}
 */
public class Page {
    public static final int MAX_COUNT_OF_ROWS = 10;
    private int startId = 1;
    private int endId;
    private Grid<User> grid;
    private UserDao dao;
    private UserUpdateMode mode;

    public Page(int startId, int endId, Grid<User> grid,
                UserDao dao, UserUpdateMode mode, String... params) {
        this.startId = startId;
        this.endId = endId;
        this.grid = grid;
        this.dao = dao;
        this.mode = mode;

        updateGridOfPage(params);
    }

    public int getStartId() {
        return startId;
    }

    public void setStartId(int startId) {
        this.startId = startId;
    }

    public int getEndId() {
        return endId;
    }

    public void setEndId(int endId) {
        this.endId = endId;
    }

    public void updateGridOfPage(String... params)
    {
        switch (mode) {
            case ALL:
                try {
                    grid.setItems(dao.getAllUsers().subList(startId, endId));
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            break;
            case NAME:
                try {
                    grid.setItems(dao.getUsersByName(params[0]).subList(startId, endId));
                } catch (SQLException e) {
                    e.printStackTrace();
                }
        }
    }
}
