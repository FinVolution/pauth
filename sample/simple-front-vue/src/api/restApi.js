import axios from 'axios'
import jwtTokenUtil from '../utils/jwtTokenUtil'

export default {

    doGetRequest(url){
        // console.log("send GET request to : " + url);
        let jwtToken = {
            "jwt-token": jwtTokenUtil.readAccess()
        };
        return axios.get(url, {headers: jwtToken})
            .then((response) => Promise.resolve(response))
            .catch((error) => Promise.reject(error))
    },
    doDeleteRequest(url){
        // console.log("send DELETE request to : " + url);
        let jwtToken = {
            "jwt-token": jwtTokenUtil.readAccess()
        };
        return axios.delete(url, {headers: jwtToken})
            .then((response) => Promise.resolve(response))
            .catch((error) => Promise.reject(error))
    },
    doPutRequest(url, data){
        // console.log("send PUT request to : " + url);
        if (typeof(data) == "object") {
            data = JSON.stringify(data);
        }

        let jwtToken = {
            "jwt-token": jwtTokenUtil.readAccess()
        };
        return axios.put(url, data, {headers: jwtToken})
            .then((response) => Promise.resolve(response))
            .catch((error) => Promise.reject(error))
    },
    doPostRequest(url, data){
        // console.log("send POST request to : " + url);
        if (typeof(data) == "object") {
            data = JSON.stringify(data);
        }

        let jwtToken = {
            "jwt-token": jwtTokenUtil.readAccess()
        };
        return axios.post(url, data, {headers: jwtToken})
            .then((response) => Promise.resolve(response))
            .catch((error) => Promise.reject(error))
    }

}
