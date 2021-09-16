package common;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

public class TrialValidator {
    public void validateTrial(Trial trial) throws IOException {
        if (trial.OutputFile == null || trial.VerificationFile == null) { trial.ValidTrial = false; }
        else {
            File outputFile = new File(trial.OutputFile);
            File validFile = new File(trial.VerificationFile);
            long mismatch = Files.mismatch(outputFile.toPath(), validFile.toPath());
            trial.ValidTrial = mismatch == -1;
        }
    }
}
