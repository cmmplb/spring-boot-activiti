package io.github.cmmplb.activiti.constants;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author penglibo
 * @date 2024-11-08 11:44:05
 * @since jdk 1.8
 */
public class BizConstant {

    /**
     * 流程 key 对应名称类型枚举
     */
    @Getter
    @AllArgsConstructor
    public enum ProcessEnum {

        LEAVE_APPLY("leave-apply", "请假申请", "考勤管理"),
        ;
        private final String key;
        private final String name;
        private final String category;

        public static String getCategory(String key) {
            for (ProcessEnum e : values()) {
                if (e.getKey().equals(key)) {
                    return e.name;
                }
            }
            return null;
        }
    }

    /**
     * 事项申请类型枚举
     */
    @Getter
    @AllArgsConstructor
    public enum ApplyTypeEnum {

        ATTENDANCE((byte) 1, "考勤管理"),
        ADMINISTRATION((byte) 2, "行政管理"),
        FINANCE((byte) 3, "财务管理"),
        PERSONAL((byte) 4, "人事管理"),
        ;

        private final Byte type;
        private final String name;

        public static String getName(byte type) {
            for (ApplyTypeEnum e : values()) {
                if (e.getType().equals(type)) {
                    return e.name;
                }
            }
            return null;
        }
    }

    /**
     * 流程状态枚举
     */
    @Getter
    @AllArgsConstructor
    public enum StatusEnum {
        IN_PROGRESS((byte) 0, "进行中"),
        COMPLETED((byte) 1, "已完成"),
        REJECTED((byte) 2, "已驳回"),
        REVOKED((byte) 3, "已撤销"),
        ;

        private final Byte status;
        private final String name;

        public static String getName(byte type) {
            for (StatusEnum e : values()) {
                if (e.getStatus().equals(type)) {
                    return e.name;
                }
            }
            return null;
        }
    }

    /**
     * 考勤申请管理
     */
    public static class AttendanceApply {

        /**
         * 类型枚举
         */
        @Getter
        @AllArgsConstructor
        public enum TypeEnum {

            LEAVE((byte) 1, "请假"),
            BUSINESS_TRIP((byte) 2, "出差"),
            ;

            private final Byte type;
            private final String name;

            public static String getName(byte type) {
                for (TypeEnum e : values()) {
                    if (e.getType().equals(type)) {
                        return e.name;
                    }
                }
                return null;
            }
        }

        /**
         * 请假类型枚举
         */
        @Getter
        @AllArgsConstructor
        public enum LeaveTypeEnum {

            LEAVE_OF_ABSENCE((byte) 1, "事假"),
            SICK_LEAVE((byte) 2, "病假"),
            ANNUAL_LEAVE((byte) 3, "年假"),
            FUNERAL_LEAVE((byte) 4, "丧假"),
            MATERNITY_LEAVE((byte) 5, "产假"),
            ;

            private final Byte type;
            private final String name;

            public static String getName(byte type) {
                for (LeaveTypeEnum e : values()) {
                    if (e.getType().equals(type)) {
                        return e.name;
                    }
                }
                return null;
            }
        }
    }
}
