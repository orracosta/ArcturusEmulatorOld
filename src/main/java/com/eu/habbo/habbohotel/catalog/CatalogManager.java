package com.eu.habbo.habbohotel.catalog;

import com.eu.habbo.Emulator;
import com.eu.habbo.habbohotel.achievements.AchievementManager;
import com.eu.habbo.habbohotel.bots.Bot;
import com.eu.habbo.habbohotel.catalog.layouts.*;
import com.eu.habbo.habbohotel.gameclients.GameClient;
import com.eu.habbo.habbohotel.items.FurnitureType;
import com.eu.habbo.habbohotel.items.Item;
import com.eu.habbo.habbohotel.items.SoundTrack;
import com.eu.habbo.habbohotel.items.interactions.*;
import com.eu.habbo.habbohotel.pets.Pet;
import com.eu.habbo.habbohotel.users.Habbo;
import com.eu.habbo.habbohotel.users.HabboBadge;
import com.eu.habbo.habbohotel.users.HabboGender;
import com.eu.habbo.habbohotel.users.HabboItem;
import com.eu.habbo.messages.outgoing.catalog.*;
import com.eu.habbo.messages.outgoing.events.calendar.AdventCalendarDataComposer;
import com.eu.habbo.messages.outgoing.events.calendar.AdventCalendarProductComposer;
import com.eu.habbo.messages.outgoing.generic.alerts.BubbleAlertComposer;
import com.eu.habbo.messages.outgoing.generic.alerts.BubbleAlertKeys;
import com.eu.habbo.messages.outgoing.generic.alerts.GenericAlertComposer;
import com.eu.habbo.messages.outgoing.inventory.AddBotComposer;
import com.eu.habbo.messages.outgoing.inventory.AddHabboItemComposer;
import com.eu.habbo.messages.outgoing.inventory.AddPetComposer;
import com.eu.habbo.messages.outgoing.inventory.InventoryRefreshComposer;
import com.eu.habbo.messages.outgoing.unknown.NuxAlertComposer;
import com.eu.habbo.messages.outgoing.users.AddUserBadgeComposer;
import com.eu.habbo.messages.outgoing.users.UserCreditsComposer;
import com.eu.habbo.messages.outgoing.users.UserPointsComposer;
import com.eu.habbo.plugin.events.emulator.EmulatorLoadCatalogManagerEvent;
import com.eu.habbo.plugin.events.users.catalog.UserCatalogFurnitureBoughtEvent;
import com.eu.habbo.plugin.events.users.catalog.UserCatalogItemPurchasedEvent;
import gnu.trove.TCollections;
import gnu.trove.iterator.TIntObjectIterator;
import gnu.trove.map.TIntObjectMap;
import gnu.trove.map.hash.THashMap;
import gnu.trove.map.hash.TIntIntHashMap;
import gnu.trove.map.hash.TIntObjectHashMap;
import gnu.trove.procedure.TObjectProcedure;
import gnu.trove.set.hash.THashSet;

import java.sql.*;
import java.util.*;

public class CatalogManager
{
    /**
     * All the CatalogPages are stored in here.
     */
    public final TIntObjectMap<CatalogPage> catalogPages;

    /**
     * All featured items on the frontpage.
     */
    public final TIntObjectMap<CatalogFeaturedPage> catalogFeaturedPages;

    /**
     * All the recycler prizes are stored in here.
     */
    public final THashMap<Integer, THashSet<Item>> prizes;

    /**
     * All the new gift wrappers are stored in here.
     */
    public final THashMap<Integer, Integer> giftWrappers;

    /**
     * All the old gift wrappers (Seperate furnis) are stored in here.
     */
    public final THashMap<Integer, Integer> giftFurnis;

    /**
     * All the items that can be claimed as club rewards are stored in here.
     */
    public final THashSet<CatalogItem> clubItems;

    /**
     * All Habbo Club subcription offers.
     */
    public final THashMap<Integer, ClubOffer> clubOffers;

    /**
     * All clothing definitions
     */
    public final THashMap<Integer, ClothItem> clothing;

    /**
     * Offer definitions.
     */
    public final TIntIntHashMap offerDefs;

    /**
     * All vouchers are stored in here.
     */
    private final List<Voucher> vouchers;

    /**
     * The box that should be given as ecotron reward.
     */
    public final Item ecotronItem;

    /**
     * The numbers available for limited furniture.
     */
    public final THashMap<Integer, CatalogLimitedConfiguration> limitedNumbers;

    /**
     * The amount of items that are on sale.
     */
    public static int catalogItemAmount;

    public final THashMap<Integer, CalendarRewardObject> calendarRewards;

    /**
     * Mapped all page definitions.
     */
    public static final THashMap<String, Class<? extends CatalogPage>> pageDefinitions = new THashMap<String, Class<? extends CatalogPage>>()
    {
        {
            for (CatalogPageLayouts layout : CatalogPageLayouts.values())
            {
                switch(layout)
                {
                    case frontpage:                     put(layout.name().toLowerCase(), FrontpageLayout.class); break;
                    case badge_display:                 put(layout.name().toLowerCase(), BadgeDisplayLayout.class); break;
                    case spaces_new:                    put(layout.name().toLowerCase(), SpacesLayout.class); break;
                    case trophies:                      put(layout.name().toLowerCase(), TrophiesLayout.class); break;
                    case bots:                          put(layout.name().toLowerCase(), BotsLayout.class); break;
                    case club_buy:                      put(layout.name().toLowerCase(), ClubBuyLayout.class); break;
                    case club_gift:                     put(layout.name().toLowerCase(), ClubGiftsLayout.class); break;
                    case sold_ltd_items:                put(layout.name().toLowerCase(), SoldLTDItemsLayout.class); break;
                    case single_bundle:                 put(layout.name().toLowerCase(), SingleBundle.class); break;
                    case roomads:                       put(layout.name().toLowerCase(), RoomAdsLayout.class); break;
                    case recycler:                      if (Emulator.getConfig().getBoolean("hotel.ecotron.enabled")) put(layout.name().toLowerCase(), RecyclerLayout.class); break;
                    case recycler_info:                 if (Emulator.getConfig().getBoolean("hotel.ecotron.enabled")) put(layout.name().toLowerCase(), RecyclerInfoLayout.class);
                    case recycler_prizes:               if (Emulator.getConfig().getBoolean("hotel.ecotron.enabled")) put(layout.name().toLowerCase(), RecyclerPrizesLayout.class); break;
                    case marketplace:                   if (Emulator.getConfig().getBoolean("hotel.marketplace.enabled")) put(layout.name().toLowerCase(), MarketplaceLayout.class); break;
                    case marketplace_own_items:         if (Emulator.getConfig().getBoolean("hotel.marketplace.enabled")) put(layout.name().toLowerCase(), MarketplaceOwnItems.class); break;
                    case info_duckets:                  put(layout.name().toLowerCase(), InfoDucketsLayout.class); break;
                    case info_pets:                     put(layout.name().toLowerCase(), InfoPetsLayout.class); break;
                    case info_rentables:                put(layout.name().toLowerCase(), InfoRentablesLayout.class); break;
                    case info_loyalty:                  put(layout.name().toLowerCase(), InfoLoyaltyLayout.class); break;
                    case loyalty_vip_buy:               put(layout.name().toLowerCase(), LoyaltyVipBuyLayout.class); break;
                    case guilds:                        put(layout.name().toLowerCase(), GuildFrontpageLayout.class); break;
                    case guild_furni:                   put(layout.name().toLowerCase(), GuildFurnitureLayout.class); break;
                    case guild_forum:                   put(layout.name().toLowerCase(), GuildForumLayout.class); break;
                    case pets:                          put(layout.name().toLowerCase(), PetsLayout.class); break;
                    case pets2:                         put(layout.name().toLowerCase(), Pets2Layout.class); break;
                    case pets3:                         put(layout.name().toLowerCase(), Pets3Layout.class); break;
                    case soundmachine:                  put(layout.name().toLowerCase(), TraxLayout.class); break;
                    case default_3x3_color_grouping:    put(layout.name().toLowerCase(), ColorGroupingLayout.class); break;
                    case recent_purchases:              put(layout.name().toLowerCase(), RecentPurchasesLayout.class); break;
                    case room_bundle:                   put(layout.name().toLowerCase(), RoomBundleLayout.class); break;
                    case petcustomization:              put(layout.name().toLowerCase(), PetCustomizationLayout.class); break;
                    case vip_buy:                       put(layout.name().toLowerCase(), VipBuyLayout.class); break;
                    case frontpage_featured:            put(layout.name().toLowerCase(), FrontPageFeaturedLayout.class); break;
                    case builders_club_addons:          put(layout.name().toLowerCase(), BuildersClubAddonsLayout.class); break;
                    case builders_club_frontpage:       put(layout.name().toLowerCase(), BuildersClubFrontPageLayout.class); break;
                    case builders_club_loyalty:         put(layout.name().toLowerCase(), BuildersClubLoyaltyLayout.class); break;
                    case default_3x3:
                    default:                            put("default_3x3", Default_3x3Layout.class); break;
                }
            }
        }
    };

    public CatalogManager()
    {
        long millis                 = System.currentTimeMillis();
        this.catalogPages           = TCollections.synchronizedMap(new TIntObjectHashMap<CatalogPage>());
        this.catalogFeaturedPages   = new TIntObjectHashMap<CatalogFeaturedPage>();
        this.prizes                 = new THashMap<Integer, THashSet<Item>>();
        this.giftWrappers           = new THashMap<Integer, Integer>();
        this.giftFurnis             = new THashMap<Integer, Integer>();
        this.clubItems              = new THashSet<CatalogItem>();
        this.clubOffers             = new THashMap<>();
        this.clothing               = new THashMap<Integer, ClothItem>();
        this.offerDefs              = new TIntIntHashMap();
        this.vouchers               = new ArrayList<Voucher>();
        this.limitedNumbers         = new THashMap<Integer, CatalogLimitedConfiguration>();
        this.calendarRewards        = new THashMap<>();

        this.initialize();

        this.ecotronItem = Emulator.getGameEnvironment().getItemManager().getItem("ecotron_box");

        Emulator.getLogging().logStart("Catalog Manager -> Loaded! ("+(System.currentTimeMillis() - millis)+" MS)");
    }

    /**
     * Initializes the CatalogManager.
     */
    public synchronized void initialize()
    {
        Emulator.getPluginManager().fireEvent(new EmulatorLoadCatalogManagerEvent());

        try
        {
            loadLimitedNumbers();
            loadCatalogPages();
            loadCatalogFeaturedPages();
            loadCatalogItems();
            loadClubOffers();
            loadVouchers();
            loadClothing();
            loadRecycler();
            loadGiftWrappers();
            loadCalendarRewards();
        }
        catch(SQLException e)
        {
            Emulator.getLogging().logSQLException(e);
        }
    }

    private synchronized void loadLimitedNumbers() throws SQLException
    {
        this.limitedNumbers.clear();

        THashMap<Integer, LinkedList<Integer>> limiteds = new THashMap<Integer, LinkedList<Integer>>();
        TIntIntHashMap totals = new TIntIntHashMap();
        try (Connection connection = Emulator.getDatabase().getDataSource().getConnection(); PreparedStatement statement = connection.prepareStatement("SELECT * FROM catalog_items_limited"))
        {
            try (ResultSet set = statement.executeQuery())
            {
                while (set.next())
                {
                    if (!limiteds.containsKey(set.getInt("catalog_item_id")))
                    {
                        limiteds.put(set.getInt("catalog_item_id"), new LinkedList<Integer>());
                    }

                    totals.adjustOrPutValue(set.getInt("catalog_item_id"), 1, 1);

                    if (set.getInt("user_id") == 0)
                    {
                        limiteds.get(set.getInt("catalog_item_id")).push(set.getInt("number"));
                    }
                }
            }
        }
        catch (SQLException e)
        {
            Emulator.getLogging().logSQLException(e);
        }

        for (Map.Entry<Integer, LinkedList<Integer>> set : limiteds.entrySet())
        {
            this.limitedNumbers.put(set.getKey(), new CatalogLimitedConfiguration(set.getKey(), set.getValue(), totals.get(set.getKey())));
        }
    }

    /**
     * Load all CatalogPages
     * @throws SQLException
     */
    private synchronized void loadCatalogPages() throws SQLException
    {
        this.catalogPages.clear();

        final THashMap<Integer, CatalogPage> pages = new THashMap<Integer, CatalogPage>();
        pages.put(-1, new CatalogRootLayout(null));
        ResultSet set = null;
        try (Connection connection = Emulator.getDatabase().getDataSource().getConnection(); PreparedStatement statement = connection.prepareStatement("SELECT * FROM catalog_pages ORDER BY parent_id, id"))
        {
            set = statement.executeQuery();
        }
        catch (SQLException e)
        {
            Emulator.getLogging().logSQLException(e);
        }

        while(set.next())
        {
            Class<? extends CatalogPage> pageClazz = pageDefinitions.get(set.getString("page_layout"));

            if (pageClazz == null)
            {
                Emulator.getLogging().logStart("Unknown Page Layout: " + set.getString("page_layout"));
                continue;
            }

            try
            {
                CatalogPage page = pageClazz.getConstructor(ResultSet.class).newInstance(set);
                pages.put(page.getId(), page);
            }
            catch (Exception e)
            {
                Emulator.getLogging().logErrorLine("Failed to load layout: " + set.getString("page_layout"));
            }
        }

        set.close();

        pages.forEachValue(new TObjectProcedure<CatalogPage>()
        {
            @Override
            public boolean execute(CatalogPage object)
            {
                CatalogPage page = pages.get(object.parentId);

                if (page != null)
                {
                    if (page.id != object.id)
                    {
                        page.addChildPage(object);
                    }
                }
                else
                {
                    if (object.parentId != -2)
                    {
                        Emulator.getLogging().logStart("Parent Page not found for " + object.getPageName() + " (ID: " + object.id + ", parent_id: " + object.parentId + ")");
                    }
                }
                return true;
            }
        });

        this.catalogPages.putAll(pages);

        Emulator.getLogging().logStart("Loaded " + this.catalogPages.size() + " Catalog Pages!");
    }

    /**
     * Loads the featured catalog pages.
     * @throws SQLException
     */
    private synchronized void loadCatalogFeaturedPages() throws SQLException
    {
        this.catalogFeaturedPages.clear();

        try (Connection connection = Emulator.getDatabase().getDataSource().getConnection(); Statement statement = connection.createStatement(); ResultSet set = statement.executeQuery("SELECT * FROM catalog_featured_pages ORDER BY slot_id ASC"))
        {
            while (set.next())
            {
                this.catalogFeaturedPages.put(set.getInt("slot_id"), new CatalogFeaturedPage(
                        set.getInt("slot_id"),
                        set.getString("caption"),
                        set.getString("image"),
                        CatalogFeaturedPage.Type.valueOf(set.getString("type").toUpperCase()),
                        set.getInt("expire_timestamp"),
                        set.getString("page_name"),
                        set.getInt("page_id"),
                        set.getString("product_name")
                        ));
            }
        }
        catch (SQLException e)
        {
            Emulator.getLogging().logSQLException(e);
        }
    }
    /**
     * Load all CatalogItems
     * @throws SQLException
     */
    private synchronized void loadCatalogItems() throws SQLException
    {
        this.clubItems.clear();
        catalogItemAmount = 0;

        try (Connection connection = Emulator.getDatabase().getDataSource().getConnection(); Statement statement = connection.createStatement(); ResultSet set = statement.executeQuery("SELECT * FROM catalog_items"))
        {
            CatalogItem item;
            while (set.next())
            {
                if (set.getString("item_ids").equals("0"))
                    continue;

                if(set.getString("catalog_name").contains("HABBO_CLUB_"))
                {
                    this.clubItems.add(new CatalogItem(set));
                    continue;
                }

                CatalogPage page = this.catalogPages.get(set.getInt("page_id"));

                if (page == null)
                    continue;

                item = page.getCatalogItem(set.getInt("id"));

                if (item == null)
                {
                    catalogItemAmount++;
                    item = new CatalogItem(set);
                    page.addItem(item);

                    if(item.getOfferId() != -1)
                    {
                        page.addOfferId(item.getOfferId());

                        this.offerDefs.put(item.getOfferId(), page.getId());
                    }
                }
                else
                    item.update(set);

                if (item.isLimited())
                {
                    this.createOrUpdateLimitedConfig(item);
                }
            }
        }
        catch (SQLException e)
        {
            Emulator.getLogging().logSQLException(e);
        }

        for (CatalogPage page : this.catalogPages.valueCollection())
        {
            for (Integer id : page.getIncluded())
            {
                CatalogPage p = this.catalogPages.get(id);

                if (p != null)
                {
                    page.getCatalogItems().putAll(p.getCatalogItems());
                }
            }
        }
    }

    private void loadClubOffers() throws SQLException
    {
        synchronized (this.clubOffers)
        {
            this.clubOffers.clear();

            try (Connection connection = Emulator.getDatabase().getDataSource().getConnection(); PreparedStatement statement = connection.prepareStatement("SELECT * FROM catalog_club_offers WHERE enabled = ?"))
            {
                statement.setString(1, "1");
                try (ResultSet set = statement.executeQuery())
                {
                    while (set.next())
                    {
                        this.clubOffers.put(set.getInt("id"), new ClubOffer(set));
                    }
                }
            }
        }
    }

    /**
     * Load all vouchers.
     * @throws SQLException
     */
    private void loadVouchers() throws SQLException
    {
        synchronized (this.vouchers)
        {
            this.vouchers.clear();

            try (Connection connection = Emulator.getDatabase().getDataSource().getConnection(); Statement statement = connection.createStatement(); ResultSet set = statement.executeQuery("SELECT * FROM vouchers"))
            {
                while (set.next())
                {
                    this.vouchers.add(new Voucher(set));
                }
            }
        }
    }

    /**
     * Load the recycler.
     * @throws SQLException
     */
    public void loadRecycler() throws SQLException
    {
        synchronized (this.prizes)
        {
            this.prizes.clear();
            try (Connection connection = Emulator.getDatabase().getDataSource().getConnection(); Statement statement = connection.createStatement(); ResultSet set = statement.executeQuery("SELECT * FROM recycler_prizes"))
            {
                while (set.next())
                {
                    Item item = Emulator.getGameEnvironment().getItemManager().getItem(set.getInt("item_id"));

                    if (item != null)
                    {
                        if (this.prizes.get(set.getInt("rarity")) == null)
                        {
                            this.prizes.put(set.getInt("rarity"), new THashSet<Item>());
                        }

                        this.prizes.get(set.getInt("rarity")).add(item);
                    }
                    else
                    {
                        Emulator.getLogging().logErrorLine("Cannot load item with ID:" + set.getInt("item_id") + " as recycler reward!");
                    }
                }
            }
            catch (SQLException e)
            {
                Emulator.getLogging().logSQLException(e);
            }
        }
    }

    /**
     * Load all gift wrappers (And old gift furnis)
     * @throws SQLException
     */
    public void loadGiftWrappers() throws SQLException
    {
        synchronized (this.giftWrappers)
        {
            synchronized (this.giftFurnis)
            {
                this.giftWrappers.clear();
                this.giftFurnis.clear();

                try (Connection connection = Emulator.getDatabase().getDataSource().getConnection(); Statement statement = connection.createStatement(); ResultSet set = statement.executeQuery("SELECT * FROM gift_wrappers ORDER BY sprite_id DESC"))
                {
                    while (set.next())
                    {
                        switch (set.getString("type"))
                        {
                            case "wrapper":
                                this.giftWrappers.put(set.getInt("sprite_id"), set.getInt("item_id"));
                                break;

                            case "gift":
                                this.giftFurnis.put(set.getInt("sprite_id"), set.getInt("item_id"));
                                break;
                        }
                    }
                }
                catch (SQLException e)
                {
                    Emulator.getLogging().logSQLException(e);
                }
            }
        }
    }

    private void loadCalendarRewards()
    {
        synchronized (this.calendarRewards)
        {
            this.calendarRewards.clear();

            try (Connection connection = Emulator.getDatabase().getDataSource().getConnection(); PreparedStatement statement = connection.prepareStatement("SELECT * FROM calendar_rewards"))
            {
                try (ResultSet set = statement.executeQuery())
                {
                    while (set.next())
                    {
                        this.calendarRewards.put(set.getInt("id"), new CalendarRewardObject(set));
                    }
                }
            }
            catch (SQLException e)
            {
                Emulator.getLogging().logSQLException(e);
            }
        }
    }

    /**
     * Loads all clothing.
     */
    private void loadClothing()
    {
        synchronized (this.clothing)
        {
            this.clothing.clear();

            try (Connection connection = Emulator.getDatabase().getDataSource().getConnection(); Statement statement = connection.createStatement(); ResultSet set = statement.executeQuery("SELECT * FROM catalog_clothing"))
            {
                while (set.next())
                {
                    this.clothing.put(set.getInt("id"), new ClothItem(set));
                }
            }
            catch (SQLException e)
            {
                Emulator.getLogging().logSQLException(e);
            }
        }
    }

    public ClothItem getClothing(String name)
    {
        for (ClothItem item : this.clothing.values())
        {
            if (item.name.equalsIgnoreCase(name))
            {
                return item;
            }
        }

        return null;
    }

    /**
     * Looks up the given code for any voucher that matches it.
     * @param code The code to look up.
     * @return The voucher that uses the code. NULL when not found.
     */
    public Voucher getVoucher(String code)
    {
        synchronized (this.vouchers)
        {
            for (Voucher voucher : this.vouchers)
            {
                if (voucher.code.equals(code))
                {
                    return voucher;
                }
            }
        }
        return null;
    }

    /**
     * Redeem a vouchercode for the given GameClient.
     * @param client The GameClient that receives the rewards.
     * @param voucherCode The voucher code.
     */
    public void redeemVoucher(GameClient client, String voucherCode)
    {
        Voucher voucher = Emulator.getGameEnvironment().getCatalogManager().getVoucher(voucherCode);

        if(voucher != null)
        {
            if(Emulator.getGameEnvironment().getCatalogManager().deleteVoucher(voucher))
            {
                client.getHabbo().getHabboInfo().addCredits(voucher.credits);

                if(voucher.points > 0)
                {
                    client.getHabbo().getHabboInfo().addCurrencyAmount(voucher.pointsType, voucher.points);
                    client.sendResponse(new UserPointsComposer(client.getHabbo().getHabboInfo().getCurrencyAmount(voucher.pointsType), voucher.points, voucher.pointsType));
                }

                if(voucher.credits > 0)
                {
                    client.getHabbo().getHabboInfo().addCredits(voucher.credits);
                    client.sendResponse(new UserCreditsComposer(client.getHabbo()));
                }

                if (voucher.catalogItemId > 0)
                {
                    CatalogItem item = this.getCatalogItem(voucher.catalogItemId);

                    if (item != null)
                    {
                        this.purchaseItem(null, item, client.getHabbo(), 1, "", true);
                    }
                }

                client.sendResponse(new RedeemVoucherOKComposer());

                return;
            }
        }

        client.sendResponse(new RedeemVoucherErrorComposer(RedeemVoucherErrorComposer.INVALID_CODE));
    }

    /**
     * Deletes an voucher from the database and emulator cache.
     * @param voucher The voucher to delete.
     * @return True when the voucher has been deleted.
     */
    public boolean deleteVoucher(Voucher voucher)
    {
        try (Connection connection = Emulator.getDatabase().getDataSource().getConnection(); PreparedStatement statement = connection.prepareStatement("DELETE FROM vouchers WHERE code = ?"))
        {
            statement.setString(1, voucher.code);

            synchronized (this.vouchers)
            {
                this.vouchers.remove(voucher);
            }

            return statement.executeUpdate() >= 1;
        }
        catch(SQLException e)
        {
            Emulator.getLogging().logSQLException(e);
        }

        return false;
    }

    /**
     * Gets the CatalogPage for the given page Id
     * @param pageId The page Id to lookup.
     * @return The CatalogPage matching the page Id. NULL When not found.
     */
    public CatalogPage getCatalogPage(int pageId)
    {
        return this.catalogPages.get(pageId);
    }

    public CatalogPage getCatalogPage(final String captionSafe)
    {
        final CatalogPage[] page = {null};

        synchronized (this.catalogPages)
        {
            this.catalogPages.forEachValue(new TObjectProcedure<CatalogPage>()
            {
                @Override
                public boolean execute(CatalogPage object)
                {
                    if(object.getPageName().equalsIgnoreCase(captionSafe))
                    {
                        page[0] = object;
                        return false;
                    }

                    return true;
                }
            });

            return page[0];
        }
    }

    /**
     * Finds the catalog item associated with the id.
     * @param id
     * @return
     */
    public CatalogItem getCatalogItem(int id)
    {
        final CatalogItem[] item = {null};
        synchronized (this.catalogPages)
        {
            this.catalogPages.forEachValue(new TObjectProcedure<CatalogPage>()
            {
                @Override
                public boolean execute(CatalogPage object)
                {
                    item[0] = object.getCatalogItem(id);

                    return item[0] == null;
                }
            });
        }

        return item[0];
    }

    /**
     * Gets all the sub pages for the given CatalogPage.
     * @param parentId The page the sub pages have to bee looked up for.
     * @param habbo The Habbo that has access to these pages.
     * @return The selected pages.
     */
    public List<CatalogPage> getCatalogPages(int parentId, final Habbo habbo)
    {
        final List<CatalogPage> pages = new ArrayList<CatalogPage>();

        this.catalogPages.get(parentId).childPages.forEachValue(new TObjectProcedure<CatalogPage>()
        {
            @Override
            public boolean execute(CatalogPage object)
            {
                if (object.getRank() <= habbo.getHabboInfo().getRank().getId() && object.visible)
                {
                    pages.add(object);
                }

                return true;
            }
        });
        Collections.sort(pages);

        return pages;
    }

    public TIntObjectMap<CatalogFeaturedPage> getCatalogFeaturedPages()
    {
        return this.catalogFeaturedPages;
    }

    /**
     * @param itemId The CatalogItem that should be lookup.
     * @return The CatalogItem for the id. NULL when not found.
     */
    public CatalogItem getClubItem(int itemId)
    {
        synchronized (this.clubItems)
        {
            for (CatalogItem item : this.clubItems)
            {
                if (item.getId() == itemId)
                    return item;
            }
        }

        return null;
    }

    /**
     * Moves a catalog item to the a different CatalogPage.
     * @param item The CatalogItem to move.
     * @param pageId The unique identifier of the page.
     * @return True if the move has been succesfully.
     */
    public boolean moveCatalogItem(CatalogItem item, int pageId)
    {
        CatalogPage page = this.getCatalogPage(item.getPageId());

        if(page == null)
            return false;

        page.getCatalogItems().remove(item.getId());

        page = this.getCatalogPage(pageId);

        page.getCatalogItems().put(item.getId(), item);

        item.setPageId(pageId);

        return true;
    }

    /**
     * @return Random recycler reward.
     */
    public Item getRandomRecyclerPrize()
    {
        int level = 1;

        if(Emulator.getRandom().nextInt(Emulator.getConfig().getInt("hotel.ecotron.rarity.chance.5")) + 1 == Emulator.getConfig().getInt("hotel.ecotron.rarity.chance.5"))
        {
            level = 5;
        }
        else if(Emulator.getRandom().nextInt(Emulator.getConfig().getInt("hotel.ecotron.rarity.chance.4")) + 1 == Emulator.getConfig().getInt("hotel.ecotron.rarity.chance.4"))
        {
            level = 4;
        }
        else if(Emulator.getRandom().nextInt(Emulator.getConfig().getInt("hotel.ecotron.rarity.chance.3")) + 1 == Emulator.getConfig().getInt("hotel.ecotron.rarity.chance.3"))
        {
            level = 3;
        }
        else if(Emulator.getRandom().nextInt(Emulator.getConfig().getInt("hotel.ecotron.rarity.chance.2")) + 1 == Emulator.getConfig().getInt("hotel.ecotron.rarity.chance.2"))
        {
            level = 2;
        }

        if (this.prizes.containsKey(level) && !this.prizes.get(level).isEmpty())
        {
            return (Item) this.prizes.get(level).toArray()[Emulator.getRandom().nextInt(this.prizes.get(level).size())];
        }
        else
        {
            Emulator.getLogging().logErrorLine("[Recycler] No rewards specified for rarity level " + level);
        }

        return null;
    }

    /**
     * Creates a new catalog page.
     * @param caption The caption of the page.
     * @param captionSave Save caption.
     * @param roomId The id of the room if the page is a room bundle.
     * @param icon The icon of the page.
     * @param layout The layout of the page.
     * @param minRank The minimum rank that is required to view the page.
     * @param parentId The id of the parent page.
     * @return The created CatalogPage.
     */
    public CatalogPage createCatalogPage(String caption, String captionSave, int roomId, int icon, CatalogPageLayouts layout, int minRank, int parentId)
    {
        CatalogPage catalogPage = null;
        try (Connection connection = Emulator.getDatabase().getDataSource().getConnection(); PreparedStatement statement = connection.prepareStatement("INSERT INTO catalog_pages (parent_id, caption, caption_save, icon_image, visible, enabled, min_rank, page_layout, room_id) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)", Statement.RETURN_GENERATED_KEYS))
        {
            statement.setInt(1, parentId);
            statement.setString(2, caption);
            statement.setString(3, captionSave);
            statement.setInt(4, icon);
            statement.setString(5, "1");
            statement.setString(6, "1");
            statement.setInt(7, minRank);
            statement.setString(8, layout.name());
            statement.setInt(9, roomId);
            statement.execute();
            try (ResultSet set = statement.getGeneratedKeys())
            {
                if (set.next())
                {
                    try (PreparedStatement stmt = connection.prepareStatement("SELECT * FROM catalog_pages WHERE id = ?"))
                    {
                        stmt.setInt(1, set.getInt(1));
                        try (ResultSet page = stmt.executeQuery())
                        {
                            if (page.next())
                            {
                                Class<? extends CatalogPage> pageClazz = pageDefinitions.get(page.getString("page_layout"));

                                if (pageClazz != null)
                                {
                                    try
                                    {
                                        catalogPage = pageClazz.getConstructor(ResultSet.class).newInstance(page);
                                    }
                                    catch (Exception e)
                                    {
                                        Emulator.getLogging().logErrorLine(e);
                                    }
                                }
                                else
                                {
                                    Emulator.getLogging().logErrorLine("Unknown Page Layout: " + page.getString("page_layout"));
                                }
                            }
                        }
                    }
                }
            }
        }
        catch (SQLException e)
        {
            Emulator.getLogging().logSQLException(e);
        }

        if(catalogPage != null)
        {
            this.catalogPages.put(catalogPage.getId(), catalogPage);
        }

        return catalogPage;
    }

    /**
     * @param item The CatalogItem to find the limited configuration for.
     * @return The limited configuration for the CatalogItem.
     */
    public CatalogLimitedConfiguration getLimitedConfig(CatalogItem item)
    {
        synchronized (this.limitedNumbers)
        {
            return this.limitedNumbers.get(item.getId());
        }
    }

    /**
     * Creates or updates the CatalogLimitedConfiguration for the given CatalogItem.
     * @param item The CatalogItem to create or update hte CatalogLimitedConfiguration for.
     * @return The created or updated CatalogLimitedConfiguration.
     */
    public CatalogLimitedConfiguration createOrUpdateLimitedConfig(CatalogItem item)
    {
        if (item.isLimited())
        {
            CatalogLimitedConfiguration limitedConfiguration = this.limitedNumbers.get(item.getId());

            if (limitedConfiguration == null)
            {
                limitedConfiguration = new CatalogLimitedConfiguration(item.getId(), new LinkedList<Integer>(), 0);
                limitedConfiguration.generateNumbers(1, item.limitedStack);
                this.limitedNumbers.put(item.getId(), limitedConfiguration);
            }
            else
            {
                if (limitedConfiguration.getTotalSet() != item.limitedStack)
                {
                    if (limitedConfiguration.getTotalSet() == 0)
                    {
                        limitedConfiguration.setTotalSet(item.limitedStack);
                    }
                    else if (item.limitedStack > limitedConfiguration.getTotalSet())
                    {
                        limitedConfiguration.generateNumbers(item.limitedStack + 1, item.limitedStack - limitedConfiguration.getTotalSet());
                    }
                    else
                    {
                        item.limitedStack = limitedConfiguration.getTotalSet();
                    }
                }
            }

            return limitedConfiguration;
        }

        return null;
    }

    /**
     * Disposes the CatalogManager.
     */
    public void dispose()
    {
        TIntObjectIterator<CatalogPage> pageIterator = this.catalogPages.iterator();

        while (pageIterator.hasNext())
        {
            pageIterator.advance();

            for(CatalogItem item : pageIterator.value().getCatalogItems().valueCollection())
            {
                item.run();
                if(item.isLimited())
                {
                    this.limitedNumbers.get(item.getId()).run();
                }
            }
        }

        Emulator.getLogging().logShutdownLine("Catalog Manager -> Disposed!");
    }

    /**
     * Purchases a catalog item.
     * @param page The page to purchase from.
     * @param item The CatalogItem to purchase.
     * @param habbo The Habbo that purchased this CatalogItem.
     * @param amount The amount of times the item should be bought.
     * @param extradata Any extradataassociated.
     * @param free Determines if this purchase is free and thus no credits / points will be deducted.
     */
    public void purchaseItem(CatalogPage page, CatalogItem item, Habbo habbo, int amount, String extradata, boolean free)
    {
        Item cBaseItem = null;

        if(item == null)
        {
            habbo.getClient().sendResponse(new AlertPurchaseFailedComposer(AlertPurchaseFailedComposer.SERVER_ERROR).compose());
            return;
        }

        if(item.isClubOnly() && !habbo.getClient().getHabbo().getHabboStats().hasActiveClub())
        {
            habbo.getClient().sendResponse(new AlertPurchaseUnavailableComposer(AlertPurchaseUnavailableComposer.REQUIRES_CLUB));
            return;
        }

        if (amount <= 0)
        {
            habbo.getClient().sendResponse(new AlertPurchaseUnavailableComposer(AlertPurchaseUnavailableComposer.ILLEGAL));
            return;
        }

        try
        {
            CatalogLimitedConfiguration limitedConfiguration = null;
            int limitedStack = 0;
            int limitedNumber = 0;
            if (item.isLimited())
            {
                amount = 1;
                if (this.getLimitedConfig(item).available() == 0)
                {
                    habbo.getClient().sendResponse(new AlertLimitedSoldOutComposer());
                    return;
                }
            }

            if(amount > 1)
            {
                if(amount == item.getAmount())
                {
                    amount = 1;
                }
                else
                {
                    if(amount * item.getAmount() > 100)
                    {
                        habbo.getClient().sendResponse(new GenericAlertComposer("Whoops! You tried to buy this " + (amount * item.getAmount()) + " times. This must've been a mistake."));
                        habbo.getClient().sendResponse(new AlertPurchaseUnavailableComposer(AlertPurchaseUnavailableComposer.ILLEGAL));
                        return;
                    }
                }
            }

            int totalCredits = 0;
            int totalPoints = 0;

            THashSet<HabboItem> itemsList = new THashSet<HabboItem>();

            /*
                Scripting protection that prevents users from buying multiple items
                when offer is not enabled for the item.

                Automatically creates a new ModToolIssue and sends it to the online moderators.
             */
            if(amount > 1 && !item.isHaveOffer())
            {
                String message = Emulator.getTexts().getValue("scripter.warning.catalog.amount").replace("%username%", habbo.getHabboInfo().getUsername()).replace("%itemname%", item.getName()).replace("%pagename%", page.getCaption());
                Emulator.getGameEnvironment().getModToolManager().quickTicket(habbo.getClient().getHabbo(), "Scripter", message);
                Emulator.getLogging().logUserLine(message);
                habbo.getClient().sendResponse(new AlertPurchaseUnavailableComposer(AlertPurchaseUnavailableComposer.ILLEGAL));
                return;
            }

            if (item.isLimited())
            {
                limitedConfiguration = this.getLimitedConfig(item);

                if (limitedConfiguration == null)
                {
                    limitedConfiguration = this.createOrUpdateLimitedConfig(item);
                }

                limitedNumber = limitedConfiguration.getNumber();
                limitedStack = limitedConfiguration.getTotalSet();
            }

            List<String> badges = new ArrayList<>();
            boolean badgeFound = false;
            for(int i = 0; i < amount; i++)
            {
                if (free || (item.getCredits() <= habbo.getClient().getHabbo().getHabboInfo().getCredits() - totalCredits))
                {
                    if(free ||
                            item.getPoints() <= habbo.getClient().getHabbo().getHabboInfo().getCurrencyAmount(item.getPointsType()) - totalPoints)
                    {
                        if (((i + 1) % 6 != 0 && item.isHaveOffer()) || !item.isHaveOffer())
                        {
                            totalCredits += item.getCredits();
                            totalPoints += item.getPoints();
                        }

                        //for (int j = 0; j < item.getAmount(); j++)
                        //{
                            for (Item baseItem : item.getBaseItems())
                            {
                                for(int k = 0; k < item.getItemAmount(baseItem.getId()); k++)
                                {
                                    cBaseItem = baseItem;
                                    if(baseItem.getName().startsWith("rentable_bot_") || baseItem.getName().startsWith("bot_"))
                                    {
                                        String type = item.getName().replace("rentable_bot_", "");
                                        type = type.replace("bot_", "");

                                        THashMap<String, String> data = new THashMap<String, String>();

                                        for(String s : item.getExtradata().split(";"))
                                        {
                                            if(s.contains(":"))
                                            {
                                                data.put(s.split(":")[0], s.split(":")[1]);
                                            }
                                        }

                                        Bot bot = Emulator.getGameEnvironment().getBotManager().createBot(data, type);

                                        if(bot != null)
                                        {
                                            bot.setOwnerId(habbo.getClient().getHabbo().getHabboInfo().getId());
                                            bot.setOwnerName(habbo.getClient().getHabbo().getHabboInfo().getUsername());
                                            bot.needsUpdate(true);
                                            Emulator.getThreading().run(bot);
                                            habbo.getClient().getHabbo().getInventory().getBotsComponent().addBot(bot);
                                            habbo.getClient().sendResponse(new AddBotComposer(bot));
                                        }
                                        else
                                        {
                                            throw new Exception("Failed to create bot of type: " + type);
                                        }
                                    }
                                    else if (baseItem.getType() == FurnitureType.EFFECT)
                                    {
                                        int effectId = baseItem.getEffectM();

                                        if (habbo.getHabboInfo().getGender().equals(HabboGender.F))
                                        {
                                            effectId = baseItem.getEffectF();
                                        }

                                        if (effectId > 0)
                                        {
                                            habbo.getInventory().getEffectsComponent().createEffect(effectId);
                                        }
                                    }
                                    else if(Item.isPet(baseItem))
                                    {
                                        String[] data = extradata.split("\n");

                                        if (data.length < 3)
                                        {
                                            habbo.getClient().sendResponse(new AlertPurchaseFailedComposer(AlertPurchaseFailedComposer.SERVER_ERROR));
                                            return;
                                        }

                                        Pet pet = null;
                                        try
                                        {
                                            pet = Emulator.getGameEnvironment().getPetManager().createPet(baseItem, data[0], data[1], data[2], habbo.getClient());
                                        }
                                        catch (Exception e)
                                        {
                                            Emulator.getLogging().logErrorLine(e);
                                            habbo.getClient().sendResponse(new AlertPurchaseFailedComposer(AlertPurchaseFailedComposer.SERVER_ERROR));
                                        }

                                        if(pet == null)
                                            return;

                                        habbo.getClient().getHabbo().getInventory().getPetsComponent().addPet(pet);
                                        habbo.getClient().sendResponse(new AddPetComposer(pet));
                                        habbo.getClient().sendResponse(new PetBoughtNotificationComposer(pet, false));

                                        AchievementManager.progressAchievement(habbo.getClient().getHabbo(), Emulator.getGameEnvironment().getAchievementManager().getAchievement("PetLover"));
                                    }
                                    else if (baseItem.getType() == FurnitureType.BADGE)
                                    {
                                        if(!habbo.getInventory().getBadgesComponent().hasBadge(baseItem.getName()))
                                        {
                                            if (!badges.contains(baseItem.getName()))
                                            {
                                                badges.add(baseItem.getName());
                                            }
                                        }
                                        else
                                        {
                                            badgeFound = true;
                                        }
                                    }
                                    else
                                    {
                                        if (baseItem.getInteractionType().getType() == InteractionTrophy.class || baseItem.getInteractionType().getType() == InteractionBadgeDisplay.class)
                                        {
                                            if(baseItem.getInteractionType().getType() == InteractionBadgeDisplay.class && !habbo.getClient().getHabbo().getInventory().getBadgesComponent().hasBadge(extradata))
                                            {
                                                Emulator.getGameEnvironment().getModToolManager().quickTicket(habbo.getClient().getHabbo(), "Scripter", Emulator.getTexts().getValue("scripter.warning.catalog.badge_display").replace("%username%", habbo.getClient().getHabbo().getHabboInfo().getUsername()).replace("%badge%", extradata));
                                                extradata = "UMAD";
                                            }

                                            extradata = habbo.getClient().getHabbo().getHabboInfo().getUsername() + (char) 9 + Calendar.getInstance().get(Calendar.DAY_OF_MONTH) + "-" + (Calendar.getInstance().get(Calendar.MONTH) + 1) + "-" + Calendar.getInstance().get(Calendar.YEAR) + (char) 9 + extradata;
                                        }

                                        if (InteractionTeleport.class.isAssignableFrom(baseItem.getInteractionType().getType()))
                                        {
                                            HabboItem teleportOne = Emulator.getGameEnvironment().getItemManager().createItem(habbo.getClient().getHabbo().getHabboInfo().getId(), baseItem, limitedStack, limitedNumber, extradata);
                                            HabboItem teleportTwo = Emulator.getGameEnvironment().getItemManager().createItem(habbo.getClient().getHabbo().getHabboInfo().getId(), baseItem, limitedStack, limitedNumber, extradata);
                                            Emulator.getGameEnvironment().getItemManager().insertTeleportPair(teleportOne.getId(), teleportTwo.getId());
                                            itemsList.add(teleportOne);
                                            itemsList.add(teleportTwo);
                                        }
                                        else if(baseItem.getInteractionType().getType() == InteractionHopper.class)
                                        {
                                            HabboItem hopper = Emulator.getGameEnvironment().getItemManager().createItem(habbo.getClient().getHabbo().getHabboInfo().getId(), baseItem, limitedStack, limitedNumber, extradata);

                                            Emulator.getGameEnvironment().getItemManager().insertHopper(hopper);

                                            itemsList.add(hopper);
                                        }
                                        else if(baseItem.getInteractionType().getType() == InteractionGuildFurni.class || baseItem.getInteractionType().getType() == InteractionGuildGate.class)
                                        {
                                            InteractionGuildFurni habboItem = (InteractionGuildFurni)Emulator.getGameEnvironment().getItemManager().createItem(habbo.getClient().getHabbo().getHabboInfo().getId(), baseItem, limitedStack, limitedNumber, extradata);
                                            habboItem.setExtradata("");
                                            habboItem.needsUpdate(true);
                                            int guildId;
                                            try
                                            {
                                                guildId = Integer.parseInt(extradata);
                                            }
                                            catch (Exception e)
                                            {
                                                Emulator.getLogging().logErrorLine(e);
                                                habbo.getClient().sendResponse(new AlertPurchaseFailedComposer(AlertPurchaseFailedComposer.SERVER_ERROR));
                                                return;
                                            }
                                            Emulator.getThreading().run(habboItem);
                                            Emulator.getGameEnvironment().getGuildManager().setGuild(habboItem, guildId);
                                            itemsList.add(habboItem);
                                        }
                                        else if(baseItem.getInteractionType().getType() == InteractionMusicDisc.class)
                                        {
                                            SoundTrack track = Emulator.getGameEnvironment().getItemManager().getSoundTrack(item.getExtradata());

                                            if(track == null)
                                            {
                                                habbo.getClient().sendResponse(new AlertPurchaseFailedComposer(AlertPurchaseFailedComposer.SERVER_ERROR));
                                                return;
                                            }

                                            InteractionMusicDisc habboItem = (InteractionMusicDisc)Emulator.getGameEnvironment().getItemManager().createItem(habbo.getClient().getHabbo().getHabboInfo().getId(), baseItem, limitedStack, limitedNumber, habbo.getClient().getHabbo().getHabboInfo().getUsername() + "\n" + Calendar.getInstance().get(Calendar.DAY_OF_MONTH) + "\n" + (Calendar.getInstance().get(Calendar.MONTH) + 1) + "\n" + Calendar.getInstance().get(Calendar.YEAR) + "\n" + track.getLength() + "\n" + track.getName() + "\n" + track.getId());
                                            habboItem.needsUpdate(true);

                                            Emulator.getThreading().run(habboItem);
                                            itemsList.add(habboItem);
                                        }
                                        else
                                        {
                                            HabboItem habboItem = Emulator.getGameEnvironment().getItemManager().createItem(habbo.getClient().getHabbo().getHabboInfo().getId(), baseItem, limitedStack, limitedNumber, extradata);
                                            itemsList.add(habboItem);
                                        }
                                    }
                                }
                            }
                        //}
                    }
                }
            }

            UserCatalogItemPurchasedEvent purchasedEvent = new UserCatalogItemPurchasedEvent(habbo, item, itemsList, totalCredits, totalPoints, badges);
            Emulator.getPluginManager().fireEvent(purchasedEvent);

            if (badgeFound)
            {
                habbo.getClient().sendResponse(new AlertPurchaseFailedComposer(AlertPurchaseFailedComposer.ALREADY_HAVE_BADGE));

                if (item.getBaseItems().size() == 1)
                {
                    return;
                }
            }

            if(!free && !habbo.getClient().getHabbo().hasPermission("acc_infinite_credits"))
            {
                if (purchasedEvent.totalCredits > 0)
                {
                    habbo.getClient().getHabbo().getHabboInfo().addCredits(-purchasedEvent.totalCredits);
                    habbo.getClient().sendResponse(new UserCreditsComposer(habbo.getClient().getHabbo()));
                }
            }

            if(!free && !habbo.getClient().getHabbo().hasPermission("acc_infinite_points"))
            {
                if (purchasedEvent.totalPoints > 0)
                {
                    habbo.getClient().getHabbo().getHabboInfo().addCurrencyAmount(item.getPointsType(), -purchasedEvent.totalPoints);
                    habbo.getClient().sendResponse(new UserPointsComposer(habbo.getClient().getHabbo().getHabboInfo().getCurrencyAmount(item.getPointsType()), -purchasedEvent.totalPoints, item.getPointsType()));
                }
            }

            if (purchasedEvent.itemsList != null)
            {
                habbo.getClient().sendResponse(new AddHabboItemComposer(purchasedEvent.itemsList));
                habbo.getClient().getHabbo().getInventory().getItemsComponent().addItems(purchasedEvent.itemsList);
                habbo.getClient().sendResponse(new PurchaseOKComposer(purchasedEvent.catalogItem));
                habbo.getClient().sendResponse(new InventoryRefreshComposer());

                Emulator.getPluginManager().fireEvent(new UserCatalogFurnitureBoughtEvent(habbo, item, purchasedEvent.itemsList));

                if (limitedConfiguration != null)
                {
                    for (HabboItem itm : purchasedEvent.itemsList)
                    {
                        limitedConfiguration.limitedSold(item.getId(), habbo, itm);
                    }
                }
            }

            for (String b : purchasedEvent.badges)
            {
                HabboBadge badge = new HabboBadge(0, b, 0, habbo);
                Emulator.getThreading().run(badge);
                habbo.getInventory().getBadgesComponent().addBadge(badge);
                habbo.getClient().sendResponse(new AddUserBadgeComposer(badge));
                THashMap<String, String> keys = new THashMap<String, String>();
                keys.put("display", "BUBBLE");
                keys.put("image", "${image.library.url}album1584/" + badge.getCode() + ".gif");
                keys.put("message", Emulator.getTexts().getValue("commands.generic.cmd_badge.received"));
                habbo.getClient().sendResponse(new BubbleAlertComposer(BubbleAlertKeys.RECEIVED_BADGE.key, keys));
            }
            habbo.getClient().getHabbo().getHabboStats().addPurchase(purchasedEvent.catalogItem);

        }
        catch(Exception e)
        {
            Emulator.getLogging().logPacketError(e);
            habbo.getClient().sendResponse(new AlertPurchaseFailedComposer(AlertPurchaseFailedComposer.SERVER_ERROR));
        }
    }

    public List<ClubOffer> getClubOffers()
    {
        List<ClubOffer> offers = new ArrayList<>();

        for (Map.Entry<Integer, ClubOffer> entry : this.clubOffers.entrySet())
        {
            if (!entry.getValue().isDeal())
            {
                offers.add(entry.getValue());
            }
        }

        return offers;
    }

    public void claimCalendarReward(Habbo habbo, int day)
    {
        if (!habbo.getHabboStats().calendarRewardsClaimed.contains(day))
        {
            habbo.getHabboStats().calendarRewardsClaimed.add(day);
            CalendarRewardObject object = this.calendarRewards.get(day);

            if (object != null)
            {
                object.give(habbo);
                habbo.getClient().sendResponse(new InventoryRefreshComposer());
                habbo.getClient().sendResponse(new AdventCalendarProductComposer(true, object));

                try (Connection connection = Emulator.getDatabase().getDataSource().getConnection(); PreparedStatement statement = connection.prepareStatement("INSERT INTO calendar_rewards_claimed (user_id, reward_id, timestamp) VALUES (?, ?, ?)"))
                {
                    statement.setInt(1, habbo.getHabboInfo().getId());
                    statement.setInt(2, day);
                    statement.setInt(3, Emulator.getIntUnixTimestamp());
                    statement.execute();
                }
                catch (SQLException e)
                {
                    Emulator.getLogging().logSQLException(e);
                }
            }


        }
    }
}
