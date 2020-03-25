package com.app2.engine.service.impl;

import com.app2.engine.entity.app.ParameterDetail;
import com.app2.engine.repository.ParameterDetailRepository;
import com.app2.engine.service.SmbFileService;
import com.app2.engine.util.AppUtil;
import jcifs.smb.NtlmPasswordAuthentication;
import jcifs.smb.SmbFile;
import jcifs.smb.SmbFileInputStream;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileOutputStream;

@Service
public class SmbFileServiceImpl implements SmbFileService {
    private Logger LOGGER = LoggerFactory.getLogger(this.getClass());

    @Autowired
    ParameterDetailRepository parameterDetailRepository;

    @Override
    public void copyRemoteFileToLocalFile(String remotePath, String localPath) {
   /*     ParameterDetail pDetail = parameterDetailRepository.findByParameterAndCode("APP_CONFIG", "10");
        String smbPath = pDetail.getVariable1();
        String username = pDetail.getVariable2();
        String password = pDetail.getVariable3();
        LOGGER.debug("smbPath    {}", smbPath);
        LOGGER.debug("username      {}", username);
        LOGGER.debug("password      {}", password);
        LOGGER.debug("remotePath    {}", remotePath);
        LOGGER.debug("localPath    {}", localPath);

        NtlmPasswordAuthentication authentication = new NtlmPasswordAuthentication("", username, password);

        try {
            SmbFile remoteFile = new SmbFile(smbPath + "/" + remotePath, authentication);
            remoteFile.connect(); //Try to connect
            SmbFileInputStream in = new SmbFileInputStream(remoteFile);

            File localFile = new File(localPath);
            if (!localFile.exists()) {
                localFile.getParentFile().mkdirs();
                localFile.createNewFile();
            }
            FileOutputStream out = new FileOutputStream(localFile, false);

            byte[] buffer = new byte[16904];
            int read = 0;
            while ((read = in.read(buffer)) > 0)
                out.write(buffer, 0, read);

            in.close();
            out.close();


        } catch (Exception e) {
            LOGGER.error("Error {}", e.getMessage(), e);
            throw new RuntimeException(e);
        }*/
    }

    @Override
    public String copyRemoteFileToLocalFile(String fileName) {
        ParameterDetail pDetail = parameterDetailRepository.findByParameterAndCode("APP_CONFIG", "10");
        String smbPath = pDetail.getVariable1();
        String username = pDetail.getVariable2();
        String password = pDetail.getVariable3();
        String backup = pDetail.getVariable4();
        LOGGER.debug("smbPath    {}", smbPath);
        LOGGER.debug("username      {}", username);
        LOGGER.debug("password      {}", password);
        LOGGER.debug("remotePath    {}", fileName);
        LOGGER.debug("localPath    {}", backup);
        String pathFileLocal=null;
        try {
            if (AppUtil.isNull(pDetail.getVariable9())) {
                //From SMTP
                NtlmPasswordAuthentication authentication = new NtlmPasswordAuthentication("", username, password);


                SmbFile remoteFile = new SmbFile(smbPath + "/" + fileName, authentication);
                remoteFile.connect(); //Try to connect
                SmbFileInputStream in = new SmbFileInputStream(remoteFile);

                File localFile = new File(backup + "/" + fileName);
                if (!localFile.exists()) {
                    localFile.getParentFile().mkdirs();
                    localFile.createNewFile();
                }
                FileOutputStream out = new FileOutputStream(localFile, false);


                byte[] buffer = new byte[16904];
                int read = 0;
                while ((read = in.read(buffer)) > 0)
                    out.write(buffer, 0, read);

                in.close();
                out.close();
                pathFileLocal=localFile.getPath();
            } else {
                File source = new File(pDetail.getVariable9() + "/" + fileName);
                File dest = new File(backup + "/" + fileName);
                FileUtils.copyFile(source, dest);
                pathFileLocal=dest.getPath();
            }
        } catch (Exception e) {
            LOGGER.error("Error {}", e.getMessage(), e);
            throw new RuntimeException(e);
        }
        LOGGER.debug("pathFileLocal {}",pathFileLocal);
        return pathFileLocal;
    }


    private void deleteFileERP(String fileName) {

        ParameterDetail pDetail = parameterDetailRepository.findByParameterAndCode("APP_CONFIG", "10");
        String smbPath = pDetail.getVariable1();
        String username = pDetail.getVariable2();
        String password = pDetail.getVariable3();
        LOGGER.debug("smbPath    {}", smbPath);
        LOGGER.debug("username      {}", username);
        LOGGER.debug("password      {}", password);
        NtlmPasswordAuthentication authentication = new NtlmPasswordAuthentication("", username, password);
        try {
            SmbFile remoteFile = new SmbFile(smbPath + "/" + fileName, authentication);
            if (remoteFile.isFile() && remoteFile.exists()) {
                remoteFile.delete();
                LOGGER.debug("Delete success");
            } else {
                LOGGER.debug("Not found File");
            }
        } catch (Exception e) {
            LOGGER.error("Error {}", e.getMessage(), e);
            throw new RuntimeException(e);
        }

    }
}

