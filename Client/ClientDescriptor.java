package com.simplechat.Client;

import com.simplechat.EProtocol;
import com.simplechat.Interfaces.IStringConsumer;
import com.simplechat.Interfaces.IStringProducer;
import com.simplechat.MessageBoard;

public class ClientDescriptor implements IStringConsumer, IStringProducer {

    private String Nickname;
    private MessageBoard messageBoard;

    public ClientDescriptor()
    {

    }

    @Override
    public void consume(EProtocol eProtocol, String str) {
        if(eProtocol == EProtocol.CONNECT)
        {
            this.Nickname = str;
        }

        messageBoard.consume(eProtocol, str);
    }

    @Override
    public void addConsumer(IStringConsumer sc) {
        this.messageBoard = (MessageBoard) sc;
    }

    @Override
    public void removeConsumer(IStringConsumer sc) {
        this.messageBoard = null;
    }


    public String GetNickname()
    {
        return Nickname;
    }
}
