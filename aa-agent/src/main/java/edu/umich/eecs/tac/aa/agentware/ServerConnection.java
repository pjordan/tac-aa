package edu.umich.eecs.tac.aa.agentware;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;

import se.sics.isl.transport.BinaryTransportReader;
import se.sics.isl.transport.BinaryTransportWriter;
import se.sics.isl.transport.Transportable;
import se.sics.tasim.aw.Agent;
import se.sics.tasim.aw.Message;
import se.sics.tasim.props.AdminContent;
import se.sics.tasim.props.Alert;
import se.sics.tasim.props.Ping;

/**
 */
public class ServerConnection implements Runnable {

    private static final Logger log =
            Logger.getLogger(ServerConnection.class.getName());

    private static final byte[] TACT_HEADER = {
            (byte) 'T', (byte) 'A', (byte) 'C', (byte) 'T',
            0, // major version
            0, // minor version
            0, 0 // reserved
    };

    private static int connectionCounter = 0;

    private final SimClient simClient;
    private int id = -1;
    private long delayInMillis = 0;

    private MessageSender messageSender;

    private DataInputStream input;
    private DataOutputStream output;
    private Socket socket;
    private BinaryTransportWriter transportWriter = new BinaryTransportWriter();
    private BinaryTransportReader transportReader = new BinaryTransportReader();

    private boolean isAuthenticated = false;

    public ServerConnection(SimClient simClient, long delayInMillis) {
        this.delayInMillis = delayInMillis;
        this.simClient = simClient;
        transportReader.setContext(simClient.getContext());
    }

    public int getID() {
        return id;
    }

    public boolean isAuthenticated() {
        return isAuthenticated;
    }

    public void setAuthenticated(boolean isAuthenticated) {
        this.isAuthenticated = isAuthenticated;
        if (isAuthenticated) {
            log.finer("(" + id + ") successfully logged in as "
                    + simClient.getUserName());
        }
    }

    public void setTransportSupported(String name) {
        transportWriter.setSupported(name, true);
    }

    public boolean sendMessage(Message msg) {
        if (messageSender == null) {
            return false;
        }
        return messageSender.addMessage(msg);
    }

    public void open() {
        if (this.id > 0) {
            throw new IllegalStateException("already opened");
        }
        // Start the connection thread
        this.id = ++connectionCounter;
        new Thread(this, "Connection." + this.id).start();
    }

    public void close() {
        AdminContent content = new AdminContent(AdminContent.QUIT);
        Message msg = new Message(simClient.getUserName(), Agent.ADMIN, content);
        if (!sendMessage(msg)) {
            disconnect();
            simClient.connectionClosed(this);
        }
    }

    // -------------------------------------------------------------------
    //  Connection handling
    // -------------------------------------------------------------------

    private boolean connect() {
        try {
            String host = simClient.getServerHost();
            int port = simClient.getServerPort();

            log.fine("(" + id + ") connecting to server " + host
                    + " at port " + port);
            socket = new Socket(host, port);
            input = new DataInputStream(socket.getInputStream());
            output = new DataOutputStream(socket.getOutputStream());
            // Send the TAC protocol header
            output.write(TACT_HEADER);
            log.fine("(" + id + ") connected to server " + host);
            this.messageSender = new MessageSender(this, "Sender." + id);
            return true;

        } catch (Exception e) {
            log.log(Level.SEVERE, "(" + id + ") connection to server failed", e);
            disconnect();
            return false;
        }
    }

    private boolean disconnect() {
        if (socket != null) {
            try {
                log.fine("(" + id + ") disconnected from server "
                        + simClient.getServerHost());

                if (output != null) {
                    output.close();
                }
                if (input != null) {
                    input.close();
                }
                socket.close();
            } catch (Exception e) {
                log.log(Level.SEVERE, "(" + id + ") could not close connection", e);
            } finally {
                socket = null;
                output = null;
                input = null;
                isAuthenticated = false;
                if (messageSender != null) {
                    messageSender.close();
                    messageSender = null;
                }
            }
            return true;
        }
        return false;
    }

    // -------------------------------------------------------------------
    //  Client thread - handles communication with the TAC server
    // -------------------------------------------------------------------

    public void run() {
        if (delayInMillis > 0) {
            try {
                Thread.sleep(delayInMillis);
            } catch (Exception e) {
            }
        }

        do {
            if (!connect()) {
                simClient.showWarning("Connection Failed",
                        "Could not connect to "
                                + simClient.getServerHost()
                                + " (will retry in 30 seconds)");
                try {
                    Thread.sleep(30000);
                } catch (Exception e) {
                }
            }
        } while (messageSender == null);

        simClient.connectionOpened(this);

        try {
            byte[] buffer = new byte[8192];
            int len;
            int lastPos;

            while (socket != null) {
                int size = input.readInt();
                if (size > buffer.length) {
                    buffer = new byte[size + 8192];
                }
                input.readFully(buffer, 0, size);

                Message msg = parseMessage(buffer, 0, size);
                if (msg != null) {
                    Transportable content = msg.getContent();
                    if (content instanceof AdminContent) {
                        AdminContent admin = (AdminContent) content;
                        if (admin.getType() == AdminContent.QUIT) {
                            if (log.isLoggable(Level.FINEST)) {
                                log.finest("(" + id + ") received " + msg);
                            }
                            disconnect();
                            simClient.connectionClosed(this);
                        } else {
                            simClient.adminFromServer(this, admin);
                        }

                    } else if (content instanceof Alert) {
                        Alert alert = (Alert) content;
                        simClient.alertFromServer(this, alert);

                    } else if (content instanceof Ping) {
                        // Ping from server: respond immediately with pong
                        sendMessage(msg.createReply(new Ping(Ping.PONG)));

                    } else {
                        simClient.messageFromServer(this, msg);
                    }
                }
            }
        } catch (Throwable e) {
            log.log(Level.SEVERE, "(" + id + ") could not read", e);
        } finally {
            if (disconnect()) {
                simClient.connectionClosed(this);
            }
        }
    }

    private Message parseMessage(byte[] buffer, int offset, int size) {
        try {
            Message msg = new Message();
            transportReader.setMessage(buffer, offset, size);
            if (transportReader.nextNode(msg.getTransportName(), false)) {
                transportReader.enterNode();
                msg.read(transportReader);
                return msg;
            } else {
                log.warning("(" + id + ") no message found in received data");
                return null;
            }
        } catch (Exception e) {
            log.log(Level.WARNING, "(" + id + ") could not parse message", e);
            return null;
        }
    }

    // -------------------------------------------------------------------
    // API towards message sender
    // -------------------------------------------------------------------

    boolean deliverMessage(Message msg) {
        DataOutputStream output = this.output;
        if (output == null) {
            log.warning("(" + id + ") could not send message (closed connection) "
                    + msg);
            return false;
        }

        try {
            if (log.isLoggable(Level.FINEST)) {
                log.finest("(" + id + ") sending " + msg);
            }

            String node = msg.getTransportName();
            transportWriter.clear();
            transportWriter.node(node);
            msg.write(transportWriter);
            transportWriter.endNode(node);
            transportWriter.finish();

            try {
                output.writeInt(transportWriter.size());
                transportWriter.write(output);
                output.flush();
                return true;
            } catch (Exception e) {
                log.log(Level.SEVERE, "(" + id
                        + ") could not send message to server", e);
                simClient.showWarning("Connection Failed",
                        "could not send message to server");
                // Need to do a complete reconnect
                if (disconnect()) {
                    simClient.connectionClosed(this);
                }
            }
        } catch (Exception e) {
            log.log(Level.SEVERE, "(" + id + ") could not generate message "
                    + msg, e);
        }
        return false;
    }

} // ServerConnection
