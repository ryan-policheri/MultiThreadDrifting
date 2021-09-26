using System;
using System.Collections;
using System.Collections.Generic;
using System.Text.Json;

namespace ConcurrencySolutionTester
{
    public class Program
    {
        private static readonly int[] _fileSizesToTest = { 10000, 100000, 1000000, 10000000 };
        private static readonly string[] _genPatternsToTest = { "SPLIT_1000", "SPLIT_1000_SORTED", "EVEN_DISTRIBUTION", "POSITIVE_MILLION", "ALL_SAME" };

        private static readonly int[] _threadCountsToTest = { 1, 2, 4, 8, 16, 32, 64, 128 };
        private static readonly string _baselineSolution = "BASELINE";
        private static readonly string[] _solutionsToTest = { "MAP_REDUCE", "QUICK_SORT" };

        private static string _jarFile;
        private static bool _inputFilesAlreadyExist = false;
        private static string _workingDirectory = SystemFunctions.CombineDirectoryComponents(AppContext.BaseDirectory, "TestFiles");

        public static void Main(string[] args)
        {
            _jarFile = args[0];
            if (args.Length > 1 && args[1]?.CapsAndTrim() == "TRUE") _inputFilesAlreadyExist = true;
            else SystemFunctions.CreateFreshDirectory(_workingDirectory);

            ICollection<TestFile> testFiles = BuildTestFiles();
            ICollection<PerformedTest> testsToPerform = CreateTestsToPerform(testFiles);

            foreach (PerformedTest test in testsToPerform)
            {
                try
                {
                    string command = $"java -jar {_jarFile.Quotify()} {test.InputFile.FilePath} {test.OutputFile.Quotify()} {test.ThreadCount} {test.Solution}";
                    ProcessStats stats = SystemFunctions.RunSystemProcess(command);
                    test.Stats = stats;
                }
                catch (Exception ex)
                {
                    test.Exception = ex;
                }
            }

            string json = JsonSerializer.Serialize(testsToPerform);
            Console.Write("FOO");
        }

        private static ICollection<PerformedTest> CreateTestsToPerform(ICollection<TestFile> testFiles)
        {
            ICollection<PerformedTest> performedTests = new List<PerformedTest>();

            foreach (TestFile test in testFiles)
            {
                performedTests.Add(new PerformedTest(test, _baselineSolution, 1));

                foreach (int threadCount in _threadCountsToTest)
                {
                    foreach (string solution in _solutionsToTest)
                    {
                        performedTests.Add(new PerformedTest(test, solution, threadCount));
                    }
                }
            }

            return performedTests;
        }

        private static ICollection<TestFile> BuildTestFiles()
        {
            ICollection<TestFile> tests = new List<TestFile>();

            foreach (int fileSize in _fileSizesToTest)
            {
                foreach (string genPattern in _genPatternsToTest)
                {
                    string fileName = $"{fileSize}_{genPattern}.bin";
                    string filePath = SystemFunctions.CombineDirectoryComponents(_workingDirectory, fileName);
                    string command = $"java -jar {_jarFile.Quotify()} --GenerateFile {filePath.Quotify()} {fileSize} {genPattern}";
                    if (!_inputFilesAlreadyExist) SystemFunctions.RunSystemProcess(command);

                    TestFile testFilesSpec = new TestFile
                    {
                        FileSize = fileSize,
                        GenerationType = genPattern,
                        FilePath = filePath,
                        FileDirectory = _workingDirectory
                    };

                    tests.Add(testFilesSpec);
                }
            }

            return tests;
        }
    }
}