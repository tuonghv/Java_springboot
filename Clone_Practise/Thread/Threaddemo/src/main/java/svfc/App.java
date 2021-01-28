
package svfc;

// import  svfc.flowa;
// import  svfc.flow_b;
// import  svfc.flow_b;

public class App{
  public static void main(String[] args) {
    svfc.flowa.myflow thread_a = new svfc.flowa.myflow();
    thread_a.start();


    svfc.flowb.myflow thread_b = new svfc.flowb.myflow();
    Thread thread = new Thread(thread_b);
    thread.start();

    
    System.out.println("This code is outside of the thread");
  }
  
}