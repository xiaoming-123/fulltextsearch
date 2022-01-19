module.exports = {
    publicPath: "./",
    productionSourceMap: false,
    devServer: {
        open: false,
        port: 8999,
        disableHostCheck: true,
        // 代理
        proxy: {
            "/api": {
                target: "http://127.0.0.1:9000",
                pathRewrite: { "^/api": "" }
            }
        }
    },
    css: {
        loaderOptions: {
            sass: {
                prependData: `
                    @import "./src/assets/styles/vars.scss";
                `
            }
        }
    },
    chainWebpack: (config) => {
        config.plugin("html").tap((args) => {
            args[0].title = "全文检索";
            return args;
        });
    }
};
