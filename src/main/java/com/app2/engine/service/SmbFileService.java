package com.app2.engine.service;

public interface SmbFileService {
    public String remoteFileToLocalFile(String fileName,String topic,String date);
    public String localFileToRemoteFile(String fileName,String topic,String date);
}
