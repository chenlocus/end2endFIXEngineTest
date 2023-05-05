package utility;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import quickfix.*;
import quickfix.MessageUtils;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.List;

public class TestContext {
    private SocketInitiator initiator;
    private SessionSettings settings;
    private FixEngineApplication application;
    private MessageStoreFactory messageStoreFactory;
    private LogFactory logFactory;
    private MessageFactory messageFactory;
    private SessionID sessionID = null;
    private static final Logger LOG = LoggerFactory.getLogger(TestContext.class);

    public TestContext(String configFile) throws ConfigError, FileNotFoundException {
        settings = new SessionSettings(new FileInputStream(configFile));
        // Create a new SocketInitiator
        application = new FixEngineApplication();
        messageStoreFactory = new FileStoreFactory(settings);
        logFactory = new ScreenLogFactory(settings);
        messageFactory = new DefaultMessageFactory();
        try {
            initiator = new SocketInitiator(application, messageStoreFactory, settings, logFactory, messageFactory);
            System.out.println("initiator created ok~!");
        } catch (ConfigError error){
            System.out.println("configuration err!");
        }
    }

    public void start() throws ConfigError {
        initiator.start();
        // Wait until session is logged on
        while (sessionID == null || !Session.lookupSession(sessionID).isLoggedOn()) {
            try {
                System.out.println("Trying to connect!!!!!!");
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                System.out.println("session login error");
                throw new RuntimeException(e);
            }
            sessionID = application.getSessionId();
        }
    }

    public void stop() {
        initiator.stop();
    }

    public void send(Message message) throws Exception{
        Session session = Session.lookupSession(sessionID);
        Session.sendToTarget(message, sessionID);
    }

    public String getSenderCompID(){
        return sessionID.getSenderCompID();
    }

    public String getTargetCompID(){
        return sessionID.getTargetCompID();
    }

    public List<Message> getOrderMessages(String orderID){
        return application.getOrderMessages(orderID);
    }

    public Message parse(String rawMessage) throws InvalidMessage {
        Session session = Session.lookupSession(sessionID);
        return MessageUtils.parse(session,convert2FIXRawMessage(rawMessage));
    }

    private static String convert2FIXRawMessage(String rawMessage) {
        return rawMessage.replaceAll("\\|", "\001");
    }

}
