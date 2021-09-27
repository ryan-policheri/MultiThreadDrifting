using System;
using System.Diagnostics;
using System.IO;
using System.Linq;

namespace ConcurrencySolutionTester
{
    public class SystemFunctions
    {
        private const string _defaultSystemProcessFile = "cmd.exe";

        public static ProcessStats RunSystemProcess(string commandString, string workingDirectory = null, string senstiveCommandInfoToReplace = null)
        {
            return RunCustomProcess(_defaultSystemProcessFile, commandString, workingDirectory, senstiveCommandInfoToReplace);
        }

        public static ProcessStats RunCustomProcess(string processFile, string parameters, string workingDirectory = null, string senstiveParameterInfoToReplace = null)
        {
            Process process = new Process();
            ProcessStartInfo startInfo = new ProcessStartInfo();
            startInfo.WindowStyle = ProcessWindowStyle.Normal;
            startInfo.FileName = processFile;
            startInfo.Arguments = processFile.StartsWith(_defaultSystemProcessFile) ? "/C " + parameters : parameters;
            if(!String.IsNullOrWhiteSpace(workingDirectory)) startInfo.WorkingDirectory = workingDirectory;
            process.StartInfo = startInfo;
            process.Start();
            bool didExit = process.WaitForExit(150000); //2.5 minutes
            if (process.ExitCode != 0)
            {
                if (!String.IsNullOrWhiteSpace(senstiveParameterInfoToReplace)) parameters = parameters.Replace(senstiveParameterInfoToReplace, "***");
                throw new Exception("An error occurred while executing the following process: " + processFile + " " + parameters + ". The exit code was " + process.ExitCode);
            }

            if (!didExit)
            {
                process.Kill();
                return new ProcessStats();
            }
            else { return new ProcessStats(process.StartTime, process.ExitTime, didExit); }
        }

        public static void CreateFreshDirectory(string directory)
        {
            DeleteDirectory(directory);
            Directory.CreateDirectory(directory);
        }

        public static void DeleteDirectory(string directory)
        {
            if (Directory.Exists(directory))
            {
                Directory.Delete(directory, true);
            }
        }

        public static void CreateDirectory(string directory)
        {
            Directory.CreateDirectory(directory);
        }

        public static string GetCurrentDirectory()
        {
            return Directory.GetCurrentDirectory();
        }

        public static void DeleteFile(string file)
        {
            if (File.Exists(file))
            {
                File.Delete(file);
            }
        }

        public static string RenameFile(string file, string newFileName)
        {
            FileInfo fileInfo = new FileInfo(file);
            string newFile = $"{fileInfo.Directory}\\{newFileName}{fileInfo.Extension}";
            File.Move(file, newFile);
            return newFile;
        }

        public static string GetDateTimeAsFileNameSafeString()
        {
            string fileNameSafeDateTime = DateTime.Now.ToString("MM/dd/yyyy hh:mm tt").Replace("/", ".").Replace(":", "-");
            return fileNameSafeDateTime;
        }

        public static void CreateFile(string file, byte[] bytes)
        {
            File.WriteAllBytes(file, bytes);
        }

        public static void OpenFile(string file)
        {
            Process.Start($"{_defaultSystemProcessFile} ", @"/c " + "\"" + file + "\"");
        }

        public static void CopyDirectory(string sourceDirectory, string targetDirectory)
        {
            Directory.CreateDirectory(targetDirectory);
            string[] files = Directory.GetFiles(sourceDirectory);
            foreach (string file in files)
            {
                FileInfo fileInfo = new FileInfo(file);
                string newFile = $"{targetDirectory}{Path.DirectorySeparatorChar}{fileInfo.Name}";
                File.Copy(file, newFile);
            }

            string[] directories = Directory.GetDirectories(sourceDirectory);

            foreach (string directory in directories)
            {
                string subDirectory = directory.Split(Path.DirectorySeparatorChar).Last();
                string targetSubDirectory = $"{targetDirectory}{Path.DirectorySeparatorChar}{subDirectory}";
                CopyDirectory(directory, targetSubDirectory);
            }
        }

        public static string CombineDirectoryComponents(string directory, string directoryOrFile)
        {
            return directory.TrimEnd(Path.DirectorySeparatorChar) + Path.DirectorySeparatorChar + directoryOrFile.TrimStart(Path.DirectorySeparatorChar);
        }
    }
}
