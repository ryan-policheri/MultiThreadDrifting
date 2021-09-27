package mapreduce;

public class ReductionResult {
    public ReducedRecord[] Records;
    public int RecordWeightedLength;

    public int recordCount() { return Records.length; }
}
