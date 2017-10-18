package com.eu.habbo.messages.incoming.rooms.pets;

import com.eu.habbo.Emulator;
import com.eu.habbo.habbohotel.pets.AbstractPet;
import com.eu.habbo.habbohotel.pets.Pet;
import com.eu.habbo.habbohotel.rooms.Room;
import com.eu.habbo.habbohotel.users.HabboItem;
import com.eu.habbo.messages.incoming.MessageHandler;
import com.eu.habbo.messages.outgoing.inventory.AddPetComposer;
import com.eu.habbo.messages.outgoing.inventory.InventoryRefreshComposer;
import com.eu.habbo.messages.outgoing.rooms.items.RemoveFloorItemComposer;
import com.eu.habbo.messages.outgoing.rooms.pets.PetPackageNameValidationComposer;
import com.eu.habbo.threading.runnables.QueryDeleteHabboItem;

public class PetPackageNameEvent extends MessageHandler
{
    @Override
    public void handle() throws Exception
    {
        int itemId = this.packet.readInt();
        String name = this.packet.readString();

        Room room = this.client.getHabbo().getHabboInfo().getCurrentRoom();

        if (room != null)
        {
            HabboItem item = room.getHabboItem(itemId);
            if (item != null)
            {
                if (item.getUserId() == this.client.getHabbo().getHabboInfo().getId())
                {
                    if (name.matches("^[a-zA-Z0-9]*$"))
                    {
                        Pet pet = null;

                        if (item.getBaseItem().getName().equalsIgnoreCase("val11_present"))
                        {
                            pet = Emulator.getGameEnvironment().getPetManager().createPet(11, name, this.client);
                        }

                        if (item.getBaseItem().getName().equalsIgnoreCase("gnome_box"))
                        {
                            pet = Emulator.getGameEnvironment().getPetManager().createGnome(name, room, this.client.getHabbo());
                        }

                        if (item.getBaseItem().getName().equalsIgnoreCase("leprechaun_box"))
                        {
                            pet = Emulator.getGameEnvironment().getPetManager().createLeprechaun(name, room, this.client.getHabbo());
                        }

                        if (item.getBaseItem().getName().equalsIgnoreCase("velociraptor_egg"))
                        {
                            pet = Emulator.getGameEnvironment().getPetManager().createPet(34, name, this.client);
                        }

                        if (item.getBaseItem().getName().equalsIgnoreCase("pterosaur_egg"))
                        {
                            pet = Emulator.getGameEnvironment().getPetManager().createPet(33, name, this.client);
                        }

                        if (item.getBaseItem().getName().equalsIgnoreCase("petbox_epic"))
                        {
                            pet = Emulator.getGameEnvironment().getPetManager().createPet(32, name, this.client);
                        }

                        if (pet != null)
                        {
                            room.placePet((AbstractPet) pet, item.getX(), item.getY(), item.getZ(), item.getRotation());
                            pet.setUserId(this.client.getHabbo().getHabboInfo().getId());
                            pet.needsUpdate = true;
                            pet.getRoomUnit().setLocation(room.getLayout().getTile(item.getX(), item.getY()));
                            pet.getRoomUnit().setZ(item.getZ());
                            Emulator.getThreading().run(new QueryDeleteHabboItem(item));
                            room.removeHabboItem(item);
                            room.updateTile(room.getLayout().getTile(item.getX(), item.getY()));
                            item.setUserId(0);
                            room.sendComposer(new RemoveFloorItemComposer(item).compose());
                        }
                    }
                    else
                    {
                        this.client.sendResponse(new PetPackageNameValidationComposer(itemId, PetPackageNameValidationComposer.CONTAINS_INVALID_CHARS, name.replaceAll("^[a-zA-Z0-9]*$", "")));
                        return;
                    }
                }
            }
        }


        this.client.sendResponse(new PetPackageNameValidationComposer(itemId, PetPackageNameValidationComposer.CLOSE_WIDGET, ""));
        return;
    }
}