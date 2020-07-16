const dateUtil = {

    formatDate(timestamp) {
        let sDate = "";
        if (timestamp != null) {
            let date = new Date(timestamp);
            sDate = date.toLocaleString();
        }
        return sDate;
    },

    formatDateEx(timestamp, fmt) {
        let date = new Date(timestamp);

        if (/(y+)/.test(fmt)) {
            fmt = fmt.replace(RegExp.$1, (date.getFullYear() + '').substr(4 - RegExp.$1.length));
        }

        let o = {
            'M+': date.getMonth() + 1,
            'd+': date.getDate(),
            'h+': date.getHours(),
            'm+': date.getMinutes(),
            's+': date.getSeconds()
        };

        for (let k in o) {
            if (new RegExp(`(${k})`).test(fmt)) {
                let str = o[k] + '';

                if (fmt.replace(RegExp.$1, (RegExp.$1.length === 1))) {
                    fmt = str;
                } else {
                    fmt = ('00' + str).substr(str.length);
                }

                // fmt = fmt.replace(RegExp.$1, (RegExp.$1.length === 1) ? str : this.padLeftZero(str));
            }
        }

        return fmt;
    },

    padLeftZero(str) {
        return ('00' + str).substr(str.length);
    }

};

export default dateUtil;

