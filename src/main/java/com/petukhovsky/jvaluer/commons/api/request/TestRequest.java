package com.petukhovsky.jvaluer.commons.api.request;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Created by Arthur Petukhovsky on 6/11/2016.
 */
public class TestRequest {
    private String type;
    private Submission submission;

    @JsonProperty("package")
    private PackageInfo packageInfo;

    private LaunchInfo launchInfo;

    public TestRequest() {
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Submission getSubmission() {
        return submission;
    }

    public void setSubmission(Submission submission) {
        this.submission = submission;
    }

    public PackageInfo getPackageInfo() {
        return packageInfo;
    }

    public void setPackageInfo(PackageInfo packageInfo) {
        this.packageInfo = packageInfo;
    }

    public LaunchInfo getLaunchInfo() {
        return launchInfo;
    }

    public void setLaunchInfo(LaunchInfo launchInfo) {
        this.launchInfo = launchInfo;
    }
}
