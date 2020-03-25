package com.app2.engine.service;

public interface SmbFileService {
    public void copyRemoteFileToLocalFile(String remotePath, String localPath);
    public String copyRemoteFileToLocalFile(String remotePath);
}
