import {api} from '../../api';

// initial state
const state = {
    testData: null
};

// getters
const getters = {
    getTestData: state => state.testData
};

// actions
const actions = {

    fetchTestData({commit, dispatch}) {
        api.appService.fetchTestData().then(function (resp) {
            commit("saveTestData", resp.data);
        }.bind(this)).catch(function (err) {
            dispatch("displayPromptByResponseMsg", err.response);
        }.bind(this));
    },

    updateTestData({commit, dispatch}, data) {
        api.appService.updateTestData(data).then(function (resp) {
            commit("saveTestData", resp.data);
        }.bind(this)).catch(function (err) {
            dispatch("displayPromptByResponseMsg", err.response);
        }.bind(this));
    }

};

// mutations
const mutations = {
    saveTestData(state, data) {
        state.testData = data;
    }
};

export default {
    state,
    getters,
    actions,
    mutations
}