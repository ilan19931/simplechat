package com.simplechat.Client;

import com.simplechat.ConnectionProxy;
import com.simplechat.EProtocol;
import com.simplechat.Interfaces.IStringConsumer;
import com.simplechat.Interfaces.IStringProducer;
import com.simplechat.ServerApplication;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.net.Socket;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;

public class ClientGUI implements IStringConsumer, IStringProducer {

    ClientProxy clientProxy;
    Socket socket;

    JFrame frame = new JFrame("chat application");

    JFrame frameGetUsername = new JFrame("enter username");
    JTextField textUsername = new JTextField(15);


    JTextArea textArea = new JTextArea(20, 65);
    JScrollPane scroll = new JScrollPane(textArea);


    //list of users
    JPanel panelUsers = new JPanel();
    JLabel labelUsers = new JLabel("Users Online");
    JList<String> listUsers = new JList<>();

    //send msg
    JPanel panelSendMsg = new JPanel();
    JLabel labelSendMsg = new JLabel("Enter Message: ");
    JTextField textMessage = new JTextField(30);
    JButton btnSendMsg = new JButton("Send");

    String username = null;
    DefaultListModel usersList = new DefaultListModel();

    public ClientGUI()
    {
        startApp();
    }

    private void startApp()
    {
        this.getUsername();
        this.connectToServer();
    }

    private void connectToServer()
    {
        try {
            socket = new Socket("127.0.0.1", ServerApplication.ServerPort);
            clientProxy = new ClientProxy(socket, this);
            clientProxy.addConsumer(this);
            clientProxy.start();


        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void initApp()
    {
        frame.setLayout(new BorderLayout());
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.setVisible(true);
        //frame.setResizable(false);
        frame.setSize(600,400);

        // TEXT AREA
        scroll.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
        textArea.setBackground(new Color(220,220,200));
        textArea.setEditable(false);

        frame.add(scroll, BorderLayout.CENTER);

        // set users list section
        panelUsers.setLayout(new BoxLayout(panelUsers, BoxLayout.Y_AXIS));
        panelUsers.add(labelUsers);
        panelUsers.add(listUsers);
        frame.add(panelUsers, BorderLayout.EAST);

        // set send message section
        labelSendMsg.setText(username + " : ");
        btnSendMsg.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if(!textMessage.getText().isEmpty())
                {
                    btnSendMsgClicked();
                }
            }
        });
        panelSendMsg.setLayout(new FlowLayout());
        panelSendMsg.add(labelSendMsg);
        panelSendMsg.add(textMessage);
        panelSendMsg.add(btnSendMsg);
        frame.add(panelSendMsg, BorderLayout.SOUTH);


        clientProxy.consume(EProtocol.CONNECT, username);
    }

    private void btnSendMsgClicked()
    {
        clientProxy.consume(EProtocol.SENDMSG, username + "  ::  " + textMessage.getText());
        textMessage.setText("");
    }

    private void getUsername()
    {
        frameGetUsername = new JFrame("enter username");
        JLabel labelUsername = new JLabel("enter username: ");
        textUsername = new JTextField(15);
        JButton btnOk = new JButton("Ok");
        GridBagConstraints gridConstrains = new GridBagConstraints();

        frameGetUsername.setLayout(new GridBagLayout());
        frameGetUsername.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frameGetUsername.setResizable(false);
        frameGetUsername.setVisible(true);
        frameGetUsername.setSize(200,200);

        gridConstrains.gridy = 0;
        gridConstrains.gridx = 0;
        frameGetUsername.add(labelUsername, gridConstrains);

        gridConstrains.gridy = 1;
        gridConstrains.gridx = 0;
        frameGetUsername.add(textUsername, gridConstrains);

        btnOk.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if(!textUsername.getText().isEmpty())
                {
                    btnOnClicked(textUsername.getText());
                }
            }
        });
        gridConstrains.gridy = 2;
        gridConstrains.gridx = 0;
        frameGetUsername.add(btnOk, gridConstrains);
    }

    private void btnOnClicked(String i_Username)
    {
        username = i_Username;

        frameGetUsername.setVisible(false);

        initApp();
    }

    public void SetAction(EProtocol eProtocol, String msg)
    {
        if(!msg.isEmpty())
        {
            switch (eProtocol)
            {
                case CONNECT:
                    this.actionConnect(msg);
                    break;

                case ADDUSER:
                    this.actionAddUser(msg);
                    break;

                case SENDMSG:
                    this.actionSendMsg(msg);
                    break;

                case DISCONNECT:
                    this.actionDisconnect(msg);
                    break;
            }
        }
    }

    private void actionDisconnect(String i_Username)
    {
        usersList.removeElement(i_Username);
        listUsers.setModel(usersList);

        String msg = "[ SERVER ]  " + i_Username + " DISCONNECTED";
        this.writeToScreen(msg);
    }

    private void actionAddUser(String i_Username) {
        usersList.addElement(i_Username);
        listUsers.setModel(usersList);
    }

    private void actionConnect(String i_Username) {
        String msg = "[ SERVER ]  " + i_Username + " has joined the chat";
        this.writeToScreen(msg);

        actionAddUser(i_Username);
    }


    private void actionSendMsg(String msg) {
        DateTimeFormatter myFormatObj = DateTimeFormatter.ofPattern("HH:mm:ss");


        msg = "[ " + LocalDateTime.now().format(myFormatObj) + " ] " + msg;
        this.writeToScreen(msg);
    }

    private void writeToScreen(String msg)
    {
        textArea.append(msg + "\n");
    }

    @Override
    public void consume(EProtocol eProtocol, String str)
    {
        clientProxy.consume(eProtocol, str);
    }

    @Override
    public void addConsumer(IStringConsumer sc)
    {
        clientProxy = (ClientProxy) sc;
    }

    @Override
    public void removeConsumer(IStringConsumer sc)
    {
        clientProxy = null;
    }
}
