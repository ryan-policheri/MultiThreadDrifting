import java.util.ArrayList;

public class ThreadQuickSort<T extends Comparable<T>> implements Runnable {
   private Thread t;
   private ArrayList<T> arr;
   private int upper_bound;
   private int lower_bound;
   
   ThreadQuickSort(ArrayList<T> arr, int lower_bound, int upper_bound) {
      this.arr = arr;
      this.upper_bound = upper_bound;
      this.lower_bound = lower_bound;
      
   }
   
   private int partition(ArrayList<T> array, int begin, int end) {
	    int pivot = end;

	    int counter = begin;
	    for (int i = begin; i < end; i++) {
	        if (array.get(i).compareTo(array.get(pivot))<0) {
	            T temp = array.get(counter);
	            array.set(counter, array.get(i));
	            array.set(i, temp);
	            counter++;
	        }
	    }
	    T temp = array.get(pivot);
        array.set(pivot, array.get(counter));
        array.set(counter, temp);

	    return counter;
	}

	private void quickSort(ArrayList<T> array, int begin, int end) {
	    if (end <= begin) return;
	    int pivot = partition(array, begin, end);
	    ThreadQuickSort<T> new_thread_1 = new ThreadQuickSort<T>(array, begin, pivot-1);
	    new_thread_1.start();
	    quickSort(array, pivot+1, end);
	    try {
	    	new_thread_1.join();
	       } catch ( Exception e) {
	          System.out.println("Interrupted");
       }
	}
   

	public void run() {
      try {
    	 quickSort(arr, lower_bound, upper_bound);
      } catch (Exception e) { //InterruptedException
    	  System.out.println(e);
         System.out.println("Thread " +  lower_bound +"/"+ upper_bound + " interrupted.");
      }
      //System.out.println("Thread " +  lower_bound +"/"+ upper_bound + " exiting.");
   }
   
   public void start () {
      //System.out.println("Starting " +  lower_bound +"/"+ upper_bound );
      if (t == null) {
         t = new Thread (this, lower_bound +"/"+ upper_bound);
         t.start ();
      }
   }

	public void join() throws InterruptedException {
		t.join();
		
	}
}
