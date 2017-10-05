package com.habboproject.server.game.navigator.types.featured;

import com.habboproject.server.api.networking.messages.IComposer;
import com.habboproject.server.game.rooms.RoomManager;
import com.habboproject.server.game.rooms.types.RoomData;
import com.habboproject.server.game.rooms.types.RoomWriter;

import java.sql.ResultSet;
import java.sql.SQLException;


public class FeaturedRoom {
    private int id;
    private boolean isCategory;
    private BannerType bannerType;
    private String caption;
    private String description;
    private String image;
    private ImageType imageType;
    private int roomId;
    private int categoryId;
    private boolean enabled;
    private boolean recommended;

    private RoomData room;

    public FeaturedRoom(ResultSet data) throws SQLException {
        this.id = data.getInt("id");
        this.bannerType = BannerType.get(data.getString("banner_type"));
        this.caption = data.getString("caption");
        this.description = data.getString("description");
        this.image = data.getString("image");
        this.imageType = ImageType.get(data.getString("image_type"));
        this.roomId = data.getInt("room_id");
        this.categoryId = data.getInt("category_id");
        this.enabled = Boolean.parseBoolean(data.getString("enabled"));
        this.recommended = data.getString("recommended").equals("1");
        this.isCategory = data.getString("type").equals("category");

        // cache the room data so we dont have to get it every time we load the nav
        if (!isCategory) this.room = RoomManager.getInstance().getRoomData(roomId);
    }

    public FeaturedRoom(int id, BannerType bannerType, String caption, String description, String image, ImageType imageType, int roomId, int categoryId, boolean enabled, boolean recommended, boolean isCategory) {
        this.id = id;
        this.bannerType = bannerType;
        this.caption = caption;
        this.description = description;
        this.image = image;
        this.imageType = imageType;
        this.roomId = roomId;
        this.categoryId = categoryId;
        this.enabled = enabled;
        this.recommended = recommended;
        this.isCategory = isCategory;

        if (!isCategory) this.room = RoomManager.getInstance().getRoomData(roomId);
    }

    public void compose(IComposer msg) {
        final boolean isActive = !isCategory && room != null && RoomManager.getInstance().isActive(room.getId());

        msg.writeInt(id);
        msg.writeString((!isCategory) ? room.getName() : caption);
        msg.writeString((!isCategory) ? (description != null && description.isEmpty() ? room.getDescription() : description) : description);

        msg.writeInt(bannerType == BannerType.BIG ? 0 : 1);
        msg.writeString(!isCategory ? caption : "");
        msg.writeString(imageType == ImageType.EXTERNAL ? image : "");
        msg.writeInt(categoryId);

        msg.writeInt(isActive ? RoomManager.getInstance().get(roomId).getEntities().playerCount() : 0);
        msg.writeInt(isCategory ? 4 : 2); // is room

        if (isCategory) {
            msg.writeBoolean(false);
        } else {
            RoomWriter.write(room, msg);
        }
    }

    public int getId() {
        return id;
    }

    public BannerType getBannerType() {
        return bannerType;
    }

    public String getCaption() {
        return caption;
    }

    public String getDescription() {
        return description;
    }

    public String getImage() {
        return image;
    }

    public ImageType getImageType() {
        return imageType;
    }

    public int getRoomId() {
        return roomId;
    }

    public int getCategoryId() {
        return categoryId;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public boolean isRecommended() {
        return recommended;
    }

    public boolean isCategory() {
        return this.isCategory;
    }
}
