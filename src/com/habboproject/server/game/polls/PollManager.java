package com.habboproject.server.game.polls;

import com.habboproject.server.game.polls.types.Poll;
import com.habboproject.server.game.polls.types.PollQuestion;
import com.habboproject.server.game.polls.types.questions.MultipleChoiceQuestion;
import com.habboproject.server.storage.queries.polls.PollDao;
import com.habboproject.server.utilities.Initializable;
import org.apache.log4j.Logger;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class PollManager implements Initializable {
    private static PollManager pollManagerInstance;
    private static Logger log = Logger.getLogger(PollManager.class.getName());

    private Map<Integer, Poll> polls;
    private final Map<Integer, Integer> roomIdToPollId;

    public PollManager() {
        this.polls = new ConcurrentHashMap<>();
        this.roomIdToPollId = new ConcurrentHashMap<>();
    }

    @Override
    public void initialize() {
        if(this.polls != null) {
            for(Poll poll : this.polls.values()) {
                for(PollQuestion pollQuestion : poll.getPollQuestions().values()) {
                    if(pollQuestion instanceof MultipleChoiceQuestion) {
                        ((MultipleChoiceQuestion) pollQuestion).getChoices().clear();
                    }
                }

                poll.getPollQuestions().clear();
            }

            this.polls.clear();
            this.roomIdToPollId.clear();
        }

        this.polls = PollDao.getAllPolls();

        for(Poll poll : this.getPolls().values()) {
            if(!this.roomIdToPollId.containsKey(poll.getRoomId())) {
                this.roomIdToPollId.put(poll.getRoomId(), poll.getPollId());
            }
        }

        log.info("Loaded " + this.getPolls().size() + " poll(s)");
    }

    public boolean roomHasPoll(int roomId) {
        return this.roomIdToPollId.containsKey(roomId);
    }

    public Poll getPollByRoomId(int roomId) {
        if(!this.roomIdToPollId.containsKey(roomId)) {
            return null;
        }

        return this.getPollbyId(this.roomIdToPollId.get(roomId));
    }

    public Poll getPollbyId(int pollId) {
        return this.polls.get(pollId);
    }

    public Map<Integer, Poll> getPolls() {
        return polls;
    }

    public static PollManager getInstance() {
        if(pollManagerInstance == null) {
            pollManagerInstance = new PollManager();
        }

        return pollManagerInstance;
    }
}
