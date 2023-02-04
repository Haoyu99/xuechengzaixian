package com.xuecheng.model.po;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
* <p>
    * 
    * </p>
*
* @author itcast
*/
    @Data
        @EqualsAndHashCode(callSuper = false)
    @Accessors(chain = true)
    public class TeachplanMedia implements Serializable {

    private static final long serialVersionUID = 1L;

            /**
            * 主键
            */
            @TableId(value = "id", type = IdType.AUTO)
    private Long id;

            /**
            * 媒资文件id
            */
    private String mediaId;

            /**
            * 课程计划标识
            */
    private Long teachplanId;

            /**
            * 课程标识
            */
    private Long courseId;

            /**
            * 媒资文件原始名称
            */
        @TableField("media_fileName")
    private String mediaFilename;

            @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createDate;

            /**
            * 创建人
            */
    private String createPeople;

            /**
            * 修改人
            */
    private String changePeople;


}
