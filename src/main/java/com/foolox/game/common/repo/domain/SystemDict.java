package com.foolox.game.common.repo.domain;

import com.foolox.game.constants.DictType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 系统字典表
 * |-- 租户名 orgi ：获取不同租户的配置
 *      |-- model名 game_model : 获取不同model的配置 （麻将、地主、牛牛）
 *          |-- type名 game_type : 游戏玩法配置 （二人地主、经典斗地主）
 *              |-- playway名 game_playway : 游戏玩法配置 (中低高级场)
 */

@Data
@AllArgsConstructor
@NoArgsConstructor
@Document(collection = "system_dict")
public class SystemDict {
    @Id
    private Long id;

    private String name;

    private String code;

    private DictType dictType;

    private Long parentId;

    private String config;

    private Date createTime;

    List<SystemDict> dicts = new ArrayList<>();
}