package fr.triedge.uchuu.beans;

import fr.triedge.uchuu.model.Model;
import jakarta.annotation.PostConstruct;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.sql.SQLException;

@Component
@Order(100)
public class InitApp {

    @PostConstruct
    public void init(){
        System.out.println("Starting server...");
        /*
        try {
            System.out.println("Loading in memory data...");
            Model.getInstance().reloadInMemoryData();
            System.out.println("Data loaded.");
            System.out.println("Current quests: "+Model.getInstance().getQuests().size());

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

         */
    }
}
