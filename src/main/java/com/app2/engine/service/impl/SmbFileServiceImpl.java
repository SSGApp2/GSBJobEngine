package com.app2.engine.service.impl;

import com.app2.engine.entity.app.Parameter;
import com.app2.engine.entity.app.ParameterDetail;
import com.app2.engine.repository.ParameterDetailRepository;
import com.app2.engine.repository.ParameterRepository;
import com.app2.engine.service.SmbFileService;
import com.app2.engine.util.AppUtil;
import com.app2.engine.util.DateUtil;
import com.app2.engine.util.FileUtil;
import com.jcraft.jsch.*;
import jcifs.smb.NtlmPasswordAuthentication;
import jcifs.smb.SmbFile;
import jcifs.smb.SmbFileInputStream;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
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
    public String remoteFileToLocalFile(String fileName, String topic,String date) {
        //folder ToLEAD --> folder Download รับจากธนาคาร
        String remoteDir  = null;
        String localDir = null;
        Session session = null;
        ChannelSftp channelSftp = null;
        try {

            localDir =  pathLocalDir(topic,"download",date);
            remoteDir =  pathRemoteDir(topic,"download",date);

            LOGGER.debug("localDir : {}", localDir);
            LOGGER.debug("remoteDir : {}", remoteDir);

            JSch jsch = new JSch();
            session = jsch.getSession(LEAD_USERNAME, LEAD_SERVER_ADDRESS, LEAD_SERVER_PORT);
            session.setPassword(LEAD_PASSWORD);
            LOGGER.debug("LEAD_USERNAME : {}", LEAD_USERNAME);
            LOGGER.debug("LEAD_SERVER_ADDRESS : {}", LEAD_SERVER_ADDRESS);
            LOGGER.debug("LEAD_SERVER_PORT : {}", LEAD_SERVER_PORT);
            LOGGER.debug("LEAD_PASSWORD : {}", LEAD_PASSWORD);

            java.util.Properties config = new java.util.Properties();
            config.put("StrictHostKeyChecking", "no");

            session.setConfig(config);
            session.connect();
            LOGGER.debug("session.connect  !!");

            channelSftp = (ChannelSftp) session.openChannel("sftp");
            channelSftp.connect();
            LOGGER.debug("channelSftp.connect  !!");

            String src =remoteDir+"/"+fileName;
            LOGGER.debug("src {}",src);
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
        }

        return localDir+"/"+fileName;
    }

    @Override
    public String localFileToRemoteFile(String fileName, String topic,String date) {
        //folder upload --> folder FormLEAD ส่งให้ธนาคาร
        String remoteDir = null;
        String localDir = null;
        Session session = null;
        ChannelSftp channelSftp = null;
        try {

            localDir =  pathLocalDir(topic,"upload",date);
            remoteDir =  pathRemoteDir(topic,"upload",date);
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

            try{
                channelSftp.cd(remoteDir);
            }catch (SftpException e){
                channelSftp.mkdir(remoteDir);
                channelSftp.cd(remoteDir);
            }

            String src = localDir+"/"+fileName;
            LOGGER.debug("src  {}",src);
            File initialFile = new File(src);
            InputStream targetStream = new FileInputStream(initialFile);

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
                            FileOutputStream out = new FileOutputStream(localFile, false);


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
        }
        LOGGER.debug("pathFileLocal {}", pathFileLocal);
        return pathFileLocal;
    }

    private String pathLocalDir(String topic, String type,String date){
        String localDir = null;
        ParameterDetail parameter_DL = null;

        switch (topic) {
            case "DCMS":
                parameter_DL = parameterDetailRepository.findByParameterAndCode("BATCH_PATH_LOCAL", "01");
                break;
            case "CBS":
                parameter_DL = parameterDetailRepository.findByParameterAndCode("BATCH_PATH_LOCAL", "02");
                break;
            case "CMS":
                parameter_DL = parameterDetailRepository.findByParameterAndCode("BATCH_PATH_LOCAL", "03");
                break;
            case "AD":
                parameter_DL = parameterDetailRepository.findByParameterAndCode("BATCH_PATH_LOCAL", "04");
                break;
            case "WRN":
                parameter_DL = parameterDetailRepository.findByParameterAndCode("BATCH_PATH_LOCAL", "05");
                break;
            case "HR":
                parameter_DL = parameterDetailRepository.findByParameterAndCode("BATCH_PATH_LOCAL", "06");
                break;
        }

        if(AppUtil.isNotNull(parameter_DL)){
            String paramDL = null;
            if (type.equals("download")){
                paramDL = parameter_DL.getVariable1();
            }else if (type.equals("upload")){
                paramDL = parameter_DL.getVariable2();
            }
            if(AppUtil.isNotNull(paramDL)){
                File directory = new File(paramDL + "/" +DateUtil.codeCurrentDate());
                if (! directory.exists()){
                    directory.mkdirs();
                }
            }
        }

        return this.getPath(parameter_DL,type,date);
    }

    private String pathRemoteDir(String topic, String type,String date){
        String remoteDir  = null;
        ParameterDetail parameter_UL = null;

        switch (topic) {
            case "DCMS":
                parameter_UL = parameterDetailRepository.findByParameterAndCode("BATCH_PATH_REMOTE", "01");
                break;
            case "CMS":
                parameter_UL = parameterDetailRepository.findByParameterAndCode("BATCH_PATH_REMOTE", "02");
                break;
            case "CBS":
                parameter_UL = parameterDetailRepository.findByParameterAndCode("BATCH_PATH_REMOTE", "03");
                break;
            case "AD":
                parameter_UL = parameterDetailRepository.findByParameterAndCode("BATCH_PATH_REMOTE", "04");
                break;
            case "HR":
                parameter_UL = parameterDetailRepository.findByParameterAndCode("BATCH_PATH_REMOTE", "05");
                break;
            case "WRN":
                parameter_UL = parameterDetailRepository.findByParameterAndCode("BATCH_PATH_REMOTE", "06");
                break;
        }

        if(AppUtil.isNotNull(parameter_UL)){
            String paramUL = null;
            if (type.equals("download")){
                paramUL = parameter_UL.getVariable1();
            }else if (type.equals("upload")){
                paramUL = parameter_UL.getVariable2();
            }
            if(AppUtil.isNotNull(paramUL)){
                File directory = new File(paramUL + "/" +DateUtil.codeCurrentDate());
                if (! directory.exists()){
                    directory.mkdirs();
                }
            }
        }

        return this.getPath(parameter_UL,type,date);
    }

    private String getPath(ParameterDetail param,String type,String date){
        String remoteDir = null;
        if (AppUtil.isNotNull(param)){
            if (type.equals("download")){
                remoteDir = param.getVariable1() + "/" + date;
            }else if (type.equals("upload")){
                remoteDir = param.getVariable2() + "/" + date;
            }
        }

        return remoteDir;
    }

}

