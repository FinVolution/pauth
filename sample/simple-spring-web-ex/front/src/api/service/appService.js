import restApi from '../restApi'

export default {
    fetchTestData(request = {}) {
        let url = 'api/test/fetch';
        return restApi.doGetRequest(url);
    },
    updateTestData(request = {}) {
        let url = 'api/test/update?newData=' + request.newData;
        return restApi.doPostRequest(url);
    }
}
