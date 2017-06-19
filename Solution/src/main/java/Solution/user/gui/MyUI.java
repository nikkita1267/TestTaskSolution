package Solution.user.gui;

import javax.servlet.annotation.WebServlet;

import Solution.user.User;
import Solution.user.UserAdminStatus;
import Solution.user.dao.UserDao;
import com.vaadin.annotations.Theme;
import com.vaadin.annotations.VaadinServletConfiguration;
import com.vaadin.server.VaadinRequest;
import com.vaadin.server.VaadinServlet;
import com.vaadin.ui.*;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.sql.Date;
import java.sql.SQLException;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

import static Solution.user.gui.Page.MAX_COUNT_OF_ROWS;

/**
 * This UI is the application entry point. A UI may either represent a browser window 
 * (or tab) or some part of a html page where a Vaadin application is embedded.
 * <p>
 * The UI is initialized using {@link #init(VaadinRequest)}. This method is intended to be 
 * overridden to add component to the user interface and initialize non-component functionality.
 */
@Theme("mytheme")
public class MyUI extends UI {
    private List<User> users;
    @Override
    protected void init(VaadinRequest vaadinRequest) {
        final Label userCreatedLabel = new Label("User created!");
        final Label userUpdatedLabel = new Label("User updated!");
        final Label userDeletedLabel = new Label("User deleted!");
        VerticalLayout vertLayout = new VerticalLayout();
        VerticalLayout verticalLayout = new VerticalLayout();
        final HorizontalLayout layout = new HorizontalLayout();
        VerticalLayout layoutForInputAndOutput = new VerticalLayout();
        final HorizontalLayout footerLayout = new HorizontalLayout();
        ApplicationContext context = new ClassPathXmlApplicationContext("spring-config.xml");
        UserDao dao = (UserDao) context.getBean("standardDaoImpl");
        MenuBar bar = new MenuBar();
        Grid<User> grid = new Grid<>(User.class);
        grid.setColumns("id", "name", "age", "isAdmin", "createdDate");
        try {
            users = dao.getAllUsers();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        PageManager manager = new PageManager(grid, dao, users.size(), footerLayout, UserUpdateMode.ALL);
        manager.updatePage();

        bar.addItem("Create", menuItem -> {
            layoutForInputAndOutput.removeAllComponents();
            TextField name = new TextField("Enter the user's name");
            TextField age = new TextField("Enter the user's age");
            NativeSelect<UserAdminStatus> status = new NativeSelect<>("Is user the admin?");
            status.setItems(UserAdminStatus.values());
            status.setSelectedItem(UserAdminStatus.No);
            status.setEmptySelectionAllowed(false);
            Button addUserButton = new Button("Create User!");
            addUserButton.addClickListener(clickEvent -> {
                boolean isAllFine = checkInput(null, name, age);
                boolean isAdmin = readIsAdmin(status.getSelectedItem());
                try {
                    if (isAllFine) {
                        layoutForInputAndOutput.removeComponent(userCreatedLabel);
                        dao.addUser(new User(name.getValue(), Integer.parseInt(age.getValue()), isAdmin, new Date(new java.util.Date().getTime())));
                        layoutForInputAndOutput.addComponent(userCreatedLabel);
                        try {
                            users = dao.getAllUsers();
                        } catch (SQLException e) {
                            e.printStackTrace();
                        }
                        if (manager.getIndex() == manager.getPagesSize() - 1) {
                            if (manager.getCurrentPage().getEndId() - manager.getCurrentPage().getStartId() < MAX_COUNT_OF_ROWS - 1) {
                                manager.getCurrentPage().setEndId(manager.getCurrentPage().getEndId() + 1);
                            } else {
                                manager.addPage(new Page(manager.getCurrentPage().getEndId(), users.size(), grid, dao, manager.getMode()));
                                manager.setIndex(manager.getIndex() + 1);
                                manager.updatePage();
                            }

                            manager.getCurrentPage().updateGridOfPage();
                        }
                    }
                } catch (SQLException e) {
                    e.printStackTrace();
                    layoutForInputAndOutput.addComponent(new Label(e.toString()));
                    for (StackTraceElement element : e.getStackTrace())
                        layoutForInputAndOutput.addComponent(new Label(element.toString()));
                }
            });

            layoutForInputAndOutput.addComponents(name, age, status, addUserButton);
        });

        bar.addItem("Read user by name", menuItem -> {
            layoutForInputAndOutput.removeAllComponents();
            TextField name = new TextField("Enter the user's name");
            HorizontalLayout footerLayoutForName = new HorizontalLayout();

            Button findButton = new Button("Read!");
            findButton.addClickListener(clickEvent -> {
                boolean isAllFine = checkInput(null, name, null);
                if (isAllFine)
                {
                    Grid<User> gridForUsersFoundByName = new Grid<>(User.class);
                    gridForUsersFoundByName.setColumns("id", "name", "age", "isAdmin", "createdDate");
                    try {
                        List<User> usersWithName = dao.getUsersByName(name.getValue());
                        new PageManager(gridForUsersFoundByName, dao, usersWithName.size(), footerLayoutForName, UserUpdateMode.NAME, name.getValue());
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                    layoutForInputAndOutput.addComponent(gridForUsersFoundByName);
                    layoutForInputAndOutput.addComponent(footerLayoutForName);
                }
            });

            layoutForInputAndOutput.addComponents(name, findButton);
        });

        grid.setSizeFull();

        bar.addItem("Update", menuItem -> {
            layoutForInputAndOutput.removeAllComponents();
            TextField id = new TextField("Enter the user's id");
            TextField name = new TextField("Enter the user's name");
            TextField age = new TextField("Enter the user's age");
            NativeSelect<UserAdminStatus> status = new NativeSelect<>("Is user the admin?");
            status.setEmptySelectionAllowed(false);
            status.setItems(UserAdminStatus.values());
            status.setSelectedItem(UserAdminStatus.No);
            Button addUserButton = new Button("Update User!");
            addUserButton.addClickListener(clickEvent -> {
                boolean isAllFine = checkInput(id, name, age);
                boolean isAdmin = readIsAdmin(status.getSelectedItem());
                try {
                    if (isAllFine) {
                        layoutForInputAndOutput.removeComponent(userUpdatedLabel);
                        dao.updateUser(new User(name.getValue(), Integer.parseInt(age.getValue()), isAdmin, new Date(new java.util.Date().getTime())),
                                Integer.parseInt(id.getValue()));
                        layoutForInputAndOutput.addComponent(userUpdatedLabel);

                    }

                    manager.getCurrentPage().updateGridOfPage();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            });

            layoutForInputAndOutput.addComponents(id, name, age, status, addUserButton);
        });

        bar.addItem("Delete", menuItem -> {
            layoutForInputAndOutput.removeAllComponents();
            TextField id = new TextField("Enter user's id");
            Button addUserButton = new Button("Delete User!");
            addUserButton.addClickListener(clickEvent -> {
                boolean isAllFine = checkInput(id, null, null);
                try {
                    if (isAllFine) {
                        layoutForInputAndOutput.removeComponent(userDeletedLabel);
                        dao.deleteUser(Integer.parseInt(id.getValue()));
                        layoutForInputAndOutput.addComponent(userDeletedLabel);
                        manager.update();
                    }

                } catch (SQLException e) {
                    e.printStackTrace();
                }
            });

            layoutForInputAndOutput.addComponents(id, addUserButton);
        });
        vertLayout.addComponent(grid);
        vertLayout.addComponent(footerLayout);
        verticalLayout.addComponent(bar);
        verticalLayout.addComponent(layoutForInputAndOutput);

        layout.addComponent(verticalLayout);
        layout.addComponent(vertLayout);

        setContent(layout);
    }

    private boolean readIsAdmin(Optional<UserAdminStatus> status)
    {
        if (status.isPresent())
            switch (status.get())
            {
                case Yes:
                    return true;
                default:
                    return false;
            }
        return false;
    }

    private boolean checkInput(TextField id, TextField name, TextField age)
    {

        if (id != null)
        {
            String idOfUserString = id.getValue();
            if (idOfUserString.equals(""))
            {
                id.setValue("You must enter the id!");
                return false;
            }

            int temp;

            try {
                temp = Integer.parseInt(idOfUserString);
            } catch (NumberFormatException e) {
                id.setValue("That's not a number!");
                return false;
            }

            if (temp <= 0)
                return false;
        }

        if (name != null)
        {
            String nameOfUser = name.getValue();
            if (nameOfUser.equals(""))
            {
                name.setValue("You must enter name!");
                return false;
            }
        }

        if (age != null)
        {
            String ageOfUserString = age.getValue();
            if (ageOfUserString.equals(""))
            {
                age.setValue("You must enter age!");
                return false;
            }
            try {
                Integer.parseInt(ageOfUserString);
            } catch (NumberFormatException e) {
                age.setValue("That's not a number!");
                return false;
            }
        }

        return true;
    }

    @WebServlet(urlPatterns = "/*", name = "MyUIServlet", asyncSupported = true)
    @VaadinServletConfiguration(ui = MyUI.class, productionMode = false)
    public static class MyUIServlet extends VaadinServlet {
    }
}