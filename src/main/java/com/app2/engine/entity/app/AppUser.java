package com.app2.engine.entity.app;

import com.app2.engine.entity.base.BaseEntity;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.Date;

@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "app_user")
@EqualsAndHashCode(of = {"id"})
public class AppUser extends BaseEntity {

    private String username;

    private String password;

    private String userType; // I:Internal , E:External

    @Column(name = "status", length = 1)
    private String status; // A:Active , S:Suspend , I:Inactive , T:Terminated ,R:Retire

    private Integer loginWrong;

    @Temporal(TemporalType.DATE)
    private Date activeDate; //วันที่ล่าสุดที่ user active

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Temporal(TemporalType.TIMESTAMP)
    private Date lockStartDate;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Temporal(TemporalType.TIMESTAMP)
    private Date passwordLastUpdate;

    private String otp;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Temporal(TemporalType.TIMESTAMP)
    private Date otpCreateDate;

}
