package com.habboproject.server.game.landing;

import com.habboproject.server.game.landing.types.PromoArticle;
import com.habboproject.server.storage.queries.landing.LandingDao;
import com.habboproject.server.utilities.Initializable;
import org.apache.log4j.Logger;

import java.util.Map;


public class LandingManager implements Initializable {
    private static LandingManager landingManagerInstance;
    private static final Logger log = Logger.getLogger(LandingManager.class.getName());

    private Map<Integer, PromoArticle> articles;

    public LandingManager() {
    }

    @Override
    public void initialize() {
        this.loadArticles();

        log.info("LandingManager initialized");
    }

    public static LandingManager getInstance() {
        if (landingManagerInstance == null) {
            landingManagerInstance = new LandingManager();
        }

        return landingManagerInstance;
    }

    public void loadArticles() {
        if (this.articles != null) {
            this.articles.clear();
        }

        this.articles = LandingDao.getArticles();
    }

    public Map<Integer, PromoArticle> getArticles() {
        return articles;
    }
}
