
package svfc.flowb;

public class myflow implements Runnable {
  public static void main(String[] args) {
    System.out.println("This code is outside of the thread BBBBBBBBBBBBBBBBBBBBBBBBB 000000000000000000000000000");
  }
  public void run() {
      for (int i = 1; i < 50; i++) {
        try {
            Thread.sleep(2000);
            System.out.println("This code is running in a thread BBBBBBBBBBBBBBBBBBBBBBBBBB"+ i);
        } catch (InterruptedException e) {
            System.out.println(e);
        }
        System.out.println(i);
    }
   
  }
}