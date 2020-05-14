package com.app2.engine.entity.app;

import com.app2.engine.entity.base.BaseEntity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;

@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = {"id"})
public class NotificationSetting extends BaseEntity {
    private String processType;
    private String department;
    private String sendToAdmin;
    private String sendToLawyer;
    private String notiType;
    private String notiTime;
    private String notiFormat;
}
