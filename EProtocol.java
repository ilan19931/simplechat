package com.simplechat;

public enum EProtocol {
    CONNECT,
    ADDUSER,
    SENDMSG,
    DISCONNECT;

    public static EProtocol FromStringToEProtocol(String str)
    {
        if (!str.isEmpty())
        {
            switch (str)
            {
                case "CONNECT":
                    return EProtocol.CONNECT;

                case "ADDUSER":
                    return EProtocol.ADDUSER;

                case "SENDMSG":
                    return EProtocol.SENDMSG;

                case "DISCONNECT":
                    return EProtocol.DISCONNECT;

                default:
                    return null;
            }
        }

        return null;
    }
}
