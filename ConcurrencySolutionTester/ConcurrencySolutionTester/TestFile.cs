namespace ConcurrencySolutionTester
{
    public class TestFile
    {
        public int FileSize { get; set; }

        public string GenerationType { get; set; }

        public string FilePath { get; set; }

        public string FileDirectory { get; set; }
        public int OwningSet { get; internal set; }
    }
}