import * as moment from "moment";
const utils = {
    /**
     * 生成UUID
     * @param len 长度，一般不需要传
     * @param radix 基数，一般不需要传
     * @returns uuid
     */
    uuid(len, radix) {
        let chars = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ".split("");
        let arr = [],
            i;
        radix = radix || chars.length;
        if (len) {
            // Compact form
            for (i = 0; i < len; i++) {
                arr[i] = chars[0 | (Math.random() * radix)];
            }
        } else {
            // rfc4122, version 4 form
            let r; // rfc4122 requires these characters
            arr[8] = arr[13] = arr[18] = arr[23] = "-";
            arr[14] = "4"; // Fill in random data. At i==19 set the high bits of clock sequence as // per rfc4122, sec. 4.1.5
            for (i = 0; i < 36; i++) {
                if (!arr[i]) {
                    r = 0 | (Math.random() * 16);
                    arr[i] = chars[i === 19 ? (r & 0x3) | 0x8 : r];
                }
            }
        }
        return arr.join("");
    },
    /**
     * 日期格式化
     * @param val 日期构建参数，支持所有满足moment的构建参数，为空时构建当前系统时间
     * @param format 格式化字符串
     * @returns 格式化后的日期字符串
     */
    formatDate(val, format) {
        let date;
        if (val) {
            date = moment(val);
        } else {
            date = moment();
        }
        format = format || "YYYY-MM-DD HH:mm:ss";
        let res = date.format(format);
        if (res === "Invalid date") {
            return val;
        }
        return res;
    }
};
export default utils;
