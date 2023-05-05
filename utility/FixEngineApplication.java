package utility;

import quickfix.Application;
import quickfix.FieldNotFound;
import quickfix.Message;
import quickfix.SessionID;
import quickfix.field.ClOrdID;
import quickfix.field.MsgType;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class FixEngineApplication implements Application {
    HashMap<String, List<Message>> messageMap = new HashMap<String, List<Message>>();
    private int timeout = 1000; //in milliseconds
    private int retries = 5;
    private SessionID sessionId;


    public SessionID getSessionId() {
        return sessionId;
    }

    public List<Message> getOrderMessages(String orderID) {
        return messageMap.getOrDefault(orderID, new ArrayList<>());
    }

    @Override
    public void fromApp(Message message, SessionID sessionID) throws FieldNotFound {
        if (receivedER(message)){
            processMsg(message,sessionID);
        }
    }

    @Override
    public void toApp(Message message, SessionID sessionID) {

    }

    private void processMsg(Message message, SessionID sessionID) throws FieldNotFound {
        try {
            String orderId = message.getString(ClOrdID.FIELD);
            List<Message> messages = messageMap.getOrDefault(orderId, new ArrayList<>());
            messages.add(message);
            messageMap.put(orderId, messages);
        } catch (FieldNotFound e) {
            // Handle field not found exception
        }
    }

    private boolean receivedER(Message message) throws FieldNotFound {
        return message.getHeader().getString(MsgType.FIELD).equals(MsgType.EXECUTION_REPORT);
    }



    @Override
    public void fromAdmin(Message message, SessionID sessionID) {
    }

    @Override
    public void toAdmin(Message message, SessionID sessionID) {
    }

    @Override
    public void onCreate(SessionID sessionID) {
        // Save the session ID value:
        sessionId = sessionID;
        messageMap = new HashMap<>();
    }

    @Override
    public void onLogon(SessionID sessionID) {
    }

    @Override
    public void onLogout(SessionID sessionID) {
    }
}