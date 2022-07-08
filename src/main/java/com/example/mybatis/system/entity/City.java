package com.example.mybatis.system.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

import java.io.Serializable;

/**
 * <p>
 * 
 * </p>
 *
 * @author songwz
 * @since 2022-07-07
 */
@Data
public class City implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    private String name;

    private String state;

    private String country;

    @Override
    public String toString() {
        return "City{" +
            "id=" + id +
            ", name=" + name +
            ", state=" + state +
            ", country=" + country +
        "}";
    }
}
