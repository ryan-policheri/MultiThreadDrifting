using System.IO;
using OfficeOpenXml;

namespace ConcurrencySolutionTester
{
    public class ExcelService : ExcelBuilderBase
    {
        public ExcelService()
        {
            ExcelPackage.LicenseContext = LicenseContext.NonCommercial;
        }

        public void ExportTests(TestReporter reporter, string outputFile)
        {
            ExcelPackage package = new ExcelPackage();

            foreach (string solution in reporter.Solutions)
            {
                ExcelWorksheet sheet = package.Workbook.Worksheets.Add(solution);
                int row = 1;
                foreach (var tableWithMeta in reporter.GetResultsBySolution(solution))
                {
                    this.BuildWorksheetHeader(sheet, tableWithMeta.Header, row++, tableWithMeta.ColumnCount);
                    row = BuildDataSection(sheet, tableWithMeta.Table, row);
                    row++;row++;
                }

                AutoFitColumns(sheet);
            }

            FileInfo file = new FileInfo(outputFile);
            package.SaveAs(file);
        }
    }
}
