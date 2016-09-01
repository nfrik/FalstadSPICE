/**
 * Created by nfrik on 8/31/16.
 */

import com.rabbitmq.client.*;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

public class IMQPConnection {

    private static final String QUEUE_NAME = "Hyper";

    private static IMQPConnection instance = null;

    private static Channel channel;

    private static Connection connection;

    void connectToIMQP() throws java.io.IOException {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("localhost");
        try {
            connection = factory.newConnection();
            channel = connection.createChannel();
        } catch (TimeoutException e) {
            e.printStackTrace();
        }
    }

    protected IMQPConnection() {

    }

    public static void sendToQueue(String data) {

//        String message = "Hello World!";
        try {
            channel.basicPublish("", QUEUE_NAME, null, data.getBytes());
            System.out.println(" [x] Sent '" + data + "'");
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public static void readFromQueue(){

        Consumer consumer = new DefaultConsumer(channel) {
            @Override
            public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body)
                    throws IOException {
                String message = new String(body, "UTF-8");
                System.out.println(" [x] Received '" + message + "'");
            }
        };
        try {
            channel.basicConsume(QUEUE_NAME, true, consumer);
        } catch (IOException e) {
            e.printStackTrace();
        }


    }

    public static IMQPConnection getInstance() {
        if (instance == null) {
            instance = new IMQPConnection();

            try {
                instance.connectToIMQP();
                channel.queueDeclare(QUEUE_NAME, false, false, false, null);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return instance;
    }

    public static void closeConnection() {
        if (channel != null) {
            try {
                channel.close();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (TimeoutException e) {
                e.printStackTrace();
            }
        }
        if (connection != null) {
            try {
                connection.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
