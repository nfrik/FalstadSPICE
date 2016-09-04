/**
 * Created by nfrik on 8/31/16.
 */


public class IMQPConnectionTest {

    IMQPConnection sdf;

    void testSend(){
        IMQPConnection.getInstance().sendToQueue("Hello");
    }

    void testReceive(){
        IMQPConnection.getInstance().sendToQueue("Hello");
    }

    public static void main(String args[]){

//        IMQPConnection.getInstance().sendToQueue("Hello");

        for(int i=0;i<100;i++){
//            IMQPConnection.getInstance().sendToQueue("Hello");
            IMQPConnection.getInstance().readFromQueue();
        }

        IMQPConnection.getInstance().closeConnection();

    }
}
