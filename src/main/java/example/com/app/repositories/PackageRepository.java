package example.com.app.repositories;

import example.com.app.daos.UserDAO;
import example.com.app.models.Card;
import example.com.app.models.User;


import example.com.app.daos.CardDAO;
import example.com.app.daos.PackageDAO;
import java.util.List;



import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;

public class PackageRepository {
    @Setter(AccessLevel.PRIVATE)
    @Getter(AccessLevel.PRIVATE)
    PackageDAO packageDAO;

    public PackageRepository(PackageDAO packageDAO) { setPackageDAO(packageDAO); }


    public int createPackage(List<Card> cards) {
        return getPackageDAO().createPackage(cards);
    }
    public String[] buyPackage( User user) {
       return getPackageDAO().buyPackage(user);
    }
    public boolean checkIfPackageAvailable(){
        return getPackageDAO().checkIfPackageExists();
    }
}
