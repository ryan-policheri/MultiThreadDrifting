namespace ConcurrencySolutionTester
{
    public class GenerationPatterns
    {
        public const string SPLIT_1000 = "SPLIT_1000";
        public const string SPLIT_1000_SORTED = "SPLIT_1000_SORTED";
        public const string EVEN_DISTRIBUTION = "EVEN_DISTRIBUTION";
        public const string POSITIVE_MILLION = "POSITIVE_MILLION";
        public const string ALL_SAME = "ALL_SAME";
        public const string X_DIFFERENT_NUMBERS = "X_DIFFERENT_NUMBERS";

        public static string BuildXDifferentNumbersInstance(int x)
        {
            return $"{X_DIFFERENT_NUMBERS}#{x}";
        }

        public static int GetX(string xDifferentNumbersInstance)
        {
            string[] parsed = xDifferentNumbersInstance.Split("#");
            return int.Parse(parsed[1]);
        }
    }
}
