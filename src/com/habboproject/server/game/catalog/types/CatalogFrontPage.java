package com.habboproject.server.game.catalog.types;

/**
 * Created by brend on 05/02/2017.
 */
public class CatalogFrontPage {
    private final int id;
    private final String caption;
    private final String image;
    private final String pageLink;
    private final int pageId;

    public CatalogFrontPage(int id, String caption, String image, String pageLink, int pageId) {
        this.id = id;
        this.caption = caption;
        this.image = image;
        this.pageLink = pageLink;
        this.pageId = pageId;
    }

    public int getId() {
        return this.id;
    }

    public String getCaption() {
        return this.caption;
    }

    public String getImage() {
        return this.image;
    }

    public String getPageLink() {
        return this.pageLink;
    }

    public int getPageId() {
        return this.pageId;
    }
}
