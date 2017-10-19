package com.eu.habbo.essentials.commands;

import com.eu.habbo.Emulator;
import com.eu.habbo.habbohotel.rooms.RoomUnitEffect;

public class HoverBoardCommand extends EffectCommand
{
    public HoverBoardCommand()
    {
        super(
                new int[]{RoomUnitEffect.HOVERBOARD.getId(),
                        RoomUnitEffect.HOVERBOARDPINK.getId(),
                        RoomUnitEffect.HOVERBOARDYELLOW.getId(),
                        RoomUnitEffect.HOVERBOARDGREEN.getId()},
                Emulator.getTexts().getValue("essentials.cmd_hoverboard.keys").split(";"),
                Emulator.getTexts().getValue("essentials.cmd_hoverboard.texts").split(";"));
    }
}