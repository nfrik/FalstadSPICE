/**
 * Created by nfrik on 8/31/16.
 */

import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.Channel;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

public class IMQPConnection {

    private static IMQPConnection instance = null;

    private static Channel channel;

    private static Connection connection;

    void connectToIMQP() throws java.io.IOException {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("localhost");
        try {
            connection = factory.newConnection();
            channel = connection.createChannel();
        }catch (TimeoutException e){
            e.printStackTrace();
        }
    }

    protected IMQPConnection(){

    }

    public static IMQPConnection getInstance(){
        if(instance == null){
            instance = new IMQPConnection();
            try {
                instance.connectToIMQP();

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return instance;
    }

}
