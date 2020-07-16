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
            use: ['babel-loader'],
            exclude: /node_modules/,
            include: path.resolve(__dirname, "./node_modules/vue-particles/src/"),
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
            template: 'src/index.html'
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
        host: 'localhost',
        port: 8400,
        contentBase: [path.join(__dirname, debugPath)],
        proxy: {},
        historyApiFallback: {
            index: debugPath,
            verbose: true
        },
        setup(app){
            app.get('/api/token', function (req, res) {

                if ((req.query == null) || (req.query.code == null)) {
                    // exit with 400 bad request error
                    console.log("The user inputs an empty code for query.");
                    res.status(400).end();
                } else {

                    let authCode = req.query.code;

                    let options = {
                        method: 'POST',
                        url: 'http://127.0.0.1/oauth2/token',
                        headers: {
                            'authorization': 'Basic ZGVtbzp6ZHMxZlQ=',
                            'content-type': 'application/x-www-form-urlencoded',
                            'cache-control': 'no-cache'
                        },
                        json: true,
                        form: {
                            "grant_type": "AUTHORIZATION_CODE",
                            "code": authCode,
                            'redirect_uri': 'http://localhost:8888/#/login'
                        }
                    };

                    request.post(options, function (e, r, response) {
                        //console.log(response);

                        if (response != null && response.access_token != null && response.refresh_token != null) {
                            //console.log(response);
                            res.send(response);
                        } else {
                            // exit with 401 Unauthorized
                            console.log("no access token is returned from sso, please ensure user has input correct auth code.");
                            res.status(401).end();
                        }

                    });

                }


            });

            app.get('/api/refresh', function (req, res) {

                if ((req.query == null) || (req.query.refresh_token == null)) {
                    // exit with 400 bad request error
                    console.log("The user inputs an empty refresh token for query.");
                    res.status(400).end();
                } else {

                    let refresh_token = req.query.refresh_token;

                    let options = {
                        method: 'POST',
                        url: 'http://127.0.0.1/oauth2/token',
                        headers: {
                            'authorization': 'Basic ZGVtbzp6ZHMxZlQ=',
                            'content-type': 'application/x-www-form-urlencoded',
                            'cache-control': 'no-cache'
                        },
                        json: true,
                        form: {
                            'grant_type': 'REFRESH_TOKEN',
                            'refresh_token': refresh_token,
                            'redirect_uri': 'http://localhost:8888/#/login'
                        }
                    };

                    request.post(options, function (e, r, response) {
                        //console.log(response);

                        if (response != null && response.access_token != null) {
                            //console.log(response);
                            res.send(response);
                        } else {
                            // exit with 401 Unauthorized
                            console.log("no access token is returned from sso, please ensure user has input correct auth code.");
                            res.status(401).end();
                        }

                    });

                }


            });
        }
    }
});
