const { defineConfig } = require('@vue/cli-service')
module.exports = defineConfig({
    transpileDependencies: true,
    lintOnSave: false,
    publicPath: './',
    devServer: {
        port: 10030,
        client: {
            webSocketURL: 'ws://127.0.0.1:10030/ws',
        },
    }
})
