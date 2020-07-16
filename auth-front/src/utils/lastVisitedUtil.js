
export default {

    save(url){
        localStorage.setItem("last-visited", url);
    },

    read(){
        return localStorage.getItem("last-visited");
    },

    clear(){
        localStorage.removeItem("last-visited");
    }

}