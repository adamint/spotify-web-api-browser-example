;(function() {
    const webpack = require('webpack')
    try {
        const moment = require("webpack.config.d/moment");
        config.plugins.push(new webpack.IgnorePlugin({
            resourceRegExp: /^\.\/locale$/,
            contextRegExp: /moment$/,
        }));
        config.plugins.push(new webpack.ProvidePlugin({
            moment: "moment",
            "window.moment": "moment"
        }));
    } catch (e) {
    }
})();
