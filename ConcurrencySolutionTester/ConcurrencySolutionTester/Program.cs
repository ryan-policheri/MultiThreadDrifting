using System;
using System.Collections;
using System.Collections.Generic;
using System.Linq;
using System.Text.Json;

namespace ConcurrencySolutionTester
{
    public class Program
    {
        private static int _testSetCount = 3;

        private static readonly int[] _fileSizesToTest = { 10000, 100000, 1000000, 10000000 };

        private static readonly string[] _genPatternsToTest =
        {
            GenerationPatterns.SPLIT_1000,
            GenerationPatterns.SPLIT_1000_SORTED,
            GenerationPatterns.EVEN_DISTRIBUTION,
            GenerationPatterns.POSITIVE_MILLION,
            GenerationPatterns.ALL_SAME,
            GenerationPatterns.BuildXDifferentNumbersInstance(100)
        };

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

            ICollection<TestFileSet> testFiles = BuildTestFiles(_testSetCount, _inputFilesAlreadyExist);
            ICollection<PerformedTest> testsToPerform = CreateTestsToPerform(testFiles);

            foreach (PerformedTest test in testsToPerform)
            {
                try
                {
                    ICollection<ProcessStats> statsAcrossSets = new List<ProcessStats>();

                    for(int i = 0; i < _testSetCount; i++)
                    {
                        ICollection<ProcessStats> stats = new List<ProcessStats>();
                        TestFile testFile = test.InputFileSet.TestFiles.ElementAt(i);
                        string outputFileName = test.OutputFiles[i];
                        string command = $"java -jar {_jarFile.Quotify()} {testFile.FilePath} {outputFileName.Quotify()} {test.ThreadCount} {test.Solution}";
                        stats.Add(SystemFunctions.RunSystemProcess(command)); stats.Add(SystemFunctions.RunSystemProcess(command)); stats.Add(SystemFunctions.RunSystemProcess(command));
                        ProcessStats bestStat = stats.OrderBy(x => x.MillisecondsEllapsed).First(); //take best of 3
                        statsAcrossSets.Add(bestStat);
                    }

                    int average = (int)statsAcrossSets.Select(x => x.MillisecondsEllapsed).Average();
                    test.BestRunAveragedAcrossSets = average;
                }
                catch (Exception ex)
                {
                    test.Exception = ex;
                }
            }

            TestReporter reporter = new TestReporter(testsToPerform);
            ExcelService service = new ExcelService();
            string outputFile = SystemFunctions.CombineDirectoryComponents(_workingDirectory, DateTime.Now.Ticks + "_" + "Results.xlsx");
            service.ExportTests(reporter, outputFile);
            SystemFunctions.OpenFile(outputFile);
        }

        private static ICollection<PerformedTest> CreateTestsToPerform(ICollection<TestFileSet> testFileSets)
        {
            ICollection<PerformedTest> performedTests = new List<PerformedTest>();

            foreach (TestFileSet testSet in testFileSets)
            {
                //performedTests.Add(new PerformedTest(test, _baselineSolution, 1));

                foreach (int threadCount in _threadCountsToTest)
                {
                    foreach (string solution in _solutionsToTest)
                    {
                        performedTests.Add(new PerformedTest(testSet, solution, threadCount));
                    }
                }
            }

            return performedTests;
        }

        private static ICollection<TestFileSet> BuildTestFiles(int testSetsCount, bool testFilesAlreadyExist)
        {
            ICollection<TestFileSet> testSets = new List<TestFileSet>();

            foreach (int fileSize in _fileSizesToTest)
            {
                foreach (string genPattern in _genPatternsToTest)
                {
                    TestFileSet set = new TestFileSet();

                    for (int i = 0; i < testSetsCount; i++)
                    {
                        string fileName = $"{fileSize}_{genPattern}_Set_{i + 1}.bin";
                        string filePath = SystemFunctions.CombineDirectoryComponents(_workingDirectory, fileName);

                        string command = $"java -jar {_jarFile.Quotify()} --GenerateFile {filePath.Quotify()} {fileSize} ";
                        if (!genPattern.StartsWith(GenerationPatterns.X_DIFFERENT_NUMBERS)) command += $"{genPattern}";
                        else if (genPattern.StartsWith(GenerationPatterns.X_DIFFERENT_NUMBERS)) command += $"{GenerationPatterns.X_DIFFERENT_NUMBERS} {GenerationPatterns.GetX(genPattern)}";

                        if (!testFilesAlreadyExist) SystemFunctions.RunSystemProcess(command);

                        TestFile testFilesSpec = new TestFile
                        {
                            FileSize = fileSize,
                            GenerationType = genPattern,
                            FilePath = filePath,
                            FileDirectory = _workingDirectory,
                            OwningSet = i+1
                        };

                        set.TestFiles.Add(testFilesSpec);
                    }

                    testSets.Add(set);
                }
            }

            return testSets;
        }
    }
}