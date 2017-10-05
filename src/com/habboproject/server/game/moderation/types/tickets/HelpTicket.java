package com.habboproject.server.game.moderation.types.tickets;

import com.habboproject.server.api.networking.messages.IComposer;
import com.habboproject.server.boot.Comet;
import com.habboproject.server.game.rooms.types.components.types.ChatMessage;
import com.habboproject.server.storage.queries.moderation.TicketDao;
import com.habboproject.server.storage.queries.player.PlayerDao;

import java.util.List;


public class HelpTicket {

    private int id;
    private int categoryId;
    private int roomId;

    private int dateSubmitted;
    private int dateClosed;

    private int submitterId;
    private int reportedId;
    private int moderatorId;

    private String message;

    private String submitterUsername;
    private String reportedUsername;
    private String moderatorUsername;

    private HelpTicketState state;
    private List<ChatMessage> chatMessages;

    public HelpTicket(int id, int categoryId, int dateSubmitted, int dateClosed, int submitterId, int reportedId, int moderatorId, String message, HelpTicketState state, List<ChatMessage> chatMessages, int roomId) {
        this.id = id;
        this.categoryId = categoryId;
        this.dateSubmitted = dateSubmitted;
        this.dateClosed = dateClosed;
        this.submitterId = submitterId;
        this.reportedId = reportedId;
        this.moderatorId = moderatorId;
        this.message = message;
        this.state = state;
        this.chatMessages = chatMessages;
        this.roomId = roomId;
    }

    public void save() {
        // Queue to be saved ??

        TicketDao.saveTicket(this);
    }

    public void compose(IComposer msg) {
        msg.writeInt(this.getId());
        msg.writeInt(this.getState().getTabId());
        msg.writeInt(3); // style
        msg.writeInt(this.getCategoryId());
        msg.writeInt((int) (Comet.getTime() - this.getDateSubmitted()) * 1000);
        msg.writeInt(1); // Priority.
        msg.writeInt(0);
        msg.writeInt(this.getSubmitterId());
        msg.writeString(this.getSubmitterUsername());
        msg.writeInt(this.getReportedId());
        msg.writeString(this.getReportedUsername());
        msg.writeInt(this.getModeratorId());
        msg.writeString(this.getModeratorId() != 0 ? this.getModeratorUsername() : "");
        msg.writeString(this.getMessage());
        msg.writeInt(0); // Public room?
        msg.writeInt(this.getChatMessages().size());

        for (ChatMessage chatMessage : this.getChatMessages()) {
            msg.writeString(chatMessage.getMessage());
            msg.writeInt(-1);
            msg.writeInt(-1);
        }
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getDateSubmitted() {
        return dateSubmitted;
    }

    public void setDateSubmitted(int dateSubmitted) {
        this.dateSubmitted = dateSubmitted;
    }

    public int getDateClosed() {
        return dateClosed;
    }

    public void setDateClosed(int dateClosed) {
        this.dateClosed = dateClosed;
    }

    public int getSubmitterId() {
        return submitterId;
    }

    public void setSubmitterId(int submitterId) {
        this.submitterId = submitterId;
    }

    public int getReportedId() {
        return reportedId;
    }

    public void setReportedId(int reportedId) {
        this.reportedId = reportedId;
    }

    public int getModeratorId() {
        return moderatorId;
    }

    public void setModeratorId(int moderatorId) {
        this.moderatorId = moderatorId;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public HelpTicketState getState() {
        return state;
    }

    public void setState(HelpTicketState state) {
        this.state = state;
    }

    public List<ChatMessage> getChatMessages() {
        return chatMessages;
    }

    public void setChatMessages(List<ChatMessage> chatMessages) {
        this.chatMessages = chatMessages;
    }

    public int getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(int categoryId) {
        this.categoryId = categoryId;
    }

    public int getRoomId() {
        return roomId;
    }

    public void setRoomId(int roomId) {
        this.roomId = roomId;
    }

    public String getSubmitterUsername() {
        if (this.submitterUsername == null) {
            this.submitterUsername = PlayerDao.getUsernameByPlayerId(this.getSubmitterId());
        }

        return submitterUsername;
    }

    public String getReportedUsername() {
        if (this.reportedUsername == null) {
            this.reportedUsername = PlayerDao.getUsernameByPlayerId(this.getReportedId());
        }

        return reportedUsername;
    }

    public String getModeratorUsername() {
        if (this.moderatorUsername == null) {
            this.moderatorUsername = PlayerDao.getUsernameByPlayerId(this.getModeratorId());
        }

        return moderatorUsername;
    }
}
