package com.app2.engine.entity.app;

import com.app2.engine.entity.base.BaseEntity;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name="app_user")
@EqualsAndHashCode(of={"id"})
public class AppUser extends BaseEntity {

    private String username;

    private String password;

    private String userType; // I:Internal , E:External

    @Column(name = "status", length = 1)
    private String status; // A:Active , S:Suspend , I:Inactive , T:Terminated ,R:Retire

    private Integer loginWrong;

    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss")
    @Temporal(TemporalType.TIMESTAMP)
    private Date lockStartDate;

    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss")
    @Temporal(TemporalType.TIMESTAMP)
    private Date passwordLastUpdate;

    private String otp;

    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss")
    @Temporal(TemporalType.TIMESTAMP)
    private Date otpCreateDate;

}
