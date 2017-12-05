package com.eu.habbo.messages.outgoing.rooms.pets.breeding;

import com.eu.habbo.messages.ServerMessage;
import com.eu.habbo.messages.outgoing.MessageComposer;
import com.eu.habbo.messages.outgoing.Outgoing;

public class PetBreedingFailedComposer extends MessageComposer
{
    public final int anInt1;
    public final int anInt2;

    public PetBreedingFailedComposer(int anInt1, int anInt2)
    {
        this.anInt1 = anInt1;
        this.anInt2 = anInt2;
    }

    @Override
    public ServerMessage compose()
    {
        this.response.init(Outgoing.PetBreedingFailedComposer);
        this.response.appendInt(this.anInt1);
        this.response.appendInt(this.anInt2);
        return this.response;
    }
}