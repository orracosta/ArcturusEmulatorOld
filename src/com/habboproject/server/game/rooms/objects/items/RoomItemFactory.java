package com.habboproject.server.game.rooms.objects.items;

import com.habboproject.server.game.items.ItemManager;
import com.habboproject.server.game.items.rares.LimitedEditionItemData;
import com.habboproject.server.game.items.types.ItemDefinition;
import com.habboproject.server.game.rooms.objects.items.types.DefaultFloorItem;
import com.habboproject.server.game.rooms.objects.items.types.DefaultWallItem;
import com.habboproject.server.game.rooms.objects.items.types.floor.adjustable.AdjustableHeightFloorItem;
import com.habboproject.server.game.rooms.objects.items.types.floor.adjustable.AdjustableHeightSeatFloorItem;
import com.habboproject.server.game.rooms.objects.items.types.floor.adjustable.MagicStackFloorItem;
import com.habboproject.server.game.rooms.objects.items.types.floor.banzai.*;
import com.habboproject.server.game.rooms.objects.items.types.floor.boutique.MannequinFloorItem;
import com.habboproject.server.game.rooms.objects.items.types.floor.football.*;
import com.habboproject.server.game.rooms.objects.items.types.floor.freeze.*;
import com.habboproject.server.game.rooms.objects.items.types.floor.gates.GateFloorItem;
import com.habboproject.server.game.rooms.objects.items.types.floor.gates.OneWayGateFloorItem;
import com.habboproject.server.game.rooms.objects.items.types.floor.groups.GroupFloorItem;
import com.habboproject.server.game.rooms.objects.items.types.floor.groups.GroupGateFloorItem;
import com.habboproject.server.game.rooms.objects.items.types.floor.hollywood.HaloTileFloorItem;
import com.habboproject.server.game.rooms.objects.items.types.floor.jukebox.SoundMachineFloorItem;
import com.habboproject.server.game.rooms.objects.items.types.floor.others.*;
import com.habboproject.server.game.rooms.objects.items.types.floor.pet.PetBreedingBoxFloorItem;
import com.habboproject.server.game.rooms.objects.items.types.floor.pet.PetToyFloorItem;
import com.habboproject.server.game.rooms.objects.items.types.floor.pet.horse.HorseJumpFloorItem;
import com.habboproject.server.game.rooms.objects.items.types.floor.pirates.CannonFloorItem;
import com.habboproject.server.game.rooms.objects.items.types.floor.rollers.RollerFloorItem;
import com.habboproject.server.game.rooms.objects.items.types.floor.snowboarding.SnowboardJumpFloorItem;
import com.habboproject.server.game.rooms.objects.items.types.floor.snowboarding.SnowboardSlopeFloorItem;
import com.habboproject.server.game.rooms.objects.items.types.floor.summer.SummerShowerFloorItem;
import com.habboproject.server.game.rooms.objects.items.types.floor.teleport.TeleportDoorFloorItem;
import com.habboproject.server.game.rooms.objects.items.types.floor.teleport.TeleportFloorItem;
import com.habboproject.server.game.rooms.objects.items.types.floor.teleport.TeleportPadFloorItem;
import com.habboproject.server.game.rooms.objects.items.types.floor.toner.BackgroundTonerFloorItem;
import com.habboproject.server.game.rooms.objects.items.types.floor.totem.TotemBodyFloorItem;
import com.habboproject.server.game.rooms.objects.items.types.floor.totem.TotemHeadFloorItem;
import com.habboproject.server.game.rooms.objects.items.types.floor.totem.TotemPlanetFloorItem;
import com.habboproject.server.game.rooms.objects.items.types.floor.valentines.LoveLockFloorItem;
import com.habboproject.server.game.rooms.objects.items.types.floor.wired.actions.*;
import com.habboproject.server.game.rooms.objects.items.types.floor.wired.addons.*;
import com.habboproject.server.game.rooms.objects.items.types.floor.wired.conditions.negative.*;
import com.habboproject.server.game.rooms.objects.items.types.floor.wired.conditions.positive.*;
import com.habboproject.server.game.rooms.objects.items.types.floor.wired.highscore.HighscoreClassicFloorItem;
import com.habboproject.server.game.rooms.objects.items.types.floor.wired.highscore.HighscoreMostWinFloorItem;
import com.habboproject.server.game.rooms.objects.items.types.floor.wired.highscore.HighscoreTeamFloorItem;
import com.habboproject.server.game.rooms.objects.items.types.floor.wired.triggers.*;
import com.habboproject.server.game.rooms.objects.items.types.wall.MoodlightWallItem;
import com.habboproject.server.game.rooms.objects.items.types.wall.PostItWallItem;
import com.habboproject.server.game.rooms.objects.items.types.wall.WheelWallItem;
import com.habboproject.server.game.rooms.types.Room;
import org.apache.log4j.Logger;

import java.util.HashMap;


public class RoomItemFactory {
    private static final int processMs = 500;
    private static final String GIFT_DATA = "GIFT::##";
    public static final String STACK_TOOL = "tile_stackmagic";
    public static final String TELEPORT_PAD = "teleport_pad";

    private static final Logger log = Logger.getLogger(RoomItemFactory.class.getName());

    private static final HashMap<String, Class<? extends RoomItemFloor>> itemDefinitionMap;

    static {
        itemDefinitionMap = new HashMap<String, Class<? extends RoomItemFloor>>() {{
            this.put("roller", RollerFloorItem.class);
            this.put("dice", DiceFloorItem.class);
            this.put("teleport", TeleportFloorItem.class);
            this.put("teleport_door", TeleportDoorFloorItem.class);
            this.put("teleport_pad", TeleportPadFloorItem.class);
            this.put("quick_teleport", TeleportPadFloorItem.class);
            this.put("onewaygate", OneWayGateFloorItem.class);
            this.put("gate", GateFloorItem.class);
            this.put("roombg", BackgroundTonerFloorItem.class);
            this.put("bed", BedFloorItem.class);
            this.put("vendingmachine", VendingMachineFloorItem.class);
            this.put("mannequin", MannequinFloorItem.class);
            this.put("beach_shower", SummerShowerFloorItem.class);
            this.put("halo_tile", HaloTileFloorItem.class);
            this.put("adjustable_height_seat", AdjustableHeightSeatFloorItem.class);
            this.put("adjustable_height", AdjustableHeightFloorItem.class);
            this.put("lovelock", LoveLockFloorItem.class);
            this.put("soundmachine", SoundMachineFloorItem.class);
            this.put("privatechat", PrivateChatFloorItem.class);
            this.put("wf_box", WiredAddonPuzzleBoxFloorItem.class);
            this.put("wf_act_bot_give_handitem", WiredActionBotGiveHandItem.class);
            this.put("wf_act_bot_move", WiredActionBotMove.class);
            this.put("wf_act_bot_clothes", WiredActionBotClothes.class);
            this.put("wf_act_bot_clothes", WiredActionBotClothes.class);
            this.put("wf_act_bot_follow_avatar", WiredActionBotFollowAvatar.class);
            this.put("wf_act_bot_talk_to_avatar", WiredActionBotTalkToAvatar.class);
            this.put("wf_act_give_score_tm", WiredActionGiveScoreTeam.class);
            this.put("wf_act_bot_talk", WiredActionBotTalk.class);
            this.put("wf_act_give_score", WiredActionGiveScore.class);
            this.put("wf_act_bot_teleport", WiredActionBotTeleport.class);
            this.put("wf_act_toggle_to_rnd", WiredActionToggleToRandom.class);
            this.put("wf_act_call_stacks", WiredActionCallStacks.class);
            this.put("wf_act_mute_triggerer", WiredActionMuteTriggerer.class);
            this.put("wf_act_flee", WiredActionFlee.class);
            this.put("wf_act_match_to_sshot", WiredActionMatchToSnapshot.class);
            this.put("wf_act_teleport_to", WiredActionTeleportPlayer.class);
            this.put("wf_act_show_message", WiredActionShowMessage.class);
            this.put("wf_act_toggle_state", WiredActionToggleState.class);
            this.put("wf_act_give_reward", WiredActionGiveReward.class);
            this.put("wf_act_move_rotate", WiredActionMoveRotate.class);
            this.put("wf_act_chase", WiredActionChase.class);
            this.put("wf_act_kick_user", WiredActionKickUser.class);
            this.put("wf_act_reset_timers", WiredActionResetTimers.class);
            this.put("wf_act_join_team", WiredActionJoinTeam.class);
            this.put("wf_act_leave_team", WiredActionLeaveTeam.class);
            this.put("wf_act_move_to_dir", WiredActionMoveToDirection.class);
            this.put("wf_trg_bot_reached_stf", WiredTriggerBotReachedFurni.class);
            this.put("wf_trg_bot_reached_avtr", WiredTriggerBotReachedAvatar.class);
            this.put("wf_trg_says_something", WiredTriggerPlayerSaysKeyword.class);
            this.put("wf_trg_enter_room", WiredTriggerEnterRoom.class);
            this.put("wf_trg_periodically", WiredTriggerPeriodically.class);
            this.put("wf_trg_walks_off_furni", WiredTriggerWalksOffFurni.class);
            this.put("wf_trg_walks_on_furni", WiredTriggerWalksOnFurni.class);
            this.put("wf_trg_state_changed", WiredTriggerStateChanged.class);
            this.put("wf_trg_game_starts", WiredTriggerGameStarts.class);
            this.put("wf_trg_game_ends", WiredTriggerGameEnds.class);
            this.put("wf_trg_collision", WiredTriggerCollision.class);
            this.put("wf_trg_period_long", WiredTriggerPeriodicallyLong.class);
            this.put("wf_trg_at_given_time", WiredTriggerAtGivenTime.class);
            this.put("wf_trg_at_time_long", WiredTriggerAtGivenTimeLong.class);
            this.put("wf_trg_score_achieved", WiredTriggerScoreAchieved.class);
            this.put("wf_cnd_date_rng_active", WiredConditionDateRangeActive.class);
            this.put("wf_cnd_stuff_is", WiredConditionStuffIs.class);
            this.put("wf_cnd_not_stuff_is", WiredNegativeConditionStuffIs.class);
            this.put("wf_cnd_actor_in_team", WiredConditionActorInTeam.class);
            this.put("wf_cnd_not_in_team", WiredNegativeConditionActorInTeam.class);
            this.put("wf_cnd_trggrer_on_frn", WiredConditionTriggererOnFurni.class);
            this.put("wf_cnd_not_trggrer_on", WiredNegativeConditionTriggererOnFurni.class);
            this.put("wf_cnd_actor_in_group", WiredConditionPlayerInGroup.class);
            this.put("wf_cnd_not_in_group", WiredNegativeConditionPlayerInGroup.class);
            this.put("wf_cnd_furnis_hv_avtrs", WiredConditionFurniHasPlayers.class);
            this.put("wf_cnd_not_hv_avtrs", WiredNegativeConditionFurniHasPlayers.class);
            this.put("wf_cnd_wearing_badge", WiredConditionPlayerHasBadgeEquipped.class);
            this.put("wf_cnd_not_wearing_b", WiredNegativeConditionPlayerHasBadgeEquipped.class);
            this.put("wf_cnd_wearing_effect", WiredConditionPlayerWearingEffect.class);
            this.put("wf_cnd_not_wearing_fx", WiredNegativeConditionPlayerWearingEffect.class);
            this.put("wf_cnd_has_furni_on", WiredConditionHasFurniOn.class);
            this.put("wf_cnd_not_furni_on", WiredNegativeConditionHasFurniOn.class);
            this.put("wf_cnd_user_count_in", WiredConditionPlayerCountInRoom.class);
            this.put("wf_cnd_not_user_count", WiredConditionPlayerCountInRoom.class);
            this.put("wf_cnd_match_snapshot", WiredConditionMatchSnapshot.class);
            this.put("wf_cnd_not_match_snap", WiredNegativeConditionMatchSnapshot.class);
            this.put("wf_cnd_has_handitem", WiredConditionHasHandItem.class);
            this.put("wf_cnd_time_more_than", WiredConditionTimeMoreThan.class);
            this.put("wf_cnd_time_less_than", WiredConditionTimeLessThan.class);
            this.put("wf_xtra_random", WiredAddonRandomEffect.class);
            this.put("wf_xtra_unseen", WiredAddonUnseenEffect.class);
            this.put("wf_floor_switch1", WiredAddonFloorSwitch.class);
            this.put("wf_floor_switch2", WiredAddonFloorSwitch.class);
            this.put("wf_colorwheel", WiredAddonColourWheel.class);
            this.put("wf_pressureplate", WiredAddonPressurePlate.class);
            this.put("wf_arrowplate", WiredAddonPressurePlate.class);
            this.put("wf_ringplate", WiredAddonPressurePlate.class);
            this.put("wf_pyramid", WiredAddonPyramid.class);
            this.put("wf_visual_timer", WiredAddonVisualTimer.class);
            this.put("highscore_classic", HighscoreClassicFloorItem.class);
            this.put("highscore_team", HighscoreTeamFloorItem.class);
            this.put("highscore_mostwin", HighscoreMostWinFloorItem.class);
            this.put("pressureplate_seat", PressurePlateSeatFloorItem.class);
            this.put("group_item", GroupFloorItem.class);
            this.put("group_forum", GroupFloorItem.class);
            this.put("group_gate", GroupGateFloorItem.class);
            this.put("bb_teleport", BanzaiTeleportFloorItem.class);
            this.put("bb_red_gate", BanzaiGateFloorItem.class);
            this.put("bb_yellow_gate", BanzaiGateFloorItem.class);
            this.put("bb_blue_gate", BanzaiGateFloorItem.class);
            this.put("bb_green_gate", BanzaiGateFloorItem.class);
            this.put("bb_patch", BanzaiTileFloorItem.class);
            this.put("bb_timer", BanzaiTimerFloorItem.class);
            this.put("bb_puck", BanzaiPuckFloorItem.class);
            this.put("bb_apparatus", BanzaiSphereFloorItem.class);
            this.put("ball", FootballFloorItem.class);
            this.put("football_timer", FootballTimerFloorItem.class);
            this.put("football_gate", FootballGateFloorItem.class);
            this.put("football_goal", FootballGoalFloorItem.class);
            this.put("football_score", FootballScoreFloorItem.class);
            this.put("snowb_slope", SnowboardSlopeFloorItem.class);
            this.put("snowb_rail", SnowboardJumpFloorItem.class);
            this.put("snowb_jump", SnowboardJumpFloorItem.class);
            this.put("freeze_exittile", FreezeExitTileFloorItem.class);
            this.put("freeze_tile", FreezeTileFloorItem.class);
            this.put("freeze_red_gate", FreezeGateFloorItem.class);
            this.put("freeze_yellow_gate", FreezeGateFloorItem.class);
            this.put("freeze_blue_gate", FreezeGateFloorItem.class);
            this.put("freeze_green_gate", FreezeGateFloorItem.class);
            this.put("freeze_timer", FreezeTimerFloorItem.class);
            this.put("freeze_block", FreezeBlockFloorItem.class);
            this.put("totem_planet", TotemPlanetFloorItem.class);
            this.put("totem_head", TotemHeadFloorItem.class);
            this.put("totem_body", TotemBodyFloorItem.class);
            this.put("pet_toy", PetToyFloorItem.class);
            this.put("cannon", CannonFloorItem.class);
            this.put("horse_jump", HorseJumpFloorItem.class);
            this.put("water", WaterFloorItem.class);
            this.put("badge_display", BadgeDisplayFloorItem.class);
            this.put("breeding_dog", PetBreedingBoxFloorItem.class);
            this.put("breeding_cat", PetBreedingBoxFloorItem.class);
            this.put("breeding_terrier", PetBreedingBoxFloorItem.class);
            this.put("breeding_bear", PetBreedingBoxFloorItem.class);
            this.put("breeding_pig", PetBreedingBoxFloorItem.class);
        }};
    }

    public static RoomItemFloor createFloor(long id, int baseId, Room room, int ownerId, int groupId, int x, int y, double height, int rot, String data, LimitedEditionItemData limitedEditionItemData) {
        ItemDefinition def = ItemManager.getInstance().getDefinition(baseId);
        RoomItemFloor floorItem = null;

        if (def == null) {
            return null;
        }

        if (def.canSit()) {
            floorItem = new SeatFloorItem(id, baseId, room, ownerId, groupId, x, y, height, rot, data);
        }

        if (def.getItemName().startsWith(STACK_TOOL)) {
            floorItem = new MagicStackFloorItem(id, baseId, room, ownerId, groupId, x, y, height, rot, data);
        }

        if (data.startsWith(GIFT_DATA)) {
            try {
                floorItem = new GiftFloorItem(id, baseId, room, ownerId, groupId, x, y, height, rot, data);
            } catch (Exception e) {
                return null;
            }
        } else {
            if (itemDefinitionMap.containsKey(def.getInteraction())) {
                try {
                    floorItem = itemDefinitionMap.get(def.getInteraction()).getConstructor(long.class, int.class, Room.class, int.class, int.class, int.class, int.class, double.class, int.class, String.class)
                            .newInstance(id, baseId, room, ownerId, groupId, x, y, height, rot, data);
                } catch (Exception e) {
                    log.warn("Failed to create instance for item: " + id + ", type: " + def.getInteraction(), e);
                }
            }
        }

        if (floorItem == null) {
            floorItem = new DefaultFloorItem(id, baseId, room, ownerId, groupId, x, y, height, rot, data);
        }

        if (limitedEditionItemData != null) {
            floorItem.setLimitedEditionItemData(limitedEditionItemData);
        }

        return floorItem;
    }

    public static RoomItemWall createWall(long id, int baseId, Room room, int owner, String position, String data, LimitedEditionItemData limitedEditionItemData) {
        ItemDefinition def = ItemManager.getInstance().getDefinition(baseId);
        if (def == null) {
            return null;
        }

        RoomItemWall wallItem;

        switch (def.getInteraction()) {
            case "habbowheel": {
                wallItem = new WheelWallItem(id, baseId, room, owner, position, data);
                break;
            }
            case "dimmer": {
                wallItem = new MoodlightWallItem(id, baseId, room, owner, position, data);
                break;
            }
            case "postit": {
                wallItem = new PostItWallItem(id, baseId, room, owner, position, data);
                break;
            }
            default: {
                wallItem = new DefaultWallItem(id, baseId, room, owner, position, data);
                break;
            }
        }

        if (limitedEditionItemData != null) {
            wallItem.setLimitedEditionItemData(limitedEditionItemData);
        }

        return wallItem;
    }

    public static int getProcessTime(double time) {
        long realTime = Math.round(time * 1000 / processMs);

        if (realTime < 1) {
            realTime = 1; //0.5s
        }

        return (int) realTime;
    }
}
