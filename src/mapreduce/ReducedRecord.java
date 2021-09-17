package mapreduce;

public class ReducedRecord implements Comparable<ReducedRecord> {
    public ReducedRecord(long number){
        Number = number;
        Count = 1;
    }

    public long Number;
    public int Count;

    @Override
    public int compareTo(ReducedRecord obj) {
        if(this.Number == obj.Number) { return 0;}
        return this.Number < obj.Number ? -1 : 1;
    }
}
