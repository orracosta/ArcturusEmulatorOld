package com.eu.habbo.messages.outgoing.unknown;

import com.eu.habbo.messages.ServerMessage;
import com.eu.habbo.messages.outgoing.MessageComposer;
import com.eu.habbo.messages.outgoing.Outgoing;

public class QuestionInfoComposer extends MessageComposer
{
    @Override
    public ServerMessage compose()
    {
        this.response.init(Outgoing.QuestionInfoComposer);
        /*
        this._SafeStr_17817 = k.readString();
            this._SafeStr_16321 = k._SafeStr_5331();
            this._SafeStr_7446 = k._SafeStr_5331();
            this._SafeStr_5614 = k._SafeStr_5331();
            this._SafeStr_16322 = new Dictionary();
            this._SafeStr_16322["id"] = k._SafeStr_5331();
            this._SafeStr_16322["number"] = k._SafeStr_5331();
            this._SafeStr_16322["type"] = k._SafeStr_5331();
            this._SafeStr_16322["content"] = k.readString();
            if ((((this._SafeStr_16322["type"] == 1)) || ((this._SafeStr_16322["type"] == 2)))){
                this._SafeStr_16322["selection_min"] = k._SafeStr_5331();
                _local_2 = k._SafeStr_5331();
                _local_3 = new Array();
                _local_4 = new Array();
                this._SafeStr_16322["selections"] = _local_3;
                this._SafeStr_16322["selection_values"] = _local_4;
                this._SafeStr_16322["selection_count"] = _local_2;
                this._SafeStr_16322["selection_max"] = _local_2;
                _local_5 = 0;
                while (_local_5 < _local_2) {
                    _local_4.push(k.readString());
                    _local_3.push(k.readString());
                    _local_5++;
                };
            };
         */
        return this.response;
    }
}