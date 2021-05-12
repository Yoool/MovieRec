package com.work.movierec.dao;

import com.work.movierec.domain.*;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import com.work.movierec.domain.SeckillUser;

import java.util.List;

@Mapper
public interface SeckillUserDao {

    @Select("select * from user where id = #{id}")
    public SeckillUser getById(@Param("id") long id);

    @Select("select * from user where stuno = #{stuno}")
    public SeckillUser getByStuno(@Param("stuno") String stuno);

    @Select("select * from user")
    public List<SeckillUser> getUserList();
}
