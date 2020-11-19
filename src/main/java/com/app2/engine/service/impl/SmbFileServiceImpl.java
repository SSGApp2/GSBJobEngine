package com.app2.engine.service.impl;

import com.app2.engine.entity.app.Parameter;
import com.app2.engine.entity.app.ParameterDetail;
import com.app2.engine.repository.ParameterDetailRepository;
import com.app2.engine.repository.ParameterRepository;
import com.app2.engine.service.SmbFileService;
import com.app2.engine.util.AppUtil;
import com.app2.engine.util.DateUtil;
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
    public String remoteFileToLocalFile(String fileName, String topic) {
        //folder ToLEAD --> folder Download
        String remoteDir  = null;
        String localDir = null;
        Session session = null;
        ChannelSftp channelSftp = null;
        try {

            localDir =  pathLocalDir(topic,"download");
            remoteDir =  pathRemoteDir(topic,"download");

            String ipAddress = "";
            String username = "";
            String password = "";
            Integer port = null;

            LOGGER.debug("topic : {}", topic);
            LOGGER.debug("localDir : {}", localDir);
            LOGGER.debug("remoteDir : {}", remoteDir);

            if(topic.equals("HR") || topic.equals("AD")){
                ParameterDetail parameter_DL = parameterDetailRepository.findByParameterAndCode("FTP_CONNECTION", "FTP_HR");
                ipAddress = parameter_DL.getVariable3();
                username = parameter_DL.getVariable1();
                password = parameter_DL.getVariable2();
                port = Integer.valueOf(parameter_DL.getVariable4());
            }else{
                ipAddress = LEAD_SERVER_ADDRESS;
                username = LEAD_USERNAME;
                password = LEAD_PASSWORD;
                port = LEAD_SERVER_PORT;
            }

            JSch jsch = new JSch();
            session = jsch.getSession(username, ipAddress, port);
            session.setPassword(password);
            LOGGER.debug("LEAD_USERNAME : {}", username);
            LOGGER.debug("LEAD_SERVER_ADDRESS : {}", ipAddress);
            LOGGER.debug("LEAD_SERVER_PORT : {}", port);
            LOGGER.debug("LEAD_PASSWORD : {}", password);

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
    public String localFileToRemoteFile(String fileName, String topic) {
        //folder upload --> folder FormLEAD
        String remoteDir = null;
        String localDir = null;
        Session session = null;
        ChannelSftp channelSftp = null;
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

    private String pathLocalDir(String topic, String type){
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

        return this.getPath(parameter_DL,type);
    }

    private String pathRemoteDir(String topic, String type){
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

        return this.getPath(parameter_UL,type);
    }

    private String getPath(ParameterDetail param,String type){
        String remoteDir = null;
        if (AppUtil.isNotNull(param)){
            if (type.equals("download")){
                remoteDir = param.getVariable1() + "/" + DateUtil.codeCurrentDate();
            }else if (type.equals("upload")){
                remoteDir = param.getVariable2() + "/" + DateUtil.codeCurrentDate();
            }
        }

        return remoteDir;
    }

}

