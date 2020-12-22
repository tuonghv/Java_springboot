
 
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
 
class jProcess {
    public static int kafkaInput(int number, int times) {
		// kafka polling until have data then return
		System.out.println("Kafka Input");
        return number * times;
    }
 
    public static int jSQL(int number) {
		// execute database
		System.out.println("SQLNode");
        return number * number;
    }
 
    public static boolean kafkaOuput(int number) {
		// update to kafka
		System.out.println("Kafka Ouput");
        return number % 2 == 0;
    }

    public static int jSQL2(boolean state) {
		// update to kafka
		System.out.println("jSQL2");
        return 1;
    }	
	
}
 
public class CompletableFuture5 {
 
    public static final int NUMBER = 5;
 
    public static void main(String[] args) throws InterruptedException, ExecutionException {
        // Create a CompletableFuture
        CompletableFuture<Integer> kafkaNode = CompletableFuture.supplyAsync(() -> {
            try {
                TimeUnit.SECONDS.sleep(1);
            } catch (InterruptedException e) {
                throw new IllegalStateException(e);
            }
            return jProcess.kafkaInput(NUMBER, 2);
        });
 
        // Attach a callback to the Future using thenApply()
        CompletableFuture<Boolean> broker = kafkaNode.thenApply(n -> {
            try {
                TimeUnit.SECONDS.sleep(1);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return jProcess.jSQL(n);
        })
        // Chaining multiple callbacks
        .thenApply(n -> {
            try {
                TimeUnit.SECONDS.sleep(1);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return jProcess.kafkaOuput(n);
        });
         
        // Block and get the result of the future.
        System.out.println(broker.get()); // true
    }
}