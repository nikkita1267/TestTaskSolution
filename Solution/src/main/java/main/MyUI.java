package main;

import com.vaadin.annotations.Theme;
import com.vaadin.annotations.VaadinServletConfiguration;
import com.vaadin.server.VaadinRequest;
import com.vaadin.server.VaadinServlet;
import com.vaadin.ui.*;
import main.db.User;
import main.db.UserDao;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import javax.servlet.annotation.WebServlet;
import java.sql.Date;
import java.sql.SQLException;

/**
 * This UI is the application entry point. A UI may either represent a browser window 
 * (or tab) or some part of a html page where a Vaadin application is embedded.
 * <p>
 * The UI is initialized using {@link #init(VaadinRequest)}. This method is intended to be 
 * overridden to add component to the user interface and initialize non-component functionality.
 */
@Theme("mytheme")
public class MyUI extends UI {

    @Override
    protected void init(VaadinRequest vaadinRequest) {
        VerticalLayout vertLayout = new VerticalLayout();
        VerticalLayout verticalLayout = new VerticalLayout();
        final HorizontalLayout layout = new HorizontalLayout();
        VerticalLayout newVerticalLayout = new VerticalLayout();
        ApplicationContext context = new ClassPathXmlApplicationContext("spring-config.xml");
        UserDao dao = (UserDao) context.getBean("standardDaoImpl");
        MenuBar bar = new MenuBar();
        Grid<User> grid = new Grid<>(User.class);
        grid.setColumns("id", "name", "age", "isAdmin", "createdDate");
        try {
            grid.setItems(dao.getAllUsers());
            vertLayout.addComponent(grid);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        bar.addItem("Create", menuItem -> {
            newVerticalLayout.removeAllComponents();
            TextField name = new TextField("Enter name of user, please");
            TextField age = new TextField("Enter age of user, please");
            NativeSelect<UserStatus> status = new NativeSelect<>("Is user the admin? (Default No)");
            status.setItems(UserStatus.values());
            Button addUserButton = new Button("Create User!");
            addUserButton.addClickListener(clickEvent -> {
                boolean isAllFine = true;
                String nameOfUser = name.getValue();
                if (nameOfUser.equals(""))
                {
                    name.setValue("You must enter name!11");
                    isAllFine = false;
                }

                String ageOfUserString = age.getValue();
                if (ageOfUserString.equals(""))
                {
                    age.setValue("You must enter age!11");
                    isAllFine = false;
                }
                int ageOfUser = -1;
                try {
                    ageOfUser = Integer.parseInt(ageOfUserString);
                } catch (NumberFormatException e) {
                    age.setValue("It's not a number!");
                    isAllFine = false;
                }

                boolean isAdmin = false;
                switch (status.getSelectedItem().get())
                {
                    case No:
                        isAdmin = false;
                        break;
                    case Yes:
                        isAdmin = true;
                        break;
                    default:
                        isAllFine = false;
                }

                try {
                    if (isAllFine) {
                        dao.addUser(new User(nameOfUser, ageOfUser, isAdmin, new Date(new java.util.Date().getTime())));
                        newVerticalLayout.addComponent(new Label("User created!"));
                    } else {
                        newVerticalLayout.addComponent(new Label("Smth went wrong:("));
                    }

                    updateGrid(grid, dao);
                } catch (SQLException e) {
                    e.printStackTrace();
                    newVerticalLayout.addComponent(new Label(e.toString()));
                    for (StackTraceElement element : e.getStackTrace())
                        newVerticalLayout.addComponent(new Label(element.toString()));
                }
            });

            newVerticalLayout.addComponents(name, age, status, addUserButton);
        });

        bar.addItem("Update", menuItem -> {
            newVerticalLayout.removeAllComponents();
            TextField id = new TextField("Enter the id, please");
            TextField name = new TextField("Enter name of user, please");
            TextField age = new TextField("Enter age of user, please");
            NativeSelect<UserStatus> status = new NativeSelect<>("Is user the admin? (Default No)");
            status.setItems(UserStatus.values());
            Button addUserButton = new Button("Update User!");
            addUserButton.addClickListener(clickEvent -> {
                boolean isAllFine = true;
                String idOfUserString = id.getValue();
                if (idOfUserString.equals(""))
                {
                    id.setValue("You must enter the id!!1");
                    isAllFine = false;
                }
                int idOfUser = -1;

                try {
                    idOfUser = Integer.parseInt(idOfUserString);
                } catch (NumberFormatException e) {
                    id.setValue("It's not a number!");
                    isAllFine = false;
                }

                String nameOfUser = name.getValue();
                if (nameOfUser.equals(""))
                {
                    name.setValue("You must enter name!11");
                    isAllFine = false;
                }

                String ageOfUserString = age.getValue();
                if (ageOfUserString.equals(""))
                {
                    age.setValue("You must enter age!11");
                    isAllFine = false;
                }
                int ageOfUser = -1;
                try {
                    ageOfUser = Integer.parseInt(ageOfUserString);
                } catch (NumberFormatException e) {
                    age.setValue("It's not a number!");
                    isAllFine = false;
                }

                boolean isAdmin = false;
                switch (status.getSelectedItem().get())
                {
                    case No:
                        isAdmin = false;
                        break;
                    case Yes:
                        isAdmin = true;
                        break;
                    default:
                        isAllFine = false;
                }

                try {
                    if (isAllFine) {
                        dao.updateUser(new User(nameOfUser, ageOfUser, isAdmin, new Date(new java.util.Date().getTime())), idOfUser);
                        newVerticalLayout.addComponent(new Label("User updated!"));
                    }

                    updateGrid(grid, dao);
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            });

            newVerticalLayout.addComponents(id, name, age, status, addUserButton);
        });

        bar.addItem("Delete", menuItem -> {
            newVerticalLayout.removeAllComponents();
            TextField id = new TextField("Enter id of user, please");
            Button addUserButton = new Button("Delete User!");
            addUserButton.addClickListener(clickEvent -> {
                boolean isAllFine = true;
                String idOfUserString = id.getValue();
                if (idOfUserString.equals(""))
                {
                    id.setValue("You must enter the id!!1");
                    isAllFine = false;
                }
                int idOfUser = -1;

                try {
                    idOfUser = Integer.parseInt(idOfUserString);
                } catch (NumberFormatException e) {
                    id.setValue("It's not a number!");
                    isAllFine = false;
                }

                try {
                    if (isAllFine) {
                        dao.deleteUser(idOfUser);
                        newVerticalLayout.addComponent(new Label("User deleted!"));
                    }

                    updateGrid(grid, dao);
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            });

            newVerticalLayout.addComponents(id, addUserButton);
        });
        verticalLayout.addComponent(bar);
        verticalLayout.addComponent(newVerticalLayout);

        layout.addComponent(verticalLayout);
        layout.addComponent(vertLayout);
        
        setContent(layout);
    }

    private void updateGrid(Grid<User> grid, UserDao dao)
    {
        try {
            grid.setItems(dao.getAllUsers());
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @WebServlet(urlPatterns = "/*", name = "MyUIServlet", asyncSupported = true)
    @VaadinServletConfiguration(ui = MyUI.class, productionMode = false)
    public static class MyUIServlet extends VaadinServlet {
    }
}
