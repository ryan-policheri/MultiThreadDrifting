package MorizInitial;
import java.util.ArrayList;

public class ThreadTimSort<T extends Comparable<T>> implements Runnable {
   private Thread t;
   private ArrayList<T> arr;
   private int upper_bound;
   private int lower_bound;
   
   ThreadTimSort(ArrayList<T> arr, int lower_bound, int upper_bound) {
      this.arr = arr;
      this.upper_bound = upper_bound;
      this.lower_bound = lower_bound;
   }
   
   

	private void sort(ArrayList<T> array, int begin, int end) {
		ArrayList<T> arr = new ArrayList<T>(array.subList(begin, end));
		arr.sort(null);
		for(int i = 0; i<end-begin; i++) {
			array.set(begin+i, arr.get(i));
		}
	}
   

	public void run() {
      try {
    	 sort(arr, lower_bound, upper_bound);
      } catch (Exception e) { //InterruptedException
    	 System.out.println(e);
         System.out.println("Thread " +  lower_bound +"/"+ upper_bound + " interrupted.");
      }
   }
   
   public void start () {
      if (t == null) {
         t = new Thread (this, lower_bound +"/"+ upper_bound);
         t.start ();
      }
   }

	public void join() throws InterruptedException {
		t.join();
		
	}
}
