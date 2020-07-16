export default {

    saveAccess(token){
        localStorage.setItem("access-token", token);
    },

    readAccess(){
        return localStorage.getItem("access-token");
    },

    saveRefresh(token){
        localStorage.setItem("refresh-token", token);
    },

    readRefresh(){
        return localStorage.getItem("refresh-token");
    },

    clear(){
        localStorage.removeItem("access-token");
        localStorage.removeItem("refresh-token");
    },

}