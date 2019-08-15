package com.ekoplat.iot.server.common.constant;

/**
 * @Description:
 * @Author: wuwudeqi
 * @Date: 14:18 2019-07-10
 **/
public class ModuleAndCmd {
    /**后台
     * Module
     * 主动升级 10 02
     */
    public static final short  H_Active_Module = 4098;

    /**后台
     * Cmd
     * 主动升级 00 01
     */
    public static final short H_Active_Cmd = 1;



    /**硬件
     * 确认升级 00 02
     */
    public static final int Y_Active_Module = 2;

        /**硬件
         * 上一包正确00 01
         */
        public static final int Y_Active_Cmd_True = 1;

        /**硬件
         * 上一包错误 00 01
         */
        public static final int Y_Active_Cmd_false = 2;

        /**硬件
         * 不需要升级升级 00 01
         */
        public static final int Y_Active_Cmd_No = 0;
}
