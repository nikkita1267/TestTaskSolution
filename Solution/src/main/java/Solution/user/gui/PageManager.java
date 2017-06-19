package Solution.user.gui;

import Solution.user.User;
import Solution.user.dao.UserDao;
import com.vaadin.ui.Button;
import com.vaadin.ui.Grid;
import com.vaadin.ui.HorizontalLayout;

import java.sql.SQLException;
import java.util.LinkedList;
import java.util.List;

import static Solution.user.gui.Page.MAX_COUNT_OF_ROWS;

/**
 * This class manage pages
 */

public class PageManager {
    private Button nextButton = new Button("Next");
    private Button prevButton = new Button("Previous");
    private Grid<User> grid;
    private UserDao dao;
    private int countOfUsers;
    private LinkedList<Page> pages = new LinkedList<>();
    private int index = 0;
    private Page currentPage;
    private HorizontalLayout footerLayout;
    private UserUpdateMode mode;
    private String[] params;

    public PageManager(Grid<User> grid, UserDao dao, int countOfUsers, HorizontalLayout footerLayout, UserUpdateMode mode, String... params) {
        this.grid = grid;
        this.dao = dao;
        this.countOfUsers = countOfUsers;
        this.footerLayout = footerLayout;
        this.mode = mode;
        this.params = params;
        init();
    }

    private void init()
    {
        int startId = 0, endId = MAX_COUNT_OF_ROWS - 1;
        for (int i = 0; i < countOfUsers / (MAX_COUNT_OF_ROWS - 1);
             i++, startId += MAX_COUNT_OF_ROWS - 1, endId += MAX_COUNT_OF_ROWS - 1)
            pages.add(new Page(startId, endId, grid, dao, mode, params));
        pages.add(new Page(startId, countOfUsers, grid, dao, mode, params));
        currentPage = pages.get(index);

        nextButton.addClickListener(clickEvent -> {
            if (index != pages.size() - 1)
                index++;
            updatePage();

            if (index != 0) {
                this.footerLayout.removeComponent(nextButton);
                this.footerLayout.addComponent(prevButton);
                footerLayout.addComponent(nextButton);
            }
            if (index == pages.size() - 1)
                this.footerLayout.removeComponent(nextButton);

            currentPage.updateGridOfPage(params);
        });

        prevButton.addClickListener(clickEvent -> {
            if (index != 0)
                index--;
            updatePage();

            if (index == 0)
                this.footerLayout.removeComponent(prevButton);
            if (index == pages.size() - 2) {
                this.footerLayout.addComponent(nextButton);
            }

            currentPage.updateGridOfPage(params);
        });

        if (countOfUsers > MAX_COUNT_OF_ROWS)
            this.footerLayout.addComponent(nextButton);

        currentPage.updateGridOfPage(params);
    }

    public void update()
    {
        try {
            countOfUsers = dao.getAllUsers().size();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        pages.clear();

        int startId = 0, endId = MAX_COUNT_OF_ROWS - 1;
        for (int i = 0; i < countOfUsers / (MAX_COUNT_OF_ROWS - 1);
             i++, startId += MAX_COUNT_OF_ROWS - 1, endId += MAX_COUNT_OF_ROWS - 1)
            pages.add(new Page(startId, endId, grid, dao, mode, params));
        pages.add(new Page(startId, countOfUsers, grid, dao, mode, params));
        currentPage = pages.get(index);
        currentPage.updateGridOfPage(params);

        if (currentPage.getEndId() == countOfUsers)
            footerLayout.removeComponent(nextButton);
    }

    public UserUpdateMode getMode() {
        return mode;
    }

    public int getCountOfUsers() {
        return countOfUsers;
    }

    public void setCountOfUsers(int countOfUsers) {
        this.countOfUsers = countOfUsers;
    }

    public void addPage(Page page) {
        pages.add(page);
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public Page getCurrentPage() {
        return currentPage;
    }

    public void updatePage()
    {
        currentPage = pages.get(index);
    }

    public int getPagesSize()
    {
        return pages.size();
    }
}
