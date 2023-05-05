package utility;

import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import quickfix.Field;
import java.util.Optional;
import java.util.Random;
import utility.TestContext;
import quickfix.*;
import quickfix.field.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import static utility.FixMessagesTemplate.*;
import quickfix.field.NoStrategyParameters;


public class Order {
    private String orderID=null;
    private TestContext testContext;
    private static final Logger LOG = LoggerFactory.getLogger(Order.class);
    public static Order createOrder(TestContext testContext,final Map<String, Object> orderDetails ) throws Exception {
        Order order = new Order();
        order.testContext= testContext;
        String rawMessage = null;
        if (!orderDetails.containsKey("StrategyType")){
            System.out.println("please input StrategyType");
            return null;
        }
        try {
            // Get the value of the string variable with the given name using reflection
            rawMessage = (String) FixMessagesTemplate.class.getField((String) orderDetails.get("StrategyType")).get(null);
            System.out.println("rawMessage is:  " +rawMessage);
        } catch (Exception e) {
            throw new IllegalArgumentException("Invalid template name: " + orderDetails.get("StrategyType"), e);
        }
        Message orderTemplate=updateMessage(order,rawMessage,orderDetails);
        testContext.send(orderTemplate);
        return order;
    }


    public Message waitUntil(Map<String, Object> executeReport,int timeoutSeconds) throws Exception {
        Instant start = Instant.now();
        Message response = null;
        while ((response == null)&&(Duration.between(start, Instant.now()).getSeconds() < timeoutSeconds)) {
            List<Message> messages = testContext.getOrderMessages(orderID);
            for (Message message:messages) {
                if (message.getHeader().getString(MsgType.FIELD).equals(MsgType.EXECUTION_REPORT) &&
                        message.getString(ClOrdID.FIELD).equals(orderID))  {
                    System.out.println("message: " + String.valueOf(message));
                    if (isMessageMatchingCriteria(executeReport,message)) {
                        response = message;
                        break;
                    }
                }
            }
            Thread.sleep(1000);
        }
        return response;
    }

    public void amend(final Map<String, Object> orderDetails) throws Exception {
//        OrderCancelReplaceRequest amend = createOrderAmendRequest(this.orderId, price, quantity);
//        Session.sendToTarget(amend, session.getSessionID());
    }

    public void cancel() throws Exception {
//        OrderCancelRequest cancel = createOrderCancelRequest(this.orderID);
//        Session.sendToTarget(cancel, session.getSessionID());
    }

    private static String generateOrderId() {
        //generate a ClOrdID in format: 50429797-1681179983325
        final int ID_PART_1_BITS = 8;
        final int ID_PART_2_BITS = 13;
        Random random = new Random();
        int orderId1 = random.nextInt(100000000); // Generate an 8-digit number
        long orderId2 = System.currentTimeMillis(); // Generate a 13-digit number
        return String.format("%08d-%013d", orderId1, orderId2);
    }

    private static Message updateMessage(Order order,String rawMessage, final Map<String, Object> orderDetails) throws InvalidMessage, FieldNotFound {
        assert rawMessage != null;
        Message orderTemplate = order.testContext.parse(rawMessage);
        //update the raw fix message
        String senderCompID = order.testContext.getSenderCompID();
        String targetCompID = order.testContext.getTargetCompID();
        //update sendCompID and targetCompID
        orderTemplate.getHeader().setField(new SenderCompID(senderCompID));
        orderTemplate.getHeader().setField(new TargetCompID(targetCompID));
        //update ClOrdID
        String orderId = generateOrderId();
        order.orderID = orderId;
        orderTemplate.setField(new ClOrdID(orderId));
        //update tag 52 and tag 60
        orderTemplate.getHeader().setField(new SendingTime());
        orderTemplate.setField(new TransactTime());
        //update tag 126 if it exists:

        orderTemplate = updateTag126(orderTemplate);
        orderTemplate = updateRepeatingGroup(orderTemplate);
        for (Map.Entry<String, Object> entry : orderDetails.entrySet()) {
            String key = entry.getKey();
            Object value = entry.getValue();
            switch (key) {
                case "OrderQty":
                    int orderQty = (int) value;
                    orderTemplate.setField(new OrderQty(orderQty));
                    break;
                case "Price":
                    double price = (double) value;
                    orderTemplate.setField(new Price(price));
                    break;
                default:
                    break;
            }
        }
        return orderTemplate;
    }

    private int getTagFromFieldName (String strFieldName){
            int tag = 0;
            try {
                Class<?> clazz = Class.forName("quickfix.field." + strFieldName);
                Field field = (Field) clazz.newInstance();
                tag = field.getTag();
            } catch (ClassNotFoundException | InstantiationException | IllegalAccessException e) {
                // Handle exception
                LOG.info("ClassNotFoundException!");
            }
            return tag;
    }

    private static Message updateTag126(Message orderTemplate) throws FieldNotFound {
        if (orderTemplate.isSetField(126)) {
            String currentTime = orderTemplate.getString(126).substring(9); // extract the time part
            String newDate = LocalDate.now().format(DateTimeFormatter.BASIC_ISO_DATE); // get current date
            String newValue = newDate + '-' + currentTime; // concatenate date and time
            orderTemplate.setField(new StringField(126, newValue)); // update tag 126
        }
        return orderTemplate;
    }

    private static Message updateRepeatingGroup(Message orderTemplate) throws FieldNotFound {
        if (orderTemplate.isSetField(NoStrategyParameters.FIELD)){
            final Optional<Group> endTime = orderTemplate.getGroups(NoStrategyParameters.FIELD).stream()
                    .filter(group-> {
                        try {
                            return group.getString(StrategyParameterName.FIELD).equals("EndTime");
                        } catch (FieldNotFound e) {
                            throw new RuntimeException(e);
                        }}).findFirst();
            endTime.ifPresent(e -> {
                try {
                    String strEndtime = e.getString(StrategyParameterValue.FIELD).substring(9);
                    String newDate = LocalDate.now().format(DateTimeFormatter.BASIC_ISO_DATE); // get current date
                    String newValue = newDate + '-' + strEndtime; // concatenate date and time
                    e.setString(StrategyParameterValue.FIELD, newValue);
                } catch (FieldNotFound ex) {
                    throw new RuntimeException(ex);
                }
            });
        }
        return orderTemplate;
    }

    private boolean isMessageMatchingCriteria (Map < String, Object > criteria, Message message) throws
        FieldNotFound, NoSuchFieldException {
        for (Map.Entry<String, Object> entry : criteria.entrySet()) {
            int tagField;
            tagField = getTagFromFieldName(entry.getKey());
            String value = entry.getValue().toString();
            System.out.println("criteria value" + value);
            System.out.println("criteria tag" + tagField);
            if (!message.isSetField(tagField)) {
                return false; // tag not present in message
            }
            String fieldValue = message.getString(tagField);
            System.out.println("message value" + fieldValue);
            if (!fieldValue.equals(value)) {
                return false; // tag value doesn't match
            }
        }
        return true;
    }
}
