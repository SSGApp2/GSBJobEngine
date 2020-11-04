package com.app2.engine.repository;

import com.app2.engine.entity.app.Parameter;
import com.app2.engine.entity.app.ParameterDetail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ParameterDetailRepository extends JpaRepository<ParameterDetail, Long> {
    List<ParameterDetail> findByParameter(@Param("parameter") Parameter parameter);
    @Query("select o from ParameterDetail o join o.parameter b where b.code=:pCode and o.code=:dCode ")
    ParameterDetail findByParameterAndCode(@Param("pCode") String pCode, @Param("dCode") String dCode);

    @Query("select o from ParameterDetail o join o.parameter b where b.code=:pCode")
    List<ParameterDetail> findByParameterDetailsCode(@Param("pCode") String pCode);

    @Query("select pd from ParameterDetail pd join  pd.parameter p where pd.code =:pdCode and p.code =:pCode")
    ParameterDetail findByPCodeAndPdCode(@Param("pCode")String pCode,@Param("pdCode")String pdCode);

    @Query("select pd from ParameterDetail pd join  pd.parameter p where p.code =:pCode")
    List<ParameterDetail>  findByPCode(@Param("pCode")String pCode);


}
