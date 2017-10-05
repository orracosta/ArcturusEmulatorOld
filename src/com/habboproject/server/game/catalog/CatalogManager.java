package com.habboproject.server.game.catalog;

import com.habboproject.server.game.catalog.purchase.PurchaseEvent;
import com.habboproject.server.game.catalog.types.CatalogFrontPage;
import com.habboproject.server.game.catalog.types.CatalogItem;
import com.habboproject.server.game.catalog.types.CatalogOffer;
import com.habboproject.server.game.catalog.types.CatalogPage;
import com.habboproject.server.storage.queries.catalog.CatalogDao;
import com.habboproject.server.utilities.Initializable;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.apache.commons.collections4.map.ListOrderedMap;
import org.apache.log4j.Logger;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class CatalogManager implements Initializable {
    private static CatalogManager catalogManagerInstance;

    /**
     * The pages within the catalog
     */
    private Map<Integer, CatalogPage> pages;

    /**
     * The items within the catalog
     */
    private Map<Integer, CatalogItem> items;

    /**
     * The news within the catalog frontpage
     */
    private Map<Integer, CatalogFrontPage> news;

    /**
     * The catalog item IDs to page IDs map
     */
    private Map<Integer, Integer> catalogItemIdToPageId;

    /**
     * Maps the offer ID of an item to the page ID.
     */
    private static final Map<Integer, CatalogOffer> catalogOffers = new HashMap<>();

    /**
     * The new style of gift boxes
     */
    private final List<Integer> giftBoxesNew = Lists.newArrayList();

    /**
     * The old style of gift boxes
     */
    private final List<Integer> giftBoxesOld = Lists.newArrayList();

    /**
     * The handler of everything catalog-purchase related
     */
    private PurchaseEvent purchaseHandler;

    /**
     * The logger for the catalog manager
     */
    private Logger log = Logger.getLogger(CatalogManager.class.getName());

    /**
     * Initialize the catalog
     */
    public CatalogManager() {

    }

    @Override
    public void initialize() {
        this.pages = new ListOrderedMap<>();
        this.items = new ListOrderedMap<>();
        this.news = new ListOrderedMap<>();

        this.catalogItemIdToPageId = new HashMap<>();

        this.purchaseHandler = new PurchaseEvent();

        this.loadItemsAndPages();
        this.loadGiftBoxes();

        log.info("CatalogManager initialized");
    }

    public static CatalogManager getInstance() {
        if (catalogManagerInstance == null)
            catalogManagerInstance = new CatalogManager();

        return catalogManagerInstance;
    }

    /**
     * Load all catalog pages
     */
    public void loadItemsAndPages() {
        if(this.items.size() >= 1) {
            this.items.clear();
        }

        if (this.getPages().size() >= 1) {
            this.getPages().clear();
        }

        if (this.news.size() >= 1) {
            this.news.clear();
        }

        if (getCatalogOffers().size() >= 1) {
            getCatalogOffers().clear();
        }

        if (this.catalogItemIdToPageId.size() >= 1) {
            this.catalogItemIdToPageId.clear();
        }

        try {
            CatalogDao.getItems(this.items);
            CatalogDao.getPages(this.pages);
            CatalogDao.getNews(this.news);
        } catch (Exception e) {
            log.error("Error while loading catalog pages/items", e);
        }

        for (CatalogPage page : this.pages.values()) {
            for (Integer item : page.getItems().keySet()) {
                this.catalogItemIdToPageId.put(item, page.getId());
            }
        }

        log.info("Loaded " + this.getPages().size() + " catalog pages and " + this.items.size() + " catalog items");
    }

    public void loadGiftBoxes() {
        if (this.giftBoxesNew.size() >= 1) {
            this.giftBoxesNew.clear();
        }

        if (this.giftBoxesOld.size() >= 1) {
            this.giftBoxesOld.clear();
        }

        CatalogDao.loadGiftBoxes(this.giftBoxesOld, this.giftBoxesNew);
        log.info("Loaded " + (this.giftBoxesNew.size() + this.giftBoxesOld.size()) + " gift wrappings");
    }

    /**
     * Get pages for a specific player rank
     *
     * @param rank Player rank
     * @return A list of pages that are accessible by the specified rank
     */
    public List<CatalogPage> getPagesForRank(int rank) {
        List<CatalogPage> pages = new ArrayList<>();

        for (CatalogPage page : this.getPages().values()) {
            if (rank >= page.getMinRank()) {
                pages.add(page);
            }
        }

        return pages;
    }

    public CatalogItem getCatalogItemByOfferId(int offerId) {
        CatalogOffer offer = getCatalogOffers().get(offerId);

        if (offer == null)
            return null;

        CatalogPage page = this.getPage(offer.getCatalogPageId());
        if (page == null)
            return null;

        return page.getItems().get(offer.getCatalogItemId());
    }

    public CatalogPage getCatalogPageByCatalogItemId(int id) {
        if(!this.catalogItemIdToPageId.containsKey(id)) {
            return null;
        }

        return this.pages.get(this.catalogItemIdToPageId.get(id));
    }

    public CatalogItem getCatalogItemByItemId(int itemId) {
        if (!this.items.containsKey(itemId)) {
            return null;
        }

        return this.items.get(itemId);
    }

    public Map<Integer, CatalogItem> getItemsForPage(int pageId) {
        Map<Integer, CatalogItem> items = Maps.newHashMap();

        for(Map.Entry<Integer, CatalogItem> catalogItem : this.items.entrySet()) {
            if(catalogItem.getValue().getPageId() == pageId) {
                items.put(catalogItem.getKey(), catalogItem.getValue());
            }
        }

        return items;
    }

    /**
     * Get a catalog page by its ID
     *
     * @param id Catalog Page ID
     * @return Catalog Page object with the specified ID
     */
    public CatalogPage getPage(int id) {
        if (this.pageExists(id)) {
            return this.getPages().get(id);
        }

        return null;
    }

    /**
     * Does a page with a specific ID exist?
     *
     * @param id The ID of the page we want to check that exists
     * @return Whether or not the page with the specified ID exists
     */
    public boolean pageExists(int id) {
        return this.getPages().containsKey(id);
    }

    /**
     * Get all catalog pages
     *
     * @return All catalog pages in-memory
     */
    public Map<Integer, CatalogPage> getPages() {
        return this.pages;
    }

    /**
     * Get the catalog page handler
     *
     * @return The catalog page handler
     */
    public PurchaseEvent getPurchaseHandler() {
        return purchaseHandler;
    }

    /**
     * Get the map that contains the offer id for catalog item id
     *
     * @return The map that contains the offer id for catalog item id
     */
    public static Map<Integer, CatalogOffer> getCatalogOffers() {
        return catalogOffers;
    }

    /**
     * Gift wrappings new
     *
     * @return The new style of gift wrapping boxes
     */
    public List<Integer> getGiftBoxesNew() {
        return giftBoxesNew;
    }

    /**
     * Gift wrappings old
     *
     * @return The old style of gift wrapping boxes
     */
    public List<Integer> getGiftBoxesOld() {
        return giftBoxesOld;
    }

    public Map<Integer, CatalogFrontPage> getNews() {
        return news;
    }
}
