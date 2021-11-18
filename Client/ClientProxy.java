package com.simplechat.Client;

import com.simplechat.EProtocol;
import com.simplechat.Interfaces.IStringConsumer;
import com.simplechat.Interfaces.IStringProducer;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

public class ClientProxy extends Thread implements IStringConsumer, IStringProducer {

    private Socket _socket = null;
    private DataInputStream dis;
    private DataOutputStream dos;
    private ClientGUI _clientGUI;

    private String msg = null;

    public ClientProxy(Socket socket, ClientGUI clientGUI) {
        _socket = socket;
        _clientGUI = clientGUI;

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

        while(true)
        {
            try {
                String protocol = dis.readUTF();
                String msg = dis.readUTF();

                _clientGUI.SetAction(EProtocol.FromStringToEProtocol(protocol), msg);

            } catch (IOException e) {
                e.printStackTrace();
                try {
                    this._socket.close();

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
        this._clientGUI = (ClientGUI) sc;
    }

    @Override
    public void removeConsumer(IStringConsumer sc) {
        this._clientGUI = null;
    }

}
