package com.petukhovsky.jvaluer.commons.checker;

import com.petukhovsky.jvaluer.commons.run.RunInfo;
import com.petukhovsky.jvaluer.commons.data.TestData;

import java.nio.file.Path;

/**
 * Created by Arthur on 12/19/2015.
 */
public abstract class Checker {

    public CheckResult check(TestData in, TestData answer, TestData out, Path source, RunInfo runInfo) {
        return check(in, answer, out);
    }

    public abstract CheckResult check(TestData in, TestData answer, TestData out);
}
