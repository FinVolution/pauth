import axios from 'axios'
import jwtTokenUtil from '../utils/jwtTokenUtil'

export default {

    doGetRequest(url){
        let jwtToken = {
            "jwt-token": jwtTokenUtil.read()
        };
        return axios.get(url, {headers: jwtToken})
            .then((response) => Promise.resolve(response))
            .catch((error) => Promise.reject(error))
    },
    doDeleteRequest(url){
        let jwtToken = {
            "jwt-token": jwtTokenUtil.read()
        };
        return axios.delete(url, {headers: jwtToken})
            .then((response) => Promise.resolve(response))
            .catch((error) => Promise.reject(error))
    },
    doPutRequest(url, data){
        if (typeof(data) == "object") {
            data = JSON.stringify(data);
        }

        let jwtToken = {
            "jwt-token": jwtTokenUtil.read()
        };
        return axios.put(url, data, {headers: jwtToken})
            .then((response) => Promise.resolve(response))
            .catch((error) => Promise.reject(error))
    },
    doPostRequest(url, data){
        if (typeof(data) == "object") {
            data = JSON.stringify(data);
        }

        let jwtToken = {
            "jwt-token": jwtTokenUtil.read()
        };
        return axios.post(url, data, {headers: jwtToken})
            .then((response) => Promise.resolve(response))
            .catch((error) => Promise.reject(error))
    }

}
