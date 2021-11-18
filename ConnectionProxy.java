package com.simplechat;

import com.simplechat.Client.ClientDescriptor;
import com.simplechat.Client.ClientGUI;
import com.simplechat.Interfaces.IStringConsumer;
import com.simplechat.Interfaces.IStringProducer;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

public class ConnectionProxy extends Thread implements IStringConsumer, IStringProducer {

    private Socket _socket = null;
    private DataInputStream dis;
    private DataOutputStream dos;
    private ClientDescriptor clientDescriptor;

    private String protocol = "";
    private String msg = "";

    public ConnectionProxy(Socket socket) {
        _socket = socket;

        try {
            dis = new DataInputStream(_socket.getInputStream());
            dos = new DataOutputStream(_socket.getOutputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        super.run();

        while (true)
        {
            try {
                protocol = dis.readUTF();
                msg = dis.readUTF();

                if(!protocol.isEmpty() && !msg.isEmpty())
                {
                    switch (protocol) {
                        case "CONNECT":
                            clientDescriptor.consume(EProtocol.FromStringToEProtocol(protocol), msg);
                            break;

                        case "SENDMSG":
                            clientDescriptor.consume(EProtocol.FromStringToEProtocol(protocol), msg);
                            break;

                        case "DISCONNECT":

                            break;
                    }
                }

            } catch (IOException e) {
                e.printStackTrace();
                try {
                    
                    this._socket.close();
                    MessageBoard.usersOnline.remove(this);
                    clientDescriptor.consume(EProtocol.DISCONNECT, clientDescriptor.GetNickname());

                } catch (IOException ex) {
                    ex.printStackTrace();
                }
                break;
            }
        }
    }


    @Override
    public void consume(EProtocol eProtocol, String str) {
        try {
            dos.writeUTF(eProtocol.toString());
            dos.writeUTF(str);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void addConsumer(IStringConsumer sc) {
        this.clientDescriptor = (ClientDescriptor) sc;
    }

    @Override
    public void removeConsumer(IStringConsumer sc) {
        this.clientDescriptor = null;
    }


    // setters and getters
    public ClientDescriptor GetClientDescriptor()
    {
        return clientDescriptor;
    }
}
