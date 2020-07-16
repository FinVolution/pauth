import axios from 'axios'
import jwtTokenUtil from '../utils/jwtTokenUtil'

export default {

    doGetRequest(url){
        // console.log("send GET request to : " + url);
        return axios.get(url)
            .then((response) => Promise.resolve(response))
            .catch((error) => Promise.reject(error))
    },
    doDeleteRequest(url){
        // console.log("send DELETE request to : " + url);
        return axios.delete(url)
            .then((response) => Promise.resolve(response))
            .catch((error) => Promise.reject(error))
    },
    doPutRequest(url, data){
        // console.log("send PUT request to : " + url);
        if (typeof(data) == "object") {
            data = JSON.stringify(data);
        }
        return axios.put(url, data)
            .then((response) => Promise.resolve(response))
            .catch((error) => Promise.reject(error))
    },
    doPostRequest(url, data){
        // console.log("send POST request to : " + url);
        if (typeof(data) == "object") {
            data = JSON.stringify(data);
        }
        return axios.post(url, data)
            .then((response) => Promise.resolve(response))
            .catch((error) => Promise.reject(error))
    }

}
