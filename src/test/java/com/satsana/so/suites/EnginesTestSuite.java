package com.satsana.so.suites;

import org.junit.platform.suite.api.SelectPackages;
import org.junit.platform.suite.api.Suite;
import org.junit.platform.suite.api.SuiteDisplayName;

@SelectPackages(value = { "com.satsana.so.engine" })
@Suite
@SuiteDisplayName("Language generation targets Test Suite")
public class EnginesTestSuite {

}
