package com.simplechat;

import com.simplechat.Interfaces.IStringConsumer;
import com.simplechat.Interfaces.IStringProducer;

import java.util.ArrayList;

public class MessageBoard implements IStringConsumer, IStringProducer {

    public static ArrayList<ConnectionProxy> usersOnline = new ArrayList<>();

    @Override
    public void consume(EProtocol eProtocol, String str) {
        switch (eProtocol)
        {
            case CONNECT:
                String nickname = null;
                for( ConnectionProxy serverConnection : usersOnline)
                {
                    nickname = serverConnection.GetClientDescriptor().GetNickname();
                    if(!nickname.equals(str))
                    {
                        serverConnection.consume(eProtocol, str);
                    }
                    else
                    {
                        for( ConnectionProxy connectionProxy : usersOnline)
                        {
                            serverConnection.consume(EProtocol.ADDUSER, connectionProxy.GetClientDescriptor().GetNickname());
                        }
                    }
                }
                break;

            case SENDMSG:
                for( ConnectionProxy serverConnection : usersOnline)
                {
                    serverConnection.consume(eProtocol, str);
                }
                break;

            case DISCONNECT:
                for( ConnectionProxy serverConnection : usersOnline)
                {
                    serverConnection.consume(eProtocol, str);
                }
                break;
        }
    }

    @Override
    public void addConsumer(IStringConsumer sc) {
        usersOnline.add((ConnectionProxy) sc);
    }

    @Override
    public void removeConsumer(IStringConsumer sc) {
        usersOnline.remove(sc);
    }
}
