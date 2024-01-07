package example.com.app.repositories;


import example.com.app.daos.TradingDAO;
import example.com.app.models.Trading;
import example.com.app.models.Card;
import example.com.app.models.User;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;

public class TradingRepository {

    @Setter(AccessLevel.PRIVATE)
    @Getter(AccessLevel.PRIVATE)
    TradingDAO TradingDAO;

    public TradingRepository(TradingDAO tradingDAO) { setTradingDAO(tradingDAO); }


    public ArrayList<Trading> getTradings() { return getTradingDAO().getTradings(); }

    public int createTrading(Trading newTrading, User user) { return getTradingDAO().createTrading(newTrading, user); }

    public int carryOutTrading(Card cardToTrade, String tradeDealID, User user) { return getTradingDAO().carryOutTrading(cardToTrade, tradeDealID, user); }

    public int deleteTrading(String tradeDealID, String token) { return getTradingDAO().deleteTrading(tradeDealID, token); }


}
