
package svfc.flowa;

public class myflow extends Thread {
  // public static void main(String[] args) {
  //   System.out.println("This code is outside of the thread AAAAAAAAAAAAAAAAAAA 0000000000000000000000000");
  // }
  public void run() {

    for (int i = 1; i < 50; i++) {
      try {
          Thread.sleep(2000);
          System.out.println("This code is running in a thread AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA"+ i);
      } catch (InterruptedException e) {
          System.out.println(e);
      }
      System.out.println(i);
  }    
  }
}