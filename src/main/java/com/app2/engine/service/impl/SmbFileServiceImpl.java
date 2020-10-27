package com.app2.engine.service.impl;

import com.app2.engine.entity.app.Parameter;
import com.app2.engine.entity.app.ParameterDetail;
import com.app2.engine.repository.ParameterDetailRepository;
import com.app2.engine.repository.ParameterRepository;
import com.app2.engine.service.SmbFileService;
import com.app2.engine.util.AppUtil;
import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.Session;
import jcifs.smb.NtlmPasswordAuthentication;
import jcifs.smb.SmbFile;
import jcifs.smb.SmbFileInputStream;
import jcifs.smb.SmbFileOutputStream;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;

@Service
public class SmbFileServiceImpl implements SmbFileService {

    private Logger LOGGER = LoggerFactory.getLogger(this.getClass());

    @Value("${ssh.lead.address}")
    public String LEAD_SERVER_ADDRESS;

    @Value("${ssh.lead.port}")
    public Integer LEAD_SERVER_PORT;

    @Value("${ssh.lead.username}")
    public String LEAD_USERNAME;

    @Value("${ssh.lead.password}")
    public String LEAD_PASSWORD;

    @Autowired
    ParameterDetailRepository parameterDetailRepository;

    @Autowired
    ParameterRepository parameterRepository;

    @Override
    public String remoteFileToLocalFile(String fileName, String topic) {
        //folder ToLEAD --> folder Download
        String remoteDir  = null;
        String localDir = null;
        Session session = null;
        ChannelSftp channelSftp = null;
        String result = null;
        try {

            localDir =  pathLocalDir(topic,"download");
            remoteDir =  pathRemoteDir(topic,"download");

            LOGGER.debug("localDir {}", localDir);
            LOGGER.debug("remoteDir {}", remoteDir);

            JSch jsch = new JSch();
            session = jsch.getSession(LEAD_USERNAME, LEAD_SERVER_ADDRESS, LEAD_SERVER_PORT);
            session.setPassword(LEAD_PASSWORD);
            LOGGER.debug("LEAD_USERNAME {}", LEAD_USERNAME);
            LOGGER.debug("LEAD_SERVER_ADDRESS {}", LEAD_SERVER_ADDRESS);
            LOGGER.debug("LEAD_SERVER_PORT {}", LEAD_SERVER_PORT);
            LOGGER.debug("LEAD_PASSWORD {}", LEAD_PASSWORD);

            java.util.Properties config = new java.util.Properties();
            config.put("StrictHostKeyChecking", "no");

            session.setConfig(config);
            session.connect();
            LOGGER.debug("session.connect  !!");

            channelSftp = (ChannelSftp) session.openChannel("sftp");
            channelSftp.connect();
            LOGGER.debug("channelSftp.connect  !!");

            String src =remoteDir+"/"+fileName;
            LOGGER.debug("src  {}",src);
            channelSftp.get(src,localDir);

            channelSftp.disconnect();
            LOGGER.debug("channelSftp.disconnect  !!");

            session.disconnect();
            LOGGER.debug("session.disconnect  !!");

        }catch (Exception e){
            LOGGER.error("Error {}", e.getMessage(),e);
        }finally {
            if (channelSftp != null && channelSftp.isConnected()) {
                channelSftp.disconnect();
            }

            if (session != null && session.isConnected()) {
                session.disconnect();
            }

            result = localDir+"/"+fileName;
            LOGGER.debug("result  {}",result);



        }
        return result;
    }

    @Override
    public String localFileToRemoteFile(String fileName, String topic) {
        //folder upload --> folder FormLEAD
        String remoteDir = null;
        String localDir = null;
        Session session = null;
        ChannelSftp channelSftp = null;
        InputStream targetStream = null;
        try {

            localDir =  pathLocalDir(topic,"upload");
            remoteDir =  pathRemoteDir(topic,"upload");
            LOGGER.debug("localDir {}", localDir);
            LOGGER.debug("remoteDir {}", remoteDir);

            JSch jsch = new JSch();
            session = jsch.getSession(LEAD_USERNAME, LEAD_SERVER_ADDRESS, LEAD_SERVER_PORT);
            session.setPassword(LEAD_PASSWORD);
            LOGGER.debug("LEAD_USERNAME {}", LEAD_USERNAME);
            LOGGER.debug("LEAD_SERVER_ADDRESS {}", LEAD_SERVER_ADDRESS);
            LOGGER.debug("LEAD_SERVER_PORT {}", LEAD_SERVER_PORT);
            LOGGER.debug("LEAD_PASSWORD {}", LEAD_PASSWORD);

            java.util.Properties config = new java.util.Properties();
            config.put("StrictHostKeyChecking", "no");

            session.setConfig(config);
            session.connect();
            LOGGER.debug("session.connect  !!");

            channelSftp = (ChannelSftp) session.openChannel("sftp");
            channelSftp.connect();
            LOGGER.debug("channelSftp.connect  !!");
            channelSftp.cd(remoteDir);

            String src = localDir+"/"+fileName;
            LOGGER.debug("src  {}",src);
            File initialFile = new File(src);
            targetStream = new FileInputStream(initialFile);

            channelSftp.put(targetStream, initialFile.getName(), ChannelSftp.OVERWRITE);
            channelSftp.disconnect();
            LOGGER.debug("channelSftp.disconnect  !!");

            session.disconnect();
            LOGGER.debug("session.disconnect  !!");

        }catch (Exception e){
            LOGGER.error("Error {}", e.getMessage(),e);
        } finally {
            if (channelSftp != null && channelSftp.isConnected()) {
                channelSftp.disconnect();
            }

            if (session != null && session.isConnected()) {
                session.disconnect();
            }

            IOUtils.closeQuietly(targetStream);
        }
        return localDir;
    }

    @Override
    public String copyRemoteFolderToLocalFolder(String parameterCode) {
        LOGGER.info("====> copyRemoteFolderToLocalFolder");
        String timeLog = new SimpleDateFormat("yyyyMMdd").format(Calendar.getInstance().getTime());
        String today = timeLog + ".txt";

        ParameterDetail pDetail = parameterDetailRepository.findByParameterAndCode("APP_CONFIG", "10");
        String path = pDetail.getVariable1();
        String username = pDetail.getVariable2();
        String password = pDetail.getVariable3();
        String backup = pDetail.getVariable4();
        LOGGER.debug("smbPath    {}", path);
        LOGGER.debug("username      {}", username);
        LOGGER.debug("password      {}", password);
        LOGGER.debug("localPath    {}", backup);
        String pathFileLocal = null;

        Parameter parameter = parameterRepository.findByCode(parameterCode);
        List<ParameterDetail> parameterDetails = parameterDetailRepository.findByParameter(parameter);
        FileOutputStream out = null;
        try {
            if (AppUtil.isNull(pDetail.getVariable9())) {
                //From SMTP
                NtlmPasswordAuthentication authentication = new NtlmPasswordAuthentication("", username, password);

                File pathFile = new File(path);
                File filesList[] = pathFile.listFiles();
                for (File file : filesList) {
                    String fileName = file.getName();

                    for (ParameterDetail detail : parameterDetails) {
                        String fileNameToDay = detail.getVariable1() + today;

                        if (fileName.equals(fileNameToDay)) {
                            SmbFile remoteFile = new SmbFile(path + "/" + fileName, authentication);
                            remoteFile.connect(); //Try to connect
                            SmbFileInputStream in = new SmbFileInputStream(remoteFile);

                            File localFile = new File(backup + "/" + fileName);
                            if (!localFile.exists()) {
                                localFile.getParentFile().mkdirs();
                                localFile.createNewFile();
                            }
                            out = new FileOutputStream(localFile, false);


                            byte[] buffer = new byte[16904];
                            int read = 0;
                            while ((read = in.read(buffer)) > 0)
                                out.write(buffer, 0, read);

                            in.close();
                            out.close();
                            pathFileLocal = localFile.getPath();
                        }
                    }
                }

            } else {
                File pathFile = new File(path);
                File filesList[] = pathFile.listFiles();
                for (File file : filesList) {
                    String source = file.getPath();
                    String fileName = file.getName();

                    for (ParameterDetail detail : parameterDetails) {
                        String fileNameToDay = detail.getVariable1() + today;

                        if (fileName.equals(fileNameToDay)) {
                            File dest = new File(backup + "/" + fileName);
                            FileUtils.copyFile(new File(source), dest);
                            pathFileLocal = dest.getPath();                        }
                    }
                }
            }

        } catch (Exception e) {
            LOGGER.error("Error {}", e.getMessage(), e);
            throw new RuntimeException(e);
        } finally {
            if (out != null) {
                try {
                    out.close();
                } catch (IOException e) {
                    LOGGER.error("Error {}", e.getMessage(), e);
                }
            }

        }
        LOGGER.debug("pathFileLocal {}", pathFileLocal);
        return pathFileLocal;
    }






    public String pathLocalDir(String topic, String type){
        String localDir = null;
        ParameterDetail parameter_DL = null;

        if(topic.equals("DCMS")){
            parameter_DL = parameterDetailRepository.findByParameterAndCode("BATCH_PATH_LOCAL","01");
        }else if(topic.equals("CBS")){
            parameter_DL = parameterDetailRepository.findByParameterAndCode("BATCH_PATH_LOCAL","02");
        }else if(topic.equals("CMS")){
            parameter_DL = parameterDetailRepository.findByParameterAndCode("BATCH_PATH_LOCAL","03");
        }else if(topic.equals("AD")){
            parameter_DL = parameterDetailRepository.findByParameterAndCode("BATCH_PATH_LOCAL","04");
        }else if(topic.equals("WRN")){
            parameter_DL = parameterDetailRepository.findByParameterAndCode("BATCH_PATH_LOCAL","05");
        }else if(topic.equals("HR")){
            parameter_DL = parameterDetailRepository.findByParameterAndCode("BATCH_PATH_LOCAL","06");
        }

        if (AppUtil.isNotNull(parameter_DL)){
            if (type.equals("download")){
                localDir = parameter_DL.getVariable1();
            }else if (type.equals("upload")){
                localDir = parameter_DL.getVariable2();
            }
        }
        return localDir;
    }

    public String pathRemoteDir(String topic,String type){
        String remoteDir  = null;
        ParameterDetail parameter_UL = null;

        if(topic.equals("DCMS")){
            parameter_UL = parameterDetailRepository.findByParameterAndCode("BATCH_PATH_REMOTE","01");
        }else if(topic.equals("CMS")){
            parameter_UL = parameterDetailRepository.findByParameterAndCode("BATCH_PATH_REMOTE","02");
        }else if(topic.equals("CBS")){
            parameter_UL = parameterDetailRepository.findByParameterAndCode("BATCH_PATH_REMOTE","03");
        }else if(topic.equals("AD")){
            parameter_UL = parameterDetailRepository.findByParameterAndCode("BATCH_PATH_REMOTE","04");
        }else if(topic.equals("HR")){
            parameter_UL = parameterDetailRepository.findByParameterAndCode("BATCH_PATH_REMOTE","05");
        }else if(topic.equals("WRN")){
            parameter_UL = parameterDetailRepository.findByParameterAndCode("BATCH_PATH_REMOTE","06");
        }

        if (AppUtil.isNotNull(parameter_UL)){
            if (type.equals("download")){
                remoteDir = parameter_UL.getVariable1();
            }else if (type.equals("upload")){
                remoteDir = parameter_UL.getVariable2();
            }
        }

        return remoteDir;
    }

    //    @Override
//    public String copyRemoteFileToLocalFile(String fileName) {
//        LOGGER.info("====> copyRemoteFileToLocalFile");
//        ParameterDetail pDetail = parameterDetailRepository.findByParameterAndCode("APP_CONFIG", "10");
//        String smbPath = pDetail.getVariable1();
//        String username = pDetail.getVariable2();
//        String password = pDetail.getVariable3();
//        String backup = pDetail.getVariable4();
//        LOGGER.debug("smbPath    {}", smbPath);
//        LOGGER.debug("username      {}", username);
//        LOGGER.debug("password      {}", password);
//        LOGGER.debug("remotePath    {}", fileName);
//        LOGGER.debug("localPath    {}", backup);
//        String pathFileLocal=null;
//        try {
//            if (AppUtil.isNull(pDetail.getVariable9())) {
//                //From SMTP
//                NtlmPasswordAuthentication authentication = new NtlmPasswordAuthentication("", username, password);
//
//
//                SmbFile remoteFile = new SmbFile(smbPath + "/" + fileName, authentication);
//                remoteFile.connect(); //Try to connect
//                SmbFileInputStream in = new SmbFileInputStream(remoteFile);
//
//                File localFile = new File(backup + "/" + fileName);
//                if (!localFile.exists()) {
//                    localFile.getParentFile().mkdirs();
//                    localFile.createNewFile();
//                }
//                FileOutputStream out = new FileOutputStream(localFile, false);
//
//
//                byte[] buffer = new byte[16904];
//                int read = 0;
//                while ((read = in.read(buffer)) > 0)
//                    out.write(buffer, 0, read);
//
//                in.close();
//                out.close();
//                pathFileLocal=localFile.getPath();
//            } else {
//                File source = new File(pDetail.getVariable9() + "/" + fileName);
//                File dest = new File(backup + "/" + fileName);
//                FileUtils.copyFile(source, dest);
//                pathFileLocal=dest.getPath();
//            }
//        } catch (Exception e) {
//            LOGGER.error("Error {}", e.getMessage(), e);
//            throw new RuntimeException(e);
//        }
//        LOGGER.debug("pathFileLocal {}",pathFileLocal);
//        return pathFileLocal;
//    }
//
//    @Override
//    public String copyLocalFileToRemoteFile(String fileName) {
//        LOGGER.info("====> copyLocalFileToRemoteFile");
//        ParameterDetail pDetail = parameterDetailRepository.findByParameterAndCode("APP_CONFIG", "10");
//        String smbPath = pDetail.getVariable1();
//        String username = pDetail.getVariable2();
//        String password = pDetail.getVariable3();
//        String backup = pDetail.getVariable4();
//        LOGGER.debug("smbPath    {}", smbPath);
//        LOGGER.debug("username      {}", username);
//        LOGGER.debug("password      {}", password);
//        LOGGER.debug("remotePath    {}", fileName);
//        LOGGER.debug("localPath    {}", backup);
//        String pathFileLocal=null;
//        try {
//            if (AppUtil.isNull(pDetail.getVariable9())) {
//                //From SMTP
//                NtlmPasswordAuthentication authentication = new NtlmPasswordAuthentication("", username, password);
//
//
//                SmbFile remoteFile = new SmbFile(smbPath + "/" + fileName, authentication);
//                remoteFile.connect(); //Try to connect
//                SmbFileOutputStream out = new SmbFileOutputStream(remoteFile,false);
//
//                File localFile = new File(backup + "/" + fileName);
//                if (!localFile.exists()) {
//                    localFile.getParentFile().mkdirs();
//                    localFile.createNewFile();
//                }
//                FileInputStream in = new FileInputStream(localFile);
//
//
//                byte[] buffer = new byte[16904];
//                int read = 0;
//                while ((read = in.read(buffer)) > 0)
//                    out.write(buffer, 0, read);
//
//                in.close();
//                out.close();
//                pathFileLocal=localFile.getPath();
//            } else {
//                File dest = new File(pDetail.getVariable9() + "/" + fileName);
//                File source = new File(backup + "/" + fileName);
//            FileUtils.copyFile(source,dest);
//                pathFileLocal = source.getPath();
//            }
//        } catch (Exception e) {
//            LOGGER.error("Error {}", e.getMessage(), e);
//            throw new RuntimeException(e);
//        }
//        LOGGER.debug("pathFileLocal {}",pathFileLocal);
//        return pathFileLocal;
//    }
//

//
//    private void deleteFileERP(String fileName) {
//
//        ParameterDetail pDetail = parameterDetailRepository.findByParameterAndCode("APP_CONFIG", "10");
//        String smbPath = pDetail.getVariable1();
//        String username = pDetail.getVariable2();
//        String password = pDetail.getVariable3();
//        LOGGER.debug("smbPath    {}", smbPath);
//        LOGGER.debug("username      {}", username);
//        LOGGER.debug("password      {}", password);
//        NtlmPasswordAuthentication authentication = new NtlmPasswordAuthentication("", username, password);
//        try {
//            SmbFile remoteFile = new SmbFile(smbPath + "/" + fileName, authentication);
//            if (remoteFile.isFile() && remoteFile.exists()) {
//                remoteFile.delete();
//                LOGGER.debug("Delete success");
//            } else {
//                LOGGER.debug("Not found File");
//            }
//        } catch (Exception e) {
//            LOGGER.error("Error {}", e.getMessage(), e);
//            throw new RuntimeException(e);
//        }
//
//    }
}

