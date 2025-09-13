package com.lcsk42.starter.database.mybatisplus.config;

import com.baomidou.mybatisplus.annotation.DbType;
import com.lcsk42.starter.database.annotation.TreeField;
import com.lcsk42.starter.database.model.resp.tree.TreeNodeConfig;
import com.lcsk42.starter.database.mybatisplus.enums.MyBatisPlusIdGeneratorType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.Validate;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.NestedConfigurationProperty;

@Data
@ConfigurationProperties(DatabaseExtensionProperties.PREFIX)
public class DatabaseExtensionProperties {

    public static final String PREFIX = "framework.database.mybatis-plus";

    public static final String MAPPER_PACKAGE = PREFIX + ".mapper-package";

    public static final String ID_GENERATOR_TYPE = PREFIX + ".id-generator.type";

    /**
     * Mapper 接口扫描包（配置时必须使用：mapper-package 键名）
     * <p>
     * e.g. com.example.**.mapper
     * </p>
     */
    private String mapperPackage;

    /**
     * ID 生成器
     */
    @NestedConfigurationProperty
    private IdGenerator idGenerator;

    /**
     * 分页插件配置
     */
    private Pagination pagination;

    /**
     * 启用乐观锁插件
     */
    private boolean optimisticLockerEnabled = false;

    /**
     * 启用防全表更新与删除插件
     */
    private boolean blockAttackPluginEnabled = true;

    private TreeDictModel treeDictModel;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class IdGenerator {
        private MyBatisPlusIdGeneratorType type = MyBatisPlusIdGeneratorType.DEFAULT;
    }

    /**
     * 分页插件配置属性
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Pagination {
        /**
         * 数据库类型
         */
        private DbType dbType;

        /**
         * 是否溢出处理
         */
        private boolean overflow = false;

        /**
         * 单页分页条数限制（默认：-1 表示无限制）
         */
        private Long maxLimit = -1L;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class TreeDictModel {
        /**
         * ID 字段名
         */
        private String idKey = "id";

        /**
         * 父 ID 字段名
         */
        private String parentIdKey = "parentId";

        /**
         * 名称字段名
         */
        private String nameKey = "name";

        /**
         * 排序字段名
         */
        private String weightKey = "weight";

        /**
         * 子列表字段名
         */
        private String childrenKey = "children";

        /**
         * 递归深度（< 0 不限制）
         */
        private Integer deep = -1;

        /**
         * 根节点 ID
         */
        private Long rootId = 0L;

        /**
         * 生成 {@link TreeNodeConfig} 对象
         *
         * @return {@link TreeNodeConfig} 对象
         */
        public TreeNodeConfig genTreeNodeConfig() {
            return TreeNodeConfig.DEFAULT_CONFIG
                    .setIdKey(idKey)
                    .setParentIdKey(parentIdKey)
                    .setNameKey(nameKey)
                    .setWeightKey(weightKey)
                    .setChildrenKey(childrenKey)
                    .setDeep(deep < 0 ? null : deep);
        }

        /**
         * 根据 @TreeField 配置生成树结构配置
         *
         * @param treeField 树结构字段注解
         * @return 树结构配置
         */
        public TreeNodeConfig genTreeNodeConfig(TreeField treeField) {
            Validate.notNull(treeField, "请添加并配置 @TreeField 树结构信息");
            return new TreeNodeConfig().setIdKey(treeField.value())
                    .setParentIdKey(treeField.parentIdKey())
                    .setNameKey(treeField.nameKey())
                    .setWeightKey(treeField.weightKey())
                    .setChildrenKey(treeField.childrenKey())
                    .setDeep(treeField.deep() < 0 ? null : treeField.deep());
        }
    }
}
