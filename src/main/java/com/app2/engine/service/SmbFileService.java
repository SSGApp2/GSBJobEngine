package com.app2.engine.service;

public interface SmbFileService {
    public String copyRemoteFileToLocalFile(String remotePath);
    public String copyLocalFileToRemoteFile(String remotePath);
}
