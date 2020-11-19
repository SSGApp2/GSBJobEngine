package com.app2.engine.service;

public interface SmbFileService {
    public String remoteFileToLocalFile(String fileName,String topic);
    public String localFileToRemoteFile(String fileName,String topic);
}
