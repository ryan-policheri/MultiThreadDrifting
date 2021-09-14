package MorizInitial;
import java.util.ArrayList;
import java.util.Random;

public class TestThreadSort {

   public static void main(String args[]) {
	  Random r = new Random();
	  ArrayList<Integer> arr = new ArrayList<Integer>();
	  ArrayList<Integer> arr_control = new ArrayList<Integer>();
	  for (int i = 0; i< 1000000; i++) {
		  int rand = r.nextInt(100);
		  arr.add(rand);
		  arr_control.add(rand);
	  }
	  
	  
	long old = System.nanoTime();
	//proliferateQuickSort(arr);
	//proliferateMergeSort(arr);
	timSort(arr, 10);
	
  	System.out.println("Post Merge: "+(System.nanoTime() - old) * 0.000001);
  		
  	old = System.nanoTime();
	//arr_control.sort(null);
  	timSort(arr_control, 1);
  	
  	System.out.println("Post Merge: "+(System.nanoTime() - old) * 0.000001);
   }   
   
   // Calls quicksort recursively creating n threads, complete failure
   private static void proliferateQuickSort(ArrayList<Integer> arr) {
	   ThreadQuickSort<Integer> R1 = new ThreadQuickSort<Integer>(arr, 0, arr.size()-1);
	      R1.start();
	      try {
	          R1.join();
	       } catch ( Exception e) {

		          System.out.println(e);
	          System.out.println("Interrupted");
	       }
   }

   // Calls mergesort recursively creating n threads, complete failure
   private static void proliferateMergeSort(ArrayList<Integer> arr) {
	   ThreadMergeSort<Integer> R1 = new ThreadMergeSort<Integer>(arr, 0, arr.size()-1);
	      R1.start();
	      try {
	          R1.join();
	       } catch ( Exception e) {

		          System.out.println(e);
	          System.out.println("Interrupted");
	       }
}

   /* 
    * splits the array into parts for each thread and 
    * has each thread sort a section with quicksort then
    * merges each section into the sorted array using a multiarray merge
    */
   private static void nonProlifQuickSort(ArrayList<Integer> arr, int thread_count) {
	   ArrayList<ThreadQuickSortNoProlif<Integer>> store = new ArrayList<ThreadQuickSortNoProlif<Integer>>(thread_count);
	   ThreadQuickSortNoProlif<Integer> temp_thread;
	   int step = arr.size()/thread_count;
	   int current_lower = 0;
	   ArrayList<Integer> bounds = new ArrayList<Integer>(thread_count);

		long old = System.nanoTime();
	   for (int i = 0; i< thread_count; i++) {
		   if (arr.size()-current_lower < 2*step){
			   step = arr.size() -current_lower;
		   }
		   temp_thread = new ThreadQuickSortNoProlif<Integer>(arr, current_lower, current_lower+step-1);
		   store.add(temp_thread);
		   temp_thread.start();
		   current_lower += step;
		   bounds.add(current_lower);
		   
	   }
	      try {
		   	   for (int i = 0; i< thread_count; i++) {
				  temp_thread = store.get(i);
		    	  temp_thread.join();
			   }	
	       } catch ( Exception e) {
		      e.printStackTrace();
	          System.out.println("Interrupted");
	       }
		 System.out.println("Post Sort: "+(System.nanoTime() - old) * 0.000001);
		 multiMerge(arr, thread_count, bounds);

          
		  
   }
   
   
   /* 
    * splits the array into parts for each thread and 
    * has each thread sort a section with Java's default sort then
    * merges each section into the sorted array using a multi array merge
    */
   private static void timSort(ArrayList<Integer> arr, int thread_count) {
	   ArrayList<ThreadTimSort<Integer>> store = new ArrayList<ThreadTimSort<Integer>>(thread_count);
	   ThreadTimSort<Integer> temp_thread;
	   
	   // rough spread, last element gets dumped the remainder, needs improvement
	   int step = arr.size()/thread_count;
	   int current_lower = 0;
	   ArrayList<Integer> bounds = new ArrayList<Integer>(thread_count);

		long old = System.nanoTime();
	   for (int i = 0; i< thread_count; i++) {
		   if (arr.size()-current_lower < 2*step){
			   step = arr.size() -current_lower;
		   }
		   temp_thread = new ThreadTimSort<Integer>(arr, current_lower, current_lower+step-1);
		   store.add(temp_thread);
		   temp_thread.start();
		   current_lower += step;
		   bounds.add(current_lower);
		   
	   }
	      try {
		   	   for (int i = 0; i< thread_count; i++) {
				  temp_thread = store.get(i);
		    	  temp_thread.join();
			   }	
	       } catch ( Exception e) {
		      e.printStackTrace();
	          System.out.println("Interrupted");
	       }

		  	System.out.println("Post Sort: "+(System.nanoTime() - old) * 0.000001);
		  	multiMerge(arr, thread_count, bounds);
   }
   
   /* 
    * Applies the merging step of merge sort to each section
    * I think this could be an opportunity for Producer/Consumer?
    */
   private static void multiMerge(ArrayList<Integer> arr, int thread_count, ArrayList<Integer> bounds) {
	      ArrayList<Integer> arr_sorted = new ArrayList<Integer>(arr.size());
	      ArrayList<Integer> arr_consider = new ArrayList<Integer>(thread_count);
	      int next = 0;
		   
		   ArrayList<Integer> indices = new ArrayList<Integer>(thread_count);
		      for (int i = 0; i< thread_count; i++) {
		    	  arr_consider.add(arr.get(next));
		    	  indices.add(next);
		    	  next = bounds.get(i);
			   }
		      while(arr_sorted.size()< arr.size()) {

		    	  Integer min = arr_consider.get(0);
		    	  int min_index = 0;
		    	  for (int i = 0; i< arr_consider.size(); i++) {
		    		  Integer temp = arr_consider.get(i);
		    		  if (temp.compareTo(min)<0) {
		    			  min = temp;
		    			  min_index = i;
		    		  }
				   }
		    	  arr_sorted.add(min);
		    	  indices.set(min_index, indices.get(min_index)+1);
		    	  if (indices.get(min_index) >= bounds.get(min_index)) {
		    		  arr_consider.remove(min_index);
		    		  indices.remove(min_index);
		    		  bounds.remove(min_index);
		    	  }
		    	  else {
		    		  arr_consider.set(min_index, arr.get(indices.get(min_index)));
		    	  }
		      }
		      for (int i = 0; i < arr.size(); i++) {
		    	  arr.set(i, arr_sorted.get(i));
		      }
	   }
   
}