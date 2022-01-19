import axios from "axios";
import qs from "qs";

// 创建实例
const instance = axios.create({
    baseURL: "./",
    timeout: 60000,
    withCredentials: true
});

// 设置默认值
instance.defaults.headers.post["Content-Type"] = "application/x-www-form-urlencoded;charset=UTF-8";

// 参数转换函数
function transformRequestData(data, encode) {
    if (!data) return {};
    let new_data = {};
    for (let key in data) {
        let value = data[key];
        if (value !== null && value !== undefined) {
            if (typeof value === "object") {
                new_data[key] = JSON.stringify(value);
            } else {
                new_data[key] = value;
            }
            if (encode) {
                new_data[key] = encodeURIComponent(new_data[key]);
            }
        }
    }
    return new_data;
}

// 请求拦截器
instance.interceptors.request.use(
    (config) => {
        // params参数转换
        if (config.params) {
            config.params = transformRequestData(config.params, true);
        }
        // data参数转换
        if (config.data) {
            config.data = qs.stringify(transformRequestData(config.data, false));
        }
        // 在发送请求之前做些什么
        return config;
    },
    (error) => {
        return Promise.reject("HTTP_REQUEST_ERROR");
    }
);

// 响应拦截器
instance.interceptors.response.use(
    (response) => {
        if (response.status === 200) {
            if (response.data.success === false) {
                // 抛出服务器端异常
                return Promise.reject(response.data.message);
            } else {
                return response;
            }
        } else {
            return Promise.reject("HTTP_RESPONSE_ERROR");
        }
    },
    (error) => {
        return Promise.reject("HTTP_RESPONSE_ERROR");
    }
);

export default {
    /**
     * axios实例
     */
    service: instance,
    /**
     * 以post方式进行http调用
     * @param url http请求调用地址
     * @param data请求参数，类型为Object，其属性为object时将自动转换为json字符串
     * @param config axios请求配置，除支持axios原生配置接口与外还支持配置参数showError（是否显示错误信息， 默认true），showSuccess（是否显示成功信息， 默认true）
     * @returns {Promise<any>}
     */
    post(url, data = {}, config = {}) {
        // 是否显示错误信息， 默认true
        let showError = config.showError !== false;
        delete config.showError;
        // 是否显示成功信息， 默认true
        let showSuccess = config.showSuccess !== false;
        delete config.showSuccess;
        // 成功信息内容
        let successMsg = config.successMsg || "HTTP_REQUEST_SUCCESS";
        delete config.successMsg;
        return instance.post(url, data, config).then(
            (response) => {
                if (showSuccess) {
                    //
                }
                return Promise.resolve({ success: true, data: response.data, response });
            },
            (error) => {
                if (showError) {
                    return successMsg;
                } else {
                    return Promise.resolve({ success: false, errMsg: error });
                }
            }
        );
    },
    /**
     * 以get方式进行http调用
     * @param url http请求调用地址
     * @param params请求参数，类型为Object，其属性为object时将自动转换为json字符串
     * @param config axios请求配置，除支持axios原生配置接口与外还支持配置参数showError（是否显示错误信息， 默认true），showSuccess（是否显示成功信息， 默认false）
     * @returns {Promise<any>}
     */
    get(url, params = {}, config = {}) {
        // 是否显示错误信息， 默认true
        let showError = config.showError !== false;
        delete config.showError;
        // 是否显示成功信息， 默认false
        let showSuccess = config.showSuccess === true;
        delete config.showSuccess;
        // 成功信息内容
        let successMsg = config.successMsg || "HTTP_REQUEST_SUCCESS";
        delete config.successMsg;
        // 设置请求参数
        config.params = params;
        return instance.get(url, config).then(
            (response) => {
                if (showSuccess) {
                    console.log(successMsg);
                }
                return Promise.resolve({ success: true, data: response.data });
            },
            (error) => {
                if (showError) {
                    return console.log(successMsg);
                } else {
                    return Promise.resolve({ success: false, errMsg: error });
                }
            }
        );
    }
};
