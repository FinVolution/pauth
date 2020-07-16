
export default {

    generateToken(data){
        let token = null;
        if (data != null) {
            data.timestamp = (new Date()).getTime();
            let c = window.btoa(JSON.stringify(data));
            token = c.split('').reverse().join('') + c.charAt(3) + c;
        }
        return token;
    }

}