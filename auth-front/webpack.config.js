const path = require('path');
const webpack = require('webpack');
const htmlWebpackPlugin = require('html-webpack-plugin');
const url = require('url');
const request = require("request");
const jwt = require('jsonwebtoken');

const debugPath = '/debug/';

module.exports = (options = {}) => ({
    entry: {
        vendor: './src/vendor',
        index: './src/main'
    },
    output: {
        // path: path.resolve(__dirname, '../auth-server/src/main/resources/static'),
        path: path.resolve(__dirname, 'dist'),
        filename: options.dev ? '[name].js' : '[name].js?[chunkhash]',
        chunkFilename: '[id].js?[chunkhash]',
        publicPath: options.dev ? debugPath : '/'
    },
    module: {
        rules: [{
            test: /\.vue$/,
            loader: 'vue-loader',
            options: {
                use: {
                    scss: ['style-loader', 'css-loader', 'sass-loader'],
                    sass: ['style-loader', 'css-loader', 'sass-loader?indentedSyntax']
                }
            }
        }, {
            test: /\.js$/,
            use: [{
                loader: 'babel-loader',
                options: {
                    presets: ['es2015']
                }
            }],
            // exclude: /node_modules/,
            include: [path.resolve(__dirname, "./node_modules/vue-particles/src/"), path.resolve(__dirname, "./src/")]
        }, {
            test: /\.css$/,
            use: ['style-loader', 'css-loader', 'postcss-loader']
        }, {
            test: /\.(png|jpg|jpeg|gif|eot|ttf|woff|woff2|svg|svgz)(\?.+)?$/,
            use: [{
                loader: 'url-loader',
                options: {
                    limit: 10000
                }
            }]
        }]
    },
    plugins: [
        new webpack.optimize.CommonsChunkPlugin({
            names: ['vendor', 'manifest']
        }),
        new htmlWebpackPlugin({
            title: 'My App',
            template: 'src/index.html',
            favicon: path.resolve(__dirname, 'src/assets/img/favicon.ico')
        })
    ],
    resolve: {
        alias: {
            'vue': 'vue/dist/vue.js',
            '~': path.resolve(__dirname, 'src'),
            'components': path.resolve(__dirname, 'src/components')
        }
    },
    devtool: options.dev ? '#eval-source-map' : '#source-map',
    devServer: {
        host: '0.0.0.0',
        port: 80,
        contentBase: [path.join(__dirname, debugPath)],
        proxy: {
            '/api/': {
                target: 'http://localhost:8090/api',
                changeOrigin: true,
                pathRewrite: {
                    '^/api': ''
                }
            },
            '/oauth2/': {
                target: 'http://localhost:8090/oauth2',
                changeOrigin: true,
                pathRewrite: {
                    '^/oauth2': ''
                }
            }
        },
        historyApiFallback: {
            index: debugPath,
            verbose: true
        },
        before(app){
        }
    }
});
